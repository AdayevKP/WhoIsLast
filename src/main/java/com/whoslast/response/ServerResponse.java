package com.whoslast.response;

/**
 * Server response
 */
public class ServerResponse {
    private String msg; //Text message of what happened
    private int errorCode; //Code of the error

    public ServerResponse(String msg, int errorCode) {
        this.msg = msg;
        this.errorCode = errorCode;
    }

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
