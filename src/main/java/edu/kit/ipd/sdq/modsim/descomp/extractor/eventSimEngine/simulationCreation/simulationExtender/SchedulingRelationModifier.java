package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.IMapContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.MethodDecoder;

import java.util.Collection;
import java.util.HashMap;

public class SchedulingRelationModifier {

    /**
     * adds scheduling relations for a a specified to other events, that so far specified Simulator instance
     *      1. for each specified event finds the scheduled events
     *      2. either adds scheduling relation to an existing event or adds a dummy event for scheduled events, which are not considered in detail within the analysis
     *
     * @param sim the Simulator instance already containing entities, attribute and events of interests as well as relation types hasAttribute, readAttribute and writeAttribute
     * @param mapContainer Container-Object for the simulation describing data structures
     * @param keyEvent the considered event, for which the scheduling relations should be added
     * @param eventsHashMap HasMap, containing all considered and in the simulator represented events
     */
    public static void addSchedulingRelationToSimulator(Simulator sim, IMapContainer mapContainer, String keyEvent, HashMap<String, Event> eventsHashMap){
        HashMap<String, Collection<String>> currScheduledEvent= mapContainer.getExtractedEventsWithRelation().get(keyEvent).get(MethodDecoder.schedule);
        Event consideredEvent = eventsHashMap.get(keyEvent);
        for (String scheduledEventKey:currScheduledEvent.keySet()) {
            for (String calledMethod : currScheduledEvent.get(scheduledEventKey)) {
                String methodReference = scheduledEventKey +"_"+calledMethod;
                boolean scheduledEventFound = false;
                for (String eventKey: eventsHashMap.keySet()) {
                    if(methodReference.contains(eventKey)){
                        scheduledEventFound = true;
                        consideredEvent.addSchedulesEvent(eventsHashMap.get(eventKey), "noCond", "noDelay");
                    }
                }
                if (!scheduledEventFound){
                    boolean containsEvent = false;
                    Event dummyEvent = null;
                    for (Event event:sim.getEvents()) {
                        if(event.getName().equals("dummyEvent_"+methodReference)){
                            containsEvent = true;
                            dummyEvent = event;
                            break;
                        }
                    }
                    if(!containsEvent){
                        dummyEvent = new Event("dummyCall_"+methodReference);
                        sim.addEvents(dummyEvent);
                    }
                    consideredEvent.addSchedulesEvent(dummyEvent, "noCond", "noDelay");
                }
            }
        }
    }
}
