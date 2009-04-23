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
package org.geotoolkit.csw.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.DomainValues;
import org.geotoolkit.csw.xml.GetDomainResponse;
import org.geotoolkit.util.Utilities;


/**
 * 
 * Returns the actual values for some property. 
 * In general this is a subset of the value domain (that is, set of permissible values),
 * although in some cases these may coincide. 
 * Multiple value ranges may be returned if the property can assume values from multiple value domains (e.g. multiple taxonomies).
 *          
 * 
 * <p>Java class for GetDomainResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetDomainResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DomainValues" type="{http://www.opengis.net/cat/csw}DomainValuesType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetDomainResponseType", propOrder = {
    "domainValues"
})
@XmlRootElement(name="GetDomainResponse")
public class GetDomainResponseType implements GetDomainResponse {

    @XmlElement(name = "DomainValues", required = true)
    private List<DomainValuesType> domainValues;

    /**
     * An empty constructor used by JAXB
     */
    public GetDomainResponseType() {

    }

    /**
     * build a new response to a getDomain request
     */
    public GetDomainResponseType(List<DomainValues> domainValues) {
        if (domainValues != null) {
            this.domainValues = new ArrayList<DomainValuesType>(domainValues.size());
            for (DomainValues dv : domainValues) {
                List<String> listOfValues = null;
                if (dv.getListOfValues() != null) {
                    listOfValues = dv.getListOfValues().getValue();
                }
                DomainValuesType dvt = new DomainValuesType(dv.getParameterName(), dv.getPropertyName(), listOfValues, dv.getType());
                this.domainValues.add(dvt);
            }
        }
    }
    
    /**
     * Gets the value of the domainValues property.
     * 
     */
    public List<DomainValuesType> getDomainValues() {
        if (domainValues == null) {
            domainValues = new ArrayList<DomainValuesType>();
        }
        return this.domainValues;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[GetDomainResponseType]").append('\n');
        if (domainValues != null) {
            sb.append("domainValues:").append('\n');
            for (DomainValuesType d : domainValues) {
                sb.append(d).append('\n');
            }
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
        if (object instanceof GetDomainResponseType) {
            final GetDomainResponseType that = (GetDomainResponseType) object;

            return  Utilities.equals(this.domainValues, that.domainValues);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.domainValues != null ? this.domainValues.hashCode() : 0);
        return hash;
    }

}
