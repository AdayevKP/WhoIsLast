package com.whoslast.response;

public abstract class ErrorCodes {
    public static final int NO_ERROR = 0;

    //COMMON ERROR CODES
    public static class Common {
        public static final int EMPTY_FIELDS = 10;
    }

    //USER ERROR CODES
    public static class Users {
        public static final int USER_EXISTS = 21;
        public static final int USER_DOES_NOT_EXIST = 22;
    }

    //AUTHORIZATION ERROR CODES
    public static class Authorization {
        public static final int ENVIRONMENT_FAIL = 31;
        public static final int BAD_PASSWORD = 32;
        public static final int WRONG_CREDENTIALS = 33;
    }

    //GROUPS ERROR CODES
    public static class Groups {
        public static final int USER_ALREADY_IN_GROUP = 41;
        public static final int GROUP_WITH_THIS_NAME_ALREADY_EXISTS = 42;
        public static final int YOU_ALREADY_HAVE_YOUR_OWN_GROUP = 43;
        public static final int NOT_IN_GROUP = 44;
        public static final int GROUP_DOES_NOT_EXIST = 45;
        public static final int NO_USER_IN_DB = 46;
        public static final int NO_GROUP_IN_DB = 47;
    }

    //QUEUE ERROR CODES
    public static class Queues {
        public static final int QUEUE_DOES_NOT_EXIST = 51;
        public static final int ALREADY_IN_QUEUE = 52;
    }
}