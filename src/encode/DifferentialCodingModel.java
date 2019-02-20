package encode;

import java.util.HashMap;
import java.util.Map;

import ac.SourceModel;

public class DifferentialCodingModel implements SourceModel<Integer> {

	private Integer[] pixelIntensity;
	private Map<Integer, Double> intenstityToProbability;
	
	public DifferentialCodingModel(Integer[] pixelIntensity, Map<Integer, Double> intenstityToProbability) {
		assert pixelIntensity != null: "pixelIntensity is null";
		assert intenstityToProbability != null: "intenstityToProbability is null";
		assert pixelIntensity.length > 1: "pixelIntensity only has 1 element in it";
		
		this.pixelIntensity = pixelIntensity.clone();
		this.intenstityToProbability = new HashMap<Integer, Double>(intenstityToProbability);
	}
	
	@Override
	public int size() {
		return pixelIntensity.length;
	}

	@Override
	public Integer get(int index) {
		assert index >= 0 && index < size();
		
		return pixelIntensity[index];
	}

	@Override
	//higher probabilties near prior value
	public double cdfLow(int index) {
		return intenstityToProbability.get(index);
	}

}
