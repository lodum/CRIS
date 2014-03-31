package de.ifgi.lodum.objects;


import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import com.hp.hpl.jena.rdf.model.ModelFactory;






public class Provenance extends AbstractLodumObject {
	
	
	public Provenance(){
		this(config.getProperty("provenanceGraph"));
	}

	public  Provenance(String context){
		super(context);

	}
	
	public void setProvenanceInformation(String className, String context){
		
		try{
			String rdf="@prefix opmv:<http://purl.org/net/opmv/ns>."+
					 "@prefix prv:<http://purl.org/net/provenance/ns#>."+
					"@prefix xsd:<http://www.w3.org/2001/XMLSchema#>."+
					"@prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>."+ 
					"@prefix rdfs:<http://www.w3.org/2000/01/rdf-schema#>."+
					"@prefix time:<http://www.w3.org/TR/owl-time/>."+

					"<"+context+"> rdf:type <http://www.w3.org/2004/03/trix/rdfg-1/Graph>, opmv:Artifact, prv:DataItem ;"+
				    "	opmv:wasGeneratedBy ["+
				    "    rdf:type opmv:Process ;" +
				    "	 opmv:wasPerformedAt ["+
				    "	 	a time:Instant ;"+
					"	 	time:inXSDDateTime \""+this.date+"\"^^xsd:date"+
             		"	 ];" +    
				    "    opmv:wasPerformedBy [" +
				    "			a opmv:Agent ;" +
				    "			rdfs:label \"JavaClass "+className+" that formats data as RDF/XML\"^^xsd:string" +
				    "	];"+
				    "    opmv:wasControlledBy <http://data.uni-muenster.de/context/cris/person/8517>"+   
				    "].";

ModelFactory.createDefaultModel().read(new StringReader(rdf),"","N3").write(System.out,"N3");
			//commitModelToStore();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
