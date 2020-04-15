package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;

import java.util.HashMap;

public interface ISimulationConstructContainer {
    HashMap<String, Entity> getEntitiesHashMap();
    HashMap<String, HashMap<String, Attribute>> getAttributesHashMap();
}
