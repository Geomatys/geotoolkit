/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2014, Geomatys
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
package org.geotoolkit.processing.coverage.statistics;

import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.lang.Static;
import org.geotoolkit.storage.coverage.ImageStatistics;
import org.geotoolkit.process.ProcessException;

/**
 *
 * @author Remi Marechal (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class StatisticOp extends Static {

    public static final String MINIMUM = "min";
    public static final String MAXIMUM = "max";

    /**
     * Analyse image to return min and max value per bands. No-data are excluded.
     * @param reader
     * @param imageIndex
     * @return A Map with two Entry.
     * Each Entry have a name ("min", "max") and values are an double[] for each bands.
     * @throws CoverageStoreException
     * @deprecated use {@link Statistics#analyse(org.geotoolkit.coverage.io.GridCoverageReader, int, boolean)}
     */
    public static Map<String,Object> analyze(GridCoverageReader reader) throws CoverageStoreException {
        return analyze(reader, ViewType.GEOPHYSICS);
    }

    /**
     * Analyse image to return min and max value per bands. No-data are excluded.
     * @param reader
     * @param imageIndex
     * @return A Map with two Entry.
     * Each Entry have a name ("min", "max") and values are an double[] for each bands.
     * @throws CoverageStoreException
     * @deprecated use {@link Statistics#analyse(org.geotoolkit.coverage.io.GridCoverageReader, int, boolean)}
     */
    public static Map<String,Object> analyze(GridCoverageReader reader, ViewType viewType) throws CoverageStoreException {
        try {
            final ImageStatistics analyse = Statistics.analyse(reader, true);
            return toMap(analyse);
        } catch (ProcessException e) {
            throw new CoverageStoreException(e.getMessage(), e);
        }
    }

    /**
     * Analyse image to return min and max value per bands.
     * @param image
     * @return A Map with two Entry.
     * Each Entry have a name ("min", "max") and values are an double[] for each bands.
     * @deprecated use {@link Statistics#analyse(java.awt.image.RenderedImage, boolean)}
     */
    public static Map<String,Object> analyze(RenderedImage image) throws CoverageStoreException {
        try {
            final ImageStatistics analyse = Statistics.analyse(image, true);
            return toMap(analyse);
        } catch (ProcessException e) {
            throw new CoverageStoreException(e.getMessage(), e);
        }
    }

    /**
     * Convert an ImageStatistics object into StatisticOp output Map.
     * @param analyse
     * @return Map
     */
    private static Map<String, Object> toMap(ImageStatistics analyse) {
        final Map<String,Object> analysisMap = new HashMap<>();
        final ImageStatistics.Band[] bands = analyse.getBands();

        int nbBands = bands.length;
        double[] min = new double[nbBands];
        double[] max = new double[nbBands];
        for (int b = 0; b < nbBands; b++) {
            min[b] = bands[b].getMin();
            max[b] = bands[b].getMax();
        }

        analysisMap.put(MINIMUM, min);
        analysisMap.put(MAXIMUM, max);
        return analysisMap;
    }

}
