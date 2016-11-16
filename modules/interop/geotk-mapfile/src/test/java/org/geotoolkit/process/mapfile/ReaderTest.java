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
package org.geotoolkit.process.mapfile;

import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.filter.FilterFactory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.style.DefaultStyleFactory;
import java.util.Collection;
import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import org.junit.Test;

import static org.geotoolkit.test.Assert.*;
import org.opengis.feature.Feature;

/**
 * Test mapfile reader
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ReaderTest extends org.geotoolkit.test.TestBase {

    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public ReaderTest() {
    }

    @Test
    public void testIsEqualTo() throws IOException {

        final URL resource = ReaderTest.class.getResource("/org/geotoolkit/mapfile/sample.map");
        final MapfileReader reader = new MapfileReader();
        reader.setInput(resource);

        final Feature feature = reader.read();
        assertNotNull(feature);
        assertEquals(MapfileTypes.MAP, feature.getType());


//check values
//╔════════════════════════════════════════════════════╤══════════════════════════════════════════════════════════╗
//║ name                                               │  value                                                   ║
//╟────────────────────────────────────────────────────┼──────────────────────────────────────────────────────────╢
//║                                                    │                                                          ║
//║   ├─{http://mapserver.org}CONFIG                   │ MS_ERRORFILE" "stderr                                    ║
//║   ├─{http://mapserver.org}DEBUG                    │ 1                                                        ║
//║   ├─{http://mapserver.org}EXTENT                   │ -100 100 -50 50                                          ║
//║   ├─{http://mapserver.org}FONTSET                  │ fonts.lst                                                ║
//║   ├─{http://mapserver.org}IMAGECOLOR               │ java.awt.Color[r=150,g=180,b=200]                        ║
//║   ├─{http://mapserver.org}IMAGETYPE                │ png                                                      ║
        assertEquals("PROJ_LIB\" \"/opt/mapserver/mapserver-utils-imposm-branch",  feature.getProperty("CONFIG").getValue());
        assertEquals("1",                               feature.getProperty("DEBUG").getValue());
        assertEquals("-100 100 -50 50",                 feature.getProperty("EXTENT").getValue());
        assertEquals("fonts.lst",                       feature.getProperty("FONTSET").getValue());
        assertEquals(new Color(150, 180, 200),          feature.getProperty("IMAGECOLOR").getValue());
        assertEquals("png",                             feature.getProperty("IMAGETYPE").getValue());

//║   ├─{http://mapserver.org}LAYER[0]                 │                                                          ║
//║   │   ├─{http://mapserver.org}CLASS                │                                                          ║
//║   │   │   └─{http://mapserver.org}STYLE            │                                                          ║
//║   │   │       ├─{http://mapserver.org}COLOR        │ #CDCBC6                                                  ║
//║   │   │       └─{http://mapserver.org}WIDTH        │ 0.5                                                      ║
//║   │   ├─{http://mapserver.org}DATA                 │ data/boundaries.shp                                      ║
//║   │   ├─{http://mapserver.org}GROUP                │ default                                                  ║
//║   │   ├─{http://mapserver.org}MAXSCALEDENOM        │ 9.9999999999E10                                          ║
//║   │   ├─{http://mapserver.org}MINSCALEDENOM        │ 30000                                                    ║
//║   │   ├─{http://mapserver.org}NAME                 │ borders0                                                 ║
//║   │   ├─{http://mapserver.org}PROJECTION           │ null"+init=epsg:4326"                                    ║
//║   │   ├─{http://mapserver.org}STATUS               │ ON                                                       ║
//║   │   └─{http://mapserver.org}TYPE                 │ LINE                                                     ║
        final Collection<Feature> layers = (Collection<Feature>) feature.getPropertyValue("LAYER");
        assertEquals(2, layers.size());

        Feature layer = (Feature) layers.iterator().next();
        assertEquals(MapfileTypes.LAYER,                layer.getType());
        assertEquals("data/boundaries.shp",             layer.getProperty("DATA").getValue());
        assertEquals("default",                         layer.getProperty("GROUP").getValue());
        assertEquals(9.9999999999E10d ,                 layer.getProperty("MAXSCALEDENOM").getValue());
        assertEquals(30000d,                            layer.getProperty("MINSCALEDENOM").getValue());
        assertEquals("borders0",                        layer.getProperty("NAME").getValue());
        assertEquals("null\"+init=epsg:4326\"",         layer.getProperty("PROJECTION").getValue());
        assertEquals("ON",                              layer.getProperty("STATUS").getValue());
        assertEquals("LINE",                            layer.getProperty("TYPE").getValue());

        Feature clazz = ((Collection<Feature>) layer.getPropertyValue("CLASS")).iterator().next();
        assertEquals(MapfileTypes.CLASS,                clazz.getType());

        Feature style = (Feature) clazz.getPropertyValue("STYLE");
        assertEquals(MapfileTypes.STYLE,                style.getType());
        assertEquals(FF.literal("#CDCBC6"),             style.getProperty("COLOR").getValue());
        assertEquals(FF.literal(0.5),                   style.getProperty("WIDTH").getValue());



//║   ├─{http://mapserver.org}LAYER[1]                 │                                                          ║
//║   │   ├─{http://mapserver.org}CLASS                │                                                          ║
//║   │   │   ├─{http://mapserver.org}EXPRESSION       │ 'continents'                                             ║
//║   │   │   └─{http://mapserver.org}LABEL            │                                                          ║
//║   │   │       ├─{http://mapserver.org}BUFFER       │ 4                                                        ║
//║   │   │       ├─{http://mapserver.org}COLOR        │ java.awt.Color[r=100,g=100,b=100]                        ║
//║   │   │       ├─{http://mapserver.org}ENCODING     │ utf-8                                                    ║
//║   │   │       ├─{http://mapserver.org}FONT         │ scb                                                      ║
//║   │   │       ├─{http://mapserver.org}OUTLINECOLOR │ -1 -1 -1                                                 ║
//║   │   │       ├─{http://mapserver.org}OUTLINEWIDTH │ 1                                                        ║
//║   │   │       ├─{http://mapserver.org}PARTIALS     │ false                                                    ║
//║   │   │       ├─{http://mapserver.org}POSITION     │ cc                                                       ║
//║   │   │       ├─{http://mapserver.org}SIZE         │ 8                                                        ║
//║   │   │       └─{http://mapserver.org}POSITION     │ cc                                                       ║
//║   │   │       ├─{http://mapserver.org}TYPE         │ TRUETYPE                                                 ║
//║   │   ├─{http://mapserver.org}CLASSITEM            │ 'type'                                                   ║
//║   │   ├─{http://mapserver.org}CONNECTION           │ host=server dbname=osm user=me password=secret port=5432 ║
//║   │   ├─{http://mapserver.org}CONNECTIONTYPE       │ postgis                                                  ║
//║   │   ├─{http://mapserver.org}DATA                 │ geometry from (select * from osm_places)                 ║
//║   │   ├─{http://mapserver.org}GROUP                │ default                                                  ║
//║   │   ├─{http://mapserver.org}LABELITEM            │ 'name'                                                   ║
//║   │   ├─{http://mapserver.org}MAXSCALEDENOM        │ 9.9999999999E10                                          ║
//║   │   ├─{http://mapserver.org}MINSCALEDENOM        │ 3.32808204E8                                             ║
//║   │   ├─{http://mapserver.org}NAME                 │ places0                                                  ║
//║   │   ├─{http://mapserver.org}STATUS               │ ON                                                       ║
//║   │   └─{http://mapserver.org}TYPE                 │ ANNOTATION                                               ║
        final Iterator ite = layers.iterator();
        ite.next();
        layer = (Feature) ite.next();
        assertEquals(MapfileTypes.LAYER,                layer.getType());
        assertEquals(FF.property("type"),               layer.getProperty("CLASSITEM").getValue());
        assertEquals("host=server dbname=osm user=me password=secret port=5432",layer.getProperty("CONNECTION").getValue());
        assertEquals("postgis",                         layer.getProperty("CONNECTIONTYPE").getValue());
        assertEquals("geometry from (select * from osm_places)",layer.getProperty("DATA").getValue());
        assertEquals("default",                         layer.getProperty("GROUP").getValue());
        assertEquals(FF.property("name"),               layer.getProperty("LABELITEM").getValue());
        assertEquals(9.9999999999E10 ,                  layer.getProperty("MAXSCALEDENOM").getValue());
        assertEquals(3.32808204E8,                      layer.getProperty("MINSCALEDENOM").getValue());
        assertEquals("places0",                         layer.getProperty("NAME").getValue());
        assertEquals("ON",                              layer.getProperty("STATUS").getValue());
        assertEquals("ANNOTATION",                      layer.getProperty("TYPE").getValue());

        clazz = ((Collection<Feature>) layer.getPropertyValue("CLASS")).iterator().next();
        assertEquals(MapfileTypes.CLASS,                clazz.getType());
        assertEquals("continents",                      clazz.getProperty("EXPRESSION").getValue());

        Feature label = (Feature) clazz.getPropertyValue("LABEL");
        assertEquals(MapfileTypes.LABEL,                label.getType());
        assertEquals(4,                                 label.getProperty("BUFFER").getValue());
        assertEquals(SF.literal(new Color(100,100,100)),label.getProperty("COLOR").getValue());
        assertEquals("utf-8",                           label.getProperty("ENCODING").getValue());
        assertEquals("scb",                             label.getProperty("FONT").getValue());
        assertEquals(SF.literal(Color.WHITE),           label.getProperty("OUTLINECOLOR").getValue());
        assertEquals(1,                                 label.getProperty("OUTLINEWIDTH").getValue());
        assertEquals(false,                             label.getProperty("PARTIALS").getValue());
        assertEquals("cc",                              label.getProperty("POSITION").getValue());
        assertEquals(FF.literal(8),                     label.getProperty("SIZE").getValue());
        assertEquals("TRUETYPE",                        label.getProperty("TYPE").getValue());



//║   ├─{http://mapserver.org}MAXSIZE                  │ 4000                                                     ║
//║   ├─{http://mapserver.org}PROJECTION               │ null"init=epsg:3857"                                     ║
//║   ├─{http://mapserver.org}SIZE                     │ 800 800                                                  ║
//║   ├─{http://mapserver.org}UNITS                    │ meters                                                   ║
        assertEquals(4000,                              feature.getProperty("MAXSIZE").getValue());
        assertEquals("null\"init=epsg:3857\"",          feature.getProperty("PROJECTION").getValue());
        assertEquals("800 800",                         feature.getProperty("SIZE").getValue());
        assertEquals("meters",                          feature.getProperty("UNITS").getValue());


//║   ├─{http://mapserver.org}WEB                      │                                                          ║
//║   │   ├─{http://mapserver.org}IMAGEPATH            │ /opt/mapserver/htdocs/tmp/                               ║
//║   │   ├─{http://mapserver.org}IMAGEURL             │ /tmp                                                     ║
//║   │   └─{http://mapserver.org}METADATA             │ null"ows_enable_request" "*"                             ║
//╚════════════════════════════════════════════════════╧══════════════════════════════════════════════════════════╝
        Feature web = (Feature) feature.getPropertyValue("WEB");
        assertEquals(MapfileTypes.WEB,                  web.getType());
        assertEquals("/opt/mapserver/htdocs/tmp/",      web.getProperty("IMAGEPATH").getValue());
        assertEquals("/tmp",                            web.getProperty("IMAGEURL").getValue());
        assertEquals("null\"ows_enable_request\" \"*\"",web.getProperty("METADATA").getValue());

    }

}
