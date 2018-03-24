package com.whoslast;

public class ErrorCodes {
    public static final int NO_ERROR = 0;

    //AUTHORIZATION ERROR CODES
    public static class Authorization {
        public static final int ENVIRONMENT_FAIL = 11;
        public static final int EMPTY_FIELDS = 12;
        public static final int USER_EXISTS = 13;
        public static final int BAD_PASSWORD = 14;
        public static final int WRONG_CREDENTIALS = 15;
    }

    public static class Groups {
        public static final int YOU_ALREADY_IN_GROUP = 16;
        public static final int GROUP_WITH_THIS_NAME_ALREADY_EXISTS = 17;
    }
}