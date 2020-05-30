package com.options.analyzer.optionsanalyzer.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import com.options.analyzer.optionsanalyzer.model.entity.OptionPair;

public class Utils {

	public static List<List<OptionPair>> getOptionPairs(JsonNode rootNode, int partitionSize) {
		List<OptionPair> results = new ArrayList<>();
		rootNode.withArray("options").elements().forEachRemaining(node -> {
			JsonNode optionChainResponseNode = node.get("OptionChainResponse");
			
			JsonNode expirationNode = node.get("Expiration");
			LocalDateTime expiration = LocalDate.of(expirationNode.get("year").asInt(), 
					expirationNode.get("month").asInt(),
					expirationNode.get("day").asInt()).atStartOfDay();
			
			optionChainResponseNode.withArray("OptionPair").elements().forEachRemaining(optionPairNode -> {
				long uniquePair = Generators.timeBasedGenerator().generate().timestamp();
				JsonNode callNode = optionPairNode.get("Call");
				JsonNode putNode = optionPairNode.get("Put");
				results.add(OptionPair.getOptionPair(expiration, uniquePair, callNode));
				results.add(OptionPair.getOptionPair(expiration, uniquePair, putNode));
			});
		});
		return Lists.partition(results, partitionSize); 
	}
}
