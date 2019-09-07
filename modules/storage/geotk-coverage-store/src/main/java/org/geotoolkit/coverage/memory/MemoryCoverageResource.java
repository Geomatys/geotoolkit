/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MemoryCoverageResource extends AbstractGridResource implements WritableGridCoverageResource {

    GenericName name;
    GridCoverage coverage;

    public MemoryCoverageResource() {
        super(null);
        this.name = null;
    }

    public MemoryCoverageResource(GenericName name) {
        super(null);
        this.name = name;
    }

    public MemoryCoverageResource(GridCoverage coverage) {
        this(null, coverage);
    }

    public MemoryCoverageResource(GenericName name, GridCoverage coverage) {
        super(null);
        if (name == null) {
            InternationalString in = CoverageUtilities.getName(coverage);
            this.name = (in == null) ? null : NamesExt.create(in.toString());
        } else {
            this.name = name;
        }
        this.coverage = coverage;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.ofNullable(name);
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        if (coverage == null) throw new DataStoreException("Coverage is undefined");
        return coverage.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        if (coverage == null) throw new DataStoreException("Coverage is undefined");
        return coverage.getSampleDimensions();
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        if (coverage == null) throw new DataStoreException("Coverage is undefined");
        return coverage;
    }

    @Override
    public void write(GridCoverage coverage, Option... options) throws DataStoreException {
        this.coverage = coverage;
    }

}
