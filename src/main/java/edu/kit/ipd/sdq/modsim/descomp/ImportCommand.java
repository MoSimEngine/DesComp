package edu.kit.ipd.sdq.modsim.descomp;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.abstractsimengine.ExtractorService;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorRepository;

@ShellComponent
@ShellCommandGroup("import")
public class ImportCommand {

	@Autowired
	private ExtractorService extractorService;

	@Autowired
	private SimulatorRepository repository;

	@ShellMethod("Extract structural Information from existing Simulation")
	public void extractStructuralInformation(String name, File simulator) {
		Simulator extractedSimulator = extractorService.extractSimulator(simulator);
		extractedSimulator.setName(name);
		repository.save(extractedSimulator);
	}

}
