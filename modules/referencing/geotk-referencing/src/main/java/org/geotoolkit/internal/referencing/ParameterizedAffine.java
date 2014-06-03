/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.referencing;

import java.util.Objects;
import java.awt.geom.AffineTransform;
import org.opengis.referencing.operation.MathTransform;
import org.apache.sis.parameter.Parameterized;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;


/**
 * An affine transform that remember the parameters given to the math transform constructor.
 * This is useful only to {@link org.geotoolkit.referencing.operation.projection.Equirectangular},
 * which is the only map projection that may be simplified to an affine transform.
 * <p>
 * Note that this class does not override {@link #getParameterValues()} and
 * {@link #getParameterDescriptors()}. Those methods shall continue to return affine transform
 * parameters, because the Equirectangular projection may have been concatenated with other
 * affine transform for axis swapping, <i>etc</i>. However subclasses may override those
 * methods if they known that no such concatenation have been applied.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public class ParameterizedAffine extends AffineTransform2D {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 906346920928432466L;

    /**
     * The parameters used for creating the projection.
     */
    public final Parameterized parameters;

    /**
     * Creates a new transform from the given affine and parameters.
     *
     * @param transform The affine transform to copy.
     * @param parameters The parameters to remember.
     */
    public ParameterizedAffine(final AffineTransform transform, final Parameterized parameters) {
        super(transform);
        this.parameters = parameters;
    }

    // Do not override getParameterValues() and getParameterDescriptors().
    // Those methods shall continue to return affine transform parameters.
    // See class javadoc for details.

    /**
     * Creates a new parameterized affine transform using the given transform and the same
     * parameters than this object. If the given transform is not affine, then it is returned
     * unchanged.
     *
     * @param  transform The transform to wrap, if possible.
     * @return A copy of the given affine transform associated to the parameter of this object,
     *         or the given transform unchanged if it was not affine.
     */
    public final MathTransform using(final MathTransform transform) {
        if (transform instanceof AffineTransform) {
            return new ParameterizedAffine((AffineTransform) transform, parameters);
        } else {
            return transform;
        }
    }

    /**
     * Compares this affine transform with the given object for equality. Parameters are
     * compared only if the other object is also an instance of {@code ParameterizedAffine},
     * in order to preserve {@link AffineTransform#equals} contract.
     *
     * @param object The object to compare with this transform for equality.
     */
    @Override
    public final boolean equals(final Object object) {
        if (super.equals(object)) {
            if (object instanceof ParameterizedAffine) {
                final ParameterizedAffine that = (ParameterizedAffine) object;
                return Objects.equals(this.parameters, that.parameters);
            }
            return true;
        }
        return false;
    }
}
