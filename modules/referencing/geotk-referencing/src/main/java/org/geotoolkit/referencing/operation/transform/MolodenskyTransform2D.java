/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.referencing.operation.transform;

import org.opengis.referencing.operation.MathTransform2D;


/**
 * A Molodensky transforms in 2D. This implementation is identical to
 * {@link MolodenksiTransform} except that it implements {@link MathTransform2D}.
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
final class MolodenskyTransform2D extends MolodenskyTransform implements MathTransform2D {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 8098439371246167474L;

    /**
     * Constructs a 2D transform using Molodensky formulas.
     *
     * @param abridged {@code true} for the abridged formula, or {@code false} for the complete one.
     * @param a        The source semi-major axis length in meters.
     * @param b        The source semi-minor axis length in meters.
     * @param ta       The target semi-major axis length in meters.
     * @param tb       The target semi-minor axis length in meters.
     * @param dx       The <var>x</var> translation in meters.
     * @param dy       The <var>y</var> translation in meters.
     * @param dz       The <var>z</var> translation in meters.
     */
    protected MolodenskyTransform2D(final boolean abridged,
            final double  a, final double  b,
            final double ta, final double tb,
            final double dx, final double dy, final double  dz)
    {
        super(abridged, a, b, false, ta, tb, false, dx, dy, dz);
    }

    /**
     * Creates the inverse of the given Molodenski transform.
     *
     * @param direct The transform for which to create the inverse transform.
     */
    private MolodenskyTransform2D(final MolodenskyTransform2D direct) {
        super(direct);
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public MathTransform2D inverse() {
        if (inverse == null) {
            inverse = new MolodenskyTransform2D(this);
        }
        return (MathTransform2D) inverse;
    }
}
