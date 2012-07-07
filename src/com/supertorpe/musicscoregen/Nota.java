package com.supertorpe.musicscoregen;

public class Nota extends Elemento {

	private String nombre;
	private int octava;
	
	public Nota(String nombre, int octava, String duracion) {
		super(duracion);
		this.nombre = nombre;
		this.octava = octava;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getOctava() {
		return octava;
	}
	public void setOctava(int octava) {
		this.octava = octava;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(nombre).append(octava);
		if (duracion != null)
			result.append("(").append(duracion).append(")");
		return result.toString();
	}
}
