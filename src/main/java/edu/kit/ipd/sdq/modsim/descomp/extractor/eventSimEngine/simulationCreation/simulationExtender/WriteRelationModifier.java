package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.*;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.IMapContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.ISimulationConstructContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.MethodDecoder;

import java.util.Collection;
import java.util.HashMap;

public class WriteRelationModifier {

    /**
     * adds a write relation to an event
     *      1. finding the corresponding attribute, eventually creating that attribute
     *      2. adding that relation to the simulator object
     *
     * @param sim the simulation that is worked on
     * @param objectWriteAt the object, which is written at
     * @param eventWriteRelation the writing relations for that event; contains the name of attributes which are written by the event at
     * @param simulationConstructContainer Container object for data structure containing information of existing entities and attributes
     * @param event event that accesses the attribute writing
     */
    public static void addWriteRelationToKnownEntityToSimulation(Simulator sim, String objectWriteAt, HashMap<String, Collection<String>>  eventWriteRelation, ISimulationConstructContainer simulationConstructContainer, Event event) {
        HashMap<String, HashMap<String, Attribute>> attributesHashMap = simulationConstructContainer.getAttributesHashMap();
        HashMap<String, Entity>entitiesHashMap = simulationConstructContainer.getEntitiesHashMap();

        for (String attributeNameComposed :eventWriteRelation.get(objectWriteAt)) {
            String attributeType = attributeNameComposed.split("_")[0];
            String attributeName = attributeNameComposed.split("_")[1];

            if (!entitiesHashMap.containsKey(objectWriteAt)) {
                entitiesHashMap.put(objectWriteAt, new Entity(objectWriteAt));
                attributesHashMap.put(objectWriteAt, new HashMap<>());
            }
            if (!attributesHashMap.get(objectWriteAt).containsKey(attributeName)) {
                attributesHashMap.get(objectWriteAt).put(attributeName, new Attribute(attributeName, attributeType));
            }

            Entity entity = entitiesHashMap.get(objectWriteAt);
            Attribute attribute = attributesHashMap.get(objectWriteAt).get(attributeName);
            updateSimWriteRelation(sim, event, entity, attribute);
        }
    }

    /**
     * adds a write relation for the return value which is written to the calling class
     *      1. finding all calling objects
     *      2. finding the corresponding attribute, eventually creating that attribute
     *      2. adding that relation to the simulator object
     *
     * @param sim the simulation that is worked on
     * @param eventWriteRelation the writing relations for that event; contains the name of attributes which are written by the event at
     * @param simulationConstructContainer Container object for data structure containing information of existing entities and attributes
     * @param mapContainer Container object for data structure containing information of original jar files
     * @param event event that accesses the attribute writing
     */
    public static void addWriteRelationToCallingEventToSimulation(Simulator sim, HashMap<String, Collection<String>> eventWriteRelation, ISimulationConstructContainer simulationConstructContainer, IMapContainer mapContainer, Event event) {
        HashMap<String, Entity>entitiesHashMap = simulationConstructContainer.getEntitiesHashMap();
        for (String callerDescriptionString:eventWriteRelation.get("caller")) {
            String[] callerDescription = callerDescriptionString.split("_");

            HashMap<String, Collection<String>> writeRelations = SchedulingRelationServices.getWriteCallerWithAttr(mapContainer, entitiesHashMap, callerDescription);
            for (String callerEntityName : writeRelations.keySet()) {
                SchedulingRelationServices.addCallerAttributeReferences(simulationConstructContainer,callerEntityName,writeRelations);

                Entity entity = entitiesHashMap.get(callerEntityName);
                updateSimCallerWriteRelation(sim, writeRelations, callerEntityName, simulationConstructContainer, entity, event);
            }
        }
    }

    private static void updateSimWriteRelation(Simulator sim, Event event, Entity entity, Attribute attribute){
        boolean notFound = true;
        for (WritesAttribute wrAttr: event.getWriteAttribute()) {
            if (wrAttr.getAttribute().equals(attribute)) {
                notFound = false;
                break;
            }
        }
        if (notFound) {
            event.addWriteAttribute(attribute, "noCond","noFkt");
        }
        if (!entity.getAttributes().contains(attribute)) {
            entity.addAttribute(attribute);
        }
        SchedulingRelationServices.addEvent(sim, event);
        SchedulingRelationServices.addEntity(sim, entity);
    }

    private static void updateSimCallerWriteRelation(Simulator sim, HashMap<String, Collection<String>> writeRelations, String callerEntityName, ISimulationConstructContainer simulationConstructContainer, Entity entity, Event event){
        HashMap<String, HashMap<String, Attribute>> attributesHashMap = simulationConstructContainer.getAttributesHashMap();
        for (String callerAttrName: writeRelations.get(callerEntityName)) {
            String[] callerAttrNameArray = callerAttrName.split("_");
            Attribute attribute = attributesHashMap.get(callerEntityName).get(callerAttrNameArray[callerAttrNameArray.length-1]);
            if(!entity.getAttributes().contains(attribute)){
                entity.addAttribute(attribute);
            }
            boolean attrExist = false;
            for (WritesAttribute wrAttr:event.getWriteAttribute()) {
                if (wrAttr.getAttribute().equals(attribute)) {
                    attrExist = true;
                    break;
                }
            }
            if(!attrExist){
                event.addWriteAttribute(attribute, "noCond", "noFkt");
            }
        }
        SchedulingRelationServices.addEvent(sim, event);
        SchedulingRelationServices.addEntity(sim, entity);
    }
}
