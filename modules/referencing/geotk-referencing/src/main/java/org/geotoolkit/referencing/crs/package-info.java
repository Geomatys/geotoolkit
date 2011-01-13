/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
 * {@linkplain org.geotoolkit.referencing.crs.AbstractCRS Coordinate reference system} implementations.
 * An explanation for this package is provided in the {@linkplain org.opengis.referencing.crs OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the Geotk implementation.
 * <p>
 * {@link org.geotoolkit.referencing.crs.AbstractCRS} is the base class for all coordinate reference
 * systems (CRS). CRS can have an arbitrary number of dimensions. Some are two-dimensional (e.g.
 * {@link org.geotoolkit.referencing.crs.DefaultGeographicCRS GeographicCRS} and
 * {@link org.geotoolkit.referencing.crs.DefaultProjectedCRS ProjectedCRS}), while some others are
 * one-dimensional (e.g. {@link org.geotoolkit.referencing.crs.DefaultVerticalCRS VerticalCRS} and
 * {@link org.geotoolkit.referencing.crs.DefaultTemporalCRS TemporalCRS}). Those simple coordinate
 * reference systems can be used as building blocks for more complex coordinate reference systems.
 * For example, it is possible to construct a three-dimensional CRS with (<var>latitude</var>,
 * <var>longitude</var>, <var>time</var>) with an aggregation of
 * {@link org.geotoolkit.referencing.crs.DefaultGeographicCRS GeographicCRS} and
 * {@link org.geotoolkit.referencing.crs.DefaultTemporalCRS TemporalCRS}. Such aggregations
 * are built with {@link org.geotoolkit.referencing.crs.DefaultCompoundCRS CompoundCRS}.
 *
 * <p>Some useful constants defined in this package are:</p>
 *
 * <blockquote><table>
 *   <tr><td nowrap>Geographic CRS:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.crs.DefaultGeographicCRS#WGS84            WGS84},
 *     {@link org.geotoolkit.referencing.crs.DefaultGeographicCRS#WGS84_3D         WGS84_3D}
 *   </td></tr>
 *   <tr><td nowrap>Geocentric CRS:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.crs.DefaultGeocentricCRS#CARTESIAN        CARTESIAN},
 *     {@link org.geotoolkit.referencing.crs.DefaultGeocentricCRS#SPHERICAL        SPHERICAL}
 *   </td></tr>
 *   <tr><td nowrap>Engineering CRS:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.crs.DefaultEngineeringCRS#CARTESIAN_2D    CARTESIAN_2D},
 *     {@link org.geotoolkit.referencing.crs.DefaultEngineeringCRS#CARTESIAN_3D    CARTESIAN_3D}
 *   </td></tr>
 *   <tr><td nowrap>Vertical CRS:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.crs.DefaultVerticalCRS#ELLIPSOIDAL_HEIGHT ELLIPSOIDAL_HEIGHT},
 *     {@link org.geotoolkit.referencing.crs.DefaultVerticalCRS#GEOIDAL_HEIGHT     GEOIDAL_HEIGHT}
 *   </td></tr>
 *   <tr><td nowrap>Temporal CRS:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.crs.DefaultTemporalCRS#JULIAN             JULIAN},
 *     {@link org.geotoolkit.referencing.crs.DefaultTemporalCRS#MODIFIED_JULIAN    MODIFIED_JULIAN},
 *     {@link org.geotoolkit.referencing.crs.DefaultTemporalCRS#TRUNCATED_JULIAN   TRUNCATED_JULIAN},
 *     {@link org.geotoolkit.referencing.crs.DefaultTemporalCRS#DUBLIN_JULIAN      DUBLIN_JULIAN},
 *     {@link org.geotoolkit.referencing.crs.DefaultTemporalCRS#UNIX               UNIX},
 *     {@link org.geotoolkit.referencing.crs.DefaultTemporalCRS#JAVA               JAVA}
 *   </td></tr>
 * </table></blockquote>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.04
 *
 * @since 1.2
 * @module
 */
@XmlSchema(elementFormDefault= XmlNsForm.QUALIFIED, namespace = Namespaces.GML, xmlns = {
    @XmlNs(prefix = "gmd", namespaceURI = Namespaces.GMD),
    @XmlNs(prefix = "gco", namespaceURI = Namespaces.GCO),
    @XmlNs(prefix = "xsi", namespaceURI = Namespaces.XSI)
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(StringAdapter.class),
    @XmlJavaTypeAdapter(AffineCSAdapter.class),
    @XmlJavaTypeAdapter(CartesianCSAdapter.class),
    @XmlJavaTypeAdapter(EllipsoidalCSAdapter.class),
    @XmlJavaTypeAdapter(GeodeticDatumAdapter.class),
    @XmlJavaTypeAdapter(ImageDatumAdapter.class),
    @XmlJavaTypeAdapter(TemporalDatumAdapter.class),
    @XmlJavaTypeAdapter(TimeCSAdapter.class),
    @XmlJavaTypeAdapter(VerticalCSAdapter.class),
    @XmlJavaTypeAdapter(VerticalDatumAdapter.class)
})
package org.geotoolkit.referencing.crs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.text.StringAdapter;
import org.geotoolkit.internal.jaxb.referencing.cs.AffineCSAdapter;
import org.geotoolkit.internal.jaxb.referencing.cs.CartesianCSAdapter;
import org.geotoolkit.internal.jaxb.referencing.cs.EllipsoidalCSAdapter;
import org.geotoolkit.internal.jaxb.referencing.cs.TimeCSAdapter;
import org.geotoolkit.internal.jaxb.referencing.cs.VerticalCSAdapter;
import org.geotoolkit.internal.jaxb.referencing.datum.GeodeticDatumAdapter;
import org.geotoolkit.internal.jaxb.referencing.datum.ImageDatumAdapter;
import org.geotoolkit.internal.jaxb.referencing.datum.TemporalDatumAdapter;
import org.geotoolkit.internal.jaxb.referencing.datum.VerticalDatumAdapter;
