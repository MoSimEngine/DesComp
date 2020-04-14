package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.IMapContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.GeneratorMappingServices;

import java.util.Collection;
import java.util.HashMap;

public class ReadRelationModifier {

    public static void addReadRelationToCallingEntityToSimulation(Simulator sim, HashMap<String, Collection<String>> eventReadRelation, HashMap<String, HashMap<String, Attribute>> allAttributesHashMap, HashMap<String, Entity>allEntityHashMap, Event event, IMapContainer mapContainer) {
        for (String callerDescriptionString:eventReadRelation.get("caller")) {
            String[] callerDescription = callerDescriptionString.split("_");

            HashMap<String, Collection<String>> readCallerWithAttr = GeneratorMappingServices.getReadCallerWithAttr(mapContainer.getEntityJavaClassHashMap(), allEntityHashMap, mapContainer.getExtractedEventsWithRelation(), callerDescription);
            for (String callerEntityName : readCallerWithAttr.keySet()) {
                if(!allEntityHashMap.containsKey(callerEntityName)){
                    // entität und attribut hinzufügen
                    allEntityHashMap.put(callerEntityName,  new Entity(callerEntityName));
                    allAttributesHashMap.put(callerEntityName, new HashMap<String, Attribute>());
                    for (String callerAttrName: readCallerWithAttr.get(callerEntityName)) {
                        String[] callerAttrNameArray = callerAttrName.split("_");
                        allAttributesHashMap.get(callerEntityName).put(callerAttrNameArray[callerAttrNameArray.length-1] , new Attribute(callerAttrNameArray[callerAttrNameArray.length-1], callerAttrNameArray[callerAttrNameArray.length-2]));
                    }
                } else {
                    for (String callerAttrName : readCallerWithAttr.get(callerEntityName)) {
                        //ggf. attribute hinzufügen
                        String[] callerAttrNameArray = callerAttrName.split("_");
                        if(!allAttributesHashMap.get(callerEntityName).containsKey(callerAttrNameArray[callerAttrNameArray.length-1])) {
                            allAttributesHashMap.get(callerEntityName).put(callerAttrNameArray[callerAttrNameArray.length-1] , new Attribute(callerAttrNameArray[callerAttrNameArray.length-1], callerAttrNameArray[callerAttrNameArray.length-2]));
                        }
                    }
                }
                //TODO adding all entitys/attributes to the simulator
                Entity entity = allEntityHashMap.get(callerEntityName);
                for (String callerAttrName: readCallerWithAttr.get(callerEntityName)) {
                    String[] callerAttrNameArray = callerAttrName.split("_");
                    Attribute attribute = allAttributesHashMap.get(callerEntityName).get(callerAttrNameArray[callerAttrNameArray.length-1]);
                    if(!entity.getAttributes().contains(attribute)){
                        entity.addAttribute(attribute);
                    }
                    if(!event.getReadAttribute().contains(attribute)){
                        event.addReadAttribute(attribute);
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

    public static void addReadRelationToKnownEntityToSimulation(Simulator sim, String objectReadAt, HashMap<String, Collection<String>> eventReadRelation, HashMap<String, HashMap<String, Attribute>> allAttributesHashMap, HashMap<String, Entity>allEntityHashMap, Event event){
        for (String attrRead : eventReadRelation.get(objectReadAt)) {
            if (allAttributesHashMap.get(objectReadAt).containsKey(attrRead)) {
                if(!allEntityHashMap.containsKey(objectReadAt)){
                    allEntityHashMap.put(objectReadAt, new Entity(objectReadAt));
                    allAttributesHashMap.put(objectReadAt, new HashMap<>());
                }
                Entity entity = allEntityHashMap.get(objectReadAt);
                if(!allAttributesHashMap.get(objectReadAt).containsKey(attrRead)){
                    allAttributesHashMap.get(objectReadAt).put(attrRead, new Attribute(attrRead, "unknown"));
                }
                Attribute attribute = allAttributesHashMap.get(objectReadAt).get(attrRead);

                event.addReadAttribute(attribute);
                if (!event.getReadAttribute().contains(attribute)) {
                    event.addReadAttribute(attribute);
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
}
