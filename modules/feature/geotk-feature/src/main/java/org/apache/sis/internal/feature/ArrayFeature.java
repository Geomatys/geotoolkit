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
package org.apache.sis.internal.feature;

import org.opengis.feature.Feature;

/**
 * A array feature is a feature which values are accessible throught an array.
 * It should not be confound with SimpleFeature which is a set of constraint
 * to comply to be considered 'simple'
 *
 * Array define property exactly is the order defined in feature type.
 * Indexes must match, properties who do not have any real values like operations
 * must reserve a space in the array (even knowing it will never be used), this is
 * to ensure indexes with the feature type are preserved.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface ArrayFeature extends Feature {

    void setPropertyValue(int index, Object value);

    Object getPropertyValue(int index);

}
