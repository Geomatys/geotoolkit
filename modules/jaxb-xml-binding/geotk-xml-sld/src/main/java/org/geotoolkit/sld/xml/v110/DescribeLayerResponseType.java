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
package org.geotoolkit.sld.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.WMSResponse;


/**
 * <p>Java class for DescribeLayerResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeLayerResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Version" type="{http://www.opengis.net/ows}VersionType"/>
 *         &lt;element name="LayerDescription" type="{http://www.opengis.net/sld}LayerDescriptionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeLayerResponseType", propOrder = {
    "version",
    "layerDescription"
})
@XmlRootElement(name="WMS_DescribeLayerResponse")
public class DescribeLayerResponseType implements WMSResponse {

    @XmlElement(name = "Version", required = true)
    private String version;
    @XmlElement(name = "LayerDescription", required = true)
    private List<LayerDescriptionType> layerDescription = new ArrayList<LayerDescriptionType>();

    /**
     * An empty constructor used by JAXB
     */
    DescribeLayerResponseType() {}
    
    /**
     * Build a new response to a DescribeLayer request.
     * 
     * @param version the version of sld specification.
     * @param layerDescriptions a list of layer description.
     */
    public DescribeLayerResponseType(String version, LayerDescriptionType... layerDescriptions) {
        this.version = version;
        for (final LayerDescriptionType element : layerDescriptions) {
            this.layerDescription.add(element);
        }
    }
    
    /**
     * Build a new response to a DescribeLayer request.
     * 
     * @param version the version of sld specification.
     * @param layerDescriptions a list of layer description.
     */
    public DescribeLayerResponseType(String version, List<LayerDescriptionType> layerDescriptions) {
        this.version          = version;
        this.layerDescription = layerDescriptions;
    }
        
    /**
     * Gets the value of the version property.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the value of the layerDescription property.
     */
    public List<LayerDescriptionType> getLayerDescription() {
        return this.layerDescription;
    }

}
