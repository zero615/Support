//package com.zero.support.compat.toolbox;
//
//
//
//import com.zero.support.work.SnapShotTask;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.Executor;
//import java.util.zip.CheckedOutputStream;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//
//public class FileDownloadTask extends SnapShotTask<FileRequest, File> {
//    private String suffix = "_fdt";
//
//    @Override
//    protected File process(FileRequest input) throws Throwable {
//        File output = input.output();
//        Preferences preferences = new Preferences(new File(output.getParent(), output.getName() + suffix + ".json"));
//        final Progress progress = new Progress();
//
//
//        if (input.parallel() == 1) {
//            progress.init(FileRequest.TYPE_DOWNLOADING, 0, input.size(), 0, 1, 1);
//            Block block = new Block(input, preferences);
//            block.start(AppExecutor.current(), new Observer<Progress>() {
//                @Override
//                public void onChanged(Progress value) {
//                    progress.init(value.handled());
//                    publishProgressChanged(progress);
//                }
//            });
//            Response<Boolean> response = block.task.getResult();
//            if (!response.isSuccessful()) {
//                throw response.getCause();
//            }
//        } else {
//            int totalStage;
//            if (input.md5() != null && input.md5().length() != 0) {
//                totalStage = 2;
//            } else {
//                totalStage = 1;
//            }
//            progress.init(FileRequest.TYPE_DOWNLOADING, 0, input.size(), 0, 1, totalStage);
//            List<Block> blocks = build(preferences, input);
//            for (Block block : blocks) {
//                block.start(AppExecutor.async(), new Observer<Progress>() {
//                    private long handled;
//
//                    @Override
//                    public void onChanged(Progress value) {
//                        progress.init(progress.handled() + value.handled() - handled);
//                        handled = progress.handled();
//                        publishProgressChanged(progress);
//                    }
//                });
//            }
//            for (Block block : blocks) {
//                block.task.getResult();
//            }
//            for (Block block : blocks) {
//                Response<Boolean> response = block.task.getResult();
//                if (!response.isSuccessful()) {
//                    throw response.getCause();
//                }
//            }
//
//            if (input.size() != -1 && input.size() != progress.handled()) {
//                throw new FileNotFoundException("file size is mismatch ");
//            }
//            if (totalStage == 2) {
//                progress.init(FileRequest.TYPE_VERIFYING, 0, progress.handled(), 0, totalStage, totalStage);
//                InputStream inputStream = new SeekInputStream(output, input.offset());
//                FileUtils.md5File(inputStream, progress.handled(), progress, new ProgressListener() {
//                    @Override
//                    public void onProgress(Progress progress) {
//                        progress.init(progress.handled(), progress.total());
//                        publishProgressChanged(progress);
//                    }
//                });
//            }
//
//
//        }
//        return input.output();
//    }
//
//    private List<Block> build(Preferences preferences, FileRequest input) {
//        long blockSize = input.size() / input.parallel();
//
//        return Collections.emptyList();
//    }
//
//    private static class Block {
//        Preferences preferences;
//        String url;
//        int index;
//        long begin;
//        long length;
//        String md5;
//        File file;
//        OkHttpClient client;
//
//        BlockTask task;
//
//        public Block(FileRequest request, Preferences preferences, int index, long begin, long length) {
//            this.preferences = preferences;
//            this.url = request.url();
//            this.index = index;
//            this.begin = begin + request.offset();
//            this.length = length;
//            this.client = request.client();
//            this.file = request.output();
//        }
//
//
//        public Block(FileRequest request, Preferences preferences) {
//            this.preferences = preferences;
//            this.url = request.url();
//            this.index = 0;
//            this.begin = request.offset();
//            this.length = request.size();
//            this.client = request.client();
//            this.file = request.output();
//            this.md5 = request.md5();
//        }
//
//        public void start(Executor executor, Observer<Progress> progress) {
//            if (task != null && !task.isFinished()) {
//                return;
//            }
//            task = new BlockTask();
//            task.progress().observe(progress);
//            task.input(this).run(executor);
//        }
//
//        public long getHandled() {
//            return preferences.getLong(String.valueOf(index), 0);
//        }
//
//        public void setHandled(long handled) {
//            preferences.putLong(String.valueOf(index), handled);
//        }
//    }
//
//    private static class BlockTask extends PromiseTask<Block, Boolean> {
//
//        @Override
//        protected Boolean process(Block block) throws Exception {
//            long downloadSize = block.preferences.getLong(String.valueOf(block.index), 0L);
//            Request request = new Request.Builder().url(block.url).addHeader("Accept-Encoding", "identity")
//                    .addHeader("Range", "bytes=" + (block.begin + downloadSize) + "-" + (block.begin + block.length - 1))
//                    .build();
//            InputStream input = block.client.newCall(request).execute().body().byteStream();
//            OutputStream output;
//            Md5CheckSum checkSum = null;
//            if (block.md5 != null) {
//                checkSum = new Md5CheckSum();
//                output = new CheckedOutputStream(new SeekOutputStream(block.file, block.begin + downloadSize), checkSum);
//            } else {
//                output = new SeekOutputStream(block.file, block.begin + downloadSize);
//            }
//            Progress progress = new Progress();
//            progress.init(downloadSize, block.length);
//            publishProgressChanged(progress);
//            int n;
//            long handled = progress.handled();
//            byte[] bytes = new byte[8192];
//            long lastTime = System.currentTimeMillis();
//            long currentTime;
//            while (-1 != (n = input.read(bytes))) {
//                output.write(bytes, 0, n);
//                handled += n;
//                progress.init(handled);
//                currentTime = System.currentTimeMillis();
//                if ((currentTime - lastTime) > 200) {
//                    lastTime = currentTime;
//                    publishProgressChanged(progress);
//                    block.setHandled(progress.handled());
//                }
//            }
//            publishProgressChanged(progress);
//            if (checkSum != null && !block.md5.equals(StringUtil.byteToHexString(checkSum.digest()))) {
//                throw new FileNotFoundException("file md5 is mismatch");
//            }
//            return true;
//        }
//    }
//}
