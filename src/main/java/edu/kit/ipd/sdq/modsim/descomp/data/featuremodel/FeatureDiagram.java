package edu.kit.ipd.sdq.modsim.descomp.data.featuremodel;

import edu.kit.ipd.sdq.modsim.descomp.data.Identifier;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

public class FeatureDiagram extends Identifier {
    @Relationship(type = "FEATURE", direction = Relationship.OUTGOING)
    private List<Feature> features;

    @Relationship(type = "CHILD_RELATION", direction = Relationship.OUTGOING)
    private List<ChildRelation> childRelations;

    @Relationship(type = "ROOT_FEATURE", direction = Relationship.OUTGOING)
    private Feature rootFeature;

    @Relationship(type = "CONSTRAINTS", direction = Relationship.OUTGOING)
    private List<Constraint> constraints;

    public FeatureDiagram(String name, Feature rootFeature){
        this.name = name;
        this.features = new ArrayList<>();
        this.childRelations = new ArrayList<>();
        this.rootFeature = rootFeature;
        this.constraints = new ArrayList<>();
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public void addFeature(Feature f){
        this.features.add(f);
    }

    public List<ChildRelation> getChildRelations() {
        return childRelations;
    }

    public void setChildRelations(List<ChildRelation> childRelations) {
        this.childRelations = childRelations;
    }

    public void addChildRelation(ChildRelation cr){
        this.childRelations.add(cr);
    }

    public Feature getRootFeature() {
        return rootFeature;
    }

    public void setRootFeature(Feature rootFeature) {
        this.rootFeature = rootFeature;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public void addConstraint(Constraint c) {
        this.constraints.add(c);
    }
}
