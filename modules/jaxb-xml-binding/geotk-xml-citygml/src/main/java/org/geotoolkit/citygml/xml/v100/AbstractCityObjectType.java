/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.citygml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.gml.xml.v311modified.AbstractFeatureEntry;


/**
 * Type describing the abstract superclass of most CityGML features.
 * Its purpose is to provide a creation and a termination date as well as a reference to corresponding objects in other information systems.
 * A generalization relation may be used to relate features, which represent the same real-world object in different  Levels-of-Detail,
 * i.e. a feature and its generalized counterpart(s).
 * The direction of this relation is from the feature to the corresponding generalized feature.
 * 
 * <p>Java class for AbstractCityObjectType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractCityObjectType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="terminationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="externalReference" type="{http://www.opengis.net/citygml/1.0}ExternalReferenceType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="generalizesTo" type="{http://www.opengis.net/citygml/1.0}GeneralizationRelationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/1.0}_GenericApplicationPropertyOfCityObject" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractCityObjectType", propOrder = {
    "creationDate",
    "terminationDate",
    "externalReference",
    "generalizesTo",
    "genericApplicationPropertyOfCityObject"
})
@XmlSeeAlso({
    AbstractSiteType.class
})
public abstract class AbstractCityObjectType extends AbstractFeatureEntry {

    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar creationDate;
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar terminationDate;
    private List<ExternalReferenceType> externalReference;
    private List<GeneralizationRelationType> generalizesTo;
    @XmlElement(name = "_GenericApplicationPropertyOfCityObject")
    private List<Object> genericApplicationPropertyOfCityObject;

    /**
     * Gets the value of the creationDate property.
     */
    public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     */
    public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the terminationDate property.
    */
    public XMLGregorianCalendar getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the value of the terminationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTerminationDate(XMLGregorianCalendar value) {
        this.terminationDate = value;
    }

    /**
     * Gets the value of the externalReference property.
     */
    public List<ExternalReferenceType> getExternalReference() {
        if (externalReference == null) {
            externalReference = new ArrayList<ExternalReferenceType>();
        }
        return this.externalReference;
    }

    /**
     * Gets the value of the generalizesTo property.
     */
    public List<GeneralizationRelationType> getGeneralizesTo() {
        if (generalizesTo == null) {
            generalizesTo = new ArrayList<GeneralizationRelationType>();
        }
        return this.generalizesTo;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfCityObject property.
     */
    public List<Object> getGenericApplicationPropertyOfCityObject() {
        if (genericApplicationPropertyOfCityObject == null) {
            genericApplicationPropertyOfCityObject = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfCityObject;
    }

}
