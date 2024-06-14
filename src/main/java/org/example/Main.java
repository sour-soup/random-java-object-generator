package org.example;

import org.example.dto.Address;
import org.example.dto.User;

public class Main {
    private static final RandomObjectGenerator randomObjectGenerator = new RandomObjectGenerator();

    public static void main(String[] args) {
        Address generatedAddress = randomObjectGenerator.fillNewObject(Address.class);
        System.out.println("Generated address: " + generatedAddress);

        User generatedUser = randomObjectGenerator.fillNewObject(User.class);
        System.out.println("Generated user: " + generatedUser);

        User user = new User();
        user.setName("Soup");
        randomObjectGenerator.fillExistingObject(user);
        System.out.println("Filled user: " + user);
    }
}