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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractMetadata;
import org.geotoolkit.ows.xml.v110.MetadataType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wfs.xml.ParameterExpression;
import org.geotoolkit.wfs.xml.QueryExpressionText;
import org.geotoolkit.wfs.xml.StoredQueryDescription;


/**
 * <p>Java class for StoredQueryDescriptionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="StoredQueryDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}Title" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}Abstract" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Parameter" type="{http://www.opengis.net/wfs/2.0}ParameterExpressionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="QueryExpressionText" type="{http://www.opengis.net/wfs/2.0}QueryExpressionTextType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoredQueryDescriptionType", propOrder = {
    "title",
    "_abstract",
    "metadata",
    "parameter",
    "queryExpressionText"
})
public class StoredQueryDescriptionType implements StoredQueryDescription {

    @XmlElement(name = "Title")
    private List<Title> title;
    @XmlElement(name = "Abstract")
    private List<Abstract> _abstract;
    @XmlElement(name = "Metadata", namespace = "http://www.opengis.net/ows/1.1")
    private List<MetadataType> metadata;
    @XmlElement(name = "Parameter")
    private List<ParameterExpressionType> parameter;
    @XmlElement(name = "QueryExpressionText", required = true)
    private List<QueryExpressionTextType> queryExpressionText;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String id;

    public StoredQueryDescriptionType() {

    }

    public StoredQueryDescriptionType(final StoredQueryDescription that) {
        if (that != null) {
            if (that.getAbstract() != null) {
                this._abstract = new ArrayList<Abstract>();
                for (org.geotoolkit.wfs.xml.Abstract a : that.getAbstract()) {
                    this._abstract.add(new Abstract(a));
                }
            }
            this.id = that.getId();
            if (that.getMetadata() != null) {
                this.metadata = new ArrayList<MetadataType>();
                for (AbstractMetadata m : that.getMetadata()) {
                    this.metadata.add(new MetadataType(m));
                }
            }
            if (that.getParameter() != null) {
                this.parameter = new ArrayList<ParameterExpressionType>();
                for (ParameterExpression m : that.getParameter()) {
                    this.parameter.add(new ParameterExpressionType(m));
                }
            }
            if (that.getQueryExpressionText() != null) {
                this.queryExpressionText = new ArrayList<QueryExpressionTextType>();
                for (QueryExpressionText m : that.getQueryExpressionText()) {
                    this.queryExpressionText.add(new QueryExpressionTextType(m));
                }
            }
            if (that.getTitle() != null) {
                this.title = new ArrayList<Title>();
                for (org.geotoolkit.wfs.xml.Title t : that.getTitle()) {
                    this.title.add(new Title(t));
                }
            }
        }
    }

    public StoredQueryDescriptionType(final String id, final String title, final String _abstract, final ParameterExpressionType parameter,
            final QueryExpressionTextType queryExpressionText) {
        this.id = id;
        if (title != null) {
            this.title = new ArrayList<Title>();
            this.title.add(new Title(title));
        }
        if (_abstract != null) {
            this._abstract = new ArrayList<Abstract>();
            this._abstract.add(new Abstract(_abstract));
        }
        if (parameter != null) {
            this.parameter = new ArrayList<ParameterExpressionType>();
            this.parameter.add(parameter);
        }
        if (queryExpressionText != null) {
            this.queryExpressionText = new ArrayList<QueryExpressionTextType>();
            this.queryExpressionText.add(queryExpressionText);
        }
    }

    /**
     * Gets the value of the title property.
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Title }
     */
    @Override
    public List<Title> getTitle() {
        if (title == null) {
            title = new ArrayList<Title>();
        }
        return this.title;
    }

    /**
     * Gets the value of the abstract property.
     *
     * {@link Abstract }
     */
    @Override
    public List<Abstract> getAbstract() {
        if (_abstract == null) {
            _abstract = new ArrayList<Abstract>();
        }
        return this._abstract;
    }

    /**
     * Gets the value of the metadata property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link MetadataType }
     */
    @Override
    public List<MetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<MetadataType>();
        }
        return this.metadata;
    }

    /**
     * Gets the value of the parameter property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link ParameterExpressionType }
     */
    @Override
    public List<ParameterExpressionType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<ParameterExpressionType>();
        }
        return this.parameter;
    }

    @Override
    public List<String> getParameterNames() {
        final List<String> results = new ArrayList<String>();
        if (parameter != null) {
            for (ParameterExpressionType param : parameter) {
                results.add(param.getName());
            }
        }
        return results;
    }
    
    /**
     * Gets the value of the queryExpressionText property.
     * Objects of the following type(s) are allowed in the list
     * {@link QueryExpressionTextType }
     */
    @Override
    public List<QueryExpressionTextType> getQueryExpressionText() {
        if (queryExpressionText == null) {
            queryExpressionText = new ArrayList<QueryExpressionTextType>();
        }
        return this.queryExpressionText;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof StoredQueryDescriptionType) {
            final StoredQueryDescriptionType that = (StoredQueryDescriptionType) object;

            return Utilities.equals(this._abstract, that._abstract) &&
                   Utilities.equals(this.id, that.id) &&
                   Utilities.equals(this.metadata, that.metadata) &&
                   Utilities.equals(this.parameter, that.parameter) &&
                   Utilities.equals(this.queryExpressionText, that.queryExpressionText) &&
                   Utilities.equals(this.title, that.title);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 79 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 79 * hash + (this.metadata != null ? this.metadata.hashCode() : 0);
        hash = 79 * hash + (this.parameter != null ? this.parameter.hashCode() : 0);
        hash = 79 * hash + (this.queryExpressionText != null ? this.queryExpressionText.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[StoredQueryDescriptionType]\n");
        if(id != null) {
            s.append("id:").append(id).append('\n');
        }
        if (title != null) {
            s.append("title:").append(title).append('\n');
        }
        if (_abstract != null) {
            s.append("_abstract:").append(_abstract).append('\n');
        }
        if (metadata != null) {
            s.append("metadata:").append('\n');
            for (MetadataType k : metadata) {
                s.append(k).append('\n');
            }
        }
       if (parameter != null) {
            s.append("parameter:").append('\n');
            for (ParameterExpressionType k : parameter) {
                s.append(k).append('\n');
            }
        }
        if (queryExpressionText != null) {
            s.append("queryExpressionText:").append('\n');
            for (QueryExpressionTextType k : queryExpressionText) {
                s.append(k).append('\n');
            }
        }
        return s.toString();
    }
}
