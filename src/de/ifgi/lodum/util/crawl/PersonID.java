package de.ifgi.lodum.util.crawl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;

import org.openrdf.rio.RDFParseException;

import org.openrdf.sail.memory.MemoryStore;

public class PersonID {
	
	public ArrayList<Integer> getAllPersonIds(){
		Repository myRepository = new SailRepository(new MemoryStore());
		try {
			myRepository.initialize();
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ArrayList<Integer> personsIDs = new ArrayList<Integer>();
		File dir = new File("conversion");
		File[] fileList = dir.listFiles();
		for(File f : fileList) {
			if(f.getName().startsWith("2012-06-11_persons_G") && f.getName().endsWith(".ttl")){

				try {

					myRepository.getConnection().add(new FileInputStream(f), "http://data.uni-muenster.de/context/",RDFFormat.TURTLE, new Resource[0]);

				} catch (RDFParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}


		}
		try {
			String queryString = "SELECT DISTINCT ?x WHERE { ?y <http://vocab.lodum.de/helper/personID> ?x }";
			TupleQuery tupleQuery;

			tupleQuery = myRepository.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			TupleQueryResult result = tupleQuery.evaluate();

			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value valueOfX = bindingSet.getValue("x");
				//System.out.println(valueOfX.stringValue());
				personsIDs.add(Integer.parseInt(valueOfX.stringValue()));

				// do something interesting with the values here...



			}
			result.close();
			myRepository.getConnection().close();
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
