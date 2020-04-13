package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.*;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator.AttributeCreator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator.EntityOperation;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.MethodDecoder;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

import java.util.*;

public class SimulationGenerator {
    public static Simulator createSimulator(HashMap<String, JavaClass> entityJavaClassHashMap, HashMap<String, HashMap<String, Field>> fieldAttrHasMap, HashMap<String, HashMap<String, HashMap<String, Collection<String>>>>  extractedEventsWithRelation){
        HashMap<String, Entity> allEntityHashMap = getAllEntities(entityJavaClassHashMap);
        HashMap<String, HashMap<String, Attribute>> allAttributesHashMap = getAllAttributes(fieldAttrHasMap);
        HashMap<String, Event> allEventsMap = new HashMap<>();

        Simulator sim = new Simulator("extractedSimulator", "Simulator extracted from EvenSim");


        //adding read and write relations
        for (String keyEvents : extractedEventsWithRelation.keySet()){
                HashMap<String, HashMap<String, Collection<String>>> currEvent= extractedEventsWithRelation.get(keyEvents);
                Event event = new Event(keyEvents);

                //reading objects
                HashMap<String, Collection<String>> eventReadRelation = currEvent.get(MethodDecoder.read);
                for (String objectReadAt: eventReadRelation.keySet()) {
                    if(allEntityHashMap.containsKey(objectReadAt)) {
                        //reading from known enitty object
                        for (String attrRead : eventReadRelation.get(objectReadAt)) {
                            if (allAttributesHashMap.get(objectReadAt).containsKey(attrRead)) {
                                if(!allEntityHashMap.containsKey(objectReadAt)){
                                    allEntityHashMap.put(objectReadAt, new Entity(objectReadAt));
                                    allAttributesHashMap.put(objectReadAt, new HashMap<String,Attribute>());
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
                        //reading value from the caller
                    if(objectReadAt.equals("caller")){
                        for (String callerDescriptionString:eventReadRelation.get("caller")) {
                            String[] callerDescription = callerDescriptionString.split("_");

                            HashMap<String, Collection<String>> readCallerWithAttr = getReadCallerWithAttr(entityJavaClassHashMap, allEntityHashMap, allAttributesHashMap, extractedEventsWithRelation, callerDescription);
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
                }

                HashMap<String, Collection<String>> eventWriteRelation = currEvent.get(MethodDecoder.write);
                for (String objectWriteAt:eventWriteRelation.keySet()) {
                    //next key kann entity name sein
                    if(allEntityHashMap.containsKey(objectWriteAt)){
                        for (String attributeNameComposed :eventWriteRelation.get(objectWriteAt)) {
                            String attributeType = attributeNameComposed.split("_")[0];
                            String attributeName = attributeNameComposed.split("_")[1];
//                                objectWriteAt;
                            if(!allEntityHashMap.containsKey(objectWriteAt)){
                                allEntityHashMap.put(objectWriteAt, new Entity(objectWriteAt));
                                allAttributesHashMap.put(objectWriteAt, new HashMap<String, Attribute>());
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
                    //alternativ caller/called
                    if(objectWriteAt.startsWith("caller")){
                        for (String callerDescriptionString:eventWriteRelation.get("caller")) {
                            String[] callerDescription = callerDescriptionString.split("_");

                            HashMap<String, Collection<String>> writeRelations = getWriteCallerWithAttr(entityJavaClassHashMap, allEntityHashMap, allAttributesHashMap, extractedEventsWithRelation, callerDescription);
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
                }

            allEventsMap.put(keyEvents, event);
        }

        //adding scheduling relations
        for (String keyEvent : extractedEventsWithRelation.keySet()) {
            HashMap<String, Collection<String>> currScheduledEvent= extractedEventsWithRelation.get(keyEvent).get(MethodDecoder.schedule);
            Event consideredEvent = allEventsMap.get(keyEvent);
            for (String scheduledEventKey:currScheduledEvent.keySet()) {
                for (String calledMethode : currScheduledEvent.get(scheduledEventKey)) {
                    String methodeReferenze = scheduledEventKey +"_"+calledMethode;
                    boolean scheduledEventFound = false;
                    for (String eventKey: allEventsMap.keySet()) {
                        if(methodeReferenze.contains(eventKey)){
                            scheduledEventFound = true;
                            consideredEvent.addSchedulesEvent(allEventsMap.get(eventKey), "noCond", "noDelay");
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
        return sim;
    }


    private static HashMap<String, Entity> getAllEntities(HashMap<String, JavaClass> entityJavaClassHashMap){
        HashMap<String, Entity> entityMap= new HashMap<>();
        for (String key:entityJavaClassHashMap.keySet()) {
            entityMap.put(key, EntityOperation.createEntity(entityJavaClassHashMap.get(key)));
        }
        return entityMap;
    }

    private static HashMap<String, HashMap<String, Attribute>> getAllAttributes(HashMap<String, HashMap<String, Field>> fieldAttrHasMap){
        HashMap<String, HashMap<String, Attribute>> attributeHashMap = new HashMap<>();
        for (String key:fieldAttrHasMap.keySet()) {
            HashMap<String, Attribute> attributes = new HashMap<>();
            for (String fieldKey : fieldAttrHasMap.get(key).keySet()) {
                Field field = fieldAttrHasMap.get(key).get(fieldKey);
                attributes.put(field.getName(), AttributeCreator.createAttributes(field));
            }
            attributeHashMap.put(key,attributes);
        }
        return attributeHashMap;
    }

    private static HashMap<String, Collection<String>> getReadCallerWithAttr(HashMap<String, JavaClass> entityJavaClassHashMap, HashMap<String, Entity> allEntityHashMap, HashMap<String, HashMap<String, Attribute>> allAttributesHashMap, HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> extractedEventsWithRelation, String[] callerDescription){
        HashMap<String, Collection<String>> callerMap = new HashMap<>();
        boolean foundSth = false;
        if(allEntityHashMap.containsKey(callerDescription[0])){
            JavaClass jc = entityJavaClassHashMap.get(callerDescription[0]);
            Collection<String> allClassNames = new ArrayList<>();
            allClassNames.add(callerDescription[0]);
            allClassNames.add(jc.getSuperclassName());
            allClassNames.addAll(Arrays.asList(jc.getInterfaceNames()));
            for (String possibleName:allClassNames) {
                for (String eventSavedName : extractedEventsWithRelation.keySet()) {
                    HashMap<String, HashMap<String, Collection<String>>> maps = extractedEventsWithRelation.get(eventSavedName);
                    for (HashMap<String, Collection<String>> scheduleEvent: Arrays.asList(maps.get(MethodDecoder.schedule))){
                        for (String keyOfScheduledEvent : scheduleEvent.keySet()){
                            if(keyOfScheduledEvent.endsWith(possibleName) && scheduleEvent.get(keyOfScheduledEvent).contains(callerDescription[1])){
                                // etwas gefunden, was die mehtode aufruft
                                for (String allReadkeys : maps.get(MethodDecoder.read).keySet()) {
                                    if(allReadkeys.startsWith("called_") && allReadkeys.contains(possibleName) && allReadkeys.endsWith(callerDescription[1])){//
                                        foundSth = true;
                                        callerMap.put(eventSavedName.split("_")[0], maps.get(MethodDecoder.read).get(allReadkeys));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(!foundSth){
            Collection<String> attributes = new ArrayList<>();
            attributes.add(callerDescription[2]+"_"+callerDescription[3]);
            callerMap.put(callerDescription[1] + "_Caller", attributes);
        }

        if(foundSth && callerMap.size()>1){
            callerMap =removeDerivedMethodes(callerMap, entityJavaClassHashMap, callerDescription);
        }

        return callerMap;
    }

    private static HashMap<String, Collection<String>> getWriteCallerWithAttr(HashMap<String, JavaClass> entityJavaClassHashMap, HashMap<String, Entity> allEntityHashMap, HashMap<String, HashMap<String, Attribute>> allAttributesHashMap, HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> extractedEventsWithRelation, String[] callerDescription) {
        HashMap<String, Collection<String>> callerMap = new HashMap<>();
        boolean foundSth = false;
        if(allEntityHashMap.containsKey(callerDescription[0])){
            JavaClass jc = entityJavaClassHashMap.get(callerDescription[0]);
            Collection<String> allClassNames = new ArrayList<>();
            allClassNames.add(callerDescription[0]);
            allClassNames.add(jc.getSuperclassName());

            for (String possibleName:allClassNames) {
                for (String eventSavedName : extractedEventsWithRelation.keySet()) {
                    HashMap<String, HashMap<String, Collection<String>>> maps = extractedEventsWithRelation.get(eventSavedName);
                    for (HashMap<String, Collection<String>> scheduleEvent: Arrays.asList(maps.get(MethodDecoder.schedule))){
                        for (String keyOfScheduledEvent : scheduleEvent.keySet()){
                            if(keyOfScheduledEvent.endsWith(possibleName) && scheduleEvent.get(keyOfScheduledEvent).contains(callerDescription[1])) {
                                // etwas gefunden, was die mehtode aufruft
                                for(String allWritekeys : maps.get(MethodDecoder.write).keySet()) {
                                    if(allWritekeys.startsWith("called_") && allWritekeys.contains(possibleName) && allWritekeys.endsWith(callerDescription[1])){//
                                        foundSth = true;
                                        callerMap.put(eventSavedName.split("_")[0], maps.get(MethodDecoder.write).get(allWritekeys));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        if(!foundSth){
            Collection<String> attributes = new ArrayList<>();
            attributes.add("Attribute_Affected_By_"+callerDescription[1]);
            callerMap.put(callerDescription[1] + "_Caller", attributes);
        }

        if(foundSth && callerMap.size()>1){
            callerMap =removeDerivedMethodes(callerMap, entityJavaClassHashMap, callerDescription);
        }

        return callerMap;
    }

    private static HashMap<String, Collection<String>> removeDerivedMethodes(HashMap<String, Collection<String>> oldCallerMap, HashMap<String, JavaClass> entityJavaClassHashMap, String[] callerDescription){
        if(entityJavaClassHashMap.get(callerDescription[0]).isSuper()){
            JavaClass originalJavaClass = entityJavaClassHashMap.get(callerDescription[0]);
            Set<String> oldCallerMapKeySet = new HashSet<>(oldCallerMap.keySet());
            for (String callerKey :oldCallerMapKeySet){
                JavaClass currentJavaClass = entityJavaClassHashMap.get(callerKey);
                if(methodeFromSameSuperClass(currentJavaClass, originalJavaClass, entityJavaClassHashMap)){
                    oldCallerMap.remove(callerKey);
                }
            }
        }
        return oldCallerMap;
    }

    private static boolean methodeFromSameSuperClass(JavaClass classA, JavaClass classB, HashMap<String, JavaClass> entityJavaClassHashMap) {
        boolean directRelation =  (classA.getSuperclassName().equals(classB.getSuperclassName()) || classA.getClassName().equals(classB.getSuperclassName()) ||classA.getSuperclassName().equals(classB.getClassName())) && !classA.equals(classB);
        boolean superRelation = false;
        if (!directRelation) {
            for (JavaClass classParrent: entityJavaClassHashMap.values()) {
                if(classA.getSuperclassName().equals(classParrent.getClassName())){
                    superRelation = methodeFromSameSuperClass(classParrent, classB, entityJavaClassHashMap);
                } else if(classB.getSuperclassName().equals(classParrent.getClassName())){
                    superRelation = methodeFromSameSuperClass(classA, classParrent, entityJavaClassHashMap);
                }
            }
        }
        return directRelation || superRelation;
    }
}
