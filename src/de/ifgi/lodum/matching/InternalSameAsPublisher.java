package de.ifgi.lodum.matching;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

import de.ifgi.lodum.config.ConfigProvider;
import de.ifgi.lodum.objects.CrisPublication;
import de.ifgi.lodum.objects.LodumObject;

public class InternalSameAsPublisher {
	static ConfigProvider config = new ConfigProvider();
	public static void generatePublisherISBN(){
		Model isbnModel = ModelFactory.createDefaultModel();
		try{
			Query query = QueryFactory.create(
						"prefix bibo: <http://purl.org/ontology/bibo/>"+
						"prefix dct: <http://purl.org/dc/terms/> "+
						"prefix foaf: <http://xmlns.com/foaf/0.1/> "+
						"SELECT DISTINCT ?publisher ?isbn WHERE {"+
						  "?a bibo:isbn ?isbn ."+
						  "?a dct:publisher ?publisher."+
						  "?publisher foaf:name ?publisherName."+
						  "filter regex(str(?publisher),'http://data.uni-muenster.de/context/cris/publication/publisher/')"+
						"}"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			ResultSet results = qexec.execSelect();
			Property isbnProperty = isbnModel.createProperty("http://helper.lodum.de/isbnPublisherCode");
			while(results.hasNext()){
				QuerySolution re = results.next();
				String isbn = re.get("isbn").toString();
				String publisher = re.get("publisher").toString();
				if(isbn.split("-").length>3){
					isbnModel.add(isbnModel.createStatement(
							isbnModel.createResource(publisher),
							isbnProperty , 
							isbnModel.createResource(isbn.split("-")[0]+"-"+isbn.split("-")[1]+"-"+isbn.split("-")[2])));
				}
			}


			String q = "prefix owl: <http://www.w3.org/2002/07/owl#> CONSTRUCT {?a owl:sameAs ?b.} WHERE {?a <http://helper.lodum.de/isbnPublisherCode> ?pCode.?b <http://helper.lodum.de/isbnPublisherCode> ?pCode.FILTER (?a != ?b)}";
			Model sameAsModel = QueryExecutionFactory.create(q,isbnModel).execConstruct();
			File turtleFile = new File(config.getProperty("conversionFolder")+"samePublisher.ttl");
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(turtleFile));
			sameAsModel.write(out,"TURTLE");
			out.flush();
			out.close();
			LodumObject sameasContext= new LodumObject("http://data.uni-muenster.de/context/sameas/internal/publisher/");
			sameasContext.readIntoModel(sameAsModel);
			sameasContext.commitModelToStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * 
	 * http://sparql.carsten.io/?query=prefix%20dct%3A%20%3Chttp%3A//purl.org/dc/terms/%3E%20%0Aprefix%20bibo%3A%20%3Chttp%3A//purl.org/ontology/bibo/%3E%20%0Aprefix%20foaf%3A%20%3Chttp%3A//xmlns.com/foaf/0.1/%3E%20%0ASELECT%20DISTINCT%20%3Fpublisher%20%3Fisbn%20WHERE%20%7B%0A%20%20%3Fa%20bibo%3Aisbn%20%3Fisbn%20.%0A%20%20%3Fa%20dct%3Apublisher%20%3Fpublisher.%0A%20%20%3Fpublisher%20foaf%3Aname%20%3FpublisherName.%0A%20%20filter%20regex%28str%28%3Fpublisher%29%2C%27http%3A//data.uni-muenster.de/context/cris/publication/publisher/%27%29%0A%7D%0ALIMIT%2020&endpoint=http%3A//data.uni-muenster.de/sparql
	 */



	public static void main(String[] args) {
		generatePublisherISBN();
	}

}
