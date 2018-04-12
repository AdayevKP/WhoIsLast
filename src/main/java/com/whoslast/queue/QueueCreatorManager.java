package com.whoslast.queue;

import com.whoslast.controllers.PartyRepository;
import com.whoslast.controllers.QueueRepository;
import com.whoslast.controllers.SuperuserRepository;
import com.whoslast.controllers.UserRepository;
import com.whoslast.entities.Party;
import com.whoslast.entities.Queue;
import com.whoslast.entities.User;
import com.whoslast.response.ErrorCodes;
import com.whoslast.response.ServerResponse;
import java.util.Date;

public class QueueCreatorManager {

    private static final String msgSuccess = "Successful queue creation";
    private static final String msgPlaceTimeFail = "There is another queue at this time and place";
    private static final String msgProfTimeFail = "Professor is busy at this time";

    private QueueRepository queueDatabase;

    public QueueCreatorManager(QueueRepository queueDatabase) {
        this.queueDatabase = queueDatabase;
    }
    private Queue newQueueBuild(Date time, String place, String prof, String queueName){
        Queue newQueue = new Queue();
        newQueue.setPlace(place);
        newQueue.setProfessor(prof);
        newQueue.setTime(time);
        newQueue.setQueueName(queueName);
        return newQueue;
    }

    public ServerResponse createNewQueue(Date time, String place, String prof, String queueName){
        ServerResponse response;

        if(queueDatabase.getQueueByProfAndPlace(prof,place) != null){
            response = new ServerResponse(msgProfTimeFail,ErrorCodes.Queues.PROFESSOR_AND_TIME);
        }
        else if(queueDatabase.getQueueByTimeAndPlace(time,place)!= null){
            response = new ServerResponse(msgPlaceTimeFail, ErrorCodes.Queues.PLACE_AND_TIME);
        }
        else{
            Queue newQueue = newQueueBuild(time, place, prof, queueName);
            queueDatabase.save(newQueue);
            response = new ServerResponse(msgSuccess, ErrorCodes.NO_ERROR);
        }

        return response;
    }
}
