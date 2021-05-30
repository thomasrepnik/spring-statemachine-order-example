/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.repnik.statemachine.listener;

import ch.repnik.statemachine.Events;
import ch.repnik.statemachine.States;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateContext.Stage;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

import java.util.LinkedList;
import java.util.List;

public class StateMachineLogListener extends StateMachineListenerAdapter<States, Events> {

	@Override
	public void stateContext(StateContext<States, Events> stateContext) {
		if (stateContext.getStage() == Stage.STATE_ENTRY) {
			System.out.println(stateContext.getStateMachine().getId() + " enter " + stateContext.getTarget().getId());
		} else if (stateContext.getStage() == Stage.STATE_EXIT) {
			System.out.println(stateContext.getStateMachine().getId() + " exit " + stateContext.getSource().getId());
		}
	}
}
