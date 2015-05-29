/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.referencing.cs;

import java.util.List;
import java.util.Arrays;

import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.lang.Decorator;
import org.geotoolkit.resources.Errors;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.referencing.operation.transform.MathTransforms;


/**
 * An implementation of {@link CompoundCRS} delegating every method calls to the wrapped CRS,
 * except the coordinate system.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.15
 * @module
 */
@Decorator(CompoundCRS.class)
final class DiscreteCompoundCRS extends DiscreteCRS<CompoundCRS> implements CompoundCRS {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4292250243969805179L;

    /**
     * The CRS components.
     */
    private final List<CoordinateReferenceSystem> components;

    /**
     * Creates a new compound CRS wrapping the given CRS with the given components.
     */
    private DiscreteCompoundCRS(final CompoundCRS crs, final DiscreteCS cs,
            final CoordinateReferenceSystem[] components)
    {
        super(crs, cs);
        this.components = UnmodifiableArrayList.wrap(components);
    }

    /**
     * Returns the components CRS as an unmodifiable list.
     */
    @Override
    public List<CoordinateReferenceSystem> getComponents() {
        return components;
    }

    /**
     * Returns a CRS instance wrapping the given CRS with the given ordinate values for each axis.
     *
     * @param  crs  The coordinate reference system to wrap.
     * @param  ordinates The ordinate values for each axis. The arrays are <strong>not</strong> cloned.
     * @return A new coordinate reference system wrapping the given one with discrete axes.
     * @throws IllegalArgumentException If the length of the {@code ordinates} array is not equals
     *         to the coordinate system {@linkplain CoordinateSystem#getDimension() dimension}.
     */
    static CompoundCRS create(final CompoundCRS crs, final double[]... ordinates) {
        /*
         * Get the CRS components. For each components, there is a choice:
         *
         *  1) If the component is not discrete, replace it by a new discrete component CRS.
         *
         *  2) Otherwise, make sure that the axes in the component CRS are equal to the axis
         *     in the CompoundCRS as a whole. This consistency check is required for NetcdfCRS,
         *     which may have temporarily an inconsistent CRS.
         */
        final List<CoordinateReferenceSystem> source = crs.getComponents();
        final CoordinateReferenceSystem[] components = new CoordinateReferenceSystem[source.size()];
        final CoordinateSystem cs = crs.getCoordinateSystem();
        boolean changed = false;
        int lower = 0;
        for (int i=0; i<components.length; i++) {
            final CoordinateReferenceSystem component = source.get(i);
            final int upper = lower + component.getCoordinateSystem().getDimension();
            components[i] = DiscreteReferencingFactory.createDiscreteCRS(
                    component, Arrays.copyOfRange(ordinates, lower, upper));
            if (!changed) {
                if (components[i] != component) {
                    // A non-discrete CRS has been replaced by a discrete CRS (case 1 above).
                    changed = true;
                } else {
                    // Ensure that the axes are consistent (case 2 above).
                    final CoordinateSystem ccs = components[i].getCoordinateSystem();
                    final int dimension = ccs.getDimension();
                    for (int j=0; j<dimension; j++) {
                        if (!ccs.getAxis(j).equals(cs.getAxis(lower + j))) {
                            changed = true;
                            break;
                        }
                    }
                }
            }
            lower = upper;
        }
        if (!changed) {
            // Current instance already have the given ordinate values.
            return crs;
        }
        /*
         * At this point, we have a list of CRS components where each components have discrete
         * axis. Now get the list of those axes in order to build a new discrete CS. Note that
         * it would be a bug if an axis is not an instance of DiscreteCoordinateSystemAxis.
         */
        final DiscreteCoordinateSystemAxis<?>[] axes = new DiscreteCoordinateSystemAxis<?>[cs.getDimension()];
        int count = 0;
        for (int i=0; i<components.length; i++) {
            final CoordinateSystem component = components[i].getCoordinateSystem();
            final int dimension = component.getDimension();
            for (int j=0; j<dimension; j++) {
                if (count < axes.length) {
                    // Following cast should never fail.
                    axes[count] = (DiscreteCoordinateSystemAxis<?>) component.getAxis(j);
                }
                count++;
            }
        }
        if (count != axes.length) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_2, axes.length, count));
        }
        return new DiscreteCompoundCRS(crs, new DiscreteCS(cs, axes), components);
    }

    /**
     * Returns the transform from grid coordinates to CRS coordinates mapping pixel center.
     * This method delegates to each component, because some component may compute their own
     * transform in a different way than {@link DiscreteReferencingFactory#getAffineTransform}.
     * For example {@link org.geotoolkit.referencing.adapters.NetcdfCRS} returns {@code null}
     * if an axis is irregular.
     */
    @Override
    public synchronized MathTransform getGridToCRS() {
        if (gridToCRS == null) {
            gridToCRS = MathTransforms.linear(DiscreteReferencingFactory.getAffineTransform(this, cs.axes));
        }
        return gridToCRS;
    }
}
