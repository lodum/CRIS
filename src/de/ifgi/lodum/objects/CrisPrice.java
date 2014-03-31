package de.ifgi.lodum.objects;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;


/**
 * @author Johannes Trame
 *
 */
public class CrisPrice extends AbstractCrisObject{
	Logger log =Logger.getLogger(this.getClass().getName());
	
	public  CrisPrice(String context){
		super(context);
		this.xslTemplate="cris_price";
		this.crisEntityName="price";
	}
	
	


	public boolean validate() {
		// TODO Auto-generated method stub
		return true;
	}




	@Override
	protected ArrayList<File> getXmlFromApi() {
		// TODO Auto-generated method stub
		return null;
	}







	
}
