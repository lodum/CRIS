package de.ifgi.lodum.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.swing.JFrame;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.turtle.TurtleParser;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileUtils;

import de.ifgi.lodum.config.ConfigProvider;
import de.ifgi.lodum.objects.AbstractCrisObject;
import de.ifgi.lodum.objects.CSA;
import de.ifgi.lodum.objects.CrisCard;
import de.ifgi.lodum.objects.CrisOrganization;
import de.ifgi.lodum.objects.CrisPerson;
import de.ifgi.lodum.objects.CrisPosition;
import de.ifgi.lodum.objects.CrisPrice;
import de.ifgi.lodum.objects.CrisProject;
import de.ifgi.lodum.objects.CrisPublication;
import de.ifgi.lodum.objects.LodumObject;


import de.ifgi.lodum.util.XmlCacher;


public class Cris {

	static String basisURL="https://www.uni-muenster.de/forschungaz/ws/public/infoobject/";
	static ConfigProvider config = new ConfigProvider();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * CONVERSION CSA
		 */
		//convertCsa();

		/*
		 * CONVERSION CRIS 
		 */
		
		//new CrisOrganization("http://data.uni-muenster.de/context/cris/organisation/").startCrawlingAndConversion();
		//new CrisPerson("http://data.uni-muenster.de/context/cris/person/").startCrawlingAndConversion();
		/*new CrisPublication("http://data.uni-muenster.de/context/cris/publication/").startCrawlingAndConversion();
		  AbstractCrisObject crisObject = new LodumObject("http://data.uni-muenster.de/context/cris/publication/");
		File dir = new File(config.getProperty("conversionFolder")+"test");
		for(File f : dir.listFiles()){
			if(f.exists() && f.isFile() && f.getName().endsWith(".xml.ttl") && f.getName().startsWith("2012-06-11_publications_")){
				System.out.println(f.getName());
				
				crisObject.addAndCommitToStore(f, RDFFormat.TURTLE);
			}
		}  */
		
		 createPersonPublicationRelation();
		
		//createPersonOrganizationRelation();
		
		// 1.	load all cards
		//convertCards();
		// 2. load all organizations
		//convertOrganizations();
		// 3. connect Organizations with  Cards
		//addCardIdsToOrgas();
		//addCardsToOrgas();
		// 4. convert all Persons
		// convertPersons();
		//readInPersons();
		// 5. convert all education information and link/add them directly to Persons
		//testEducation();
		// 6. relate all persons to organizations
		//createPersonOrganizationRelation();
		// 7. convert all CRIS projects
		//convertProjects();
		// 8. relate all projects to persons
		//createProjectPersonRelation();
		// 9. convert Publications
		//convertPublications();
		// 10. relate persons and publications
		//createPersonPublicationRelation();
		// 11. 
		//crawlAndConvertPrices();
		// 12. create all persons relations
		//	createProjectPersonRelation();
		//createPersonOrganizationRelation();
		//	createPersonAwardRelation();
		//	createPersonPublicationRelation();
		//crawlAndConvertPrices();

		//	convertOrganizations();
		//		createPersonFoafDepiction();
		
		//deleteTriple();

		//createCrisPersonCsaPerson();
	}

	private static void convertOrganizations(){
		CrisOrganization crisOrga = new CrisOrganization(config.getProperty("crisOrgaNS"));
		try{

			/*
			URL url = new URL(basisURL+"get/Organisation/4917");
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
			 */

			//single resource from local xml file for testing purpose
			//crisOrga.readIntoModel("cris_organisation",new InputStreamReader(new FileInputStream(new File("testxml/organisation_5395.xml"))));

			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("testxml/organisations.xml"),Charset.forName("UTF-8")));
		//	BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("testxml/orga9446.xml"),Charset.forName("UTF-8")));
			
			crisOrga.readIntoModel("cris_organisation",in);

			crisOrga.commitModelToStore();
			//System.out.println(crisOrga.getObjectModel().write(System.out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Requests CSA data from the API (already converted into RDF) and commits it to the store
	 * !! The CSA API is only accessible from the x.uni-muenster.de domain !!
	 */
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

	private static void deleteCardIdsFromPersons(){
		CrisPerson crisPerson = new CrisPerson("http://data.uni-muenster.de/context/cris/person/");
		Model m = crisPerson.getStoreModel();
		ResIterator resources = m.listResourcesWithProperty(ResourceFactory.createProperty("http://vocab.lodum.de/helper/cardID"));
		System.out.println(m.size());
		while(resources.hasNext()){
			Statement st = m.getProperty(resources.next(),ResourceFactory.createProperty("http://vocab.lodum.de/helper/cardID"));
			m.remove(st);
		}
		System.out.println(m.size());
		crisPerson.commitModelToStore();

	}

	/*	private static void linkPersonsProjects(){
		CrisPerson crisPerson = new CrisPerson("http://data.uni-muenster.de/context/cris/person/");
		CrisProject crisProj = new CrisProject("http://data.uni-muenster.de/context/cris/project/");
		Property helperProp = ResourceFactory.createProperty("http://vocab.lodum.de/helper/card");

		Model perM = crisPerson.getStoreModel();
		//	Model prjM = crisProj.getStoreModel();
		Model m = ModelFactory.createDefaultModel();

		ResIterator resources = perM.listResourcesWithProperty(helperProp);
		//		System.out.println(m.size());
		while(resources.hasNext()){
			Resource r = resources.next();
			//	System.out.println(r.getURI());
			Statement st = perM.getProperty(r,helperProp);
			ResIterator projectSubjects = crisProj.getStoreModel().listSubjectsWithProperty(helperProp, st.getObject());
			while(projectSubjects.hasNext()){
				m.add(ResourceFactory.createStatement(projectSubjects.next(), ResourceFactory.createProperty("http://linkedscience.org/pv/ns#participant"),st.getSubject()));
			}
		}
		crisProj.readIntoModel(m);
		crisProj.commitModelToStore();
	}
	 */

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

	private static void convertPublications(){
		CrisPublication crisPublication = new CrisPublication("http://data.uni-muenster.de/context/cris/publication/");
		try{
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd'-'HH:mm:ss");

			SimpleDateFormat dateFormatter = new SimpleDateFormat ("yyyy.MM.dd");

			//	String [] pubs = {"publications/publications_a.xml","publications/publications_b.xml","publications/publications_c.xml","publications/publications_d.xml"};

			//,"publications/publications_h.xml","publications/publications_i.xml","publications/publications_j.xml","publications/publications_k.xml","publications/publications_l.xml","publications/publications_m.xml","publications/publications_n.xml","publications/publications_o.xml","publications/publications_p.xml","publications/publications_q.xml","publications/publications_r.xml","publications/publications_s.xml","publications/publications_t.xml","publications/publications_v.xml","publications/publications_w.xml","publications/publications_x.xml","publications/publications_y.xml","publications/publications_z.xml"};
			//String [] pubs = {"publications/publications_e.xml","publications/publications_f.xml","publications/publications_g.xml","publications/publications_h-clean.xml","publications/publications_i.xml","publications/publications_j.xml","publications/publications_k.xml","publications/publications_l.xml","publications/publications_m.xml","publications/publications_n.xml","publications/publications_o.xml","publications/publications_p.xml","publications/publications_q.xml","publications/publications_r.xml","publications/publications_s-clean.xml","publications/publications_t-clean.xml","publications/publications_v-clean.xml","publications/publications_w-clean.xml","publications/publications_x-clean.xml","publications/publications_y-clean.xml","publications/publications_z-clean.xml"};
			//"publications/publications_s.xml","publications/publications_t.xml","publications/publications_v.xml","publications/publications_w.xml","publications/publications_x.xml","publications/publications_y.xml","publications/publications_z.xml"};

			//String [] pubs = {"publication_36592.xml"};
			//String [] pubs = {"publications/publications_d.xml"};
			File folder = new File("testxml/publications_15_05_2012");
			File[] listOfFiles = folder.listFiles();
			ArrayList<String> pubs = new ArrayList<String>();
			for (int i = 0; i < listOfFiles.length; i++) {
			  if (listOfFiles[i].isFile() && listOfFiles[i].toString().endsWith("-clean.xml")) {
				  pubs.add(listOfFiles[i].getAbsolutePath());
			  } 
			}

			for(String file:pubs){
				System.out.println("startet:"+formatter.format( new Date()));
				System.out.println("Starting to process file:" + file);
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),Charset.forName("UTF-8")));
				//BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("testxml/publications/publications_c.xml"),Charset.forName("UTF-8")));
				crisPublication.readIntoModel("cris_publication",in);
				
				crisPublication.getObjectModel().write(new FileOutputStream(new File(file)), "TRIG");
				//crisPublication.commitModelToStore();
				System.out.println("finished :"+formatter.format( new Date()));
				System.out.println("Finished processing file:" + file);
			}
			//BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("testxml/publication_20960.xml"),Charset.forName("UTF-8")));
			//a,u

			//System.out.println(crisPublication.getObjectModel().write(System.out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void convertPersons(){
		
		try{
			/*
			URL url = new URL(basisURL+"get/Person/9325"); //9325 carsten // 8517
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
			 */

			//String[] files={"N_person.xml","O_person.xml","P_person.xml","Q_person.xml","R_person.xml","S_person.xml","T_person.xml","U_person.xml","V_person.xml","W_person.xml","X_person.xml","Y_person.xml","Z_person.xml"};
			//String[] files={"A_person.xml","B_person.xml","C_person.xml","D_person.xml","E_person.xml","F_person.xml","G_person.xml","H_person.xml","I_person.xml","J_person.xml","K_person.xml","L_person.xml","M_person.xml","N_person.xml","O_person.xml","P_person.xml","Q_person.xml","R_person.xml","S_person.xml","T_person.xml","U_person.xml","V_person.xml","W_person.xml","X_person.xml","Y_person.xml","Z_person.xml"};
			//String[] files={"M_person.xml","N_person.xml","O_person.xml","P_person.xml","Q_person.xml","R_person.xml","S_person.xml","T_person.xml","U_person.xml","V_person.xml","W_person.xml","X_person.xml","Y_person.xml","Z_person.xml"};

			//	String[] files={"W_person.xml"};
			
			File folder = new File("testxml/persons_24_05_2012");
			File[] listOfFiles = folder.listFiles();
			ArrayList<String> pubs = new ArrayList<String>();
			for (int i = 0; i < listOfFiles.length; i++) {
			  if (listOfFiles[i].isFile() && listOfFiles[i].toString().endsWith("-clean.xml")) {
				  pubs.add(listOfFiles[i].getName());
			  } 
			}


			for(String file:pubs){
				System.out.println("Starting to process file:" + file);
				CrisPerson crisPerson = new CrisPerson("http://data.uni-muenster.de/context/cris/person/");
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("testxml/persons_24_05_2012/"+file),Charset.forName("UTF-8")));
				crisPerson.readIntoModel("cris_person",in);
				crisPerson.getObjectModel().write(new FileOutputStream(new File("conversion/persons_24_05_2012/"+file+".ttl")), "TURTLE");
				//crisPerson.commitModelToStore();
				System.out.println("Finished processing file:" + file);
			}


			//crisPerson.getObjectModel().setNsPrefixes(prefixes);
			/* crisPerson.getObjectModel().add(ResourceFactory.createResource("http://data.uni-muenster.de/context/cris/person/9325"), ResourceFactory.createProperty("http://purl.org/ontology/bibo/produce"), ResourceFactory.createResource("http://data.uni-muenster.de/context/ulb/4613915"));
			crisPerson.getObjectModel().add(ResourceFactory.createResource("http://data.uni-muenster.de/context/cris/person/9325"), ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/depiction"), ResourceFactory.createResource("http://carsten.io/images/carsten_kessler.jpg"));

			 */

			//add carsten as a teacher
			// <http://data.uni-muenster.de/context/csa/127> <http://linkedscience.org/teach/ns#teacher> <http://data.uni-muenster.de/context/cris/person/9325>.
			//crisPerson.getObjectModel().add(ResourceFactory.createResource("http://data.uni-muenster.de/context/cris/person/8517"), ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/depiction"), ResourceFactory.createResource("http://ifgi.uni-muenster.de/~j_tram02/jt.jpg"));
			//crisPerson.getObjectModel().add(ResourceFactory.createResource("http://data.uni-muenster.de/context/cris/person/8517"), ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/workInfoHomepage"), ResourceFactory.createResource("http://ifgi.uni-muenster.de/~j_tram02/"));
			// crisPerson.getObjectModel().add(ResourceFactory.createResource("http://data.uni-muenster.de/context/cris/person/9325"), ResourceFactory.createProperty("http://www.w3.org/2002/07/owl#sameAs"), ResourceFactory.createResource("http://carsten.io/foaf.rdf"));

			//System.out.println(crisPerson.getObjectModel().write(System.out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void readInPersons(){
		CrisPerson crisPerson = new CrisPerson("http://data.uni-muenster.de/context/cris/person1/");
		File folder = new File("conversion/persons_24_05_2012");
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			  try {
					crisPerson.getReposConnection().add(new File("conversion/persons_24_05_2012/"+listOfFiles[i].getName()),"http://data.uni-muenster.de/context/",RDFFormat.TURTLE) ;
					crisPerson.commitModelToStore();
					System.out.println(listOfFiles[i].getName() +" have been commited");
				} catch (RDFParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  } 
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


	/**
	 * connects all available cards with organizations (if there are any)
	 */
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

	private static void convertPositions(){
		CrisPosition crisPosition = new CrisPosition("http://data.uni-muenster.de/context/cris/position/");
		try{
			URL url = new URL(basisURL+"getrelated/Person/9148/PERS_has_POSI");
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
			crisPosition.readIntoModel("cris_position",in);
			//crisPrice.commitModelToStore();
			System.out.println(crisPosition.getObjectModel().write(System.out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * converts all positions
	 * status: testing
	 */
	private static void crawlAndConvertPrices(){

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

	private static void convertCards(){
		CrisCard crisCard= new CrisCard("http://data.uni-muenster.de/context/cris/card/");
		try{

			/* Example for retrieving a source directly from the API
			URL url = new URL(basisURL+"getrelated/Person/9325/PERS_has_CARD");
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
			 */

			// convert xml card file (contains all cards, retrieved by a certain date from the API)
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("testxml/cards.xml"),Charset.forName("UTF-8")));
			//BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("testxml/card_10562.xml"),Charset.forName("UTF-8")));

			crisCard.readIntoModel("cris_card",in);

			crisCard.commitModelToStore();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * converts all projects
	 * status: final
	 */
	private static void convertProjects(){
		CrisProject crisProj = new CrisProject("http://data.uni-muenster.de/context/cris/project/");
		try{
			//URL url = new URL("https://www.uni-muenster.de/forschungaz/ws/public/infoobject/get/Project/4429");
			//URLConnection c = url.openConnection();
			//FileInputStream c = new FileInputStream("testxml/project_4429.xml");
			//BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
			//BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("testxml/project_4429.xml"),Charset.forName("UTF-8")));
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("testxml/projects.xml"),Charset.forName("UTF-8")));
			crisProj.readIntoModel("cris_project",in);
			crisProj.commitModelToStore();
			//System.out.println(crisProj.getObjectModel().write(System.out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * constructs pv:participant relation between Persons and ResearchProject via sparql
	 * status: final
	 */
	private static void createProjectPersonRelation(){
		CrisProject crisProj = new CrisProject("http://data.uni-muenster.de/context/cris/project-person/");
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

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			Model results = qexec.execConstruct();
			//	results.write(System.out, "TURTLE");
			crisProj.getObjectModel().add(results);
			crisProj.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * constructs aiiso:part_of relation between Persons and Organizations via sparql
	 * status: final
	 */
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

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			Model results = qexec.execConstruct();
			//	results.write(System.out, "N3");
			crisPers.getObjectModel().add(results);
			crisPers.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * constructs  res:holdsAward relation between Persons and Organizations via sparql
	 * status: final
	 */
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

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			Model results = qexec.execConstruct();
			//	results.write(System.out, "N3");
			award.getObjectModel().add(results);
			award.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * constructs  foaf:depiction relation between Persons and Organizations via sparql
	 * status: final
	 */
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

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			Model results = qexec.execConstruct();
			//	results.write(System.out, "N3");
			foafDepiction.getObjectModel().add(results);
			//foafDepiction.getObjectModel().write(System.out,"TURTLE");
			foafDepiction.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * constructs  foaf:depiction relation between Persons and Organizations via sparql
	 * status: final
	 */
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

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			Model results = qexec.execConstruct();
			//	results.write(System.out, "N3");
			foafDepiction.getObjectModel().add(results);
			foafDepiction.getObjectModel().write(new FileOutputStream(new File("autobackup/personcris-personcsa.ttl")),"TURTLE");
			foafDepiction.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * constructs bibo:creator relation between Persons and Organizations via sparql
	 * status: final
	 */
	private static void createPersonPublicationRelation(){
		LodumObject Rel= new LodumObject("http://data.uni-muenster.de/context/cris/person-publication/");
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
					"}"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			Model results = qexec.execConstruct();
			//	results.write(System.out, "N3");

			Rel.getObjectModel().add(results);
			Rel.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void refactorCards(){
		LodumObject crisCard= new LodumObject("http://data.uni-muenster.de/context/cris/card/");

		try{
			Query query = QueryFactory.create(
					"SELECT DISTINCT ?a "+
					"WHERE {" +
					"?a ^<http://vocab.lodum.de/helper/card> ?c." +
					"}"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);
			ResultSet results = qexec.execSelect();
			ArrayList<Integer> cardids = new ArrayList<Integer>();


			while(results.hasNext()){
				cardids.add(Integer.parseInt(results.next().get("a").toString().substring(46)));

				//System.out.println(results.nextSolution().get("a").toString().substring(46));

			}
			int size=cardids.size();
			System.out.println("finished crawling all ids... "+size);
			results=null;
			qexec=null;
			query=null;
			URL url;
			URLConnection c;
			BufferedReader in;
			ConfigProvider config = new ConfigProvider();
			int i=0;



			for(Integer cardid:cardids){
				i++;


				File file = null;
				file=new XmlCacher().getXML(config.getProperty("crisURL")+"get/Card/"+cardid, "cards/"+cardid+".xml", config);
				//url = new URL(config.getProperty("crisURL")+"get/Card/"+cardid);
				//c = url.openConnection();
				if(file!=null){
					try{
						in =new BufferedReader(new InputStreamReader(new FileInputStream(file),Charset.forName("UTF-8")));
						//	in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
					}catch(IOException e){
						e.printStackTrace();
						in=null;
					}
					if(in!=null){
						//crisCard.readIntoModel("cris_card",in);
						in.close();
						crisCard.commitModelToStore();


					}
				}




			}
		}catch(Exception e){
			e.printStackTrace();

		}


	}
	
	public static void deleteTriple(){
		CrisOrganization crisOrga = new CrisOrganization(config.getProperty("crisOrgaNS"));
		
		Model m= crisOrga.getObjectModel();
		StmtIterator st = crisOrga.getStoreModel().listStatements(m.createResource("http://data.uni-muenster.de/context/cris/organization/5425"),m.createProperty("http://www.w3.org/2006/vcard/ns#adr"),(RDFNode)null);
		Statement stmt=null;
		while(st.hasNext()){
			Statement s = st.next();
			if(s.getObject().asResource().getURI().contains("Weselasdfasstr.")){
			System.out.println(s);
			
			ArrayList<Statement>a = new ArrayList<Statement>();
			a.add(s);
			Resource[] r;
		//	crisOrga.getReposConnection().remove(null,null,s.getObject());
			}
		}
		
		//crisOrga.getStoreModel().remove(m.createResource("http://data.uni-muenster.de/context/cris/organization/5425"),m.createProperty("http://www.w3.org/2006/vcard/ns#adr"),m.createResource("http://data.uni-muenster.de/context/cris/addresses/48151/Weselasdfasstr."));
		crisOrga.commitModelToStore();
	}


}
