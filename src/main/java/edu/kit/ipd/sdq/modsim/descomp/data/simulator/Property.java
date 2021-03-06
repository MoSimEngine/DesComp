package edu.kit.ipd.sdq.modsim.descomp.data.simulator;

import edu.kit.ipd.sdq.modsim.descomp.data.Identifier;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Property extends Identifier {

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
