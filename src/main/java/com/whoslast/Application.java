package com.whoslast;

import com.whoslast.controllers.*;
import com.whoslast.entities.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication(scanBasePackageClasses = {GroupQueueRepository.class,
        GroupRepository.class, MainController.class,
        QueueRepository.class, UserQueueRepository.class,
        UserRepository.class, Party.class,
        PartyQueue.class, Queue.class,
        User.class, UserQueue.class})
@ComponentScan(basePackages = {"com"})
@EntityScan(basePackages = {"com"})
@EnableJpaAuditing
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}


