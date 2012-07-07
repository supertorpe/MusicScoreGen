package com.supertorpe.musicscoregen;

import java.io.File;

public class FileUtil
{

  public static String searchFile(String filename)
  {
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    java.net.URL url = cl.getResource(filename);
    if (url == null)
      return null;
    return url.getFile();
  }

  public static boolean existeDirectorio(String path)
  {
    File f = new File(path);
  	return (f.exists() && f.isDirectory());
  }

  public static void crearDirectorio(String path)
  {
    File f = new File(path);
    if (!f.exists())
      f.mkdirs();
  }

  public static boolean esDirectorioLectura(String path)
  {
  	File f = new File(path);
  	return (f.exists() && f.isDirectory() && f.canRead());
  }

  public static boolean esDirectorioEscritura(String path)
  {
  	File f = new File(path);
  	return (f.exists() && f.isDirectory() && f.canWrite());
  }

  public static boolean existeFichero(String path)
  {
    File f = new File(path);
  	return (f.exists() && f.isFile());
  }

  public static boolean esFicheroLectura(String path)
  {
  	File f = new File(path);
  	return (f.exists() && f.isFile() && f.canRead());
  }

  public static boolean esFicheroEscritura(String path)
  {
  	File f = new File(path);
  	return (f.exists() && f.isFile() && f.canWrite());
  }

  public static void renombrarFichero(String oldPath, String newPath)
  {
    File oldFile = new File(oldPath);
    File newFile = new File(newPath);
    oldFile.renameTo(newFile);
  }
  
  public static long fecha(String path)
  {
	  File f = new File(path);
	  return f.lastModified();
  }

}