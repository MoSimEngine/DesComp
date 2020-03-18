package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.extractionTools;

import org.apache.bcel.classfile.JavaClass;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Service
public class ClassFilter  implements IClassFilter {

    String[] entityNames = {"EventSimEntity.java"};


    @Override
    public Collection<JavaClass> filterEntityClasses(Collection<JavaClass> javaClasses) {
        Collection<JavaClass> entityItem = new ArrayList<>();
        javaClasses.stream().forEach(s -> {
            if(Arrays.asList(entityNames).contains(s.getSourceFileName())){
                entityItem.add(s);
            }
        });

        Collection<JavaClass> newItems = entityItem;
        Collection<JavaClass> filteredItem = new ArrayList<>();

        do{
            newItems = lookChildClassesUp(newItems,javaClasses);
            filteredItem.addAll(newItems);
        }while(!newItems.isEmpty());

        filteredItem.stream().forEach(s -> System.out.println(s.getClassName() +" \n  -->" + s.getSuperclassName() +" \n \n"));

        return filteredItem;
    }

    private Collection<JavaClass> lookChildClassesUp(Collection<JavaClass> currentFoundClasses, Collection<JavaClass> allClasses){
        Collection<JavaClass> newClasses = new ArrayList<>();
        for (JavaClass jc: currentFoundClasses) {
           try {
            allClasses.stream().forEach(s -> {
                if (s.getSuperclassName().equals(jc.getClassName())){
                    newClasses.add(s);
                }
            });
           } catch (Exception e){

           }
        }
        return newClasses;
    }
}
