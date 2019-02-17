package encode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import ac.ArithmeticEncoder;
import io.InputStreamBitSource;
import io.OutputStreamBitSink;

public class EncodeVideo {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String inputFileName = System.getProperty("user.dir") + "/data/out.dat";
		String outputFileName = System.getProperty("user.dir") + "/data/compressed.dat";

		int range_bit_width = 40;

		System.out.println("Encoding text file: " + inputFileName);
		System.out.println("Output file: " + outputFileName);
		System.out.println("Range Register Bit Width: " + range_bit_width);

		int totalBytes = (int) new File(inputFileName).length();
		
		Integer[] pixelIntensity = new Integer[256];
		for(int i = 0; i < 256; i++) {
			pixelIntensity[i] = i;
		}

		// Create 256 models. Model chosen depends on intensity of pixel prior to 
				// pixel being encoded.
		DifferentialCodingModel[] models = new DifferentialCodingModel[256];
		
		for(int i = 0; i < 256; i++) {
			// Create new model with default count of 1 for all pixel intensities
			models[i] = new DifferentialCodingModel(pixelIntensity);
		}
		
		ArithmeticEncoder<Integer> encoder = new ArithmeticEncoder<Integer>(range_bit_width);
		
		FileOutputStream fos = new FileOutputStream(outputFileName);
		OutputStreamBitSink bit_sink = new OutputStreamBitSink(fos);
		
		LinkedList<Integer[][]> frames = new LinkedList<Integer[][]>();

		
		fos.close();
	}
	
}
