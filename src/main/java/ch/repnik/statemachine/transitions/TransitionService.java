package ch.repnik.statemachine.transitions;

import ch.repnik.statemachine.Events;
import ch.repnik.statemachine.States;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.sound.midi.Soundbank;

@Service
public class TransitionService {

    @Bean
    public Action<States, Events> pack() {
        return new Action<States, Events>() {

            @Override
            public void execute(StateContext<States, Events> context) {
                System.out.println("Bestellung verpackt!");
                context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(Events.PACK).build())).subscribe();
            }
        };
    }


    @Bean
    public Action<States, Events> deliver() {
        return new Action<States, Events>() {

            @Override
            public void execute(StateContext<States, Events> context) {
                System.out.println("Bestellung versendet!");
                context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(Events.DELIVER).build())).subscribe();
            }
        };
    }




}
