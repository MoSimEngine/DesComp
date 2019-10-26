package edu.kit.ipd.sdq.modsim.descomp.data.simulator;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;

public abstract class Identifier {
    @Id
    @GeneratedValue
    protected Long id;

    @Property
    protected String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
