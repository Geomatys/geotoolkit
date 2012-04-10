/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

/**
 * {@linkplain org.geotoolkit.referencing.operation.MathTransformProvider Providers} of
 * {@linkplain org.geotoolkit.referencing.operation.transform transforms} and
 * {@linkplain org.geotoolkit.referencing.operation.projection projections} implementations.
 * This package is provided mostly for documentation purpose, since the javadoc of each provider
 * lists the operation names, identifiers (e.g. EPSG codes) and parameters. The preferred way to
 * get a provider is to use the {@link org.opengis.referencing.operation.MathTransformFactory} class.
 * <p>
 * Providers are registered in the following file, which may appears in any JAR file. See
 * the {@linkplain org.geotoolkit.factory factory} package for more information about how to
 * manage providers registered in such files.
 *
 * {@preformat text
 *     META-INF/services/org.geotoolkit.referencing.operation.MathTransformProvider
 * }
 *
 *
 * {@section Parameters}
 *
 * Each provider declares a single {@link org.opengis.parameter.ParameterDescriptorGroup} constant
 * named {@code PARAMETERS}. Each group describes all the parameters expected by an operation method.
 * The set of parameters varies for each projection, but the following can be considered typical:
 * <p>
 * <ul>
 *   <li>A <cite>semi-major</cite> and <cite>semi-minor</cite> axis length in metres.</li>
 *   <li>A <cite>central meridian</cite> and <cite>latitude of origin</cite> in decimal degrees.</li>
 *   <li>A <cite>scale factor</cite>, which default to 1.</li>
 *   <li>A <cite>false easting</cite> and <cite>false northing</cite> in metres, which default to 0.</li>
 * </ul>
 * <p>
 * Each descriptor has many aliases, and those aliases may vary between different projections.
 * For example the <cite>false easting</cite> parameter is usually called {@code "false_easting"}
 * by OGC, while EPSG uses various names like "<cite>False easting</cite>" or "<cite>Easting at
 * false origin</cite>".
 *
 * {@section Mandatory and optional parameters}
 *
 * <a name="Obligation">Parameters are flagged as either <cite>mandatory</cite> or <cite>optional</cite></a>.
 * A parameter may be mandatory and still have a default value. In the context of this package, "mandatory"
 * means that the parameter is an essential part of the projection defined by standards. Such
 * mandatory parameters will always appears in any <cite>Well Known Text</cite> (WKT) formatting,
 * even if not explicitly set by the user. For example the central meridian is typically a mandatory
 * parameter with a default value of 0&deg; (the Greenwich meridian).
 * <p>
 * Optional parameters, on the other hand, are often non-standard extensions. They will appear
 * in WKT formatting only if the user defined explicitly a value which is different than the
 * default value.
 *
 *
 * {@section Geotoolkit.org extensions}
 *
 * Geotk defines a {@code "roll_longitude"} boolean parameter.
 * If this parameter is set to {@code TRUE}, then any longitude outside the
 * [-180 &hellip; 180)&deg; range (upper value is exclusive) will be forced to that range by the
 * addition or subtraction of some multiple of 360&deg;. This applies to both the input of
 * projections and the output of inverse projections.
 * <p>
 * If this parameter is not provided, then the default behavior is to roll longitudes only if
 * the central meridian is different than zero.
 *
 *
 * {@section ESRI extensions}
 *
 * In Geotk, axis flips are inferred from the {@linkplain org.opengis.referencing.is.AxisOrientation
 * axis orientation}. In <cite>Well Known Text</cite> (WKT), they are given by {@code AXIS} elements.
 * But ESRI ignores {@code AXIS} elements and uses instead some additional parameters:
 * {@code "X_Scale"}, {@code "Y_Scale"} and {@code "XY_Plane_Rotation"}.
 * Those parameters are not OGC standards, but are nevertheless provided in Geotk for better
 * inter-operability with ESRI definitions of coordinate reference systems.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.20
 *
 * @since 3.00
 * @level advanced
 * @module
 */
package org.geotoolkit.referencing.operation.provider;
