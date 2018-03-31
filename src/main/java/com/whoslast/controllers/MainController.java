package com.whoslast.controllers;

import com.mysql.fabric.Server;
import com.whoslast.queue.QueueAvailableManager;
import com.whoslast.queue.QueueJoinManager;
import com.whoslast.response.ServerResponse;
import com.whoslast.authorization.SignInManager;
import com.whoslast.authorization.SignUpManager;
import com.whoslast.entities.User;
import com.whoslast.group.GroupManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@EnableJpaRepositories
@RequestMapping(path = "/test") // URL beginning
public class MainController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private UserQueueRepository userQueueRepository;
    @Autowired
    private PartyQueueRepository partyQueueRepository;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private SuperuserRepository suRepository;

    // @ResponseBody means the returned String is the response, not a view name
    // @RequestParam means it is a parameter from the GET or POST request
    @GetMapping(path = "/sign_up") // Map ONLY GET Requests
    public @ResponseBody
    String signUp(@RequestParam String name
            , @RequestParam String email, @RequestParam String password) {
        SignUpManager signUpManager = new SignUpManager(userRepository);
        ServerResponse response = signUpManager.signUp(new SignUpManager.UserSignUpData(name, email, password));
        return response.toString();
    }

    @RequestMapping(path = "/signup", method = RequestMethod.GET)
    public String signUpGet() {
        return "check_in";
    }

    @RequestMapping(path = "/signup", method = RequestMethod.POST)
    public String signUpPost(@RequestParam(value = "inputEmail", required = true) String email,
                             @RequestParam(value = "inputPassword", required = true) String password,
                             @RequestParam(value = "inputName", required = true) String name) {
        System.out.println("in signupPost currently " + email);
        SignUpManager signUpManager = new SignUpManager(userRepository);
        SignUpManager.UserSignUpData signUpData = new SignUpManager.UserSignUpData(name, email, password);
        ServerResponse response = signUpManager.signUp(signUpData);
        System.out.println(response);
        return "index";
    }

    @GetMapping(path = "/sign_in")
    public @ResponseBody
    String signIn(@RequestParam String email, @RequestParam String password) {
        ServerResponse response = authorize(email, password);
        return response.toString();
    }

    @RequestMapping(path = "/signin", method = RequestMethod.GET)
    public String signInGet() {
        System.out.println("inside signin get method");
        return "sign_in";
    }

    @RequestMapping(path = "/signin", method = RequestMethod.POST)
    public String signInPost(@RequestParam(value = "inputEmail", required = true) String email,
                             @RequestParam(value = "inputPassword", required = true) String password) {
        System.out.println("in signinPost currently " + email);
        ServerResponse response = authorize(email, password);
        System.out.println(response);
        return "index";
    }

    @RequestMapping(path = "/new_group", method = RequestMethod.GET)
    public String addNewGroupGet() {
        return "create_group";
    }

    @RequestMapping(path = "/new_group", method = RequestMethod.POST)
    public String addNewGroup(@RequestParam(value = "inputEmail", required = true) String email,
                              @RequestParam(value = "inputPassword", required = true) String password,
                              @RequestParam(value = "inputName", required = true) String grName) {
        ServerResponse authResponse = authorize(email, password);
        ServerResponse groupResponse = null;

        if (authResponse.isSuccess()) {
            GroupManager groupManager = new GroupManager(partyRepository, userRepository, suRepository);
            groupResponse = groupManager.NewGroup(email, grName);
        } else {
            System.out.println(authResponse.toString());
        }
        if (groupResponse != null)
            System.out.println(groupResponse.toString());
        return "index";
    }

    @GetMapping(path = "/join_queue")
    public @ResponseBody
    String joinQueue(@RequestParam String email, @RequestParam String password, @RequestParam String queue_id) {
        ServerResponse authResponse = authorize(email, password);
        if (!authResponse.isSuccess())
            return authResponse.toString();

        QueueJoinManager queueJoinManager = new QueueJoinManager(userRepository, queueRepository, userQueueRepository);
        ServerResponse response = queueJoinManager.join(new QueueJoinManager.QueueJoinData(email, queue_id));
        return response.toString();
    }

    @GetMapping(path = "/available_user_queues")
    public @ResponseBody
    String getAvailableUserQueues(@RequestParam String email, @RequestParam String password) {
        ServerResponse authResponse = authorize(email, password);
        if (!authResponse.isSuccess())
            return authResponse.toString();

        ServerResponse response = getQueuesResponse(email, QueueAvailableManager.QueueAvailableMode.BY_USER);
        if (response.isSuccess()) {
            QueueAvailableManager.QueueAvailableList queues = (QueueAvailableManager.QueueAvailableList)response.getAdditionalData();
            return queues.toString();
        } else {
            return response.toString();
        }
    }

    @GetMapping(path = "/available_party_queues")
    public @ResponseBody
    String getAvailablePartyQueues(@RequestParam String email, @RequestParam String password, @RequestParam String party_id) {
        ServerResponse authResponse = authorize(email, password);
        if (!authResponse.isSuccess())
            return authResponse.toString();

        ServerResponse response = getQueuesResponse(party_id, QueueAvailableManager.QueueAvailableMode.BY_PARTYID);
        if (response.isSuccess()) {
            QueueAvailableManager.QueueAvailableList queues = (QueueAvailableManager.QueueAvailableList)response.getAdditionalData();
            return queues.toString();
        } else {
            return response.toString();
        }
    }

    @GetMapping(path = "/all_json")
    public @ResponseBody
    Iterable<User> getAllUsersJson() {
        return userRepository.findAll();
    }

    @GetMapping(path = "/all")
    public String getAllUsers() {
        return "index";
    }


    @GetMapping(path = "/users_json/{id}")
    public @ResponseBody
    Iterable<User> singleUser(@PathVariable Integer id) {
        return userRepository.findUserById(id);
    }

    private ServerResponse getQueuesResponse(String argument, QueueAvailableManager.QueueAvailableMode mode) {
        QueueAvailableManager queueAvailableManager = new QueueAvailableManager(userRepository, partyRepository, queueRepository);
        ServerResponse response = queueAvailableManager.get_available(new QueueAvailableManager.QueueAvailableData(argument, mode));
        return response;
    }

    private ServerResponse authorize(String email, String password) {
        return new SignInManager(userRepository).signIn(new SignInManager.UserSignInData(email, password));
    }
}
