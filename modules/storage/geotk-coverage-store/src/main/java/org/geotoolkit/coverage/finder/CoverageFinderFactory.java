/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.finder;

/**
 *
 * @author rmarechal
 */
public final class CoverageFinderFactory {

    private CoverageFinderFactory() {
    }
    
    /**
     * Return a default {@link CoverageFinder} adapted for projects.
     * 
     * @return a default CoverageFinder adapted for projects.
     * @deprecated In attempt to replace this class by {@link strictlyCoverageFinder}.
     */
    @Deprecated
    public static CoverageFinder createDefaultCoverageFinder(){
        return new DefaultCoverageFinder();
    }
    
    /**
     * Return a default {@link CoverageFinder}.
     * 
     * @return a default {@link CoverageFinder}.
     */
    public static CoverageFinder createStrictlyCoverageFinder(){
        return new StrictlyCoverageFinder();
    }
}
