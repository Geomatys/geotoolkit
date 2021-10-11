/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractKeywords;


/**
 * For OWS use, the optional thesaurusName element was
 *       omitted as being complex information that could be referenced by the
 *       codeSpace attribute of the Type element.
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
 *         &lt;element name="Keyword" type="{http://www.opengis.net/ows/2.0}LanguageStringType" maxOccurs="unbounded"/>
 *         &lt;element name="Type" type="{http://www.opengis.net/ows/2.0}CodeType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeywordsType", propOrder = {
    "keyword",
    "type"
})
public class KeywordsType implements AbstractKeywords {

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
    public KeywordsType(final List<String> keyword){
        this.keyword = new ArrayList<>();
        if (keyword != null) {
            for (String k : keyword){
                this.keyword.add(new LanguageStringType(k));
            }
        }
    }

    /**
     * Build a new list of keywords.
     */
    public KeywordsType(final List<LanguageStringType> keyword, final CodeType type){
        this.keyword = keyword;
        this.type    = type;
    }

    /**
     * Build a simple list of keywords without type.
     * all the element of the list are in the parameters.
     */
    public KeywordsType(final LanguageStringType... keywords){
        this.keyword = new ArrayList<>();
        for (LanguageStringType element: keywords){
            keyword.add(element);
        }
    }

    /**
     * Build a simple list of keywords without type.
     * all the element of the list are in the parameters.
     */
    public KeywordsType(final String... keywords){
        this.keyword = new ArrayList<>();
        for (String element: keywords){
            keyword.add(new LanguageStringType(element));
        }
    }

    /**
     * Gets the value of the keyword property.
     *
     */
    public List<LanguageStringType> getKeyword() {
        if (keyword == null) {
            keyword = new ArrayList<>();
        }
        return this.keyword;
    }

    @Override
    public List<String> getKeywordList() {
        List<String> keywordList = new ArrayList<>();
        if (keyword == null) {
            keyword = new ArrayList<>();
        } else {
            for (LanguageStringType ls : keyword) {
                keywordList.add(ls.getValue());
            }
        }
        return keywordList;
    }


    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setType(CodeType value) {
        this.type = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (type != null) {
            sb.append("type:").append(type).append('\n');
        }
        if (keyword != null) {
            sb.append("keyword:\n");
            for (LanguageStringType jb : keyword) {
                sb.append(jb).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     *
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof KeywordsType) {
            final KeywordsType that = (KeywordsType) object;
            return Objects.equals(this.keyword, that.keyword) &&
                   Objects.equals(this.type, that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.keyword);
        hash = 97 * hash + Objects.hashCode(this.type);
        return hash;
    }
}
