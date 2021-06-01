package ch.repnik.statemachine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Events {
    PACK,
    DELIVER,
    ERROR
}
