package com.whoslast.group;
import com.whoslast.ErrorCodes;
import com.whoslast.authorization.AuthResponse;
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

    private static final String msgSuccess = "Successful gruop creation";
    private static final String msgFailYouinGroup = "Can't create new group because you already in other group";
    private static final String msgFailGroupExists = "Group with this name is already exists";


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

        suDatabase.save(SUser);

        newGroup.setName(grName);
        newGroup.setSuperuser(SUser);

        foundUser.setGroupId(newGroup);

        return newGroup;
    }

    public AuthResponse NewGroup(String email, String newGrName){
        AuthResponse actuallyResponse;
        User foundUser = userDatabase.findUserByEmail(email);
        if(foundUser.getPartyId() == null){
            if(partyDatabase.findGroupByName(newGrName) == null){
                Party newGroup = newGroupBuild(newGrName, email);
                partyDatabase.save(newGroup);
                actuallyResponse = new AuthResponse(msgSuccess, AuthResponse.Status.SUCCESS, ErrorCodes.NO_ERROR);
            }
            else {
                actuallyResponse = new AuthResponse(msgFailGroupExists, AuthResponse.Status.FAIL_USER, ErrorCodes.Groups.GROUP_WITH_THIS_NAME_ALREADY_EXISTS);
            }
        }
        else{
            actuallyResponse = new AuthResponse(msgFailYouinGroup, AuthResponse.Status.FAIL_USER, ErrorCodes.Groups.YOU_ALREADY_IN_GROUP);
        }
        return actuallyResponse;
    }
}
