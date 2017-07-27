/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wps.adaptor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.wps.xml.v200.DataInputType;
import org.geotoolkit.wps.xml.v200.Format;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GMLAdaptorTest {
    
    @Test
    public void featureWPS2() throws FactoryException {
                
        final Format format = new Format("UTF-8", "text/xml", "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd", null);
        
        final ComplexAdaptor adaptor = ComplexAdaptor.getAdaptor(format);
        assertEquals(Feature.class, adaptor.getValueClass());
        
        
        
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Country");
        ftb.addAttribute(String.class).setName("code").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(Polygon.class).setName("geom").setCRS(CommonCRS.WGS84.geographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType ft = ftb.build();
        
        final GeometryFactory gf = new GeometryFactory();
        final LinearRing ring = gf.createLinearRing(new Coordinate[]{
                                    new Coordinate(23, 78),
                                    new Coordinate(-10, 43),
                                    new Coordinate(12, 94),
                                    new Coordinate(23, 78)});
        final Polygon polygon = gf.createPolygon(ring, new LinearRing[0]);
        
        final Feature feature = ft.newInstance();
        feature.setPropertyValue("code", "id-1");
        feature.setPropertyValue("geom", polygon);
        
        DataInputType out = adaptor.toWPS2Input(feature);
        System.out.println(out);
        
        
        
    }
}
