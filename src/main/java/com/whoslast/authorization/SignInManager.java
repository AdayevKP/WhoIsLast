package com.whoslast.authorization;

import com.whoslast.response.ErrorCodes;
import com.whoslast.controllers.UserRepository;
import com.whoslast.entities.User;
import com.whoslast.response.ServerResponse;

/**
 * Sign-in manager
 */
public class SignInManager extends AuthManager {
    /**
     * Data provided by user to sign in
     */
    public static class UserSignInData {
        private String email;
        private String password;

        public UserSignInData(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPassword(String password) {
            this.password = password;
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
            return this.email.isEmpty() || this.password.isEmpty();
        }
    }

    /**
     * Exception, happened during sign in procedure
     */
    private class SignInException extends Exception {
        private SignInException(String s) {
            super(s);
        }
    }

    private static final String msgSignInSuccess = "Successful sign in";
    private static final String msgSignInErrorCredentials = "Wrong pair of e-mail/password";
    private static final String msgSignInErrorEmptyFields = "Some of provided fields are empty";
    private static final String msgSignInErrorEnvironment = "Environment fail";

    public SignInManager(UserRepository userDatabase) {
        super(userDatabase);
    }

    /**
     * Get credentials of the user, data of whom were provided
     * @param signInData Data provided by user (email especially)
     * @return Credentials of user
     * @throws SignInException Exception, happened during sign in procedure
     */
    private CredentialsManager.Credentials getCredentials(UserSignInData signInData) throws SignInException {
        User user = userDatabase.findUserByEmail(signInData.getEmail());
        CredentialsManager.Credentials credentials;
        if (user == null)
            throw new SignInException(msgSignInErrorCredentials);
        else
            credentials = new CredentialsManager.Credentials(user.getHash(), user.getSalt(), user.getHashSize());
        return credentials;
    }

    /**
     * Sign-in provedure
     * @param signInData Data provided by user
     * @return Response indicating status of operation
     */
    public ServerResponse signIn(UserSignInData signInData) {
        ServerResponse response;
        try {
            if (signInData.hasEmptyFields())
                throw new SignInException(msgSignInErrorEmptyFields);
            CredentialsManager.Credentials credentials = getCredentials(signInData);
            if (!CredentialsManager.verifyPassword(signInData.getPassword(), credentials))
                throw new SignInException(msgSignInErrorCredentials);
            response = new ServerResponse(msgSignInSuccess, ErrorCodes.NO_ERROR);
        }
        catch (CredentialsManager.HashEnginePerformException e){
            response = new ServerResponse(msgSignInErrorEnvironment, ErrorCodes.Authorization.ENVIRONMENT_FAIL);
        }
        catch (CredentialsManager.BadPasswordException | SignInException e){
            response = new ServerResponse(msgSignInErrorCredentials, ErrorCodes.Authorization.WRONG_CREDENTIALS);
        }
        return response;
    }
}