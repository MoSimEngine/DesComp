package edu.kit.ipd.sdq.modsim.descomp.data;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "SCHEDULES")
public class Schedules {

	@Id
	@GeneratedValue
	private Long relationshipId;

	@Property
	private String condition;

	@Property
	private String delay;

	@StartNode
	private Event startEvent;

	@EndNode
	private Event endEvent;

	public Long getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(Long relationshipId) {
		this.relationshipId = relationshipId;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public Event getStartEvent() {
		return startEvent;
	}

	public void setStartEvent(Event startEvent) {
		this.startEvent = startEvent;
	}

	public Event getEndEvent() {
		return endEvent;
	}

	public void setEndEvent(Event endEvent) {
		this.endEvent = endEvent;
	}

}