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
package org.geotoolkit.sml.xml.v100;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractKeywordList;
import org.geotoolkit.util.Utilities;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="keyword" type="{http://www.w3.org/2001/XMLSchema}token" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="codeSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "keyword"
})
public class KeywordList implements AbstractKeywordList {

    @XmlElementRef(name = "keyword", namespace = "http://www.opengis.net/sensorML/1.0", type = JAXBElement.class)
    private List<JAXBElement<String>> keyword;
    @XmlAttribute
    private URI codeSpace;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    public KeywordList() {
    }

    public KeywordList(final URI codeSpace, final List<JAXBElement<String>> keyword) {
        this.codeSpace = codeSpace;
        this.keyword = keyword;
    }

    public KeywordList(final AbstractKeywordList kl) {
        if (kl != null) {
            this.codeSpace = kl.getCodeSpace();
            this.id        = kl.getId();
            this.keyword   = new ArrayList<JAXBElement<String>>();
            ObjectFactory fact = new ObjectFactory();
            for (String s : kl.getKeyword()) {
                this.keyword.add(fact.createKeywordsKeywordListKeyword(s));
            }
        }
    }
    
    /**
     * Gets the value of the keyword property.
     */
    public List<String> getKeyword() {
        if (keyword == null) {
            keyword = new ArrayList<JAXBElement<String>>();
        }
        List<String> result = new ArrayList<String>();
        for (JAXBElement<String> kw : keyword) {
            result.add(kw.getValue());
        }
        return result;
    }

    /**
     * Gets the value of the keyword property.
     */
    public void setKeyword(final List<JAXBElement<String>> keyword) {
        this.keyword = keyword;
    }

    /**
     * Gets the value of the keyword property.
     */
    public void setKeyword(final JAXBElement<String> keyword) {
        if (this.keyword == null) {
            this.keyword = new ArrayList<JAXBElement<String>>();
        }
        this.keyword.add(keyword);
    }

    /**
     * Gets the value of the keyword property.
     */
    public void setKeyword(final String keyword) {
        if (this.keyword == null) {
            this.keyword = new ArrayList<JAXBElement<String>>();
        }
        ObjectFactory factory = new ObjectFactory();
        this.keyword.add(factory.createKeywordsKeywordListKeyword(keyword));
    }

    /**
     * Gets the value of the codeSpace property.
     */
    public URI getCodeSpace() {
        return codeSpace;
    }

    /**
     * Sets the value of the codeSpace property.
     */
    public void setCodeSpace(final URI value) {
        this.codeSpace = value;
    }

    /**
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(final String value) {
        this.id = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[KeywordsList]").append("\n");
        if (keyword != null) {
            for (JAXBElement<String> k : keyword) {
                sb.append("keyword:").append(k.getValue()).append('\n');
            }
        }
        if (codeSpace != null) {
            sb.append("codeSpace: ").append(codeSpace).append('\n');
        }
        if (id != null) {
            sb.append("id: ").append(id).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof KeywordList) {
            final KeywordList that = (KeywordList) object;
            return Utilities.equals(this.codeSpace, that.codeSpace) &&
                    Utilities.equals(this.id, that.id) &&
                    Utilities.equals(this.getKeyword(), that.getKeyword());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.keyword != null ? this.keyword.hashCode() : 0);
        hash = 43 * hash + (this.codeSpace != null ? this.codeSpace.hashCode() : 0);
        hash = 43 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
