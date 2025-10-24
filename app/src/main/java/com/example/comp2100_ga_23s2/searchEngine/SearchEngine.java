package com.example.comp2100_ga_23s2.searchEngine;

import com.example.comp2100_ga_23s2.objects.AVLTree;
import com.example.comp2100_ga_23s2.objects.Course;
import com.example.comp2100_ga_23s2.objects.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Xuan An
 */
public class SearchEngine {
    /**
     * This method finds the k users whose UIs are closest to the target UID.
     * It returns a list of up to k users.
     *
     * @param users List of users.
     * @param targetUid The UID to which we want to find the closest users.
     * @param limit k number of users
     * @return List of up to k users closest to targetUid.
     */
    public List<User> findClosestUsersList(List<User> users, int targetUid,int limit) {
        return users.stream()
                .sorted((u1, u2) -> {
                    int diff1 = Math.abs(u1.uid - targetUid);
                    int diff2 = Math.abs(u2.uid - targetUid);
                    return diff1 - diff2; // Sort based on difference from target UID.
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * This method finds the k users who are closest to Target User
     * It returns a list of up to k users.
     * @param avu AVLTree of Users
     * @param user Target User to look for
     * @param limit k number of users
     * @return List of up to k users closest to targetUser
     */
    public List<User> findClosestUsersTree(AVLTree<User> avu,User user,int limit){
        return avu.kClosestValues(user,limit);
    }

    /**
     * Finds and potentially reranks a list of {@code User} objects based on a given {@code UserQuery}.
     * The method prioritizes search based on username and UID, and then reranks based on matching courses
     * if they are provided in the query.
     *
     * @param userQuery The query containing details such as username, UID, and a list of courses to match against.
     * @param avu An AVL tree containing {@code User} objects to search within.
     * @return A list of {@code User} objects that match the criteria set by the {@code UserQuery}.
     *         The list can contain up to 10 users and might be reranked based on matching courses.
     */
    public List<User> findUserInTree(UserQuery userQuery, AVLTree<User> avu) {
        List<User> users = new ArrayList<>();
        SearchEngine searchEngine = new SearchEngine();

        // Case: Both username and UID are provided in the query.
        if (userQuery.getUsername() != null && userQuery.getUid() != null) {
            User targetUser = new User(userQuery.getUsername(), Integer.parseInt(userQuery.getUid().substring(1)));
            // Find the closest users in the tree based on the provided username and UID.
            users = searchEngine.findClosestUsersTree(avu, targetUser, 10);
        }
        // Case: Only username is provided in the query.
        else if (userQuery.getUsername() != null && userQuery.getUid() == null) {
            User targetUser = new User(userQuery.getUsername(), -1);
            // Find the closest users in the tree based on the provided username.
            users = searchEngine.findClosestUsersTree(avu, targetUser, 10);
        }
        // Case: Only UID is provided in the query.
        else if (userQuery.getUsername() == null && userQuery.getUid() != null){
            int uid = Integer.parseInt(userQuery.getUid().substring(1));
            // Find the closest users in a list based on the provided UID.
            users = searchEngine.findClosestUsersList(avu.inOrder(), uid, 10);
        }
        // Case: Both UID and Username are null
        else{
            return users;
        }
        // If courses are provided in the query, rerank users based on matching courses.
        if (!userQuery.getCourses().isEmpty()) {
            int[] counts = new int[users.size()];
            for (int i = 0; i < users.size() - 1; i++) {
                int courseCount = 0;
                // Count how many courses from the query each user is enrolled in.
                for (String s : userQuery.getCourses()) {
                    for (Course c : users.get(i).getCurrentCourses()) {
                        if (c.getName().equals(s)) {
                            courseCount++;
                        }
                    }
                }
                counts[i] = courseCount;
            }
            // Rerank users based on the number of matching courses.
            users = reRank(users, counts);
        }

        return users;
    }
    /**
     * Reorder the list of Users by the displacement to the right in indices array
     * @param users List of Users in random order
     * @param displacements Relative new displacements of Users
     * @return Reordered list of Users
     */
    private List<User> reRank(List<User> users, int[] displacements) {
        int[] indices = new int[displacements.length];
        for (int i = 0; i < displacements.length; i++) {
            indices[i] = displacements[i] + i;
        }
        if (users == null || users.size() != indices.length) {
            throw new IllegalArgumentException("Invalid input");
        }
        // Pairing Users and their indices and sorting by indices
        return IntStream.range(0, users.size())
                .boxed()
                .sorted(Comparator.comparingInt(i -> indices[i]))
                .map(users::get)
                .collect(Collectors.toList());
    }
}
