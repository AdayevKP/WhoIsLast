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
}
