package de.ifgi.lodum.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import de.ifgi.lodum.util.Sha1;
import de.ifgi.lodum.util.XmlCacher;

/**
 * @author Johannes Trame
 *
 */
public class CrisPerson extends AbstractCrisObject{
	Logger log =Logger.getLogger(this.getClass().getName());

	public CrisPerson(){
		new CrisPerson(this.config.getProperty("context.persons"));
	}
	
	public  CrisPerson(String context){
		super(context);
		this.xslTemplate="cris_person";
		this.crisEntityName="person";
	}


	@Override
	public void readIntoModel(Reader reader) {
		Model m = ModelFactory.createDefaultModel();
		m.read(reader,"");
		this.log.info("Converted all persons into Model. Model size:" +m.size());
		Property pro = getObjectModel().getProperty("http://vocab.lodum.de/helper/personID");
		NodeIterator ndIt = m.listObjectsOfProperty(pro);

		while(ndIt.hasNext()){
			String personId = ndIt.nextNode().toString();
			Model cardModel = getCard(personId);
			Resource personRes = m.getResource("http://data.uni-muenster.de/context/cris/person/"+personId);

			/* ResIterator proIt = cardModel.listSubjectsWithProperty(getObjectModel().getProperty("http://vocab.lodum.de/helper/personID"));
			while(proIt.hasNext()){
				Resource cardId = proIt.next();
				cardModel.add(cardId, ResourceFactory.createProperty("http://vocab.lodum.de/helper/isCardOfPerson"), personRes);
				m.remove(personRes, pro, m.createLiteral(personId));
			} */

			//add mail to personModel
			Property faxProp = cardModel.getProperty("http://www.w3.org/2006/vcard/ns#fax");
			NodeIterator obs = cardModel.listObjectsOfProperty(faxProp);
			while(obs.hasNext()){
				m.add(personRes, faxProp, obs.next().asLiteral());
			}
			obs=null;
			Property phoneProp = cardModel.getProperty("http://xmlns.com/foaf/0.1/phone");
			obs = cardModel.listObjectsOfProperty(phoneProp);
			while(obs.hasNext()){
				m.add(personRes, phoneProp, obs.next().asLiteral());
			}
			obs=null;
			Property mboxShaProp = cardModel.createProperty("http://xmlns.com/foaf/0.1/mbox_sha1sum");
			Property mboxProp = cardModel.createProperty("http://xmlns.com/foaf/0.1/mbox");
			obs = cardModel.listObjectsOfProperty(mboxProp);
			while(obs.hasNext()){
				Literal shaLit = m.createLiteral(Sha1.stringToSha(obs.next().asLiteral().toString()));
				m.add(personRes, mboxShaProp, shaLit);
			}
			obs=null;
			Property cardIdProp = cardModel.getProperty("http://vocab.lodum.de/helper/cardID");

			Property cardProp = cardModel.getProperty("http://vocab.lodum.de/helper/card");

			obs = cardModel.listObjectsOfProperty(cardIdProp);
			while(obs.hasNext()){
				//remove original added new one
				Resource cardIDres = m.createResource("http://data.uni-muenster.de/context/cris/card/"+obs.next().asLiteral());
				m.add(personRes, cardProp, cardIDres );
			}

		}
		super.readIntoModel(m);
	}


	private Model getCard(String personID){
		try{
			File file = new XmlCacher().getXML(config.getProperty("crisURL")+"getrelated/Person/"+personID+"/PERS_has_CARD", "PERS_has_CARD/"+personID+".xml", config);
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
		char[] alphabet = new char[26];
		for(char c='A', i=0; i<=alphabet.length; c++, i++){
			try {
				fl.add(new XmlCacher().getXML(config.getProperty("crisURL")+"findsimple/Person/First%20name/"+c+"*","persons/"+this.date+"_persons_"+c+".xml", config));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.log.info("finished: crawling persons xml from cris api");
		return fl;

	}

	/**
	 * ReturnsIDs from all entities of type person from the store
	 * @return ArrayList<Integer> personIdList
	 */
	public ArrayList<Integer> getAllPersonIds(){
		ArrayList<Integer> personsIDs = new ArrayList<Integer>();
		try {
			String queryString = "SELECT DISTINCT ?x WHERE { ?y <http://vocab.lodum.de/helper/personID> ?x }";
			TupleQuery tupleQuery;
			tupleQuery = this.getReposConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult result = tupleQuery.evaluate();
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value valueOfX = bindingSet.getValue("x");
				personsIDs.add(Integer.parseInt(valueOfX.stringValue()));
			}
			result.close();
			this.getReposConnection().close();
		}catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return personsIDs;


	}











}
