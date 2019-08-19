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
public class ImportTestSimulizar {
//	protected static List<String> excludePackagesList = new ArrayList<String>();
//	static {
//		excludePackagesList.add("java.");
//		excludePackagesList.add("javax.");
//		excludePackagesList.add("sun.");
//	}

	protected static List<String> processJars = new ArrayList<String>();
	static {

		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar_2.1.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.action_1.1.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.action.edit_1.1.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.action.editor_1.1.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.action.repository_1.1.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.aggregation_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.edp2.measuringpoint_0.1.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.edp2.measuringpoint.edit_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.measuringpoint_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.measuringpoint.create_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.monitorrepository.feedthrough_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.monitorrepository.map_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.reconfiguration.henshin_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.reconfiguration.qvto_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.reconfigurationrule_0.1.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.reconfigurationrule.edit_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.reconfigurationrule.editor_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.reconfiguration.storydiagram_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.runtimemeasurement_2.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.runtimemeasurement.edit_2.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.slidingwindow_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.ui_2.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.ui.perspective_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.ui.wizards_1.0.0.201907290009.jar");
		processJars.add(
				"/home/johannes/studium/masterarbeit/simulizar/org.palladiosimulator.simulizar.utilization_1.0.1.201907290009.jar");
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

		Simulator importEventSim = new Simulator("IMPORT_SIMULIZAR", "Tolle SIMULIZAR Simulation");

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