package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementOperation.AttributeOperation;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementOperation.EntityOperation;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.MethodDecoder;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

import java.util.*;

public class GeneratorMappingServices {

    public static HashMap<String, Entity> createEntityObjectsForJavaClasses(HashMap<String, JavaClass> entityJavaClassHashMap){
        HashMap<String, Entity> entityMap= new HashMap<>();
        for (String key:entityJavaClassHashMap.keySet()) {
            entityMap.put(key, EntityOperation.createEntity(entityJavaClassHashMap.get(key)));
        }
        return entityMap;
    }

    public static HashMap<String, HashMap<String, Attribute>> createAttributeObjectsForFieldClasses(HashMap<String, HashMap<String, Field>> fieldAttrHasMap){
        HashMap<String, HashMap<String, Attribute>> attributeHashMap = new HashMap<>();
        for (String key:fieldAttrHasMap.keySet()) {
            HashMap<String, Attribute> attributes = new HashMap<>();
            for (String fieldKey : fieldAttrHasMap.get(key).keySet()) {
                Field field = fieldAttrHasMap.get(key).get(fieldKey);
                attributes.put(field.getName(), AttributeOperation.createAttributes(field));
            }
            attributeHashMap.put(key,attributes);
        }
        return attributeHashMap;
    }

    public static HashMap<String, Collection<String>> getReadCallerWithAttr(HashMap<String, JavaClass> entityJavaClassHashMap, HashMap<String, Entity> allEntityHashMap, HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> extractedEventsWithRelation, String[] callerDescription){
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
            callerMap = removeDerivedMethods(callerMap, entityJavaClassHashMap, callerDescription);
        }

        return callerMap;
    }

    public static HashMap<String, Collection<String>> getWriteCallerWithAttr(HashMap<String, JavaClass> entityJavaClassHashMap, HashMap<String, Entity> allEntityHashMap, HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> extractedEventsWithRelation, String[] callerDescription) {
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
            callerMap = removeDerivedMethods(callerMap, entityJavaClassHashMap, callerDescription);
        }

        return callerMap;
    }

    private static HashMap<String, Collection<String>> removeDerivedMethods(HashMap<String, Collection<String>> oldCallerMap, HashMap<String, JavaClass> entityJavaClassHashMap, String[] callerDescription){
        if(entityJavaClassHashMap.get(callerDescription[0]).isSuper()){
            JavaClass originalJavaClass = entityJavaClassHashMap.get(callerDescription[0]);
            Set<String> oldCallerMapKeySet = new HashSet<>(oldCallerMap.keySet());
            for (String callerKey :oldCallerMapKeySet){
                JavaClass currentJavaClass = entityJavaClassHashMap.get(callerKey);
                if(methodFromSameSuperClass(currentJavaClass, originalJavaClass, entityJavaClassHashMap)){
                    oldCallerMap.remove(callerKey);
                }
            }
        }
        return oldCallerMap;
    }

    private static boolean methodFromSameSuperClass(JavaClass classA, JavaClass classB, HashMap<String, JavaClass> entityJavaClassHashMap) {
        boolean directRelation =  (classA.getSuperclassName().equals(classB.getSuperclassName()) || classA.getClassName().equals(classB.getSuperclassName()) ||classA.getSuperclassName().equals(classB.getClassName())) && !classA.equals(classB);
        boolean superRelation = false;
        if (!directRelation) {
            for (JavaClass classParrent: entityJavaClassHashMap.values()) {
                if(classA.getSuperclassName().equals(classParrent.getClassName())){
                    superRelation = methodFromSameSuperClass(classParrent, classB, entityJavaClassHashMap);
                } else if(classB.getSuperclassName().equals(classParrent.getClassName())){
                    superRelation = methodFromSameSuperClass(classA, classParrent, entityJavaClassHashMap);
                }
            }
        }
        return directRelation || superRelation;
    }
}
