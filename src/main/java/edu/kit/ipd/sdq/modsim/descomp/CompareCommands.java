package edu.kit.ipd.sdq.modsim.descomp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.kit.ipd.sdq.modsim.descomp.data.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.services.DomainCompare;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorCompareEventsService;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorRepository;

@ShellComponent
@ShellCommandGroup("compare")
public class CompareCommands {

	@Autowired
	private SimulatorRepository repository;

	@Autowired
	private SimulatorCompareEventsService compareEventsService;

	@Autowired
	private DomainCompare domainCompareService;

	@ShellMethod("Calculate Similarity between words")
	public String calcSimilarityBetweenWords(String word1, String word2) {
		String calcRelatednessOfWords = domainCompareService.calcRelatednessOfWords(word1, word2);

		return calcRelatednessOfWords;
	}

	@ShellMethod("Calculate Similarity matrice between communities")
	public String calcSimilarityBetweenCommunities(String simulator1, String simulator2) {

		Map<String, String> communities = new HashMap<String, String>();

		for (Map<String, Object> map : repository.computeLouvainCommunitiesForEvents()) {

			if (communities.containsKey(map.get("community"))) {
				String concat = communities.get(map.get("community"));

				System.out.println(concat);

				communities.put(map.get("community").toString(), concat + "," + map.get("event"));

			} else {
				communities.put(map.get("community").toString(), map.get("event").toString());
			}
		}

		StringBuffer bf = new StringBuffer();

		bf.append("Compute Similarity between Communities (LouvainCommunities)" + System.lineSeparator());

		for (String community1 : communities.keySet()) {
			for (String community2 : communities.keySet()) {

				if (!community1.contentEquals(community2)) {

					bf.append("Compare Community " + community1 + " with Community " + community2
							+ System.lineSeparator());

					bf.append("Community " + community1 + " contains the following words: "
							+ communities.get(community1) + System.lineSeparator());
					bf.append("Community " + community2 + " contains the following words: "
							+ communities.get(community2) + System.lineSeparator());

					String[] wordsOfCommunity1 = communities.get(community1).split(",");
					String[] wordsOfCommunity2 = communities.get(community2).split(",");

					Map<String, double[][]> calcRelatednessOfWordMatric = domainCompareService
							.calcRelatednessOfWordMatric(wordsOfCommunity1, wordsOfCommunity2);

					for (String calculator : calcRelatednessOfWordMatric.keySet()) {
						bf.append("\t" + calculator + System.lineSeparator());

						double[][] matrice = calcRelatednessOfWordMatric.get(calculator);

						for (int i = 0; i < matrice.length; i++) { // this equals to the row in our matrix.
							for (int j = 0; j < matrice[i].length; j++) { // this equals to the column in each row.
								bf.append(matrice[i][j] + " ");
							}
							bf.append(System.lineSeparator());
						}

					}

				}

			}
		}

		bf.append("Compute Label Propagation for Events " + System.lineSeparator());
		bf.append("Found the following communities: " + System.lineSeparator());

		for (Map<String, Object> map : repository.computeLabelPropagationForEvents()) {
			bf.append("\t In community " + map.get("label") + " is Event " + map.get("event") + System.lineSeparator());
		}

		bf.append("Compute Union Find for Events " + System.lineSeparator());
		bf.append("Found the following communities: " + System.lineSeparator());

		for (Map<String, Object> map : repository.computeUnionFind()) {
			bf.append("\t In community " + map.get("setId") + " is Event " + map.get("event") + System.lineSeparator());
		}

		return bf.toString();
	}

	@ShellMethod("Calculate Similarity Between Events and Entities")
	public String calcSimilarityBetweenSimulators(String simulator1, String simulator2) {
		Simulator simulator_a = repository.findByName(simulator1);
		Simulator simulator_b = repository.findByName(simulator2);

		StringBuffer bf = new StringBuffer();

		for (Entity entity : simulator_a.getEntitys()) {
			for (Entity entity2 : simulator_b.getEntitys()) {
				bf.append("Compare Entity " + entity.getName() + " from Simulator " + simulator_a.getName()
						+ " with Entity " + entity2.getName() + " from Simulator " + simulator_b.getName()
						+ System.lineSeparator());
				bf.append(domainCompareService.calcRelatednessOfWords(entity.getName(), entity2.getName())
						+ System.lineSeparator());
			}
		}

		for (Event event : simulator_a.getEvents()) {
			for (Event event2 : simulator_b.getEvents()) {
				bf.append("Compare Event " + event.getName() + " from Simulator " + simulator_a.getName()
						+ " with Event " + event2.getName() + " from Simulator " + simulator_b.getName()
						+ System.lineSeparator());
				bf.append(domainCompareService.calcRelatednessOfWords(event.getName(), event2.getName())
						+ System.lineSeparator());
			}
		}

		return bf.toString();

	}

	@ShellMethod("Find Similar Events")
	public String findSimilarEvents(String simulator, String event) {

		Simulator simu = repository.findByName(simulator);

		Event events = simu.getEvents().stream().filter(e -> e.getName().contentEquals(event)).findFirst().get();

		StringBuffer bf = new StringBuffer();

		if (null != events) {

			bf.append("Event " + event + " found in " + simulator + System.lineSeparator());

			Event compareEvent = events;

			bf.append("Following Events are exact matches: " + System.lineSeparator());

			List<Simulator> simualtors = new ArrayList<Simulator>();
			repository.findAll().forEach(simualtors::add);
			Map<Event, Map<String, Boolean>> findExactMatches = compareEventsService.findExactMatches(compareEvent,
					simualtors);

			for (Event event2 : findExactMatches.keySet()) {
				bf.append("Event " + event2.getName() + " is a exact match" + System.lineSeparator());

				for (String reason : findExactMatches.get(event2).keySet()) {
					bf.append("\t" + reason + "\t" + findExactMatches.get(event2).get(reason) + System.lineSeparator());
				}

			}
			bf.append("Find similar Events finished. " + findExactMatches.size() + " where found.");
		} else {
			bf.append("Event " + event + " not found in " + simulator + System.lineSeparator());
		}

		return bf.toString();
	}

}
