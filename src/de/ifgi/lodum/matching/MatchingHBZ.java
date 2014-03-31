package de.ifgi.lodum.matching;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

import de.ifgi.lodum.objects.CrisPublication;
import de.ifgi.lodum.objects.LodumObject;

public class MatchingHBZ {
	public static void matchLodumLobidISBN10(){
		Model sameasModel = ModelFactory.createDefaultModel();
		LodumObject sameasContext= new LodumObject("http://data.uni-muenster.de/context/cris/publication-hbz-relations/");
		try{
			Query query = QueryFactory.create(
					"prefix bibo: <http://purl.org/ontology/bibo/>"+
					"SELECT ?isbn10 ?uri WHERE {"+
					" ?uri bibo:isbn10 ?c ."+
					" BIND(REPLACE(?c, \"-\", \"\") AS ?isbn10)"+
					"}"
					//"LIMIT 10"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			ResultSet results = qexec.execSelect();
			Property sameasProperty = sameasModel.createProperty("http://www.w3.org/2002/07/owl#sameAs");
			while(results.hasNext()){
				QuerySolution re = results.next();
				String lodumIsbn10 = re.get("isbn10").toString();
				String lodumUri = re.get("uri").toString();
				//System.out.println(  + "lodum isbn: "+isbn10);
				Query q = QueryFactory.create("prefix bibo: <http://purl.org/ontology/bibo/>"+
						"Select ?uri WHERE {?uri bibo:isbn10 \""+lodumIsbn10+"\"}");

				//	ResultSet res = QueryExecutionFactory.sparqlService("http://lod.openlinksw.com/sparql", q).execSelect();
				ResultSet res = QueryExecutionFactory.sparqlService("http://lobid.org/sparql/", q).execSelect();
				while(res.hasNext()){
					//System.out.println("LOBID with same isbn "+res.next().get("uri"));
					sameasModel.add(sameasModel.createStatement(
							sameasModel.createResource(lodumUri),
							sameasProperty, 
							sameasModel.createResource(res.next().get("uri").toString())
					)

					);
				}

			}
			System.out.println(sameasModel.size()+" matches found");
			// sameasModel.write(System.out,"TURTLE");
			//	results.write(System.out, "N3");

			sameasContext.readIntoModel(sameasModel);
			sameasContext.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void matchLodumLobidISBN(){
		Model sameasModel = ModelFactory.createDefaultModel();
		LodumObject sameasContext= new LodumObject("http://data.uni-muenster.de/context/cris/publication-hbz-relations/");
		try{
			Query query = QueryFactory.create(
					"prefix bibo: <http://purl.org/ontology/bibo/>"+
					"SELECT ?isbn ?uri WHERE {"+
					" ?uri bibo:isbn ?c ."+
					" BIND(REPLACE(?c, \"-\", \"\") AS ?isbn)"+
					"}"
					//+"LIMIT 10"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			ResultSet results = qexec.execSelect();
			Property sameasProperty = sameasModel.createProperty("http://www.w3.org/2002/07/owl#sameAs");
			while(results.hasNext()){
				QuerySolution re = results.next();
				String[] isbns=re.get("isbn").asLiteral().getValue().toString().split(",");
				for(String isbn:isbns){
					String lodumIsbn = isbn.trim();
					String lodumUri = re.get("uri").toString();
				//	System.out.println( "lodum isbn: "+lodumIsbn);
					Query q = QueryFactory.create("prefix bibo: <http://purl.org/ontology/bibo/>"+
							"Select ?uri WHERE {?uri bibo:isbn13 \""+lodumIsbn+"\"}");

					//	ResultSet res = QueryExecutionFactory.sparqlService("http://lod.openlinksw.com/sparql", q).execSelect();
					ResultSet res = QueryExecutionFactory.sparqlService("http://lobid.org/sparql/", q).execSelect();
					boolean t=true;
					while(res.hasNext()){
						t=false;
						//System.out.println("LOBID with same isbn "+res.next().get("uri"));
						sameasModel.add(sameasModel.createStatement(
								sameasModel.createResource(lodumUri),
								sameasProperty, 
								sameasModel.createResource(res.next().get("uri").toString())
						)

						);
					}

					if(t){
						q = QueryFactory.create("prefix bibo: <http://purl.org/ontology/bibo/>"+
								"Select ?uri WHERE {?uri bibo:isbn10 \""+lodumIsbn+"\"}");

						//	ResultSet res = QueryExecutionFactory.sparqlService("http://lod.openlinksw.com/sparql", q).execSelect();
						res = QueryExecutionFactory.sparqlService("http://lobid.org/sparql/", q).execSelect();
						while(res.hasNext()){
							//System.out.println("LOBID with same isbn "+res.next().get("uri"));
							sameasModel.add(sameasModel.createStatement(
									sameasModel.createResource(lodumUri),
									sameasProperty, 
									sameasModel.createResource(res.next().get("uri").toString())
							)

							);
						}
					}


				}
			}
			System.out.println(sameasModel.size()+" matches found");
			// sameasModel.write(System.out,"TURTLE");
			//	results.write(System.out, "N3");

			sameasContext.readIntoModel(sameasModel);
			sameasContext.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void matchLodumLobidISBN13(){
		Model sameasModel = ModelFactory.createDefaultModel();
		LodumObject sameasContext= new LodumObject("http://data.uni-muenster.de/context/cris/publication-hbz-relations/");
		try{
			Query query = QueryFactory.create(
					"prefix bibo: <http://purl.org/ontology/bibo/>"+
					"SELECT ?isbn13 ?uri WHERE {"+
					" ?uri bibo:isbn13 ?c ."+
					" BIND(REPLACE(?c, \"-\", \"\") AS ?isbn13)"+
					"}"
					//	+"LIMIT 10"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			ResultSet results = qexec.execSelect();
			Property sameasProperty = sameasModel.createProperty("http://www.w3.org/2002/07/owl#sameAs");
			while(results.hasNext()){
				QuerySolution re = results.next();
				String lodumIsbn13 = re.get("isbn13").toString();
				String lodumUri = re.get("uri").toString();
				//System.out.println(  + "lodum isbn: "+isbn10);
				Query q = QueryFactory.create("prefix bibo: <http://purl.org/ontology/bibo/>"+
						"Select ?uri WHERE {?uri bibo:isbn13 \""+lodumIsbn13+"\"}");

				//	ResultSet res = QueryExecutionFactory.sparqlService("http://lod.openlinksw.com/sparql", q).execSelect();
				ResultSet res = QueryExecutionFactory.sparqlService("http://lobid.org/sparql/", q).execSelect();
				while(res.hasNext()){
					//System.out.println("LOBID with same isbn "+res.next().get("uri"));
					sameasModel.add(sameasModel.createStatement(
							sameasModel.createResource(lodumUri),
							sameasProperty, 
							sameasModel.createResource(res.next().get("uri").toString())
					)

					);
				}

			}
			System.out.println(sameasModel.size()+" matches found");
			// sameasModel.write(System.out,"TURTLE");
			//	results.write(System.out, "N3");

			sameasContext.readIntoModel(sameasModel);
			sameasContext.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void matchLodumLobidDoi(){
		Model sameasModel = ModelFactory.createDefaultModel();
		LodumObject sameasContext= new LodumObject("http://data.uni-muenster.de/context/cris/publication-hbz-relations/");
		try{
			Query query = QueryFactory.create(
					"prefix bibo: <http://purl.org/ontology/bibo/>"+
					"SELECT ?doi?uri WHERE {"+
					"?uri a bibo:Book."+
					"?uri bibo:doi ?c ."+
					// "BIND(REPLACE(?c, \"-\", \"\") AS ?d)"+
					"BIND(concat('http://dx.doi.org/',?c) as ?doi)"+

					"}"
					+"LIMIT 100"
			);

			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.uni-muenster.de/sparql", query);

			ResultSet results = qexec.execSelect();
			Property sameasProperty = sameasModel.createProperty("http://www.w3.org/2002/07/owl#sameAs");
			while(results.hasNext()){
				QuerySolution re = results.next();
				String doi = re.get("doi").toString();
				String lodumUri = re.get("uri").toString();
				//System.out.println(  + "lodum isbn: "+isbn10);
				Query q = QueryFactory.create("prefix bibo: <http://purl.org/ontology/bibo/>"+
						"Select ?uri WHERE {?uri bibo:doi \""+doi+"\"}");
				//	"Select ?uri WHERE {?uri bibo:doi ?d. FILTER regex(str(?d),\""+doi+"\")}");
				//	ResultSet res = QueryExecutionFactory.sparqlService("http://lod.openlinksw.com/sparql", q).execSelect();
				ResultSet res = QueryExecutionFactory.sparqlService("http://lobid.org/sparql/", q).execSelect();
				while(res.hasNext()){
					//System.out.println("LOBID with same isbn "+res.next().get("uri"));
					sameasModel.add(sameasModel.createStatement(
							sameasModel.createResource(lodumUri),
							sameasProperty, 
							sameasModel.createResource(res.next().get("uri").toString())
					)

					);
				}

			}
			System.out.println(sameasModel.size()+" matches found");
			sameasModel.write(System.out,"TURTLE");
			//	results.write(System.out, "N3");

			//sameasContext.readIntoModel(sameasModel);
			//sameasContext.commitModelToStore();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		//matchLodumLobidISBN10();
		//matchLodumLobidDoi();
		//matchLodumLobidISBN13();
		matchLodumLobidISBN();
	}

}
