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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.AbstractCoverageStore;
import org.geotoolkit.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStoreContentEvent;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.DefaultCoverageReference;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.opengis.util.NameSpace;

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
    private static final ParameterDescriptorGroup desc = new DefaultParameterDescriptorGroup("", AbstractCoverageStoreFactory.NAMESPACE);

    private final Map<Name, CoverageReference> layers = new HashMap<Name, CoverageReference>();
    

    public MemoryCoverageStore() {
        super(desc.createValue());
    }

    public MemoryCoverageStore(final GridCoverage2D gridCov) {
        this(gridCov, null);
    }

    public MemoryCoverageStore(final GridCoverage2D gridCov, final String name) {
        this();
        try {
            final CoverageReference ref = create(new DefaultName(getDefaultNamespace(), name));
            ref.createWriter().write(gridCov, null);
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
    public Set<Name> getNames() throws DataStoreException {
        return layers.keySet();
    }

    @Override
    public CoverageReference getCoverageReference(final Name name) throws DataStoreException {
        final CoverageReference cr = layers.get(name);
        if(cr == null){
            throw new DataStoreException("CoverageReference for name "+name+" does not exist.");
        }
        return cr;
    }

    @Override
    public CoverageReference create(final Name name) throws DataStoreException {
        if(layers.containsKey(name)){
            throw new DataStoreException("Layer "+name+" already exist");
        }
        layers.put(name, new MemoryCoverageReference(name));
        fireCoverageAdded(name);
        return getCoverageReference(name);
    }

    /**
     * Do nothing in this implementation.
     */
    @Override
    public void dispose() {
    }

    private class MemoryCoverageReference extends DefaultCoverageReference{

        private final Name name;
        private GridCoverage2D coverage;
        
        public MemoryCoverageReference(Name name) {
            super(null, 0);
            this.name = name;
        }

        public void setCoverage(GridCoverage2D coverage) {
            this.coverage = coverage;
            final CoverageStoreContentEvent event = fireDataUpdated();
            getStore().forwardContentEvent(event);
        }

        @Override
        public Name getName() {
            return name;
        }
        
        @Override
        public MemoryCoverageStore getStore() {
            return MemoryCoverageStore.this;
        }

        @Override
        public GridCoverageWriter createWriter() throws DataStoreException {
            return new MemoryCoverageWriter(this);
        }

        @Override
        public GridCoverageReader createReader() throws DataStoreException {
            return new MemoryCoverageReader(this);
        }
        
    }

    private static class MemoryCoverageReader extends GridCoverageReader {

        private final MemoryCoverageReference ref;

        public MemoryCoverageReader(MemoryCoverageReference ref){
            this.ref = ref;
        }
                
        @Override
        public GeneralGridGeometry getGridGeometry(final int i) throws CoverageStoreException, CancellationException {
            return (GeneralGridGeometry) ref.coverage.getGridGeometry();
        }

        @Override
        public List<GridSampleDimension> getSampleDimensions(final int i) throws CoverageStoreException, CancellationException {
            return Collections.singletonList(ref.coverage.getSampleDimension(i));
        }

        @Override
        public GridCoverage read(final int i, final GridCoverageReadParam gcrp) throws CoverageStoreException, CancellationException {
            return ref.coverage;
        }

        @Override
        public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
            final NameFactory dnf = FactoryFinder.getNameFactory(null);
            final String nameSpace = "http://geotoolkit.org" ;
            final NameSpace ns = dnf.createNameSpace(dnf.createGenericName(null, nameSpace), null);
            final String covName = ref.getName().getLocalPart();
            final GenericName gn = dnf.createLocalName(ns, covName);
            return Collections.singletonList(gn);
        }
    }

    private static class MemoryCoverageWriter extends GridCoverageWriter{

        private final MemoryCoverageReference ref;
        
        public MemoryCoverageWriter(MemoryCoverageReference ref){
            this.ref = ref;
        }
        
        @Override
        public void write(GridCoverage coverage, GridCoverageWriteParam param) throws CoverageStoreException, CancellationException {
            ref.setCoverage((GridCoverage2D)coverage);
        }
        
    }
    
}
