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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for wildcard complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="wildcard">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}namespaceList" default="##any" />
 *       &lt;attribute name="processContents" default="strict">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="skip"/>
 *             &lt;enumeration value="lax"/>
 *             &lt;enumeration value="strict"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wildcard")
@XmlSeeAlso({
    Any.class
})
public class Wildcard extends Annotated {

    @XmlAttribute
    @XmlSchemaType(name = "namespaceList")
    private List<String> namespace;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String processContents;

    /**
     * Gets the value of the namespace property.
     *
     */
    public List<String> getNamespace() {
        if (namespace == null) {
            namespace = new ArrayList<String>();
        }
        return this.namespace;
    }

    /**
     * Gets the value of the processContents property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProcessContents() {
        if (processContents == null) {
            return "strict";
        } else {
            return processContents;
        }
    }

    /**
     * Sets the value of the processContents property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProcessContents(final String value) {
        this.processContents = value;
    }

        /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Wildcard && super.equals(object)) {
            final Wildcard that = (Wildcard) object;
            return Utilities.equals(this.namespace,   that.namespace) &&
                   Utilities.equals(this.processContents, that.processContents);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + super.hashCode();
        hash = 83 * hash + (this.namespace != null ? this.namespace.hashCode() : 0);
        hash = 83 * hash + (this.processContents != null ? this.processContents.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (namespace != null) {
            sb.append("namespace:").append(namespace).append('\n');
        }
        if (processContents != null) {
            sb.append("processContents:").append(processContents).append('\n');
        }
        return  sb.toString();
    }
}
