package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.simulationCreation;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementOperation.AttributeOperation;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementOperation.EntityOperation;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

import java.util.*;

class GeneratorMappingServices {

    /**
     * takes java classes and creates for each class a Entity object, represented in the simulator
     *
     * @param entityJavaClassHashMap map of all bcel.JavaClass objects to create Entity objects for
     * @return HashMap containing pairs with key entityName, value Entity pair for all passed JavaClasses
     */
    static HashMap<String, Entity> createEntityObjectsForJavaClasses(HashMap<String, JavaClass> entityJavaClassHashMap){
        HashMap<String, Entity> entityMap= new HashMap<>();
        for (String key:entityJavaClassHashMap.keySet()) {
            entityMap.put(key, EntityOperation.createEntity(entityJavaClassHashMap.get(key)));
        }
        return entityMap;
    }

    /**
     * Takes HashMap containing fields as values and transforms those fields into Attribute Objects, represented in the simulator
     *
     * @param fieldAttrHasMap m,ap of all bcel.Field objects to create attributes for
     * @return HashMap containing pair with key entityName, of the attribute having entity, and value HashMap with key attributeName and value Attribute object
     */
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
