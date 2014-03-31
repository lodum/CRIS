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
    <!ENTITY dc "http://purl.org/dc/elements/1.1/#">
    <!ENTITY geo "http://www.w3.org/2003/01/geo/wgs84_pos#">
    <!ENTITY georss "http://www.georss.org/georss#">
    <!ENTITY owl "http://www.w3.org/2002/07/owl#">
    <!ENTITY vcard "http://www.w3.org/2006/vcard/ns#">
    <!ENTITY doac "http://ramonantonio.net/doac/0.1/">
    <!ENTITY bio "http://purl.org/vocab/bio/0.1/">
    <!ENTITY doap "http://usefulinc.com/ns/doap#">
    <!ENTITY project "http://ebiquity.umbc.edu/ontology/project.owl#"> 
    <!ENTITY pv "http://linkedscience.org/pv/ns#"> 
    <!ENTITY dct "http://purl.org/dc/terms/">
    
]>


<stylesheet xmlns="http://www.w3.org/1999/XSL/Transform" 
    xpath-default-namespace=""
    xmlns:xd="http://www.pnp-software.com/XSLTdoc"
    xmlns:krextor="http://kwarc.info/projects/krextor"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:math="http://www.jclark.com/xt/java/java.lang.Math"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:fn="http://www.w3.org/TR/xpath-functions/"
    xmlns:util="http://lodum.de/xsl/util/"
    exclude-result-prefixes="#all"
    version="2.0">
    <param name="debug" select="true()"/>
    
<xsl:function name="util:strip-tags">
  <xsl:param name="text"/>
  <xsl:choose>
    <xsl:when test="contains($text, '&lt;')">
      <xsl:value-of select="concat(substring-before($text, '&lt;'),
        util:strip-tags(substring-after($text, '&gt;')))"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$text"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:function>
    
<!-- create resource and add all static information as literal or uri properties -->
<xsl:template match="//infoObject" mode="krextor:main">

	<!--extract information into variables-->
    <xsl:variable name="id" select="@id"/>
    <xsl:variable name="update" select="substring-before(@updatedOn,'.')"/>

    <xsl:variable name="updateDay" select="xs:date(substring-before(@updatedOn,'T'))"/>
    <xsl:variable name="currentDay" select="string(current-dateTime())"/>
    <xsl:variable name="dateTest" select="xs:date(concat(substring($currentDay,1.0,4.0),'-',substring($currentDay,6.0,2.0),'-',substring($currentDay,9.0,2.0)))- xs:dayTimeDuration('P1500D')"/>
     

  	<xsl:if test="number(translate(string($updateDay), '-', ''))>number(translate(string($dateTest), '-', ''))">
	<!--create root resource using the id as unique identifier-->
	<xsl:call-template name="krextor:create-resource">
    	<xsl:with-param name="subject" select="concat('&cris;project/',$id)"/>
    	
    	<xsl:with-param name="properties">
            <krextor:property uri="&rdf;type" object="&pv;ResearchProject"/>
            <krextor:property uri="http://vocab.lodum.de/helper/projectID" value="{$id}"/>
            <krextor:property uri="&dct;modified" datatype="&xsd;dateTime" value="{$update}"/>
        </xsl:with-param>
    	
    </xsl:call-template>
    </xsl:if>

</xsl:template>

<xsl:template name="key-value-rules" match="//infoObject/attribute" mode="krextor:main">
    <xsl:variable name="varName" select="@name"/>
    <xsl:variable name="varLang" select="@language"/>
    <xsl:variable name="data" select="data"/>
    

    
    <xsl:choose>
	   	<xsl:when test="$varName='Name' and $varLang='1' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&pv;','title')"/>
	        	<xsl:with-param name="language" select="'de'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
	   	<xsl:when test="$varName='Name' and $varLang='2' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&pv;','title')"/>
	        	<xsl:with-param name="language" select="'en'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Acronym' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&pv;','acronym')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Description' and $varLang='1' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&pv;','description')"/>
	        	<xsl:with-param name="language" select="'de'"/>
	        	<xsl:with-param name="object" select="normalize-unicode(util:strip-tags($data),'NFC')"/>
	    	</xsl:call-template>
		</xsl:when>
		
	   	<xsl:when test="$varName='Description' and $varLang='2' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&pv;','description')"/>
	        	<xsl:with-param name="language" select="'de'"/>
	        	<xsl:with-param name="object" select="normalize-unicode(util:strip-tags($data),'NFC')"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Project number' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&pv;','projectNumber')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Start month' and string($data)">
			<xsl:variable name="varY" select="substring-after($data,'/')"/>
			<xsl:variable name="varM" select="substring-before($data,'/')"/>
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="'&pv;date-starts'"/>
		        <xsl:with-param name="datatype" select="'&xsd;gYearMonth'"/>
	        	<xsl:with-param name="object" select="concat(concat($varY,'-'),$varM)"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='End month' and string($data)">
			<xsl:variable name="varY" select="substring-after($data,'/')"/>
			<xsl:variable name="varM" select="substring-before($data,'/')"/>
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="'&pv;date-ends'"/>
	        	<xsl:with-param name="datatype" select="'&xsd;gYearMonth'"/>
	        	<xsl:with-param name="object" select="concat(concat($varY,'-'),$varM)"/>
	    	</xsl:call-template>
		</xsl:when>
		
    </xsl:choose>


</xsl:template>



</stylesheet>
