package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import org.apache.bcel.classfile.Field;

import java.util.Collection;
import java.util.regex.Pattern;

public class AttributeCreator {

    public static Attribute createAttributes(Field field){
        String[] typeStringArray = field.getType().toString().split(Pattern.quote("."));
        return new Attribute(field.getName(), typeStringArray[typeStringArray.length-1]);
    }


}
