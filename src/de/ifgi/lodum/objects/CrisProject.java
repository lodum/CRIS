package de.ifgi.lodum.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.log4j.Logger;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;

import com.hp.hpl.jena.rdf.model.Resource;


import de.ifgi.lodum.util.XmlCacher;

/**
 * @author Johannes Trame
 * 
 */
public class CrisProject extends AbstractCrisObject{
	Logger log =Logger.getLogger(this.getClass().getName());
	
	public  CrisProject(String context){
		super(context);
		this.xslTemplate="cris_project";
		this.crisEntityName="project";
	}

	public boolean validate() {
		// TODO Auto-generated method stub
		return true;
	}

	public static void main(String[] args) {
		
	}
	@Override
	public void readIntoModel(Reader reader) {
		Model m = ModelFactory.createDefaultModel();
		m.read(reader,"");

		Property pro = getObjectModel().getProperty("http://vocab.lodum.de/helper/projectID");
		NodeIterator ndIt = m.listObjectsOfProperty(pro);
		while(ndIt.hasNext()){
			String projectId = ndIt.nextNode().toString();
			Model cardModel = getCard(projectId);


			Resource projRes = m.getResource("http://data.uni-muenster.de/context/cris/project/"+projectId);
			/*	ResIterator proIt = cardModel.listSubjectsWithProperty(getObjectModel().getProperty("http://vocab.lodum.de/helper/personID"));
			while(proIt.hasNext()){
				Resource cardId = proIt.next();
				cardModel.add(cardId, ResourceFactory.createProperty("http://vocab.lodum.de/helper/isCardOfPerson"), personRes);
				m.remove(personRes, pro, m.createLiteral(personId));
			}
			 */
			Property cardIdProp = cardModel.getProperty("http://vocab.lodum.de/helper/cardID");
			Property cardProp = cardModel.createProperty("http://vocab.lodum.de/helper/card");
			NodeIterator obs = cardModel.listObjectsOfProperty(cardIdProp);
			while(obs.hasNext()){
				m.add(projRes, cardProp, cardModel.createResource("http://data.uni-muenster.de/context/cris/card/" +obs.next().asLiteral()));
			}
		}
		
		//TODO Clean HTML
	/*	Property descPro = getObjectModel().getProperty("http://linkedscience.org/pv/ns#description");
		StmtIterator stIt = m.listStatements((Resource)null, descPro, (RDFNode)null);
		ArrayList<Statement> stToRemove = new ArrayList<Statement>();
		while(stIt.hasNext()){
			Statement st = stIt.nextStatement();
			Literal l=m.createTypedLiteral(m.createLiteral(Jsoup.parse(st.getObject().asLiteral().getString()).text()), "http://www.w3.org/2001/XMLSchema#string");
			
			m.add(m.createStatement(st.getSubject(), st.getPredicate(), l));
			stToRemove.add(st);
			
		}
		for(Statement st :stToRemove){
			m.remove(st);
		} */
		super.readIntoModel(m);
	}


	private Model getCard(String projectID){
		try{
			URL url = new URL(config.getProperty("crisURL")+"getrelated/Project/"+projectID+"/PROJ_has_CARD");
			URLConnection c = url.openConnection();
			InputStreamReader ins = new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8"));
			BufferedReader in = new BufferedReader(ins);
			return this.readAndReturnModel("cris_card", in);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected ArrayList<File> getXmlFromApi(){
		ArrayList<File> fl = new ArrayList<File>();
		try {
			//this.date="2012-06-15";
			String url =config.getProperty("crisURL")+"findsimple/Project/Acronym/*";
			File f=new XmlCacher().getXML(url,this.date+"_"+this.crisEntityName+"_all.xml", config);
			fl.add(f);
			this.log.info("Cached XML form "+url+" to file "+ f.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.log.info("finished: crawling projects xml from cris api");
		return fl;

	}

}
