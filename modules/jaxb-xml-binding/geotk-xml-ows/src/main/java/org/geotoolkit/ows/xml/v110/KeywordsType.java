/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ows.xml.v110;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * For OWS use, the optional thesaurusName element was omitted as being complex information that could be referenced by the codeSpace attribute of the Type element. 
 * 
 * <p>Java class for KeywordsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KeywordsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Keyword" type="{http://www.opengis.net/ows/1.1}LanguageStringType" maxOccurs="unbounded"/>
 *         &lt;element name="Type" type="{http://www.opengis.net/ows/1.1}CodeType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeywordsType", propOrder = {
    "keyword",
    "type"
})
public class KeywordsType {

    @XmlElement(name = "Keyword", required = true)
    private List<LanguageStringType> keyword;
    @XmlElement(name = "Type")
    private CodeType type;

    /**
     * Empty constructor used by JAXB.
     */
    KeywordsType(){
    }
    
    /**
     * Build a new list of keywords.
     */
    public KeywordsType(List<LanguageStringType> keyword, CodeType type){
        this.keyword = keyword;
        this.type    = type;
    }
    
    /**
     * Build a simple list of keywords without type.
     * all the element of the list are in the parameters. 
     */
    public KeywordsType(LanguageStringType... keywords){
        this.keyword = new ArrayList<LanguageStringType>();
        for (LanguageStringType element: keywords){
            keyword.add(element);
        }
    }
    
    /**
     * Gets the value of the keyword property.
     */
    public List<LanguageStringType> getKeyword() {
        if (keyword == null) {
            keyword = new ArrayList<LanguageStringType>();
        }
        return Collections.unmodifiableList(keyword);
    }

    /**
     * Gets the value of the type property.
     */
    public CodeType getType() {
        return type;
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof KeywordsType) {
            final KeywordsType that = (KeywordsType) object;
            return Utilities.equals(this.keyword, that.keyword) &&
                   Utilities.equals(this.type,    that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.keyword != null ? this.keyword.hashCode() : 0);
        hash = 43 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("keywords:").append('\n');
        if (keyword != null) {
            for (int i = 0; i < keyword.size(); i++) {
                s.append(keyword.get(i).toString()).append('\n');
            }
        }
        if (type != null) {
            s.append("type=" + type.toString());
        }
        return s.toString();
    }


}
