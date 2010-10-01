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
package org.geotoolkit.referencing.cs;

import java.util.List;
import java.util.Arrays;

import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.CompoundCRS;

import org.geotoolkit.lang.Decorator;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

import static org.geotoolkit.referencing.cs.DiscreteReferencingFactory.*;


/**
 * An implementation of {@link CompoundCRS} delegating every method calls to the wrapped CRS,
 * except the coordinate system.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
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
    private DiscreteCompoundCRS(final CompoundCRS crs, final DiscreteCS cs, final CoordinateReferenceSystem[] components) {
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
         * Get the CRS components where each components have discrete axes.
         */
        final List<CoordinateReferenceSystem> source = crs.getComponents();
        final CoordinateReferenceSystem[] components = new CoordinateReferenceSystem[source.size()];
        boolean changed = false;
        int lower = 0;
        for (int i=0; i<components.length; i++) {
            final CoordinateReferenceSystem component = source.get(i);
            final int upper = lower + component.getCoordinateSystem().getDimension();
            changed |= (source != (components[i] = createDiscreteCRS(component,
                                Arrays.copyOfRange(ordinates, lower, upper))));
            lower = upper;
        }
        if (!changed) {
            // Current instance already have the given ordinate values.
            return crs;
        }
        /*
         * Get every axes in order to build a new discrete CS.
         */
        final CoordinateSystem cs = crs.getCoordinateSystem();
        final DiscreteCoordinateSystemAxis[] axes = new DiscreteCoordinateSystemAxis[cs.getDimension()];
        int count = 0;
        for (int i=0; i<components.length; i++) {
            final CoordinateSystem component = components[i].getCoordinateSystem();
            final int dimension = component.getDimension();
            for (int j=0; j<dimension; j++) {
                if (count < axes.length) {
                    axes[count] = createDiscreteAxis(component.getAxis(j), ordinates[count]);
                }
                count++;
            }
        }
        if (count != axes.length) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_$2, axes.length, count));
        }
        return new DiscreteCompoundCRS(crs, new DiscreteCS(cs, axes), components);
    }
}
