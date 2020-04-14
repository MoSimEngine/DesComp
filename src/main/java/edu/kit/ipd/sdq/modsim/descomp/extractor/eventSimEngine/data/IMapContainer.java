package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

import java.util.Collection;
import java.util.HashMap;

public interface IMapContainer {
    HashMap<String, JavaClass> getEntityJavaClassHashMap();
    HashMap<String, HashMap<String, Field>> getFieldAttrHasMap();
    HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> getExtractedEventsWithRelation();
}
