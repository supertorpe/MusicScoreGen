package com.supertorpe.musicscoregen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class MusicUtils {

	public static final List<String> NOTAS = Arrays.asList( "C", "D", "E", "F", "G", "A", "B" );
	
	public static Random rnd = new Random();
	
	// Obtiene las alteraciones de una tonalidad mayor
	private static Map<String, List<String>> alteraciones;
	
	static {
		alteraciones = new HashMap<String, List<String>>();
		// C
		alteraciones.put("C", null);
		// G
		alteraciones.put("G", Arrays.asList(new String[] {"F"}));
		// D
		alteraciones.put("D", Arrays.asList(new String[] {"F", "C"}));
		// A
		alteraciones.put("A", Arrays.asList(new String[] {"F", "C", "G"}));
		// E
		alteraciones.put("E", Arrays.asList(new String[] {"F", "C", "G", "D"}));
		// B
		alteraciones.put("B", Arrays.asList(new String[] {"F", "C", "G", "D", "A"}));
		// F#
		alteraciones.put("F#", Arrays.asList(new String[] {"F", "C", "G", "D", "A", "E"}));
		// Gb
		alteraciones.put("Gb", Arrays.asList(new String[] {"B", "E", "A", "D", "G", "C"}));
		// Db
		alteraciones.put("Db", Arrays.asList(new String[] {"B", "E", "A", "D", "G"}));
		// Ab
		alteraciones.put("Ab", Arrays.asList(new String[] {"B", "E", "A", "D"}));
		// Eb
		alteraciones.put("Eb", Arrays.asList(new String[] {"B", "E", "A"}));
		// Bb
		alteraciones.put("Bb", Arrays.asList(new String[] {"B", "E"}));
		// F
		alteraciones.put("F", Arrays.asList(new String[] {"B"}));
	}
	
	public static void aplicarAlteraciones(String tonalidad, Nota[] notas) {
		String alteracion;
		if ("C".equals(tonalidad))
			return;
		else if ("F".equals(tonalidad) || tonalidad.charAt(tonalidad.length() - 1) == 'b')
			alteracion = "b";
		else
			alteracion = "#";
		List<String> notasAlteradas = alteraciones.get(tonalidad);
		for (Nota nota : notas) {
			if (notasAlteradas.indexOf(nota.getNombre()) != -1)
				nota.setNombre(nota.getNombre() + alteracion);
		}
	}
	
	// Obtiene una lista de notas candidatas que pertenezcan al grado de la tonalidad y esté en la lista de disponibles
	public static List<Nota> obtenerNotas(String tonalidad, Integer grado, Nota[] notasDisponibles) {
		List<Nota> result = new ArrayList<Nota>();
		List<String> acorde = new ArrayList<String>();
		// 1. Obtener las notas del acorde sobre el grado indicado
		// añadir la fundamental
		int idx = NOTAS.indexOf(tonalidad.substring(0, 1));
		idx += grado - 1;
		if (idx >= NOTAS.size())
			idx -= NOTAS.size();
		acorde.add(NOTAS.get(idx));
		// añadir la tercera
		idx += 2;
		if (idx >= NOTAS.size())
			idx -= NOTAS.size();
		acorde.add(NOTAS.get(idx));
		// añadir la quinta
		idx += 2;
		if (idx >= NOTAS.size())
			idx -= NOTAS.size();
		acorde.add(NOTAS.get(idx));
		// 2. Añadir al resultado las notas disponibles que pertenezcan al acorde
		for (Nota nota : notasDisponibles) {
			if (acorde.indexOf(nota.getNombre().substring(0, 1)) > -1) {
				result.add(nota);
			}
		}
		return result;
	}
	
	// Calcula la longitud en tiempo de una figura, según su duración 
	public static double calcularLongitud(String duracion) {
		double result;
		boolean puntillo = ('.' == duracion.charAt(duracion.length() - 1));
		int dur;
		if (puntillo)
			dur = Integer.valueOf(duracion.substring(0, duracion.length() - 1));
		else 
			dur = Integer.valueOf(duracion);
		result = 1.0d / dur;
		if (puntillo)
			result += result / 2;
		return result;
	}
	
	public static String leerParametro(String paramName, String paramNameEn, Properties properties, boolean required) throws Exception {
		String result = System.getProperty(paramName);
		if (esCadenaVacia(result))
			result = System.getProperty(paramNameEn);
		if (esCadenaVacia(result))
			result = properties.getProperty(paramName);
		if (esCadenaVacia(result))
			result = properties.getProperty(paramNameEn);
		if (esCadenaVacia(result) && required)
			throw new Exception("No está configurado el parámetro " + paramName);
		return result;
	}

	public static boolean esCadenaVacia(String cadena) {
		return (cadena == null || cadena.trim().length() == 0);
	}	
	
	
}
