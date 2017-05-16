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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sos.xml.GetResultTemplate;
import org.geotoolkit.swes.xml.v200.ExtensibleRequestType;


/**
 * <p>Java class for GetResultTemplateType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GetResultTemplateType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleRequestType">
 *       &lt;sequence>
 *         &lt;element name="offering" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="observedProperty" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetResultTemplateType", propOrder = {
    "offering",
    "observedProperty"
})
@XmlRootElement(name="GetResultTemplate")
public class GetResultTemplateType extends ExtensibleRequestType implements GetResultTemplate {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String offering;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String observedProperty;

    public GetResultTemplateType() {

    }

    public GetResultTemplateType(final String version, final String offering, final String observedProperty) {
        super(version, "SOS");
        this.observedProperty = observedProperty;
        this.offering         = offering;
    }

    public GetResultTemplateType(final String version, final String service, final String offering, final String observedProperty) {
        super(version, service);
        this.observedProperty = observedProperty;
        this.offering         = offering;
    }

    public GetResultTemplateType(final String version, final String service, final String offering, final String observedProperty, final List<Object> extension) {
        super(version, service, extension);
        this.observedProperty = observedProperty;
        this.offering         = offering;
    }

    /**
     * Gets the value of the offering property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOffering() {
        return offering;
    }

    /**
     * Sets the value of the offering property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOffering(String value) {
        this.offering = value;
    }

    /**
     * Gets the value of the observedProperty property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getObservedProperty() {
        return observedProperty;
    }

    /**
     * Sets the value of the observedProperty property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setObservedProperty(String value) {
        this.observedProperty = value;
    }

    @Override
    public String getResponseFormat() {
        for (Object ext : getExtension()) {
            if (ext instanceof String) {
                String outputFormat = (String) ext;
                if (outputFormat.startsWith("responseFormat=")) {
                    return outputFormat.substring(15);
                }
            }
        }
        return "text/xml";
    }
}
