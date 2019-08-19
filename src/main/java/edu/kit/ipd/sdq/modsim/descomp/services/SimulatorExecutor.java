package edu.kit.ipd.sdq.modsim.descomp.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import edu.kit.ipd.sdq.modsim.descomp.data.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.Simulator;

@Service
public class SimulatorExecutor {

	public void executeSimulator(Simulator simulator) {
		initSimulation(simulator);

	}

	private void initSimulation(Simulator simulator) {

		Map<String, String> entities = new HashMap<String, String>(simulator.getEntitys().size());
		for (Entity entity : simulator.getEntitys()) {

		}

	}
}
