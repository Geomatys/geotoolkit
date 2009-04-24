/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wcs.xml.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.PolygonType;
import org.geotoolkit.gml.xml.v311modified.EnvelopeEntry;
import org.geotoolkit.gml.xml.v311modified.GridType;
import org.geotoolkit.gml.xml.v311modified.ObjectFactory;

/**
 * Definition of the spatial domain of a coverage. 
 * 
 * <p>Java class for SpatialDomainType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpatialDomainType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}BoundingBox" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}GridCRS" minOccurs="0"/>
 *         &lt;element name="Transformation" type="{http://www.opengis.net/gml}AbstractCoordinateOperationType" minOccurs="0"/>
 *         &lt;element name="ImageCRS" type="{http://www.opengis.net/wcs}ImageCRSRefType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}Polygon" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpatialDomainType", propOrder = {
    "envelope",
    "grid",
    "polygon"
})
public class SpatialDomainType {

    // for both version 1.0.0 et 1.1.1
    @XmlElement(name = "Polygon", namespace = "http://www.opengis.net/gml")
    private List<PolygonType> polygon = new ArrayList<PolygonType>();

    /**
     * WCS version 1.0.0 attribute
     */ 
    @XmlElementRef(name = "Envelope", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<? extends EnvelopeEntry>> envelope;
    @XmlElementRef(name = "Grid", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<? extends GridType>> grid;
    
    
    /**
     * An empty constructor used by JAXB.
     */
    SpatialDomainType(){
    }
    
    /**
     * Build a new light Spatial Domain type version 1.0.0
     */
    public SpatialDomainType(EnvelopeEntry envelope) {
       ObjectFactory gmlFactory = new ObjectFactory();
       this.envelope = new ArrayList<JAXBElement<? extends EnvelopeEntry>>();
       this.envelope.add(gmlFactory.createEnvelope(envelope));
    }
    
    /**
     * Build a new light Spatial Domain type version 1.0.0
     */
    public SpatialDomainType(EnvelopeEntry envelope, GridType grid) {
       ObjectFactory gmlFactory = new ObjectFactory();
       this.envelope = new ArrayList<JAXBElement<? extends EnvelopeEntry>>();
       this.envelope.add(gmlFactory.createEnvelope(envelope));
       this.grid     = new ArrayList<JAXBElement<? extends GridType>>();
       this.grid.add(gmlFactory.createGrid(grid));
    }
    
    /**
     * Build a new full Spatial Domain type version 1.0.0
     */
    public SpatialDomainType(List<JAXBElement<? extends EnvelopeEntry>> envelope, List<JAXBElement<? extends GridType>> grid,
            List<PolygonType> polygon) {
       this.envelope = envelope;
       this.grid     = grid;
       this.polygon  = polygon;
       
    }
    
    /**
     * Gets the value of the envelope.
     * 
     */
    public EnvelopeEntry getEnvelope() {
        if (envelope != null && envelope.size() >0) {
            return envelope.get(0).getValue();
        }
        return null;
    }
    
    /**
     * Gets the value of the grid.
     * 
     */
    public GridType getGrid() {
        if (grid != null && grid.size() >0) {
            return grid.get(0).getValue();
        }
        return null;
    }

    /**
     * Unordered list of polygons whose union (combined areas) covers the spatial domain of this coverage.
     * Polygons are particularly useful for areas that are poorly approximated by a BoundingBox 
     * (such as satellite image swaths, island groups, other non-convex areas). 
     * 
     */
    public List<PolygonType> getPolygon() {
       return Collections.unmodifiableList(polygon);
    }

}
