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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AcceptFormats;


/**
 * Prioritized sequence of zero or more GetCapabilities
 *       operation response formats desired by client, with preferred formats
 *       listed first. Each response format shall be identified by its MIME type.
 *       See AcceptFormats parameter use subclause for more
 *       information.
 *
 * <p>Java class for AcceptFormatsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AcceptFormatsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OutputFormat" type="{http://www.opengis.net/ows/2.0}MimeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcceptFormatsType", propOrder = {
    "outputFormat"
})
public class AcceptFormatsType implements AcceptFormats {

    @XmlElement(name = "OutputFormat")
    private List<String> outputFormat;

    /**
     * An empty constructor used by JAXB.
     */
    AcceptFormatsType() {
    }

    /**
     * Build a new Accepted format.
     */
    public AcceptFormatsType(final List<String> outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * Build a new Accepted format.
     */
    public AcceptFormatsType(final String... outputFormat) {
        this.outputFormat = new ArrayList<String>();
        for (String element: outputFormat) {
            if (element != null)
                this.outputFormat.add(element);
        }
    }

    /**
     * Gets the value of the outputFormat property.
     *
     */
    @Override
    public List<String> getOutputFormat() {
        if (outputFormat == null) {
            outputFormat = new ArrayList<String>();
        }
        return this.outputFormat;
    }

}
