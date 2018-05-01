package com.whoslast.authorization;

import com.whoslast.response.ErrorCodes;
import com.whoslast.controllers.UserRepository;
import com.whoslast.entities.User;
import com.whoslast.response.ServerResponse;

import java.util.Random;

/**
 * Sign-up manager
 */
public class SignUpManager extends AuthManager {
    /**
     * Data provided by user to sign up
     */
    public static class UserSignUpData {
        private String name;
        private String email;
        private String password;

        public UserSignUpData(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        /**
         * Is there any empty fields (empty strings)?
         * @return True -- there are empty strings, False -- there aren't any
         */
            public boolean hasEmptyFields() {
            return this.email.isEmpty() || this.name.isEmpty() || this.password.isEmpty();

        }        }

    private static final String msgSignUpSuccess = "Successful sign up";
    private static final String msgSignUpErrorEmptyFields = "Some of provided fields are empty";
    private static final String msgSignUpErrorUserExists = "A user with provided email already exists";

    private static final int REG_CODE_SIZE = 18;

    private int initialPartyId; //Value used while signing up to make user unbounded from any groups (but he has to do it after) ("dummy value")

    public SignUpManager(UserRepository userDatabase) {
        super(userDatabase);
        this.initialPartyId = 0;
    }

    public SignUpManager(UserRepository userDatabase, int initialPartyId) {
        super(userDatabase);
        this.initialPartyId = initialPartyId;
    }

    /**
     * Build credentials from user-provided data
     * @param signUpData Data provided by user
     * @return Credentials
     * @throws CredentialsManager.HashEnginePerformException Environment fail while building credentials
     * @throws CredentialsManager.BadPasswordException Provided password somehow is bad
     */
    private User buildDatabaseUser(UserSignUpData signUpData) throws CredentialsManager.HashEnginePerformException, CredentialsManager.BadPasswordException {
        CredentialsManager.Credentials credentials = CredentialsManager.buildCredentials(signUpData.getPassword());
        User newDatabaseUser = new User();

        newDatabaseUser.setName(signUpData.getName());
        newDatabaseUser.setEmail(signUpData.getEmail());
        newDatabaseUser.setSalt(credentials.getSalt());
        newDatabaseUser.setHash(credentials.getHash());
        newDatabaseUser.setHashSize(credentials.getHashSize());
        newDatabaseUser.setRegistrationCode(getRegistrationCode());

        return  newDatabaseUser;
    }

    private String getRegistrationCode() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random rnd = new Random();
        while (stringBuilder.length() < REG_CODE_SIZE) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            stringBuilder.append(SALTCHARS.charAt(index));
        }
        String code = stringBuilder.toString();
        return code;
    }

    /**
     * Sign-up procedure
     * @param signUpData Data provided by user
     * @return Response indicating status of operation
     */
    public ServerResponse signUp(UserSignUpData signUpData) {
        ServerResponse response;
        try {
            if (signUpData.hasEmptyFields()) {
                response = new ServerResponse(msgSignUpErrorEmptyFields, ErrorCodes.Common.EMPTY_FIELDS);
            } else {
                User foundUser = userDatabase.findUserByEmail(signUpData.getEmail());
                if (foundUser == null) {
                    User newDatabaseUser = buildDatabaseUser(signUpData);
                    userDatabase.save(newDatabaseUser);
                    response = new ServerResponse(msgSignUpSuccess, ErrorCodes.NO_ERROR, newDatabaseUser);
                } else {
                    response = new ServerResponse(msgSignUpErrorUserExists, ErrorCodes.Users.USER_EXISTS);
                }
            }
        }
        catch (CredentialsManager.HashEnginePerformException e){
            response = new ServerResponse(e.getMessage(), ErrorCodes.Authorization.ENVIRONMENT_FAIL);
        }
        catch (CredentialsManager.BadPasswordException e){
            response = new ServerResponse(e.getMessage(), ErrorCodes.Authorization.BAD_PASSWORD);
        }
        return response;
    }
}