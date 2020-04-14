package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine;

import java.util.Collection;

public interface IEventSimHierarchyService {
    Collection<String> getDerivedClasses(String parentClass);
}
