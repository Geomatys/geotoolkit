/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.internal.image.io;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.io.IOException;
import java.awt.geom.Point2D;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import ucar.ma2.Array;
import ucar.nc2.Dimension;
import ucar.ma2.Index2D;
import ucar.nc2.NetcdfFile;
import ucar.nc2.VariableIF;

import static java.util.Collections.singletonMap;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;


/**
 * Attempts to convert an irregular two-dimensional grids by its original CRS.
 * Current implementation contains hard-coded special cases encounter in practice.
 * Future implementations will need to replace those special cases by more general detection mechanism.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 4.00
 *
 * @since 4.00
 * @module
 */
public final class IrregularGridConverter {
    /**
     * The NetCDF file to inspect.
     */
    private final NetcdfFile file;

    /**
     * The domain of the variable for which to create a grid geometry.
     */
    private final List<Dimension> domain;

    /**
     * The factory to use for creating math transform.
     */
    private final MathTransformFactory mtFactory;

    /**
     * The factory to use for creating CRS objects.
     */
    private final CRSFactory crsFactory;

    /**
     * The <cite>grid to CRS</cite> transform to test.
     */
    private AffineTransform2D gridToCRS;

    /**
     * The CRS to replace by the one to be created by this class.
     */
    private final CoordinateReferenceSystem toReplace;

    /**
     * Creates a new converter.
     *
     * @param file      The NetCDF file to inspect.
     * @param domain    The variable for which to find a CRS.
     * @param toReplace CRS to replace by the one to be created by this class.
     * @param hints     An optional set of hints for determining the factories to use.
     */
    public IrregularGridConverter(final NetcdfFile file, final List<Dimension> domain,
            final CoordinateReferenceSystem toReplace, final Hints hints)
    {
        this.file      = file;
        this.domain    = domain;
        this.toReplace = toReplace;
        mtFactory      = FactoryFinder.getMathTransformFactory(hints);
        crsFactory     = FactoryFinder.getCRSFactory(hints);
    }

    /**
     * Returns {@code true} if the name of the two last dimension are the given ones.
     */
    private boolean endsWith(final String y, final String x) {
        final int size = domain.size();
        return (size >= 2) && domain.get(size-1).getName().equals(x) && domain.get(size-2).getName().equals(y);
    }

    /**
     * Hard-coded attempt to create a CRS for a ROM model.
     * Example of data:
     *
     * {@preform text
     *   float temp(ocean_time, s_rho, eta_rho, xi_rho)
     * }
     *
     * where:
     *
     * {@preformat text
     *   double lon_rho(eta_rho, xi_rho)
     *   double lat_rho(eta_rho, xi_rho)
     * }
     *
     * Greek symbol of "eta" is η and Greek symbol of "xi" is ξ.
     *
     * @throws IOException        If the NetCDF file can not be read.
     * @throws FactoryException   If the map projection can not be created.
     * @throws TransformException If an error occurred during the map projection of a coordinate.
     * @return The CRS if there is a match, or {@code null} otherwise.
     */
    public CoordinateReferenceSystem forROM() throws IOException, FactoryException, TransformException {
        if (endsWith("eta_rho", "xi_rho")) {
            final VariableIF longitudes = file.findVariable("lon_rho");
            if (longitudes != null) {
                final VariableIF latitudes = file.findVariable("lat_rho");
                if (latitudes != null) {
                    final ParameterValueGroup p = mtFactory.getDefaultParameters("Polar Stereographic (variant B)");
                    p.parameter("semi_major").setValue(6371000);
                    p.parameter("semi_minor").setValue(6371000);
                    p.parameter("central_meridian").setValue(70);
                    p.parameter("standard_parallel_1").setValue(60);
                    p.parameter("false_easting").setValue(3192000);
                    p.parameter("false_northing").setValue(1783200);
                    gridToCRS = new AffineTransform2D(800, 0, 0, 800, 0, 0);
                    if (matches((MathTransform2D) mtFactory.createParameterizedTransform(p),
                                longitudes.read(), latitudes.read(), 1E-4))
                    {
                        return replace(crsFactory.createProjectedCRS(singletonMap(NAME_KEY, "ROM"),
                                DefaultGeographicCRS.SPHERE, new DefiningConversion("ROM", p),
                                DefaultCartesianCS.PROJECTED));
                    }
                }
            }
        }
        return null;
    }

    /**
     * Tests if the current {@link #gridToCRS} is consistent with the projection of the given longitudes and
     * latitudes using the current {@link #projection}.
     *
     * @todo Current implementation expects a candidate affine transform to be specified as {@link #gridToCRS}.
     *       A future version should compute the affine transform using a port of the code available in
     *       {@code LocalizationGrid.getAffineTransform()}.
     */
    private boolean matches(final MathTransform2D projection, final Array longitudes, final Array latitudes,
            final double tolerance) throws TransformException
    {
        final int[] shape = longitudes.getShape();
        if (shape.length != 2 || !Arrays.equals(latitudes.getShape(), shape)) {
            return false;
        }
        final int               width       = shape[1];
        final int               height      = shape[0];
        final AffineTransform2D gridToCRS   = this.gridToCRS;
        final Index2D           index       = new Index2D(shape);
        final Point2D.Double    candidatePt = new Point2D.Double();
        final Point2D.Double    expectedPt  = new Point2D.Double();
        for (int η=0; η<height; η++) {
            for (int ξ=0; ξ<width; ξ++) {
                index.set(η, ξ);
                candidatePt.x = longitudes.getDouble(index);
                candidatePt.y = latitudes .getDouble(index);
                projection.transform(candidatePt, candidatePt);
                expectedPt.x = ξ;
                expectedPt.y = η;
                gridToCRS.transform(expectedPt, expectedPt);
                if (!(Math.abs(expectedPt.x - candidatePt.x) <= tolerance) || // Use '!' for catching NaN.
                    !(Math.abs(expectedPt.y - candidatePt.y) <= tolerance))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * If the {@link #toReplace} CRS is a compound one, replace its geographic component by the given one.
     */
    private CoordinateReferenceSystem replace(final ProjectedCRS crs) throws FactoryException {
        if (toReplace instanceof CompoundCRS) {
            final CoordinateReferenceSystem[] components = ((CompoundCRS) toReplace)
                    .getComponents().toArray(new CoordinateReferenceSystem[2]);
            if (components[0] instanceof GeographicCRS) {
                components[0] = crs;
                return crsFactory.createCompoundCRS(IdentifiedObjects.getProperties(toReplace), components);
            }
        }
        if (toReplace instanceof GeographicCRS) {
            return crs;
        }
        /*
         * TODO: it is a little bit late to realize that computing the CRS was useless.
         *       But actually this case should never happen. Should we log a warning?
         */
        return null;
    }

    /**
     * Return the grid to CRS transform.
     *
     * @param  crs The CRS created by {@link #forROM()}.
     * @return The grid to CRS transform.
     */
    public GeneralGridGeometry getGridToCRS(final CoordinateReferenceSystem crs) {
        final CoordinateSystem cs = toReplace.getCoordinateSystem();
        final int dim = cs.getDimension();
        final int[] upper = new int[dim];
        final MatrixSIS m = Matrices.createIdentity(dim + 1);
        for (int i=0; i<dim; i++) {
            upper[i] = domain.get((dim - 1) - i).getLength();
            switch (i) {
                case 0: {
                    m.setElement(0, 0,   gridToCRS.getScaleX());
                    m.setElement(0, 1,   gridToCRS.getShearX());
                    m.setElement(0, dim, gridToCRS.getTranslateX());
                    break;
                }
                case 1: {
                    m.setElement(1, 0,   gridToCRS.getShearY());
                    m.setElement(1, 1,   gridToCRS.getScaleY());
                    m.setElement(1, dim, gridToCRS.getTranslateY());
                    break;
                }
                default: {
                    // TODO: following code is unsafe and inacurate.
                    final DiscreteCoordinateSystemAxis<?> axis = (DiscreteCoordinateSystemAxis<?>) cs.getAxis(i);
                    final int n = axis.length() - 1;
                    final double start, end;
                    if (Date.class.isAssignableFrom(axis.getElementType())) {
                        // TODO: there is no guarantee that we get the right CRS here.
                        final DefaultTemporalCRS c = DefaultTemporalCRS.castOrCopy(CRS.getTemporalCRS(toReplace));
                        start = c.toValue((Date) axis.getOrdinateAt(0));
                        end   = c.toValue((Date) axis.getOrdinateAt(n));
                    } else {
                        start = ((Number) axis.getOrdinateAt(0)).doubleValue();
                        end = ((Number) axis.getOrdinateAt(n)).doubleValue();
                    }
                    m.setElement(i, dim, start);
                    m.setElement(i, i, (end - start) / n);
                }
            }
        }
        return new GeneralGridGeometry(new GeneralGridEnvelope(new int[dim], upper, false), MathTransforms.linear(m), crs);
    }
}
