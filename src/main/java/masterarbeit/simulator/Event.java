package masterarbeit.simulator;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Event {

	@Id
	public String name;

	@Relationship(type = "ENTITY", direction = Relationship.UNDIRECTED)
	private Set<Entity> entitys = new HashSet<Entity>();

	@Relationship(type = "SCHEDULES", direction = Relationship.DIRECTION)
	private Set<Event> events = new HashSet<Event>();

	@Relationship(type = "READ_PROPERTIES", direction = Relationship.DIRECTION)
	private Set<Property> readProperties = new HashSet<Property>();

	@Relationship(type = "WRITE_PROPERTIES", direction = Relationship.DIRECTION)
	private Set<Property> writeProperties = new HashSet<Property>();

	@Relationship(type = "READ_ATTRIBUTE", direction = Relationship.DIRECTION)
	private Set<Attribute> readAttribute = new HashSet<Attribute>();

	@Relationship(type = "WRITE_ATTRIBUTE", direction = Relationship.DIRECTION)
	private Set<Attribute> writeAttribute = new HashSet<Attribute>();

	private String eventRoutine;

	public Event(String name, String eventRoutine) {
		this.name = name;
		this.setEventRoutine(eventRoutine);
	}

	public void addSchedulesEvent(Event event) {
		events.add(event);
	}

	public void addEntity(Entity entity) {
		entitys.add(entity);
	}

	public void addReadProperty(Property property) {
		readProperties.add(property);
	}

	public void addWriteProperty(Property property) {
		writeProperties.add(property);
	}

	public String getEventRoutine() {
		return eventRoutine;
	}

	public void setEventRoutine(String eventRoutine) {
		this.eventRoutine = eventRoutine;
	}

	public void addReadAttribute(Attribute attribute) {
		readAttribute.add(attribute);

	}

	public void addWriteAttribute(Attribute attribute) {
		writeAttribute.add(attribute);
	}
}