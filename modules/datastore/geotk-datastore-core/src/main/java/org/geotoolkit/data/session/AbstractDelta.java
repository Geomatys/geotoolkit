/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.data.session;

import org.opengis.feature.type.Name;
import java.util.logging.Logger;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.logging.Logging;
import org.opengis.filter.FilterFactory2;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
abstract class AbstractDelta implements Delta{

    protected static final FilterFactory2 FF = (FilterFactory2)
            FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

    private static final Logger LOGGER = Logging.getLogger(AbstractDelta.class);

    protected final Session session;
    protected final Name type;

    public AbstractDelta(final Session session,final Name type){
        ensureNonNull("session", session);
        ensureNonNull("name", type);
        this.session = session;
        this.type = type;
    }

    @Override
    public Name getType() {
        return type;
    }

    protected Logger getLogger(){
        return LOGGER;
    }

}
