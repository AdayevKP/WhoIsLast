package com.whoslast.group;
import com.whoslast.response.ErrorCodes;
import com.whoslast.response.ServerResponse;
import com.whoslast.controllers.SuperuserRepository;
import com.whoslast.controllers.UserRepository;
import com.whoslast.entities.Party;

import com.whoslast.controllers.PartyRepository;
import com.whoslast.entities.Superuser;
import com.whoslast.entities.User;

public class GroupManager {
    private PartyRepository partyDatabase;
    private UserRepository userDatabase;
    private SuperuserRepository suDatabase;

    private static final String msgSuccess = "Successful group creation";
    private static final String msgFailYouInGroup = "Can't create new group because you already in other group";
    private static final String msgFailGroupExists = "Group with this name is already exists";
    private static final String msgNoSuchUser = "There is no user with this email";
    private static final String msgNoSuchGroup = "There is no group with this name";
    private static final String msgFailUserInGroup = "Can't add user to group because he already in other group";

    public GroupManager(PartyRepository partyDatabase, UserRepository userDatabase, SuperuserRepository suDatabase) {
        this.partyDatabase = partyDatabase;
        this.userDatabase = userDatabase;
        this.suDatabase = suDatabase;
    }

    private Party newGroupBuild(String grName, String userEmail)
    {
        Party newGroup = new Party();

        User foundUser = userDatabase.findUserByEmail(userEmail);

        Superuser SUser = new Superuser();
        SUser.setUserId(foundUser.getUserId());

        newGroup.setName(grName);
        newGroup.setSuperuser(SUser);

        foundUser.setGroupId(newGroup);

        suDatabase.save(SUser);
        userDatabase.save(foundUser);

        return newGroup;
    }

    public ServerResponse NewGroup(String email, String newGrName){
        ServerResponse actuallyResponse;
        User foundUser = userDatabase.findUserByEmail(email);
        if(foundUser.getPartyId() == null){
            if(partyDatabase.findGroupByName(newGrName) == null){
                Party newGroup = newGroupBuild(newGrName, email);
                partyDatabase.save(newGroup);
                actuallyResponse = new ServerResponse(msgSuccess, ErrorCodes.NO_ERROR);
            }
            else {
                actuallyResponse = new ServerResponse(msgFailGroupExists, ErrorCodes.Groups.GROUP_WITH_THIS_NAME_ALREADY_EXISTS);
            }
        }
        else{
            actuallyResponse = new ServerResponse(msgFailYouInGroup, ErrorCodes.Groups.YOU_ALREADY_HAVE_YOUR_OWN_GROUP);
        }
        return actuallyResponse;
    }

    public ServerResponse AddUserToGroup(String email, String groupName){
        User foundUser = userDatabase.findUserByEmail(email);
        Party foundGroup = partyDatabase.findGroupByName(groupName);
        ServerResponse response;
        if(foundUser == null){
            response = new ServerResponse(msgNoSuchUser, ErrorCodes.Groups.NO_USER_IN_DB);
        }
        else if(foundGroup == null){
            response = new ServerResponse(msgNoSuchGroup, ErrorCodes.Groups.NO_GROUP_IN_DB);
        }
        else{
            if(foundUser.getPartyId() == null) {
                foundUser.setGroupId(foundGroup);
                userDatabase.save(foundUser);
                response = new ServerResponse(msgSuccess, ErrorCodes.NO_ERROR);
            }
            else{
                response = new ServerResponse(msgFailUserInGroup, ErrorCodes.Groups.USER_ALREADY_IN_GROUP);
            }
        }
        return response;
    }
}
