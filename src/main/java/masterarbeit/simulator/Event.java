package masterarbeit.simulator;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Event {

//	@Id
//	@GeneratedValue
	private Long id;

	@Relationship(type = "ENTITY", direction = Relationship.UNDIRECTED)
	private Set<Entity> entitys = new HashSet<Entity>();

	@Relationship(type = "SCHEDULES", direction = Relationship.DIRECTION)
	private Set<Event> events = new HashSet<Event>();

	@Id
	public String name;

	public Event(String name, String eventRoutine) {
		this.name = name;
		this.eventRoutine = eventRoutine;
	}

	public String eventRoutine;

	public void addSchedulesEvent(Event event) {
		events.add(event);
	}

	public void addEntity(Entity entity) {
		entitys.add(entity);
	}
}