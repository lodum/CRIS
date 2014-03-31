package de.ifgi.lodum.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.log4j.Logger;

import de.ifgi.lodum.config.ConfigProvider;


/**
 * Class to retrieve, cache and clean remote xml files 
 * @author johannes
 *
 */
public class XmlCacher {	
	protected Logger log =Logger.getLogger(this.getClass().getName());
	
	/**
	 * @param url Remote XML file
	 * @param filename	The filename under which the streamed remote xml should be stored
	 * @param config Class encapsulating the config file, where the path to the cache folder is defined 
	 * @return
	 * @throws IOException
	 */
	public File getXML(String url,String filename,ConfigProvider config) throws IOException {


		File file = new File(config.getProperty("cacheFolder")+filename);
		//check if file alread exists in local cache folder
		if(!file.exists()){
			URL downloadUrl = new URL(
					url);
			InputStream in=null;
			try{
			
			// clean xml stream from invalid xml 
			in = new XmlCleaner(downloadUrl.openStream());
			}catch(FileNotFoundException e){
				e.printStackTrace();
				return null;
			}
			


			int len=0;
			byte[] buffer = new byte[10000];
			try {
				len = in.read(buffer);
			}catch(IOException ex){
				
			}
			//check if the xml contains more than the header <?xml version="1.0" encoding="UTF-8" standalone="yes"?><infoObjects/>
			if(buffer[69]!=0){
				OutputStream out = new FileOutputStream(file);
				try{

					while (len > 0) {
						out.write(buffer, 0, len);
						len = in.read(buffer);
					}
				} finally {
					out.close();
					in.close();
				}
			}else{
				return null;
			}
			
			
		}
	
	return file;	
	}

}
