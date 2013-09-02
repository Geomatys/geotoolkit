/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.s57;

import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * S-57 type bank.
 * Implement and register in META-INF to add new types.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface TypeBank {

    Set<String> getFeatureTypeNames();

    Set<String> getPropertyTypeNames();

    int getFeatureTypeCode(String name) throws DataStoreException;

    String getFeatureTypeName(int code) throws DataStoreException;

    int getPropertyTypeCode(String name) throws DataStoreException;

    String getPropertyTypeName(int code) throws DataStoreException;

    FeatureType getFeatureType(String name, CoordinateReferenceSystem crs) throws DataStoreException;

    FeatureType getFeatureType(int code, CoordinateReferenceSystem crs) throws DataStoreException;

    AttributeDescriptor getAttributeDescriptor(final String code) throws DataStoreException;

    AttributeDescriptor getAttributeDescriptor(final int code) throws DataStoreException;

}
