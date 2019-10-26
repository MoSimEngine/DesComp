package edu.kit.ipd.sdq.modsim.descomp.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FPSort;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Schedules;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;

@Service
public class SimulatorExecutor {

	public Map<String, String> simulationState = new HashMap<String, String>();

	public void executeSimulator(Simulator simulator) {

		Simulator example = new Simulator("Example Simulator", "");

		example.addEntities(new Entity("Example Entity"));

		Event example1Event = new Event("Example Event1");
		example1Event.addSchedulesEvent(example1Event, "true", "2.0");

		Event startEvent = new Event("STARTEVENT");
		startEvent.addSchedulesEvent(example1Event, "(assert true)",
				"(declare-fun unloadingTimePerPassenger () Int)\n" + "(declare-fun passengers () Int)\n"
						+ "(declare-fun delay () Int)\n"
						+ "(assert (= delay (* passengers unloadingTimePerPassenger)))\n");

		scheduleEvents(startEvent);

		example.addEvents(example1Event);
		example.addEvents(startEvent);
		initSimulation(example);

	}

	private boolean checkSchedulesCondition(Event event, Schedules schedules) {

		boolean scheduleEvent = false;

		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		Solver s = ctx.mkSolver();

		BoolExpr condition = ctx.parseSMTLIB2String(schedules.getCondition(), null, null, null, null)[0];

		// Add Context to Solver
		s.add(condition);

		if (Status.UNSATISFIABLE == s.check()) {
			// Keine Lösung -> Event wird nicht gescheduled
			scheduleEvent = false;
		} else {
			// Lösung gefunden -> Bedingung für Event erfüllt. Schedule nächstes Event
			scheduleEvent = true;

		}
		ctx.close();

		return scheduleEvent;
	}

	private double getDelay(Schedules schedules) {

		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		Solver sy = ctx.mkSolver();
		BoolExpr delayFunction = ctx.parseSMTLIB2String(schedules.getDelay(), null, null, null, null)[0];

		// Add Context to Solver
		sy.add(delayFunction);

		IntExpr mkIntConst = ctx.mkIntConst("passengers");
		IntExpr mkIntConst2 = ctx.mkIntConst("unloadingTimePerPassenger");
		IntNum mkInt = ctx.mkInt(100);
		IntNum mkInt2 = ctx.mkInt(200);
		sy.add(ctx.mkEq(mkIntConst, mkInt));
		sy.add(ctx.mkEq(mkIntConst2, mkInt2));

		Status q = sy.check();
		System.out.println("Solver says: " + q);

		// Get Delay of Function
		IntExpr delayConst = ctx.mkIntConst("delay");
		Expr constInterp = sy.getModel().getConstInterp(delayConst);

		System.out.println(sy.getModel());
		System.out.println(constInterp);
		double delay = Double.parseDouble(constInterp.toString());

		ctx.close();

		return delay;
	}

	private void scheduleEvents(Event event) {

		for (Schedules schedules : event.getEvents()) {

			// Check if the Event of schedules should be scheduled
			if (checkSchedulesCondition(event, schedules)) {

				// Schedule Conditon evaluated to true, evaluate Delayfunciton
				double delay = getDelay(schedules);

				System.out.println("Schedule next Event: " + schedules.getEndEvent().getName() + " in " + delay + "s");
			}

		}

	}

	private void initSimulation(Simulator simulator) {

		System.out.println("INIT");
		Map<String, Event> events = new HashMap<String, Event>(simulator.getEvents().size());

		for (Event event : simulator.getEvents()) {
			events.put(event.getName(), event);
		}

		Event event = events.get("STARTEVENT");
		for (Schedules event2 : event.getEvents()) {

			// Check if condition is met
			String condition = event2.getCondition();

			System.out.println("condition:" + condition);

			HashMap<String, String> cfg = new HashMap<String, String>();
			cfg.put("model", "true");

			Context ctx = new Context(cfg);

			Solver s = ctx.mkSolver();
			FPSort sa = ctx.mkFPSort(11, 53);
			System.out.println("Sort: " + sa);

			BoolExpr mkFalse = ctx.mkBool(true);

			System.out.println(mkFalse);

			String t = "(assert true)";

			BoolExpr f = ctx.parseSMTLIB2String(t, null, null, null, null)[0];

			BoolExpr mkTrue = ctx.mkBool(false);

			BoolExpr mkEq2 = ctx.mkEq(f, mkTrue);

			s.add(mkEq2);

			Status check = s.check();

			System.out.println(check);

			BoolExpr cond = ctx.mkBool(false);
			BoolExpr condTrue = ctx.mkBool(true);

			Expr evaluate = s.getModel().evaluate(cond, false);
			evaluate.isTrue();

			if (evaluate.isTrue()) {

				System.out.println("Condition is true:" + evaluate.simplify().toString());
			} else {
				System.out.println("Ne... ");
			}
			ctx.close();
			// Evaluate Delay

		}

		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		System.out.println("FindModelExample1");
		// Log.append("FindModelExample1");

		BoolExpr x = ctx.mkBoolConst("x");
		BoolExpr y = ctx.mkBoolConst("y");
		BoolExpr x_xor_y = ctx.mkXor(x, y);

		Model model = check(ctx, x_xor_y, Status.SATISFIABLE);

		System.out.println(
				"x_xor_y = " + model + " x = " + model.evaluate(x, false) + ", y = " + model.evaluate(y, false));
		ctx.close();
	}

	private Model check(Context ctx, BoolExpr f, Status sat) {
		Solver s = ctx.mkSolver();
		s.add(f);
		if (s.check() != sat) {
		}

		if (sat == Status.SATISFIABLE) {

			return s.getModel();
		} else {
			return null;

		}
	}

}
