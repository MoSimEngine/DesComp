package edu.kit.ipd.sdq.modsim.descomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorExecutor;

@ShellComponent
@ShellCommandGroup("run")
public class RunSimulator {

	@Autowired
	private SimulatorExecutor executor;

	@ShellMethod("Extract structural Information from existing Simulation")
	public String runSimulator(String name) {
		// Simulator simulator = repository.findByName(name);

		// executor.executeSimulator(null);

		return "AWESOME!!!";
	}

}
