package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import org.apache.bcel.classfile.JavaClass;

import java.util.regex.Pattern;

public class EntityCreator {

    public static Entity createEntity(JavaClass javaClass){
        String[] arr = javaClass.getClassName().split(Pattern.quote("."));
        return new Entity(arr[arr.length-1]);
    }

}
