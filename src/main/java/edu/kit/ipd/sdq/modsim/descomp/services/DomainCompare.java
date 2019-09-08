package edu.kit.ipd.sdq.modsim.descomp.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

@Service
public class DomainCompare {

	private static ILexicalDatabase db = new NictWordNet();
	private static RelatednessCalculator[] rcs = { new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),
			new WuPalmer(db), new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db) };

	public Map<String, double[][]> calcRelatednessOfWordMatrices(String[] word1, String[] word2) {

		Map<String, double[][]> similarityMatrix = new HashMap<String, double[][]>();

		WS4JConfiguration.getInstance().setMFS(true);
		for (RelatednessCalculator rc : rcs) {
			double[][] s = rc.getSimilarityMatrix(word1, word2);
			 similarityMatrix.put(rc.getClass().getName(), s);
		}

		return  similarityMatrix;
	}

	public String calcRelatednessOfWords(String word1, String word2) {
		StringBuilder bf = new StringBuilder();

		word1 = word1.replace("Event", "");
		word2 = word2.replace("Event", "");

		WS4JConfiguration.getInstance().setMFS(true);
		for (RelatednessCalculator rc : rcs) {
			double s = rc.calcRelatednessOfWords(word1, word2);

			bf.append("\t");
			bf.append(rc.getClass().getName());
			bf.append("\t");
			bf.append(s);
			bf.append(System.lineSeparator());

		}

		return bf.toString();
	}

	public static void main(String[] args) {
		new DomainCompare().calcRelatednessOfWords("car", "bus");
	}

}