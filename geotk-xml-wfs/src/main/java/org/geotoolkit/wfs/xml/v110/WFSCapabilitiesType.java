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
package org.geotoolkit.wfs.xml.v110;

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v110.FilterCapabilities;
import org.geotoolkit.ows.xml.AbstractCapabilitiesBase;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v100.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v100.OperationsMetadata;
import org.geotoolkit.ows.xml.v100.ServiceIdentification;
import org.geotoolkit.ows.xml.v100.ServiceProvider;
import org.geotoolkit.wfs.xml.WFSCapabilities;
import org.geotoolkit.wfs.xml.WFSResponse;


/**
 * XML encoded WFS GetCapabilities operation response. This document provides clients with service metadata about a
 * specific service instance, including metadata about the tightly-coupled data served.
 * If the server does not implement the updateSequence parameter,
 * the server shall always return the complete Capabilities document,
 * without the updateSequence parameter.
 * When the server implements the updateSequence parameter and the GetCapabilities operation request included
 * the updateSequence parameter with the current value, the server shall return this element with only the "version" and
 * "updateSequence" attributes.
 * Otherwise, all optional elements shall be included or not depending on the actual value of the
 * Contents parameter in the GetCapabilities operation request.
 *
 *
 * <p>Java class for WFS_CapabilitiesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="WFS_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows}CapabilitiesBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wfs}FeatureTypeList" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}ServesGMLObjectTypeList" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}SupportsGMLObjectTypeList" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Filter_Capabilities"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WFS_CapabilitiesType", propOrder = {
    "featureTypeList",
    "servesGMLObjectTypeList",
    "supportsGMLObjectTypeList",
    "filterCapabilities"
})
@XmlRootElement(name = "WFS_Capabilities")
public class WFSCapabilitiesType extends CapabilitiesBaseType implements WFSResponse, WFSCapabilities {

    @XmlElement(name = "FeatureTypeList")
    private FeatureTypeListType featureTypeList;
    @XmlElement(name = "ServesGMLObjectTypeList")
    private GMLObjectTypeListType servesGMLObjectTypeList;
    @XmlElement(name = "SupportsGMLObjectTypeList")
    private GMLObjectTypeListType supportsGMLObjectTypeList;
    @XmlElement(name = "Filter_Capabilities", namespace = "http://www.opengis.net/ogc", required = true)
    private FilterCapabilities filterCapabilities;

    public WFSCapabilitiesType() {

    }

    public WFSCapabilitiesType(final String version, final String updateSequence) {
        super(version, updateSequence);
    }

    public WFSCapabilitiesType(final String version, final String updateSequence, final ServiceIdentification si, final ServiceProvider sp, final OperationsMetadata om,
            final FeatureTypeListType ft, final FilterCapabilities fc) {
        super(si, sp, om, version, updateSequence);
        this.featureTypeList = ft;
        this.filterCapabilities = fc;
    }

    /**
     * Gets the value of the featureTypeList property.
     *
     * @return
     *     possible object is
     *     {@link FeatureTypeListType }
     *
     */
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
    public void setFeatureTypeList(final FeatureTypeListType value) {
        this.featureTypeList = value;
    }

    /**
     * Gets the value of the servesGMLObjectTypeList property.
     *
     * @return
     *     possible object is
     *     {@link GMLObjectTypeListType }
     *
     */
    public GMLObjectTypeListType getServesGMLObjectTypeList() {
        return servesGMLObjectTypeList;
    }

    /**
     * Sets the value of the servesGMLObjectTypeList property.
     *
     * @param value
     *     allowed object is
     *     {@link GMLObjectTypeListType }
     *
     */
    public void setServesGMLObjectTypeList(final GMLObjectTypeListType value) {
        this.servesGMLObjectTypeList = value;
    }

    /**
     * Gets the value of the supportsGMLObjectTypeList property.
     *
     * @return
     *     possible object is
     *     {@link GMLObjectTypeListType }
     *
     */
    public GMLObjectTypeListType getSupportsGMLObjectTypeList() {
        return supportsGMLObjectTypeList;
    }

    /**
     * Sets the value of the supportsGMLObjectTypeList property.
     *
     * @param value
     *     allowed object is
     *     {@link GMLObjectTypeListType }
     *
     */
    public void setSupportsGMLObjectTypeList(final GMLObjectTypeListType value) {
        this.supportsGMLObjectTypeList = value;
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
    public void setFilterCapabilities(final FilterCapabilities value) {
        this.filterCapabilities = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof WFSCapabilitiesType && super.equals(object)) {
            final WFSCapabilitiesType that = (WFSCapabilitiesType) object;

            return Objects.equals(this.featureTypeList,           that.featureTypeList)           &&
                   Objects.equals(this.filterCapabilities,        that.filterCapabilities)        &&
                   Objects.equals(this.servesGMLObjectTypeList,   that.servesGMLObjectTypeList)   &&
                   Objects.equals(this.supportsGMLObjectTypeList, that.supportsGMLObjectTypeList);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.featureTypeList != null ? this.featureTypeList.hashCode() : 0);
        hash = 23 * hash + (this.servesGMLObjectTypeList != null ? this.servesGMLObjectTypeList.hashCode() : 0);
        hash = 23 * hash + (this.supportsGMLObjectTypeList != null ? this.supportsGMLObjectTypeList.hashCode() : 0);
        hash = 23 * hash + (this.filterCapabilities != null ? this.filterCapabilities.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if(featureTypeList != null) {
            s.append("featureTypeList:").append(featureTypeList).append('\n');
        }
        if (servesGMLObjectTypeList != null) {
            s.append("servesGMLObjectTypeList:").append(servesGMLObjectTypeList).append('\n');
        }
        if (supportsGMLObjectTypeList != null) {
            s.append("supportsGMLObjectTypeList:").append(supportsGMLObjectTypeList).append('\n');
        }
        if (filterCapabilities != null) {
            s.append("filterCapabilities:").append(filterCapabilities).append('\n');
        }
        return s.toString();
    }

    @Override
    public AbstractCapabilitiesBase applySections(Sections sections) {
        FeatureTypeListType ftl     = null;
        OperationsMetadata om       = null;
        ServiceProvider sp          = null;
        ServiceIdentification si    = null;
        if (sections.containsSection("featureTypeList")|| sections.containsSection("All")) {
            ftl = featureTypeList;
        }
        if (sections.containsSection("operationsMetadata")|| sections.containsSection("All")) {
            om =  getOperationsMetadata();
        }
        if (sections.containsSection("serviceProvider")|| sections.containsSection("All")) {
            sp = getServiceProvider();
        }
        if (sections.containsSection("serviceIdentification")|| sections.containsSection("All")) {
            si = getServiceIdentification();
        }
        return new WFSCapabilitiesType("1.1.0", getUpdateSequence(), si, sp, om, ftl, filterCapabilities);
    }
}
