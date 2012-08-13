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
import org.geotoolkit.util.Utilities;


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
        if (particle != null) {
            for (Object obj : particle) {
                if (obj instanceof JAXBElement) {
                    JAXBElement jb = (JAXBElement) obj;
                    if (jb.getValue() instanceof Element) {
                        result.add((Element) jb.getValue());
                    }
                }
            }
        }
        return result;
    }

    public void addElement(final Element element) {
        if (particle == null) {
            particle = new ArrayList<Object>();
        }
        if (element instanceof TopLevelElement) {
            particle.add(FACTORY.createElement((TopLevelElement)element));
        } else if (element instanceof LocalElement) {
            particle.add(FACTORY.createGroupElement((LocalElement)element));
        }
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
    public void setRef(final QName value) {
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
    public void setMinOccurs(final Integer value) {
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
    public void setMaxOccurs(final String value) {
        this.maxOccurs = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Group && super.equals(object)) {
            final Group that = (Group) object;
            boolean part = false;
            if (this.particle != null && that.particle != null) {
                if (this.particle.size() == that.particle.size()) {
                    part = true;
                    for (int i = 0; i < this.particle.size(); i++) {
                        Object o1 = this.particle.get(i);
                        Object o2 = that.particle.get(i);
                        if (o1 instanceof JAXBElement) {
                            o1 = ((JAXBElement)o1).getValue();
                        }
                        if (o2 instanceof JAXBElement) {
                            o2 = ((JAXBElement)o2).getValue();
                        }
                        if (!Utilities.equals(o1, o2)) {
                            part = false;
                            break;
                        }
                    }
                }
            } else if (this.particle == null && that.particle == null) {
                part = true;
            }
            return Utilities.equals(this.maxOccurs,                 that.maxOccurs) &&
                   Utilities.equals(this.minOccurs,                 that.minOccurs) &&
                   Utilities.equals(this.name,                      that.name) &&
                   Utilities.equals(this.ref,                       that.ref) &&
                   part;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + super.hashCode();
        hash = 37 * hash + (this.minOccurs != null ? this.minOccurs.hashCode() : 0);
        hash = 37 * hash + (this.maxOccurs != null ? this.maxOccurs.hashCode() : 0);
        hash = 37 * hash + (this.ref != null ? this.ref.hashCode() : 0);
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.particle != null ? this.particle.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        if (maxOccurs != null) {
            sb.append("maxOccurs:").append(maxOccurs).append('\n');
        }
        if (minOccurs != null) {
            sb.append("minOccurs:").append(minOccurs).append('\n');
        }
        if (ref != null) {
            sb.append("ref:").append(ref).append('\n');
        }
        if (particle != null) {
            sb.append("particle:\n");
            for (Object obj : particle) {
                if (obj instanceof JAXBElement) {
                    obj = ((JAXBElement)obj).getValue();
                    sb.append("JB:");
                }
                sb.append(obj).append('\n');
            }
        }
        return  sb.toString();
    }
}
