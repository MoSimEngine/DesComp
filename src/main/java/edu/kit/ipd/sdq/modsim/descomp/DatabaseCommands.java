package edu.kit.ipd.sdq.modsim.descomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

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
