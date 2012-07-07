package com.supertorpe.musicscoregen;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Pentagrama {
	
	private static final String PARAM_RANGO = "pentagrama.rango.";
	private static final String PARAM_RANGO_EN = "stave.range.";
	private static final String PARAM_RITMOS = "bloques.ritmicos.";
	private static final String PARAM_RITMOS_EN = "rythms.blocks.";
	
	private String clave;
	private Nota[] notasDisponibles;
	private BloqueRitmico[] bloquesRitmicosDisponibles;
	private int porcentajeSilencios;
	
	public Pentagrama(String tonalidad, String clave, Properties props) throws Exception {
		this.clave = clave;
		// Leer el rango de notas
		String rango = MusicUtils.leerParametro(PARAM_RANGO + clave, PARAM_RANGO_EN + clave, props, true);
		String[] notas = rango.split(",");
		if (notas.length != 2)
			throw new Exception("El rango de notas debe estar formado por dos notas\nThe range of notes must contain two notes");
		String notaIni = notas[0].substring(0, 1);
		int octavaIni = Integer.valueOf(notas[0].substring(notas[0].length() - 1, notas[0].length()));
		String notaFin = notas[1].substring(0, 1);
		int octavaFin = Integer.valueOf(notas[1].substring(notas[1].length() - 1, notas[1].length()));
		// calcular el número de notas
		int numNotas = MusicUtils.NOTAS.size() * (octavaFin - octavaIni + 1)
		             - MusicUtils.NOTAS.indexOf(notaIni)
		             - (MusicUtils.NOTAS.size() - 1 - MusicUtils.NOTAS.indexOf(notaFin));
		notasDisponibles = new Nota[numNotas];
		int idxNota = 0;
		for (int octava = octavaIni; octava <= octavaFin; octava++) {
			int idxNotaInicio = 0;
			int idxNotaFin = MusicUtils.NOTAS.size() - 1;
			if (octava == octavaIni)
				idxNotaInicio = MusicUtils.NOTAS.indexOf(notaIni);
			if (octava == octavaFin)
				idxNotaFin = MusicUtils.NOTAS.indexOf(notaFin);
			for (int i = idxNotaInicio; i <= idxNotaFin; i++) {
				notasDisponibles[idxNota] = new Nota(MusicUtils.NOTAS.get(i), octava, null);
				idxNota++;
			}
		}
		MusicUtils.aplicarAlteraciones(tonalidad, notasDisponibles);
		// Bloques rítmicos a utilizar
		String sRitmos = MusicUtils.leerParametro(PARAM_RITMOS + clave, PARAM_RITMOS_EN + clave, props, true);
		String sListaRimtos[] = sRitmos.split(",") ;
		bloquesRitmicosDisponibles = new BloqueRitmico[sListaRimtos.length];
		for (int i = 0; i < sListaRimtos.length; i++) {
			String[] duraciones = sListaRimtos[i].split("\\+");
			bloquesRitmicosDisponibles[i] = new BloqueRitmico(duraciones);
		}
	}
	
	public List<Elemento> generarCompas(Compas compas, String tonalidad, Integer grado) throws Exception {
		List<Elemento> result = new ArrayList<Elemento>();
		// generar el ritmo
		double longitudCompas = compas.getLongitud();
		List<BloqueRitmico> bloques = new ArrayList<BloqueRitmico>();
		double longitud = 0;
		longitud = generarBloquesRitmicos(bloques, longitud, longitudCompas);
		if (longitud < longitudCompas)
			throw new Exception("Con los bloques rítmicos disponibles no se puede rellenar el compás");
		// generar la melodía
		List<Nota> notasCandidatas = MusicUtils.obtenerNotas(tonalidad, grado, notasDisponibles);
		for (BloqueRitmico bloque : bloques) {
			String[] duraciones = bloque.getDuraciones();
			for (String duracion : duraciones) {
				Nota notaCandidata = notasCandidatas.get(MusicUtils.rnd.nextInt(notasCandidatas.size()));
				Nota nota = new Nota(notaCandidata.getNombre(), notaCandidata.getOctava(), duracion);
				result.add(nota);
			}
		}
		return result;
	}
	
	private double generarBloquesRitmicos(List<BloqueRitmico> bloques, double longitudActual, double longitudObjetivo) {
		// 1. Obtenemos la lista de bloques candidatos
		// 2. Eliminamos de la lista aquellos bloques cuya longitud exceda el límite
		// 3. Mientras queden bloques candidatos y longitudActual < longitudObjetivo
		// 3.1. Elegir al azar uno de los bloques candidatos de los que quedan y añadirlo a 'bloques' incrementando la 'longitudActual'
		// 3.2. Invocar recursivamente a 'generarBloquesRitmicos'
		// 3.3. Si longitudActual < longitudObjetivo
		// 3.3.1. Eliminamos de la lista de bloques candidatos y de bloques el último elegido, decrementando 'longitudActual'

		// 1. Obtenemos la lista de bloques candidatos
		List<BloqueRitmico> bloquesCandidatos = new ArrayList<BloqueRitmico>();
		for (int i = 0; i < bloquesRitmicosDisponibles.length; i++)
			bloquesCandidatos.add(bloquesRitmicosDisponibles[i]);
		// 2. Eliminamos de la lista aquellos bloques cuya longitud exceda el límite
		int i = 0;
		while (i < bloquesCandidatos.size()) {
			if (bloquesCandidatos.get(i).getLongitud() > longitudObjetivo - longitudActual)
				bloquesCandidatos.remove(i);
			else
				i++;
		}
		// 3. Mientras queden bloques candidatos y longitudActual < longitudObjetivo
		while (longitudActual < longitudObjetivo && bloquesCandidatos.size() > 0) {
			// 3.1. Elegir al azar uno de los bloques candidatos de los que quedan y añadirlo a 'bloques' incrementando la 'longitudActual'
			int idxBloque = MusicUtils.rnd.nextInt(bloquesCandidatos.size());
			BloqueRitmico bloque = bloquesCandidatos.get(idxBloque);
			bloques.add(bloque);
			longitudActual += bloque.getLongitud();
			if (longitudActual < longitudObjetivo) {
				// 3.2. Invocar recursivamente a 'generarBloquesRitmicos'
				longitudActual = generarBloquesRitmicos(bloques, longitudActual, longitudObjetivo);
				// 3.3. Si longitudActual < longitudObjetivo
				if (longitudActual < longitudObjetivo) {
					// 3.3.1. Eliminamos de la lista de bloques candidatos y de bloques el último elegido, decrementando 'longitudActual'
					bloquesCandidatos.remove(idxBloque);
					bloques.remove(bloques.size() - 1);
					longitudActual -= bloque.getLongitud(); 
				}
			}
		}
		return longitudActual;
	}
	
	public String getClave() {
		return clave;
	}
	
	public void setClave(String clave) {
		this.clave = clave;
	}
	
	public Nota[] getNotasDisponibles() {
		return notasDisponibles;
	}
	
	public void setNotasDisponibles(Nota[] notasDisponibles) {
		this.notasDisponibles = notasDisponibles;
	}
	
	public BloqueRitmico[] getBloquesRitmicosDisponibles() {
		return bloquesRitmicosDisponibles;
	}
	
	public void setBloquesRitmicosDisponibles(BloqueRitmico[] bloquesRitmicosDisponibles) {
		this.bloquesRitmicosDisponibles = bloquesRitmicosDisponibles;
	}
	
	public int getPorcentajeSilencios() {
		return porcentajeSilencios;
	}
	
	public void setPorcentajeSilencios(int porcentajeSilencios) {
		this.porcentajeSilencios = porcentajeSilencios;
	}

}
