package com.visualAnalitycs.demo;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.el.stream.Stream;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * LAS Reader
 * @author Drogon
 *
 */
@RestController
public class LASReader {

	private final static String UNIT = "ohm/m";
	private final static String UNIT_DEPT = "FEET";

	private Map<String, WellInformation> wellInformations;
	
	private List<VersionInformation> versionInformation;
	private List<Curve> curve;

	private final static int TILDE = 126;
	private final static int POUND = 35;

	private final static int V = 86;
	private final static int W = 87;
	private final static int C = 67;
	private final static int P = 80;
	private final static int O = 79;
	private final static int A = 65;

	private final static String DOT = "\\.";
	private final static String SPACE = " ";

	private long logDate;
	private List<String> curves;

	private String filename;
	private long fileDate;

	private Pattern patron = Pattern.compile("^~.*");
	private Map<Integer, String> curvesPosition = new HashMap<Integer, String>();

	private Double[][] matriz;

	private String jsonData;
	private int sizeMatriz;

	private final static int MAX_CURVE = 4;

	public void readFile() {
		jsonData ="";
		curvesPosition.clear();
		curves = new ArrayList<String>();

		wellInformations = new HashMap<String, WellInformation>();
		try {
			File f = new File("src/main/resources/LAS 30a_Revised_2010.las");
			Scanner scnr = new Scanner(f,"Cp1252").useDelimiter(System.lineSeparator());

			String tempData = null;
			while(scnr.hasNextLine()) {
				String line = "";

				if(tempData!=null && !tempData.isEmpty()) {
					line = tempData;
					tempData = null;
				}else {
					line = scnr.nextLine();
				}

				if(!line.isEmpty()) {
					if((int)line.charAt(0)==TILDE) {
						if((int)line.charAt(1)==V||(int)line.charAt(1)==W) {
							while(scnr.hasNextLine() && tempData==null) {
								String data = scnr.nextLine();
								if(data.isEmpty()) {
									tempData = null;
								} else if(data!=null && !data.isEmpty() && data.charAt(0)==TILDE) {
									tempData = data;									
								}else {
									data = data.trim();
									if(!isEspecialCharacter(data.charAt(0))) {
										System.out.println(data);
										data = data.trim();

										//First dot in a line
										String mnemonic = data.split(DOT)[0];
										data = data.substring(mnemonic.length()+1, data.length());

										String units = "";
										if(!data.startsWith(SPACE)) {
											units = data.split(SPACE)[0];
											data = data.substring(units.length(), data.length());
										}

										String[] temp = data.split(":");
										String description = temp[temp.length-1];
										data = data.substring(0, data.length()-description.length()-1);

										WellInformation wellInformation = new WellInformation(mnemonic, units, data, description);
										wellInformations.put(mnemonic, wellInformation);
									}
								}
							}
						}else if((int)line.charAt(1)==C) {
							int positionC = 0;
							while(scnr.hasNextLine() && tempData==null) {
								String data = scnr.nextLine();
								if(data.isEmpty()) {
									tempData = null;
								} else if(data!=null && !data.isEmpty() && data.charAt(0)==TILDE) {
									tempData = data;

								}else {
									data = data.trim();
									if(!isEspecialCharacter(data.charAt(0))) {
										data = data.trim();

										//First dot in a line
										String mnemonic = data.split(DOT)[0];
										data = data.substring(mnemonic.length()+1, data.length());

										String units = "";
										if(!data.startsWith(SPACE)) {
											units = data.split(SPACE)[0];
											if(units.equalsIgnoreCase(UNIT)||units.equalsIgnoreCase(UNIT_DEPT)) {
												curves.add(mnemonic.trim());
												curvesPosition.put(positionC, mnemonic.trim());
											}
										}
									}else {
										positionC--;
									}
									positionC++;
								}
							}
						}
						else if((int)line.charAt(1)==A) {
							System.out.println(curvesPosition);

							Double start = Double.parseDouble(wellInformations.get("STRT").getDescription().trim());
							Double stop = Double.parseDouble(wellInformations.get("STOP").getDescription().trim());
							Double step = Double.parseDouble(wellInformations.get("STEP").getDescription().trim());

							sizeMatriz = (int) (((stop-start)/step)+1);
							System.out.println("sizeMatriz: "+sizeMatriz);
							matriz = new Double[sizeMatriz][MAX_CURVE-1];
							ArrayList<Double> porcentajes = new ArrayList<Double>();
							int indiceMatriz = 0;

							jsonData = String.join(",", curves.subList(0, MAX_CURVE));
							while(scnr.hasNextLine()){
								String data = scnr.nextLine().trim();
								String values[] = data.split(" +");
								String lineA = ""+"\n";
								int cont = 0;
								int indiceJ = 0;
								Double sumaValores = 0.0;
								for (int i = 0; cont < MAX_CURVE; i++) {
									if(curvesPosition.get(i)!=null) {
										cont++;
										String finalValue = values[i];
										if(i==0) {
											lineA = lineA + "-"+getValue(finalValue);
										}else {
											lineA = lineA +","+getValue(finalValue);

											matriz[indiceMatriz][indiceJ]=getValue(finalValue);
											indiceJ++;
											sumaValores+=getValue(finalValue);
										}										
									}
								}
								indiceMatriz++;

								String[] porcentajesIn = lineA.split(",");

								for (int i = 0; i < porcentajesIn.length; i++) {
									if(i==0) {

									}else {
										if(porcentajesIn[i]!=null) {
											Double porcentaje = 0.0;
											if(sumaValores==0.0) {
												porcentaje = 0.0;
											}else {
												Double valor = Double.valueOf(porcentajesIn[i]);
												porcentaje = valor*1/sumaValores;
												porcentaje = ((double) Math.round(porcentaje*10d)/10d);
											}
											porcentajes.add(porcentaje);
										}
									}									
								}

								jsonData = jsonData + lineA;								
							}
							getMaximumOfEveryColumn(porcentajes);
						}
					}
				}
			}
		}catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}


	public void getMaximumOfEveryColumn (ArrayList<Double> porcentajes){
		try {

			ArrayList<Double> highestList = new ArrayList<Double>();
			for ( int col = 0; col < MAX_CURVE-1; col++){
				double highest = Integer.MIN_VALUE;
				for ( int row = 0; row < matriz.length; row++) {
					if ( col < matriz[row].length && matriz[row][col] > highest) {
						highest = matriz[row][col];
					}
				}        
				highestList.add(highest);
				System.out.println( "Highest number in column " + col + " = " + highest);
			}

			String [][] matrizPlus = new String[sizeMatriz][MAX_CURVE-1];

			int indice = 0;
			int indiceHighestList = 0;
			for ( int row = 0; row < matriz.length; row++){
				for ( int col = 0; col < MAX_CURVE-1; col++) {

					double value = matriz[row][col];
					//System.out.println("Value: "+value);
					value = value*1/highestList.get(indiceHighestList);
					value = ((double) Math.round(value*1000d)/1000d);
					indiceHighestList++;
					matrizPlus[row][col]= value + "-" + porcentajes.get(indice);
					if(row>=6300 && row <=6800) {
						System.out.print("*"+matrizPlus[row][col]+"*,");
					}	
					indice++;

				}
				if(row>=6300 && row <=6800) {
					System.out.println("");
				}
				
				indiceHighestList = 0;
			}
		}catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getAZIM")
	@ResponseBody
	public String greeting() {
		readFile();
		//System.out.println(jsonData);
		return jsonData;
	}

	public void parseData(BufferedReader buffer) throws Exception{
		String data = "";
		while((data=buffer.readLine())!=null && !data.isEmpty() && !isEspecialCharacter(data.charAt(0))) {
			System.out.println(data);
			data = data.trim();

			//First dot in a line
			String mnemonic = data.split(DOT)[0];
			data = data.substring(mnemonic.length()+1, data.length());

			String units = "";
			if(!data.startsWith(SPACE)) {
				units = data.split(SPACE)[0];
				data = data.substring(units.length(), data.length());
			}

			String[] temp = data.split(":");
			String description = temp[temp.length-1];
			data = data.substring(0, data.length()-description.length()-1);

			WellInformation wellInformation = new WellInformation(mnemonic, units, data, description);
			wellInformations.put(mnemonic, wellInformation);
		}
	}



	public void parseData(String data) throws Exception{
		if(data!=null && !data.isEmpty() && !isEspecialCharacter(data.charAt(0))) {
			System.out.println(data);
			data = data.trim();

			//First dot in a line
			String mnemonic = data.split(DOT)[0];
			data = data.substring(mnemonic.length()+1, data.length());

			String units = "";
			if(!data.startsWith(SPACE)) {
				units = data.split(SPACE)[0];
				data = data.substring(units.length(), data.length());
			}

			String[] temp = data.split(":");
			String description = temp[temp.length-1];
			data = data.substring(0, data.length()-description.length()-1);

			WellInformation wellInformation = new WellInformation(mnemonic, units, data, description);
			wellInformations.put(mnemonic, wellInformation);
		}
	}

	public void createPie3D() {

	}

	public boolean isEspecialCharacter(char value) {
		if(value==TILDE||value==POUND||value==' ') {
			return true; 
		}
		return false;
	}

	public double getValue(String nValue) {
		double value = Double.parseDouble(nValue);
		if(value==-999.25) {
			return 0;
		}else {
			return Math.abs(value);
		}
	}



	/**
	 * @return the jsonData
	 */
	public String getJsonData() {
		return jsonData;
	}

	/**
	 * @param jsonData the jsonData to set
	 */
	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}
}