/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display.canvas;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Collections;

import org.opengis.display.primitive.Graphic;
import org.opengis.display.canvas.CanvasState;
import org.opengis.display.container.GraphicsContainer;
import org.opengis.util.FactoryException;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.DerivedCRS;
import org.opengis.referencing.crs.GeneralDerivedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.TransformedDirectPosition;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.factory.ReferencingFactoryContainer;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;
import org.geotoolkit.referencing.operation.transform.IdentityTransform;
import org.geotoolkit.display.primitive.AbstractReferencedGraphic;
import org.geotoolkit.referencing.crs.DefaultDerivedCRS;
import org.geotoolkit.referencing.operation.DefaultMathTransformFactory;


/**
 * A canvas implementation with default support for Coordinate Reference System (CRS) management.
 * This abstract class provides some common facilities for various implementations. This class
 * has no dependencies to the AWT or <cite>Java2D</cite> toolkits. Subclasses can choose an other
 * graphics toolkit (e.g. SWT) if they wish, including a 3D one.
 * <p>
 * Note that because this class is not tied to any widget toolkit, it has
 * no idea about what are the widget visible area bounds. For this class, the
 * {@linkplain GraphicsContainer#getGraphicsEnvelope() canvas envelope}
 * is an envelope that completly encloses all graphic primitives, regardless of any map scale
 * or zoom factor. Subclasses like {@link ReferencedCanvas2D} will restrict that to an envelope
 * that encloses only the visible part of this canvas.
 *
 * @module pending
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author johann Sorel (Geomatys)
 */
public abstract class ReferencedCanvas extends AbstractCanvas {
    
    /**
     * A set of {@link MathTransform}s from various source CRS. The target CRS must be the
     * {@linkplain #getObjectiveCRS objective CRS} for all entries. Keys are source CRS.
     * This map is used only in order to avoid the costly call to
     * {@link CoordinateOperationFactory#createOperation} as much as possible. If a
     * transformation is not available in this collection, then the usual factory will be used.
     */
    private final transient Map<CoordinateReferenceSystem,MathTransform> transforms =
            new HashMap<CoordinateReferenceSystem,MathTransform>();

    protected CoordinateReferenceSystem objectiveCRS;

    /**
     * The display coordinate reference system.
     *
     * @see #getDisplayCRS
     * @see #setDisplayCRS
     */
    protected DerivedCRS displayCRS;

    /**
     * The device coordinate reference system.
     *
     * @see #getDeviceCRS
     * @see #setDeviceCRS
     */
    protected DerivedCRS deviceCRS;

    /**
     * Properties for the {@linkplain #displayCRS display CRS}. They are saved here because
     * {@link #displayCRS} will be recreated often (everytime the zoom change).
     */
    private Map<String,?> displayProperties;

    /**
     * Properties for the {@linkplain #deviceCRS device CRS}. They are saved here because
     * {@link #deviceCRS} may be recreated often (everytime the zoom change).
     */
    private Map<String,?> deviceProperties;

    /**
     * A temporary position used for coordinate transformations from an arbitrary CRS to the
     * objective CRS. This position CRS should always be identical to the {@linkplain #graphicsEnvelope}
     * CRS. This object will be created when first needed.
     */
    private transient TransformedDirectPosition objectivePosition;

    /**
     * A temporary position used for coordinate transformations from an arbitrary CRS to the
     * display CRS. This object will be created when first needed.
     */
    private transient TransformedDirectPosition displayPosition;

    /**
     * The {@code "affine"} operation method. Cached here because used often. Will be created
     * in same time than {@link #crsFactories}, since they are usually needed together.
     */
    private transient OperationMethod affineMethod;

    /**
     * Factories for CRS objects creation, coordinate operations and math transforms.
     * Will be created when first needed. The actual instance is {@link #hints} dependent.
     */
    private transient ReferencingFactoryContainer crsFactories;

    /**
     * The coordinate operation factory. Will be created when first needed.
     * The actual instance is {@link #hints} dependent.
     */
    private transient CoordinateOperationFactory opFactory;

    /**
     * {@code true} if this canvas or graphic has {@value #SCALE_PROPERTY} properties listeners.
     * Used in order to reduce the amount of {@link PropertyChangeEvent} objects created in the
     * common case where no listener have interest in this property. This optimisation may be
     * worth since a {@value #SCALE_PROPERTY} property change event is sent for every graphics
     * everytime a zoom change.
     *
     * @see #listenersChanged
     */
    private boolean hasScaleListeners;

    /**
     * {@code true} if this canvas has
     * {@value org.geotoolkit.display.canvas.DisplayObject#DISPLAY_CRS_PROPERTY} properties
     * listeners. Used in order to reduce the amount of {@link PropertyChangeEvent} objects
     * created in the common case where no listener have interest in this property. May be
     * a significant optimisation, since this property change everytime the zoom change.
     *
     * @see #listenersChanged
     */
    private boolean hasDisplayListeners;

    /**
     * Creates an initially empty canvas with the specified objective CRS.
     *
     * @param objectiveCRS The initial objective CRS.
     * @param hints        The initial set of hints, or {@code null} if none.
     */
    protected ReferencedCanvas(final CoordinateReferenceSystem objectiveCRS,
                               final Hints hints){
        super(hints);
        this.objectiveCRS = objectiveCRS;
    }

    /**
     * Returns a copy of the current state of this {@code Canvas}. The default implementation
     * returns a {@link CanvasState} with a center position inferred from the canvas
     * envelope.
     */
    @Override
    public synchronized CanvasState getState() {
        //replace by property events
        //TODO remove this methd from geoapi
        //TODO check also if CanvasEvent must hold a CanvasState or not, It doesnt seems usefull
        //a simple propertyListener can be enough
        throw new IllegalStateException("This method will be removed from geoapi");
    }

    /**
     * Returns {@code true} if the given coordinate is visible on this {@code Canvas}. The default
     * implementation checks if the coordinate is inside the canvas envelope. Subclasses should
     * override this method if a more accurate check is possible.
     */
    @Override
    public synchronized boolean isVisible(final DirectPosition coordinate) {
        try {
            //todo uncorrect check, should check against the canvas envelope, not container
            return getContainer().getGraphicsEnvelope().contains(toObjectivePosition(coordinate));
        } catch (TransformException e) {
            /*
             * A typical reason for transformation failure is a coordinate point outside the area
             * of validity. If the specified point is outside the area of validity of the CRS used
             * by this canvas, then we can reasonably assume that it is outside the canvas envelope
             * as well.
             */
            return false;
        }
    }

    /**
     * Invoked when a property change listener has been {@linkplain #addPropertyChangeListener
     * added} or {@linkplain #removePropertyChangeListener removed}.
     */
    @Override
    protected void listenersChanged() {
        super.listenersChanged();
        hasScaleListeners    = propertyListeners.hasListeners(SCALE_PROPERTY);
        hasDisplayListeners  = propertyListeners.hasListeners(DISPLAY_CRS_PROPERTY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearCache() {
        super.clearCache();
        transforms.clear();
        opFactory         = null;
        crsFactories      = null;
        affineMethod      = null;
        objectivePosition = null;
        displayPosition   = null;
    }

    
    protected abstract RenderingContext getRenderingContext();
    
    //----------------------CRS & MathTransform methods-------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public CoordinateReferenceSystem getObjectiveCRS() {
        return objectiveCRS;
    }

    /**
     * Sets the objective Coordinate Reference System for this {@code Canvas}.
     */
    @Override
    public synchronized void setObjectiveCRS(final CoordinateReferenceSystem crs)
            throws TransformException {
        final CoordinateReferenceSystem oldCRS = getObjectiveCRS();
        
        if (CRS.equalsIgnoreMetadata(crs, oldCRS)) {
            return;
        }
        
        objectiveCRS = crs;

        /*
         * Compute the display CRS from the new objective CRS (keeping the same zoom factor
         * than the previous display CRS), but do not store the result yet.  We compute the
         * display CRS now in order to avoid any change in case of failure, and will invoke
         * setDisplayCRS(...) only after all transformation steps below will succeed.
         */
        DerivedCRS displayCRS = this.displayCRS;
        if (displayCRS != null) try {
            final Conversion objectiveToDisplay = displayCRS.getConversionFromBase();
            displayCRS = /*getFactoryGroup().getCRSFactory().*/createDerivedCRS(
                    displayProperties, objectiveToDisplay, crs, displayCRS.getCoordinateSystem());
        } catch (FactoryException exception) {
            throw new TransformException(Errors.format(
                    Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), exception);
        }

        /*
         * The CRS change has been successful for all graphic primitives.
         * Now updates internal states.
         */
        clearCache();
        updateNormalizationFactor(crs);
        computeEnvelope(ReferencedCanvas.class, "setObjectiveCRS");
        /*
         * Set the display CRS last because it may fires a property change event,
         * and we don't want to expose our changes before they are completed.
         */
        if (displayCRS != null) {
            setDisplayCRS(displayCRS);
        }
        assert getObjectiveCRS() == crs;
        propertyListeners.firePropertyChange(OBJECTIVE_CRS_PROPERTY, oldCRS, crs);
    }

    /**
     * Updates the normalization factor after a CRS change. Normalization factor doesn't
     * apply to this basic {@code ReferencedCanvas} class. This method is merely a hook
     * for {@link ReferencedCanvas2D} implementation.
     *
     * @see ReferencedCanvas2D#updateNormalizationFactor
     */
    protected void updateNormalizationFactor(final CoordinateReferenceSystem crs) {
    }

    /**
     * Recomputes inconditionnaly the {@linkplain #graphicsEnvelope}. The envelope will be computed
     * from the value provided by {@link ReferencedGraphic#envelope} for all graphics.
     * <p>
     * <strong>NOTE:</strong> Callers are responsible for firing an event after the envelope change.
     * This method doesn't fire an {@value org.geotoolkit.display.canvas.DisplayObject#ENVELOPE_PROPERTY}
     * change event itself because this step is often only an intermediate step (see for example
     * {@link #setObjectiveCRS}).
     *
     * @param  sourceClassName  The caller's class name,  for logging purpose.
     * @param  sourceMethodName The caller's method name, for logging purpose.
     */
    private void computeEnvelope(final Class<?> sourceClassName, final String sourceMethodName) {
//        assert Thread.holdsLock(this);
//        graphicsEnvelope.setToNull();
        CoordinateReferenceSystem lastCRS = null;
        MathTransform           transform = null;
        
        final Collection<Graphic>  graphics;
        if(getContainer() != null) graphics = getContainer().graphics();
        else graphics = Collections.EMPTY_LIST;

        for (final Graphic candidate : graphics) {
            if (!(candidate instanceof AbstractReferencedGraphic)) {
                continue;
            }
            final AbstractReferencedGraphic graphic = (AbstractReferencedGraphic) candidate;
            final Envelope candidateEnvelope = graphic.getEnvelope();
            /*
             * In theory, the Graphic should use the same CRS than this Canvas.
             * However, as a safety, we will check for coordinate transformations anyway.
             */
            //TODO fix this : graphicCRS should always have the same as CRS as the canvas
            final CoordinateReferenceSystem crs = candidateEnvelope.getCoordinateReferenceSystem();
            try {
                
                if (!CRS.equalsIgnoreMetadata(crs, lastCRS)) {
                    //optimisation : only recalculate transform if different from last one.
                    transform = getMathTransform(crs, getObjectiveCRS(), sourceClassName, sourceMethodName);
                    lastCRS = crs;
                }
                
//                final GeneralEnvelope bounds = CRS.transform(transform, candidateEnvelope);
                
                
//                if (graphicsEnvelope.isNull()) {
//                    graphicsEnvelope.setEnvelope(bounds);
//                } else {
//                    graphicsEnvelope.add(bounds);
//                }
            } catch (FactoryException exception) {
                handleException(sourceClassName, sourceMethodName, exception);
            }
//            catch (TransformException exception) {
//                handleException(sourceClassName, sourceMethodName, exception);
//                // Continue. The envelope for this graphic will be ignored.
//            }
        }
    }

    /**
     * Returns the Coordinate Reference System associated with the display of this {@code Canvas}.
     * Unless otherwise specified by a call to {@link #setDisplayCRS setDisplayCRS}, the default
     * display CRS assumes a rendering on a flat screen with axis oriented as in the
     * <cite>Java2D</cite> default {@linkplain java.awt.Graphics2D user space}: Coordinates are
     * in "dots" (about 1/72 of inch), <var>x</var> values increasing right and <var>y</var>
     * values increasing <strong>down</strong>.
     */
    @Override
    public synchronized CoordinateReferenceSystem getDisplayCRS() {
        if (displayCRS == null) try {
            final ReferencingFactoryContainer crsFactories;
            final CoordinateReferenceSystem objectiveCRS;
            final CoordinateSystem displayCS;
            final Conversion conversion;
            final int sourceDim, targetDim;
            final Matrix identity;
            final MathTransform mt;

            crsFactories      = getFactoryGroup();
            objectiveCRS      = getObjectiveCRS();
            displayCS         = DefaultCartesianCS.DISPLAY;
            sourceDim         = objectiveCRS.getCoordinateSystem().getDimension();
            targetDim         = displayCS.getDimension();
            identity          = MatrixFactory.create(targetDim+1, sourceDim+1);
            mt                = crsFactories.getMathTransformFactory().createAffineTransform(identity);
            displayProperties = AbstractIdentifiedObject.getProperties(displayCS, null);
            conversion        = new DefiningConversion(displayProperties, affineMethod, mt);
            displayCRS        = crsFactories.getCRSFactory().createDerivedCRS(
                                    displayProperties, objectiveCRS, conversion, displayCS);
            // TODO: above call is heavy; maybe we should use direct instantiation.
        } catch (FactoryException exception) {
            /*
             * Should never happen, because the CRS that we tried to create is somewhat basic
             * (an identity transform). Rethrows as an illegal state, since this exception is
             * probably caused by some misconfiguration.
             */
            throw new IllegalStateException(Errors.format(Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM));
            // TODO: include the cause when we will be allowed to compile for J2SE 1.5.
        }
        assert displayProperties!=null && !displayProperties.isEmpty() : displayProperties;
        assert displayCRS.getBaseCRS() == getObjectiveCRS() : displayCRS;
        return displayCRS;
    }

    /**
     * Sets the display Coordinate Reference System for this {@code Canvas}. The {@linkplain
     * DerivedCRS#getBaseCRS base CRS} must be the {@linkplain #getObjectiveCRS objective CRS},
     * and the {@linkplain DerivedCRS#getConversionFromBase conversion from base} is related to
     * the map scale or zoom. This method is usually invoked by subclasses rather than users, as
     * a consequence of zoom changes. For example this method may be invoked as a side-effect of
     * the following methods:
     * <p>
     * <ul>
     *   <li>{@link ReferencedCanvas#setObjectiveCRS}</li>
     *   <li>{@link ReferencedCanvas#setObjectiveToDisplayTransform(Matrix)}</li>
     * </ul>
     * <p>
     * This method fires a {@value AbstractCanvas#DISPLAY_CRS_PROPERTY}
     * property change event.
     *
     * @param  crs The display coordinate reference system.
     * @throws TransformException If the data can't be transformed.
     */
    protected synchronized void setDisplayCRS(final DerivedCRS crs) throws TransformException {
        final CoordinateReferenceSystem oldCRS = displayCRS;
        try {
            displayCRS = validateBaseCRS(crs, getObjectiveCRS());
        } catch (FactoryException exception) {
            throw new TransformException(Errors.format(
                        Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), exception);
        }
        if (displayProperties == null) {
            displayProperties = AbstractIdentifiedObject.getProperties(crs, null);
        }
        displayPosition = null;
        if (hasDisplayListeners) {
            propertyListeners.firePropertyChange(DISPLAY_CRS_PROPERTY, oldCRS, crs);
        }
        /*
         * In theory, the 'deviceCRS' has changed too  since it need to be rebuilt with the new
         * 'displayCRS' as the base CRS. However, because must users will not care about device
         * CRS,  we do not recompute it here  (remember that this 'setDisplayCRS' method may be
         * invoked often). A new 'deviceCRS' will be automatically recomputed by 'getDeviceCRS()'
         * if needed.
         */
    }

    /**
     * Returns the Coordinate Reference System associated with the device of this {@code Canvas}.
     */
    @Override
    public synchronized CoordinateReferenceSystem getDeviceCRS() {
        final DerivedCRS displayCRS = (DerivedCRS) getDisplayCRS();
        if (deviceCRS == null) try {
            final CoordinateSystem deviceCS;
            final Conversion conversion;
            final Matrix identity;
            final MathTransform mt;

            deviceCS         = displayCRS.getCoordinateSystem();
            identity         = MatrixFactory.create(deviceCS.getDimension()+1);
            mt               = getFactoryGroup().getMathTransformFactory().createAffineTransform(identity);
            deviceProperties = AbstractIdentifiedObject.getProperties(deviceCS, null);
            conversion       = new DefiningConversion(deviceProperties, affineMethod, mt);
            deviceCRS        = new DefaultDerivedCRS(deviceProperties, conversion, displayCRS, mt, deviceCS);
            // Note: the above call does not use MathTransformFactory, because it is invoked often
            // and the call to WeakHashSet.unique(...) in DefaultMathTransformFactory is costly.
        } catch (FactoryException exception) {
            /*
             * Should never happen, because the CRS that we tried to create is somewhat basic
             * (an identity transform). Rethrows as an illegal state, since this exception is
             * probably caused by some misconfiguration.
             */
            throw new IllegalStateException(Errors.format(Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM));
            // TODO: include the cause when we will be allowed to compile for J2SE 1.5.
        }
        /*
         * If the 'displayCRS' has changed since the last time that this method has been invoked,
         * then recomputes a new 'deviceCRS' using the new 'displayCRS' as its base and the same
         * conversion than the old CRS.
         */
        if (deviceCRS.getBaseCRS() != displayCRS) try {
            final Conversion displayToDevice = deviceCRS.getConversionFromBase();
            deviceCRS = /*getFactoryGroup().getCRSFactory().*/createDerivedCRS(
                    deviceProperties, displayToDevice, displayCRS, deviceCRS.getCoordinateSystem());
        } catch (FactoryException exception) {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM));
            // TODO: include the cause when we will be allowed to compile for J2SE 1.5.
        }
        assert deviceProperties!=null && !deviceProperties.isEmpty() : deviceProperties;
        assert deviceCRS.getBaseCRS() == getDisplayCRS() : deviceCRS;
        assert deviceCRS.getCoordinateSystem() == displayCRS.getCoordinateSystem() : deviceCRS;
        return deviceCRS;
    }

    /**
     * Sets the device Coordinate Reference System for this {@code Canvas}.
     * This method is usually invoked by subclasses rather than users. At the difference of
     * {@link #setDisplayCRS setDisplayCRS(...)} (which is invoked everytime the zoom change),
     * this method is usually invoked only once since the
     * {@linkplain DerivedCRS#getConversionFromBase conversion} from display CRS to derived CRS
     * is usually constant.
     *
     * @param  crs The device coordinate reference system.
     * @throws TransformException If the data can't be transformed.
     */
    protected synchronized void setDeviceCRS(final DerivedCRS crs) throws TransformException {
        try {
            deviceCRS = validateBaseCRS(crs, getDisplayCRS());
        } catch (FactoryException exception) {
            throw new TransformException(Errors.format(
                        Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), exception);
        }
        if (deviceProperties == null) {
            deviceProperties = AbstractIdentifiedObject.getProperties(crs, null);
        }
    }

    /**
     * Ensures that the {@linkplain DerivedCRS#getBaseCRS base CRS} for the specified derived CRS
     * is the expected one. If this is not the case, attempt to create a new CRS derived from the
     * expected base CRS.
     *
     * @param  crs The derived CRS to check.
     * @param  baseCRS The expected base CRS.
     * @return {@code crs}, or a new one if the CRS was not derived from the expected base CRS.
     * @throws FactoryException if the CRS can't be created.
     */
    private DerivedCRS validateBaseCRS(DerivedCRS crs, final CoordinateReferenceSystem baseCRS)
            throws FactoryException {
        if (crs.getBaseCRS() != baseCRS) {
            final CoordinateOperationFactory factory = getCoordinateOperationFactory();
            final CoordinateOperation operation = factory.createOperation(baseCRS, crs);
            final Conversion conversion;
            /*
             * Gets the conversion using a 'try...catch' block instead of testing with
             * 'if (operation instanceof Conversion)' in order to include the cause in
             * the stack trace, so the user can get more tips why the CRS is invalid.
             */
            try {
                conversion = (Conversion) operation;
            } catch (ClassCastException exception) {
                throw new FactoryException(Errors.format(
                            Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), exception);
            }
            crs = /*getFactoryGroup().getCRSFactory().*/createDerivedCRS(
                    AbstractIdentifiedObject.getProperties(crs, null),
                    conversion, baseCRS, crs.getCoordinateSystem());
        }
        return crs;
    }

    /**
     * Temporary placeholder for future GeoAPI method.
     *
     * @todo Delete this method when the equivalent GeoAPI method will be available (GeoAPI 2.1?).
     *       Note: make sure that our implementation will use the math transform when available
     *       instead of creating a new one from the parameter values, while ignoring the baseCRS.
     *       This would be different from {@code createProjectedCRS}, which uses a more heuristic
     *       algorithm.
     */
    private static DerivedCRS createDerivedCRS(final Map                 properties,
                                               final Conversion  conversionFromBase,
                                               final CoordinateReferenceSystem base,
                                               final CoordinateSystem     derivedCS)
            throws FactoryException {
        return new org.geotoolkit.referencing.crs.DefaultDerivedCRS(properties,
                conversionFromBase, base, conversionFromBase.getMathTransform(), derivedCS);
    }

    /**
     * Creates a derived CRS for a {@linkplain #getDisplayCRS display CRS} or
     * a {@linkplain #getDeviceCRS device CRS}. The coordinate system will be
     * inherited from the current display or device CRS. The operation method
     * will always be the affine transform.
     *
     * @param  device    {@code true} for creating a device CRS, or {@code false} for a display one.
     * @param  transform The affine transform from base to the derived CRS as matrix.
     * @return The derived CRS.
     * @throws FactoryException if the CRS can't be created.
     *
     * @see #setObjectiveToDisplayTransform(Matrix)
     */
    private DerivedCRS createDerivedCRS(final boolean device, final Matrix transform)
            throws FactoryException {
//        assert Thread.holdsLock(this);
        DerivedCRS crs;
        Map<String,?> properties;
        if (device) {
            crs = (DerivedCRS) getDeviceCRS();
            properties = deviceProperties;
        } else {
            crs = (DerivedCRS) getDisplayCRS();
            properties = displayProperties;
        }
        final MathTransform mt = getFactoryGroup().getMathTransformFactory().createAffineTransform(transform);
        final Conversion conversion = new DefiningConversion(properties, affineMethod, mt);
        crs = new DefaultDerivedCRS(properties, conversion, crs.getBaseCRS(), mt, crs.getCoordinateSystem());
        // Note: the above call does not use MathTransformFactory, because it is invoked often
        // and the call to WeakHashSet.unique(...) in DefaultMathTransformFactory is costly.
        return crs;
    }

    /**
     * Sets the {@linkplain #getDisplayCRS display} to {@linkplain #getDeviceCRS device} transform
     * to the specified affine transform. This method creates a new device CRS and invokes
     * {@link #setDeviceCRS} with the result.
     *
     * @param  transform The {@linkplain #getDisplayCRS display} to
     *         {@linkplain #getDeviceCRS device} affine transform as a matrix.
     * @throws TransformException if the transform can not be set to the specified value.
     */
    protected synchronized void setDisplayToDeviceTransform(final Matrix transform)
            throws TransformException {
        final DerivedCRS crs;
        try {
            crs = createDerivedCRS(true, transform);
        } catch (FactoryException exception) {
            // Should not occurs for an affine transform, since it is quite a basic one.
            throw new TransformException(exception.getLocalizedMessage(), exception);
        }
        setDeviceCRS(crs);
    }

    /**
     * Sets the {@linkplain #getObjectiveCRS objective} to {@linkplain #getDisplayCRS display}
     * transform to the specified affine transform. This method creates a new display CRS and
     * invokes {@link #setDisplayCRS} with the result.
     *
     * @param  transform The {@linkplain #getObjectiveCRS objective} to
     *         {@linkplain #getDisplayCRS display} affine transform as a matrix.
     * @throws TransformException if the transform can not be set to the specified value.
     */
    public synchronized void setObjectiveToDisplayTransform(final Matrix transform)
            throws TransformException {
        final DerivedCRS crs;
        try {
            crs = createDerivedCRS(false, transform);
        } catch (FactoryException exception) {
            // Should not occurs for an affine transform, since it is quite a basic one.
            throw new TransformException(exception.getLocalizedMessage(), exception);
        }
        setDisplayCRS(crs);
    }

    /**
     * Sets the {@linkplain #getObjectiveCRS objective} to {@linkplain #getDisplayCRS display}
     * transform to the specified transform. The default implementation expects an affine
     * transform and delegates the work to {@link #setObjectiveToDisplayTransform(Matrix)}.
     *
     * @param  transform The {@linkplain #getObjectiveCRS objective} to
     *         {@linkplain #getDisplayCRS display} affine transform.
     * @throws TransformException if the transform can not be set to the specified value.
     */
    @Override
    public void setObjectiveToDisplayTransform(final MathTransform transform)
            throws TransformException {
        if (transform instanceof LinearTransform) {
            setObjectiveToDisplayTransform(((LinearTransform) transform).getMatrix());
        } else {
            throw new TransformException(Errors.format(Errors.Keys.NOT_AN_AFFINE_TRANSFORM));
        }
    }

    /**
     * Returns the coordinate transformation object for this {@code Canvas}. This allows the
     * {@code Canvas} to resolve conversions of coordinates between the objective and display
     * Coordinate Reference Systems.
     *
     * @return MathTransform
     */
    public synchronized MathTransform getObjectiveToDisplayTransform() {
        final DerivedCRS displayCRS = (DerivedCRS) getDisplayCRS();
        assert displayCRS.getBaseCRS().equals(getObjectiveCRS()) : displayCRS;
        return displayCRS.getConversionFromBase().getMathTransform();
    }

    /**
     * Returns the coordinate transformation object for this {@code Canvas}. This allows the
     * {@code Canvas} to resolve conversions of coordinates between the display and objective
     * Coordinate Reference Systems.
     * <p>
     * The default implementation returns the {@linkplain MathTransform#inverse inverse} of
     * the {@linkplain #getObjectiveToDisplayTransform objective to display transform}.
     *
     * @return MathTransform
     */
    public MathTransform getDisplayToObjectiveTransform() {
        try {
            return getObjectiveToDisplayTransform().inverse();
        } catch (NoninvertibleTransformException exception) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NONINVERTIBLE_TRANSFORM));
            // TODO: Add the cause when we will be allowed to compile for J2SE 1.5.
        }
    }

    /**
     * Constructs a transform between two coordinate reference systems. If a
     * {@link Hints#COORDINATE_OPERATION_FACTORY} has been provided, then the specified
     * {@linkplain CoordinateOperationFactory coordinate operation factory} will be used.
     *
     * @param  sourceCRS The source coordinate reference system.
     * @param  targetCRS The target coordinate reference system.
     * @param  sourceClassName  The caller class name, for logging purpose only.
     * @param  sourceMethodName The caller method name, for logging purpose only.
     * @return A transform from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException if the transform can't be created.
     *
     * @see DisplayObject#getRenderingHint(java.awt.RenderingHints.Key)
     * @see DisplayObject#setRenderingHint(java.awt.RenderingHints.Key, java.lang.Object)
     * @see Hints#COORDINATE_OPERATION_FACTORY
     */
    public final synchronized MathTransform getMathTransform(final CoordinateReferenceSystem sourceCRS,
                                                      final CoordinateReferenceSystem targetCRS,
                                                      final Class<?> sourceClassName,
                                                      final String sourceMethodName)
            throws FactoryException {
        /*
         * Fast check for a very common case. We will use the more general (but slower)
         * 'equalsIgnoreMetadata(...)' version implicitly in the call to factory method.
         */
        if (sourceCRS == targetCRS) {
            return IdentityTransform.create(sourceCRS.getCoordinateSystem().getDimension());
        }
        MathTransform tr;
        /*
         * Checks if the math transform is available in the cache. A majority of transformations
         * will be from 'graphicCRS' to 'objectiveCRS' to 'displayCRS'.  The cache looks for the
         * 'graphicCRS' to 'objectiveCRS' transform.
         */
        final CoordinateReferenceSystem objectiveCRS = getObjectiveCRS();
        final boolean cachedTransform = CRS.equalsIgnoreMetadata(targetCRS, objectiveCRS);
        if (cachedTransform) {
            tr = transforms.get(sourceCRS);
            if (tr != null) {
                return tr;
            }
        }
        /*
         * If one of the CRS is a derived CRS, then check if we can use directly its conversion
         * from base without using the costly coordinate operation factory. This check is worth
         * to be done since it is a very common situation. A majority of transformations will be
         * from 'objectiveCRS' to 'displayCRS', which is the case we test first. The converse
         * (transformations from 'displayCRS' to 'objectiveCRS') is less frequent and can be
         * handled by the 'transform' cache, which is why we let the factory check for it.
         */
        if (targetCRS instanceof GeneralDerivedCRS) {
            final GeneralDerivedCRS derivedCRS = (GeneralDerivedCRS) targetCRS;
            if (CRS.equalsIgnoreMetadata(sourceCRS, derivedCRS.getBaseCRS())) {
                return derivedCRS.getConversionFromBase().getMathTransform();
            }
        }
        /*
         * Now that we failed to reuse a pre-existing transform, ask to the factory
         * to create a new one. A message is logged in order to trace down the amount
         * of coordinate operations created.
         */
        final Logger logger = getLogger();
        if (logger.isLoggable(Level.FINER)) {
            // FINER is the default level for entering, returning, or throwing an exception.
            final LogRecord record = Loggings.getResources(getLocale()).getLogRecord(Level.FINER,
                    Loggings.Keys.INITIALIZING_TRANSFORMATION_$2,
                    toString(sourceCRS), toString(targetCRS));
            record.setSourceClassName (sourceClassName.getName());
            record.setSourceMethodName(sourceMethodName);
            logger.log(record);
        }
        
        
        tr = CRS.findMathTransform(sourceCRS, targetCRS, true);
        //TODO I used the CRS utility class, the following commented code, raises bursa wolf errors
//        CoordinateOperatetCoordinateOperationFactory();
//        tr = factory.createOperation(sourceCRSionFactory factory = getCoordinateOperationFactory();
//        tr = factory.createOperation(sourceCRS, targetCRS).getMathTransform();
        
        if (cachedTransform) {
            transforms.put(sourceCRS, tr);
        }
        return tr;
    }

    /**
     * Returns the coordinate operation factory. The actual instance is {@link #hints} dependent.
     */
    private CoordinateOperationFactory getCoordinateOperationFactory() throws FactoryException {
//        assert Thread.holdsLock(this);
        if (opFactory == null) {
            final Hints tmp = new Hints(hints);
            tmp.putAll(getFactoryGroup().getImplementationHints());
            opFactory = AuthorityFactoryFinder.getCoordinateOperationFactory(tmp);
        }
        return opFactory;
    }

    /**
     * Returns the factories for CRS objects creation, coordinate operations and math transforms.
     *
     * @return The factory group (never {@code null}).
     * @throws FactoryException if the affine method can't be fetched.
     */
    private ReferencingFactoryContainer getFactoryGroup() throws FactoryException {
//        assert Thread.holdsLock(this);
        if (crsFactories == null) {
            crsFactories = ReferencingFactoryContainer.instance(hints);
            affineMethod = ((DefaultMathTransformFactory) crsFactories.getMathTransformFactory()).getOperationMethod("affine");
        }
        return crsFactories;
    }

    /**
     * Returns a string representation of a coordinate reference system. This method is
     * used for formatting a logging message in {@link #getMathTransform}.
     */
    private static String toString(final CoordinateReferenceSystem crs) {
        return Classes.getShortClassName(crs) + "[\"" + crs.getName().getCode() + "\"]";
    }

    /**
     * Transforms a coordinate from an arbitrary CRS to the {@linkplain #getObjectiveCRS objective
     * CRS}. For performance reason, this method recycles always the same destination point. Do not
     * keep a reference to the returned point for a long time, since its value may be changed at
     * any time.
     *
     * @param  coordinate The direct position to transform.
     * @return The transformed direct position.
     * @throws TransformException if the transformation failed.
     */
    protected final GeneralDirectPosition toObjectivePosition(final DirectPosition coordinate)
            throws TransformException {
//        assert Thread.holdsLock(this);
        if (objectivePosition == null) {
            objectivePosition = new TransformedDirectPosition(null, getObjectiveCRS(), hints);
        }
        assert Utilities.equals(objectivePosition.getCoordinateReferenceSystem(), getObjectiveCRS());
        objectivePosition.transform(coordinate);
        return objectivePosition;
    }

    /**
     * Transforms a coordinate from an arbitrary CRS to the {@linkplain #getDisplayCRS display CRS}.
     * For performance reason, this method recycles always the same destination point. Do not keep a
     * reference to the returned point for a long time, since its value may be changed at any time.
     *
     * @param  coordinate The direct position to transform.
     * @return The transformed direct position.
     * @throws TransformException if the transformation failed.
     */
    protected final GeneralDirectPosition toDisplayPosition(final DirectPosition coordinate)
            throws TransformException {
//        assert Thread.holdsLock(this);
        if (displayPosition == null) {
            displayPosition = new TransformedDirectPosition(null, getDisplayCRS(), hints);
        }
        assert Utilities.equals(displayPosition.getCoordinateReferenceSystem(), getDisplayCRS());
        displayPosition.transform(coordinate);
        return displayPosition;
    }

}
