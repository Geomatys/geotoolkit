/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.report;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.geotoolkit.report.JRMappingUtils;
import org.geotoolkit.util.collection.WeakHashSet;

/**
 * Implementation of a JRDataSource backed by a Map of JRMappers and an Iterator.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JRMappedDataSource<C> implements JRDataSource{

    private final Map<String,JRMapper<?, ? super C>> map = new HashMap<String, JRMapper<?, ? super C>>();
    private final Collection<Object> renderedValues = new WeakHashSet<Object>(Object.class);
    private Iterator<C> iterator = null;
    private C currentRecord = null;

    public JRMappedDataSource(){
    }

    /**
     * Force the datasource to generate the best JRField-JRMapper mapping for the given template.
     */
    public void findMapping(final JasperDesign design){

        for(final JRField field : design.getFields()){
            JRMapper mapper = findBestMapper(field);
            if(mapper != null){
                map.put(field.getName(), mapper);
            }
        }

    }

    private JRMapper findBestMapper(final JRField field){
        final String name                     = field.getName();
        final Class classe                    = field.getValueClass();

        for(final JRMapperFactory factory : JRMappingUtils.getFactories((Class)classe)){
            for(final String favorite : factory.getFavoritesFieldName()){
                if(favorite.equals(name)) return factory.createMapper();
            }
        }

        return null;
    }

    /**
     * Set the iterator that will provide the records.
     *
     * @param iterator : iterator of C
     */
    public void setIterator(Iterator<C> iterator){
        this.iterator = iterator;
    }

    /**
     * Returns the live map associating the field name with a JRMapper object.
     */
    public Map<String, JRMapper<?,? super C>> mapping() {
        return map;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean next() throws JRException {
        renderedValues.clear();

        if(iterator == null) return false;

        if(iterator.hasNext()){
            currentRecord = iterator.next();
            return true;
        }else{
            return false;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getFieldValue(final JRField field) throws JRException {
        final String fieldName = field.getName();
        
        //no record
        if(currentRecord == null) return null;

        final JRMapper<?,? super C> mapper = map.get(fieldName);

        //no mapper associeted for this field
        if(mapper == null) return null;

        mapper.setCandidate(currentRecord);
        Object renderedValue = mapper.getValue(renderedValues);
        renderedValues.add(renderedValue);
        return renderedValue;
    }

}
