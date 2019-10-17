/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.data.session;

import java.util.logging.Logger;
import org.apache.sis.internal.system.DefaultFactories;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.logging.Logging;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
abstract class AbstractDelta implements Delta{

    protected static final FilterFactory2 FF = (FilterFactory2) DefaultFactories.forBuildin(FilterFactory.class);

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.session");

    protected final Session session;
    protected final String type;

    public AbstractDelta(final Session session,final String type){
        ensureNonNull("session", session);
        ensureNonNull("name", type);
        this.session = session;
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    protected Logger getLogger(){
        return LOGGER;
    }

}
