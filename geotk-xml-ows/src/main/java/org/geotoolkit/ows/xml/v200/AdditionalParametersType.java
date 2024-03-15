/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.sis.util.privy.UnmodifiableArrayList;
import org.geotoolkit.ows.xml.AbstractAdditionalParameters;
import static org.geotoolkit.ows.xml.v200.ObjectFactory._AdditionalParameter_QNAME;


/**
 * <p>Java class for AdditionalParametersType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AdditionalParametersType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}AdditionalParametersBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}AdditionalParameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdditionalParametersType", propOrder = {
    "additionalParameter"
})
public class AdditionalParametersType extends AdditionalParametersBaseType implements AbstractAdditionalParameters {

    @XmlElement(name = "AdditionalParameter")
    private List<AdditionalParameter> additionalParameter;

    public AdditionalParametersType() {

    }

    public AdditionalParametersType(List<AdditionalParameter> additionalParameter) {
        this.additionalParameter = additionalParameter;
    }

    public AdditionalParametersType(String role, List<AdditionalParameter> additionalParameter) {
        super(role);
        this.additionalParameter = additionalParameter;
    }

    /**
     * Gets the value of the additionalParameter property.
     *
     */
    @Override
    public List<AdditionalParameter> getAdditionalParameter() {
        final List<AdditionalParameter> results = new ArrayList<>();
        if (additionalParameter != null) {
            results.addAll(additionalParameter);
        }
        // at unmarshalling time additional parameters are swallowed by the abstractMetadata attribute of MetadataType.
        for (JAXBElement jb : getAbstractMetaData())  {
            if (jb.getName().equals(_AdditionalParameter_QNAME) && jb.getValue() instanceof AdditionalParameter) {
                results.add((AdditionalParameter) jb.getValue());
            }
        }
        return Collections.unmodifiableList(results);
    }

    public void setAdditionalParameter(List<AdditionalParameter> additionalParameter) {
        this.additionalParameter = additionalParameter;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof  AdditionalParametersType) {
            final AdditionalParametersType that = (AdditionalParametersType) object;
            return Objects.equals(this.additionalParameter, that.additionalParameter);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.additionalParameter);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(super.toString());
        if (additionalParameter != null) {
            s.append("additionalParameter:\n");
            for (AdditionalParameter a : additionalParameter) {
                s.append(a).append('\n');
            }
        }
        return s.toString();
    }
}
