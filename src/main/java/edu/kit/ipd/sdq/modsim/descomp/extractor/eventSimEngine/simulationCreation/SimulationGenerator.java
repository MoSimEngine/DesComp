package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.*;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.IMapContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.MethodDecoder;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender.ReadRelationModifier;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender.SchedulingRelationModifier;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender.WriteRelationModifier;

import java.util.*;

public class SimulationGenerator {
    public static Simulator createSimulator(IMapContainer mapContainer){
        HashMap<String, Entity> allEntityHashMap = GeneratorMappingServices.createEntityObjectsForJavaClasses(mapContainer.getEntityJavaClassHashMap());
        HashMap<String, HashMap<String, Attribute>> allAttributesHashMap = GeneratorMappingServices.createAttributeObjectsForFieldClasses(mapContainer.getFieldAttrHasMap());
        HashMap<String, Event> allEventsMap = new HashMap<>();

        Simulator sim = new Simulator("extractedSimulator", "Simulator extracted from EvenSim");


        //adding read and write relations
        for (String keyEvent : mapContainer.getExtractedEventsWithRelation().keySet()){
            HashMap<String, HashMap<String, Collection<String>>> currEvent= mapContainer.getExtractedEventsWithRelation().get(keyEvent);
            Event event = new Event(keyEvent);

            //adding all read relations to the current event
            HashMap<String, Collection<String>> eventReadRelation = currEvent.get(MethodDecoder.read);
            for (String objectReadAt: eventReadRelation.keySet()) {
                readModification(sim,objectReadAt,eventReadRelation,allAttributesHashMap,allEntityHashMap,event,mapContainer);
            }

            //adding all write relations to the current event
            HashMap<String, Collection<String>> eventWriteRelation = currEvent.get(MethodDecoder.write);
            for (String objectWriteAt:eventWriteRelation.keySet()) {
                writeModification(sim,objectWriteAt,eventWriteRelation,allAttributesHashMap,allEntityHashMap,event,mapContainer);
            }

            allEventsMap.put(keyEvent, event);
        }
        //adding scheduling relations
        for (String keyEvent : mapContainer.getExtractedEventsWithRelation().keySet()) {
            SchedulingRelationModifier.addSchedulingRelationToSimulator(sim, mapContainer, keyEvent, allEventsMap);
        }
        return sim;
    }

    private static void writeModification(Simulator sim, String objectWriteAt, HashMap<String, Collection<String>> eventWriteRelation, HashMap<String, HashMap<String, Attribute>> allAttributesHashMap, HashMap<String, Entity>allEntityHashMap, Event event, IMapContainer mapContainer){
        //next key kann entity name sein
        if(allEntityHashMap.containsKey(objectWriteAt)){
            WriteRelationModifier.addWriteRelationToKnownEntityToSimulation(sim,objectWriteAt,eventWriteRelation,allAttributesHashMap,allEntityHashMap,event);
        }
        //alternativ caller/called
        if(objectWriteAt.startsWith("caller")){
            WriteRelationModifier.addWriteRelationToCallingEventToSimulation(sim, eventWriteRelation, allAttributesHashMap, allEntityHashMap, event, mapContainer);
        }
    }


    private static void readModification(Simulator sim,String objectReadAt, HashMap<String, Collection<String>> eventReadRelation, HashMap<String, HashMap<String, Attribute>> allAttributesHashMap, HashMap<String, Entity>allEntityHashMap, Event event, IMapContainer mapContainer){
        if(allEntityHashMap.containsKey(objectReadAt)) {
            //reading from known enitty object
            ReadRelationModifier.addReadRelationToKnownEntityToSimulation(sim, objectReadAt, eventReadRelation, allAttributesHashMap, allEntityHashMap, event);
        }
        //reading value from the caller
        if(objectReadAt.equals("caller")){
            ReadRelationModifier.addReadRelationToCallingEntityToSimulation(sim, eventReadRelation, allAttributesHashMap, allEntityHashMap, event, mapContainer);
        }
    }
}
