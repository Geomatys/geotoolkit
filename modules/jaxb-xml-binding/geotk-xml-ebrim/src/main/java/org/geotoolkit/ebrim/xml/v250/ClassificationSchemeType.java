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
package org.geotoolkit.ebrim.xml.v250;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.util.Utilities;


/**
 * 
 * ClassificationScheme is the mapping of the same named interface in ebRIM.
 * It extends RegistryEntry.
 * 			
 * 
 * <p>Java class for ClassificationSchemeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClassificationSchemeType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryEntryType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ClassificationNode" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="isInternal" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="nodeType" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NCName">
 *             &lt;enumeration value="UniqueCode"/>
 *             &lt;enumeration value="EmbeddedPath"/>
 *             &lt;enumeration value="NonUniqueCode"/>
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
@XmlType(name = "ClassificationSchemeType", propOrder = {
    "classificationNode"
})
@XmlRootElement(name = "ClassificationScheme")        
public class ClassificationSchemeType extends RegistryEntryType {

    @XmlElement(name = "ClassificationNode")
    private List<ClassificationNodeType> classificationNode;
    @XmlAttribute(required = true)
    private boolean isInternal;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String nodeType;

    /**
     * Gets the value of the classificationNode property.
     */
    public List<ClassificationNodeType> getClassificationNode() {
        if (classificationNode == null) {
            classificationNode = new ArrayList<ClassificationNodeType>();
        }
        return this.classificationNode;
    }
    
     /**
     * Sets the value of the classificationNode property.
     */
    public void setClassificationNode(final ClassificationNodeType classificationNode) {
        if (this.classificationNode == null) {
            this.classificationNode = new ArrayList<ClassificationNodeType>();
        }
        this.classificationNode.add(classificationNode);
    }
    
    /**
     * Sets the value of the classificationNode property.
     */
    public void setClassificationNode(final List<ClassificationNodeType> classificationNode) {
        this.classificationNode = classificationNode;
    }

    /**
     * Gets the value of the isInternal property.
     * 
     */
    public boolean isIsInternal() {
        return isInternal;
    }

    /**
     * Sets the value of the isInternal property.
     * 
     */
    public void setIsInternal(final boolean value) {
        this.isInternal = value;
    }

    /**
     * Gets the value of the nodeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * Sets the value of the nodeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeType(final String value) {
        this.nodeType = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append("isInternal:").append(isInternal).append('\n');
        
        if (nodeType != null) {
            sb.append("nodeType:").append(nodeType).append('\n');
        }
        if (classificationNode != null) {
            sb.append("classificationNode:\n");
            for (ClassificationNodeType cn : classificationNode) {
                sb.append(cn).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ClassificationSchemeType && super.equals(obj)) {
            final ClassificationSchemeType that = (ClassificationSchemeType) obj;
            return Utilities.equals(this.classificationNode, that.classificationNode) &&
                   Utilities.equals(this.isInternal,         that.isInternal) &&
                   Utilities.equals(this.nodeType,           that.nodeType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + super.hashCode();
        hash = 97 * hash + (this.classificationNode != null ? this.classificationNode.hashCode() : 0);
        hash = 97 * hash + (this.isInternal ? 1 : 0);
        hash = 97 * hash + (this.nodeType != null ? this.nodeType.hashCode() : 0);
        return hash;
    }
}
