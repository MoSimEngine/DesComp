package edu.kit.ipd.sdq.modsim.descomp.data;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Property {

	@Id
	@GeneratedValue
	private Long id;
	@org.neo4j.ogm.annotation.Property
	private String name;

	public Property(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
