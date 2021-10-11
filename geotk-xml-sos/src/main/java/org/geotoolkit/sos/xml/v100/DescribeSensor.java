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
package org.geotoolkit.sos.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sos/1.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="procedure" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *       &lt;attribute name="outputFormat" use="required" type="{http://www.opengis.net/ows/1.1}MimeType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeSensor", propOrder = {
    "procedure"
})
@XmlRootElement(name = "DescribeSensor")
public class DescribeSensor extends RequestBaseType implements org.geotoolkit.swes.xml.DescribeSensor {

    /**
     * Identifier of the sensor, for which detailed metadata is requested.
     */
    @XmlElement(name="procedure", namespace="http://www.opengis.net/sos/1.0", required = true)
    @XmlSchemaType(name = "anyURI")
    private String procedure;

    /**
     * Identifier of the output format to be used for the requested data.
     * The outputFormats supported by a SOS server are listed in the operations
     * metadata section of the service metadata (capabilities XML).
     * If this attribute is omitted, the outputFormat should be text/xml;subtype="sensorML/1.0.0".
     * If the requested outputFormat is not supported by the SOS server,
     * an exception message shall be returned.
     */
    @XmlAttribute(required = true)
    private String outputFormat;

    /**
     * An empty constructor used by jaxB
     */
     public DescribeSensor() {
     }

     /**
     * Build a new DescribeSensor Request
     */
     public DescribeSensor(final String version, final String service, final String procedure, final String outputFormat) {
         super(version, service);
         this.outputFormat = outputFormat;
         this.procedure    = procedure;
     }

     /**
     * Build a new DescribeSensor Request
     */
     public DescribeSensor(final String procedure, final String outputFormat) {
         this.outputFormat = outputFormat;
         this.procedure    = procedure;
     }

    /**
     * Return the value of the sensorId property.
     */
    @Override
    public String getProcedure() {
        return procedure;
    }

    /**
     * Return the value of the outputFormat property.
     */
    @Override
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DescribeSensor && super.equals(object)) {
            final DescribeSensor that = (DescribeSensor) object;
            return Objects.equals(this.outputFormat, that.outputFormat) &&
                   Objects.equals(this.procedure,   that.procedure);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.procedure != null ? this.procedure.hashCode() : 0);
        hash = 11 * hash + (this.outputFormat != null ? this.outputFormat.hashCode() : 0);
        return hash;
    }

}
