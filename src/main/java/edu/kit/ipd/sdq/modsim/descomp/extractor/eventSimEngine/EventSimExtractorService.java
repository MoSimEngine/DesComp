package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator.EntityOperation;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.MethodDecoder;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools.ClassFilter;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools.IClassFilter;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools.JavaClassExtraction;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.bcel.classfile.JavaClass;


import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class EventSimExtractorService implements EventExtractorService, EventSimHierarchyService {

    @Autowired
    private JavaClassExtraction classExtractor;

    @Autowired
    private IClassFilter classFilter;


    private Map<String, List<JavaClass>> extractedJavaClasses;

    public EventSimExtractorService(){
        extractedJavaClasses = new HashMap<>();
    }

    @Override
    public Simulator extractEventSim(Collection<File> jarCollection) {

        extractedJavaClasses = classExtractor.extractJavaClasses(jarCollection);
        HashMap<String, JavaClass> entityJavaClassHasMap = getEntitiesInHashMap(classFilter.extractClassesWithHierarchie(collectJavaClasses(), ClassFilter.classNames));
        HashMap<String, HashMap<String, Field>> fieldAttrHasMap = getAttributeHashMap(entityJavaClassHasMap);

        HashMap<String, HashMap<String, HashMap<String, Collection<String>>>>  extractedEventsWithRelation = extractAllEvents();
        return SimulationGenerator.createSimulator(entityJavaClassHasMap,fieldAttrHasMap,extractedEventsWithRelation);
    }

    private Collection<JavaClass> collectJavaClasses(){
        Collection<JavaClass> javaClasses = new ArrayList<>();
        for (List<JavaClass>  classList : extractedJavaClasses.values()) {
            javaClasses.addAll(classList);
        }
        return javaClasses;
    }

    private HashMap<String, JavaClass> getEntitiesInHashMap(Collection<JavaClass> javaClassCollection){
        HashMap<String, JavaClass> classHashMap = new HashMap<>();
        for (JavaClass jc: javaClassCollection) {
            classHashMap.put(EntityOperation.getJavaClassName(jc), jc);
        }
        return classHashMap;
    }

    private HashMap<String,HashMap<String, Field>> getAttributeHashMap(HashMap<String, JavaClass> jclassesHasMap){
        HashMap<String, HashMap<String, Field>> attributeHashMap = new HashMap<>();
        for (String key:jclassesHasMap.keySet()) {
            HashMap<String, Field> attributes = new HashMap<>();
            for (Field field : jclassesHasMap.get(key).getFields()) {
                attributes.put(field.getName(),field);
            }
            attributeHashMap.put(key,attributes);
        }
        return attributeHashMap;
    }

    private HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> extractAllEvents(){
        Collection<JavaClass> eventClasses = classFilter.extractClassesWithHierarchie(collectJavaClasses(), ClassFilter.classNames);//getEventClasses(collectJavaClasses());
        Collection<JavaClass> abstractClasses = classFilter.extractClassesAbstractParents(collectJavaClasses(), ClassFilter.classNames);//getEventClasses(collectJavaClasses());
        HashMap<String, Method> methodClasses = classFilter.getMethodes(eventClasses, abstractClasses,  ClassFilter.eventMethodeNames);
        return MethodDecoder.extractEventsFromMethods(methodClasses, this);
    }

    public Collection<String> getDerivedClasses(String parentClass){
        String[] newStringArray = {parentClass.split(Pattern.quote("."))[parentClass.split(Pattern.quote(".")).length -1] + ".java"};
        Collection<JavaClass> derivedClasses = classFilter.extractClassesAbstractParents(collectJavaClasses() , newStringArray);
        Collection<String> allDerivedNames = new ArrayList<>();
        derivedClasses.stream().forEach(jc -> allDerivedNames.add(jc.getClassName()));
        return allDerivedNames;
    }


}
