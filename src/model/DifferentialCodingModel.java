package model;

import java.util.HashMap;
import java.util.Map;

import ac.SourceModel;

public class DifferentialCodingModel implements SourceModel<Integer>{

	private Integer[] pixelDifferences;
	private Map<Integer, Double> intenstityToProbability;
	private int lastPixel;
	
	public DifferentialCodingModel(Integer[] pixelDifferences, Map<Integer, Double> intenstityToProbability) {
		assert pixelDifferences != null: "pixelIntensity is null";
		assert intenstityToProbability != null: "intenstityToProbability is null";
		assert pixelDifferences.length > 1: "pixelIntensity only has 1 element in it";
		
		this.pixelDifferences = pixelDifferences.clone();
		this.intenstityToProbability = new HashMap<Integer, Double>(intenstityToProbability);
		lastPixel = 0;
	}
	
	public void setLastPixel(int lastPixel) {
		this.lastPixel = lastPixel;
	}
	
//	@Override
//	public int getLastPixel() {
//		return lastPixel;
//	}
	
	@Override
	public int size() {
		return pixelDifferences.length;
	}

	@Override
	public Integer get(int index) {
		assert index > -size() && index < size();
		
		if(index < 0) {
			index = 255 + (index * -1);
		}
		
		return pixelDifferences[index];
	}

	@Override
	public double cdfLow(int index) {
		if(index < 0) {
			index = 255 + (index * -1);
		}
		
		return intenstityToProbability.get(index);
	}

}
