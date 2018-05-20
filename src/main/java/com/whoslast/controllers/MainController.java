package com.whoslast.controllers;

import com.whoslast.entities.*;
import com.whoslast.queue.QueueAvailableManager;
import com.whoslast.queue.QueueCreatorManager;
import com.whoslast.response.ErrorCodes;
import com.whoslast.response.ServerResponse;
import com.whoslast.authorization.SignUpManager;
import com.whoslast.group.GroupManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private JavaMailSenderImpl mailSender;

    // @ResponseBody means the returned String is the response, not a view name
    // @RequestParam means it is a parameter from the GET or POST request

    @RequestMapping(path = "/signup", method = RequestMethod.GET)
    public String signUpGet(Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);
        return "sign_up";
    }

    @RequestMapping(path = "/signup", method = RequestMethod.POST)
    public String signUpPost(@RequestParam(value = "inputEmail") String email,
                             @RequestParam(value = "inputPassword") String password,
                             @RequestParam(value = "inputName") String name,
                             HttpServletRequest request, Model model) {

        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);
        //register user in system
        SignUpManager signUpManager = new SignUpManager(userRepository);
        SignUpManager.UserSignUpData signUpData = new SignUpManager.UserSignUpData(name, email, password);
        ServerResponse response = signUpManager.signUp(signUpData);

        /* //Закомментил авто-вход, так как нужно подтвердить e-mail
        //auto login in current session
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        // generate session if one doesn't exist
        request.getSession();
        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authenticatedUser = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);*/



        if (response.getErrorCode() == ErrorCodes.NO_ERROR) {
            user = (User) response.getAdditionalData();
            try {
                sendConfirmationMessage(request.getHeader("host"), (User) response.getAdditionalData());
            } catch (MessagingException e) {
                model.addAttribute("error", "Проблема с отправкой e-mail. Повторите регистрацию");
                userRepository.delete(user);
                return "sign_up";
            }
            model.addAttribute("notification", "Для окончания регистрации подтвердите свой e-mail (проверьте электронную почту)");
            return "start_page";
        } else {
            model.addAttribute("error", response.getMsg());
            return "sign_up";
        }
    }

    private void sendConfirmationMessage(String host, User user) throws MessagingException {
        String link = "http://" + host + "/verify?email="+user.getEmail()+"&registrationCode="+user.getRegistrationCode();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mailMsg = new MimeMessageHelper(mimeMessage);
        mailMsg.setFrom("WhoIsLastQueueNotifier@gmail.com");

        mailMsg.setTo(user.getEmail());
        mailMsg.setSubject("Подтверждение регистрации");
        mailMsg.setText("Ваш логин: "+ user.getName() +"\nДля подтверждения регистрации перейдите по ссылке: " + link + "\nВы будете перенаправлены на страницу входа");
        mailSender.send(mimeMessage);
    }

    @RequestMapping(path = "/verify", method = RequestMethod.GET)
    public String verifyEmail(String email, String registrationCode, Model model) {
        User user = userRepository.findUserByEmail(email);
        if (user != null && user.getRegistrationCode() != null && user.getRegistrationCode().equals(registrationCode)) {
            user.setRegistrationCode(null);
            userRepository.save(user);
            model.addAttribute("notification", "Успешное подтверждение e-mail!");
            return "sign_in";
        } else {
            model.addAttribute("error", "Недействительная ссылка подтверждения e-mail");
            return "sign_in";
        }
    }

    @RequestMapping(path = "/signin", method = RequestMethod.GET)
    public String signInGet(@RequestParam(value = "error", required = false) String error, Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);
        if (error != null) {
            model.addAttribute("error", "Ошибка входа");
            model.addAttribute("notification", "Возможно, этот аккаунт не был активирован");
        }
        return "sign_in";
    }

    @RequestMapping(path = "/signin", method = RequestMethod.POST)
    public String signInPost(@RequestParam(value = "username", required = true) String email,
                             @RequestParam(value = "password", required = true) String password,
                             Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);
        return "groupmates";
    }

    @RequestMapping(path = "/new_group", method = RequestMethod.GET)
    public String addNewGroupGet(Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);
        return "create_group";
    }

    @RequestMapping(path = "/new_group", method = RequestMethod.POST)
    public String addNewGroup(@RequestParam(value = "inputName") String grName, Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);

        ServerResponse groupResponse = null;
        String email = getCurrentEmail();
        GroupManager groupManager = new GroupManager(partyRepository, userRepository, suRepository);
        groupResponse = groupManager.NewGroup(email, grName);

        if (groupResponse != null)
            model.addAttribute("msg", groupResponse.getMsg());

        return "redirect:/groupmates";
    }

    @GetMapping(path = "/add_user_to_group")
    String addUserToGroup(Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);

        return "add_user_to_group";
    }

    @GetMapping(path = "/")
    String greetingPage(Model model) {
        model.addAttribute("logged", false);
        model.addAttribute("superuser", false);
        return "start_page";
    }

    @PostMapping(path = "/add_user_to_group")
    String addUsertoGroupPost(@RequestParam Map<String, String> map, Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);
        Superuser suser = suRepository.findByUserId(user.getUserId());

        //if current user doesn't own group, return with error message
        if (suser == null || !user.getUserId().equals(suser.getUserId())) {
            model.addAttribute("error", "Вы не владелец группы и не можете добавлять юзеров в группу");
            return "add_user_to_group";
        }

        Party party = partyRepository.findPartyBySuperuserId(suser.getId());

        StringBuilder error = new StringBuilder();
        GroupManager manager = new GroupManager(partyRepository, userRepository, suRepository);
        ServerResponse response;
        List<String> addedUsers = new ArrayList<>();
        for (String email : map.keySet()) {
            if (!map.get(email).isEmpty()) {
                response = manager.AddUserToGroup(map.get(email), party.getName());
                if (response.getErrorCode() != ErrorCodes.NO_ERROR) {
                    if (error.length() == 0)
                        error = new StringBuilder(response.getMsg() + ": ");
                    error.append(map.get(email)).append(" ");
                    //model.addAttribute("error", response + ": " + map.get(email));
                } else {
                    addedUsers.add(map.get(email));
                }
            }
        }
        if (error.length() > 0) {
            model.addAttribute("error", error.toString());
        }
        StringBuilder msg = new StringBuilder("Успешно добавлены пользователи" + " : ");
        for (String el : addedUsers) {
            msg.append(el).append(", ");
        }

        if(!addedUsers.isEmpty())
        model.addAttribute("msg", msg);
        return "add_user_to_group";
    }

    @GetMapping(path = "/create_queue")
    public String createQueue(Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);

        return "create_queue";
    }

    @PostMapping(path = "/create_queue")
    public String createQueue(@RequestParam(value = "GrName") String name, Model model) {
        QueueCreatorManager manager = new QueueCreatorManager(queueRepository, partyQueueRepository);
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);


        Party party = user.getPartyId();
        boolean error = false;

        //check whether user is party leader
        if (!Objects.equals(user.getUserId(), party.getSuperuser().getUserId())) {
            error = true;
        } else {
            Iterable<Queue> queues = queueRepository.findByQueueName(name);

            for (Queue q : queues) {
                PartyQueue records = partyQueueRepository.findByQueue(q);

                if (records.getPartyId().getPartyId().equals(party.getPartyId())) {
                    model.addAttribute("error", "группа с таким именем уже существует");
                    return "create_queue";
                }
            }
            manager.createNewQueue(name, party);
            new Thread(() -> {
                try {
                    sendNotifications(name, user);
                } catch (MessagingException e) {
                    System.out.println("no internet connection");
                }
            }).start();
            return "redirect:/home_page";
        }
        return "redirect:/home_page?error=true";
    }

    private void sendNotifications(String name, User user) throws MessagingException {
        Iterable<User> groupmates = userRepository.findGroupMates(user.getPartyId(), user.getUserId());

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mailMsg = new MimeMessageHelper(mimeMessage);
        mailMsg.setFrom("WhoIsLastQueueNotifier@gmail.com");

        for (User student : groupmates) {
            mailMsg.setTo(student.getEmail());
            mailMsg.setSubject("Оповещение о создании новой очереди");
            mailMsg.setText("Создана новая очередь: " + name);
            mailSender.send(mimeMessage);
        }
    }

    @GetMapping(path = "/join_the_queue")
    String joinTheQueue(Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);
        model = setAttributes(model, user);
        if (user.getPartyId() == null) {
            model.addAttribute("error", "Пользователь не состоит в группе");
        }
        return "join_the_queue";
    }

    @GetMapping(path = "/all")
    public String getAllUsers(Model model) {
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
    public String homePage(@RequestParam(value = "error", required = false) String error, Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);


        if (error != null && error.equals("true"))
            model.addAttribute("error", "You are not party leader, you can't create queue :(");

        model = setAttributes(model, user);
        return "home_page";
    }

    private Model setAttributes(Model model, User user) {
        model.addAttribute("userName", user.getName());
        model.addAttribute("email", user.getEmail());
        Party party = user.getPartyId();
        if (party != null) {
            model.addAttribute("userGroup", user.getPartyId().getSpeciality());
            model.addAttribute("userQueues", queueRepository.getQueuesEntriesAvailableToUser(user.getPartyId().getPartyId(), user.getUserId()));
            model.addAttribute("partyQueues", queueRepository.getQueuesEntriesUserAlreadyIn(user.getPartyId().getPartyId(), user.getUserId()));
        } else {
            model.addAttribute("userGroup", "Вы не состоите ни в одной из групп");
        }
        return model;
    }

    @GetMapping(path = "/groupmates")
    public String getGroupMates(Model model) {
        User currentUser = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, currentUser);

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
    public String enterQueue(@RequestParam(value = "id") Integer id) {

        Queue queue = queueRepository.getQueueById(id);
        QueueRecord record = new QueueRecord();
        record.setQueue(queue);
        record.setUser(userRepository.findUserByEmail(getCurrentEmail()));
        userQueueRepository.save(record);
        return "redirect:/home_page";
    }

    @PostMapping(path = "/enter_queue")
    public String enterQueuePost(@RequestParam Map<String, String> map, Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);

        for (String el : map.keySet()) {
            Queue queue = queueRepository.getQueueById(Integer.valueOf(map.get(el)));
            QueueRecord record = new QueueRecord();
            record.setQueue(queue);
            record.setUser(user);
            userQueueRepository.save(record);
        }

        return "redirect:/home_page";
    }

    @PostMapping(path = "/delete_queue")
    public String deleteQueue(@RequestParam Map<String, String> map, Model model){
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);
        model = setAttributes(model, user);
        if(!user.getPartyId().getSuperuser().getUserId().equals(user.getUserId()))
            model.addAttribute("error", "Пользователь не является старостой группы");
        else {
            for (String el: map.keySet()){
                Queue queue = queueRepository.getQueueById(Integer.valueOf(map.get(el)));
                PartyQueue pq = partyQueueRepository.findByQueue(queue);
                partyQueueRepository.delete(pq);
                queueRepository.delete(queue);
                }
        }
        return "join_the_queue";
    }

    @PostMapping(path = "/quit_queue")
    public String quitQueuePost(@RequestParam Map<String, String> map, Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);

        for (String el : map.keySet()) {
            Queue queue = queueRepository.getQueueById(Integer.valueOf(map.get(el)));
            QueueRecord record = userQueueRepository.findByQueueAndUser(queue, user);
            record.setQueue(null);
            record.setUser(null);
            userQueueRepository.save(record);
        }

        return "redirect:/home_page";
    }

    @GetMapping(path = "/queue")
    public String queueUserList(@RequestParam(value = "id") Integer id, Model model) {
        User user = userRepository.findUserByEmail(getCurrentEmail());
        model = setRights(model, user);
        List<Integer> ids = queueRepository.findAllUsersInQueue(id);
        List<User> users = new ArrayList<>();
        for (int el : ids) {
            users.add(userRepository.findUserById(el).iterator().next());
        }
        model.addAttribute("users", users);
        model.addAttribute("groupName", user.getPartyId().getName());
        model.addAttribute("queueName", queueRepository.getQueueById(id).getQueueName());
        return "groupmates";
    }


    private Model setRights(Model model, User user) {
        //rights for displaying buttons
        if (user != null) {

            model.addAttribute("logged", true);
            if (suRepository.findByUserId(user.getUserId()) != null)
                model.addAttribute("superuser", true);
            else
                model.addAttribute("superuser", false);
        } else {
            model.addAttribute("logged", false);
            model.addAttribute("superuser", false);
        }
        return model;
    }
}
