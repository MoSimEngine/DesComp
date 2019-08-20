package edu.kit.ipd.sdq.modsim.descomp.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FPSort;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import edu.kit.ipd.sdq.modsim.descomp.data.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.Schedules;
import edu.kit.ipd.sdq.modsim.descomp.data.Simulator;

@Service
public class SimulatorExecutor {

	public void executeSimulator(Simulator simulator) {

		Simulator example = new Simulator("Example Simulator", "");

		example.addEntitys(new Entity("Example Entity"));

		Event example1Event = new Event("Example Event1", "");
		example1Event.addSchedulesEvent(example1Event, "true", "2.0");

		Event startEvent = new Event("STARTEVENT", "");
		startEvent.addSchedulesEvent(example1Event, "(assert true)",
				"(declare-fun unloadingTimePerPassenger () Int)\n" + "(declare-fun passengers () Int)\n"
						+ "(declare-fun delay () Int)\n"
						+ "(assert (= delay (* passengers unloadingTimePerPassenger)))\n");

		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		BoolExpr x = ctx.mkBoolConst("x");
		BoolExpr y = ctx.mkBoolConst("y");
//		BoolExpr x_xor_y = ctx.mkXor(x, y);

		String cond = "(declare-fun y () Bool)" + "(declare-fun x () Bool)" + "(assert (xor x y))";
		BoolExpr f = ctx.parseSMTLIB2String(cond, null, null, null, null)[0];

		Solver s = ctx.mkSolver();
//		s.add(x_xor_y);
		s.add(f);
		// System.out.println(s);

		IntNum mkInt = ctx.mkInt(100);
		IntNum mkInt2 = ctx.mkInt(200);

		for (Schedules schedules : startEvent.getEvents()) {

			BoolExpr delayFunction = ctx.parseSMTLIB2String(schedules.getDelay(), null, null, null, null)[0];
			IntExpr mkIntConst = ctx.mkIntConst("passengers");
			IntExpr mkIntConst2 = ctx.mkIntConst("unloadingTimePerPassenger");

			Solver sy = ctx.mkSolver();

			sy.add(delayFunction);
			System.out.println(sy);

			IntExpr mkIntConst3 = ctx.mkIntConst("delay");
			sy.add(ctx.mkEq(mkIntConst, mkInt));
			sy.add(ctx.mkEq(mkIntConst2, mkInt2));
			Status q = sy.check();
			System.out.println("Solver says: " + q);

			// Get Delay of Function
			Expr constInterp = sy.getModel().getConstInterp(mkIntConst3);
			System.out.println(constInterp);
		}

//		ArithExpr mkMul = ctx.mkMul(mkIntConst, mkIntConst2);

//		FuncDecl[] constDecls = sy.getModel().getConstDecls();
//		for (FuncDecl funcDecl : constDecls) {
//			System.out.println(funcDecl.toString());
//		}

		System.out.println("Model: \n");

		BoolExpr mkTrue = ctx.mkBool(true);
		BoolExpr mkFalse = ctx.mkBool(false);
		BoolExpr mkEq2 = ctx.mkEq(x, mkTrue);
		BoolExpr mkEq3 = ctx.mkEq(y, mkFalse);
		s.add(mkEq2);
		s.add(mkEq3);
		System.out.println(s);

		Status check = s.check();
		System.out.println(check);

//		Goal g = ctx.mkGoal(true, false, false);
//		g.add(f, mkEq2, mkEq3);
//
//		ApplyResult ar = ctx.mkTactic("ctx-solver-simplify").apply(g);
//		Goal[] subgoals = ar.getSubgoals();
//		BoolExpr[] formulas_0 = subgoals[0].getFormulas();

		if (Status.UNSATISFIABLE == check) {
			// Keine Lösung -> Event wird nicht gescheduled
			Expr proof = s.getProof();
			System.out.println(proof);
		} else {
			// Lösung gefunden -> Bedingung für Event erfüllt. Schedule nächstes Event
			Model model = s.getModel();
			System.out.println(model);

		}
		ctx.close();

		example.addEvents(example1Event);
		example.addEvents(startEvent);
		initSimulation(example);

	}

	private void initSimulation(Simulator simulator) {

		System.out.println("INIT");
		Map<String, Event> events = new HashMap<String, Event>(simulator.getEvents().size());

		for (Event event : simulator.getEvents()) {
			events.put(event.name, event);
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

			BoolExpr mkEq = ctx.mkEq(cond, condTrue);

			FuncDecl[] funcDecls = s.getModel().getFuncDecls();

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
