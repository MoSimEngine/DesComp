package edu.kit.ipd.sdq.modsim.descomp.services;

import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

import edu.kit.ipd.sdq.modsim.descomp.data.Simulator;

public interface SimulatorRepository extends CrudRepository<Simulator, Long> {

	Simulator findByName(String name);

	@Query("MATCH (n) DETACH DELETE n")
	void cleanAll();

	@Query("MATCH (s1:Simulator)-[:EVENT]->(event1)\n" + "WITH s1, collect(id(event1)) AS s1Event\n"
			+ "MATCH (s2:Simulator)-[:EVENT]->(event2)\n" + "WITH s1, s1Event, s2, collect(id(event2)) AS s2Event\n"
			+ "RETURN s1.name AS from,\n" + "       s2.name AS to,\n"
			+ "       algo.similarity.jaccard(s1Event, s2Event) AS similarity")
	Iterable<Map<String, Object>> computeJaccardCoeffincyForEvents();

	@Query("MATCH (s1:Simulator)-[:ENTITY]->(event1)\n" + "WITH s1, collect(id(event1)) AS s1Event\n"
			+ "MATCH (s2:Simulator)-[:ENTITY]->(event2)\n" + "WITH s1, s1Event, s2, collect(id(event2)) AS s2Event\n"
			+ "RETURN s1.name AS from,\n" + "       s2.name AS to,\n"
			+ "       algo.similarity.jaccard(s1Event, s2Event) AS similarity")
	Iterable<Map<String, Object>> computeJaccardCoeffincyForEntitys();

	@Query("CALL algo.louvain.stream('Event', 'SCHEDULES', {includeIntermediateCommunities: true})\n"
			+ "YIELD nodeId, community\n" + "\n" + "RETURN algo.asNode(nodeId).name AS event, community\n"
			+ "ORDER BY community")
	Iterable<Map<String, Object>> computeLouvainCommunitiesForEvents();

	@Query("CALL algo.labelPropagation.stream('Event', 'Schedules',"
			+ "  {direction: 'OUTGOING', iterations: 10}) YIELD nodeId, label"
			+ " RETURN algo.asNode(nodeId).name AS event, label ORDER BY label")
	Iterable<Map<String, Object>> computeLabelPropagationForEvents();

	@Query("CALL algo.unionFind.stream('Event', 'Schedules', {})" + "YIELD nodeId,setId"
			+ " RETURN algo.asNode(nodeId).name AS event, setId")
	Iterable<Map<String, Object>> computeUnionFind();

}
