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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Association is the mapping of the same named interface in ebRIM.
 * It extends RegistryObject. An Association specifies references to two previously submitted registry entrys.
 * The sourceObject is id of the sourceObject in association
 * The targetObject is id of the targetObject in association
 *       
 * 
 * <p>Java class for AssociationType1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AssociationType1">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;attribute name="associationType" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="sourceObject" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="targetObject" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssociationType")
@XmlRootElement(name = "Association")
public class AssociationType extends RegistryObjectType {

    @XmlAttribute(required = true)
    private String associationType;
    @XmlAttribute(required = true)
    private String sourceObject;
    @XmlAttribute(required = true)
    private String targetObject;

    /**
     * Gets the value of the associationType property.
     */
    public String getAssociationType() {
        return associationType;
    }

    /**
     * Sets the value of the associationType property.
     */
    public void setAssociationType(final String value) {
        this.associationType = value;
    }

    /**
     * Gets the value of the sourceObject property.
     */
    public String getSourceObject() {
        return sourceObject;
    }

    /**
     * Sets the value of the sourceObject property.
     */
    public void setSourceObject(final String value) {
        this.sourceObject = value;
    }

    /**
     * Gets the value of the targetObject property.
     */
    public String getTargetObject() {
        return targetObject;
    }

    /**
     * Sets the value of the targetObject property.
     */
    public void setTargetObject(final String value) {
        this.targetObject = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (associationType != null) {
            sb.append("associationType:").append(associationType).append('\n');
        }
        if (targetObject != null) {
            sb.append("targetObject:").append(targetObject).append('\n');
        }
        if (sourceObject != null) {
            sb.append("sourceObject:").append(sourceObject).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AssociationType && super.equals(obj)) {
            final AssociationType that = (AssociationType) obj;
            return Utilities.equals(this.associationType, that.associationType) &&
                   Utilities.equals(this.sourceObject, that.sourceObject) &&
                   Utilities.equals(this.targetObject, that.targetObject);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + super.hashCode();
        hash = 53 * hash + (this.associationType != null ? this.associationType.hashCode() : 0);
        hash = 53 * hash + (this.sourceObject != null ? this.sourceObject.hashCode() : 0);
        hash = 53 * hash + (this.targetObject != null ? this.targetObject.hashCode() : 0);
        return hash;
    }
}
