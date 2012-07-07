package com.supertorpe.musicscoregen;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MusicScoreGen {

	private static final String PARAM_FICHERO = "fichero";
	private static final String PARAM_FICHERO_EN = "file";
	private static final String PARAM_COMPAS = "compas";
	private static final String PARAM_COMPAS_EN = "time";
	private static final String PARAM_TONALIDAD = "tonalidad";
	private static final String PARAM_TONALIDAD_EN = "key";
	private static final String PARAM_COMPASES = "compases";
	private static final String PARAM_COMPASES_EN = "measures";
	private static final String PARAM_PENTAGRAMAS = "pentagramas";
	private static final String PARAM_PENTAGRAMAS_EN = "staves";
	private static final String PARAM_ACORDES = "progresiones.acordes";
	private static final String PARAM_ACORDES_EN = "chords.progressions";
	
	protected static Properties prop; 
	protected static String fichero;
	protected static Compas compas;
	protected static String tonalidad;
	protected static int compases;
	protected static Pentagrama[] pentagramas;

	protected static Map<Integer, List<Integer>> progresiones;
	protected static Map<Integer, List<Integer>> progresionesInversas;
	
	protected static List<Elemento>[][] score;
	
	public static void main(String[] args) {
		try {
			cargarConfiguracion(args);
			ejecutar();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}		
	}

	protected static void cargarConfiguracion(String[] args) throws Exception {
		String cfgFile = "musicscoregen.properties";
		// Si no se encuentra el fichero, buscarlo en el classpath
		if (!FileUtil.esFicheroLectura(cfgFile)) {
			String filename = FileUtil.searchFile(cfgFile);
			if (filename == null || !FileUtil.esFicheroLectura(filename))
				throw new Exception("No se puede leer el fichero " + cfgFile);
			cfgFile = filename;
		}
		prop = new Properties();
		try {
			prop.load(new BufferedInputStream(new FileInputStream(cfgFile)));
		} catch (Exception ex) {
			throw new Exception("No se ha podido cargar el fichero de propiedades " + cfgFile);
		}
		// Leer la configuración, dando prioridad a los parámetros de la JVM
		fichero = MusicUtils.leerParametro(PARAM_FICHERO, PARAM_FICHERO_EN, prop, true);
		String sCompas = MusicUtils.leerParametro(PARAM_COMPAS, PARAM_COMPAS_EN, prop, true);
		compas = new Compas(sCompas);
		tonalidad = MusicUtils.leerParametro(PARAM_TONALIDAD, PARAM_TONALIDAD_EN, prop, true);
		String sCompases = MusicUtils.leerParametro(PARAM_COMPASES, PARAM_COMPASES_EN, prop, true);
		compases = Integer.valueOf(sCompases);
		String sPentagramas = MusicUtils.leerParametro(PARAM_PENTAGRAMAS, PARAM_PENTAGRAMAS_EN, prop, true);
		String[] sListaPentagramas = sPentagramas.split(",");
		pentagramas = new Pentagrama[sListaPentagramas.length];
		for (int i = 0; i < sListaPentagramas.length; i++) {
			if (!"G".equals(sListaPentagramas[i]) && !"F".equals(sListaPentagramas[i]))
				throw new Exception("En esta versión sólo se soportan las claves de SOL y FA");
			pentagramas[i] = new Pentagrama(tonalidad, sListaPentagramas[i], prop);
		}
		progresiones = new HashMap<Integer, List<Integer>>();
		progresionesInversas = new HashMap<Integer, List<Integer>>();
		String sProgresiones = MusicUtils.leerParametro(PARAM_ACORDES, PARAM_ACORDES_EN, prop, true);
		String[] sListaProgresiones = sProgresiones.split(",");
		for (int i = 0; i < sListaProgresiones.length; i++) {
			String[] sValores = sListaProgresiones[i].split(":");
			String sOrigen = sValores[0];
			String[] sDestinos = sValores[1].split("-");
			Integer key = Integer.valueOf(sOrigen);
			List<Integer> value = new ArrayList<Integer>();
			for (int j = 0; j < sDestinos.length; j++) {
				Integer val = Integer.valueOf(sDestinos[j]); 
				value.add(val);
				List<Integer> inversa = progresionesInversas.get(val); 
				if (inversa == null) {
					inversa = new ArrayList<Integer>();
					inversa.add(key);
					progresionesInversas.put(val, inversa);
				} else if (inversa.indexOf(key) < 0) {
					inversa.add(key);
				}
			}
			progresiones.put(key, value);
		}
	}
	
	private static void ejecutar() throws Exception {
		// se va a crear un tablero: las filas son los pentagramas y las columnas son los compases
		// inicializamos el tablero
		score = (List<Elemento>[][])new List[pentagramas.length][compases];
		// generamos los acordes, terminando en el grado I
		Integer[] acordes = generarAcordes();
		// generamos las partituras compás a compás
		for (int i = 0; i < compases; i++) {
			for (int j = 0; j < pentagramas.length; j++) {
				score[j][i] = pentagramas[j].generarCompas(compas, tonalidad, acordes[i]);
			}
		}
		// generar el fichero de salida
		PrintWriter printer = null;
		try {
			printer = new PrintWriter(new FileWriter(fichero));
			Generador generador = new GeneradorLilypond();
			generador.generar(score, tonalidad, compas.getNombre(), pentagramas, printer);
			printer.flush();
		} finally {
			if (printer != null)
				printer.close();
		}
	}
	
	private static Integer[] generarAcordes() {
		Integer[] result = new Integer[compases];
		result[compases - 1] = 1;
		for (int i = compases - 2; i >= 0; i--) {
			List<Integer> candidatos = progresionesInversas.get(result[i + 1]);
			result[i] = candidatos.get(MusicUtils.rnd.nextInt(candidatos.size()));
		}
		return result;
	}

	public static Pentagrama[] getPentagramas() {
		return pentagramas;
	}
}
