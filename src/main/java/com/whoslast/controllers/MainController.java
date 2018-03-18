package com.whoslast.controllers;

import com.whoslast.authorization.AuthResponse;
import com.whoslast.authorization.SignInManager;
import com.whoslast.authorization.SignUpManager;
import com.whoslast.entities.User;
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


	// @ResponseBody means the returned String is the response, not a view name
	// @RequestParam means it is a parameter from the GET or POST request
    @GetMapping(path="/sign_up") // Map ONLY GET Requests
    public @ResponseBody String signUp (@RequestParam String name
            , @RequestParam String email, @RequestParam String password) {
        SignUpManager signUpManager = new SignUpManager(userRepository);
        SignUpManager.UserSignUpData signUpData = new SignUpManager.UserSignUpData(name, email, password);
        AuthResponse response = signUpManager.signUp(signUpData);
        return response.toString();
    }

    @GetMapping(path = "/sign_in")
    public @ResponseBody String signIn(@RequestParam String email, @RequestParam String password) {
        SignInManager signInManager = new SignInManager(userRepository);
        SignInManager.UserSignInData signInData = new SignInManager.UserSignInData(email, password);
        AuthResponse response = signInManager.signIn(signInData);
        return response.toString();
    }
	
	@GetMapping(path="/all_json")
	public @ResponseBody Iterable<User> getAllUsersJson() {
		// returns JSON with all users
		return userRepository.findAll();
	}

    @GetMapping(path = "/all")
    public String getAllUsers() {
        // returns JSON with all users
        System.out.println("showing all users");
        return "index";
    }
}
