package edu.kit.ipd.sdq.modsim.descomp.data.simulator;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "WRITES")
public class WritesAttribute extends Identifier {

	@Property
	private String condition;

	@Property
	private String writeFunction;

	@StartNode
	private Event startEvent;

	@EndNode
	private Attribute attribute;

	public Long getRelationshipId() {
		return id;
	}

	public void setRelationshipId(Long relationshipId) {
		this.id = relationshipId;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Event getStartEvent() {
		return startEvent;
	}

	public void setStartEvent(Event startEvent) {
		this.startEvent = startEvent;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public String getWriteFunction() {
		return writeFunction;
	}

	public void setWriteFunction(String writeFunction) {
		this.writeFunction = writeFunction;
	}

}