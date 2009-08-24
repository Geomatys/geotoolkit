/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

/**
 * Map projection implementations. This package is mostly for implementors
 * and should usually not be used directly.
 * <p>
 * The best way to get a projection is to use the
 * {@linkplain org.opengis.referencing.operation.CoordinateOperationFactory coordinate operation
 * factory} with the source and target CRS. That factory can bundle the projections defined in
 * this package, together with any affine transform required for handling unit conversions and
 * axis swapping, in a single (potentially concatenated) transform.
 * <p>
 * Users wanting to build their transforms directly should also avoid instantiating objects directly
 * from this package and use {@link org.opengis.referencing.operation.MathTransformFactory} instead.
 * The {@link org.opengis.referencing.operation.MathTransformFactory#createParameterizedTransform
 * createParameterizedTransform} method of that factory is subjects to the same rules than this
 * package, namely input coordinates must be (<var>longitude</var>, <var>latitude</var>) in decimal
 * degrees and output coordinates must be (<var>easting</var>, <var>northing</var>) in metres.
 * More on this convention is explained below.
 * <p>
 * Users wanting to know more about the available projections and their parameters should look
 * at the {@linkplain org.geotoolkit.referencing.operation.provider provider} package. Only users
 * interested in the <em>implementation</em> of those projections should look at this package.
 *
 *
 * {@section Definition of terms}
 *
 * <ul>
 *   <li><p><b>Coordinate operation</b><br>
 *       In the particular case of this package, the conversion of geographic coordinates in any
 *       axis order, geodesic orientation and angular units to projected coordinates in any axis
 *       order, horizontal orientation and linear units.<p></li>
 *   <li><p><b>Map projection</b> (a.k.a. cartographic projection)<br>
 *       The conversion of geographic coordinates from (<var>longitude</var>, <var>latitude</var>)
 *       in decimal degrees to projected coordinates (<var>x</var>, <var>y</var>) in metres.<p></li>
 *   <li><p><b>Unitary projection</b><br>
 *       The conversion of geographic coordinates from (<var>longitude</var>, <var>latitude</var>)
 *       in radians to projected coordinates (<var>x</var>, <var>y</var>) on a sphere or ellipse
 *       having a semi-major axis length of 1. This definition may be slightly relaxed if some
 *       projection-specifics coefficients are concatenated with the conversions that take place
 *       between the above map projection and this unitary projection.<p></li>
 * </ul>
 *
 * {@section Axis units and orientation}
 *
 * Many {@linkplain org.opengis.referencing.crs.GeographicCRS geographic coordinate reference systems}
 * use axis in (<var>latitude</var>, <var>longitude</var>) order, but not all. Axis order,
 * orientation and units are CRS-dependent. For example some CRS use longitude values increasing
 * toward {@linkplain org.opengis.referencing.cs.AxisDirection#EAST East}, while some others use
 * longitude values increasing toward {@linkplain org.opengis.referencing.cs.AxisDirection#WEST West}.
 * The axis order must be specified in all CRS, and any method working with them should take their
 * axis order and units in account.
 * <p>
 * However, map projections defined in this package are <strong>transform steps</strong>, not the
 * full transform to the final CRS. All projections defined in this package must comply with the
 * OGC 01-009 specification. This specification says (quoting section 10.6 at page 34):
 *
 * <blockquote>
 * Cartographic projection transforms are used by projected coordinate reference systems to map
 * geographic coordinates (e.g. <var>longitude</var> and <var>latitude</var>) into (<var>x</var>,
 * <var>y</var>) coordinates. These (<var>x</var>, <var>y</var>) coordinates can be imagined to
 * lie on a plane, such as a paper map or a screen. All cartographic projection transforms will
 * have the following properties:
 * <p>
 * <ul>
 *   <li>Converts from (<var>longitude</var>, <var>latitude</var>) coordinates to (<var>x</var>,<var>y</var>).</li>
 *   <li>All angles are assumed to be decimal degrees, and all distances are assumed to be metres.</li>
 *   <li>The domain should be a subset of {[-180 &hellip; 180)&times;[-90 &hellip; 90]}&deg;.</li>
 * </ul>
 * <p>
 * Although all cartographic projection transforms must have the properties listed above, many
 * projected coordinate reference systems have different properties. For example, in Europe some
 * projected coordinate reference systems use grads instead of decimal degrees, and often the base
 * geographic coordinate reference system is (<var>latitude</var>, <var>longitude</var>) instead of
 * (<var>longitude</var>, <var>latitude</var>). This means that the cartographic projected transform
 * is often used as a single step in a series of transforms, where the other steps change units and
 * swap ordinates.
 * </blockquote>
 *
 * The Geotk implementation extends this rule to axis directions as well, i.e. (<var>x</var>,
 * <var>y</var>) coordinates must be ({@linkplain org.opengis.referencing.cs.AxisDirection#EAST East},
 * {@linkplain org.opengis.referencing.cs.AxisDirection#NORTH North}) orientated.
 *
 * <blockquote><font size="-1"><b>Implications on South oriented projections</b><br>
 * The above rule implies a non-intuitive behavior for the <cite>Transverse Mercator (South
 * Orientated)</cite> projection, which still projects coordinates with <var>y</var> values
 * increasing toward North. The real axis flip is performed outside this projection package,
 * upon {@linkplain org.opengis.referencing.cs.CoordinateSystemAxis coordinate system axis}
 * inspection, as a concatenation of the North oriented cartographic projection with an affine
 * transform. Such axis analysis and transforms concatenation can be performed automatically
 * by the {@link org.opengis.referencing.operation.MathTransformFactory#createBaseToDerived
 * createBaseToDerived} method defined in the {@code MathTransformFactory} interface. The
 * same rule applies to the {@linkplain org.geotoolkit.referencing.operation.projection.Krovak}
 * projection as well (at the opposite of what ESRI does).
 * </font></blockquote>
 * <p>
 * In order to reduce the risk of confusion, this package never defines south orientated
 * map projection. This rule removes all ambiguity when reading a transform in
 * <A HREF="http://geoapi.sourceforge.net/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
 * Known Text</cite> (WKT)</A> format, since only the north-orientated variant is used and
 * the affine transform coefficients tell exactly which axis flips are applied.
 *
 *
 * {@section Projection on unit ellipse}
 *
 * A map projection in this package is actually the concatenation of the following transforms,
 * in that order:
 * <p>
 * <ul>
 *   <li>{@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#normalize(boolean) normalize} affine transform</li>
 *   <li>{@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection} subclass</li>
 *   <li>{@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#normalize(boolean) denormalize} affine transform</li>
 * </ul>
 * <p>
 * The first step ("<cite>normalize</cite>") converts longitude and latitude values from degrees to radians and removes the
 * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian central meridian}
 * from the longitude. The last step ("<cite>denormalize</cite>") multiplies the result of the middle
 * step by the global scale factor (which is typically, but not always, the product of the
 * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor scale factor} with the
 * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#semiMajor semi-major} axis length),
 * then adds the
 * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting false easting} and
 * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing false northing}.
 * This means that the middle step ("<cite>unitary projection</cite>") is performed on an ellipse
 * (or sphere) having a semi-major axis of 1.
 * <p>
 * In other words, the
 * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection#transform(double[],int,double[],int)
 * transform} method of the middle step works typically on longitude and latitude values in <strong>radians</strong>
 * relative to the central meridian (not necessarly Greenwich). Its results are typically (<var>x</var>, <var>y</var>)
 * coordinates having ({@linkplain org.opengis.referencing.cs.AxisDirection#EAST East},
 * {@linkplain org.opengis.referencing.cs.AxisDirection#NORTH North}) axis orientation. However in
 * some cases the actual input and output coordinates may be different than the above by some scale
 * factor, translation or rotation, if the projection implementation choose to combine some linear
 * coefficients with the above-cited normalize and denormalize affine transforms.
 * <p>
 * In <a href="http://www.remotesensing.org/proj/">PROJ.4</a>, the same standardization is handled
 * by {@code pj_fwd.c} and {@code pj_inv.c}. Therefore when porting projections from PROJ.4, the
 * transform equations can be used directly with minimal change. It also make the equations closer
 * to the ones published in Snyder's book, where the
 * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting false easting},
 * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing false northing} and
 * {@linkplain org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor scale factor}
 * are usually not given.
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author André Gosselin (MPO)
 * @author Rueben Schulz (UBC)
 * @version 3.00
 *
 * @see org.geotoolkit.referencing.operation.provider
 * @see <A HREF="http://mathworld.wolfram.com/MapProjection.html">Map projections on MathWorld</A>
 * @see <A HREF="http://atlas.gc.ca/site/english/learningresources/carto_corner/map_projections.html">Map projections on the atlas of Canada</A>
 *
 * @since 1.0
 * @level advanced
 * @module
 */
package org.geotoolkit.referencing.operation.projection;
