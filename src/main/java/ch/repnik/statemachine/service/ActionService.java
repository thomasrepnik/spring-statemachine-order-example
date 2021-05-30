package ch.repnik.statemachine.service;

import ch.repnik.statemachine.Events;
import ch.repnik.statemachine.States;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Service;

@Service
public class ActionService {


    public Action<States, Events> pack() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                System.out.println(context.getStateMachine().getId() + ": Paketierungs Service wird konsumiert");
            }
        };

    }

    public Action<States, Events> deliver() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                if (context.getExtendedState().get("fail", Boolean.class) == Boolean.TRUE){
                    throw new IllegalStateException("Sorry, ich musste failen...");
                }
                System.out.println(context.getStateMachine().getId() + ": Auslieferungs Service wird konsumiert");
            }
        };
    }
}
