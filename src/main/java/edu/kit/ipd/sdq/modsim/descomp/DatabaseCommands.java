package edu.kit.ipd.sdq.modsim.descomp;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import edu.kit.ipd.sdq.modsim.descomp.data.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.Event;
import edu.kit.ipd.sdq.modsim.descomp.data.Schedules;
import edu.kit.ipd.sdq.modsim.descomp.data.Simulator;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorRepository;

@ShellComponent
@ShellCommandGroup("database")
public class DatabaseCommands {

	private Long currentSimulatorId;

	@Autowired
	private SimulatorRepository repository;

	@ShellMethod("Clear Database")
	public void cleanAllDatabase() {
		repository.cleanAll();
	}

	@ShellMethod("Set current Simulator")
	public String setCurrentSimulator(@ShellOption(valueProvider = SimulatorValueProvider.class) String simulator) {

		Simulator currSimulator = repository.findByName(simulator);

		StringBuffer sb = new StringBuffer();

		if (null == currSimulator) {
			sb.append("Simulator " + simulator + " not found.");
		} else {
			sb.append("Current Simulator is set to: " + currSimulator.getName());
			currentSimulatorId = currSimulator.getId();
		}

		return sb.toString();
	}

	@ShellMethod("Get current Simulator")
	public String getCurrentSimulator() {

		Optional<Simulator> currSimulator = repository.findById(currentSimulatorId, 3);

		StringBuffer sb = new StringBuffer();

		if (!currSimulator.isPresent()) {
			sb.append("Current set Simulator does not exists.");
		} else {
			sb.append("Current Simulator is set to: " + currSimulator.get().getName());
		}

		return sb.toString();
	}

	@ShellMethod("Compute Event communities")
	public String computeEventCommunities() {

		StringBuffer bf = new StringBuffer();
		bf.append("Compute Louvain Communities for Events " + System.lineSeparator());
		bf.append("Found the following communities: " + System.lineSeparator());

		for (Map<String, Object> map : repository.computeLouvainCommunitiesForEvents()) {
			bf.append("\t In community " + map.get("community") + " is Event " + map.get("event")
					+ System.lineSeparator());
		}

		bf.append("Compute Label Propagation for Events " + System.lineSeparator());
		bf.append("Found the following communities: " + System.lineSeparator());

		for (Map<String, Object> map : repository.computeLabelPropagationForEvents()) {
			bf.append("\t In community " + map.get("label") + " is Event " + map.get("event") + System.lineSeparator());
		}

		bf.append("Compute Union Find for Events " + System.lineSeparator());
		bf.append("Found the following communities: " + System.lineSeparator());

		for (Map<String, Object> map : repository.computeUnionFind()) {
			bf.append("\t In community " + map.get("setId") + " is Event " + map.get("event") + System.lineSeparator());
		}

		return bf.toString();
	}

	@ShellMethod("Add Entity")
	@ShellMethodAvailability("currenSimulatorAvailabilityCheck")
	public String addEntity(String name) {

		Simulator simu = repository.findById(currentSimulatorId, 3).get();

		long count = simu.getEntitys().stream().filter(e -> !e.getName().contentEquals(name)).count();

		if (0 > count) {
			return "Entity: " + name + " already exisits in " + simu.getName() + "!";
		}

		simu.addEntitys(new Entity(name));
		repository.save(simu);
		return "Added Entity: " + name + " to " + simu.getName();
	}

	@ShellMethod("Add Attribute to Entity")
	@ShellMethodAvailability("currenSimulatorAvailabilityCheck")
	public String addAttributesToEntity(String entityName,
			@ShellOption(valueProvider = DataTypeValueProvider.class) String type, String attributeName) {

		Simulator simu = repository.findById(currentSimulatorId, 3).get();

		Optional<Entity> opEntity = simu.getEntitys().stream().filter(e -> e.getName().contentEquals(entityName))
				.findFirst();

		StringBuffer bf = new StringBuffer();

		if (opEntity.isEmpty()) {
			bf.append(
					"Entity: " + entityName + " does not exisits in " + simu.getName() + "!" + System.lineSeparator());
		} else {
			Entity entity = opEntity.get();
			entity.addAttribute(new Attribute(attributeName, type));
			repository.save(simu);

			bf.append("Entity " + entityName + " changed. Attribute " + attributeName + " added."
					+ System.lineSeparator());
		}

		return bf.toString();
	}

	@ShellMethod("Add Read Attribute to Event")
	@ShellMethodAvailability("currenSimulatorAvailabilityCheck")
	public String addReadAttributeToEvent(String eventName, String entityName, String attributeName) {

		Simulator simu = repository.findById(currentSimulatorId, 3).get();
		StringBuffer bf = new StringBuffer();

		Optional<Event> opEvent = simu.getEvents().stream().filter(e -> e.getName().contentEquals(eventName))
				.findFirst();

		Optional<Entity> opEntity = simu.getEntitys().stream().filter(e -> e.getName().contentEquals(entityName))
				.findFirst();

		if (opEntity.isPresent() && opEvent.isPresent()) {

			Optional<Attribute> attribute = opEntity.get().getAttributes().stream()
					.filter(a -> a.getName().contentEquals(attributeName)).findFirst();

			if (attribute.isPresent()) {
				Event event = opEvent.get();
				event.addReadAttribute(attribute.get());
				repository.save(simu);

				bf.append("Added Attribute " + attributeName + " from " + entityName + " as releation to "
						+ event.getName() + "." + System.lineSeparator());
			} else {
				bf.append("Attribute " + attributeName + " in " + entityName + " not found." + System.lineSeparator());
			}

		} else {
			bf.append("Entity " + entityName + " or Event " + eventName + " does not exisits in " + simu.getName() + "!"
					+ System.lineSeparator());
		}

		return bf.toString();
	}

	@ShellMethod("Add Write Attribute to Event")
	@ShellMethodAvailability("currenSimulatorAvailabilityCheck")
	public String addWriteAttributeToEvent(String eventName, String entityName, String attributeName,
			String writeFunction, String condition) {

		Simulator simu = repository.findById(currentSimulatorId, 3).get();
		StringBuffer bf = new StringBuffer();

		Optional<Event> opEvent = simu.getEvents().stream().filter(e -> e.getName().contentEquals(eventName))
				.findFirst();

		Optional<Entity> opEntity = simu.getEntitys().stream().filter(e -> e.getName().contentEquals(entityName))
				.findFirst();

		if (opEntity.isPresent() && opEvent.isPresent()) {

			Optional<Attribute> attribute = opEntity.get().getAttributes().stream()
					.filter(a -> a.getName().contentEquals(attributeName)).findFirst();

			if (attribute.isPresent()) {
				Event event = opEvent.get();

				event.addWriteAttribute(attribute.get(), condition, writeFunction);

				repository.save(simu);

				bf.append("Added Attribute " + attributeName + " from " + entityName + "as releation to "
						+ event.getName() + ".");
			}

		} else {
			bf.append("Entity " + entityName + " or Event " + eventName + " does not exisits in " + simu.getName() + "!"
					+ System.lineSeparator());
		}

		return bf.toString();
	}

	@ShellMethod("Remove Read Attribute from Event")
	@ShellMethodAvailability("currenSimulatorAvailabilityCheck")
	public String removeReadAttributeFromEvent(String eventName, String entityName, String attributeName) {
		StringBuffer bf = new StringBuffer();

		Simulator simu = repository.findById(currentSimulatorId, 3).get();
		Optional<Event> opEvent = simu.getEvents().stream().filter(e -> e.getName().contentEquals(eventName))
				.findFirst();

		Optional<Entity> opEntity = simu.getEntitys().stream().filter(e -> e.getName().contentEquals(entityName))
				.findFirst();

		if (opEntity.isPresent() && opEvent.isPresent()) {
			Optional<Attribute> attribute = opEntity.get().getAttributes().stream()
					.filter(a -> a.getName().contentEquals(attributeName)).findFirst();

			if (attribute.isPresent()) {
				Event event = opEvent.get();
				event.getReadAttribute().remove(attribute.get());
				repository.save(simu);

				bf.append("Removed Attribute " + attributeName + " from " + entityName + "as releation to "
						+ event.getName() + ".");
			}

		} else {
			bf.append("Entity " + entityName + " or Event " + eventName + " does not exisits in " + simu.getName() + "!"
					+ System.lineSeparator());
		}

		return bf.toString();
	}

	@ShellMethod("Print Events of Simulators")
	public String printEventsOfSimulator(@ShellOption(valueProvider = SimulatorValueProvider.class) String simulator) {

		Simulator simu = repository.findById(repository.findByName(simulator).getId(), 3).get();

		StringBuffer output = new StringBuffer(
				"Events from Simulator " + simu.getName() + ":" + System.lineSeparator());

		simu.getEvents().stream().forEach(e -> {
			output.append("\t" + e.getName() + System.lineSeparator());
			printEventInformation(e, output);
		});

		return output.toString();
	}

	private void printEventInformation(Event e, StringBuffer bf) {
		e.getEvents().forEach(s -> bf.append("\t\t" + "Event " + s.getEndEvent().getName() + " condition "
				+ s.getCondition() + " delay " + s.getDelay() + System.lineSeparator()));

	}

	@ShellMethod("Print Entities of Simulators")
	public String printEntitiesOfSimulator(
			@ShellOption(valueProvider = SimulatorValueProvider.class) String simulator) {

		Simulator simu = repository.findByName(simulator);

		StringBuffer output = new StringBuffer(
				"Entities from Simulator " + simu.getName() + ":" + System.lineSeparator());

		simu.getEntitys().stream().forEach(e -> output.append(("\t" + e.getName() + System.lineSeparator())));

		return output.toString();
	}

	@ShellMethod("Add Event")
	@ShellMethodAvailability("currenSimulatorAvailabilityCheck")
	public String addEvent(String name) {

		Simulator simu = repository.findById(currentSimulatorId, 3).get();

		long count = simu.getEvents().stream().filter(e -> !e.getName().contentEquals(name)).count();

		if (0 > count) {
			return "Entity: " + name + " already exisits in " + simu.getName() + "!";
		}

		simu.addEvents(new Event(name));
		repository.save(simu);
		return "Added Entity: " + name + " to " + simu.getName();
	}

	@ShellMethod("Add Schedules Relation")
	@ShellMethodAvailability("currenSimulatorAvailabilityCheck")
	public String addSchedulesRelation(String startEventName, String endEventName, String conditionFunction,
			String delayFunction) {

		StringBuffer bf = new StringBuffer();

		Simulator simu = repository.findById(currentSimulatorId, 3).get();

		Event startEvent = simu.getEvents().stream().filter(e -> e.getName().contentEquals(startEventName)).findAny()
				.get();
		Event endEvent = simu.getEvents().stream().filter(e -> e.getName().contentEquals(endEventName)).findAny().get();

		if (null == startEvent || null == endEvent) {
			bf.append("Start Event or End Event not found in the Simulator " + simu.getName() + System.lineSeparator());
		} else {
			startEvent.addSchedulesEvent(endEvent, conditionFunction, delayFunction);
			repository.save(simu);
			bf.append("Schedule Relationship added." + System.lineSeparator());
		}

		return bf.toString();
	}

	@ShellMethod("Edit Schedules Relation")
	@ShellMethodAvailability("currenSimulatorAvailabilityCheck")
	public String editSchedulesRelation(String startEventName, String endEventName, String conditionFunction,
			String delayFunction) {

		StringBuffer bf = new StringBuffer();

		Simulator simu = repository.findById(currentSimulatorId, 3).get();

		Event startEvent = simu.getEvents().stream().filter(e -> e.getName().contentEquals(startEventName)).findAny()
				.get();
		Event endEvent = simu.getEvents().stream().filter(e -> e.getName().contentEquals(endEventName)).findAny().get();

		if (null == startEvent || null == endEvent) {
			bf.append("Start Event or End Event not found in the Simulator " + simu.getName() + System.lineSeparator());
		} else {

			Schedules schedules = startEvent.getEvents().stream()
					.filter(s -> s.getEndEvent().getName().contentEquals(endEventName)).findFirst().get();

			if (null != conditionFunction && !conditionFunction.isEmpty()) {
				schedules.setCondition(conditionFunction);
			}

			if (null != delayFunction && !delayFunction.isEmpty()) {
				schedules.setDelay(delayFunction);
			}

			repository.save(simu);
			bf.append("Schedule Relationship edited." + System.lineSeparator());
		}
		return bf.toString();
	}

	@ShellMethod("Edit Schedules Relation")
	@ShellMethodAvailability("currenSimulatorAvailabilityCheck")
	public String removeSchedulesRelation(String startEventName, String endEventName) {

		StringBuffer bf = new StringBuffer();

		Simulator simu = repository.findById(currentSimulatorId, 3).get();

		Event startEvent = simu.getEvents().stream().filter(e -> e.getName().contentEquals(startEventName)).findAny()
				.get();
		Event endEvent = simu.getEvents().stream().filter(e -> e.getName().contentEquals(endEventName)).findAny().get();

		if (null == startEvent || null == endEvent) {
			bf.append("Start Event or End Event not found in the Simulator " + simu.getName() + System.lineSeparator());
		} else {

			Schedules schedules = startEvent.getEvents().stream()
					.filter(s -> s.getEndEvent().getName().contentEquals(endEventName)).findFirst().get();

			startEvent.getEvents().remove(schedules);

			repository.save(simu);
			bf.append("Schedule Relationship removed." + System.lineSeparator());
		}
		return bf.toString();

	}

	@ShellMethod("Delete Simulator")
	public void deleteSimulator(@ShellOption(valueProvider = SimulatorValueProvider.class) String name) {
		Simulator findByName = repository.findByName(name);
		repository.deleteById(findByName.getId());
	}

	@ShellMethod("Create Simulator")
	public void createSimulator(String name, String description) {

		Simulator simu = new Simulator(name, description);
		simu.setName(name);
		simu.setBeschreibung(description);
		repository.save(simu);
	}

	@ShellMethod("Edit Simulator name")
	@ShellMethodAvailability("currenSimulatorAvailabilityCheck")
	public void editSimulatorName(String newSimulatorName) {

		Simulator simulator = repository.findById(currentSimulatorId, 3).get();
		simulator.setName(newSimulatorName);

		repository.save(simulator);
	}

	@ShellMethod("Edit Simulator description")
	@ShellMethodAvailability("currenSimulatorAvailabilityCheck")
	public void editSimulatorDescription(String newSimulatorDescription) {

		Simulator simulator = repository.findById(currentSimulatorId, 3).get();
		simulator.setBeschreibung(newSimulatorDescription);

		repository.save(simulator);
	}

	@ShellMethod("Print existing Simulators")
	public String printSimulators() {
		StringBuffer output = new StringBuffer("Found following Simulators in the Database:" + System.lineSeparator());

		Iterable<Simulator> findAll = repository.findAll();
		for (Simulator simulator : findAll) {
			output.append(simulator.getName() + System.lineSeparator());
		}

		return output.toString();
	}

	public Availability currenSimulatorAvailabilityCheck() {
		return (null != currentSimulatorId) ? Availability.available()
				: Availability.unavailable("No Simulator is set.");
	}

}
