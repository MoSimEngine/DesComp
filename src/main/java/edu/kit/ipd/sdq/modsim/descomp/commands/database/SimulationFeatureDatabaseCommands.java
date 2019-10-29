package edu.kit.ipd.sdq.modsim.descomp.commands.database;

import edu.kit.ipd.sdq.modsim.descomp.SimulationValueProvider;
import edu.kit.ipd.sdq.modsim.descomp.data.featuremodel.Feature;
import edu.kit.ipd.sdq.modsim.descomp.data.featuremodel.FeatureDiagram;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulationRepository;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.*;

import java.util.Optional;

@ShellComponent
@ShellCommandGroup("database - simulation")
public class SimulationFeatureDatabaseCommands {

    private Long currentSimulationId;

    @Autowired
    private SimulationRepository simulationRepository;

    @ShellMethod("Create Simulator")
    public void createSimulator(String name, String description) {
        FeatureDiagram simulation = new FeatureDiagram(name, description);
        simulation.setName(name);
        simulation.setDescription(description);
        simulationRepository.save(simulation);
    }

    @ShellMethod("Set current Simulation Specification")
    public String setCurrentSimulation(@ShellOption(valueProvider = SimulationValueProvider.class) String simulation){
        FeatureDiagram currentSimulation = simulationRepository.findByName(simulation);
        StringBuilder sb = new StringBuilder();

        if (null == currentSimulation){
            sb.append("Simulation Specification for ").append(simulation).append(" not found.");
        } else {
            sb.append("Current Simulation is set to: ").append(currentSimulation.getName());
            this.currentSimulationId = currentSimulation.getId();
        }
        return sb.toString();
    }

    @ShellMethod("Get current Simulation Specification")
    public String getCurrentSimulation(){
        Optional<FeatureDiagram> currentSimulation = simulationRepository.findById(currentSimulationId, 3);
        StringBuilder sb = new StringBuilder();

        if (!currentSimulation.isPresent()){
            sb.append("Current set Simulation Specification does not exist");
        } else {
            sb.append("Current Simulation Specification is set to: ").append(currentSimulation.get().getName());
        }
        return sb.toString();
    }

    @ShellMethod("Add Feature")
    @ShellMethodAvailability("currentSimulationSpecificationAvailabilityCheck")
    public String addFeature(String name){
        FeatureDiagram simulation = simulationRepository.findById(currentSimulationId, 3).get();

        long count = simulation.getFeatures().stream().filter(e ->!e.getName().contentEquals(name)).count();

        if (count < 0){
            return "Feature: " + name + " already exists in " + simulation.getName() + "!";
        }

        simulation.addFeature(new Feature(name));
        simulationRepository.save(simulation);

        return "Added Feature: " + name + " to " + simulation.getName();
    }
}
