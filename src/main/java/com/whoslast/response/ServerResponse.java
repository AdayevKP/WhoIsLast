package com.whoslast.response;

/**
 * Server response
 */
public class ServerResponse {
    private String msg; //Text message of what happened
    private int errorCode; //Code of the error
    private Object additionalData;

    public ServerResponse(String msg, int errorCode) {
        this.msg = msg;
        this.errorCode = errorCode;
        this.additionalData = null;
    }

    public ServerResponse(String msg, int errorCode, Object additionalData) {
        this.msg = msg;
        this.errorCode = errorCode;
        this.additionalData = additionalData;
    }

    public String getMsg() { return msg; }

    public int getErrorCode() { return errorCode; }

    public Object getAdditionalData() { return additionalData; }

    /**
     * Was the execution successful?
     * @return True -- success, False -- error
     */
    public boolean isSuccess() {
        return errorCode == ErrorCodes.NO_ERROR;
    }

    @Override
    public String toString() {
        return msg;
    }
}
