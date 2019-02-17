package encode;

import ac.SourceModel;

public class DifferentialCodingModel implements SourceModel<Integer> {

	private Integer[] pixelIntensity;
	private int[] counts;
	
	public DifferentialCodingModel(Integer[] pixelIntensity) {
		assert pixelIntensity != null: "pixelIntensity is null";
		assert pixelIntensity.length > 1: "pixelIntensity only has 1 element in it";
		counts = new int[pixelIntensity.length];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = 1;
		}
		
		this.pixelIntensity = pixelIntensity.clone();
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Integer get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double cdfLow(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

}
