package com.cjwatts.wally.persistence;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public abstract class PersistenceHandler<I, S extends Storable<I>> {
	
	private static JAXBContext jaxb = null;
	
	static {
		String packageName = PersistenceHandler.class.getPackage().getName();
		
		try {
			jaxb = JAXBContext.newInstance(
					Class.forName(packageName + ".StorableHeuristic"),
					Class.forName(packageName + ".StorableSubject"));
		} catch (JAXBException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	protected final String dbPath;
	protected final I instance;
	protected final File file;
	
	public PersistenceHandler(String dbPath, I instance) {
		if (!dbPath.endsWith("/")) dbPath += "/";
		this.dbPath = dbPath;
		this.instance = instance;
		
		file = new File(dbPath + instance.toString() + ".xml");
	}
	
	@SuppressWarnings("unchecked")
	public boolean load() {
		try {
			Unmarshaller u = jaxb.createUnmarshaller();
			
			S s;
			
			synchronized (file) {
				s = (S) u.unmarshal(file);
			}
			
			synchronized (instance) {
				s.restoreTo(instance);
			}
			
			return true;
		} catch (ClassCastException | JAXBException ex) {
			return false;
		}
	}

	public boolean save() {
		try {
			S s = createStore();
			
			synchronized (instance) {
				s.store(instance);
			}
			
			Marshaller m = jaxb.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			synchronized (file) {
				m.marshal(s, file);
			}
			
			return true;
		} catch (JAXBException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Create an instance of the store to use
	 * @return
	 */
	protected abstract S createStore();
	
}
