package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementOperation.AttributeOperation;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementOperation.EntityOperation;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

import java.util.*;

public class GeneratorMappingServices {

    static HashMap<String, Entity> createEntityObjectsForJavaClasses(HashMap<String, JavaClass> entityJavaClassHashMap){
        HashMap<String, Entity> entityMap= new HashMap<>();
        for (String key:entityJavaClassHashMap.keySet()) {
            entityMap.put(key, EntityOperation.createEntity(entityJavaClassHashMap.get(key)));
        }
        return entityMap;
    }

    static HashMap<String, HashMap<String, Attribute>> createAttributeObjectsForFieldClasses(HashMap<String, HashMap<String, Field>> fieldAttrHasMap){
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


}
