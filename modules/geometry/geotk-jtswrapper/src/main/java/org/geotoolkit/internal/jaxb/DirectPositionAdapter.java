/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.internal.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.opengis.geometry.coordinate.Position;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DirectPositionAdapter extends XmlAdapter<DirectPositionType, Position>{

    /**
     * Empty constructor for JAXB only.
     */
    public DirectPositionAdapter() {
    }

    /**
     * Substitutes the adapter value read from an XML stream by the object which will
     * contains the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param  adapter The adapter for this metadata value.
     * @return A code list which represents the metadata value.
     */
    @Override
    public Position unmarshal(DirectPositionType v) throws Exception {
        return new GeneralDirectPosition(v);
    }

    @Override
    public DirectPositionType marshal(Position v) throws Exception {
        return new DirectPositionType(v);
    }


}
