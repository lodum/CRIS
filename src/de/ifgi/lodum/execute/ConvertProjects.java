package de.ifgi.lodum.execute;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;

import de.ifgi.lodum.objects.CrisProject;
import de.ifgi.lodum.objects.CrisPublication;
import de.ifgi.lodum.objects.LodumObject;

public class ConvertProjects {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CrisProject("http://data.uni-muenster.de/context/cris/project/").crawlConvertCommit();
		createPersonProjectRelation();
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

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			Model results = qexec.execConstruct();

			rel.getObjectModel().add(results);
			rel.commitModelToStore();
			


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}
