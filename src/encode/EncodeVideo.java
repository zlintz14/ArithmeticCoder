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

		int rangeBitWidth = 40;

		System.out.println("Encoding text file: " + inputFileName);
		System.out.println("Output file: " + outputFileName);
		System.out.println("Range Register Bit Width: " + rangeBitWidth);

		int numFrames = (int) new File(inputFileName).length() / 4096;
		
		FileInputStream fis = new FileInputStream(inputFileName);

		LinkedList<Integer[][]> frames = new LinkedList<Integer[][]>();
		
		int nextByte, row = 0, col = 0, frame = 0, leadingByte = 0;
		Integer[][] currFrame = null;
		while((nextByte = fis.read()) != -1) {
			if(row == 0 && col == 0) {
				frames.add(new Integer[64][64]);
				currFrame = frames.get(frame);
				frame++;
				
				currFrame[row][col] = nextByte;
				col++;
			} else if(col != 64) {
				currFrame[row][col] = nextByte - leadingByte;
				col++;
			} else {
				row++;
				if(row == 64 && col == 64) {
					row = 0;
					col = 0;
				} else {
					col = 0;
					currFrame[row][col] = nextByte;
					col++;
				}
			}
			leadingByte = nextByte;
			
		}
		fis.close();
		
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
		
		ArithmeticEncoder<Integer> encoder = new ArithmeticEncoder<Integer>(rangeBitWidth);
		
		FileOutputStream fos = new FileOutputStream(outputFileName);
		OutputStreamBitSink bit_sink = new OutputStreamBitSink(fos);
		
		fis = new FileInputStream(inputFileName);
		
		// Use model 0 as initial model.
		DifferentialCodingModel model = models[0];
		
		for(int i = 0; i < numFrames; i++) {
			for(int j = 0; j < 4096; j++) {
				int nextPixel = fis.read();
				int firstPixInRow = 0;
				if(j % 64 == 0)  {
					// get firstPixInRow
				}
				// Update model used
				
				// Set up next model based on symbol just encoded
				model = models[nextPixel];
			}
			
		}

		fis.close();
		fos.close();
	}
	
}
