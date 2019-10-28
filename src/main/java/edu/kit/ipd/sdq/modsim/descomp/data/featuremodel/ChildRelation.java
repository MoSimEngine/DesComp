package edu.kit.ipd.sdq.modsim.descomp.data.featuremodel;

import edu.kit.ipd.sdq.modsim.descomp.data.Identifier;
import org.neo4j.ogm.annotation.Relationship;

public abstract class ChildRelation extends Identifier {
    @Relationship(type = "FEATURE", direction = Relationship.OUTGOING)
    private Feature parent;

    public ChildRelation(String name, Feature parent){
        this.name = name;
        this.parent = parent;
    }

    public Feature getParent() {
        return parent;
    }

    public void setParent(Feature parent) {
        this.parent = parent;
    }
}
