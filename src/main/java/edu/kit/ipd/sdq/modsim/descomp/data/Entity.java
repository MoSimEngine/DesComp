package edu.kit.ipd.sdq.modsim.descomp.data;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Entity {

	@Id
	@GeneratedValue
	private Long id;
	@Property
	private String name;

	@Relationship(type = "HAS_ATTRIBUTE", direction = Relationship.OUTGOING)
	private Set<Attribute> attributes = new HashSet<Attribute>();

	public Entity(String name) {
		this.setName(name);
	}

	public void addAttribute(Attribute attribute) {
		getAttributes().add(attribute);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Attribute> getAttributes() {
		return attributes;
	}

}
