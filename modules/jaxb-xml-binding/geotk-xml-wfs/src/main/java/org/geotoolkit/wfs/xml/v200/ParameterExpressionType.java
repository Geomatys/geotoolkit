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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ows.xml.AbstractMetadata;
import org.geotoolkit.ows.xml.v110.MetadataType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wfs.xml.ParameterExpression;

/**
 * <p>Java class for ParameterExpressionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ParameterExpressionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}Title" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}Abstract" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParameterExpressionType", propOrder = {
    "title",
    "_abstract",
    "metadata"
})
public class ParameterExpressionType implements ParameterExpression {

    @XmlElement(name = "Title")
    private List<Title> title;
    @XmlElement(name = "Abstract")
    private List<Abstract> _abstract;
    @XmlElement(name = "Metadata", namespace = "http://www.opengis.net/ows/1.1")
    private List<MetadataType> metadata;
    @XmlAttribute(required = true)
    private String name;
    @XmlAttribute(required = true)
    private QName type;

    public ParameterExpressionType() {

    }

    public ParameterExpressionType(final ParameterExpression that) {
        if (that != null) {
            if (that.getAbstract() != null) {
                this._abstract = new ArrayList<Abstract>();
                for (org.geotoolkit.wfs.xml.Abstract a : that.getAbstract()) {
                    this._abstract.add(new Abstract(a));
                }
            }
            if (that.getMetadata() != null) {
                this.metadata = new ArrayList<MetadataType>();
                for (AbstractMetadata m : that.getMetadata()) {
                    this.metadata.add(new MetadataType(m));
                }
            }
            this.name = that.getName();
            if (that.getTitle() != null) {
                this.title = new ArrayList<Title>();
                for (org.geotoolkit.wfs.xml.Title  m : that.getTitle()) {
                    this.title.add(new Title(m));
                }
            }
            this.type = that.getType();
        }
    }

    public ParameterExpressionType(final String name, final String title, final String _abstract, final QName type) {
        this.name = name;
        this.type = type;
        if (title != null) {
            this.title = new ArrayList<Title>();
            this.title.add(new Title(title));
        }
        if (_abstract != null) {
            this._abstract = new ArrayList<Abstract>();
            this._abstract.add(new Abstract(_abstract));
        }
    }

    /**
     * Gets the value of the title property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Title }
     */
    public List<Title> getTitle() {
        if (title == null) {
            title = new ArrayList<Title>();
        }
        return this.title;
    }

    /**
     * Gets the value of the abstract property.
     *
     * Objects of the following type(s) are allowed in the list
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
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link QName }
     *
     */
    public QName getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link QName }
     *
     */
    public void setType(QName value) {
        this.type = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ParameterExpressionType) {
            final ParameterExpressionType that = (ParameterExpressionType) object;

            return Utilities.equals(this._abstract, that._abstract) &&
                   Utilities.equals(this.name, that.name) &&
                   Utilities.equals(this.metadata, that.metadata) &&
                   Utilities.equals(this.type, that.type) &&
                   Utilities.equals(this.title, that.title);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 79 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 79 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 79 * hash + (this.metadata != null ? this.metadata.hashCode() : 0);
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[ParameterExpressionType]\n");
        if(name != null) {
            s.append("name:").append(name).append('\n');
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
        if (type != null) {
            s.append("type:").append(type).append('\n');
        }
        return s.toString();
    }
}
