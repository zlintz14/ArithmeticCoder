package encode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import ac.ArithmeticEncoder;
import app.FreqCountIntegerSymbolModel;
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
		
		int[] histogram = new int[(256*2) + 1];
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
				int difference = nextByte - prevByte;
				currFrame[row][col] = difference;
				col++;
				if(difference < 0) {
					difference = 255 + (difference * -1);
				}
				histogram[difference]++;
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

		// Create 256 models. Model chosen depends on intensity of pixel prior to 
				// pixel being encoded.
		FreqCountIntegerSymbolModel[] models = new FreqCountIntegerSymbolModel[(256 * 2) + 1];
		
		for(int i = 0; i < models.length; i++) {
			models[i] = new FreqCountIntegerSymbolModel(pixelDifferences);
		}
		
		ArithmeticEncoder<Integer> encoder = new ArithmeticEncoder<Integer>(rangeBitWidth);
		
		FileOutputStream fos = new FileOutputStream(outputFileName);
		OutputStreamBitSink bitSink = new OutputStreamBitSink(fos);
		
		//4 bytes for number of pixels encoded
		bitSink.write(numFrames * 4096, 32);
		
		// Next byte is the width of the range registers
		bitSink.write(rangeBitWidth, 8);
		
		// Use model 0 as initial model.
		FreqCountIntegerSymbolModel model = models[0];	
		
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
						lastPixel = pixel;
					}
					
					if(lastPixel < 0) {
						lastPixel = 256 + (lastPixel * -1);
					}
					// Update model used
					model.addToCount(lastPixel);
					
					// Set up next model based on pixel just encoded
					model = models[lastPixel];
				}
				
			}
		}		
		encoder.emitMiddle(bitSink);
		bitSink.padToWord();
		fos.close();
		
		System.out.println("Done");
	}

}
