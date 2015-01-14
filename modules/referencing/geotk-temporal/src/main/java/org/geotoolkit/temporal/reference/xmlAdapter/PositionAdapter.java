///*
// *    Geotoolkit - An Open Source Java GIS Toolkit
// *    http://www.geotoolkit.org
// * 
// *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
// *    (C) 2014, Geomatys
// *
// *    This library is free software; you can redistribute it and/or
// *    modify it under the terms of the GNU Lesser General Public
// *    License as published by the Free Software Foundation;
// *    version 2.1 of the License.
// *
// *    This library is distributed in the hope that it will be useful,
// *    but WITHOUT ANY WARRANTY; without even the implied warranty of
// *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// *    Lesser General Public License for more details.
// */
//package org.geotoolkit.temporal.reference.xmlAdapter;
//
//import javax.xml.bind.annotation.adapters.XmlAdapter;
//import org.geotoolkit.temporal.object.DefaultPosition;
//import org.opengis.temporal.Position;
//
///**
// * JAXB adapter for {@link DefaultPosition} values mapped to {@link Position}.
// *
// * @author Remi Marechal (Geomatys).
// * @version 4.0
// * @since   4.0
// */
//public class PositionAdapter extends XmlAdapter<DefaultPosition, Position> {
//
//    /**
//     * Converts an object read from a XML stream to an {@link Position}
//     * implementation. JAXB invokes automatically this method at unmarshalling time.
//     *
//     * @param  adapter The adapter for the {@link DefaultPosition} value.
//     * @return An {@link Position} for the {@link DefaultPosition} value.
//     */
//    @Override
//    public Position unmarshal(DefaultPosition v) throws Exception {
//        return v;
//    }
//
//    /**
//     * Converts an {@link Position} to an object to formatted into a
//     * XML stream. JAXB invokes automatically this method at marshalling time.
//     *
//     * @param  value The {@link Position} value.
//     * @return The adapter for the {@link Position}.
//     */
//    @Override
//    public DefaultPosition marshal(Position v) throws Exception {
//        return DefaultPosition.castOrCopy(v);
//    }
//}
