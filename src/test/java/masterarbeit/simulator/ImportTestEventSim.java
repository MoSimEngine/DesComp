package masterarbeit.simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import masterarbeit.simulator.soot.AbstractSimEngineSceneTransformer;
import soot.Main;
import soot.PackManager;
import soot.Transform;
import soot.options.Options;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImportTestEventSim {
//	protected static List<String> excludePackagesList = new ArrayList<String>();
//	static {
//		excludePackagesList.add("java.");
//		excludePackagesList.add("javax.");
//		excludePackagesList.add("sun.");
//	}

	protected static List<String> processJars = new ArrayList<String>();
	static {

		processJars.add("/home/johannes/studium/masterarbeit/eventsim/com.google.inject.guice_4.0.0.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.api_2.0.0.201907282351.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.core_2.0.0.201907282351.jar");
//		processJars.add(
//				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.instrumentation.description_1.0.0.201907282351.jar");
//		processJars.add(
//				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.instrumentation.injection_1.0.0.201907282351.jar");
//		processJars.add(
//				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.instrumentation.restrictions_1.0.0.201907282351.jar");
//		processJars.add(
//				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.instrumentation.specification_1.0.0.201907282351.jar");
//		processJars.add(
//				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.instrumentation.utils_1.0.0.201907282351.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.launch_2.0.0.201907282351.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.measurement_2.0.0.201907282351.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.measurement.osgi_2.0.0.201907282351.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.measurement.r_2.0.0.201907282351.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.measurement.r.launch_2.0.0.201907282351.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.middleware_2.0.0.201907282351.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.resources_2.0.0.201907282351.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.rvisualization_1.0.0.201907282351.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.system_2.0.0.201907282351.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/eventsim/edu.kit.ipd.sdq.eventsim.workload_2.0.0.201907282351.jar");
	}
	@Autowired
	SimulatorRepository simulatorRepository;

	@Test
	public void importBusSim() throws IOException {
		simulatorRepository.cleanAll();

		importEventSimViaSoot();
		// printSimilarityResult();
	}

	public void importEventSimViaSoot() throws IOException {

		String[] args1 = { "-w" };

		Options.v().set_src_prec(Options.src_prec_c);
		Options.v().set_output_format(Options.output_format_jimple);
// Options.v().setPhaseOption("cg.spark", "on");
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_whole_program(true);
//		Options.v().set_exclude(excludePackagesList);
		Options.v().set_process_dir(processJars);

		AbstractSimEngineSceneTransformer transformer = new AbstractSimEngineSceneTransformer();
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));

		try {
			Main.v().run(args1);
		} catch (Exception e) {
			// System.out.println(e.getMessage());
		}

		Simulator importEventSim = new Simulator("IMPORT_EVENTSIM", "Tolle EVENTSIM Simulation");

		transformer.getEvents().forEach(event -> importEventSim.addEvents(event));
		transformer.getEntities().forEach(entity -> importEventSim.addEntitys(entity));
		System.out.println("OK");
		System.out.println(importEventSim);
		simulatorRepository.save(importEventSim);

		// printSimilarityResult();

	}

	private void printSimilarityResult() throws IOException {
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

	public <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {
			public T next() {
				return e.nextElement();
			}

			public boolean hasNext() {
				return e.hasMoreElements();
			}
		}, Spliterator.ORDERED), false);
	}
}