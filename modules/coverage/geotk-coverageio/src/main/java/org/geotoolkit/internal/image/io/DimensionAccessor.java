/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal.image.io;

import javax.imageio.metadata.IIOMetadata;

import org.opengis.metadata.content.TransferFunctionType;

import org.geotoolkit.util.NumberRange;
import org.geotoolkit.image.io.metadata.MetadataAccessor;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.FORMAT_NAME;


/**
 * A convenience specialization of {@link MetadataAccessor} for the
 * {@code "ImageDescription/Dimensions"} node. Example:
 *
 * {@preformat java
 *     SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
 *     DimensionAccessor accessor = new DimensionAccessor(metadata);
 *     accessor.selectChild(accessor.appendChild());
 *     accessor.setValueRange(-100, 2000);
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.06
 * @module
 */
public final class DimensionAccessor extends MetadataAccessor {
    /**
     * Creates a new accessor for the given metadata.
     *
     * @param metadata The Image I/O metadata. An instance of the {@link SpatialMetadata}
     *                 sub-class is recommanded, but not mandatory.
     */
    public DimensionAccessor(final IIOMetadata metadata) {
        super(metadata, FORMAT_NAME, "ImageDescription/Dimensions", "Dimension");
    }

    /**
     * Sets the {@code "units"} attribute to the given value.
     *
     * @param units The units, or {@code null} if none.
     */
    public void setUnits(final String units) {
        setAttribute("units", units);
    }

    /**
     * Sets the {@code "minValue"} and {@code "maxValue"} attributes to the given range.
     * They are the geophysical value, already transformed by the transfert function if
     * there is one.
     *
     * @param minimum The value to be assigned to the {@code "minValue"} attribute.
     * @param maximum The value to be assigned to the {@code "maxValue"} attribute.
     */
    public void setValueRange(final float minimum, final float maximum) {
        setAttribute("minValue", minimum);
        setAttribute("maxValue", maximum);
    }

    /**
     * Sets the {@code "minValue"} and {@code "maxValue"} attributes to the given range.
     * They are the geophysical value, already transformed by the transfert function if
     * there is one.
     *
     * @param minimum The value to be assigned to the {@code "minValue"} attribute.
     * @param maximum The value to be assigned to the {@code "maxValue"} attribute.
     */
    public void setValueRange(final double minimum, final double maximum) {
        setAttribute("minValue", minimum);
        setAttribute("maxValue", maximum);
    }

    /**
     * Sets the {@code "validSampleValues"} attribute to the given range. This is the range of
     * values encoded in the file, before the transformation by the transfert function if there
     * is one.
     *
     * @param range The value to be assigned to the {@code "validSampleValues"} attribute.
     */
    public void setValidSampleValue(final NumberRange<?> range) {
        setAttribute("validSampleValues", range);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given value.
     *
     * @param value The value to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final int value) {
        setAttribute("fillSampleValues", value);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given array.
     *
     * @param values The values to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final int... values) {
        setAttribute("fillSampleValues", values);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given value.
     *
     * @param value The value to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final float value) {
        setAttribute("fillSampleValues", value);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given array.
     *
     * @param values The values to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final float... values) {
        setAttribute("fillSampleValues", values);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given value.
     *
     * @param value The value to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final double value) {
        setAttribute("fillSampleValues", value);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given array.
     *
     * @param values The values to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final double... values) {
        setAttribute("fillSampleValues", values);
    }

    /**
     * Sets the {@code "scaleFactor"}, {@code "offset"} and {@code "transferFunctionType"}
     * attributes to the given values.
     *
     * @param scale  The value to be assigned to the {@code "scaleFactor"} attribute.
     * @param offset The value to be assigned to the {@code "offset"} attribute.
     * @param type   The value to be assigned to the {@code "transferFunctionType"} attribute.
     */
    public void setTransfertFunction(final double scale, final double offset, final TransferFunctionType type) {
        setAttribute("scaleFactor", scale);
        setAttribute("offset", offset);
        setAttribute("transferFunctionType", type);
    }
}
