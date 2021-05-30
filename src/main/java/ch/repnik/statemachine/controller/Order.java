package ch.repnik.statemachine.controller;

import lombok.Data;

@Data
public class Order {

    private String customer;
    private int articleNumber;
    private int count;

}
