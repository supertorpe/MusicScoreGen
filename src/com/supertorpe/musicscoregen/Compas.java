package com.supertorpe.musicscoregen;

public class Compas {

	private String nombre;
	private double longitud;
	
	public Compas(String nombre) {
		this.nombre = nombre;
		actualizarLongitud();
	}
	
	private void actualizarLongitud() {
		String[] valores = nombre.split("/");
		double numerador = Double.valueOf(valores[0]);
		double denominador = Double.valueOf(valores[1]);
		longitud = numerador / denominador;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
		actualizarLongitud();
	}

	public double getLongitud() {
		return longitud;
	}
	
}
