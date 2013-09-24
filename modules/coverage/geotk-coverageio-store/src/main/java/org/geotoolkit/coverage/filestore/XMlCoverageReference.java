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
package org.geotoolkit.coverage.filestore;

import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.SI;
import javax.xml.bind.JAXBException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.AbstractPyramidalModel;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.Pyramid;
import org.opengis.coverage.SampleDimensionType;

import org.opengis.feature.type.Name;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XMLCoverageReference extends AbstractPyramidalModel {

    private final XMLPyramidSet set;


    public XMLCoverageReference(XMLCoverageStore store, Name name, XMLPyramidSet set) {
        super(store,name,0);
        this.set = set;
    }

    @Override
    public boolean isWritable() throws DataStoreException {
        return true;
    }

    @Override
    public XMLPyramidSet getPyramidSet() throws DataStoreException {
        return set;
    }

    @Override
    public Pyramid createPyramid(CoordinateReferenceSystem crs) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        save();
        return set.createPyramid(crs);
    }

    @Override
    public void deletePyramid(String pyramidId) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public GridMosaic createMosaic(String pyramidId, Dimension gridSize,
    Dimension tilePixelSize, DirectPosition upperleft, double pixelscale) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        final XMLPyramid pyramid = (XMLPyramid) set.getPyramid(pyramidId);
        final XMLMosaic mosaic = pyramid.createMosaic(gridSize, tilePixelSize, upperleft, pixelscale);
        save();
        return mosaic;
    }

    @Override
    public void deleteMosaic(String pyramidId, String mosaicId) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public void writeTile(String pyramidId, String mosaicId, int col, int row, RenderedImage image) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        final XMLPyramid pyramid = (XMLPyramid) set.getPyramid(pyramidId);
        final XMLMosaic mosaic = pyramid.getMosaic(mosaicId);
        mosaic.createTile(col,row,image);
        save();
    }

    /**
     * Save the pyramid set in the file
     * @throws DataStoreException
     */
    synchronized void save() throws DataStoreException{
        final XMLPyramidSet set = getPyramidSet();
        try {
            set.write();
        } catch (JAXBException ex) {
            Logger.getLogger(XMLCoverageReference.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    public void writeTiles(String pyramidId, String mosaicId, RenderedImage image, boolean onlyMissing) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        final XMLPyramid pyramid = (XMLPyramid) set.getPyramid(pyramidId);
        final XMLMosaic mosaic = pyramid.getMosaic(mosaicId);
        mosaic.writeTiles(image,onlyMissing);
        save();
    }

    @Override
    public void deleteTile(String pyramidId, String mosaicId, int col, int row) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws DataStoreException {
        final List<XMLSampleDimension> xmlDimensions = getPyramidSet().getSampleDimensions();
        if(xmlDimensions.isEmpty()) return null;

        final List<GridSampleDimension> dimensions = new ArrayList<>();
        int i=0;
        for(XMLSampleDimension xsd : xmlDimensions){
            GridSampleDimension gsd = new GridSampleDimension(""+i,xsd.getSampleType(),
                    null, null, null, null, -100d, 100d, 1, 1, SI.METRE);
            dimensions.add(gsd);
            i++;
        }

        return dimensions;
    }

    @Override
    public void createSampleDimension(List<GridSampleDimension> dimensions, final Map<String, Object> analyse) throws DataStoreException {
        if(dimensions==null) return;
        final List<XMLSampleDimension> xmlDimensions = getPyramidSet().getSampleDimensions();
        xmlDimensions.clear();
        for(GridSampleDimension dimension : dimensions){
            final SampleDimensionType sdt = dimension.getSampleDimensionType();
            final XMLSampleDimension dim = new XMLSampleDimension();
            dim.setSampleType(sdt);
            xmlDimensions.add(dim);
        }
        try {
            set.write();
        } catch (JAXBException ex) {
            throw new DataStoreException(ex);
        }
    }

}
