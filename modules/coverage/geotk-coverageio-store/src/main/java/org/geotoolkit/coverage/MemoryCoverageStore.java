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
package org.geotoolkit.coverage;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataStoreException;
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

    /**
     * Grid coverage to store.
     */
    private final GridCoverage2D gridCov;

    private final String name;

    public MemoryCoverageStore(final GridCoverage2D gridCov) {
        this(gridCov, null);
    }

    public MemoryCoverageStore(final GridCoverage2D gridCov, final String name) {
        super(desc.createValue());
        this.gridCov = gridCov;
        this.name = name;
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
        final String covName = (name != null) ? name : "memory";
        final Name n = new DefaultName(covName);
        return Collections.singleton(n);
    }

    @Override
    public CoverageReference getCoverageReference(Name name) throws DataStoreException {
        final SimpleCoverageReader reader = new SimpleCoverageReader(gridCov, this.name);
        return new DefaultCoverageReference(reader, 0);
    }

    /**
     * Do nothing in this implementation.
     */
    @Override
    public void dispose() {
    }


    /**
     *
     */
    private static class SimpleCoverageReader extends GridCoverageReader {

        private final GridCoverage2D coverage;
        private final String name;

        public SimpleCoverageReader(final GridCoverage2D coverage) {
            this(coverage, null);
        }

        public SimpleCoverageReader(final GridCoverage2D coverage, final String name) {
            this.coverage = coverage;
            this.name = name;
        }

        @Override
        public GeneralGridGeometry getGridGeometry(final int i) throws CoverageStoreException, CancellationException {
            return (GeneralGridGeometry) coverage.getGridGeometry();
        }

        @Override
        public List<GridSampleDimension> getSampleDimensions(final int i) throws CoverageStoreException, CancellationException {
            return Collections.singletonList(coverage.getSampleDimension(i));
        }

        @Override
        public GridCoverage read(final int i, final GridCoverageReadParam gcrp) throws CoverageStoreException, CancellationException {
            return coverage;
        }

        @Override
        public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
            final NameFactory dnf = FactoryFinder.getNameFactory(null);
            final String nameSpace = "http://geotoolkit.org" ;
            final NameSpace ns = dnf.createNameSpace(dnf.createGenericName(null, nameSpace), null);
            final String covName = (name != null) ? name : "memory";
            final GenericName gn = dnf.createLocalName(ns, covName);
            return Collections.singletonList(gn);
        }
    }
}
