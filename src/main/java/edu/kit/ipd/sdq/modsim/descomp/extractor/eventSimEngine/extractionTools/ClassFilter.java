package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools;

import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementOperation.EntityOperation;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

@Service
public class ClassFilter  implements IClassFilter {

    private String[] classFilterNames = {"EventSimEntity.java", "AbstractActiveResource.java", "StartSimulationJob.java"};
    private String[] eventFilterMethodNames = {"calculateConcreteDemand","<init>", "execute","loadInstrumentationDesciptionFromXML"};

    public String[] getClassFilterNames() {
        return classFilterNames;
    }

    public void setClassFilterNames(String[] classFilterNames) {
        this.classFilterNames = classFilterNames;
    }

    public String[] getEventFilterMethodNames() {
        return eventFilterMethodNames;
    }

    public void setEventFilterMethodNames(String[] eventFilterMethodNames) {
        this.eventFilterMethodNames = eventFilterMethodNames;
    }

    /**
     * filters collection of java classes for those, named in the passed string. includes those standing in inheritance relationship
     *
     * @param javaClasses Collection of all java classes occurring in the jar files
     * @param classNames list of names of the considered java classes
     * @param allowAbstract defines if either abstract classes are of interest when inspecting class inheritance or not
     * @return returns collection of bcel.JavaClass objects representing the corresponding java classes
     */
    public Collection<JavaClass> extractClassesWithHierarchy(Collection<JavaClass> javaClasses, String[] classNames, boolean allowAbstract){
        Collection<JavaClass> rootElementList = new ArrayList<>();
        javaClasses.stream().forEach(s -> {
            if(Arrays.asList(classNames).contains(s.getSourceFileName())){
                if(!s.getClassName().contains("$")) {
                    rootElementList.add(s);
                }
            }
        });
        return getHierarchy(rootElementList, javaClasses, allowAbstract);
    }

    /**
     * extract methods of interest from the passed bcel.JavaClass objects
     *
     * @param javaClasses Collection of java classes being of interest
     * @param abstractClasses collection of abstract java classes being of interest
     * @param methodNames name of the considered methods
     * @return HashMap with mapped method name and bcel.method object of the considered methods
     */
    public HashMap<String, Method> getMethods(Collection<JavaClass> javaClasses, Collection<JavaClass> abstractClasses, String[] methodNames){
        HashMap<String, Method> methodCollection = new HashMap<>();
        for (JavaClass jc :javaClasses) {
            for (Method currentMethod:jc.getMethods()) {
                if(Arrays.asList(methodNames).contains(currentMethod.getName())){
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
                            for (String requestedMethode: methodNames) {
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

    private Collection<JavaClass> getHierarchy(Collection<JavaClass> rootList, Collection<JavaClass> javaClasses, boolean allowAbstract){
        Collection<JavaClass> newItems = rootList;
        Collection<JavaClass> filteredItem = new ArrayList<>(newItems);
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
