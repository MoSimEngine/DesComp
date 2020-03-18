package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools;

import org.apache.bcel.classfile.JavaClass;

import java.util.Collection;

public interface IClassFilter {

    Collection<JavaClass> filterEntityClasses(Collection<JavaClass> javaClasses);
}
