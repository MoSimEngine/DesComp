package edu.kit.ipd.sdq.modsim.descomp.data.simulator;

import edu.kit.ipd.sdq.modsim.descomp.data.Identifier;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * An attribute is part of a simulation entity.
 * e.g. throughput or latency
 */
@NodeEntity
public class Attribute extends Identifier {

	@Property
	private String type;

	public Attribute(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
