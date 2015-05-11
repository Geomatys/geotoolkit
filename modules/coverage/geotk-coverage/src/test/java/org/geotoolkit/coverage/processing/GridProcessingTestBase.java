/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.coverage.processing;

import java.util.Collections;
import java.util.Map;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.util.logging.Level;
import javax.media.jai.RenderedOp;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.opengis.referencing.crs.SingleCRS;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.grid.Viewer;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageTestBase;
import org.apache.sis.referencing.crs.DefaultDerivedCRS;
import org.geotoolkit.referencing.operation.MathTransforms;

import org.apache.sis.internal.referencing.provider.Affine;
import org.apache.sis.referencing.operation.DefaultConversion;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;
import static java.lang.StrictMath.*;


/**
 * Base class for grid processing tests. This class provides a few convenience
 * methods performing some operations on {@link GridCoverage2D}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.02
 *
 * @since 2.1
 */
public abstract strictfp class GridProcessingTestBase extends GridCoverageTestBase {
    /**
     * Creates a new test suite for the given class.
     *
     * @param testing The class to be tested.
     */
    protected GridProcessingTestBase(final Class<?> testing) {
        super(testing);
    }

    /**
     * Rotates the {@linkplain #coverage current coverage} by the given angle. This
     * method replaces the coverage CRS by a derived one containing the rotated axes.
     *
     * @param  angle The rotation angle, in degrees.
     */
    protected final void rotate(final double angle) {
        final AffineTransform atr = AffineTransform.getRotateInstance(toRadians(angle));
        atr.concatenate(getAffineTransform(coverage));
        final MathTransform tr = MathTransforms.linear(atr);
        SingleCRS crs = (SingleCRS) coverage.getCoordinateReferenceSystem();
        final Map<String, String> name = Collections.singletonMap(DefaultDerivedCRS.NAME_KEY, "Rotation " + angle + "°");
        crs = new DefaultDerivedCRS(name, crs, new DefaultConversion(name, new Affine(), tr, null), crs.getCoordinateSystem());
        resample(crs, null, null, true);
    }

    /**
     * Resamples the {@linkplain #coverage current coverage} by a new coverage using
     * the specified CRS.
     *
     * @param targetCRS The target CRS, or {@code null} if the same.
     * @param geometry  The target geometry, or {@code null} if the same.
     * @param hints     An optional set of hints, or {@code null} if none.
     * @param useGeophysics {@code true} for resampling the geophysics view.
     */
    protected final void resample(final CoordinateReferenceSystem targetCRS,
                                  final GridGeometry2D            geometry,
                                  final Hints                     hints,
                                  final boolean                   useGeophysics)
    {
        final AbstractCoverageProcessor processor = AbstractOperation.getProcessor(hints);
        final String arg1, arg2;
        final Object value1, value2;
        if (targetCRS != null) {
            arg1 = "CoordinateReferenceSystem";
            value1 = targetCRS;
            if (geometry != null) {
                arg2 = "GridGeometry";
                value2 = geometry;
            } else {
                arg2 = "InterpolationType";
                value2 = "bilinear";
            }
        } else {
            arg1 = "GridGeometry";
            value1 = geometry;
            arg2 = "InterpolationType";
            value2 = "bilinear";
        }
        coverage = coverage.view(useGeophysics ? ViewType.GEOPHYSICS : ViewType.PACKED);
        final ParameterValueGroup param = processor.getOperation("Resample").getParameters();
        param.parameter("Source").setValue(coverage);
        param.parameter(arg1).setValue(value1);
        param.parameter(arg2).setValue(value2);
        coverage = (GridCoverage2D) processor.doOperation(param);
    }

    /**
     * Resamples the {@linkplain #coverage current coverage} to the specified CRS using the specified
     * hints. The result will be displayed in a window if {@link #viewEnabled} is set to {@code true}.
     *
     * @param targetCRS The target CRS, or {@code null} if the same.
     * @param geometry  The target geometry, or {@code null} if the same.
     * @param hints     An optional set of hints, or {@code null} if none.
     * @param useGeophysics {@code true} for projecting the geophysics view.
     * @return The operation name which was applied on the image, or {@code null} if none.
     */
    protected final String showResampled(final CoordinateReferenceSystem targetCRS,
                                         final GridGeometry2D            geometry,
                                         final Hints                     hints,
                                         final boolean                   useGeophysics)
    {
        resample(targetCRS, geometry, hints, useGeophysics);
        final RenderedImage image = coverage.getRenderedImage();
        String operation = null;
        if (image instanceof RenderedOp) {
            operation = ((RenderedOp) image).getOperationName();
            AbstractCoverageProcessor.LOGGER.log(Level.FINE, "Applied \"{0}\" JAI operation.", operation);
        }
        coverage = coverage.view(ViewType.PACKED);
        if (viewEnabled) {
            /*
             * Note: In current Resample implementation, simple affine transforms like
             *       translations will not be visible with the simple viewer used here.
             *       It would be visible however with more elaborated viewers.
             */
            Viewer.show(coverage, operation);
        } else {
            // Forces computation in order to check if an exception is thrown.
            assertNotNull(coverage.getRenderedImage().getData());
        }
        return operation;
    }

    /**
     * Performs an affine transformation on the {@linkplain #coverage current coverage}.
     * The transformation is a translation by 5 units along x and y axes. The result will
     * be displayed in a window if {@link #viewEnabled} is set to {@code true}.
     *
     * @param hints
     *          An optional set of hints, or {@code null} if none.
     * @param useGeophysics
     *          {@code true} for performing the operation on the geophysics view.
     * @param asCRS
     *          The expected operation name if the resampling is performed as a CRS change.
     * @param asGG
     *          The expected operation name if the resampling is performed as a Grid Geometry change.
     */
    protected final void showTranslated(final Hints    hints,
                                        final boolean  useGeophysics,
                                        final String   asCRS,
                                        final String   asGG)
    {
//        final AffineTransform atr = AffineTransform.getTranslateInstance(5, 5);
//        atr.concatenate(getAffineTransform(coverage));
//        final MathTransform tr = MathTransforms.linear(atr);
//        SingleCRS crs = (SingleCRS) coverage.getCoordinateReferenceSystem();
//        crs = new DefaultDerivedCRS("Translated", crs, tr, crs.getCoordinateSystem());
//        assertEquals(asCRS, showResampled(crs, null, hints, useGeophysics));
//
//        // Same operation, given the translation in the GridGeometry argument rather than the CRS.
//        final GridGeometry2D gg = new GridGeometry2D(null, tr, null);
//        assertEquals(asGG, showResampled(null, gg, hints, useGeophysics));

        // TODO: we should probably invoke "assertRasterEquals" with both coverages.
    }
}
