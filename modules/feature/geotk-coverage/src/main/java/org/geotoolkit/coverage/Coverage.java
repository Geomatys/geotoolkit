/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.coverage;

import java.util.List;

/**
 * Temporary class while moving classes to Apache SIS.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Coverage extends org.opengis.coverage.Coverage {

    /**
     * Returns information about the <cite>range</cite> of this grid coverage.
     * Information include names, sample value ranges, fill values and transfer functions for all bands in this grid coverage.
     *
     * @return names, value ranges, fill values and transfer functions for all bands in this grid coverage.
     */
    List<? extends SampleDimension> getSampleDimensions();

    /**
     * Deprecated, will be removed from GeoAPI.
     */
    @Deprecated
    @Override
    public default int getNumSampleDimensions() {
        throw new UnsupportedOperationException("Deprecated");
    }

    /**
     * Deprecated, will be removed from GeoAPI.
     */
    @Deprecated
    @Override
    public default org.opengis.coverage.SampleDimension getSampleDimension(int index) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Deprecated");
    }

}
