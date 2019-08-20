package edu.kit.ipd.sdq.modsim.descomp;

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
