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
package org.geotoolkit.metadata.iso.quality;

import javax.xml.bind.annotation.XmlTransient;
import org.opengis.util.InternationalString;
import org.opengis.metadata.quality.Result;
import org.opengis.metadata.quality.PositionalAccuracy;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Descriptions;


/**
 * Accuracy of the position of features.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@XmlTransient
public class AbstractPositionalAccuracy extends org.apache.sis.metadata.iso.quality.AbstractPositionalAccuracy {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6043381860937480828L;

    /**
     * Indicates that a {@linkplain org.opengis.referencing.operation.Transformation transformation}
     * requires a datum shift and some method has been applied. Datum shift methods often use
     * {@linkplain org.apache.sis.referencing.datum.BursaWolfParameters Bursa Wolf parameters},
     * but other kind of method may have been applied as well.
     *
     * @see org.opengis.referencing.operation.Transformation#getCoordinateOperationAccuracy
     * @see org.geotoolkit.referencing.operation.AbstractCoordinateOperationFactory#DATUM_SHIFT
     */
    public static final PositionalAccuracy DATUM_SHIFT_APPLIED;

    /**
     * Indicates that a {@linkplain org.opengis.referencing.operation.Transformation transformation}
     * requires a datum shift, but no method has been found applicable. This usually means that no
     * {@linkplain org.apache.sis.referencing.datum.BursaWolfParameters Bursa Wolf parameters} have
     * been found. Such datum shifts are approximative and may have 1 kilometer error. This
     * pseudo-transformation is allowed by
     * {@linkplain org.geotoolkit.referencing.operation.DefaultCoordinateOperationFactory coordinate
     * operation factory} only if it was created with
     * {@link org.geotoolkit.factory.Hints#LENIENT_DATUM_SHIFT} set to {@link Boolean#TRUE}.
     *
     * @see org.opengis.referencing.operation.Transformation#getCoordinateOperationAccuracy
     * @see org.geotoolkit.referencing.operation.AbstractCoordinateOperationFactory#ELLIPSOID_SHIFT
     */
    public static final PositionalAccuracy DATUM_SHIFT_OMITTED;
    static {
        final InternationalString desc = Vocabulary  .formatInternational(Vocabulary  .Keys.TRANSFORMATION_ACCURACY);
        final InternationalString eval = Descriptions.formatInternational(Descriptions.Keys.CONFORMANCE_MEANS_DATUM_SHIFT);
        DATUM_SHIFT_APPLIED = new PositionalAccuracyConstant(desc, eval, true);
        DATUM_SHIFT_OMITTED = new PositionalAccuracyConstant(desc, eval, false);
    }

    /**
     * Constructs an initially empty positional accuracy.
     */
    public AbstractPositionalAccuracy() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public AbstractPositionalAccuracy(final PositionalAccuracy source) {
        super(source);
    }

    /**
     * Creates an positional accuracy initialized to the given result.
     *
     * @param result The value obtained from applying a data quality measure against a specified
     *               acceptable conformance quality level.
     */
    public AbstractPositionalAccuracy(final Result result) {
        super(result);
    }
}
