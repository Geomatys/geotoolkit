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


package org.geotoolkit.ogc.xml.v200;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.util.Utilities;
import org.opengis.feature.Attribute;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;


/**
 * <p>Java class for ResourceIdType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResourceIdType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}AbstractIdType">
 *       &lt;attribute name="rid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="previousRid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" type="{http://www.opengis.net/fes/2.0}VersionType" />
 *       &lt;attribute name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceIdType")
public class ResourceIdType extends AbstractIdType  implements FeatureId {

    @XmlAttribute(required = true)
    private String rid;
    @XmlAttribute
    private String previousRid;
    @XmlAttribute
    private String version;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar startDate;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar endDate;

    public ResourceIdType() {
        
    }
    
    public ResourceIdType(final String rid) {
        this.rid = rid;
    }
    
    /**
     * Gets the value of the rid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRid() {
        return rid;
    }

    /**
     * Sets the value of the rid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRid(String value) {
        this.rid = value;
    }

    /**
     * Gets the value of the previousRid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreviousRid() {
        return previousRid;
    }

    /**
     * Sets the value of the previousRid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreviousRid(String value) {
        this.previousRid = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ResourceIdType]\n");
        if (version != null) {
           sb.append("version: ").append(version).append('\n');
        }
        if (previousRid != null) {
           sb.append("previousRid: ").append(previousRid).append('\n');
        }
        if (rid != null) {
            sb.append("rid: ").append(rid).append('\n');
        }
        if (startDate != null) {
            sb.append("startDate: ").append(startDate).append('\n');
        }
        if (endDate != null) {
            sb.append("endDate: ").append(endDate).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ResourceIdType) {
            final ResourceIdType that = (ResourceIdType) object;
            return Objects.equals(this.endDate,      that.endDate)        &&
                   Objects.equals(this.previousRid,  that.previousRid)   &&
                   Objects.equals(this.rid,          that.rid)   &&
                   Objects.equals(this.startDate,    that.startDate)   &&
                   Objects.equals(this.version,      that.version) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.endDate != null ? this.endDate.hashCode() : 0);
        hash = 19 * hash + (this.previousRid != null ? this.previousRid.hashCode() : 0);
        hash = 19 * hash + (this.rid != null ? this.rid.hashCode() : 0);
        hash = 19 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 19 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        return hash;
    }

    @Override
    public String getID() {
        return rid;
    }

    @Override
    public boolean matches(Object feature) {
        if (feature instanceof Attribute) {
            final Identifier identifier = ((Attribute)feature).getIdentifier();
            return identifier != null && rid.equals(identifier.getID());
        }
        return false;
    }
}
