package it.sky.rulesengine.examples.advertising;

import java.time.LocalDate;

import lombok.Data;

@Data
public class User {
    private final String name;
    private final String emailAddress;
    private final LocalDate birthDate;
}
