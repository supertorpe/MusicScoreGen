package com.supertorpe.musicscoregen;

public class Elemento {

	protected String duracion;
	protected double longitud;
	
	public Elemento(String duracion) {
		this.duracion = duracion;
		actualizarLongitud();
	}

	private void actualizarLongitud() {
		if (duracion == null)
			longitud = 0;
		else
			longitud += MusicUtils.calcularLongitud(duracion);
	}
	
	public String getDuracion() {
		return duracion;
	}

	public void setDuracion(String duracion) {
		this.duracion = duracion;
		actualizarLongitud();
	}
	
	public double getLongitud() {
		return longitud;
	}

}
