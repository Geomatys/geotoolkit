/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.referencing.rs;

import java.util.Collection;
import java.util.Collections;
import org.opengis.metadata.quality.PositionalAccuracy;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;


/**
 * An operation on codes that transforms or converts code to another <abbr>RS</abbr>.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CodeOperation extends IdentifiedObject {

    /**
     * Key for the <code>{@value}</code> property.
     * This is used for setting the value to be returned by {@link #getCoordinateOperationAccuracy()}.
     *
     * @see #getCoordinateOperationAccuracy()
     */
    String COORDINATE_OPERATION_ACCURACY_KEY = "coordinateOperationAccuracy";

    /**
     * Returns the <abbr>RS</abbr> from which codes are changed.
     *
     * @return the <abbr>RS</abbr> from which codes are changed.
     */
    ReferenceSystem getSourceRS();

    /**
     * Returns the <abbr>RS</abbr> to which codes are changed.
     *
     * @return the <abbr>RS</abbr> to which codes are changed.
     *
     * @see #getTargetEpoch()
     */
    ReferenceSystem getTargetRS();

    /**
     * Returns estimate(s) of the impact of this operation on point accuracy.
     * It gives position error estimates for target coordinates of this coordinate operation,
     * assuming no errors in source coordinates.
     *
     * @return the position error estimates, or an empty collection if not available.
     */
    default Collection<PositionalAccuracy> getCoordinateOperationAccuracy() {
        return Collections.emptyList();
    }

    /**
     * Get or create inverse transform.
     *
     * @return inverse code transform
     * @throws NoninvertibleTransformException if reverse transformation is not possible
     */
    CodeOperation inverse() throws NoninvertibleTransformException;

    /**
     * Transform a single code.
     *
     * @param source to transform, never null
     * @param target can be null
     * @return target code if provided, a new one if null.
     * @throws TransformException
     */
    default Code transform(Code source, Code target) throws TransformException {
        final Code[] sarray = new Code[]{source};
        final Code[] tarray = new Code[]{target};
        transform(sarray, 0, tarray, 0, 1);
        return tarray[0];
    }

    /**
     * Transform multiple codes at once.
     *
     * @param source to read from
     * @param soffset source array offset
     * @param target to write into
     * @param toffset target array offset
     * @param nb number of codes to transform
     */
    void transform(Code[] source, int soffset, Code[] target, int toffset, int nb) throws TransformException;

}
