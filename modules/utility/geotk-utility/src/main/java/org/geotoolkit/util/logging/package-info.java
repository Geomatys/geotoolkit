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
 * Extensions to the {@linkplain java.util.logging Java logging} framework.
 * This package provides:
 * <p>
 * <ul>
 *   <li>A {@link org.apache.sis.util.logging.Logging} utility class with static methods for
 *       fetching a logger (see <cite>Choosing a logging framework</cite> below) or logging an
 *       exception.</li>
 *   <li>A {@link org.apache.sis.util.logging.PerformanceLevel} with configurable levels for
 *       logging the duration of lengthly processes.</li>
 *   <li>A {@link org.apache.sis.util.logging.MonolineFormatter} for formatting the log
 *       records on single line with colors, for easier reading on the console output.</li>
 * </ul>
 *
 * {@section Choosing a logging framework}
 * The Geotk project uses the standard {@link java.util.logging.Logger} API for its logging,
 * but this package allows redirection of logging messages to some other frameworks like
 * <a href="http://logging.apache.org/log4j/">Log4J</a>.
 * <p>
 * We recommend to stick to standard Java logging when possible. However if inter-operability
 * with an other logging framework is required, then the only action needed is to include
 * <strong>one</strong> of the following JAR on the classpath:
 * <p>
 * <ul>
 *   <li>{@code geotk-logging-commons.jar} for Apache logging</li>
 *   <li>{@code geotk-logging-log4j.jar} for Log4J logging</li>
 * </ul>
 *
 * {@section Note for Geotk developers}
 * All Geotk code should fetch their logger through a call to our custom
 * {@link org.apache.sis.util.logging.Logging#getLogger(String)} method, <strong>not</strong>
 * the standard {@link java.util.logging.Logger#getLogger(String)} method. This is necessary in
 * order to give Geotk a chance to redirect log events to an other logging framework.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @see <a href="http://download.oracle.com/javase/6/docs/technotes/guides/logging/overview.html">Java Logging Overview</a>
 *
 * @since 2.4
 * @module
 */
package org.geotoolkit.util.logging;
