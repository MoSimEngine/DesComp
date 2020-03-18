
package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.util;

import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data.DataComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DependencyMapWrapper {
    private Map<Package, List<DataComponent>> dependencyMap;

    public DependencyMapWrapper(){
        this.dependencyMap = new HashMap<>();
    }

    public boolean containsKey(Package key){
        AtomicBoolean isContained = new AtomicBoolean(false);
        dependencyMap.forEach((k, v) -> {
            if(k.getName().equals(key.getName())){
                isContained.set(true);
            }
        });
        return isContained.get();
    }

    public void put(Package key, List<DataComponent> value){
        dependencyMap.put(key, value);
    }

    public boolean isEmpty(){
        return dependencyMap.isEmpty();
    }

    public List<DataComponent> get(Package key){
        return dependencyMap.get(key);
    }

    public Map<Package, List<DataComponent>> get(){
        return dependencyMap;
    }

    public void removeDuplicates(){
        Map<Package, List<DataComponent>> tmpDependencyMap = new HashMap<>();

        dependencyMap.forEach((key, value) -> {

        });
    }
}
