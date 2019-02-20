package decode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ac.ArithmeticDecoder;
import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;
import model.DifferentialCodingModel;

public class DecodeVideo {

	public static void main(String[] args) throws InsufficientBitsLeftException, IOException {
		String inputFileName = System.getProperty("user.dir") + "/data/compressed.dat";
		String outputFileName = System.getProperty("user.dir") + "/data/newOut.dat";

		FileInputStream fis = new FileInputStream(inputFileName);

		InputStreamBitSource bitSource = new InputStreamBitSource(fis);

		//first 4 bytes are total number of Bytes in file, this is signaled by encoder
		int totalNumPixels = bitSource.next(32);
		
		// Read in range bit width and setup the decoder
		int rangeBitWidth = bitSource.next(8);
		ArithmeticDecoder<Integer> decoder = new ArithmeticDecoder<Integer>(rangeBitWidth);

		
		Map<Integer, Double> intenstityToSummedProbability = new HashMap<Integer, Double>();
		double currProbability = 0.0;
		for(int i = 0; i < 256; i++) {
			double tempProbability = (double) bitSource.next(32) / totalNumPixels;
			currProbability += tempProbability;
			intenstityToSummedProbability.put(i, currProbability);
		}
		
		Integer[] pixelIntensity = new Integer[256];
		for (int i=0; i<256; i++) {
			pixelIntensity[i] = i;
		}
		
		DifferentialCodingModel[] models = new DifferentialCodingModel[256];
		
		for(int i = 0; i < 256; i++) {
			models[i] = new DifferentialCodingModel(pixelIntensity, intenstityToSummedProbability);
		}
		
		System.out.println("Uncompressing file: " + inputFileName);
		System.out.println("Output file: " + outputFileName);
		System.out.println("Range Register Bit Width: " + rangeBitWidth);
		System.out.println("Number of encoded symbols: " + totalNumPixels);
		
		FileOutputStream fos = new FileOutputStream(outputFileName);
		
		// Use model 0 as initial model.
		DifferentialCodingModel model = models[0];	
		
		int lastPixel = Integer.MIN_VALUE;
		for(int i = 0; i < totalNumPixels; i++) {
			if(i % 64 == 0) {
				int pixel = 0;
				pixel = decoder.decode(model, bitSource);
				fos.write(pixel);
				lastPixel = pixel;
			} else {
				int pixel = decoder.decode(model, bitSource);
				pixel = lastPixel + pixel;
				fos.write(pixel);
				lastPixel = pixel;
			}
			
			// Set up next model based on pixel just encoded
			model = models[lastPixel];
			
			//Make updates to Model
			if(i % 63 == 0) {
				model.setLastPixel(0);
			} else {
				model.setLastPixel(lastPixel);
			}
			
		}
		
		System.out.println("Done.");
		fos.flush();
		fos.close();
		fis.close();		
	}
	
}
