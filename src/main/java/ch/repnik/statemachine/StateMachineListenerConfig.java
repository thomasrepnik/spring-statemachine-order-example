package ch.repnik.statemachine;

import ch.repnik.statemachine.listener.StateMachineLogListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StateMachineListenerConfig {

    @Bean
    public StateMachineLogListener stateMachineLogListener() {
        return new StateMachineLogListener();
    }

}
