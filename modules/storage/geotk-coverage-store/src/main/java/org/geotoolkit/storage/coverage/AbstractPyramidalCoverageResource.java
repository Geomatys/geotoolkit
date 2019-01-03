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

package org.geotoolkit.storage.coverage;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.data.multires.Tile;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * Abstract pyramidal coverage reference.
 * All methods return null values if authorized and writing operations raise exceptions.
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlTransient
public abstract class AbstractPyramidalCoverageResource extends AbstractCoverageResource implements PyramidalCoverageResource {

    protected final int imageIndex;

    public AbstractPyramidalCoverageResource(DataStore store, GenericName name,int imageIndex) {
        super(store, name);
        this.imageIndex = imageIndex;
    }

    @Override
    public int getImageIndex() {
        return imageIndex;
    }

    @Override
    public boolean isWritable() throws CoverageStoreException {
        return false;
    }

    @Override
    public DefaultMetadata createMetadata() throws DataStoreException {
        final DefaultMetadata meta = super.createMetadata();

        final DefaultDataIdentification ident = (DefaultDataIdentification) meta.getIdentificationInfo().iterator().next();

        //-- geographic extent
        try {
            final Envelope envelope = Pyramids.getEnvelope(this);
            if(envelope != null) {
                final Envelope geoenv = Envelopes.transform(envelope, CommonCRS.WGS84.normalizedGeographic());
                final DefaultGeographicBoundingBox geo = new DefaultGeographicBoundingBox(
                        geoenv.getMinimum(0), geoenv.getMaximum(0),
                        geoenv.getMinimum(1), geoenv.getMaximum(1));
                final DefaultExtent ex = new DefaultExtent();
                ex.setGeographicElements(Arrays.asList(geo));
                ident.setExtents(Arrays.asList(ex));
            }
        } catch (TransformException ex) {
            //do not break metadata creation for a possible not geographic CRS
        }

        return meta;
    }

    @Override
    public Envelope getEnvelope() throws DataStoreException {
        return Pyramids.getEnvelope(this);
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        final PyramidalModelReader reader = new PyramidalModelReader();
        reader.setInput(this);
        return reader;
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        if(isWritable()){
            return new PyramidalModelWriter(this);
        }else{
            throw new CoverageStoreException("Pyramid is not writable");
        }
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
    public List<GridSampleDimension> getGridSampleDimensions() throws DataStoreException {
        return null;
    }

    @Override
    public void setGridSampleDimensions(List<GridSampleDimension> dimensions) throws DataStoreException {
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

    /**
     * Get a tile as coverage.
     */
    public static GridCoverage2D getTileAsCoverage(PyramidalCoverageResource covRef,
            String pyramidId, String mosaicId, int tileX, int tileY) throws DataStoreException {

        Tile tile = null;
        final org.geotoolkit.data.multires.Pyramid pyramid = Pyramids.getPyramid(covRef, pyramidId);
        if(pyramid==null){
            throw new DataStoreException("Invalid pyramid reference : "+pyramidId);
        }

        Mosaic mosaic = null;
        for (Mosaic gm : pyramid.getMosaics()) {
            if (gm.getIdentifier().equals(mosaicId)) {
                mosaic = gm;
                tile = gm.getTile(tileX, tileY);
            }
        }

        if (tile == null) {
            throw new DataStoreException("Invalid tile reference : "+pyramidId+" "+mosaicId+" "+tileX+" "+tileY);
        }

        return getTileAsCoverage(covRef, pyramidId, mosaicId, (ImageTile) tile);
    }

    /**
     * Get a tile as coverage.
     */
    public static GridCoverage2D getTileAsCoverage(PyramidalCoverageResource covRef,
            String pyramidId, String mosaicId, ImageTile tile) throws DataStoreException {

        final org.geotoolkit.data.multires.Pyramid pyramid = Pyramids.getPyramid(covRef, pyramidId);
        if(pyramid==null){
            throw new DataStoreException("Invalid pyramid reference : "+pyramidId);
        }

        Mosaic mosaic = null;
        for(Mosaic gm : pyramid.getMosaics()){
            if(gm.getIdentifier().equals(mosaicId)){
                mosaic = gm;
            }
        }

        RenderedImage image;
        try {
            image = tile.getImage();
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(),ex);
        }

        //build the coverage ---------------------------------------------------
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("tile");

        final CoordinateReferenceSystem tileCRS = pyramid.getCoordinateReferenceSystem();
        final MathTransform gridToCrs = Pyramids.getTileGridToCRS(mosaic,tile.getPosition());

        final long[] low = new long[tileCRS.getCoordinateSystem().getDimension()];
        final long[] high = new long[low.length];
        Arrays.fill(low, 0);
        Arrays.fill(high, 1);
        high[0] = image.getWidth();
        high[1] = image.getHeight();

        final GridExtent ge = new GridExtent(null, low, high, false);
        final GridGeometry2D gridgeo = new GridGeometry2D(ge, PixelInCell.CELL_CORNER, gridToCrs, tileCRS, null);
        gcb.setGridGeometry(gridgeo);
        gcb.setRenderedImage(image);

        final List<GridSampleDimension> dimensions = covRef.getGridSampleDimensions();
        if(dimensions!=null){
            gcb.setSampleDimensions(dimensions.toArray(new GridSampleDimension[0]));
        }
        return (GridCoverage2D) gcb.build();
    }
}
