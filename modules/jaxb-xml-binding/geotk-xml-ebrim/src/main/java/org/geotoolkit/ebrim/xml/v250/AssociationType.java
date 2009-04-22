/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.ebrim.xml.v250;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * Association is the mapping of the same named interface in ebRIM.
 * It extends RegistryObject.
 * 
 * An Association specifies references to two previously submitted
 * registry entrys.
 * 
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
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryObjectType">
 *       &lt;attribute name="associationType" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="sourceObject" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="targetObject" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="isConfirmedBySourceOwner" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isConfirmedByTargetOwner" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssociationType")
@XmlRootElement(name = "Association")
public class AssociationType extends RegistryObjectType {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String associationType;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String sourceObject;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String targetObject;
    @XmlAttribute
    private Boolean isConfirmedBySourceOwner;
    @XmlAttribute
    private Boolean isConfirmedByTargetOwner;

    /**
     * Gets the value of the associationType property.
      */
    public String getAssociationType() {
        return associationType;
    }

    /**
     * Sets the value of the associationType property.
     */
    public void setAssociationType(String value) {
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
    public void setSourceObject(String value) {
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
    public void setTargetObject(String value) {
        this.targetObject = value;
    }

    /**
     * Gets the value of the isConfirmedBySourceOwner property.
     */
    public Boolean getIsConfirmedBySourceOwner() {
        return isConfirmedBySourceOwner;
    }

    /**
     * Sets the value of the isConfirmedBySourceOwner property.
     */
    public void setIsConfirmedBySourceOwner(Boolean value) {
        this.isConfirmedBySourceOwner = value;
    }

    /**
     * Gets the value of the isConfirmedByTargetOwner property.
     */
    public Boolean getIsConfirmedByTargetOwner() {
        return isConfirmedByTargetOwner;
    }

    /**
     * Sets the value of the isConfirmedByTargetOwner property.
     */
    public void setIsConfirmedByTargetOwner(Boolean value) {
        this.isConfirmedByTargetOwner = value;
    }

}
