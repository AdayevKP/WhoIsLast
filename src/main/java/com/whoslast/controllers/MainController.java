package com.whoslast.controllers;

import com.whoslast.authorization.AuthResponse;
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
        SignUpManager.UserSignUpData signUpData = new SignUpManager.UserSignUpData(name, email, password);
        AuthResponse response = signUpManager.signUp(signUpData);
        return response.toString();
    }

    @RequestMapping(path = "/signup", method = RequestMethod.POST)
    public String signUpPost(@RequestParam(value = "inputEmail", required = true) String email,
                             @RequestParam(value = "inputPassword", required = true) String password,
                             @RequestParam(value = "inputName", required = true) String name) {
        System.out.println("in signupPost currently " + email);
        SignUpManager signUpManager = new SignUpManager(userRepository);
        SignUpManager.UserSignUpData signUpData = new SignUpManager.UserSignUpData(name, email, password);
        AuthResponse response = signUpManager.signUp(signUpData);
        System.out.println(response);
        return "index";
    }

    @RequestMapping(path = "/signup", method = RequestMethod.GET)
    public String signUpGet() {
        return "check_in";
    }

    @GetMapping(path = "/sign_in")
    public @ResponseBody
    String signIn(@RequestParam String email, @RequestParam String password) {
        SignInManager signInManager = new SignInManager(userRepository);
        SignInManager.UserSignInData signInData = new SignInManager.UserSignInData(email, password);
        AuthResponse response = signInManager.signIn(signInData);
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
        SignInManager signInManager = new SignInManager(userRepository);
        SignInManager.UserSignInData signInData = new SignInManager.UserSignInData(email, password);
        AuthResponse response = signInManager.signIn(signInData);
        System.out.println(response);
        return "index";
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


    @RequestMapping(path = "/new_group", method = RequestMethod.GET)
    public String addNewGroupGet() {
        return "create_group";
    }

    @RequestMapping(path = "/new_group", method = RequestMethod.POST)
    public String addNewGroup(@RequestParam(value = "inputEmail", required = true) String email,
                              @RequestParam(value = "inputPassword", required = true) String password,
                              @RequestParam(value = "inputName", required = true) String grName) {
        SignInManager signInManager = new SignInManager(userRepository);
        SignInManager.UserSignInData signInData = new SignInManager.UserSignInData(email, password);
        AuthResponse AuthResponse = signInManager.signIn(signInData);
        AuthResponse groupResponse = null;

        if (AuthResponse.isSuccess()) {
            GroupManager groupManager = new GroupManager(partyRepository, userRepository, suRepository);
            groupResponse = groupManager.NewGroup(email, grName);
        } else {
            System.out.println(AuthResponse.toString());
        }
        if (groupResponse != null)
            System.out.println(groupResponse.toString());
        return "index";
    }


}
