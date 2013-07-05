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

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;
import org.opengis.metadata.quality.Result;
import org.opengis.metadata.quality.PositionalAccuracy;
import org.opengis.metadata.quality.GriddedDataPositionalAccuracy;
import org.opengis.metadata.quality.AbsoluteExternalPositionalAccuracy;
import org.opengis.metadata.quality.RelativeInternalPositionalAccuracy;

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
@ThreadSafe
@XmlType(name = "AbstractDQ_PositionalAccuracy_Type")
@XmlRootElement(name = "DQ_PositionalAccuracy")
@XmlSeeAlso({
    DefaultAbsoluteExternalPositionalAccuracy.class,
    DefaultGriddedDataPositionalAccuracy.class,
    DefaultRelativeInternalPositionalAccuracy.class
})
public class AbstractPositionalAccuracy extends AbstractElement implements PositionalAccuracy {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6043381860937480828L;

    /**
     * Indicates that a {@linkplain org.opengis.referencing.operation.Transformation transformation}
     * requires a datum shift and some method has been applied. Datum shift methods often use
     * {@linkplain org.geotoolkit.referencing.datum.BursaWolfParameters Bursa Wolf parameters},
     * but other kind of method may have been applied as well.
     *
     * @see org.opengis.referencing.operation.Transformation#getCoordinateOperationAccuracy
     * @see org.geotoolkit.referencing.operation.AbstractCoordinateOperationFactory#DATUM_SHIFT
     */
    public static final PositionalAccuracy DATUM_SHIFT_APPLIED;

    /**
     * Indicates that a {@linkplain org.opengis.referencing.operation.Transformation transformation}
     * requires a datum shift, but no method has been found applicable. This usually means that no
     * {@linkplain org.geotoolkit.referencing.datum.BursaWolfParameters Bursa Wolf parameters} have
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

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     * <p>
     * This method checks for the {@link GriddedDataPositionalAccuracy},
     * {@link AbsoluteExternalPositionalAccuracy} and {@link RelativeInternalPositionalAccuracy}
     * sub-interfaces. If one of those interfaces is found, then this method delegates to the
     * corresponding {@code castOrCopy} static method. If the given object implements more than one
     * of the above-cited interfaces, then the {@code castOrCopy} method to be used is unspecified.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static AbstractPositionalAccuracy castOrCopy(final PositionalAccuracy object) {
        if (object instanceof AbsoluteExternalPositionalAccuracy) {
            return DefaultAbsoluteExternalPositionalAccuracy.castOrCopy((AbsoluteExternalPositionalAccuracy) object);
        }
        if (object instanceof GriddedDataPositionalAccuracy) {
            return DefaultGriddedDataPositionalAccuracy.castOrCopy((GriddedDataPositionalAccuracy) object);
        }
        if (object instanceof RelativeInternalPositionalAccuracy) {
            return DefaultRelativeInternalPositionalAccuracy.castOrCopy((RelativeInternalPositionalAccuracy) object);
        }
        return (object == null) || (object instanceof AbstractPositionalAccuracy)
                ? (AbstractPositionalAccuracy) object : new AbstractPositionalAccuracy(object);
    }
}
