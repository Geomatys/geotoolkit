/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.renderer;

import org.apache.sis.storage.Resource;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.map.MapLayer;
import org.opengis.feature.Feature;

/**
 * Produced by the portrayal engines when an exception occures.
 * Exception presentations are placed in the Stream of presentation leaving
 * the user the choice to log, ignore or stop rendering as needed.
 *
 * <p>
 * NOTE: this class is a first draft subject to modifications.
 * </p>
 *
 * @author  Johann Sorel (Geomatys)
 */
public class ExceptionPresentation extends AbstractPresentation {

    private final Exception exception;

    /**
     * @param exception not null.
     */
    public ExceptionPresentation(MapLayer layer, Resource resource, Feature feature, Exception exception) {
        super(layer, resource, feature);
        ArgumentChecks.ensureNonNull("exception", exception);
        this.exception = exception;
    }

    /**
     * @return exception, never null
     */
    public Exception getException() {
        return exception;
    }

}
