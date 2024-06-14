package org.example;

import org.example.dto.Address;
import org.example.dto.User;

public class Main {
    public static void main(String[] args) {
        RandomObjectGenerator g = new RandomObjectGenerator();
        Address address = g.generate(Address.class);
        System.out.println("Address: " + address);

        User user = g.generate(User.class);
        System.out.println("User: " + user);
    }
}