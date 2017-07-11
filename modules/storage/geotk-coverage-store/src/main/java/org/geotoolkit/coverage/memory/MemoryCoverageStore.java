/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.coverage.memory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import org.apache.sis.parameter.ParameterBuilder;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.CoverageStoreContentEvent;
import org.geotoolkit.storage.coverage.CoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageType;
import org.geotoolkit.storage.coverage.DefaultCoverageResource;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.storage.DefaultDataSet;
import org.geotoolkit.storage.Resource;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.GenericName;
import org.geotoolkit.storage.coverage.CoverageResource;

/**
 * Simple implementation to provide a {@link MemoryCoverageStore} for a {@link GridCoverage2D}.
 *
 * @author Johan Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
public class MemoryCoverageStore extends AbstractCoverageStore {
    /**
     * Dummy parameter descriptor group.
     */
    private static final ParameterDescriptorGroup EMPTY_DESCRIPTOR = new ParameterBuilder().addName("Unamed").createGroup();

    private final DefaultDataSet rootNode = new DefaultDataSet(NamesExt.create("root"));


    public MemoryCoverageStore() {
        super(EMPTY_DESCRIPTOR.createValue());
    }

    public MemoryCoverageStore(final GridCoverage2D gridCov) {
        this(gridCov, String.valueOf(gridCov.getName()));
    }

    public MemoryCoverageStore(final GridCoverage2D gridCov, final String name) {
        this();
        try {
            final CoverageResource ref = create(NamesExt.create(name));
            final GridCoverageWriter writer = ref.acquireWriter();
            writer.write(gridCov, null);
            ref.recycle(writer);
        } catch (CoverageStoreException ex) {
            getLogger().log(Level.WARNING, ex.getMessage(), ex);
        } catch (CancellationException ex) {
            getLogger().log(Level.WARNING, ex.getMessage(), ex);
        } catch (DataStoreException ex) {
            getLogger().log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * Do nothing in this implementation.
     */
    @Override
    public CoverageStoreFactory getFactory() {
        return null;
    }

    @Override
    public Resource getRootResource() {
        return rootNode;
    }

    @Override
    public CoverageResource create(final GenericName name) throws DataStoreException {
        final Set<GenericName> names = getNames();
        if(names.contains(name)){
            throw new DataStoreException("Layer "+name+" already exist");
        }
        rootNode.addResource(new MemoryCoverageResource(name));
        fireCoverageAdded(name);
        return findResource(name);
    }

    /**
     * Do nothing in this implementation.
     */
    @Override
    public void close() {
    }

    private class MemoryCoverageResource extends DefaultCoverageResource{

        private GridCoverage2D coverage;

        public MemoryCoverageResource(GenericName name) {
            super(MemoryCoverageStore.this,null,name);
        }

        public void setCoverage(GridCoverage2D coverage) {
            this.coverage = coverage;
            final CoverageStoreContentEvent event = fireDataUpdated();
            ((MemoryCoverageStore)getStore()).forwardContentEvent(event);
        }

        @Override
        public GridCoverageWriter acquireWriter() throws CoverageStoreException {
            return new MemoryCoverageWriter(this);
        }

        @Override
        public GridCoverageReader acquireReader() throws CoverageStoreException {
            return new MemoryCoverageReader(this);
        }

    }

    private static class MemoryCoverageReader extends GridCoverageReader {

        private final MemoryCoverageResource ref;

        public MemoryCoverageReader(MemoryCoverageResource ref){
            this.ref = ref;
        }

        @Override
        public GeneralGridGeometry getGridGeometry(final int i) throws CoverageStoreException, CancellationException {
            return (GeneralGridGeometry) ref.coverage.getGridGeometry();
        }

        @Override
        public List<GridSampleDimension> getSampleDimensions(final int i) throws CoverageStoreException, CancellationException {
            return Arrays.asList(ref.coverage.getSampleDimensions());
        }

        @Override
        public GridCoverage read(final int i, final GridCoverageReadParam gcrp) throws CoverageStoreException, CancellationException {
            return ref.coverage;
        }

        @Override
        public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
            return Collections.singletonList(ref.getName());
        }
    }

    private static class MemoryCoverageWriter extends GridCoverageWriter{

        private final MemoryCoverageResource ref;

        public MemoryCoverageWriter(MemoryCoverageResource ref){
            this.ref = ref;
        }

        @Override
        public void write(GridCoverage coverage, GridCoverageWriteParam param) throws CoverageStoreException, CancellationException {
            ref.setCoverage((GridCoverage2D)coverage);
        }

    }

    @Override
    public CoverageType getType() {
        return CoverageType.GRID;
    }

}
