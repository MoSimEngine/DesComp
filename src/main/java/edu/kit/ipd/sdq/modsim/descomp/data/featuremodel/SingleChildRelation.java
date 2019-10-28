package edu.kit.ipd.sdq.modsim.descomp.data.featuremodel;

import org.neo4j.ogm.annotation.Relationship;

public abstract class SingleChildRelation extends ChildRelation {
    @Relationship(type = "FEATURE", direction = Relationship.OUTGOING)
    private Feature child;

    public SingleChildRelation(String name, Feature parent, Feature child) {
        super(name, parent);
        this.child = child;
    }

    public Feature getChild() {
        return child;
    }

    public void setChild(Feature child) {
        this.child = child;
    }
}
