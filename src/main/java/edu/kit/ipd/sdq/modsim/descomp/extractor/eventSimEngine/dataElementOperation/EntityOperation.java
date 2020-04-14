package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementOperation;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import org.apache.bcel.classfile.JavaClass;

import java.util.regex.Pattern;

public class EntityOperation {

    public static Entity createEntity(JavaClass javaClass){
        String[] arr = javaClass.getClassName().split(Pattern.quote("."));
        return new Entity(arr[arr.length-1]);
    }

    public static  String getJavaClassName(JavaClass javaClass){
        String[] arr = javaClass.getClassName().split(Pattern.quote("."));
        return arr[arr.length-1];
    }

}
