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
package org.geotoolkit.csw.xml.v202;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.GetDomain;


/**
 * Requests the actual values of some specified request parameter
 *         or other data element.
 *
 * <p>Java class for GetDomainType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GetDomainType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/2.0.2}RequestBaseType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="PropertyName" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *           &lt;element name="ParameterName" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetDomainType", propOrder = {
    "propertyName",
    "parameterName"
})
@XmlRootElement(name="GetDomain")
public class GetDomainType extends RequestBaseType implements GetDomain {

    @XmlElement(name = "PropertyName")
    @XmlSchemaType(name = "anyURI")
    private String propertyName;
    @XmlElement(name = "ParameterName")
    @XmlSchemaType(name = "anyURI")
    private String parameterName;

    /**
     * An empty constructor used by JAXB
     */
    public GetDomainType() {

    }

    /**
     * Build a new GetDomain request.
     * One of propertyName or parameterName must be null
     *
     * @param service
     * @param version
     * @param propertyName
     */
    public GetDomainType(final String service, final String version, final String propertyName, final String parameterName) {
        super(service, version);
        if (propertyName != null && parameterName != null) {
            throw new IllegalArgumentException("One of propertyName or parameterName must be null");
        }
        this.propertyName  = propertyName;
        this.parameterName = parameterName;
    }

    /**
     * Gets the value of the propertyName property.
     */
    @Override
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Gets the value of the parameterName property.
     */
    @Override
    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(final String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public String getOutputFormat() {
        return "application/xml";
    }

    @Override
    public void setOutputFormat(final String value) {}

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetDomainType && super.equals(object)) {
            final GetDomainType that = (GetDomainType) object;
            return Objects.equals(this.parameterName, that.parameterName) &&
                   Objects.equals(this.propertyName,  that.propertyName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.propertyName != null ? this.propertyName.hashCode() : 0);
        hash = 67 * hash + (this.parameterName != null ? this.parameterName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());

        if (parameterName != null) {
            s.append("parameterName: ").append(parameterName).append('\n');
        }
        if (propertyName != null) {
            s.append("propertyName: ").append(propertyName).append('\n');
        }

        return s.toString();
    }
}
