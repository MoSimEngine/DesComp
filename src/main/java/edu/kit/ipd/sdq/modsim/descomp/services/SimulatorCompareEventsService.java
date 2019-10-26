package edu.kit.ipd.sdq.modsim.descomp.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;

@Service
public class SimulatorCompareEventsService {

	public String checkEqualityDelay(String smtLiba, String smtLibb, List<String> parameters1,
			List<String> parameters2) {
		String combination = "";
		try {
			HashMap<String, String> cfg = new HashMap<String, String>();
			cfg.put("model", "true");
			Context ctx = new Context(cfg);
			Solver s = ctx.mkSolver();

			smtLibb = smtLibb.replace("delay", "delay1");

			BoolExpr[] conditionsA = ctx.parseSMTLIB2String(smtLiba, null, null, null, null);
			BoolExpr[] conditionsB = ctx.parseSMTLIB2String(smtLibb, null, null, null, null);
			s.add(conditionsA);
			s.add(conditionsB);
			s.add(ctx.mkNot(ctx.mkEq(ctx.mkRealConst("delay"), ctx.mkRealConst("delay1"))));

			List<List<String>> lists = new ArrayList<List<String>>(2);
			lists.add(parameters1);
			lists.add(parameters2);

			List<String> result = new ArrayList<String>();
			generatePermutations(lists, result, 0, "");

			for (String permParameterCombination : result) {
				// System.out.println("Checking:" + permParameterCombination);

				if (combination.isEmpty()) {

					s.push();

					String[] split = permParameterCombination.split("=");

					BoolExpr mkEq = ctx.mkEq(ctx.mkRealConst(split[1]), ctx.mkRealConst(split[2]));

					s.add(mkEq);

					if (Status.SATISFIABLE == s.check()) {

					} else {
						combination = "EQUALFunctions match with the assumption" + mkEq;
					}
				}
				s.pop();

			}
		} catch (Z3Exception e) {
			// System.out.println(e.getMessage());
		}
		return combination;

	}

	public String checkEqualityDelayCondtion(String smtLiba, String smtLibb, List<String> parameters1,
			List<String> parameters2) {
		String combination = "";
		try {

			HashMap<String, String> cfg = new HashMap<String, String>();
			cfg.put("model", "true");
			Context ctx = new Context(cfg);
			Solver s = ctx.mkSolver();

			BoolExpr[] conditionsA = ctx.parseSMTLIB2String(smtLiba, null, null, null, null);
			BoolExpr[] conditionsB = ctx.parseSMTLIB2String(smtLibb, null, null, null, null);
			s.add(ctx.mkNot(ctx.mkEq(conditionsA[0], conditionsB[0])));

			List<List<String>> lists = new ArrayList<List<String>>(2);
			lists.add(parameters1);
			lists.add(parameters2);

			List<String> result = new ArrayList<String>();
			generatePermutations(lists, result, 0, "");

			for (String permParameterCombination : result) {
				// System.out.println("Checking:" + permParameterCombination);

				if (combination.isEmpty()) {

					s.push();

					String[] split = permParameterCombination.split("=");

					BoolExpr mkEq = ctx.mkEq(ctx.mkRealConst(split[1]), ctx.mkRealConst(split[2]));

					s.add(mkEq);

					if (Status.SATISFIABLE == s.check()) {

					} else {
						combination = "EQUALFunctions match with the assumption" + mkEq;
					}
				}
				s.pop();

			}
		} catch (Z3Exception e) {
			// System.out.println(e.getMessage());
		}
		return combination;

	}

	public String checkEqualityWrite(String smtLiba, String smtLibb, List<String> parameters1,
			List<String> parameters2) {
		String combination = "";
		try {

			HashMap<String, String> cfg = new HashMap<String, String>();
			cfg.put("model", "true");
			Context ctx = new Context(cfg);
			Solver s = ctx.mkSolver();

			smtLibb = smtLibb.replace("value", "value1");

			BoolExpr[] conditionsA = ctx.parseSMTLIB2String(smtLiba, null, null, null, null);
			BoolExpr[] conditionsB = ctx.parseSMTLIB2String(smtLibb, null, null, null, null);
			s.add(conditionsA);
			s.add(conditionsB);
			s.add(ctx.mkNot(ctx.mkEq(ctx.mkIntConst("value"), ctx.mkIntConst("value1"))));

			List<List<String>> lists = new ArrayList<List<String>>(2);
			lists.add(parameters1);
			lists.add(parameters2);

			List<String> result = new ArrayList<String>();
			generatePermutations(lists, result, 0, "");

			for (String permParameterCombination : result) {
				// System.out.println("Checking:" + permParameterCombination);
				if (combination.isEmpty()) {

					s.push();

					String[] split = permParameterCombination.split("=");

					BoolExpr mkEq = ctx.mkEq(ctx.mkIntConst(split[1]), ctx.mkIntConst(split[2]));

					s.add(mkEq);
					System.out.println(s);

					if (Status.SATISFIABLE == s.check()) {

					} else {
						combination = "EQUALFunctions match with the assumption" + mkEq;
					}
				}
				s.pop();

			}
		} catch (Z3Exception e) {
			// System.out.println(e.getMessage());
		}
		return combination;

	}

	public Map<String, Boolean> compareEvents(Event a, Event b) {

		Map<String, Boolean> compareInformation = new HashMap<String, Boolean>();

		boolean size = (a.getEvents().size() == b.getEvents().size());

		compareInformation.put("CountScheduledEvents", size);

		// for (Schedules scheduledEventByA : a.getEvents()) {
		// for (Schedules scheduledEventByB : b.getEvents()) {
		//
		// String conditonA = scheduledEventByA.getCondition();
		// String conditonB = scheduledEventByA.getCondition();
		//
		//// Model checkCondition = checkEquality(conditonA, conditonB, null, null);
		//
		// // Condition cannot be eq
		// if (null == checkCondition) {
		// compareInformation.put("DelayFunctionCompareScheduledEvent" +
		// scheduledEventByA + scheduledEventByB,
		// false);
		// } else {
		// compareInformation.put("DelayFunctionCompareScheduledEvent" +
		// scheduledEventByA + scheduledEventByB,
		// true);
		// }
		//
		// String delayA = scheduledEventByA.getDelay();
		// String delayB = scheduledEventByA.getDelay();
		//
		// Model checkDelay = checkEquality(delayA, delayB, null, null);
		// // Delay Funtion cannot be equal
		// if (null == checkDelay) {
		// compareInformation.put("DelayFunctionCompareScheduledEvent" +
		// scheduledEventByA + scheduledEventByB,
		// false);
		// } else {
		// compareInformation.put("DelayFunctionCompareScheduledEvent" +
		// scheduledEventByA + scheduledEventByB,
		// true);
		// }
		//
		// }
		// }
		return null;

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

	private static void generatePermutations(List<List<String>> lists, List<String> result, int depth, String current) {
		if (depth == lists.size()) {
			result.add(current);
			return;
		}

		for (int i = 0; i < lists.get(depth).size(); i++) {
			generatePermutations(lists, result, depth + 1, current + "=" + lists.get(depth).get(i));
		}
	}

}
