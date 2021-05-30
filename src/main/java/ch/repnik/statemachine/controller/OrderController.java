package ch.repnik.statemachine.controller;

import ch.repnik.statemachine.Events;
import ch.repnik.statemachine.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.websocket.server.PathParam;
import java.util.UUID;

@RestController
public class OrderController {

    @Autowired
    private StateMachineService<States, Events> stateMachineService;


    @GetMapping(path = "/order/success")
    public ResponseEntity<String> createSuccessfulOrder(){
        String uuid = UUID.randomUUID().toString();
        getMachine(uuid, Boolean.FALSE);
        return ResponseEntity.ok(uuid);
    }

    @GetMapping(path = "/order/fail")
    public ResponseEntity<String> createFailingOrder(){
        String uuid = UUID.randomUUID().toString();
        getMachine(uuid, Boolean.TRUE);
        return ResponseEntity.ok(uuid);
    }

    @GetMapping(path = "/retry/success/{id}")
    public ResponseEntity<String> retryFailingOrderSuccess(@PathVariable String id){
        StateMachine<States, Events> machine = getMachine(id, Boolean.FALSE);

        //Lese letzte Transition aus dem Context
        Transition<States, Events> lastTransition = machine.getTransitions().stream().skip(machine.getTransitions().size() - 1).findFirst().get();

        //Replay des letzten Events
        machine.sendEvent(Mono.just(MessageBuilder.withPayload(lastTransition.getTrigger().getEvent()).build())).subscribe();

        return ResponseEntity.ok(id);
    }

    @GetMapping(path = "/retry/fail/{id}")
    public ResponseEntity<String> retryFailingOrderFail(@PathVariable String id){
        StateMachine<States, Events> machine = getMachine(id, Boolean.TRUE);

        //Lese letzte Transition aus dem Context
        Transition<States, Events> lastTransition = machine.getTransitions().stream().skip(machine.getTransitions().size() - 1).findFirst().get();

        //Replay des letzten Events
        machine.sendEvent(Mono.just(MessageBuilder.withPayload(lastTransition.getTrigger().getEvent()).build())).subscribe();

        return ResponseEntity.ok(id);
    }

    private synchronized StateMachine<States, Events> getMachine(String id, Boolean fail) {
        System.out.println("reading machine with id " + id);
        StateMachine<States, Events> stateMachine = stateMachineService.acquireStateMachine(id);
        stateMachine.getExtendedState().getVariables().put("fail", fail);
        stateMachine.startReactively().subscribe();
        return stateMachine;
    }



}
