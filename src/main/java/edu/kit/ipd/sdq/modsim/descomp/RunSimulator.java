package edu.kit.ipd.sdq.modsim.descomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.kit.ipd.sdq.modsim.descomp.data.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorExecutor;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorRepository;

@ShellComponent
@ShellCommandGroup("run")
public class RunSimulator {

	@Autowired
	private SimulatorRepository repository;

	@Autowired
	private SimulatorExecutor executor;

	@ShellMethod("Extract structural Information from existing Simulation")
	public String runSimulator(String name) {
		Simulator simulator = repository.findByName(name);

		executor.executeSimulator(simulator);

		return "AWESOME!!!";
	}

}
