/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gml.xml;

import java.util.List;
import java.util.logging.Level;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.util.logging.Logging;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Definition of a GML boundary.
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface Envelope extends org.opengis.geometry.Envelope, AbstractGeometry, WithCoordinates {

    void setSrsDimension(Integer dim);

    //DirectPosition getLowerCorner();

    //DirectPosition getUpperCorner();

    List<String> getAxisLabels();

    void setAxisLabels(final List<String> axisLabels);

    List<String> getUomLabels();

    List<? extends DirectPosition> getPos();

    boolean isCompleteEnvelope2D();

    @Override
    default public CoordinateReferenceSystem getCoordinateReferenceSystem(boolean longitudeFirst) {
        if (getSrsName() != null) {
            try {
                CoordinateReferenceSystem crs = CRS.forCode(getSrsName());
                if (longitudeFirst) {
                    crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.RIGHT_HANDED);
                }
                return crs;
            } catch (FactoryException ex) {
                Logging.getLogger("org.geotoolkit.gml.xml.v321").log(Level.WARNING, "Could not decode CRS which name is : " + getSrsName(), ex);
            }
        }

        return null;
    }

    @Override
    default public int getCoordinateDimension() {
        Integer bi = getSrsDimension();
        if(bi == null){
            return 2;
        }else{
            return bi.intValue();
        }
    }

    @Override
    default public void setId(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void setSrsName(String srsName) {
        throw new UnsupportedOperationException();
    }



    @Override
    default public Code getParameterName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public default String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public default void setDescription(String description) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public default void setName(Identifier name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public default Reference getDescriptionReference() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public default Identifier getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public default String getId() {
        return null;
    }
}
