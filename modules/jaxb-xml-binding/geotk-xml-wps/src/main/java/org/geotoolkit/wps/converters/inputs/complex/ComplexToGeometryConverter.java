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
package org.geotoolkit.wps.converters.inputs.complex;


import com.vividsolutions.jts.geom.Geometry;
import java.util.List;
import java.util.Map;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.opengis.util.FactoryException;


/**
 * Implementation of ObjectConverter to convert a complex input into a JTS Geometry.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public final class ComplexToGeometryConverter extends AbstractComplexInputConverter<Geometry> {

    private static ComplexToGeometryConverter INSTANCE;

    private ComplexToGeometryConverter(){
    }

    public static synchronized ComplexToGeometryConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ComplexToGeometryConverter();
        }
        return INSTANCE;
    }
 
    @Override
    public Class<? extends Geometry> getTargetClass() {
        return Geometry.class;
    }
    
    /**
     * {@inheritDoc}
     * @return Geometry.
     */
    @Override
    public Geometry convert(final ComplexDataType source, final Map<String, Object> params) throws NonconvertibleObjectException {

        try {                
            final List<Object> data = source.getContent();
            if(data.size() == 1){
                return GeometrytoJTS.toJTS((AbstractGeometryType) data.get(0));
            }else{
                throw new NonconvertibleObjectException("Invalid data input : Only one geometry expected.");
            }
        }catch(ClassCastException ex){
            throw new NonconvertibleObjectException("Invalid data input : empty GML geometry.",ex);
        }catch (FactoryException ex) {
            throw new NonconvertibleObjectException("Invalid data input : Cannot convert GML geometry.",ex);
        }
    }
}