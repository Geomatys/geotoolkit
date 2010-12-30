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
package org.geotoolkit.ows.xml.v110;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;


/**
 * This type is adapted from the EnvelopeType of GML 3.1, with modified contents and documentation for encoding a MINIMUM size box SURROUNDING all associated data. 
 * 
 * <p>Java class for BoundingBoxType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BoundingBoxType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LowerCorner" type="{http://www.opengis.net/ows/1.1}PositionType"/>
 *         &lt;element name="UpperCorner" type="{http://www.opengis.net/ows/1.1}PositionType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="crs" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="dimensions" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoundingBoxType", propOrder = {
    "lowerCorner",
    "upperCorner"
})
@XmlSeeAlso({
    WGS84BoundingBoxType.class
})
public class BoundingBoxType {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.ows.xml.v110");

    @XmlList
    @XmlElement(name = "LowerCorner", type = Double.class)
    private List<Double> lowerCorner  = new ArrayList<Double>();
    @XmlList
    @XmlElement(name = "UpperCorner", type = Double.class)
    private List<Double> upperCorner = new ArrayList<Double>();
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String crs;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer dimensions;

    BoundingBoxType(){
    }
    
    /**
     * Build a 2 dimension boundingBox.
     * 
     * @param crs
     * @param maxx
     * @param maxy
     * @param minx
     * @param miny
     */
    public BoundingBoxType(String crs, double minx, double miny, double maxx, double maxy){
        this.dimensions = 2;
        this.lowerCorner.add(minx);
        this.lowerCorner.add(miny);
        this.upperCorner.add(maxx);
        this.upperCorner.add(maxy);
        this.crs = crs;
    }

    public BoundingBoxType(Envelope envelope) {
        if (envelope != null) {
            for (Double d : envelope.getLowerCorner().getCoordinate()) {
                this.lowerCorner.add(d);
            }
            for (Double d : envelope.getUpperCorner().getCoordinate()) {
                this.upperCorner.add(d);
            }
            final CoordinateReferenceSystem crss = envelope.getCoordinateReferenceSystem();
            if (crss != null) {
                try {
                    crs = "EPSG:" + CRS.lookupEpsgCode(crss, true);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.SEVERE, "Factory exception while creating OWS BoundingBox from opengis one", ex);
                }
            }
            this.dimensions = envelope.getDimension();
        }
    }
    
    /**
     * Gets the value of the lowerCorner property.
     * (unmodifiable)
     */
    public List<Double> getLowerCorner() {
        return lowerCorner;
    }

    /**
     * Gets the value of the upperCorner property.
     * (unmodifiable)
     */
    public List<Double> getUpperCorner() {
        return upperCorner;
    }

    /**
     * Gets the value of the crs property.
     * 
     */
    public String getCrs() {
        return crs;
    }

    /**
     * Gets the value of the dimensions property.
     */
    public Integer getDimensions() {
        return dimensions;
    }
}
