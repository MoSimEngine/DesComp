package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools;

import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator.EntityOperation;
import fj.data.Stream;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

@Service
public class ClassFilter  implements IClassFilter {

    public static String[] classNames = {"EventSimEntity.java", "AbstractActiveResource.java", "StartSimulationJob.java"};
    public static String[] eventMethodeNames= {"calculateConcreteDemand", "execute","loadInstrumentationDesciptionFromXML", "consumeResource"};


    public Collection<JavaClass> extractClassesWithHierarchie(Collection<JavaClass> javaClasses, String[] classNames){
        Collection<JavaClass> rootElementList = new ArrayList<>();
        javaClasses.stream().forEach(s -> {
            if(Arrays.asList(classNames).contains(s.getSourceFileName())){
                if(!s.getClassName().contains("$")) {
                    rootElementList.add(s);
                }
            }
        });
        Collection<JavaClass> returnedList = getHierarchie(rootElementList, javaClasses, false);
        return returnedList;
    }

    public Collection<JavaClass> extractClassesAbstractParents(Collection<JavaClass> javaClasses, String[] classNames) {
        Collection<JavaClass> rootElementList = new ArrayList<>();
        javaClasses.stream().forEach(s -> {
            if (Arrays.asList(classNames).contains(s.getSourceFileName())) {
                if (!s.getClassName().contains("$")) {
                    rootElementList.add(s);
                }
            }
        });
        Collection<JavaClass> returnedList = getHierarchie(rootElementList, javaClasses, true);
        return returnedList;
    }

    public HashMap<String, Method> getMethodes (Collection<JavaClass> javaClasses, Collection<JavaClass> abstractClasses, String[] methodeNames){

        HashMap<String, Method> methodCollection = new HashMap<>();
        for (JavaClass jc :javaClasses) {

            for (Method currentMethod:jc.getMethods()) {
                if(Arrays.asList(methodeNames).contains(currentMethod.getName())){
                    methodCollection.put(EntityOperation.getJavaClassName(jc)+ "_" +currentMethod.getName(), currentMethod);
                }
            }

            JavaClass parentJavaClass = jc;
            while(parentJavaClass.isSuper()){
                boolean foundSuperClass = false;
                //super klasse aus
                for (JavaClass possibleParentClass :abstractClasses) {
                    if(possibleParentClass.getClassName().equals(parentJavaClass.getSuperclassName())){
                        parentJavaClass = possibleParentClass;
                        foundSuperClass=true;
                        break;
                    }
                }
                if(foundSuperClass) {
                    for (Method parentMethode : parentJavaClass.getMethods()) {
                        boolean newMethode = true;
                        for (Method jcMethode : jc.getMethods()) {
                            if (jcMethode.getName().equals(parentMethode.getName())) {
                                newMethode = false;
                                break;
                            }
                        }
                        if (newMethode && !methodCollection.containsKey(EntityOperation.getJavaClassName(jc) + "_" + parentMethode.getName())) {
                            for (String requestedMethode:methodeNames) {
                                if(requestedMethode.equals(parentMethode.getName())) {
                                    methodCollection.put(EntityOperation.getJavaClassName(jc) + "_" + parentMethode.getName(), parentMethode);
                                    break;
                                }
                            }

                        }
                    }
                }else {
                    break;
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



    private Collection<JavaClass> getHierarchie(Collection<JavaClass> rootList, Collection<JavaClass> javaClasses, boolean allowAbstract){
        Collection<JavaClass> newItems = rootList;
        Collection<JavaClass> filteredItem = new ArrayList<>();
        filteredItem.addAll(newItems);
        do{
            newItems = lookChildClassesUp(newItems,javaClasses);
            filteredItem.addAll(newItems);
        }while(!newItems.isEmpty());

        Collection<JavaClass> returnList = new ArrayList<>();
        for (JavaClass jc:filteredItem) {
            if(!jc.isInterface()&& (!jc.isAbstract()) ||(allowAbstract) ){
                returnList.add(jc);
            }
        }
        return returnList;
    }

}
