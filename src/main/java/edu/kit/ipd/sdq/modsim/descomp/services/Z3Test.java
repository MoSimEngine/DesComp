package edu.kit.ipd.sdq.modsim.descomp.services;

import java.util.HashMap;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Model;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class Z3Test {

	public static void main(String[] args) {

		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		Solver s = ctx.mkSolver();

		RealExpr delay = ctx.mkRealConst("delay");
		RealExpr LOADING_TIME_PER_PASSENGER = ctx.mkRealConst("LOADING_TIME_PER_PASSENGER");
		IntExpr totalSeats = ctx.mkIntConst("totalSeats");
		IntExpr waitingPassengers = ctx.mkIntConst("waitingPassengers");

		IntExpr servedPassengers = ctx.mkIntConst("servedPassengers");

		Expr ite = ctx.mkITE(ctx.mkLe(waitingPassengers, totalSeats), waitingPassengers, totalSeats);

		s.add(ctx.mkEq(servedPassengers, ite));

		BoolExpr mkEq2 = ctx.mkEq(delay, ctx.mkMul(LOADING_TIME_PER_PASSENGER, servedPassengers));

		s.add(mkEq2);

		s.add(ctx.mkEq(LOADING_TIME_PER_PASSENGER, ctx.mkReal(3)));
		s.add(ctx.mkEq(totalSeats, ctx.mkInt(3)));
		s.add(ctx.mkEq(waitingPassengers, ctx.mkInt(3)));
		s.check();
		s.getModel();
		System.out.println(s);
		
		

		// BoolExpr mkTrue = ctx.mkBool(true);
		// BoolExpr mkFalse = ctx.mkBool(true);
		// s.add(mkTrue);

		// int waitingPassengers = position.getWaitingPassengers();
		//
		// int servedPassengers = Math.min(waitingPassengers, bus.getTotalSeats());
		//
		// Utils.log(bus, "Loading " + servedPassengers + " passengers at bus stop " +
		// position + "...");
		// bus.load(servedPassengers);
		//
		// int remainingPassengers = waitingPassengers - servedPassengers;
		// position.setWaitingPassengers(remainingPassengers);
		//
		// // wait until all passengers have entered the bus
		// double loadingTime = servedPassengers *
		// LOADING_TIME_PER_PASSENGER.toSeconds().value();
		//

		// BoolExpr conditionA = ctx.parseSMTLIB2String(
		// "(declare-fun waitingPassengers () Int) (declare-fun value () Int)(assert (=
		// value (+ 1 waitingPassengers)))",
		// null, null, null, null)[0];
		//
		// IntExpr mkIntConst = ctx.mkIntConst("value");
		//
		// ctx.mkRealConst("delay");
		// ctx.mkRealConst("UNLOADING_TIME_PER_PASSENGER");
		// ctx.mkRealConst("occupiedSeats");
		// s.add(ctx.mkEq(ctx.mkRealConst("delay"),
		// ctx.mkMul(ctx.mkRealConst("UNLOADING_TIME_PER_PASSENGER"),
		// ctx.mkRealConst("occupiedSeats"))));
		// System.out.println(s);
		// IntNum mkInt2 = ctx.mkInt(0);
		//
		// s.add(conditionA);
		// s.add(ctx.mkEq(mkIntConst, mkInt2));
		// System.out.println(s);
		// Status check2 = s.check();
		// Model model = s.getModel();
		// System.out.println(model);
		// BoolExpr conditionB = ctx.parseSMTLIB2String(s.toString(), null, null, null,
		// null)[0];
		//
		// System.out.println(s);
		// s.add(ctx.mkEq(conditionA, conditionB));
		// System.out.println(s);
		// Status check = s.check();
		// System.out.println(check);
		// System.out.println(s);
		//
		// IntExpr interarrivalTime = ctx.mkIntConst("waitingPassengers");
		// IntNum mkInt = ctx.mkInt(1);
		// ArithExpr mkAdd = ctx.mkAdd(mkInt, interarrivalTime);
		//
		// BoolExpr mkEq = ctx.mkEq(mkIntConst, mkAdd);
		// s.add(mkEq);
		// System.out.println(s);

		// BoolExpr conditionA = ctx.parseSMTLIB2String(smtLiba, null, null, null,
		// null)[0];
		// BoolExpr conditionB = ctx.parseSMTLIB2String(smtLibb, null, null, null,
		// null)[0];
		// Add Context to Solver
		// s.add(ctx.mkEq(conditionA, conditionB));
		ctx.close();

	}
}
