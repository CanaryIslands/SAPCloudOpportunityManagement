package com.eone.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileLoader {
	
	public List<String[]> getFile(String fileName){
		
		List<String[]> result = new ArrayList<String[]>();
		
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(getClass().getResource(fileName).getFile());
		
		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] lineValues = line.split(";");
				result.add(lineValues);
			}

			scanner.close();

		} catch (IOException e) {e.printStackTrace();}
		
		return result;
		
	}

}
