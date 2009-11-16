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
package org.geotoolkit.xsd.xml.v2001;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * 
 *    group type for explicit groups, named top-level groups and
 *    group references
 * 
 * <p>Java class for group complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="group">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       &lt;group ref="{http://www.w3.org/2001/XMLSchema}particle" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;attGroup ref="{http://www.w3.org/2001/XMLSchema}defRef"/>
 *       &lt;attGroup ref="{http://www.w3.org/2001/XMLSchema}occurs"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "group", propOrder = {
    "particle"
})
@XmlSeeAlso({
    ExplicitGroup.class,
    RealGroup.class
})
public abstract class Group extends Annotated {

    @XmlElementRefs({
        @XmlElementRef(name = "group",    namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class),
        @XmlElementRef(name = "all",      namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class),
        @XmlElementRef(name = "choice",   namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class),
        @XmlElementRef(name = "sequence", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class),
        @XmlElementRef(name = "any",      namespace = "http://www.w3.org/2001/XMLSchema", type = Any.class),
        @XmlElementRef(name = "element",  namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class)
    })
    private List<Object> particle;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private String name;
    @XmlAttribute
    private QName ref;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer minOccurs;
    @XmlAttribute
    @XmlSchemaType(name = "allNNI")
    private String maxOccurs;

    private static final ObjectFactory FACTORY = new ObjectFactory();

    /**
     * Gets the value of the particle property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link Any }
     * {@link JAXBElement }{@code <}{@link ExplicitGroup }{@code >}
     * {@link JAXBElement }{@code <}{@link GroupRef }{@code >}
     * {@link JAXBElement }{@code <}{@link All }{@code >}
     * {@link JAXBElement }{@code <}{@link LocalElement }{@code >}
     * {@link JAXBElement }{@code <}{@link ExplicitGroup }{@code >}
     * 
     * 
     */
    public List<Object> getParticle() {
        if (particle == null) {
            particle = new ArrayList<Object>();
        }
        return this.particle;
    }

    public List<Element> getElements() {
        List<Element> result = new ArrayList<Element>();
        for (Object obj : particle) {
            if (obj instanceof JAXBElement) {
                JAXBElement jb = (JAXBElement) obj;
                if (jb.getValue() instanceof Element) {
                    result.add((Element) jb.getValue());
                }
            }
        }
        return result;
    }

    public void addElement(TopLevelElement element) {
        if (particle == null) {
            particle = new ArrayList<Object>();
        }
        particle.add(FACTORY.createElement(element));
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
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setRef(QName value) {
        this.ref = value;
    }

    /**
     * Gets the value of the minOccurs property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public Integer getMinOccurs() {
        if (minOccurs == null) {
            return 1;
        } else {
            return minOccurs;
        }
    }

    /**
     * Sets the value of the minOccurs property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMinOccurs(Integer value) {
        this.minOccurs = value;
    }

    /**
     * Gets the value of the maxOccurs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaxOccurs() {
        if (maxOccurs == null) {
            return "1";
        } else {
            return maxOccurs;
        }
    }

    /**
     * Sets the value of the maxOccurs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxOccurs(String value) {
        this.maxOccurs = value;
    }

}
