package com.cjwatts.wally.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.xmlbeans.impl.util.Base64;

import com.cjwatts.wally.training.Heuristic;
import com.cjwatts.wally.training.TrainingAlgorithm;

@XmlRootElement(name = "heuristic")
public class StorableHeuristic implements Storable<Heuristic> {
	public StorableWeightingsMap weightings;
	public StorableAlgorithm algorithm;
	
	public StorableHeuristic() {}
	
	@Override
	public void store(Heuristic h) {
		weightings = new StorableWeightingsMap(h.getWeightings());
		
		TrainingAlgorithm a = h.getAlgorithm();
		if (a != null) {
			algorithm = new StorableAlgorithm();
			algorithm.name = a.getClass().getName();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(a);
				oos.close();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
			algorithm.data = new String(Base64.encode(baos.toByteArray()));
		}
	}
	
	@Override
	public void restoreTo(Heuristic h) {
		for (StorableWeighting w : weightings.weighting) {
			h.setWeighting(w.component, w.weight, false);
		}
		
		if (algorithm != null) {
			byte[] data = Base64.decode(algorithm.data.getBytes());
			
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois;
			Object o = null;
			try {
				ois = new ObjectInputStream(bais);
				o = ois.readObject();
				ois.close();
			} catch (IOException | ClassNotFoundException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
			h.setAlgorithm((TrainingAlgorithm) o, false);
		}
	}
}