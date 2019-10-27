package edu.kit.ipd.sdq.modsim.descomp.data.simulator;

import java.util.HashSet;
import java.util.Set;

import edu.kit.ipd.sdq.modsim.descomp.data.Identifier;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

public class Simulator extends Identifier {

	@Property
	private String description;

	public Simulator(String name, String description) {
		super();
		this.setName(name);
		this.setDescription(description);
		setEvents(new HashSet<>());
		setEntities(new HashSet<>());
	}

	@Relationship(type = "EVENT", direction = Relationship.UNDIRECTED)
	private Set<Event> events;

	@Relationship(type = "ENTITY", direction = Relationship.UNDIRECTED)
	private Set<Entity> entities;

	public void addEvents(Event event) {
		this.getEvents().add(event);
	}

	public void addEntities(Entity entity) {
		this.getEntities().add(entity);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Event> getEvents() {
		return events;
	}

	public void setEvents(Set<Event> events) {
		this.events = events;
	}

	public Set<Entity> getEntities() {
		return entities;
	}

	public void setEntities(Set<Entity> entities) {
		this.entities = entities;
	}

}
