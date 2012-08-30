/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.io.mosaic;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.jai.TiledImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeFactory;
import org.geotoolkit.index.tree.io.TreeVisitor;
import org.geotoolkit.index.tree.nodefactory.NodeFactory;
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
 *
 * @author rmarech
 */
public class PyramidBuilder {

    /**
     * The default tile size in pixels.
     */
    private static final int DEFAULT_TILE_SIZE = 256;

    /**
     * Minimum tile size when using {@link TileLayout#CONSTANT_GEOGRAPHIC_AREA} without
     * explicit subsamplings provided by user.
     */
    private static final int MIN_TILE_SIZE = 64;

    /**
     * Tile size.
     */
    private final Dimension tileSize;

    /**
     * The filename formatter.
     */
    private final FilenameFormatter formatter;


    ////////image attributs//////////////////
    /**
     *
     */
    private final RenderedImage renderedImage;

    private final int rIMinX;
    private final int rIMinY;
    private final int rIWidth;
    private final int rIHeight;
    private final SampleModel sampleMod;
    private final ColorModel colorMod;
    ///////////////////////////////////////////////

    /**
     * Directory path which will contain tile.
     */
    private File tileDirectory = null;

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

    private final int dataType;
    private final int numBand;

    private final Rectangle areaTile = new Rectangle();

    //initialisation RTree
    //pseudo 2D temporal
    private static final CoordinateReferenceSystem TEMPORALCRS     = DefaultTemporalCRS.JAVA;
    private static final CoordinateReferenceSystem CARTESIAN_2DCRS = DefaultEngineeringCRS.CARTESIAN_2D;
    private final CoordinateReferenceSystem crsCompound = new DefaultCompoundCRS("compoundCrs",
            new CoordinateReferenceSystem[] {CARTESIAN_2DCRS, TEMPORALCRS});
    private final Tree hilberTree = TreeFactory.createHilbertRTree(1, 3, crsCompound,TreeNodeFactory.DEFAULT_FACTORY);

    //interpolation attributs
    private final Interpolation interpolation;
    private final double[] fillValue;

    /**
     *
     * @param renderedImage
     * @param coeffX
     * @param coeffY
     * @param tileSize
     * @param interpolationCase
     * @param lanczosWindow
     */
    public PyramidBuilder(RenderedImage renderedImage, double[] coeffX, double[] coeffY,
            Dimension tileSize, String formatName,
            InterpolationCase interpolationCase, int lanczosWindow, double[] fillValue) {
        ArgumentChecks.ensureNonNull("rendered image", renderedImage);
        ArgumentChecks.ensureNonNull("coeffX", coeffX);
        ArgumentChecks.ensureNonNull("coeffY", coeffY);
        this.levelNbre = coeffX.length;
        if (levelNbre != coeffY.length)
            throw new IllegalArgumentException("resampling coeffX and coeffY will be able to have same length");
        //image attributs
        this.renderedImage = renderedImage;
        this.rIMinX = renderedImage.getMinX();
        this.rIMinY = renderedImage.getMinY();
        this.rIWidth = renderedImage.getWidth();
        this.rIHeight = renderedImage.getHeight();
        this.sampleMod = renderedImage.getSampleModel();
        this.dataType = sampleMod.getDataType();
        this.numBand = sampleMod.getNumBands();
        this.colorMod = renderedImage.getColorModel();
        this.formatName = formatName;
        //resampling coefficients
        this.coeffX = coeffX;
        this.coeffY = coeffY;
        this.tileSize = (tileSize == null) ? new Dimension(DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE) : tileSize;
        interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(renderedImage), interpolationCase, lanczosWindow);
        this.fillValue = fillValue;
        formatter = new FilenameFormatter();
        formatter.ensurePrefixSet("tile");
    }

    public void createPyramid() throws NoninvertibleTransformException, TransformException, IOException {
        if (tileDirectory == null)
            throw new IllegalStateException("you must set tile directory path");
        //vider le dossier courant
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
            nbrTX     = resWidth  / tileSize.width;
            //nbre de tuile suivant y
            nbrTY     = resHeight / tileSize.height;
            //si la division n'est pas entiere
            if (resWidth  % tileSize.width  != 0) nbrTX++;
            if (resHeight % tileSize.height != 0) nbrTY++;

            //on resample, ecrit sur le disk et insert dans l'arbre pour chaque tuile
            for (int ity = 0; ity<nbrTY; ity++) {
                int tempMinx = minx;
                for (int itx = 0; itx<nbrTX; itx++) {
                    int tmaxx = Math.min(tempMinx + tileSize.width, maxx);
                    int tmaxy = Math.min(miny + tileSize.height, maxy);
                    //on creer la tuile
                    TiledImage tuile = new TiledImage(tempMinx, miny, tmaxx - tempMinx, tmaxy - miny, tempMinx, miny, sampleMod, colorMod);
                    //on rempli la tuile
                    areaTile.setBounds(tempMinx, miny, tmaxx - tempMinx, tmaxy - miny);
                    PixelIterator pix = PixelIteratorFactory.createRowMajorIterator(wri, areaTile);
                    PixelIterator destPix = PixelIteratorFactory.createRowMajorWriteableIterator(tuile, tuile);
                    while (pix.next()) {
                        destPix.next();
                        destPix.setSample(pix.getSample());
                    }
                    //on genere un nom
                    namePTile = tileDirectory.getPath()+"/";
                    namePTile += formatter.generateFilename(lev, itx, ity)+"."+formatName;
                    //on genere le path
                    File path = new File(namePTile);
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
                    tempMinx += tileSize.width;
                }
                //ligne de tuile suivante
                miny += tileSize.height;
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
     *
     * @return
     */
    public TileLayout getTileLayout(){
        return TileLayout.CONSTANT_TILE_SIZE;
    }

    public RenderedImage getImage(Rectangle bound, int pyramidLevel) {
        GeneralEnvelope regionSearch = new GeneralEnvelope(crsCompound);
        regionSearch.setEnvelope(bound.x, bound.y, pyramidLevel, bound.x+bound.width, bound.y+bound.height, pyramidLevel);
        WritableRenderedImage renderResult = new TiledImage(bound.x, bound.y, bound.width, bound.height, bound.x, bound.y, sampleMod, colorMod);
        TreeVisitor treevisit = new PyramidVisitor(renderResult, bound);
        hilberTree.search(regionSearch, treevisit);
        return renderResult;
    }
}
