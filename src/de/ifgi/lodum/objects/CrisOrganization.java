package de.ifgi.lodum.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;


import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.ifgi.lodum.util.XmlCacher;


/**
 * @author Johannes Trame
 *	@version $Name$ $Revision$ $Date$
 */
public class CrisOrganization extends AbstractCrisObject{

	public  CrisOrganization(String context){
		super(context);
		this.xslTemplate="cris_organisation";
		this.crisEntityName="organisation";
	}

	@Override
	public void readIntoModel(Reader reader) {
		Model m = ModelFactory.createDefaultModel();
		m.read(reader,"");
		/*
		 * gets and attaches CardIDs for the resource
		 */
		Property orgaIDproperty = m.createProperty("http://vocab.lodum.de/helper/orgaID");
		Property cardIDproperty = m.createProperty("http://vocab.lodum.de/helper/cardID");
		Property cardProp = m.createProperty("http://vocab.lodum.de/helper/card");

		//iterates over all objects(literals) {?a <http://vocab.lodum.de/helper/orgaID> ?orgaID}
		NodeIterator nodeIt = m.listObjectsOfProperty(orgaIDproperty);
		while(nodeIt.hasNext()){
			String orgaId = nodeIt.nextNode().toString();
			//get all cards associated with the orgaID
			Model cardModel = getCard(orgaId);

			Resource orgaRes = m.getResource("http://data.uni-muenster.de/context/cris/organization/"+orgaId);

			NodeIterator obs = cardModel.listObjectsOfProperty(cardIDproperty);
			while(obs.hasNext()){
				Resource cardIDres = m.createResource("http://data.uni-muenster.de/context/cris/card/"+obs.next().asLiteral());
				m.add(orgaRes, cardProp, cardIDres );
			}

		}
		/*
		 * fix street address
		 */
		Property pro = getObjectModel().getProperty("http://www.w3.org/2006/vcard/ns#street-address");
		StmtIterator ndIt = m.listStatements(new SimpleSelector(null,pro,(RDFNode)null));
		ArrayList<Statement> deleteList = new ArrayList<Statement>();
		ArrayList<Statement> addList = new ArrayList<Statement>();

		while(ndIt.hasNext()){
			Statement st = ndIt.nextStatement();

			String street = st.getObject().asLiteral().getString();
			street=street.replace("strasse", "stra�e");
			street=street.replace("Strasse", "Stra�e");
			street=street.replace("str.", "stra�e");
			street=street.replace("Str.", "Stra�e");
			street=street.replace("�", "oe");
			street=street.replace("�", "ue");
			street=street.replace("�", "ae");
			deleteList.add(m.createStatement(st.getSubject(), st.getPredicate(), st.getObject()));
			addList.add(m.createStatement(st.getSubject(), st.getPredicate(),m.createTypedLiteral(street, XSDDatatype.XSDstring)));
		}

		m.remove(deleteList);
		m.add(addList);
		super.readIntoModel(m);
	}



	private Model getCard(String orgaID){
		try{
			File file = new XmlCacher().getXML(config.getProperty("crisURL")+"getrelated/Organisation/"+orgaID+"/CARD_has_ORGA", "CARD_has_ORGA/"+orgaID+".xml", config);
			if(file!=null){
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),Charset.forName("UTF-8")));
				return this.readAndReturnModel("cris_card",in);
			}
			else{
				return ModelFactory.createDefaultModel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ModelFactory.createDefaultModel();
	}


	protected ArrayList<File> getXmlFromApi(){
		//https://www.uni-muenster.de/forschungaz/ws/public/infoobject/findsimple/Organisation/City/** -o c_organisation_10.06.2012.xml
		ArrayList<File> fl = new ArrayList<File>();
		try {
			fl.add(new XmlCacher().getXML(config.getProperty("crisURL")+"findsimple/Organisation/City/**",this.date+"_organisations.xml", config));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fl;
	}








}
