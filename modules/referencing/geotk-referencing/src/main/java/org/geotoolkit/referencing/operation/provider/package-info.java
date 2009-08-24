/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
 * {@linkplain org.geotoolkit.referencing.operation.MathTransformProvider Math transforms providers},
 * including map projections. Most users will not need to work directly with this package.
 * It is included in the documentation mostly for information purpose, as a way to list the
 * available projections, their EPSG code (when any) and their expected parameters.
 * <p>
 * Providers instantiate objects from the {@linkplain org.geotoolkit.referencing.operation.transform
 * transform} or {@linkplain org.geotoolkit.referencing.operation.projection projection} packages
 * from a set of parameter values. The above packages contain the actual formulas transforming
 * the coordinates. Providers never contain such formulas themself.
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
 * Each provider declares two kind of static constants:
 * <p>
 * <ul>
 *   <li>A set of {@link org.opengis.parameter.ParameterDescriptor} constants,
 *       one for each parameter.</li>
 *   <li>A single {@link org.opengis.parameter.ParameterDescriptorGroup} constant called
 *       {@code PARAMETERS}. This group contains all the individual parameters enumerated
 *       above.</li>
 * </ul>
 *
 * The parameter descriptors for
 * {@link org.geotoolkit.referencing.operation.provider.MapProjection#SEMI_MAJOR SEMI_MAJOR} and
 * {@link org.geotoolkit.referencing.operation.provider.MapProjection#SEMI_MINOR SEMI_MINOR} axis
 * length are common to all map projection providers. Other descriptors are available on a
 * projection-by-projection basis, but the most typical ones are the
 * {@link org.geotoolkit.referencing.operation.provider.Mercator1SP#CENTRAL_MERIDIAN   CENTRAL_MERIDIAN},
 * {@link org.geotoolkit.referencing.operation.provider.Mercator1SP#LATITUDE_OF_ORIGIN LATITUDE_OF_ORIGIN},
 * {@link org.geotoolkit.referencing.operation.provider.Mercator1SP#SCALE_FACTOR       SCALE_FACTOR},
 * {@link org.geotoolkit.referencing.operation.provider.Mercator1SP#FALSE_EASTING      FALSE_EASTING} and
 * {@link org.geotoolkit.referencing.operation.provider.Mercator1SP#FALSE_NORTHING     FALSE_NORTHING}.
 * <p>
 * Each descriptor has many aliases, and those aliases may vary between different projections.
 * For example the {@code FALSE_EASTING} parameter is usually called {@code "false_easting"}
 * by OGC and {@code "False easting"} by EPSG (those names are used for example in the
 * {@link org.geotoolkit.referencing.operation.provider.Mercator1SP} projection). But some
 * projections use different names. For example in the
 * {@link org.geotoolkit.referencing.operation.provider.LambertConformal2SP} projection,
 * {@code FALSE_EASTING} is still called {@code "false_easting"} by OGC, but is called
 * {@code "Easting at false origin"} by EPSG, which is different than the name EPSG used
 * in the {@code Mercator1SP} case.
 *
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
 * {@section Geotoolkit extensions}
 *
 * Geotoolkit defines a
 * {@link org.geotoolkit.referencing.operation.provider.MapProjection#ROLL_LONGITUDE ROLL_LONGITUDE}
 * boolean parameter. If this parameter is set to {@code TRUE}, then any longitude outside the
 * [-180 &hellip; 180)&deg; range (upper value is exclusive) will be forced to that range by the
 * addition or substraction of some multiple of 360&deg;. This applies to both the input of
 * projections and the output of inverse projections.
 * <p>
 * If this parameter is not provided, then the default behavior is to roll longitudes only if
 * the central meridian is different than zero.
 *
 *
 * {@section ESRI extensions}
 *
 * In Geotoolkit, axis flips are inferred from the {@linkplain org.opengis.referencing.is.AxisOrientation
 * axis orientation}. In <cite>Well Known Text</cite> (WKT), they are given by {@code AXIS} elements.
 * But ESRI ignores {@code AXIS} elements and uses intead some additional parameters:
 * {@link org.geotoolkit.referencing.operation.provider.MapProjection#X_SCALE X_SCALE},
 * {@link org.geotoolkit.referencing.operation.provider.MapProjection#Y_SCALE Y_SCALE} and
 * {@link org.geotoolkit.referencing.operation.provider.MapProjection#XY_PLANE_ROTATION XY_PLANE_ROTATION}.
 * Those parameters are not OGC standards, but are nevertheless provided in Geotoolkit for better
 * interoperability with ESRI definitions of coordinate reference systems.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.03
 *
 * @see org.geotoolkit.referencing.operation.transform
 * @see org.geotoolkit.referencing.operation.projection
 *
 * @since 3.00
 * @level advanced
 * @module
 */
package org.geotoolkit.referencing.operation.provider;
