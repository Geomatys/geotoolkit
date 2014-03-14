/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.List;
import javax.swing.ProgressMonitor;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.opengis.feature.type.Name;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Abstract pyramidal coverage reference.
 * All methods return null values if authorized and writing operations raise exceptions.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractPyramidalCoverageReference extends AbstractCoverageReference implements PyramidalCoverageReference {

    public AbstractPyramidalCoverageReference(CoverageStore store, Name name) {
        super(store, name);
    }

    @Override
    public boolean isWritable() throws CoverageStoreException {
        return false;
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        final PyramidalModelReader reader = new PyramidalModelReader();
        reader.setInput(this);
        return reader;
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        throw new CoverageStoreException("Pyramid writing not supported.");
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return null;
    }

    @Override
    public ViewType getPackMode() throws DataStoreException {
        return ViewType.RENDERED;
    }

    @Override
    public void setPackMode(ViewType packMode) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions() throws DataStoreException {
        return null;
    }

    @Override
    public void setSampleDimensions(List<GridSampleDimension> dimensions) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public ColorModel getColorModel() throws DataStoreException {
        return null;
    }

    @Override
    public void setColorModel(ColorModel colorModel) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public SampleModel getSampleModel() throws DataStoreException {
        return null;
    }

    @Override
    public void setSampleModel(SampleModel sampleModel) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public Pyramid createPyramid(CoordinateReferenceSystem crs) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public void deletePyramid(String pyramidId) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public GridMosaic createMosaic(String pyramidId, Dimension gridSize, Dimension tilePixelSize, 
            DirectPosition upperleft, double pixelscale) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public void deleteMosaic(String pyramidId, String mosaicId) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public void writeTiles(String pyramidId, String mosaicId, RenderedImage image, 
            boolean onlyMissing, ProgressMonitor monitor) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public void writeTile(String pyramidId, String mosaicId, int tileX, int tileY, 
            RenderedImage image) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public void deleteTile(String pyramidId, String mosaicId, 
            int tileX, int tileY) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }
    
}
