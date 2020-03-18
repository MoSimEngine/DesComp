package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

public class DataClass extends Identifier {

    public DataClass(String name){
        this.name = name;
        this.compositionDependencyUsedBy = new HashSet<>();
    }

    @Relationship(type = "PARENT", direction = Relationship.INCOMING)
    private DataClass parent;

    @Relationship(type = "CHILD", direction = Relationship.OUTGOING)
    private DataClass child;

    @Relationship(type = "USED_BY", direction = Relationship.INCOMING)
    private Set<DataClass> compositionDependencyUsedBy;

    private String className;

    public DataClass getParent() {
        return parent;
    }

    public void setParent(DataClass parent) {
        this.parent = parent;
    }

    public boolean hasParent(){
        return this.parent != null;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public DataClass getChild() {
        return child;
    }

    public void setChild(DataClass child) {
        this.child = child;
    }

    public Set<DataClass> getCompositionDependencyUsedBy() {
        return compositionDependencyUsedBy;
    }

    public void setCompositionDependencyUsedBy(DataClass compositionDependencyUsedBy) {
        this.compositionDependencyUsedBy.add(compositionDependencyUsedBy);
    }
}