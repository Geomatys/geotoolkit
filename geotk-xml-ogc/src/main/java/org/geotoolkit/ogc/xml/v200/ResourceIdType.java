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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.feature.FeatureExt;
import org.opengis.feature.Feature;
import org.opengis.filter.ResourceId;


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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceIdType")
public class ResourceIdType extends AbstractIdType implements ResourceId {

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

    public ResourceIdType(final ResourceIdType that) {
        if (that != null) {
            this.rid         = that.rid;
            this.previousRid = that.previousRid;
            this.endDate     = that.endDate;
            this.startDate   = that.startDate;
            this.version     = that.version;
        }
    }

    /**
     * Gets the value of the rid property.
     */
    public String getRid() {
        return rid;
    }

    /**
     * Sets the value of the rid property.
     */
    public void setRid(String value) {
        this.rid = value;
    }

    /**
     * Gets the value of the previousRid property.
     */
    public String getPreviousRid() {
        return previousRid;
    }

    /**
     * Sets the value of the previousRid property.
     */
    public void setPreviousRid(String value) {
        this.previousRid = value;
    }

    /**
     * Gets the value of the version property.
     */
    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    /**
     * Sets the value of the version property.
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the startDate property.
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
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
    public String getIdentifier() {
        return rid;
    }

    public boolean matches(Object feature) {
        if (feature instanceof Feature) {
            final ResourceId identifier = FeatureExt.getId((Feature)feature);
            return identifier != null && rid.equals(identifier.getIdentifier());
        }
        return false;
    }

    @Override
    public Class getResourceClass() {
        return null;
    }

    @Override
    public List getExpressions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean test(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
