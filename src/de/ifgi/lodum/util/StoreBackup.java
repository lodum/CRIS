package de.ifgi.lodum.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.ifgi.lodum.config.ConfigProvider;
import de.ifgi.lodum.objects.CrisPerson;
import de.ifgi.lodum.objects.LodumObject;

public class StoreBackup {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
		
		Model repModel;
		ContextAwareConnection reposConnection;
		RepositoryManager repositoryManager; 
		Repository repos;
		ConfigProvider config =new ConfigProvider();

		Logger log = Logger.getLogger(StoreBackup.class);
		String repositoryURL=config.getProperty("repositoryURL");;
		String repositoryID=config.getProperty("repositoryID");
		repositoryManager= new RemoteRepositoryManager( repositoryURL);
		try {
			repositoryManager.initialize();
		} catch (RepositoryException e1) {
			e1.printStackTrace();
		}
		RepositoryResult<Resource> contextIDs =null;
		try {
			repos= repositoryManager.getRepository( repositoryID);
			contextIDs = repos.getConnection().getContextIDs();

			while(contextIDs.hasNext()){
				String contextString= contextIDs.next().stringValue();
				//contextString="http://data.uni-muenster.de/context/cris/organization/";
				String shortContextString = contextString.replace("http://data.uni-muenster.de/", "");
				shortContextString = shortContextString.replace("/", "_")+"#";
				
				System.out.println(shortContextString);
				LodumObject lodumObject = new LodumObject(contextString);
				
				
				Resource[] context = new Resource[1];
				context[0]=new URIImpl(contextString);
				RepositoryResult<Statement> statements =
					lodumObject.getReposConnection().getStatements(null, null, null, context);

				java.io.OutputStream out =null;
				File f = new File("autobackup/"+formatter.format(new Date())+"/");
		        f.mkdir();
				/* try {
					//+formatter.format(new Date())+"/"
					out = new FileOutputStream(new File("autobackup/"+formatter.format(new Date())+"/"+shortContextString+".nt"));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} */
				RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
				writer.startRDF();
				int i=0;
				while (statements.hasNext()) {
			//		System.out.println(statements.next().getObject().toString());
					if(i%10000==0){
						
						writer.endRDF();
						out.flush();
						try {
							//+formatter.format(new Date())+"/"
							out = new FileOutputStream(new File("autobackup/"+formatter.format(new Date())+"/"+shortContextString+i+".nt"));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
						writer.startRDF();
					}
					writer.handleStatement(statements.next());
					i++;
					
				}
				statements.close();
				writer.endRDF();
				out.flush();
			}
		} catch (RepositoryConfigException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
