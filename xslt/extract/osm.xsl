<?xml version="1.0" encoding="UTF-8"?>

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

<!DOCTYPE stylesheet [
    <!ENTITY ical "http://www.w3.org/2002/12/cal/ical#">
    <!ENTITY xsd  "http://www.w3.org/2001/XMLSchema#" >
    <!ENTITY rdf  "http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    <!ENTITY rdfs "http://www.w3.org/2000/01/rdf-schema#" >
    <!ENTITY foaf "http://xmlns.com/foaf/0.1#">
    <!ENTITY losm "http://purl.org/ifgi/losm#">
    <!ENTITY losmr "http://losm.uni-muenster.de/resource/">
    <!ENTITY dc "http://purl.org/dc/elements/1.1/#">
    <!ENTITY geo "http://www.w3.org/2003/01/geo/wgs84_pos#">
    <!ENTITY georss "http://www.georss.org/georss#">
    <!ENTITY owl "http://www.w3.org/2002/07/owl#">
    
    
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
    
<xsl:template name="replace-string">
    <xsl:param name="text"/>
    <xsl:param name="replace"/>
    <xsl:param name="with"/>
    <xsl:choose>
      <xsl:when test="contains($text,$replace)">
        <xsl:value-of select="substring-before($text,$replace)"/>
        <xsl:value-of select="$with"/>
        <xsl:call-template name="replace-string">
          <xsl:with-param name="text" select="substring-after($text,$replace)"/>
          <xsl:with-param name="replace" select="$replace"/>
          <xsl:with-param name="with" select="$with"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
    
    
<!-- create resource and add all static information as literal or uri properties -->
<xsl:template match="//node" mode="krextor:main">
 <xsl:if test="count(child::*) > 1 and tag">
	<!--extract information into variables-->
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="uid" select="@uid"/>
        <xsl:variable name="user" select="@user"/>
        <xsl:variable name="lat" select="@lat"/>
        <xsl:variable name="lon" select="@lon"/>
         <xsl:variable name="timestamp" select="@timestamp"/>
  	
	<!--create root resource using the id as unique identifier-->
	<xsl:call-template name="krextor:create-resource">
    	<xsl:with-param name="subject" select="concat('&losmr;node/',$id)"/>
    	<xsl:with-param name="properties">
            <krextor:property uri="&dc;creator" object="{concat('&losmr;user/',$uid)}"/>
            <krextor:property uri="&geo;lat" value="{$lat}" datatype="&xsd;double"/>
            <krextor:property uri="&geo;lon" value="{$lon}" datatype="&xsd;double"/>
             <krextor:property uri="&georss;point" value="{$lat} {$lon}"/>
             <krextor:property uri="&xsd;dateTime" value="{$timestamp}" datatype="&xsd;dateTime"/>
        </xsl:with-param>
	</xsl:call-template>	  
</xsl:if>
</xsl:template>

<xsl:template name="key-value-rules" match="//node/tag" mode="krextor:main">
    <xsl:variable name="key">
    	<xsl:value-of select="@k"/>
    </xsl:variable>
   	<xsl:variable name="value">
    	<xsl:value-of select="@v"/>
    </xsl:variable>
    <xsl:choose>
    	<!--conditions where key&&value mapping is defined-->
	    <!--add-literal-property-->
	 
		<!--conditions where only a key mapping is defined-->
	    <!--add-literal-property-->
	   	<xsl:when test="$key='name'">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&rdfs;','label')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$value"/>
	    	</xsl:call-template>
		</xsl:when>
		
		
		<xsl:when test="$value='yes'">
	    	<xsl:call-template name="krextor:add-uri-property">
	        	<xsl:with-param name="property" select="concat('&rdfs;','type')"/>
	        	<xsl:with-param name="object" select="concat('&losm;',$key)"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="contains($key,'sameAs')">
			<xsl:call-template name="krextor:add-uri-property">
	        	<xsl:with-param name="property" select="concat('&owl;','sameAs')"/>
	        	<xsl:with-param name="object" select="@v"/>
	    	</xsl:call-template>
		</xsl:when>
		
		

		
		<xsl:when test="contains($key,'amenity') or contains($key,'tourism') or contains($key,'barrier') or contains($key,'highway') or contains($key,'railway') or contains($key,'areaway') or contains($key,'power') or contains($key,'leisure') or contains($key,'man_made') or contains($key,'office') or contains($key,'shop') or contains($key,'craft') or contains($key,'emergency') or contains($key,'historic') or contains($key,'military') or contains($key,'landuse') or contains($key,'natural') or contains($key,'geological') or contains($key,'sport') or contains($key,'abutters') or contains($key,'accessories')">
	    	<xsl:call-template name="krextor:add-uri-property">
	        	<xsl:with-param name="property" select="concat('&rdfs;','type')"/>
	        	<xsl:with-param name="object" select="concat('&losm;',$value)"/>
	    	</xsl:call-template>
	    	</xsl:when>
	    <xsl:otherwise> 
	
	 


	    </xsl:otherwise> 
    </xsl:choose>

</xsl:template>



</stylesheet>
