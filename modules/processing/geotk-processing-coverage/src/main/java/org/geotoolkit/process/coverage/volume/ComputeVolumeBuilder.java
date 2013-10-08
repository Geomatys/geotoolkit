/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.process.coverage.volume;

//final GridCoverageReader gcReader       = value(IN_GRIDCOVERAGE_READER , inputParameters);

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

//        final Geometry jtsGeom                  = value(IN_JTSGEOMETRY         , inputParameters);
//        final CoordinateReferenceSystem geomCRS = value(IN_GEOMETRY_CRS        , inputParameters);
//        final int bandIndex                     = value(IN_INDEX_BAND          , inputParameters);
//        final double zCeiling                   = value(IN_ALTITUDE_CEILING    , inputParameters);


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
    
    private GridCoverageReader gcReader;
    private Geometry jtsGeom;
    private double groundAltitude = 0;
    private double zCeiling;
    private CoordinateReferenceSystem geomCRS = null;
    private int bandIndex = 0;

    public ComputeVolumeBuilder(GridCoverageReader gcReader, Geometry jtsGeom, double zCeiling) {
        ArgumentChecks.ensureNonNull("geometry", jtsGeom);
        ArgumentChecks.ensureNonNull("GridcoverageReader", gcReader);
        this.gcReader = gcReader;
        this.jtsGeom  = jtsGeom;
        this.zCeiling = zCeiling;
    }
    
    public void setAnotherReader(final GridCoverageReader reader) {
        ArgumentChecks.ensureNonNull("GridcoverageReader", reader);
        this.gcReader = reader;
    }
    
    public void setAnotherArea(final Geometry geometry) {
        ArgumentChecks.ensureNonNull("geometry", geometry);
        this.jtsGeom = geometry;
    }
    
    public void setGeometryAltitude(final double geometryAltitude) {
        this.groundAltitude = geometryAltitude;
    }
    
    public void setAnotherCeiling(final double altitudeCeiling) {
        this.zCeiling = altitudeCeiling;
    }
    
    public void setGeometryCRS(final CoordinateReferenceSystem crs) {
        this.geomCRS = crs;
    }
    
    public void setStudyBandIndex(final int bandIndex) {
        this.bandIndex = bandIndex;
    }
    
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
