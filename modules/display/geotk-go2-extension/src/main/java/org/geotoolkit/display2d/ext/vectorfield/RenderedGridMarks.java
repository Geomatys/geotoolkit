/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.ext.vectorfield;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.List;
import javax.measure.unit.Unit;
import javax.media.jai.PlanarImage;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;

import org.geotoolkit.coverage.CoverageFactoryFinder;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;

import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;


/**
 * Renderer {@linkplain GridCoverage grid coverage} data as marks.
 * The default appearance depends on the number of bands:
 * <ul>
 *   <li>For one band, data are displayed as {@linkplain Ellipse2D circles}.
 *       Circle area are proportional to the sample value.</li>
 *   <li>For two bands, data are displayed as {@linkplain Arrow2D arrows}.
 *       Arrows sizes and direction depends of the sample values.</li>
 * </ul>
 *
 * @version $Id$
 * @author Martin Desruisseaux
 * @module pending
 */
public class RenderedGridMarks extends RenderedMarks {
    /**
     * Extends the zoomable area by this amount of pixels when computing the "grid" clip area.
     * This is needed since a mark located outside the clip area may have a part showing in the
     * visible area if the mark is big enough.
     *
     * @task REVISIT: This number should be computed rather than hard-coded.
     */
    private static final int VISIBLE_AREA_EXTENSION = 10;

    /**
     * Default value for the {@linkplain #getZOrder z-order}.
     */
    private static final float DEFAULT_Z_ORDER = Float.POSITIVE_INFINITY;

    /**
     * The default shape for displaying data with only 1 band.
     * This shape is a circle centered at (0,0) with a radius of 5 dots.
     */
    private static final Shape DEFAULT_SHAPE_1D = MarkIterator.DEFAULT_SHAPE;

    /**
     * Forme géométrique représentant une flèche.  Le début de cette flèche
     * est l'origine à (0,0) et sa longueur est de 10 points. La flèche est
     * pointée dans la direction des <var>x</var> positifs (soit à un angle
     * de 0 radians arithmétiques).
     */
    private static final Shape DEFAULT_SHAPE_2D = new Arrow2D(0, -2.5, 20,10);

    /**
     * The grid coverage.
     *
     * @see #image
     * @see #mainSD
     */
    private final GridCoverage2D coverage;
    
    /**
     * The transform from grid (<var>i</var>,<var>j</var>) to <strong>coverage</strong>
     * coordinate system (<var>x</var>,<var>y</var>).
     */
    private final MathTransform2D gridToCoverage;

    /**
     * Image contenant les composantes <var>x</var> et <var>x</var> des vecteurs.
     */
    private PlanarImage image;

    /**
     * The number of visible bands. Should be 0, 1 or 2.
     *
     * @see #getBands
     * @see #setBands
     * @see #bandX
     * @see #bandY
     */
    private int numBands;

    /**
     * Index des bandes <var>X</var> et <var>Y</var> dans l'image.
     *
     * @see #numBands
     * @see #getBands
     * @see #setBands
     */
    private int bandX, bandY;

    /**
     * Nombre de points à décimer selon l'axe des <var>x</var> et des <var>y</var>.
     * Ce nombre doit être supérieur à 0. La valeur <code>1</code> signifie qu'aucune
     * décimation ne sera faite.
     */
    private int decimateX=1, decimateY=1;

    /**
     * Espace minimal (en points) à laisser entre les symboles selon les axes
     * <var>x</var> et <var>y</var>. La valeur 0 désactive la décimation selon cet axe.
     */
    private int spaceX=0, spaceY=0;

    /**
     * Indique si la décimation est active. Ce champ prend la valeur <code>true</code>
     * si <code>decimateX</code> ou <code>decimateY</code> sont supérieurs à 1.
     */
    private boolean decimate = false;

    /**
     * Indique si la décimation automatique est active. Ce champ prend la
     * valeur <code>true</code> lorsque {@link #setAutoDecimation} est
     * appellée et que <code>spaceX</code> ou <code>spaceY</code> sont
     * supérieurs à 0.
     */
    private boolean autoDecimate = false;
    
    /**
     * Couleur des flèches.
     */
    private Paint markPaint = MarkIterator.DEFAULT_COLOR;

    /**
     * The shape to use for marks, or <code>null</code> for displaying labels only.
     */
    private Shape markShape = DEFAULT_SHAPE_1D;

    /**
     * The default {@linkplain #getPreferredArea preferred area} for this layer.
     * Used only if the user didn't set explicitely a preferred area.
     */
    private Rectangle2D preferredArea;

    /**
     * A band to use as a formatter for geophysics values.
     */
    private SampleDimension mainSD;
//
//    /**
//     * Buffer temporaire pour l'écriture des "tooltip".
//     */
//    private transient StringBuffer buffer;
//
//    /**
//     * Objet à utiliser pour l'écriture des angles.
//     */
//    private transient AngleFormat angleFormat;

    /**
     * Construct a new layer for the specified grid coverage. If the supplied grid coverage has
     * only one band, then marks will be displayed as circles with area proportional to sample
     * values. Otherwise, marks will be displayed as arrows with <var>x</var> and <var>y</var>
     * components fetched from sample dimensions (bands) 0 and 1 respectively.
     *
     * @param coverage The grid coverage, or <code>null</code> if none.
     */
    public RenderedGridMarks(final ReferencedCanvas2D canvas,final GridCoverage2D cover) {
        super(canvas, cover.getCoordinateReferenceSystem2D());
        if(cover == null){
            throw new NullPointerException("Coverage must not be null");
        }
        
        //we reproject in 2D CRS
        CoordinateReferenceSystem planarCRS = CRS.getHorizontalCRS(cover.getEnvelope().getCoordinateReferenceSystem());
        
        if(planarCRS.equals(cover.getCoordinateReferenceSystem())){
            this.coverage = cover;
        }
        else{
            this.coverage = reSample(cover, planarCRS);
        }
        
        this.gridToCoverage = this.coverage.getGridGeometry().getGridToCRS2D();
        
        try {
            numBands = this.coverage.getRenderedImage().getSampleModel().getNumBands();
            if (numBands >= 2) {
                numBands  = 2;
                bandY     = 1;
                markShape = DEFAULT_SHAPE_2D;
            }
            
            setEnvelope(this.coverage.getEnvelope());
            initGridCoverage(coverage);
        } catch (TransformException exception) {
            exception.printStackTrace();
            // Should not happen for most GridCoverage instances.
            // However, it may occurs in some special cases.
            final IllegalArgumentException e;
            e = new IllegalArgumentException(exception.getLocalizedMessage());
            e.initCause(exception);
            throw e;
        }
                
        setAutoDecimation(20, 20);
    }
        
    
    private GridCoverage2D reSample(GridCoverage2D coverage, CoordinateReferenceSystem crs){
        
        RenderedImage rendered = coverage.getRenderedImage();
        GridGeometry2D gridGeom = coverage.getGridGeometry();
        MathTransform transform = gridGeom.getGridToCRS2D();
        GridSampleDimension[] bands = coverage.getSampleDimensions();
        
        GridCoverageFactory factory = CoverageFactoryFinder.getGridCoverageFactory(null);
        GridCoverage2D coverage2 = factory.create("resample2D", rendered, crs, transform, bands, null, null);
        
        return coverage2;
    }
    
    
    /**
     * Set the grid coverage for this graphic.
     *
     * @param  coverage The grid coverage, or <code>null</code> if none.
     * @throws TransformException is a transformation was required and failed.
     */
    private void initGridCoverage(GridCoverage2D coverage) throws TransformException {
        
        synchronized (getTreeLock()) {
            
            coverage = coverage.view(ViewType.GEOPHYSICS);
            final SampleDimension[] samples = coverage.getSampleDimensions();
            if (Math.max(bandX, bandY) >= samples.length) {
                // TODO: localize.
                throw new IllegalArgumentException("Too few bands in the grid coverage.");
            }
            final SampleDimension sampleX = samples[bandX];
            final SampleDimension sampleY = samples[bandY];
            final Unit unitX = sampleX.getUnits();
            final Unit unitY = sampleY.getUnits();
            if (!Utilities.equals(unitX, unitY)) {
                // TODO: localize.
                throw new IllegalArgumentException("Mismatched units");
            }

            this.image = PlanarImage.wrapRenderedImage(coverage.getRenderedImage());
            this.mainSD = sampleX;
        }    
        
        clearCache();
           
    }

    /**
     * Returns the current grid coverage.
     */
    public GridCoverage2D getGridCoverage() {
        return coverage;
    }

    /**
     * Set the bands to use for querying mark values.
     *
     * @param  bands The band. This array length should 0, 1 or 2. A length of 0 is equivalents
     *         to a call to <code>{@link #setVisible setVisible}(false)</code>.
     * @throws IllegalArgumentException if the array length is illegal, or if a band is greater than
     *         the number of bands in the {@linkplain #getGridCoverage underlying grid coverage}.
     */
    public void setBands(final int[] bands) throws IllegalArgumentException {
        final int[] oldBands;
        synchronized (getTreeLock()) {
            final int max = (coverage!=null) ? image.getNumBands() : Integer.MAX_VALUE;
            for (int i=0; i<bands.length; i++) {
                final int band = bands[i];
                if (band<0 || band>=max) {
                    throw new IllegalArgumentException("No such band: "+band);
                    // TODO: localize
                }
            }
            oldBands = getBands();
            switch (bands.length) {
                default: {
                    // TODO: localize
                    throw new IllegalArgumentException("Can't renderer more than 2 bands.");
                }
                case 2: {
                    bandX = bands[0];
                    bandY = bands[1];
                    if (markShape == DEFAULT_SHAPE_1D) {
                        markShape  = DEFAULT_SHAPE_2D;
                    }
                    break;
                }
                case 1: {
                    bandX = bandY = bands[0];
                    if (markShape == DEFAULT_SHAPE_2D) {
                        markShape  = DEFAULT_SHAPE_1D;
                    }
                    break;
                }
                case 0: {
                    bandX = bandY = 0;
                    setVisible(false);
                    if (markShape == DEFAULT_SHAPE_2D) {
                        markShape  = DEFAULT_SHAPE_1D;
                    }
                    break;
                }
            }
            numBands = bands.length;
            clearCache();
//            repaint();         //--------------------------------------------------------------------------------------
        }
//        listeners.firePropertyChange("bands", oldBands, bands);    //--------------------------------------------------------------------------------------
    }

    /**
     * Returns the bands to use for querying mark values.
     */
    public int[] getBands() {
        synchronized (getTreeLock()) {
            switch (numBands) {
                default: throw new AssertionError(numBands); // Should not happen.
                case  2: return new int[] {bandX, bandY};
                case  1: return new int[] {bandX};
                case  0: return new int[] {};
            }
        }
    }

    /**
     * Set a decimation factor. A value greater than 1 will reduces the number of points
     * iterated by the {@link MarkIterator}. Note that points are not actually decimated,
     * but rather averaged. For example a "decimation" factor of 2 will average two neighbor
     * points and replace them with new one in the middle of the original points.
     *
     * @param decimateX Decimation among <var>x</var>, or 1 for none.
     * @param decimateY Decimation among <var>y</var>, or 1 for none.
     */
    public void setDecimation(final int decimateX, final int decimateY) {
        if (decimateX <=0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.NOT_GREATER_THAN_ZERO_$1,
                                               new Integer(decimateX)));
        }
        if (decimateY <=0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.NOT_GREATER_THAN_ZERO_$1,
                                               new Integer(decimateY)));
        }
        if (decimateX!=this.decimateX || decimateY!=this.decimateY) {
            synchronized (getTreeLock()) {
                                
                autoDecimate   = false;
                this.decimateX = decimateX;
                this.decimateY = decimateY;
                decimate = (decimateX!=1 || decimateY!=1);
                clearCache();
//                repaint();     //--------------------------------------------------------------------------------------
            }
        }
    }

    /**
     * Décime automatiquement les points de la grille de façon à conserver un espace
     * d'au moins <code>spaceX</code> et <code>spaceY</code> entre chaque point.
     *
     * @param spaceX Espace minimal (en points) selon <var>x</var> à laisser entre les
     *        symboles. La valeur 0 désactive la décimation selon cet axe.
     * @param spaceY Espace minimal (en points) selon <var>y</var> à laisser entre les
     *        symboles. La valeur 0 désactive la décimation selon cet axe.
     */
    public void setAutoDecimation(final int spaceX, final int spaceY) {
        if (spaceX < 0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2,
                                               "spaceX", new Integer(spaceX)));
        }
        if (spaceY < 0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2,
                                               "spaceY", new Integer(spaceY)));
        }
        if (spaceX!=this.spaceX || spaceY!=this.spaceY) {
            synchronized (getTreeLock()) {
                this.spaceX  = spaceX;
                this.spaceY  = spaceY;
                autoDecimate = (spaceX!=0 || spaceY!=0);
                clearCache();
//                repaint();     //--------------------------------------------------------------------------------------
            }
        }
    }

    /**
     * Returns the shape to use for painting marks. If this layer paint labels rather than
     * marks, then this method returns <code>null</code>.
     *
     * @see #setMarkShape
     * @see Iterator#markShape
     */
    public Shape getMarkShape() {
        return markShape;
    }

    /**
     * Set the shape to use for painting marks. This shape must be centred at the origin (0,0)
     * and its coordinates must be expressed in dots (1/72 of inch). For example in order to
     * paint wind arrows, this shape should be oriented toward positives <var>x</var> (i.e.
     * toward 0 arithmetic radians), has a base centred at (0,0) and have a raisonable size
     * (for example 16&times;4 pixels). The method {@link RenderedMarks#paint(RenderingContext)}
     * will automatically takes care of rotation, translation and scale in order to adjust this
     * model to each mark properties.
     * <br><br>
     * A value of <code>null</code> is legal. In this case, this layer will renderer amplitudes
     * as labels rather than marks.
     *
     * @see #getMarkShape
     * @see Iterator#markShape
     */
    public void setMarkShape(final Shape shape) {
        final Shape oldShape;
        synchronized (getTreeLock()) {
            oldShape = markShape;
            markShape = shape;
        }
//        listeners.firePropertyChange("markShape", oldShape, shape);    //--------------------------------------------------------------------------------------
    }

    /**
     * Returns the default fill paint for marks.
     *
     * @see #setMarkPaint
     * @see Iterator#markPaint
     */
    public Paint getMarkPaint() {
        return markPaint;
    }

    /**
     * Set the default fill paint for marks.
     *
     * @see #getMarkPaint
     * @see Iterator#markPaint
     */
    public void setMarkPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2,
                                               "paint", paint));
        }
        final Paint oldPaint;
        synchronized (getTreeLock()) {
            oldPaint = markPaint;
            markPaint = paint;
        }
//        listeners.firePropertyChange("markPaint", oldPaint, paint);    //--------------------------------------------------------------------------------------
    }

    /**
     * Returns the preferred area for this layer. If no preferred area has been explicitely
     * set, then this method returns the grid coverage's bounding box.
     */
    public Rectangle2D getPreferredArea() {
        synchronized (getTreeLock()) {
//            final Rectangle2D area = super.getPreferredArea();     //--------------------------------------------------------------------------------------
//            if (area != null) {   
//                return area;
//            }
            return (preferredArea!=null) ? (Rectangle2D) preferredArea.clone() : null;
        }
    }

    /**
     * Retourne le nombre de points de cette grille. Le nombre de point retourné
     * tiendra compte de la décimation spécifiée avec {@link #setDecimation}.
     */
    @Override
    final int getCount() {
        assert Thread.holdsLock(getTreeLock());
        if (image==null || numBands==0) {
            return 0;
        }
        return (image.getWidth()/decimateX) * (image.getHeight()/decimateY);
    }

    /**
     * Returns an iterator for iterating through the marks. The default implementation
     * returns an instance of {@link RenderedGridMarks.Iterator}.
     */
    @Override
    public MarkIterator getMarkIterator() {
        return new Iterator();
    }

    /**
     * Returns the units for {@linkplain MarkIterator#amplitude marks amplitude}.
     * The default implementation infers the units from the underlying grid coverage.
     */
    @Override
    public Unit getAmplitudeUnit() {
        return mainSD.getUnits();
    }

    /**
     * Returns the typical amplitude of marks. The default implementation computes the <cite>Root
     * Mean Square</cite> (RMS) value of sample values in the underlying grid coverage, no matter
     * what the {@linkplain #setDecimation decimation factor} is (if any).
     */
    @Override
    public double getTypicalAmplitude() {
        //TODO find a way to personalize the arrow size
        //keep commun arrow size, for foss4g demo
        //return 0.27d;
        
        synchronized (getTreeLock()) {
            if (!(typicalAmplitude > 0)) {
                double sum = 0;
                int  count = 0;
                final RectIter iter = RectIterFactory.create(image, null);
                if (!iter.finishedLines()) do {
                    iter.startPixels();
                    if (!iter.finishedPixels()) do {
                        final double x = iter.getSampleDouble(bandX);
                        final double y = iter.getSampleDouble(bandY);
                        final double s = x*x + y*y;
                        if (!Double.isNaN(s)) {
                            sum += s;
                            count++;
                        }
                    }
                    while (!iter.nextPixelDone());
                } while (!iter.nextLineDone());
                if (numBands == 1) {
                    typicalAmplitude = Math.pow(sum/(2*count), 0.25);
                } else {
                    typicalAmplitude = Math.sqrt(sum/count);
                }
                if (!(typicalAmplitude > 0)) {
                    typicalAmplitude = 1;
                }
            }
            return typicalAmplitude;
        }
    }

    /**
     * Returns the grid indices for the specified zoomable bounds.
     * Those indices will be used by {@link Iterator#visible(Rectangle)}.
     *
     * @param  zoomableBounds The zoomable bounds. Do not modify!
     * @param  csToMap  The transform from {@link #getCoordinateSystem()} to the rendering CS.
     * @param  mapToTxt The transform from the rendering CS to the Java2D CS.
     * @return The grid clip, or <code>null</code> if it can't be computed.
     */
    @Override
    protected final Rectangle getGridClip(final Rectangle zoomableBounds,
                                final MathTransform2D coverageToObjective,
                                final AffineTransform objectiveToDisplay)
    {
        assert Thread.holdsLock(getTreeLock());
        Rectangle2D visibleArea = new Rectangle2D.Double(
                zoomableBounds.x      - VISIBLE_AREA_EXTENSION,
                zoomableBounds.y      - VISIBLE_AREA_EXTENSION,
                zoomableBounds.width  + VISIBLE_AREA_EXTENSION*2,
                zoomableBounds.height + VISIBLE_AREA_EXTENSION*2);
        try {
            visibleArea = XAffineTransform.inverseTransform(objectiveToDisplay, visibleArea, visibleArea);
            final MathTransform2D objectiveToCoverage = (MathTransform2D)coverageToObjective.inverse();
            if (!objectiveToCoverage.isIdentity()) {
                visibleArea = objectiveToCoverage.createTransformedShape(visibleArea).getBounds();
            }
            // Note: on profite du fait que {@link Rectangle#setRect}
            //       arrondie correctement vers les limites supérieures. 
            final Rectangle bounds = gridToCoverage.inverse().createTransformedShape(visibleArea).getBounds();
                                             
            bounds.x      = (bounds.x      -1) / decimateX;
            bounds.y      = (bounds.y      -1) / decimateY;
            bounds.width  = (bounds.width  +2) / decimateX +1;
            bounds.height = (bounds.height +2) / decimateY +1;
            return bounds;
        } catch (NoninvertibleTransformException exception) {
            return null;
        } catch (TransformException exception) {
            return null;
        }
    }

    /**
     * Procède au traçage des marques de cette grille.
     *
     * @throws TransformException si une projection
     *         cartographique était nécessaire et a échouée.
     */
    @Override
    public void paint(final RenderingContext2D context) {
        final Graphics2D g2 = context.getGraphics();

        //enable anti-aliasing
        final Object before = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (autoDecimate) {
                        
            assert Thread.holdsLock(getTreeLock());
            final AffineTransform tr;
            try {
                // TODO: handle the case where gridToCRS is not affine.
                tr = ((AffineTransform) gridToCoverage).createInverse();

                MathTransform trans = context.getMathTransform(context.getDisplayCRS(), coverage.getCoordinateReferenceSystem());
                if(trans instanceof AffineTransform){
                    tr.concatenate(context.getAffineTransform(context.getDisplayCRS(), coverage.getCoordinateReferenceSystem()));
                }else{
                    //TODO try to find a better way to calculate the step
                    //currently make a fake affinetransform using the difference between envelopes.
                    AffineTransform dispToObjective = context.getAffineTransform(context.getDisplayCRS(), context.getObjectiveCRS());
                    AffineTransform objToCoverage   = calculateAverageAffine(context);
                    tr.concatenate(dispToObjective);
                    tr.concatenate(objToCoverage);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            Point2D delta = new Point2D.Double(spaceX, 0);
            delta = tr.deltaTransform(delta, delta);
            final int decimateX = length(delta);
            delta.setLocation(0, spaceY);
            delta = tr.deltaTransform(delta, delta);
            final int decimateY = length(delta);
            if (decimateX!=this.decimateX || decimateY!=this.decimateY) {
                this.decimateX = decimateX;
                this.decimateY = decimateY;
                decimate = (decimateX!=1 || decimateY!=1);
                invalidate();
            }
        }
        super.paint(context);

        //restore previous anti-aliasing
        if(before == null) g2.getRenderingHints().remove(RenderingHints.KEY_ANTIALIASING);
        else               g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, before);
        
    }


    private AffineTransform calculateAverageAffine(RenderingContext2D context) throws FactoryException, TransformException{

        MathTransform trs = context.getMathTransform(context.getObjectiveCRS(),coverage.getCoordinateReferenceSystem2D() );

        final Envelope refEnv = context.getCanvasObjectiveBounds();
        final GeneralEnvelope coverageEnv = CRS.transform(trs, refEnv);

        final double objX = refEnv.getSpan(0);
        final double objY = refEnv.getSpan(1);
        final double covX = coverageEnv.getMaximum(0)-coverageEnv.getMinimum(0);
        final double covY = coverageEnv.getMaximum(1)-coverageEnv.getMinimum(1);
        final double scaleX = covX/objX;
        final double scaleY = covY/objY;

        AffineTransform aff = new AffineTransform();
        aff.setToScale(scaleX,scaleY);
        return aff;
    }

    /**
     * Evaluate the lenght of the given vector.
     * 
     * @param delta : vector to evaluate
     * @return integer : lenght of the vector rounded at the above integer
     */
    private static int length(final Point2D delta) {
        return Math.max(1, (int) Math.ceil(Math.hypot(delta.getX(), delta.getY())));
    }

    /**
     * Clear all informations relative to the grid coverage.
     */
    private void clearCoverage() {
        image         = null;
        mainSD        = null;
        preferredArea = null;
        zOrder        = DEFAULT_Z_ORDER;
        //gridToCoordinateSystem = MathTransform2D.IDENTITY; //--------------------------------------------------------------------------------------
    }

    /**
     * Provides a hint that a layer will no longer be accessed from a reference in user
     * space. The results are equivalent to those that occur when the program loses its
     * last reference to this layer, the garbage collector discovers this, and finalize
     * is called. This can be used as a hint in situations where waiting for garbage
     * collection would be overly conservative.
     */
    @Override
    public void dispose() {
        synchronized (getTreeLock()) {
            clearCoverage();
            super.dispose();
        }
    }

    /**
     * Iterates through all marks in a {@link RenderedGridMarks}.
     *
     * @version $Id$
     * @author Martin Desruisseaux
     */
    protected class Iterator extends MarkIterator {
        /**
         * The upper limit (exclusive) for {@link #index}.
         */
        private final int count;

        /**
         * The index of current mark. The <var>i</var>,<var>j</var> index in the underlying
         * grid coverage can be deduced with:
         * <blockquote><pre>
         * i = index % width;
         * j = index / width;
         * </pre></blockquote>
         */
        private int index = -1;

        /**
         * The <var>i</var>,<var>j</var> indices for the current mark.
         * Will be computed only when first required.
         */
        private double i, j;

        /**
         * The <var>x</var>,<var>y</var> component for the current mark.
         * Will be computed only when first required.
         */
        private double x, y;

        /**
         * <code>true</code> if {@link #i}, {@link #j}, {@link #x} and {@link #y} are valids.
         */
        private boolean valid;

        /**
         * Construct a mark iterator.
         */
        public Iterator() {
            count = getCount();
        }

        @Override
        public int getIteratorPosition() {
            return index;
        }
        
        /**
         * Moves the iterator to the specified index.
         */
        @Override
        public void setIteratorPosition(final int n) {
            if (n>=-1 && n<count) {
                index = n;
                valid = false;
            } else {
                throw new IllegalArgumentException(String.valueOf(n));
            }
        }

        /**
         * Moves the iterator to the next mark.
         */
        @Override
        public boolean next() {
            valid = false;
            return ++index < count;
        }

        /**
         * Indique si la marque à l'index spécifié est visible dans le clip spécifié.
         * Le rectangle <code>clip</code> doit avoir été obtenu par {@link #getGridClip}.
         */
        @Override
        final boolean visible(final Rectangle clip) {
            if (!visible()) {
                return false;
            }
            if (clip == null) {
                return true;
            }
            assert Thread.holdsLock(getTreeLock());
            final int decWidth = image.getWidth()/decimateX;
            return clip.contains(index%decWidth, index/decWidth);
        }

        /**
         * Calcule les composantes <var>x</var> et <var>y</var> du vecteur à l'index spécifié.
         */
        private void compute() {
            assert Thread.holdsLock(getTreeLock());
            int    count = 0;
            int    sumI  = 0;
            int    sumJ  = 0;
            double vectX = 0;
            double vectY = 0;
            final int decWidth = image.getWidth()/decimateX;
            final int imin = (index % decWidth)*decimateX + image.getMinX();
            final int jmin = (index / decWidth)*decimateY + image.getMinY();
            for (int i=imin+decimateX; --i>=imin;) {
                for (int j=jmin+decimateY; --j>=jmin;) {
                    final Raster tile = image.getTile(image.XToTileX(i), image.YToTileY(j));
                    final double x = tile.getSampleDouble(i, j, bandX);
                    final double y = tile.getSampleDouble(i, j, bandY);
                    if (!Double.isNaN(x) && !Double.isNaN(y)) {
                        vectX += x;
                        vectY += y;
                        sumI  += i;
                        sumJ  += j;
                        count++;
                    }
                }
            }
            this.x = vectX/count;
            this.y = vectY/count;
            this.i = (double)sumI / count;
            this.j = (double)sumJ / count;
            assert Double.isNaN(i) == Double.isNaN(j);
            assert Double.isNaN(x) == Double.isNaN(y);
            valid = true;
        }

        /**
         * Retourne les coordonnées (<var>x</var>,<var>y</var>) d'un point de la grille.
         * Les coordonnées <var>x</var> et <var>y</var> seront exprimées selon le système
         * de coordonnées du {@linkplain #getGridCoverage grid coverage}.
         *
         * Si une décimation a été spécifiée avec la méthode {@link #setDecimation},
         * alors la position retournée sera située au milieu des points à moyenner.
         *
         * @throws TransformException if a transform was required and failed.
         */
        @Override
        public Point2D position() throws TransformException {
            assert Thread.holdsLock(getTreeLock());
            final Point2D point;
            
            if (!decimate) {
                final int width = image.getWidth();
                point = new Point2D.Double(index % width, index / width);
            } else {
                if (!valid) {
                    compute();
                }
                point = new Point2D.Double(i, j);
            }
            
            return gridToCoverage.transform(point, point);
        }

        /**
         * Retourne l'amplitude à la position d'une marque. Si une décimation a été spécifiée avec
         * la méthode {@link #setDecimation}, alors cette méthode calcule la moyenne vectorielle
         * (la moyenne des composantes <var>x</var> et <var>y</var>) aux positions des marques à
         * décimer, et retourne l'amplitude du vecteur moyen.
         */
        @Override
        public double amplitude() {
            double amplitude = 0;
            assert Thread.holdsLock(getTreeLock());
            if (!valid) {
                compute();
            }
            switch (numBands) {
                case 0:  amplitude = 0; break;
                case 1:  amplitude = Math.sqrt(Math.abs(x)); break;
                case 2:  amplitude = Math.hypot(x, y); break;
                default: throw new AssertionError(numBands);
            }
            
            return amplitude;
        }

        /**
         * Retourne la direction de la valeur d'une marque. Si une décimation a été spécifiée avec
         * la méthode {@link #setDecimation}, alors cette méthode calcule la moyenne vectorielle
         * (la moyenne des composantes <var>x</var> et <var>y</var>) aux positions des marques à
         * décimer, et retourne la direction du vecteur moyen.
         */
        @Override
        public double direction() {
            assert Thread.holdsLock(getTreeLock());
            if (!valid) {
                compute();
            }
            switch (numBands) {
                case 0:  // Fall through
                case 1:  return 0;
                case 2:  return Math.atan2(y, x);
                default: throw new AssertionError(numBands);
            }
        }

        /**
         * Retourne la forme géométrique servant de modèle au traçage des marques.
         * Lorsque deux bandes sont utilisées, la forme par défaut sera une flèche
         * dont l'origine est à (0,0) et qui pointe dans la direction des <var>x</var>
         * positifs (soit à un angle de 0 radians arithmétiques).
         *
         * @see RenderedGridMarks#getMarkShape
         * @see RenderedGridMarks#setMarkShape
         */
        @Override
        public Shape markShape() {
            return markShape;
        }

        /**
         * Returns the paint for current mark.
         *
         * @see RenderedGridMarks#getMarkPaint
         * @see RenderedGridMarks#setMarkPaint
         */
        @Override
        public Paint markPaint() {
            return markPaint;
        }
        
    }

    /**
     * {@inheritDoc }
     */
     @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        //not selectable graphic
        return graphics;
    }

}
