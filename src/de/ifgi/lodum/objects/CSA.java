package de.ifgi.lodum.objects;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class CSA  extends AbstractLodumObject{
	Logger log =Logger.getLogger(this.getClass().getName());
	
	public  CSA(String context){
		super(context);
	}
	

	@Override
	public void readIntoModel(Reader reader) {
		
		Model m = ModelFactory.createDefaultModel();
		m.read(reader,"");
		ArrayList<Statement> toRemove = new ArrayList<Statement>();
		StmtIterator statements = m.listStatements(null, m.createProperty("http://linkedscience.org/teach/ns#weeklyHours"), (RDFNode)null);
		while(statements.hasNext()){
			Statement st = statements.next();
			Object value=null;
			try{
			value = st.getLiteral().getValue();
			}catch(DatatypeFormatException ex){
				toRemove.addAll(m.listStatements(st.getSubject(),m.createProperty("http://linkedscience.org/teach/ns#weeklyHours"), (RDFNode)null).toList());
				
			}
		}
		statements = m.listStatements(null, m.createProperty("http://linkedscience.org/teach/ns#ects"), (RDFNode)null);
		while(statements.hasNext()){
			Statement st = statements.next();
			Object value=null;
			try{
				value = st.getLiteral().getValue();
				}catch(DatatypeFormatException ex){
					toRemove.addAll(m.listStatements(st.getSubject(),m.createProperty("http://linkedscience.org/teach/ns#ects"), (RDFNode)null).toList());
					
				}
		}
		statements = m.listStatements(null, m.createProperty("http://linkedscience.org/teach/ns#bookingNumber"), (RDFNode)null);
		while(statements.hasNext()){
			Statement st = statements.next();
			Object value=null;
			try{
				value = st.getLiteral().getValue();
				}catch(DatatypeFormatException ex){
					toRemove.addAll(m.listStatements(st.getSubject(),m.createProperty("http://linkedscience.org/teach/ns#bookingNumber"), (RDFNode)null).toList());
					
				}
		}
		long before = m.size();
		m.remove(toRemove);
		log.info(before-m.size()+" Statements have been removed, because they were not valid");
		getObjectModel().add(m);
		m.close();
		
	}




	
}