package edu.kit.ipd.sdq.modsim.descomp.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class Z3Test2 {

	public static void main(String[] args) {

		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		Solver s = ctx.mkSolver();

		String a = "(declare-fun readProcessingRate () Real)(declare-fun abstractDemand () Real)(declare-fun value () Real)(assert (= value (+ abstractDemand readProcessingRate)))";
		String b = "(declare-fun writeProcessingRate () Real)(declare-fun abstractDemand () Real)(declare-fun value () Real)(assert (= value (+ abstractDemand writeProcessingRate)))";
		b = b.replace("value", "value1");

		BoolExpr[] conditionsA = ctx.parseSMTLIB2String(a, null, null, null, null);
		BoolExpr[] conditionsB = ctx.parseSMTLIB2String(b, null, null, null, null);
		s.add(conditionsA);
		s.add(conditionsB);
		s.add(ctx.mkNot(ctx.mkEq(ctx.mkRealConst("value"), ctx.mkRealConst("value1"))));

		// Add Context to Solver

		BoolExpr mkEq = ctx.mkEq(ctx.mkRealConst("writeProcessingRate"), ctx.mkRealConst("readProcessingRate"));
		s.add(mkEq);

		Status check = s.check();

		if (check == Status.SATISFIABLE) {

			// Problem wir wollen das Gegenteil
			System.out.println("SAT");
			System.out.println(check);

		} else {

			// Es gibt kein Model für das gilt value ungleich value1. Daher gilt value =
			// value1
			System.out.println("Sind gleich unter der annahme, dass" + mkEq);
		}

		List<List<String>> lists = new ArrayList<List<String>>(2);

		String[] parameters1 = { "writeProcessingRate" };
		String[] parameters2 = { "readProcessingRate" };

		lists.add(Arrays.asList(parameters1));
		lists.add(Arrays.asList(parameters2));

		List<String> result = new ArrayList<String>();
		generatePermutations(lists, result, 0, "");

		for (String string : result) {

			String string2 = string.split("=")[1];
			System.out.println(string2);
		}

	}
	
	
	

	//Adapted from https://stackoverflow.com/questions/17192796/generate-all-combinations-from-multiple-lists
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
