package com.example.comp2100_ga_23s2.searchEngine;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents an individual token from a user's query.
 * A token can be of type COURSE, UID, USERNAME, or SPACE.
 * @author Xuan An
 */
public class UserQueryToken {

    // Type of the token (e.g., COURSE, UID, USERNAME, SPACE).
    Type type;

    // The actual string representation of the token.
    String token;

    /**
     * Creates a new UserQueryToken.
     * @param token The string representation of the token.
     * @param type  The type of the token.
     */
    public UserQueryToken(String token, Type type){
        this.token = token;
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserQueryToken{" +
                "type=" + type +
                ", token='" + token + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserQueryToken that = (UserQueryToken) o;
        return type == that.type && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, token);
    }

    /**
     * Enumerates the different types of tokens that can be recognized.
     */
    public enum Type {
        COURSE("course:([A-Z]{4}\\d{4})"),  // Expected pattern for a course, e.g., "course:COMP1001".
        UID("id:(u\\d+)"),                  // Expected pattern for a user ID, e.g., "id:u1234567".
        USERNAME("[a-zA-Z0-9]+"),          // Expected pattern for a username.
        SPACE(" ");                         // Represents spaces between tokens.

        // Compiled pattern for regular expression associated with each token type.
        final Pattern pattern;

        /**
         * Creates a new Type.
         * @param regex Regular expression string for the token type.
         */
        Type(String regex) {
            this.pattern = Pattern.compile("^" + regex);
        }
    }

    /**
     * Represents an exception that should be thrown if a tokenizer
     * attempts to tokenize something that is not of one of the recognized token types.
     */
    public static class IllegalTokenException extends IllegalArgumentException {
        public IllegalTokenException(String errorMessage) {
            super(errorMessage);
        }
    }
}
