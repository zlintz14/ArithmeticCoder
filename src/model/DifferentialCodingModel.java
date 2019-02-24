package model;

import java.util.HashMap;
import java.util.Map;

import ac.SourceModel;

public class DifferentialCodingModel implements SourceModel<Integer>{

	private Integer[] pixelDifferences;
	private Map<Integer, Double> intenstityToProbability;
	
	public DifferentialCodingModel(Integer[] pixelDifferences, Map<Integer, Double> intenstityToProbability) {
		assert pixelDifferences != null: "pixelIntensity is null";
		assert intenstityToProbability != null: "intenstityToProbability is null";
		assert pixelDifferences.length > 1: "pixelIntensity only has 1 element in it";
		
		this.pixelDifferences = pixelDifferences.clone();
		this.intenstityToProbability = new HashMap<Integer, Double>(intenstityToProbability);
	}
	
	@Override
	public int size() {
		return pixelDifferences.length;
	}

	@Override
	public Integer get(int index) {
		assert index >= 0 && index < size();
		
		return pixelDifferences[index];
	}

	@Override
	public double cdfLow(int index) {
		assert index >= 0 && index < size();
		
		return intenstityToProbability.get(index);
	}

}
