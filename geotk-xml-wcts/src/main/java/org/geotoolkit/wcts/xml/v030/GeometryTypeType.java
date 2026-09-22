/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wcts.xml.v030;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Type of GML 3 geometric primitive possibly handled by an OWS server, as encoded by
 * {@code owsAdditions.xsd/GeometryTypeType} (private WCTS extension namespace).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlType(name = "GeometryTypeType", namespace = "http://www.opengeospatial.net/ows")
@XmlEnum
public enum GeometryTypeType {
    Envelope, Point, LineString, Polygon, LinearRing, Curve, LineStringSegment,
    ArcString, Arc, Circle, ArcStringByBulge, ArcByBulge, ArcByCenterPoint,
    CircleByCenterPoint, CubicSpline, BSpline, Bezier, OrientableCurve, Surface,
    PolygonPatch, Triangle, Rectangle, Ring, OrientableSurface, Solid,
    CompositeCurve, CompositeSurface, CompositeSolid
}
