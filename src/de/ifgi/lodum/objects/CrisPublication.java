package de.ifgi.lodum.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;

import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.ibm.icu.text.Normalizer;


import de.ifgi.lodum.util.Md5;
import de.ifgi.lodum.util.XmlCacher;

/**
 * @author Johannes Trame
 *
 */
public class CrisPublication extends AbstractCrisObject{
	//TDO not sure if we need this any longer ... convertedSubjects was supposed to hold all subject-uris as set of strings (unique) in order to delete old ones from the repository
	Set<String> convertedSubjects = new HashSet<String>();
	Logger log =Logger.getLogger(this.getClass().getName());


	public  CrisPublication(String context){
		super(context);
		this.xslTemplate="cris_publication";
		this.crisEntityName="publication";
	}


	@Override
	public void readIntoModel(Reader reader) {
		Model m = ModelFactory.createDefaultModel();
		m.read(reader,"");
		reader=null;
		//add card
		Property pro = m.createProperty("http://vocab.lodum.de/helper/pubID");
		NodeIterator ndIt = m.listObjectsOfProperty(pro);
		while(ndIt.hasNext()){
			RDFNode nd = ndIt.nextNode();
			
			
			//card ID
			String publicationId = nd.toString();
			Model cardModel = getCard(publicationId);
			//log.info("card model of publicationsid "+publicationId + " - size of cardModel: "+cardModel.size());
			String pubStr = "http://data.uni-muenster.de/context/cris/publication/"+publicationId;
			Resource publicationRes = m.getResource(pubStr);
			this.convertedSubjects.add(pubStr);
			Property cardProp = cardModel.getProperty("http://vocab.lodum.de/helper/card");

			//for each card uri add a card property to the publication
			Property cardIdProp = cardModel.getProperty("http://vocab.lodum.de/helper/cardID");
			NodeIterator obs = cardModel.listObjectsOfProperty(cardIdProp);
			while(obs.hasNext()){
				RDFNode obsNext = obs.next();
				Resource cardURI = m.createResource("http://data.uni-muenster.de/context/cris/card/" +obsNext.asLiteral());
				m.add(publicationRes, cardProp, cardURI);
			}

		}

		//modify authorlist
		Property authorlist = m.createProperty("http://purl.org/ontology/bibo/authorlist");

		ResIterator resIter =m.listResourcesWithProperty(authorlist);
		while(resIter.hasNext()){
			Resource resource = resIter.next();
			NodeIterator authors = m.listObjectsOfProperty(resource,authorlist);
			
			List<RDFNode> authorsList = authors.toList();

			Seq sequence = m.createSeq();
			for(RDFNode author: authorsList){
				String[] authorArray = author.toString().split(",");
				for(String s: authorArray){
					s=Normalizer.normalize(s, Normalizer.NFKC).trim();
					//	s=new String(s, "UTF-8");
					Resource authorRes=null;
					try {
						authorRes = m.createResource("http://data.uni-muenster.de/context/cris/publication/author/"+Md5.md5(resource.getURI().toString()+s));
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					m.add(m.createStatement(authorRes,m.createProperty("http://xmlns.com/foaf/0.1/name"),m.createTypedLiteral(m.createLiteral(s), "http://www.w3.org/2001/XMLSchema#string")));
					//m.add(m.createStatement(authorRes,m.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),m.createProperty("http://xmlns.com/foaf/0.1/Person")));
					sequence.add(authorRes);

				}

			} 

			StmtIterator iter = m.listStatements(
					new SimpleSelector(resource, authorlist, (RDFNode) null) );
			List<Statement> toDelete = iter.toList();
			m.remove(toDelete);
			
			NodeIterator l = getStoreModel().listObjectsOfProperty(resource,authorlist);
			while(l.hasNext()){
				Resource r =l.next().asResource();
				getStoreModel().remove(resource,authorlist,r);
				if(r.isAnon()){
					StmtIterator it = getStoreModel().listStatements(new SimpleSelector(r, null, (RDFNode) null) );
					getStoreModel().remove(it.toList());
				}
			} 
			//l.toList();
			
			//cleaning up homepage
			StmtIterator homepageIterator = m.listStatements(new SimpleSelector(resource,m.createProperty("http://xmlns.com/foaf/0.1/homepage"),(RDFNode)null));
			//HashMap<String,String> map = new HashMap<String,String>();
			for(Statement s:homepageIterator.toList()){
				
				String st=s.getObject().asResource().toString();
				 if(s.getObject().asResource().toString().contains("<") || s.getObject().asResource().toString().contains(">") ){
					//stToRemove.add(s);
					m.remove(s);
					//s.changeObject(m.createResource(URLEncoder.encode(st)));
					this.log.error("Removed Homepage from "+s.getSubject().toString()+" since it contained < or >");
				}else if(s.getObject().asResource().toString().contains("\\")){
					//s.changeObject(m.createResource(URLEncoder.encode("s.getObject().asResource().toString()")));
					//s.changeObject(s.getObject().asResource().toString().replace("\\", ""));
					this.log.error("Removed backslah from homepage "+s.getPredicate().toString()+" of "+s.getSubject().toString());
					s.changeObject(m.createResource(st.replace("\\", "")));
				} 
				//s.changeObject(m.createResource(URLEncoder.encode(st)));
			}
			//m.remove(stToRemove); 
			
			m.add(resource, authorlist, sequence);

		}

		super.readIntoModel(m); 
		m=null;
	}


	/**
	 * 
	 * @param publicationID
	 * @return JenaModel containing the CrisCards belonging to the Publication
	 */
	private Model getCard(String publicationID){

		//CrisCard crisCa = new CrisCard("http://data.uni-muenster.de/context/cris/card/");
		try{
			File file = new XmlCacher().getXML(config.getProperty("crisURL")+"getrelated/Publication/"+publicationID+"/PUBL_has_CARD", "PUBL_has_CARD/"+publicationID+".xml", config);
			if(file!=null && file.exists()){
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),Charset.forName("UTF-8")));
			return this.readAndReturnModel("cris_card",in);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ModelFactory.createDefaultModel();
	}
	
	protected ArrayList<File> getXmlFromApi(){
		ArrayList<File> fl = new ArrayList<File>();
		//TODO check if the whole alpabet is covered. For example, with char[26] the "[" is covered, but do we need more ?
		char[] alphabet = new char[26];
		for(char c='A', i=0; i<=alphabet.length; c++, i++){
			try {
				//this.date="2012-06-15";
				String url =config.getProperty("crisURL")+"findsimple/Publication/Title/"+c+"*";
				File f=new XmlCacher().getXML(url,"publications/"+this.date+"_publications_"+c+".xml", config);
				fl.add(f);
				//fl.add(new XmlCacher().getXML(config.getProperty("crisURL")+"findsimple/Publication/Title/"+c+"*","publications/2012-06-11_publications_"+c+".xml", config));
				this.log.info("Cached XML form "+url+" to file"+ f.getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		this.log.info("finished: crawling publications xml from cris api");
		return fl;

	}











}
