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
    <!ENTITY dct "http://purl.org/dc/terms/">
    <!ENTITY geo "http://www.w3.org/2003/01/geo/wgs84_pos#">
    <!ENTITY georss "http://www.georss.org/georss#">
    <!ENTITY owl "http://www.w3.org/2002/07/owl#">
    <!ENTITY vcard "http://www.w3.org/2006/vcard/ns#">
    <!ENTITY doac "http://ramonantonio.net/doac/0.1/">
    <!ENTITY bio "http://purl.org/vocab/bio/0.1/">
    <!ENTITY resume "http://rdfs.org/resume-rdf/#">
    <!ENTITY ti "http://www.ontologydesignpatterns.org/cp/owl/timeinterval.owl#">
    <!ENTITY tis "http://www.ontologydesignpatterns.org/cp/owl/timeindexedsituation.owl#">
    
    
]>


<stylesheet xmlns="http://www.w3.org/1999/XSL/Transform" 
    xpath-default-namespace=""
    xmlns:xd="http://www.pnp-software.com/XSLTdoc"
    xmlns:krextor="http://kwarc.info/projects/krextor"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:math="http://www.jclark.com/xt/java/java.lang.Math"
    xmlns:md5="java:de.ifgi.lodum.util.Md5"
    xmlns:fn="http://www.w3.org/TR/xpath-functions/"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:util="http://lodum.de/xsl/util/"
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
    	<xsl:with-param name="subject" select="concat('&cris;workhistory/',$id)"/>
    	
    	<xsl:with-param name="properties">
            <krextor:property uri="&rdf;type" object="&resume;WorkHistory"/>
				<krextor:property uri="&dct;modified" datatype="&xsd;dateTime" value="{$update}"/>
        </xsl:with-param>
    	
    </xsl:call-template>

</xsl:template>

<xsl:template name="key-value-rules" match="//infoObject/attribute" mode="krextor:main">
    <xsl:variable name="varName" select="@name"/>
    <xsl:variable name="varLang" select="@language"/>
    <xsl:variable name="data" select="data"/>
    <xsl:variable name="id" select="../@id" />

     
    <xsl:choose>
	   	<xsl:when test="$varName='Name' and $varLang='1' and string-length(string($data))&gt;2">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&resume;','jobDescription')"/>
				<xsl:with-param name="language" select="'de'"/>
	        	<xsl:with-param name="object" select="normalize-unicode($data,'NFC')"/>
	    	</xsl:call-template>
		</xsl:when>
		
	   	<xsl:when test="$varName='Name' and $varLang='2' and string-length(string($data))&gt;2">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&resume;','jobDescription')"/>
				<xsl:with-param name="language" select="'en'"/>
	        	<xsl:with-param name="object" select="normalize-unicode($data,'NFC')"/>
	    	</xsl:call-template>
		</xsl:when>
		

	
		<xsl:when test="$varName='Start month' and string($data) and string-length(string($data))&gt;2">
			<xsl:choose>
			
				<xsl:when test="number(string-length(string($data)))=7">
						<xsl:variable name="varY" select="substring-after($data,'/')"/>
						<xsl:variable name="varM" select="substring-before($data,'/')"/>

			    		<xsl:call-template name="krextor:add-literal-property">
				        	<xsl:with-param name="property" select="'&resume;startDate'"/>
				        	<xsl:with-param name="datatype" select="'&xsd;gYearMonth'"/>
				        	<xsl:with-param name="object" select="concat(concat($varY,'-'),$varM)"/>
			    		</xsl:call-template>
			   
						<call-template name="krextor:create-resource">
		       				<with-param name="subject" select="concat('&cris;position/',$id,'/interval')" />
		        	            <with-param name="properties">
		        	            	    <krextor:property uri="&rdf;type" object="&ti;TimeInterval"/>
		                            	<krextor:property uri="&ti;hasIntervalStartDate" value="{concat(concat($varY,'-',$varM),'-01T00:00:00Z')}" datatype="&xsd;dateTime" />
		                       </with-param>
		            		<with-param name="related-via-properties" select="'&tis;includesTime'" tunnel="yes" />
		        		</call-template>	
		    	</xsl:when>
		    	
		    	<xsl:when test="number(string-length(string($data)))=4">
		    		<xsl:variable name="varY" select="$data"/>

		    	    <!--
		    	    <xsl:variable name="endMonth" select="../attribute[@name='End month']/data"/>
		    		<xsl:choose>
					<xsl:when test="number(string-length(string($endMonth)))&lt;2">
						<xsl:call-template name="krextor:add-literal-property">
			        	<xsl:with-param name="property" select="'&resume;endDate'"/>
			        	<xsl:with-param name="datatype" select="'&xsd;gYearMonth'"/>
			        	<xsl:with-param name="object" select="concat(concat($varY,'-'),'12')"/>
			    		</xsl:call-template>
					</xsl:when>
					</xsl:choose>
					-->

					
			    	<xsl:call-template name="krextor:add-literal-property">
			        	<xsl:with-param name="property" select="'&resume;startDate'"/>
			        	<xsl:with-param name="datatype" select="'&xsd;gYearMonth'"/>
			        	<xsl:with-param name="object" select="concat(concat($varY,'-'),'01')"/>
		    		</xsl:call-template>
		    		
		    	
					<call-template name="krextor:create-resource">
	       				<with-param name="subject" select="concat('&cris;position/',$id,'/interval')" />
	        	            <with-param name="properties">
	        	            	    <krextor:property uri="&rdf;type" object="&ti;TimeInterval"/>
	                            <krextor:property uri="&ti;hasIntervalStartDate" value="{concat(concat($varY,'-'),'01-01T00:00:00Z')}" datatype="&xsd;dateTime" />
	                       </with-param>
	            		<with-param name="related-via-properties" select="'&tis;includesTime'" tunnel="yes" />
	        		</call-template>	
		    		
		    	</xsl:when>
	    	</xsl:choose>
		</xsl:when>
		
		<xsl:when test="$varName='End month' and string($data) and string-length(string($data))&gt;2">
			<xsl:choose>
				<xsl:when test="number(string-length(string($data)))=7">
					<xsl:variable name="varY" select="substring-after($data,'/')"/>
					<xsl:variable name="varM" select="substring-before($data,'/')"/>
			    	<xsl:call-template name="krextor:add-literal-property">
			        	<xsl:with-param name="property" select="'&resume;endDate'"/>
			        	<xsl:with-param name="datatype" select="'&xsd;gYearMonth'"/>
			        	<xsl:with-param name="object" select="concat(concat($varY,'-'),$varM)"/>
		    		</xsl:call-template>
		    		
		    		<call-template name="krextor:create-resource">
	       				<with-param name="subject" select="concat('&cris;position/',$id,'/interval')" />
	        	            <with-param name="properties">
	        	            	    <krextor:property uri="&rdf;type" object="&ti;TimeInterval"/>
	        	            	    	<xsl:choose>
											<xsl:when test="number($varM)=1 or number($varM)=3 or number($varM)=5 or number($varM)=7 or number($varM)=8 or number($varM)=10 or number($varM)=12">
	                            				<krextor:property uri="&ti;hasIntervalEndDate" value="{concat(concat($varY,'-',$varM),'-31T23:59:59')}" datatype="&xsd;dateTime" />
	                       					</xsl:when>
	                       					<xsl:when test="number($varM)=2">
	                            				<krextor:property uri="&ti;hasIntervalEndDate" value="{concat(concat($varY,'-',$varM),'-28T23:59:59')}" datatype="&xsd;dateTime" />
	                       					</xsl:when>
	                       					<xsl:otherwise>
	                            				<krextor:property uri="&ti;hasIntervalEndDate" value="{concat(concat($varY,'-',$varM),'-30T23:59:59')}" datatype="&xsd;dateTime" />
	                       					</xsl:otherwise>
	    								</xsl:choose>
	                       </with-param>
	            		<with-param name="related-via-properties" select="'&tis;includesTime'" tunnel="yes" />
	        		</call-template>
		    	</xsl:when>
		    	<xsl:when test="number(string-length(string($data)))=4">
					<xsl:variable name="varY" select="$data"/>
					<xsl:variable name="varM" select="12"/>
			    	<xsl:call-template name="krextor:add-literal-property">
			        	<xsl:with-param name="property" select="'&resume;endDate'"/>
			        	<xsl:with-param name="datatype" select="'&xsd;gYearMonth'"/>
			        	<xsl:with-param name="object" select="concat(concat($varY,'-'),$varM)"/>
		    		</xsl:call-template>
		    		
		    		<call-template name="krextor:create-resource">
	       				<with-param name="subject" select="concat('&cris;position/',$id,'/interval')" />
	        	            <with-param name="properties">
	        	            	    <krextor:property uri="&rdf;type" object="&ti;TimeInterval"/>
	                            <krextor:property uri="&ti;hasIntervalEndDate" value="{concat(concat($varY,'-'),'12-31T23:59:59')}" datatype="&xsd;dateTime" />
	                       </with-param>
	            		<with-param name="related-via-properties" select="'&tis;includesTime'" tunnel="yes" />
	        		</call-template>
		    	</xsl:when>
	    	</xsl:choose>
	    	
		</xsl:when>
		
    </xsl:choose>


</xsl:template>



</stylesheet>
