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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.websocket.server.PathParam;
import java.util.UUID;

@RestController
public class OrderController {

    @Autowired
    private StateMachineService<States, Events> stateMachineService;


    @PostMapping(path = "/order/success")
    public ResponseEntity<String> createSuccessfulOrder(@RequestBody Order order){
        String uuid = UUID.randomUUID().toString();
        getMachine(uuid, order, Boolean.FALSE);
        return ResponseEntity.ok(uuid);
    }

    @PostMapping(path = "/order/fail")
    public ResponseEntity<String> createFailingOrder(@RequestBody Order order){
        String uuid = UUID.randomUUID().toString();
        getMachine(uuid, order, Boolean.TRUE);
        return ResponseEntity.ok(uuid);
    }

    @GetMapping(path = "/retry/success/{id}")
    public ResponseEntity<String> retryFailingOrderSuccess(@PathVariable String id){
        StateMachine<States, Events> machine = getMachine(id, null, Boolean.FALSE);

        //Lese letzte Transition aus dem Context
        Transition<States, Events> lastTransition = machine.getTransitions().stream().skip(machine.getTransitions().size() - 1).findFirst().get();

        //Replay des letzten Events
        machine.sendEvent(Mono.just(MessageBuilder.withPayload(lastTransition.getTrigger().getEvent()).build())).subscribe();

        return ResponseEntity.ok(id);
    }

    @GetMapping(path = "/retry/fail/{id}")
    public ResponseEntity<String> retryFailingOrderFail(@PathVariable String id){
        StateMachine<States, Events> machine = getMachine(id, null, Boolean.TRUE);

        //Lese letzte Transition aus dem Context
        Transition<States, Events> lastTransition = machine.getTransitions().stream().skip(machine.getTransitions().size() - 1).findFirst().get();

        //Replay des letzten Events
        machine.sendEvent(Mono.just(MessageBuilder.withPayload(lastTransition.getTrigger().getEvent()).build())).subscribe();

        return ResponseEntity.ok(id);
    }

    @GetMapping(path = "/order/{id}")
    public ResponseEntity<Order> readOrder(@PathVariable String id){
        StateMachine<States, Events> machine = getMachine(id, null, Boolean.TRUE);

        //Lese letzte Transition aus dem Context
        Order order = machine.getExtendedState().get("order", Order.class);


        return ResponseEntity.ok(order);
    }

    private synchronized StateMachine<States, Events> getMachine(String id, Order order, Boolean fail) {
        System.out.println("reading machine with id " + id);
        StateMachine<States, Events> stateMachine = stateMachineService.acquireStateMachine(id);
        stateMachine.getExtendedState().getVariables().put("fail", fail);
        if (order != null){
            stateMachine.getExtendedState().getVariables().put("order", order);
        }
        stateMachine.startReactively().block();
        return stateMachine;
    }



}
