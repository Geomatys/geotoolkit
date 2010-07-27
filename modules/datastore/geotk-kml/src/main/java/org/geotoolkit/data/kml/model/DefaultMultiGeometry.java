/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultMultiGeometry extends DefaultAbstractGeometry implements MultiGeometry {

    private List<AbstractGeometry> geometries;

    /**
     * 
     */
    public DefaultMultiGeometry() {
        this.geometries = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param geometries
     * @param multiGeometrySimpleExtensions
     * @param multiGeometryObjectExtensions
     */
    public DefaultMultiGeometry(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            List<AbstractGeometry> geometries,
            List<SimpleTypeContainer> multiGeometrySimpleExtensions,
            List<Object> multiGeometryObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions,
                abstractGeometryObjectExtensions);
        this.geometries = (geometries == null) ? EMPTY_LIST : geometries;
        if (multiGeometrySimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.MULTI_GEOMETRY).addAll(multiGeometrySimpleExtensions);
        }
        if (multiGeometryObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.MULTI_GEOMETRY).addAll(multiGeometryObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractGeometry> getGeometries() {
        return this.geometries;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setGeometries(List<AbstractGeometry> geometries) {
        this.geometries = geometries;
    }
}
