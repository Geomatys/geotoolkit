/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.render2d.edition;

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.gui.javafx.render2d.AbstractNavigationHandler;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractEditionTool extends AbstractNavigationHandler implements EditionTool{

    private final Spi spi;

    public AbstractEditionTool(Spi spi) {
        ArgumentChecks.ensureNonNull("spi", spi);
        this.spi = spi;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Spi getSpi() {
        return spi;
    }


}
