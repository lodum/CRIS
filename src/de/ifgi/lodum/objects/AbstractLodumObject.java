package de.ifgi.lodum.objects;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import org.openrdf.model.Namespace;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;


import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.ontotext.jena.SesameDataset;

import de.ifgi.lodum.config.ConfigProvider;
import de.ifgi.lodum.util.FileUtils;
import de.ifgi.lodum.util.Md5;



/**
 * Provides common methods for reading external rdf resources as well as for commit those to the triplestore.<br>
 * Access parameters like repositoryURL and repositoryID can be configured in the configuration file config/lodum.xml
 * @author Johannes Trame
 *
 */
public abstract class AbstractLodumObject {

	protected SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
	protected String date = formatter.format(new Date());
	protected String repositoryURL;
	protected String repositoryID;
	protected Model objectModel;
	protected Model repModel;
	private ContextAwareConnection reposConnection;
	private RepositoryManager repositoryManager; 
	private Repository repos;
	protected static ConfigProvider config=new ConfigProvider();
	public URIImpl contextURI =null;
	Logger log =Logger.getLogger(this.getClass().getName());

	/**
	 * Encapsulates a read/write repository connection restricted to the specified context/graph. 
	 * This class can be extended by any class in order to access any context of the repository, specified in the config file.
	 * In case a context/graph does not exist already, it will be created.
	 * @param context The context under which all added Statements will be stored in the triplestore
	 */
	public  AbstractLodumObject(String context){

		if(context!=null){
			this.contextURI=new URIImpl(context);
		}
		
		this.repositoryURL=config.getProperty("repositoryURL");
		this.repositoryID=config.getProperty("repositoryID");
		this.objectModel=ModelFactory.createDefaultModel();

		//Create Logger
		//SimpleLayout layout = new SimpleLayout();
		FileAppender fileAppender = null;
		try {
			fileAppender = new FileAppender( new HTMLLayout(), config.getProperty("logFolder")+"/"+this.date+"."+this.getClass().getSimpleName()+"."+this.repositoryID+".html", true );
		} catch (IOException e2) {
			System.out.println(e2.getMessage());
		}

		ConsoleAppender ca = new ConsoleAppender();
		ca.setWriter(new OutputStreamWriter(System.out));
		ca.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
		Logger.getRootLogger().addAppender( fileAppender );
		//Logger.getRootLogger().addAppender(ca);
		Logger.getRootLogger().setLevel(Level.INFO);



		this.repositoryManager= new RemoteRepositoryManager(this.repositoryURL);
		try {
			this.repositoryManager.initialize();
		} catch (RepositoryException e1) {
			e1.printStackTrace();
		}
		try {
			this.repos=this.repositoryManager.getRepository(this.repositoryID);
		} catch (RepositoryConfigException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		this.reposConnection = null;
		try {
			this.reposConnection=new ContextAwareConnection(this.repos);
			//this.reposConnection.setAddContexts(this.contextURI);
			if(this.contextURI!=null){
			this.reposConnection.setInsertContext(this.contextURI);
			this.reposConnection.setReadContexts(this.contextURI);
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		SesameDataset dataset = new SesameDataset(this.reposConnection);
		if(this.contextURI!=null){
			this.repModel = ModelFactory.createModelForGraph(dataset.getGraph(ResourceFactory.createResource(this.contextURI.toString()).asNode()));
		}

	}

	public  AbstractLodumObject(){
		config=new ConfigProvider();

		this.repositoryURL=config.getProperty("repositoryURL");
		this.repositoryID=config.getProperty("repositoryID");
		this.objectModel=ModelFactory.createDefaultModel();

		this.repositoryManager= new RemoteRepositoryManager(this.repositoryURL);
		try {
			this.repositoryManager.initialize();
		} catch (RepositoryException e1) {
			e1.printStackTrace();
		}
		try {
			this.repos=this.repositoryManager.getRepository(this.repositoryID);
		} catch (RepositoryConfigException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		this.reposConnection = null;
		try {
			this.reposConnection=new ContextAwareConnection(this.repos);

		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		SesameDataset dataset = new SesameDataset(this.reposConnection);

		this.repModel = ModelFactory.createModelForGraph(		dataset.getDefaultGraph());

	}

	public ContextAwareConnection getReposConnection() {
		return reposConnection;
	}

	/**
	 * Reads a RDF InputStream into the local JenaModel (not the repository model)
	 * @param input 
	 */
	public void readIntoModel(Reader reader){
		getObjectModel().read(reader, "");		
		this.log.info("Read converted RDF into local JenaModel.");
}
	
	/**
	 * Adds a JenaModel to the local JenaModel (not the repository model)
	 * @param input 
	 */
	public void readIntoModel(Model model){
		getObjectModel().add(model);		
	}
	


	/**
	 * The objectModel is separated from the RepositoryModel
	 * and will only be merged once the commitModelToStore() method is called
	 * @return RDF JenaModel holding, for example, converted data. 
	 */
	public Model getObjectModel() {
		return objectModel;
	}
	
	public void setObjectModel(Model m) {
		this.objectModel=m;
	}

	/**
	 * Resets the local JenaModel
	 * 
	 * @return Model Empty JenaModel
	 */
	protected Model resetObjectModel(){
		return this.objectModel=ModelFactory.createDefaultModel();
	}



	/**
	 * Returns the a JenaModel of the current repository connection with the specified context/graph
	 * @return JenaModel
	 */
	public Model getStoreModel(){
		return this.repModel;
	}


	/**
	 * Method gets all prefixes from the repository and set the prefixes map of the supplied JenaModel
	 * @param m JenaModel to be set
	 * @return JenaModel with prefixes set
	 */
	public Model setPrefixes(Model m){
		Map<String, String> prefixMap=new HashMap<String,String> ();

		try {
			for(Namespace ns : reposConnection.getNamespaces().asList()){
				prefixMap.put(ns.getPrefix(), ns.getName());
			}
			prefixMap.put("cris", "http://data.uni-muenster.de/context/cris/");
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		m.setNsPrefixes(prefixMap);
		return m;
	}

	/**
	 * Merges the objectModel with the repositoryModel and commits the model to the repository
	 * @return
	 */
	public boolean commitModelToStore(){
		long beforeCommit=repModel.size();
		log.info("Context <"+this.contextURI.toString()+">  before commit: "+beforeCommit);
		try {
			Map<String, String> prefixMap=new HashMap<String,String> ();
			for(Namespace ns : reposConnection.getNamespaces().asList()){
				prefixMap.put(ns.getPrefix(), ns.getName());
			} 
			getObjectModel().setNsPrefixes(prefixMap);
			//TODO
			getObjectModel().removeNsPrefix("ns1");
			this.repModel.add(getObjectModel());
			reposConnection.commit();
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}
		long afterCommit=repModel.size();
		log.info("Context <"+this.contextURI.toString()+"> after commit: "+afterCommit);
		try {
			reposConnection.close();
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}



	/**
	 * Adds a rdf file with the specified RDFFormat to the repository model. Afterwards, the changes will be committed. 
	 * @param fileToAdd
	 * @param rdfformat
	 * @see org.openrdf.rio.RDFFormat
	 */
	public void addAndCommitToStore(File fileToAdd, RDFFormat rdfformat){
		try {
			log.info("RepositoryModelSize  before adding "+ fileToAdd.getName()+" : "+this.getStoreModel().size());
			InputStreamReader in = new InputStreamReader(new FileInputStream(fileToAdd),Charset.forName("UTF-8"));
			this.reposConnection.add(in,"http://data.uni-muenster.de/context/", rdfformat,this.reposConnection.getAddContexts());
			this.reposConnection.commit();
			in.close();
			in=null;
			log.info("RepositoryModelSize after commiting:"+this.getStoreModel().size());
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
	}
	
	public void addProvenanceData(String javaClass){
		String md5JavaClass="default";
		try {
			md5JavaClass = Md5.md5(javaClass);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			//e.printStackTrace();
		}
		String query = 
		"prefix xsd:  <http://www.w3.org/2001/XMLSchema#>" +
		"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
		"prefix opmv: <http://purl.org/net/opmv/ns#>"+
		"prefix dct: <http://purl.org/dc/terms/>"+
		"prefix : <http://data.uni-muenster.de/context/provenance/>"+
		
		"CONSTRUCT {<"+this.contextURI+"> a opmv:Artifact, <http://www.w3.org/2004/03/trix/rdfg-1/Graph> ;"+
	    "opmv:wasGeneratedBy :p0 ;"+
	    "dct:created '"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date())+"'^^xsd:dateTime "+
		"."+
		":p0"+
		"    a opmv:Process ;   "+      
		"    opmv:wasPerformedBy :"+md5JavaClass+" ;    "+
		"    opmv:wasControlledBy <http://data.uni-muenster.de/context/cris/person/8517>      ;"+
		"."+
	
		":"+md5JavaClass+" a opmv:Agent ;   "+
		"    rdfs:label 'JavaCode "+javaClass+"';"+
		".}WHERE{}";
		
		Model model = ModelFactory.createDefaultModel();
		QueryExecutionFactory.create(query, ModelFactory.createDefaultModel()).execConstruct(model);
		LodumObject prov = new LodumObject("http://data.uni-muenster.de/context/provenance/");
		//	model.read(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(query.getBytes()))),"TURTLE");
		prov.readIntoModel(model);
		//prov.getObjectModel().write(System.out,"TURTLE");
		prov.commitModelToStore();
		
	}

	/*//TODO any longer needed?
 public void addAndCommitToStore(Set<String> convertedSubjects, File fileToAdd, RDFFormat rdfformat){

	Set<org.openrdf.model.Statement> stds = new HashSet<org.openrdf.model.Statement>();
	for(String r: convertedSubjects){
			try {
				stds.addAll(this.reposConnection.getStatements(this.reposConnection.getValueFactory().createURI(r), null, null, this.reposConnection.getReadContexts()).asList());
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//this.reposConnection.remove(statements, contexts)
		}
	//addAndCommitToStore(fileToAdd,rdfformat);
	for(org.openrdf.model.Statement st : stds){
		this.log.info(st);
	}
	}
	 */

}
