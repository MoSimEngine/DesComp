package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.Collection;

public interface IClassFilter {
//    Collection<JavaClass> filterEntityClasses(Collection<JavaClass> javaClasses);
//    Collection<JavaClass> getEventClasses(Collection<JavaClass> javaClasses);
    Collection<JavaClass> extractClassesWithHierarchie(Collection<JavaClass> javaClasses, String[] classNames);
    Collection<Method> getMethodes(Collection<JavaClass> javaClasses);

}
