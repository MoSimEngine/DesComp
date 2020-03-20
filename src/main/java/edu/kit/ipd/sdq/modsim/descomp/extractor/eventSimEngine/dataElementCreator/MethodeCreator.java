package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import org.apache.bcel.classfile.*;

import java.util.Arrays;
import java.util.Collection;

public class MethodeCreator {

    public static Collection<Event> extractEventsFromMethods(Collection<Method> methodCollection){
        for (Method m:methodCollection) {
            Code code = m.getCode();
            System.out.println(code.toString());
            System.out.println("----");
        }
        return null;
    }

}
