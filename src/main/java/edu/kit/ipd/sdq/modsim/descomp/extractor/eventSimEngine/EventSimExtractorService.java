package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools.JavaCLassExtraction;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.util.ClassVisitor;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.util.EnumerationUtil;
import org.apache.bcel.classfile.ClassParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.bcel.classfile.JavaClass;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

@Service
public class EventSimExtractorService implements EventExtractorService{


    @Autowired
    private JavaCLassExtraction classExtrator;

    private Map<String, List<JavaClass>> extractedJavaClasses;

    public EventSimExtractorService(){
        extractedJavaClasses = new HashMap<>();
    }

    @Override
    public Simulator extractEventSim(Collection<File> jarCollection) {
        extractedJavaClasses = classExtrator.extractJavaClasses(jarCollection);
        return null;
    }
}
