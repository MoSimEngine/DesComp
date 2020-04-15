package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.simulationExtender;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.IMapContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.ISimulationConstructContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.MethodDecoder;
import org.apache.bcel.classfile.JavaClass;

import java.util.*;

class SchedulingRelationServices {
    /**
     * evaluates read relation referring passed attributes, that belong to entities
     *  1. evaluates whether the calling instance is from interest or not
     *  2. either adds dummy calling objects or removes duplication by inheritance
     *
     * @param mapContainer container object for data structure containing information of original jar files
     * @param allEntityHashMap map containing already added entities
     * @param callerDescription descriptive name of the caller instance
     * @return the list of calling entities
     */
    static HashMap<String, Collection<String>> getReadCallerWithAttr(IMapContainer mapContainer, HashMap<String, Entity> allEntityHashMap, String[] callerDescription){
        HashMap<String, Collection<String>> callerMap = new HashMap<>();
        boolean foundCallingInstance = false;

        //evaluates whether the calling instance is from interest or not
        if(allEntityHashMap.containsKey(callerDescription[0])){
            foundCallingInstance = getMethodCaller(mapContainer, callerDescription, callerMap, MethodDecoder.read);
        }
        if(!foundCallingInstance){
            Collection<String> attributes = new ArrayList<>();
            attributes.add(callerDescription[2]+"_"+callerDescription[3]);
            callerMap.put(callerDescription[1] + "_Caller", attributes);
        }else if(callerMap.size() > 1){
            removeDerivedMethods(callerMap, mapContainer.getEntityJavaClassHashMap(), callerDescription);
        }
        return callerMap;
    }

    /**
     * evaluates write relation referring passed attributes, that belong to entities
     *  1. evaluates whether the called instance is from interest or not
     *  2. either adds dummy called objects or removes duplication by inheritance
     *
     * @param mapContainer container object for data structure containing information of original jar files
     * @param allEntityHashMap map containing already added entities
     * @param callerDescription descriptive name of the called instance
     * @return the list of called entities
     */
    static HashMap<String, Collection<String>> getWriteCallerWithAttr(IMapContainer mapContainer, HashMap<String, Entity> allEntityHashMap, String[] callerDescription) {
        HashMap<String, Collection<String>> callerMap = new HashMap<>();
        boolean foundCallingInstance = false;
        if(allEntityHashMap.containsKey(callerDescription[0])){
            foundCallingInstance = getMethodCaller(mapContainer, callerDescription, callerMap, MethodDecoder.write);
        }

        if(!foundCallingInstance){
            Collection<String> attributes = new ArrayList<>();
            attributes.add("Attribute_Affected_By_"+callerDescription[1]);
            callerMap.put(callerDescription[1] + "_Caller", attributes);
        } else if(callerMap.size() > 1){
            removeDerivedMethods(callerMap, mapContainer.getEntityJavaClassHashMap(), callerDescription);
        }
        return callerMap;
    }

    /**
     * updates the data structures and add reference attributes and the attribute having entities
     *
     * @param simulationConstructContainer data structure containing all currently known entity objects
     * @param callerEntityName description of the calling entity
     * @param callerRelations potential names for calling entities
     */
    static void addCallerAttributeReferences(ISimulationConstructContainer simulationConstructContainer, String callerEntityName, HashMap<String, Collection<String>> callerRelations){
        HashMap<String, HashMap<String, Attribute>> attributesHashMap = simulationConstructContainer.getAttributesHashMap();
        HashMap<String, Entity>entitiesHashMap = simulationConstructContainer.getEntitiesHashMap();

        if(!entitiesHashMap.containsKey(callerEntityName)){
            entitiesHashMap.put(callerEntityName,  new Entity(callerEntityName));
            attributesHashMap.put(callerEntityName, new HashMap<>());
            for (String callerAttrName: callerRelations.get(callerEntityName)) {
                String[] callerAttrNameArray = callerAttrName.split("_");
                int arraySize = callerAttrNameArray.length;
                attributesHashMap.get(callerEntityName).put(callerAttrNameArray[arraySize-1] , new Attribute(callerAttrNameArray[arraySize-1], callerAttrNameArray[arraySize-2]));
            }
        } else {
            for (String callerAttrName : callerRelations.get(callerEntityName)) {
                String[] callerAttrNameArray = callerAttrName.split("_");
                if(!attributesHashMap.get(callerEntityName).containsKey(callerAttrNameArray[callerAttrNameArray.length-1])) {
                    int arraySize = callerAttrNameArray.length;
                    attributesHashMap.get(callerEntityName).put(callerAttrNameArray[arraySize-1] , new Attribute(callerAttrNameArray[arraySize-1], callerAttrNameArray[arraySize-2]));
                }
            }
        }
    }

    /**
     * adds an entity to the simulation, iff its not already added
     *
     * @param sim the simulation to add the entity to
     * @param entity the to be added entity
     */
    static void addEntity(Simulator sim, Entity entity){
        if(!sim.getEntities().contains(entity)){
            sim.addEntities(entity);
        }
    }

    /**
     * adds an event to the simulation, iff its not already added
     *
     * @param sim the simulation to add the event to
     * @param event the to be added event
     */
    static void addEvent(Simulator sim, Event event){
        if(!sim.getEvents().contains(event)){
            sim.addEvents(event);
        }
    }

    private static void removeDerivedMethods(HashMap<String, Collection<String>> oldCallerMap, HashMap<String, JavaClass> entityJavaClassHashMap, String[] callerDescription){
        if(entityJavaClassHashMap.get(callerDescription[0]).isSuper()){
            JavaClass originalJavaClass = entityJavaClassHashMap.get(callerDescription[0]);
            Set<String> oldCallerMapKeySet = new HashSet<>(oldCallerMap.keySet());
            for (String callerKey :oldCallerMapKeySet){
                JavaClass currentJavaClass = entityJavaClassHashMap.get(callerKey);
                if(isMethodFromSameSuperClass(currentJavaClass, originalJavaClass, entityJavaClassHashMap)){
                    oldCallerMap.remove(callerKey);
                }
            }
        }
    }

    private static boolean isMethodFromSameSuperClass(JavaClass classA, JavaClass classB, HashMap<String, JavaClass> entityJavaClassHashMap) {
        boolean directRelation =  (classA.getSuperclassName().equals(classB.getSuperclassName()) || classA.getClassName().equals(classB.getSuperclassName()) ||classA.getSuperclassName().equals(classB.getClassName())) && !classA.equals(classB);
        boolean superRelation = false;
        if (!directRelation) {
            for (JavaClass classParrent: entityJavaClassHashMap.values()) {
                if(classA.getSuperclassName().equals(classParrent.getClassName())){
                    superRelation = isMethodFromSameSuperClass(classParrent, classB, entityJavaClassHashMap);
                } else if(classB.getSuperclassName().equals(classParrent.getClassName())){
                    superRelation = isMethodFromSameSuperClass(classA, classParrent, entityJavaClassHashMap);
                }
            }
        }
        return directRelation || superRelation;
    }

    private static boolean getMethodCaller(IMapContainer mapContainer, String[] callerDescription, HashMap<String, Collection<String>> callerMap, String callerRelation){
        boolean foundCallingInstance = false;
        HashMap<String, JavaClass> entityJavaClassHashMap = mapContainer.getEntityJavaClassHashMap();
        HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> extractedEventsWithRelation = mapContainer.getExtractedEventsWithRelation();

        JavaClass jc = entityJavaClassHashMap.get(callerDescription[0]);
        Collection<String> allClassNames = new ArrayList<>();
        allClassNames.add(callerDescription[0]);
        allClassNames.add(jc.getSuperclassName());
        allClassNames.addAll(Arrays.asList(jc.getInterfaceNames()));
        for (String possibleName:allClassNames) {
            for (String eventSavedName : extractedEventsWithRelation.keySet()) {
                HashMap<String, HashMap<String, Collection<String>>> eventMap = extractedEventsWithRelation.get(eventSavedName);
                for (HashMap<String, Collection<String>> scheduleEvent: Collections.singletonList(eventMap.get(MethodDecoder.schedule))){
                    for (String keyOfScheduledEvent : scheduleEvent.keySet()){
                        if(keyOfScheduledEvent.endsWith(possibleName) && scheduleEvent.get(keyOfScheduledEvent).contains(callerDescription[1])){
                            for (String callReadkeys : eventMap.get(callerRelation).keySet()) {
                                if(callReadkeys.startsWith("called_") && callReadkeys.contains(possibleName) && callReadkeys.endsWith(callerDescription[1])){//
                                    foundCallingInstance = true;
                                    callerMap.put(eventSavedName.split("_")[0], eventMap.get(callerRelation).get(callReadkeys));
                                }
                            }
                        }
                    }
                }
            }
        }
        return foundCallingInstance;
    }
}
