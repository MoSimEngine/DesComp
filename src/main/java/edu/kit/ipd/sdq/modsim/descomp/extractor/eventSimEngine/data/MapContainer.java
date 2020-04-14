package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

import java.util.Collection;
import java.util.HashMap;

public class MapContainer implements IMapContainer {

    private HashMap<String, JavaClass> entityJavaClassHashMap;
    private HashMap<String, HashMap<String, Field>> fieldAttrHashMap;
    private HashMap<String, HashMap<String, HashMap<String, Collection<String>>>>  extractedEventsWithRelation;

    public MapContainer(HashMap<String, JavaClass> entityJavaClassHashMap, HashMap<String, HashMap<String, Field>> fieldAttrHashMap, HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> extractedEventsWithRelation) {
        this.entityJavaClassHashMap = entityJavaClassHashMap;
        this.fieldAttrHashMap = fieldAttrHashMap;
        this.extractedEventsWithRelation = extractedEventsWithRelation;
    }


    public HashMap<String, JavaClass> getEntityJavaClassHashMap() {
        return entityJavaClassHashMap;
    }

    public HashMap<String, HashMap<String, Field>> getFieldAttrHasMap() {
        return fieldAttrHashMap;
    }

    public HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> getExtractedEventsWithRelation() {
        return extractedEventsWithRelation;
    }
}
