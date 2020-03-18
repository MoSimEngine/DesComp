package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.xml.crypto.Data;
import java.util.HashSet;
import java.util.Set;

public class DataComponent extends Identifier {
    @Relationship(type = "CONTAINS", direction = Relationship.OUTGOING)
    private Set<DataClass> classes;

    public DataComponent(String name){
        this.name = name;
        this.classes = new HashSet<>();
    }

    public Set<DataClass> getClasses() {
        return classes;
    }

    public void setClasses(Set<DataClass> classes) {
        this.classes = classes;
    }

    public void addClass(DataClass mc){
        this.classes.add(mc);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.id.equals(((DataComponent) o).getId());
    }

    @Override
    public int hashCode(){
        return this.id.intValue();
    }
}