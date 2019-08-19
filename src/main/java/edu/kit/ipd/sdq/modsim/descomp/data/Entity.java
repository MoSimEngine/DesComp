package edu.kit.ipd.sdq.modsim.descomp.data;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Entity {

//	@Id
//	@GeneratedValue
//	private Long id;
	@Id
	private String name;

	@Relationship(type = "HAS_ATTRIBUTE", direction = Relationship.DIRECTION)
	private Set<Attribute> attributes = new HashSet<Attribute>();

	public Entity(String name) {
		this.name = name;
	}

	public void addWriteAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

}
