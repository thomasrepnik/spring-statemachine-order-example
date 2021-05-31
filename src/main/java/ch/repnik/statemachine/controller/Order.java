package ch.repnik.statemachine.controller;

import lombok.Data;

import java.io.Serializable;

@Data
public class Order implements Serializable {

    private String customer;
    private int articleNumber;
    private int count;

}
