package de.ifgi.lodum.execute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import de.ifgi.lodum.config.*;
import de.ifgi.lodum.objects.CSA;
import de.ifgi.lodum.objects.CrisCard;
import de.ifgi.lodum.objects.CrisObject;
import de.ifgi.lodum.objects.CrisOrganization;
import de.ifgi.lodum.objects.CrisPerson;
import de.ifgi.lodum.objects.CrisPosition;
import de.ifgi.lodum.objects.CrisPrice;
import de.ifgi.lodum.objects.CrisProject;
import de.ifgi.lodum.objects.CrisPublication;
import de.ifgi.lodum.objects.LodumObject;
import de.ifgi.lodum.util.XmlCacher;
import de.ifgi.lodum.util.crawl.PersHasPosi;
import de.ifgi.lodum.util.crawl.PersonID;

public class StartCRISConversion {

	/**
	 * @author jones
	 * 
	 */
	static String basisURL="https://www.uni-muenster.de/forschungaz/ws/public/infoobject/";
	static ConfigProvider config = new ConfigProvider();
	
	//Change it to a global variable: http://data.uni-muenster.de/sparql
		
	public static void main(String[] args) {
		
		GlobalSettings.setEndpoint("http://data.uni-muenster.de:8080/openrdf-sesame/repositories/lodumhbz");
		//GlobalSettings.setEndpoint("http://data.uni-muenster.de/sparql");

//		System.out.println("Creating Organizations...");
//		loadOrganisations();
//		
//		System.out.println("Creating Cards (Add to ORGA)... ");
//		addCardsToOrgas();
//		addCardIdsToOrgas();
		
		
//		System.out.println("Converting CRIS Person...");
//		new CrisPerson("http://data.uni-muenster.de/context/cris/person/").crawlConvertCommit();
//	
//		System.out.println("Converting CRIS Position...");
//		new CrisPosition("http://data.uni-muenster.de/context/cris/position/").convert();
//	
//		System.out.println("Converting CRIS Project...");
//		new CrisProject("http://data.uni-muenster.de/context/cris/project/").crawlConvertCommit();
//		createPersonProjectRelation();
//
//		System.out.println("Converting CRIS Publication...");
//		new CrisPublication("http://data.uni-muenster.de/context/cris/publication/").crawlConvertCommit();
//		createPersonPublicationRelation();
//		
//		System.out.println("Creating Person-Organization Relation...");
//		createPersonOrganizationRelation();
//		
//		System.out.println("Creating Person-Award Relation...");
//		createPersonAwardRelation();	
//		
//		System.out.println("Creating Person-Depiction Relation...");
//		createPersonFoafDepiction();
//		
//		System.out.println("Creating Person-CSA Relation...");
//		createCrisPersonCsaPerson();

		System.out.println("Creating Prizes...");
		createPrizes();
		
		System.out.println("Creating Cards (Add to ORGA)... ");
		addCardsToOrgas();
		addCardIdsToOrgas();
		
		System.out.println("Convert CSA...");
		convertCsa();
		
		System.out.println("Linking Organizations");
		linkPersonsOrganizations();
		
		convertPositions();
		
		testEducation();

		
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

	
	private static void addCardIdsToOrgas(){
		CrisCard crisCard= new CrisCard("http://data.uni-muenster.de/context/cris/card/");
		Property pro = crisCard.getStoreModel().getProperty("http://vocab.lodum.de/helper/cardID");
		NodeIterator ndIt = crisCard.getStoreModel().listObjectsOfProperty(pro);
		int i=0;
		while(ndIt.hasNext()){
			RDFNode nd = ndIt.nextNode();
			String cardId = nd.toString();
			CrisOrganization crisOrga = new CrisOrganization (config.getProperty("crisOrgaNS"));
			try{
				URL url = new URL(basisURL+"getrelated/Card/"+cardId+"/CARD_has_ORGA");
				URLConnection c = url.openConnection();
				InputStreamReader ins = new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8"));
				BufferedReader in = new BufferedReader(ins);
				crisOrga.readIntoModel("cris_organisation",in);
				Property orgaProp = crisCard.getStoreModel().getProperty("http://vocab.lodum.de/helper/orgaID");
				//get all ids of organizations from the triple store
				NodeIterator orgIt = crisOrga.getObjectModel().listObjectsOfProperty(orgaProp);
				while(orgIt.hasNext()){
					i++;
					String orgaId = orgIt.nextNode().toString();
					Statement st = ResourceFactory.createStatement(ResourceFactory.createResource(config.getProperty("crisOrgaNS")+orgaId),crisOrga.getObjectModel().createProperty("http://vocab.lodum.de/helper/card"),crisOrga.getObjectModel().createResource("http://data.uni-muenster.de/context/cris/card/"+cardId) );
					crisOrga.getObjectModel().add(st);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			crisOrga.commitModelToStore();

		}
		System.out.println("Connected "+i+" Organizations with cards");
	}
	
	private static void addCardsToOrgas(){
		CrisOrganization crisOrga = new CrisOrganization(config.getProperty("crisOrgaNS"));
		Property pro = crisOrga.getStoreModel().getProperty("http://vocab.lodum.de/helper/orgaID");
		NodeIterator ndIt = crisOrga.getStoreModel().listObjectsOfProperty(pro);
		Property cardProp = crisOrga.getStoreModel().getProperty("http://vocab.lodum.de/helper/cardID");
		while(ndIt.hasNext()){
			CrisCard crisCard= new CrisCard("http://data.uni-muenster.de/context/cris/card/");
			String orgaId = ndIt.next().toString();
			try{
				URL url = new URL(basisURL+"getrelated/Organisation/"+orgaId+"/CARD_has_ORGA");
				URLConnection c = url.openConnection();
				InputStreamReader ins = new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8"));
				BufferedReader in = new BufferedReader(ins);
				crisCard.readIntoModel("cris_card",in);
				NodeIterator cardIt = crisCard.getObjectModel().listObjectsOfProperty(cardProp);
				while(cardIt.hasNext()){
					String cardId=cardIt.next().asLiteral().toString();
					Statement st = ResourceFactory.createStatement(ResourceFactory.createResource(config.getProperty("crisOrgaNS")+orgaId),crisOrga.getObjectModel().createProperty("http://vocab.lodum.de/helper/card"),crisOrga.getObjectModel().createResource("http://data.uni-muenster.de/context/cris/card/"+cardId) );
					crisOrga.getObjectModel().add(st);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			//crisOrga.getObjectModel().write(System.out);
			crisOrga.commitModelToStore();
		}

	}

	
	
	private static void createCrisPersonCsaPerson(){
		LodumObject foafDepiction= new LodumObject("http://data.uni-muenster.de/context/crisperson-csaperson/");
		try{
			Query query = QueryFactory.create(
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+ 
					"prefix foaf: <http://xmlns.com/foaf/0.1/> "+
					"prefix res: <http://www.medsci.ox.ac.uk/vocab/researchers/0.1/>"+
					"prefix owl: <http://www.w3.org/2002/07/owl#>"+
					"CONSTRUCT"+
					"{?a owl:sameAs ?b.}"+
					"WHERE {"+
					"?a foaf:depiction ?depiction."+
					"?b foaf:depiction ?depiction."+
					"FILTER (!regex(str(?b), \"http://data.uni-muenster.de/context/cris/person/\"))."+
					"FILTER (!regex(str(?a), \"http://data.uni-muenster.de/context/csa/teacher/\"))."+
					"}"
					//+"LIMIT 10"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService(GlobalSettings.getEndpoint(), query);

			Model results = qexec.execConstruct();
			//	results.write(System.out, "N3");
			foafDepiction.getObjectModel().add(results);
			foafDepiction.getObjectModel().write(new FileOutputStream(new File("autobackup/personcris-personcsa.ttl")),"TURTLE");
			foafDepiction.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		

	private static void createPersonFoafDepiction(){
		LodumObject foafDepiction= new LodumObject("http://data.uni-muenster.de/context/cris/person-depiction/");
		try{
			Query query = QueryFactory.create(
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+ 
					"prefix foaf: <http://xmlns.com/foaf/0.1/> "+
					"prefix res: <http://www.medsci.ox.ac.uk/vocab/researchers/0.1/>"+
					"CONSTRUCT"+
					"{?a foaf:depiction ?image.}"+
					"WHERE {"+
					"?a <http://vocab.lodum.de/helper/personID> ?c ."+
					"BIND(URI(concat(concat(\"http://data.uni-muenster.de/images/dynamic/cris/person/\",str(?c)),\".jpg\")) as ?image)"+
					"}"
					//+"LIMIT 10"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService(GlobalSettings.getEndpoint(), query);

			Model results = qexec.execConstruct();
			//	results.write(System.out, "N3");
			foafDepiction.getObjectModel().add(results);
			//foafDepiction.getObjectModel().write(System.out,"TURTLE");
			foafDepiction.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	
	private static void createPersonAwardRelation(){
		
		LodumObject award= new LodumObject("http://data.uni-muenster.de/context/cris/person-award/");
		
		try{
			Query query = QueryFactory.create(
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+ 
					"prefix foaf: <http://xmlns.com/foaf/0.1/> "+
					"prefix res: <http://www.medsci.ox.ac.uk/vocab/researchers/0.1/>"+
					"CONSTRUCT {?persons res:holdsAward ?award}"+
					"WHERE {" +
					"?award rdf:type res:Award." +
					"?award  <http://vocab.lodum.de/helper/card> ?cards." +
					"?persons rdf:type foaf:Person." +
					"?persons <http://vocab.lodum.de/helper/card> ?cards."+
					"}"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService(GlobalSettings.getEndpoint(), query);

			Model results = qexec.execConstruct();
			//	results.write(System.out, "N3");
			award.getObjectModel().add(results);
			award.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void createPersonOrganizationRelation(){
		CrisPerson crisPers= new CrisPerson("http://data.uni-muenster.de/context/cris/person-orga/");
		try{
			Query query = QueryFactory.create(
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+ 
					"prefix foaf: <http://xmlns.com/foaf/0.1/> "+
					"prefix pv: <http://linkedscience.org/pv/ns#>"+
					"prefix aiiso: <http://purl.org/vocab/aiiso/schema#>"+
					"CONSTRUCT {?orga foaf:member ?persons}"+
					"WHERE {" +
					"?orga rdf:type foaf:Organization." +
					"?orga  <http://vocab.lodum.de/helper/card> ?cards." +
					"?persons rdf:type foaf:Person." +
					"?persons <http://vocab.lodum.de/helper/card> ?cards."+
					"}"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService(GlobalSettings.getEndpoint(), query);

			Model results = qexec.execConstruct();
			//	results.write(System.out, "N3");
			crisPers.getObjectModel().add(results);
			crisPers.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void loadOrganisations(){
		
		
		
		CrisOrganization crisOrga = new CrisOrganization(config.getProperty("crisOrgaNS"));
		URL website;

		try {
			
			System.out.println("Downloading CRIS Organisations...");
			
			website = new URL("https://www.uni-muenster.de/forschungaz/ws/public/infoobject/findsimple/Organisation/City/**");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream("xml/organisations.xml");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

			System.out.println("Converting CRIS Oranisations...");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("xml/organisations.xml"),Charset.forName("UTF-8")));		
			crisOrga.readIntoModel("cris_organisation",in);
			
			System.out.println("Commiting CRIS Oranisations to triple store...");
			
			crisOrga.commitModelToStore();

			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void createPersonProjectRelation(){
		LodumObject rel= new LodumObject("http://data.uni-muenster.de/context/cris/project-person/");
		try{
			Query query = QueryFactory.create(
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+ 
					"prefix foaf: <http://xmlns.com/foaf/0.1/> "+
					"prefix pv: <http://linkedscience.org/pv/ns#>"+
					"CONSTRUCT {?projects pv:participant ?persons}"+
					"WHERE {" +
					"?projects rdf:type pv:ResearchProject."+
					"?projects <http://vocab.lodum.de/helper/card> ?cards."+
					"?persons rdf:type foaf:Person."+
					"?persons <http://vocab.lodum.de/helper/card> ?cards."+
					"}"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService(GlobalSettings.getEndpoint(), query);

			Model results = qexec.execConstruct();

			rel.getObjectModel().add(results);
			rel.commitModelToStore();
			


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void createPersonPublicationRelation(){
		LodumObject rel= new LodumObject("http://data.uni-muenster.de/context/cris/person-publication/");
		try{
			Query query = QueryFactory.create(
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+ 
					"prefix foaf: <http://xmlns.com/foaf/0.1/> "+
					"prefix pv: <http://linkedscience.org/pv/ns#>"+
					"prefix bibo: <http://purl.org/ontology/bibo/>"+
					"CONSTRUCT {?publication bibo:producer ?persons}"+
					"WHERE {" +
					"?publication rdf:type bibo:Document."+
					"?publication  <http://vocab.lodum.de/helper/card> ?cards." +
					"?persons rdf:type foaf:Person." +
					"?persons <http://vocab.lodum.de/helper/card> ?cards."+
					"FILTER(!EXISTS{?publication bibo:producer ?persons})."+
					"}"
			);

			//QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);
			
			QueryExecution qexec = QueryExecutionFactory.sparqlService(GlobalSettings.getEndpoint(), query);
			//http://giv-lodum.uni-muenster.de:8080/openrdf-sesame/repositories/agile-lod4wfs
			Model results = qexec.execConstruct();

			rel.getObjectModel().add(results);
			rel.commitModelToStore();
			


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void createPrizes(){

		try{
			CrisCard crisCard= new CrisCard("http://data.uni-muenster.de/context/cris/card/");
			Model cardModel = crisCard.getStoreModel();
			Property cardProp = cardModel.getProperty("http://vocab.lodum.de/helper/card");
			Property cardIdProp = cardModel.getProperty("http://vocab.lodum.de/helper/cardID");
			NodeIterator obs = cardModel.listObjectsOfProperty(cardIdProp);
			ConfigProvider config = new ConfigProvider();
			
			while(obs.hasNext()){
				String cardId= obs.next().asLiteral().toString();
				//URL url = new URL("https://www.uni-muenster.de/forschungaz/ws/public/infoobject/getrelated/Card/8459/PRIC_has_CARD");
				//System.out.println(cardId);

				/*URL url = new URL(basisURL+"getrelated/Card/"+cardId+"/PRIC_has_CARD");
				URLConnection c = url.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
				 */
				File file = new XmlCacher().getXML(basisURL+"getrelated/Card/"+cardId+"/PRIC_has_CARD", "prices/"+cardId+".xml", config);
				
				System.out.println("File _>> " + file.toString());
				
				if(file.length()>100){
					BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),Charset.forName("UTF-8")));

					CrisPrice crisPrice = new CrisPrice("http://data.uni-muenster.de/context/cris/price/");
					crisPrice.readIntoModel("cris_price",in);
					//crisPrice.getObjectModel().write(System.out);
					if(crisPrice.getObjectModel().size()>1){
						Resource cardURI = cardModel.createResource("http://data.uni-muenster.de/context/cris/card/" +cardId);
						//TODO for each price add 

						Property priceIdProp = cardModel.getProperty("http://vocab.lodum.de/helper/priceID");
						NodeIterator priceObs = crisPrice.getObjectModel().listObjectsOfProperty(priceIdProp);
						while(priceObs.hasNext()){
							String priceId = priceObs.next().asLiteral().toString();
							Resource priceRes = cardModel.getResource("http://data.uni-muenster.de/context/cris/price/"+priceId);
							crisPrice.getObjectModel().add(priceRes, cardProp, cardURI);
							//System.out.println(priceRes.toString());
						}
						crisPrice.commitModelToStore();
					}
				}

			}




			//	System.out.println(crisEdu.getObjectModel().write(System.out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void linkPersonsOrganizations(){
		CrisPerson crisPerson = new CrisPerson("http://data.uni-muenster.de/context/cris/person/");
		CrisOrganization crisOrga = new CrisOrganization(config.getProperty("crisOrgaNS"));
		Property helperProp = ResourceFactory.createProperty("http://vocab.lodum.de/helper/card");

		Model perM = crisPerson.getStoreModel();
		//System.out.println(perM.size());
		//	Model prjM = crisProj.getStoreModel();
		Model orgaM = crisOrga.getStoreModel();
		Model m = ModelFactory.createDefaultModel();

		ResIterator personResources = perM.listResourcesWithProperty(helperProp);

		//		System.out.println(m.size());
		while(personResources.hasNext()){
			Resource personR = personResources.next();
			//	System.out.println(r.getURI());
			Statement st = perM.getProperty(personR,helperProp);
			//	System.out.println(st);
			ResIterator projectSubjects = orgaM.listSubjectsWithProperty(helperProp, st.getObject());
			while(projectSubjects.hasNext()){
				Resource pr = projectSubjects.next();
				//		System.out.println(pr); foaf:member
				m.add(ResourceFactory.createStatement(st.getSubject(), ResourceFactory.createProperty("http://vocab.org/aiiso/schema#part_of"),pr));
			}
			//	System.out.println(m.write(System.out));

		}
		crisPerson.readIntoModel(m);
		crisPerson.commitModelToStore();
	}
	
	private static void convertCsa(){
		CSA crisOrga = new CSA("http://data.uni-muenster.de/context/csa/");
		try{
			URL url = new URL("http://ifgi.uni-muenster.de/services/rdf/linkedCsa.php?term=3");
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
			crisOrga.readIntoModel(in);
			crisOrga.commitModelToStore();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		crisOrga = new CSA("http://data.uni-muenster.de/context/csa/");
		try{
			URL url = new URL("http://ifgi.uni-muenster.de/services/rdf/linkedCsa.php?term=4");
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
			crisOrga.readIntoModel(in);
			crisOrga.commitModelToStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
		crisOrga = new CSA("http://data.uni-muenster.de/context/csa/");
		try{
			URL url = new URL("http://ifgi.uni-muenster.de/services/rdf/linkedCsa.php?term=5");
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
			crisOrga.readIntoModel(in);
			crisOrga.commitModelToStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
		crisOrga = new CSA("http://data.uni-muenster.de/context/csa/");
		try{
			URL url = new URL("http://ifgi.uni-muenster.de/services/rdf/linkedCsa.php?term=6");
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
			crisOrga.readIntoModel(in);
			crisOrga.commitModelToStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
		crisOrga = new CSA("http://data.uni-muenster.de/context/csa/");
		try{
			URL url = new URL("http://ifgi.uni-muenster.de/services/rdf/linkedCsa.php?term=7");
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
			crisOrga.readIntoModel(in);
			crisOrga.commitModelToStore();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void testEducation(){
		CrisPerson crisEdu = new CrisPerson("http://data.uni-muenster.de/context/cris/education/");
		try{
			URL url = new URL(basisURL+"getrelated/Person/9325/PERS_has_EDUC");
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));

			crisEdu.readIntoModel("cris_education",in);
			crisEdu.commitModelToStore();
			System.out.println(crisEdu.getObjectModel().write(System.out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
