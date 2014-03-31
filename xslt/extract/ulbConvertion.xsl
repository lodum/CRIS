<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE xsl:stylesheet [
      <!ENTITY ical "http://www.w3.org/2002/12/cal/ical#">
    <!ENTITY xsd  "http://www.w3.org/2001/XMLSchema#" >
    <!ENTITY rdfs "http://www.w3.org/2000/01/rdf-schema#" >
    <!ENTITY foaf "http://xmlns.com/foaf/0.1/">
    <!ENTITY lodum "http://data.uni-muenster.de/context/ulb/">
    <!ENTITY losmr "http://losm.uni-muenster.de/resource/">
    <!ENTITY dc "http://purl.org/dc/elements/1.1/">
    <!ENTITY geo "http://www.w3.org/2003/01/geo/wgs84_pos#">
    <!ENTITY georss "http://www.georss.org/georss#">
    <!ENTITY owl "http://www.w3.org/2002/07/owl#">
    <!ENTITY bibo "http://purl.org/ontology/bibo/">
    <!ENTITY dcterms "http://purl.org/dc/terms/">
    <!ENTITY gn "http://www.geonames.org/ontology#">
     <!ENTITY ex "http://www.example.org/ontology#">
     <!ENTITY rdf 	"http://www.w3.org/1999/02/22-rdf-syntax-ns#">
]>

<stylesheet xmlns="http://www.w3.org/1999/XSL/Transform" 
    xmlns:krextor="http://kwarc.info/projects/krextor"
    exclude-result-prefixes="#all"
    version="2.0">

    <param name="autogenerate-fragment-uris" select="'pseudo-xpath'"/>

    <template match="documents" mode="krextor:main">
    
        <!-- prevent <fakulties/> children from being processed here -->
        <apply-templates select="document" mode="krextor:main"/>
    </template>

    <template match="document" mode="krextor:main">
    	<variable name="id" select="id" />
    	<variable name="type_functional" select="type_functional" />
        <call-template name="krextor:create-resource">
        	<with-param name="subject" select="concat('&lodum;',$id)" />
 			<with-param name="properties">
 			
 				 <choose>
	   				<when test="$type_functional='book'">
	   				    <krextor:property  uri="&rdf;type" object="&bibo;Book"/>
	   				
	   				</when>
	   				<when test="$type_functional='article'">
	   				    <krextor:property  uri="&rdf;type" object="&bibo;Article"/>
	   				
	   				</when>
	   				<when test="$type_functional='dvd'">
	   				    <krextor:property  uri="&rdf;type" object="&bibo;AudioVisualDocument"/>
	   				
	   				</when>
	   				<when test="$type_functional='cdrom'">
	   				    <krextor:property  uri="&rdf;type" object="&bibo;AudioVisualDocument"/>
	   				
	   				</when>
	   				
	   				  <otherwise> 
							<krextor:property  uri="&rdf;type" object="&bibo;Document"/>
	    			  </otherwise> 
	   			</choose>
    			<!-- <krextor:property  uri="&rdf;type" object="&bibo;{$type_functional}"/> -->
           <!-- process all <fakulties/> elements that immediately follow -->
        </with-param>  
        </call-template>
        
    </template>

 

    <!-- we assume that there are only <name/> elements in this context;
         otherwise one can disambiguate them as "fakulties/name" -->
    	<template match="contributor" mode="krextor:main" >
		<call-template name="krextor:add-literal-property">
             <with-param name="property" select="'&dc;contributor'"/>
        </call-template>
	</template>

	<!--template for publish date -->
	<template match="publish_date" mode="krextor:main">
		<call-template name="krextor:add-literal-property">
             <with-param name="property" select="'&dcterms;issued'"/>
        </call-template>
	</template>

	<!--template for title -->
	<template match="document/title" mode="krextor:main">
		<call-template name="krextor:add-literal-property">
             <with-param name="property" select="'&dcterms;title'"/>
        </call-template>
	</template>
	
	<template match="document/subtitle" mode="krextor:main">
		<call-template name="krextor:add-literal-property">
             <with-param name="property" select="'&dcterms;alternative'"/>
        </call-template>
	</template>
	
	<!--template for author -->
	<template match="author" mode="krextor:main">
		<call-template name="krextor:add-literal-property">
             <with-param name="property" select="'&bibo;authorlist'"/>
        </call-template>
	</template>
	
	<!--template for isbn13 -->
	<template match="isbn13" mode="krextor:main">
		<call-template name="krextor:add-literal-property">
             <with-param name="property" select="'&bibo;isbn13'"/>
        </call-template>
	</template>
	
	<!--template for isbn10 -->
	<template match="isbn10" mode="krextor:main">
		<call-template name="krextor:add-literal-property">
             <with-param name="property" select="'&bibo;isbn10'"/>
        </call-template>
	</template>
	
	<!--template for edition -->
	<template match="edition" mode="krextor:main">
		<call-template name="krextor:add-literal-property">
             <with-param name="property" select="'&bibo;edition'"/>
        </call-template>
	</template>
	
	<!--template for author -->
	<template match="description" mode="krextor:main">
		<call-template name="krextor:add-literal-property">
             <with-param name="property" select="'&dc;description'"/>
        </call-template>
	</template>

    <template match="parent" mode="krextor:main">
    <variable name="id2" select="id" />
	<variable name="title" select="title" />
		
	
        <call-template name="krextor:create-resource">
        	<with-param name="subject" select="concat('&lodum;',$id2)" />
        
           
            <with-param name="related-via-properties" select="'&dcterms;isPartOf'" tunnel="yes" />
            <with-param name="properties">
                <krextor:property uri="&dcterms;title" value="{$title}"/>
                
                
            </with-param>
        </call-template>
    </template>
    
    <template match="child" mode="krextor:main">
    <variable name="id3" select="id" />
	<variable name="title2" select="title" />
        <call-template name="krextor:create-resource">
        	<with-param name="subject" select="concat('&lodum;',$id3)" />
        
           
            <with-param name="related-via-properties" select="'&dcterms;hasPart'" tunnel="yes" />
            <with-param name="properties">
                <krextor:property uri="&dcterms;title" value="{$title2}"/>
            </with-param>
        </call-template>
    </template>
    
    <template match="publisher" mode="krextor:main">
    <variable name="id3" select="text()" />
    <variable name="hash">
		<value-of select="md5:md5($id3)" xmlns:md5="java:net.sf.saxon.functions.Md5"/>
	</variable>
	<variable name="publish_country" select="parent::node()/publish_country" />
	<variable name="publisher_city" select="parent::node()/publisher_city" />
	
        <call-template name="krextor:create-resource">
        	<with-param name="subject" select="concat('&lodum;Publisher/',$hash)" />
        	            <with-param name="properties">
        	
        	<!--    <krextor:property uri="&gn;parentCountry" value="{$publish_country}"/> -->
                                <krextor:property uri="&foaf;name" value="{$id3}"/>
                                <krextor:property uri="&gn;name" value="{$publisher_city}"/>
        	   
                
                       </with-param>
           
            <with-param name="related-via-properties" select="'&dcterms;publisher'" tunnel="yes" />
             
                
                
        </call-template>	
    </template>
</stylesheet>
