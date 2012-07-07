package com.supertorpe.musicscoregen;

import java.io.PrintWriter;
import java.util.List;

public interface Generador {
	
	public void generar(List<Elemento>[][] score, String tonalidad, String compas, Pentagrama[] pentagramas, PrintWriter writer);

}
