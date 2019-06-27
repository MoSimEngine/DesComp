package masterarbeit.simulator;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Relationship;

public class Simulator {

	@Id
	@GeneratedValue
	private Long id;

	private String name;
	private String beschreibung;

	public Simulator(String name, String beschreibung) {
		super();
		this.name = name;
		this.beschreibung = beschreibung;
		events = new HashSet<Event>();
		entitys = new HashSet<Entity>();
	}

	@Relationship(type = "EVENT", direction = Relationship.UNDIRECTED)
	private Set<Event> events;

	@Relationship(type = "ENTITY", direction = Relationship.UNDIRECTED)
	private Set<Entity> entitys;

	public void addEvents(Event event) {
		this.events.add(event);
	}

	public void addEntitys(Entity entity) {
		this.entitys.add(entity);
	}

}
