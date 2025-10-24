package com.example.comp2100_ga_23s2.searchEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Tokenizes a user's input string into a list of UserQueryTokens.
 * This class is responsible for breaking down a string input into a sequence of
 * tokens representing the different components of a user's query.
 * @author Xuan An
 */
public class UserQueryTokenizer {

    /**
     * Converts an input string into a list of UserQueryTokens.
     * @param input The input string to tokenize.
     * @return A list of UserQueryTokens.
     * @throws UserQueryToken.IllegalTokenException if an invalid token is encountered.
     */
    public static List<UserQueryToken> tokenize(String input) {
        List<UserQueryToken> tokens = new ArrayList<>();

        while (!input.isEmpty()) {
            boolean matched = false;

            // Attempt to match each type of token against the current input.
            for (UserQueryToken.Type tt : UserQueryToken.Type.values()) {
                Matcher matcherInput = tt.pattern.matcher(input);
                if (matcherInput.find()) {
                    // Extract the matched token string.
                    String token = matcherInput.group();
                    Matcher matcherToken = tt.pattern.matcher(token);
                    // Retrieve the data within the token string.
                    String tokenData = null;
                    int groupId = (tt == UserQueryToken.Type.COURSE || tt == UserQueryToken.Type.UID) ? 1 : 0;
                    if (matcherToken.find()) { tokenData = matcherToken.group(groupId); }
                    // If the token is a UID, extend it to ensure it's 7 digits long.
                    if (tt == UserQueryToken.Type.UID) {
                        assert tokenData != null;
                        tokenData = extendToSeven(tokenData);
                    }
                    // Add the token to the result list.
                    tokens.add(new UserQueryToken(tokenData, tt));
                    // Update the input to point after the current token.
                    input = input.substring(token.length());
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                throw new UserQueryToken.IllegalTokenException("Invalid Token");
            }
        }
        return tokens;
    }

    /**
     * Ensures that a uid Token is exactly 7 characters long by padding or truncating it as necessary.
     * @param s The input uid string.
     * @return The uid string extended to 7 characters.
     */
    public static String extendToSeven(String s) {
        String numberPart = s.substring(1);
        int numZeros = 7 - numberPart.length();

        // If the length is less than 7, pad with zeros.
        if (numZeros > 0) {
            numberPart += repeatChar(numZeros);
        }
        // If the length is more than 7, truncate.
        else if (numZeros < 0) {
            numberPart = numberPart.substring(0, 7);
        }

        return "u" + numberPart;
    }

    /**
     * Generates a string containing a repeated character (0) a specified number of times.
     * @param count The number of times to repeat the character.
     * @return A string of repeated characters.
     */
    private static String repeatChar(int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append('0');
        }
        return sb.toString();
    }
}
