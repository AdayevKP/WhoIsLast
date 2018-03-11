package com.whoslast.authorization;
//Added functionality to convert given password to hash/salt structure and to verify given password with hash/salt structure
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Converts plain password to hash/salt and vice versa, verifies plain password with data in credentials
 */
public class CredentialsManager {
    public static class BadPasswordException extends Exception {
        BadPasswordException(String s) {
            super(s);
        }
    }

    public static class HashEnginePerformException extends Exception {
        HashEnginePerformException(String s) {
            super(s);
        }
    }

    /**
     * Data from password, which is placed into database (hash, salt, hash size)
     */
    public static class Credentials {
        private String hash;
        private String salt;
        private int hashSize;

        public Credentials(String hash, String salt, int hashSize) {
            this.hash = hash;
            this.salt = salt;
            this.hashSize = hashSize;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }

        public void setHashSize(int hashSize) {
            this.hashSize = hashSize;
        }

        public String getHash() {
            return this.hash;
        }

        public String getSalt() {
            return this.salt;
        }

        public int getHashSize() {
            return this.hashSize;
        }
    }

    public static final String ERROR_PASSWORD_IS_EMPTY  = "Provided password is empty";

    /**
     * Build credentials structure of hash, it's size, salt  from the password
     * @param password Password (plain)
     * @return Built credentials structure
     * @throws HashEnginePerformException Hashing went wrong in this environment (not-safe/etc.)
     * @throws BadPasswordException Bad password is provided (a.e. empty)
     */
    public static Credentials buildCredentials(String password) throws HashEnginePerformException, BadPasswordException {
        if (password.isEmpty())
            throw new BadPasswordException(ERROR_PASSWORD_IS_EMPTY);

        String[] parts = buildCredentialsParts(password).split(":");
        int hashSize = Integer.parseInt(parts[PasswordStorage.HASH_SIZE_INDEX]);
        String salt = parts[PasswordStorage.SALT_INDEX];
        String hash = parts[PasswordStorage.PBKDF2_INDEX];
        return new Credentials(hash, salt, hashSize);
    }

    /**
     * Verify provided password using data in the credentials
     * @param password Password (plain)
     * @param credentials Credentials (consisting of hash, salt, hashSize)
     * @return Result of the verification (True/False)
     * @throws HashEnginePerformException Hashing went wrong in this environment (not-safe/etc.)
     * @throws BadPasswordException Bad password is provided (a.e. empty)
     */
    public static boolean verifyPassword(String password, Credentials credentials) throws HashEnginePerformException, BadPasswordException {
        if (password.isEmpty())
            throw new BadPasswordException(ERROR_PASSWORD_IS_EMPTY);

        String mergedCredentials = buildCredentialsString(credentials);
        return checkPassword(password, mergedCredentials);
    }

    /**
     * Specifying of what an exception message from engine's exceptions will be
     * @param e Engine exception
     * @return Exception message
     */
    private static String buildExceptionMessage(Exception e) {
        return e.getMessage();
    }

    /**
     * Build credentials parts
     * @param password Password (plain)
     * @return Credentials data string
     * @throws HashEnginePerformException Hashing went wrong in this environment (not-safe/etc.)
     */
    private static String buildCredentialsParts(String password) throws HashEnginePerformException {
        try {
            return PasswordStorage.createHash(password);
        } catch(PasswordStorage.CannotPerformOperationException e) {
            throw new HashEnginePerformException(buildExceptionMessage(e));
        }
    }

    /**
     * Build credentials string
     * @param credentials Credentials structure
     * @return Credentials string
     */
    private static String buildCredentialsString(Credentials credentials) {
        String parts[] = new String[PasswordStorage.HASH_SECTIONS];
        parts[PasswordStorage.HASH_ALGORITHM_INDEX] = PasswordStorage.HASH_ALGORITHM;
        parts[1] = Integer.toString(PasswordStorage.PBKDF2_ITERATIONS);
        parts[PasswordStorage.HASH_SIZE_INDEX] = Integer.toString(credentials.getHashSize());
        parts[PasswordStorage.SALT_INDEX] = credentials.getSalt();
        parts[PasswordStorage.PBKDF2_INDEX] = credentials.getHash();
        return String.join(":", parts);
    }

    /**
     * Inner password verifying (reinterpretation of the hash engine's exceptions)
     * @param password Password (plain)
     * @param credentialsData Formed credentials string
     * @return Result of the verification (True/False)
     * @throws HashEnginePerformException Hashing went wrong in this environment (not-safe/etc.)
     */
    private static boolean checkPassword(String password, String credentialsData) throws HashEnginePerformException {
        try {
            return PasswordStorage.verifyPassword(password, credentialsData);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            throw new HashEnginePerformException(buildExceptionMessage(e));
        } catch (PasswordStorage.InvalidHashException e) {
            throw new HashEnginePerformException(buildExceptionMessage(e));
        }
    }

    /**
     * Hashing engine
     *
     * Copyright (c) 2016, Taylor Hornby
     * All rights reserved.
     * Redistribution and use in source and binary forms, with or without modification,
     * are permitted provided that the following conditions are met:
     *
     * 1. Redistributions of source code must retain the above copyright notice, this
     * list of conditions and the following disclaimer.
     *
     * 2. Redistributions in binary form must reproduce the above copyright notice,
     * this list of conditions and the following disclaimer in the documentation and/or
     * other materials provided with the distribution.
     * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
     * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
     * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
     * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
     * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
     * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
     * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
     * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
     * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
     * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */
    private static class PasswordStorage {
        @SuppressWarnings("serial")
        static public class InvalidHashException extends Exception {
            public InvalidHashException(String message) {
                super(message);
            }
            public InvalidHashException(String message, Throwable source) {
                super(message, source);
            }
        }

        @SuppressWarnings("serial")
        static public class CannotPerformOperationException extends Exception {
            public CannotPerformOperationException(String message) {
                super(message);
            }
            public CannotPerformOperationException(String message, Throwable source) {
                super(message, source);
            }
        }

        public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
        public static final String HASH_ALGORITHM = "sha1";

        // These constants may be changed without breaking existing hashes.
        public static final int SALT_BYTE_SIZE = 24;
        public static final int HASH_BYTE_SIZE = 18;
        public static final int PBKDF2_ITERATIONS = 64000;

        // These constants define the encoding and may not be changed.
        public static final int HASH_SECTIONS = 5;
        public static final int HASH_ALGORITHM_INDEX = 0;
        public static final int ITERATION_INDEX = 1;
        public static final int HASH_SIZE_INDEX = 2;
        public static final int SALT_INDEX = 3;
        public static final int PBKDF2_INDEX = 4;

        public static String createHash(String password)
                throws PasswordStorage.CannotPerformOperationException
        {
            return createHash(password.toCharArray());
        }

        public static String createHash(char[] password)
                throws PasswordStorage.CannotPerformOperationException
        {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_BYTE_SIZE];
            random.nextBytes(salt);

            // Hash the password
            byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
            int hashSize = hash.length;

            // format: algorithm:iterations:hashSize:salt:hash
            String parts = HASH_ALGORITHM + ":" +
                    PBKDF2_ITERATIONS +
                    ":" + hashSize +
                    ":" +
                    toBase64(salt) +
                    ":" +
                    toBase64(hash);
            return parts;
        }

        public static boolean verifyPassword(String password, String correctHash)
                throws PasswordStorage.CannotPerformOperationException, PasswordStorage.InvalidHashException
        {
            return verifyPassword(password.toCharArray(), correctHash);
        }

        public static boolean verifyPassword(char[] password, String correctHash)
                throws PasswordStorage.CannotPerformOperationException, PasswordStorage.InvalidHashException
        {
            // Decode the hash into its parameters
            String[] params = correctHash.split(":");
            if (params.length != HASH_SECTIONS) {
                throw new PasswordStorage.InvalidHashException(
                        "Fields are missing from the password hash."
                );
            }

            // Currently, Java only supports SHA1.
            if (!params[HASH_ALGORITHM_INDEX].equals("sha1")) {
                throw new PasswordStorage.CannotPerformOperationException(
                        "Unsupported hash type."
                );
            }

            int iterations;
            try {
                iterations = Integer.parseInt(params[ITERATION_INDEX]);
            } catch (NumberFormatException ex) {
                throw new PasswordStorage.InvalidHashException(
                        "Could not parse the iteration count as an integer.",
                        ex
                );
            }

            if (iterations < 1) {
                throw new PasswordStorage.InvalidHashException(
                        "Invalid number of iterations. Must be >= 1."
                );
            }


            byte[] salt;
            try {
                salt = fromBase64(params[SALT_INDEX]);
            } catch (IllegalArgumentException ex) {
                throw new PasswordStorage.InvalidHashException(
                        "Base64 decoding of salt failed.",
                        ex
                );
            }

            byte[] hash;
            try {
                hash = fromBase64(params[PBKDF2_INDEX]);
            } catch (IllegalArgumentException ex) {
                throw new PasswordStorage.InvalidHashException(
                        "Base64 decoding of pbkdf2 output failed.",
                        ex
                );
            }


            int storedHashSize;
            try {
                storedHashSize = Integer.parseInt(params[HASH_SIZE_INDEX]);
            } catch (NumberFormatException ex) {
                throw new PasswordStorage.InvalidHashException(
                        "Could not parse the hash size as an integer.",
                        ex
                );
            }

            if (storedHashSize != hash.length) {
                throw new PasswordStorage.InvalidHashException(
                        "Hash length doesn't match stored hash length."
                );
            }

            // Compute the hash of the provided password, using the same salt,
            // iteration count, and hash length
            byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
            // Compare the hashes in constant time. The password is correct if
            // both hashes match.
            return slowEquals(hash, testHash);
        }

        private static boolean slowEquals(byte[] a, byte[] b)
        {
            int diff = a.length ^ b.length;
            for(int i = 0; i < a.length && i < b.length; i++)
                diff |= a[i] ^ b[i];
            return diff == 0;
        }

        private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
                throws PasswordStorage.CannotPerformOperationException
        {
            try {
                PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
                SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
                return skf.generateSecret(spec).getEncoded();
            } catch (NoSuchAlgorithmException ex) {
                throw new PasswordStorage.CannotPerformOperationException(
                        "Hash algorithm not supported.",
                        ex
                );
            } catch (InvalidKeySpecException ex) {
                throw new PasswordStorage.CannotPerformOperationException(
                        "Invalid key spec.",
                        ex
                );
            }
        }

        private static byte[] fromBase64(String hex)
                throws IllegalArgumentException
        {
            return DatatypeConverter.parseBase64Binary(hex);
        }

        private static String toBase64(byte[] array)
        {
            return DatatypeConverter.printBase64Binary(array);
        }
    }
}
