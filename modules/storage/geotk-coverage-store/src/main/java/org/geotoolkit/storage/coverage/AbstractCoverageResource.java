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
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.opengis.util.GenericName;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.metadata.iso.content.DefaultAttributeGroup;
import org.apache.sis.metadata.iso.content.DefaultCoverageDescription;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.metadata.ImageStatistics;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.storage.AbstractResource;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlTransient
public abstract class AbstractCoverageResource extends AbstractResource implements CoverageResource {

    protected final CoverageStore store;
    protected final GenericName name;

    private DefaultCoverageDescription desc = null;

    /**
     *
     * @param store can be null
     * @param name never null
     */
    public AbstractCoverageResource(CoverageStore store, GenericName name) {
        ArgumentChecks.ensureNonNull("name",name);
        this.store = store;
        this.name = name;
    }

    @Override
    public GenericName getName() {
        return name;
    }

    @Override
    public CoverageStore getStore() {
        return store;
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {

        GridCoverageReader reader = null;
        try {
            reader = acquireReader();
            return reader.getMetadata();
        } finally {
            if (reader!=null) recycle(reader);
        }
    }

    @Override
    public synchronized CoverageDescription getCoverageDescription() {
        if(desc!=null) return desc;

        //calculate image statistics
        try {
            final GridCoverageReader reader = acquireReader();
            final GeneralGridGeometry gridGeom = reader.getGridGeometry(getImageIndex());
            final Envelope env = gridGeom.getEnvelope();
            final GridEnvelope ext = gridGeom.getExtent();

            final double[] res = new double[ext.getDimension()];
            double max = 0;
            for(int i=0;i<res.length;i++){
                res[i] = (env.getSpan(i) / 1000);
                max = Math.max(max,res[i]);
            }
            Arrays.fill(res, max);

            final GridCoverageReadParam param = new GridCoverageReadParam();
            param.setEnvelope(env);
            param.setResolution(res);
            final GridCoverage coverage = reader.read(getImageIndex(), param);
            if(!(coverage instanceof GridCoverage2D)) return null;

            final ProcessDescriptor processDesc = ProcessFinder.getProcessDescriptor("geotoolkit", "coverage:statistic");
            final ParameterValueGroup processParam = processDesc.getInputDescriptor().createValue();
            ParametersExt.getOrCreateValue(processParam, "inCoverage").setValue(coverage);
            ParametersExt.getOrCreateValue(processParam, "inExcludeNoData").setValue(true);
            final Process process = processDesc.createProcess(processParam);
            final ParameterValueGroup result = process.call();
            final ImageStatistics stats = (ImageStatistics) ParametersExt.getOrCreateValue(result, "outStatistic").getValue();
            desc = new CoverageDescriptionAdapter(stats);

        } catch (CoverageStoreException | NoSuchIdentifierException | ProcessException ex) {
            Logging.getLogger("org.geotoolkit.storage.coverage").log(Level.WARNING, ex.getMessage(), ex);
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
     *
     * @param reader
     */
    @Override
    public void recycle(CoverageReader reader) {
        dispose(reader);
    }

    /**
     * Default recycle implementation.
     * Dispose the writer.
     *
     * @param writer
     */
    @Override
    public void recycle(GridCoverageWriter writer) {
        try {
            writer.dispose();
        } catch (CoverageStoreException ex) {
            Logging.getLogger("org.geotoolkit.storage.coverage").log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    protected CoverageStoreManagementEvent firePyramidAdded(final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidAddEvent(this, getName(), pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidUpdated(final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidUpdateEvent(this, getName(), pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidDeleted(final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidDeleteEvent(this, getName(), pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicAdded(final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicAddEvent(this, getName(), pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicUpdated(final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicUpdateEvent(this, getName(), pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicDeleted(final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicDeleteEvent(this, getName(), pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireDataUpdated(){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createDataUpdateEvent(this, getName());
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileAdded(final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileAddEvent(this, getName(), pyramidId, mosaicId, tiles);
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileUpdated(final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileUpdateEvent(this, getName(), pyramidId, mosaicId, tiles);
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileDeleted(final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileDeleteEvent(this, getName(), pyramidId, mosaicId, tiles);
        sendContentEvent(event);
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
