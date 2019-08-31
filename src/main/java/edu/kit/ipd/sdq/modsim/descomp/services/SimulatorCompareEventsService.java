package edu.kit.ipd.sdq.modsim.descomp.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import edu.kit.ipd.sdq.modsim.descomp.data.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.Schedules;
import edu.kit.ipd.sdq.modsim.descomp.data.Simulator;

@Service
public class SimulatorCompareEventsService {

	public Model checkEquality(String smtLiba, String smtLibb, List<String> parameters1, List<String> parameters2) {

		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		Solver s = ctx.mkSolver();

		String a = "(declare-fun readProcessingRate () Real)(declare-fun abstractDemand () Real)(declare-fun value () Real)(assert (= value (/ abstractDemand readProcessingRate)))";
		String b = "(declare-fun writeProcessingRate () Real)(declare-fun abstractDemand () Real)(declare-fun value () Real)(assert (= value (/ abstractDemand writeProcessingRate)))";
		FuncDecl[] funcDecls = null;

		BoolExpr[] conditionsA = ctx.parseSMTLIB2String(smtLiba, null, null, null, funcDecls);
		BoolExpr[] conditionsB = ctx.parseSMTLIB2String(smtLibb, null, null, null, null);
		// Add Context to Solver
		s.add(ctx.mkEq(ctx.mkIntConst("demand"), ctx.mkInt(3)));
		s.add(ctx.mkEq(conditionsA[0], conditionsB[0]));
		// for (String string : parameters1) {
		// for (String string2 : parameters2) {
		// s.add(ctx.mkEq(ctx.mkSymbol(string), ctx.mkSymbol(string2)));
		// }
		// }

		if (Status.SATISFIABLE == s.check()) {
			Model model = s.getModel();
			System.out.println(model);

			FuncDecl[] funcDecls1 = model.getDecls();

			System.out.println(s);
			Status check = s.check();
			s.getModel();
			List<FuncDecl> asList = Arrays.asList(funcDecls);

			for (String string : parameters1) {
				for (String string2 : parameters2) {

				}
			}

			for (FuncDecl funcDecl : funcDecls) {

				if (parameters1.contains(funcDecl.getName())) {
				}

				System.out.println();
			}

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

				Model checkCondition = checkEquality(conditonA, conditonB, null, null);

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

				Model checkDelay = checkEquality(delayA, delayB, null, null);
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
