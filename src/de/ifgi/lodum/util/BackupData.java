package de.ifgi.lodum.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import de.ifgi.lodum.objects.CrisCard;
import de.ifgi.lodum.objects.CrisOrganization;
import de.ifgi.lodum.objects.CrisPerson;
import de.ifgi.lodum.objects.CrisProject;


public class BackupData {

	static SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd'-'HH:mm:ss");
	
	public static void backupCrisPerson(){
		try{
			CrisPerson crisPerson = new CrisPerson("http://data.uni-muenster.de/context/cris/person/");
			
			java.io.OutputStream out = new FileOutputStream(new File("backup/persons_"+new Date().getDay()+"-"+new Date().getMonth()+".xml"));
			RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
			writer.startRDF();
			Resource[] context = new Resource[1];
			context[0]=new URIImpl("http://data.uni-muenster.de/context/cris/person/");
			RepositoryResult<Statement> statements =
				crisPerson.getReposConnection().getStatements(null, null, null, context);

			while (statements.hasNext()) {
				writer.handleStatement(statements.next());
			}
			statements.close();
			writer.endRDF();
			out.flush();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public static void backupCrisPrice(){
		try{
			CrisPerson crisPerson = new CrisPerson("http://data.uni-muenster.de/context/cris/price/");
			

			java.io.OutputStream out = new FileOutputStream(new File("backup/price_"+formatter.format(new Date())+".xml"));
			RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
			writer.startRDF();
			Resource[] context = new Resource[1];
			context[0]=new URIImpl("http://data.uni-muenster.de/context/cris/price/");
			RepositoryResult<Statement> statements =
				crisPerson.getReposConnection().getStatements(null, null, null, context);

			while (statements.hasNext()) {
				writer.handleStatement(statements.next());
			}
			statements.close();
			writer.endRDF();
			out.flush();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public static void backupCrisPublication(){
		try{
			CrisPerson crisPerson = new CrisPerson("http://data.uni-muenster.de/context/cris/publication/");
			

			java.io.OutputStream out = new FileOutputStream(new File("backup/publications_"+formatter.format(new Date())+".xml"));
			RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
			writer.startRDF();
			Resource[] context = new Resource[1];
			context[0]=new URIImpl("http://data.uni-muenster.de/context/cris/publication/");
			RepositoryResult<Statement> statements =
				crisPerson.getReposConnection().getStatements(null, null, null, context);

			while (statements.hasNext()) {
				writer.handleStatement(statements.next());
			}
			statements.close();
			writer.endRDF();
			out.flush();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public static void backupCrisCard(){
		try{
			CrisCard crisCard = new CrisCard("http://data.uni-muenster.de/context/cris/card/");
			
			java.io.OutputStream out = new FileOutputStream(new File("backup/cards_"+new Date().getDay()+"-"+new Date().getMonth()+".xml"));
			RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
			writer.startRDF();
			Resource[] context = new Resource[1];
			context[0]=new URIImpl("http://data.uni-muenster.de/context/cris/card/");
			RepositoryResult<Statement> statements =
				crisCard.getReposConnection().getStatements(null, null, null, context);

			while (statements.hasNext()) {
				writer.handleStatement(statements.next());
			}
			statements.close();
			writer.endRDF();
			out.flush();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public static void backupCrisProject(){
		try{
			CrisProject crisProject = new CrisProject("http://data.uni-muenster.de/context/cris/project/");
			
			java.io.OutputStream out = new FileOutputStream(new File("backup/projects_"+new Date().getDay()+"-"+new Date().getMonth()+".xml"));
			RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
			writer.startRDF();
			Resource[] context = new Resource[1];
			context[0]=new URIImpl("http://data.uni-muenster.de/context/cris/project/");
			RepositoryResult<Statement> statements =
				crisProject.getReposConnection().getStatements(null, null, null, context);
			while (statements.hasNext()) {
				writer.handleStatement(statements.next());
			}
			

			statements.close();
			writer.endRDF();
			out.flush();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public static void backupCrisOrganizations(){
		try{
			CrisOrganization crisOrganization = new CrisOrganization("http://data.uni-muenster.de/context/cris/organization/");
			
			java.io.OutputStream out = new FileOutputStream(new File("backup/organizations_"+new Date().getDay()+"-"+new Date().getMonth()+".xml"));
			RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
			writer.startRDF();
			Resource[] context = new Resource[1];
			context[0]=new URIImpl("http://data.uni-muenster.de/context/cris/organization/");
			RepositoryResult<Statement> statements =
				crisOrganization.getReposConnection().getStatements(null, null, null, context);
			while (statements.hasNext()) {
				writer.handleStatement(statements.next());
			}
			

			statements.close();
			writer.endRDF();
			out.flush();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public static void backupCrisCards(){
		try{
			CrisCard crisOrganization = new CrisCard("http://data.uni-muenster.de/context/cris/card/");
			
			java.io.OutputStream out = new FileOutputStream(new File("backup/cards_"+formatter.format(new Date())+".xml"));
			RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
			writer.startRDF();
			Resource[] context = new Resource[1];
			context[0]=new URIImpl("http://data.uni-muenster.de/context/cris/card/");
			RepositoryResult<Statement> statements =
				crisOrganization.getReposConnection().getStatements(null, null, null, context);
			while (statements.hasNext()) {
				writer.handleStatement(statements.next());
			}
			

			statements.close();
			writer.endRDF();
			out.flush();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	//	backupCrisPerson();
		
	//	backupCrisProject();
		
	//	backupCrisOrganizations();

		
	//backupCrisPublication()	;
	//backupCrisCard();
		
	//	backupCrisCards();
		backupCrisPrice();
	}

}
