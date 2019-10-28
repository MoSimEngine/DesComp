package edu.kit.ipd.sdq.modsim.descomp.data.featuremodel;

import edu.kit.ipd.sdq.modsim.descomp.data.Identifier;
import org.neo4j.ogm.annotation.Relationship;

public abstract class Constraint extends Identifier {
    @Relationship(type = "SOURCE", direction = Relationship.OUTGOING)
    private Feature source;

    @Relationship(type = "TARGET", direction = Relationship.OUTGOING)
    private Feature target;

    public Constraint(String name){
        this.name = name;
    }

    public Feature getSource() {
        return source;
    }

    public void setSource(Feature source) {
        this.source = source;
    }

    public Feature getTarget() {
        return target;
    }

    public void setTarget(Feature target) {
        this.target = target;
    }
}
