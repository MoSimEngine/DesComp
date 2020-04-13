package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools;

import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.util.ClassVisitor;
import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.util.EnumerationUtil;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.springframework.stereotype.Service;

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
public class JavaClassesExtractor implements JavaClassExtraction {

    private Map<String, List<JavaClass>> extractedJavaClasses;


    public Map<String, List<JavaClass>> extractJavaClasses(Collection<File> jarCollection) {
        extractedJavaClasses = new HashMap<>();
        for (File file: jarCollection) {
            extractAllJavaClasses(file.getPath());
        }
        return extractedJavaClasses;
    }


    private void extractAllJavaClasses(String rootPath)  {
        try {
            Files.find(Paths.get(rootPath),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .forEach(this::getJavaClasses);
        }catch(Exception e){
        }
    }

    private void getJavaClasses(Path path){
        String sPath = path.toString();
        if (sPath.contains(".jar")) {
            try {
                JarFile jarFile = new JarFile(sPath);
                Stream<JarEntry> jarEntries = filterClassFilesOfJarFile(jarFile);
                fillAllJavaClasses(sPath, jarEntries, jarFile.getName());
            } catch (IOException e) {
                System.out.println(e.getClass());
                System.out.println(e.getMessage());
            }
        }
    }

    private Stream<JarEntry> filterClassFilesOfJarFile(JarFile jarFile) {
        Stream<JarEntry> entries = EnumerationUtil.enumerationAsStream(jarFile.entries());
        List<JarEntry> sanitizedJarEntries = new ArrayList<>();
        entries.forEach(jarEntry -> {
            if (jarEntry.getName().endsWith(".class"))
                sanitizedJarEntries.add(jarEntry);
        });
        return sanitizedJarEntries.stream();
    }

    private void fillAllJavaClasses(String sPath, Stream<JarEntry> jarEntries, String moduleName) {
        jarEntries.flatMap(jarEntry -> {
            ClassParser classParser = new ClassParser(sPath, jarEntry.getRealName());
            extractedJavaClasses.computeIfAbsent(moduleName, k -> new ArrayList<>());
            return ClassVisitor.getClassVisitor().apply(classParser).start().javaClass().stream();
        }).forEach(clazz -> {
            extractedJavaClasses.get(moduleName).add(clazz);
        });
    }
}
