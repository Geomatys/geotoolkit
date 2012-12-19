/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.io.mosaicsql;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.media.jai.TiledImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.geotoolkit.coverage.*;
import org.geotoolkit.coverage.grid.GridCoverage2D;
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
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
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
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

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

    private static final int MAX_RESAMPLE = 600;

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

//    private Rectangle globaleArea;
//
//    private GridEnvelope pixelGlobaleArea;

    private Envelope coverageEnvelope;
    private CoordinateReferenceSystem coverageCRS;
    private double[] coverageResolution;
    private GridCoverageReader coverageReader;
    private MathTransform coverageGridToCrs;

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
        this.interpolationCase = interpolation;
        this.lanczosWindow     = lanczosWindow;
        this.fillValue         = fillValue;
    }


//    public void create(GridCoverageReader reader, CoverageStore coverageStore, Name coverageName, List<Envelope> pyramids, double[] pixelScales) throws CoverageStoreException, DataStoreException, NoninvertibleTransformException, TransformException {
//        //pixel scale corespond a la valeur d'un pixel pour x km ou degré
//        //exemple une tuile de 256x256 avec scale 1/100 -> 25600x25600 km
//        Dimension tileSize = new Dimension(tileWidth, tileHeight);
//        GridCoverage gc = reader.read(0, null);
//        GridGeometry gg = reader.getGridGeometry(0);
//        if (!(gg instanceof GridGeometry2D)) throw new IllegalArgumentException("GridGeometry not instance of GridGeometry2D");
//        GridGeometry2D gg2d = (GridGeometry2D)gg;
//
//        coverageCRS         = gg2d.getCoordinateReferenceSystem2D();
//        coverageResolution  = gg2d.getResolution();
//        coverageEnvelope    = gg2d.getEnvelope2D();
//        coverageReader      = reader;
//        coverageGridToCrs   = gg2d.getGridToCRS();
//
//        CoverageReference cv = coverageStore.create(coverageName);
//        if (!(cv instanceof PyramidalModel)) throw new IllegalArgumentException("CoverageStore parameter not instance of PyramidalModel");
//        PyramidalModel pm = (PyramidalModel) cv;
//
//        ColorModel cm = null;
//
//        //une pyramid par envelope
//        for (Envelope pyramidBase : pyramids) {
//            CoordinateReferenceSystem crsDest = pyramidBase.getCoordinateReferenceSystem();
//            //on projete le coverage du reader dans le crs courant
//            Pyramid pyram = pm.createPyramid(crsDest);
//
//            //Envelope dans le crs du coverage
//            Envelope baseCoverage = Envelopes.transform(pyramidBase, coverageCRS);
//
//            //penser a faire une intersection
//
//            Envelope pixelBaseCoverage = Envelopes.transform(coverageGridToCrs.inverse(), baseCoverage);
//
//            //zone iteration
////            DirectPosition lowerPixel = coverageGridToCrs.inverse().transform(baseCoverage.getLowerCorner(), null);
////            DirectPosition upperPixel = coverageGridToCrs.inverse().transform(baseCoverage.getUpperCorner(), null);
//
//            int baseGridMinX  = (int) Math.round(pixelBaseCoverage.getMinimum(0));
//            int baseGridMinY  = (int) Math.round(pixelBaseCoverage.getMinimum(1));
//            int baseGridMaxX  = (int) Math.round(pixelBaseCoverage.getMaximum(0));
//            int baseGridMaxY  = (int) Math.round(pixelBaseCoverage.getMaximum(1));
//            int baseGridSpanX = baseGridMaxX - baseGridMinX;
//            int baseGridSpanY = baseGridMaxY - baseGridMinY;
//
//
//            //modifier tou ca a propos du color model !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//            GridGeometry2D ggDest2D = (GridGeometry2D) gc.getGridGeometry();
//
//            final int[] xyAxis = getXYiD(ggDest2D.getCoordinateReferenceSystem2D());
//
//            if (cm == null) cm = gc.getRenderableImage(xyAxis[0], xyAxis[1]).createDefaultRendering().getColorModel();
//
//            //taille du coverage dans l'espace du crs
//            final double envWidth  = pyramidBase.getSpan(xyAxis[0]);//ok taille de l'envelope resultat
//            final double envHeight = pyramidBase.getSpan(xyAxis[1]);
//
//
//            GeneralDirectPosition upperleft = new GeneralDirectPosition(crsDest);
//            upperleft.setLocation(pyramidBase.getMinimum(xyAxis[0]), pyramidBase.getMaximum(xyAxis[1]));
//
//            //une mosaic par niveau d'echelle
//            for (double pixelScal : pixelScales) {
//
//                //taille de l'image en sortie
//                int imgWidth  = (int) (envWidth  / pixelScal);
//                int imgHeight = (int) (envHeight / pixelScal);
//
//                //definir la taille d'une tuile dans la baseGrid pixel
//                int tileSizeX = (int) ((((double) baseGridSpanX) / imgWidth)  * tileWidth);
//                int tileSizeY = (int) ((((double) baseGridSpanY) / imgHeight) * tileHeight);
//
//                double rastSpanX = tileWidth  * pixelScal;//////
//                double rastSpanY = tileHeight * pixelScal;/////
//
////                //nbre tuile
////                int nbrTileX = (int) ((pyramidBase.getSpan(0)  + rastSpanX  - 1) / rastSpanX);
////                int nbrTileY = (int) ((pyramidBase.getSpan(1)  + rastSpanY  - 1) / rastSpanY);
//
////                //nbre tuile
////                int nbrTileX = (int) ((imgWidth  + tileWidth  - 1) / tileWidth);
////                int nbrTileY = (int) ((imgHeight  + tileHeight  - 1) / tileHeight);
//
//                //nbre tuile
//                int nbrTileX = (int) ((baseGridSpanX + tileSizeX - 1) / tileSizeX);
//                int nbrTileY = (int) ((baseGridSpanY + tileSizeY - 1) / tileSizeY);
//
//                //Creation de la mosaic
//                Dimension gridSize = new Dimension(nbrTileX, nbrTileY);
////                GridMosaic mosaic = pm.createMosaic(pyram.getId(), gridSize, tileSize, pyramidBase.getLowerCorner(), pixelScal);
//                GridMosaic mosaic = pm.createMosaic(pyram.getId(), gridSize, tileSize, upperleft, pixelScal);
//                String mosaicId = mosaic.getId();
//                SampleModel sm = null;
//
//                GeneralEnvelope coverageTile = new GeneralEnvelope(coverageCRS);
//                Envelope tileDest;
//
//
//                for (int cTY = 0; cTY < nbrTileY; cTY++) {
//                    for (int cTX = 0; cTX < nbrTileX; cTX++) {
//
////                        if (sm == null) sm = cm.createCompatibleSampleModel(tileWidth, tileHeight);//a voir pour derniere tuile
//
////                        int destMinX = cTX*tileWidth;
////                        int destMinY = cTY*tileHeight;
////                        int cuTWidth = Math.min(destMinX + tileWidth, imgWidth);
////                        int cuTHeight = Math.min(destMinY + tileHeight, imgHeight);
//
//                        //attention on est dans l'espace pixel du grib
//                        int destMinX = baseGridMinX + cTX * tileSizeX;
//                        int destMinY = baseGridMinY + cTY * tileSizeY;
//                        int cuTWidth = Math.min(destMinX + tileSizeX, baseGridMaxX);
//                        int cuTHeight = Math.min(destMinY + tileSizeY, baseGridMaxY);
//
//                        //on passe dans crs
//                        GeneralEnvelope env = new GeneralEnvelope(coverageCRS);
//                        env.setEnvelope(destMinX, destMinY, cuTWidth, cuTHeight);
//                        coverageTile = Envelopes.transform(coverageGridToCrs, env);
//
//                        //on l'envoi dans le crs destination
//                        tileDest = Envelopes.transform(coverageTile, crsDest);
//
//                        int mx = cTX * tileWidth;
//                        int my = cTY * tileHeight;
//                        int max = Math.min(mx+tileWidth, imgWidth);
//                        int may = Math.min(my+tileHeight, imgHeight);
//
//                        sm = cm.createCompatibleSampleModel(max-mx, may-my);//a voir pour derniere tuile
//
//                        WritableRenderedImage destImg = new TiledImage(0, 0, max-mx, may-my, 0, 0, sm, cm);
//
//
////                        GeneralEnvelope env = new GeneralEnvelope(crsDest);
////                        double envMinX = pyramidBase.getMinimum(0) + cTX*rastSpanX;
////                        double envMinY = pyramidBase.getMinimum(1) + cTY*rastSpanY;
////                        double envMaxX = Math.min(envMinX+rastSpanX, pyramidBase.getMaximum(0));
////                        double envMaxY = Math.min(envMinY+rastSpanY, pyramidBase.getMaximum(1));
////                        env.setEnvelope(envMinX, envMinY, envMaxX, envMaxY);
//
//                        fillRaster(destImg, tileDest, new Rectangle(0, 0, max-mx, may-my));
//
//                        pm.writeTile(pyram.getId(), mosaicId, cTX, cTY/*nbrTileY-cTY-1*/, destImg);
//                    }
//                }
//            }
//
//        }
//    }
//
//    private void fillRaster(WritableRenderedImage raster, Envelope envelopeRaster, Rectangle resampleArea) throws TransformException, CoverageStoreException {
//
//        Envelope rastincov = Envelopes.transform(envelopeRaster, coverageCRS);
//        //quelle taille en pixel represente mon raster sur le coverage de base.
//        final int covWidth  = (int) (rastincov.getSpan(0)/coverageResolution[0]);//voir avec xyAxis
//        final int covHeight = (int) (rastincov.getSpan(1)/coverageResolution[1]);
//
//        //faire un if de plus au cas ou les subdivisions sont aussi petite qu'un pixel
//        if (covWidth > MAX_RESAMPLE || covHeight > MAX_RESAMPLE) {
//            CoordinateReferenceSystem rastCRS =  envelopeRaster.getCoordinateReferenceSystem();
//            final int minRX = resampleArea.x;
//            final int minRY = resampleArea.y;
//            final int demiRW = (resampleArea.width+1)/2;
//            final int demiRH = (resampleArea.height+1)/2;
//            final int medRX = minRX+demiRW;
//            final int medRY = minRY+demiRH;
//            GeneralEnvelope lowerLeft  = new GeneralEnvelope(rastCRS);
//            GeneralEnvelope upperLeft  = new GeneralEnvelope(rastCRS);
//            GeneralEnvelope lowerRight = new GeneralEnvelope(rastCRS);
//            GeneralEnvelope upperRight = new GeneralEnvelope(rastCRS);
//
//            //bas gauche
//            lowerLeft.setEnvelope(envelopeRaster.getMinimum(0),envelopeRaster.getMinimum(1), envelopeRaster.getMedian(0),envelopeRaster.getMedian(1));
//            Rectangle rectLL = new Rectangle(minRX, minRY, demiRW, demiRH);
//            //haut gauche
//            upperLeft.setEnvelope(envelopeRaster.getMinimum(0),envelopeRaster.getMedian(1), envelopeRaster.getMedian(0), envelopeRaster.getMaximum(1));
//            Rectangle rectUL = new Rectangle(minRX, medRY,demiRW, demiRH);
//            //bas droite
//            lowerRight.setEnvelope(envelopeRaster.getMedian(0),envelopeRaster.getMinimum(1), envelopeRaster.getMaximum(0), envelopeRaster.getMedian(1));
//            Rectangle rectLR = new Rectangle(medRX, minRY, demiRW, demiRH);
//            //haut droite
//            upperRight.setEnvelope(envelopeRaster.getMedian(0), envelopeRaster.getMedian(1), envelopeRaster.getMaximum(0), envelopeRaster.getMaximum(1));
//            Rectangle rectUR = new Rectangle(medRX, medRY, demiRW, demiRH);
//            fillRaster(raster, lowerLeft, rectLL);
//            fillRaster(raster, upperLeft, rectUL);
//            fillRaster(raster, lowerRight, rectLR);
//            fillRaster(raster, upperRight, rectUR);
//        } else {
//            //remplissage raster
//            //gridcoverageparamreader envelope raster
//            GridCoverageReadParam gcrp = new GridCoverageReadParam();
//            gcrp.setEnvelope(rastincov);
//            GridCoverage2D grid = (GridCoverage2D) coverageReader.read(0, gcrp);
//            final RenderedImage image = grid.getRenderedImage();
//
//
//            final JFrame frm = new JFrame();
//            JPanel jp = new JPanel() {
//
//                @Override
//                protected void paintComponent(Graphics g) {
//                    super.paintComponent(g);
//
//                    Graphics2D g2 = (Graphics2D) g;
//                    g2.setTransform(new AffineTransform2D(1, 0, 0, 1, (this.getWidth() - image.getWidth()) / 2.0 - image.getMinX(), (this.getHeight() - image.getHeight()) / 2.0 - image.getMinY()));
//                    g2.drawRenderedImage(image, new AffineTransform2D(1, 0, 0, 1, 0, 0));
//                }
//            };
//            frm.setTitle("tuiles");
//            frm.setSize(image.getWidth(), image.getHeight());
//            frm.setLocationRelativeTo(null);
//            frm.add(jp);
//            frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frm.setVisible(true);
//
//            //afficher l'image img source pour voir le rendu
//            MathTransform mt = new AffineTransform2D((double)image.getWidth()/raster.getWidth(), 0, 0, (double)image.getHeight()/raster.getHeight(), 0, 0);
//            final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(image), interpolationCase, lanczosWindow);
//            Resample resample = new Resample(mt, raster, resampleArea, interpolation, fillValue);
//            resample.fillImage();
//            //afficher le rendu  du raster pour voir
//        }
//    }



    public void create(GridCoverageReader reader, CoverageStore coverageStore, Name coverageName, List<Envelope> pyramids, double[] pixelScales) throws CoverageStoreException, DataStoreException, NoninvertibleTransformException, TransformException {
        //pixel scale corespond a la valeur d'un pixel pour x km ou degré
        //exemple une tuile de 256x256 avec scale 1/100 -> 25600x25600 km
        Dimension tileSize = new Dimension(tileWidth, tileHeight);
        //utiliser le reader pour lire dans l'image de base
        GridCoverageReadParam gcrp = new GridCoverageReadParam();

        GridCoverage gc = reader.read(0, null);
        //pour resamplé le coverage
//        Operations.DEFAULT.resample(null, null);

        GridGeometry gg = gc.getGridGeometry();
        if (!(gg instanceof GridGeometry2D)) throw new IllegalArgumentException("GridGeometry not instance of GridGeometry2D");
        GridGeometry2D gg2d = (GridGeometry2D)gg;
        GridEnvelope globaleArea = gg2d.getExtent2D();

        CoverageReference cv = coverageStore.create(coverageName);
        if (!(cv instanceof PyramidalModel)) throw new IllegalArgumentException("CoverageStore parameter not instance of PyramidalModel");
        PyramidalModel pm = (PyramidalModel) cv;

        ColorModel cm = null;

        //une pyramid par envelope
        for (Envelope pyramidBase : pyramids) {
            CoordinateReferenceSystem crsDest = pyramidBase.getCoordinateReferenceSystem();
            //on projete le coverage du reader dans le crs courant
            Pyramid pyram = pm.createPyramid(crsDest);
            GridCoverage2D gcDest = (GridCoverage2D) Operations.DEFAULT.resample(gc, crsDest);

            GridGeometry2D ggDest2D = (GridGeometry2D) gcDest.getGridGeometry();

            final int[] xyAxis = getXYiD(ggDest2D.getCoordinateReferenceSystem2D());

            if (cm == null) cm = gcDest.getRenderableImage(xyAxis[0], xyAxis[1]).createDefaultRendering().getColorModel();


            //taille du coverage dans l'espace du crs
            final double envWidth  = pyramidBase.getSpan(xyAxis[0]);
            final double envHeight = pyramidBase.getSpan(xyAxis[1]);

            ///////////////CROP/////////////

            final double[] res = ggDest2D.getResolution();
            final double  resX = res[xyAxis[0]];
            final double  resY = res[xyAxis[1]];

            final int baseWidth  = (int) ((pyramidBase.getSpan(xyAxis[0])) / resX);
            final int baseHeight = (int) ((pyramidBase.getSpan(xyAxis[1])) / resY);

            final WritableRenderedImage baseImg = new TiledImage(0, 0, baseWidth, baseHeight, 0, 0, cm.createCompatibleSampleModel(tileWidth, tileHeight), cm);
            PixelIterator pixBase = PixelIteratorFactory.createRowMajorWriteableIterator(baseImg, baseImg);

            //evaluate
            final Point2D pt = new Point2D.Double();
            double[] evaluate = null;
            int nb = 0;

            DirectPosition2D dp2d = new DirectPosition2D();
            MathTransform mtcrop = new AffineTransform2D(resX, 0, 0, resY, pyramidBase.getMinimum(xyAxis[0]), pyramidBase.getMinimum(xyAxis[1]));
            for (int by = 0; by < baseHeight; by++) {
                for (int bx = 0; bx < baseWidth; bx++) {
                    dp2d.setLocation(bx, baseHeight-by);
                    mtcrop.transform(dp2d, dp2d);
                    pt.setLocation(dp2d.getOrdinate(0), dp2d.getOrdinate(1));
                    if (evaluate == null) {
                        evaluate = gcDest.evaluate(pt, (double[])null);
                        nb = evaluate.length;
                    } else {
                        gcDest.evaluate(pt, evaluate);
                    }
                    for(int b = 0; b < nb; b++){
                        pixBase.next();
                        pixBase.setSampleDouble(evaluate[b]);
                    }
                }
            }




            GeneralDirectPosition upperleft = new GeneralDirectPosition(crsDest);
            upperleft.setLocation(pyramidBase.getMinimum(xyAxis[0]), pyramidBase.getMaximum(xyAxis[1]));


            //une mosaic par niveau d'echelle
            for (double pixelScal : pixelScales) {

                //taille de l'image en sortie
                int imgWidth  = (int) (envWidth  / pixelScal);
                int imgHeight = (int) (envHeight / pixelScal);
                double sx = ((double)imgWidth)/baseWidth;
                double sy = ((double)imgHeight)/baseHeight;

                //nbre tuile
                int nbrTileX = (imgWidth  + tileWidth  - 1) / tileWidth;
                int nbrTileY = (imgHeight + tileHeight - 1) / tileHeight;

                //Creation de la mosaic
                Dimension gridSize = new Dimension(nbrTileX, nbrTileY);
                GridMosaic mosaic = pm.createMosaic(pyram.getId(), gridSize, tileSize, upperleft, pixelScal);
                String mosaicId = mosaic.getId();
                SampleModel sm = null;

                for (int cTY = 0; cTY < nbrTileY; cTY++) {
                    for (int cTX = 0; cTX < nbrTileX; cTX++) {

                        if (sm == null) sm = cm.createCompatibleSampleModel(tileWidth, tileHeight);


                        int destMinX = cTX*tileWidth;
                        int destMinY = cTY*tileHeight;
                        int cuTWidth = Math.min(destMinX + tileWidth, imgWidth);
                        int cuTHeight = Math.min(destMinY + tileHeight, imgHeight);

                        WritableRenderedImage destImg = new TiledImage(destMinX, destMinY, cuTWidth-destMinX, cuTHeight-destMinY, destMinX, destMinY, sm, cm);

                        ////////// resampling
                        //definir mathtransform
                        final MathTransform mt  = new AffineTransform2D(sx, 0, 0, sy, 0, 0);
                        final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(baseImg), interpolationCase, lanczosWindow);
                        //definir l'interpolateur
                        ////////// fin

                        Resample resample = new Resample(mt, destImg, interpolation, fillValue);
                        resample.fillImage();

                        pm.writeTile(pyram.getId(), mosaicId, cTX, cTY, destImg);
                    }
                }
            }

        }
    }

    public void create(GridCoverage gridCoverage, CoverageStore coverageStore, Name coverageName, List<Envelope> pyramids, double[] pixelScales) throws CoverageStoreException, DataStoreException, NoninvertibleTransformException, TransformException {
        //pixel scale corespond a la valeur d'un pixel pour x km ou degré
        //exemple une tuile de 256x256 avec scale 1/100 -> 25600x25600 km
        Dimension tileSize = new Dimension(tileWidth, tileHeight);
        //utiliser le reader pour lire dans l'image de base
//        GridCoverageReadParam gcrp = new GridCoverageReadParam();

//        GridCoverage gc = reader.read(0, null);
        //pour resamplé le coverage
//        Operations.DEFAULT.resample(null, null);

        GridGeometry gg = gridCoverage.getGridGeometry();
        if (!(gg instanceof GridGeometry2D)) throw new IllegalArgumentException("GridGeometry not instance of GridGeometry2D");
        GridGeometry2D gg2d = (GridGeometry2D)gg;
        GridEnvelope globaleArea = gg2d.getExtent2D();

        CoverageReference cv = coverageStore.create(coverageName);
        if (!(cv instanceof PyramidalModel)) throw new IllegalArgumentException("CoverageStore parameter not instance of PyramidalModel");
        PyramidalModel pm = (PyramidalModel) cv;

        ColorModel cm = null;

        //une pyramid par envelope
        for (Envelope pyramidBase : pyramids) {
            CoordinateReferenceSystem crsDest = pyramidBase.getCoordinateReferenceSystem();
            //on projete le coverage du reader dans le crs courant
            Pyramid pyram = pm.createPyramid(crsDest);
            GridCoverage2D gcDest = (GridCoverage2D) Operations.DEFAULT.resample(gridCoverage, crsDest);

            GridGeometry2D ggDest2D = (GridGeometry2D) gcDest.getGridGeometry();

            final int[] xyAxis = getXYiD(ggDest2D.getCoordinateReferenceSystem2D());

            if (cm == null) cm = gcDest.getRenderableImage(xyAxis[0], xyAxis[1]).createDefaultRendering().getColorModel();


            //taille du coverage dans l'espace du crs
            final double envWidth  = pyramidBase.getSpan(xyAxis[0]);
            final double envHeight = pyramidBase.getSpan(xyAxis[1]);

            ///////////////CROP/////////////

            final double[] res = ggDest2D.getResolution();
            final double  resX = res[xyAxis[0]];
            final double  resY = res[xyAxis[1]];

            final int baseWidth  = (int) ((pyramidBase.getSpan(xyAxis[0])) / resX);
            final int baseHeight = (int) ((pyramidBase.getSpan(xyAxis[1])) / resY);

//            final WritableRenderedImage baseImg = new TiledImage(0, 0, baseWidth, baseHeight, 0, 0, cm.createCompatibleSampleModel(tileWidth, tileHeight), cm);
//            PixelIterator pixBase = PixelIteratorFactory.createRowMajorWriteableIterator(baseImg, baseImg);
//
//            //evaluate
//            final Point2D pt = new Point2D.Double();
//            double[] evaluate = null;
//            int nb = 0;
//
//            DirectPosition2D dp2d = new DirectPosition2D();
//            MathTransform mtcrop = new AffineTransform2D(resX, 0, 0, -resY, pyramidBase.getMinimum(xyAxis[0]), pyramidBase.getMaximum(xyAxis[1]));
//            for (int by = 0; by < baseHeight; by++) {
//                for (int bx = 0; bx < baseWidth; bx++) {
//                    dp2d.setLocation(bx, by);
//                    mtcrop.transform(dp2d, dp2d);
//                    pt.setLocation(dp2d.getOrdinate(0), dp2d.getOrdinate(1));
//                    if (evaluate == null) {
//                        evaluate = gcDest.evaluate(pt, (double[])null);
//                        nb = evaluate.length;
//                    } else {
//                        gcDest.evaluate(pt, evaluate);
//                    }
//                    for(int b = 0; b < nb; b++){
//                        pixBase.next();
//                        pixBase.setSampleDouble(evaluate[b]);
//                    }
//                }
//            }

            final RenderedImage baseImg = gcDest.getRenderedImage();



            GeneralDirectPosition upperleft = new GeneralDirectPosition(crsDest);
            upperleft.setLocation(pyramidBase.getMinimum(xyAxis[0]), pyramidBase.getMaximum(xyAxis[1]));


            //une mosaic par niveau d'echelle
            for (double pixelScal : pixelScales) {

                //taille de l'image en sortie
                int imgWidth  = (int) (envWidth  / pixelScal);
                int imgHeight = (int) (envHeight / pixelScal);
                double sx = ((double)imgWidth)/baseWidth;
                double sy = ((double)imgHeight)/baseHeight;

                //nbre tuile
                int nbrTileX = (imgWidth  + tileWidth  - 1) / tileWidth;
                int nbrTileY = (imgHeight + tileHeight - 1) / tileHeight;

                //Creation de la mosaic
                Dimension gridSize = new Dimension(nbrTileX, nbrTileY);
                GridMosaic mosaic = pm.createMosaic(pyram.getId(), gridSize, tileSize, upperleft, pixelScal);
                String mosaicId = mosaic.getId();
                SampleModel sm = null;

                for (int cTY = 0; cTY < nbrTileY; cTY++) {
                    for (int cTX = 0; cTX < nbrTileX; cTX++) {

                        if (sm == null) sm = cm.createCompatibleSampleModel(tileWidth, tileHeight);


                        int destMinX = cTX*tileWidth;
                        int destMinY = cTY*tileHeight;
                        int cuTWidth = Math.min(destMinX + tileWidth, imgWidth);
                        int cuTHeight = Math.min(destMinY + tileHeight, imgHeight);

                        WritableRenderedImage destImg = new TiledImage(destMinX, destMinY, cuTWidth-destMinX, cuTHeight-destMinY, destMinX, destMinY, sm, cm);

                        ////////// resampling
                        //definir mathtransform
                        final MathTransform mt  = new AffineTransform2D(sx, 0, 0, sy, 0, 0);
                        final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(baseImg), interpolationCase, lanczosWindow);
                        //definir l'interpolateur
                        ////////// fin

                        Resample resample = new Resample(mt, destImg, interpolation, fillValue);
                        resample.fillImage();

                        pm.writeTile(pyram.getId(), mosaicId, cTX, cTY, destImg);
                    }
                }
            }

        }
    }

    /**
      * Find x axis and y axis dimension index ordinate from {@link CoordinateReferenceSystem}.
      *
      * @param crs {@link CoordinateReferenceSystem} where find ordinate.
      * @return double table of length 2 where table[0] = X axis ordinate
      * and table[1] = Y axis ordinate.
      */
     private int[] getXYiD(CoordinateReferenceSystem crs) {
        final int[] xy = new int[]{-1, -1};
        if (crs instanceof CompoundCRS) {
            int tempOrdinate = 0;
            for (CoordinateReferenceSystem crss : ((CompoundCRS) crs).getComponents()) {
                final CoordinateSystem cs = crss.getCoordinateSystem();
                final int csDim = cs.getDimension();
                for (int cd = 0; cd < csDim; cd++) {
                    final CoordinateSystemAxis csa = cs.getAxis(cd);
                    final AxisDirection ad = csa.getDirection();
                    if (ad.compareTo(AxisDirection.EAST) == 0 || ad.compareTo(AxisDirection.WEST) == 0) {
                        xy[0] = cd + tempOrdinate;
                    } else if (ad.compareTo(AxisDirection.NORTH) == 0 || ad.compareTo(AxisDirection.SOUTH) == 0) {
                        xy[1] = cd + tempOrdinate;
                    }
                }
                tempOrdinate += csDim;
            }
        } else {
            final CoordinateSystem cs = crs.getCoordinateSystem();
            final int csDim = cs.getDimension();
            for (int cd = 0; cd < csDim; cd++) {
                final CoordinateSystemAxis csa = cs.getAxis(cd);
                final AxisDirection ad = csa.getDirection();
                if (ad.compareTo(AxisDirection.EAST) == 0 || ad.compareTo(AxisDirection.WEST) == 0) {
                    xy[0] = cd;
                } else if (ad.compareTo(AxisDirection.NORTH) == 0 || ad.compareTo(AxisDirection.SOUTH) == 0) {
                    xy[1] = cd;
                }
            }
        }
        if (xy[0] == -1 || xy[1] == -1)
            throw new IllegalArgumentException("appropriate system axis not find");
        return xy;
    }

}
