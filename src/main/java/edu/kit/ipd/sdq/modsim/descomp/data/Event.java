package edu.kit.ipd.sdq.modsim.descomp.data;

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

	// @Relationship(type = "SCHEDULES", direction = Relationship.DIRECTION)
	private Set<Schedules> events = new HashSet<Schedules>();

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

	public void addSchedulesEvent(Event event, String condition, String delay) {
		Schedules schedules = new Schedules();
		schedules.setCondition(condition);
		schedules.setDelay(delay);
		schedules.setStartEvent(this);
		schedules.setEndEvent(event);

		getEvents().add(schedules);
	}

	public void addEntity(Entity entity) {
		getEntitys().add(entity);
	}

	public void addReadProperty(Property property) {
		getReadProperties().add(property);
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
		getReadAttribute().add(attribute);

	}

	public void addWriteAttribute(Attribute attribute) {
		getWriteAttribute().add(attribute);
	}

	public Set<Entity> getEntitys() {
		return entitys;
	}

	public void setEntitys(Set<Entity> entitys) {
		this.entitys = entitys;
	}

	public Set<Schedules> getEvents() {
		return events;
	}

	public void setEvents(Set<Schedules> events) {
		this.events = events;
	}

	public Set<Property> getReadProperties() {
		return readProperties;
	}

	public void setReadProperties(Set<Property> readProperties) {
		this.readProperties = readProperties;
	}

	public Set<Attribute> getReadAttribute() {
		return readAttribute;
	}

	public void setReadAttribute(Set<Attribute> readAttribute) {
		this.readAttribute = readAttribute;
	}

	public Set<Attribute> getWriteAttribute() {
		return writeAttribute;
	}

	public void setWriteAttribute(Set<Attribute> writeAttribute) {
		this.writeAttribute = writeAttribute;
	}
}