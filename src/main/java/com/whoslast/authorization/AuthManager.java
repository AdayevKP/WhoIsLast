package com.whoslast.authorization;

import com.whoslast.controllers.UserRepository;

/**
 * Sign up / sign in (authorization) manager base class
 */
abstract class AuthManager {
    protected UserRepository userDatabase;

    AuthManager(UserRepository userDatabase) {
        this.userDatabase = userDatabase;
    }
}
