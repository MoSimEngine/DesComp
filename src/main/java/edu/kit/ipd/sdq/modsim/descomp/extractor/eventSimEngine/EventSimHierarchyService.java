package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine;

import java.util.Collection;

public interface EventSimHierarchyService {
    Collection<String> getDerivedClasses(String parentClass);
}
