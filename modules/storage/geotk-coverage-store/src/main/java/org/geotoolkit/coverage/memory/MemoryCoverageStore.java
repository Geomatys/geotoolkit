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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.WritableAggregate;
import org.geotoolkit.coverage.io.AbstractGridCoverageReader;
import org.geotoolkit.coverage.io.AbstractGridCoverageWriter;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.CoverageStoreContentEvent;
import org.geotoolkit.storage.coverage.DefaultCoverageResource;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.geotoolkit.util.NamesExt;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.GenericName;

/**
 * Simple implementation to provide a {@link MemoryCoverageStore} for a {@link GridCoverage}.
 *
 * @author Johan Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
public class MemoryCoverageStore extends AbstractCoverageStore implements WritableAggregate {
    /**
     * Dummy parameter descriptor group.
     */
    private static final ParameterDescriptorGroup EMPTY_DESCRIPTOR = new ParameterBuilder().addName("Unamed").createGroup();

    private final List<Resource> resources = Collections.synchronizedList(new ArrayList<>());


    public MemoryCoverageStore() {
        super(EMPTY_DESCRIPTOR.createValue());
    }

    public MemoryCoverageStore(final GridCoverage gridCov) {
        this(gridCov, String.valueOf(CoverageUtilities.getName(gridCov)));
    }

    public MemoryCoverageStore(final GridCoverage gridCov, final String name) {
        this();
        try {
            final GridCoverageResource ref = add(new DefiningCoverageResource(NamesExt.create(name),null));
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
    public DataStoreFactory getProvider() {
        return null;
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        return Collections.unmodifiableList(resources);
    }

    @Override
    public GridCoverageResource add(org.apache.sis.storage.Resource resource) throws DataStoreException {
        if (!(resource instanceof DefiningCoverageResource)) {
            throw new DataStoreException("Unsupported resource "+resource);
        }
        final DefiningCoverageResource cr = (DefiningCoverageResource) resource;
        final GenericName name = cr.getName();

        final Set<GenericName> names = getNames();
        if (names.contains(name)) {
            throw new DataStoreException("Layer "+name+" already exist");
        }
        final MemoryCoverageResource res = new MemoryCoverageResource(name);
        resources.add(res);
        fireCoverageAdded(name);
        return res;
    }

    @Override
    public void remove(org.apache.sis.storage.Resource resource) throws DataStoreException {
        if (!(resource instanceof GridCoverageResource)) {
            throw new DataStoreException("Unknown resource "+resource);
        }
        final GridCoverageResource cr = (GridCoverageResource) resource;
        final NamedIdentifier name = cr.getIdentifier();

        //TODO
        throw new DataStoreException("Remove operation not supported.");
    }

    /**
     * Do nothing in this implementation.
     */
    @Override
    public void close() {
    }

    private class MemoryCoverageResource extends DefaultCoverageResource {

        private GridCoverage coverage;

        public MemoryCoverageResource(GenericName name) {
            super(MemoryCoverageStore.this,null,name);
        }

        public void setCoverage(GridCoverage coverage) {
            this.coverage = coverage;
            final CoverageStoreContentEvent event = fireDataUpdated();
            ((MemoryCoverageStore)getOriginator()).forwardEvent(event);
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

    private static class MemoryCoverageReader extends AbstractGridCoverageReader {

        private final MemoryCoverageResource ref;

        public MemoryCoverageReader(MemoryCoverageResource ref){
            this.ref = ref;
        }

        @Override
        public GridGeometry getGridGeometry() throws CoverageStoreException, CancellationException {
            return (GridGeometry) ref.coverage.getGridGeometry();
        }

        @Override
        public List<SampleDimension> getSampleDimensions() throws CoverageStoreException, CancellationException {
            return ref.coverage.getSampleDimensions();
        }

        @Override
        public GridCoverage read(final GridCoverageReadParam gcrp) throws CoverageStoreException, CancellationException {
            return ref.coverage;
        }

        @Override
        public GenericName getCoverageName() throws CoverageStoreException, CancellationException {
            return ref.getIdentifier();
        }
    }

    private static class MemoryCoverageWriter extends AbstractGridCoverageWriter {

        private final MemoryCoverageResource ref;

        public MemoryCoverageWriter(MemoryCoverageResource ref){
            this.ref = ref;
        }

        @Override
        public void write(GridCoverage coverage, GridCoverageWriteParam param) throws CoverageStoreException, CancellationException {
            ref.setCoverage(coverage);
        }

    }

}
