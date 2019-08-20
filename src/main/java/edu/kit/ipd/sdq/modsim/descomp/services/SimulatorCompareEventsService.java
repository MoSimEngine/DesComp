package edu.kit.ipd.sdq.modsim.descomp.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import edu.kit.ipd.sdq.modsim.descomp.data.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.Schedules;
import edu.kit.ipd.sdq.modsim.descomp.data.Simulator;

@Service
public class SimulatorCompareEventsService {

	public Model checkEquality(String smtLiba, String smtLibb) {

		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		Solver s = ctx.mkSolver();

		BoolExpr conditionA = ctx.parseSMTLIB2String(smtLiba, null, null, null, null)[0];
		BoolExpr conditionB = ctx.parseSMTLIB2String(smtLibb, null, null, null, null)[0];
		// Add Context to Solver
		s.add(ctx.mkEq(conditionA, conditionB));
		ctx.close();

		if (Status.SATISFIABLE == s.check()) {
			return s.getModel();
		}

		return null;

	}

	public Map<String, Boolean> compareEvents(Event a, Event b) {

		Map<String, Boolean> compareInformation = new HashMap<String, Boolean>();

		boolean size = (a.getEvents().size() == b.getEvents().size());

		compareInformation.put("CountScheduledEvents", size);

		for (Schedules scheduledEventByA : a.getEvents()) {
			for (Schedules scheduledEventByB : b.getEvents()) {

				String conditonA = scheduledEventByA.getCondition();
				String conditonB = scheduledEventByA.getCondition();

				Model checkCondition = checkEquality(conditonA, conditonB);

				// Condition cannot be eq
				if (null == checkCondition) {
					compareInformation.put("DelayFunctionCompareScheduledEvent" + scheduledEventByA + scheduledEventByB,
							false);
				} else {
					compareInformation.put("DelayFunctionCompareScheduledEvent" + scheduledEventByA + scheduledEventByB,
							true);
				}

				String delayA = scheduledEventByA.getDelay();
				String delayB = scheduledEventByA.getDelay();

				Model checkDelay = checkEquality(delayA, delayB);
				// Delay Funtion cannot be equal
				if (null == checkDelay) {
					compareInformation.put("DelayFunctionCompareScheduledEvent" + scheduledEventByA + scheduledEventByB,
							false);
				} else {
					compareInformation.put("DelayFunctionCompareScheduledEvent" + scheduledEventByA + scheduledEventByB,
							true);
				}

			}
		}

		return compareInformation;
	}

	public Map<Event, Map<String, Boolean>> findExactMatches(Event compareEvent, List<Simulator> simulators) {
		Map<Event, Map<String, Boolean>> exactMatchtes = new HashMap<Event, Map<String, Boolean>>();

		for (Simulator simulator : simulators) {
			for (Event event : simulator.getEvents()) {
				Map<String, Boolean> compareEvents = compareEvents(compareEvent, event);

				if (compareEvents.values().stream().noneMatch(b -> b == false)) {
					exactMatchtes.put(event, compareEvents);
				}

			}
		}

		return exactMatchtes;

	}

}
