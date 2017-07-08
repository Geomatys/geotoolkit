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
package org.geotoolkit.map;

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.geotoolkit.storage.coverage.CoverageResource;

/**
 * An elevation model of elevation values.<br/>
 * It is possible to ask for a grid coverage or a single elevation.
 *
 * @author Johann Sorel (Geomatys)
 * @author Marechal remi (Geomatys)
 * @module
 */
public class ElevationModel {

    /**
     * {@link GridCoverageReader} which contain DEM altitude values.
     */
    private final GridCoverageReader coverage;

    /**
     * Coefficient (or factor) in per cent to controle shadow length spread
     * in function of maximum DEM amplitude value.
     */
    private final double scale;

    /**
     * Angle in degrees between Origin to North axis and light source.
     */
    private final double azimuth;

    /**
     * Angle in degrees between Digital Elevation Model ground and light source.
     */
    private final double altitude;

    /**
     * Define positive altitude value sens.<br/>
     * {@link AxisDirection} should be instance of following type: <br/>
     * {@link AxisDirection#DOWN} or {@link AxisDirection#UP}.
     */
    private final AxisDirection axisDirection;

    /**
     * Build {@link ElevationModel} object which contain some elevation information need to build shadow relief. <br/><br/>
     *
     * Note : The default value of {@linkplain #axisDirection} is {@link AxisDirection#UP}.
     *
     * @param ref {@link CoverageResource} where we can features DEM.
     * @param azimuth Light angle in degree from  {@link CoordinateReferenceSystem} North {@link CoordinateSystemAxis} from {@link GridCoverage}.
     * @param altitude Light angle in degree of the light from the ground.
     * @param scale Coefficient (or factor) in per cent to controle shadow length spread in function of maximum DEM amplitude value.
     */
    public ElevationModel(final CoverageResource ref, final double azimuth, final double altitude, final double scale) throws CoverageStoreException {
        this(ref, azimuth, altitude, scale, AxisDirection.UP);
    }

    /**
     * Build {@link ElevationModel} object which contain some elevation information need to build shadow relief.
     *
     * @param ref {@link CoverageResource} where we can features DEM.
     * @param azimuth Light angle in degree from  {@link CoordinateReferenceSystem} North {@link CoordinateSystemAxis} from {@link GridCoverage}.
     * @param altitude Light angle in degree of the light from the ground.
     * @param scale Coefficient (or factor) in per cent to controle shadow length spread in function of maximum DEM amplitude value.
     * @param axisDirection
     * @throws IllegalArgumentException if axis direction is not instance of {@link AxisDirection#DOWN} or {@link AxisDirection#UP}.
     */
    public ElevationModel(final CoverageResource ref, final double azimuth, final double altitude,
                          final double scale, final AxisDirection axisDirection) throws CoverageStoreException {
        ArgumentChecks.ensureNonNull("CoverageReference", ref);
        this.coverage = ref.acquireReader();
        this.azimuth  = azimuth;
        this.altitude = altitude;
        this.scale    = scale;
        if (!(axisDirection.equals(AxisDirection.UP) || axisDirection.equals(AxisDirection.DOWN))) {
            throw new IllegalArgumentException("setted altitude direction should be instance of "
                    + "AxisDirection.DOWN or AxisDirection.UP. setted altitude = "+axisDirection);
        }
        this.axisDirection = axisDirection;
    }

    /**
     * Return angle in degrees between Origin to North axis and light source.<br/>
     * Moreother angle is defined positive in clockwise.
     *
     * @return angle between Origin to North axis and light source.
     */
    public GridCoverageReader getCoverageReader() {
        return coverage;
    }

    /**
     * Return coefficient (or factor) in per cent to controle shadow length spread
     * in function of maximum DEM amplitude value.
     *
     * @return coefficient (or factor) in per cent to controle shadow length spread.
     */
    public double getAmplitudeScale() {
        return scale;
    }

    /**
     * Return angle in degrees between Origin to North axis and light source.<br/>
     * Moreother angle is defined positive in clockwise.
     *
     * @return angle between Origin to North axis and light source.
     */
    public double getAzimuth() {
        return azimuth;
    }

    /**
     * Return angle in degrees between Digital Elevation Model ground and light source.
     *
     * @return angle in degrees between Digital Elevation Model ground and light source.
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * Return altitude positive value sens.<br/>
     * {@link AxisDirection} is instance of following type: <br/>
     * {@link AxisDirection#DOWN} or {@link AxisDirection#UP}.
     *
     * @return {@link AxisDirection} which define altitude positive value sens.
     */
    public AxisDirection getAltitudeDirection() {
        return axisDirection;
    }
}
