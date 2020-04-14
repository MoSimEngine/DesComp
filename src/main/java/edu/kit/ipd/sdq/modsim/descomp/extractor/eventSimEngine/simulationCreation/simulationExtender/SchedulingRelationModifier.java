package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.IMapContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.MethodDecoder;

import java.util.Collection;
import java.util.HashMap;

public class SchedulingRelationModifier {

    public static void addSchedulingRelationToSimulator(Simulator sim, IMapContainer mapContainer, String keyEvent, HashMap<String, Event> eventsHashMap){
        HashMap<String, Collection<String>> currScheduledEvent= mapContainer.getExtractedEventsWithRelation().get(keyEvent).get(MethodDecoder.schedule);
        Event consideredEvent = eventsHashMap.get(keyEvent);
        for (String scheduledEventKey:currScheduledEvent.keySet()) {
            for (String calledMethode : currScheduledEvent.get(scheduledEventKey)) {
                String methodeReferenze = scheduledEventKey +"_"+calledMethode;
                boolean scheduledEventFound = false;
                for (String eventKey: eventsHashMap.keySet()) {
                    if(methodeReferenze.contains(eventKey)){
                        scheduledEventFound = true;
                        consideredEvent.addSchedulesEvent(eventsHashMap.get(eventKey), "noCond", "noDelay");
                    }
                }
                if (!scheduledEventFound){
                    boolean containsEvent = false;
                    Event dummyEvent = null;
                    for (Event event:sim.getEvents()) {
                        if(event.getName().equals("dummyEvent_"+methodeReferenze)){
                            containsEvent = true;
                            dummyEvent = event;
                            break;
                        }
                    }
                    if(!containsEvent){
                        dummyEvent = new Event("dummyCall_"+methodeReferenze);
                        sim.addEvents(dummyEvent);
                    }
                    consideredEvent.addSchedulesEvent(dummyEvent, "noCond", "noDelay");
                }
            }
        }
    }
}
