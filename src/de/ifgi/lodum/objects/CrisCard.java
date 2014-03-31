package de.ifgi.lodum.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public class CrisCard extends AbstractCrisObject{


	public CrisCard(String context) {
		super(context);
	}

	public void readIntoModel(Reader reader) {
		Model m = ModelFactory.createDefaultModel();
		m.read(reader,"");
		/* Property cardIdPro= m.getProperty("http://vocab.lodum.de/helper/cardID");
		NodeIterator statements = m.listObjectsOfProperty(cardIdPro);
		while(statements.hasNext()){
			RDFNode res =statements.nextNode();
			String cardID=res.asLiteral().toString();
			//addCardToOrga(cardID);
		} 
		super.readIntoModel(m); */
	}
	
	public void getModel(Reader reader) {
		Model m = ModelFactory.createDefaultModel();
		m.read(reader,"");
		super.readIntoModel(m);
	}

	/*
	 * //probably outdated
	 
	private void addCardToOrga(String cardID){

		CrisOrganization crisOrga = new CrisOrganization(this.config.getProperty("crisOrgaNS"));
		Model tempModel = ModelFactory.createDefaultModel();
		try{
			URL url = new URL(this.config.getProperty("crisURL")+"getrelated/Card/"+cardID+"/CARD_has_ORGA");
			//System.out.println(cardID);
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(),Charset.forName("UTF-8")));
			Model m = crisOrga.readAndReturnModel("cris_organisation",in);
			NodeIterator nodeIt = m.listObjectsOfProperty(m.createProperty("http://vocab.lodum.de/helper/orgaID"));
			while(nodeIt.hasNext()){
				RDFNode nd=nodeIt.next();
				tempModel.add(tempModel.createStatement(tempModel.createResource(this.config.getProperty("crisOrgaNS")+nd.asLiteral().toString()),tempModel.createProperty("http://vocab.lodum.de/helper/card"),m.createResource("http://data.uni-muenster.de/context/cris/card/"+cardID)));
			}

		}catch(Exception e){
			log.error(e.toString());
		}
		//System.out.println(tempModel.write(System.out));
		crisOrga.getObjectModel().add(tempModel);
		crisOrga.commitModelToStore();
	}
	*/


	
	protected ArrayList<File> getXmlFromApi() {
		// TODO Auto-generated method stub
		return null;
	}



}