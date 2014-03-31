package de.ifgi.lodum.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Johannes Trame
 *
 */
public class ConfigProvider extends Properties{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Properties prop;

	public ConfigProvider() {
		prop = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("config/lodum.xml");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			prop.loadFromXML(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void clear() {
		prop.clear();
	}


	public Object clone() {
		return prop.clone();
	}


	public boolean contains(Object arg0) {
		return prop.contains(arg0);
	}


	public boolean containsKey(Object arg0) {
		return prop.containsKey(arg0);
	}


	public boolean containsValue(Object arg0) {
		return prop.containsValue(arg0);
	}


	public Enumeration<Object> elements() {
		return prop.elements();
	}


	public Set<java.util.Map.Entry<Object, Object>> entrySet() {
		return prop.entrySet();
	}


	public boolean equals(Object arg0) {
		return prop.equals(arg0);
	}


	public Object get(Object arg0) {
		return prop.get(arg0);
	}


	public String getProperty(String key, String defaultValue) {
		return prop.getProperty(key, defaultValue);
	}


	public String getProperty(String key) {
		return prop.getProperty(key);
	}


	public int hashCode() {
		return prop.hashCode();
	}


	public boolean isEmpty() {
		return prop.isEmpty();
	}


	public Set<Object> keySet() {
		return prop.keySet();
	}


	public Enumeration<Object> keys() {
		return prop.keys();
	}


	public void list(PrintStream out) {
		prop.list(out);
	}


	public void list(PrintWriter out) {
		prop.list(out);
	}


	public void load(InputStream inStream) throws IOException {
		prop.load(inStream);
	}


	public void load(Reader reader) throws IOException {
		prop.load(reader);
	}


	public void loadFromXML(InputStream in) throws IOException,
			InvalidPropertiesFormatException {
		prop.loadFromXML(in);
	}


	public Enumeration<?> propertyNames() {
		return prop.propertyNames();
	}


	public Object put(Object arg0, Object arg1) {
		return prop.put(arg0, arg1);
	}


	public void putAll(Map<? extends Object, ? extends Object> arg0) {
		prop.putAll(arg0);
	}


	public Object remove(Object arg0) {
		return prop.remove(arg0);
	}


	public void save(OutputStream out, String comments) {
		//prop.save(out, comments);
		try {
			prop.store(out, comments);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public Object setProperty(String key, String value) {
		return prop.setProperty(key, value);
	}


	public int size() {
		return prop.size();
	}


	public void store(OutputStream out, String comments) throws IOException {
		prop.store(out, comments);
	}


	public void store(Writer writer, String comments) throws IOException {
		prop.store(writer, comments);
	}


	public void storeToXML(OutputStream os, String comment, String encoding)
			throws IOException {
		prop.storeToXML(os, comment, encoding);
	}


	public void storeToXML(OutputStream os, String comment) throws IOException {
		prop.storeToXML(os, comment);
	}


	public Set<String> stringPropertyNames() {
		return prop.stringPropertyNames();
	}


	public String toString() {
		return prop.toString();
	}


	public Collection<Object> values() {
		return prop.values();
	}





}
