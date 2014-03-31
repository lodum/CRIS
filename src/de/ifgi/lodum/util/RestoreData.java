package de.ifgi.lodum.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.ontotext.jena.SesameDataset;

import de.ifgi.lodum.config.ConfigProvider;

public class RestoreData {

	public static void main(String[] args) {
		ConfigProvider config = new ConfigProvider();

		String repositoryURL = config.getProperty("repositoryURL");
		String repositoryID = "lodumhbz";


		RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(repositoryURL);
		try {
			repositoryManager.initialize();
		} catch (RepositoryException e1) {
			e1.printStackTrace();
		}
		Repository repos = null;
		try {
			repos=repositoryManager.getRepository(repositoryID);
		} catch (RepositoryConfigException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		ContextAwareConnection reposConnection = null;
		try {
			reposConnection=new ContextAwareConnection(repos);



		} catch (RepositoryException e) {
			e.printStackTrace();
		}


		File dir = new File("autobackup/lodum_backup_28-03-13");
		File[] fileList = dir.listFiles();
		for(File f : fileList) {
			//if(f.getName().contains("data.uni-muenster.de-context-food")){
			if(f.getName().endsWith(".trig") && !f.getName().contains("ulb")&& f.getName().contains("contextcrispersonpublication.trig")  ){ //&& !list.contains(f.getName()) && !f.getName().contains("ulb") && !f.getName().contains("publication")){
				try {
					reposConnection.add(f, "http://data.uni-muenster.de/context/", RDFFormat.TRIG);
				} catch (RDFParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				try {
					reposConnection.commit();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(f.getName());
			}

		}

	}
}
