package com.whoslast.queue;

import com.whoslast.response.ErrorCodes;
import com.whoslast.controllers.QueueRepository;
import com.whoslast.controllers.UserQueueRepository;
import com.whoslast.controllers.UserRepository;
import com.whoslast.entities.Queue;
import com.whoslast.entities.QueueRecord;
import com.whoslast.entities.User;
import com.whoslast.response.ServerResponse;

/**
 * Manager performing join to the queue
 */
public class QueueJoinManager {
    /**
     * Data provided by request generator to join a queue
     */
    public static class QueueJoinData {
        private String email;
        private String queueId;

        public QueueJoinData(String email, String queueId) {
            this.email = email;
            this.queueId = queueId;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setQueueId(String queueId) {
            this.queueId = queueId;
        }

        public String getEmail() {
            return email;
        }

        public String getQueueId() {
            return queueId;
        }

        /**
         * Is there any empty fields (empty strings)?
         * @return True -- there are empty strings, False -- there aren't any
         */
        public boolean hasEmptyFields() {
            return this.email.isEmpty() || this.queueId.isEmpty();
        }
    }

    /**
     * Exception thrown on demand of join procedure
     */
    private static class QueueJoinException extends Exception {
        int errorCode;
        QueueJoinException(String s, int errorCode) {
            super(s);
            this.errorCode = errorCode;
        }
    }

    private static final String msgQueueJoinSuccess = "Successful queue join";
    private static final String msgQueueJoinEmptyFields = "Some of provided fields were empty";
    private static final String msgQueueJoinErrorBadUser = "User with such an email does not exist";
    private static final String msgQueueJoinErrorUserIsInQueue = "The user is already in the queue";
    private static final String msgQueueJoinErrorBadId = "Queue with such an id does not exist";

    private UserRepository userDatabase;
    private QueueRepository queueDatabase;
    private UserQueueRepository queueListsDatabase;

    public QueueJoinManager(UserRepository userDatabase, QueueRepository queueDatabase, UserQueueRepository queueListsDatabase) {
        this.userDatabase = userDatabase;
        this.queueDatabase = queueDatabase;
        this.queueListsDatabase = queueListsDatabase;
    }

    /**
     * Join user to the queue
     * @param queueJoinData Data provided by user
     * @return Response that contains status of execution
     */
    public ServerResponse join(QueueJoinData queueJoinData) {
        ServerResponse response;
        try {
            analyzeInputData(queueJoinData);
            User user = getUserByEmail(queueJoinData.getEmail());
            Integer queueId = parseQueueId(queueJoinData);
            Queue queue = getQueueById(queueId);
            analyzeExistenceInQueue(user.getUserId(), queueId);
            Integer queueUserPosition = getQueueListLastNum(queueId) + 1;
            QueueRecord record = buildQueueRecord(user, queue, queueUserPosition);
            queueListsDatabase.save(record);
            response = new ServerResponse(msgQueueJoinSuccess, ErrorCodes.NO_ERROR);
        } catch (QueueJoinException e) {
            response = new ServerResponse(e.getMessage(), e.errorCode);
        }
        return response;
    }

    /**
     * Check whether user is already in the queue
     * @param userId User identifier
     * @param queueId Queue identifier
     * @throws QueueJoinException
     */
    private void analyzeExistenceInQueue(Integer userId, Integer queueId) throws QueueJoinException {
        if (queueListsDatabase.checkExistence(userId, queueId) != 0)
            throw new QueueJoinException(msgQueueJoinErrorUserIsInQueue, ErrorCodes.Queue.ALREADY_IN_QUEUE);
    }

    /**
     * Analyze input data for correctness
     * @param queueJoinData Data provided by user
     * @throws QueueJoinException
     */
    private void analyzeInputData(QueueJoinData queueJoinData) throws QueueJoinException {
        if (queueJoinData.hasEmptyFields())
            throw new QueueJoinException(msgQueueJoinEmptyFields, ErrorCodes.Common.EMPTY_FIELDS);
    }

    /**
     * Parse string in input data for queue id
     * @param queueJoinData Data provided by user
     * @return Queue identifier
     * @throws QueueJoinException
     */
    private Integer parseQueueId(QueueJoinData queueJoinData) throws QueueJoinException {
        Integer num;
        try {
            num = Integer.parseInt(queueJoinData.getQueueId());
        } catch (NumberFormatException e) {
            throw new QueueJoinException(msgQueueJoinErrorBadId, ErrorCodes.Queue.QUEUE_DOES_NOT_EXIST);
        }
        return num;
    }

    /**
     * Build record tuple
     * @param user User entity
     * @param queue Queue entity
     * @param queueUserPosition Position in the queue
     * @return Record tuple
     */
    private QueueRecord buildQueueRecord(User user, Queue queue, Integer queueUserPosition) {
        QueueRecord record = new QueueRecord();
        record.setQueue(queue);
        record.setUser(user);
        record.setNumber(queueUserPosition);
        return record;
    }

    /**
     * Get last number in the queue
     * @param queueId Queue identifier
     * @return Last number in the queue
     */
    private Integer getQueueListLastNum(Integer queueId) {
        Integer lastNumber = queueListsDatabase.getLastNumberInCurrentQueue(queueId);
        return lastNumber == null ? 0 : lastNumber;
    }

    /**
     * Find queue entity by it's id
     * @param queueId Queue identifier
     * @return Found queue entity
     * @throws QueueJoinException
     */
    private Queue getQueueById(Integer queueId) throws QueueJoinException {
        Queue queue = queueDatabase.getQueueById(queueId);
        if (queue == null)
            throw new QueueJoinException(msgQueueJoinErrorBadId, ErrorCodes.Queue.QUEUE_DOES_NOT_EXIST);
        return queue;
    }

    /**
     * Find user entity by it's email
     * @param email User's email
     * @return Found user entity
     * @throws QueueJoinException
     */
    private User getUserByEmail(String email) throws QueueJoinException {
        User user = userDatabase.findUserByEmail(email);
        if (user == null)
            throw new QueueJoinException(msgQueueJoinErrorBadUser, ErrorCodes.User.USER_DOES_NOT_EXIST);
        return user;
    }
}
