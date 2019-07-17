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
import soot.PackManager;
import soot.SceneTransformer;
import soot.Transform;
import soot.options.Options;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImportTest {

	@Autowired
	SimulatorRepository simulatorRepository;

	@Test
	public void importBusSim() throws IOException {
		// simulatorRepository.cleanAll();

		importBusViaSoot();
		printSimilarityResult();
	}

	public void importBusViaSoot() throws IOException {

		String[] args1 = { "-w", "-process-dir",
				"/home/johannes/studium/masterarbeit/gitTest/callgraphTest/bussim.jar" };

		Options.v().set_src_prec(Options.src_prec_c);
		Options.v().set_output_format(Options.output_format_jimple);
// Options.v().setPhaseOption("cg.spark", "on");
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_whole_program(true);

		AbstractSimEngineSceneTransformer transformer = new AbstractSimEngineSceneTransformer();
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));

		soot.Main.main(args1);

		Simulator importBussim = new Simulator("IMPORT_BUSSUM", "Tolle Bussimulation");

		transformer.getEvents().forEach(event -> importBussim.addEvents(event));
		transformer.getEntities().forEach(entity -> importBussim.addEntitys(entity));

		simulatorRepository.save(importBussim);

		printSimilarityResult();

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
//	public void importBusSimJar() {

//		Function<ClassParser, ClassVisitor> getClassVisitor = (ClassParser cp) -> {
//			try {
//				return new ClassVisitor(cp.parse());
//			} catch (IOException e) {
//				throw new UncheckedIOException(e);
//			}
//		};
//
//		try {
//			File f = new File("lib/de.uka.ipd.sdq.simulation.abstractsimengine.example_4.1.0.201907010845.jar");
//
//			if (!f.exists()) {
//				System.err.println("Jar file " + f.getAbsolutePath() + " does not exist");
//			}
//
//			try (JarFile jar = new JarFile(f)) {
//				Stream<JarEntry> entries = enumerationAsStream(jar.entries());
//
////				List<String> flatMap = entries.flatMap(e -> {
////					if (e.isDirectory() || !e.getName().endsWith(".class"))
////						return (new ArrayList<String>()).stream();
////					ClassParser cp = new ClassParser(
////							"lib/de.uka.ipd.sdq.simulation.abstractsimengine.example_4.1.0.201907010845.jar",
////							e.getName());
////					return getClassVisitor.apply(cp)
////				}).collect(Collectors.toList());
//
//				Simulator busSim = new Simulator("Bus", "Tolle BusSimulation");
//
//				for (String string : flatMap) {
//					if (string.startsWith("C:") && string.contains("events")) {
//						System.out.println(string);
//					}
//				}
//
/////				flatMap.filter(method -> method.contains("events")).forEach(method -> System.out.println(method));
//
//			}
//		} catch (IOException e) {
//			System.err.println("Error while processing jar: " + e.getMessage());
//			e.printStackTrace();
//		}
//	}
