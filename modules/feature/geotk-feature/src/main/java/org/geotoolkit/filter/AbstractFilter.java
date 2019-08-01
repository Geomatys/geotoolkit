/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.filter;

import java.util.function.Predicate;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;

/**
 * Implements Predicate
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractFilter implements Filter, Predicate<Feature> {

    @Override
    public boolean test(Feature t) {
        return evaluate(t);
    }

}
