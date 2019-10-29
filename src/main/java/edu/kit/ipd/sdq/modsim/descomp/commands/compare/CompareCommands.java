package edu.kit.ipd.sdq.modsim.descomp.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Schedules;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.WritesAttribute;
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
		return domainCompareService.calcRelatednessOfWords(word1, word2);
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

		StringBuilder sb = new StringBuilder();

		sb.append("Compute Similarity between Communities (LouvainCommunities)").append(System.lineSeparator());

		for (String community1 : communities.keySet()) {
			for (String community2 : communities.keySet()) {

				if (!community1.contentEquals(community2)) {

					sb.append("Compare Community ")
							.append(community1)
							.append(" with Community ")
							.append(community2)
							.append(System.lineSeparator());

					sb.append("Community ")
							.append(community1)
							.append(" contains the following words: ")
							.append(communities.get(community1))
							.append(System.lineSeparator());

					sb.append("Community ")
							.append(community2)
							.append(" contains the following words: ")
							.append(communities.get(community2))
							.append(System.lineSeparator());

					String[] wordsOfCommunity1 = communities.get(community1).split(",");
					String[] wordsOfCommunity2 = communities.get(community2).split(",");

					Map<String, double[][]> calcRelatednessOfWordMatric = domainCompareService
							.calcRelatednessOfWordMatrices(wordsOfCommunity1, wordsOfCommunity2);

					for (String calculator : calcRelatednessOfWordMatric.keySet()) {
						sb.append("\t").append(calculator).append(System.lineSeparator());

						double[][] matrice = calcRelatednessOfWordMatric.get(calculator);

						for (double[] doubles : matrice) { // this equals to the row in our matrix.
							for (double aDouble : doubles) { // this equals to the column in each row.
								sb.append(aDouble).append(" ");
							}
							sb.append(System.lineSeparator());
						}

					}

				}

			}
		}

		sb.append("Compute Label Propagation for Events " + System.lineSeparator());
		sb.append("Found the following communities: " + System.lineSeparator());

		for (Map<String, Object> map : repository.computeLabelPropagationForEvents()) {
			sb.append("\t In community " + map.get("label") + " is Event " + map.get("event") + System.lineSeparator());
		}

		sb.append("Compute Union Find for Events " + System.lineSeparator());
		sb.append("Found the following communities: " + System.lineSeparator());

		for (Map<String, Object> map : repository.computeUnionFind()) {
			sb.append("\t In community " + map.get("setId") + " is Event " + map.get("event") + System.lineSeparator());
		}

		return sb.toString();
	}

	@ShellMethod("Calculate Similarity Between Events and Entities")
	public String calcSimilarityBetweenSimulators(String simulator1, String simulator2) {
		Simulator simulator_a = repository.findByName(simulator1);
		Simulator simulator_b = repository.findByName(simulator2);

		StringBuffer bf = new StringBuffer();

		for (Entity entity : simulator_a.getEntities()) {
			for (Entity entity2 : simulator_b.getEntities()) {
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
	public String compareSimulatorsBehavior(String simulator1, String simulator2) {
		Simulator simu1 = repository.findById(repository.findByName(simulator1).getId(), 3).get();
		Simulator simu2 = repository.findById(repository.findByName(simulator2).getId(), 3).get();

		// Compare Events
		for (Event event1 : simu1.getEvents()) {
			for (Event event2 : simu2.getEvents()) {
				// System.out.println("Check Events:" + event1.getName() + ";" +
				// event2.getName());
				compareEvents(event1, event2);
			}
		}

		return "";

	}

	private Map<String, String> compareEvents(Event event1, Event event2) {
		Map<String, String> result = new HashMap<String, String>();

		// Compare simple Informations of the Event
		result.put("countOfEventsScheduled",
				Boolean.toString(((event1.getEvents().size() == event2.getEvents().size()))));

		result.put("countOfReadAttributes",
				Boolean.toString(((event1.getReadAttribute().size() == event2.getReadAttribute().size()))));

		result.put("countOfWriteAttributes",
				Boolean.toString(((event1.getWriteAttribute().size() == event2.getWriteAttribute().size()))));

		// Compare Condition for delay function

		for (Schedules schedules : event1.getEvents()) {
			for (Schedules schedules2 : event2.getEvents()) {

				List<String> parametersOfEvent1 = new ArrayList<String>();
				List<String> parametersOfEvent2 = new ArrayList<String>();

				if (event1.getReadAttribute().size() == event2.getReadAttribute().size()) {

					for (Attribute attribute : event1.getReadAttribute()) {
						parametersOfEvent1.add(attribute.getName());
					}

					for (Attribute attribute : event2.getReadAttribute()) {
						parametersOfEvent2.add(attribute.getName());
					}

					String checkEqualityDelay = compareEventsService.checkEqualityDelay(schedules.getDelay(),
							schedules2.getDelay(), parametersOfEvent1, parametersOfEvent2);

					if (checkEqualityDelay.contains("EQUAL")) {

						System.out.println("DELEAY: Compared Events: " + event1.getName() + ";" + event2.getName()
								+ " Compared Relationship: " + schedules.getEndEvent().getName() + ";"
								+ schedules2.getEndEvent().getName() + " are equal under the assumption:"
								+ checkEqualityDelay);
					} else {
						// System.out.println("Functions are not equal" + checkEquality.toString());
					}

					// Check for condition

					String checkEqualityConditon = compareEventsService.checkEqualityDelayCondtion(
							schedules.getCondition(), schedules.getCondition(), parametersOfEvent1, parametersOfEvent2);

					if (checkEqualityConditon.contains("EQUAL")) {
						System.out.println("DELAYCONDITON: Compared Events: " + event1.getName() + ";"
								+ event2.getName() + " Compared Relationship: " + schedules.getEndEvent().getName()
								+ ";" + schedules2.getEndEvent().getName() + " are equal under the assumption:"
								+ checkEqualityConditon);
					} else {
						// System.out.println("Functions are not equal" + checkEquality.toString());
					}
				}

			}
		}

		for (

		WritesAttribute writesAttribute : event1.getWriteAttribute()) {
			for (WritesAttribute writesAttribute2 : event2.getWriteAttribute()) {
				List<String> parametersOfEvent1 = new ArrayList<String>();
				List<String> parametersOfEvent2 = new ArrayList<String>();

				if (event1.getReadAttribute().size() == event2.getReadAttribute().size()) {

					for (Attribute attribute : event1.getReadAttribute()) {
						parametersOfEvent1.add(attribute.getName());
					}

					for (Attribute attribute : event2.getReadAttribute()) {
						parametersOfEvent2.add(attribute.getName());
					}
					System.out.println("Check Write");
					String checkEqualityOfWrite = compareEventsService.checkEqualityWrite(
							writesAttribute.getWriteFunction(), writesAttribute2.getWriteFunction(), parametersOfEvent1,
							parametersOfEvent2);

					if (checkEqualityOfWrite.contains("EQUAL")) {
						System.out.println("WRITE: Compared Events: " + event1.getName() + ";" + event2.getName()
								+ " Compared Relationship: " + writesAttribute.getAttribute().getName() + ";"
								+ writesAttribute2.getAttribute().getName() + " are equal under the assumption:"
								+ checkEqualityOfWrite);
					} else {
						// System.out.println("Functions are not equal" + checkEquality.toString());

					}
				}
			}
		}

		return result;
	}

	@ShellMethod("Find Similar Events")
	public String findSimilarEvents(String simulator, String event) {

		Simulator simu = repository.findByName(simulator);

		Event events = simu.getEvents().stream().filter(e -> e.getName().contentEquals(event)).findFirst().get();

		StringBuffer bf = new StringBuffer();

		// if (null != events) {
		//
		// bf.append("Event " + event + " found in " + simulator +
		// System.lineSeparator());
		//
		// Event compareEvent = events;
		//
		// bf.append("Following Events are exact matches: " + System.lineSeparator());
		//
		// List<Simulator> simualtors = new ArrayList<Simulator>();
		// repository.findAll().forEach(simualtors::add);
		// Map<Event, Map<String, Boolean>> findExactMatches =
		// compareEventsService.findExactMatches(compareEvent,
		// simualtors);
		//
		// for (Event event2 : findExactMatches.keySet()) {
		// bf.append("Event " + event2.getName() + " is a exact match" +
		// System.lineSeparator());
		//
		// for (String reason : findExactMatches.get(event2).keySet()) {
		// bf.append("\t" + reason + "\t" + findExactMatches.get(event2).get(reason) +
		// System.lineSeparator());
		// }
		//
		// }
		// bf.append("Find similar Events finished. " + findExactMatches.size() + "
		// where found.");
		// } else {
		// bf.append("Event " + event + " not found in " + simulator +
		// System.lineSeparator());
		// }

		return bf.toString();
	}

}
