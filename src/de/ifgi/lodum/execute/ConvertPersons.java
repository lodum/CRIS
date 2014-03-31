package de.ifgi.lodum.execute;



import de.ifgi.lodum.objects.CrisPerson;


public class ConvertPersons {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CrisPerson("http://data.uni-muenster.de/context/cris/person/").crawlConvertCommit();

	}
	

	


}
