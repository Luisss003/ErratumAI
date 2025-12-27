package com.luis.textlift_backend.features.upload.domain;

public enum UploadStatus {
    UPLOADING, //Bytes uploading
    UPLOADED,
    PENDING, //Record created; no bytes sent
    FAILED
}
