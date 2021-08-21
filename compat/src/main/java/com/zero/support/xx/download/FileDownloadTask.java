package com.zero.support.xx.download;


import android.net.Uri;

import com.excean.support.util.BoundInputStream;
import com.excean.support.util.IgnoreOutputStream;
import com.excean.support.util.Md5CheckSum;
import com.excean.support.util.Preferences;
import com.excean.support.util.SeekInputStream;
import com.excean.support.util.SeekOutputStream;
import com.excean.support.work.AppExecutor;
import com.excean.support.work.Observer;
import com.excean.support.work.Progress;
import com.excean.support.work.PromiseTask;
import com.excean.support.work.Response;
import com.excean.support.work.SnapShotTask;
import com.excean.support.work.exception.FileVerifyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.zip.CheckedOutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FileDownloadTask extends SnapShotTask<FileRequest, File> {
    private static final String suffix = "";

    @Override
    protected File process(FileRequest input) throws Throwable {
        File output = input.output();

        Preferences preferences = new Preferences(new File(output.getParent(), output.getName() + suffix + ".json"));
        final Progress progress = new Progress();
        if (input.parallel() == 1) {
            progress.init(FileRequest.TYPE_DOWNLOADING, 0, input.size(), 0, 1, 1);
            Block block = new Block(input, preferences);
            block.start(AppExecutor.current(), new Observer<Progress>() {
                @Override
                public void onChanged(Progress value) {
                    progress.init(value.handled);
                    progress.speed = value.speed;
                    publishProgressChanged(progress);
                }
            });
            Response<Boolean> response = block.task.getResult();
            if (!response.isSuccessful()) {
                throw response.getCause();
            }
            if (input.md5() != null) {
                FileInputStream inputStream = new FileInputStream(output);
                String md5 = md5(inputStream);
                inputStream.close();
                if (!md5.equals(input.md5())) {
                    preferences.edit().clear().commit();
                    output.delete();
                    throw new FileVerifyException("md5 mismatch " + md5 + "--" + input.md5());
                }
            }
        } else {
            int totalStage;
            if (input.md5() != null && input.md5().length() != 0) {
                totalStage = 2;
            } else {
                totalStage = 1;
            }
            progress.init(FileRequest.TYPE_DOWNLOADING, 0, input.size(), 0, 1, totalStage);
            List<Block> blocks = build(preferences, input);
            for (Block block : blocks) {
                block.start(AppExecutor.async(), new Observer<Progress>() {
                    private long handled;

                    @Override
                    public void onChanged(Progress value) {
                        progress.init(progress.handled + value.handled - handled);
                        progress.speed = value.speed;
                        handled = progress.handled;
                        publishProgressChanged(progress);
                    }
                });
            }
            for (Block block : blocks) {
                block.task.getResult();
            }
            for (Block block : blocks) {
                Response<Boolean> response = block.task.getResult();
                if (!response.isSuccessful()) {
                    throw response.getCause();
                }
            }

            if (input.size() != -1 && input.size() != progress.handled) {
                throw new FileNotFoundException("file size is mismatch ");
            }
            if (totalStage == 2) {
                progress.init(FileRequest.TYPE_VERIFYING, 0, progress.handled, 0, totalStage, totalStage);
                if (input.md5() != null) {
                    InputStream inputStream = new SeekInputStream(output, input.offset());
                    String md5 = md5(inputStream);
                    inputStream.close();
                    if (!md5.equals(input.md5())) {
                        preferences.edit().clear().commit();
                        output.delete();
                        throw new FileVerifyException("md5 mismatch " + md5 + "--" + input.md5());
                    }
                }
            }


        }
        return input.output();
    }

    public static String md5(InputStream inputStream) throws IOException {
        Md5CheckSum checkSum = new Md5CheckSum();
        CheckedOutputStream outputStream = new CheckedOutputStream(new IgnoreOutputStream(), checkSum);
        byte[] bytes = new byte[4096];
        int len;
        while ((len = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
        }
        outputStream.close();


        return byteToHexString(checkSum.digest());

    }

    private static String byteToHexString(byte[] byteArray) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            int byteCode = byteArray[i] & 0xFF;
            if (byteCode < 0x10) {
                builder.append(0);
            }
            builder.append(Integer.toHexString(byteCode));
        }
        return builder.toString();
    }

    private List<Block> build(Preferences preferences, FileRequest input) {
        long blockSize = input.size() / input.parallel();

        return Collections.emptyList();
    }

    private static class Block {
        Preferences preferences;
        String url;
        int index;
        long begin;
        long length;
        String md5;
        File file;
        OkHttpClient client;

        BlockTask task;

        public Block(FileRequest request, Preferences preferences, int index, long begin, long length) {
            this.preferences = preferences;
            this.url = request.url();
            this.index = index;
            this.begin = begin + request.offset();
            this.length = length;
            this.client = request.client();
            this.file = request.output();
        }


        public Block(FileRequest request, Preferences preferences) {
            this.preferences = preferences;
            this.url = request.url();
            this.index = 0;
            this.begin = request.offset();
            this.length = request.size();
            this.client = request.client();
            this.file = request.output();
            this.md5 = request.md5();
        }

        public void start(Executor executor, Observer<Progress> progress) {
            if (task != null && !task.isFinished()) {
                return;
            }
            task = new BlockTask();
            task.progress().observe(progress);
            task.input(this).run(executor);
        }

        public long getHandled() {
            return preferences.getLong(String.valueOf(index), 0);
        }

        public void setHandled(long handled) {
            preferences.putLong(String.valueOf(index), handled);
        }
    }

    private static class BlockTask extends PromiseTask<Block, Boolean> {

        @Override
        protected Boolean process(Block block) throws Exception {
            long downloadSize = block.preferences.getLong(String.valueOf(block.index), 0L);
            if (downloadSize==block.length){
                return true;
            }
            InputStream input;
            if (block.url.startsWith("file://")) {
                File file = new File(Uri.parse(block.url).getPath());
                input = new BoundInputStream(new SeekInputStream(file, block.begin + downloadSize), block.length - block.begin);
            } else {
                long end = (block.begin + block.length - 1);
                Request request = new Request.Builder().url(block.url).addHeader("Accept-Encoding", "identity")
                        .addHeader("Range", "bytes=" + (block.begin + downloadSize) + "-" +end)
                        .build();
                input = block.client.newCall(request).execute().body().byteStream();
            }

            OutputStream output;
            Md5CheckSum checkSum = null;
            if (block.md5 != null) {
                checkSum = new Md5CheckSum();
                output = new CheckedOutputStream(new SeekOutputStream(block.file, block.begin + downloadSize), checkSum);
            } else {
                output = new SeekOutputStream(block.file, block.begin + downloadSize);
            }
            Progress progress = new Progress();
            progress.init(downloadSize, block.length);
            publishProgressChanged(progress);
            int n;
            long handled = progress.handled;
            long lashHandled = handled;
            byte[] bytes = new byte[8192];
            long lastTime = System.currentTimeMillis();
            long currentTime;
            long duration;

            while (-1 != (n = input.read(bytes))) {
                output.write(bytes, 0, n);
                handled += n;
                progress.init(handled);
                currentTime = System.currentTimeMillis();
                duration = currentTime - lastTime;
                if (duration > 200) {
                    lastTime = currentTime;
                    progress.speed = (handled - lashHandled) * 1000 / duration;
                    lashHandled = handled;
                    publishProgressChanged(progress);
                    block.setHandled(progress.handled);
                }
            }
            input.close();
            output.close();
            progress.init(block.length);
            block.setHandled(block.length);
            publishProgressChanged(progress);
            return true;
        }
    }
}
