/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

import java.awt.image.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.data.multires.MultiResolutionModel;
import org.geotoolkit.data.multires.MultiResolutionResource;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.storage.coverage.*;
import org.opengis.util.GenericName;

/**
 *
 *
 * @author Marechal Remi (Geomatys).
 * @author Johann Sorel (Geomatys).
 */
public class MPCoverageResource extends AbstractGridResource implements MultiResolutionResource, StoreResource, WritableGridCoverageResource{

    private final MPCoverageStore store;
    private final GenericName name;
    private final List<Pyramid> pyramids = new ArrayList<>();
    final AtomicLong mosaicID = new AtomicLong(0);
    private ViewType viewType;
    private List<SampleDimension> dimensions;
    private SampleModel sampleModel;
    private ColorModel colorModel;

    public MPCoverageResource(final GenericName name) {
        this(null, name);
    }

    public MPCoverageResource(final MPCoverageStore store, final GenericName name) {
        super(null);
        this.store = store;
        this.name = name;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(name);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return pyramids;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return dimensions;
    }

    /**
     * {@inheritDoc }.
     */
    public void setSampleDimensions(List<SampleDimension> dimensions) throws DataStoreException {
        this.dimensions = dimensions;
    }

    /**
     * {@inheritDoc }.
     */
    public ColorModel getColorModel() throws DataStoreException {
        return colorModel;
    }

    /**
     * {@inheritDoc }.
     */
    public void setColorModel(ColorModel colorModel) {
        if (this.colorModel != null)
            assert colorModel.equals(this.colorModel) : "Into Pyramid, internal data ColorModel must be equals. "
                    + "                                 Expected : "+this.colorModel+", found : "+colorModel;
        this.colorModel = colorModel;
    }

    /**
     * {@inheritDoc }.
     */
    public SampleModel getSampleModel() throws DataStoreException {
        return sampleModel;
    }

    /**
     * {@inheritDoc }.
     */
    public void setSampleModel(SampleModel sampleModel) {
        this.sampleModel = sampleModel;
    }

    /**
     * {@inheritDoc }.
     */
    public ViewType getPackMode() throws DataStoreException {
        return viewType;
    }

    /**
     * {@inheritDoc }.
     */
    public void setPackMode(ViewType packMode) throws DataStoreException {
        this.viewType = packMode;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        if (template instanceof Pyramid) {
            final Pyramid p = (Pyramid) template;
            final MPPyramid py = new MPPyramid(this, UUID.randomUUID().toString(), p.getCoordinateReferenceSystem());
            Pyramids.copyStructure(p, py);
            pyramids.add(py);
            return py;
        } else {
            throw new DataStoreException("Unsupported model "+template);
        }
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        final Iterator<Pyramid> it     = pyramids.iterator();
        while (it.hasNext()) {
            final Pyramid py = it.next();
            if (identifier.equalsIgnoreCase(py.getIdentifier())) {
                pyramids.remove(py);
                return;
            }
        }
        throw new DataStoreException("Identifier "+identifier+" not found in models.");
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new PyramidReader<>(this).getGridGeometry();
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return new PyramidReader<>(this).read(domain, range);
    }

    @Override
    public DataStore getOriginator() {
        return store;
    }

    @Override
    public void write(GridCoverage coverage, Option... options) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported.");
    }

}
