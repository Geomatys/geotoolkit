/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.georss.xml.v100;

import org.geotoolkit.gml.xml.v311.EnvelopeType;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WhereType", propOrder = {
        "envelope"
})
public class WhereType {
    @XmlElement(name="Envelope", namespace = "http://www.opengis.net/gml", required = true)
    protected EnvelopeType envelope;

    public WhereType() {

    }

    public WhereType(EnvelopeType envelope) {
        this.envelope = envelope;
    }

    public EnvelopeType getEnvelope() {
        return envelope;
    }

    public void setEnvelope(EnvelopeType envelope) {
        this.envelope = envelope;
    }

}
