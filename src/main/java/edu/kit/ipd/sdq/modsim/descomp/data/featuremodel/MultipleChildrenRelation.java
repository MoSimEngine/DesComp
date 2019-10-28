package edu.kit.ipd.sdq.modsim.descomp.data.featuremodel;

import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

public abstract class MultipleChildrenRelation extends ChildRelation {
    @Relationship(type = "FEATURE", direction = Relationship.OUTGOING)
    private List<Feature> children;

    public MultipleChildrenRelation(String name, Feature parent) {
        super(name, parent);
        this.children = new ArrayList<>();
    }

    public List<Feature> getChildren() {
        return children;
    }

    public void setChildren(List<Feature> children) {
        this.children = children;
    }

    public void addChild(Feature child){
        this.children.add(child);
    }
}
