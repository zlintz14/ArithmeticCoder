package model;

import ac.SourceModel;

/* extended SourceModel so I could implement my own lookup method since I am doing
*  differential coding and need to modify symbols coming in so I can look up the correct symbol
*/

public interface SourceModelExtended<T> extends SourceModel<T> {

	int getLastPixel();
	
	default int lookup(T pixel) {
		int lastPixel = getLastPixel();
		for (int i=0; i<size(); i++) {
			if (get(i).equals(lastPixel + (int) pixel)) {
				return i;
			}
		}
		throw new RuntimeException("Pixel not in source model");
	}
	
}
