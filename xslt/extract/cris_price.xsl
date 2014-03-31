<?xml version="1.0" encoding="UTF-8" standalone="no"?>

<!--
    *  
    *	Johannes Trame
    *	Institut for Geoinformatics
    *
    *	based on work of 
    *  	Christoph Lange
    *  	KWARC, Jacobs University Bremen
    *  	http://kwarc.info/projects/krextor/
    *
    *   Krextor is free software; you can redistribute it and/or
    * 	modify it under the terms of the GNU Lesser General Public
    * 	License as published by the Free Software Foundation; either
    * 	version 2 of the License, or (at your option) any later version.
    *
    * 	This program is distributed in the hope that it will be useful,
    * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
    * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    * 	Lesser General Public License for more details.
    *
    * 	You should have received a copy of the GNU Lesser General Public
    * 	License along with this library; if not, write to the
    * 	Free Software Foundation, Inc., 59 Temple Place - Suite 330,
    * 	Boston, MA 02111-1307, USA.
    * 
-->
<!--<!DOCTYPE stylesheet SYSTEM "stylesheet.dtd" >-->

<!DOCTYPE stylesheet [
	<!ENTITY cris "http://data.uni-muenster.de/context/cris/">
    <!ENTITY ical "http://www.w3.org/2002/12/cal/ical#">
    <!ENTITY xsd  "http://www.w3.org/2001/XMLSchema#" >
    <!ENTITY rdfs "http://www.w3.org/2000/01/rdf-schema#" >
    <!ENTITY rdf  "http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    <!ENTITY foaf "http://xmlns.com/foaf/0.1/">
    <!ENTITY dc "http://purl.org/dc/elements/1.1/">
    <!ENTITY geo "http://www.w3.org/2003/01/geo/wgs84_pos#">
    <!ENTITY georss "http://www.georss.org/georss#">
    <!ENTITY owl "http://www.w3.org/2002/07/owl#">
    <!ENTITY vcard "http://www.w3.org/2006/vcard/ns#">
    <!ENTITY doac "http://ramonantonio.net/doac/0.1/">
    <!ENTITY bio "http://purl.org/vocab/bio/0.1/">
    <!ENTITY res "http://www.medsci.ox.ac.uk/vocab/researchers/0.1/">
    
    
]>


<stylesheet xmlns="http://www.w3.org/1999/XSL/Transform" 
    xpath-default-namespace=""
    xmlns:xd="http://www.pnp-software.com/XSLTdoc"
    xmlns:krextor="http://kwarc.info/projects/krextor"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:math="http://www.jclark.com/xt/java/java.lang.Math"
    exclude-result-prefixes="#all"
    version="2.0">
    <param name="debug" select="true()"/>
    
    
    
<!-- create resource and add all static information as literal or uri properties -->
<xsl:template match="//infoObject" mode="krextor:main">

	<!--extract information into variables-->
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="update" select="substring-before(@updatedOn,'.')"/>
 
  	
	<!--create root resource using the id as unique identifier-->
	<xsl:call-template name="krextor:create-resource">
    	<xsl:with-param name="subject" select="concat('&cris;price/',$id)"/>
    	
    	<xsl:with-param name="properties">
            <krextor:property uri="&rdf;type" object="&res;Award"/>
             <krextor:property uri="http://vocab.lodum.de/helper/priceID" value="{$id}"/>
             <krextor:property uri="http://vocab.lodum.de/helper/lastUpdate" datatype="&xsd;dateTime" value="{$update}"/>
        </xsl:with-param>
    	
    </xsl:call-template>

</xsl:template>

<xsl:template name="key-value-rules" match="//infoObject/attribute" mode="krextor:main">
    <xsl:variable name="varName" select="@name"/>
    <xsl:variable name="varLang" select="@language"/>
    <xsl:variable name="data" select="data"/>
     
     
    
    <xsl:choose>
	   	<xsl:when test="$varName='Name' and $varLang='1'  and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&dc;','title')"/>
	        	<xsl:with-param name="object" select="$data"/>
	        	<xsl:with-param name="language" select="'de'"/>
	    	</xsl:call-template>
		</xsl:when>
		
	   	<xsl:when test="$varName='Name' and $varLang='2' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&dc;','title')"/>
	        	<xsl:with-param name="language" select="'en'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Committee' and $varLang='1' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&res;','awardedBy')"/>
				<xsl:with-param name="language" select="'de'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Committee' and $varLang='2' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&res;','awardedBy')"/>
	        	<xsl:with-param name="object" select="$data"/>
	        	<xsl:with-param name="language" select="'en'"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Month' and string($data)">

			<xsl:choose>
			   	<xsl:when test="contains(string($data),'/')">
			    		<xsl:variable name="varY" select="substring-after($data,'/')"/>
						<xsl:variable name="varM" select="substring-before($data,'/')"/>
						<xsl:call-template name="krextor:add-literal-property">
			        	<xsl:with-param name="property" select="'&dc;date'"/>
				        <xsl:with-param name="datatype" select="'&xsd;gYearMonth'"/>
			        	<xsl:with-param name="object" select="concat(concat($varY,'-'),$varM)"/>
			    		</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
						<xsl:variable name="varY" select="$data"/>
						<xsl:call-template name="krextor:add-literal-property">
			        	<xsl:with-param name="property" select="'&dc;date'"/>
				        <xsl:with-param name="datatype" select="'&xsd;gYear'"/>
			        	<xsl:with-param name="object" select="$varY"/>
			    		</xsl:call-template>
				</xsl:otherwise> 
    		</xsl:choose>
		
	    	
		</xsl:when>
		
				<xsl:when test="$varName='Comment' and $varLang='1' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&rdfs;','comment')"/>
	        	<xsl:with-param name="object" select="$data"/>
	        	<xsl:with-param name="language" select="'de'"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Comment' and $varLang='2' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&rdfs;','comment')"/>
	        	<xsl:with-param name="language" select="'en'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		

		
	    <xsl:otherwise> 
	
	 


	    </xsl:otherwise> 
    </xsl:choose>


</xsl:template>



</stylesheet>
