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
package org.geotoolkit.wcs.xml.v111;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v311modified.CodeType;


/**
 * This GridCRS is a simplification and specialization of a gml:DerivedCRS. All elements and attributes not required to define this GridCRS are optional. 
 * 
 * <p>Java class for GridCrsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GridCrsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}srsName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}GridBaseCRS"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}GridType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}GridOrigin" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}GridOffsets"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}GridCS" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/gml}id"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GridCrsType", propOrder = {
    "srsName",
    "gridBaseCRS",
    "gridType",
    "gridOrigin",
    "gridOffsets",
    "gridCS"
})
public class GridCrsType {

    @XmlElement(namespace = "http://www.opengis.net/gml")
    private CodeType srsName;
    @XmlElement(name = "GridBaseCRS", required = true)
    @XmlSchemaType(name = "anyURI")
    private String gridBaseCRS;
    @XmlElement(name = "GridType", defaultValue = "urn:ogc:def:method:WCS:1.1:2dSimpleGrid")
    @XmlSchemaType(name = "anyURI")
    private String gridType;
    @XmlList
    @XmlElement(name = "GridOrigin", type = Double.class, defaultValue = "0 0")
    private List<Double> gridOrigin = new ArrayList<Double>();
    @XmlList
    @XmlElement(name = "GridOffsets", type = Double.class)
    private List<Double> gridOffsets = new ArrayList<Double>();
    @XmlElement(name = "GridCS", defaultValue = "urn:ogc:def:cs:OGC:0.0:Grid2dSquareCS")
    @XmlSchemaType(name = "anyURI")
    private String gridCS;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    /**
     *  empty constructor used by JAXB
     */
    GridCrsType() {
    }
    
    /**
     * Build a new grid crs type
     */
    public GridCrsType(CodeType srsName, String gridBaseCRS, String gridType, List<Double> gridOrigin,
            List<Double> gridOffsets, String gridCS, String id) {
        this.gridBaseCRS = gridBaseCRS;
        this.gridCS      = gridCS;
        this.gridOffsets = gridOffsets;
        this.gridType    = gridType;
        this.id          = id;
        this.srsName     = srsName;
        this.gridOrigin  = gridOrigin;
    }
    
    /**
     * Gets the value of the srsName property.
     */
    public CodeType getSrsName() {
        return srsName;
    }

    /**
     * Gets the value of the gridBaseCRS property.
     * 
     */
    public String getGridBaseCRS() {
        return gridBaseCRS;
    }

   /**
     * When this GridType reference is omitted, the OperationMethod shall be the most commonly used method in a GridCRS, which is referenced by the default URN "urn:ogc:def:method:WCS:1.1:2dSimpleGrid". 
     */
    public String getGridType() {
        return gridType;
    }

    /**
     * When this GridOrigin position is omitted, the origin defaults be the most commonly used origin in a GridCRS used in the output part of a GetCapabilities operation request, namely "0 0". Gets the value of the gridOrigin property.
     */
    public List<Double> getGridOrigin() {
        if (gridOrigin == null) {
            gridOrigin = new ArrayList<Double>();
        }
       return Collections.unmodifiableList(gridOrigin);
    }

    /**
     * Gets the value of the gridOffsets property.
     */
    public List<Double> getGridOffsets() {
       if (gridOffsets == null) {
           gridOffsets = new ArrayList<Double>();
       }
       return Collections.unmodifiableList(gridOffsets);
    }

    /**
     * When this GridCS reference is omitted, the GridCS defaults to the most commonly used grid coordinate system, which is referenced by the URN "urn:ogc:def:cs:OGC:0.0:Grid2dSquareCS". 
     */
    public String getGridCS() {
        return gridCS;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public String getId() {
        return id;
    }
}
