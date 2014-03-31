package de.ifgi.lodum.testing;

import de.ifgi.lodum.objects.LodumObject;
import de.ifgi.lodum.objects.Provenance;

public class TestProvenance {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		LodumObject l = new LodumObject("http://data.uni-muenster.de/context/sameas/internal/publisher/");
		l.addProvenanceData(Thread.currentThread().getStackTrace()[1].getClassName());
		l.getObjectModel().write(System.out,"TTL");
		//Provenance o = new Provenance();

		//o.setProvenanceInformation("","http://data.uni-muenster.de/context/cris/project/");
		//o.getObjectModel().write(System.out,"TTL");
	}

}
