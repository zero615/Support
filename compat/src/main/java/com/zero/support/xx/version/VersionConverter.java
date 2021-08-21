package com.zero.support.xx.version;

import com.excean.support.download.FileRequest;
import com.excean.support.work.Observable;
import com.excean.support.work.Response;

public interface VersionConverter<T> {
    Observable<Response<T>> fetchVersion();

    FileRequest createFileRequest(T t,boolean allowCellData);
}
