package edu.kit.ipd.sdq.modsim.descomp.services;

import java.util.HashMap;

import com.microsoft.z3.ArrayExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.RatNum;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Symbol;

public class Z3Test {

	public static void main(String[] args) {

		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		Solver s = ctx.mkSolver();

		Symbol name = ctx.mkSymbol("actions");

		EnumSort mkEnumSort = ctx.mkEnumSort(name, ctx.mkSymbol("AcquireAction"), ctx.mkSymbol("ExternalCall"),
				ctx.mkSymbol("ReleaseAction"), ctx.mkSymbol("InternalAction"));
		ArrayExpr mkArrayConst = ctx.mkArrayConst("behavior", ctx.getIntSort(), mkEnumSort);
		Expr sel = ctx.mkSelect(mkArrayConst, ctx.mkInt(0));
		// s.add(ctx.mkEq(sel, mkEnumSort.getConsts()[0]));

		s.add(ctx.mkEq(sel, mkEnumSort.getConsts()[2]));

		System.out.println(s);

		// (declare-datatypes ((actions 0)) (((AcquireAction) (ExternalCall)
		// (ReleaseAction) (InternalAction)))) (declare-fun behavior () (Array Int
		// actions)) (assert (= (select behavior 0) ReleaseAction))

		RealExpr countOfRunningProcesses = ctx.mkRealConst("countOfRunningProcesses");
		RealExpr capacity = ctx.mkRealConst("capacity");
		RealExpr speed = ctx.mkRealConst("speed");
		RealExpr demand = ctx.mkRealConst("demand");
		RealExpr delay = ctx.mkRealConst("delay");
		ctx.mkReal(1);
		ctx.mkEq(speed, ctx.mkReal(1));
		BoolExpr mkLe2 = ctx.mkLe(ctx.mkDiv(countOfRunningProcesses, capacity), ctx.mkReal(1));
		Expr mkITE = ctx.mkITE(mkLe2, ctx.mkReal(1), ctx.mkDiv(countOfRunningProcesses, capacity));
		s.add(ctx.mkEq(speed, mkITE));

		BoolExpr mkEq = ctx.mkEq(delay, ctx.mkMul(speed, demand));
		s.add(mkEq);
		System.out.println(s);

		// BusStop position = bus.getPosition();
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
		// // schedule load finished event
		//// LoadFinishedEvent e = new LoadFinishedEvent(loadingTime,
		// remainingPassengers, this.getModel(), "LoadFinished");
		// e.schedule(bus, loadingTime);
		// BoolExpr[] conditionA = ctx.parseSMTLIB2String(
		// "(declare-datatypes (T1 T2) ((Process (mk-process (id T1) (demand
		// T2)))))(declare-const p1 (Process Int Real))(declare-const p2 (Process Int
		// Real))(assert (= p1 p2))",
		// null, null, null, null);
		// // ctx.mkDatatypeSort("Process", ctx.mkConstructor);
		// s.add(conditionA);

		// (declare-fun servedPassengers () Int) (declare-fun value () Int) (declare-fun
		// totalSeats () Int) (declare-fun waitingPassengers () Int) (assert (=
		// servedPassengers (ite (<= waitingPassengers totalSeats) waitingPassengers
		// totalSeats)))
		// IntExpr value = ctx.mkIntConst("value");
		IntExpr population = ctx.mkIntConst("type");
		IntNum mkInt = ctx.mkInt(1);

		BoolExpr mkEq3 = ctx.mkEq(population, mkInt);
		// s.add(mkEq3);

		System.out.println(s);

		IntExpr totalSeats = ctx.mkIntConst("capacity");
		BoolExpr mkLe = ctx.mkLe(population, totalSeats);
		// s.add(mkLe);
		// System.out.println(s);

		// (declare-fun throughput () Real)(declare-fun abstractDemand ()
		// Real)(declare-fun latency () Real)(declare-fun value () Real)(assert (= value
		// (+ latency (/ abstractDemand throughput))))

		// (declare-fun readProcessingRate () Real)(declare-fun abstractDemand ()
		// Real)(declare-fun value () Real)(assert (= value (/ abstractDemand
		// readProcessingRate)))

		// (declare-fun processingRate () Real)(declare-fun abstractDemand ()
		// Real)(declare-fun value () Real)(assert (= value (/ abstractDemand
		// processingRate)))
		BoolExpr mkBoolConst = ctx.mkBoolConst("value");
		BoolExpr mkBool = ctx.mkBool(true);
		BoolExpr mkBoolConst2 = ctx.mkBoolConst("simulateThroughput");

		BoolExpr mkEq4 = ctx.mkEq(mkBoolConst, ctx.mkEq(mkBoolConst2, mkBool));
		// s.add(mkEq4);
		System.out.println(s);

		RealExpr value1 = ctx.mkRealConst("value");
		RealExpr bytes = ctx.mkRealConst("sumOfBytes");

		RatNum mkReal = ctx.mkReal(0);
		BoolExpr mkEq5 = ctx.mkEq(value1, mkReal);
		s.add(mkEq5);
		System.out.println(s);

		// (declare-fun simulateThroughput () Bool) (declare-fun value () Bool) (assert
		// (= value (= simulateThroughput true)))

		RealExpr abstractDemand = ctx.mkRealConst("abstractDemand");
		RealExpr latency = ctx.mkRealConst("latency");
		RealExpr throughput = ctx.mkRealConst("processingRate");
		RealExpr value = ctx.mkRealConst("value");

		BoolExpr mkE1q = ctx.mkEq(value, ctx.mkDiv(abstractDemand, throughput));
		s.add(mkEq);
		System.out.println(s);

		ArrayExpr a1 = ctx.mkArrayConst("waitingProcesses", ctx.mkIntSort(), ctx.mkIntSort());
		IntNum one = ctx.mkInt(1);

		IntExpr waitingProcessCount = ctx.mkIntConst("waitingProcessCount");
		IntExpr processId = ctx.mkIntConst("processId");

		ArrayExpr mkStore = ctx.mkStore(a1, ctx.mkAdd(waitingProcessCount, one), processId);

		// IntExpr mkInt = ctx.mkIntConst("value");

		ArrayExpr value11 = ctx.mkArrayConst("value", ctx.mkIntSort(), ctx.mkIntSort());

		s.add(ctx.mkEq(value, mkStore));
		System.out.println(s);

		// Symbol name = ctx.mkSymbol("actions");
		//
		// EnumSort mkEnumSort = ctx.mkEnumSort(name, ctx.mkSymbol("AcquireAction"),
		// ctx.mkSymbol("ExternalCall"), ctx.mkSymbol("ReleaseAction"),
		// ctx.mkSymbol("InternalAction"));
		// ArrayExpr mkArrayConst = ctx.mkArrayConst("behavior", ctx.getIntSort(),
		// mkEnumSort);
		// Expr sel = ctx.mkSelect(mkArrayConst, ctx.mkInt(0));
		// // s.add(ctx.mkEq(sel, mkEnumSort.getConsts()[0]));
		//
		// s.add(ctx.mkEq(sel, mkEnumSort.getConsts()[2]));

		// (declare-fun delaySpecification () Real)(declare-fun delay () Real)(assert (=
		// delay delaySpecification))
		System.out.println(s);
		System.out.println("");

		// ctx.mkGe(value, mkInt);
		//
		// s.add(ctx.mkGe(value, mkInt));

		System.out.println(s);

		// add-schedules-relation --condition-function "(assert true)" --delay-function
		// "(declare-fun thinkTime () Real)(declare-fun delay () Real)(assert (= delay
		// thinkTime))" --end-event-name "PassengerArrival" --start-event-name
		// "PassengerArrival"
		// Expr ite = ctx.mkITE(ctx.mkLe(waitingPassengers, totalSeats),
		// waitingPassengers, totalSeats);
		//
		//
		// IntExpr value = ctx.mkIntConst("value");
		// IntExpr totalSeats = ctx.mkIntConst("totalSeats");
		IntExpr waitingPassengers = ctx.mkIntConst("waitingPassengers");

		IntExpr servedPassengers = ctx.mkIntConst("servedPassengers");

		Expr ite = ctx.mkITE(ctx.mkLe(waitingPassengers, totalSeats), waitingPassengers, totalSeats);

		s.add(ctx.mkEq(servedPassengers, ite));

		BoolExpr mkEq2 = ctx.mkEq(value, ctx.mkSub(waitingPassengers, servedPassengers));

		s.add(mkEq2);
		System.out.println(s);
		// s.add(ctx.mkEq(LOADING_TIME_PER_PASSENGER, ctx.mkReal(3)));
		s.add(ctx.mkEq(totalSeats, ctx.mkInt(3)));
		s.add(ctx.mkEq(waitingPassengers, ctx.mkInt(3)));
		s.check();
		s.getModel();
		System.out.println(s);

		// RealExpr delay = ctx.mkRealConst("delay");
		// RealExpr LOADING_TIME_PER_PASSENGER =
		// ctx.mkRealConst("LOADING_TIME_PER_PASSENGER");
		// IntExpr totalSeats = ctx.mkIntConst("totalSeats");
		// IntExpr waitingPassengers = ctx.mkIntConst("waitingPassengers");
		//
		// IntExpr servedPassengers = ctx.mkIntConst("servedPassengers");
		//
		// Expr ite = ctx.mkITE(ctx.mkLe(waitingPassengers, totalSeats),
		// waitingPassengers, totalSeats);
		//
		// s.add(ctx.mkEq(servedPassengers, ite));
		//
		// BoolExpr mkEq2 = ctx.mkEq(delay, ctx.mkMul(LOADING_TIME_PER_PASSENGER,
		// servedPassengers));
		//
		// s.add(mkEq2);
		//
		// s.add(ctx.mkEq(LOADING_TIME_PER_PASSENGER, ctx.mkReal(3)));
		// s.add(ctx.mkEq(totalSeats, ctx.mkInt(3)));
		// s.add(ctx.mkEq(waitingPassengers, ctx.mkInt(3)));
		// s.check();
		// s.getModel();
		// System.out.println(s);

		// RealExpr delay = ctx.mkRealConst("delay");
		// IntExpr distance = ctx.mkIntConst("distance");
		// IntExpr averageSpeed = ctx.mkIntConst("averageSpeed");
		//
		// s.add(ctx.mkEq(delay, ctx.mkDiv(distance, averageSpeed)));
		// System.out.println(s);

		// BoolExpr mkTrue = ctx.mkBool(true);
		// BoolExpr mkFalse = ctx.mkBool(true);
		// s.add(mkTrue);
		// double drivingTime = Duration.hours(segment.getDistance() / (double)
		// segment.getAverageSpeed()).toSeconds()
		// .value();
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
