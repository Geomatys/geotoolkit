/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.io.mosaicsql;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.media.jai.TiledImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.geotoolkit.coverage.*;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.large.WritableLargeRenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.math.XMath;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.geotoolkit.referencing.operation.DefaultMathTransformFactory;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.processing.Operation;
import org.opengis.feature.type.Name;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

/**
 *
 * @author rmarech
 */
public class PGCoverageBuilder {

    /**
     * The default tile size in pixels.
     */
    private static final int DEFAULT_TILE_SIZE = 256;

    /**
     * Minimum tile size.
     */
    private static final int MIN_TILE_SIZE = 64;

//    private static final int MAX_RESAMPLE = 600;

    /**
     * Tile width.
     */
    private final int tileWidth;

    /**
     * Tile height.
     */
    private final int tileHeight;

    /**
     * Interpolation attributes.
     */
    private final double[] fillValue;

    /**
     * Interpolation properties.
     */
    private final InterpolationCase interpolationCase;
    private final int lanczosWindow;

    private final javax.media.jai.Interpolation resampleInterpolation;

//    private Rectangle globaleArea;
//
//    private GridEnvelope pixelGlobaleArea;

//    private Envelope coverageEnvelope;
//    private CoordinateReferenceSystem coverageCRS;
//    private double[] coverageResolution;
//    private GridCoverageReader coverageReader;
//    private MathTransform coverageGridToCrs;

    public PGCoverageBuilder(Dimension tileSize, InterpolationCase interpolation, int lanczosWindow, double[] fillValue) {
        ArgumentChecks.ensureNonNull("interpolation", interpolation);
        ArgumentChecks.ensureNonNull("fillValue", fillValue);
        ArgumentChecks.ensureStrictlyPositive("lanczosWindow", lanczosWindow);
        if (tileSize == null) {
            tileWidth = tileHeight = DEFAULT_TILE_SIZE;
        } else {
            tileWidth  = Math.min(DEFAULT_TILE_SIZE, Math.max(tileSize.width, MIN_TILE_SIZE));
            tileHeight = Math.min(DEFAULT_TILE_SIZE, Math.max(tileSize.height, MIN_TILE_SIZE));
        }
        switch(interpolation){
            case BICUBIC2 : resampleInterpolation = javax.media.jai.Interpolation.getInstance(javax.media.jai.Interpolation.INTERP_BICUBIC_2);break;
            case BILINEAR : resampleInterpolation = javax.media.jai.Interpolation.getInstance(javax.media.jai.Interpolation.INTERP_BILINEAR);break;
            case NEIGHBOR : resampleInterpolation = javax.media.jai.Interpolation.getInstance(javax.media.jai.Interpolation.INTERP_NEAREST);break;
            default       : resampleInterpolation = javax.media.jai.Interpolation.getInstance(javax.media.jai.Interpolation.INTERP_BICUBIC);break;
        }
        this.interpolationCase = interpolation;
        this.lanczosWindow     = lanczosWindow;
        this.fillValue         = fillValue;
    }

//    /**
//     *
//     * @param reader
//     * @param coverageStore
//     * @param coverageName
//     * @param crsDest
//     * @param pixelScales
//     * @throws CoverageStoreException
//     * @throws DataStoreException
//     * @throws NoninvertibleTransformException
//     * @throws TransformException
//     */
//    public void create(GridCoverageReader reader, CoverageStore coverageStore, Name coverageName, List<CoordinateReferenceSystem> crsDest, double[] pixelScales) throws CoverageStoreException, DataStoreException, NoninvertibleTransformException, TransformException {
//        create(reader.read(0, null), coverageStore, coverageName, crsDest, pixelScales);
//    }


//    /**
//     *
//     * @param gridCoverage
//     * @param coverageStore
//     * @param coverageName
//     * @param crsDest
//     * @param pixelScales
//     * @throws CoverageStoreException
//     * @throws DataStoreException
//     * @throws NoninvertibleTransformException
//     * @throws TransformException
//     */
//    public void create(GridCoverage gridCoverage, CoverageStore coverageStore, Name coverageName, List<CoordinateReferenceSystem> crsDest, double[] pixelScales) throws CoverageStoreException, DataStoreException, NoninvertibleTransformException, TransformException {
//        //pixel scale corespond a la valeur d'un pixel pour x km ou degré
//        //exemple une tuile de 256x256 avec scale 1/100 -> 25600x25600 km
//        final Dimension tileSize = new Dimension(tileWidth, tileHeight);
//
//        GridGeometry gg = gridCoverage.getGridGeometry();
//        if (!(gg instanceof GridGeometry2D)) throw new IllegalArgumentException("GridGeometry not instance of GridGeometry2D");
//
//        CoverageReference cv = coverageStore.create(coverageName);
//        if (!(cv instanceof PyramidalModel)) throw new IllegalArgumentException("CoverageStore parameter not instance of PyramidalModel");
//        PyramidalModel pm = (PyramidalModel) cv;
//
//        ColorModel cm = null;
//
//        //une pyramid par envelope
//        for (CoordinateReferenceSystem crs : crsDest) {
//
//            //on projete le coverage du reader dans le crs courant
//            final Pyramid pyram = pm.createPyramid(crs);
//            final GridCoverage2D gcDest = (GridCoverage2D) Operations.DEFAULT.resample(gridCoverage, crs);
//
//            final GridGeometry2D ggDest2D = (GridGeometry2D) gcDest.getGridGeometry();
//
////            final int[] xyAxis = getXYiD(ggDest2D.getCoordinateReferenceSystem2D());
//
//            final RenderedImage baseImg = gcDest.getRenderedImage();
//
//            if (cm == null) cm = baseImg.getColorModel();
//            final int datatype = cm.getColorSpace().getType();
//
//            final Envelope envDest = ggDest2D.getEnvelope2D();
//
//            final MathTransform2D crsDest_to_gridBase = ggDest2D.getGridToCRS2D().inverse();
//
//            //taille du coverage dans l'espace du crs
//            final double envWidth  = envDest.getSpan(0);
//            final double envHeight = envDest.getSpan(1);
//
//            GeneralDirectPosition upperleft = new GeneralDirectPosition(crs);
//            upperleft.setLocation(envDest.getMinimum(0), envDest.getMaximum(1));
//
//            //une mosaic par niveau d'echelle
//            for (double pixelScal : pixelScales) {
//                //taille de l'image en sortie
//                final int imgWidth  = (int) ((envWidth+pixelScal-1)  / pixelScal);
//                final int imgHeight = (int) ((envHeight+pixelScal-1) / pixelScal);
//                final double sx = envWidth/((double)imgWidth);
//                final double sy = envHeight/((double)imgHeight);
//
//                final MathTransform2D gridDest_to_crs = new AffineTransform2D(sx, 0, 0, -sy, envDest.getMinimum(0), envDest.getMaximum(1));
//
//                final int nbrTileX = (imgWidth  + tileWidth  - 1) / tileWidth;
//                final int nbrTileY = (imgHeight + tileHeight - 1) / tileHeight;
//
//                //Creation de la mosaic
//                final GridMosaic mosaic = pm.createMosaic(pyram.getId(), new Dimension(nbrTileX, nbrTileY), tileSize, upperleft, pixelScal);
//                final String mosaicId = mosaic.getId();
//
//                final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(baseImg), interpolationCase, lanczosWindow);
//
//                for (int cTY = 0; cTY < nbrTileY; cTY++) {
//                    for (int cTX = 0; cTX < nbrTileX; cTX++) {
//                        final int destMinX  = cTX*tileWidth;
//                        final int destMinY  = cTY*tileHeight;
//                        final int cuTWidth  = Math.min(destMinX + tileWidth, imgWidth)-destMinX;
//                        final int cuTHeight = Math.min(destMinY + tileHeight, imgHeight)-destMinY;
//                        final WritableRenderedImage destImg = new BufferedImage(cuTWidth, cuTHeight, datatype);
//
//                        final MathTransform2D tile_to_gridDest = new AffineTransform2D(1, 0, 0, 1, destMinX, destMinY);
//
//                        //translation --> envelope coordinate --> gridBase
//                        //gridbase concat envelope coordinate --> translation
//
//                        final MathTransform mt  = MathTransforms.concatenate(tile_to_gridDest, gridDest_to_crs, crsDest_to_gridBase).inverse();
//                        final Resample resample = new Resample(mt, destImg, interpolation, fillValue);
//                        resample.fillImage();
//                        pm.writeTile(pyram.getId(), mosaicId, cTX, cTY, destImg);
//                    }
//                }
//            }
//        }
//    }

    /**
     *
     * @param reader
     * @param coverageStore
     * @param coverageName
     * @param crsDest
     * @param pixelScales
     * @throws CoverageStoreException
     * @throws DataStoreException
     * @throws NoninvertibleTransformException
     * @throws TransformException
     */
    public void create(GridCoverageReader reader, CoverageStore coverageStore, Name coverageName, List<Envelope> envelopes, double[] pixelScales) throws CoverageStoreException, DataStoreException, NoninvertibleTransformException, TransformException, NoSuchAuthorityCodeException, FactoryException, IOException {
        create(reader.read(0, null), coverageStore, coverageName, envelopes, pixelScales);
    }

    /**
     *
     * @param gridCoverage
     * @param coverageStore
     * @param coverageName
     * @param crsDest
     * @param pixelScales
     * @throws CoverageStoreException
     * @throws DataStoreException
     * @throws NoninvertibleTransformException
     * @throws TransformException
     */
    public void create(GridCoverage gridCoverage, CoverageStore coverageStore, Name coverageName, List<Envelope> envelopes, double[] pixelScales)
            throws CoverageStoreException, DataStoreException, NoninvertibleTransformException,
            TransformException, NoSuchAuthorityCodeException, FactoryException, IOException {
        //pixel scale corespond a la valeur d'un pixel pour x km ou degré
        //exemple une tuile de 256x256 avec scale 1/100 -> 25600x25600 km
        final Dimension tileSize = new Dimension(tileWidth, tileHeight);

        GridGeometry gg = gridCoverage.getGridGeometry();
        if (!(gg instanceof GridGeometry2D)) throw new IllegalArgumentException("GridGeometry not instance of GridGeometry2D");

        CoverageReference cv = coverageStore.create(coverageName);
        if (!(cv instanceof PyramidalModel)) throw new IllegalArgumentException("CoverageStore parameter not instance of PyramidalModel");
        PyramidalModel pm = (PyramidalModel) cv;

        ColorModel cm = null;

        final CoordinateReferenceSystem coverageCRS = gridCoverage.getCoordinateReferenceSystem();

        //une pyramid par envelope
        for (Envelope envDest : envelopes) {

            CoordinateReferenceSystem crs = envDest.getCoordinateReferenceSystem();

            if (!checkDomain(crs, envDest))
                throw new IllegalArgumentException("impossible to resample coverage in this area. Envelope not within crs validity domaine."+envDest.toString());

            //on projete le coverage du reader dans le crs courant
            final Pyramid pyram = pm.createPyramid(crs);
//            final GridCoverage2D gcDest = (GridCoverage2D) Operations.DEFAULT.resample(gridCoverage, envDest, resampleInterpolation);
            final GridCoverage2D gcDest = (GridCoverage2D) Operations.DEFAULT.resample(gridCoverage, crs);

            final GridGeometry2D ggDest2D = (GridGeometry2D) gcDest.getGridGeometry();

            //init
            MathTransform2D crsDest_to_gridBase = ggDest2D.getGridToCRS2D().inverse();
            RenderedImage baseImg = gcDest.getRenderedImage();
            if (cm == null) cm = baseImg.getColorModel();
            final int datatype = cm.getColorSpace().getType();

            ///test
            double det = 0;

            //on verifie que le grid to crs est inversible
            if (crsDest_to_gridBase instanceof AffineTransform) {
                det = ((AffineTransform)crsDest_to_gridBase).getDeterminant();
            } else if (crsDest_to_gridBase instanceof LinearTransform) {
                ((LinearTransform)crsDest_to_gridBase).getMatrix();
                det = Matrices.toAffineTransform(((LinearTransform)crsDest_to_gridBase).getMatrix()).getDeterminant();
            }

            // s'il n'est pas inversible on va chercher l'image dans le crs de depart
            if (det == 0 || Double.isNaN(det)||Double.isInfinite(det)) {
                Envelope env = Envelopes.transform(envDest, coverageCRS);
                MathTransform2D covCRStoGrid = ((GridGeometry2D)gg).getGridToCRS2D().inverse();
                Envelope bound = Envelopes.transform(covCRStoGrid, env);
                //normalement on est en coordonnées pixels dans le crs du coverage
                int minx   = (int) Math.round(bound.getMinimum(0));
                int miny   = (int) Math.round(bound.getMinimum(1));
                int width  = (int) Math.round(bound.getSpan(0));
                int height = (int) Math.round(bound.getSpan(1));

                //recopie du morceau de l'image interessé
                PixelIterator pix = PixelIteratorFactory.createRowMajorIterator(baseImg, new Rectangle(minx, miny, width, height));
                WritableLargeRenderedImage wlri = new WritableLargeRenderedImage(minx, miny, width, height, new Dimension(baseImg.getTileWidth(), baseImg.getTileHeight()), minx,miny, cm);
                PixelIterator copix = PixelIteratorFactory.createRowMajorWriteableIterator(wlri, wlri);
                while(pix.next()){
                    copix.next();
                    copix.setSampleDouble(pix.getSampleDouble());
                }
                //creation du coverage adapté
                GridCoverageBuilder gcb = new GridCoverageBuilder();
                gcb.setCoordinateReferenceSystem(coverageCRS);
                gcb.setEnvelope(env);
                gcb.setRenderedImage(wlri);
                GridCoverage2D gc2d = (GridCoverage2D) Operations.DEFAULT.resample(gcb.getGridCoverage2D(), crs);
                crsDest_to_gridBase = gc2d.getGridGeometry().getGridToCRS2D().inverse();
//                baseImg = gc2d.getRenderedImage();
                baseImg = wlri;
                final RenderedImage baseimg2 = wlri;

//                ImageIO.write(baseimg2, "tiff", new File("/home/rmarech/Documents/image/test.tiff"));
                        ////////////////////////////// affichage /////////////////////////////
                final JFrame frm = new JFrame();
                JPanel jp = new JPanel() {

                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);

                        Graphics2D g2 = (Graphics2D) g;
                        g2.setTransform(new AffineTransform2D(1, 0, 0, 1, (this.getWidth() - baseimg2.getWidth()) / 2.0 - baseimg2.getMinX(), (this.getHeight() - baseimg2.getHeight()) / 2.0 - baseimg2.getMinY()));
                        g2.drawRenderedImage(baseimg2, new AffineTransform2D(1, 0, 0, 1, 0, 0));
                    }
                };
                frm.setTitle("tuiles");
                frm.setSize(baseimg2.getWidth(), baseimg2.getHeight());
                frm.setLocationRelativeTo(null);
                frm.add(jp);
                frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frm.setVisible(true);
                ///////////////////////////// affichage ////////////////////////////////

            }

            //taille du coverage dans l'espace du crs
            final double envWidth  = envDest.getSpan(0);
            final double envHeight = envDest.getSpan(1);

            GeneralDirectPosition upperleft = new GeneralDirectPosition(crs);
            upperleft.setLocation(envDest.getMinimum(0), envDest.getMaximum(1));

            //une mosaic par niveau d'echelle
            for (double pixelScal : pixelScales) {
                //taille de l'image en sortie
                final int imgWidth  = (int) ((envWidth+pixelScal-1)  / pixelScal);
                final int imgHeight = (int) ((envHeight+pixelScal-1) / pixelScal);
                final double sx = envWidth/((double)imgWidth);
                final double sy = envHeight/((double)imgHeight);

                final MathTransform2D gridDest_to_crs = new AffineTransform2D(sx, 0, 0, -sy, envDest.getMinimum(0), envDest.getMaximum(1));

                final int nbrTileX = (imgWidth  + tileWidth  - 1) / tileWidth;
                final int nbrTileY = (imgHeight + tileHeight - 1) / tileHeight;

                //Creation de la mosaic
                final GridMosaic mosaic = pm.createMosaic(pyram.getId(), new Dimension(nbrTileX, nbrTileY), tileSize, upperleft, pixelScal);
                final String mosaicId = mosaic.getId();

                final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(baseImg), interpolationCase, lanczosWindow);

                for (int cTY = 0; cTY < nbrTileY; cTY++) {
                    for (int cTX = 0; cTX < nbrTileX; cTX++) {
                        final int destMinX  = cTX*tileWidth;
                        final int destMinY  = cTY*tileHeight;
                        final int cuTWidth  = Math.min(destMinX + tileWidth, imgWidth)-destMinX;
                        final int cuTHeight = Math.min(destMinY + tileHeight, imgHeight)-destMinY;
                        final WritableRenderedImage destImg = new BufferedImage(cuTWidth, cuTHeight, datatype);

                        final MathTransform2D tile_to_gridDest = new AffineTransform2D(1, 0, 0, 1, destMinX, destMinY);

                        //translation --> envelope coordinate --> gridBase
                        //gridbase concat envelope coordinate --> translation

                        final MathTransform mt  = MathTransforms.concatenate(tile_to_gridDest, gridDest_to_crs, crsDest_to_gridBase).inverse();
                        final Resample resample = new Resample(mt, destImg, interpolation, fillValue);
                        resample.fillImage();
                        pm.writeTile(pyram.getId(), mosaicId, cTX, cTY, destImg);
                    }
                }
            }
        }
    }

    private boolean checkDomain(CoordinateReferenceSystem crs, Envelope envelope) throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        final CoordinateReferenceSystem crstest = CRS.decode("CRS:84");
        final GeographicBoundingBox gbb = CRS.getGeographicBoundingBox(crs);
        final GeneralEnvelope bb = new GeneralEnvelope(crstest);
        bb.setEnvelope(gbb.getWestBoundLongitude(), gbb.getSouthBoundLatitude(), gbb.getEastBoundLongitude(), gbb.getNorthBoundLatitude());
        return bb.contains(Envelopes.transform(envelope, crstest), true);
    }

//    /**
//      * Find x axis and y axis dimension index ordinate from {@link CoordinateReferenceSystem}.
//      *
//      * @param crs {@link CoordinateReferenceSystem} where find ordinate.
//      * @return double table of length 2 where table[0] = X axis ordinate
//      * and table[1] = Y axis ordinate.
//      */
//     private int[] getXYiD(CoordinateReferenceSystem crs) {
//        final int[] xy = new int[]{-1, -1};
//        if (crs instanceof CompoundCRS) {
//            int tempOrdinate = 0;
//            for (CoordinateReferenceSystem crss : ((CompoundCRS) crs).getComponents()) {
//                final CoordinateSystem cs = crss.getCoordinateSystem();
//                final int csDim = cs.getDimension();
//                for (int cd = 0; cd < csDim; cd++) {
//                    final CoordinateSystemAxis csa = cs.getAxis(cd);
//                    final AxisDirection ad = csa.getDirection();
//                    if (ad.compareTo(AxisDirection.EAST) == 0 || ad.compareTo(AxisDirection.WEST) == 0) {
//                        xy[0] = cd + tempOrdinate;
//                    } else if (ad.compareTo(AxisDirection.NORTH) == 0 || ad.compareTo(AxisDirection.SOUTH) == 0) {
//                        xy[1] = cd + tempOrdinate;
//                    }
//                }
//                tempOrdinate += csDim;
//            }
//        } else {
//            final CoordinateSystem cs = crs.getCoordinateSystem();
//            final int csDim = cs.getDimension();
//            for (int cd = 0; cd < csDim; cd++) {
//                final CoordinateSystemAxis csa = cs.getAxis(cd);
//                final AxisDirection ad = csa.getDirection();
//                if (ad.compareTo(AxisDirection.EAST) == 0 || ad.compareTo(AxisDirection.WEST) == 0) {
//                    xy[0] = cd;
//                } else if (ad.compareTo(AxisDirection.NORTH) == 0 || ad.compareTo(AxisDirection.SOUTH) == 0) {
//                    xy[1] = cd;
//                }
//            }
//        }
//        if (xy[0] == -1 || xy[1] == -1)
//            throw new IllegalArgumentException("appropriate system axis not find");
//        return xy;
//    }

}
