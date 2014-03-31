package de.ifgi.lodum.objects;

import info.kwarc.krextor.Krextor;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.openrdf.rio.RDFFormat;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.ifgi.lodum.util.FileUtils;


import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.xslt.XSLException;

public abstract class AbstractCrisObject extends AbstractLodumObject{

	Logger log =Logger.getLogger(this.getClass().getName());
	protected String xslTemplate;
	protected String crisEntityName;
	
	public AbstractCrisObject(String context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public AbstractCrisObject() {
		super();
	}
	

	/**
	 * Method supplies a xslTemplate and a XML Stream (supplied by the Reader) to the KrextorFramework, which will convert the XMl Stream to RDF XML according to the mapping specified in the xslTemplate. 
	 * The converted RDF XML will be read into the JenaModel of the concrete CrisObject. 
	 * @param xslTemplate xslTemplate name (without file ext) which will be applied by Krextor to convert the xml stream to RDF
	 * @param reader Any java reader (for example a BufferedReader with the correct encoding) containing the xml file stream
	 */
	public void readIntoModel(String xslTemplate,Reader reader){
		Krextor k = new Krextor();
		nu.xom.Document doc=null;
			try {
		        
				doc =  k.extract(xslTemplate,"rdf-xml",new Builder().build(reader));
				reader.close();
				this.log.info("Converted XML with Krextor Template ("+xslTemplate +") to RDF");
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
			//String str = ;
			//System.out.println(str);
			
			try {
				readIntoModel(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(doc.toXML().getBytes("UTF-8")))));
				//str=null;
				doc=null;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	/**
	 * HelperMethod to convert XML and return it immediately (neither the local jenaModel nor the repositoryModel will be touched).
	 * This method can be used, for example, to convert cris-cards within other objects without the need establish a new instance of a new repository connection.
	 * @param xslTemplate
	 * @param reader
	 * @return
	 */
	public Model readAndReturnModel(String xslTemplate,Reader reader){

		
		Krextor k = new Krextor();
		String str=null;
		//nu.xom.Document doc=null;
			try {
		        
				str =  k.extract(xslTemplate,"rdf-xml",new Builder().build(reader)).toXML();
				reader.close();
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
			///String str = doc.toXML();
			//System.out.println(str);
			
			try {

				return ModelFactory.createDefaultModel().read(new InputStreamReader(new ByteArrayInputStream(str.getBytes("UTF-8"))),"");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ModelFactory.createDefaultModel();
		
	}
	
	/**
	 * Method to crawl the xml data (possibly in chunks by wildcards) from the official cris api and cache it locally
	 * @return a ArrayList of locally cached xml files to convert
	 */
	protected abstract ArrayList<File> getXmlFromApi();


	
	private File convertXml(File f,String xslTemplate){

		File turtleFile = new File(config.getProperty("conversionFolder")+f.getName()+".ttl");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f),Charset.forName("UTF-8")));
			readIntoModel(xslTemplate,in);
			if(getObjectModel().size()>0){
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(turtleFile));
				this.setPrefixes(getObjectModel()).write(out,"TURTLE");
				out.flush();
				out.close();
				this.log.info("Wrote JenaModel to file: " +turtleFile.getName() + "containing "+getObjectModel().size()+" statements");
			}else{
				this.log.info(f.getName() + " contains no new statments.");
			}
			resetObjectModel();
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			in=null;
			return turtleFile;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//TODO change back to private
	protected ArrayList<File> startCrawlingAndConversion(){
		ArrayList<File> convertedFiles = new ArrayList<File>();
		for(File f :getXmlFromApi()){
			if(f != null && f.exists()){
				File cf = convertXml(f,this.xslTemplate);
				if(cf!=null && cf.exists()){
					convertedFiles.add(cf);
				}
				//delete cached xml files
				f.deleteOnExit(); //TODO
			}

		}
		return convertedFiles;

	}
	
	public void crawlConvertCommit(){
		this.log.info("Start crawling and conversation process");
		ArrayList<File> ttlfiles =startCrawlingAndConversion();
		for(File f : ttlfiles){
			 this.addAndCommitToStore(f, RDFFormat.TURTLE);
			//old this.addAndCommitToStore(this.convertedSubjects,f, RDFFormat.TURTLE);
			//delete cached ttl files
			f.deleteOnExit(); //TODO
		}
		try {
			FileUtils.zipFiles(ttlfiles, config.getProperty("conversionArchiveFolder")+this.date+this.crisEntityName+".zip");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
