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


import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.font.FontRenderContext;
import java.awt.image.RenderedImage;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Map;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.util.XArrays;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;


/**
 * A set of marks and/or labels to be rendered. Marks can have different sizes and orientations
 * (for example a field of wind arrows). This abstract class is not a container for marks.
 * Subclasses must override the {@link #getMarkIterator} method in order to returns informations
 * about marks.
 *
 * @source $URL$
 * @version $Id$
 * @author Martin Desruisseaux
 */
public abstract class RenderedMarks extends GraphicJ2D {
    
    private static final Stroke DEFAULT_STROKE = new BasicStroke(1);
    
    /**
     * The number of entries in {@link #markTransforms} for each {@link AffineTransform}.
     */
    private static final int TRANSFORM_RECORD_LENGTH = 6;

    /**
     * A bitmask to specify the validity of {@linkplain MarkIterator#geographicArea
     * geographic areas}.
     *
     * @see #invalidate(int)
     */
    protected static final int AREAS_MASK = 1;   // Used for 'areaShapes'

    /**
     * A bitmask to specify the validity of mark's {@linkplain MarkIterator#markShape shapes},
     * {@linkplain MarkIterator#position position}, {@linkplain MarkIterator#amplitude amplitude}
     * and {@linkplain MarkIterator#direction direction}.
     *
     * @see #invalidate(int)
     */
    protected static final int MARKS_MASK = 2;  // Used for 'markShapes' and 'markTransforms'

    /**
     * A bitmask to specify the validity of mark's {@linkplain MarkIterator#markIcon icon}.
     *
     * @see #invalidate(int)
     */
    protected static final int ICONS_MASK = 4;  // Used for 'markShapes' and 'markTransforms'

    /**
     * A bitmask to specify the validity of {@linkplain MarkIterator#label labels} and their
     * {@linkplain MarkIterator#labelPosition positions}.
     *
     * @see #invalidate(int)
     */
    protected static final int GLYPHS_MASK = 8;  // Used for 'glyphVectors' and 'glyphPositions'.

    /**
     * The 'or'-ed together valid bitmasks. Specify if arrays {@link #markShapes},
     * {@link #markTransforms}, {@link #glyphVectors} and {@link #glyphPositions}
     * are valids.
     *
     * @task TODO: Current implementation doesn't yet support "partial" revalidation (e.g.
     *             revalidating MARKS_MASK without GLYPHS_MASK). We invalidate/revalidate
     *             everything or nothing. Future implementations may add more fine grain
     *             validations.
     *
     * @see #isValid
     * @see #AREAS_MASK
     * @see #MARKS_MASK
     * @see #ICONS_MASK
     * @see #GLYPHS_MASK
     */
    private transient int validMask;

    /**
     * The mark index for each mark in {@link #markShapes} and {@link #markTransforms}.
     */
    private transient int[] markIndex;

    /**
     * The transformed shape returned by {@link MarkIterator#geographicArea} for each mark.
     * Geographic areas usually delimit the land which belong to some building. We are unlikely
     * to find the same geographic area for two different marks. Consequently, this array consume
     * much more memory than {@link #markShapes}.
     *
     * @see #AREAS_MASK
     */
    private transient Shape[] areaShapes;

    /**
     * The shape returned by {@link MarkIterator#markShape} for each mark. In most cases, each
     * references in this array point to the same {@link Shape} instance, or to a small amount
     * of {@link Shape} instances. Consequently, the amount of memory used here should be low.
     *
     * @see #MARKS_MASK
     */
    private transient Shape[] markShapes;

    /**
     * The {@link AffineTransform} to apply on each shapes in {@link #markShapes}. Each record
     * in this array is {@link #TRANSFORM_RECORD_LENGTH} entries long. Entries are given to
     * {@link AffineTransform#setTransform(double,double,double,double,double,double)}
     *
     * @see #MARKS_MASK
     */
    private transient float[] markTransforms;

    /**
     * The gylphs for each labels to be rendered, or <code>null</code> if there is no glyphs
     * for this layer. Position for each glyph vectors will be stored in {@link #glyphPositions}.
     *
     * @see #GLYPHS_MASK
     */
    private transient GlyphVector[] glyphVectors;

    /**
     * (<var>x</var>,<var>y</var>) positions for each glyphs, or <code>null</code> if there
     * is no glyphs for this layer.
     *
     * @see #GLYPHS_MASK
     */
    private transient float[] glyphPositions;

    /**
     * The bounding box of all {@link #markShapes} transformed with the corresponding
     * {@link #markTransforms}, as well as {@link #glyphVectors} at their {@link #glyphPositions}.
     * Coordinates for this box must be pixels (or dots).
     */
    private transient Rectangle boundingBox;

    /**
     * Typical amplitude of marks, or 0 or {@link Double#NaN} if it need to be recomputed.
     * This value is computed by {@link #getTypicalAmplitude} and cached here for faster
     * access. The default implementation computes the Root Mean Square (RMS) value of all
     * {@linkplain MarkIterator#amplitude marks amplitude}.
     *
     * Note: this field is read and write by {@link RenderedGridMarks}, which overrides
     * {@link #getTypicalAmplitude}.
     */
    transient double typicalAmplitude;

    /**
     * An instance of {@link TransformedShape} to be reused every time this layer is rendered.
     * Will be created only when first needed.
     */
    private transient TransformedShape transformedShape;

    /**
     * Construct a new layer of marks using the specified coordinate system.
     *
     * @param  crs The coordinate reference system. If the specified coordinate system has more than
     *            two dimensions, then it must be a {@link CompoundCoordinateSystem} with
     *            a two dimensional {@link CompoundCoordinateSystem#getHeadCS headCS}.
     * @throws IllegalArgumentException if <code>cs</code> is nul.
     */
    public RenderedMarks(final ReferencedCanvas2D canvas, final CoordinateReferenceSystem crs) {
        super(canvas,crs);        
    }

    /**
     * Returns a guess of the number of marks. This method will be overrided with
     * a more efficient guess by {@link RenderedGridMarks}.
     */
    int getCount() {
        return (markIndex!=null) ? markIndex.length : 32;
    }

    /**
     * Returns an iterator for iterating through the marks. Whenever it is invoked on the same
     * <code>RenderedMarks</code> more than once, the mark iterator must consistently iterates
     * through the same marks in the same order, unless {@link #invalidate()} has been invoked.
     * If some marks are added, removed or changed, then {@link #invalidate()} must be invoked
     * first (usually by the methods implementing the addition, change or removal of marks).
     *
     * @return An iterator for iterating through the marks.
     *         This iterator doesn't need to be thread-safe.
     */
    public abstract MarkIterator getMarkIterator();
    
    /**
     * Returns the units for {@linkplain MarkIterator#amplitude marks amplitude}, or
     * <code>null</code> if unknow. All marks must use the same units. The default
     * implementation returns always <code>null</code>.
     */
    public Unit getAmplitudeUnit() {
        return null;
    }

    /**
     * Returns the typical amplitude of marks. The default implementation computes the <cite>Root
     * Mean Square</cite> (RMS) value of all {@linkplain MarkIterator#amplitude marks amplitude}.
     *
     * This information is used with mark's {@linkplain MarkIterator#amplitude amplitude} and
     * {@linkplain MarkIterator#markShape shape} in order to determine how big they should be
     * rendered. Marks with an {@linkplain MarkIterator#amplitude amplitude} equals to the
     * typical amplitude will be rendered with their {@linkplain MarkIterator#markShape shape}
     * unscaled. Other marks will be rendered with scaled versions of their shapes.
     */
    public double getTypicalAmplitude() {
        synchronized (getTreeLock()) {
            if (!(typicalAmplitude>0)) {
                int n=0;
                double rms=0;
                for (final MarkIterator it = getMarkIterator(); it.next();) {
                    final double v = it.amplitude();
                    if (!Double.isNaN(v)) {
                        rms += v*v;
                        n++;
                    }
                }
                typicalAmplitude = (n>0) ? Math.sqrt(rms/n) : 1;
            }
            return typicalAmplitude;
        }
    }

    /**
     * Returns the grid indices for the specified zoomable bounds.
     * Those indices will be used by {@link Iterator#visible(Rectangle)}.
     * This method is overriden by {@link RenderedGridMarks}.
     *
     * @param  zoomableBounds The zoomable bounds. Do not modify!
     * @param  csToMap  The transform from {@link #getCoordinateSystem()} to the rendering CS.
     * @param  mapToTxt The transform from the rendering CS to the Java2D CS.
     * @return The grid clip, or <code>null</code> if it can't be computed.
     */
    protected Rectangle getGridClip(final Rectangle zoomableBounds,
                          final MathTransform2D  gridToObj,
                          final AffineTransform objToDisp)
    {
        return null;
    }

    /**
     * Procède au traçage des marques de cette couche. Les classes dérivées ne
     * devraient pas avoir besoin de redéfinir cette méthode. Pour modifier la
     * façon de dessiner les marques, redéfinissez plutôt les méthodes de
     * {@link MarkIterator}.
     *
     * @throws TransformException if a coordinate transformation was required and failed.
     *
     * @see MarkIterator#position
     * @see MarkIterator#markShape
     * @see MarkIterator#markIcon
     * @see MarkIterator#geographicArea
     * @see MarkIterator#label
     * @see #getTypicalAmplitude
     * @see #getAmplitudeUnit
     * @see MarkIterator#paint
     */
    @Override
    public void paint(final RenderingContext2D context) {
        
        assert Thread.holdsLock(getTreeLock());        
        final Graphics2D g2 = context.getGraphics();
        final AffineTransform oldTransform = g2.getTransform();
        final Stroke oldStroke = g2.getStroke();
        final Paint oldPaint = g2.getPaint();
        final Rectangle zoomableBounds = context.getGraphics().getClipBounds(); //paintedArea.getBounds();
        final MarkIterator iterator = getMarkIterator();
        iterator.font = g2.getFont();
        
        if (transformedShape == null) {
            transformedShape = new TransformedShape();
        }
        
        try {
            context.switchToDisplayCRS();
            g2.setStroke(DEFAULT_STROKE);

            
            AffineTransform objectiveToDisplay = null;
            try {
                objectiveToDisplay = context.getAffineTransform(context.getObjectiveCRS(), context.getDisplayCRS());
            } catch (FactoryException ex) {
                Logger.getLogger(RenderedMarks.class.getName()).log(Level.SEVERE, null, ex);
            }
                        
            MathTransform2D coverageToObjective = null;
            try {
                coverageToObjective = (MathTransform2D) context.getMathTransform(getEnvelope().getCoordinateReferenceSystem(), context.getObjectiveCRS());
            } catch (FactoryException ex) {
                Logger.getLogger(RenderedMarks.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*
             * If the transforms are not valids, compute them now. We will compute one affine
             * transform for each mark to be rendered. Affine transforms will be used for all
             * subsequent repaints, including repaints after zoom changes, unless the cache
             * were cleared programmatically through the {@link #clearCache()} method.
             */
            if (!isValid(AREAS_MASK|MARKS_MASK|ICONS_MASK|GLYPHS_MASK)) {
//                logUpdateCache("RenderedMarks");
                final Rectangle            gridClip = getGridClip(zoomableBounds, coverageToObjective, objectiveToDisplay);
                final FontRenderContext fontContext = g2.getFontRenderContext();
                final double           typicalScale = getTypicalAmplitude();
                /*
                 * On veut utiliser une transformation affine identitée (donc en utilisant
                 * une échelle basée sur les pixels plutôt que les coordonnées utilisateur),
                 * mais en utilisant la même rotation que celle qui a cours dans la matrice
                 * <code>mapToTxt</code>. On peut y arriver en utilisant l'identité ci-dessous:
                 *
                 *    [ m00  m01 ]     m00² + m01²  == constante sous rotation
                 *    [ m10  m11 ]     m10² + m11²  == constante sous rotation
                 */
                final double[] matrix = new double[6];
                objectiveToDisplay.getMatrix(matrix);
                if (true) {
                    double scale;
                    scale = Math.hypot(matrix[0], matrix[2]);
                    matrix[0] /= scale;
                    matrix[2] /= scale;
                    scale = Math.hypot(matrix[1], matrix[3]);
                    matrix[1] /= scale;
                    matrix[3] /= scale;
                }
                /*
                 * Iterates through each mark while performing the following steps:
                 * (results will be saved for reuse during next rendering)
                 *
                 *   - Gets and transforms the geographic area (if any).
                 *   - Setup an affine transform taking in account the translation, scale and
                 *     rotation of the mark (if any).
                 *   - Gets the mark icon (if any).
                 *   - Computes the glyph vectors and the positions of each labels (if any).
                 */
                Map   iconBoundsPool = null;
                Rectangle iconBounds = null;
                boolean    hasAreas  = false;
                boolean    hasMarks  = false;
                boolean    hasLabels = false;
                int        numShapes = 0;
                
                while (iterator.next()) {
                    
                    if (!iterator.visible(gridClip)) {
                        continue;
                    }
                    Point2D position;
                    try { 
                        position = iterator.position();
                    } catch (TransformException ex) { 
                        context.getMonitor().exceptionOccured(ex, Level.SEVERE);
                        return;
                    }
                    
                    if (position == null) {
                        continue;
                    }
                    
                    try { 
                        position = coverageToObjective.transform(position, position);
                    } catch (TransformException ex) { 
                        context.getMonitor().exceptionOccured(ex, Level.SEVERE);
                        return;
                    }
                    
                    if (Double.isNaN(matrix[4] = position.getX())) continue;
                    if (Double.isNaN(matrix[5] = position.getY())) continue;
                    objectiveToDisplay.transform(matrix, 4, matrix, 4, 1);
                    /*
                     * STEP 1  -  Gets and transforms the geographic area. Transforming each shapes
                     *            make sense if geographic areas are different for each mark. It is
                     *            usually the case when geographic areas delimit the land which
                     *            belong to some building (two lands are unlikely to have the same
                     *            shape). Note that we will take an opposite strategy for mark
                     *            shapes, since many marks usually use the same shape.
                     */
                    Shape geographicArea = iterator.geographicArea();
                    if (geographicArea != null) {
                        final Shape oldShape = geographicArea;
                        
                        try { 
                            geographicArea = coverageToObjective.createTransformedShape(geographicArea);
                        } catch (TransformException ex) { 
                            context.getMonitor().exceptionOccured(ex, Level.SEVERE);
                            return;
                        }
                        
                        if (oldShape!=geographicArea && (geographicArea instanceof GeneralPath)) {
                            ((GeneralPath) geographicArea).transform(objectiveToDisplay);
                        } else {
                            geographicArea = objectiveToDisplay.createTransformedShape(geographicArea);
                        }
                        if (!geographicArea.intersects(zoomableBounds)) {
                            geographicArea = null;
                        } else {
                            final Rectangle bounds = geographicArea.getBounds();
                            if (boundingBox == null) {
                                boundingBox = bounds;
                            } else {
                                boundingBox.add(bounds);
                            }
                            hasAreas = true;
                        }
                    }
                    /*
                     * STEP 2  -  Computes the transform for the mark.  We do not really transform
                     *            the mark. Instead, we keep a reference to the untransformed mark
                     *            (which is usually the same reference for all marks) and store the
                     *            affine transform to be used later.  Storing the affine transform
                     *            always uses 6 floats, no matter how complex the shape is. Storing
                     *            a transformed mark would take more memory for any kind of mark
                     *            with more than 3 points (plus the overhead for each new objects).
                     */
                    transformedShape.setOriginalShape(iterator.markShape());
                    if (transformedShape.getOriginalShape() != null) {
                        final double amplitude = iterator.amplitude();
                        if (Double.isNaN(amplitude) || amplitude==0) {
                            transformedShape.setOriginalShape(null);
                        } else {
                            transformedShape.setTransform(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
                            double sc = amplitude/typicalScale;
                            transformedShape.scale(sc, sc);
                            transformedShape.rotate(iterator.direction());
                            if (!transformedShape.intersects(zoomableBounds)) {
                                transformedShape.setOriginalShape(null);
                            } else {
                                final Rectangle bounds = transformedShape.getBounds();
                                if (boundingBox == null) {
                                    boundingBox = bounds;
                                } else {
                                    boundingBox.add(bounds);
                                }
                                hasMarks = true;
                            }
                        }
                    }
                    /*
                     * STEP 3  -  Gets the mark icon. The bounding box for the icon is computed and
                     *            will be used for hit detection (e.g. tooltips) if no mark shape
                     *            exists for the current mark. If a mark shape exists, then the icon
                     *            bounding box will not be used for hit detection since the mark
                     *            shape should be more accurate.
                     */
                    final RenderedImage markIcon = iterator.markIcon();
                    if (markIcon != null) {
                        if (iconBounds == null) {
                            iconBounds = new Rectangle();
                        }
                        iconBounds.setBounds(markIcon.getMinX(),  markIcon.getMinY(),
                                             markIcon.getWidth(), markIcon.getHeight());
                        iconBounds.x -= iconBounds.width /2;
                        iconBounds.y -= iconBounds.height/2;
                        if (transformedShape.getOriginalShape() == null) {
                            if (iconBoundsPool == null) {
                                iconBoundsPool = new HashMap();
                            }
                            Rectangle bounds = (Rectangle) iconBoundsPool.get(iconBounds);
                            if (bounds == null) {
                                bounds = new Rectangle(iconBounds);
                                iconBoundsPool.put(bounds, bounds);
                            }
                            transformedShape.setOriginalShape(bounds);
                            transformedShape.setTransform(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
                        }
                        iconBounds.x += (int)Math.round(matrix[4]);
                        iconBounds.y += (int)Math.round(matrix[5]);
                        iconBounds.width++;
                        iconBounds.height++;
                        if (boundingBox == null) {
                            boundingBox = new Rectangle(iconBounds);
                        } else {
                            boundingBox.add(iconBounds);
                        }
                        hasMarks = true;
                    }
                    /*
                     * STEP 4  -  Creates the glyph vector. We need to extract the 'GlyphVector' in
                     *            order to compute its location.  Furthermore, drawing GlyphVectors
                     *            is faster than drawing Strings. Consequently, we stores the glyph
                     *            vectors references for next paint events.
                     */
                    GlyphVector glyphs = null;
                    float       glyphX = Float.NaN;
                    float       glyphY = Float.NaN;
                    final String label = iterator.label();
                    if (label != null) {
                        glyphs = iterator.font().createGlyphVector(fontContext, label);
                        // TODO: rename LegendPosition to TextPosition since it's that's what it's used for
                        LegendPosition labelPos = iterator.labelPosition();
                        Rectangle2D labelBounds = glyphs.getVisualBounds();
                        labelPos.setLocation(labelBounds, matrix[4], matrix[5]);
                        if (labelBounds.intersects(zoomableBounds)) {
                            glyphX = (float) labelBounds.getMinX();
                            glyphY = (float) labelBounds.getMaxY();
                            if (boundingBox == null) {
                                boundingBox = labelBounds.getBounds();
                            } else {
                                boundingBox.add(labelBounds);
                            }
                            hasLabels = true;
                        } else {
                            glyphs = null;
                        }
                    }
                    /*
                     * FINAL STEP  -  Stores the result for future use. Arrays will be created only
                     *                if needed (for example we could create the 'markShapes' array
                     *                but never create the 'glyphVectors' array). But once an array
                     *                is created, it must have the same capacity than every others.
                     */
                    if (transformedShape.getOriginalShape()==null && markIcon==null
                        && glyphs==null && geographicArea==null)
                    {
                        continue;
                    }
                    if (markIndex == null) {
                        markIndex = new int[getCount()];
                    }
                    if (numShapes >= markIndex.length) {
                        // Augment the capacity
                        final int capacity = numShapes + Math.min(Math.max(numShapes, 8), 2048);
                        markIndex = XArrays.resize(markIndex, capacity);
                        if (areaShapes != null) {
                            areaShapes = (Shape[])XArrays.resize(areaShapes, capacity);
                        }
                        if (markShapes != null) {
                            markShapes     = (Shape[])XArrays.resize(markShapes, capacity);
                            markTransforms = XArrays.resize(markTransforms, capacity*TRANSFORM_RECORD_LENGTH);
                        }
                        if (glyphVectors != null) {
                            glyphVectors   = (GlyphVector[]) XArrays.resize(glyphVectors, capacity);
                            glyphPositions = XArrays.resize(glyphPositions, capacity*2);
                        }
                    }
                    // STEP 1  -  Geographic areas
                    if (geographicArea != null) {
                        if (areaShapes == null) {
                            areaShapes = new Shape[markIndex.length];
                        }
                        areaShapes[numShapes] = geographicArea;
                    }
                    // STEP 2,3  -  Mark shapes or icons bounding box
                    if (transformedShape.getOriginalShape() != null) {
                        if (markShapes == null) {
                            markShapes     = new Shape[markIndex.length];
                            markTransforms = new float[markIndex.length*TRANSFORM_RECORD_LENGTH];
                        }
                        transformedShape.getMatrix(markTransforms, numShapes*TRANSFORM_RECORD_LENGTH);
                        markShapes[numShapes] = transformedShape.getOriginalShape();
                    }
                    // STEP 4  - Gylph vectors
                    if (glyphs != null) {
                        if (glyphVectors == null) {
                            glyphVectors   = new GlyphVector[markIndex.length];
                            glyphPositions = new float[markIndex.length*2];
                        }
                        glyphVectors  [  numShapes  ] = glyphs;
                        glyphPositions[2*numShapes+0] = glyphX;
                        glyphPositions[2*numShapes+1] = glyphY;
                    }
                    markIndex[numShapes++] = iterator.getIteratorPosition();
                }
                //
                // LOOP FINISHED  -  Trim to size and clear unused arrays.
                //
                if (!hasAreas) {
                    areaShapes = null;
                }
                if (!hasMarks) {
                    markShapes     = null;
                    markTransforms = null;
                }
                if (!hasLabels) {
                    glyphVectors   = null;
                    glyphPositions = null;
                }
                if (numShapes == 0) {
                    markIndex = null;
                }
                if (markIndex != null) {
                    markIndex = XArrays.resize(markIndex, numShapes);
                }
                if (areaShapes != null) {
                    areaShapes = (Shape[])XArrays.resize(areaShapes, numShapes);
                }
                if (markShapes != null) {
                    markShapes = (Shape[])XArrays.resize(markShapes, numShapes);
                    markTransforms = XArrays.resize(markTransforms, numShapes*TRANSFORM_RECORD_LENGTH);
                }
                if (glyphVectors != null) {
                    glyphVectors   = (GlyphVector[]) XArrays.resize(glyphVectors, numShapes);
                    glyphPositions = XArrays.resize(glyphPositions, numShapes*2);
                }
                validMask |= AREAS_MASK|MARKS_MASK|ICONS_MASK|GLYPHS_MASK;
            }
            /*
             * FINISHED LAYER VALIDATION. Now all marks, icons and labels are ready for rendering.
             * Loops over all rendered marks (which may not be all marks know to the MarkIterator)
             * and gets the arguments to be sent to MarkIterator.paint(...).
             */
            if (markIndex == null) {
                return;
            }
            Point2D.Float  labelXY = null;
            AffineTransform iconXY = null;
            for (int i=0; i<markIndex.length; i++) {
                iterator.setIteratorPosition(markIndex[i]);
                RenderedImage   icon = iterator.markIcon();
                Shape geographicArea = null;
                Shape      markShape = null;
                GlyphVector    label = null;
                if (areaShapes != null) {
                    geographicArea = areaShapes[i];
                }
                if (markShapes != null) {
                    transformedShape.setOriginalShape(markShapes[i]);
                    if (transformedShape.getOriginalShape() != null) {
                        transformedShape.setTransform(markTransforms, i*TRANSFORM_RECORD_LENGTH);
                        markShape = transformedShape;
                    }
                }
                if (icon != null) {
                    if (iconXY == null) {
                        iconXY = new AffineTransform();
                    }
                    iconXY.setToTranslation(transformedShape.getTranslateX() -0.5*icon.getWidth(),
                                            transformedShape.getTranslateY() -0.5*icon.getHeight());
                }
                if (glyphVectors!=null && glyphVectors[i]!=null) {
                    if (labelXY == null) {
                        labelXY = new Point2D.Float();
                    }
                    label     = glyphVectors  [  i  ];
                    labelXY.x = glyphPositions[2*i+0];
                    labelXY.y = glyphPositions[2*i+1];
                }
                iterator.paint(g2, geographicArea, markShape, icon, iconXY, label, labelXY);
            }
        } finally {
            g2.setTransform(oldTransform);
            g2.setStroke(oldStroke);
            g2.setPaint(oldPaint);
        }
//        context.addPaintedArea(boundingBox, context.getDisplayCRS());
    }

    /**
     * Tells that a single mark need to be repainted. This method can be invoked iteratively when
     * few marks changed,  and when the change was slight  (e.g. a change of color).  If a lot of
     * marks changed, then invoking {@link #repaint()} may be more effective.
     *
     * @param iterator An iterator over the mark to be repainted.
     *                 Only the current mark will be repainted.
     *
     * @task TODO: If the shape expanded or if the mark moved, we would need to repaint
     *             a bigger area. We can do this with the information provided by the
     *             iterator.
     */
    protected void repaint(final MarkIterator iterator) {
//        synchronized (getTreeLock()) {
//            if (markShapes!=null && isValid(AREAS_MASK|MARKS_MASK)) {
//                final int i = Arrays.binarySearch(markIndex, iterator.getIteratorPosition());
//                if (i < 0) {
//                    // The mark doesn't appear in current clip.
//                    return;
//                }
//                if (transformedShape == null) {
//                    transformedShape = new TransformedShape();
//                }
//                transformedShape.shape = markShapes[i];
//                if (transformedShape.shape != null) {
//                    transformedShape.setTransform(markTransforms, i*TRANSFORM_RECORD_LENGTH);
//                    repaintComponent(transformedShape.getBounds());
//                    return;
//                }
//            }
//            repaint();
//        }
    }

    /**
     * Test is all the specified bits are set in {@link #validMask}.
     *
     * @see #AREAS_MASK
     * @see #MARKS_MASK
     * @see #ICONS_MASK
     * @see #GLYPHS_MASK
     */
    private boolean isValid(final int mask) {
        assert (markShapes  !=null) == (markTransforms!=null) : validMask;
        assert (glyphVectors!=null) == (glyphPositions!=null) : validMask;
        return (validMask & mask) == mask;
    }

    /**
     * Tells that some data has changed. This method must be invoked when the internal state
     * of this object changed in such a way that its {@link #getMarkIterator MarkIterator} will
     * iterates through marks in a different order, returns different geographic areas, mark
     * shapes, icons and/or labels. This method do <strong>not</strong> invokes {@link #repaint}.
     * This is the caller's responsability to invokes <code>repaint()</code> after an
     * <code>invalidate(...)</code> call, if needed.
     *
     * @param mask A bitwise combinaison of {@link #AREAS_MASK}, {@link #MARKS_MASK},
     *        {@link #ICONS_MASK} and/or {@link #GLYPHS_MASK}.
     *
     * @see #AREAS_MASK
     * @see #MARKS_MASK
     * @see #ICONS_MASK
     * @see #GLYPHS_MASK
     */
    protected void invalidate(final int mask) {
        synchronized (getTreeLock()) {
            final int toClear = validMask & mask;
            validMask &= ~mask;
            if ((toClear & AREAS_MASK)!=0 && areaShapes!=null) {
                Arrays.fill(areaShapes,  null);
            }
            if ((toClear & MARKS_MASK)!=0 && markShapes!=null) {
                Arrays.fill(markShapes,  null);
                Arrays.fill(markTransforms, 0);
            }
            if ((toClear & GLYPHS_MASK)!=0 && glyphVectors!=null) {
                Arrays.fill(glyphVectors, null);
                Arrays.fill(glyphPositions,  0);
            }
            if (toClear != 0) {
                boundingBox = null;
            }
        }
    }

    /**
     * Tells that some data has changed in all aspects of marks.
     * This method invokes {@link #invalidate(int)} with all flags set.
     */
    protected void invalidate() {
        invalidate(AREAS_MASK | MARKS_MASK | ICONS_MASK | GLYPHS_MASK);
        assert validMask == 0 : validMask;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent prop) {
//        super.zoomChanged(change);
        
        if(prop.getPropertyName().equals(AbstractCanvas.OBJECTIVE_TO_DISPLAY_PROPERTY)){
            invalidate();
            clearCache();
            setDisplayBounds(null);
        }
        
//        if (change == null) {
//            invalidate();
//        } else if (!change.isIdentity()) {
//            /*
//             * TODO: We could add an optimization here:  apply the change on all transforms
//             *       instead of invalidating the whole layer. A starting point could be the
//             *       following code:
//             *
//             *       if (transformedShape == null) {
//             *           transformedShape = new TransformedShape();
//             *       }
//             *       for (int i=0; i<markTransforms.length; i+=TRANSFORM_RECORD_LENGTH) {
//             *           transformedShape.setTransform(markTransforms, i);
//             *           transformedShape.preConcatenate(change);
//             *           transformedShape.getMatrix(markTransforms, i);
//             *       }
//             *
//             *       However, some issues need to be solved: 1) We want to apply the full transform
//             *       (translation, scale, rotation) on the mark positions.   However, we don't want
//             *       to scale the mark shapes. But we do want to apply the rotation...   2) Current
//             *       'Renderer' implementation does not give us exactly the "change" transform, but
//             *       rather a slightly scaled one.
//             */
//            invalidate();
//        }
    }
    
    /**
     * Efface des informations qui avaient été conservées dans une mémoire cache.
     * Cette méthode est automatiquement appelée lorsqu'il a été déterminé que cette
     * couche ne sera plus affichée avant un certain temps.
     */
    void clearCache() {
        assert Thread.holdsLock(getTreeLock());
        validMask        = 0;
        glyphVectors     = null;
        glyphPositions   = null;
        markIndex        = null;
        areaShapes       = null;
        markShapes       = null;
        markTransforms   = null;
        transformedShape = null;
        boundingBox      = null;
        typicalAmplitude = Double.NaN;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    ////////////    EVENTS (note: may be moved out of this class in a future version)    ////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Temporary point for mouse events.
     */
    private transient Point2D point;
    
}
