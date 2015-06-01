/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009 - 2012, Geomatys
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
package org.geotoolkit.processing.coverage.volume;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 * Helper class for compute volume from Digital Elevation Model (DEM) defined by {@link GridCoverageReader} and between 
 * area surface define by {@link Geometry} and maximal or minimal altitude define by a ceiling. 
 * </p>
 * <p>
 * The builder supports the following properties :<br/><br/>
 * <table border="1" cellspacing="0" cellpadding="1">
 *   <tr bgcolor="lightblue">
 *     <th>Properties</th>
 *     <th>Can be set from</th>
 *     <th>Default value</th>
 *     <th>Obligatory Parameter</th>
 *   </tr><tr>
 *     <td>&nbsp;{@link #gcReader}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setAnotherReader(org.geotoolkit.coverage.io.GridCoverageReader) GridCoverageReader instance} or
 *               by builder constructor &nbsp;</td>
 *     <td>&nbsp;&nbsp;</td>
 *      <td>&nbsp;{@code true}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #jtsGeom}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setAnotherArea(com.vividsolutions.jts.geom.Geometry) JTS Geometry instance} or
 *               by builder constructor &nbsp;</td>
 *     <td>&nbsp;&nbsp;</td>
 *     <td>&nbsp;{@code true}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #zCeiling}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setAnotherCeiling(double) Double altitude ceiling value} or
 *               by builder constructor&nbsp;</td>
 *     <td>&nbsp;&nbsp;</td>
 *     <td>&nbsp;{@code true}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #bandIndex}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setStudyBandIndex(int) Integer band index }&nbsp;</td>
 *     <td>&nbsp;{@code 0}</td>
 *     <td>&nbsp;{@code false}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #geomCRS}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setGeometryCRS(org.opengis.referencing.crs.CoordinateReferenceSystem)  CRS instance}&nbsp;</td>
 *     <td>&nbsp;{@code null}&nbsp;</td>
 *     <td>&nbsp;{@code false}&nbsp;</td>
 *   </tr>
 * </table>
 * </p>
 *
 * @author Remi Marechal (Geomatys).
 */
public class ComputeVolumeBuilder {
    
    /**
     * {@link GridCoverageReader} to get {@link GridCoverage} which contain Digital Elevation Model.
     */
    private GridCoverageReader gcReader;
    
    /**
     * {@link Geometry geometry} which represente area where volume is computed.
     */
    private Geometry jtsGeom;
    
    /**
     * {@link Geometry } Altitude.<br/>
     * Default value is 0.
     */
    private double groundAltitude = 0;
    
    /**
     * Maximal altitude value.<br/>
     * Volume is computed between ground formed by geometry at minimum ceiling value and its value.<br/>
     * Moreover {@link ComputeVolumeBuilder#groundAltitude geometry altitude} may be superior than this value to compute lock volume for example.
     */
    private double zCeiling;
    
    /**
     * In some case {@link Geometry} has no {@link CoordinateReferenceSystem} defined 
     * then we suppose {@link Geometry} is define in same CRS than {@link GridCoverage} 
     * from {@link ComputeVolumeBuilder#gcReader gridCoverageReader}.<br/>
     * Default value is {@code null}, means geometry and coverage (DEM) are in the same space.
     */
    private CoordinateReferenceSystem geomCRS = null;
    
    /**
     * Band index where we compute volume from {@link ComputeVolumeBuilder#gcReader GridCoverageReader}.<br/>
     * Default value is 0.
     */
    private int bandIndex = 0;

    /**
     * Create a builder to compute volume.
     * 
     * @param gcReader {@link GridCoverageReader} which permit to get DEM which contain elevation values to compute volume.
     * @param jtsGeom {@link Geometry} which represente area on DEM where compute volume.
     * @param zCeiling Maximal altitude value.
     * @see ComputeVolumeBuilder#gcReader.
     * @see ComputeVolumeBuilder#jtsGeom.
     * @see ComputeVolumeBuilder#zCeiling
     */
    public ComputeVolumeBuilder(final GridCoverageReader gcReader, final Geometry jtsGeom, final double zCeiling) {
        ArgumentChecks.ensureNonNull("geometry", jtsGeom);
        ArgumentChecks.ensureNonNull("GridcoverageReader", gcReader);
        this.gcReader = gcReader;
        this.jtsGeom  = jtsGeom;
        this.zCeiling = zCeiling;
    }
    
    /**
     * Set an another {@link GridCoverageReader} to avoid to create another {@link ComputeVolumeBuilder} object.
     * 
     * @param reader {@link GridCoverageReader} which permit to get DEM which contain elevation values to compute volume.
     * @see ComputeVolumeBuilder#gcReader.
     */
    public void setAnotherReader(final GridCoverageReader reader) {
        ArgumentChecks.ensureNonNull("GridcoverageReader", reader);
        this.gcReader = reader;
    }
    
    /**
     * Set an another {@link Geometry} to avoid to create another {@link ComputeVolumeBuilder} object.
     * 
     * @param geometry {@link Geometry} which represente area on DEM where compute volume.
     * @see ComputeVolumeBuilder#jtsGeom.
     */
    public void setAnotherArea(final Geometry geometry) {
        ArgumentChecks.ensureNonNull("geometry", geometry);
        this.jtsGeom = geometry;
    }
    
    /**
     * Set an another altitudeCeiling value to avoid to create another {@link ComputeVolumeBuilder} object.
     * 
     * @param altitudeCeiling Maximal altitude value.
     * @see ComputeVolumeBuilder#groundAltitude.
     * @see ComputeVolumeBuilder#zCeiling.
     */
    public void setAnotherCeiling(final double altitudeCeiling) {
        this.zCeiling = altitudeCeiling;
    }
    
    /**
     * Set geometryAltitude value.<br/>
     * Volume is computed between ground formed by geometry at this value and {@link ComputeVolumeBuilder#zCeiling Maximum altitude} value.
     * 
     * @param geometryAltitude Maximal altitude value.
     * @see ComputeVolumeBuilder#groundAltitude.
     * @see ComputeVolumeBuilder#zCeiling.
     */
    public void setGeometryAltitude(final double geometryAltitude) {
        this.groundAltitude = geometryAltitude;
    }
    
    /**
     * {@link Geometry} {@link CoordinateReferenceSystem}.
     * 
     * @param crs geometry space.
     * @see ComputeVolumeBuilder#geomCRS.
     */
    public void setGeometryCRS(final CoordinateReferenceSystem crs) {
        this.geomCRS = crs;
    }
    
    /**
     * DEM band index which will be study to compute volume.
     * 
     * @param bandIndex Digital Elevation Model band.
     * @see ComputeVolumeBuilder#bandIndex.
     */
    public void setStudyBandIndex(final int bandIndex) {
        this.bandIndex = bandIndex;
    }
    
    /**
     * Return volume from {@link ComputeVolumeProcess process} computed from all previously setted attributs. 
     * 
     * @return volume from {@link ComputeVolumeProcess process} computed from all previously setted attributs.
     * @throws ProcessException 
     */
    public double getVolume() throws ProcessException {
        final ProcessDescriptor volumeDescriptor = ComputeVolumeDescriptor.INSTANCE;
        final ParameterValueGroup volumeInput    = volumeDescriptor.getInputDescriptor().createValue();
        volumeInput.parameter(ComputeVolumeDescriptor.INPUT_READER_NAME).setValue(gcReader);
        volumeInput.parameter(ComputeVolumeDescriptor.INPUT_JTS_GEOMETRY_NAME).setValue(jtsGeom);
        volumeInput.parameter(ComputeVolumeDescriptor.INPUT_GEOMETRY_ALTITUDE_NAME).setValue(groundAltitude);
        volumeInput.parameter(ComputeVolumeDescriptor.INPUT_MAX_CEILING_NAME).setValue(zCeiling);
        volumeInput.parameter(ComputeVolumeDescriptor.INPUT_GEOMETRY_CRS_NAME).setValue(geomCRS);
        volumeInput.parameter(ComputeVolumeDescriptor.INPUT_BAND_INDEX_NAME).setValue(bandIndex);
        org.geotoolkit.process.Process volumeProcess = volumeDescriptor.createProcess(volumeInput);
        return ((Double) volumeProcess.call().parameter(ComputeVolumeDescriptor.OUTPUT_VOLUME_NAME).getValue());
    }
}
