package masterarbeit.simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SimulatorApplicationTests {

	@Autowired
	SimulatorRepository simulatorRepository;

	@Test
	public void contextLoads() {
	}

	@Test
	public void menschbussim() throws IOException {
		simulatorRepository.cleanAll();

		simulatorRepository.save(busSim1());
		simulatorRepository.save(busSim2());
		simulatorRepository.save(busSim3());
		simulatorRepository.save(busSim4());
		simulatorRepository.save(busSim5());
		simulatorRepository.save(busSim6());
		simulatorRepository.save(busSim7());

		BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"));

		writer.write("Similarity Entitys:");
		writer.newLine();
		Iterable<Map<String, Object>> a = simulatorRepository.computeJaccardCoeffincyForEntitys();
		for (Map<String, Object> map : a) {
			writer.write("FROM: " + map.get("from") + " TO " + map.get("to") + " SIMILARITY: " + map.get("similarity"));
			writer.newLine();
		}

		writer.write("Similarity Events:");
		writer.newLine();

		Iterable<Map<String, Object>> b = simulatorRepository.computeJaccardCoeffincyForEvents();
		for (Map<String, Object> map : b) {
			writer.write("FROM: " + map.get("from") + " TO " + map.get("to") + " SIMILARITY: " + map.get("similarity"));
			writer.newLine();
		}
		writer.close();
	}

	private Simulator busSim1() {
		Simulator humanBus1 = new Simulator("busSim1", "Tolle Beschreibung");

		Entity human = new Entity("Human");
		Entity bus = new Entity("Bus");
		Entity busStop = new Entity("BusStop");

		humanBus1.addEntitys(bus);
		humanBus1.addEntitys(busStop);
		humanBus1.addEntitys(human);

		Event arriveEvent = new Event("ArriveEvent", null);
		arriveEvent.addEntity(bus);
		Event loadFinishedEvent = new Event("LoadFinishedEvent", null);
		loadFinishedEvent.addEntity(bus);
		Event loadPassengersEvent = new Event("LoadPassengersEvent", null);
		loadPassengersEvent.addEntity(bus);
		Event travelEvent = new Event("TravelEvent", null);
		travelEvent.addEntity(bus);
		Event unloadingFinishedEvent = new Event("UnloadingFinishedEvent", null);
		unloadingFinishedEvent.addEntity(bus);
		Event unloadPassengersEvent = new Event("UnloadPassengersEvent", null);
		unloadPassengersEvent.addEntity(bus);

		arriveEvent.addSchedulesEvent(unloadPassengersEvent);
		loadFinishedEvent.addSchedulesEvent(unloadingFinishedEvent);
		loadPassengersEvent.addSchedulesEvent(loadFinishedEvent);
		travelEvent.addSchedulesEvent(arriveEvent);
		unloadingFinishedEvent.addSchedulesEvent(loadPassengersEvent);
		unloadPassengersEvent.addSchedulesEvent(unloadingFinishedEvent);

		humanBus1.addEvents(arriveEvent);
		humanBus1.addEvents(loadFinishedEvent);
		humanBus1.addEvents(loadPassengersEvent);
		humanBus1.addEvents(travelEvent);
		humanBus1.addEvents(unloadingFinishedEvent);
		humanBus1.addEvents(unloadPassengersEvent);
		return humanBus1;
	}

	private Simulator busSim2() {
		Simulator humanBus1 = new Simulator("busSim2", "Tolle Beschreibung");

		Entity human = new Entity("Human");
		Entity bus = new Entity("Bus");
		Entity busStop = new Entity("BusStop");

		humanBus1.addEntitys(bus);
		humanBus1.addEntitys(busStop);
		humanBus1.addEntitys(human);

		Event arriveEvent = new Event("ArriveEvent", null);
		arriveEvent.addEntity(bus);
		Event loadFinishedEvent = new Event("LoadFinishedEvent", null);
		loadFinishedEvent.addEntity(bus);
		Event loadPassengersEvent = new Event("LoadPassengersEvent", null);
		loadPassengersEvent.addEntity(bus);
		Event travelEvent = new Event("TravelEvent", null);
		travelEvent.addEntity(bus);
		Event unloadingFinishedEvent = new Event("UnloadingFinishedEvent", null);
		unloadingFinishedEvent.addEntity(bus);
		Event unloadPassengersEvent = new Event("UnloadPassengersEvent", null);
		unloadPassengersEvent.addEntity(bus);

		arriveEvent.addSchedulesEvent(unloadPassengersEvent);
		loadFinishedEvent.addSchedulesEvent(unloadingFinishedEvent);
		loadPassengersEvent.addSchedulesEvent(loadFinishedEvent);
		travelEvent.addSchedulesEvent(arriveEvent);
		unloadingFinishedEvent.addSchedulesEvent(loadPassengersEvent);
		unloadPassengersEvent.addSchedulesEvent(unloadingFinishedEvent);

		humanBus1.addEvents(arriveEvent);
		humanBus1.addEvents(loadFinishedEvent);
		humanBus1.addEvents(loadPassengersEvent);
		humanBus1.addEvents(travelEvent);
		humanBus1.addEvents(unloadingFinishedEvent);
		humanBus1.addEvents(unloadPassengersEvent);
		return humanBus1;
	}

	private Simulator busSim3() {
		Simulator humanBus1 = new Simulator("busSim3", "Tolle Beschreibung");

		Entity human = new Entity("Human");
		Entity bus = new Entity("Bus");
		Entity busStop = new Entity("BusStop");

		humanBus1.addEntitys(bus);
		humanBus1.addEntitys(busStop);
		humanBus1.addEntitys(human);

		Event arriveEvent = new Event("ArriveEvent", null);
		arriveEvent.addEntity(bus);
		Event loadFinishedEvent = new Event("LoadFinishedEvent", null);
		loadFinishedEvent.addEntity(bus);
		Event loadPassengersEvent = new Event("LoadPassengersEvent", null);
		loadPassengersEvent.addEntity(bus);
		Event travelEvent = new Event("TravelEvent", null);
		travelEvent.addEntity(bus);
		Event unloadingFinishedEvent = new Event("UnloadingFinishedEvent", null);
		unloadingFinishedEvent.addEntity(bus);
		Event unloadPassengersEvent = new Event("UnloadPassengersEvent", null);
		unloadPassengersEvent.addEntity(bus);

		arriveEvent.addSchedulesEvent(unloadPassengersEvent);
		loadFinishedEvent.addSchedulesEvent(unloadingFinishedEvent);
		loadPassengersEvent.addSchedulesEvent(loadFinishedEvent);
		travelEvent.addSchedulesEvent(arriveEvent);
		unloadingFinishedEvent.addSchedulesEvent(loadPassengersEvent);
		unloadPassengersEvent.addSchedulesEvent(unloadingFinishedEvent);

		humanBus1.addEvents(arriveEvent);
		humanBus1.addEvents(travelEvent);
		humanBus1.addEvents(unloadingFinishedEvent);
		humanBus1.addEvents(unloadPassengersEvent);
		return humanBus1;
	}

	private Simulator busSim4() {
		Simulator humanBus1 = new Simulator("busSim4", "Tolle Beschreibung");

		Entity human = new Entity("Human");
		Entity bus = new Entity("Bus");
		Entity busStop = new Entity("BusStop");

		humanBus1.addEntitys(bus);
		humanBus1.addEntitys(busStop);
		humanBus1.addEntitys(human);

		Event loadPassengersEvent = new Event("LoadPassengersEvent", null);
		loadPassengersEvent.addEntity(bus);
		Event travelEvent = new Event("TravelEvent", null);
		travelEvent.addEntity(bus);
		Event unloadingFinishedEvent = new Event("UnloadingFinishedEvent", null);
		unloadingFinishedEvent.addEntity(bus);
		Event unloadPassengersEvent = new Event("UnloadPassengersEvent", null);
		unloadPassengersEvent.addEntity(bus);

		unloadingFinishedEvent.addSchedulesEvent(loadPassengersEvent);
		unloadPassengersEvent.addSchedulesEvent(unloadingFinishedEvent);

		humanBus1.addEvents(loadPassengersEvent);
		humanBus1.addEvents(travelEvent);
		humanBus1.addEvents(unloadingFinishedEvent);
		humanBus1.addEvents(unloadPassengersEvent);
		return humanBus1;
	}

	private Simulator busSim5() {
		Simulator humanBus1 = new Simulator("busSim5", "Tolle Beschreibung");

		Entity human = new Entity("Human");
		Entity bus = new Entity("Bus");
		Entity busStop = new Entity("BusStop");

		humanBus1.addEntitys(bus);
		humanBus1.addEntitys(busStop);
		humanBus1.addEntitys(human);

		return humanBus1;
	}

	private Simulator busSim6() {
		Simulator humanBus1 = new Simulator("busSim6", "Tolle Beschreibung");

		Entity human = new Entity("Human");
		Entity bus = new Entity("Bus");
		Entity busStop = new Entity("BusStop");

		humanBus1.addEntitys(bus);
		humanBus1.addEntitys(busStop);
		humanBus1.addEntitys(human);

		Event arriveEvent = new Event("ArriveEvent", null);
		arriveEvent.addEntity(bus);
		Event loadFinishedEvent = new Event("LoadFinishedEvent", null);
		loadFinishedEvent.addEntity(bus);
		Event loadPassengersEvent = new Event("LoadPassengersEvent", null);
		loadPassengersEvent.addEntity(bus);
		Event unloadingFinishedEvent = new Event("UnloadingFinishedEvent", null);
		unloadingFinishedEvent.addEntity(bus);
		Event unloadPassengersEvent = new Event("UnloadPassengersEvent", null);
		unloadPassengersEvent.addEntity(bus);

		arriveEvent.addSchedulesEvent(unloadPassengersEvent);
		loadFinishedEvent.addSchedulesEvent(unloadingFinishedEvent);
		loadPassengersEvent.addSchedulesEvent(loadFinishedEvent);
		unloadingFinishedEvent.addSchedulesEvent(loadPassengersEvent);
		unloadPassengersEvent.addSchedulesEvent(unloadingFinishedEvent);

		humanBus1.addEvents(arriveEvent);
		humanBus1.addEvents(loadFinishedEvent);
		humanBus1.addEvents(loadPassengersEvent);
		humanBus1.addEvents(unloadingFinishedEvent);
		humanBus1.addEvents(unloadPassengersEvent);
		return humanBus1;
	}

	private Simulator busSim7() {
		Simulator humanBus1 = new Simulator("busSim7", "Tolle Beschreibung");

		Entity human = new Entity("Human");
		Entity bus = new Entity("Bus");
		Entity busStop = new Entity("BusStop");

		humanBus1.addEntitys(bus);
		humanBus1.addEntitys(busStop);
		humanBus1.addEntitys(human);

		Event loadFinishedEvent = new Event("LoadFinishedEvent", null);
		loadFinishedEvent.addEntity(bus);
		Event loadPassengersEvent = new Event("LoadPassengersEvent", null);
		loadPassengersEvent.addEntity(bus);
		Event travelEvent = new Event("TravelEvent", null);
		travelEvent.addEntity(bus);
		Event unloadingFinishedEvent = new Event("UnloadingFinishedEvent", null);
		unloadingFinishedEvent.addEntity(bus);
		Event unloadPassengersEvent = new Event("UnloadPassengersEvent", null);
		unloadPassengersEvent.addEntity(bus);

		loadFinishedEvent.addSchedulesEvent(unloadingFinishedEvent);
		loadPassengersEvent.addSchedulesEvent(loadFinishedEvent);
		unloadingFinishedEvent.addSchedulesEvent(loadPassengersEvent);
		unloadPassengersEvent.addSchedulesEvent(unloadingFinishedEvent);

		humanBus1.addEvents(loadFinishedEvent);
		humanBus1.addEvents(loadPassengersEvent);
		humanBus1.addEvents(travelEvent);
		humanBus1.addEvents(unloadingFinishedEvent);
		humanBus1.addEvents(unloadPassengersEvent);
		return humanBus1;
	}

}
