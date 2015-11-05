/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.thw.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.skos.xml.Concept;


/**
 * <p>Java class for getConceptsMatchingKeywordResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getConceptsMatchingKeywordResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.w3.org/2004/02/skos/core#}Concept" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getConceptsMatchingKeywordResponse", propOrder = {
    "response",
    "labels",
    "matching"
})
@XmlRootElement(name = "GetConceptsMatchingKeywordResponse", namespace = "http://ws.geotk.org/")
public class GetConceptsMatchingKeywordResponse {

    @XmlElement(name = "return")
    private List<Concept> response;

    @XmlElement(name = "label")
    private List<String> labels;
    
    @XmlElement(name = "matching")
    private List<Match> matching;

    public GetConceptsMatchingKeywordResponse() {

    }

    public GetConceptsMatchingKeywordResponse(final List<Concept> response, final List<String> labels, final Map<String, List<String>> matching) {
        this.response = response;
        this.labels   = labels;
        if (matching != null) {
            this.matching = new ArrayList<Match>();
            for (Entry<String, List<String>> entry : matching.entrySet()) {
                this.matching.add(new Match(entry.getKey(), entry.getValue()));
            }
        }
    }
    
    /**
     * Gets the value of the return property.
     */
    public List<Concept> getReturn() {
        if (response == null) {
            response = new ArrayList<Concept>();
        }
        return this.response;
    }

    /**
     * @return the labels
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * @return the matching
     */
    public Map<String, List<String>> getMatching() {
        final Map<String, List<String>> result = new HashMap<String, List<String>>();
        if (matching != null) {
            for (Match m : matching) {
                result.put(m.thesaurusURI, m.conceptURI);
            }
        }
        return result;
    }

    private static class Match {
        
        public String thesaurusURI;
        
        public List<String> conceptURI;
        
        public Match() {
            
        }
        
        public Match(final String thesaurusUri, final List<String> conceptURI) {
            this.conceptURI   = conceptURI;
            this.thesaurusURI = thesaurusUri;
        }
    }

}
