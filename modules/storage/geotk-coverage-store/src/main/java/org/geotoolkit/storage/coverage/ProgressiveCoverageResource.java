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
package org.geotoolkit.storage.coverage;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.finder.StrictlyCoverageFinder;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.data.multires.GeneralProgressiveResource;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.TileGenerator;
import org.opengis.metadata.content.CoverageDescription;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ProgressiveCoverageResource<T extends GridCoverageResource & PyramidalCoverageResource>
        extends GeneralProgressiveResource implements GridCoverageResource, PyramidalCoverageResource {

    private T base = null;

    public ProgressiveCoverageResource(T resource, TileGenerator generator) throws DataStoreException {
        super(resource, generator);
        this.base = resource;
    }

    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return (Collection<Pyramid>) super.getModels();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return base.getGridGeometry();
    }

    @Override
    public GridCoverageReader acquireReader() throws DataStoreException {
        final PyramidalModelReader reader = new PyramidalModelReader(new StrictlyCoverageFinder());
        reader.setInput(this);
        return reader;
    }

    @Override
    public GridCoverageWriter acquireWriter() throws DataStoreException {
        throw new CoverageStoreException("Not supported.");
    }

    @Override
    public CoverageDescription getCoverageDescription() {
        return base.getCoverageDescription();
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public DataStore getOriginator() {
        return null;
    }

    @Override
    public void recycle(GridCoverageReader reader) {
        try {
            reader.dispose();
        } catch (DataStoreException ex) {
            Logging.getLogger("org.geotoolkit.storage.coverage").log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    public void recycle(GridCoverageWriter writer) {
        try {
            writer.dispose();
        } catch (DataStoreException ex) {
            Logging.getLogger("org.geotoolkit.storage.coverage").log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return base.getLegend();
    }

    @Override
    public ViewType getPackMode() throws DataStoreException {
        return base.getPackMode();
    }

    @Override
    public void setPackMode(ViewType packMode) throws DataStoreException {
        base.setPackMode(packMode);
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return base.getSampleDimensions();
    }

    @Override
    public void setSampleDimensions(List<SampleDimension> dimensions) throws DataStoreException {
        base.setSampleDimensions(dimensions);
    }

    @Override
    public ColorModel getColorModel() throws DataStoreException {
        return base.getColorModel();
    }

    @Override
    public void setColorModel(ColorModel colorModel) throws DataStoreException {
        base.setColorModel(colorModel);
    }

    @Override
    public SampleModel getSampleModel() throws DataStoreException {
        return base.getSampleModel();
    }

    @Override
    public void setSampleModel(SampleModel sampleModel) throws DataStoreException {
        base.setSampleModel(sampleModel);
    }

}
