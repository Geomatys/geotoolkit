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
package org.geotoolkit.ebrim.xml.v300;

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Classification is the mapping of the same named interface in ebRIM.
 * It extends RegistryObject.
 * A Classification specifies references to two registry entrys.
 * The classifiedObject is id of the Object being classified.
 * The classificationNode is id of the ClassificationNode classying the object
 *
 *
 * <p>Java class for ClassificationType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ClassificationType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;attribute name="classificationScheme" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="classifiedObject" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="classificationNode" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="nodeRepresentation" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}LongName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClassificationType")
@XmlRootElement(name = "Classification")
public class ClassificationType extends RegistryObjectType {

    @XmlAttribute
    private String classificationScheme;
    @XmlAttribute(required = true)
    private String classifiedObject;
    @XmlAttribute
    private String classificationNode;
    @XmlAttribute
    private String nodeRepresentation;

    /**
     * Gets the value of the classificationScheme property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClassificationScheme() {
        return classificationScheme;
    }

    /**
     * Sets the value of the classificationScheme property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClassificationScheme(final String value) {
        this.classificationScheme = value;
    }

    /**
     * Gets the value of the classifiedObject property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClassifiedObject() {
        return classifiedObject;
    }

    /**
     * Sets the value of the classifiedObject property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClassifiedObject(final String value) {
        this.classifiedObject = value;
    }

    /**
     * Gets the value of the classificationNode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClassificationNode() {
        return classificationNode;
    }

    /**
     * Sets the value of the classificationNode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClassificationNode(final String value) {
        this.classificationNode = value;
    }

    /**
     * Gets the value of the nodeRepresentation property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNodeRepresentation() {
        return nodeRepresentation;
    }

    /**
     * Sets the value of the nodeRepresentation property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNodeRepresentation(final String value) {
        this.nodeRepresentation = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (classificationNode != null) {
            sb.append("classificationNode:").append(classificationNode).append('\n');
        }
        if (classificationScheme != null) {
            sb.append("classificationScheme:").append(classificationScheme).append('\n');
        }
        if (classifiedObject != null) {
            sb.append("classifiedObject:").append(classifiedObject).append('\n');
        }
        if (nodeRepresentation != null) {
            sb.append("nodeRepresentation:").append(nodeRepresentation).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ClassificationType && super.equals(obj)) {
            final ClassificationType that = (ClassificationType) obj;
            return Objects.equals(this.classificationNode,   that.classificationNode) &&
                   Objects.equals(this.classificationScheme, that.classificationScheme) &&
                   Objects.equals(this.classifiedObject,     that.classifiedObject) &&
                   Objects.equals(this.nodeRepresentation,   that.nodeRepresentation);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + super.hashCode();
        hash = 29 * hash + (this.classificationScheme != null ? this.classificationScheme.hashCode() : 0);
        hash = 29 * hash + (this.classifiedObject != null ? this.classifiedObject.hashCode() : 0);
        hash = 29 * hash + (this.classificationNode != null ? this.classificationNode.hashCode() : 0);
        hash = 29 * hash + (this.nodeRepresentation != null ? this.nodeRepresentation.hashCode() : 0);
        return hash;
    }

}
