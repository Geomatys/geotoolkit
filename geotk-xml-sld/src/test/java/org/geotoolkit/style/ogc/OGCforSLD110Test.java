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
package org.geotoolkit.style.ogc;

import javax.xml.bind.JAXBException;
import org.apache.sis.geometry.Envelope2D;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.Literal;
import org.opengis.filter.ValueReference;
import org.opengis.filter.SortProperty;
import org.opengis.filter.SortOrder;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.referencing.CommonCRS;
import static org.junit.Assert.*;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.SpatialOperatorName;

/**
 * Test class for Filter and Expression jaxb marshelling and unmarshelling.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OGCforSLD110Test {

    @Test
    public void testCustom1() throws JAXBException, FactoryException{

        StyleXmlIO util = new StyleXmlIO();
        final Filter filter = util.readFilter(OGCforSLD110Test.class.getResource("/org/geotoolkit/test/filter/filterbbox.xml"), org.geotoolkit.sld.xml.Specification.Filter.V_1_1_0);

        assertEquals(LogicalOperatorName.OR, filter.getOperatorType());

        final LogicalOperator<Object> or = (LogicalOperator) filter;
        final Filter f1 = or.getOperands().get(0);
        final Filter f2 = or.getOperands().get(1);
        assertEquals(ComparisonOperatorName.PROPERTY_IS_EQUAL_TO, f1.getOperatorType());
        assertEquals(SpatialOperatorName.BBOX, f2.getOperatorType());

        final BinaryComparisonOperator<Object> ff1 = (BinaryComparisonOperator) f1;
        final BinarySpatialOperator<Object> ff2 = (BinarySpatialOperator) f2;

        assertTrue(ff1.getOperand1() instanceof ValueReference);
        assertTrue(ff1.getOperand2() instanceof Literal);
        assertTrue(ff2.getOperand1() instanceof ValueReference);
        assertTrue(ff2.getOperand2() instanceof Literal);

        assertEquals("sf:str4Property", ((ValueReference)ff1.getOperand1()).getXPath());
        assertEquals("abc3", ((Literal)ff1.getOperand2()).getValue());
        assertEquals("sf:attribut.Géométrie", ((ValueReference)ff2.getOperand1()).getXPath());

        final Envelope2D geom = new Envelope2D( (Envelope) ((Literal)ff2.getOperand2()).getValue());
        assertEquals(34d,geom.getMinX(),1e-7);
        assertEquals(40d,geom.getMaxX(),1e-7);
        assertEquals(15d,geom.getMinY(),1e-7);
        assertEquals(19d,geom.getMaxY(),1e-7);
        final CoordinateReferenceSystem crs = geom.getCoordinateReferenceSystem();
        assertEquals(CommonCRS.WGS84.geographic(), crs);
    }

    @Test
    public void testCustom2() throws JAXBException, FactoryException {
        StyleXmlIO util = new StyleXmlIO();
        final SortProperty sort = util.readSortBy(
                OGCforSLD110Test.class.getResource("/org/geotoolkit/test/filter/sortby.xml"),
                org.geotoolkit.sld.xml.Specification.Filter.V_1_1_0);

        assertEquals("sf:str4Property", sort.getValueReference().getXPath());
        assertEquals(SortOrder.ASCENDING, sort.getSortOrder());
    }
}
