package masterarbeit.simulator;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Entity {

//	@Id
//	@GeneratedValue
	private Long id;
	@Id
	private String name;

	public Entity(String name) {
		this.name = name;
	}

}
