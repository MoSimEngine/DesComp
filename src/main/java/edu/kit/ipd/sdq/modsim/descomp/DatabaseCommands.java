package edu.kit.ipd.sdq.modsim.descomp;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.kit.ipd.sdq.modsim.descomp.data.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorRepository;

@ShellComponent
@ShellCommandGroup("database")
public class DatabaseCommands {

	@Autowired
	private SimulatorRepository repository;

	@ShellMethod("Clear Database")
	public void cleanAllDatabase() {
		repository.cleanAll();
	}

	@ShellMethod("Compute Event communities")
	public String computeEventCommunities() {

		StringBuffer bf = new StringBuffer();
		bf.append("Compute Louvain Communities for Events " + System.lineSeparator());
		bf.append("Found the following communities: " + System.lineSeparator());

		for (Map<String, Object> map : repository.computeLouvainCommunitiesForEvents()) {
			bf.append("\t In community " + map.get("community") + " is Event " + map.get("event")
					+ System.lineSeparator());
		}

		bf.append("Compute Label Propagation for Events " + System.lineSeparator());
		bf.append("Found the following communities: " + System.lineSeparator());

		for (Map<String, Object> map : repository.computeLabelPropagationForEvents()) {
			bf.append("\t In community " + map.get("label") + " is Event " + map.get("event")
					+ System.lineSeparator());
		}

		bf.append("Compute Union Find for Events " + System.lineSeparator());
		bf.append("Found the following communities: " + System.lineSeparator());

		for (Map<String, Object> map : repository.computeUnionFind()) {
			bf.append("\t In community " + map.get("setId") + " is Event " + map.get("event")
					+ System.lineSeparator());
		}

		return bf.toString();
	}

	@ShellMethod("Add Entity")
	public String addEntity(String simulator, String name) {

		Simulator simu = repository.findByName(simulator);

		long count = simu.getEntitys().stream().filter(e -> !e.getName().contentEquals(name)).count();

		if (0 > count) {
			return "Entity: " + name + " already exisits in " + simulator + "!";
		}

		simu.addEntitys(new Entity(name));
		repository.save(simu);
		return "Added Entity: " + name + " to " + simulator;
	}

//	@ShellMethod("Add Entity")
//	public String printEventsOfSimulator(String simulator) {
//
//		Simulator simu = repository.findByName(simulator);
//
//		StringBuffer output = new StringBuffer("Events from Simulator:" + System.lineSeparator());
//
////		simu.getEvents().stream().forEach(e -> output.append(("\t" + e.getId());
//
//		if (0 > count) {
//			return "Entity: " + name + " already exisits in " + simulator + "!";
//		}
//
//		simu.addEntitys(new Entity(name));
//		repository.save(simu);
//		return "Added Entity: " + name + " to " + simulator;
//	}

	@ShellMethod("Add Event")
	public String addEvent(String simulator, String name) {

		Simulator simu = repository.findByName(simulator);

		long count = simu.getEvents().stream().filter(e -> !e.getName().contentEquals(name)).count();

		if (0 > count) {
			return "Entity: " + name + " already exisits in " + simulator + "!";
		}

		simu.addEvents(new Event(name));
		repository.save(simu);
		return "Added Entity: " + name + " to " + simulator;
	}

	@ShellMethod("Delete Simulator")
	public void deleteSimulator(String name) {
		Simulator findByName = repository.findByName(name);
		repository.deleteById(findByName.getId());
	}

	@ShellMethod("Print existing Simulators")
	public String printSimulators() {
		StringBuffer output = new StringBuffer("Found following Simulators in the Database:" + System.lineSeparator());

		Iterable<Simulator> findAll = repository.findAll();
		for (Simulator simulator : findAll) {
			output.append(simulator.getName() + System.lineSeparator());
		}

		return output.toString();
	}

}
