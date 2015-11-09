/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.tms.xml.v100;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Services }
     * 
     */
    public Services createServices() {
        return new Services();
    }

    /**
     * Create an instance of {@link TileMapService }
     * 
     */
    public TileMapService createTileMapService() {
        return new TileMapService();
    }

    /**
     * Create an instance of {@link ContactInformationType }
     * 
     */
    public ContactInformationType createContactInformationType() {
        return new ContactInformationType();
    }

    /**
     * Create an instance of {@link TileMapsType }
     * 
     */
    public TileMapsType createTileMapsType() {
        return new TileMapsType();
    }

    /**
     * Create an instance of {@link TileMap }
     * 
     */
    public TileMap createTileMap() {
        return new TileMap();
    }

    /**
     * Create an instance of {@link MetadataType }
     * 
     */
    public MetadataType createMetadataType() {
        return new MetadataType();
    }

    /**
     * Create an instance of {@link AttributionType }
     * 
     */
    public AttributionType createAttributionType() {
        return new AttributionType();
    }

    /**
     * Create an instance of {@link WebMapContextType }
     * 
     */
    public WebMapContextType createWebMapContextType() {
        return new WebMapContextType();
    }

    /**
     * Create an instance of {@link BoundingBoxType }
     * 
     */
    public BoundingBoxType createBoundingBoxType() {
        return new BoundingBoxType();
    }

    /**
     * Create an instance of {@link OriginType }
     * 
     */
    public OriginType createOriginType() {
        return new OriginType();
    }

    /**
     * Create an instance of {@link TileFormatType }
     * 
     */
    public TileFormatType createTileFormatType() {
        return new TileFormatType();
    }

    /**
     * Create an instance of {@link TileSetsType }
     * 
     */
    public TileSetsType createTileSetsType() {
        return new TileSetsType();
    }

    /**
     * Create an instance of {@link TileSetType }
     * 
     */
    public TileSetType createTileSetType() {
        return new TileSetType();
    }

    /**
     * Create an instance of {@link LogoType }
     * 
     */
    public LogoType createLogoType() {
        return new LogoType();
    }

    /**
     * Create an instance of {@link ContactPersonPrimaryType }
     * 
     */
    public ContactPersonPrimaryType createContactPersonPrimaryType() {
        return new ContactPersonPrimaryType();
    }

    /**
     * Create an instance of {@link ContactAddressType }
     * 
     */
    public ContactAddressType createContactAddressType() {
        return new ContactAddressType();
    }

}
