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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.wmts._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Layer_QNAME = new QName("http://www.opengis.net/wmts/1.0", "Layer");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wmts._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Theme }
     * 
     */
    public Theme createTheme() {
        return new Theme();
    }

    /**
     * Create an instance of {@link TileMatrix }
     * 
     */
    public TileMatrix createTileMatrix() {
        return new TileMatrix();
    }

    /**
     * Create an instance of {@link Dimension }
     * 
     */
    public Dimension createDimension() {
        return new Dimension();
    }

    /**
     * Create an instance of {@link TileMatrixSetLimits }
     * 
     */
    public TileMatrixSetLimits createTileMatrixSetLimits() {
        return new TileMatrixSetLimits();
    }

    /**
     * Create an instance of {@link BinaryPayload }
     * 
     */
    public BinaryPayload createBinaryPayload() {
        return new BinaryPayload();
    }

    /**
     * Create an instance of {@link ContentsType }
     * 
     */
    public ContentsType createContentsType() {
        return new ContentsType();
    }

    /**
     * Create an instance of {@link TileMatrixSet }
     * 
     */
    public TileMatrixSet createTileMatrixSet() {
        return new TileMatrixSet();
    }

    /**
     * Create an instance of {@link Themes }
     * 
     */
    public Themes createThemes() {
        return new Themes();
    }

    /**
     * Create an instance of {@link Capabilities }
     * 
     */
    public Capabilities createCapabilities() {
        return new Capabilities();
    }

    /**
     * Create an instance of {@link LayerType }
     * 
     */
    public LayerType createLayerType() {
        return new LayerType();
    }

    /**
     * Create an instance of {@link TileMatrixLimits }
     * 
     */
    public TileMatrixLimits createTileMatrixLimits() {
        return new TileMatrixLimits();
    }

    /**
     * Create an instance of {@link GetTile }
     * 
     */
    public GetTile createGetTile() {
        return new GetTile();
    }

    /**
     * Create an instance of {@link GetFeatureInfo }
     * 
     */
    public GetFeatureInfo createGetFeatureInfo() {
        return new GetFeatureInfo();
    }

    /**
     * Create an instance of {@link LegendURL }
     * 
     */
    public LegendURL createLegendURL() {
        return new LegendURL();
    }

    /**
     * Create an instance of {@link Style }
     * 
     */
    public Style createStyle() {
        return new Style();
    }

    /**
     * Create an instance of {@link URLTemplateType }
     * 
     */
    public URLTemplateType createURLTemplateType() {
        return new URLTemplateType();
    }

    /**
     * Create an instance of {@link TextPayload }
     * 
     */
    public TextPayload createTextPayload() {
        return new TextPayload();
    }

    /**
     * Create an instance of {@link FeatureInfoResponse }
     * 
     */
    public FeatureInfoResponse createFeatureInfoResponse() {
        return new FeatureInfoResponse();
    }

    /**
     * Create an instance of {@link GetCapabilities }
     * 
     */
    public GetCapabilities createGetCapabilities() {
        return new GetCapabilities();
    }

    /**
     * Create an instance of {@link TileMatrixSetLink }
     * 
     */
    public TileMatrixSetLink createTileMatrixSetLink() {
        return new TileMatrixSetLink();
    }

    /**
     * Create an instance of {@link DimensionNameValue }
     * 
     */
    public DimensionNameValue createDimensionNameValue() {
        return new DimensionNameValue();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LayerType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wmts/1.0", name = "Layer", substitutionHeadNamespace = "http://www.opengis.net/ows/1.1", substitutionHeadName = "DatasetDescriptionSummary")
    public JAXBElement<LayerType> createLayer(final LayerType value) {
        return new JAXBElement<LayerType>(_Layer_QNAME, LayerType.class, null, value);
    }

}
