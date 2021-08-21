package com.zero.support.xx.download;


import java.io.File;
import java.util.Objects;

import okhttp3.OkHttpClient;

public class FileRequest {
    public static final OkHttpClient DEFAULT_CLIENT = new OkHttpClient.Builder().build();

    public static final int TYPE_VERIFYING = 2;
    public static final int TYPE_DOWNLOADING = 1;
    private final String url;
    private final String md5;
    private final long size;
    private final int parallel;
    private final File file;
    private final long offset;
    private final OkHttpClient client;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileRequest that = (FileRequest) o;
        return offset == that.offset &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, offset);
    }

    public FileRequest(Builder builder) {
        this.url = builder.url;
        this.md5 = builder.md5;
        this.size = builder.length;
        this.parallel = builder.parallel;
        this.file = builder.file;
        this.offset = builder.offset;
        this.client = builder.client;
        if (url == null) {
            throw new IllegalArgumentException("url is null");
        }
        if (file == null) {
            throw new IllegalArgumentException("file is null");
        }
        if (client == null) {
            throw new IllegalArgumentException("client is null");
        }
    }

    public String url() {
        return url;
    }

    public String md5() {
        return md5;
    }

    public int parallel() {
        return parallel;
    }

    public File output() {
        return file;
    }

    public long offset() {
        return offset;
    }

    public OkHttpClient client() {
        return client;
    }

    public long size() {
        return size;
    }

    public static class Builder {
        private String url;
        private String md5;
        private long length = -1;
        private int parallel = 1;
        private File file;
        private long offset;
        private OkHttpClient client = DEFAULT_CLIENT;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder md5(String md5) {
            this.md5 = md5;
            return this;
        }

        public Builder contentLength(long length) {
            this.length = length;
            return this;
        }

        public Builder parallel(int count) {
            this.parallel = count;
            return this;
        }

        public Builder output(File file, long offset) {
            this.file = file;
            this.offset = offset;
            return this;
        }

        public Builder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public FileRequest request() {
            return new FileRequest(this);
        }

    }


}
