package encode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ac.ArithmeticEncoder;
import io.OutputStreamBitSink;
import model.DifferentialCodingModel;

public class EncodeVideo {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String inputFileName = System.getProperty("user.dir") + "/data/out.dat";
		String outputFileName = System.getProperty("user.dir") + "/data/compressed.dat";

		int rangeBitWidth = 40;

		System.out.println("Encoding text file: " + inputFileName);
		System.out.println("Output file: " + outputFileName);
		System.out.println("Range Register Bit Width: " + rangeBitWidth);

		int numFrames = (int) new File(inputFileName).length() / 4096;
		
		FileInputStream fis = new FileInputStream(inputFileName);

		LinkedList<Integer[][]> frames = new LinkedList<Integer[][]>();
		
		int[] histogram = new int[256];
		for(int i = 0; i < histogram.length; i++) {
			histogram[i] = 0;
		}
		
		int nextByte, row = 0, col = 0, frame = 0, prevByte = 0;		
		Integer[][] currFrame = null;
		while((nextByte = fis.read()) != -1) {
			if(row == 64 && col == 64) {
				row = 0;
				col = 0;
			}
			
			if(row == 0 && col == 0) {
				frames.add(new Integer[64][64]);
				currFrame = frames.get(frame);
				frame++;
				
				currFrame[row][col] = nextByte;
				col++;
				histogram[nextByte]++;
			} else if(col != 64) {
				currFrame[row][col] = nextByte - prevByte;
				col++;
				histogram[nextByte]++;
				if(col == 64) {
					row++;
				}
			} else {
				col = 0;
				currFrame[row][col] = nextByte;
				col++;
				histogram[nextByte]++;
			}
			prevByte = nextByte;
		}
		fis.close();
		
		
		/* note this maps the pixel intensity to the total probability at the time a new entry in the set is inserted into the map.
		 * for example, if intensity 0 had a probability of 0.001, 0.001 is inserted, if intensity 1 had a probability of
		 * 0.002, its probability inserted into the map is 0.003, if intensity 2 had a probability of 0.004, 
		 * its probability inserted into the map is 0.007, etc. This is done because the only purpose of the map
		 * is for cdfLow, and summing the probabilities one time and storing them in the map avoids summing the probabilities to the 
		 * given index every time cdfLow is called and thus saves time
		 */
		Map<Integer, Double> intenstityToSummedProbability = new HashMap<Integer, Double>();
		double currProbability = 0.0;
		for(int i = 0; i < histogram.length; i++) {
			double tempProbability = (double) histogram[i] / (numFrames * 4096);
			currProbability += tempProbability;
			intenstityToSummedProbability.put(i, currProbability);
		}
		
		
		Integer[] pixelIntensity = new Integer[256];
		for(int i = 0; i < 256; i++) {
			pixelIntensity[i] = i;
		}

		// Create 256 models. Model chosen depends on intensity of pixel prior to 
				// pixel being encoded.
		DifferentialCodingModel[] models = new DifferentialCodingModel[256];
		
		for(int i = 0; i < 256; i++) {
			models[i] = new DifferentialCodingModel(pixelIntensity, intenstityToSummedProbability);
		}
		
		ArithmeticEncoder<Integer> encoder = new ArithmeticEncoder<Integer>(rangeBitWidth);
		
		FileOutputStream fos = new FileOutputStream(outputFileName);
		OutputStreamBitSink bitSink = new OutputStreamBitSink(fos);
		
		//4 bytes for number of pixels encoded
		bitSink.write(numFrames * 4096, 32);
		
		// Next byte is the width of the range registers
		bitSink.write(rangeBitWidth, 8);
		
		//encode frequencies at beginning of compressed file as header
		for(int i = 0; i < 256; i++) {
			bitSink.write(histogram[i], 32);
		}
		
		// Use model 0 as initial model.
		DifferentialCodingModel model = models[0];	
		
		/* note these nested for-loops are iterating through the entire file byte by byte, I just need 
		 * the nested for-loops to properly iterate over my linked list of 2-d arrays representing the individual frames
		 */
		for(Integer[][] arr: frames) {	
			int lastPixel = arr[0][0];
			for(int i = 0; i < arr.length; i++) {
				for(int j = 0; j < arr[i].length; j++) {
					if(j == 0) {
						encoder.encode(arr[i][j], model, bitSink);
						lastPixel = arr[i][j];
					} else {
						int pixel = arr[i][j];
						encoder.encode(pixel, model, bitSink);
						pixel = lastPixel + pixel;
						lastPixel = pixel;
					}
					
					// Set up next model based on pixel just encoded
					model = models[lastPixel];
					
					//Make updates to Model
					if(j == 63) {
						model.setLastPixel(0);
					} else {
						model.setLastPixel(lastPixel);
					}
				}
				
			}
		}		
		encoder.emitMiddle(bitSink);
		bitSink.padToWord();
		fos.close();
		
		System.out.println("Done");
	}

}
