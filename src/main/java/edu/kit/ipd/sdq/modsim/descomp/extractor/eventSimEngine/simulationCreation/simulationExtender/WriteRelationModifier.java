package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.*;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.IMapContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.GeneratorMappingServices;

import java.util.Collection;
import java.util.HashMap;

public class WriteRelationModifier {

    public static void addWriteRelationToCallingEventToSimulation(Simulator sim, HashMap<String, Collection<String>> eventWriteRelation, HashMap<String, HashMap<String, Attribute>> allAttributesHashMap, HashMap<String, Entity>allEntityHashMap, Event event, IMapContainer mapContainer) {
        for (String callerDescriptionString:eventWriteRelation.get("caller")) {
            String[] callerDescription = callerDescriptionString.split("_");

            HashMap<String, Collection<String>> writeRelations = GeneratorMappingServices.getWriteCallerWithAttr(mapContainer.getEntityJavaClassHashMap(), allEntityHashMap, mapContainer.getExtractedEventsWithRelation(), callerDescription);
            for (String callerEntityName : writeRelations.keySet()) {
                if (!allEntityHashMap.containsKey(callerEntityName)) {
                    // entität und attribut hinzufügen
                    allEntityHashMap.put(callerEntityName, new Entity(callerEntityName));
                    allAttributesHashMap.put(callerEntityName, new HashMap<String, Attribute>());
                    for (String callerAttrName : writeRelations.get(callerEntityName)) {
                        String[] callerAttrNameArray = callerAttrName.split("_");
                        allAttributesHashMap.get(callerEntityName).put(callerAttrNameArray[callerAttrNameArray.length - 1], new Attribute(callerAttrNameArray[callerAttrNameArray.length - 1], callerAttrNameArray[callerAttrNameArray.length - 2]));
                    }
                } else {
                    for (String callerAttrName : writeRelations.get(callerEntityName)) {
                        //ggf. attribute hinzufügen
                        String[] callerAttrNameArray = callerAttrName.split("_");
                        if (!allAttributesHashMap.get(callerEntityName).containsKey(callerAttrNameArray[callerAttrNameArray.length - 1])) {
                            allAttributesHashMap.get(callerEntityName).put(callerAttrNameArray[callerAttrNameArray.length - 1], new Attribute(callerAttrNameArray[callerAttrNameArray.length - 1], callerAttrNameArray[callerAttrNameArray.length - 2]));
                        }
                    }
                }

                Entity entity = allEntityHashMap.get(callerEntityName);
                for (String callerAttrName: writeRelations.get(callerEntityName)) {
                    String[] callerAttrNameArray = callerAttrName.split("_");
                    Attribute attribute = allAttributesHashMap.get(callerEntityName).get(callerAttrNameArray[callerAttrNameArray.length-1]);
                    if(!entity.getAttributes().contains(attribute)){
                        entity.addAttribute(attribute);
                    }
                    boolean attrExist = false;
                    for (WritesAttribute wrAttr:event.getWriteAttribute()) {
                        if(wrAttr.getAttribute().equals(attribute)){
                            attrExist = true;
                        }
                    }
                    if(!attrExist){
                        event.addWriteAttribute(attribute, "noCond", "noFkt");
                    }
                }
                if(!sim.getEvents().contains(event)){
                    sim.addEvents(event);
                }
                if(!sim.getEntities().contains(entity)){
                    sim.addEntities(entity);
                }

            }
        }
    }



    public static void addWriteRelationToKnownEntityToSimulation(Simulator sim, String objectWriteAt, HashMap<String, Collection<String>>  eventWriteRelation, HashMap<String, HashMap<String, Attribute>> allAttributesHashMap, HashMap<String, Entity>allEntityHashMap, Event event) {
        for (String attributeNameComposed :eventWriteRelation.get(objectWriteAt)) {
            String attributeType = attributeNameComposed.split("_")[0];
            String attributeName = attributeNameComposed.split("_")[1];
            //objectWriteAt;
            if(!allEntityHashMap.containsKey(objectWriteAt)){
                allEntityHashMap.put(objectWriteAt, new Entity(objectWriteAt));
                allAttributesHashMap.put(objectWriteAt, new HashMap<>());
            }
            if(!allAttributesHashMap.get(objectWriteAt).containsKey(attributeName)){
                allAttributesHashMap.get(objectWriteAt).put(attributeName, new Attribute(attributeName, attributeType));
            }

            Entity entity = allEntityHashMap.get(objectWriteAt);
            Attribute attribute = allAttributesHashMap.get(objectWriteAt).get(attributeName);
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
            if (!sim.getEntities().contains(entity)) {
                sim.addEntities(entity);
            }
            if (!sim.getEvents().contains(event)) {
                sim.addEvents(event);
            }
        }
    }
}
