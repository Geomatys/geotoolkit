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
package org.geotoolkit.coverage.processing.operation;

import javax.media.jai.Interpolation;
import javax.media.jai.operator.WarpDescriptor;
import javax.media.jai.operator.AffineDescriptor;
import net.jcip.annotations.Immutable;

import org.opengis.geometry.Envelope;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.processing.Operation2D;
import org.geotoolkit.coverage.processing.CannotReprojectException;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.internal.image.ImageUtilities;
import org.geotoolkit.resources.Errors;


/**
 * Resample a grid coverage using a different grid geometry. This operation provides the following
 * functionality:
 * <p>
 * <UL>
 *   <LI><b>Resampling</b><br>
 *       The grid coverage can be resampled at a different cell resolution. Some implementations
 *       may be able to do resampling efficiently at any resolution. Also a non-rectilinear grid
 *       coverage can be accessed as rectilinear grid coverage with this operation.</LI>
 *   <LI><b>Reprojecting</b><br>
 *       The new grid geometry can have a different coordinate reference system than the underlying
 *       grid geometry. For example, a grid coverage can be reprojected from a geodetic coordinate
 *       reference system to Universal Transverse Mercator CRS.</LI>
 *   <LI><b>Subsetting</b><br>
 *       A subset of a grid can be viewed as a separate coverage by using this operation with a
 *       grid geometry which as the same geoferencing and a region. Grid envelope in the grid
 *       geometry defines the region to subset in the grid coverage.</LI>
 * </UL>
 *
 * <P><b>Name:</b>&nbsp;{@code "Resample"}<BR>
 *    <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain AffineDescriptor Affine}"</CODE>
 *            or <CODE>"{@linkplain WarpDescriptor Warp}"</CODE><BR>
 *    <b>Parameters:</b></P>
 * <table border='3' cellpadding='6' bgcolor='F4F8FF'>
 *   <tr bgcolor='#B9DCFF'>
 *     <th>Name</th>
 *     <th>Class</th>
 *     <th>Default value</th>
 *     <th>Minimum value</th>
 *     <th>Maximum value</th>
 *   </tr>
 *   <tr>
 *     <td>{@code "Source"}</td>
 *     <td>{@link org.geotoolkit.coverage.grid.GridCoverage2D}</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@code "InterpolationType"}</td>
 *     <td>{@link java.lang.CharSequence}</td>
 *     <td>"NearestNieghbor"</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@code "CoordinateReferenceSystem"}</td>
 *     <td>{@link org.opengis.referencing.crs.CoordinateReferenceSystem}</td>
 *     <td>Same as source grid coverage</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@code "GridGeometry"}</td>
 *     <td>{@link org.opengis.coverage.grid.GridGeometry}</td>
 *     <td>(automatic)</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@code "Background"}</td>
 *     <td>{@code double[]}</td>
 *     <td>(automatic)</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 * </table>
 *
 * {@section Geotoolkit.org extension}
 * The {@code "Resample"} operation use the default
 * {@link org.opengis.referencing.operation.CoordinateOperationFactory} for creating a
 * transformation from the source to the destination coordinate reference systems.
 * If a custom factory is desired, it may be supplied as a rendering hint with the
 * {@link org.geotoolkit.factory.Hints#COORDINATE_OPERATION_FACTORY} key. Rendering
 * hints can be supplied to {@link org.geotoolkit.coverage.processing.DefaultCoverageProcessor}
 * at construction time.
 * <p>
 * Geotk adds a background parameter which is not part of OGC specification. This parameter
 * specifies the color to use for pixels in the destination image that don't map to a pixel
 * in the source image. If this parameter is not specified, then it is inferred from the
 * "no data" category in the source coverage.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @see org.geotoolkit.coverage.processing.Operations#resample
 * @see WarpDescriptor
 *
 * @since 2.2
 * @module
 */
@Immutable
public class Resample extends Operation2D {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2022393087647420577L;

    /**
     * The parameter descriptor for the interpolation type.
     */
    public static final ParameterDescriptor<Object> INTERPOLATION_TYPE =
            new DefaultParameterDescriptor<>(Citations.OGC,
                "InterpolationType",                // Parameter name
                Object.class,                       // Value class (mandatory)
                null,                               // Array of valid values
                "NearestNeighbor",                  // Default value
                null,                               // Minimal value
                null,                               // Maximal value
                null,                               // Unit of measure
                false);                             // Parameter is optional

    /**
     * The parameter descriptor for the coordinate reference system.
     */
    public static final ParameterDescriptor<CoordinateReferenceSystem> COORDINATE_REFERENCE_SYSTEM =
            new DefaultParameterDescriptor<>(Citations.OGC,
                "CoordinateReferenceSystem",        // Parameter name
                CoordinateReferenceSystem.class,    // Value class (mandatory)
                null,                               // Array of valid values
                null,                               // Default value
                null,                               // Minimal value
                null,                               // Maximal value
                null,                               // Unit of measure
                false);                             // Parameter is optional

    /**
     * The parameter descriptor for the grid geometry.
     */
    public static final ParameterDescriptor<GridGeometry> GRID_GEOMETRY =
            new DefaultParameterDescriptor<>(Citations.OGC,
                "GridGeometry",                     // Parameter name
                GridGeometry.class,                 // Value class (mandatory)
                null,                               // Array of valid values
                null,                               // Default value
                null,                               // Minimal value
                null,                               // Maximal value
                null,                               // Unit of measure
                false);                             // Parameter is optional

    /**
     * The parameter descriptor for the background values.
     *
     * @since 3.16
     */
    public static final ParameterDescriptor<double[]> BACKGROUND =
            new DefaultParameterDescriptor<>(Citations.GEOTOOLKIT,
                "Background",                       // Parameter name
                double[].class,                     // Value class (mandatory)
                null,                               // Array of valid values
                null,                               // Default value
                null,                               // Minimal value
                null,                               // Maximal value
                null,                               // Unit of measure
                false);                             // Parameter is optional

    /**
     * Constructs a {@code "Resample"} operation.
     */
    public Resample() {
        super(new DefaultParameterDescriptorGroup(Citations.OGC, "Resample",
                SOURCE_0, INTERPOLATION_TYPE, COORDINATE_REFERENCE_SYSTEM, GRID_GEOMETRY, BACKGROUND
        ));
    }

    /**
     * Returns the preferred view for computation purpose.
     *
     * @todo Needs to migrate here the relevant code from {@link Resampler2D}.
     */
//  @Override
//  protected ViewType getComputationView(final ParameterValueGroup parameters) {
//      return ViewType.SAME;
//  }

    /**
     * Resamples a grid coverage. This method is invoked by
     * {@link org.geotoolkit.coverage.processing.DefaultCoverageProcessor}
     * for the {@code "Resample"} operation.
     */
    @Override
    protected Coverage doOperation(final ParameterValueGroup parameters, final Hints hints) {
        final GridCoverage2D source = (GridCoverage2D) parameters.parameter("Source").getValue();
        final double[] background = (double[]) parameters.parameter("Background").getValue();
        final Interpolation interpolation = ImageUtilities.toInterpolation(
                parameters.parameter("InterpolationType").getValue());
        CoordinateReferenceSystem targetCRS = (CoordinateReferenceSystem)
                parameters.parameter("CoordinateReferenceSystem").getValue();
        if (targetCRS == null) {
            targetCRS = source.getCoordinateReferenceSystem();
        }
        final GridGeometry2D targetGG = GridGeometry2D.castOrCopy(
                (GridGeometry) parameters.parameter("GridGeometry").getValue());
        final GridCoverage2D target;
        try {
            target = Resampler2D.reproject(source, targetCRS, targetGG, interpolation, background, hints);
        } catch (FactoryException | TransformException exception) {
            throw new CannotReprojectException(Errors.format(
                    Errors.Keys.CANT_REPROJECT_COVERAGE_1, source.getName()), exception);
        }
        return target;
    }

    /**
     * Computes a grid geometry from a source coverage and a target envelope. This is a convenience
     * method for computing the {@link #GRID_GEOMETRY} argument of a {@code "resample"} operation
     * from an envelope. The target envelope may contains a different coordinate reference system,
     * in which case a reprojection will be performed.
     *
     * @param source The source coverage.
     * @param target The target envelope, including a possibly different coordinate reference system.
     * @return A grid geometry inferred from the target envelope.
     * @throws TransformException If a transformation was required and failed.
     *
     * @since 2.5
     */
    public static GridGeometry computeGridGeometry(final GridCoverage source, final Envelope target)
            throws TransformException
    {
        final CoordinateReferenceSystem targetCRS = target.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem sourceCRS = source.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem reducedCRS;
        if (target.getDimension() == 2 && sourceCRS.getCoordinateSystem().getDimension() != 2) {
            reducedCRS = CoverageUtilities.getCRS2D(source);
        } else {
            reducedCRS = sourceCRS;
        }
        GridGeometry gridGeometry = source.getGridGeometry();
        if (targetCRS == null || CRS.equalsIgnoreMetadata(reducedCRS, targetCRS)) {
            /*
             * Same CRS (or unknown target CRS, which we treat as same), so we will keep the same
             * "gridToCRS" transform. Basically the result will be the same as if we did a crop,
             * except that we need to take in account a change from nD to 2D.
             */
            final MathTransform gridToCRS;
            if (reducedCRS == sourceCRS) {
                gridToCRS = gridGeometry.getGridToCRS();
            } else {
                gridToCRS = GridGeometry2D.castOrCopy(gridGeometry).getGridToCRS2D();
            }
            gridGeometry = new GridGeometry2D(PixelInCell.CELL_CENTER, gridToCRS, target, null);
        } else {
            /*
             * Different CRS. We need to infer an image size, which may be the same than the
             * original size or something smaller if the envelope is a subarea. We process by
             * transforming the target envelope to the source CRS and compute a new grid geometry
             * with that envelope. The grid envelope of that grid geometry is the new image size.
             * Note that failure to transform the envelope is non-fatal (we will assume that the
             * target image should have the same size). Then create again a new grid geometry,
             * this time with the target envelope.
             */
            GridEnvelope gridEnvelope;
            try {
                final GeneralEnvelope transformed;
                transformed = CRS.transform(CRS.getCoordinateOperationFactory(true)
                        .createOperation(targetCRS, reducedCRS), target);
                final Envelope reduced;
                final MathTransform gridToCRS;
                if (reducedCRS == sourceCRS) {
                    reduced   = source.getEnvelope();
                    gridToCRS = gridGeometry.getGridToCRS();
                } else {
                    reduced   = CoverageUtilities.getEnvelope2D(source);
                    gridToCRS = GridGeometry2D.castOrCopy(gridGeometry).getGridToCRS2D();
                }
                transformed.intersect(reduced);
                gridGeometry = new GridGeometry2D(PixelInCell.CELL_CENTER, gridToCRS, transformed, null);
            } catch (FactoryException | TransformException exception) {
                recoverableException("resample", exception);
                // Will use the grid envelope from the original geometry,
                // which will result in keeping the same image size.
            }
            gridEnvelope = gridGeometry.getExtent();
            gridGeometry = new GridGeometry2D(gridEnvelope, target);
        }
        return gridGeometry;
    }

    /**
     * Invoked when an error occurred but the application can fallback on a reasonable default.
     *
     * @param method The method where the error occurred.
     * @param exception The error.
     */
    private static void recoverableException(final String method, final Exception exception) {
        Logging.recoverableException(Resample.class, method, exception);
    }
}
