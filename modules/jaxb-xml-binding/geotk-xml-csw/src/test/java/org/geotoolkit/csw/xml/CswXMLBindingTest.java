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
package org.geotoolkit.csw.xml;

// J2SE dependencies
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

// JAXB dependencies
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

// Constellation dependencies
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.v202.AbstractRecordType;
import org.geotoolkit.csw.xml.v202.BriefRecordType;
import org.geotoolkit.csw.xml.v202.Capabilities;
import org.geotoolkit.csw.xml.v202.ElementSetNameType;
import org.geotoolkit.csw.xml.v202.GetRecordByIdResponseType;
import org.geotoolkit.csw.xml.v202.GetRecordsType;
import org.geotoolkit.csw.xml.v202.ObjectFactory;
import org.geotoolkit.csw.xml.v202.QueryConstraintType;
import org.geotoolkit.csw.xml.v202.QueryType;
import org.geotoolkit.csw.xml.v202.RecordPropertyType;
import org.geotoolkit.csw.xml.v202.RecordType;
import org.geotoolkit.csw.xml.ResultType;
import org.geotoolkit.csw.xml.v202.SummaryRecordType;
import org.geotoolkit.csw.xml.v202.TransactionType;
import org.geotoolkit.csw.xml.v202.UpdateType;
import org.geotoolkit.dublincore.xml.v2.elements.SimpleLiteral;
import org.geotoolkit.inspire.xml.DocumentType;
import org.geotoolkit.inspire.xml.InspireCapabilitiesType;
import org.geotoolkit.inspire.xml.LanguagesType;
import org.geotoolkit.inspire.xml.MultiLingualCapabilities;
import org.geotoolkit.inspire.xml.TranslatedCapabilitiesType;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.ogc.xml.v110.NotType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLikeType;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.ows.xml.v100.BoundingBoxType;
import org.geotoolkit.ows.xml.v100.OperationsMetadata;
import org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType;

// Geotools dependencies
import org.geotoolkit.metadata.iso.DefaultMetaData;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;

//Junit dependencies
import org.junit.*;
import static org.junit.Assert.*;

/**
 * A Test suite verifying that the Record are correctly marshalled/unmarshalled
 * 
 * @author Guilhem Legal
 * @module pending
 */
public class CswXMLBindingTest {
    
    private static Logger LOGGER = Logging.getLogger(CswXMLBindingTest.class);

    private MarshallerPool pool202;
    private Unmarshaller recordUnmarshaller202;
    private Marshaller   recordMarshaller202;

    private MarshallerPool pool200;
    private Unmarshaller recordUnmarshaller200;
    private Marshaller   recordMarshaller200;
   
     /**
     * A JAXB factory to csw object version 2.0.2
     */
    protected final ObjectFactory cswFactory202 = new ObjectFactory();;
    
    /**
     * A JAXB factory to csw object version 2.0.0 
     */
    protected final org.geotoolkit.csw.xml.v200.ObjectFactory cswFactory200 = new org.geotoolkit.csw.xml.v200.ObjectFactory();
    
    /**
     * a QName for csw:Record type
     */
    private static final QName _Record_QNAME = new QName("http://www.opengis.net/cat/csw/2.0.2", "Record");

    @Before
    public void setUp() throws JAXBException {
        pool202 = new MarshallerPool(org.geotoolkit.csw.xml.v202.ObjectFactory.class, DefaultMetaData.class);
        recordUnmarshaller202    = pool202.acquireUnmarshaller();
        recordMarshaller202      = pool202.acquireMarshaller();
        
        pool200 = new MarshallerPool(org.geotoolkit.csw.xml.v200.ObjectFactory.class,
                                     org.geotoolkit.dublincore.xml.v1.terms.ObjectFactory.class,
                                     org.geotoolkit.dublincore.xml.v2.terms.ObjectFactory.class);
        recordUnmarshaller200    = pool200.acquireUnmarshaller();
        recordMarshaller200      = pool200.acquireMarshaller();
    }

    @After
    public void tearDown() {
        if (recordUnmarshaller202 != null) {
            pool202.release(recordUnmarshaller202);
        }
        if (recordMarshaller202 != null) {
            pool202.release(recordMarshaller202);
        }
        if (recordUnmarshaller200 != null) {
            pool200.release(recordUnmarshaller200);
        }
        if (recordMarshaller200 != null) {
            pool200.release(recordMarshaller200);
        }
    }
    
    /**
     * Test simple Record Marshalling. 
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void recordMarshalingTest() throws JAXBException {
        
        /*
         * Test marshalling csw Record v2.0.2
         */
        
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");
        
        List<SimpleLiteral> subject = new ArrayList<SimpleLiteral>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));
        
        SimpleLiteral modified   = new SimpleLiteral("2007-11-15 21:26:49");
        SimpleLiteral Abstract   = new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.");
        SimpleLiteral references = new SimpleLiteral("http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml");
        SimpleLiteral spatial    = new SimpleLiteral("northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;");
        
        List<BoundingBoxType> bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));
        
        RecordType record = new RecordType(id, title, type, subject, null, modified, null, Abstract, bbox, null, null, null, spatial, references);
        
        StringWriter sw = new StringWriter();
        recordMarshaller202.marshal(record, sw);
        
        String result = sw.toString();
        String expResult = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:Record xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>" + '\n' +
        "    <dc:title>(JASON-1)</dc:title>"                                        + '\n' +
        "    <dc:type>clearinghouse</dc:type>"                                      + '\n' +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>"            + '\n' +
        "    <dc:subject>oceans elevation 2</dc:subject>"                           + '\n' +
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>"                      + '\n' +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>" + '\n' +
        "    <dct:references>http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml</dct:references>" + '\n' +
        "    <dct:spatial>northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;</dct:spatial>" + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                                + '\n' +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                               + '\n' +
        "</csw:Record>" + '\n';
    
        //we remove the 2 first line because the xlmns are not always in the same order.
        expResult = expResult.substring(expResult.indexOf('\n') + 1);
        expResult = expResult.substring(expResult.indexOf('\n') + 1);
        
        result = result.substring(result.indexOf('\n') + 1);
        result = result.substring(result.indexOf('\n') + 1);
        
        assertEquals(expResult, result);
        
    }
    
    /**
     * Test simple Record Marshalling. 
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void recordUnmarshalingTest() throws JAXBException {
        
        /*
         * Test Unmarshalling csw Record v2.0.2
         */
        
        String xml = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:Record xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>" + '\n' +
        "    <dc:title>(JASON-1)</dc:title>"                                        + '\n' +
        "    <dc:type>clearinghouse</dc:type>"                                      + '\n' +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>"            + '\n' +
        "    <dc:subject>oceans elevation 2</dc:subject>"                           + '\n' +
        "    <dc:format>binary</dc:format>"                                         + '\n' +        
        "    <dc:date>2007-12-01</dc:date>"                                         + '\n' +  
        "    <dc:publisher>geomatys</dc:publisher>"                                 + '\n' +           
        "    <dc:creator>geomatys</dc:creator>"                                 + '\n' +               
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>"                      + '\n' +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>" + '\n' +
        "    <dct:spatial>northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;</dct:spatial>" + '\n' +
        "    <dct:references>http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml</dct:references>" + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                                + '\n' +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                               + '\n' +
        "</csw:Record>" + '\n';
        
        StringReader sr = new StringReader(xml);
        
        JAXBElement jb = (JAXBElement) recordUnmarshaller202.unmarshal(sr);
        RecordType result = (RecordType) jb.getValue();
        
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");
        
        List<SimpleLiteral> subject = new ArrayList<SimpleLiteral>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));
        
        SimpleLiteral modified    = new SimpleLiteral("2007-11-15 21:26:49");
        SimpleLiteral date        = new SimpleLiteral("2007-12-01");
        SimpleLiteral Abstract    = new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.");
        SimpleLiteral references  = new SimpleLiteral("http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml");
        SimpleLiteral spatial     = new SimpleLiteral("northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;");
        SimpleLiteral format      = new SimpleLiteral("binary");
        SimpleLiteral distributor = new SimpleLiteral("geomatys");
        SimpleLiteral creator = new SimpleLiteral("geomatys");
        
        List<BoundingBoxType> bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));
        
        RecordType expResult = new RecordType(id, title, type, subject, format, modified, date, Abstract, bbox, creator, distributor, null, spatial, references);
        
        LOGGER.finer("DATE " +expResult.getDate() + " - " + result.getDate());
        assertEquals(expResult.getDate(), result.getDate());

        LOGGER.finer("ABSTRACT " +expResult.getAbstract() + " - " + result.getAbstract());
        assertEquals(expResult.getAbstract(), result.getAbstract());
        
        LOGGER.finer("SPATIAL " +expResult.getSpatial() + " - " + result.getSpatial());
        assertEquals(expResult.getSpatial(), result.getSpatial());
        
        LOGGER.finer("BBOXES " +expResult.getBoundingBox() + " - " + result.getBoundingBox());
        assertEquals(expResult.getBoundingBox().get(0).getValue(), result.getBoundingBox().get(0).getValue());
        
                
        LOGGER.finer("RESULT: " + result.toString());
        LOGGER.finer("");
        LOGGER.finer("EXPRESULT: " + expResult.toString());
        LOGGER.finer("-----------------------------------------------------------");
        assertEquals(expResult, result);
        
        
        /*
         * Test Unmarshalling csw Record v2.0.0 with http://purl... DC namespace
         */
        
        xml = 
        "<csw:Record xmlns:csw=\"http://www.opengis.net/cat/csw\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">" + '\n' +
        "   <dc:identifier>ESIGNGRAVIMÉTRICOPENINSULAYBALEARES200703070000</dc:identifier>"                      + '\n' +
        "   <dc:title>Estudio Gravimétrico de la Península Ibérica y Baleares</dc:title>"                        + '\n' +      
        "   <dc:title>Mapa de Anomalías Gravimétricas</dc:title>"                                                + '\n' +
        "   <dc:creator>Instituto Geográfico Nacional</dc:creator>"                                              + '\n' +
        "   <dc:subject>http://www.fao.org/aos/concept#4668.Gravimetría</dc:subject>"                            + '\n' +
        "   <dc:subject>Anomalías Gravimétricas</dc:subject>"                                                    + '\n' +
        "   <dc:subject>Anomalías Aire Libre</dc:subject>"                                                       + '\n' +
        "   <dc:subject>Anomalías Bouguer</dc:subject>"                                                          + '\n' +
        "   <dc:subject>Información geocientífica</dc:subject>"                                                  + '\n' +
        "   <dc:description>El Estudio Gravimétrico de la Península Ibérica y Baleares, que representa las anomalías gravimétricas de esa zona, fue generado por el Instituto Geográfico Nacional en el año 1996. El estudio está constituido por dos mapas; Anomalías Gravimétricas Aire Libre de la Península Ibérica y Baleares, Anomalías Gravimétricas Bouguer de la Península Ibérica y Baleares más una memoria. Inicialmente para su generación se creó una base de datos gravimétrica homogénea a partir de observaciones de distinta procedencia, también se formó un modelo digital del terreno homogéneo a partir de otros modelos digitales del terreno procedentes de España, Portugal y Francia. Los mapas contienen isolíneas de anomalías gravimétricas en intervalos de 2mGal. Los datos se almacenan en formato DGN.</dc:description>" + '\n' +
        "   <dc:date>2007-03-07</dc:date>"                                                                       + '\n' +
        "   <dc:type>mapHardcopy</dc:type>"                                                                      + '\n' +
        "   <dc:type>mapDigital</dc:type>"                                                                       + '\n' +
        "   <dc:type>documentHardcopy</dc:type>"                                                                 + '\n' +
        "   <dc:format>DGN - Microstation format (Intergraph Corporation)</dc:format>"                           + '\n' +
        "   <dc:format>Papel</dc:format>"                                                                        + '\n' +
        "   <dc:identifier>www.cnig.es</dc:identifier>"                                                          + '\n' +
        "   <dc:source>El Banco de Datos Gravimétricos es una base de datos compuesta principalmente por las observaciones realizadas por el Instituto Geográfico Nacional desde 1960. Además se han añadido los datos del Instituto Portugués de Geografía y Catastro, del proyecto ECORS, de la Universidad de Cantabria y del Bureau Gravimétrico Internacional.</dc:source>" + '\n' +
        "   <dc:source>Para la creación del Modelo Digital del Terreno a escala 1:200.000 para toda la Península Ibérica, áreas marinas y terrestres adyacentes, en particular, se ha dispuesto de la siguiente información; Modelo Digital del Terreno a escala 1:200.000, Modelo Digital del Terreno obtenido del Defense Mapping Agency de los Estados Unidos, para completar la zona de Portugal; Modelo Digital del Terreno obtenido del Instituto Geográfico Nacional de Francia para la parte francesa del Pirineo y el Modelo Digital del Terreno generado a partir de las cartas náuticas del Instituto Hidrográfico de la Marina de España, que completa la parte marina hasta los 167 km. de la costa con un ancho de malla de 5 km.</dc:source>" + '\n' +
        "   <dc:language>es</dc:language>"                                                                       + '\n' +
        "   <dcterms:spatial>northlimit=43.83; southlimit=36.00; westlimit=-9.35; eastlimit=4.32;</dcterms:spatial>" + '\n' +
        "   <dcterms:spatial>ESPAÑA.ANDALUCÍA</dcterms:spatial>"                                                 + '\n' +
        "   <dcterms:spatial>ESPAÑA.ARAGÓN</dcterms:spatial>"                                                    + '\n' +
        "</csw:Record>";
        
        sr = new StringReader(xml);
        
        jb = (JAXBElement) recordUnmarshaller200.unmarshal(sr);
        org.geotoolkit.csw.xml.v200.RecordType result2 = (org.geotoolkit.csw.xml.v200.RecordType) jb.getValue();
        
        LOGGER.finer("result:" + result2.toString());
        
         /*
         * Test Unmarshalling csw Record v2.0.0 with http://www.purl... DC namespace
         */
        
        xml = 
        "<csw:Record xmlns:csw=\"http://www.opengis.net/cat/csw\" xmlns:dcterms=\"http://www.purl.org/dc/terms/\" xmlns:dc=\"http://www.purl.org/dc/elements/1.1/\">" + '\n' +
        "   <dc:identifier>ESIGNGRAVIMÉTRICOPENINSULAYBALEARES200703070000</dc:identifier>"                      + '\n' +
        "   <dc:title>Estudio Gravimétrico de la Península Ibérica y Baleares</dc:title>"                        + '\n' +      
        "   <dc:title>Mapa de Anomalías Gravimétricas</dc:title>"                                                + '\n' +
        "   <dc:creator>Instituto Geográfico Nacional</dc:creator>"                                              + '\n' +
        "   <dc:subject>http://www.fao.org/aos/concept#4668.Gravimetría</dc:subject>"                            + '\n' +
        "   <dc:subject>Anomalías Gravimétricas</dc:subject>"                                                    + '\n' +
        "   <dc:subject>Anomalías Aire Libre</dc:subject>"                                                       + '\n' +
        "   <dc:subject>Anomalías Bouguer</dc:subject>"                                                          + '\n' +
        "   <dc:subject>Información geocientífica</dc:subject>"                                                  + '\n' +
        "   <dc:description>El Estudio Gravimétrico de la Península Ibérica y Baleares, que representa las anomalías gravimétricas de esa zona, fue generado por el Instituto Geográfico Nacional en el año 1996. El estudio está constituido por dos mapas; Anomalías Gravimétricas Aire Libre de la Península Ibérica y Baleares, Anomalías Gravimétricas Bouguer de la Península Ibérica y Baleares más una memoria. Inicialmente para su generación se creó una base de datos gravimétrica homogénea a partir de observaciones de distinta procedencia, también se formó un modelo digital del terreno homogéneo a partir de otros modelos digitales del terreno procedentes de España, Portugal y Francia. Los mapas contienen isolíneas de anomalías gravimétricas en intervalos de 2mGal. Los datos se almacenan en formato DGN.</dc:description>" + '\n' +
        "   <dc:date>2007-03-07</dc:date>"                                                                       + '\n' +
        "   <dc:type>mapHardcopy</dc:type>"                                                                      + '\n' +
        "   <dc:type>mapDigital</dc:type>"                                                                       + '\n' +
        "   <dc:type>documentHardcopy</dc:type>"                                                                 + '\n' +
        "   <dc:format>DGN - Microstation format (Intergraph Corporation)</dc:format>"                           + '\n' +
        "   <dc:format>Papel</dc:format>"                                                                        + '\n' +
        "   <dc:identifier>www.cnig.es</dc:identifier>"                                                          + '\n' +
        "   <dc:source>El Banco de Datos Gravimétricos es una base de datos compuesta principalmente por las observaciones realizadas por el Instituto Geográfico Nacional desde 1960. Además se han añadido los datos del Instituto Portugués de Geografía y Catastro, del proyecto ECORS, de la Universidad de Cantabria y del Bureau Gravimétrico Internacional.</dc:source>" + '\n' +
        "   <dc:source>Para la creación del Modelo Digital del Terreno a escala 1:200.000 para toda la Península Ibérica, áreas marinas y terrestres adyacentes, en particular, se ha dispuesto de la siguiente información; Modelo Digital del Terreno a escala 1:200.000, Modelo Digital del Terreno obtenido del Defense Mapping Agency de los Estados Unidos, para completar la zona de Portugal; Modelo Digital del Terreno obtenido del Instituto Geográfico Nacional de Francia para la parte francesa del Pirineo y el Modelo Digital del Terreno generado a partir de las cartas náuticas del Instituto Hidrográfico de la Marina de España, que completa la parte marina hasta los 167 km. de la costa con un ancho de malla de 5 km.</dc:source>" + '\n' +
        "   <dc:language>es</dc:language>"                                                                       + '\n' +
        "   <dcterms:spatial>northlimit=43.83; southlimit=36.00; westlimit=-9.35; eastlimit=4.32;</dcterms:spatial>" + '\n' +
        "   <dcterms:spatial>ESPAÑA.ANDALUCÍA</dcterms:spatial>"                                                 + '\n' +
        "   <dcterms:spatial>ESPAÑA.ARAGÓN</dcterms:spatial>"                                                    + '\n' +
        "</csw:Record>";
        
        sr = new StringReader(xml);
        
        jb = (JAXBElement) recordUnmarshaller200.unmarshal(sr);
        result2 = (org.geotoolkit.csw.xml.v200.RecordType) jb.getValue();
        
        LOGGER.finer("result:" + result2.toString());
    }

    /**
     * Test summary Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void summmaryRecordMarshalingTest() throws JAXBException {

        /*
         * Test marshalling csw summmary Record v2.0.2
         */

        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<SimpleLiteral>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        List<SimpleLiteral> formats = new ArrayList<SimpleLiteral>();
        formats.add(new SimpleLiteral("format 11-11"));
        formats.add(new SimpleLiteral("format 22-22"));

        SimpleLiteral modified         = new SimpleLiteral("2007-11-15 21:26:49");
        List<SimpleLiteral> Abstract   = new ArrayList<SimpleLiteral>();
        Abstract.add(new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm."));

        List<BoundingBoxType> bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        SummaryRecordType record = new SummaryRecordType(id, title, type,  bbox, subject, formats, modified, Abstract);

        StringWriter sw = new StringWriter();
        recordMarshaller202.marshal(record, sw);

        String result = sw.toString();
        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:SummaryRecord xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>" + '\n' +
        "    <dc:title>(JASON-1)</dc:title>"                                        + '\n' +
        "    <dc:type>clearinghouse</dc:type>"                                      + '\n' +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>"            + '\n' +
        "    <dc:subject>oceans elevation 2</dc:subject>"                           + '\n' +
        "    <dc:format>format 11-11</dc:format>"                                   + '\n' +
        "    <dc:format>format 22-22</dc:format>"                                   + '\n' +
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>"                      + '\n' +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>" + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                                + '\n' +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                               + '\n' +
        "</csw:SummaryRecord>" + '\n';

        //we remove the 2 first line because the xlmns are not always in the same order.
        expResult = expResult.substring(expResult.indexOf('\n') + 1);
        expResult = expResult.substring(expResult.indexOf('\n') + 1);

        result = result.substring(result.indexOf('\n') + 1);
        result = result.substring(result.indexOf('\n') + 1);

        assertEquals(expResult, result);


        /*
         * Test marshalling csw summmary Record v2.0.2
         */

        List<SimpleLiteral> ids    = new ArrayList<SimpleLiteral>();
        ids.add(new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}"));
        ids.add(new SimpleLiteral("urn:ogc-x:df:F7807C8AB645"));
        List<SimpleLiteral> titles = new ArrayList<SimpleLiteral>();
        titles.add(new SimpleLiteral("(JASON-1)"));
        titles.add(new SimpleLiteral("(JASON-2)"));

        type       = new SimpleLiteral("clearinghouse");

        subject = new ArrayList<SimpleLiteral>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        formats = new ArrayList<SimpleLiteral>();
        formats.add(new SimpleLiteral("format 11-11"));
        formats.add(new SimpleLiteral("format 22-22"));

        List<SimpleLiteral> modifieds   = new ArrayList<SimpleLiteral>();
        modifieds.add(new SimpleLiteral("2007-11-15 21:26:49"));
        modifieds.add(new SimpleLiteral("2007-11-15 21:26:48"));
        Abstract   = new ArrayList<SimpleLiteral>();
        Abstract.add(new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm."));
        Abstract.add(new SimpleLiteral("Jason-2 blablablablabla."));

        bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));
        bbox.add(new WGS84BoundingBoxType(100, -6.04, -144, 5.9));

        record = new SummaryRecordType(ids, titles, type,  bbox, subject, formats, modifieds, Abstract);

        sw = new StringWriter();
        recordMarshaller202.marshal(record, sw);

        result = sw.toString();
        expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:SummaryRecord xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>" + '\n' +
        "    <dc:identifier>urn:ogc-x:df:F7807C8AB645</dc:identifier>" + '\n' +
        "    <dc:title>(JASON-1)</dc:title>"                                        + '\n' +
        "    <dc:title>(JASON-2)</dc:title>"                                        + '\n' +
        "    <dc:type>clearinghouse</dc:type>"                                      + '\n' +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>"            + '\n' +
        "    <dc:subject>oceans elevation 2</dc:subject>"                           + '\n' +
        "    <dc:format>format 11-11</dc:format>"                                   + '\n' +
        "    <dc:format>format 22-22</dc:format>"                                   + '\n' +
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>"                      + '\n' +
        "    <dct:modified>2007-11-15 21:26:48</dct:modified>"                      + '\n' +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>" + '\n' +
        "    <dct:abstract>Jason-2 blablablablabla.</dct:abstract>" + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                                + '\n' +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                               + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                                + '\n' +
        "        <ows:LowerCorner>100.0 -6.04</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-144.0 5.9</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                               + '\n' +
        "</csw:SummaryRecord>" + '\n';

        //we remove the 2 first line because the xlmns are not always in the same order.
        expResult = expResult.substring(expResult.indexOf('\n') + 1);
        expResult = expResult.substring(expResult.indexOf('\n') + 1);

        result = result.substring(result.indexOf('\n') + 1);
        result = result.substring(result.indexOf('\n') + 1);

        assertEquals(expResult, result);

    }

    /**
     * Test summary Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void summmaryRecordUnmarshalingTest() throws JAXBException {

        /*
         * Test marshalling csw summmary Record v2.0.2
         */

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:SummaryRecord xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>" + '\n' +
        "    <dc:title>(JASON-1)</dc:title>"                                        + '\n' +
        "    <dc:type>clearinghouse</dc:type>"                                      + '\n' +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>"            + '\n' +
        "    <dc:subject>oceans elevation 2</dc:subject>"                           + '\n' +
        "    <dc:format>format 11-11</dc:format>"                                   + '\n' +
        "    <dc:format>format 22-22</dc:format>"                                   + '\n' +
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>"                      + '\n' +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>" + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                                + '\n' +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                               + '\n' +
        "</csw:SummaryRecord>" + '\n';

        StringReader sr = new StringReader(xml);
        JAXBElement<SummaryRecordType> jb = (JAXBElement) recordUnmarshaller202.unmarshal(sr);
        SummaryRecordType result = jb.getValue();

        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<SimpleLiteral>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        List<SimpleLiteral> formats = new ArrayList<SimpleLiteral>();
        formats.add(new SimpleLiteral("format 11-11"));
        formats.add(new SimpleLiteral("format 22-22"));

        SimpleLiteral modified         = new SimpleLiteral("2007-11-15 21:26:49");
        List<SimpleLiteral> Abstract   = new ArrayList<SimpleLiteral>();
        Abstract.add(new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm."));

        List<BoundingBoxType> bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        SummaryRecordType expResult = new SummaryRecordType(id, title, type,  bbox, subject, formats, modified, Abstract);

        assertEquals(expResult, result);


        /*
         * Test marshalling csw summmary Record v2.0.2
         */

        xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:SummaryRecord xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>" + '\n' +
        "    <dc:identifier>urn:ogc-x:df:F7807C8AB645</dc:identifier>" + '\n' +
        "    <dc:title>(JASON-1)</dc:title>"                                        + '\n' +
        "    <dc:title>(JASON-2)</dc:title>"                                        + '\n' +
        "    <dc:type>clearinghouse</dc:type>"                                      + '\n' +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>"            + '\n' +
        "    <dc:subject>oceans elevation 2</dc:subject>"                           + '\n' +
        "    <dc:format>format 11-11</dc:format>"                                   + '\n' +
        "    <dc:format>format 22-22</dc:format>"                                   + '\n' +
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>"                      + '\n' +
        "    <dct:modified>2007-11-15 21:26:48</dct:modified>"                      + '\n' +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>" + '\n' +
        "    <dct:abstract>Jason-2 blablablablabla.</dct:abstract>" + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                                + '\n' +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                               + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                                + '\n' +
        "        <ows:LowerCorner>100.0 -6.04</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-144.0 5.9</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                               + '\n' +
        "</csw:SummaryRecord>" + '\n';

        sr = new StringReader(xml);
        jb = (JAXBElement) recordUnmarshaller202.unmarshal(sr);
        result = jb.getValue();


        List<SimpleLiteral> ids    = new ArrayList<SimpleLiteral>();
        ids.add(new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}"));
        ids.add(new SimpleLiteral("urn:ogc-x:df:F7807C8AB645"));
        List<SimpleLiteral> titles = new ArrayList<SimpleLiteral>();
        titles.add(new SimpleLiteral("(JASON-1)"));
        titles.add(new SimpleLiteral("(JASON-2)"));

        type       = new SimpleLiteral("clearinghouse");

        subject = new ArrayList<SimpleLiteral>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        formats = new ArrayList<SimpleLiteral>();
        formats.add(new SimpleLiteral("format 11-11"));
        formats.add(new SimpleLiteral("format 22-22"));

        List<SimpleLiteral> modifieds   = new ArrayList<SimpleLiteral>();
        modifieds.add(new SimpleLiteral("2007-11-15 21:26:49"));
        modifieds.add(new SimpleLiteral("2007-11-15 21:26:48"));
        Abstract   = new ArrayList<SimpleLiteral>();
        Abstract.add(new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm."));
        Abstract.add(new SimpleLiteral("Jason-2 blablablablabla."));

        bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));
        bbox.add(new WGS84BoundingBoxType(100, -6.04, -144, 5.9));

        expResult = new SummaryRecordType(ids, titles, type,  bbox, subject, formats, modifieds, Abstract);

        assertEquals(expResult, result);

    }

    /**
     * Test brief Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void briefRecordMarshalingTest() throws JAXBException {

        /*
         * Test marshalling BRIEF csw Record v2.0.2
         */

        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<BoundingBoxType> bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        BriefRecordType record = new BriefRecordType(id, title, type, bbox);

        StringWriter sw = new StringWriter();
        recordMarshaller202.marshal(record, sw);

        String result = sw.toString();
        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"               + '\n' +
        "<csw:BriefRecord xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>"   + '\n' +
        "    <dc:title>(JASON-1)</dc:title>"                                          + '\n' +
        "    <dc:type>clearinghouse</dc:type>"                                        + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                 + '\n' +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                                 + '\n' +
        "</csw:BriefRecord>" + '\n';

        //we remove the 2 first line because the xlmns are not always in the same order.
        expResult = expResult.substring(expResult.indexOf('\n') + 1);
        expResult = expResult.substring(expResult.indexOf('\n') + 1);

        result = result.substring(result.indexOf('\n') + 1);
        result = result.substring(result.indexOf('\n') + 1);

        assertEquals(expResult, result);

         /*
         * Test marshalling csw Record v2.0.2
         */

        List<SimpleLiteral> identifiers = new ArrayList<SimpleLiteral>();
        identifiers.add(new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}"));
        identifiers.add(new SimpleLiteral("urn:ogc:x-def:F7807C8AB645"));

        List<SimpleLiteral> titles = new ArrayList<SimpleLiteral>();
        titles.add(new SimpleLiteral("(JASON-1)"));
        titles.add(new SimpleLiteral("(JASON-2)"));

        type       = new SimpleLiteral("clearinghouse");

        bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));
        bbox.add(new WGS84BoundingBoxType(176, -16.4, -178, 6.1));

        record = new BriefRecordType(identifiers, titles, type, bbox);

        sw = new StringWriter();
        recordMarshaller202.marshal(record, sw);

        result = sw.toString();
        expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"               + '\n' +
        "<csw:BriefRecord xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>"   + '\n' +
        "    <dc:identifier>urn:ogc:x-def:F7807C8AB645</dc:identifier>"               + '\n' +
        "    <dc:title>(JASON-1)</dc:title>"                                          + '\n' +
        "    <dc:title>(JASON-2)</dc:title>"                                          + '\n' +
        "    <dc:type>clearinghouse</dc:type>"                                        + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                 + '\n' +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                                 + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                 + '\n' +
        "        <ows:LowerCorner>176.0 -16.4</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-178.0 6.1</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                                 + '\n' +
        "</csw:BriefRecord>" + '\n';

        //we remove the 2 first line because the xlmns are not always in the same order.
        expResult = expResult.substring(expResult.indexOf('\n') + 1);
        expResult = expResult.substring(expResult.indexOf('\n') + 1);

        result = result.substring(result.indexOf('\n') + 1);
        result = result.substring(result.indexOf('\n') + 1);

        assertEquals(expResult, result);

    }

    /**
     * Test brief Record Unmarshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void briefRecordUnmarshalingTest() throws JAXBException {

        /*
         * Test marshalling BRIEF csw Record v2.0.2
         */

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"               + '\n' +
        "<csw:BriefRecord xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>"   + '\n' +
        "    <dc:title>(JASON-1)</dc:title>"                                          + '\n' +
        "    <dc:type>clearinghouse</dc:type>"                                        + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                 + '\n' +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                                 + '\n' +
        "</csw:BriefRecord>" + '\n';

        StringReader sr = new StringReader(xml);
        JAXBElement<BriefRecordType> jb = (JAXBElement) recordUnmarshaller202.unmarshal(sr);
        BriefRecordType result = jb.getValue();

        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<BoundingBoxType> bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        BriefRecordType expResult = new BriefRecordType(id, title, type, bbox);

        assertEquals(expResult, result);

         /*
         * Test marshalling csw Record v2.0.2
         */

        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"               + '\n' +
        "<csw:BriefRecord xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>"   + '\n' +
        "    <dc:identifier>urn:ogc:x-def:F7807C8AB645</dc:identifier>"               + '\n' +
        "    <dc:title>(JASON-1)</dc:title>"                                          + '\n' +
        "    <dc:title>(JASON-2)</dc:title>"                                          + '\n' +
        "    <dc:type>clearinghouse</dc:type>"                                        + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                 + '\n' +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                                 + '\n' +
        "    <ows:WGS84BoundingBox dimensions=\"2\">"                                 + '\n' +
        "        <ows:LowerCorner>176.0 -16.4</ows:LowerCorner>"          + '\n' +
        "        <ows:UpperCorner>-178.0 6.1</ows:UpperCorner>"          + '\n' +
        "    </ows:WGS84BoundingBox>"                                                 + '\n' +
        "</csw:BriefRecord>" + '\n';

        sr = new StringReader(xml);
        jb = (JAXBElement<BriefRecordType>) recordUnmarshaller202.unmarshal(sr);
        result = jb.getValue();

        List<SimpleLiteral> identifiers = new ArrayList<SimpleLiteral>();
        identifiers.add(new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}"));
        identifiers.add(new SimpleLiteral("urn:ogc:x-def:F7807C8AB645"));

        List<SimpleLiteral> titles = new ArrayList<SimpleLiteral>();
        titles.add(new SimpleLiteral("(JASON-1)"));
        titles.add(new SimpleLiteral("(JASON-2)"));

        type       = new SimpleLiteral("clearinghouse");

        bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));
        bbox.add(new WGS84BoundingBoxType(176, -16.4, -178, 6.1));

        expResult = new BriefRecordType(identifiers, titles, type, bbox);

        assertEquals(expResult, result);

    }
    
    /**
     * Test getRecordById request Marshalling.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void getRecordByIdResponseMarshalingTest() throws JAXBException {
        
         /*
         * Test marshalling csw getRecordByIdResponse v2.0.2
         */
        
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");
        
        List<SimpleLiteral> subject = new ArrayList<SimpleLiteral>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));
        
        SimpleLiteral modified   = new SimpleLiteral("2007-11-15 21:26:49");
        SimpleLiteral Abstract   = new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.");
        SimpleLiteral references = new SimpleLiteral("http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml");
        SimpleLiteral spatial    = new SimpleLiteral("northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;");
        
        List<BoundingBoxType> bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));
        
        RecordType record           = new RecordType(id, title, type, subject, null, modified, null, Abstract, bbox, null, null, null, spatial, references);
        BriefRecordType briefRecord = new BriefRecordType(id, title, type, bbox);
        SummaryRecordType sumRecord = new SummaryRecordType(id, title, type, bbox, subject, null, modified, Abstract);
        
        List<AbstractRecordType> records = new ArrayList<AbstractRecordType>(); 
        records.add(record);
        records.add(briefRecord);
        records.add(sumRecord);
        GetRecordByIdResponse response = new GetRecordByIdResponseType(records, null);
        
        StringWriter sw = new StringWriter();
        recordMarshaller202.marshal(response, sw);
        
        String result = sw.toString();
        
        
        String expResult = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:GetRecordByIdResponse xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <csw:Record>"                                                              + '\n' +
        "        <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>" + '\n' +
        "        <dc:title>(JASON-1)</dc:title>"                                        + '\n' +
        "        <dc:type>clearinghouse</dc:type>"                                      + '\n' +
        "        <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>"            + '\n' +
        "        <dc:subject>oceans elevation 2</dc:subject>"                           + '\n' +
        "        <dct:modified>2007-11-15 21:26:49</dct:modified>"                      + '\n' +
        "        <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>" + '\n' +
        "        <dct:references>http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml</dct:references>" + '\n' +
        "        <dct:spatial>northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;</dct:spatial>" + '\n' +
        "        <ows:WGS84BoundingBox dimensions=\"2\">"                               + '\n' +
        "            <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"        + '\n' +
        "            <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"        + '\n' +
        "        </ows:WGS84BoundingBox>"                                               + '\n' +
        "    </csw:Record>"                                                             + '\n' +
        "    <csw:BriefRecord>"                                                         + '\n' +
        "        <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>" + '\n' +
        "        <dc:title>(JASON-1)</dc:title>"                                        + '\n' +
        "        <dc:type>clearinghouse</dc:type>"                                      + '\n' +
        "        <ows:WGS84BoundingBox dimensions=\"2\">"                               + '\n' +
        "            <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"        + '\n' +
        "            <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"        + '\n' +
        "        </ows:WGS84BoundingBox>"                                               + '\n' +
        "    </csw:BriefRecord>"                                                        + '\n' +
        "    <csw:SummaryRecord>"                                                              + '\n' +
        "        <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>" + '\n' +
        "        <dc:title>(JASON-1)</dc:title>"                                        + '\n' +
        "        <dc:type>clearinghouse</dc:type>"                                      + '\n' +
        "        <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>"            + '\n' +
        "        <dc:subject>oceans elevation 2</dc:subject>"                           + '\n' +
        "        <dct:modified>2007-11-15 21:26:49</dct:modified>"                      + '\n' +
        "        <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>" + '\n' +
        "        <ows:WGS84BoundingBox dimensions=\"2\">"                               + '\n' +
        "            <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>"        + '\n' +
        "            <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>"        + '\n' +
        "        </ows:WGS84BoundingBox>"                                               + '\n' +
        "    </csw:SummaryRecord>"                                                             + '\n' +
        "</csw:GetRecordByIdResponse>" + '\n';
        
        //we remove the 2 first line because the xlmns are not always in the same order.
        expResult = expResult.substring(expResult.indexOf('\n') + 1);
        expResult = expResult.substring(expResult.indexOf('\n') + 1);
        
        result = result.substring(result.indexOf('\n') + 1);
        result = result.substring(result.indexOf('\n') + 1);
        
        LOGGER.finer("RESULT:" + '\n' + result);
        LOGGER.finer("EXPRESULT:" + '\n' + expResult);
        assertEquals(expResult, result);
        
 
    }
    
    /**
     * Test simple Record Marshalling. 
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void getRecordsMarshalingTest() throws JAXBException {
        
         /*
         * Test marshalling csw getRecordByIdResponse v2.0.2
         */
        
        /*
         * we build the first filter : < dublinCore:Title IS LIKE '*' >
         */ 
        List<QName> typeNames  = new ArrayList<QName>();
        PropertyNameType pname = new PropertyNameType("dc:Title");
        PropertyIsLikeType pil = new PropertyIsLikeType(pname, "something?", "*", "?", "\\");
        NotType n              = new NotType(pil);
        FilterType filter1     = new FilterType(n);
        
        QueryConstraintType constraint = new QueryConstraintType(filter1, "1.1.0");
        typeNames.add(_Record_QNAME);
        QueryType query = new QueryType(typeNames, new ElementSetNameType(ElementSetType.FULL), null, constraint); 
        
        GetRecordsType getRecordsRequest = new GetRecordsType("CSW", "2.0.2", ResultType.RESULTS, null, "application/xml", "http://www.opengis.net/cat/csw/2.0.2", 1, 20, query, null);
         
        
        StringWriter sw = new StringWriter();
        recordMarshaller202.marshal(getRecordsRequest, sw);
        
        String result = sw.toString();
        
        
        String expResult = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:GetRecords maxRecords=\"20\" startPosition=\"1\" outputSchema=\"http://www.opengis.net/cat/csw/2.0.2\" outputFormat=\"application/xml\" resultType=\"results\" version=\"2.0.2\" service=\"CSW\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <csw:Query typeNames=\"csw:Record\">" + '\n' +
        "        <csw:ElementSetName>full</csw:ElementSetName>"                         + '\n' +
        "        <csw:Constraint version=\"1.1.0\">"                                    + '\n' +
        "            <ogc:Filter>"                                                      + '\n' +
        "                <ogc:Not>"                                                     + '\n' +
        "                    <ogc:PropertyIsLike wildCard=\"*\" singleChar=\"?\" escapeChar=\"\\\">"    + '\n' +
        "                        <ogc:PropertyName>dc:Title</ogc:PropertyName>"         + '\n' +
        "                        <ogc:Literal>something?</ogc:Literal>"                 + '\n' +
        "                    </ogc:PropertyIsLike>"                                     + '\n' +
        "                </ogc:Not>"                                                    + '\n' +
        "            </ogc:Filter>"                                                     + '\n' +
        "        </csw:Constraint>"                                                     + '\n' +
        "    </csw:Query>"                                                              + '\n' +
        "</csw:GetRecords>" + '\n';
        LOGGER.finer("RESULT:" + '\n' + result);
        
        //we remove the 2 first line because the xlmns are not always in the same order.
        expResult = expResult.substring(expResult.indexOf('\n') + 1);
        expResult = expResult.substring(expResult.indexOf('\n') + 1);
        
        result = result.substring(result.indexOf('\n') + 1);
        result = result.substring(result.indexOf('\n') + 1);
        
        LOGGER.finer("RESULT:" + '\n' + result);
        LOGGER.finer("EXPRESULT:" + '\n' + expResult);
        assertEquals(expResult, result);

         /*
         * Test marshalling csw getRecordByIdResponse v2.0.0
         */


        org.geotoolkit.csw.xml.v200.QueryConstraintType constraint200 = new org.geotoolkit.csw.xml.v200.QueryConstraintType(filter1, "1.1.0");
        typeNames  = new ArrayList<QName>();
        typeNames.add( org.geotoolkit.csw.xml.v200.ObjectFactory._Record_QNAME);
        org.geotoolkit.csw.xml.v200.QueryType query200 = new org.geotoolkit.csw.xml.v200.QueryType(typeNames, new org.geotoolkit.csw.xml.v200.ElementSetNameType(ElementSetType.FULL), constraint200);

        org.geotoolkit.csw.xml.v200.GetRecordsType getRecordsRequest200 = new org.geotoolkit.csw.xml.v200.GetRecordsType("CSW", "2.0.0", ResultType.RESULTS, null, "application/xml", "http://www.opengis.net/cat/csw", 1, 20, query200, null);


        sw = new StringWriter();
        recordMarshaller200.marshal(getRecordsRequest200, sw);

        result = sw.toString();


        expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<cat:GetRecords maxRecords=\"20\" startPosition=\"1\" outputSchema=\"http://www.opengis.net/cat/csw\" outputFormat=\"application/xml\" resultType=\"results\" version=\"2.0.0\" service=\"CSW\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:cat=\"http://www.opengis.net/cat/csw\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <cat:Query typeNames=\"cat:Record\">" + '\n' +
        "        <cat:ElementSetName>full</cat:ElementSetName>"                         + '\n' +
        "        <cat:Constraint version=\"1.1.0\">"                                    + '\n' +
        "            <ogc:Filter>"                                                      + '\n' +
        "                <ogc:Not>"                                                     + '\n' +
        "                    <ogc:PropertyIsLike wildCard=\"*\" singleChar=\"?\" escapeChar=\"\\\">"    + '\n' +
        "                        <ogc:PropertyName>dc:Title</ogc:PropertyName>"         + '\n' +
        "                        <ogc:Literal>something?</ogc:Literal>"                 + '\n' +
        "                    </ogc:PropertyIsLike>"                                     + '\n' +
        "                </ogc:Not>"                                                    + '\n' +
        "            </ogc:Filter>"                                                     + '\n' +
        "        </cat:Constraint>"                                                     + '\n' +
        "    </cat:Query>"                                                              + '\n' +
        "</cat:GetRecords>" + '\n';
        LOGGER.finer("RESULT:" + '\n' + result);

        //we remove the 2 first line because the xlmns are not always in the same order.
        expResult = expResult.substring(expResult.indexOf('\n') + 1);
        expResult = expResult.substring(expResult.indexOf('\n') + 1);

        result = result.substring(result.indexOf('\n') + 1);
        result = result.substring(result.indexOf('\n') + 1);

        LOGGER.finer("RESULT:" + '\n' + result);
        LOGGER.finer("EXPRESULT:" + '\n' + expResult);
        assertEquals(expResult, result);
    }
    
    /**
     * Test simple Record Marshalling. 
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void getRecordsUnMarshalingTest() throws JAXBException {
        
         /*
         * Test unmarshalling csw getRecordByIdResponse v2.0.2
         */
        
        String xml = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:GetRecords maxRecords=\"20\" startPosition=\"1\" outputSchema=\"http://www.opengis.net/cat/csw/2.0.2\" outputFormat=\"application/xml\" resultType=\"results\" version=\"2.0.2\" service=\"CSW\"  xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <csw:Query typeNames=\"csw:Record\">" + '\n' +
        "        <csw:ElementSetName>full</csw:ElementSetName>"                         + '\n' +
        "        <csw:Constraint version=\"1.1.0\">"                                    + '\n' +
        "            <ogc:Filter>"                                                      + '\n' +
        "                <ogc:Not>"                                                     + '\n' +
        "                    <ogc:PropertyIsLike wildCard=\"*\" singleChar=\"?\" escapeChar=\"\\\">"    + '\n' +
        "                        <ogc:PropertyName>dc:Title</ogc:PropertyName>"         + '\n' +
        "                        <ogc:Literal>something?</ogc:Literal>"                 + '\n' +
        "                    </ogc:PropertyIsLike>"                                     + '\n' +
        "                </ogc:Not>"                                                    + '\n' +
        "            </ogc:Filter>"                                                     + '\n' +
        "        </csw:Constraint>"                                                     + '\n' +
        "    </csw:Query>"                                                              + '\n' +
        "</csw:GetRecords>" + '\n';
        
        StringReader sr = new StringReader(xml);
        
        Object result = recordUnmarshaller202.unmarshal(sr);
        
        /*
         * we build the first filter : < dublinCore:Title IS LIKE '*' >
         */ 
        List<QName> typeNames  = new ArrayList<QName>();
        PropertyNameType pname = new PropertyNameType("dc:Title");
        PropertyIsLikeType pil = new PropertyIsLikeType(pname, "something?", "*", "?", "\\");
        NotType n              = new NotType(pil);
        FilterType filter1     = new FilterType(n);
        
        QueryConstraintType constraint = new QueryConstraintType(filter1, "1.1.0");
        typeNames.add(_Record_QNAME);
        QueryType query = new QueryType(typeNames, new ElementSetNameType(ElementSetType.FULL), null, constraint); 
        
        GetRecordsType expResult = new GetRecordsType("CSW", "2.0.2", ResultType.RESULTS, null, "application/xml", "http://www.opengis.net/cat/csw/2.0.2", 1, 20, query, null);
         
        
        
        LOGGER.finer("RESULT:" + '\n' + result);
        LOGGER.finer("EXPRESULT:" + '\n' + expResult);
        GetRecordsType gres = (GetRecordsType)result;
        QueryType expQT = (QueryType) expResult.getAbstractQuery();
        QueryType resQT = (QueryType) gres.getAbstractQuery();

        assertEquals(expQT.getConstraint().getFilter().getLogicOps().getValue(), resQT.getConstraint().getFilter().getLogicOps().getValue());
        assertEquals(expQT.getConstraint().getFilter(), resQT.getConstraint().getFilter());
        assertEquals(expQT.getConstraint(), resQT.getConstraint());
        assertEquals(expResult.getAbstractQuery(), gres.getAbstractQuery());
        assertEquals(expResult, result);

        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<cat:GetRecords maxRecords=\"20\" startPosition=\"1\" outputSchema=\"http://www.opengis.net/cat/csw\" outputFormat=\"application/xml\" resultType=\"results\" version=\"2.0.0\" service=\"CSW\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:cat=\"http://www.opengis.net/cat/csw\" xmlns:dct=\"http://purl.org/dc/terms/\">" + '\n' +
        "    <cat:Query typeNames=\"cat:Record\">" + '\n' +
        "        <cat:ElementSetName>full</cat:ElementSetName>"                         + '\n' +
        "        <cat:Constraint version=\"1.1.0\">"                                    + '\n' +
        "            <ogc:Filter>"                                                      + '\n' +
        "                <ogc:Not>"                                                     + '\n' +
        "                    <ogc:PropertyIsLike wildCard=\"*\" singleChar=\"?\" escapeChar=\"\\\">"    + '\n' +
        "                        <ogc:PropertyName>dc:Title</ogc:PropertyName>"         + '\n' +
        "                        <ogc:Literal>something?</ogc:Literal>"                 + '\n' +
        "                    </ogc:PropertyIsLike>"                                     + '\n' +
        "                </ogc:Not>"                                                    + '\n' +
        "            </ogc:Filter>"                                                     + '\n' +
        "        </cat:Constraint>"                                                     + '\n' +
        "    </cat:Query>"                                                              + '\n' +
        "</cat:GetRecords>" + '\n';

        org.geotoolkit.csw.xml.v200.QueryConstraintType constraint200 = new org.geotoolkit.csw.xml.v200.QueryConstraintType(filter1, "1.1.0");
        typeNames  = new ArrayList<QName>();
        typeNames.add( org.geotoolkit.csw.xml.v200.ObjectFactory._Record_QNAME);
        org.geotoolkit.csw.xml.v200.QueryType query200 = new org.geotoolkit.csw.xml.v200.QueryType(typeNames, new org.geotoolkit.csw.xml.v200.ElementSetNameType(ElementSetType.FULL), constraint200);

        org.geotoolkit.csw.xml.v200.GetRecordsType expResult200 = new org.geotoolkit.csw.xml.v200.GetRecordsType("CSW", "2.0.0", ResultType.RESULTS, null, "application/xml", "http://www.opengis.net/cat/csw", 1, 20, query200, null);

        sr = new StringReader(xml);

        result = recordUnmarshaller200.unmarshal(sr);

        assertTrue(result instanceof JAXBElement);

        org.geotoolkit.csw.xml.v200.GetRecordsType result200 = (org.geotoolkit.csw.xml.v200.GetRecordsType) ((JAXBElement)result).getValue();

        assertEquals(expResult200.getAbstractQuery(), result200.getAbstractQuery());
        assertEquals(expResult200, result200);
    }


    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void updateMarshalingTest() throws JAXBException {

        // <TODO
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<SimpleLiteral>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        SimpleLiteral modified   = new SimpleLiteral("2007-11-15 21:26:49");
        SimpleLiteral Abstract   = new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.");
        SimpleLiteral references = new SimpleLiteral("http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml");
        SimpleLiteral spatial    = new SimpleLiteral("northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;");

        List<BoundingBoxType> bbox = new ArrayList<BoundingBoxType>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        RecordType record = new RecordType(id, title, type, subject, null, modified, null, Abstract, bbox, null, null, null, spatial, references);

        QueryConstraintType query = new QueryConstraintType("identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'", "1.1.0");
        UpdateType update = new UpdateType(record, query);

        TransactionType request = new TransactionType("CSW", "2.0.2", update);

        //recordMarshaller202.marshal(request, System.out);

        // TODO/>

        /**
         * Test 2 : Simple recordProperty (String)
         */
        RecordPropertyType recordProperty = new RecordPropertyType("/csw:Record/dc:contributor", "Jane");
        query = new QueryConstraintType("identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'", "1.1.0");
        update = new UpdateType(Arrays.asList(recordProperty), query);
        request = new TransactionType("CSW", "2.0.2", update);

         String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:Transaction verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" >"             + '\n' +
        "    <csw:Update>"                                                                           + '\n' +
        "        <csw:RecordProperty>"                                                               + '\n' +
        "            <csw:Name>/csw:Record/dc:contributor</csw:Name>"                                + '\n' +
        "            <csw:Value xsi:type=\"xsd:string\" >Jane</csw:Value>"                           + '\n' +
        "        </csw:RecordProperty>"                                                              + '\n' +
        "        <csw:Constraint version=\"1.1.0\">"                                                 + '\n' +
        "            <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>" + '\n' +
        "        </csw:Constraint>"                                                                  + '\n' +
        "    </csw:Update>"                                                                          + '\n' +
        "</csw:Transaction>"+ '\n';
         
        StringWriter sw = new StringWriter();
        recordMarshaller202.marshal(request, sw);
        
        String result = sw.toString();

        result = removeXmlns(result);

        assertEquals(expResult, result);

        /**
         * Test 3 : Complex recordProperty (GeographicBoundingBox)
         */
        DefaultGeographicBoundingBox geographicElement = new DefaultGeographicBoundingBox(1.1, 1.1, 1.1, 1.1);
        recordProperty = new RecordPropertyType("/gmd:MD_Metadata/identificationInfo/extent/geographicElement", geographicElement);
        query = new QueryConstraintType("identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'", "1.1.0");
        update = new UpdateType(Arrays.asList(recordProperty), query);
        request = new TransactionType("CSW", "2.0.2", update);

        expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:Transaction verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" >"             + '\n' +
        "    <csw:Update>"                                                                           + '\n' +
        "        <csw:RecordProperty>"                                                               + '\n' +
        "            <csw:Name>/gmd:MD_Metadata/identificationInfo/extent/geographicElement</csw:Name>" + '\n' +
        "            <csw:Value xsi:type=\"gmd:EX_GeographicBoundingBox\" >"                         + '\n' +
        "                <gmd:extentTypeCode>"                                                       + '\n' +
        "                    <gco:Boolean>true</gco:Boolean>"                                        + '\n' +
        "                </gmd:extentTypeCode>"                                                      + '\n' +
        "                <gmd:westBoundLongitude>"                                                   + '\n' +
        "                    <gco:Decimal>1.1</gco:Decimal>"                                         + '\n' +
        "                </gmd:westBoundLongitude>"                                                  + '\n' +
        "                <gmd:eastBoundLongitude>"                                                   + '\n' +
        "                    <gco:Decimal>1.1</gco:Decimal>"                                         + '\n' +
        "                </gmd:eastBoundLongitude>"                                                  + '\n' +
        "                <gmd:southBoundLatitude>"                                                   + '\n' +
        "                    <gco:Decimal>1.1</gco:Decimal>"                                         + '\n' +
        "                </gmd:southBoundLatitude>"                                                  + '\n' +
        "                <gmd:northBoundLatitude>"                                                   + '\n' +
        "                    <gco:Decimal>1.1</gco:Decimal>"                                         + '\n' +
        "                </gmd:northBoundLatitude>"                                                  + '\n' +
        "            </csw:Value>"                                                                   + '\n' +
        "        </csw:RecordProperty>"                                                              + '\n' +
        "        <csw:Constraint version=\"1.1.0\">"                                                 + '\n' +
        "            <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>" + '\n' +
        "        </csw:Constraint>"                                                                  + '\n' +
        "    </csw:Update>"                                                                          + '\n' +
        "</csw:Transaction>"+ '\n';

        sw = new StringWriter();
        recordMarshaller202.marshal(request, sw);

        result = sw.toString();

        result = removeXmlns(result);

        assertEquals(expResult, result);

    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void updateUnmarshalingTest() throws JAXBException {

        /**
         * Test 1 : Simple recordProperty (String)
         */


        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:Transaction xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" >"             + '\n' +
        "    <csw:Update>"                                                                           + '\n' +
        "        <csw:RecordProperty>"                                                               + '\n' +
        "            <csw:Name>/csw:Record/dc:contributor</csw:Name>"                                + '\n' +
        "            <csw:Value xsi:type=\"xs:string\" >Jane</csw:Value>"                           + '\n' +
        "        </csw:RecordProperty>"                                                              + '\n' +
        "        <csw:Constraint version=\"1.1.0\">"                                                 + '\n' +
        "            <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>" + '\n' +
        "        </csw:Constraint>"                                                                  + '\n' +
        "    </csw:Update>"                                                                          + '\n' +
        "</csw:Transaction>"+ '\n';

        TransactionType result = (TransactionType) recordUnmarshaller202.unmarshal(new StringReader(xml));

        RecordPropertyType recordProperty = new RecordPropertyType("/csw:Record/dc:contributor", "Jane");
        QueryConstraintType query         = new QueryConstraintType("identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'", "1.1.0");
        UpdateType update                 = new UpdateType(Arrays.asList(recordProperty), query);
        TransactionType expResult         = new TransactionType("CSW", "2.0.2", update);

        assertEquals(expResult, result);


        xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:Transaction verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:gco=\"http://www.isotc211.org/2005/gco\">" + '\n' +
        "    <csw:Update>"                                                                           + '\n' +
        "        <csw:RecordProperty>"                                                               + '\n' +
        "            <csw:Name>/gmd:MD_Metadata/identificationInfo/extent/geographicElement</csw:Name>" + '\n' +
        "            <csw:Value xsi:type=\"gmd:EX_GeographicBoundingBox\" >"                         + '\n' +
        "                <gmd:extentTypeCode>"                                                       + '\n' +
        "                    <gco:Boolean>true</gco:Boolean>"                                        + '\n' +
        "                </gmd:extentTypeCode>"                                                      + '\n' +
        "                <gmd:westBoundLongitude>"                                                   + '\n' +
        "                    <gco:Decimal>1.1</gco:Decimal>"                                         + '\n' +
        "                </gmd:westBoundLongitude>"                                                  + '\n' +
        "                <gmd:eastBoundLongitude>"                                                   + '\n' +
        "                    <gco:Decimal>1.1</gco:Decimal>"                                         + '\n' +
        "                </gmd:eastBoundLongitude>"                                                  + '\n' +
        "                <gmd:southBoundLatitude>"                                                   + '\n' +
        "                    <gco:Decimal>1.1</gco:Decimal>"                                         + '\n' +
        "                </gmd:southBoundLatitude>"                                                  + '\n' +
        "                <gmd:northBoundLatitude>"                                                   + '\n' +
        "                    <gco:Decimal>1.1</gco:Decimal>"                                         + '\n' +
        "                </gmd:northBoundLatitude>"                                                  + '\n' +
        "            </csw:Value>"                                                                   + '\n' +
        "        </csw:RecordProperty>"                                                              + '\n' +
        "        <csw:Constraint version=\"1.1.0\">"                                                 + '\n' +
        "            <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>" + '\n' +
        "        </csw:Constraint>"                                                                  + '\n' +
        "    </csw:Update>"                                                                          + '\n' +
        "</csw:Transaction>"+ '\n';

        result = (TransactionType) recordUnmarshaller202.unmarshal(new StringReader(xml));
        
        DefaultGeographicBoundingBox geographicElement = new DefaultGeographicBoundingBox(1.1, 1.1, 1.1, 1.1);
        recordProperty = new RecordPropertyType("/gmd:MD_Metadata/identificationInfo/extent/geographicElement", geographicElement);
        query          = new QueryConstraintType("identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'", "1.1.0");
        update         = new UpdateType(Arrays.asList(recordProperty), query);
        expResult      = new TransactionType("CSW", "2.0.2", update);

        assertEquals(expResult, result);

    }

    /**
     * Test capabilities with INSPIRE extendedCapabilities unmarshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void InspireUnmarshalingTest() throws Exception {

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:Capabilities version=\"2.0.2\"  xmlns:ows=\"http://www.opengis.net/ows\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\">"  + '\n' +
        "   <ows:OperationsMetadata>"                                                                                                        + '\n' +
        "       <ows:ExtendedCapabilities>"                                                                                                  + '\n' +
        "           <ins:MultiLingualCapabilities xmlns:ins=\"http://www.inspire.org\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">"         + '\n' +
        "               <ins:Languages>"                                                                                                     + '\n' +
        "                   <ins:Language>GER</ins:Language>"                                                                                + '\n' +
        "                   <ins:Language>DUT</ins:Language>"                                                                                + '\n' +
        "               </ins:Languages>"                                                                                                    + '\n' +
        "               <ins:TranslatedCapabilities>"                                                                                        + '\n' +
        "                   <ins:Document xlink:href=\"http://www.somehost.com/capabilities_german.xml\" language=\"GER\"/>"                 + '\n' +
        "                   <ins:Document xlink:href=\"http://www.somehost.com/capabilities_dutch.xml\"  language=\"DUT\"/>"                 + '\n' +
        "               </ins:TranslatedCapabilities>"                                                                                       + '\n' +
        "          </ins:MultiLingualCapabilities>"                                                                                           + '\n' +
        "      </ows:ExtendedCapabilities>"                                                                                                  + '\n' +
        "   </ows:OperationsMetadata>"                                                                                                       + '\n' +
        "</csw:Capabilities>"+ '\n';

        Capabilities result = (Capabilities) recordUnmarshaller202.unmarshal(new StringReader(xml));

        OperationsMetadata om = new OperationsMetadata();

        LanguagesType languages = new LanguagesType(Arrays.asList("GER", "DUT"));
        List<DocumentType> docs = Arrays.asList(new DocumentType("http://www.somehost.com/capabilities_german.xml", "GER"),
                                                new DocumentType("http://www.somehost.com/capabilities_dutch.xml", "DUT"));
        TranslatedCapabilitiesType trans = new TranslatedCapabilitiesType(docs);
        InspireCapabilitiesType inspireCapa = new InspireCapabilitiesType(languages, trans);
        MultiLingualCapabilities m = new MultiLingualCapabilities();
        m.setMultiLingualCapabilities(inspireCapa);
        om.setExtendedCapabilities(m);

        Capabilities expResult = new Capabilities(null, null, om, "2.0.2", null, null);

        assertEquals(expResult, result);
    }

    /**
     * Test capabilities with INSPIRE extendedCapabilities unmarshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void InspireMarshalingTest() throws Exception {

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                                        + '\n' +
        "<csw:Capabilities version=\"2.0.2\" >"                                                                                + '\n' +
        "    <ows:OperationsMetadata>"                                                                                         + '\n' +
        "        <ows:ExtendedCapabilities>"                                                                                   + '\n' +
        "            <ins:MultiLingualCapabilities>"                                                                           + '\n' +
        "                <ins:Languages>"                                                                                      + '\n' +
        "                    <ins:Language>GER</ins:Language>"                                                                 + '\n' +
        "                    <ins:Language>DUT</ins:Language>"                                                                 + '\n' +
        "                </ins:Languages>"                                                                                     + '\n' +
        "                <ins:TranslatedCapabilities>"                                                                         + '\n' +
        "                    <ins:Document language=\"GER\" xlink:href=\"http://www.somehost.com/capabilities_german.xml\"/>"  + '\n' +
        "                    <ins:Document language=\"DUT\" xlink:href=\"http://www.somehost.com/capabilities_dutch.xml\"/>"   + '\n' +
        "                </ins:TranslatedCapabilities>"                                                                        + '\n' +
        "            </ins:MultiLingualCapabilities>"                                                                          + '\n' +
        "        </ows:ExtendedCapabilities>"                                                                                  + '\n' +
        "    </ows:OperationsMetadata>"                                                                                        + '\n' +
        "</csw:Capabilities>"+ '\n';

        OperationsMetadata om = new OperationsMetadata();

        LanguagesType languages = new LanguagesType(Arrays.asList("GER", "DUT"));
        List<DocumentType> docs = Arrays.asList(new DocumentType("http://www.somehost.com/capabilities_german.xml", "GER"),
                                                new DocumentType("http://www.somehost.com/capabilities_dutch.xml", "DUT"));
        TranslatedCapabilitiesType trans = new TranslatedCapabilitiesType(docs);
        InspireCapabilitiesType inspireCapa = new InspireCapabilitiesType(languages, trans);
        MultiLingualCapabilities m = new MultiLingualCapabilities();
        m.setMultiLingualCapabilities(inspireCapa);
        om.setExtendedCapabilities(m);

        Capabilities capa = new Capabilities(null, null, om, "2.0.2", null, null);

        StringWriter sw = new StringWriter();
        recordMarshaller202.marshal(capa, sw);

        String result = sw.toString();

        result = removeXmlns(result);

        assertEquals(expResult, result);
    }

    public String removeXmlns(String xml) {

        String s = xml;
        s = s.replaceAll("xmlns=\"[^\"]*\" ", "");

        s = s.replaceAll("xmlns=\"[^\"]*\"", "");

        s = s.replaceAll("xmlns:[^=]*=\"[^\"]*\" ", "");

        s = s.replaceAll("xmlns:[^=]*=\"[^\"]*\"", "");


        return s;
    }
}
