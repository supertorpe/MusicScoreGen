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
	public int notetovalue(String nombre){
		int val=0;		
		switch(nombre.charAt(0)){
			case 'C': val=0; break;
			case 'D': val=1; break;
			case 'E': val=2; break;
			case 'F': val=3; break;
			case 'G': val=4; break;
			case 'A': val=5; break;
			case 'B': val=6; break;			
		}
		return val;
	}
	public String valuetonote(int val){
		String nn="";
		
		switch(val){
			case 0: nn="C"; break;
			case 1: nn="D"; break;
			case 2: nn="E"; break;
			case 3: nn="F"; break;
			case 4: nn="G"; break;
			case 5: nn="A"; break;
			case 6: nn="B"; break;			
		}
		return nn;
	}

    public int interval(Nota norg){
    	int inter;
    	int dist0,dist1;
    	
    	dist0=notetovalue(norg.nombre)+7*norg.octava;
    	dist1=notetovalue(this.nombre)+7*this.octava;
    	inter=dist0-dist1;
    	inter=Math.abs(inter)+1;
    	return inter;
    }
    public void addinterval(int val,int dir){
    	int dist0,dist1;
    	
    	dist0=notetovalue(this.nombre)+7*this.octava;
    	dist0=dist0+(2*dir-1)*(val-1);
    	dist1=dist0%7;
    	this.nombre=valuetonote(dist1);
    	this.octava=dist0/7;
    }	
}
