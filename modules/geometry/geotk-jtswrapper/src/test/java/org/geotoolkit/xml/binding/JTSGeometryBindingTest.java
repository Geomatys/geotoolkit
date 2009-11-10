/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.xml.binding;

// JUnit dependencies
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSEnvelope;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPoint;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPrimitive;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.complex.JTSCompositeCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPoint;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPolyhedralSurface;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSRing;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSSurfaceBoundary;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.internal.jaxb.PolygonType;
import org.geotoolkit.internal.jaxb.PolyhedralSurfaceType;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.primitive.Ring;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.junit.Assert.*;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JTSGeometryBindingTest {

    private static final Logger LOGGER = Logger.getAnonymousLogger();
    
    private MarshallerPool pool;

    private Unmarshaller un;

    private Marshaller m;

    private ObjectFactory factory;

    @BeforeClass
    public static void setUpClass() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {

     }

    @Before
    public void setUp() throws Exception {
        pool    = new MarshallerPool(ObjectFactory.class);
        factory = new ObjectFactory();
        un      = pool.acquireUnmarshaller();
        m       = pool.acquireMarshaller();

        File xsdDirectory = getDirectoryFromResource("org.geotoolkit.gml.311.base");
        SchemaFactory sf = SchemaFactory.newInstance(
        javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File(xsdDirectory, "gml.xsd"));
        un.setSchema(schema);
        //m.setSchema(schema);

    }

    @After
    public void tearDown() throws Exception {
        if (un != null) {
            pool.release(un);
        }

        if (m != null) {
            pool.release(m);
        }
    }

    /**
     * Searches in the Context ClassLoader for the named directory and returns it.
     *
     * @param packagee The name of package.
     *
     * @return A directory if it exist.
     */
    public static File getDirectoryFromResource(final String packagee) {
        File result = null;
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try {
            final String fileP = packagee.replace('.', '/');
            final Enumeration<URL> urls = classloader.getResources(fileP);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                try {
                    final URI uri = url.toURI();
                    result  = scanDir(uri, fileP);
                } catch (URISyntaxException e) {
                    LOGGER.severe("URL, " + url + "cannot be converted to a URI");
                }
            }
        } catch (IOException ex) {
            LOGGER.severe("The resources for the package" + packagee + ", could not be obtained");
        }


        return result;
    }

    /**
     * Scan a resource file (a JAR or a directory) and return it as a File.
     *
     * @param u The URI of the file.
     * @param filePackageName The package to scan.
     *
     * @return a list of package names.
     * @throws java.io.IOException
     */
    public static File scanDir(final URI u, final String filePackageName) throws IOException {
        final String scheme = u.getScheme();
        if (scheme.equals("file")) {
            final File f = new File(u.getPath());
            if (f.isDirectory()) {
                return f;
            }
        } else if (scheme.equals("jar") || scheme.equals("zip")) {
            final File f = new File(System.getProperty("java.io.tmpdir") + "/Constellation");
            if (f != null && f.exists()) {
                final File fConfig = new File(f, filePackageName);
                if (fConfig.exists() && fConfig.isDirectory()) {
                    return fConfig;
                } else {
                    LOGGER.info("The configuration directory was not found in the temporary folder.");
                }
            } else {
                LOGGER.info("The Constellation directory was not present in the temporary folder.");
            }
        }
        return null;
    }

    /**
     * Test point Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void PointMarshalingTest() throws Exception {

        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        assertTrue(crs != null);

        DirectPosition dp = new GeneralDirectPosition(crs);
        dp.setOrdinate(0, 2.1);
        dp.setOrdinate(1, 12.6);
        JTSPoint point = new JTSPoint(dp, crs);
        
        StringWriter sw = new StringWriter();
        m.marshal(factory.createJTSPoint(point), sw);

        String result = sw.toString();

        String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"              + '\n' +
                           "<gml:Point srsName=\"EPSG:4326\" xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
                           "    <gml:pos srsName=\"EPSG:4326\" srsDimension=\"2\">2.1 12.6</gml:pos>"   + '\n' +
                           "</gml:Point>" + '\n';

        assertEquals(expResult, result);

        pool.release(m);
    }


    /**
     * Test point Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void PointUnMarshalingTest() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"    + '\n' +
                     "<gml:Point srsName=\"EPSG:4326\" xmlns:gml=\"http://www.opengis.net/gml\">"             + '\n' +
                     "    <gml:pos srsName=\"EPSG:4326\" srsDimension=\"2\">2.1 12.6</gml:pos>" + '\n' +
                     "</gml:Point>" + '\n';

        JAXBElement result = (JAXBElement) un.unmarshal(new StringReader(xml));


        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        assertTrue(crs != null);

        DirectPosition dp = new GeneralDirectPosition(crs);
        dp.setOrdinate(0, 2.1);
        dp.setOrdinate(1, 12.6);
        JTSPoint expResult = new JTSPoint(dp, crs);

        assertEquals(expResult, result.getValue());

        
    }

     /**
     * Test curve Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void CurveMarshalingTest() throws Exception {
    
        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        JTSCurve curve = new JTSCurve(crs);
        JTSLineString line1 = new JTSLineString();
        DirectPosition p1 = new GeneralDirectPosition(crs);
        p1.setOrdinate(0, 401500);
        p1.setOrdinate(1, 3334500);
        DirectPosition p2 = new GeneralDirectPosition(crs);
        p2.setOrdinate(0, 401700);
        p2.setOrdinate(1, 3334850);
        DirectPosition p3 = new GeneralDirectPosition(crs);
        p3.setOrdinate(0, 402200);
        p3.setOrdinate(1, 3335200);

        line1.getControlPoints().add(p1);
        line1.getControlPoints().add(p2);
        line1.getControlPoints().add(p3);

        curve.getSegments().add(line1);

        JTSLineString line2 = new JTSLineString();
        DirectPosition p21 = new GeneralDirectPosition(crs);
        p21.setOrdinate(0, 402320);
        p21.setOrdinate(1, 3334850);
        DirectPosition p22 = new GeneralDirectPosition(crs);
        p22.setOrdinate(0, 402200);
        p22.setOrdinate(1, 3335200);

        line2.getControlPoints().add(p21);
        line2.getControlPoints().add(p22);
        curve.getSegments().add(line2);

        
        StringWriter sw = new StringWriter();
        m.marshal(factory.createJTSCurve(curve), sw);

        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                 + '\n' +
        "<gml:Curve srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"                   + '\n' +
        "    <gml:segments>"                                                                            + '\n' +
        "        <gml:LineStringSegment interpolation=\"linear\">"                                      + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>"   + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>"   + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>"   + '\n' +
        "        </gml:LineStringSegment>"                                                              + '\n' +
        "        <gml:LineStringSegment interpolation=\"linear\">"                                      + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>"   + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>"   + '\n' +
        "        </gml:LineStringSegment>"                                                              + '\n' +
        "    </gml:segments>"                                                                           + '\n' +
        "</gml:Curve>"                                                                                  + '\n';

        
        assertEquals(expResult, result);

        pool.release(m);
    }

     /**
     * Test curve Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void CurveUnmarshalingTest() throws Exception {

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                 + '\n' +
        "<gml:Curve srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"                   + '\n' +
        "    <gml:segments>"                                                                            + '\n' +
        "        <gml:LineStringSegment interpolation=\"linear\">"                                      + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>"   + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>"   + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>"   + '\n' +
        "        </gml:LineStringSegment>"                                                              + '\n' +
        "        <gml:LineStringSegment interpolation=\"linear\">"                                      + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>"   + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>"   + '\n' +
        "        </gml:LineStringSegment>"                                                              + '\n' +
        "    </gml:segments>"                                                                           + '\n' +
        "</gml:Curve>"                                                                                  + '\n';

        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        JTSCurve expResult = new JTSCurve(crs);
        JTSLineString line1 = new JTSLineString();
        DirectPosition p1 = new GeneralDirectPosition(crs);
        p1.setOrdinate(0, 401500);
        p1.setOrdinate(1, 3334500);
        DirectPosition p2 = new GeneralDirectPosition(crs);
        p2.setOrdinate(0, 401700);
        p2.setOrdinate(1, 3334850);
        DirectPosition p3 = new GeneralDirectPosition(crs);
        p3.setOrdinate(0, 402200);
        p3.setOrdinate(1, 3335200);

        line1.getControlPoints().add(p1);
        line1.getControlPoints().add(p2);
        line1.getControlPoints().add(p3);

        expResult.getSegments().add(line1);

        JTSLineString line2 = new JTSLineString();
        DirectPosition p21 = new GeneralDirectPosition(crs);
        p21.setOrdinate(0, 402320);
        p21.setOrdinate(1, 3334850);
        DirectPosition p22 = new GeneralDirectPosition(crs);
        p22.setOrdinate(0, 402200);
        p22.setOrdinate(1, 3335200);

        line2.getControlPoints().add(p21);
        line2.getControlPoints().add(p22);
        expResult.getSegments().add(line2);

        
        JTSCurve result = (JTSCurve) ((JAXBElement) un.unmarshal(new StringReader(xml))).getValue();

        assertEquals(expResult.getSegments(), result.getSegments());
        assertEquals(expResult, result);

        
    }

    /**
     * Test envelope Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void EnvelopeMarshalingTest() throws Exception {

        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        DirectPosition p1 = new GeneralDirectPosition(crs);
        p1.setOrdinate(0, 402320);
        p1.setOrdinate(1, 3334850);
        DirectPosition p2 = new GeneralDirectPosition(crs);
        p2.setOrdinate(0, 402200);
        p2.setOrdinate(1, 3335200);
        
        JTSEnvelope envelope = new JTSEnvelope(p1, p2);


        
        StringWriter sw = new StringWriter();
        m.marshal(factory.createJTSEnvelope(envelope), sw);
        String result = sw.toString();

        String expresult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                               + '\n' +
        "<gml:Envelope xmlns:gml=\"http://www.opengis.net/gml\">"                                     + '\n' +
        "    <gml:lowerCorner srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:lowerCorner>" + '\n' +
        "    <gml:upperCorner srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:upperCorner>" + '\n' +
        "</gml:Envelope>" + '\n';

        assertEquals(expresult, result);
        pool.release(m);
    }

    /**
     * Test envelope Unmarshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void EnvelopeUnMarshalingTest() throws Exception {

        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        DirectPosition p1 = new GeneralDirectPosition(crs);
        p1.setOrdinate(0, 402000);
        p1.setOrdinate(1, 3334850);
        DirectPosition p2 = new GeneralDirectPosition(crs);
        p2.setOrdinate(0, 402200);
        p2.setOrdinate(1, 3335200);

        JTSEnvelope expResult = new JTSEnvelope(p1, p2);

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                               + '\n' +
        "<gml:Envelope xmlns:gml=\"http://www.opengis.net/gml\">"                                     + '\n' +
        "    <gml:lowerCorner srsName=\"EPSG:27572\" srsDimension=\"2\">402000.0 3334850.0</gml:lowerCorner>" + '\n' +
        "    <gml:upperCorner srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:upperCorner>" + '\n' +
        "</gml:Envelope>" + '\n';


        
        
        JTSEnvelope result = (JTSEnvelope) ((JAXBElement)un.unmarshal(new StringReader(xml))).getValue();

        assertEquals(expResult, result);

        
    }

    /**
     * Test multiPoint Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void MultiPointMarshalingTest() throws Exception {

        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        DirectPosition p1 = new GeneralDirectPosition(crs);
        p1.setOrdinate(0, 402000);
        p1.setOrdinate(1, 3334850);
        JTSPoint pt1 = new JTSPoint(p1);

        DirectPosition p2 = new GeneralDirectPosition(crs);
        p2.setOrdinate(0, 402200);
        p2.setOrdinate(1, 3335200);
        JTSPoint pt2 = new JTSPoint(p2);

        JTSMultiPoint multiPoint = new JTSMultiPoint(crs);
        multiPoint.getElements().add(pt1);
        multiPoint.getElements().add(pt2);

        
        StringWriter sw = new StringWriter();
        m.marshal(factory.createJTSMultiPoint(multiPoint), sw);
        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                               + '\n' +
        "<gml:MultiPoint srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"            + '\n' +
        "    <gml:pointMember>"                                                                       + '\n' +
        "        <gml:Point srsName=\"EPSG:27572\">"                                                  + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402000.0 3334850.0</gml:pos>" + '\n' +
        "        </gml:Point>"                                                                        + '\n' +
        "    </gml:pointMember>"                                                                      + '\n' +
        "    <gml:pointMember>"                                                                       + '\n' +
        "        <gml:Point srsName=\"EPSG:27572\">"                                                  + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "        </gml:Point>"                                                                        + '\n' +
        "    </gml:pointMember>"                                                                      + '\n' +
        "</gml:MultiPoint>" + '\n';

        assertEquals(expResult, result);
    }

    /**
     * Test multiPoint Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void MultiPointUnmarshalingTest() throws Exception {

        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        DirectPosition p1 = new GeneralDirectPosition(crs);
        p1.setOrdinate(0, 402000);
        p1.setOrdinate(1, 3334850);
        JTSPoint pt1 = new JTSPoint(p1);

        DirectPosition p2 = new GeneralDirectPosition(crs);
        p2.setOrdinate(0, 402200);
        p2.setOrdinate(1, 3335200);
        JTSPoint pt2 = new JTSPoint(p2);

        JTSMultiPoint expResult = new JTSMultiPoint(crs);
        expResult.getElements().add(pt1);
        expResult.getElements().add(pt2);

        

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                               + '\n' +
        "<gml:MultiPoint srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"            + '\n' +
        "    <gml:pointMember>"                                                                       + '\n' +
        "        <gml:Point srsName=\"EPSG:27572\">"                                                  + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "        </gml:Point>"                                                                        + '\n' +
        "    </gml:pointMember>"                                                                      + '\n' +
        "    <gml:pointMember>"                                                                       + '\n' +
        "        <gml:Point srsName=\"EPSG:27572\">"                                                  + '\n' +
        "            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402000.0 3334850.0</gml:pos>" + '\n' +
        "        </gml:Point>"                                                                        + '\n' +
        "    </gml:pointMember>"                                                                      + '\n' +
        "</gml:MultiPoint>" + '\n';

        JTSMultiPoint result = (JTSMultiPoint) ((JAXBElement)un.unmarshal(new StringReader(xml))).getValue();

        

        assertEquals(expResult, result);
    }

    /**
     * Test Composite curve Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void CompositeCurveMarshalingTest() throws Exception {

        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        DirectPosition p1 = new GeneralDirectPosition(crs);
        p1.setOrdinate(0, 402000);
        p1.setOrdinate(1, 3334850);
        

        DirectPosition p2 = new GeneralDirectPosition(crs);
        p2.setOrdinate(0, 402200);
        p2.setOrdinate(1, 3335200);


        JTSLineString l1 = new JTSLineString();
        l1.getControlPoints().add(p1);
        l1.getControlPoints().add(p2);

        JTSCurve c2 = new JTSCurve(crs);
        c2.getSegments().add(l1);

        JTSCurve c1 = new JTSCurve(crs);
        JTSLineString l2 = new JTSLineString();
        DirectPosition p21 = new GeneralDirectPosition(crs);
        p21.setOrdinate(0, 401500);
        p21.setOrdinate(1, 3334500);
        DirectPosition p22 = new GeneralDirectPosition(crs);
        p22.setOrdinate(0, 401700);
        p22.setOrdinate(1, 3334850);
        DirectPosition p23 = new GeneralDirectPosition(crs);
        p23.setOrdinate(0, 402200);
        p23.setOrdinate(1, 3335200);

        l2.getControlPoints().add(p21);
        l2.getControlPoints().add(p22);
        l2.getControlPoints().add(p23);

        c1.getSegments().add(l2);

        JTSLineString l3 = new JTSLineString();
        DirectPosition p31 = new GeneralDirectPosition(crs);
        p31.setOrdinate(0, 402320);
        p31.setOrdinate(1, 3334850);
        DirectPosition p32 = new GeneralDirectPosition(crs);
        p32.setOrdinate(0, 402200);
        p32.setOrdinate(1, 3335200);

        l3.getControlPoints().add(p31);
        l3.getControlPoints().add(p32);
        c1.getSegments().add(l3);

        JTSCompositeCurve compositeCurve = new JTSCompositeCurve(null, crs);
       // compositeCurve.getElements().add(l1); TODO
        compositeCurve.getElements().add(c1);
        compositeCurve.getElements().add(c2);

        
        StringWriter sw = new StringWriter();
        m.marshal(factory.createJTSCompositeCurve(compositeCurve), sw);
        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                       + '\n' +
        "<gml:CompositeCurve srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"                + '\n' +
        "    <gml:curveMember>"                                                                               + '\n' +
        "        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n' +
        "            <gml:segments>"                                                                          + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "            </gml:segments>"                                                                         + '\n' +
        "        </gml:Curve>"                                                                                + '\n' +
        "    </gml:curveMember>"                                                                              + '\n' +
        "    <gml:curveMember>"                                                                               + '\n' +
        "        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n' +
        "            <gml:segments>"                                                                          + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402000.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "            </gml:segments>"                                                                         + '\n' +
        "        </gml:Curve>"                                                                                + '\n' +
        "    </gml:curveMember>"                                                                              + '\n' +
        "</gml:CompositeCurve>"                                                                               + '\n';

        assertEquals(expResult, result);
    }

    /**
     * Test Composite curve Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void CompositeCurveUnmarshalingTest() throws Exception {

        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        DirectPosition p1 = new GeneralDirectPosition(crs);
        p1.setOrdinate(0, 402000);
        p1.setOrdinate(1, 3334850);


        DirectPosition p2 = new GeneralDirectPosition(crs);
        p2.setOrdinate(0, 402200);
        p2.setOrdinate(1, 3335200);


        JTSLineString l1 = new JTSLineString();
        l1.getControlPoints().add(p1);
        l1.getControlPoints().add(p2);

        JTSCurve c2 = new JTSCurve(crs);
        c2.getSegments().add(l1);

        JTSCurve c1 = new JTSCurve(crs);
        JTSLineString l2 = new JTSLineString();
        DirectPosition p21 = new GeneralDirectPosition(crs);
        p21.setOrdinate(0, 401500);
        p21.setOrdinate(1, 3334500);
        DirectPosition p22 = new GeneralDirectPosition(crs);
        p22.setOrdinate(0, 401700);
        p22.setOrdinate(1, 3334850);
        DirectPosition p23 = new GeneralDirectPosition(crs);
        p23.setOrdinate(0, 402200);
        p23.setOrdinate(1, 3335200);

        l2.getControlPoints().add(p21);
        l2.getControlPoints().add(p22);
        l2.getControlPoints().add(p23);

        c1.getSegments().add(l2);

        JTSLineString l3 = new JTSLineString();
        DirectPosition p31 = new GeneralDirectPosition(crs);
        p31.setOrdinate(0, 402320);
        p31.setOrdinate(1, 3334850);
        DirectPosition p32 = new GeneralDirectPosition(crs);
        p32.setOrdinate(0, 402200);
        p32.setOrdinate(1, 3335200);

        l3.getControlPoints().add(p31);
        l3.getControlPoints().add(p32);
        c1.getSegments().add(l3);

        JTSCompositeCurve expResult = new JTSCompositeCurve(null, crs);
       // compositeCurve.getElements().add(l1); TODO
        expResult.getElements().add(c1);
        expResult.getElements().add(c2);

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                       + '\n' +
        "<gml:CompositeCurve srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"                + '\n' +
        "    <gml:curveMember>"                                                                               + '\n' +
        "        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n' +
        "            <gml:segments>"                                                                          + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "            </gml:segments>"                                                                         + '\n' +
        "        </gml:Curve>"                                                                                + '\n' +
        "    </gml:curveMember>"                                                                              + '\n' +
        "    <gml:curveMember>"                                                                               + '\n' +
        "        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n' +
        "            <gml:segments>"                                                                          + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402000.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "            </gml:segments>"                                                                         + '\n' +
        "        </gml:Curve>"                                                                                + '\n' +
        "    </gml:curveMember>"                                                                              + '\n' +
        "</gml:CompositeCurve>"                                                                               + '\n';

        
        JTSCompositeCurve result = (JTSCompositeCurve) ((JAXBElement)un.unmarshal(new StringReader(xml))).getValue();

        assertEquals(expResult, result);
    }

    /**
     * Test PolyHedral surface Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void PolyHedralSurfaceMarshalingTest() throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);
        JTSPolyhedralSurface polyHedralSurface = new JTSPolyhedralSurface(crs);

        /*
         * FIRST POLYGON
         */

        // EXTERIOR
        JTSRing exterior1    = new JTSRing(crs);

        JTSCurve c1 = new JTSCurve(crs);
        JTSLineString c1l1 = new JTSLineString();
        DirectPosition c1l1p1 = new GeneralDirectPosition(crs);
        c1l1p1.setOrdinate(0, 401500);
        c1l1p1.setOrdinate(1, 3334500);
        DirectPosition c1l1p2 = new GeneralDirectPosition(crs);
        c1l1p2.setOrdinate(0, 401700);
        c1l1p2.setOrdinate(1, 3334850);
        DirectPosition c1l1p3 = new GeneralDirectPosition(crs);
        c1l1p3.setOrdinate(0, 402200);
        c1l1p3.setOrdinate(1, 3335200);

        c1l1.getControlPoints().add(c1l1p1);
        c1l1.getControlPoints().add(c1l1p2);
        c1l1.getControlPoints().add(c1l1p3);

        c1.getSegments().add(c1l1);

        JTSLineString c1l2 = new JTSLineString();
        DirectPosition c1l2p1 = new GeneralDirectPosition(crs);
        c1l2p1.setOrdinate(0, 402320);
        c1l2p1.setOrdinate(1, 3334850);
        DirectPosition c1l2p2 = new GeneralDirectPosition(crs);
        c1l2p2.setOrdinate(0, 402200);
        c1l2p2.setOrdinate(1, 3335200);

        c1l2.getControlPoints().add(c1l2p1);
        c1l2.getControlPoints().add(c1l2p2);
        c1.getSegments().add(c1l2);
        exterior1.getElements().add(c1);

        // INTERIOR
        Ring[] interiors1 = new Ring[1];

        JTSRing interior1 = new JTSRing(crs);

        JTSCurve c2 = new JTSCurve(crs);
        JTSLineString c2l1 = new JTSLineString();
        DirectPosition c2l1p1 = new GeneralDirectPosition(crs);
        c2l1p1.setOrdinate(0, 401500);
        c2l1p1.setOrdinate(1, 3334500);
        DirectPosition c2l1p2 = new GeneralDirectPosition(crs);
        c2l1p2.setOrdinate(0, 401700);
        c2l1p2.setOrdinate(1, 3334850);
        DirectPosition c2l1p3 = new GeneralDirectPosition(crs);
        c2l1p3.setOrdinate(0, 402200);
        c2l1p3.setOrdinate(1, 3335200);

        c2l1.getControlPoints().add(c2l1p1);
        c2l1.getControlPoints().add(c2l1p2);
        c2l1.getControlPoints().add(c2l1p3);

        c2.getSegments().add(c2l1);

        interior1.getElements().add(c2);

        interiors1[0] = interior1;

        JTSSurfaceBoundary bound1 = new JTSSurfaceBoundary(crs, exterior1, interiors1);
        JTSPolygon p1 = new JTSPolygon(bound1);

        /*
         * SECOND POLYGON
         */
       

        // EXTERIOR
        JTSRing exterior2    = new JTSRing(crs);

        JTSCurve c3 = new JTSCurve(crs);
        JTSLineString c3l1 = new JTSLineString();
        DirectPosition c3l1p1 = new GeneralDirectPosition(crs);
        c3l1p1.setOrdinate(0, 401500);
        c3l1p1.setOrdinate(1, 3334500);
        DirectPosition c3l1p2 = new GeneralDirectPosition(crs);
        c3l1p2.setOrdinate(0, 401700);
        c3l1p2.setOrdinate(1, 3334850);
        DirectPosition c3l1p3 = new GeneralDirectPosition(crs);
        c3l1p3.setOrdinate(0, 402200);
        c3l1p3.setOrdinate(1, 3335200);

        c3l1.getControlPoints().add(c3l1p1);
        c3l1.getControlPoints().add(c3l1p2);
        c3l1.getControlPoints().add(c3l1p3);

        c3.getSegments().add(c3l1);

        exterior2.getElements().add(c3);


        // INTERIOR
        JTSRing interior2    = new JTSRing(crs);

        JTSCurve c4 = new JTSCurve(crs);
        JTSLineString c4l1 = new JTSLineString();
        DirectPosition c4l1p1 = new GeneralDirectPosition(crs);
        c4l1p1.setOrdinate(0, 401500);
        c4l1p1.setOrdinate(1, 3334500);
        DirectPosition c4l1p2 = new GeneralDirectPosition(crs);
        c4l1p2.setOrdinate(0, 401700);
        c4l1p2.setOrdinate(1, 3334850);
        DirectPosition c4l1p3 = new GeneralDirectPosition(crs);
        c4l1p3.setOrdinate(0, 402200);
        c4l1p3.setOrdinate(1, 3335200);

        c4l1.getControlPoints().add(c4l1p1);
        c4l1.getControlPoints().add(c4l1p2);
        c4l1.getControlPoints().add(c4l1p3);

        c4.getSegments().add(c4l1);

        interior2.getElements().add(c4);
        
        Ring[] interiors2 = new Ring[1];
        interiors2[0]     = interior2;

        JTSSurfaceBoundary bound2 = new JTSSurfaceBoundary(crs, exterior2, interiors2);
        JTSPolygon p2 = new JTSPolygon(bound2);

        polyHedralSurface.getPatches().add(p1);
        polyHedralSurface.getPatches().add(p2);

        
        StringWriter sw = new StringWriter();
        m.marshal(factory.createJTSPolyhedralSurface(polyHedralSurface), sw);
        String result = sw.toString();


        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                                       + '\n'  +
        "<gml:PolyhedralSurface srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"                             + '\n'  +
        "    <gml:polygonPatches>"                                                                                            + '\n'  +
        "        <gml:PolygonPatch>"                                                                                          + '\n'  +
        "            <gml:exterior>"                                                                                          + '\n'  +
        "                <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "                    <gml:curveMember>"                                                                               + '\n'  +
        "                        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                            <gml:segments>"                                                                          + '\n'  +
        "                                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                                </gml:LineStringSegment>"                                                            + '\n'  +
        "                                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                                </gml:LineStringSegment>"                                                            + '\n'  +
        "                            </gml:segments>"                                                                         + '\n'  +
        "                        </gml:Curve>"                                                                                + '\n'  +
        "                    </gml:curveMember>"                                                                              + '\n'  +
        "                </gml:Ring>"                                                                                         + '\n'  +
        "            </gml:exterior>"                                                                                         + '\n'  +
        "            <gml:interior>"                                                                                          + '\n'  +
        "                <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "                    <gml:curveMember>"                                                                               + '\n'  +
        "                        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                            <gml:segments>"                                                                          + '\n'  +
        "                                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                                </gml:LineStringSegment>"                                                            + '\n'  +
        "                            </gml:segments>"                                                                         + '\n'  +
        "                        </gml:Curve>"                                                                                + '\n'  +
        "                    </gml:curveMember>"                                                                              + '\n'  +
        "                </gml:Ring>"                                                                                         + '\n'  +
        "            </gml:interior>"                                                                                         + '\n'  +
        "        </gml:PolygonPatch>"                                                                                         + '\n'  +
        "        <gml:PolygonPatch>"                                                                                          + '\n'  +
        "            <gml:exterior>"                                                                                          + '\n'  +
        "                <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "                    <gml:curveMember>"                                                                               + '\n'  +
        "                        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                            <gml:segments>"                                                                          + '\n'  +
        "                                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                                </gml:LineStringSegment>"                                                            + '\n'  +
        "                            </gml:segments>"                                                                         + '\n'  +
        "                        </gml:Curve>"                                                                                + '\n'  +
        "                    </gml:curveMember>"                                                                              + '\n'  +
        "                </gml:Ring>"                                                                                         + '\n'  +
        "            </gml:exterior>"                                                                                         + '\n'  +
        "            <gml:interior>"                                                                                          + '\n'  +
        "                <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "                    <gml:curveMember>"                                                                               + '\n'  +
        "                        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                            <gml:segments>"                                                                          + '\n'  +
        "                                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                                </gml:LineStringSegment>"                                                            + '\n'  +
        "                            </gml:segments>"                                                                         + '\n'  +
        "                        </gml:Curve>"                                                                                + '\n'  +
        "                    </gml:curveMember>"                                                                              + '\n'  +
        "                </gml:Ring>"                                                                                         + '\n'  +
        "            </gml:interior>"                                                                                         + '\n'  +
        "        </gml:PolygonPatch>"                                                                                         + '\n'  +
        "    </gml:polygonPatches>"                                                                                           + '\n'  +
        "</gml:PolyhedralSurface>"                                                                                            + '\n';

        assertEquals(expResult, result);
    }

    /**
     * Test PolyHedral surface Unmarshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void PolyHedralSurfaceUnmarshalingTest() throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);
        
        JTSPolyhedralSurface expResult = new JTSPolyhedralSurface(crs);

        /*
         * FIRST POLYGON
         */

        // EXTERIOR
        JTSRing exterior1    = new JTSRing(crs);

        JTSCurve c1 = new JTSCurve(crs);
        JTSLineString c1l1 = new JTSLineString();
        DirectPosition c1l1p1 = new GeneralDirectPosition(crs);
        c1l1p1.setOrdinate(0, 401500);
        c1l1p1.setOrdinate(1, 3334500);
        DirectPosition c1l1p2 = new GeneralDirectPosition(crs);
        c1l1p2.setOrdinate(0, 401700);
        c1l1p2.setOrdinate(1, 3334850);
        DirectPosition c1l1p3 = new GeneralDirectPosition(crs);
        c1l1p3.setOrdinate(0, 402200);
        c1l1p3.setOrdinate(1, 3335200);

        c1l1.getControlPoints().add(c1l1p1);
        c1l1.getControlPoints().add(c1l1p2);
        c1l1.getControlPoints().add(c1l1p3);

        c1.getSegments().add(c1l1);

        JTSLineString c1l2 = new JTSLineString();
        DirectPosition c1l2p1 = new GeneralDirectPosition(crs);
        c1l2p1.setOrdinate(0, 402320);
        c1l2p1.setOrdinate(1, 3334850);
        DirectPosition c1l2p2 = new GeneralDirectPosition(crs);
        c1l2p2.setOrdinate(0, 402200);
        c1l2p2.setOrdinate(1, 3335200);

        c1l2.getControlPoints().add(c1l2p1);
        c1l2.getControlPoints().add(c1l2p2);
        c1.getSegments().add(c1l2);
        exterior1.getElements().add(c1);

        // INTERIOR
        Ring[] interiors1 = new Ring[1];

        JTSRing interior1 = new JTSRing(crs);

        JTSCurve c2 = new JTSCurve(crs);
        JTSLineString c2l1 = new JTSLineString();
        DirectPosition c2l1p1 = new GeneralDirectPosition(crs);
        c2l1p1.setOrdinate(0, 401500);
        c2l1p1.setOrdinate(1, 3334500);
        DirectPosition c2l1p2 = new GeneralDirectPosition(crs);
        c2l1p2.setOrdinate(0, 401700);
        c2l1p2.setOrdinate(1, 3334850);
        DirectPosition c2l1p3 = new GeneralDirectPosition(crs);
        c2l1p3.setOrdinate(0, 402200);
        c2l1p3.setOrdinate(1, 3335200);

        c2l1.getControlPoints().add(c2l1p1);
        c2l1.getControlPoints().add(c2l1p2);
        c2l1.getControlPoints().add(c2l1p3);

        c2.getSegments().add(c2l1);

        interior1.getElements().add(c2);

        interiors1[0] = interior1;

        JTSSurfaceBoundary bound1 = new JTSSurfaceBoundary(crs, exterior1, interiors1);
        JTSPolygon p1 = new JTSPolygon(bound1);

        /*
         * SECOND POLYGON
         */


        // EXTERIOR
        JTSRing exterior2    = new JTSRing(crs);

        JTSCurve c3 = new JTSCurve(crs);
        JTSLineString c3l1 = new JTSLineString();
        DirectPosition c3l1p1 = new GeneralDirectPosition(crs);
        c3l1p1.setOrdinate(0, 401500);
        c3l1p1.setOrdinate(1, 3334500);
        DirectPosition c3l1p2 = new GeneralDirectPosition(crs);
        c3l1p2.setOrdinate(0, 401700);
        c3l1p2.setOrdinate(1, 3334850);
        DirectPosition c3l1p3 = new GeneralDirectPosition(crs);
        c3l1p3.setOrdinate(0, 402200);
        c3l1p3.setOrdinate(1, 3335200);

        c3l1.getControlPoints().add(c3l1p1);
        c3l1.getControlPoints().add(c3l1p2);
        c3l1.getControlPoints().add(c3l1p3);

        c3.getSegments().add(c3l1);

        exterior2.getElements().add(c3);


        // INTERIOR
        JTSRing interior2    = new JTSRing(crs);

        JTSCurve c4 = new JTSCurve(crs);
        JTSLineString c4l1 = new JTSLineString();
        DirectPosition c4l1p1 = new GeneralDirectPosition(crs);
        c4l1p1.setOrdinate(0, 401500);
        c4l1p1.setOrdinate(1, 3334500);
        DirectPosition c4l1p2 = new GeneralDirectPosition(crs);
        c4l1p2.setOrdinate(0, 401700);
        c4l1p2.setOrdinate(1, 3334850);
        DirectPosition c4l1p3 = new GeneralDirectPosition(crs);
        c4l1p3.setOrdinate(0, 402200);
        c4l1p3.setOrdinate(1, 3335200);

        c4l1.getControlPoints().add(c4l1p1);
        c4l1.getControlPoints().add(c4l1p2);
        c4l1.getControlPoints().add(c4l1p3);

        c4.getSegments().add(c4l1);

        interior2.getElements().add(c4);
        Ring[] interiors2 = new Ring[1];
        interiors2[0]     = interior2;

        JTSSurfaceBoundary bound2 = new JTSSurfaceBoundary(crs, exterior2, interiors2);
        JTSPolygon p2 = new JTSPolygon(bound2);

        expResult.getPatches().add(p1);
        expResult.getPatches().add(p2);


        // TODO remove the srsName

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                                       + '\n'  +
        "<gml:PolyhedralSurface srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"                             + '\n'  +
        "    <gml:polygonPatches>"                                                                                            + '\n'  +
        "        <gml:PolygonPatch>"                                                                                          + '\n'  +
        "            <gml:exterior>"                                                                                          + '\n'  +
        "                <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "                    <gml:curveMember>"                                                                               + '\n'  +
        "                        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                            <gml:segments>"                                                                          + '\n'  +
        "                                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                                </gml:LineStringSegment>"                                                            + '\n'  +
        "                                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                                </gml:LineStringSegment>"                                                            + '\n'  +
        "                            </gml:segments>"                                                                         + '\n'  +
        "                        </gml:Curve>"                                                                                + '\n'  +
        "                    </gml:curveMember>"                                                                              + '\n'  +
        "                </gml:Ring>"                                                                                         + '\n'  +
        "            </gml:exterior>"                                                                                         + '\n'  +
        "            <gml:interior>"                                                                                          + '\n'  +
        "                <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "                    <gml:curveMember>"                                                                               + '\n'  +
        "                        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                            <gml:segments>"                                                                          + '\n'  +
        "                                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                                </gml:LineStringSegment>"                                                            + '\n'  +
        "                            </gml:segments>"                                                                         + '\n'  +
        "                        </gml:Curve>"                                                                                + '\n'  +
        "                    </gml:curveMember>"                                                                              + '\n'  +
        "                </gml:Ring>"                                                                                         + '\n'  +
        "            </gml:interior>"                                                                                         + '\n'  +
        "        </gml:PolygonPatch>"                                                                                         + '\n'  +
        "        <gml:PolygonPatch>"                                                                                          + '\n'  +
        "            <gml:exterior>"                                                                                          + '\n'  +
        "                <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "                    <gml:curveMember>"                                                                               + '\n'  +
        "                        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                            <gml:segments>"                                                                          + '\n'  +
        "                                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                                </gml:LineStringSegment>"                                                            + '\n'  +
        "                            </gml:segments>"                                                                         + '\n'  +
        "                        </gml:Curve>"                                                                                + '\n'  +
        "                    </gml:curveMember>"                                                                              + '\n'  +
        "                </gml:Ring>"                                                                                         + '\n'  +
        "            </gml:exterior>"                                                                                         + '\n'  +
        "            <gml:interior>"                                                                                          + '\n'  +
        "                <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "                    <gml:curveMember>"                                                                               + '\n'  +
        "                        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                            <gml:segments>"                                                                          + '\n'  +
        "                                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                                </gml:LineStringSegment>"                                                            + '\n'  +
        "                            </gml:segments>"                                                                         + '\n'  +
        "                        </gml:Curve>"                                                                                + '\n'  +
        "                    </gml:curveMember>"                                                                              + '\n'  +
        "                </gml:Ring>"                                                                                         + '\n'  +
        "            </gml:interior>"                                                                                         + '\n'  +
        "        </gml:PolygonPatch>"                                                                                         + '\n'  +
        "    </gml:polygonPatches>"                                                                                           + '\n'  +
        "</gml:PolyhedralSurface>"                                                                                            + '\n';

        

        PolyhedralSurfaceType tmp = (PolyhedralSurfaceType) ((JAXBElement)un.unmarshal(new StringReader(xml))).getValue();

        JTSPolyhedralSurface result = tmp.getIsoPolyHedralSurface();

        assertEquals(expResult, result);
    }

    /**
     * Test Ring Unmarshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void RingMarshalingTest() throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        JTSRing ring    = new JTSRing(crs);

        JTSCurve c1 = new JTSCurve(crs);
        JTSLineString c1l1 = new JTSLineString();
        DirectPosition c1l1p1 = new GeneralDirectPosition(crs);
        c1l1p1.setOrdinate(0, 401500);
        c1l1p1.setOrdinate(1, 3334500);
        DirectPosition c1l1p2 = new GeneralDirectPosition(crs);
        c1l1p2.setOrdinate(0, 401700);
        c1l1p2.setOrdinate(1, 3334850);
        DirectPosition c1l1p3 = new GeneralDirectPosition(crs);
        c1l1p3.setOrdinate(0, 402200);
        c1l1p3.setOrdinate(1, 3335200);

        c1l1.getControlPoints().add(c1l1p1);
        c1l1.getControlPoints().add(c1l1p2);
        c1l1.getControlPoints().add(c1l1p3);

        c1.getSegments().add(c1l1);

        JTSLineString c1l2 = new JTSLineString();
        DirectPosition c1l2p1 = new GeneralDirectPosition(crs);
        c1l2p1.setOrdinate(0, 402320);
        c1l2p1.setOrdinate(1, 3334850);
        DirectPosition c1l2p2 = new GeneralDirectPosition(crs);
        c1l2p2.setOrdinate(0, 402200);
        c1l2p2.setOrdinate(1, 3335200);

        c1l2.getControlPoints().add(c1l2p1);
        c1l2.getControlPoints().add(c1l2p2);
        c1.getSegments().add(c1l2);
        ring.getElements().add(c1);

        
        StringWriter sw = new StringWriter();
        m.marshal(factory.createJTSRing(ring), sw);
        String result = sw.toString();

        String expResult = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                       + '\n'  +
        "<gml:Ring srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"                          + '\n'  +
        "    <gml:curveMember>"                                                                               + '\n'  +
        "        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "            <gml:segments>"                                                                          + '\n'  +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                </gml:LineStringSegment>"                                                            + '\n'  +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>" + '\n'  +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                </gml:LineStringSegment>"                                                            + '\n'  +
        "            </gml:segments>"                                                                         + '\n'  +
        "        </gml:Curve>"                                                                                + '\n'  +
        "    </gml:curveMember>"                                                                              + '\n'  +
        "</gml:Ring>"                                                                                         + '\n';

        assertEquals(expResult, result);
    }

    /**
     * Test Ring Unmarshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void RingUnmarshalingTest() throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        JTSRing expResult    = new JTSRing(crs);

        JTSCurve c1 = new JTSCurve(crs);
        JTSLineString c1l1 = new JTSLineString();
        DirectPosition c1l1p1 = new GeneralDirectPosition(crs);
        c1l1p1.setOrdinate(0, 401500);
        c1l1p1.setOrdinate(1, 3334500);
        DirectPosition c1l1p2 = new GeneralDirectPosition(crs);
        c1l1p2.setOrdinate(0, 401700);
        c1l1p2.setOrdinate(1, 3334850);
        DirectPosition c1l1p3 = new GeneralDirectPosition(crs);
        c1l1p3.setOrdinate(0, 402200);
        c1l1p3.setOrdinate(1, 3335200);

        c1l1.getControlPoints().add(c1l1p1);
        c1l1.getControlPoints().add(c1l1p2);
        c1l1.getControlPoints().add(c1l1p3);

        c1.getSegments().add(c1l1);

        JTSLineString c1l2 = new JTSLineString();
        DirectPosition c1l2p1 = new GeneralDirectPosition(crs);
        c1l2p1.setOrdinate(0, 402320);
        c1l2p1.setOrdinate(1, 3334850);
        DirectPosition c1l2p2 = new GeneralDirectPosition(crs);
        c1l2p2.setOrdinate(0, 402200);
        c1l2p2.setOrdinate(1, 3335200);

        c1l2.getControlPoints().add(c1l2p1);
        c1l2.getControlPoints().add(c1l2p2);
        c1.getSegments().add(c1l2);
        expResult.getElements().add(c1);


        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                       + '\n'  +
        "<gml:Ring srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"                          + '\n'  +
        "    <gml:curveMember>"                                                                               + '\n'  +
        "        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "            <gml:segments>"                                                                          + '\n'  +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                </gml:LineStringSegment>"                                                            + '\n'  +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>" + '\n'  +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                </gml:LineStringSegment>"                                                            + '\n'  +
        "            </gml:segments>"                                                                         + '\n'  +
        "        </gml:Curve>"                                                                                + '\n'  +
        "    </gml:curveMember>"                                                                              + '\n'  +
        "</gml:Ring>"                                                                                         + '\n';

        
        JTSRing result  = (JTSRing) ((JAXBElement)un.unmarshal(new StringReader(xml))).getValue();

        assertEquals(expResult, result);
    }

    /**
     * Test Ring Unmarshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void PolygonMarshalingTest() throws Exception {

        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        // EXTERIOR
        JTSRing exterior1    = new JTSRing(crs);

        JTSCurve c1 = new JTSCurve(crs);
        JTSLineString c1l1 = new JTSLineString();
        DirectPosition c1l1p1 = new GeneralDirectPosition(crs);
        c1l1p1.setOrdinate(0, 401500);
        c1l1p1.setOrdinate(1, 3334500);
        DirectPosition c1l1p2 = new GeneralDirectPosition(crs);
        c1l1p2.setOrdinate(0, 401700);
        c1l1p2.setOrdinate(1, 3334850);
        DirectPosition c1l1p3 = new GeneralDirectPosition(crs);
        c1l1p3.setOrdinate(0, 402200);
        c1l1p3.setOrdinate(1, 3335200);

        c1l1.getControlPoints().add(c1l1p1);
        c1l1.getControlPoints().add(c1l1p2);
        c1l1.getControlPoints().add(c1l1p3);

        c1.getSegments().add(c1l1);

        JTSLineString c1l2 = new JTSLineString();
        DirectPosition c1l2p1 = new GeneralDirectPosition(crs);
        c1l2p1.setOrdinate(0, 402320);
        c1l2p1.setOrdinate(1, 3334850);
        DirectPosition c1l2p2 = new GeneralDirectPosition(crs);
        c1l2p2.setOrdinate(0, 402200);
        c1l2p2.setOrdinate(1, 3335200);

        c1l2.getControlPoints().add(c1l2p1);
        c1l2.getControlPoints().add(c1l2p2);
        c1.getSegments().add(c1l2);
        exterior1.getElements().add(c1);

        // INTERIOR
        Ring[] interiors1 = new Ring[1];

        JTSRing interior1 = new JTSRing(crs);

        JTSCurve c2 = new JTSCurve(crs);
        JTSLineString c2l1 = new JTSLineString();
        DirectPosition c2l1p1 = new GeneralDirectPosition(crs);
        c2l1p1.setOrdinate(0, 401500);
        c2l1p1.setOrdinate(1, 3334500);
        DirectPosition c2l1p2 = new GeneralDirectPosition(crs);
        c2l1p2.setOrdinate(0, 401700);
        c2l1p2.setOrdinate(1, 3334850);
        DirectPosition c2l1p3 = new GeneralDirectPosition(crs);
        c2l1p3.setOrdinate(0, 402200);
        c2l1p3.setOrdinate(1, 3335200);

        c2l1.getControlPoints().add(c2l1p1);
        c2l1.getControlPoints().add(c2l1p2);
        c2l1.getControlPoints().add(c2l1p3);

        c2.getSegments().add(c2l1);

        interior1.getElements().add(c2);

        interiors1[0] = interior1;

        JTSSurfaceBoundary bound1 = new JTSSurfaceBoundary(crs, exterior1, interiors1);
        JTSPolygon polygon = new JTSPolygon(bound1);

        
        StringWriter sw = new StringWriter();
        m.marshal(factory.createJTSPolygon(polygon), sw);
        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                               + '\n'  +
        "<gml:Polygon srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"                               + '\n'  +
        "    <gml:exterior>"                                                                                          + '\n'  +
        "        <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "            <gml:curveMember>"                                                                               + '\n'  +
        "                <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                    <gml:segments>"                                                                          + '\n'  +
        "                        <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                        </gml:LineStringSegment>"                                                            + '\n'  +
        "                        <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>" + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                        </gml:LineStringSegment>"                                                            + '\n'  +
        "                    </gml:segments>"                                                                         + '\n'  +
        "                </gml:Curve>"                                                                                + '\n'  +
        "            </gml:curveMember>"                                                                              + '\n'  +
        "        </gml:Ring>"                                                                                         + '\n'  +
        "    </gml:exterior>"                                                                                         + '\n'  +
        "    <gml:interior>"                                                                                          + '\n'  +
        "        <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "            <gml:curveMember>"                                                                               + '\n'  +
        "                <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                    <gml:segments>"                                                                          + '\n'  +
        "                        <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                        </gml:LineStringSegment>"                                                            + '\n'  +
        "                    </gml:segments>"                                                                         + '\n'  +
        "                </gml:Curve>"                                                                                + '\n'  +
        "            </gml:curveMember>"                                                                              + '\n'  +
        "        </gml:Ring>"                                                                                         + '\n'  +
        "    </gml:interior>"                                                                                         + '\n'  +
        "</gml:Polygon>"                                                                                              + '\n';

        assertEquals(expResult, result);
    }

    /**
     * Test Ring Unmarshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void PolygonUnmarshalingTest() throws Exception {

        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        // EXTERIOR
        JTSRing exterior1    = new JTSRing(crs);

        JTSCurve c1 = new JTSCurve(crs);
        JTSLineString c1l1 = new JTSLineString();
        DirectPosition c1l1p1 = new GeneralDirectPosition(crs);
        c1l1p1.setOrdinate(0, 401500);
        c1l1p1.setOrdinate(1, 3334500);
        DirectPosition c1l1p2 = new GeneralDirectPosition(crs);
        c1l1p2.setOrdinate(0, 401700);
        c1l1p2.setOrdinate(1, 3334850);
        DirectPosition c1l1p3 = new GeneralDirectPosition(crs);
        c1l1p3.setOrdinate(0, 402200);
        c1l1p3.setOrdinate(1, 3335200);

        c1l1.getControlPoints().add(c1l1p1);
        c1l1.getControlPoints().add(c1l1p2);
        c1l1.getControlPoints().add(c1l1p3);

        c1.getSegments().add(c1l1);

        JTSLineString c1l2 = new JTSLineString();
        DirectPosition c1l2p1 = new GeneralDirectPosition(crs);
        c1l2p1.setOrdinate(0, 402320);
        c1l2p1.setOrdinate(1, 3334850);
        DirectPosition c1l2p2 = new GeneralDirectPosition(crs);
        c1l2p2.setOrdinate(0, 402200);
        c1l2p2.setOrdinate(1, 3335200);

        c1l2.getControlPoints().add(c1l2p1);
        c1l2.getControlPoints().add(c1l2p2);
        c1.getSegments().add(c1l2);
        exterior1.getElements().add(c1);

        // INTERIOR
        Ring[] interiors1 = new Ring[1];

        JTSRing interior1 = new JTSRing(crs);

        JTSCurve c2 = new JTSCurve(crs);
        JTSLineString c2l1 = new JTSLineString();
        DirectPosition c2l1p1 = new GeneralDirectPosition(crs);
        c2l1p1.setOrdinate(0, 401500);
        c2l1p1.setOrdinate(1, 3334500);
        DirectPosition c2l1p2 = new GeneralDirectPosition(crs);
        c2l1p2.setOrdinate(0, 401700);
        c2l1p2.setOrdinate(1, 3334850);
        DirectPosition c2l1p3 = new GeneralDirectPosition(crs);
        c2l1p3.setOrdinate(0, 402200);
        c2l1p3.setOrdinate(1, 3335200);

        c2l1.getControlPoints().add(c2l1p1);
        c2l1.getControlPoints().add(c2l1p2);
        c2l1.getControlPoints().add(c2l1p3);

        c2.getSegments().add(c2l1);

        interior1.getElements().add(c2);

        interiors1[0] = interior1;

        JTSSurfaceBoundary bound1 = new JTSSurfaceBoundary(crs, exterior1, interiors1);
        JTSPolygon expResult = new JTSPolygon(bound1);

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                               + '\n'  +
        "<gml:Polygon srsName=\"EPSG:27572\" xmlns:gml=\"http://www.opengis.net/gml\">"                               + '\n'  +
        "    <gml:exterior>"                                                                                          + '\n'  +
        "        <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "            <gml:curveMember>"                                                                               + '\n'  +
        "                <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                    <gml:segments>"                                                                          + '\n'  +
        "                        <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                        </gml:LineStringSegment>"                                                            + '\n'  +
        "                        <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>" + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                        </gml:LineStringSegment>"                                                            + '\n'  +
        "                    </gml:segments>"                                                                         + '\n'  +
        "                </gml:Curve>"                                                                                + '\n'  +
        "            </gml:curveMember>"                                                                              + '\n'  +
        "        </gml:Ring>"                                                                                         + '\n'  +
        "    </gml:exterior>"                                                                                         + '\n'  +
        "    <gml:interior>"                                                                                          + '\n'  +
        "        <gml:Ring srsName=\"EPSG:27572\">"                                                                   + '\n'  +
        "            <gml:curveMember>"                                                                               + '\n'  +
        "                <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n'  +
        "                    <gml:segments>"                                                                          + '\n'  +
        "                        <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n'  +
        "                            <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n'  +
        "                        </gml:LineStringSegment>"                                                            + '\n'  +
        "                    </gml:segments>"                                                                         + '\n'  +
        "                </gml:Curve>"                                                                                + '\n'  +
        "            </gml:curveMember>"                                                                              + '\n'  +
        "        </gml:Ring>"                                                                                         + '\n'  +
        "    </gml:interior>"                                                                                         + '\n'  +
        "</gml:Polygon>"                                                                                              + '\n';

        
        PolygonType temp = (PolygonType) ((JAXBElement)un.unmarshal(new StringReader(xml))).getValue();

        JTSPolygon result  = temp.getJTSPolygon();
        assertEquals(expResult, result);
    }

    /**
     * Test Composite curve Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void MultiPrimitiveMarshalingTest() throws Exception {

        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        DirectPosition p1 = new GeneralDirectPosition(crs);
        p1.setOrdinate(0, 402000);
        p1.setOrdinate(1, 3334850);


        DirectPosition p2 = new GeneralDirectPosition(crs);
        p2.setOrdinate(0, 402200);
        p2.setOrdinate(1, 3335200);


        JTSLineString l1 = new JTSLineString();
        l1.getControlPoints().add(p1);
        l1.getControlPoints().add(p2);

        JTSCurve c2 = new JTSCurve(crs);
        c2.getSegments().add(l1);

        JTSCurve c1 = new JTSCurve(crs);
        JTSLineString l2 = new JTSLineString();
        DirectPosition p21 = new GeneralDirectPosition(crs);
        p21.setOrdinate(0, 401500);
        p21.setOrdinate(1, 3334500);
        DirectPosition p22 = new GeneralDirectPosition(crs);
        p22.setOrdinate(0, 401700);
        p22.setOrdinate(1, 3334850);
        DirectPosition p23 = new GeneralDirectPosition(crs);
        p23.setOrdinate(0, 402200);
        p23.setOrdinate(1, 3335200);

        l2.getControlPoints().add(p21);
        l2.getControlPoints().add(p22);
        l2.getControlPoints().add(p23);

        c1.getSegments().add(l2);

        JTSLineString l3 = new JTSLineString();
        DirectPosition p31 = new GeneralDirectPosition(crs);
        p31.setOrdinate(0, 402320);
        p31.setOrdinate(1, 3334850);
        DirectPosition p32 = new GeneralDirectPosition(crs);
        p32.setOrdinate(0, 402200);
        p32.setOrdinate(1, 3335200);

        l3.getControlPoints().add(p31);
        l3.getControlPoints().add(p32);
        c1.getSegments().add(l3);

        JTSMultiPrimitive multip = new JTSMultiPrimitive();
        multip.getElements().add(c1);
        multip.getElements().add(c2);

        
        StringWriter sw = new StringWriter();
        m.marshal(factory.createJTSMultiGeometry(multip), sw);
        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                       + '\n' +
        "<gml:MultiGeometry xmlns:gml=\"http://www.opengis.net/gml\">"                                        + '\n' +
        "    <gml:geometryMember>"                                                                               + '\n' +
        "        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n' +
        "            <gml:segments>"                                                                          + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "            </gml:segments>"                                                                         + '\n' +
        "        </gml:Curve>"                                                                                + '\n' +
        "    </gml:geometryMember>"                                                                              + '\n' +
        "    <gml:geometryMember>"                                                                               + '\n' +
        "        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n' +
        "            <gml:segments>"                                                                          + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402000.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "            </gml:segments>"                                                                         + '\n' +
        "        </gml:Curve>"                                                                                + '\n' +
        "    </gml:geometryMember>"                                                                              + '\n' +
        "</gml:MultiGeometry>"                                                                               + '\n';

        assertEquals(expResult, result);


        crs = CRS.decode("epsg:27593");
        assertTrue(crs != null);

        /*
         * FIRST POLYGON
         */

        // EXTERIOR
        JTSRing exterior1    = new JTSRing(crs);

        JTSCurve cu1 = new JTSCurve(crs);
        JTSLineString c1l1 = new JTSLineString();

        DirectPosition dp1 = new GeneralDirectPosition(crs);
        dp1.setOrdinate(0, 656216.1977884835);
        dp1.setOrdinate(1, 38574.31079256255);

        DirectPosition dp2 = new GeneralDirectPosition(crs);
        dp2.setOrdinate(0, 656209.434300029);
        dp2.setOrdinate(1, 38569.570186997764);

        c1l1.getControlPoints().add(dp1);
        c1l1.getControlPoints().add(dp2);


        cu1.getSegments().add(c1l1);
        exterior1.getElements().add(cu1);

        // INTERIOR
        Ring[] interiors1 = new Ring[0];


        JTSSurfaceBoundary bound1 = new JTSSurfaceBoundary(crs, exterior1, interiors1);
        JTSPolygon poly1 = new JTSPolygon(bound1);

        JTSPolyhedralSurface PS = new JTSPolyhedralSurface(crs);
        PS.getPatches().add(poly1);

        multip = new JTSMultiPrimitive();
        multip.getElements().add(PS);

        sw = new StringWriter();
        m.marshal(factory.createJTSMultiGeometry(multip), sw);
        result = sw.toString();

        expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                       + '\n' +
        "<gml:MultiGeometry xmlns:gml=\"http://www.opengis.net/gml\">"                                        + '\n' +
        "    <gml:geometryMember>" + '\n' +
        "        <gml:PolyhedralSurface srsName=\"EPSG:27593\">" + '\n' +
        "            <gml:polygonPatches>" + '\n' +
        "                <gml:PolygonPatch>" + '\n' +
        "                    <gml:exterior>" + '\n' +
        "                        <gml:Ring srsName=\"EPSG:27593\">" + '\n' +
        "                            <gml:curveMember>" + '\n' +
        "                                <gml:Curve srsName=\"EPSG:27593\">" + '\n' +
        "                                    <gml:segments>" + '\n' +
        "                                        <gml:LineStringSegment interpolation=\"linear\">"                                                      + '\n' +
        "                                            <gml:pos srsName=\"EPSG:27593\" srsDimension=\"2\">656216.1977884835 38574.31079256255</gml:pos>"  + '\n' +
        "                                            <gml:pos srsName=\"EPSG:27593\" srsDimension=\"2\">656209.434300029 38569.570186997764</gml:pos>"  + '\n' +
        "                                        </gml:LineStringSegment>"                                                                              + '\n' +
        "                                    </gml:segments>" + '\n' +
        "                                </gml:Curve>" + '\n' +
        "                            </gml:curveMember>" + '\n' +
        "                        </gml:Ring>" + '\n' +
        "                    </gml:exterior>" + '\n' +
        "                </gml:PolygonPatch>" + '\n' +
        "            </gml:polygonPatches>" + '\n' +
        "        </gml:PolyhedralSurface>" + '\n' +
        "    </gml:geometryMember>" + '\n' +
        "</gml:MultiGeometry>"                                                                               + '\n';

        assertEquals(expResult, result);
    }

     /**
     * Test Composite curve Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void MultiPrimitiveUnmarshalingTest() throws Exception {

        CoordinateReferenceSystem crs = CRS.decode("epsg:27572");
        assertTrue(crs != null);

        DirectPosition p1 = new GeneralDirectPosition(crs);
        p1.setOrdinate(0, 402000);
        p1.setOrdinate(1, 3334850);


        DirectPosition p2 = new GeneralDirectPosition(crs);
        p2.setOrdinate(0, 402200);
        p2.setOrdinate(1, 3335200);


        JTSLineString l1 = new JTSLineString();
        l1.getControlPoints().add(p1);
        l1.getControlPoints().add(p2);

        JTSCurve c2 = new JTSCurve(crs);
        c2.getSegments().add(l1);

        JTSCurve c1 = new JTSCurve(crs);
        JTSLineString l2 = new JTSLineString();
        DirectPosition p21 = new GeneralDirectPosition(crs);
        p21.setOrdinate(0, 401500);
        p21.setOrdinate(1, 3334500);
        DirectPosition p22 = new GeneralDirectPosition(crs);
        p22.setOrdinate(0, 401700);
        p22.setOrdinate(1, 3334850);
        DirectPosition p23 = new GeneralDirectPosition(crs);
        p23.setOrdinate(0, 402200);
        p23.setOrdinate(1, 3335200);

        l2.getControlPoints().add(p21);
        l2.getControlPoints().add(p22);
        l2.getControlPoints().add(p23);

        c1.getSegments().add(l2);

        JTSLineString l3 = new JTSLineString();
        DirectPosition p31 = new GeneralDirectPosition(crs);
        p31.setOrdinate(0, 402320);
        p31.setOrdinate(1, 3334850);
        DirectPosition p32 = new GeneralDirectPosition(crs);
        p32.setOrdinate(0, 402200);
        p32.setOrdinate(1, 3335200);

        l3.getControlPoints().add(p31);
        l3.getControlPoints().add(p32);
        c1.getSegments().add(l3);

        JTSMultiPrimitive expResult = new JTSMultiPrimitive();
        expResult.getElements().add(c1);
        expResult.getElements().add(c2);

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                       + '\n' +
        "<gml:MultiGeometry xmlns:gml=\"http://www.opengis.net/gml\">"                                        + '\n' +
        "    <gml:geometryMember>"                                                                               + '\n' +
        "        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n' +
        "            <gml:segments>"                                                                          + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401500.0 3334500.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">401700.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402320.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "            </gml:segments>"                                                                         + '\n' +
        "        </gml:Curve>"                                                                                + '\n' +
        "    </gml:geometryMember>"                                                                              + '\n' +
        "    <gml:geometryMember>"                                                                               + '\n' +
        "        <gml:Curve srsName=\"EPSG:27572\">"                                                          + '\n' +
        "            <gml:segments>"                                                                          + '\n' +
        "                <gml:LineStringSegment interpolation=\"linear\">"                                    + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402000.0 3334850.0</gml:pos>" + '\n' +
        "                    <gml:pos srsName=\"EPSG:27572\" srsDimension=\"2\">402200.0 3335200.0</gml:pos>" + '\n' +
        "                </gml:LineStringSegment>"                                                            + '\n' +
        "            </gml:segments>"                                                                         + '\n' +
        "        </gml:Curve>"                                                                                + '\n' +
        "    </gml:geometryMember>"                                                                              + '\n' +
        "</gml:MultiGeometry>"                                                                               + '\n';

        JTSMultiPrimitive result = (JTSMultiPrimitive) ((JAXBElement)un.unmarshal(new StringReader(xml))).getValue();

        assertEquals(expResult.getElements().size(), result.getElements().size());
        assertEquals(2, result.getElements().size());
        Iterator expIt = expResult.getElements().iterator();
        Iterator resIt = result.getElements().iterator();
        assertEquals(expIt.next(), resIt.next());
        assertEquals(expIt.next(), resIt.next());
        expIt = expResult.getElements().iterator();
        resIt = result.getElements().iterator();
        assertEquals(expIt.next().hashCode(), resIt.next().hashCode());
        assertEquals(expIt.next().hashCode(), resIt.next().hashCode());
        assertEquals(expResult.getElements(), result.getElements());
        assertEquals(expResult, result);

        crs = CRS.decode("epsg:27593");
        assertTrue(crs != null);

        xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                       + '\n' +
        "<gml:MultiGeometry xmlns:gml=\"http://www.opengis.net/gml\">"                                        + '\n' +
        "    <gml:geometryMember>" + '\n' +
        "        <gml:PolyhedralSurface srsName=\"EPSG:27593\">" + '\n' +
        "            <gml:polygonPatches>" + '\n' +
        "                <gml:PolygonPatch>" + '\n' +
        "                    <gml:exterior>" + '\n' +
        "                        <gml:Ring srsName=\"EPSG:27593\">" + '\n' +
        "                            <gml:curveMember>" + '\n' +
        "                                <gml:Curve srsName=\"EPSG:27593\">" + '\n' +
        "                                    <gml:segments>" + '\n' +
        "                                        <gml:LineStringSegment interpolation=\"linear\">"                                                      + '\n' +
        "                                            <gml:pos srsName=\"EPSG:27593\" srsDimension=\"2\">656216.1977884835 38574.31079256255</gml:pos>"  + '\n' +
        "                                            <gml:pos srsName=\"EPSG:27593\" srsDimension=\"2\">656209.434300029 38569.570186997764</gml:pos>"  + '\n' +
        "                                        </gml:LineStringSegment>"                                                                              + '\n' +
        "                                    </gml:segments>" + '\n' +
        "                                </gml:Curve>" + '\n' +
        "                            </gml:curveMember>" + '\n' +
        "                        </gml:Ring>" + '\n' +
        "                    </gml:exterior>" + '\n' +
        "                </gml:PolygonPatch>" + '\n' +
        "            </gml:polygonPatches>" + '\n' +
        "        </gml:PolyhedralSurface>" + '\n' +
        "    </gml:geometryMember>" + '\n' +
        "</gml:MultiGeometry>" + '\n';

       result = (JTSMultiPrimitive) ((JAXBElement)un.unmarshal(new StringReader(xml))).getValue();



       /*
         * FIRST POLYGON
         */

        // EXTERIOR
        JTSRing exterior1    = new JTSRing(crs);

        JTSCurve cu1 = new JTSCurve(crs);
        JTSLineString c1l1 = new JTSLineString();

        DirectPosition dp1 = new GeneralDirectPosition(crs);
        dp1.setOrdinate(0, 656216.1977884835);
        dp1.setOrdinate(1, 38574.31079256255);

        DirectPosition dp2 = new GeneralDirectPosition(crs);
        dp2.setOrdinate(0, 656209.434300029);
        dp2.setOrdinate(1, 38569.570186997764);

        c1l1.getControlPoints().add(dp1);
        c1l1.getControlPoints().add(dp2);


        cu1.getSegments().add(c1l1);
        exterior1.getElements().add(cu1);

        // INTERIOR
        Ring[] interiors1 = null;


        JTSSurfaceBoundary bound1 = new JTSSurfaceBoundary(crs, exterior1, interiors1);
        JTSPolygon poly1 = new JTSPolygon(bound1);

        JTSPolyhedralSurface PS = new JTSPolyhedralSurface(crs);
        PS.getPatches().add(poly1);

        expResult = new JTSMultiPrimitive();
        expResult.getElements().add(PS);

        JTSPolyhedralSurface expPoly = (JTSPolyhedralSurface) expResult.getElements().iterator().next();
        JTSPolyhedralSurface resPoly = (JTSPolyhedralSurface) result.getElements().iterator().next();
        assertEquals(expPoly.getPatches().get(0).getBoundary().getExterior(), resPoly.getPatches().get(0).getBoundary().getExterior());
        assertEquals(expPoly.getPatches().get(0).getBoundary().getInteriors(), resPoly.getPatches().get(0).getBoundary().getInteriors());
        assertEquals(expPoly.getPatches().get(0).getBoundary(), resPoly.getPatches().get(0).getBoundary());
        assertEquals(expPoly.getPatches().get(0).getInterpolation(), resPoly.getPatches().get(0).getInterpolation());
        assertEquals(expPoly.getPatches().get(0).getSurface(), resPoly.getPatches().get(0).getSurface());
        assertEquals(expPoly.getPatches().get(0), resPoly.getPatches().get(0));
        assertEquals(expPoly.getPatches(), resPoly.getPatches());
        assertEquals(expPoly, resPoly);
        assertEquals(expResult.getElements(), result.getElements());
        assertEquals(expResult, result);
    }
}

