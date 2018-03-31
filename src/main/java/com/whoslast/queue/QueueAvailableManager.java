package com.whoslast.queue;

import com.whoslast.controllers.QueueRepository;
import com.whoslast.controllers.UserRepository;
import com.whoslast.entities.Party;
import com.whoslast.entities.Queue;
import com.whoslast.entities.User;
import com.whoslast.response.ErrorCodes;
import com.whoslast.response.ServerResponse;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Get queues available to user
 */
public class QueueAvailableManager {
    /**
     * Data provided by request generator to get available queues
     */
    public static class QueueAvailableData {
        private String email;

        public QueueAvailableData(String email) {
            this.email = email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        /**
         * Is there any empty fields (empty strings)?
         * @return True -- there are empty strings, False -- there aren't any
         */
        public boolean hasEmptyFields() { return this.email.isEmpty() ; }
    }

    /**
     * Exception thrown on demand of get_available procedure
     */
    private static class QueueAvailableException extends Exception {
        int errorCode;
        QueueAvailableException(String s, int errorCode) {
            super(s);
            this.errorCode = errorCode;
        }
    }

    /**
     * Additional data in response that contains list of available queues
     */
    public static class QueueAvailableList {
        private List<Queue> queues;

        public QueueAvailableList(Iterable<Queue> queues) {
            this.queues = new ArrayList<>();
            queues.forEach(this.queues::add);
        }

        public Queue get(int i) {
            return this.queues.get(i);
        }

        public void remove(int i) {
            this.queues.remove(i);
        }

        public int size() {
            return this.queues.size();
        }

        public boolean isEmpty() {
            return queues.isEmpty();
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            Format dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            stringBuilder.append("[");
            for (Queue queue : this.queues) {
                String date = dateFormatter.format(queue.getTime());
                stringBuilder.append(String.format("[ %s, %s, %s, %s]", queue.getQueueId(), queue.getPlace(), queue.getProfessor(), date));
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    private static final String msgQueueAvailableSuccess = "Successful get of available queues";
    private static final String msgQueueAvailableEmptyFields = "Some of provided fields were empty";
    private static final String msgQueueAvailableErrorBadUser = "User with such an email does not exist";
    private static final String msgQueueAvailableErrorNoGroup = "The user is not in a group";

    private UserRepository userDatabase;
    private QueueRepository queueDatabase;

    public QueueAvailableManager(UserRepository userDatabase, QueueRepository queueDatabase) {
        this.userDatabase = userDatabase;
        this.queueDatabase = queueDatabase;
    }

    /**
     * Get available queues
     * @param queueAvailableData Data providided by user
     * @return Server response that contains list of available queues
     */
    public ServerResponse get_available(QueueAvailableData queueAvailableData) {
        ServerResponse response;
        try {
            analyzeInputData(queueAvailableData);
            User user = getUserByEmail(queueAvailableData.getEmail());
            QueueAvailableList queues = getUserQueues(user);
            response = new ServerResponse(msgQueueAvailableSuccess, ErrorCodes.NO_ERROR, queues);
        } catch (QueueAvailableException e) {
            response = new ServerResponse(e.getMessage(), e.errorCode);
        }
        return response;
    }

    /**
     * Get party of current user
     * @param user User entity
     * @return Party of current user
     * @throws QueueAvailableException
     */
    private Party getPartyOfUser(User user) throws QueueAvailableException {
        if (user.getPartyId() == null)
            throw new QueueAvailableException(msgQueueAvailableErrorNoGroup, ErrorCodes.Groups.NOT_IN_GROUP);
        return user.getPartyId();
    }

    /**
     * Get queues, associated with current user
     * @param user User entity
     * @return List of available queues
     * @throws QueueAvailableException
     */
    private QueueAvailableList getUserQueues(User user) throws QueueAvailableException {
        Party party = getPartyOfUser(user);
        Iterable<Queue> queues = queueDatabase.getQueuesEntriesByPartyId(party.getPartyId());
        return new QueueAvailableList(queues);
    }

    /**
     * Get user by email
     * @param email Email
     * @return User entity
     * @throws QueueAvailableException
     */
    private User getUserByEmail(String email) throws QueueAvailableException {
        User user = userDatabase.findUserByEmail(email);
        if (user == null)
            throw new QueueAvailableException(msgQueueAvailableErrorBadUser, ErrorCodes.Users.USER_DOES_NOT_EXIST);
        return user;
    }

    /**
     * Analyze input data for correctness
     * @param queueAvailableData Data provided by user
     * @throws QueueAvailableException
     */
    private void analyzeInputData(QueueAvailableData queueAvailableData) throws QueueAvailableException {
        if (queueAvailableData.hasEmptyFields())
            throw new QueueAvailableException(msgQueueAvailableEmptyFields, ErrorCodes.Common.EMPTY_FIELDS);
    }
}