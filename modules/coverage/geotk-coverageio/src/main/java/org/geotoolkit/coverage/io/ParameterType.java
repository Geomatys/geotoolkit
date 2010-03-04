/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.coverage.io;


/**
 * An enumeration of the parameters that can be set in {@link GridCoverageReadParam}.
 * This enumeration is used for specifying whatever a parameter shall be used strictly
 * as defined, or if the {@link GridCoverageReader} is allowed to use a more efficient
 * value.
 * <p>
 * For example if the envelope is not strict, then the reader may use the intersection
 * of the coverage envelope (as available in the store) with the requested envelope. If
 * the resolution is not strict, then the reader may use a finer resolution if it avoid
 * the need for a resampling operation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @see GridCoverageReadParam#strictParameters
 *
 * @since 3.09
 * @module
 *
 * @deprecated The choice to perform resampling or not is left to caller. Doing resampling
 *             in {@link GridCoverageReader} would be arbitrary (what to do if the envelope
 *             is offseted by only half a pixe?).
 */
@Deprecated
public enum ParameterType {
    /**
     * The {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate
     * Reference System} parameter.
     *
     * {@section Strict and non-strict modes behavior}
     * <ul>
     *   <li>In strict mode, grid coverages read from the stream will be resampled to that CRS.</li>
     *   <li>In non-strict mode, grid coverages read from the stream will be returned in the given
     *       CRS only if that operation can be performed cheaply, or in their native CRS otherwise.</li>
     * </ul>
     *
     * @see GridCoverageReadParam#getCoordinateReferenceSystem()
     */
    CRS,

    /**
     * The {@linkplain org.opengis.geometry.Envelope Envelope} parameter.
     *
     * {@section Strict and non-strict modes behavior}
     * <ul>
     *   <li>In strict mode, grid coverages read from the stream will be resampled to fit
     *       the envelope.</li>
     *   <li>In non-strict mode, readers will use the intersection of the coverage envelope
     *       (as available in the store) with the requested envelope.</li>
     * </ul>
     *
     * @see GridCoverageReadParam#getEnvelope()
     */
    ENVELOPE,

    /**
     * The resolution parameter.
     *
     * {@section Strict and non-strict modes behavior}
     * <ul>
     *   <li>In strict mode, grid coverages read from the stream will be resampled to that resolution.</li>
     *   <li>In non-strict mode, grid coverages read from the stream may be returned in a finer
     *       resolution if it avoid the need for resampling.</li>
     * </ul>
     *
     * @see GridCoverageReadParam#getResolution()
     */
    RESOLUTION
}
