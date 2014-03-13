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
package org.geotoolkit.wmsc;

import java.awt.Dimension;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.util.List;
import javax.swing.ProgressMonitor;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.coverage.*;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.wms.WMSCoverageReference;
import org.geotoolkit.wmsc.model.WMSCPyramidSet;
import org.opengis.feature.type.Name;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCCoverageReference extends WMSCoverageReference implements PyramidalCoverageReference{

    private final PyramidSet set;

    public WMSCCoverageReference(final WebMapClientCached server,
            final Name name) throws CapabilitiesException{
        super(server, name);
        set = new WMSCPyramidSet(server, name.getLocalPart());
    }

    @Override
    public synchronized GridCoverageReader acquireReader() throws CoverageStoreException {
        final PyramidalModelReader reader = new PyramidalModelReader();
        reader.setInput(this);
        return reader;
    }

    @Override
    public PyramidSet getPyramidSet() throws DataStoreException {
        return set;
    }

    @Override
    public Pyramid createPyramid(CoordinateReferenceSystem crs) throws DataStoreException {
        throw new DataStoreException("Model is not writeable.");
    }

    @Override
    public void deletePyramid(String pyramidId) throws DataStoreException {
        throw new DataStoreException("Model is not writeable.");
    }

    @Override
    public GridMosaic createMosaic(String pyramidId, Dimension gridSize, Dimension tilePixelSize, DirectPosition upperleft, double pixelscale) throws DataStoreException {
        throw new DataStoreException("Model is not writeable.");
    }

    @Override
    public void deleteMosaic(String pyramidId, String mosaicId) throws DataStoreException {
        throw new DataStoreException("Model is not writeable.");
    }

    @Override
    public void writeTile(String pyramidId, String mosaicId, int col, int row, RenderedImage image) throws DataStoreException {
        throw new DataStoreException("Model is not writeable.");
    }

    @Override
    public void writeTiles(String pyramidId, String mosaicId, RenderedImage image, boolean onlyMissing, ProgressMonitor monitor) throws DataStoreException {
        throw new DataStoreException("Model is not writeable.");
    }

    @Override
    public void deleteTile(String pyramidId, String mosaicId, int col, int row) throws DataStoreException {
        throw new DataStoreException("Model is not writeable.");
    }

    @Override
    public ViewType getPackMode() throws DataStoreException {
        return ViewType.RENDERED;
    }

    @Override
    public void setPackMode(ViewType packMode) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions() throws DataStoreException {
        return null;
    }

    @Override
    public void setSampleDimensions(List<GridSampleDimension> dimensions) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public ColorModel getColorModel() throws DataStoreException {
        return null;
    }

    @Override
    public void setColorModel(ColorModel colorModel) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

}
