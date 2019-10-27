package edu.kit.ipd.sdq.modsim.descomp.data.featuremodel;

import edu.kit.ipd.sdq.modsim.descomp.data.Identifier;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

public class Feature extends Identifier {
    @Property(name = "IS_MANDATORY")
    private boolean isMandatory;

    @Relationship(type = "FEATURE", direction = Relationship.UNDIRECTED)
    private Set<Feature> alternatives;

    @Relationship(type = "FEATURE", direction = Relationship.UNDIRECTED)
    private Set<Feature> replacements;

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public Set<Feature> getAlternative() {
        return alternatives;
    }

    public void setAlternative(Feature alternative) {
        this.alternatives.add(alternative);
    }

    public Set<Feature> getReplacements() {
        return replacements;
    }

    public void setReplacement(Feature replacement) {
        this.replacements.add(replacement);
    }
}
