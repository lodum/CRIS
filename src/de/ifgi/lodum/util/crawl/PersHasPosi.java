package de.ifgi.lodum.util.crawl;



import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;




import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;


import de.ifgi.lodum.config.ConfigProvider;
import de.ifgi.lodum.objects.CrisObject;
import de.ifgi.lodum.objects.LodumObject;
import de.ifgi.lodum.util.XmlCacher;

public class PersHasPosi {
	static ConfigProvider config = new ConfigProvider();

	static void crawlPositions(){
		ArrayList<Integer> personIds = new PersonID().getAllPersonIds();
		System.out.println(personIds.size());

		for(Integer personID:personIds){
			try {
				File file = new XmlCacher().getXML(config.getProperty("crisURL")+"getrelated/Person/"+personID+"/PERS_has_POSI", "PERS_has_POSI/"+personID+".xml",config);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	static void convertPositions(){
		File files = new File(config.getProperty("cacheFolder")+"/PERS_has_POSI");
		Model allPositionsModel = ModelFactory.createDefaultModel();
		int i=0;
		for(File f : files.listFiles()){
			if(f.getName().endsWith(".xml")){
				Integer personID=Integer.parseInt(f.getName().replace(".xml",""));
				//System.out.println(personID);
				CrisObject eduObject = new CrisObject("http://data.uni-muenster.de/context/cris/person/");
				BufferedReader in=null;
				try {
					in = new BufferedReader(new InputStreamReader(new FileInputStream(f),Charset.forName("UTF-8")));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//http://vocab.lodum.de/helper/personID
				Model m = eduObject.readAndReturnModel("cris_position", in);
				ResIterator subjects = m.listSubjectsWithProperty(m.createProperty("http://rdfs.org/resume-rdf/#startDate"));
				Resource personResource = m.createResource("http://data.uni-muenster.de/context/cris/person/"+personID);
				Property hasWorkHistory = m.createProperty("http://rdfs.org/resume-rdf/#hasWorkHistory");
				i++;
				while(subjects.hasNext()){
					Resource s = subjects.next();
					if(!s.equals(personResource)){
						m.add(m.createStatement(personResource, hasWorkHistory, s));
					}


				}
				allPositionsModel.add(m);
				//m.write(System.out,"TURTLE");
			}
		}

		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
		String date = formatter.format(new Date());
		try {
			System.out.println(allPositionsModel.size());
			allPositionsModel.write(new FileOutputStream(new File("conversion/AllPersonHasPosition_"+date+".ttl")),"TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//convertPositions();
		//crawlPositions();
		convertSinglePosition("9103");
	}
	
	public static void convertSinglePosition(String personId){
		Model allPositionsModel = ModelFactory.createDefaultModel();
		CrisObject eduObject = new CrisObject("http://data.uni-muenster.de/context/cris/person/");
		Model m=null;
		try {
			m = eduObject.readAndReturnModel("cris_position", new BufferedReader(new InputStreamReader(new FileInputStream(new File(config.getProperty("cacheFolder")+"/PERS_has_POSI/"+personId+".xml")),Charset.forName("UTF-8"))));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResIterator subjects = m.listSubjectsWithProperty(m.createProperty("http://rdfs.org/resume-rdf/#startDate"));
		Resource personResource = m.createResource("http://data.uni-muenster.de/context/cris/person/"+personId);
		Property hasWorkHistory = m.createProperty("http://rdfs.org/resume-rdf/#hasWorkHistory");

		while(subjects.hasNext()){
			Resource s = subjects.next();
			if(!s.equals(personResource)){
				m.add(m.createStatement(personResource, hasWorkHistory, s));
			}


		}
		allPositionsModel.add(m);
		try {
			allPositionsModel.write(new FileOutputStream(new File("conversion/positions"+personId+".ttl")),"TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
