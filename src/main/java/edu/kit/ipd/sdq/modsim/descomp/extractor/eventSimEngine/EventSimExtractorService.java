package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator.AttributeCreator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator.EntityCreator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator.MethodeCreator;
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

@Service
public class EventSimExtractorService implements EventExtractorService{

    @Autowired
    private JavaClassExtraction classExtrator;

    @Autowired
    private IClassFilter classFilter;


    private Map<String, List<JavaClass>> extractedJavaClasses;

    public EventSimExtractorService(){
        extractedJavaClasses = new HashMap<>();
    }

    @Override
    public Simulator extractEventSim(Collection<File> jarCollection) {

        Simulator simulator = new Simulator("EXTARCTED_SIMULATION", "EXTARCTED_SIMULATION");


        //get all java classes of available jar files
        extractedJavaClasses = classExtrator.extractJavaClasses(jarCollection);
        //filter jar classes for available entity classes and add entities to simulator
        Collection<JavaClass> extractedEnitiesClasses = classFilter.extractClassesWithHierarchie(collectJavaClasses(), ClassFilter.entityClassNames);//filterEntityClasses(collectJavaClasses());
        Collection<Entity> entityCollection = getEntityObjects(extractedEnitiesClasses);
        entityCollection.forEach( entity -> simulator.addEntities(entity));

        Collection<Event> eventClasses = extractAllEvents();

        return simulator;
    }

    private Collection<JavaClass> collectJavaClasses(){
        Collection<JavaClass> javaClasses = new ArrayList<>();
        for (List<JavaClass>  classList : extractedJavaClasses.values()) {
            javaClasses.addAll(classList);
        }
        return javaClasses;
    }

    private Collection<Entity> getEntityObjects(Collection<JavaClass> javaClassCollection){
        Collection<Entity> entityCollection = new ArrayList<>();
        for (JavaClass jc: javaClassCollection) {
            Entity entity = EntityCreator.createEntity(jc);
            for (Attribute attr: getAttributes(jc)) {
                entity.addAttribute(attr);
            }
            entityCollection.add(entity);
        }
        return entityCollection;
    }

    private Collection<Attribute> getAttributes(JavaClass jc){
        Collection<Attribute> attributes = new ArrayList<>();
        Field[] fieldArray = jc.getFields();
        for (Field field: fieldArray) {
            attributes.add(AttributeCreator.createAttributes(field));
        }
        return attributes;
    }

    public Collection<Event> extractAllEvents(){
        Collection<JavaClass> eventClasses = classFilter.extractClassesWithHierarchie(collectJavaClasses(), ClassFilter.eventClassNames);//getEventClasses(collectJavaClasses());
        Collection<Method> methodClasses = classFilter.getMethodes(eventClasses);
        MethodeCreator.extractEventsFromMethods(methodClasses);
        return null;
    }


}
