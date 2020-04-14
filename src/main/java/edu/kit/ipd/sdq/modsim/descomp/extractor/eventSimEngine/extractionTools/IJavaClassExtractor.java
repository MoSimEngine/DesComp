package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools;

import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IJavaClassExtractor {
    Map<String, List<JavaClass>> extractJavaClasses(Collection<File> jarCollection);
}
