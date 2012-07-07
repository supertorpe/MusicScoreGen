package com.supertorpe.musicscoregen;

public class BloqueRitmico {

	private String[] duraciones;
	private double longitud;
	
	public BloqueRitmico(String[] duraciones) {
		this.duraciones = duraciones;
		actualizarLongitud();
	}
	
	private void actualizarLongitud() {
		longitud = 0;
		for (String duracion : duraciones)
			longitud += MusicUtils.calcularLongitud(duracion);
	}

	public String[] getDuraciones() {
		return duraciones;
	}

	public void setDuraciones(String[] duraciones) {
		this.duraciones = duraciones;
		actualizarLongitud();
	}
	
	public double getLongitud() {
		return longitud;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < duraciones.length; i++) {
			result.append(duraciones[i]);
			if (i < duraciones.length - 1)
				result.append("+");
		}
		return result.toString();
	}

}
