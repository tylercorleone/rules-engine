package it.sky.rulesengine.examples.advertising;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Company {
    private final String name;
    private final LocalDate foundationDate;
}
