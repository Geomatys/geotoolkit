/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.gx;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.gx.model.GxModelConstants;
import org.geotoolkit.data.gx.model.PlayList;
import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.Delete;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.xml.DomCompare;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class Tour3Test {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/tour3.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public Tour3Test() {
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

    /*
     * Methode de test en lecture
     */
    @Test
    public void tour3ReadTest()
            throws IOException, XMLStreamException, URISyntaxException, KmlException {

        // Instanciation du reader Kml
        final KmlReader reader = new KmlReader();

        // Instanciation du reader d'extensions à partir du reader Kml
        final GxReader gxReader = new GxReader(reader);

        // Affectation au reader Kml du fichier de lecture et d'un reader d'extensions
        reader.setInput(new File(pathToTestFile));
        reader.addExtensionReader(gxReader);

        // Lecture, puis libération du reader Kml
        final Kml kmlObjects = reader.read();
        reader.dispose();

        // Vérification du contenu lu...
        final NetworkLinkControl networkLinkControl = kmlObjects.getNetworkLinkControl();
        final Update update = networkLinkControl.getUpdate();
        assertEquals(new URI("http://myserver.com/Bof.kml"), update.getTargetHref());

        assertEquals(1, update.getUpdates().size());
        assertTrue(update.getUpdates().get(0) instanceof Delete);

        final Delete delete = (Delete) update.getUpdates().get(0);
        assertEquals(1, delete.getFeatures().size());
        assertTrue(((Feature) delete.getFeatures().get(0)).getType()
                .equals(GxModelConstants.TYPE_TOUR));

        final Feature tour = (Feature) delete.getFeatures().get(0);
        final PlayList playlist = (PlayList) tour.getProperty(
                GxModelConstants.ATT_TOUR_PLAY_LIST.getName()).getValue();
        assertEquals(0, playlist.getTourPrimitives().size());

    }

    /*
     * Méthode de test en écriture
     */
    @Test
    public void tour3WriteTest() 
            throws KmlException, IOException, XMLStreamException,
            ParserConfigurationException, SAXException, URISyntaxException {

        // Récupération des instances de deux fabriques (Kml et extensions Gx)
        final GxFactory gxFactory = DefaultGxFactory.getInstance();
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        // Construction de l'objet Kml
        final PlayList playList = gxFactory.createPlayList();

        final Feature tour = gxFactory.createTour();
        Collection<Property> tourProperties = tour.getProperties();
        tourProperties.add(FF.createAttribute(
                playList, GxModelConstants.ATT_TOUR_PLAY_LIST, null));

        final Delete delete = kmlFactory.createDelete();
        delete.setFeatures(Arrays.asList(tour));

        final Update update = kmlFactory.createUpdate();
        update.setTargetHref(new URI("http://myserver.com/Bof.kml"));
        update.setUpdates(Arrays.asList((Object) delete));

        final NetworkLinkControl networkLinkControl = kmlFactory.createNetworkLinkControl();
        networkLinkControl.setUpdate(update);

        final Kml kml = kmlFactory.createKml(networkLinkControl, null, null, null);

        // Ajout de l'espace de nom des extensions avec un préfixe
        kml.addExtensionUri(GxConstants.URI_GX, "gx");

        // Création du fichier d'écriture
        final File temp = File.createTempFile("testTour3", ".kml");
        temp.deleteOnExit();

        // Instanciation du writer Kml
        final KmlWriter writer = new KmlWriter();

        // Instanciation du writer d'extensions à partir du writer Kml
        final GxWriter gxWriter = new GxWriter(writer);

        // Affectation du fichier de sortie et du writer d'extensions au writer Kml
        writer.setOutput(temp);
        writer.addExtensionWriter(GxConstants.URI_GX, gxWriter);

        // Écriture, puis libération du writer Kml
        writer.write(kml);
        writer.dispose();

        // Vérification du contenu écrit...
        DomCompare.compare(
                new File(pathToTestFile), temp);
    }
}