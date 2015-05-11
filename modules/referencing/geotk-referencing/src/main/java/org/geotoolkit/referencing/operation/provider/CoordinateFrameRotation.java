/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.provider;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.OperationMethod;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.apache.sis.util.ArgumentChecks;


/**
 * The provider for "<cite>Coordinate Frame Rotation</cite>" (EPSG:9607). This is the same
 * transformation than "{@linkplain PositionVector7Param Position Vector 7-param."} except
 * that the rotation angles have the opposite sign.
 * <p>
 * In addition to the EPSG parameters, this provider defines some OGC/Geotk-specific parameters.
 * Those parameters begin with the {@code "src_"} or {@code "tgt_"} prefix, and modify the math
 * transform as below:
 * <p>
 * <ul>
 *   <li>If a {@code "src_*"} parameter is present, then an {@link EllipsoidToGeocentric}
 *       transform will be concatenated before the geocentric operation.</li>
 *   <li>If a {@code "tgt_*"} parameter is present, then an {@link GeocentricToEllipsoid}
 *       transform will be concatenated after the geocentric operation.</li>
 * </ul>
 *
 * <!-- PARAMETERS CoordinateFrameRotation -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Coordinate Frame Rotation (geog2D domain)}</p>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code dx}</td><td>0 metres</td></tr>
 *   <tr><td>{@code dy}</td><td>0 metres</td></tr>
 *   <tr><td>{@code dz}</td><td>0 metres</td></tr>
 *   <tr><td>{@code ex}</td><td>0 ''</td></tr>
 *   <tr><td>{@code ey}</td><td>0 ''</td></tr>
 *   <tr><td>{@code ez}</td><td>0 ''</td></tr>
 *   <tr><td>{@code ppm}</td><td>0</td></tr>
 *   <tr><td>{@code src_semi_major}</td><td></td></tr>
 *   <tr><td>{@code src_semi_minor}</td><td></td></tr>
 *   <tr><td>{@code tgt_semi_major}</td><td></td></tr>
 *   <tr><td>{@code tgt_semi_minor}</td><td></td></tr>
 *   <tr><td>{@code src_dim}</td><td>2</td></tr>
 *   <tr><td>{@code tgt_dim}</td><td>2</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.2
 * @module
 */
public class CoordinateFrameRotation extends PositionVector7Param {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5513675854809530038L;

    /**
     * The parameters group. This is the same group than
     * {@link PositionVector7Param#PARAMETERS} except for the operation name.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Coordinate Frame Rotation (geog2D domain)</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Coordinate Frame Rotation</code></td></tr>
     *       <tr><td></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Bursa-Wolf</code></td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9607</code></td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>dx</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>X-axis translation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>dy</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Y-axis translation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>dz</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Z-axis translation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>ex</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>X-axis rotation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-648000 … 648000] ''</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 ''</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>ey</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Y-axis rotation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-648000 … 648000] ''</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 ''</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>ez</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Z-axis rotation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-648000 … 648000] ''</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 ''</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>ppm</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Scale difference</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>src_semi_major</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>src_semi_minor</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>tgt_semi_major</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>tgt_semi_minor</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>src_dim</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Integer}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[2…3]</td></tr>
     *       <tr><td><b>Default value:</b></td><td>2</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>tgt_dim</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Integer}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[2…3]</td></tr>
     *       <tr><td><b>Default value:</b></td><td>2</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(9607,
            "Coordinate Frame Rotation (geog2D domain)", "Coordinate Frame Rotation");

    /**
     * Constructs the provider.
     */
    public CoordinateFrameRotation() {
        super(PARAMETERS); // TODO (3,3)
    }

    private CoordinateFrameRotation(final int sourceDimensions, final int targetDimensions) {
        super(sourceDimensions, targetDimensions, PARAMETERS);
    }

    /**
     * Fills the given Bursa-Wolf parameters according the specified values.
     */
    @Override
    final void fill(final BursaWolfParameters parameters, final ParameterValueGroup values) {
        super.fill(parameters, values);
        parameters.reverseRotation();
    }

    /**
     * Returns the same operation method, but for different dimensions.
     *
     * @param  sourceDimensions The desired number of input dimensions.
     * @param  targetDimensions The desired number of output dimensions.
     * @return The redimensioned operation method, or {@code this} if no change is needed.
     */
    @Override
    public OperationMethod redimension(final int sourceDimensions, final int targetDimensions) {
        ArgumentChecks.ensureBetween("sourceDimensions", 2, 3, sourceDimensions);
        ArgumentChecks.ensureBetween("targetDimensions", 2, 3, targetDimensions);
        return new CoordinateFrameRotation(sourceDimensions, targetDimensions); // TODO: cache the instances.
    }
}
