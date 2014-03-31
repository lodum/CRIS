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
    <!ENTITY xsd  "http://www.w3.org/2001/XMLSchema#">
    <!ENTITY rdfs "http://www.w3.org/2000/01/rdf-schema#">
    <!ENTITY rdf  "http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    <!ENTITY foaf "http://xmlns.com/foaf/0.1/">
    <!ENTITY dc "http://purl.org/dc/elements/1.1/">
    <!ENTITY dct "http://purl.org/dc/terms/">
    <!ENTITY geo "http://www.w3.org/2003/01/geo/wgs84_pos#">
    <!ENTITY georss "http://www.georss.org/georss#">
    <!ENTITY owl "http://www.w3.org/2002/07/owl#">
    <!ENTITY vcard "http://www.w3.org/2006/vcard/ns#">
    <!ENTITY xml "http://www.asdfas.de#">
    <!ENTITY bio "http://purl.org/vocab/bio/0.1/">
    <!ENTITY bibo "http://purl.org/ontology/bibo/">
    <!ENTITY dct "http://purl.org/dc/terms/">
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
    	<xsl:variable name="dateTest" select="xs:date(concat(substring($currentDay,1.0,4.0),'-',substring($currentDay,6.0,2.0),'-',substring($currentDay,9.0,2.0)))- xs:dayTimeDuration('P1D')"/>
            
	<!--create root resource using the id as unique identifier-->
	<xsl:if test="number(translate(string($updateDay), '-', ''))>number(translate(string($dateTest), '-', ''))">
	<xsl:call-template name="krextor:create-resource">
    	<xsl:with-param name="subject" select="concat('&cris;publication/',$id)"/>
    	
    	<xsl:with-param name="properties">
           <krextor:property uri="&rdf;type" object="&bibo;Document"/>
            <krextor:property uri="http://vocab.lodum.de/helper/pubID" value="{$id}"/>
            <krextor:property uri="&dct;modified" datatype="&xsd;dateTime" value="{$update}"/>
        </xsl:with-param>
        
    	
    </xsl:call-template>
    </xsl:if>

</xsl:template>


<xsl:template name="key-value-rules" match="//infoObject/attribute" mode="krextor:main">
	<xsl:variable name="varName" select="@name"/>
	<xsl:variable name="varLang" select="@language"/>
	<xsl:variable name="data" select="normalize-unicode(util:strip-tags(data),'NFC')"/>


    <xsl:choose>
	   	<!--<xsl:when test="$varName='Title' and $varLang='1' and string($data)"> -->
	    	<xsl:when test="$varName='Title' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&dct;','title')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Publication year' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&dct;','issued')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;gYear'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Abstract' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&bibo;','abstract')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Keywords' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&dct;','subject')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='Authors' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
             <with-param name="property" select="'&bibo;authorlist'"/>
	    	</xsl:call-template>
		</xsl:when>
		
	 <xsl:when test="$varName='Publisher' and string($data)">
   				<xsl:variable name="hash" select="md5:md5(string($data))" />
				<call-template name="krextor:create-resource">
       				<with-param name="subject" select="concat('&cris;publication/publisher/',$hash)" />
        	            <with-param name="properties">
        	            	    <krextor:property uri="&rdf;type" object="&foaf;Organization"/>
                            <krextor:property uri="&foaf;name" value="{string($data)}" datatype="&xsd;string" />
                       </with-param>
            		<with-param name="related-via-properties" select="'&dct;publisher'" tunnel="yes" />
        		</call-template>	
	    	<!--	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&dct;','publisher')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template> -->
		</xsl:when>
		
		<xsl:when test="$varName='Conference' and string($data)">
				<xsl:variable name="chash" select="md5:md5(string($data))" />
				<call-template name="krextor:create-resource">
       				<with-param name="subject" select="concat('&cris;publication/conference/',$chash)" />
        	            <with-param name="properties">
        	            	    <krextor:property uri="&rdf;type" object="&bibo;Conference"/>
                                <krextor:property uri="&foaf;name" value="{string($data)}" datatype="&xsd;string" />
                       </with-param>
            		<with-param name="related-via-properties" select="'&bibo;presentedAt'" tunnel="yes" />
        		</call-template>	
	    	 	 <!--  <xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&bibo;','presented')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>-->
		</xsl:when> 
		
		<xsl:when test="$varName='DOI' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&bibo;','doi')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='ISBN' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&bibo;','isbn')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$varName='ISSN' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&bibo;','issn')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when>
		
		<!-- <xsl:when test="$varName='Language' and string($data)">
	    	<xsl:call-template name="krextor:add-literal-property">
	        	<xsl:with-param name="property" select="concat('&bibo;','language')"/>
	        	<xsl:with-param name="datatype" select="'&xsd;string'"/>
	        	<xsl:with-param name="object" select="$data"/>
	    	</xsl:call-template>
		</xsl:when> -->
		
		
	<!-- Commented because of URI errors	-->
		<xsl:when test="$varName='URL' and string($data)">
	    	<xsl:call-template name="krextor:add-uri-property">
	        	<xsl:with-param name="property" select="concat('&foaf;','homepage')"/>
	        	<xsl:with-param name="object" select="escape-html-uri($data)"/>
	    	</xsl:call-template>
		</xsl:when> 
		
		
		<xsl:when test="$varName='Publication type' and string($data)">

	    	
	    		<xsl:choose>
	    			<xsl:when test="$data='212'">
	    				<xsl:call-template name="krextor:add-uri-property"> <!--BookMonographie-->
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','Book')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	        	<xsl:when test="$data='569'">
	    				<xsl:call-template name="krextor:add-uri-property">
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','Collection')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	        		<xsl:when test="$data='394'">
	    				<xsl:call-template name="krextor:add-uri-property"><!--BookChapter-->
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','BookSection')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	    			<xsl:when test="$data='570'">
	    				<xsl:call-template name="krextor:add-uri-property"><!--ConferencePaper-->
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','Article')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	        		<xsl:when test="$data='1567'"> <!--Workshop/Poster-->
	    				<xsl:call-template name="krextor:add-uri-property">
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','Article')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	        		<xsl:when test="$data='210'"><!--Journal-->
	    				<xsl:call-template name="krextor:add-uri-property"> 
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','AcademicArticle')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	        		<xsl:when test="$data='1566'">
	    				<xsl:call-template name="krextor:add-uri-property"><!--ArticleNewspaper-->
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','Document')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	        		<xsl:when test="$data='1568'">
	    				<xsl:call-template name="krextor:add-uri-property"><!--EncyclopediaArticle-->
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','ReferenceSource')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	        		<xsl:when test="$data='568'">
	    				<xsl:call-template name="krextor:add-uri-property"><!--ReviewArticle-->
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','Document')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	        		<xsl:when test="$data='1569'">
	    				<xsl:call-template name="krextor:add-uri-property">
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','Thesis')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	        		<xsl:when test="$data='211'">
	    				<xsl:call-template name="krextor:add-uri-property"><!--Report-->
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','Report')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	        			<xsl:when test="$data='1644'"> <!--AudioVisual-->
	    				<xsl:call-template name="krextor:add-uri-property">
	        			<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','AudioVisualDocument')"/>
	        			</xsl:call-template>
	        		</xsl:when>
	        	   <xsl:otherwise> 
	        	   		<xsl:call-template name="krextor:add-uri-property">
		        		<xsl:with-param name="property" select="concat('&rdf;','type')"/>
	        			<xsl:with-param name="object" select="concat('&bibo;','Document')"/>
	        			</xsl:call-template>
	   				</xsl:otherwise> 
	    	</xsl:choose>

		</xsl:when>

		
	    <xsl:otherwise> 
	
	 


	    </xsl:otherwise> 
    </xsl:choose>

</xsl:template>



</stylesheet>
