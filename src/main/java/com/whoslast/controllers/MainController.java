package com.whoslast.controllers;
import com.whoslast.entities.*;
import com.whoslast.queue.QueueAvailableManager;
import com.whoslast.queue.QueueCreatorManager;
import com.whoslast.queue.QueueJoinManager;
import com.whoslast.response.ErrorCodes;
import com.whoslast.response.ServerResponse;
import com.whoslast.authorization.SignUpManager;
import com.whoslast.group.GroupManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@EnableJpaRepositories
@RequestMapping(path = "/") // URL beginning
public class MainController {

    @Autowired
    protected AuthenticationManager authenticationManager;

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

    @RequestMapping(path = "/signup", method = RequestMethod.GET)
    public String signUpGet() {
        return "sign_up";
    }

    @RequestMapping(path = "/signup", method = RequestMethod.POST)
    public String signUpPost(@RequestParam(value = "inputEmail", required = true) String email,
                             @RequestParam(value = "inputPassword", required = true) String password,
                             @RequestParam(value = "inputName", required = true) String name,
                             HttpServletRequest request, Model model) {

        //register user in system
        SignUpManager signUpManager = new SignUpManager(userRepository);
        SignUpManager.UserSignUpData signUpData = new SignUpManager.UserSignUpData(name, email, password);
        ServerResponse response = signUpManager.signUp(signUpData);


        //auto login in current session
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        // generate session if one doesn't exist
        request.getSession();
        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authenticatedUser = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

        if(response.getErrorCode() == ErrorCodes.NO_ERROR)
            return "redirect:all";
        else{
            model.addAttribute("error", response.getMsg());
            return "sign_up";
        }
    }

    @RequestMapping(path = "/signin", method = RequestMethod.GET)
    public String signInGet(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null)
            model.addAttribute("error", "неверные логин и/или пароль");
        return "sign_in";
    }

    @RequestMapping(path = "/signin", method = RequestMethod.POST)
    public String signInPost(@RequestParam(value = "username", required = true) String email,
                             @RequestParam(value = "password", required = true) String password) {
        return "groupmates";
    }

    @RequestMapping(path = "/new_group", method = RequestMethod.GET)
    public String addNewGroupGet() {
        return "create_group";
    }

    @RequestMapping(path = "/new_group", method = RequestMethod.POST)
    public String addNewGroup(@RequestParam(value = "inputName") String grName, Model model) {
        ServerResponse groupResponse = null;
        String email = getCurrentEmail();
        GroupManager groupManager = new GroupManager(partyRepository, userRepository, suRepository);
        groupResponse = groupManager.NewGroup(email, grName);

        if (groupResponse != null)
            System.out.println(groupResponse.toString());
        return "groupmates";
    }

    @GetMapping(path = "/add_user_to_group")
    String addUserToGroup(){
        return  "add_user_to_group";
    }

    @PostMapping(path = "/add_user_to_group")
    String addUsertoGroupPost(@RequestParam(value = "inputEmail") String email, Model model){
        User user = userRepository.findUserByEmail(getCurrentEmail());
        Superuser suser = suRepository.findByUserId(user.getUserId());
        Party party = partyRepository.findPartyBySuperuserId(suser.getId());
        GroupManager manager = new GroupManager(partyRepository, userRepository, suRepository);
        model.addAttribute("msg", manager.AddUserToGroup(email, party.getName()));
        return "add_user_to_group";
    }

    @GetMapping(path = "/create_queue")
    public String createQueue(){
        return "create_queue";
    }

    @PostMapping(path = "/create_queue")
    public String createQueue(@RequestParam(value = "GrName") String name, Model model) {
        QueueCreatorManager manager = new QueueCreatorManager(queueRepository, partyQueueRepository);
        User user = userRepository.findUserByEmail(getCurrentEmail());
        Party party = user.getPartyId();
        boolean error = false;

        //check whether user is party leader
        if(!Objects.equals(user.getUserId(), party.getSuperuser().getUserId())){
            error = true;
            }
        else
            manager.createNewQueue(name, user.getPartyId());

        String ans = "redirect:/home_page";
        if (error)
            ans += "?error=true";
        return ans;
    }

    @GetMapping(path = "/join_queue")
    public @ResponseBody
    String joinQueue(@RequestParam String queue_id, Model model) {
        String email = getCurrentEmail();

        QueueJoinManager queueJoinManager = new QueueJoinManager(userRepository, queueRepository, userQueueRepository);
        ServerResponse response = queueJoinManager.join(new QueueJoinManager.QueueJoinData(email, queue_id));
        return response.toString();
    }

    @GetMapping(path = "/join_the_queue")
    String joinTheQueue(Model model){
        User user = userRepository.findUserByEmail(getCurrentEmail());

        model.addAttribute("userName", user.getName());
        model.addAttribute("userGroup", user.getPartyId().getSpeciality());
        model.addAttribute("userQueues", queueRepository.getQueuesEntriesAvailableToUser(user.getPartyId().getPartyId(), user.getUserId()));
        model.addAttribute("partyQueues", queueRepository.getQueuesEntriesUserAlreadyIn(user.getPartyId().getPartyId(), user.getUserId()));

        return  "join_the_queue";
    }

    @GetMapping(path = "/available_user_queues")
    public @ResponseBody
    String getAvailableUserQueues(Model model) {
        String email = getCurrentEmail();

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
    String getAvailablePartyQueues(@RequestParam String party_id, Model model) {
        ServerResponse response = getQueuesResponse(party_id, QueueAvailableManager.QueueAvailableMode.BY_PARTYID);
        if (response.isSuccess()) {
            QueueAvailableManager.QueueAvailableList queues = (QueueAvailableManager.QueueAvailableList)response.getAdditionalData();
            return queues.toString();
        } else {
            return response.toString();
        }
    }

    @GetMapping(path = "/all")
    public String getAllUsers(Model model) {
        //model.addAttribute("users", userRepository.findAll());
        //model.addAttribute("msg", "from server with love");
        return "redirect:/home_page";
    }

    private ServerResponse getQueuesResponse(String argument, QueueAvailableManager.QueueAvailableMode mode) {
        QueueAvailableManager queueAvailableManager = new QueueAvailableManager(userRepository, partyRepository, queueRepository);
        return queueAvailableManager.get_available(new QueueAvailableManager.QueueAvailableData(argument, mode));
    }

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping(path = "/home_page")
    public String homePage(@RequestParam(value = "error", required = false) String error , Model model){
        User user = userRepository.findUserByEmail(getCurrentEmail());

        if(error != null && error.equals("true"))
            model.addAttribute("error", "You are not party leader, you can't create queue :(");

        model.addAttribute("userName", user.getName());
        model.addAttribute("email", user.getEmail());
        Party party = user.getPartyId();
        if(party != null) {

            model.addAttribute("userGroup", user.getPartyId().getSpeciality());
            model.addAttribute("userQueues", queueRepository.getQueuesEntriesAvailableToUser(user.getPartyId().getPartyId(), user.getUserId()));
            model.addAttribute("partyQueues", queueRepository.getQueuesEntriesUserAlreadyIn(user.getPartyId().getPartyId(), user.getUserId()));
        }
        else{
            model.addAttribute("userGroup", "Вы не состоите ни в одной из групп");
            //model.addAttribute("msg", "You are not a member of any group");
        }
        return "user_home";
    }

    @GetMapping(path = "/groupmates")
    public String getGroupMates(Model model){
        User currentUser = userRepository.findUserByEmail(getCurrentEmail());
        if (currentUser.getPartyId() == null) {
            model.addAttribute("error", "Пользователь не состоит в группе");
        } else {
            Party party = partyRepository.findPartyByPartyId(currentUser.getPartyId().getPartyId());
            model.addAttribute("groupName", party.getName());
            Iterable<User> users = userRepository.findGroupMates(currentUser.getPartyId(), currentUser.getUserId());
            if (!users.iterator().hasNext())
                model.addAttribute("error", "У вас пока что нет одногруппников");
            else
                model.addAttribute("users", users);
        }

        return "groupmates";
    }

    @GetMapping(path = "/enter_queue")
    public String enterQueue(@RequestParam(value = "id") Integer id){

        Queue queue = queueRepository.getQueueById(id);
        QueueRecord record = new QueueRecord();
        record.setQueue(queue);
        record.setUser(userRepository.findUserByEmail(getCurrentEmail()));
        userQueueRepository.save(record);
        return "redirect:/home_page";
    }

    @GetMapping(path = "/queue")
    public String queueUserList(@RequestParam(value = "id") Integer id, Model model){
        List<Integer> ids = queueRepository.findAllUsersInQueue(id);
        List<User> users = new ArrayList<>();
        for (int el: ids){
            users.add(userRepository.findUserById(el).iterator().next());
        }
        model.addAttribute("users", users);
        model.addAttribute("groupName", users.get(0).getPartyId().getName());
        model.addAttribute("queueName", queueRepository.getQueueById(id).getQueueName());
        return "groupmates";
    }
}
