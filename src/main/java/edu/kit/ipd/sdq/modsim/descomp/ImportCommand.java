package edu.kit.ipd.sdq.modsim.descomp;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.EventExtractorService;
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

	@Autowired
	private EventExtractorService eventExtractorService;


	@ShellMethod("Extract structural Information from existing Simulation")
	public void extractStructuralInformation(String name, File simulator) {
		Simulator extractedSimulator = extractorService.extractSimulator(simulator);
		extractedSimulator.setName(name);
		repository.save(extractedSimulator);
	}

	@ShellMethod("Extract structural Information from existing Simulation in a Directory")
	public void extractStructuralInformationFromDirectory(String name, File dir) {
		Collection<File> fileList = new ArrayList<File>();
		dfsForJar(fileList, dir);

		Simulator extractedSimulator = extractorService.extractSimulatorList(fileList);
		extractedSimulator.setName(name);
		repository.save(extractedSimulator);
	}

	@ShellMethod("Extract structural Information of multiple Models from existing Simulation in a Directory")
	public void extractStructuralInformationFromDirectoryForMultipleDiagrams(String name, File dir){
		Collection<File> fileList = new ArrayList<File>();
		dfsForJar(fileList, dir);

		int i = 1;
		for(File file : fileList){
			Simulator extractedSimulator = extractorService.extractSimulator(file);
			extractedSimulator.setName(name + i);
			repository.save(extractedSimulator);
			i++;
		}

	}


	@ShellMethod("Extract Event Sim from Directory")
	public void extractEventSim(String simulationName, File dir){
		Collection<File> fileList = new ArrayList<File>();
		dfsForJar(fileList, dir);
        Simulator extractedSimulator = eventExtractorService.extractEventSim(fileList);
//        extractedSimulator.setName(simulationName);
//        repository.save(extractedSimulator);
    }




    private Collection<File> dfsForJar(Collection<File> fileList, File dir) {
			for (File foundFile : dir.listFiles()) {
				if (foundFile.isDirectory()) {
					dfsForJar(fileList, foundFile);
				} else if (foundFile.getName().endsWith(".jar")) {
					fileList.add(foundFile);
				}
			}
			return fileList;
		}
}