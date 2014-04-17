package de.ifgi.lodum.config;

public class GlobalSettings {

	public static String endpoint = "http://giv-lodum.uni-muenster.de:8080/openrdf-sesame/repositories/lod4wfs2";

	public static String getEndpoint() {
		return endpoint;
	}

	public static void setEndpoint(String endpoint) {
		GlobalSettings.endpoint = endpoint;
	}

	
}


