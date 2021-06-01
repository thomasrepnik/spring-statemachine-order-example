package ch.repnik.statemachine;

import ch.repnik.statemachine.service.ActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StatemachineTests {

	@MockBean
	private ActionService actionService;
	
	@Autowired
	private StateMachineFactory<States, Events> stateMachineFactory;
	
	private StateMachine<States, Events> stateMachine;
	
	@BeforeEach
	public void setup() throws Exception {
		stateMachine = stateMachineFactory.getStateMachine();
		stateMachine.startReactively().block();
	}
	
	@Test
	public void testInitial() throws Exception {
		StateMachineTestPlan<States, Events> plan =
			StateMachineTestPlanBuilder.<States, Events>builder()
				.stateMachine(stateMachine)
				.defaultAwaitTime(2)
				.step().expectState(States.CREATED).and()
				.build();
		plan.test();
	}
	
	@Test
	public void testWithEvent() throws Exception {
		// TODO: REACTOR check if less changes is good
		StateMachineTestPlan<States, Events> plan =
			StateMachineTestPlanBuilder.<States, Events>builder()
				.stateMachine(stateMachine)
				.step()
					.expectState(States.CREATED)
					.and()
				.step()
					.sendEvent(Events.PACK)
					.expectState(States.PACKED)
	//						.expectStateChanged(2)
					.and()
				.build();
		plan.test();
	}
	
	
	
}
