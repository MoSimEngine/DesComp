package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;

import java.util.HashMap;

public class SimulationConstructContainer implements ISimulationConstructContainer{
    private HashMap<String, Entity> entitiesHashMap;
    private HashMap<String, HashMap<String, Attribute>> attributesHashMap;

    public SimulationConstructContainer(HashMap<String, Entity> entitiesHashMap, HashMap<String, HashMap<String, Attribute>> attributesHashMap) {
        this.entitiesHashMap = entitiesHashMap;
        this.attributesHashMap = attributesHashMap;
    }

    public HashMap<String, Entity> getEntitiesHashMap() {
        return entitiesHashMap;
    }

    public HashMap<String, HashMap<String, Attribute>> getAttributesHashMap() {
        return attributesHashMap;
    }
}
