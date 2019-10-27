package edu.kit.ipd.sdq.modsim.descomp.data.simulator;

import java.util.HashSet;
import java.util.Set;

import edu.kit.ipd.sdq.modsim.descomp.data.Identifier;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Entity extends Identifier {

	@Relationship(type = "HAS_ATTRIBUTE", direction = Relationship.OUTGOING)
	private Set<Attribute> attributes = new HashSet<>();

	public Entity(String name) {
		this.setName(name);
	}

	public void addAttribute(Attribute attribute) {
		getAttributes().add(attribute);
	}

	public Set<Attribute> getAttributes() {
		return attributes;
	}

}
