package ch.repnik.statemachine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum States {
    CREATED,
    PACKED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
}
