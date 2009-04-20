/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.report;

import java.awt.Component;
import java.util.Collection;

/**
 * A JRMapper is a class mapping a jasper report field to his
 * related value. This mapper is able to extract the value from a
 * complexe record (candidate) object.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface JRMapper<V,C> {

    /**
     * @return the factory who generated this mapper.
     */
    JRMapperFactory<V,C> getFactory();

    /**
     * Set the current record
     */
    void setCandidate(C candidate);

    /**
     * Returns a component for user interface who can edit the mapper properties.
     * @return component
     */
    Component getComponent();

    /**
     * Return the result value for JasperReport.
     */
    V getValue(Collection renderedValues);

}
