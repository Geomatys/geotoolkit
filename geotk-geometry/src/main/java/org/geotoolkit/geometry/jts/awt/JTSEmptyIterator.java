/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.geometry.jts.awt;

import org.locationtech.jts.geom.Geometry;

/**
 * An iterator for empty geometries
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 * @since 2.9
 */
public class JTSEmptyIterator extends JTSGeometryIterator<Geometry> {

    public static final JTSEmptyIterator INSTANCE = new JTSEmptyIterator();

    private JTSEmptyIterator() {
        super(null,null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getWindingRule() {
        return WIND_NON_ZERO;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isDone() {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void next() {
        throw new IllegalStateException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(final double[] coords) {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(final float[] coords) {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void reset() {
    }
}
