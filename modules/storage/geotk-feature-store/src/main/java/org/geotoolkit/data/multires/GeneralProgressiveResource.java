/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.data.multires;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.apache.sis.internal.storage.AbstractResource;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.process.ProcessListener;
import org.opengis.geometry.Envelope;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeneralProgressiveResource extends AbstractResource implements ProgressiveResource {

    protected final MultiResolutionResource base;
    protected TileGenerator generator;

    public GeneralProgressiveResource(MultiResolutionResource base, TileGenerator generator) throws DataStoreException {
        super(null);
        this.base = base;
        this.generator = generator;
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        try {
            return base.getIdentifier();
        } catch (DataStoreException ex) {
            throw new BackingStoreException(ex.getMessage(), ex);
        }
    }

    public TileGenerator getGenerator() {
        return generator;
    }

    @Override
    public void setGenerator(TileGenerator generator) {
        this.generator = generator;
    }

    @Override
    public MultiResolutionModel createModel(MultiResolutionModel model) throws DataStoreException {
        MultiResolutionModel rm = base.createModel(model);
        if (rm instanceof Pyramid) {
            rm = new ProgressivePyramid(this, (Pyramid) rm);
        }
        return rm;
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        base.removeModel(identifier);
    }

    @Override
    public void clear(Envelope env, NumberRange resolutions) throws DataStoreException {
        for (Pyramid pyramid : Pyramids.getPyramids(base)) {
            Pyramids.clear(pyramid, env, resolutions);
        }
    }

    @Override
    public void generate(Envelope env, NumberRange resolutions, ProcessListener listener) throws DataStoreException {
        try {
            for (Pyramid pyramid : Pyramids.getPyramids(base)) {
                generator.generate(pyramid, env, resolutions, listener);
            }
        } catch (InterruptedException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public Collection<? extends MultiResolutionModel> getModels() throws DataStoreException {
        final Collection<? extends MultiResolutionModel> models = base.getModels();
        final List<MultiResolutionModel> mapped = new ArrayList<>(models.size());
        final Iterator<? extends MultiResolutionModel> ite = models.iterator();
        while (ite.hasNext()) {
            MultiResolutionModel mr = ite.next();
            if (mr instanceof Pyramid) {
                mr = new ProgressivePyramid(this, (Pyramid) mr);
            }
            mapped.add(mr);
        }
        return mapped;
    }
}
