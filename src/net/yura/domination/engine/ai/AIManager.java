package net.yura.domination.engine.ai;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Utilizzato per integrare facilmente nuove AI nel gioco
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class AIManager {
	private static HashMap<String, AIClass> aiClasses = new HashMap<String, AIClass>();
	
	/**
	 * Utilizzato per integrare le Ai nel gioco
	 * Per far ci&ograve, instanziare una AI ed aggiungerla alla mappa.
	 * &Egrave; preferibile usare i metodi addAI o addAIs dato che
	 * fanno qualche controllo sulla validità dell'AI 
	 * 
	 */
	public static void setup(){
		autodiscoverAIs();
	}
	
	/**
	 * Esamina l'intero classpath alla ricerca di classi che implementano un'AI e le carica.
	 * 
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 */
	private static void autodiscoverAIs() {
		String classpath = System.getProperty("java.class.path");
		String[] paths = classpath.split(File.pathSeparator);
		
		List<String> candidateClassNames = new ArrayList<String>();
		for(String p : paths) {
			File file = new File(p);
			if(file.isDirectory()) {
				candidateClassNames.addAll(autodiscoverDirectory(file, file));
			} else if(p.toLowerCase().endsWith(".jar")) {
				candidateClassNames.addAll(autodiscoverJar(file));
			}
		}
		
		for(String className : candidateClassNames) {
			try {
				Class<?> cl = Class.forName(className);
				
				if(AI.class.isAssignableFrom(cl) && cl.isAnnotationPresent(Discoverable.class)) {
					System.out.println("Discovered ai " + cl);
					AIClass clazz = new AIClass((Class<? extends AI>) cl); 
					aiClasses.put(clazz.getId(), clazz);
	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Esamina una directory alla ricerca di classi candidate per essere AI.
	 * 
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 */
	private static List<String> autodiscoverDirectory(File root, File path) {
		List<String> classes = new LinkedList<String>();
		FileFilter classFilter = new FileFilter() {
			
			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory() || (arg0.getName().toLowerCase().endsWith(".class") && arg0.getName().startsWith("AI"));
			}
		};
		
		if(!path.isDirectory())
			return Collections.<String>emptyList();
		
		for(File file : path.listFiles(classFilter)) {
			if(file.isDirectory())
				classes.addAll(autodiscoverDirectory(root, file));
			else {
				String absRoot = root.getAbsolutePath();
				String absFile = file.getAbsolutePath();
				String className = absFile.substring(absRoot.length() + 1, absFile.lastIndexOf('.')).replace(File.separatorChar, '.');
				int lastDot = className.lastIndexOf('.');
				String simpleName = (lastDot != -1) ? className.substring(lastDot + 1) : className;
				if(simpleName.startsWith("AI"))
					classes.add(className);
			}
		}
		
		return classes;
	}

	/**
	 * Esamina un JAR alla ricerca di classi candidate per essere AI.
	 * 
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 */
	private static List<String> autodiscoverJar(File file) {
		JarFile jar = null;
		try {
			jar = new JarFile(file);
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.<String>emptyList();
		}
		
		Enumeration<JarEntry> entries = jar.entries();
		List<String> classes = new LinkedList<String>();
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			if(name.endsWith(".class")) {
				String className = name.substring(0, name.lastIndexOf('.')).replace(File.separatorChar, '.');
				int lastDot = className.lastIndexOf('.');
				String simpleName = (lastDot != -1) ? className.substring(lastDot + 1) : className;
				if(simpleName.startsWith("AI"))
					classes.add(className);
			}
				
		}
		
		return classes;
	}



	
	/**
	 * Viene utilizzato quando l'engine risolve l'AI a partire dall'id
	 * @param id
	 * @return
	 */
	public static Class<? extends AI> getAIClass(String id){
		return aiClasses.get(id).getAIclass();
	}
	
	
	/**
	 * Viene utilizzato dall'interfaccia grafica per visualizzare le AI disponibili
	 * @return
	 */
	public static Collection<AIClass> getAIs(){
		return aiClasses.values();
	}
	
	
	public static String getAIClassId(Class<? extends AI> clazz){
		if(clazz.getSimpleName().equals("AIHuman"))
			return "human";
		return "ai "+clazz.getSimpleName();
	}
	
	
	public static class AIClass{
		
		private String name, id;
		private Class<? extends AI> AIclass;
		
		public AIClass(Class<? extends AI> clazz) {
			this.AIclass = clazz;
			if(clazz.getSimpleName().equals("AIHuman")){
				id = "human";
				name = "Umano";
			}else{
				id = "ai "+clazz.getSimpleName().toLowerCase().substring(2);
				name = clazz.getSimpleName().substring(2);
			}
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

		public Class<? extends AI> getAIclass() {
			return AIclass;
		}
	
		
		@Override
		public String toString() {
			return name;
		}
		
		
	}
}
