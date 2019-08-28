package edu.kit.ipd.sdq.modsim.descomp;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ValueProviderSupport;
import org.springframework.stereotype.Component;

@Component
public class DataTypeValueProvider extends ValueProviderSupport {

	private String[] datatypes = { "DOUBLE", "INT" };

	@Override
	public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext,
			String[] hints) {

		return Arrays.asList(datatypes).stream().map(CompletionProposal::new).collect(Collectors.toList());
	}
}