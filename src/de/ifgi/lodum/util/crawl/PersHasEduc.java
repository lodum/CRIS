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
import de.ifgi.lodum.objects.CrisEducation;
import de.ifgi.lodum.objects.LodumObject;
import de.ifgi.lodum.util.XmlCacher;

public class PersHasEduc {
	static ConfigProvider config = new ConfigProvider();

	static void crawlEducations(){
		ArrayList<Integer> personIds = new PersonID().getAllPersonIds();
		System.out.println(personIds.size());

		for(Integer personID:personIds){
			try {
				File file = new XmlCacher().getXML(config.getProperty("crisURL")+"getrelated/Person/"+personID+"/PERS_has_EDUC", "PERS_has_EDUC/"+personID+".xml",config);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	static void convertEducations(){
		File files = new File(config.getProperty("cacheFolder")+"/PERS_has_EDUC");
		Model allEdcuationsModel = ModelFactory.createDefaultModel();
		for(File f : files.listFiles()){
			if(f.getName().endsWith("-clean.xml")){
				Integer personID=Integer.parseInt(f.getName().replace(".xml-clean.xml",""));
				//System.out.println(personID);
				CrisEducation eduObject = new CrisEducation("http://data.uni-muenster.de/context/cris/person/");
				BufferedReader in=null;
				try {
					in = new BufferedReader(new InputStreamReader(new FileInputStream(f),Charset.forName("UTF-8")));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//http://vocab.lodum.de/helper/personID
				Model m = eduObject.readAndReturnModel("cris_education", in);
				ResIterator subjects = m.listSubjectsWithProperty(null);
				Resource personResource = m.createResource("http://data.uni-muenster.de/context/cris/person/"+personID);
				Property hasEducation = m.createProperty("http://rdfs.org/resume-rdf/#term_hasEducation");
				while(subjects.hasNext()){
					Resource s = subjects.next();
					if(!s.equals(personResource)){
						m.add(m.createStatement(personResource, hasEducation, s.getURI().toString()));
					}


				}
				allEdcuationsModel.add(m);
				//m.write(System.out,"TURTLE");
			}
		}

		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
		String date = formatter.format(new Date());
		try {
			allEdcuationsModel.write(new FileOutputStream(new File("conversion/hasEducations_"+date+".ttl")),"TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		convertEducations();

	}

}
