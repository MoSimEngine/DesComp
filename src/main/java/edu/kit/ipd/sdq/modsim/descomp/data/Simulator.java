package edu.kit.ipd.sdq.modsim.descomp.data;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

public class Simulator {

	@Id
	@GeneratedValue
	private Long id;

	@Property
	private String name;

	@Property
	private String beschreibung;

	public Simulator(String name, String beschreibung) {
		super();
		this.setName(name);
		this.setBeschreibung(beschreibung);
		setEvents(new HashSet<Event>());
		setEntitys(new HashSet<Entity>());
	}

	@Relationship(type = "EVENT", direction = Relationship.UNDIRECTED)
	private Set<Event> events;

	@Relationship(type = "ENTITY", direction = Relationship.UNDIRECTED)
	private Set<Entity> entitys;

	public void addEvents(Event event) {
		this.getEvents().add(event);
	}

	public void addEntitys(Entity entity) {
		this.getEntitys().add(entity);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
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

	public Set<Entity> getEntitys() {
		return entitys;
	}

	public void setEntitys(Set<Entity> entitys) {
		this.entitys = entitys;
	}

}
