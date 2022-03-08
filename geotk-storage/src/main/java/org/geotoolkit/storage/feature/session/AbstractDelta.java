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

package org.geotoolkit.storage.feature.session;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.filter.Filter;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
abstract class AbstractDelta implements Delta{

    protected static final FilterFactory2 FF = FilterUtilities.FF;

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.data.session");

    protected final Session session;
    protected final String type;

    public AbstractDelta(final Session session,final String type){
        ensureNonNull("session", session);
        ensureNonNull("name", type);
        this.session = session;
        this.type = type;
    }

    static List<Filter<Object>> list(final Filter<Object> filter) {
        if (filter.getOperatorType() == LogicalOperatorName.OR) {
            return ((LogicalOperator) filter).getOperands();
        } else if (filter == Filter.exclude()) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(filter);
        }
    }

    @Override
    public String getType() {
        return type;
    }

    protected Logger getLogger(){
        return LOGGER;
    }
}
