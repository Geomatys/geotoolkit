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

package org.geotoolkit.report;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.OutputDef;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.report.graphic.map.MapDef;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Sanity test for reports.
 * Testing the content of the generated reports is difficult.
 *
 * @author Johann Sorel (Geomatys)
 * @modul pending
 */
public class JasperReportServiceTest extends org.geotoolkit.test.TestBase {

    private static final MutableStyleFactory SF = new DefaultStyleFactory();

    public JasperReportServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPDF() throws JRException, IOException {

        final MapContext context = createContext();

        final URL template = JasperReportServiceTest.class.getResource("/report/MapReport.jrxml");
        final Entry<JasperReport,FeatureType> entry = JasperReportService.prepareTemplate(template);
        final JasperReport report = entry.getKey();
        final FeatureType type = entry.getValue();

        List<Feature> collection = new ArrayList<>();
        Feature feature = type.newInstance();
        feature.setPropertyValue("map",new MapDef(
                new CanvasDef(new Dimension(1, 1), Color.RED),
                new SceneDef(context),
                new ViewDef(context.getBounds()),
                null));
        collection.add(feature);

        final File tempfile = File.createTempFile("report", ".pdf");
        tempfile.deleteOnExit();
        OutputDef output = new OutputDef(JasperReportService.MIME_PDF, tempfile);
        JasperReportService.generateReport(report, collection, null, output);

        //just test it's not empty
        assertTrue(tempfile.length() > 1000);
    }


    @Test
    public void testHTML() throws JRException, IOException {

        final MapContext context = createContext();

        final URL template = JasperReportServiceTest.class.getResource("/report/MapReport.jrxml");
        final Entry<JasperReport,FeatureType> entry = JasperReportService.prepareTemplate(template);
        final JasperReport report = entry.getKey();
        final FeatureType type = entry.getValue();

        List<Feature> collection = new ArrayList<>();
        Feature feature = type.newInstance();
        feature.setPropertyValue("map",new MapDef(
                new CanvasDef(new Dimension(1, 1), Color.RED),
                new SceneDef(context),
                new ViewDef(context.getBounds()),
                null));
        collection.add(feature);

        final File tempfile = File.createTempFile("report", ".html");
        tempfile.deleteOnExit();
        OutputDef output = new OutputDef(JasperReportService.MIME_HTML, tempfile);
        JasperReportService.generateReport(report, collection, null, output);

        //just test it's not empty
        assertTrue(tempfile.length() > 1000);
    }


    private static MapContext createContext(){

        //create a coverage for the test
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0,  -10,  10);
        env.setRange(1, -10, 10);
        final BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = img.createGraphics();
        final LinearGradientPaint paint = new LinearGradientPaint(0, 0, 1000, 0,
                new float[]{0f,         0.5f,      1f},
                new Color[]{Color.CYAN, Color.RED, Color.GREEN});
        g.setPaint(paint);
        g.fill(new Rectangle(0, 0, 1000, 1000));
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setEnvelope(env);
        gcb.setRenderedImage(img);
        final GridCoverage coverage = gcb.getGridCoverage2D();

        final MapContext context = MapBuilder.createContext();
        context.layers().add(MapBuilder.createCoverageLayer(coverage,
                SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "coverage"));
        return context;
    }

}
