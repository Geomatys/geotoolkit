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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a complex input into a JTS Geometry array.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public final class ComplexToGeometryArrayConverter extends AbstractComplexInputConverter<Geometry[]> {

    private static ComplexToGeometryArrayConverter INSTANCE;

    private ComplexToGeometryArrayConverter(){
    }

    public static synchronized ComplexToGeometryArrayConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ComplexToGeometryArrayConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? extends Geometry[]> getTargetClass() {
        return Geometry[].class;
    }
    
    /**
     * {@inheritDoc}
     * @return Geometry array.
     */
    @Override
    public Geometry[] convert(final ComplexDataType source, final Map<String, Object> params) throws NonconvertibleObjectException {

        try {                
            final List<Object> data = source.getContent();
            if(!data.isEmpty()){
                final List<Geometry> geoms = new ArrayList<Geometry>();
                for(int i = 0; i<data.size(); i++){
                    geoms.add(GeometrytoJTS.toJTS((AbstractGeometryType) data.get(i)));
                }
                return geoms.toArray(new Geometry[geoms.size()]);
            }else{
                throw new NonconvertibleObjectException("Invalid data input : Empty geometry list.");
            }
        }catch(ClassCastException ex){
            throw new NonconvertibleObjectException("Invalid data input : empty GML geometry.",ex);
        }catch (FactoryException ex) {
            throw new NonconvertibleObjectException("Invalid data input : Cannot convert GML geometry.",ex);
        }
    }
}