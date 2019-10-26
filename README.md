# DesComp

DesComp is a tool to describe Simulators and to Compare them.

## Getting Started

The easiest way to run DesComp is using the Docker. Checkout the repository and run the following commands.

docker build -t gruenj/descomp .
docker run -it gruenj/descomp

### Prerequisites

For the execution via Docker, Docker must be installed.

For the execution without Docker, Gradle and Z3 must be installed.

A Neo4j-Database.

### Installing

## Running the examples

In order to run the examples a Database have to exist.

### Describe Simulators


### Compare Simulators

Clean Database

import EventSim
import Workload

run compare


### Graph-Communities



## Built With

* [Spring Boot CLI](https://docs.spring.io/spring-boot/docs/current/reference/html/cli-using-the-cli.html) - Java Framework for CLI and DI
* [Z3](https://github.com/Z3Prover/z3) - SMT-Solver
* [Gradle](https://gradle.org/) - Used to build the Project
* [Docker] (https://docs.docker.com/install/) - Docker 


## Authors

* **Johannes Grün** - *Initial work* - [gruenj](https://github.com/gruenj)
* **Sandro Koch** - **

