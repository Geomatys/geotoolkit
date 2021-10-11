/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wfs.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v100.FilterCapabilities;
import org.geotoolkit.ows.xml.AbstractCapabilitiesBase;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.wfs.xml.WFSCapabilities;
import org.geotoolkit.wfs.xml.WFSResponse;


/**
 * <p>Java class for WFS_CapabilitiesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="WFS_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Service" type="{http://www.opengis.net/wfs}ServiceType"/>
 *         &lt;element name="Capability" type="{http://www.opengis.net/wfs}CapabilityType"/>
 *         &lt;element name="FeatureTypeList" type="{http://www.opengis.net/wfs}FeatureTypeListType"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Filter_Capabilities"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *       &lt;attribute name="updateSequence" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WFS_CapabilitiesType", propOrder = {
    "service",
    "capability",
    "featureTypeList",
    "filterCapabilities"
})
@XmlRootElement(name = "WFS_Capabilities")
public class WFSCapabilitiesType implements WFSResponse, WFSCapabilities {

    @XmlElement(name = "Service", required = true)
    private ServiceType service;
    @XmlElement(name = "Capability", required = true)
    private CapabilityType capability;
    @XmlElement(name = "FeatureTypeList", required = true)
    private FeatureTypeListType featureTypeList;
    @XmlElement(name = "Filter_Capabilities", namespace = "http://www.opengis.net/ogc", required = true)
    private FilterCapabilities filterCapabilities;
    @XmlAttribute
    private String version;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer updateSequence;

    public WFSCapabilitiesType() {

    }

    public WFSCapabilitiesType(final String version, final String updateSequence) {
        this.version = version;
        if (updateSequence != null) {
            try {
                this.updateSequence = Integer.parseInt(updateSequence);
            } catch (NumberFormatException ex)  {
                throw new IllegalArgumentException("updateSequence must be an integer for WFS 1.0.0", ex);
            }
        }
    }

    /**
     * Gets the value of the service property.
     *
     * @return
     *     possible object is
     *     {@link ServiceType }
     *
     */
    public ServiceType getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceType }
     *
     */
    public void setService(ServiceType value) {
        this.service = value;
    }

    /**
     * Gets the value of the capability property.
     *
     * @return
     *     possible object is
     *     {@link CapabilityType }
     *
     */
    public CapabilityType getCapability() {
        return capability;
    }

    /**
     * Sets the value of the capability property.
     *
     * @param value
     *     allowed object is
     *     {@link CapabilityType }
     *
     */
    public void setCapability(CapabilityType value) {
        this.capability = value;
    }

    @Override
    public void updateURL(final String url) {
        if (capability != null && capability.getRequest() != null) {
            capability.getRequest().updateURL(url);
        }
    }

    /**
     * Gets the value of the featureTypeList property.
     *
     * @return
     *     possible object is
     *     {@link FeatureTypeListType }
     *
     */
    @Override
    public FeatureTypeListType getFeatureTypeList() {
        return featureTypeList;
    }

    /**
     * Sets the value of the featureTypeList property.
     *
     * @param value
     *     allowed object is
     *     {@link FeatureTypeListType }
     *
     */
    public void setFeatureTypeList(FeatureTypeListType value) {
        this.featureTypeList = value;
    }

    /**
     * Gets the value of the filterCapabilities property.
     *
     * @return
     *     possible object is
     *     {@link FilterCapabilities }
     *
     */
    public FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

    /**
     * Sets the value of the filterCapabilities property.
     *
     * @param value
     *     allowed object is
     *     {@link FilterCapabilities }
     *
     */
    public void setFilterCapabilities(FilterCapabilities value) {
        this.filterCapabilities = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getVersion() {
        if (version == null) {
            return "1.0.0";
        } else {
            return version;
        }
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the updateSequence property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    @Override
    public String getUpdateSequence() {
        if (updateSequence == null) {
            return "0";
        } else {
            return updateSequence.toString();
        }
    }

    /**
     * Sets the value of the updateSequence property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setUpdateSequence(Integer value) {
        this.updateSequence = value;
    }

    @Override
    public AbstractServiceProvider getServiceProvider() {
        throw new UnsupportedOperationException("Not supported by this version.");
    }

    @Override
    public AbstractServiceIdentification getServiceIdentification() {
        throw new UnsupportedOperationException("Not supported by this version.");
    }

    @Override
    public AbstractOperationsMetadata getOperationsMetadata() {
        throw new UnsupportedOperationException("Not supported by this version.");
    }

    @Override
    public AbstractCapabilitiesBase applySections(Sections sections) {
        throw new UnsupportedOperationException("Not supported by this version.");
    }
}
