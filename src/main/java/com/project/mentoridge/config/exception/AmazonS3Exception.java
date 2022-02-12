package com.project.mentoridge.config.exception;

public class AmazonS3Exception extends RuntimeException {

    public static final String UPLOAD = "S3 파일 업로드 실패";
    public static final String DELETE = "S3 파일 삭제 실패";

    private static final long serialVersionUID = 395373762474915663L;

    public AmazonS3Exception() {
        super();
    }

    public AmazonS3Exception(final String message) {
        super(message);
    }

    public AmazonS3Exception(final String message, final Throwable cause) {
        super(message, cause);
    }
}
