package de.ifgi.lodum.objects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.ifgi.lodum.util.XmlCacher;

public class CrisPosition extends AbstractCrisObject{
	Logger log =Logger.getLogger(this.getClass().getName());
	
	public  CrisPosition(String context){
		super(context);
		this.xslTemplate="cris_position";
		this.crisEntityName="position";
	}

	@Override
	public ArrayList<File> getXmlFromApi(){
		ArrayList<File> fl = new ArrayList<File>();
		char[] alphabet = new char[1];
		for(char c='K', i=0; i<=alphabet.length; c++, i++){
			try {
				fl.add(new XmlCacher().getXML(config.getProperty("crisURL")+"findsimple/Position/Name/"+c+"*","positions/"+this.date+"_positions_"+c+".xml", config));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.log.info("finished: crawling positions xml from cris api");
		return fl;

	}
	
	public void convert(){
		super.startCrawlingAndConversion();
	}

	
}
