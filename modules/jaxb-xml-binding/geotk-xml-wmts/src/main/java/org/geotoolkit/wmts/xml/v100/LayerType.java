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
package org.geotoolkit.wmts.xml.v100;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;
import org.geotoolkit.ows.xml.v110.DatasetDescriptionSummaryBaseType;


/**
 * <p>Java class for LayerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LayerType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}DatasetDescriptionSummaryBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}Style" maxOccurs="unbounded"/>
 *         &lt;element name="Format" type="{http://www.opengis.net/ows/1.1}MimeType" maxOccurs="unbounded"/>
 *         &lt;element name="InfoFormat" type="{http://www.opengis.net/ows/1.1}MimeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}Dimension" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}TileMatrixSetLink" maxOccurs="unbounded"/>
 *         &lt;element name="ResourceURL" type="{http://www.opengis.net/wmts/1.0}URLTemplateType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LayerType", propOrder = {
    "style",
    "format",
    "infoFormat",
    "dimension",
    "tileMatrixSetLink",
    "resourceURL"
})
public class LayerType extends DatasetDescriptionSummaryBaseType {

    @XmlElement(name = "Style", required = true)
    private List<Style> style;
    @XmlElement(name = "Format", required = true)
    private List<String> format;
    @XmlElement(name = "InfoFormat")
    private List<String> infoFormat;
    @XmlElement(name = "Dimension")
    private List<Dimension> dimension;
    @XmlElement(name = "TileMatrixSetLink", required = true)
    private List<TileMatrixSetLink> tileMatrixSetLink;
    @XmlElement(name = "ResourceURL")
    private List<URLTemplateType> resourceURL;

    public LayerType() {

    }

    public LayerType(final String layerName, final String remarks, final BoundingBoxType bbox, final List<Style> style, final List<Dimension> dimension) {
        super(layerName, remarks, Arrays.asList(bbox));
        this.style     = style;
        this.dimension = dimension;
    }

    /**
     * Metadata about the styles of this layer Gets the value of the style property.
     */
    public List<Style> getStyle() {
        if (style == null) {
            style = new ArrayList<Style>();
        }
        return this.style;
    }

    /**
     * Gets the value of the format property.
     */
    public List<String> getFormat() {
        if (format == null) {
            format = new ArrayList<String>();
        }
        return this.format;
    }

    /**
     * Gets the value of the infoFormat property.
     */
    public List<String> getInfoFormat() {
        if (infoFormat == null) {
            infoFormat = new ArrayList<String>();
        }
        return this.infoFormat;
    }

    /**
     * Extra dimensions for a tile and FeatureInfo requests.
     * Gets the value of the dimension property.
     */
    public List<Dimension> getDimension() {
        if (dimension == null) {
            dimension = new ArrayList<Dimension>();
        }
        return this.dimension;
    }

    /**
     * Reference to a tileMatrixSet and limits Gets the value of the tileMatrixSetLink property.
     * 
     */
    public List<TileMatrixSetLink> getTileMatrixSetLink() {
        if (tileMatrixSetLink == null) {
            tileMatrixSetLink = new ArrayList<TileMatrixSetLink>();
        }
        return this.tileMatrixSetLink;
    }

    /**
     * Gets the value of the resourceURL property.
     */
    public List<URLTemplateType> getResourceURL() {
        if (resourceURL == null) {
            resourceURL = new ArrayList<URLTemplateType>();
        }
        return this.resourceURL;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("class=LayerType").append('\n');
        s.append("style:").append('\n');
        for (Style st:getStyle()) {
             s.append(st).append('\n');
        }
        s.append("format:").append('\n');
        for (String f:getFormat()) {
             s.append(f).append('\n');
        }
        s.append("infoFormat:").append('\n');
        for (String infoForm:getInfoFormat()) {
             s.append(infoForm).append('\n');
        }
        s.append("dimension:").append('\n');
        for (Dimension dim:getDimension()) {
             s.append(dim).append('\n');
        }
        s.append("tileMatrixSetLink:").append('\n');
        for (TileMatrixSetLink tms:getTileMatrixSetLink()) {
             s.append(tms).append('\n');
        }
        return s.toString();
    }
}
