/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wfs.xml.QueryExpressionText;
import org.w3c.dom.Element;


/**
 * <p>Java class for QueryExpressionTextType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="QueryExpressionTextType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;any processContents='skip' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;any processContents='skip' namespace='http://www.opengis.net/wfs/2.0' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="returnFeatureTypes" use="required" type="{http://www.opengis.net/wfs/2.0}ReturnFeatureTypesListType" />
 *       &lt;attribute name="language" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="isPrivate" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryExpressionTextType", propOrder = {
    "content"
})
public class QueryExpressionTextType implements org.geotoolkit.wfs.xml.QueryExpressionText {

    @XmlMixed
    @XmlAnyElement(lax=true)
    private List<Object> content;
    @XmlAttribute(required = true)
    private List<QName> returnFeatureTypes;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String language;
    @XmlAttribute
    private Boolean isPrivate;

    public QueryExpressionTextType() {

    }

    public QueryExpressionTextType(final String language, final QueryType query, final List<QName> returnFeatureTypes) {
        this.language = language;
        this.returnFeatureTypes = returnFeatureTypes;
        if (query != null) {
            this.content = new ArrayList<Object>();
            this.content.add(query);
        }
    }

    public QueryExpressionTextType(final QueryExpressionText that) {
        if (that != null) {
            this.language = that.getLanguage();
            if (that.getReturnFeatureTypes() != null) {
                this.returnFeatureTypes = new ArrayList<QName>(that.getReturnFeatureTypes());
            }
            this.isPrivate = that.isIsPrivate();
            if (that.getContent() != null) {
                this.content = new ArrayList<Object>();
                for (Object obj : that.getContent()) {
                    // only can duplicate queryType
                    if (obj instanceof QueryType) {
                        this.content.add(new QueryType((QueryType)obj));
                    } else {
                        throw new IllegalArgumentException("Unable to clone the object:" + obj.getClass().getName());
                    }
                }
            }
        }
    }

    /**
     * Gets the value of the content property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link String }
     *
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();

        // remove the empty String
        } else {
            final List<Object> toRemove = new ArrayList<Object>();
            for (Object obj : content) {
                if (obj instanceof String) {
                    final String s = ((String) obj).trim();
                    if (s.isEmpty()) {
                        toRemove.add(obj);
                    }
                }
            }
            this.content.removeAll(toRemove);
        }
        return this.content;
    }

    /**
     * Gets the value of the returnFeatureTypes property.
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     */
    public List<QName> getReturnFeatureTypes() {
        if (returnFeatureTypes == null) {
            returnFeatureTypes = new ArrayList<QName>();
        }
        return this.returnFeatureTypes;
    }

    /**
     * Gets the value of the language property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Gets the value of the isPrivate property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isIsPrivate() {
        if (isPrivate == null) {
            return false;
        } else {
            return isPrivate;
        }
    }

    /**
     * Sets the value of the isPrivate property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsPrivate(Boolean value) {
        this.isPrivate = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof QueryExpressionTextType) {
            final QueryExpressionTextType that = (QueryExpressionTextType) object;
            boolean contentE;
            if (this.getContent() == null && that.getContent() == null) {
                contentE = true;
            } else if (this.getContent() != null && that.getContent() != null){
                if (this.getContent().size() == that.getContent().size()) {
                    contentE = true;
                    for (int i = 0; i < 0; i++) {
                        final Object thiso = this.getContent().get(i);
                        final Object thato = that.getContent().get(i);
                        if (thiso instanceof JAXBElement && thato instanceof JAXBElement) {
                            final JAXBElement thisjb = (JAXBElement)thiso;
                            final JAXBElement thatjb = (JAXBElement)thato;
                            if (!Objects.equals(thisjb.getValue(), thatjb.getValue())) {
                                return false;
                            }
                        } else {
                            if (!Objects.equals(thiso, thato)) {
                                return false;
                            }
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }

            return contentE &&
                   Objects.equals(this.isPrivate, that.isPrivate) &&
                   Objects.equals(this.language, that.language) &&
                   Objects.equals(this.returnFeatureTypes, that.returnFeatureTypes);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.getContent() != null ? this.getContent().hashCode() : 0);
        hash = 79 * hash + (this.isPrivate != null ? this.isPrivate.hashCode() : 0);
        hash = 79 * hash + (this.language != null ? this.language.hashCode() : 0);
        hash = 79 * hash + (this.returnFeatureTypes != null ? this.returnFeatureTypes.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[QueryExpressionTextType]\n");
        if(language != null) {
            sb.append("language:").append(language).append('\n');
        }
        if (isPrivate != null) {
            sb.append("isPrivate:").append(isPrivate).append('\n');
        }
        if (getContent() != null) {
            sb.append("content (").append(getContent().size()).append("):\n");
            for (Object k : getContent()) {
                if (k instanceof JAXBElement) {
                    sb.append(((JAXBElement)k).getValue()).append('\n');
                } else {
                    sb.append(k).append('\n');
                }
            }
        }
        if (returnFeatureTypes != null) {
            sb.append("returnFeatureTypes:").append('\n');
            for (QName k : returnFeatureTypes) {
                sb.append(k).append('\n');
            }
        }
        return sb.toString();
    }
}
