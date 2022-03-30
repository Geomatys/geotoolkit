/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.storage.coverage.mosaic;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import javax.measure.Unit;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridDerivation;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.image.ComputedImage;
import org.apache.sis.internal.referencing.j2d.Tile;
import org.apache.sis.internal.referencing.j2d.TileOrganizer;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.util.StringUtilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TiledCoverageResource extends AbstractGridCoverageResource {

    private final GridGeometry gridGeometry;
    private final ResourceTile[] tiles;
    private final Map<Point, ResourceTile> indexedTiles = new HashMap();

    private List<SampleDimension> sampleDimensions;
    private boolean forceTransformedValues;
    private double[] noData;
    private Raster rasterTemplate;
    private SampleModel sampleModel;
    private ColorModel colorModel;

    private TiledCoverageResource(GridGeometry gridGeometry, Tile[] tiles) throws DataStoreException {
        super(null);
        this.gridGeometry = gridGeometry;
        this.tiles = Arrays.copyOf(tiles, tiles.length, ResourceTile[].class);


        //add a no-data category
        //the no-data is needed to fill possible gaps between coverages
        //TODO need to improve detection cases to avoid switching to transformed values all the time
        sampleDimensions = new ArrayList<>(this.tiles[0].getResource().getSampleDimensions());
        forceTransformedValues = false;
        noData = new double[sampleDimensions.size()];
        for (int i = 0,n = sampleDimensions.size(); i < n; i++) {
            SampleDimension baseDim = sampleDimensions.get(i);
            Set<Number> noData = baseDim.getNoDataValues();
            Optional<Number> background = baseDim.getBackground();
            if (noData.isEmpty() && !background.isPresent()) {
                baseDim = sampleDimensions.get(i).forConvertedValues(true);
                final SampleDimension.Builder builder = new SampleDimension.Builder();
                final Unit<?> unit = baseDim.getUnits().orElse(null);

                for (Category c : baseDim.getCategories()) {
                    if (c.isQuantitative()) {
                        builder.addQuantitative(c.getName(), c.getSampleRange(), c.getTransferFunction().orElse(null), unit);
                    } else {
                        builder.addQualitative(c.getName(), c.getSampleRange());
                    }
                }
                builder.setBackground(null, Double.NaN);
                baseDim = builder.build();
                noData = baseDim.getNoDataValues();
                background = baseDim.getBackground();
                sampleDimensions.set(i, baseDim);
                forceTransformedValues = true;
            }

            if (background.isPresent()) {
                this.noData[i] = background.get().doubleValue();
            } else {
                this.noData[i] = noData.iterator().next().doubleValue();
            }
        }

        if (forceTransformedValues) {
            for (int i = 0,n = sampleDimensions.size(); i < n; i++) {
                sampleDimensions.set(i, sampleDimensions.get(i).forConvertedValues(true));
            }
        }

        for (ResourceTile tile : this.tiles) {
            final GridCoverageResource resource = tile.getResource();
            final GridExtent extent = resource.getGridGeometry().getExtent();
            final Point location = tile.getLocation();

            final Point tileXY = new Point(
                    location.x / (int) extent.getSize(0),
                    location.y / (int) extent.getSize(1));
            indexedTiles.put(tileXY, tile);

            if (sampleModel == null) {
                RenderedImage img;
                if (forceTransformedValues) {
                    img = resource.read(null).forConvertedValues(true).render(null);
                } else {
                    img = resource.read(null).render(null);
                }
                sampleModel = img.getSampleModel();
                colorModel = img.getColorModel();
                rasterTemplate = img.getTile(img.getMinTileX(), img.getMinTileY()).createCompatibleWritableRaster(2, 2);
            }
        }

        if (rasterTemplate.getSampleModel().getNumBands() != sampleDimensions.size()) {
            throw new DataStoreException("Raster and sample dimension size differ, raster has " + sampleModel.getNumBands() +" samples has " + sampleDimensions.size());
        }

        if (forceTransformedValues) {
            final BufferedImage imageTemplate = BufferedImages.createImage(sampleModel.getWidth(), sampleModel.getHeight(), sampleDimensions.size(), DataBuffer.TYPE_DOUBLE);
            rasterTemplate = imageTemplate.getTile(0, 0);
            sampleModel = rasterTemplate.getSampleModel();
            colorModel = imageTemplate.getColorModel();
        }

    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return gridGeometry;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return Collections.unmodifiableList(sampleDimensions);
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {

        final TiledCoveragesImage img = new TiledCoveragesImage(sampleModel, colorModel);

        final GridCoverage coverage = new GridCoverageBuilder()
                .setDomain(gridGeometry)
                .setRanges(sampleDimensions)
                .setValues(img)
                .build();

        if (domain != null) {
            final GridDerivation derivate = gridGeometry.derive().rounding(GridRoundingMode.ENCLOSING).subgrid(domain);
            final GridExtent intersection = derivate.getIntersection();

            if (!intersection.equals(gridGeometry.getExtent())) {
                //trye to reduce returned coverage
                final RenderedImage subImage = coverage.render(intersection);

                final GridGeometry subGridGeometry = new GridGeometry(null,
                        PixelInCell.CELL_CENTER,
                        gridGeometry.getGridToCRS(PixelInCell.CELL_CENTER),
                        gridGeometry.getCoordinateReferenceSystem());

                return new GridCoverageBuilder()
                    .setDomain(subGridGeometry)
                    .setRanges(sampleDimensions)
                    .setValues(subImage)
                    .build();
            }
        }
        return coverage;
    }

    public GridCoverageResource[] getTileResources() {
        final GridCoverageResource[] res = new GridCoverageResource[tiles.length];
        for (int i=0;i<tiles.length;i++) {
            res[i] = tiles[i].getResource();
        }
        return res;
    }

    @Override
    public String toString() {
        final List<String> texts = new ArrayList<>();
        int i=0;
        for (Tile vb : tiles) {
            texts.add(((ResourceTile) vb).getResource().toString());
            i++;
            if (i>10) {
                texts.add("... ("+tiles.length+" entries) ...");
                break;
            }
        }
        return StringUtilities.toStringTree("Mosaiced coverage resource", texts);
    }

    static boolean isAllZero(double[] array) {
        for (double d : array) {
            if (d != 0) return false;
        }
        return true;
    }

    /**
     * Try to create mosaics of the different provided coverages.
     */
    public static List<GridCoverageResource> create(GridCoverageResource... resources) throws IOException, DataStoreException {

        final TileOrganizer calculator = new TileOrganizer(null);
        final List<GridCoverageResource> mosaics = new ArrayList<>();

        for (GridCoverageResource resource : resources) {
            try {
                append(resource, calculator);
            } catch (DataStoreException ex) {
                //an error at this place mean the resource doesn't have an affine grid to crs
                //we use the resource as is it defined.
                mosaics.add(resource);
            }
        }

        for (Entry<Tile,Tile[]> entry : calculator.tiles().entrySet()) {

            final Tile[] tiles = entry.getValue();

            //keep tiles with the same subsampling + dimension together
            //we consider different subsampling+dimension as different mosaics
            final Map<Entry,List<GridCoverageResource>> groups = new HashMap<>();

            for (Tile tile : tiles) {
                final Dimension subsampling = tile.getSubsampling();
                final Dimension size = tile.getSize();
                final Entry key = new AbstractMap.SimpleImmutableEntry(subsampling, size);

                List<GridCoverageResource> lst = groups.get(key);
                if (lst == null) {
                    lst = new ArrayList<>();
                    groups.put(key, lst);
                }
                lst.add(((ResourceTile) tile).getResource());
            }

            for (List<GridCoverageResource> lst : groups.values()) {
                if (lst.size() == 1) {
                   mosaics.add(lst.get(0));
                } else {

                    final TileOrganizer calculator2 = new TileOrganizer(null);
                    for (GridCoverageResource resource : lst) {
                        append(resource, calculator2);
                    }
                    Entry<Tile,Tile[]> next = calculator2.tiles().entrySet().iterator().next();
                    Tile group = next.getKey();

                    //set the crs which is missing from the grid geometry
                    CoordinateReferenceSystem crs = ((ResourceTile) next.getValue()[0]).getResource().getGridGeometry().getCoordinateReferenceSystem();
                    final Rectangle r = group.getRegion();
                    GridGeometry grid = new GridGeometry(new GridExtent(null,
                            new long[] {r.x, r.y},
                            new long[] {r.x + r.width, r.y + r.height}, false),
                            PixelInCell.CELL_CENTER, group.getGridToCRS(), crs);

                    mosaics.add(new TiledCoverageResource(grid, next.getValue()));
                }
            }
        }
        return mosaics;
    }

    private static void append(GridCoverageResource r, TileOrganizer c) throws DataStoreException {
        if (r instanceof TiledCoverageResource) {
            TiledCoverageResource mcr = (TiledCoverageResource) r;
            for (ResourceTile t : mcr.tiles) {
                c.add(new ResourceTile(t.getResource()));
            }
        } else {
            c.add(new ResourceTile(r));
        }
    }

    private class TiledCoveragesImage extends ComputedImage {

        private final ColorModel colorModel;

        private TiledCoveragesImage(SampleModel sampleModel, ColorModel colorModel) {
            super(sampleModel);
            this.colorModel = colorModel;
        }

        @Override
        protected Raster computeTile(int tileX, int tileY, WritableRaster previous) throws Exception {
            final int tileWidth = sampleModel.getWidth();
            final int tileHeight = sampleModel.getHeight();
            final int x = tileX * tileWidth;
            final int y = tileY * tileHeight;

            final ResourceTile tile = indexedTiles.get(new Point(tileX, tileY));
            if (tile != null) {
                final GridCoverageResource resource = tile.getResource();
                final GridCoverage coverage = resource.read(null).forConvertedValues(forceTransformedValues);
                final RenderedImage image = coverage.render(null);
                Raster raster = image.getData();
                raster = BufferedImages.makeConform(raster, rasterTemplate);

                //change offset
                if (raster.getMinX() != x || raster.getMinY() != y) {
                    raster = raster.createTranslatedChild(x, y);
                }
                return raster;
            } else {
                //create an empty tile
                final WritableRaster raster = rasterTemplate.createCompatibleWritableRaster(x, y, tileWidth, tileHeight);
                if (!TiledCoverageResource.isAllZero(noData)) BufferedImages.setAll(raster, noData);
                return raster;
            }
        }

        @Override
        public ColorModel getColorModel() {
            return colorModel;
        }

        @Override
        public int getWidth() {
            return Math.toIntExact(gridGeometry.getExtent().getSize(0));
        }

        @Override
        public int getHeight() {
            return Math.toIntExact(gridGeometry.getExtent().getSize(1));
        }

    }

}
