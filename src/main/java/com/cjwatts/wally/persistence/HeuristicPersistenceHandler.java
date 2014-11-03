package com.cjwatts.wally.persistence;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.cjwatts.wally.training.Heuristic;

public class HeuristicPersistenceHandler {
	private static final String dbPath = "heuristics/";
	private static JAXBContext jaxb = null;
	
	static {
		String packageName = HeuristicPersistenceHandler.class.getPackage().getName();
		try {
			jaxb = JAXBContext.newInstance(Class.forName(packageName + ".StorableHeuristic"));
		} catch (JAXBException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	private final Heuristic heuristic;
	private final File file;
	
	public HeuristicPersistenceHandler(Heuristic heuristic) {
		this.heuristic = heuristic;
		
		file = new File(dbPath + heuristic.toString() + ".xml");
	}
	
	public void load() {
		try {
			Unmarshaller u = jaxb.createUnmarshaller();
			StorableHeuristic sh = (StorableHeuristic) u.unmarshal(file);
			
			sh.restoreTo(heuristic);
		} catch (JAXBException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	public void save() {
		try {
			StorableHeuristic sh = new StorableHeuristic(heuristic);
			
			Marshaller m = jaxb.createMarshaller();
			m.marshal(sh, file);
		} catch (JAXBException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
}
