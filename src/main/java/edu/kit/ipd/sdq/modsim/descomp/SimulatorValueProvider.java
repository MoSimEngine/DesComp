package edu.kit.ipd.sdq.modsim.descomp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ValueProviderSupport;
import org.springframework.stereotype.Component;

import edu.kit.ipd.sdq.modsim.descomp.services.SimulatorRepository;

@Component
class SimulatorValueProvider extends ValueProviderSupport {

	@Autowired
	private SimulatorRepository repository;

	private List<String> simulatoren;

	@Override
	public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext,
			String[] hints) {

		// Cache results
		if (null == simulatoren || simulatoren.isEmpty()) {
			simulatoren = new ArrayList<String>();
			repository.findAll().forEach(e -> simulatoren.add(e.getName()));
		}

		return simulatoren.stream().map(CompletionProposal::new).collect(Collectors.toList());
	}
}