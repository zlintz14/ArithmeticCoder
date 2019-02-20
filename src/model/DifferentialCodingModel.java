package model;

import java.util.HashMap;
import java.util.Map;

import ac.SourceModel;

public class DifferentialCodingModel implements SourceModel<Integer>, SourceModelExtended<Integer> {

	private Integer[] pixelIntensity;
	private Map<Integer, Double> intenstityToProbability;
	private int lastPixel;
	
	public DifferentialCodingModel(Integer[] pixelIntensity, Map<Integer, Double> intenstityToProbability) {
		assert pixelIntensity != null: "pixelIntensity is null";
		assert intenstityToProbability != null: "intenstityToProbability is null";
		assert pixelIntensity.length > 1: "pixelIntensity only has 1 element in it";
		
		this.pixelIntensity = pixelIntensity.clone();
		this.intenstityToProbability = new HashMap<Integer, Double>(intenstityToProbability);
		lastPixel = 0;
	}
	
	public void setLastPixel(int lastPixel) {
		this.lastPixel = lastPixel;
	}
	
	@Override
	public int getLastPixel() {
		return lastPixel;
	}
	
	@Override
	public int size() {
		return pixelIntensity.length;
	}

	@Override
	public Integer get(int index) {
		assert index > -size() && index < size();
		
		return pixelIntensity[index];
	}

	@Override
	public double cdfLow(int index) {
		return intenstityToProbability.get(index);
	}

}
