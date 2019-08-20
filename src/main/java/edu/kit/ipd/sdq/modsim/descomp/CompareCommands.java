package edu.kit.ipd.sdq.modsim.descomp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.kit.ipd.sdq.modsim.descomp.data.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorCompareEventsService;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorRepository;

@ShellComponent
@ShellCommandGroup("compare")
public class CompareCommands {

	@Autowired
	private SimulatorRepository repository;

	@Autowired
	private SimulatorCompareEventsService compareEventsService;

	@ShellMethod("Find Similar Events")
	public String findSimilarEvents(String simulator, String event) {

		Simulator simu = repository.findByName(simulator);

		Event[] events = (Event[]) simu.getEvents().stream().filter(e -> !e.getName().contentEquals(event)).toArray();

		StringBuffer bf = new StringBuffer();

		if (1 == events.length) {

			bf.append("Event " + event + " found in " + simulator + System.lineSeparator());

			Event compareEvent = events[0];

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
			bf.append("Find similar Events finisched. " + findExactMatches.size() + " where found.");
		} else {
			bf.append("Event " + event + " not found in " + simulator + System.lineSeparator());
		}

		return bf.toString();
	}

}
