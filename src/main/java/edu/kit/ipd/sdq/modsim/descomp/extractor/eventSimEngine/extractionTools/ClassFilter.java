package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools;

import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator.EntityOperation;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

@Service
public class ClassFilter  implements IClassFilter {

    public static String[] classNames = {"EventSimEntity.java", "AbstractActiveResource.java"};
    public static String[] eventMethodeNames= {"calculateConcreteDemand", "<init>"};


    public Collection<JavaClass> extractClassesWithHierarchie(Collection<JavaClass> javaClasses, String[] classNames){
        Collection<JavaClass> rootElementList = new ArrayList<>();
        javaClasses.stream().forEach(s -> {
            if(Arrays.asList(classNames).contains(s.getSourceFileName())){
                if(!s.getClassName().contains("$")) {
                    rootElementList.add(s);
                }
            }
        });
        Collection<JavaClass> returnedList = getHierarchie(rootElementList, javaClasses);
        return returnedList;
    }



    public HashMap<String, Method> getMethodes (Collection<JavaClass> javaClasses, String[] methodeNames){

        HashMap<String, Method> methodCollection = new HashMap<>();
        for (JavaClass jc :javaClasses) {
            for (Method currentMethod:jc.getMethods()) {
                if(Arrays.asList(methodeNames).contains(currentMethod.getName())){
                    methodCollection.put(EntityOperation.getJavaClassName(jc)+ "_" +currentMethod.getName(), currentMethod);
                }
            }
        }
        return methodCollection;
    }



    private Collection<JavaClass> lookChildClassesUp(Collection<JavaClass> currentFoundClasses, Collection<JavaClass> allClasses){
        Collection<JavaClass> newClasses = new ArrayList<>();
        for (JavaClass jc: currentFoundClasses) {
           try {
            allClasses.stream().forEach(s -> {
                if (s.getSuperclassName().equals(jc.getClassName())){
                    newClasses.add(s.copy());
                } else if(s.getInterfaceNames().length >0){
                    for (String interfaceName:s.getInterfaceNames()) {
                        if(interfaceName.equals(jc.getClassName())){
                            newClasses.add(s);
                        }
                    }
                }
            });
           } catch (Exception e){
               System.out.println(e.getCause());
           }
        }
        return newClasses;
    }



    private Collection<JavaClass> getHierarchie(Collection<JavaClass> rootList, Collection<JavaClass> javaClasses){
        Collection<JavaClass> newItems = rootList;
        Collection<JavaClass> filteredItem = new ArrayList<>();
        filteredItem.addAll(newItems);
        do{
            newItems = lookChildClassesUp(newItems,javaClasses);
            filteredItem.addAll(newItems);
        }while(!newItems.isEmpty());

        Collection<JavaClass> returnList = new ArrayList<>();
        for (JavaClass jc:filteredItem) {
            if(!jc.isInterface()&& !jc.isAbstract()){
                returnList.add(jc);
            }
        }
        return returnList;
    }

}
