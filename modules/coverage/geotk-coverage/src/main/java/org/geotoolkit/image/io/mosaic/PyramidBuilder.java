/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.image.io.mosaic;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.jai.TiledImage;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.LanczosInterpolation;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeFactory;
import org.geotoolkit.index.tree.io.TreeVisitor;
import org.geotoolkit.index.tree.nodefactory.TreeNodeFactory;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

/**
 * Construct and write from a defined path, a "pyramid" of tiles from given {@code Rendered image}.
 *
 * @author Remi Marechal (Geomatys).
 */
public class PyramidBuilder {

    /**
     * The default tile size in pixels.
     */
    private static final int DEFAULT_TILE_SIZE = 256;

    /**
     * Minimum tile size.
     */
    private static final int MIN_TILE_SIZE = 64;

    /**
     * Tile width.
     */
    private final int tileWidth;

    /**
     * Tile height.
     */
    private final int tileHeight;

    /**
     * The filename formatter.
     */
    private final FilenameFormatter formatter;

    /**
     * Pyramid base {@code RenderedImage}.
     */
    private final RenderedImage renderedImage;

    /**
     * {@code RenderedImage} lower corner min x value.
     */
    private final int rIMinX;

    /**
     * {@code RenderedImage} lower corner min y value.
     */
    private final int rIMinY;

    /**
     * {@code RenderedImage} width.
     */
    private final int rIWidth;

    /**
     * {@code RenderedImage} height.
     */
    private final int rIHeight;

    /**
     * {@code RenderedImage} {@code SampleModel}.
     */
    private final SampleModel sampleMod;

    /**
     * {@code RenderedImage} {@code ColorModel}.
     */
    private final ColorModel colorMod;

    /**
     * Directory path which will contain tile.
     */
    private File tileDirectory = null;

    /**
     * Written tile extention.
     */
    private final String formatName;

    /**
     * Resampling coefficient in X direction.
     */
    private final double[] coeffX;

    /**
     * Resampling coefficient in Y direction.
     */
    private final double[] coeffY;

    /**
     * Pyramid level.
     */
    private final int levelNbre;

    /**
     * Temporary tile area iterate.
     */
    private final Rectangle areaTile = new Rectangle();

    /**
     * RTree attributes.
     */
    private static final CoordinateReferenceSystem TEMPORALCRS     = DefaultTemporalCRS.JAVA;
    private static final CoordinateReferenceSystem CARTESIAN_2DCRS = DefaultEngineeringCRS.CARTESIAN_2D;
    private final CoordinateReferenceSystem crsCompound = new DefaultCompoundCRS("compoundCrs",
            new CoordinateReferenceSystem[] {CARTESIAN_2DCRS, TEMPORALCRS});
    private final Tree hilberTree = TreeFactory.createHilbertRTree(1, 3, crsCompound,TreeNodeFactory.DEFAULT_FACTORY);

    /**
     * Interpolation attributes.
     */
    private final Interpolation interpolation;
    private final double[] fillValue;

    /**
     * <p>Construct a pyramid of tiles, from {@code RenderedImage}.<br/>
     *
     * Pyramid level number is given by {@code coeffx} and {@code coeffy} length.<br/>
     *
     * Note : if Lanczos interpolation isn't choose, {@code Lanczoswindow} parameter has no impact.</p>
     *
     * @param renderedImage image which will be pyramid.
     * @param coeffX Transformation in x direction for each pyramid level.
     * @param coeffY Transformation in y direction for each pyramid level.
     * @param tileSize Result tile size.
     * @param interpolationCase Interpolation type for pyramid.
     * @param lanczosWindow lanczos window for Lanczos interpolation.
     * @param fillValue
     * @see LanczosInterpolation
     * @see Resample
     */
    public PyramidBuilder(final RenderedImage renderedImage, final double[] coeffX, final double[] coeffY,
            final Dimension tileSize, final String formatName,
            final InterpolationCase interpolationCase, final int lanczosWindow, final double[] fillValue) {
        ArgumentChecks.ensureNonNull("rendered image", renderedImage);
        ArgumentChecks.ensureNonNull("coeffX", coeffX);
        ArgumentChecks.ensureNonNull("coeffY", coeffY);
        this.levelNbre = coeffX.length;
        if (levelNbre != coeffY.length)
            throw new IllegalArgumentException("resampling coeffX and coeffY will be able to have same length");
        //image attributs
        this.renderedImage = renderedImage;
        this.rIMinX        = renderedImage.getMinX();
        this.rIMinY        = renderedImage.getMinY();
        this.rIWidth       = renderedImage.getWidth();
        this.rIHeight      = renderedImage.getHeight();
        this.sampleMod     = renderedImage.getSampleModel();
        this.colorMod      = renderedImage.getColorModel();
        this.formatName    = formatName;
        //resampling coefficients
        this.coeffX = coeffX;
        this.coeffY = coeffY;
        //tile attributs
        this.tileWidth  = (tileSize == null) ? DEFAULT_TILE_SIZE : (tileSize.width  < MIN_TILE_SIZE) ? MIN_TILE_SIZE : tileSize.width;
        this.tileHeight = (tileSize == null) ? DEFAULT_TILE_SIZE : (tileSize.height < MIN_TILE_SIZE) ? MIN_TILE_SIZE : tileSize.height;

        interpolation   = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(renderedImage), interpolationCase, lanczosWindow);
        this.fillValue  = fillValue;
        formatter       = new FilenameFormatter();
        formatter.ensurePrefixSet("tile");
    }

    /**
     * Construct and write pyramid.
     *
     * @throws NoninvertibleTransformException if can't resample image.
     * @throws TransformException if can't resample image.
     * @throws IOException if can't write tile.
     */
    public void createPyramid() throws NoninvertibleTransformException, TransformException, IOException {
        if (tileDirectory == null)
            throw new IllegalStateException("you must set tile directory path");
        /**
         * Clean destination directory.
         */
        File[] tabfil = tileDirectory.listFiles();
        for (File f : tabfil) {
            f.delete();
        }

        MathTransform mt;
        Resample resample;
        RenderedImage wri;
        PyramidTile pTile;
        String namePTile;
        GeneralEnvelope pTileEnvelop;
        int minx,miny, maxx, maxy, resWidth, resHeight, nbrTX, nbrTY;
        for (int lev = 0; lev<levelNbre; lev++) {
            //creation du mathTransform
            mt        = new AffineTransform2D(coeffX[lev], 0, 0, coeffY[lev], 0, 0);
            //creation renderedimage boundary de rendu
            minx      = (int) (rIMinX   * coeffX[lev]);
            miny      = (int) (rIMinY   * coeffY[lev]);
            resWidth  = (int) (rIWidth  * coeffX[lev]);
            resHeight = (int) (rIHeight * coeffY[lev]);
            maxx = minx + resWidth;
            maxy = miny + resHeight;

            if (coeffX[lev] == 1 && coeffY[lev] == 1) {
                //si transformation identité
                wri = renderedImage;
            } else {
                // c'est ici ke tu dois resampler
                wri = new TiledImage(minx, miny, resWidth, resHeight, minx, miny, sampleMod, colorMod);
                //on resample
                resample  = new Resample(mt,(WritableRenderedImage) wri, interpolation, fillValue);
                //on rempli l'image
                resample.fillImage();
            }

            //nbre de tuile suivant x
            nbrTX     = resWidth  / tileWidth;
            //nbre de tuile suivant y
            nbrTY     = resHeight / tileHeight;
            //si la division n'est pas entiere
            if (resWidth  % tileWidth  != 0) nbrTX++;
            if (resHeight % tileHeight != 0) nbrTY++;

            //on resample, ecrit sur le disk et insert dans l'arbre pour chaque tuile
            for (int ity = 0; ity<nbrTY; ity++) {
                int tempMinx = minx;
                for (int itx = 0; itx<nbrTX; itx++) {
                    final int tmaxx = Math.min(tempMinx + tileWidth, maxx);
                    final int tmaxy = Math.min(miny + tileHeight, maxy);
                    //on creer la tuile
                    TiledImage tuile = new TiledImage(tempMinx, miny, tmaxx - tempMinx, tmaxy - miny, tempMinx, miny, sampleMod, colorMod);
                    //on rempli la tuile
                    areaTile.setBounds(tempMinx, miny, tmaxx - tempMinx, tmaxy - miny);
                    //optimisation : faire un seul iterateur pour recopier
                    final PixelIterator pix = PixelIteratorFactory.createRowMajorIterator(wri, areaTile);
                    final PixelIterator destPix = PixelIteratorFactory.createRowMajorWriteableIterator(tuile, tuile);
                    while (pix.next()) {
                        destPix.next();
                        destPix.setSample(pix.getSample());
                    }
                    //on genere un nom
                    namePTile = tileDirectory.getPath()+"/";
                    namePTile += formatter.generateFilename(lev, itx, ity)+"."+formatName;
                    //on genere le path
                    final File path = new File(namePTile);
                    //on ecrit sur disk
                    ImageIO.write(tuile, formatName, new File(namePTile));
                    //on genere un envelope de pyramidtile
                    pTileEnvelop = new GeneralEnvelope(crsCompound);
                    pTileEnvelop.setEnvelope(tempMinx, miny, lev, tmaxx, tmaxy, lev);
                    //on creer le pyramidtile
                    pTile = new PyramidTile(path, pTileEnvelop);
                    //on insert dans l'arbre pyramidtile
                    hilberTree.insert(pTile);
                    //tuile suivante en x
                    tempMinx += tileWidth;
                }
                //ligne de tuile suivante
                miny += tileHeight;
            }
        }
    }

    /**
     * <p>Sets the directory where tiles will be read or written.<br/>
     * May be a relative or absolute path, or null (the default) for current directory.</p>
     *
     * @param directory directory path which will contain tile(s).
     */
    public void setTileDirectory(File directory) {
        this.tileDirectory = directory;
        //on check si les dossiers existes
        if (!tileDirectory.exists()) {
            tileDirectory.mkdirs();
        }
    }

    /**
     * Return format tile size.
     *
     * @return format tile size.
     */
    public TileLayout getTileLayout(){
        return TileLayout.CONSTANT_TILE_SIZE;
    }

    /**
     * Construct and fill a {@code RenderedImage} of boundary parameter size
     * from pyramid tile at pyramidLevel parameter level.
     *
     * @param boundary RenderedImage result boundary.
     * @param pyramidLevel pyramid level.
     * @return result {@code RenderedImage}.
     */
    public RenderedImage getImage(Rectangle boundary, int pyramidLevel) {
        final int brx = boundary.x;
        final int bry = boundary.y;
        final int brw = boundary.width;
        final int brh = boundary.height;
        final GeneralEnvelope regionSearch = new GeneralEnvelope(crsCompound);
        regionSearch.setEnvelope(brx, bry, pyramidLevel, brx + brw, bry + brh, pyramidLevel);
        final WritableRenderedImage renderResult = new TiledImage(brx, bry, brw, brh, brx, bry, sampleMod, colorMod);
        final TreeVisitor treevisit = new PyramidVisitor(renderResult, boundary);
        hilberTree.search(regionSearch, treevisit);
        return renderResult;
    }
}
