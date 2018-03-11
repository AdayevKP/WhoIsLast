package com.whoslast.controllers;

import com.whoslast.authorization.CredentialsManager;
import com.whoslast.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableJpaRepositories
@RequestMapping(path = "/test") // URL beginning
public class MainController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
    private QueueRepository queueRepository;


	// @ResponseBody means the returned String is the response, not a view name
	// @RequestParam means it is a parameter from the GET or POST request
    @GetMapping(path="/add") // Map ONLY GET Requests
    public @ResponseBody String addNewUser (@RequestParam String name
            , @RequestParam String email, @RequestParam String password, @RequestParam String partyId) {

        try {
            CredentialsManager.Credentials myCred = CredentialsManager.buildCredentials(password);
            String salt = myCred.getSalt();
            String hash = myCred.getHash();
            int hashsize = myCred.getHashSize();
            User n = new User();
            n.setName(name);
            n.setEmail(email);
            n.setSalt(salt);
            n.setHash(hash);
            n.setHashsize(hashsize);
            userRepository.save(n);
            return "Saved";
        }
        catch (Exception e){
            System.out.println("Problems with password\n");
        }
        return "Problems with registration";
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

    @GetMapping(path = "/signin")
    public String signIn() {
        // returns JSON with all users
        System.out.println("signing in");
        return "sign_in";
    }
}
