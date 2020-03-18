package edu.kit.ipd.sdq.modsim.descomp.extractor.abstractsimengine;

import java.io.File;
import java.util.Collection;

import org.springframework.stereotype.Service;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;
import soot.*;
import soot.options.Options;

@Service
public class AbstractSimEngineExtractorService implements ExtractorService {

	@Override
	public Simulator extractSimulator(File simulator) {
		String[] args1 = {"-w", "-process-dir", simulator.getAbsolutePath()};

		return runExtractionForArgs(args1);
	}

	@Override
	public Simulator extractSimulatorList(Collection<File> simulatorList) {
		String[] args1 = new String[simulatorList.size() * 2 + 1];
		args1[0] = "-w";
//			args1[1] = "-process-dir";
		int i = 1;
		for (File simulator : simulatorList) {
			args1[i] = "-process-dir";
			args1[i + 1] = simulator.getAbsolutePath();
			System.out.println(args1[i] + "-" + args1[i + 1]);
			i += 2;
		}
		return runExtractionForArgs(args1);
	}

	private Simulator runExtractionForArgs(String[] args1){
		Options.v().set_src_prec(Options.src_prec_c);
		Options.v().set_output_format(Options.output_format_jimple);
		// Options.v().setPhaseOption("cg.spark", "on");
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_whole_program(true);

		AbstractSimEngineSceneTransformer transformer = new AbstractSimEngineSceneTransformer();
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));
		soot.Main.main(args1);


		Simulator extractedSimulation = new Simulator("EXTARCTED_SIMULATION", "EXTARCTED_SIMULATION");

		transformer.getEvents().forEach(event -> extractedSimulation.addEvents(event));
		transformer.getEntities().forEach(entity -> extractedSimulation.addEntities(entity));

		if(PackManager.v().hasPack("wjtp")){
			PackManager.v().getPack("wjtp").remove("wjtp.myTrans");
		}
		G.reset();
		return extractedSimulation;
	}

}

