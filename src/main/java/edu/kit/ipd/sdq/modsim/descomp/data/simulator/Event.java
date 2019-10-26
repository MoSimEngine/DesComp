package edu.kit.ipd.sdq.modsim.descomp.data.simulator;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Event extends Identifier {

	@Relationship(type = "ENTITY", direction = Relationship.OUTGOING)
	private Set<Entity> entities = new HashSet<>();

	@Relationship(type = "SCHEDULES", direction = Relationship.OUTGOING)
	private Set<Schedules> events = new HashSet<>();

	@Relationship(type = "READ_ATTRIBUTE", direction = Relationship.OUTGOING)
	private Set<Attribute> readAttribute = new HashSet<>();

	@Relationship(type = "WRITES", direction = Relationship.OUTGOING)
	private Set<WritesAttribute> writeAttribute = new HashSet<>();

	public Event(String name) {
		this.setName(name);
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
		getEntities().add(entity);
	}

	public void addReadAttribute(Attribute attribute) {
		getReadAttribute().add(attribute);
	}

	public Set<Entity> getEntities() {
		return entities;
	}

	public void setEntities(Set<Entity> entities) {
		this.entities = entities;
	}

	public Set<Schedules> getEvents() {
		return events;
	}

	public void setEvents(Set<Schedules> events) {
		this.events = events;
	}

	public Set<Attribute> getReadAttribute() {
		return readAttribute;
	}

	public void setReadAttribute(Set<Attribute> readAttribute) {
		this.readAttribute = readAttribute;
	}

	public Set<WritesAttribute> getWriteAttribute() {
		return writeAttribute;
	}

	public void addWriteAttribute(Attribute attribute, String condition, String writeFunction) {

		WritesAttribute newWritesAttribute = new WritesAttribute();
		newWritesAttribute.setAttribute(attribute);
		newWritesAttribute.setCondition(condition);
		newWritesAttribute.setStartEvent(this);
		newWritesAttribute.setWriteFunction(writeFunction);
		this.writeAttribute.add(newWritesAttribute);
	}

	public void setWriteAttribute(Set<WritesAttribute> writeAttribute) {
		this.writeAttribute = writeAttribute;
	}
}