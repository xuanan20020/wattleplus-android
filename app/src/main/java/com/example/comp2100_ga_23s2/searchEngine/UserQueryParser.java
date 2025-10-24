package com.example.comp2100_ga_23s2.searchEngine;

import java.util.List;

/**
 * Parses a list of UserQueryTokens to produce a UserQuery object.
 * This class is responsible for parsing a sequence of tokens representing a user's query
 * into a structured UserQuery object.
 * @author Xuan An
 */
public class UserQueryParser {
    private final List<UserQueryToken> tokens;  // Tokens to be parsed
    private int pos = 0;                        // Current position in the token list

    /**
     * Constructor that initializes the token list.
     * @param tokens List of tokens to be parsed.
     */
    public UserQueryParser(List<UserQueryToken> tokens) {
        this.tokens = tokens;
    }

    /**
     * Accepts the given token type if the current token matches.
     * @param tt The expected token type.
     * @return true if the current token matches, otherwise false.
     */
    private boolean accept(UserQueryToken.Type tt) {
        if (pos < tokens.size() && tokens.get(pos).type == tt) {
            pos++;
            return true;
        }
        return false;
    }

    /**
     * Expects the given token type to be the current token and throws an exception if it doesn't match.
     * @param tt The expected token type.
     */
    private void expect(UserQueryToken.Type tt) {
        if (!accept(tt)) {
            throw new RuntimeException("Expected " + tt.name());
        }
    }

    /**
     * Parses the tokens into a UserQuery object.
     * @return The parsed UserQuery object.
     * @throws RuntimeException if the tokens do not match the expected format.
     */
    public UserQuery parse() throws RuntimeException {
        UserQuery query = new UserQuery();
        consumeSpace();
        expressionList(query);

        if (pos != tokens.size()) {
            throw new RuntimeException("Unexpected token: " + tokens.get(pos));
        }
        return query;
    }

    // Recursively processes expressions from the token list and adds them to the query.
    private void expressionList(UserQuery query) {
        UserQueryToken.Type expType = expression(query);
        // If there's a space after an expression, parse the next block
        if (accept(UserQueryToken.Type.SPACE)) {
            consumeSpace();
            if (pos >= tokens.size()) return;
            if (tokens.get(pos).type == UserQueryToken.Type.USERNAME ||
                    tokens.get(pos).type == UserQueryToken.Type.UID) {
                exp_ID_Or_Username(query,expType);
            } else {
                expNo_ID_No_Username(query);
            }
        }
    }

    // Processes a single expression token and updates the query accordingly.
    private UserQueryToken.Type expression(UserQuery query) {
        if (accept(UserQueryToken.Type.UID)) {
            query.setUid(tokens.get(pos - 1).token);
            return UserQueryToken.Type.UID;
        } else if (accept(UserQueryToken.Type.COURSE)) {
            query.addCourse(tokens.get(pos - 1).token);
            return UserQueryToken.Type.COURSE;
        } else if (accept(UserQueryToken.Type.USERNAME)) {
            query.setUsername(tokens.get(pos - 1).token);
            return UserQueryToken.Type.USERNAME;
        } else {
            throw new RuntimeException("Expected expression");
        }
    }

    // Processes a block of tokens that doesn't begin with UID or USERNAME.
    private void expNo_ID_No_Username(UserQuery query) {
        if (pos >= tokens.size()) return;
        expect(UserQueryToken.Type.COURSE);
        query.addCourse(tokens.get(pos - 1).token);
        if (accept(UserQueryToken.Type.SPACE)) {
            consumeSpace();
            expNo_ID_No_Username(query);
        }
    }

    // Processes a block of tokens that begins with either UID or USERNAME.
    private void exp_ID_Or_Username(UserQuery query, UserQueryToken.Type expType) {
        if (expType == UserQueryToken.Type.COURSE){
            throw new RuntimeException("Incorrect Argument Order");
        }
        if (tokens.get(pos).type == UserQueryToken.Type.USERNAME) {
            if (expType == UserQueryToken.Type.USERNAME){
                throw new RuntimeException("More than 1 Username Found");
            } else {
                accept(UserQueryToken.Type.USERNAME);
                query.setUsername(tokens.get(pos - 1).token);
            }

        } else {
            if (expType == UserQueryToken.Type.UID){
                throw new RuntimeException("More than 1 UID Found");
            } else {
                accept(UserQueryToken.Type.UID);
                query.setUid(tokens.get(pos - 1).token);
            }
        }
        // After processing UID or USERNAME, if there's a space, then parse the next block.
        if (accept(UserQueryToken.Type.SPACE)) {
            consumeSpace();
            expNo_ID_No_Username(query);
        }
    }

    // Skips over any SPACE tokens in the list.
    private void consumeSpace() {
        while (pos < tokens.size() && tokens.get(pos).type == UserQueryToken.Type.SPACE) {
            pos++;  // Increment the position to move to the next token
        }
    }
}
