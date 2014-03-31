package de.ifgi.lodum.objects;

import info.kwarc.krextor.Krextor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.openrdf.rio.RDFFormat;

import com.hp.hpl.jena.mem.ObjectIterator;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.pfunction.library.seq;

import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.xslt.XSLException;

public class UlbObject extends AbstractLodumObject {

	public  UlbObject(String context){
		super(context);
	}
	
	public void modifyModel(Model model){
		
		Property publisher = model.createProperty("http://purl.org/dc/terms/publisher");
		NodeIterator objIter =model.listObjectsOfProperty(publisher);
	//	StmtIterator stmIter = null;
		while(objIter.hasNext()){
			StmtIterator stmIter = null;
			RDFNode node =objIter.next();
		//	System.out.println("YAAAAA: " +node.toString().contains(" "));
			if(node.toString().contains(" ")){
				stmIter =model.listStatements(null, null,node);
			}
			if(stmIter != null){
			List<Statement> statements = stmIter.toList();
			
			
			model.remove(statements);
			Resource resourDel =model.createResource(node.asResource().getURI().replace(" ", "%20").replace("&", "").replace("[", "").replace("]", "").replace("+", "").replace(".", ""));
			
			for(Statement stat: statements){
				stat.getSubject().addProperty(stat.getPredicate(), resourDel);
			//	resourDel.addProperty(stat.getPredicate(), stat.getObject());
				
	//			System.out.println("STAAAT" +stat.toString());
			}
			
			}
		//	node.asResource().getURI();
			//node.asResource().getURI().replace(" ", "%20");
			//model.listStatements(new SimpleSelector(resource, authorlist, (RDFNode) null))
			
		}
		
		
		
		Property authorlist = model.createProperty("http://purl.org/ontology/bibo/authorlist");
		
		
		ResIterator resIter =model.listResourcesWithProperty(authorlist);
		
		while(resIter.hasNext()){
		Resource resource = resIter.next();
	//	System.out.println("URI:" +resource.getURI());
		NodeIterator authors = model.listObjectsOfProperty(resource,authorlist);
		
		List<RDFNode> authorsList = authors.toList();
		//System.out.println(authorsList.size());
		String[] authorArray =  new String[authorsList.size()];
		for(RDFNode author: authorsList){
			
			String[] split = author.toString().split(";");
	//		System.out.println(split[0] +"JKDASL" + split[1]);
			if(split.length>1){
			authorArray[Integer.parseInt(split[0])] = split[1];
			}
			else{
				//System.out.println(author.toString());
				authorArray[0] = "empty";
			}
		}
		
//		for(String str: authorArray){
//		//	System.out.println(str);
//		}
		StmtIterator iter = model.listStatements(
			    new SimpleSelector(resource, authorlist, (RDFNode) null) );
		
		List toDelete = iter.toList();
		model.remove(toDelete);
		
		
		//CREATE SEQUENCE OF AUTHORS
		Seq sequence = model.createSeq();
		
		//Bag sequence =	model.createBag();
		
		for(String s: authorArray){
			sequence.add(model.createLiteral(s));
			
		}
		//System.out.println(sequence.getURI());
		if(authorArray[0] != "empty"){
		resource.addProperty(authorlist, sequence);
		}
	//	model.createStatement(resource, authorlist, sequence);
		//model.commit();
		}
		
		
//		while(iter.hasNext()){
//			
//			
//		//	System.out.println("ITER:" +iter.next().toString());
//		}
	}
	
	public void readIntoModel(Reader reader){
		Krextor k = new Krextor();
		nu.xom.Document doc=null;
			try {
		        
				doc =  k.extract("ulbConvertion","rdf-xml",new Builder().build(reader));
				//doc = k.extract("person","rdf-xml",new Builder().build("textxml/person_9325.xml"));
				
			} catch (ValidityException e) {
				e.printStackTrace();
			} catch (XSLException e) {
				e.printStackTrace();
			} catch (ParsingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String str = doc.toXML();
			System.out.println(str);
			
			try {
				getObjectModel().read(new ByteArrayInputStream(str.getBytes("UTF-8")),"");
				modifyModel(getObjectModel());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		
	}
	
//	public void addFileToStore(){
////		reposConnection.add(new File(fileName), "", RDFFormat.TURTLE, contextURI);
////		System.out.println("reModel before commit: "+repModel.size());
////
////		reposConnection.commit();
////		System.out.println("reModel after commit: "+repModel.size());
////		reposConnection.close();
//	}


	public boolean validate() {
		// TODO Auto-generated method stub
		return true;
	}



	
}

