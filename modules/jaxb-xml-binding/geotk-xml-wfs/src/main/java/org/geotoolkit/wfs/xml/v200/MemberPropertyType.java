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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for MemberPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MemberPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;any processContents='lax' namespace='##other'/>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}Tuple"/>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}SimpleFeatureCollection"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/>
 *       &lt;attribute name="state" type="{http://www.opengis.net/wfs/2.0}StateValueType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MemberPropertyType", propOrder = {
    "content"
})
public class MemberPropertyType {

    @XmlElementRefs({
        @XmlElementRef(name = "Tuple", namespace = "http://www.opengis.net/wfs/2.0", type = JAXBElement.class),
        @XmlElementRef(name = "SimpleFeatureCollection", namespace = "http://www.opengis.net/wfs/2.0", type = JAXBElement.class)
    })
    @XmlMixed
    @XmlAnyElement(lax = true)
    private List<Object> content;
    @XmlAttribute
    private String state;
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

    /**
     * Gets the value of the content property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TupleType }{@code >}
     * {@link JAXBElement }{@code <}{@link FeatureCollectionType }{@code >}
     * {@link Object }
     * {@link Element }
     * {@link String }
     * {@link JAXBElement }{@code <}{@link SimpleFeatureCollectionType }{@code >}
     *
     *
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        cleanContent();
        return this.content;
    }

    public void cleanContent() {
        if (this.content != null) {
            final List<Object> toRemove = new ArrayList<Object>();
            int i = 0;
            for (Object element : content) {
                if (element instanceof String) {
                    String s = (String) element;
                    s = s.replace("\n", "");
                    s = s.replace("\t", "");
                    s = s.trim();
                    if (s.isEmpty()) {
                        toRemove.add(element);
                    } else {
                        content.set(i, s);
                    }
                }
                i++;
            }
            this.content.removeAll(toRemove);
        }
    }

    /**
     * Gets the value of the state property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setState(String value) {
        this.state = value;
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
        if (type == null) {
            return "simple";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
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
    public void setHref(String value) {
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
    public void setRole(String value) {
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
    public void setArcrole(String value) {
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
    public void setTitle(String value) {
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
    public void setShow(String value) {
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
    public void setActuate(String value) {
        this.actuate = value;
    }

    @Override
     public String toString() {
        final StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (actuate != null) {
            sb.append("actuate:").append(actuate).append('\n');
        }
        if (arcrole != null) {
            sb.append("arcrole:").append(arcrole).append('\n');
        }
        if (href != null) {
            sb.append("href:").append(href).append('\n');
        }
        if (role != null) {
            sb.append("role:").append(role).append('\n');
        }
        if (show != null) {
            sb.append("show:").append(show).append('\n');
        }
        if (state != null) {
            sb.append("state:").append(state).append('\n');
        }
        if (title != null) {
            sb.append("title:").append(title).append('\n');
        }
        if (type != null) {
            sb.append("type:").append(type).append('\n');
        }
        cleanContent();
        if (content != null) {
            for (Object obj : content) {
                if (obj instanceof JAXBElement) {
                    sb.append("content [JAXBElement]= ").append(((JAXBElement)obj).getValue()).append('\n');
                } else {
                    sb.append("content= ").append(obj).append('\n');
                }
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MemberPropertyType) {
            final MemberPropertyType that = (MemberPropertyType) obj;
            boolean anyEq = false;
            if (this.content == null && that.content == null) {
                anyEq = true;
            } else if (this.content != null && that.content != null) {
                this.cleanContent();
                that.cleanContent();
                if (this.content.size() == that.content.size()) {
                    for (int i = 0; i < this.content.size(); i++) {
                        final Object thisany = this.content.get(i);
                        final Object thatany = that.content.get(i);
                        if (thisany instanceof JAXBElement && thatany instanceof JAXBElement) {
                            anyEq = Utilities.equals(((JAXBElement)thisany).getValue(), ((JAXBElement)thatany).getValue());
                        } else {
                            anyEq = Utilities.equals(thisany, thatany);
                        }
                    }
                }
            }
            return Utilities.equals(this.actuate, that.actuate) &&
                   Utilities.equals(this.arcrole, that.arcrole) &&
                   anyEq                                        &&
                   Utilities.equals(this.href,    that.href)    &&
                   Utilities.equals(this.role,    that.role)    &&
                   Utilities.equals(this.show,    that.show)    &&
                   Utilities.equals(this.state,   that.state)   &&
                   Utilities.equals(this.title,   that.title)   &&
                   Utilities.equals(this.type,    that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.content != null ? this.content.hashCode() : 0);
        hash = 89 * hash + (this.state != null ? this.state.hashCode() : 0);
        hash = 89 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 89 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 89 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 89 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 89 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 89 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 89 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        return hash;
    }

}
