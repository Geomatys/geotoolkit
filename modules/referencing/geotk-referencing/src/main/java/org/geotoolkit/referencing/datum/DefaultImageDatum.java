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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.datum;

import java.util.Map;
import java.util.Objects;
import java.util.Collections;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.datum.ImageDatum;
import org.opengis.referencing.datum.PixelInCell;

import org.geotoolkit.io.wkt.Formatter;
import org.geotoolkit.util.ComparisonMode;

import static org.geotoolkit.util.Utilities.hash;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Defines the origin of an image coordinate reference system. An image datum is used in a local
 * context only. For an image datum, the anchor point is usually either the centre of the image
 * or the corner of the image.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.0
 * @module
 */
@Immutable
@XmlType(name = "ImageDatumType")
@XmlRootElement(name = "ImageDatum")
public class DefaultImageDatum extends AbstractDatum implements ImageDatum {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4304193511244150936L;

    /**
     * Specification of the way the image grid is associated with the image data attributes.
     */
    private final PixelInCell pixelInCell;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultImageDatum() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new datum with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param datum The datum to copy.
     *
     * @since 2.2
     */
    public DefaultImageDatum(final ImageDatum datum) {
        super(datum);
        pixelInCell = datum.getPixelInCell();
    }

    /**
     * Constructs an image datum from a name.
     *
     * @param name The datum name.
     * @param pixelInCell the way the image grid is associated with the image data attributes.
     */
    public DefaultImageDatum(final String name, final PixelInCell pixelInCell) {
        this(Collections.singletonMap(NAME_KEY, name), pixelInCell);
    }

    /**
     * Constructs an image datum from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractDatum#AbstractDatum(Map) super-class constructor}.
     *
     * @param properties  Set of properties. Should contains at least {@code "name"}.
     * @param pixelInCell the way the image grid is associated with the image data attributes.
     */
    public DefaultImageDatum(final Map<String,?> properties, final PixelInCell pixelInCell) {
        super(properties);
        this.pixelInCell = pixelInCell;
        ensureNonNull("pixelInCell", pixelInCell);
    }

    /**
     * Returns a Geotk datum implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultImageDatum castOrCopy(final ImageDatum object) {
        return (object == null) || (object instanceof DefaultImageDatum)
                ? (DefaultImageDatum) object : new DefaultImageDatum(object);
    }

    /**
     * Specification of the way the image grid is associated with the image data attributes.
     *
     * @return The way image grid is associated with image data attributes.
     */
    @Override
    public PixelInCell getPixelInCell() {
        return pixelInCell;
    }

    /**
     * Compare this datum with the specified object for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true; // Slight optimization.
        }
        if (super.equals(object, mode)) {
            switch (mode) {
                case STRICT: {
                    final DefaultImageDatum that = (DefaultImageDatum) object;
                    return Objects.equals(this.pixelInCell, that.pixelInCell);
                }
                default: {
                    final ImageDatum that = (ImageDatum) object;
                    return Objects.equals(getPixelInCell(), that.getPixelInCell());
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(pixelInCell, super.computeHashCode());
    }

    /**
     * Format the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite> (WKT)</A> element.
     *
     * {@note WKT of image datum is not yet part of OGC specification.}
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name.
     */
    @Override
    public String formatWKT(final Formatter formatter) {
        super.formatWKT(formatter);
        formatter.append(pixelInCell);
        formatter.setInvalidWKT(ImageDatum.class);
        return "IMAGE_DATUM";
    }
}
