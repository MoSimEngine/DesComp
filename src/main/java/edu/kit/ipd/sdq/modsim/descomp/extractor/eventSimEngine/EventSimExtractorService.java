package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.IMapContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.MapContainer;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementOperation.EntityOperation;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.MethodDecoder;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools.ClassFilter;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools.IClassFilter;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools.IJavaClassExtractor;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation.SimulationGenerator;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.bcel.classfile.JavaClass;


import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class EventSimExtractorService implements IEventSimExtractorService, IEventSimHierarchyService {

    @Autowired
    private IJavaClassExtractor classExtractor;

    @Autowired
    private IClassFilter classFilter;

    private Map<String, List<JavaClass>> extractedJavaClasses;
    private Collection<JavaClass> javaClassesCollection;

    public EventSimExtractorService(){
        extractedJavaClasses = new HashMap<>();
    }

    @Override
    public Simulator extractEventSim(Collection<File> jarCollection) {
        extractedJavaClasses = classExtractor.extractJavaClasses(jarCollection);
        javaClassesCollection = collectJavaClasses();
        HashMap<String, JavaClass> entityJavaClassHashMap = getEntitiesInHashMap(classFilter.extractClassesWithHierarchy(javaClassesCollection, ClassFilter.classNames));
        HashMap<String, HashMap<String, Field>> fieldAttrHashMap = getAttributeHashMap(entityJavaClassHashMap);
        HashMap<String, HashMap<String, HashMap<String, Collection<String>>>>  extractedEventsWithRelation = extractAllEvents();

        IMapContainer mapContainer = new MapContainer(entityJavaClassHashMap, fieldAttrHashMap, extractedEventsWithRelation);

        return SimulationGenerator.createSimulator(mapContainer);
    }

    public Collection<String> getDerivedClasses(String parentClass){
        String[] newStringArray = {parentClass.split(Pattern.quote("."))[parentClass.split(Pattern.quote(".")).length -1] + ".java"};
        Collection<JavaClass> derivedClasses = classFilter.extractClassesAbstractParents(javaClassesCollection , newStringArray);
        Collection<String> allDerivedNames = new ArrayList<>();
        derivedClasses.stream().forEach(jc -> allDerivedNames.add(jc.getClassName()));
        return allDerivedNames;
    }

    private Collection<JavaClass> collectJavaClasses(){
        Collection<JavaClass> javaClasses = new ArrayList<>();
        for (List<JavaClass>  classList : extractedJavaClasses.values()) {
            javaClasses.addAll(classList);
        }
        return javaClasses;
    }

    private HashMap<String, JavaClass> getEntitiesInHashMap(Collection<JavaClass> javaClassWithHierarchyCollection){
        HashMap<String, JavaClass> classHashMap = new HashMap<>();
        for (JavaClass jc: javaClassWithHierarchyCollection) {
            classHashMap.put(EntityOperation.getJavaClassName(jc), jc);
        }
        return classHashMap;
    }

    private HashMap<String,HashMap<String, Field>> getAttributeHashMap(HashMap<String, JavaClass> javaClassesHashMap){
        HashMap<String, HashMap<String, Field>> attributeHashMap = new HashMap<>();
        for (String key:javaClassesHashMap.keySet()) {
            HashMap<String, Field> attributes = new HashMap<>();
            for (Field field : javaClassesHashMap.get(key).getFields()) {
                attributes.put(field.getName(),field);
            }
            attributeHashMap.put(key,attributes);
        }
        return attributeHashMap;
    }

    private HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> extractAllEvents(){
        Collection<JavaClass> eventClasses = classFilter.extractClassesWithHierarchy(javaClassesCollection, ClassFilter.classNames);//getEventClasses(collectJavaClasses());
        Collection<JavaClass> abstractClasses = classFilter.extractClassesAbstractParents(javaClassesCollection, ClassFilter.classNames);//getEventClasses(collectJavaClasses());
        HashMap<String, Method> methodClasses = classFilter.getMethods(eventClasses, abstractClasses,  ClassFilter.eventMethodNames);
        return MethodDecoder.extractEventsFromMethods(methodClasses, this);
    }

}
