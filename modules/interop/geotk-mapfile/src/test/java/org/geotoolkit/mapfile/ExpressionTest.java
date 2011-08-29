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
package org.geotoolkit.mapfile;

import org.opengis.filter.Filter;
import java.io.IOException;

import org.geotoolkit.process.ProcessException;
import org.junit.Test;

import org.geotoolkit.process.Process;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.style.DefaultStyleFactory;

import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.mapfile.process.MapfileFilterToOGCFilterDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Test mapfile expression to OGC Filter.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ExpressionTest {

    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    
    private final ProcessDescriptor desc;
    
    public ExpressionTest() throws NoSuchIdentifierException {
        desc = ProcessFinder.getProcessDescriptor("mapfile", "MFFilterToOGCFilter");
    }

    @Test
    public void testSingleQuoteString() throws IOException, ProcessException {
        
        final Expression reference = FF.property("name");
        
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        getOrCreate(IN_TEXT, input).setValue("\'husky\'");
        getOrCreate(IN_REFERENCE, input).setValue(reference);
        
        final Process process = desc.createProcess(input);        
        final ParameterValueGroup output = process.call();
        final Filter result = value(OUT_FILTER, output);
        
        assertEquals(FF.equals(reference, FF.literal("husky")), result);        
    }
    
    @Test
    public void testDoubleQuoteString() throws IOException, ProcessException {
        
        final Expression reference = FF.property("name");
        
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        getOrCreate(IN_TEXT, input).setValue("\"husky\"");
        getOrCreate(IN_REFERENCE, input).setValue(reference);
        
        final Process process = desc.createProcess(input);        
        final ParameterValueGroup output = process.call();
        final Filter result = value(OUT_FILTER, output);
        
        assertEquals(FF.equals(reference, FF.literal("husky")), result);        
    }
    
    @Test
    public void testMultipleChoice() throws IOException, ProcessException {
        
        final Expression reference = FF.property("name");
        
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        getOrCreate(IN_TEXT, input).setValue("/joe|marcel|emile/");
        getOrCreate(IN_REFERENCE, input).setValue(reference);
        
        final Process process = desc.createProcess(input);        
        final ParameterValueGroup output = process.call();
        final Filter result = value(OUT_FILTER, output);
        
        
        final Filter f1 = FF.equals(reference,FF.literal("joe"));
        final Filter f2 = FF.equals(reference,FF.literal("marcel"));
        final Filter f3 = FF.equals(reference,FF.literal("emile"));
        final Filter combine = FF.or(UnmodifiableArrayList.wrap(f1,f2,f3));
        
        assertEquals(combine, result);        
    }

}
