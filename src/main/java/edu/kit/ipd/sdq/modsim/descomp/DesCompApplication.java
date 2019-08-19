package edu.kit.ipd.sdq.modsim.descomp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories
public class DesCompApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesCompApplication.class, args);
	}

}
