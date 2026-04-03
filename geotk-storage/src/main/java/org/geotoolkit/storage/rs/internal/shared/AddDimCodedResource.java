/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.storage.rs.internal.shared;

import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.rs.CodedCoverage;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.CodedResource;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class AddDimCodedResource extends AbstractResource implements CodedResource {

    private final CodedResource source;
    private final GridGeometry slice;
    private final CodedGeometry compound;

    public AddDimCodedResource(CodedResource source, GridGeometry slice) throws DataStoreException {
        super(null);
        this.source = source;
        this.slice = slice;
        compound = CodedGeometry.compound(source.getGridGeometry(), new CodedGeometry(slice));
    }

    public CodedResource getSource() {
        return source;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return source.getIdentifier();
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        Optional<Envelope> opt = source.getEnvelope();
        if (opt.isPresent() && slice.isDefined(GridGeometry.ENVELOPE)) {
            Envelope e;
            try {
                e = Envelopes.compound(opt.get(), slice.getEnvelope());
                return Optional.of(e);
            } catch (FactoryException ex) {
                //we have try
            }
        }
        return opt;
    }

    @Override
    protected Metadata createMetadata() throws DataStoreException {
        return source.getMetadata();
    }

    @Override
    public CodedGeometry getGridGeometry() throws DataStoreException {
        return compound;
    }

    @Override
    public CodedCoverage read(CodedGeometry geometry, int... range) throws DataStoreException {
        final CodedCoverage coverage = source.read(geometry, range);
        return new AddDimCodedCoverage(coverage, slice);
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return source.getSampleDimensions();
    }

}
