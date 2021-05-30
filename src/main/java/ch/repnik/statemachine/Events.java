package ch.repnik.statemachine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Events {
    PAY,
    SHIP,
    PACK,
    RESIGN,
    DELIVER,
    SEND_SURVEY,
    CLIENT_RETURN
}
