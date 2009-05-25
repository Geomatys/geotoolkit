/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
 * Extensions to the {@linkplain java.util.logging Java logging} framework. The Geotoolkit project
 * uses the standard {@link java.util.logging.Logger} API for its logging, but this package allows
 * redirection of logs to some other frameworks like <a href="http://logging.apache.org/log4j/">Log4J</a>.
 *
 * {@section Using Log4J or Apache logging}
 * We recommand to stick to standard Java logging when possible. However if interoperability
 * with an other logging framework is required, then the only action needed is to include
 * <strong>one</strong> of the following JAR on the classpath:
 * <p>
 * <ul>
 *   <li>{@code geotk-logging-commons.jar} for Apache logging</li>
 *   <li>{@code geotk-logging-log4j.jar} for Log4J logging</li>
 * </ul>
 *
 * {@section For developpers}
 * All Geotoolkit code should fetch their logger through a call to
 * {@link org.geotoolkit.util.logging.Logging#getLogger(String)}, not
 * {@link java.util.logging.Logger#getLogger(String)}. This is necessary in
 * order to give Geotoolkit a chance to redirect log events to an other logging framework.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
package org.geotoolkit.util.logging;
