package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Simulator;

import java.io.File;
import java.util.Collection;

public interface IEventSimExtractorService {
    /**
     * extracts an simulator instance predefined filters
     *
     * @param FileCollection collection of jar file to analyse. also contain the expected predefined filter classes
     * @return the extracted simulator
     */
    Simulator extractEventSim(Collection<File> FileCollection);

    /**
     * extract an simulator instance with by parameter defined filters
     *
     * @param jarCollection collection of jar file to analyse. also contain the expected predefined filter classes
     * @param expectedClassFilterNames specifies relevant classes by their classfile name
     * @param expectedMethodFilterNames specifies relevant methods by their name
     * @return the extracted simulator
     */
    Simulator extractEventSimForSpecifiedClassesAndMethods(Collection<File> jarCollection, String[] expectedClassFilterNames, String[] expectedMethodFilterNames);
}
