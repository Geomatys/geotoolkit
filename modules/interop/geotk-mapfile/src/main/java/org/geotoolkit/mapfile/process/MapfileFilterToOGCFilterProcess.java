/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.mapfile.process;

import java.util.List;
import java.util.ArrayList;

import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.mapfile.process.MapfileFilterToOGCFilterDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MapfileFilterToOGCFilterProcess extends AbstractProcess{
    
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final FilterFactory FF = new DefaultFilterFactory2();
        
    public MapfileFilterToOGCFilterProcess(final ParameterValueGroup input){
        super(INSTANCE, input);
    }
    
    @Override
    public ParameterValueGroup call() throws ProcessException{
        
        String text  = value(IN_TEXT, inputParameters);
        final Expression ref  = value(IN_REFERENCE, inputParameters);
       
        text = text.trim();
        if(text.startsWith("\"") || text.startsWith("'")){
            text = text.substring(1, text.length()-1);
        }
        
        final Filter filter;
        if(text.startsWith("/")){
            //pattern match
            text = text.substring(1, text.length()-1);
            //TODO we handle only basic type for now
            
            final String[] parts = text.split("\\|");
            if(parts.length == 1){
                //Equal filter
                filter = FF.equals(ref, FF.literal(text));
            }else{
                //several equal filter
                final List<Filter> filters = new ArrayList<Filter>();
                for(String part : parts){
                    final Filter f = FF.equals(ref, FF.literal(part));
                    filters.add(f);
                }
                filter = FF.or(filters);
            }
        }else{
            filter = FF.equals(ref, FF.literal(text));
        }
        
        getOrCreate(OUT_FILTER, outputParameters).setValue(filter);
        return outputParameters;
    }
    
    
}
