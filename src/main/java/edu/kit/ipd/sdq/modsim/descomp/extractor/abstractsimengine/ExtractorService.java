package edu.kit.ipd.sdq.modsim.descomp.extractor.abstractsimengine;

import java.io.File;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;

public interface ExtractorService {

	Simulator extractSimulator(File simulator);

}