package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;

import java.io.File;
import java.util.Collection;

public interface EventExtractorService {
    Simulator extractEventSim(Collection<File> FileCollection);
}
