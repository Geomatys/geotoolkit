/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.display2d.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.Go2UtilitiesTest;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import static org.junit.Assert.*;

/**
 * Testing portrayal service.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PortrayalServiceTest {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private final FeatureCollection col;

    private final List<Envelope> envelopes = new ArrayList<Envelope>();
    private final List<Date[]> dates = new ArrayList<Date[]>();
    private final List<Double[]> elevations = new ArrayList<Double[]>();

    public PortrayalServiceTest() throws Exception {

        // create the feature collection for tests -----------------------------
        final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.setName("test");
        sftb.add("geom", Point.class, DefaultGeographicCRS.WGS84);
        sftb.add("att1", String.class);
        sftb.add("att2", Double.class);
        final SimpleFeatureType sft = sftb.buildFeatureType();
        col = DataUtilities.collection("id", sft);

        final FeatureWriter writer = col.getSession().getDataStore().getFeatureWriterAppend(sft.getName());

        SimpleFeature sf = (SimpleFeature) writer.next();
        sf.setAttribute("geom", GF.createPoint(new Coordinate(0, 0)));
        sf.setAttribute("att1", "value1");
        writer.write();
        sf = (SimpleFeature) writer.next();
        sf.setAttribute("geom", GF.createPoint(new Coordinate(-180, -90)));
        sf.setAttribute("att1", "value1");
        writer.write();
        sf = (SimpleFeature) writer.next();
        sf.setAttribute("geom", GF.createPoint(new Coordinate(-180, 90)));
        sf.setAttribute("att1", "value1");
        writer.write();
        sf = (SimpleFeature) writer.next();
        sf.setAttribute("geom", GF.createPoint(new Coordinate(180, -90)));
        sf.setAttribute("att1", "value1");
        writer.write();
        sf = (SimpleFeature) writer.next();
        sf.setAttribute("geom", GF.createPoint(new Coordinate(180, -90)));
        sf.setAttribute("att1", "value1");
        writer.write();

        writer.close();


        //create a serie of envelopes for tests --------------------------------
        GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);
        envelopes.add(env);
        env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -12, 31);
        env.setRange(1, -5, 46);
        envelopes.add(env);
        env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        envelopes.add(env);
        env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -5, 46);
        env.setRange(1, -12, 31);
        envelopes.add(env);
        env = new GeneralEnvelope(CRS.decode("EPSG:3395"));
        env.setRange(0, -1200000, 3100000);
        env.setRange(1, -500000, 4600000);
        envelopes.add(env);

        //create a serie of date ranges ----------------------------------------
        dates.add(new Date[]{new Date(1000),new Date(15000)});
        dates.add(new Date[]{null,          new Date(15000)});
        dates.add(new Date[]{new Date(1000),null});
        dates.add(new Date[]{null,          null});

        //create a serie of elevation ranges -----------------------------------
        elevations.add(new Double[]{-15d,   50d});
        elevations.add(new Double[]{null,   50d});
        elevations.add(new Double[]{-15d,   null});
        elevations.add(new Double[]{null,   null});

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testEnvelopeNotNull() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        MapContext context = MapBuilder.createContext(CRS.decode("EPSG:4326"));
        GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), null),
                new SceneDef(context),
                new ViewDef(env));



        //CRS can not obtain envelope for this projection. we check that we don't reaise any error.
        context = MapBuilder.createContext(CRS.decode("CRS:84"));
        env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), null),
                new SceneDef(context),
                new ViewDef(env));


    }

    @Test
    public void testFeatureRendering() throws Exception{
        final MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
        context.layers().add(MapBuilder.createFeatureLayer(col, SF.style(SF.pointSymbolizer())));

        assertEquals(1, context.layers().size());

        for(final Envelope env : envelopes){
            for(Date[] drange : dates){
                for(Double[] erange : elevations){
                    final Envelope cenv = GO2Utilities.combine(env, drange, erange);
                    BufferedImage img = DefaultPortrayalService.portray(
                        new CanvasDef(new Dimension(800, 600), null),
                        new SceneDef(context),
                        new ViewDef(cenv));
                    assertNotNull(img);
                }
            }
        }
        
    }

//    @Test
//    public void testCoverageRendering() throws Exception{
//
//    }


}
