package encode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import io.InputStreamBitSource;

public class EncodeVideo {

	public static void main(String[] args) throws FileNotFoundException {
		String inputFileName = System.getProperty("user.dir") + "/data/out.dat";
		String outputFileName = System.getProperty("user.dir") + "/data/compressed.dat";

		int range_bit_width = 40;

		System.out.println("Encoding text file: " + inputFileName);
		System.out.println("Output file: " + outputFileName);
		System.out.println("Range Register Bit Width: " + range_bit_width);

		int totalBytes = (int) new File(inputFileName).length();
				
//		FileInputStream fis = new FileInputStream(inputFileName);
//		InputStreamBitSource bit_source = new InputStreamBitSource(fis);
//
//		Integer[] symbols = new Integer[256];
	}
	
}
