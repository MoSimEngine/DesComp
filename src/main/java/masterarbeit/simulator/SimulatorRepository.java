package masterarbeit.simulator;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface SimulatorRepository extends CrudRepository<Simulator, Long> {

	Simulator findByName(String name);

	@Query("MATCH (n) DETACH DELETE n")
	void cleanAll();

	@Query("MATCH (s1:Simulator)-[:EVENT]->(event1)\n" + "WITH s1, collect(id(event1)) AS s1Event\n"
			+ "MATCH (s2:Simulator)-[:EVENT]->(event2)\n" + "WITH s1, s1Event, s2, collect(id(event2)) AS s2Event\n"
			+ "RETURN s1.name AS from,\n" + "       s2.name AS to,\n"
			+ "       algo.similarity.jaccard(s1Event, s2Event) AS similarity")
	void computeJaccardCoeffincyForEvents();

	@Query("MATCH (s1:Simulator)-[:ENTITY]->(event1)\n" + "WITH s1, collect(id(event1)) AS s1Event\n"
			+ "MATCH (s2:Simulator)-[:ENTITY]->(event2)\n" + "WITH s1, s1Event, s2, collect(id(event2)) AS s2Event\n"
			+ "RETURN s1.name AS from,\n" + "       s2.name AS to,\n"
			+ "       algo.similarity.jaccard(s1Event, s2Event) AS similarity")
	void computeJaccardCoeffincyForEntitys();

}
