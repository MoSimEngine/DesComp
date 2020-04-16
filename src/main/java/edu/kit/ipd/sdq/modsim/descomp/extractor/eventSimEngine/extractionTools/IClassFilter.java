package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.Collection;
import java.util.HashMap;

public interface IClassFilter {
    Collection<JavaClass> extractClassesWithHierarchy(Collection<JavaClass> javaClasses, String[] classNames, boolean allowAbstract);
    HashMap<String, Method> getMethods(Collection<JavaClass> javaClasses, Collection<JavaClass> abstractClasses, String[] methodNames);

    String[] getClassFilterNames();
    String[] getEventFilterMethodNames();
    void setClassFilterNames(String[] classFilterNames);
    void setEventFilterMethodNames(String[] eventFilterMethodNames);
}
