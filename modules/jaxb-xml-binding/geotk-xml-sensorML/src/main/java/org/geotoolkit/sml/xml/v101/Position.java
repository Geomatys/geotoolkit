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
package org.geotoolkit.sml.xml.v101;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.System;
import org.geotoolkit.sml.xml.Component;
import org.geotoolkit.sml.xml.AbstractDataSource;
import org.geotoolkit.sml.xml.AbstractPosition;
import org.geotoolkit.sml.xml.AbstractProcess;
import org.geotoolkit.sml.xml.AbstractProcessChain;
import org.geotoolkit.sml.xml.AbstractProcessModel;
import org.geotoolkit.sml.xml.ComponentArray;
import org.geotoolkit.swe.xml.v101.PositionType;
import org.geotoolkit.swe.xml.v101.VectorType;
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
 *       &lt;choice minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}AbstractProcess"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}Position"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}Vector"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
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
    "process",
    "position",
    "vector"
})
@XmlRootElement(name = "position")
public class Position implements AbstractPosition {

    @XmlElementRef(name = "AbstractProcess", namespace = "http://www.opengis.net/sensorML/1.0.1", type = JAXBElement.class)
    private JAXBElement<? extends AbstractProcessType> process;
    @XmlElement(name = "Position", namespace = "http://www.opengis.net/swe/1.0.1")
    private PositionType position;
    @XmlElement(name = "Vector", namespace = "http://www.opengis.net/swe/1.0.1")
    private VectorType vector;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String name;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlSchemaType(name = "anyURI")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;

    public Position() {

    }

    public Position(final AbstractPosition pos) {
        if (pos != null) {
            this.actuate = pos.getActuate();
            this.arcrole = pos.getArcrole();
            this.href    = pos.getHref();
            this.name    = pos.getName();
            this.remoteSchema = pos.getRemoteSchema();
            this.role    = pos.getRole();
            this.show    = pos.getShow();
            this.title   = pos.getTitle();
            this.type    = pos.getType();
            if (pos.getAbstractProcess() != null) {
                ObjectFactory facto = new ObjectFactory();
                AbstractProcess aProcess = pos.getAbstractProcess();
                if (aProcess instanceof AbstractDataSource) {
                    this.process = facto.createDataSource(new DataSourceType( (AbstractDataSource) aProcess));
                } else if (aProcess instanceof AbstractProcessModel) {
                    this.process = facto.createProcessModel(new ProcessModelType( (AbstractProcessModel) aProcess));
                } else if (aProcess instanceof AbstractProcessChain) {
                    this.process = facto.createProcessChain(new ProcessChainType( (AbstractProcessChain) aProcess));
                } else if (aProcess instanceof System) {
                    this.process = facto.createSystem(new SystemType((System)aProcess));
                } else if (aProcess instanceof Component) {
                    this.process = facto.createComponent(new ComponentType((Component) aProcess));
                } else if (aProcess instanceof ComponentArray) {
                    this.process = facto.createComponentArray(new ComponentArrayType((ComponentArray) aProcess));
                } else {
                    throw new IllegalArgumentException("unexepected process type:" + aProcess);
                }
            }
            if (pos.getPosition() != null) {
                this.position = new PositionType(pos.getPosition());
            }
            if (pos.getVector() != null) {
                this.vector = new VectorType(pos.getVector());
            }
        }
    }

    public Position(final String name, final String href) {
        this.href = href;
        this.name = name;
    }

    public Position(final String name, final PositionType position) {
        this.name = name;
        this.position = position;
    }
    
    /**
     * Gets the value of the process property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DataSourceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessModelType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SystemType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessChainType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ComponentArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ComponentType }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractProcessType> getProcess() {
        return process;
    }

    public AbstractProcessType getAbstractProcess() {
        if (process != null) {
            return process.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the process property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DataSourceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessModelType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SystemType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessChainType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ComponentArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ComponentType }{@code >}
     *     
     */
    public void setProcess(final JAXBElement<? extends AbstractProcessType> value) {
        this.process = ((JAXBElement<? extends AbstractProcessType> ) value);
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link PositionType }
     *     
     */
    public PositionType getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionType }
     *     
     */
    public void setPosition(final PositionType value) {
        this.position = value;
    }

    /**
     * Gets the value of the vector property.
     * 
     * @return
     *     possible object is
     *     {@link VectorType }
     *     
     */
    public VectorType getVector() {
        return vector;
    }

    /**
     * Sets the value of the vector property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorType }
     *     
     */
    public void setVector(final VectorType value) {
        this.vector = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Gets the value of the remoteSchema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteSchema(final String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(final String value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHref(final String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(final String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArcrole(final String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(final String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShow(final String value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActuate(final String value) {
        this.actuate = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof Position) {
            final Position that = (Position) object;

            boolean proc = false;
            if (this.process != null && that.process != null) {
                proc = Utilities.equals(this.process.getValue(), that.process.getValue());
            } else if (this.process == null && that.process == null) {
                proc = true;
            }

            return Utilities.equals(this.actuate, that.actuate)           &&
                   Utilities.equals(this.href, that.href)                 &&
                   Utilities.equals(this.name, that.name)                 &&
                   Utilities.equals(this.position, that.position)         &&
                   Utilities.equals(this.vector, that.vector)             &&
                   Utilities.equals(this.remoteSchema, that.remoteSchema) &&
                   Utilities.equals(this.role, that.role)                 &&
                   Utilities.equals(this.show, that.show)                 &&
                   Utilities.equals(this.title, that.title)               &&
                   Utilities.equals(this.type, that.type)                 &&
                   proc                                                   &&
                   Utilities.equals(this.arcrole, that.arcrole);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.process != null ? this.process.hashCode() : 0);
        hash = 59 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 59 * hash + (this.vector != null ? this.vector.hashCode() : 0);
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 59 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 59 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 59 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 59 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 59 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 59 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 59 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

}
