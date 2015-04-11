package com.cjwatts.wally.persistence;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public abstract class XMLPersistenceHandler<I, S extends Storable<I>>
		implements PersistenceHandler<I> {
	
	private static JAXBContext jaxb = null;
	
	static {
		String packageName = XMLPersistenceHandler.class.getPackage().getName();
		
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
	protected I instance;
	protected File file;
	
	public XMLPersistenceHandler(String dbPath) {
		if (!dbPath.endsWith("/")) dbPath += "/";
		this.dbPath = dbPath;
	}
	
	public XMLPersistenceHandler(String dbPath, I instance) {
		this(dbPath);
		setInstance(instance);
	}
	
	@SuppressWarnings("unchecked")
	public boolean load() {
		if (instance == null) return false;
		
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
		if (instance == null) return false;
		
		try {
			S s = createStore();
			
			synchronized (instance) {
				s.store(instance);
			}
			
			Marshaller m = jaxb.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			synchronized (file) {
				file.getParentFile().mkdirs();
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
	
	public void setInstance(I instance) {
		this.instance = instance;
		file = new File(dbPath + instance.toString() + ".xml");
	}
	
}
