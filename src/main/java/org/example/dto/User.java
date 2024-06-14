package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
    public String name;
    public Integer age;
    public Address address;
}
