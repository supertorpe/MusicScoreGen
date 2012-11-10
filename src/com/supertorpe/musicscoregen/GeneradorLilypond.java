package com.supertorpe.musicscoregen;

import java.io.PrintWriter;
import java.util.List;

public class GeneradorLilypond implements Generador {
	
	private static final String[] NOMBRE_PENTAGRAMAS = { "staffA", "staffB", "staffC", "staffD", "staffE", "staffF", "staffG" };
	private static final String NEWLINE = System.getProperty("line.separator");

	private static String mapearNota(String nota) {
		if (nota.length() > 1) {
			if (nota.charAt(1) == '#')
				nota = nota.charAt(0) + "is";
			else
				nota = nota.charAt(0) + "es";
		}
		return nota.toLowerCase();
	}
	
	private static String mapearOctava(int octava) {
		String result = "";
		if (octava == 3)
			return result;
		else if (octava < 3) {
			int numComas = 3 - octava;
			for (int i = 0; i < numComas; i++)
				result += ",";
		} else {
			int numComas = octava - 3;
			for (int i = 0; i < numComas; i++)
				result += "'";
		}
		return result;
	}
	
	@Override
	public void generar(List<Elemento>[][] score, String tonalidad, String compas, Pentagrama[] pentagramas,PrintWriter writer) {
		/*
		for (int j = 0; j < score.length; j++) {
			for (int i = 0; i < score[j].length; i++) {
				System.out.println("score[" + j + "][" + i + "]=" + score[j][i]);
			}
		}
		*/
		StringBuilder[] textoPentagramas = new StringBuilder[score.length];
		for (int i = 0; i < score.length; i++) {
			textoPentagramas[i] = new StringBuilder();
			// staffA = {
			textoPentagramas[i].append(NOMBRE_PENTAGRAMAS[i]).append(" = {").append(NEWLINE);
			// \clef treble
			if ("G".equals(pentagramas[i].getClave()))
				textoPentagramas[i].append("\t\\clef treble").append(NEWLINE);
			else if ("F".equals(pentagramas[i].getClave()))
				textoPentagramas[i].append("\t\\clef bass").append(NEWLINE);
			// \key c \major
			String key = mapearNota(tonalidad);
			textoPentagramas[i].append("\t\\key ").append(key).append(" \\major").append(NEWLINE);
			// \time 3/4
			textoPentagramas[i].append("\t\\time ").append(compas).append(NEWLINE);
			for (int j = 0; j < score[i].length; j++) {
				// c'4 d'4 e'4 f'4 g'4 a'4 b'4 |
				List<Elemento> elementos = score[i][j];
				textoPentagramas[i].append("\t");
				for (Elemento elemento : elementos) {
					if (elemento instanceof Silencio) {
						textoPentagramas[i].append("R").append(elemento.getDuracion()).append(" ");
					} else if (elemento instanceof Nota) {
						Nota nota = (Nota)elemento;
						textoPentagramas[i].append(mapearNota(nota.getNombre())).append(mapearOctava(nota.getOctava())).append(nota.getDuracion()).append(" ");
					}
				}
				textoPentagramas[i].append("|").append(NEWLINE);
			}
			// \bar "|."
			textoPentagramas[i].append("\t\\bar \"|.\"").append(NEWLINE);
			// }
			textoPentagramas[i].append("}").append(NEWLINE).append(NEWLINE);
		}
		for (StringBuilder textoPentagrama : textoPentagramas) {
			writer.println(textoPentagrama.toString());
		}
		// \score {
		writer.println("\\score {");
		// \new TheStaff <<
		writer.println("\t\\new TheStaff <<");
		for (int i = 0; i < score.length; i++) {
			String staffName = NOMBRE_PENTAGRAMAS[i]; 
		    // \new Staff = "staffA" \staffA
			writer.println("\t\t\\new Staff = \"" + staffName + "\" \\" + staffName);
		}
		// >>
		writer.println("\t>>");
		// \layout { }
		writer.println("\t\\layout { }");
		writer.println("\t\\midi { }");
		// }
		writer.println("}");
	}

}
