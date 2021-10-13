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
import java.io.IOException;
import java.util.logging.Level;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

// JAXB dependencies
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

// GeotoolKit dependencies
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
import org.geotoolkit.csw.xml.v202.GetRecordsResponseType;
import org.geotoolkit.csw.xml.v202.InsertResultType;
import org.geotoolkit.csw.xml.v202.SearchResultsType;
import org.geotoolkit.csw.xml.v202.TransactionResponseType;
import org.geotoolkit.csw.xml.v202.TransactionSummaryType;

import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.xml.MarshallerPool;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.apache.sis.test.MetadataAssert.*;


/**
 * A Test suite verifying that the Record are correctly marshalled/unmarshalled
 *
 * @author Guilhem Legal
 * @module
 */
public class CswXMLBindingTest extends org.geotoolkit.test.TestBase {
    @BeforeClass
    public static void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
    }

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.csw.xml");

    private final MarshallerPool pool = CSWMarshallerPool.getInstance();

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

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void recordMarshalingTest() throws JAXBException {
        Marshaller marshaller = pool.acquireMarshaller();
        /*
         * Test marshalling csw Record v2.0.2
         */
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        SimpleLiteral modified   = new SimpleLiteral("2007-11-15 21:26:49");
        SimpleLiteral Abstract   = new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.");
        SimpleLiteral references = new SimpleLiteral("http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml");
        SimpleLiteral spatial    = new SimpleLiteral("northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;");

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        RecordType record = new RecordType(id, title, type, subject, null, modified, null, Abstract, bbox, null, null, null, spatial, references);

        StringWriter sw = new StringWriter();
        marshaller.marshal(record, sw);
        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:Record xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">\n" +
        "  <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "  <dc:title>(JASON-1)</dc:title>\n" +
        "  <dc:type>clearinghouse</dc:type>\n" +
        "  <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "  <dc:subject>oceans elevation 2</dc:subject>\n" +
        "  <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "  <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "  <dct:references>http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml</dct:references>\n" +
        "  <dct:spatial>northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;</dct:spatial>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "</csw:Record>\n";

        assertXmlEquals(expResult, result, "xmlns:*");
        pool.recycle(marshaller);
    }

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void recordUnmarshalingTest() throws JAXBException {
        Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        /*
         * Test Unmarshalling csw Record v2.0.2
         */
        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:Record xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">\n" +
        "  <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "  <dc:title>(JASON-1)</dc:title>\n" +
        "  <dc:type>clearinghouse</dc:type>\n" +
        "  <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "  <dc:subject>oceans elevation 2</dc:subject>\n" +
        "  <dc:format>binary</dc:format>\n" +
        "  <dc:date>2007-12-01</dc:date>\n" +
        "  <dc:publisher>geomatys</dc:publisher>\n" +
        "  <dc:creator>geomatys</dc:creator>\n" +
        "  <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "  <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "  <dct:spatial>northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;</dct:spatial>\n" +
        "  <dct:references>http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml</dct:references>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "</csw:Record>\n";

        StringReader sr = new StringReader(xml);
        RecordType result = (RecordType) unmarshaller.unmarshal(sr);

        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<>();
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

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        RecordType expResult = new RecordType(id, title, type, subject, format, modified, date, Abstract, bbox, creator, distributor, null, spatial, references);

        assertEquals(expResult.getDate(), result.getDate());
        assertEquals(expResult.getAbstract(), result.getAbstract());
        assertEquals(expResult.getSpatial(), result.getSpatial());
        assertEquals(expResult.getBoundingBox().get(0).getValue(), result.getBoundingBox().get(0).getValue());
        assertEquals(expResult, result);
        /*
         * Test Unmarshalling csw Record v2.0.0 with http://purl... DC namespace
         */
        xml =
        "<csw:Record xmlns:csw=\"http://www.opengis.net/cat/csw\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
        "  <dc:identifier>ESIGNGRAVIMÉTRICOPENINSULAYBALEARES200703070000</dc:identifier>\n" +
        "  <dc:title>Estudio Gravimétrico de la Península Ibérica y Baleares</dc:title>\n" +
        "  <dc:title>Mapa de Anomalías Gravimétricas</dc:title>\n" +
        "  <dc:creator>Instituto Geográfico Nacional</dc:creator>\n" +
        "  <dc:subject>http://www.fao.org/aos/concept#4668.Gravimetría</dc:subject>\n" +
        "  <dc:subject>Anomalías Gravimétricas</dc:subject>\n" +
        "  <dc:subject>Anomalías Aire Libre</dc:subject>\n" +
        "  <dc:subject>Anomalías Bouguer</dc:subject>\n" +
        "  <dc:subject>Información geocientífica</dc:subject>\n" +
        "  <dc:description>El Estudio Gravimétrico de la Península Ibérica y Baleares, que representa las anomalías gravimétricas de esa zona, fue generado por el Instituto Geográfico Nacional en el año 1996. El estudio está constituido por dos mapas; Anomalías Gravimétricas Aire Libre de la Península Ibérica y Baleares, Anomalías Gravimétricas Bouguer de la Península Ibérica y Baleares más una memoria. Inicialmente para su generación se creó una base de datos gravimétrica homogénea a partir de observaciones de distinta procedencia, también se formó un modelo digital del terreno homogéneo a partir de otros modelos digitales del terreno procedentes de España, Portugal y Francia. Los mapas contienen isolíneas de anomalías gravimétricas en intervalos de 2mGal. Los datos se almacenan en formato DGN.</dc:description>\n" +
        "  <dc:date>2007-03-07</dc:date>\n" +
        "  <dc:type>mapHardcopy</dc:type>\n" +
        "  <dc:type>mapDigital</dc:type>\n" +
        "  <dc:type>documentHardcopy</dc:type>\n" +
        "  <dc:format>DGN - Microstation format (Intergraph Corporation)</dc:format>\n" +
        "  <dc:format>Papel</dc:format>\n" +
        "  <dc:identifier>www.cnig.es</dc:identifier>\n" +
        "  <dc:source>El Banco de Datos Gravimétricos es una base de datos compuesta principalmente por las observaciones realizadas por el Instituto Geográfico Nacional desde 1960. Además se han añadido los datos del Instituto Portugués de Geografía y Catastro, del proyecto ECORS, de la Universidad de Cantabria y del Bureau Gravimétrico Internacional.</dc:source>\n" +
        "  <dc:source>Para la creación del Modelo Digital del Terreno a escala 1:200.000 para toda la Península Ibérica, áreas marinas y terrestres adyacentes, en particular, se ha dispuesto de la siguiente información; Modelo Digital del Terreno a escala 1:200.000, Modelo Digital del Terreno obtenido del Defense Mapping Agency de los Estados Unidos, para completar la zona de Portugal; Modelo Digital del Terreno obtenido del Instituto Geográfico Nacional de Francia para la parte francesa del Pirineo y el Modelo Digital del Terreno generado a partir de las cartas náuticas del Instituto Hidrográfico de la Marina de España, que completa la parte marina hasta los 167 km. de la costa con un ancho de malla de 5 km.</dc:source>\n" +
        "  <dc:language>es</dc:language>\n" +
        "  <dcterms:spatial>northlimit=43.83; southlimit=36.00; westlimit=-9.35; eastlimit=4.32;</dcterms:spatial>\n" +
        "  <dcterms:spatial>ESPAÑA.ANDALUCÍA</dcterms:spatial>\n" +
        "  <dcterms:spatial>ESPAÑA.ARAGÓN</dcterms:spatial>\n" +
        "</csw:Record>";

        sr = new StringReader(xml);
        org.geotoolkit.csw.xml.v200.RecordType result2 = (org.geotoolkit.csw.xml.v200.RecordType) unmarshaller.unmarshal(sr);
        LOGGER.log(Level.FINER, "result:{0}", result2.toString());
        /*
         * Test Unmarshalling csw Record v2.0.0 with http://www.purl... DC namespace
         */
        xml =
        "<csw:Record xmlns:csw=\"http://www.opengis.net/cat/csw\" xmlns:dcterms=\"http://www.purl.org/dc/terms/\" xmlns:dc=\"http://www.purl.org/dc/elements/1.1/\">\n" +
        "  <dc:identifier>ESIGNGRAVIMÉTRICOPENINSULAYBALEARES200703070000</dc:identifier>\n" +
        "  <dc:title>Estudio Gravimétrico de la Península Ibérica y Baleares</dc:title>\n" +
        "  <dc:title>Mapa de Anomalías Gravimétricas</dc:title>\n" +
        "  <dc:creator>Instituto Geográfico Nacional</dc:creator>\n" +
        "  <dc:subject>http://www.fao.org/aos/concept#4668.Gravimetría</dc:subject>\n" +
        "  <dc:subject>Anomalías Gravimétricas</dc:subject>\n" +
        "  <dc:subject>Anomalías Aire Libre</dc:subject>\n" +
        "  <dc:subject>Anomalías Bouguer</dc:subject>\n" +
        "  <dc:subject>Información geocientífica</dc:subject>\n" +
        "  <dc:description>El Estudio Gravimétrico de la Península Ibérica y Baleares, que representa las anomalías gravimétricas de esa zona, fue generado por el Instituto Geográfico Nacional en el año 1996. El estudio está constituido por dos mapas; Anomalías Gravimétricas Aire Libre de la Península Ibérica y Baleares, Anomalías Gravimétricas Bouguer de la Península Ibérica y Baleares más una memoria. Inicialmente para su generación se creó una base de datos gravimétrica homogénea a partir de observaciones de distinta procedencia, también se formó un modelo digital del terreno homogéneo a partir de otros modelos digitales del terreno procedentes de España, Portugal y Francia. Los mapas contienen isolíneas de anomalías gravimétricas en intervalos de 2mGal. Los datos se almacenan en formato DGN.</dc:description>\n" +
        "  <dc:date>2007-03-07</dc:date>\n" +
        "  <dc:type>mapHardcopy</dc:type>\n" +
        "  <dc:type>mapDigital</dc:type>\n" +
        "  <dc:type>documentHardcopy</dc:type>\n" +
        "  <dc:format>DGN - Microstation format (Intergraph Corporation)</dc:format>\n" +
        "  <dc:format>Papel</dc:format>\n" +
        "  <dc:identifier>www.cnig.es</dc:identifier>\n" +
        "  <dc:source>El Banco de Datos Gravimétricos es una base de datos compuesta principalmente por las observaciones realizadas por el Instituto Geográfico Nacional desde 1960. Además se han añadido los datos del Instituto Portugués de Geografía y Catastro, del proyecto ECORS, de la Universidad de Cantabria y del Bureau Gravimétrico Internacional.</dc:source>\n" +
        "  <dc:source>Para la creación del Modelo Digital del Terreno a escala 1:200.000 para toda la Península Ibérica, áreas marinas y terrestres adyacentes, en particular, se ha dispuesto de la siguiente información; Modelo Digital del Terreno a escala 1:200.000, Modelo Digital del Terreno obtenido del Defense Mapping Agency de los Estados Unidos, para completar la zona de Portugal; Modelo Digital del Terreno obtenido del Instituto Geográfico Nacional de Francia para la parte francesa del Pirineo y el Modelo Digital del Terreno generado a partir de las cartas náuticas del Instituto Hidrográfico de la Marina de España, que completa la parte marina hasta los 167 km. de la costa con un ancho de malla de 5 km.</dc:source>\n" +
        "  <dc:language>es</dc:language>\n" +
        "  <dcterms:spatial>northlimit=43.83; southlimit=36.00; westlimit=-9.35; eastlimit=4.32;</dcterms:spatial>\n" +
        "  <dcterms:spatial>ESPAÑA.ANDALUCÍA</dcterms:spatial>\n" +
        "  <dcterms:spatial>ESPAÑA.ARAGÓN</dcterms:spatial>\n" +
        "</csw:Record>";

        sr = new StringReader(xml);
        result2 = (org.geotoolkit.csw.xml.v200.RecordType) unmarshaller.unmarshal(sr);
        LOGGER.log(Level.FINER, "result:{0}", result2.toString());
        pool.recycle(unmarshaller);
    }

    /**
     * Test summary Record Marshalling.
     */
    @Test
    public void summmaryRecordMarshalingTest() throws JAXBException {
        Marshaller marshaller = pool.acquireMarshaller();
        /*
         * Test marshalling csw summmary Record v2.0.2
         */
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        List<SimpleLiteral> formats = new ArrayList<>();
        formats.add(new SimpleLiteral("format 11-11"));
        formats.add(new SimpleLiteral("format 22-22"));

        SimpleLiteral modified         = new SimpleLiteral("2007-11-15 21:26:49");
        List<SimpleLiteral> Abstract   = new ArrayList<>();
        Abstract.add(new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm."));

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        SummaryRecordType record = new SummaryRecordType(id, title, type,  bbox, subject, formats, modified, Abstract);

        StringWriter sw = new StringWriter();
        marshaller.marshal(record, sw);
        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:SummaryRecord xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">\n" +
        "  <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "  <dc:title>(JASON-1)</dc:title>\n" +
        "  <dc:type>clearinghouse</dc:type>\n" +
        "  <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "  <dc:subject>oceans elevation 2</dc:subject>\n" +
        "  <dc:format>format 11-11</dc:format>\n" +
        "  <dc:format>format 22-22</dc:format>\n" +
        "  <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "  <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "</csw:SummaryRecord>\n";

        assertXmlEquals(expResult, result, "xmlns:*");
        /*
         * Test marshalling csw summmary Record v2.0.2
         */
        List<SimpleLiteral> ids    = new ArrayList<>();
        ids.add(new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}"));
        ids.add(new SimpleLiteral("urn:ogc-x:df:F7807C8AB645"));
        List<SimpleLiteral> titles = new ArrayList<>();
        titles.add(new SimpleLiteral("(JASON-1)"));
        titles.add(new SimpleLiteral("(JASON-2)"));

        type = new SimpleLiteral("clearinghouse");

        subject = new ArrayList<>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        formats = new ArrayList<>();
        formats.add(new SimpleLiteral("format 11-11"));
        formats.add(new SimpleLiteral("format 22-22"));

        List<SimpleLiteral> modifieds   = new ArrayList<>();
        modifieds.add(new SimpleLiteral("2007-11-15 21:26:49"));
        modifieds.add(new SimpleLiteral("2007-11-15 21:26:48"));
        Abstract   = new ArrayList<>();
        Abstract.add(new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm."));
        Abstract.add(new SimpleLiteral("Jason-2 blablablablabla."));

        bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));
        bbox.add(new WGS84BoundingBoxType(100, -6.04, -144, 5.9));

        record = new SummaryRecordType(ids, titles, type,  bbox, subject, formats, modifieds, Abstract);

        sw = new StringWriter();
        marshaller.marshal(record, sw);
        result = sw.toString();

        expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:SummaryRecord xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">\n" +
        "  <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "  <dc:identifier>urn:ogc-x:df:F7807C8AB645</dc:identifier>\n" +
        "  <dc:title>(JASON-1)</dc:title>\n" +
        "  <dc:title>(JASON-2)</dc:title>\n" +
        "  <dc:type>clearinghouse</dc:type>\n" +
        "  <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "  <dc:subject>oceans elevation 2</dc:subject>\n" +
        "  <dc:format>format 11-11</dc:format>\n" +
        "  <dc:format>format 22-22</dc:format>\n" +
        "  <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "  <dct:modified>2007-11-15 21:26:48</dct:modified>\n" +
        "  <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "  <dct:abstract>Jason-2 blablablablabla.</dct:abstract>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>100.0 -6.04</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-144.0 5.9</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "</csw:SummaryRecord>\n";

        assertXmlEquals(expResult, result, "xmlns:*");
        pool.recycle(marshaller);
    }

    /**
     * Test summary Record Marshalling.
     */
    @Test
    public void summmaryRecordUnmarshalingTest() throws JAXBException {
        Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        /*
         * Test marshalling csw summmary Record v2.0.2
         */
        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:SummaryRecord xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">\n" +
        "  <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "  <dc:title>(JASON-1)</dc:title>\n" +
        "  <dc:type>clearinghouse</dc:type>\n" +
        "  <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "  <dc:subject>oceans elevation 2</dc:subject>\n" +
        "  <dc:format>format 11-11</dc:format>\n" +
        "  <dc:format>format 22-22</dc:format>\n" +
        "  <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "  <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "</csw:SummaryRecord>\n";

        StringReader sr = new StringReader(xml);
        SummaryRecordType result = (SummaryRecordType) unmarshaller.unmarshal(sr);

        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        List<SimpleLiteral> formats = new ArrayList<>();
        formats.add(new SimpleLiteral("format 11-11"));
        formats.add(new SimpleLiteral("format 22-22"));

        SimpleLiteral modified         = new SimpleLiteral("2007-11-15 21:26:49");
        List<SimpleLiteral> Abstract   = new ArrayList<>();
        Abstract.add(new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm."));

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        SummaryRecordType expResult = new SummaryRecordType(id, title, type,  bbox, subject, formats, modified, Abstract);
        assertEquals(expResult, result);
        /*
         * Test marshalling csw summmary Record v2.0.2
         */
        xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:SummaryRecord xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">\n" +
        "  <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "  <dc:identifier>urn:ogc-x:df:F7807C8AB645</dc:identifier>\n" +
        "  <dc:title>(JASON-1)</dc:title>\n" +
        "  <dc:title>(JASON-2)</dc:title>\n" +
        "  <dc:type>clearinghouse</dc:type>\n" +
        "  <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "  <dc:subject>oceans elevation 2</dc:subject>\n" +
        "  <dc:format>format 11-11</dc:format>\n" +
        "  <dc:format>format 22-22</dc:format>\n" +
        "  <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "  <dct:modified>2007-11-15 21:26:48</dct:modified>\n" +
        "  <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "  <dct:abstract>Jason-2 blablablablabla.</dct:abstract>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>100.0 -6.04</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-144.0 5.9</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "</csw:SummaryRecord>\n";

        sr = new StringReader(xml);
        result = (SummaryRecordType) unmarshaller.unmarshal(sr);

        List<SimpleLiteral> ids    = new ArrayList<>();
        ids.add(new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}"));
        ids.add(new SimpleLiteral("urn:ogc-x:df:F7807C8AB645"));
        List<SimpleLiteral> titles = new ArrayList<>();
        titles.add(new SimpleLiteral("(JASON-1)"));
        titles.add(new SimpleLiteral("(JASON-2)"));

        type = new SimpleLiteral("clearinghouse");

        subject = new ArrayList<>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        formats = new ArrayList<>();
        formats.add(new SimpleLiteral("format 11-11"));
        formats.add(new SimpleLiteral("format 22-22"));

        List<SimpleLiteral> modifieds   = new ArrayList<>();
        modifieds.add(new SimpleLiteral("2007-11-15 21:26:49"));
        modifieds.add(new SimpleLiteral("2007-11-15 21:26:48"));
        Abstract   = new ArrayList<>();
        Abstract.add(new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm."));
        Abstract.add(new SimpleLiteral("Jason-2 blablablablabla."));

        bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));
        bbox.add(new WGS84BoundingBoxType(100, -6.04, -144, 5.9));

        expResult = new SummaryRecordType(ids, titles, type,  bbox, subject, formats, modifieds, Abstract);

        assertEquals(expResult, result);
        pool.recycle(unmarshaller);
    }

    /**
     * Test brief Record Marshalling.
     */
    @Test
    public void briefRecordMarshalingTest() throws JAXBException {
        Marshaller marshaller = pool.acquireMarshaller();
        /*
         * Test marshalling BRIEF csw Record v2.0.2
         */
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        BriefRecordType record = new BriefRecordType(id, title, type, bbox);

        StringWriter sw = new StringWriter();
        marshaller.marshal(record, sw);
        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:BriefRecord xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\">\n" +
        "  <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "  <dc:title>(JASON-1)</dc:title>\n" +
        "  <dc:type>clearinghouse</dc:type>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "</csw:BriefRecord>\n";

        assertXmlEquals(expResult, result, "xmlns:*");
        /*
         * Test marshalling csw Record v2.0.2
         */
        List<SimpleLiteral> identifiers = new ArrayList<>();
        identifiers.add(new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}"));
        identifiers.add(new SimpleLiteral("urn:ogc:x-def:F7807C8AB645"));

        List<SimpleLiteral> titles = new ArrayList<>();
        titles.add(new SimpleLiteral("(JASON-1)"));
        titles.add(new SimpleLiteral("(JASON-2)"));

        type = new SimpleLiteral("clearinghouse");

        bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));
        bbox.add(new WGS84BoundingBoxType(176, -16.4, -178, 6.1));

        record = new BriefRecordType(identifiers, titles, type, bbox);

        sw = new StringWriter();
        marshaller.marshal(record, sw);
        result = sw.toString();

        expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:BriefRecord xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\">\n" +
        "  <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "  <dc:identifier>urn:ogc:x-def:F7807C8AB645</dc:identifier>\n" +
        "  <dc:title>(JASON-1)</dc:title>\n" +
        "  <dc:title>(JASON-2)</dc:title>\n" +
        "  <dc:type>clearinghouse</dc:type>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>176.0 -16.4</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-178.0 6.1</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "</csw:BriefRecord>\n";

        assertXmlEquals(expResult, result, "xmlns:*");
        pool.recycle(marshaller);
    }

    /**
     * Test brief Record Unmarshalling.
     */
    @Test
    public void briefRecordUnmarshalingTest() throws JAXBException {
        Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        /*
         * Test marshalling BRIEF csw Record v2.0.2
         */
        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:BriefRecord xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\">\n" +
        "  <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "  <dc:title>(JASON-1)</dc:title>\n" +
        "  <dc:type>clearinghouse</dc:type>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "</csw:BriefRecord>\n";

        StringReader sr = new StringReader(xml);
        BriefRecordType result = (BriefRecordType) unmarshaller.unmarshal(sr);

        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        BriefRecordType expResult = new BriefRecordType(id, title, type, bbox);
        assertEquals(expResult, result);
        /*
         * Test marshalling csw Record v2.0.2
         */
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:BriefRecord xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\">\n" +
        "  <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "  <dc:identifier>urn:ogc:x-def:F7807C8AB645</dc:identifier>\n" +
        "  <dc:title>(JASON-1)</dc:title>\n" +
        "  <dc:title>(JASON-2)</dc:title>\n" +
        "  <dc:type>clearinghouse</dc:type>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "  <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "    <ows:LowerCorner>176.0 -16.4</ows:LowerCorner>\n" +
        "    <ows:UpperCorner>-178.0 6.1</ows:UpperCorner>\n" +
        "  </ows:WGS84BoundingBox>\n" +
        "</csw:BriefRecord>\n";

        sr = new StringReader(xml);
        result = (BriefRecordType) unmarshaller.unmarshal(sr);

        List<SimpleLiteral> identifiers = new ArrayList<>();
        identifiers.add(new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}"));
        identifiers.add(new SimpleLiteral("urn:ogc:x-def:F7807C8AB645"));

        List<SimpleLiteral> titles = new ArrayList<>();
        titles.add(new SimpleLiteral("(JASON-1)"));
        titles.add(new SimpleLiteral("(JASON-2)"));

        type = new SimpleLiteral("clearinghouse");

        bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));
        bbox.add(new WGS84BoundingBoxType(176, -16.4, -178, 6.1));

        expResult = new BriefRecordType(identifiers, titles, type, bbox);

        assertEquals(expResult, result);
        pool.recycle(unmarshaller);
    }

    /**
     * Test getRecordById request Marshalling.
     */
    @Test
    public void getRecordByIdResponseMarshalingTest() throws JAXBException {
        Marshaller marshaller = pool.acquireMarshaller();
        /*
         * Test marshalling csw getRecordByIdResponse v2.0.2
         */
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        SimpleLiteral modified   = new SimpleLiteral("2007-11-15 21:26:49");
        SimpleLiteral Abstract   = new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.");
        SimpleLiteral references = new SimpleLiteral("http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml");
        SimpleLiteral spatial    = new SimpleLiteral("northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;");

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        RecordType record           = new RecordType(id, title, type, subject, null, modified, null, Abstract, bbox, null, null, null, spatial, references);
        BriefRecordType briefRecord = new BriefRecordType(id, title, type, bbox);
        SummaryRecordType sumRecord = new SummaryRecordType(id, title, type, bbox, subject, null, modified, Abstract);

        List<Object> records = new ArrayList<>();
        records.add(record);
        records.add(briefRecord);
        records.add(sumRecord);
        GetRecordByIdResponse response = new GetRecordByIdResponseType(records);

        StringWriter sw = new StringWriter();
        marshaller.marshal(response, sw);
        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:GetRecordByIdResponse xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">\n" +
        "  <csw:Record>\n" +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "    <dc:title>(JASON-1)</dc:title>\n" +
        "    <dc:type>clearinghouse</dc:type>\n" +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "    <dc:subject>oceans elevation 2</dc:subject>\n" +
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "    <dct:references>http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml</dct:references>\n" +
        "    <dct:spatial>northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;</dct:spatial>\n" +
        "    <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "      <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "      <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "    </ows:WGS84BoundingBox>\n" +
        "  </csw:Record>\n" +
        "  <csw:BriefRecord>\n" +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "    <dc:title>(JASON-1)</dc:title>\n" +
        "    <dc:type>clearinghouse</dc:type>\n" +
        "    <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "      <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "      <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "    </ows:WGS84BoundingBox>\n" +
        "  </csw:BriefRecord>\n" +
        "  <csw:SummaryRecord>\n" +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "    <dc:title>(JASON-1)</dc:title>\n" +
        "    <dc:type>clearinghouse</dc:type>\n" +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "    <dc:subject>oceans elevation 2</dc:subject>\n" +
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "    <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "      <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "      <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "    </ows:WGS84BoundingBox>\n" +
        "  </csw:SummaryRecord>\n" +
        "</csw:GetRecordByIdResponse>\n";

        LOGGER.log(Level.FINER, "RESULT:\n{0}", result);
        LOGGER.log(Level.FINER, "EXPRESULT:\n{0}", expResult);
        assertXmlEquals(expResult, result, "xmlns:*");
        pool.recycle(marshaller);
    }

    /**
     * Test getRecordById request Marshalling.
     */
    @Test
    public void getRecordByIdResponseUnMarshalingTest() throws JAXBException {
        Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        /*
         * Test marshalling csw getRecordByIdResponse v2.0.2
         */
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        SimpleLiteral modified   = new SimpleLiteral("2007-11-15 21:26:49");
        SimpleLiteral Abstract   = new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.");
        SimpleLiteral references = new SimpleLiteral("http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml");
        SimpleLiteral spatial    = new SimpleLiteral("northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;");

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        RecordType record           = new RecordType(id, title, type, subject, null, modified, null, Abstract, bbox, null, null, null, spatial, references);
        BriefRecordType briefRecord = new BriefRecordType(id, title, type, bbox);
        SummaryRecordType sumRecord = new SummaryRecordType(id, title, type, bbox, subject, null, modified, Abstract);

        List<AbstractRecordType> records = new ArrayList<>();
        records.add(record);
        records.add(briefRecord);
        records.add(sumRecord);

        GetRecordByIdResponse expResult = new GetRecordByIdResponseType(records);

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:GetRecordByIdResponse xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">\n" +
        "  <csw:Record>\n" +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "    <dc:title>(JASON-1)</dc:title>\n" +
        "    <dc:type>clearinghouse</dc:type>\n" +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "    <dc:subject>oceans elevation 2</dc:subject>\n" +
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "    <dct:references>http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml</dct:references>\n" +
        "    <dct:spatial>northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;</dct:spatial>\n" +
        "    <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "      <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "      <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "    </ows:WGS84BoundingBox>\n" +
        "  </csw:Record>\n" +
        "  <csw:BriefRecord>\n" +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "    <dc:title>(JASON-1)</dc:title>\n" +
        "    <dc:type>clearinghouse</dc:type>\n" +
        "    <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "      <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "      <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "    </ows:WGS84BoundingBox>\n" +
        "  </csw:BriefRecord>\n" +
        "  <csw:SummaryRecord>\n" +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "    <dc:title>(JASON-1)</dc:title>\n" +
        "    <dc:type>clearinghouse</dc:type>\n" +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "    <dc:subject>oceans elevation 2</dc:subject>\n" +
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "    <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "      <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "      <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "    </ows:WGS84BoundingBox>\n" +
        "  </csw:SummaryRecord>\n" +
        "</csw:GetRecordByIdResponse>\n";

        GetRecordByIdResponse result = ((JAXBElement<GetRecordByIdResponse>) unmarshaller.unmarshal(new StringReader(xml))).getValue();
        assertTrue(result.getAny()instanceof List);
        List<Object> resultList = result.getAny();
        List<Object> expResultList = expResult.getAny();
        assertEquals(resultList.get(0), expResultList.get(0));
        assertEquals(resultList.get(1), expResultList.get(1));
        assertEquals(resultList.get(2), expResultList.get(2));
        assertEquals(resultList, expResultList);
        assertEquals(expResult.getAny(), result.getAny());
        assertEquals(expResult, result);
        pool.recycle(unmarshaller);
    }

    /**
     * Test getRecordById request Marshalling.
     */
    @Test
    public void getRecordsResponseMarshalingTest() throws JAXBException {
        Marshaller marshaller = pool.acquireMarshaller();
        /*
         * Test marshalling csw getRecordByIdResponse v2.0.2
         */
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        SimpleLiteral modified   = new SimpleLiteral("2007-11-15 21:26:49");
        SimpleLiteral Abstract   = new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.");
        SimpleLiteral references = new SimpleLiteral("http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml");
        SimpleLiteral spatial    = new SimpleLiteral("northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;");

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        RecordType record           = new RecordType(id, title, type, subject, null, modified, null, Abstract, bbox, null, null, null, spatial, references);
        BriefRecordType briefRecord = new BriefRecordType(id, title, type, bbox);
        SummaryRecordType sumRecord = new SummaryRecordType(id, title, type, bbox, subject, null, modified, Abstract);

        List<Object> records = new ArrayList<>();
        records.add(record);
        records.add(briefRecord);
        records.add(sumRecord);

        final SearchResultsType sr = new SearchResultsType("set", ElementSetType.BRIEF, 1, records, 1, 0);
        GetRecordsResponse response = new GetRecordsResponseType("rid", 100000, "v1.2", sr);

        StringWriter sw = new StringWriter();
        marshaller.marshal(response, sw);
        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:GetRecordsResponse version=\"v1.2\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">\n" +
        "  <csw:RequestId>rid</csw:RequestId>\n" +
        "  <csw:SearchStatus timestamp=\"1970-01-01T01:01:40.000+01:00\"/>\n" +
        "  <csw:SearchResults resultSetId=\"set\" elementSet=\"brief\" numberOfRecordsMatched=\"1\" numberOfRecordsReturned=\"1\" nextRecord=\"0\">\n" +
        "    <csw:Record>\n" +
        "      <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "      <dc:title>(JASON-1)</dc:title>\n" +
        "      <dc:type>clearinghouse</dc:type>\n" +
        "      <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "      <dc:subject>oceans elevation 2</dc:subject>\n" +
        "      <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "      <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "      <dct:references>http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml</dct:references>\n" +
        "      <dct:spatial>northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;</dct:spatial>\n" +
        "      <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "      </ows:WGS84BoundingBox>\n" +
        "    </csw:Record>\n" +
        "    <csw:BriefRecord>\n" +
        "      <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "      <dc:title>(JASON-1)</dc:title>\n" +
        "      <dc:type>clearinghouse</dc:type>\n" +
        "      <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "      </ows:WGS84BoundingBox>\n" +
        "    </csw:BriefRecord>\n" +
        "    <csw:SummaryRecord>\n" +
        "      <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "      <dc:title>(JASON-1)</dc:title>\n" +
        "      <dc:type>clearinghouse</dc:type>\n" +
        "      <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "      <dc:subject>oceans elevation 2</dc:subject>\n" +
        "      <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "      <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "      <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "      </ows:WGS84BoundingBox>\n" +
        "    </csw:SummaryRecord>\n" +
        "  </csw:SearchResults>\n" +
        "</csw:GetRecordsResponse>\n";

        LOGGER.log(Level.FINER, "RESULT:\n{0}", result);
        LOGGER.log(Level.FINER, "EXPRESULT:\n{0}", expResult);
        assertXmlEquals(expResult, result, "xmlns:*");
        pool.recycle(marshaller);
    }

    /**
     * Test getRecordById request Marshalling.
     */
    @Test
    public void getRecordsResponseUnMarshalingTest() throws JAXBException {
        Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        /*
         * Test marshalling csw getRecordByIdResponse v2.0.2
         */
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        SimpleLiteral modified   = new SimpleLiteral("2007-11-15 21:26:49");
        SimpleLiteral Abstract   = new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.");
        SimpleLiteral references = new SimpleLiteral("http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml");
        SimpleLiteral spatial    = new SimpleLiteral("northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;");

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        RecordType record           = new RecordType(id, title, type, subject, null, modified, null, Abstract, bbox, null, null, null, spatial, references);
        BriefRecordType briefRecord = new BriefRecordType(id, title, type, bbox);
        SummaryRecordType sumRecord = new SummaryRecordType(id, title, type, bbox, subject, null, modified, Abstract);

        List<Object> records = new ArrayList<>();
        records.add(record);
        records.add(briefRecord);
        records.add(sumRecord);

        final SearchResultsType sr = new SearchResultsType("set", ElementSetType.BRIEF, 1, records, 1, 0);
        GetRecordsResponse expResult = new GetRecordsResponseType("rid", 100000, "v1.2", sr);

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:GetRecordsResponse version=\"v1.2\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dct=\"http://purl.org/dc/terms/\">\n" +
        "  <csw:RequestId>rid</csw:RequestId>\n" +
        "  <csw:SearchStatus timestamp=\"1970-01-01T01:01:40.000+01:00\"/>\n" +
        "  <csw:SearchResults resultSetId=\"set\" elementSet=\"brief\" numberOfRecordsMatched=\"1\" numberOfRecordsReturned=\"1\" nextRecord=\"0\">\n" +
        "  <csw:Record>\n" +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "    <dc:title>(JASON-1)</dc:title>\n" +
        "    <dc:type>clearinghouse</dc:type>\n" +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "    <dc:subject>oceans elevation 2</dc:subject>\n" +
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "    <dct:references>http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml</dct:references>\n" +
        "    <dct:spatial>northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;</dct:spatial>\n" +
        "    <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "      <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "      <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "    </ows:WGS84BoundingBox>\n" +
        "  </csw:Record>\n" +
        "  <csw:BriefRecord>\n" +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "    <dc:title>(JASON-1)</dc:title>\n" +
        "    <dc:type>clearinghouse</dc:type>\n" +
        "    <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "      <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "      <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "    </ows:WGS84BoundingBox>\n" +
        "  </csw:BriefRecord>\n" +
        "  <csw:SummaryRecord>\n" +
        "    <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "    <dc:title>(JASON-1)</dc:title>\n" +
        "    <dc:type>clearinghouse</dc:type>\n" +
        "    <dc:subject>oceans elevation NASA/JPL/JASON-1</dc:subject>\n" +
        "    <dc:subject>oceans elevation 2</dc:subject>\n" +
        "    <dct:modified>2007-11-15 21:26:49</dct:modified>\n" +
        "    <dct:abstract>Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.</dct:abstract>\n" +
        "    <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "      <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "      <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "    </ows:WGS84BoundingBox>\n" +
        "  </csw:SummaryRecord>\n" +
        "  </csw:SearchResults>\n" +
        "</csw:GetRecordsResponse>\n";

        GetRecordsResponse result = ((JAXBElement<GetRecordsResponse>) unmarshaller.unmarshal(new StringReader(xml))).getValue();
        assertTrue(result.getSearchResults().getAny() instanceof List);
        List<Object> resultList = result.getSearchResults().getAny();
        List<Object> expResultList = expResult.getSearchResults().getAny();
        assertEquals(expResultList.get(0), resultList.get(0));
        assertEquals(expResultList.get(1), resultList.get(1));
        assertEquals(expResultList.get(2), resultList.get(2));
        assertEquals(expResultList, resultList);
        assertEquals(expResult.getSearchResults().getAny(), result.getSearchResults().getAny());
        assertEquals(expResult.getSearchStatus(), result.getSearchStatus());
        assertEquals(expResult, result);
        pool.recycle(unmarshaller);
    }

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void getRecordsMarshalingTest() throws JAXBException, IOException, ParserConfigurationException, SAXException {
        Marshaller marshaller = pool.acquireMarshaller();
        /*
         * Test marshalling csw getRecordByIdResponse v2.0.2
         */

        /*
         * we build the first filter : < dublinCore:Title IS LIKE '*' >
         */
        List<QName> typeNames  = new ArrayList<>();
        PropertyNameType pname = new PropertyNameType("dc:Title");
        PropertyIsLikeType pil = new PropertyIsLikeType(pname, "something?", "*", "?", "\\");
        NotType n              = new NotType(pil);
        FilterType filter1     = new FilterType(n);

        QueryConstraintType constraint = new QueryConstraintType(filter1, "1.1.0");
        typeNames.add(_Record_QNAME);
        QueryType query = new QueryType(typeNames, new ElementSetNameType(ElementSetType.FULL), null, constraint);

        GetRecordsType getRecordsRequest = new GetRecordsType("CSW", "2.0.2", ResultType.RESULTS, null, "application/xml", "http://www.opengis.net/cat/csw/2.0.2", 1, 20, query, null);

        StringWriter sw = new StringWriter();
        marshaller.marshal(getRecordsRequest, sw);
        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw2:GetRecords xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:csw2=\"http://www.opengis.net/cat/csw/2.0.2\""
                + " maxRecords=\"20\" startPosition=\"1\" outputSchema=\"http://www.opengis.net/cat/csw/2.0.2\" outputFormat=\"application/xml\""
                + " resultType=\"results\" version=\"2.0.2\" service=\"CSW\">\n" +
        "  <csw2:Query typeNames=\"csw2:Record\">\n" +
        "    <csw2:ElementSetName>full</csw2:ElementSetName>\n" +
        "    <csw2:Constraint version=\"1.1.0\">\n" +
        "      <ogc:Filter>\n" +
        "        <ogc:Not>\n" +
        "          <ogc:PropertyIsLike wildCard=\"*\" singleChar=\"?\" escapeChar=\"\\\">\n" +
        "            <ogc:PropertyName>dc:Title</ogc:PropertyName>\n" +
        "            <ogc:Literal>something?</ogc:Literal>\n" +
        "          </ogc:PropertyIsLike>\n" +
        "        </ogc:Not>\n" +
        "      </ogc:Filter>\n" +
        "    </csw2:Constraint>\n" +
        "  </csw2:Query>\n"+
        "</csw2:GetRecords>\n";

        assertXmlEquals(expResult, result, "xmlns:*");
        /*
         * Test marshalling csw getRecordByIdResponse v2.0.0
         */
        org.geotoolkit.csw.xml.v200.QueryConstraintType constraint200 = new org.geotoolkit.csw.xml.v200.QueryConstraintType(filter1, "1.1.0");
        typeNames  = new ArrayList<>();
        typeNames.add( org.geotoolkit.csw.xml.v200.ObjectFactory._Record_QNAME);
        org.geotoolkit.csw.xml.v200.QueryType query200 = new org.geotoolkit.csw.xml.v200.QueryType(typeNames, new org.geotoolkit.csw.xml.v200.ElementSetNameType(ElementSetType.FULL), constraint200);

        org.geotoolkit.csw.xml.v200.GetRecordsType getRecordsRequest200 = new org.geotoolkit.csw.xml.v200.GetRecordsType("CSW", "2.0.0", ResultType.RESULTS, null, "application/xml", "http://www.opengis.net/cat/csw", 1, 20, query200, null);

        sw = new StringWriter();
        marshaller.marshal(getRecordsRequest200, sw);
        result = sw.toString();

        expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<cat:GetRecords xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:cat=\"http://www.opengis.net/cat/csw\""
                + " maxRecords=\"20\" startPosition=\"1\" outputSchema=\"http://www.opengis.net/cat/csw\" outputFormat=\"application/xml\""
                + " resultType=\"results\" version=\"2.0.0\" service=\"CSW\">\n" +
        "  <cat:Query typeNames=\"cat:Record\">\n" +
        "    <cat:ElementSetName>full</cat:ElementSetName>\n" +
        "    <cat:Constraint version=\"1.1.0\">\n" +
        "      <ogc:Filter>\n" +
        "        <ogc:Not>\n" +
        "          <ogc:PropertyIsLike wildCard=\"*\" singleChar=\"?\" escapeChar=\"\\\">\n" +
        "            <ogc:PropertyName>dc:Title</ogc:PropertyName>\n" +
        "            <ogc:Literal>something?</ogc:Literal>\n" +
        "          </ogc:PropertyIsLike>\n" +
        "        </ogc:Not>\n" +
        "      </ogc:Filter>\n" +
        "    </cat:Constraint>\n" +
        "  </cat:Query>\n"+
        "</cat:GetRecords>\n";

        assertXmlEquals(expResult, result, "xmlns:*");
        pool.recycle(marshaller);
    }

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void getRecordsUnMarshalingTest() throws JAXBException {
        Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        /*
         * Test unmarshalling csw getRecordByIdResponse v2.0.2
         */
        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:GetRecords xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\""
                + " maxRecords=\"20\" startPosition=\"1\" outputSchema=\"http://www.opengis.net/cat/csw/2.0.2\" outputFormat=\"application/xml\""
                + " resultType=\"results\" version=\"2.0.2\" service=\"CSW\">\n" +
        "  <csw:Query typeNames=\"csw:Record\">\n" +
        "    <csw:ElementSetName>full</csw:ElementSetName>\n" +
        "    <csw:Constraint version=\"1.1.0\">\n" +
        "      <ogc:Filter>\n" +
        "        <ogc:Not>\n" +
        "          <ogc:PropertyIsLike wildCard=\"*\" singleChar=\"?\" escapeChar=\"\\\">\n" +
        "            <ogc:PropertyName>dc:Title</ogc:PropertyName>\n" +
        "            <ogc:Literal>something?</ogc:Literal>\n" +
        "          </ogc:PropertyIsLike>\n" +
        "        </ogc:Not>\n" +
        "      </ogc:Filter>\n" +
        "    </csw:Constraint>\n" +
        "  </csw:Query>\n" +
        "</csw:GetRecords>\n";

        StringReader sr = new StringReader(xml);
        Object result = unmarshaller.unmarshal(sr);
        /*
         * we build the first filter : < dublinCore:Title IS LIKE '*' >
         */
        List<QName> typeNames  = new ArrayList<>();
        PropertyNameType pname = new PropertyNameType("dc:Title");
        PropertyIsLikeType pil = new PropertyIsLikeType(pname, "something?", "*", "?", "\\");
        NotType n              = new NotType(pil);
        FilterType filter1     = new FilterType(n);

        QueryConstraintType constraint = new QueryConstraintType(filter1, "1.1.0");
        typeNames.add(_Record_QNAME);
        QueryType query = new QueryType(typeNames, new ElementSetNameType(ElementSetType.FULL), null, constraint);

        GetRecordsType expResult = new GetRecordsType("CSW", "2.0.2", ResultType.RESULTS, null, "application/xml", "http://www.opengis.net/cat/csw/2.0.2", 1, 20, query, null);

        LOGGER.log(Level.FINER, "RESULT:\n{0}", result);
        LOGGER.log(Level.FINER, "EXPRESULT:\n{0}", expResult);
        GetRecordsType gres = (GetRecordsType)result;
        QueryType expQT = (QueryType) expResult.getAbstractQuery();
        QueryType resQT = (QueryType) gres.getAbstractQuery();

        assertEquals(expQT.getConstraint().getFilter().getLogicOps().getValue(), resQT.getConstraint().getFilter().getLogicOps().getValue());
        assertEquals(expQT.getConstraint().getFilter(), resQT.getConstraint().getFilter());
        assertEquals(expQT.getConstraint(), resQT.getConstraint());
        assertEquals(expResult.getAbstractQuery(), gres.getAbstractQuery());
        assertEquals(expResult, result);

        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<cat:GetRecords xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:cat=\"http://www.opengis.net/cat/csw\""
                + " maxRecords=\"20\" startPosition=\"1\" outputSchema=\"http://www.opengis.net/cat/csw\" outputFormat=\"application/xml\""
                + " resultType=\"results\" version=\"2.0.0\" service=\"CSW\">\n" +
        "  <cat:Query typeNames=\"cat:Record\">\n" +
        "    <cat:ElementSetName>full</cat:ElementSetName>\n" +
        "    <cat:Constraint version=\"1.1.0\">\n" +
        "      <ogc:Filter>\n" +
        "        <ogc:Not>\n" +
        "          <ogc:PropertyIsLike wildCard=\"*\" singleChar=\"?\" escapeChar=\"\\\">\n" +
        "            <ogc:PropertyName>dc:Title</ogc:PropertyName>\n" +
        "            <ogc:Literal>something?</ogc:Literal>\n" +
        "          </ogc:PropertyIsLike>\n" +
        "        </ogc:Not>\n" +
        "      </ogc:Filter>\n" +
        "    </cat:Constraint>\n" +
        "  </cat:Query>\n" +
        "</cat:GetRecords>\n";

        org.geotoolkit.csw.xml.v200.QueryConstraintType constraint200 = new org.geotoolkit.csw.xml.v200.QueryConstraintType(filter1, "1.1.0");
        typeNames  = new ArrayList<>();
        typeNames.add( org.geotoolkit.csw.xml.v200.ObjectFactory._Record_QNAME);
        org.geotoolkit.csw.xml.v200.QueryType query200 = new org.geotoolkit.csw.xml.v200.QueryType(typeNames, new org.geotoolkit.csw.xml.v200.ElementSetNameType(ElementSetType.FULL), constraint200);

        org.geotoolkit.csw.xml.v200.GetRecordsType expResult200 = new org.geotoolkit.csw.xml.v200.GetRecordsType("CSW", "2.0.0", ResultType.RESULTS, null, "application/xml", "http://www.opengis.net/cat/csw", 1, 20, query200, null);

        sr = new StringReader(xml);
        result = unmarshaller.unmarshal(sr);

        assertTrue(result instanceof JAXBElement);

        org.geotoolkit.csw.xml.v200.GetRecordsType result200 = (org.geotoolkit.csw.xml.v200.GetRecordsType) ((JAXBElement)result).getValue();

        assertEquals(expResult200.getAbstractQuery(), result200.getAbstractQuery());
        assertEquals(expResult200, result200);
        pool.recycle(unmarshaller);
    }

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void updateMarshalingTest() throws JAXBException, IOException, ParserConfigurationException, SAXException {
        Marshaller marshaller = pool.acquireMarshaller();

        // <TODO
        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<SimpleLiteral> subject = new ArrayList<>();
        subject.add(new SimpleLiteral("oceans elevation NASA/JPL/JASON-1"));
        subject.add(new SimpleLiteral("oceans elevation 2"));

        SimpleLiteral modified   = new SimpleLiteral("2007-11-15 21:26:49");
        SimpleLiteral Abstract   = new SimpleLiteral("Jason-1 is the first follow-on to the highly successful TOPEX/Poseidonmission that measured ocean surface topography to an accuracy of 4.2cm.");
        SimpleLiteral references = new SimpleLiteral("http://keel.esri.com/output/TOOLKIT_Browse_Metadata_P7540_T8020_D1098.xml");
        SimpleLiteral spatial    = new SimpleLiteral("northlimit=65.9999999720603; eastlimit=180; southlimit=-66.0000000558794; westlimit=-180;");

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        RecordType record = new RecordType(id, title, type, subject, null, modified, null, Abstract, bbox, null, null, null, spatial, references);

        QueryConstraintType query = new QueryConstraintType("identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'", "1.1.0");
        UpdateType update = new UpdateType(record, query);

        TransactionType request = new TransactionType("CSW", "2.0.2", update);

        //marshaller.marshal(request, System.out);

        // TODO/>

        /*
         * Test 2 : Simple recordProperty (String)
         */
        RecordPropertyType recordProperty = new RecordPropertyType("/csw:Record/dc:contributor", "Jane");
        query = new QueryConstraintType("identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'", "1.1.0");
        update = new UpdateType(Arrays.asList(recordProperty), query);
        request = new TransactionType("CSW", "2.0.2", update);

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:Transaction verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "  <csw:Update>\n" +
        "    <csw:RecordProperty>\n" +
        "      <csw:Name>/csw:Record/dc:contributor</csw:Name>\n" +
        "      <csw:Value>Jane</csw:Value>\n" +
        "    </csw:RecordProperty>\n" +
        "    <csw:Constraint version=\"1.1.0\">\n" +
        "      <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>\n" +
        "    </csw:Constraint>\n" +
        "  </csw:Update>\n" +
        "</csw:Transaction>\n";

        StringWriter sw = new StringWriter();
        marshaller.marshal(request, sw);
        String result = sw.toString();
        assertXmlEquals(expResult, result, "xmlns:*");
        /*
         * Test 3 : Complex recordProperty (GeographicBoundingBox)
         */
        DefaultGeographicBoundingBox geographicElement = new DefaultGeographicBoundingBox(1.1, 1.1, 1.1, 1.1);
        recordProperty = new RecordPropertyType("/gmd:MD_Metadata/identificationInfo/extent/geographicElement", geographicElement);
        query = new QueryConstraintType("identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'", "1.1.0");
        update = new UpdateType(Arrays.asList(recordProperty), query);
        request = new TransactionType("CSW", "2.0.2", update);

        expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:Transaction verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:gco=\"http://www.isotc211.org/2005/gco\">\n" +
        "  <csw:Update>\n" +
        "    <csw:RecordProperty>\n" +
        "      <csw:Name>/gmd:MD_Metadata/identificationInfo/extent/geographicElement</csw:Name>\n" +
        "      <csw:Value>\n" +
        "       <gmd:EX_GeographicBoundingBox>"                                             + '\n' +
        "        <gmd:extentTypeCode>\n" +
        "          <gco:Boolean>true</gco:Boolean>\n" +
        "        </gmd:extentTypeCode>\n" +
        "        <gmd:westBoundLongitude>\n" +
        "          <gco:Decimal>1.1</gco:Decimal>\n" +
        "        </gmd:westBoundLongitude>\n" +
        "        <gmd:eastBoundLongitude>\n" +
        "          <gco:Decimal>1.1</gco:Decimal>\n" +
        "        </gmd:eastBoundLongitude>\n" +
        "        <gmd:southBoundLatitude>\n" +
        "          <gco:Decimal>1.1</gco:Decimal>\n" +
        "        </gmd:southBoundLatitude>\n" +
        "        <gmd:northBoundLatitude>\n" +
        "          <gco:Decimal>1.1</gco:Decimal>\n" +
        "        </gmd:northBoundLatitude>\n" +
        "       </gmd:EX_GeographicBoundingBox>"                                             + '\n' +
        "      </csw:Value>\n" +
        "    </csw:RecordProperty>\n" +
        "    <csw:Constraint version=\"1.1.0\">\n" +
        "      <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>\n" +
        "    </csw:Constraint>\n" +
        "  </csw:Update>\n" +
        "</csw:Transaction>\n";

        sw = new StringWriter();
        marshaller.marshal(request, sw);
        result = sw.toString();
        assertXmlEquals(expResult, result, "xmlns:*");
        pool.recycle(marshaller);
    }

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void updateUnmarshalingTest() throws JAXBException {
        Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        /*
         * Test 1 : Simple recordProperty (String)
         */
        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:Transaction xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" >\n" +
        "  <csw:Update>\n" +
        "    <csw:RecordProperty>\n" +
        "      <csw:Name>/csw:Record/dc:contributor</csw:Name>\n" +
        "      <csw:Value xsi:type=\"xs:string\" >Jane</csw:Value>\n" +
        "    </csw:RecordProperty>\n" +
        "    <csw:Constraint version=\"1.1.0\">\n" +
        "      <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>\n" +
        "    </csw:Constraint>\n" +
        "  </csw:Update>\n" +
        "</csw:Transaction>\n";

        TransactionType result = (TransactionType) unmarshaller.unmarshal(new StringReader(xml));

        RecordPropertyType recordProperty = new RecordPropertyType("/csw:Record/dc:contributor", "Jane");
        QueryConstraintType query         = new QueryConstraintType("identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'", "1.1.0");
        UpdateType update                 = new UpdateType(Arrays.asList(recordProperty), query);
        TransactionType expResult         = new TransactionType("CSW", "2.0.2", update);

        assertEquals(expResult, result);

        xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:Transaction verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:gco=\"http://www.isotc211.org/2005/gco\">\n" +
        "  <csw:Update>\n" +
        "    <csw:RecordProperty>\n" +
        "      <csw:Name>/gmd:MD_Metadata/identificationInfo/extent/geographicElement</csw:Name>\n" +
        "      <csw:Value xsi:type=\"gmd:EX_GeographicBoundingBox_Type\" >\n" +
        "       <gmd:EX_GeographicBoundingBox>"                                             + '\n' +
        "        <gmd:extentTypeCode>\n" +
        "          <gco:Boolean>true</gco:Boolean>\n" +
        "        </gmd:extentTypeCode>\n" +
        "        <gmd:westBoundLongitude>\n" +
        "          <gco:Decimal>1.1</gco:Decimal>\n" +
        "        </gmd:westBoundLongitude>\n" +
        "        <gmd:eastBoundLongitude>\n" +
        "          <gco:Decimal>1.1</gco:Decimal>\n" +
        "        </gmd:eastBoundLongitude>\n" +
        "        <gmd:southBoundLatitude>\n" +
        "          <gco:Decimal>1.1</gco:Decimal>\n" +
        "        </gmd:southBoundLatitude>\n" +
        "        <gmd:northBoundLatitude>\n" +
        "          <gco:Decimal>1.1</gco:Decimal>\n" +
        "        </gmd:northBoundLatitude>\n" +
        "       </gmd:EX_GeographicBoundingBox>"                                             + '\n' +
        "      </csw:Value>\n" +
        "    </csw:RecordProperty>\n" +
        "    <csw:Constraint version=\"1.1.0\">\n" +
        "      <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>\n" +
        "    </csw:Constraint>\n" +
        "  </csw:Update>\n" +
        "</csw:Transaction>\n";

        result = (TransactionType) unmarshaller.unmarshal(new StringReader(xml));

        DefaultGeographicBoundingBox geographicElement = new DefaultGeographicBoundingBox(1.1, 1.1, 1.1, 1.1);
        recordProperty = new RecordPropertyType("/gmd:MD_Metadata/identificationInfo/extent/geographicElement", geographicElement);
        query          = new QueryConstraintType("identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'", "1.1.0");
        update         = new UpdateType(Arrays.asList(recordProperty), query);
        expResult      = new TransactionType("CSW", "2.0.2", update);

        assertEquals(expResult, result);
        pool.recycle(unmarshaller);
    }

    /**
     * Test capabilities with INSPIRE extendedCapabilities unmarshalling.
     */
    @Test
    public void InspireUnmarshalingTest() throws Exception {
        Unmarshaller unmarshaller = pool.acquireUnmarshaller();

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:Capabilities version=\"2.0.2\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\">\n" +
        " <ows:OperationsMetadata>\n" +
        "   <ows:ExtendedCapabilities>\n" +
        "     <ins:MultiLingualCapabilities xmlns:ins=\"http://www.inspire.org\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
        "       <ins:Languages>\n" +
        "         <ins:Language>GER</ins:Language>\n" +
        "         <ins:Language>DUT</ins:Language>\n" +
        "       </ins:Languages>\n" +
        "       <ins:TranslatedCapabilities>\n" +
        "         <ins:Document xlink:href=\"http://www.somehost.com/capabilities_german.xml\" language=\"GER\"/>\n" +
        "         <ins:Document xlink:href=\"http://www.somehost.com/capabilities_dutch.xml\"  language=\"DUT\"/>\n" +
        "       </ins:TranslatedCapabilities>\n" +
        "    </ins:MultiLingualCapabilities>\n" +
        "  </ows:ExtendedCapabilities>\n" +
        " </ows:OperationsMetadata>\n" +
        "</csw:Capabilities>\n";

        Capabilities result = (Capabilities) unmarshaller.unmarshal(new StringReader(xml));

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
        pool.recycle(unmarshaller);
    }

    /**
     * Test capabilities with INSPIRE extendedCapabilities unmarshalling.
     */
    @Test
    public void InspireMarshalingTest() throws Exception {
        Marshaller marshaller = pool.acquireMarshaller();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<csw:Capabilities version=\"2.0.2\" xmlns:ins=\"http://www.inspire.org\" xmlns:ows=\"http://www.opengis.net/ows\""
                + " xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
        "  <ows:OperationsMetadata>\n" +
        "    <ows:ExtendedCapabilities>\n" +
        "      <ins:MultiLingualCapabilities>\n" +
        "        <ins:Languages>\n" +
        "          <ins:Language>GER</ins:Language>\n" +
        "          <ins:Language>DUT</ins:Language>\n" +
        "        </ins:Languages>\n" +
        "        <ins:TranslatedCapabilities>\n" +
        "          <ins:Document language=\"GER\" xlink:href=\"http://www.somehost.com/capabilities_german.xml\"/>\n" +
        "          <ins:Document language=\"DUT\" xlink:href=\"http://www.somehost.com/capabilities_dutch.xml\"/>\n" +
        "        </ins:TranslatedCapabilities>\n" +
        "      </ins:MultiLingualCapabilities>\n" +
        "    </ows:ExtendedCapabilities>\n" +
        "  </ows:OperationsMetadata>\n" +
        "</csw:Capabilities>\n";

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
        marshaller.marshal(capa, sw);
        String result = sw.toString();

        assertXmlEquals(expResult, result, "xmlns:*");
        pool.recycle(marshaller);
    }


    @Test
    public void transactionResponseMarshalingTest() throws JAXBException, IOException, ParserConfigurationException, SAXException {
        Marshaller marshaller = pool.acquireMarshaller();

        SimpleLiteral id    = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type  = new SimpleLiteral("clearinghouse");

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        Object record = new BriefRecordType(id, title, type, bbox);

        final List<InsertResultType> inserteds = new ArrayList<>();
        inserteds.add(new InsertResultType(Arrays.asList(record), null));

        TransactionSummaryType summ = new TransactionSummaryType(2, 0, 1, "rid");
        TransactionResponseType request = new TransactionResponseType(summ, inserteds, "2.0.2");

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<csw:TransactionResponse version=\"2.0.2\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:ows=\"http://www.opengis.net/ows\">\n" +
        "  <csw:TransactionSummary requestId=\"rid\">\n" +
        "    <csw:totalInserted>2</csw:totalInserted>\n" +
        "    <csw:totalUpdated>0</csw:totalUpdated>\n" +
        "    <csw:totalDeleted>1</csw:totalDeleted>\n" +
        "  </csw:TransactionSummary>\n" +
        "  <csw:InsertResult>\n" +
        "    <csw:BriefRecord>\n" +
        "      <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "      <dc:title>(JASON-1)</dc:title>\n" +
        "      <dc:type>clearinghouse</dc:type>\n" +
        "      <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "      </ows:WGS84BoundingBox>\n" +
        "    </csw:BriefRecord>\n" +
        "  </csw:InsertResult>\n" +
        "</csw:TransactionResponse>\n";

        StringWriter sw = new StringWriter();
        marshaller.marshal(request, sw);
        String result = sw.toString();

        assertXmlEquals(expResult, result, "xmlns:*");
        pool.recycle(marshaller);
    }

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void transactionResponseUnmarshalingTest() throws JAXBException {
        Unmarshaller unmarshaller = pool.acquireUnmarshaller();

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<csw:TransactionResponse version=\"2.0.2\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:ows=\"http://www.opengis.net/ows\">\n" +
        "  <csw:TransactionSummary requestId=\"rid\">\n" +
        "    <csw:totalInserted>2</csw:totalInserted>\n" +
        "    <csw:totalUpdated>0</csw:totalUpdated>\n" +
        "    <csw:totalDeleted>1</csw:totalDeleted>\n" +
        "  </csw:TransactionSummary>\n" +
        "  <csw:InsertResult>\n" +
        "    <csw:BriefRecord>\n" +
        "      <dc:identifier>{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}</dc:identifier>\n" +
        "      <dc:title>(JASON-1)</dc:title>\n" +
        "      <dc:type>clearinghouse</dc:type>\n" +
        "      <ows:WGS84BoundingBox dimensions=\"2\">\n" +
        "        <ows:LowerCorner>180.0 -66.0000000558794</ows:LowerCorner>\n" +
        "        <ows:UpperCorner>-180.0 65.9999999720603</ows:UpperCorner>\n" +
        "      </ows:WGS84BoundingBox>\n" +
        "    </csw:BriefRecord>\n" +
        "  </csw:InsertResult>\n" +
        "</csw:TransactionResponse>\n";

        TransactionResponseType result = ((JAXBElement<TransactionResponseType>) unmarshaller.unmarshal(new StringReader(xml))).getValue();

        SimpleLiteral id         = new SimpleLiteral("{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}");
        SimpleLiteral title      = new SimpleLiteral("(JASON-1)");
        SimpleLiteral type       = new SimpleLiteral("clearinghouse");

        List<BoundingBoxType> bbox = new ArrayList<>();
        bbox.add(new WGS84BoundingBoxType(180, -66.0000000558794, -180, 65.9999999720603));

        Object record = new BriefRecordType(id, title, type, bbox);

        final List<InsertResultType> inserteds = new ArrayList<>();
        inserteds.add(new InsertResultType(Arrays.asList(record), null));

        TransactionSummaryType summ = new TransactionSummaryType(2, 0, 1, "rid");
        TransactionResponseType expResult = new TransactionResponseType(summ, inserteds, "2.0.2");

        assertEquals(expResult.getTransactionSummary(), result.getTransactionSummary());
        assertEquals(expResult.getInsertResult(), result.getInsertResult());
        assertEquals(expResult, result);

        pool.recycle(unmarshaller);
    }
}
