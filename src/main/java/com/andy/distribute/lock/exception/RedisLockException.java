package com.andy.distribute.lock.exception;

/**
 * Created by andy on 2016/8/24.
 */
public class RedisLockException extends InterruptedException{

    private int errorCode;

    public RedisLockException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public RedisLockException(String message) {
        super(message);
    }

    public RedisLockException(Throwable cause) {
        super(cause.toString());
    }

    public int getErrorCode() {
        return errorCode;
    }
}
