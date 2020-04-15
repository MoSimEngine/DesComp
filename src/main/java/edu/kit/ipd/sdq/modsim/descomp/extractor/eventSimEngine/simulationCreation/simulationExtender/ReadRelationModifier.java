package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.IMapContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.ISimulationConstructContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.MethodDecoder;

import java.util.Collection;
import java.util.HashMap;

public class ReadRelationModifier {

    /**
     * adds a read relation to an event
     *      1. finding the corresponding attribute, eventually creating that attribute
     *      2. adding that relation to the simulator object
     *
     * @param sim the simulation that is worked on
     * @param objectReadAt the object, which is written at
     * @param eventReadRelation the read relations for that event; contains the name of attributes which are read by the event at
     * @param simulationConstructContainer Container object for data structure containing information of existing entities and attributes
     * @param event event that accesses the attribute writing
     */
    public static void addReadRelationToKnownEntityToSimulation(Simulator sim, String objectReadAt, HashMap<String, Collection<String>> eventReadRelation, ISimulationConstructContainer simulationConstructContainer, Event event){
        HashMap<String, HashMap<String, Attribute>> attributesHashMap = simulationConstructContainer.getAttributesHashMap();
        HashMap<String, Entity>entitiesHashMap = simulationConstructContainer.getEntitiesHashMap();

        for (String attrRead : eventReadRelation.get(objectReadAt)) {
            if (attributesHashMap.get(objectReadAt).containsKey(attrRead)) {
                if(!entitiesHashMap.containsKey(objectReadAt)){
                    entitiesHashMap.put(objectReadAt, new Entity(objectReadAt));
                    attributesHashMap.put(objectReadAt, new HashMap<>());
                }
                if(!attributesHashMap.get(objectReadAt).containsKey(attrRead)){
                    attributesHashMap.get(objectReadAt).put(attrRead, new Attribute(attrRead, "unknown"));
                }

                Entity entity = entitiesHashMap.get(objectReadAt);
                Attribute attribute = attributesHashMap.get(objectReadAt).get(attrRead);
                updateSimReadRelation(sim, event, entity, attribute);
            }
        }
    }

    /**
     * adds a read relation for the return value which is read of the calling class
     *      1. finding all calling objects
     *      2. finding the corresponding attribute, eventually creating that attribute
     *      2. adding that relation to the simulator object
     *
     * @param sim the simulation that is worked on
     * @param eventReadRelation the reading relations for that event; contains the name of attributes which are read by the event at
     * @param simulationConstructContainer Container object for data structure containing information of existing entities and attributes
     * @param mapContainer container object for data structure containing information of original jar files
     * @param event event that accesses the attribute reading
     */
    public static void addReadRelationToCallingEntityToSimulation(Simulator sim, HashMap<String, Collection<String>> eventReadRelation, ISimulationConstructContainer simulationConstructContainer, Event event, IMapContainer mapContainer) {
        HashMap<String, Entity>entitiesHashMap = simulationConstructContainer.getEntitiesHashMap();

        for (String callerDescriptionString:eventReadRelation.get("caller")) {
            String[] callerDescription = callerDescriptionString.split("_");

            HashMap<String, Collection<String>> readCallerWithAttr = SchedulingRelationServices.getReadCallerWithAttr(mapContainer, entitiesHashMap, callerDescription);
            for (String callerEntityName : readCallerWithAttr.keySet()) {
                SchedulingRelationServices.addCallerAttributeReferences(simulationConstructContainer, callerEntityName, readCallerWithAttr);

                Entity entity = entitiesHashMap.get(callerEntityName);
                updateSimCallerReadRelation(sim, readCallerWithAttr, callerEntityName, simulationConstructContainer, entity, event);
            }
        }
    }

    private static void updateSimReadRelation(Simulator sim, Event event, Entity entity, Attribute attribute){
        event.addReadAttribute(attribute);
        if (!event.getReadAttribute().contains(attribute)) {
            event.addReadAttribute(attribute);
        }
        if (!entity.getAttributes().contains(attribute)) {
            entity.addAttribute(attribute);
        }

        SchedulingRelationServices.addEvent(sim, event);
        SchedulingRelationServices.addEntity(sim, entity);
    }

    private static void updateSimCallerReadRelation(Simulator sim, HashMap<String, Collection<String>> readCallerWithAttr, String callerEntityName, ISimulationConstructContainer simulationConstructContainer, Entity entity, Event event){
        HashMap<String, HashMap<String, Attribute>> attributesHashMap = simulationConstructContainer.getAttributesHashMap();

        for (String callerAttrName: readCallerWithAttr.get(callerEntityName)) {
            String[] callerAttrNameArray = callerAttrName.split("_");
            Attribute attribute = attributesHashMap.get(callerEntityName).get(callerAttrNameArray[callerAttrNameArray.length-1]);
            if(!entity.getAttributes().contains(attribute)){
                entity.addAttribute(attribute);
            }
            if(!event.getReadAttribute().contains(attribute)){
                event.addReadAttribute(attribute);
            }
        }
        SchedulingRelationServices.addEvent(sim, event);
        SchedulingRelationServices.addEntity(sim, entity);
    }

}
