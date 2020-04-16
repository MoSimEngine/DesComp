package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.*;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.IMapContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.ISimulationConstructContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.SimulationConstructContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.MethodDecoder;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender.ReadRelationModifier;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender.SchedulingRelationModifier;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender.WriteRelationModifier;

import java.util.*;

public class SimulationGenerator {

    /**
     * Creates simulation based on the passed description
     *  1. step: creating events and linking reading and writing attribute relations as well as the attribute having relation between entities and attributes
     *  2. step: linking events by adding scheduling relation
     *
     * @param mapContainer Container-Object for the simulation describing data structures
     * @return the extracted simulation, described by the mapContainer
     */
    public static Simulator extractSimulator(IMapContainer mapContainer){
        HashMap<String, Entity> allEntityHashMap = GeneratorMappingServices.createEntityObjectsForJavaClasses(mapContainer.getEntityJavaClassHashMap());
        HashMap<String, HashMap<String, Attribute>> allAttributesHashMap = GeneratorMappingServices.createAttributeObjectsForFieldClasses(mapContainer.getFieldAttrHasMap());
        ISimulationConstructContainer simulationConstructContainer = new SimulationConstructContainer(allEntityHashMap, allAttributesHashMap);
        HashMap<String, Event> eventsMap = new HashMap<>();

        Simulator sim = new Simulator("extractedSimulator", "Simulator extracted from EvenSim");

        //creating events and linking reading and writing attribute relations as well as the attribute having relation between entities and attributes
        for (String keyEvent : mapContainer.getExtractedEventsWithRelation().keySet()){
            HashMap<String, HashMap<String, Collection<String>>> currEvent= mapContainer.getExtractedEventsWithRelation().get(keyEvent);
            Event event = new Event(keyEvent);

            //adding all read relations to the current event
            HashMap<String, Collection<String>> eventReadRelation = currEvent.get(MethodDecoder.read);
            for (String objectReadAt: eventReadRelation.keySet()) {
                readModification(sim,objectReadAt,eventReadRelation, simulationConstructContainer,event,mapContainer);
            }

            //adding all write relations to the current event
            HashMap<String, Collection<String>> eventWriteRelation = currEvent.get(MethodDecoder.write);
            for (String objectWriteAt:eventWriteRelation.keySet()) {
                writeModification(sim,objectWriteAt,eventWriteRelation, simulationConstructContainer,event,mapContainer);
            }

            eventsMap.put(keyEvent, event);
        }

        //linking events by adding scheduling relation
        for (String keyEvent : mapContainer.getExtractedEventsWithRelation().keySet()) {
            SchedulingRelationModifier.addSchedulingRelationToSimulator(sim, mapContainer, keyEvent, eventsMap);
        }
        return sim;
    }

    private static void writeModification(Simulator sim, String objectWriteAt, HashMap<String, Collection<String>> eventWriteRelation, ISimulationConstructContainer simulationConstructContainer, Event event, IMapContainer mapContainer){
        if(simulationConstructContainer.getEntitiesHashMap().containsKey(objectWriteAt)){
            WriteRelationModifier.addWriteRelationToKnownEntityToSimulation(sim,objectWriteAt,eventWriteRelation, simulationConstructContainer,event);
        }
        //alternativ caller/called
        else if(objectWriteAt.startsWith("caller")){
            WriteRelationModifier.addWriteRelationToCallingEventToSimulation(sim, eventWriteRelation, simulationConstructContainer, mapContainer, event);
        }
    }

    private static void readModification(Simulator sim,String objectReadAt, HashMap<String, Collection<String>> eventReadRelation, ISimulationConstructContainer simulationConstructContainer, Event event, IMapContainer mapContainer){
        if(simulationConstructContainer.getEntitiesHashMap().containsKey(objectReadAt)) {
            ReadRelationModifier.addReadRelationToKnownEntityToSimulation(sim, objectReadAt, eventReadRelation, simulationConstructContainer, event);
        }
        //reading value from the caller
        else if(objectReadAt.equals("caller")){
            ReadRelationModifier.addReadRelationToCallingEntityToSimulation(sim, eventReadRelation, simulationConstructContainer, event, mapContainer);
        }
    }
}
