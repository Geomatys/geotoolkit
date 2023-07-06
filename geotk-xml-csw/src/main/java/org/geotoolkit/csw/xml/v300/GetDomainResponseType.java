/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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

package org.geotoolkit.csw.xml.v300;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.DomainValues;
import org.geotoolkit.csw.xml.GetDomainResponse;


/**
 *
 *             Returns the actual values for some property. In general this is
 *             a subset of the value domain (that is, set of permissible values),
 *             although in some cases these may be the same.
 *
 *
 * <p>Classe Java pour GetDomainResponseType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GetDomainResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DomainValues" type="{http://www.opengis.net/cat/csw/3.0}DomainValuesType" maxOccurs="unbounded"/>
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
    protected List<DomainValuesType> domainValues;

    /**
     * An empty constructor used by JAXB
     */
    public GetDomainResponseType() {

    }

    /**
     * build a new response to a getDomain request
     */
    public GetDomainResponseType(final List<DomainValues> domainValues) {
        if (domainValues != null) {
            this.domainValues = new ArrayList<>(domainValues.size());
            for (DomainValues dv : domainValues) {
                List<Object> listOfValues = null;
                if (dv.getListOfValues() != null) {
                    listOfValues = (List<Object>) dv.getListOfValues().getValue();
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
    @Override
    public List<DomainValuesType> getDomainValues() {
        if (domainValues == null) {
            domainValues = new ArrayList<>();
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

            return  Objects.equals(this.domainValues, that.domainValues);
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
