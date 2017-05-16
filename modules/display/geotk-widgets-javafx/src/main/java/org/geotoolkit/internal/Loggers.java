/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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

package org.geotoolkit.internal;

import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;

/**
 * Common loggers for javafx widgets.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Loggers {

    public static final Logger JAVAFX = Logging.getLogger("geotk.javafx");
    public static final Logger REFERENCING = Logging.getLogger("geotk.referencing");
    public static final Logger DATA = Logging.getLogger("geotk.data");

    private Loggers(){}

}
