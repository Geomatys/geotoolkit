/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

/**
 * {@linkplain org.geotoolkit.referencing.cs.AbstractCS Coordinate system} implementations.
 * An explanation for this package is provided in the {@linkplain org.opengis.referencing.cs OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the Geotk implementation.
 * <p>
 * Geotk provides some convenience methods for fetching specific coordinate values in standard
 * units. For example the {@link org.geotoolkit.referencing.cs.DefaultEllipsoidalCS} class provides a
 * {@link org.geotoolkit.referencing.cs.DefaultEllipsoidalCS#getLongitude getLongitude} method that
 * returns the longitude value in a given set of coordinates. This convenience method free the user
 * from the task of finding which axis is for the longitude, and performing unit conversion.
 *
 * <p>Some useful constants defined in this package are:</p>
 *
 * <blockquote><table>
 *   <tr><td nowrap>Spherical CS:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.cs.DefaultSphericalCS#GEOCENTRIC             GEOCENTRIC}
 *   </td></tr>
 *   <tr><td nowrap>Ellipsoidal CS:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.cs.DefaultEllipsoidalCS#GEODETIC_2D          GEODETIC_2D},
 *     {@link org.geotoolkit.referencing.cs.DefaultEllipsoidalCS#GEODETIC_3D          GEODETIC_3D}
 *   </td></tr>
 *   <tr><td nowrap>Cartesian CS:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.cs.DefaultCartesianCS#PROJECTED              PROJECTED},
 *     {@link org.geotoolkit.referencing.cs.DefaultCartesianCS#GEOCENTRIC             GEOCENTRIC},
 *     {@link org.geotoolkit.referencing.cs.DefaultCartesianCS#GENERIC_2D             GENERIC_2D},
 *     {@link org.geotoolkit.referencing.cs.DefaultCartesianCS#GENERIC_3D             GENERIC_3D},
 *     {@link org.geotoolkit.referencing.cs.DefaultCartesianCS#GRID                   GRID},
 *     {@link org.geotoolkit.referencing.cs.DefaultCartesianCS#DISPLAY                DISPLAY}
 *   </td></tr>
 *   <tr><td nowrap>Vertical CS:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.cs.DefaultVerticalCS#ELLIPSOIDAL_HEIGHT      ELLIPSOIDAL_HEIGHT},
 *     {@link org.geotoolkit.referencing.cs.DefaultVerticalCS#GRAVITY_RELATED_HEIGHT  GRAVITY_RELATED_HEIGHT},
 *     {@link org.geotoolkit.referencing.cs.DefaultVerticalCS#DEPTH                   DEPTH}
 *   </td></tr>
 *   <tr><td nowrap>Time CS:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.cs.DefaultTimeCS#DAYS                        DAYS},
 *     {@link org.geotoolkit.referencing.cs.DefaultTimeCS#SECONDS                     SECONDS},
 *     {@link org.geotoolkit.referencing.cs.DefaultTimeCS#MILLISECONDS                MILLISECONDS}
 *   </td></tr>
 * </table></blockquote>
 *
 * <p>Some worthy Geotk-specific methods defined in this package are:</p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.cs.DefaultEllipsoidalCS#getLongitude(double[])},
 *       {@link org.geotoolkit.referencing.cs.DefaultEllipsoidalCS#getLatitude(double[]) getLatitude(double[])} and
 *       {@link org.geotoolkit.referencing.cs.DefaultEllipsoidalCS#getHeight(double[]) getHeight(double[])}</li>
 *   <li>{@link org.geotoolkit.referencing.cs.AbstractCS#swapAndScaleAxis AbstractCS.swapAndScaleAxis(CoordinateSystem, CoordinateSystem)}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.0
 * @module
 */
@XmlSchema(elementFormDefault= XmlNsForm.QUALIFIED, namespace = Namespaces.GML, xmlns = {
    @XmlNs(prefix = "gmd", namespaceURI = Namespaces.GMD),
    @XmlNs(prefix = "xsi", namespaceURI = Namespaces.XSI),
    @XmlNs(prefix = "gml", namespaceURI = Namespaces.GML)
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(CS_AxisDirection.class),
    @XmlJavaTypeAdapter(CS_CoordinateSystemAxis.class),

    // Java types, primitive types and basic OGC types handling
    @XmlJavaTypeAdapter(UnitAdapter.class)
})
package org.geotoolkit.referencing.cs;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.apache.sis.xml.Namespaces;
import org.geotoolkit.internal.jaxb.gco.*;
import org.geotoolkit.internal.jaxb.referencing.*;
