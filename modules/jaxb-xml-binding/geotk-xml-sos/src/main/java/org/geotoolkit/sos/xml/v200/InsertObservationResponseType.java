/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.sos.xml.v200;

import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sos.xml.InsertObservationResponse;
import org.geotoolkit.swes.xml.v200.ExtensibleResponseType;


/**
 * <p>Java class for InsertObservationResponseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InsertObservationResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleResponseType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertObservationResponseType")
public class InsertObservationResponseType extends ExtensibleResponseType implements InsertObservationResponse {

    @XmlElement
    private List<String> observation;

    public InsertObservationResponseType() {

    }

    public InsertObservationResponseType(final List<String> assignedObservationIds) {
        this.observation = assignedObservationIds;
    }

    /**
     * @return the observation
     */
    public List<String> getObservation() {
        return observation;
    }

    /**
     * @param observation the observation to set
     */
    public void setObservation(List<String> observation) {
        this.observation = observation;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 89 * hash + (this.observation != null ? this.observation.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof InsertObservationResponseType && super.equals(obj)) {
            final InsertObservationResponseType that = (InsertObservationResponseType) obj;
            return Objects.equals(this.observation, that.observation);
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (observation != null) {
            sb.append("observation:\n");
            for (Object obs : observation) {
                sb.append(obs).append('\n');
            }
        }
        return sb.toString();
    }
}
