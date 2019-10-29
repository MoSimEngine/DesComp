package edu.kit.ipd.sdq.modsim.descomp;

import edu.kit.ipd.sdq.modsim.descomp.services.SimulationRepository;
import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ValueProviderSupport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SimulationValueProvider extends ValueProviderSupport {

	@Autowired
	private SimulationRepository repository;

	private List<String> simulations;

	@Override
	public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext,
			String[] hints) {

		// Cache results
		if (null == simulations || simulations.isEmpty()) {
			simulations = new ArrayList<String>();
			repository.findAll().forEach(e -> simulations.add(e.getName()));
		}

		return simulations.stream().map(CompletionProposal::new).collect(Collectors.toList());
	}
}
