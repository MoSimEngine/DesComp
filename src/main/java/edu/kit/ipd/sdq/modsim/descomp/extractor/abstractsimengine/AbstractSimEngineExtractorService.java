package edu.kit.ipd.sdq.modsim.descomp.extractor.abstractsimengine;

import java.io.File;

import org.springframework.stereotype.Service;

import edu.kit.ipd.sdq.modsim.descomp.data.Simulator;
import soot.PackManager;
import soot.Transform;
import soot.options.Options;

@Service
public class AbstractSimEngineExtractorService implements ExtractorService {

	@Override
	public Simulator extractSimulator(File simulator) {

		String[] args1 = { "-w", "-process-dir", simulator.getAbsolutePath() };

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

		return extractedSimulation;

	}

}
