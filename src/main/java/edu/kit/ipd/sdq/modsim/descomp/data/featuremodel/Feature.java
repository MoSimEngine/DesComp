package edu.kit.ipd.sdq.modsim.descomp.data.featuremodel;

import edu.kit.ipd.sdq.modsim.descomp.data.Identifier;
import org.neo4j.ogm.annotation.Property;

public class Feature extends Identifier {
    @Property
    private boolean isMandatory;

    public Feature(String name){
        this.name = name;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }
}
