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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import org.geotoolkit.csw.xml.Harvest;


/**
 *
 *             Requests that the catalogue attempt to harvest a resource from some
 *             network location identified by the source URL.
 *
 *             Source          - a URL from which the resource is retrieved
 *             ResourceType    - normally a URI that specifies the type of the
 *                               resource being harvested
 *             ResourceFormat  - a media type indicating the format of the
 *                               resource being harvested.  The default is
 *                               "application/xml".
 *             ResponseHandler - a reference to some endpoint to which the
 *                               response shall be forwarded when the
 *                               harvest operation has been completed
 *             HarvestInterval - an interval expressed using the ISO 8601 syntax;
 *                               it specifies the interval between harvest
 *                               attempts (e.g., P6M indicates an interval of
 *                               six months).
 *
 *
 * <p>Classe Java pour HarvestType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="HarvestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/3.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="Source" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="ResourceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ResourceFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HarvestInterval" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/>
 *         &lt;element name="ResponseHandler" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HarvestType", propOrder = {
    "source",
    "resourceType",
    "resourceFormat",
    "harvestInterval",
    "responseHandler"
})
public class HarvestType extends RequestBaseType implements Harvest {

    @XmlElement(name = "Source", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String source;
    @XmlElement(name = "ResourceType", required = true)
    protected String resourceType;
    @XmlElement(name = "ResourceFormat", defaultValue = "application/xml")
    protected String resourceFormat;
    @XmlElement(name = "HarvestInterval")
    protected Duration harvestInterval;
    @XmlElement(name = "ResponseHandler")
    @XmlSchemaType(name = "anyURI")
    protected List<String> responseHandler;

    /**
     * An empty constructor used by JAXB
     */
    HarvestType() {
    }

    /**
     * Build a new harvest request
     *
     * @param service Service type fixed at CSW.
     * @param version The service version fixed at 3.0.0.
     * @param source  The distant resource URL.
     * @param resourceType The type of the resource to harvest.
     */
    public HarvestType(final String service, final String version, final String source, final String resourceType,
            final String resourceFormat, final String handler, final Duration harvestInterval) {
        super(service, version);
        this.source          = source;
        this.resourceType    = resourceType;
        this.resourceFormat  = resourceFormat;
        this.responseHandler = new ArrayList<>();
        this.responseHandler.add(handler);
        this.harvestInterval = harvestInterval;

    }

    /**
     * Obtient la valeur de la propriété source.
     */
    @Override
    public String getSource() {
        return source;
    }

    /**
     * Définit la valeur de la propriété source.
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Obtient la valeur de la propriété resourceType.
     */
    @Override
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Définit la valeur de la propriété resourceType.
     */
    public void setResourceType(String value) {
        this.resourceType = value;
    }

    /**
     * Obtient la valeur de la propriété resourceFormat.
     */
    @Override
    public String getResourceFormat() {
        return resourceFormat;
    }

    /**
     * Définit la valeur de la propriété resourceFormat.
     */
    public void setResourceFormat(String value) {
        this.resourceFormat = value;
    }

    /**
     * Obtient la valeur de la propriété harvestInterval.
     */
    @Override
    public Duration getHarvestInterval() {
        return harvestInterval;
    }

    /**
     * Définit la valeur de la propriété harvestInterval.
     */
    public void setHarvestInterval(Duration value) {
        this.harvestInterval = value;
    }

    /**
     * Gets the value of the responseHandler property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the responseHandler property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResponseHandler().add(newItem);
     * </pre>
     */
    @Override
    public List<String> getResponseHandler() {
        if (responseHandler == null) {
            responseHandler = new ArrayList<>();
        }
        return this.responseHandler;
    }

    @Override
    public String getOutputFormat() {
        return "application/xml";
    }

    @Override
    public void setOutputFormat(final String value) {}
}
