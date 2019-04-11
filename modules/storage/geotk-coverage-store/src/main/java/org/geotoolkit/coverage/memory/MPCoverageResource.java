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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.data.multires.MultiResolutionModel;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.storage.coverage.*;
import org.opengis.util.GenericName;

/**
 *
 *
 * @author Marechal Remi (Geomatys).
 */
public class MPCoverageResource extends AbstractPyramidalCoverageResource {

    private final DefaultPyramidSet pyramidSet;
    final AtomicLong mosaicID = new AtomicLong(0);
    private ViewType viewType;
    private List<SampleDimension> dimensions;
    private SampleModel sampleModel;
    private ColorModel colorModel;

    public MPCoverageResource(final MPCoverageStore store, final GenericName name) {
        super(store,name);
        this.pyramidSet = new DefaultPyramidSet();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean isWritable() throws CoverageStoreException {
        return true;
    }

    /**
     * {@inheritDoc }.
     */
    public DefaultPyramidSet getPyramidSet() {
        return pyramidSet;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return pyramidSet.getPyramids();
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
    @Override
    public void setSampleDimensions(List<SampleDimension> dimensions) throws DataStoreException {
        this.dimensions = dimensions;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public ColorModel getColorModel() throws DataStoreException {
        return colorModel;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setColorModel(ColorModel colorModel) {
        if (this.colorModel != null)
            assert colorModel.equals(this.colorModel) : "Into Pyramid, internal data ColorModel must be equals. "
                    + "                                 Expected : "+this.colorModel+", found : "+colorModel;
        this.colorModel = colorModel;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public SampleModel getSampleModel() throws DataStoreException {
        return sampleModel;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleModel(SampleModel sampleModel) {
        this.sampleModel = sampleModel;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public ViewType getPackMode() throws DataStoreException {
        return viewType;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
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
            pyramidSet.getPyramids().add(py);
            return py;
        } else {
            throw new DataStoreException("Unsupported model "+template);
        }
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        final Collection<Pyramid> coll = pyramidSet.getPyramids();
        final Iterator<Pyramid> it     = coll.iterator();
        while (it.hasNext()) {
            final Pyramid py = it.next();
            if (identifier.equalsIgnoreCase(py.getIdentifier())) {
                coll.remove(py);
                return;
            }
        }
        throw new DataStoreException("Identifier "+identifier+" not found in models.");
    }
}
