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
package org.geotoolkit.citygml.xml.v100.building;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AssociationType;


/**
 * Denotes the relation of an _AbstractBuilding to its bounding thematic surfaces (walls, roofs, ..).
 * The BoundarySurfacePropertyType element must either carry a reference to a _BoundarySurface object or contain a
 *  _BoundarySurface object inline, but neither both nor none. There is no differentiation between interior surfaces
 * bounding rooms and outer ones bounding buildings (one reason is, that ClosureSurfaces belong to both types). It
 * has to be made sure by additional integrity constraints that, e.g. an _AbstractBuilding is not related to
 * CeilingSurfaces or a room not to RoofSurfaces.
 *
 * <p>Java class for BoundarySurfacePropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BoundarySurfacePropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.opengis.net/gml}AssociationType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/citygml/building/1.0}_BoundarySurface"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoundarySurfacePropertyType")
public class BoundarySurfacePropertyType extends AssociationType {


}
