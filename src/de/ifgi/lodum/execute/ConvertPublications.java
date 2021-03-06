package de.ifgi.lodum.execute;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import de.ifgi.lodum.objects.CrisPublication;
import de.ifgi.lodum.objects.LodumObject;

public class ConvertPublications {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CrisPublication("http://data.uni-muenster.de/context/cris/publication/").crawlConvertCommit();
		 createPersonPublicationRelation();
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
			
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://giv-lodum.uni-muenster.de:8080/openrdf-sesame/repositories/lod4wfs2", query);
			//http://giv-lodum.uni-muenster.de:8080/openrdf-sesame/repositories/agile-lod4wfs
			Model results = qexec.execConstruct();

			rel.getObjectModel().add(results);
			rel.commitModelToStore();
			


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
