/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.crs;

import java.util.Map;
import java.util.Collections;
import javax.xml.bind.annotation.XmlTransient;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.AffineCS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.datum.ImageDatum;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.crs.ImageCRS;

import org.apache.sis.referencing.AbstractReferenceSystem;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.datum.DefaultImageDatum;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.referencing.crs.AbstractCRS.name;


/**
 * An engineering coordinate reference system applied to locations in images. Image coordinate
 * reference systems are treated as a separate sub-type because a separate user community exists
 * for images with its own terms of reference.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CS type(s)</TH></TR>
 * <TR><TD>
 *   {@link CartesianCS Cartesian},
 *   {@link AffineCS    Affine}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@Immutable
@XmlTransient
public class DefaultImageCRS extends org.apache.sis.referencing.crs.DefaultImageCRS {
    /**
     * A two-dimensional Cartesian coordinate reference system with
     * {@linkplain org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis#COLUMN column},
     * {@linkplain org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis#ROW row} axes.
     * By default, this CRS has no transformation path to any other CRS (i.e. a map using this
     * CS can't be reprojected to a {@linkplain DefaultGeographicCRS geographic coordinate
     * reference system} for example).
     * <p>
     * The {@link PixelInCell} attribute of the associated {@link ImageDatum}
     * is set to {@link PixelInCell#CELL_CENTER CELL_CENTER}.
     *
     * @since 3.09
     */
    public static final DefaultImageCRS GRID_2D;
    static {
        final Map<String,?> properties = name(Vocabulary.Keys.GRID);
        GRID_2D = new DefaultImageCRS(properties, new DefaultImageDatum(properties,
                PixelInCell.CELL_CENTER), DefaultCartesianCS.GRID);
    }

    /**
     * Constructs a new image CRS with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param crs The coordinate reference system to copy.
     *
     * @since 2.2
     */
    public DefaultImageCRS(final ImageCRS crs) {
        super(crs);
    }

    /**
     * Constructs an image CRS from a name.
     *
     * @param name The name.
     * @param datum The datum.
     * @param cs The coordinate system.
     */
    public DefaultImageCRS(final String     name,
                           final ImageDatum datum,
                           final AffineCS   cs)
    {
        this(Collections.singletonMap(NAME_KEY, name), datum, cs);
    }

    /**
     * Constructs an image CRS from a set of properties. The properties are given unchanged to
     * the {@linkplain AbstractReferenceSystem#AbstractReferenceSystem(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param datum The datum.
     * @param cs The coordinate system.
     */
    public DefaultImageCRS(final Map<String,?> properties,
                           final ImageDatum    datum,
                           final AffineCS      cs)
    {
        super(properties, datum, cs);
    }
}
