package com.whoslast;

import com.whoslast.controllers.MainController;
import com.whoslast.entities.LiveQueue;
import com.whoslast.entities.User;
import com.whoslast.controllers.LiveQueueRepository;
import com.whoslast.controllers.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication(scanBasePackageClasses = {UserRepository.class, MainController.class, User.class,
        LiveQueueRepository.class, LiveQueue.class})
@ComponentScan(basePackages = {"com"})
@EntityScan(basePackages = {"com"})
@EnableJpaAuditing
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}


