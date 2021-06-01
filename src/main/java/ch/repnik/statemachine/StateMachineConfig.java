package ch.repnik.statemachine;

import ch.repnik.statemachine.listener.StateMachineLogListener;
import ch.repnik.statemachine.service.ActionService;
import ch.repnik.statemachine.transitions.TransitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.*;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;
import reactor.core.publisher.Mono;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<States, Events> {

    @Autowired
    private StateMachineLogListener stateMachineLogListener;

    @Autowired
    private StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister;

    @Autowired
    private TransitionService transitionService;

    @Autowired
    private ActionService actionService;

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states
                .withStates()
                .initial(States.CREATED)
                .stateDo(States.CREATED, transitionService.pack(), errorAction())
                .stateDo(States.PACKED, transitionService.deliver(), errorAction())
                .state(States.RETRY)
                .end(States.DELIVERED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(States.CREATED)
                    .target(States.PACKED)
                    .event(Events.PACK)
                    .action(actionService.pack())
                    .and()
                .withExternal()
                    .source(States.PACKED)
                    .target(States.DELIVERED)
                    .event(Events.DELIVER)
                    .action(actionService.deliver(), errorAction())
                    .and()
                .withExternal()
                    .source(States.PACKED)
                    .target(States.RETRY)
                    .event(Events.ERROR)
                    .and()
                .withInternal()
                    .source(States.RETRY)
                    .action(retryAction())
                    .timer(3000)
                    .and()
                .withExternal()
                    .source(States.RETRY)
                    .target(States.PACKED)
                    .event(Events.PACK);

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister)
                .and()
                .withConfiguration()
                .listener(stateMachineLogListener);
    }

    @Bean
    public Action<States, Events> errorAction() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                context.getException().printStackTrace();
                context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(Events.ERROR).build())).subscribe();
            }
        };
    }
    
    @Bean
    public Action<States, Events> retryAction() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                final Integer count = context.getExtendedState().get("count", Integer.class);
                int newCount = (count == null ? 1 : count + 1);
                context.getExtendedState().getVariables().put("count", newCount);
                
                if (count == 3){
                    //Beim 3. Versuch wird er nicht mehr failen --> Flag wird dafür neu gesetzt
                    context.getExtendedState().getVariables().put("fail", Boolean.FALSE);
                }
                
                context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(Events.PACK).build())).subscribe();
            }
        };
    }

    @Configuration
    public static class ServiceConfig {

        //tag::snippetE[]
        @Bean
        public StateMachineService<States, Events> stateMachineService(
                StateMachineFactory<States, Events> stateMachineFactory,
                StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister) {
            return new DefaultStateMachineService<States, Events>(stateMachineFactory, stateMachineRuntimePersister);
        }
//end::snippetE[]
    }



}
