package com.whoslast.authorization;

/**
 * Response of sign-in / sign-up procedures
 */
public class AuthResponse {
    /**
     * Status of execution
     */
    public enum Status {
        SUCCESS, //Execution succeeded
        FAIL_USER, //Means we've failed because of user's fault
        FAIL_ENVIRONMENT //Means we've failed because of environment's fault (can do nothing with it :( )
    }

    private String msg; //Text message of what happened
    private Status status; //Status of execution
    private int errorCode; //Code of the error

    public AuthResponse(String msg, Status status, int errorCode) {
        this.msg = msg;
        this.status = status;
        this.errorCode = errorCode;
    }

    /**
     * Was the execution successful?
     * @return True -- success, False -- error
     */
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    @Override
    public String toString() {
        return msg;
    }
}
