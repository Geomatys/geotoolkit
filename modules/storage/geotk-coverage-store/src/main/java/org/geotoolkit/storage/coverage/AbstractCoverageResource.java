/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2015, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.opengis.util.GenericName;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.content.DefaultAttributeGroup;
import org.apache.sis.metadata.iso.content.DefaultCoverageDescription;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import org.geotoolkit.coverage.io.CoverageWriter;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.metadata.ImageStatistics;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.storage.AbstractFeatureSet;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlTransient
public abstract class AbstractCoverageResource extends AbstractFeatureSet implements GridCoverageResource {

    private static final int DEFAULT_SUBSET_SIZE = 256;

    protected final DataStore store;

    private DefaultCoverageDescription desc = null;

    /**
     *
     * @param store can be null
     * @param name never null
     */
    public AbstractCoverageResource(DataStore store, GenericName name) {
        super(name);
        this.store = store;
    }

    @Override
    public DataStore getStore() {
        return store;
    }

    @Override
    protected DefaultMetadata createMetadata() throws DataStoreException {
        final DefaultDataIdentification idf = new DefaultDataIdentification();
        final DefaultCitation citation = new DefaultCitation();
        citation.getIdentifiers().add(getIdentifier());
        idf.setCitation(citation);

        GridCoverageReader reader = null;
        DefaultMetadata metadata = null;
        try {
            reader = acquireReader();
            metadata = DefaultMetadata.castOrCopy(reader.getMetadata());
        } catch (DataStoreException ex) {
            metadata = new DefaultMetadata();
        } finally {
            if (reader != null) recycle(reader);
        }

        metadata.setIdentificationInfo(Arrays.asList(idf));
        return metadata;
    }

    @Override
    public synchronized CoverageDescription getCoverageDescription() {
        if (desc!=null) return desc;

        //calculate image statistics
        try {
            final GridCoverageReader reader = acquireReader();
            // Read a subset of the coverage, to compute approximative statistics
            GridCoverage coverage = null;
            try {
                GeneralGridGeometry gridGeom = reader.getGridGeometry(getImageIndex());
                // For multi-dimensional data, we get the first 2D slice available
                GridGeometryIterator geometryIterator = new GridGeometryIterator(gridGeom);
                if (geometryIterator.hasNext()) {
                    gridGeom = geometryIterator.next();
                }
                if (gridGeom instanceof GridGeometry2D) {
                    final GridGeometry2D geom2d = (GridGeometry2D) gridGeom;
                    final double[] subsetResolution = new double[geom2d.getDimension()];
                    Arrays.fill(subsetResolution, 1);

                    subsetResolution[geom2d.gridDimensionX] = geom2d.getEnvelope().getSpan(geom2d.axisDimensionX) / DEFAULT_SUBSET_SIZE;
                    subsetResolution[geom2d.gridDimensionY] = geom2d.getEnvelope().getSpan(geom2d.axisDimensionY) / DEFAULT_SUBSET_SIZE;

                    final double[] nativeResolution = geom2d.getResolution();
                    if (nativeResolution != null) {
                        if (Double.isFinite(nativeResolution[geom2d.gridDimensionX])) {
                            subsetResolution[geom2d.gridDimensionX] = Math.max(subsetResolution[geom2d.gridDimensionX], nativeResolution[geom2d.gridDimensionX]);
                        }
                        if (Double.isFinite(nativeResolution[geom2d.gridDimensionY])) {
                            subsetResolution[geom2d.gridDimensionY] = Math.max(subsetResolution[geom2d.gridDimensionY], nativeResolution[geom2d.gridDimensionY]);
                        }
                    }

                    final GridCoverageReadParam param = new GridCoverageReadParam();
                    param.setResolution(subsetResolution);
                    coverage = reader.read(getImageIndex(), param);
                }

                recycle(reader);

            } catch (Exception e) {
                try {
                    reader.dispose();
                } catch (Exception bis) {
                    e.addSuppressed(bis);
                }
                throw e;
            }

            if (coverage instanceof GridCoverage2D) {
                final ProcessDescriptor processDesc = ProcessFinder.getProcessDescriptor("geotoolkit", "coverage:statistic");
                final ParameterDescriptorGroup inputDesc = processDesc.getInputDescriptor();
                final Parameters processParam = Parameters.castOrWrap(inputDesc.createValue());
                processParam.parameter("inCoverage").setValue(coverage);
                processParam.parameter("inExcludeNoData").setValue(true);

                final Process process = processDesc.createProcess(processParam);
                final ParameterValueGroup result = process.call();
                final ImageStatistics stats = (ImageStatistics) result.parameter("outStatistic").getValue();
                desc = new CoverageDescriptionAdapter(stats);
            }

        } catch (Exception ex) {
            Logging.getLogger("org.geotoolkit.storage.coverage").log(Level.WARNING, "Cannot compute statistics on coverage content", ex);
        }

        if (desc == null) {
            desc = new DefaultCoverageDescription();
            final DefaultAttributeGroup attg = new DefaultAttributeGroup();
            desc.getAttributeGroups().add(attg);
        }

        return desc;
    }

    /**
     * Default recycle implementation.
     * Dispose the reader.
     */
    @Override
    public void recycle(CoverageReader reader) {
        dispose(reader);
    }

    /**
     * Default recycle implementation.
     * Dispose the writer.
     */
    @Override
    public void recycle(CoverageWriter writer) {
        try {
            writer.dispose();
        } catch (CoverageStoreException ex) {
            Logging.getLogger("org.geotoolkit.storage.coverage").log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    protected CoverageStoreManagementEvent firePyramidAdded(final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidAddEvent(this, getIdentifier(), pyramidId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidUpdated(final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidUpdateEvent(this, getIdentifier(), pyramidId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidDeleted(final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidDeleteEvent(this, getIdentifier(), pyramidId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicAdded(final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicAddEvent(this, getIdentifier(), pyramidId, mosaicId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicUpdated(final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicUpdateEvent(this, getIdentifier(), pyramidId, mosaicId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicDeleted(final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicDeleteEvent(this, getIdentifier(), pyramidId, mosaicId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireDataUpdated(){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createDataUpdateEvent(this, getIdentifier());
        sendEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileAdded(final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileAddEvent(this, getIdentifier(), pyramidId, mosaicId, tiles);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileUpdated(final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileUpdateEvent(this, getIdentifier(), pyramidId, mosaicId, tiles);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileDeleted(final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileDeleteEvent(this, getIdentifier(), pyramidId, mosaicId, tiles);
        sendEvent(event);
        return event;
    }

    /**
     * Dispose a reader, trying to properly release sub resources.
     * Best effort.
     *
     * @param reader
     */
    protected void dispose(CoverageReader reader) {
        try {
//            //try to close sub stream
//            Object input = reader.getInput();
//            if(input instanceof ImageReader){
//                final ImageReader ireader = (ImageReader)input;
//                ImageIOUtilities.releaseReader(ireader);
//            }else if(input instanceof InputStream){
//                final InputStream stream = (InputStream) input;
//                stream.close();
//            }else if(input instanceof ImageInputStream){
//                final ImageInputStream stream = (ImageInputStream) input;
//                stream.close();
//            }

            reader.dispose();

        } catch (CoverageStoreException ex) {
            Logging.getLogger("org.geotoolkit.storage.coverage").log(Level.WARNING, ex.getMessage(), ex);
        }
    }

}
