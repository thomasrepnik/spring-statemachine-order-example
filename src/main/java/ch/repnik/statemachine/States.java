package ch.repnik.statemachine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum States {
    CREATED,
    PACKED,
    DELIVERED,
    RETRY,
    FAILED
}
