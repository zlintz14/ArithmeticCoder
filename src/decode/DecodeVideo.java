package decode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ac.ArithmeticDecoder;
import app.FreqCountIntegerSymbolModel;
import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;

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
		
		Integer[] pixelDifferences = new Integer[(256 * 2) + 1];
		int index = 0;
		for(int i = 0; i < pixelDifferences.length; i++) {
			if(index == 257) {
				index = 1;
			}
			if(i > 256) {
				pixelDifferences[i] = index * -1; 
			} else {
				pixelDifferences[i] = index;
			}
			index++;
		}
		
		FreqCountIntegerSymbolModel[] models = new FreqCountIntegerSymbolModel[(256 * 2) + 1];
		
		for(int i = 0; i < models.length; i++) {
			models[i] = new FreqCountIntegerSymbolModel(pixelDifferences);
		}
		
		System.out.println("Uncompressing file: " + inputFileName);
		System.out.println("Output file: " + outputFileName);
		System.out.println("Range Register Bit Width: " + rangeBitWidth);
		System.out.println("Number of encoded symbols: " + totalNumPixels);
		
		FileOutputStream fos = new FileOutputStream(outputFileName);
		
		// Use model 0 as initial model.
		FreqCountIntegerSymbolModel model = models[0];	
		
		int lastPixel = Integer.MIN_VALUE, lastDifferentialPixel = Integer.MIN_VALUE;
		for(int i = 0; i < totalNumPixels; i++) {
			if(i % 64 == 0) {
				int pixel = 0;
				pixel = decoder.decode(model, bitSource);
				fos.write(pixel);
				lastPixel = pixel;
				lastDifferentialPixel = pixel;
			} else {
				int pixel = decoder.decode(model, bitSource);
				if(pixel < 0) {
					lastDifferentialPixel = 256 + (pixel * -1);
				} else {
					lastDifferentialPixel = pixel;
				}
				pixel = lastPixel + pixel;
				fos.write(pixel);
				lastPixel = pixel;
			}
			// Update model used
			model.addToCount(lastDifferentialPixel);
			// Set up next model based on pixel just encoded
			model = models[lastDifferentialPixel];
			
		}
		
		System.out.println("Done");
		fos.flush();
		fos.close();
		fis.close();		
	}
	
}
