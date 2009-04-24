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
package org.geotoolkit.wcs.xml.v111;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.AbstractCoordinateOperationType;
import org.geotoolkit.gml.xml.v311modified.PolygonType;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;

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
    "boundingBox",
    "gridCRS",
    "transformation",
    "imageCRS",
    "polygon"
})
public class SpatialDomainType {

    /**
     * WCS version 1.1.1 attribute
     */ 
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows/1.1", type = JAXBElement.class)
    private List<JAXBElement<? extends BoundingBoxType>> boundingBox  = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
    @XmlElement(name = "GridCRS")
    private GridCrsType gridCRS;
    @XmlElement(name = "Transformation")
    private AbstractCoordinateOperationType transformation;
    @XmlElement(name = "ImageCRS")
    private ImageCRSRefType imageCRS;
    
    // for both version 1.0.0 et 1.1.1
    @XmlElement(name = "Polygon", namespace = "http://www.opengis.net/gml")
    private List<PolygonType> polygon = new ArrayList<PolygonType>();

    
    /**
     * An empty constructor used by JAXB.
     */
    SpatialDomainType(){
    }
    
    /**
     * Build a new light Spatial Domain type version 1.1.1
     */
    public SpatialDomainType(JAXBElement<? extends BoundingBoxType> boundingBox) {
       this.boundingBox.add(boundingBox);
    }
    
    /**
     * Build a new light Spatial Domain type version 1.1.1
     */
    public SpatialDomainType(List<JAXBElement<? extends BoundingBoxType>> boundingBoxes) {
       this.boundingBox = boundingBoxes;
    }
   
    
    /**
     * Build a new full Spatial Domain type version 1.1.1
     */
    public SpatialDomainType(List<JAXBElement<? extends BoundingBoxType>> boundingBox, GridCrsType gridCRS,
            AbstractCoordinateOperationType transformation, ImageCRSRefType imageCRS, List<PolygonType> polygon) {
       this.boundingBox    = boundingBox;
       this.gridCRS        = gridCRS;
       this.imageCRS       = imageCRS;
       this.polygon        = polygon;
       this.transformation = transformation;
    }
    
    
    /**
     * The first bounding box shall exactly specify the spatial domain of the offered coverage in the CRS of that offered coverage, 
     * thus specifying the available grid row and column indices. 
     * For a georectified coverage (that has a GridCRS), 
     * this bounding box shall specify the spatial domain in that GridCRS. 
     * For an image that is not georectified, this bounding box shall specify the spatial domain in the ImageCRS of that image, 
     * whether or not that image is georeferenced. 
     * Additional bounding boxes, if any, shall specify the spatial domain in other CRSs.
     * One bounding box could simply duplicate the information in the ows:WGS84BoundingBox;
     * but the intent is to describe the spatial domain in more detail (e.g., in several different CRSs, or several rectangular areas instead of one overall bounding box).
     * Multiple bounding boxes with the same CRS shall be interpreted as an unordered list of bounding boxes whose union covers spatial domain of this coverage.
     * Notice that WCS use of this BoundingBox is further specified in specification Subclause 7.5.
     * 
     */
    public List<JAXBElement<? extends BoundingBoxType>> getBoundingBox() {
        return Collections.unmodifiableList(boundingBox);
    }

    /**
     * Definition of GridCRS of the stored coverage. 
     * This GridCRS shall be included when this coverage is georectified and is thus stored in a GridCRS.
     * This GridCRS applies to this offered coverage, and specifies its spatial resolution.
     * The definition is included to inform clients of this GridCRS,
     * for possible use in a GetCoverage operation request. 
     */
    public GridCrsType getGridCRS() {
        return gridCRS;
    }

    /**
     * Gets the value of the transformation property.
     */
    public AbstractCoordinateOperationType getTransformation() {
        return transformation;
    }

    /**
     * Gets the value of the imageCRS property.
     * 
     */
    public ImageCRSRefType getImageCRS() {
        return imageCRS;
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
