package edu.kit.ipd.sdq.modsim.descomp.data.feature;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Identifier;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

public class Simulation extends Identifier {
    @Property
    private String description;

    @Relationship(type = "SIMULATOR_FEATURE", direction = Relationship.UNDIRECTED)
    private SimulatorFeature simulatorFeature;

    @Relationship(type = "INPUT_MODEL", direction = Relationship.UNDIRECTED)
    private InputModel inputModel;

    @Relationship(type = "OUTPUT_MODEL", direction = Relationship.UNDIRECTED)
    private OutputModel outputModel;

    public Simulation(String name, String description){
        super();
        this.name =name;
        this.description =description;
        this.simulatorFeature = new SimulatorFeature();
        this.inputModel = new InputModel();
        this.outputModel = new OutputModel();
    }

    public SimulatorFeature getSimulatorFeature() {
        return simulatorFeature;
    }

    public void setSimulatorFeature(SimulatorFeature simulatorFeature) {
        this.simulatorFeature = simulatorFeature;
    }

    public InputModel getInputModel() {
        return inputModel;
    }

    public void setInputModel(InputModel inputModel) {
        this.inputModel = inputModel;
    }

    public OutputModel getOutputModel() {
        return outputModel;
    }

    public void setOutputModel(OutputModel outputModel) {
        this.outputModel = outputModel;
    }
}
