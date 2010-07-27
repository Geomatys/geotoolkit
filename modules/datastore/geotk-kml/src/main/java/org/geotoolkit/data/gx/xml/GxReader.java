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
package org.geotoolkit.data.gx.xml;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.gx.DefaultGxFactory;
import org.geotoolkit.data.gx.GxFactory;
import org.geotoolkit.data.gx.model.AbstractTourPrimitive;
import org.geotoolkit.data.gx.model.AnimatedUpdate;
import org.geotoolkit.data.gx.model.EnumAltitudeMode;
import org.geotoolkit.data.gx.model.EnumFlyToMode;
import org.geotoolkit.data.gx.model.EnumPlayMode;
import org.geotoolkit.data.gx.model.FlyTo;
import org.geotoolkit.data.gx.model.LatLonQuad;
import org.geotoolkit.data.gx.model.PlayList;
import org.geotoolkit.data.gx.model.SoundCue;
import org.geotoolkit.data.gx.model.TourControl;
import org.geotoolkit.data.gx.model.Wait;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.AbstractObject;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.AbstractTimePrimitive;
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.model.TimeSpan;
import org.geotoolkit.data.kml.model.TimeStamp;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xml.KmlExtensionReader;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.temporal.object.FastDateParser;
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.xml.StaxStreamReader;
import org.opengis.feature.Feature;
import static org.geotoolkit.data.gx.xml.GxConstants.*;
import static java.util.Collections.EMPTY_MAP;

/**
 *
 * @author Samuel Andrés
 */
public class GxReader extends StaxStreamReader implements KmlExtensionReader {

    private String URI_KML = KmlConstants.URI_KML_2_2;
    private static final GxFactory gxFactory = new DefaultGxFactory();
    private static final KmlFactory kmlFactory = new DefaultKmlFactory();
    private final KmlReader kmlReader = new KmlReader();
    private final FastDateParser fastDateParser = new FastDateParser();
    public Map<String, Map<String, Extensions.Names>> complexTable;
    public Map<String, Map<String, Extensions.Names>> simpleTable;

    public GxReader(){
        super();
        initComplexTable();
        initSimpleTable();
    }

    /**
     * <p>Set input (use KML 2.2).</p>
     *
     * @param input
     * @throws IOException
     * @throws XMLStreamException
     */
    @Override
    public void setInput(Object input) 
            throws IOException, XMLStreamException {
        
        super.setInput(input);
        try {
            this.kmlReader.setInput(reader, URI_KML);
        } catch (KmlException ex) {
            Logger.getLogger(GxReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private AnimatedUpdate readAnimatedUpdate() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = null;
        IdAttributes idAttributes = kmlReader.readIdAttributes();

        // AnimatedUpdate
        double duration = DEF_DURATION;
        Update update = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_GX.equals(eUri)) {
                        if (TAG_DURATION.equals(eName)) {
                            duration = parseDouble(reader.getElementText());
                        }
                    } else if (URI_KML.equals(eUri)) {
                        if (KmlConstants.TAG_UPDATE.equals(eName)) {
                            update = this.kmlReader.readUpdate();
                        }
                    }

                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ANIMATED_UPDATE.equals(reader.getLocalName())
                            && URI_GX.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        AnimatedUpdate u;
        return GxReader.gxFactory.createAnimatedUpdate(objectSimpleExtensions,
                idAttributes,duration, update);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    public Feature readTour() 
            throws XMLStreamException, KmlException, URISyntaxException {

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = null;
        IdAttributes idAttributes = kmlReader.readIdAttributes();

        // AbstractFeature
        String name = null;
        boolean visibility = KmlConstants.DEF_VISIBILITY;
        boolean open = KmlConstants.DEF_OPEN;
        AtomPersonConstruct author = null;
        AtomLink link = null;
        String address = null;
        AddressDetails addressDetails = null;
        String phoneNumber = null;
        Object snippet = null;
        Object description = null;
        AbstractView view = null;
        AbstractTimePrimitive timePrimitive = null;
        URI styleUrl = null;
        List<AbstractStyleSelector> styleSelector = new ArrayList<AbstractStyleSelector>();
        Region region = null;
        Object extendedData = null;
        List<SimpleTypeContainer> featureSimpleExtensions = null;
        List<AbstractObject> featureObjectExtensions = null;

        // Tour
        List<PlayList> playlists = new ArrayList<PlayList>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        // ABSTRACT FEATURE
                        if (KmlConstants.TAG_NAME.equals(eName)) {
                            name = reader.getElementText();
                        } else if (KmlConstants.TAG_VISIBILITY.equals(eName)) {
                            visibility = parseBoolean(reader.getElementText());
                        } else if (KmlConstants.TAG_OPEN.equals(eName)) {
                            open = parseBoolean(reader.getElementText());
                        } else if (KmlConstants.TAG_ADDRESS.equals(eName)) {
                            address = reader.getElementText();
                        } else if (KmlConstants.TAG_PHONE_NUMBER.equals(eName)) {
                            phoneNumber = reader.getElementText();
                        } else if (KmlConstants.TAG_SNIPPET.equals(eName)) {
                            snippet = kmlReader.readElementText();
                        } else if (KmlConstants.TAG_SNIPPET_BIG.equals(eName)) {
                            snippet = kmlReader.readSnippet();
                        } else if (KmlConstants.TAG_DESCRIPTION.equals(eName)) {
                            description = kmlReader.readElementText();
                        } else if (kmlReader.isAbstractView(eName)) {
                            view = kmlReader.readAbstractView(eName);
                        } else if (kmlReader.isAbstractTimePrimitive(eName)) {
                            timePrimitive = kmlReader.readAbstractTimePrimitive(eName);
                        } else if (KmlConstants.TAG_STYLE_URL.equals(eName)) {
                            styleUrl = new URI(reader.getElementText());
                        } else if (kmlReader.isAbstractStyleSelector(eName)) {
                            styleSelector.add(kmlReader.readAbstractStyleSelector(eName));
                        } else if (KmlConstants.TAG_REGION.equals(eName)) {
                            region = kmlReader.readRegion();
                        } else if (KmlConstants.TAG_EXTENDED_DATA.equals(eName)) {
                            extendedData = kmlReader.readExtendedData();
                        } else if (KmlConstants.TAG_META_DATA.equals(eName)) {
                            extendedData = kmlReader.readMetaData();
                        }
                    } else if (KmlConstants.URI_ATOM.equals(eUri)) {
                        kmlReader.checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (KmlConstants.TAG_ATOM_AUTHOR.equals(eName)) {
                            author = kmlReader.readAtomPersonConstruct();
                        } else if (KmlConstants.TAG_ATOM_LINK.equals(eName)) {
                            link = kmlReader.readAtomLink();
                        }
                    } else if (KmlConstants.URI_XAL.equals(eUri)) {
                        kmlReader.checkVersion(URI_KML_2_2);
                        // ABSTRACT FEATURE
                        if (KmlConstants.TAG_XAL_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails = kmlReader.readXalAddressDetails();
                        }
                    } else if (URI_GX.equals(eUri)){
                        if(TAG_PLAYLIST.equals(eName)){
                            playlists.add(this.readPlayList());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_TOUR.equals(reader.getLocalName())
                            && URI_GX.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return GxReader.gxFactory.createTour(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive, styleUrl,
                styleSelector, region, extendedData, featureSimpleExtensions,
                featureObjectExtensions, playlists);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private PlayList readPlayList() 
            throws XMLStreamException, KmlException, URISyntaxException{

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = null;
        IdAttributes idAttributes = kmlReader.readIdAttributes();

        // PlayList
        List<AbstractTourPrimitive> tourPrimitives = new ArrayList<AbstractTourPrimitive>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_GX.equals(eUri)) {
                        if (isAbstractTourPrimitive(eName)) {
                            tourPrimitives.add(this.readAbstractTourPrimitive(eName));
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_PLAYLIST.equals(reader.getLocalName())
                            && URI_GX.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return GxReader.gxFactory.createPlayList(
                objectSimpleExtensions, idAttributes, tourPrimitives);
    }

    /**
     *
     * @param eName
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private AbstractTourPrimitive readAbstractTourPrimitive(String eName)
            throws XMLStreamException, KmlException, URISyntaxException{

        AbstractTourPrimitive resultat = null;
        if (TAG_FLY_TO.equals(eName)){
            resultat = this.readFlyTo();
        } else if (TAG_ANIMATED_UPDATE.equals(eName)){
            resultat = this.readAnimatedUpdate();
        } else if (TAG_TOUR_CONTROL.equals(eName)){
            resultat = this.readTourControl();
        } else if (TAG_WAIT.equals(eName)){
            resultat = this.readWait();
        } else if (TAG_SOUND_CUE.equals(eName)){
            resultat = this.readSoundCue();
        }
        return resultat;
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private FlyTo readFlyTo() 
            throws XMLStreamException, KmlException, URISyntaxException{

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = null;
        IdAttributes idAttributes = kmlReader.readIdAttributes();

        // FlyTo
        double duration = DEF_DURATION;
        EnumFlyToMode flyToMode = DEF_FLY_TO_MODE;
        AbstractView view = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_GX.equals(eUri)) {
                        if (TAG_DURATION.equals(eName)) {
                            duration = parseDouble(reader.getElementText());
                        } else if (TAG_FLY_TO_MODE.equals(eName)) {
                            flyToMode = EnumFlyToMode.transform(reader.getElementText());
                        }
                    } else if (URI_KML.equals(eUri)) {
                        if (kmlReader.isAbstractView(eName)) {
                            view = this.kmlReader.readAbstractView(eName);
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_FLY_TO.equals(reader.getLocalName())
                            && URI_GX.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return GxReader.gxFactory.createFlyTo(objectSimpleExtensions, idAttributes,
                duration, flyToMode, view);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    private TourControl readTourControl() 
            throws XMLStreamException{
        
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = null;
        IdAttributes idAttributes = kmlReader.readIdAttributes();

        // TourControl
        EnumPlayMode playMode = DEF_PLAY_MODE;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_GX.equals(eUri)) {
                        if (TAG_PLAY_MODE.equals(eName)) {
                            playMode = EnumPlayMode.transform(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_TOUR_CONTROL.equals(reader.getLocalName())
                            && URI_GX.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return GxReader.gxFactory.createTourControl(objectSimpleExtensions, idAttributes, playMode);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    private Wait readWait() throws XMLStreamException{
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = null;
        IdAttributes idAttributes = kmlReader.readIdAttributes();

        // Wait
        double duration = DEF_DURATION;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_GX.equals(eUri)) {
                        if (TAG_DURATION.equals(eName)) {
                            duration = parseDouble(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_FLY_TO.equals(reader.getLocalName())
                            && URI_GX.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return GxReader.gxFactory.createWait(objectSimpleExtensions, idAttributes, duration);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    private SoundCue readSoundCue() 
            throws XMLStreamException{
        
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = null;
        IdAttributes idAttributes = kmlReader.readIdAttributes();

        // SoundCue
        String href = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        if (KmlConstants.TAG_HREF.equals(eName)) {
                            href = reader.getElementText();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_SOUND_CUE.equals(reader.getLocalName())
                            && URI_GX.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return GxReader.gxFactory.createSoundCue(objectSimpleExtensions, idAttributes, href);
    }

    public LatLonQuad readLatLonQuad() 
            throws XMLStreamException{

        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = null;
        IdAttributes idAttributes = kmlReader.readIdAttributes();

        // LatLonQuad
        Coordinates coordinates = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        if (KmlConstants.TAG_COORDINATES.equals(eName)) {
                            coordinates = kmlReader.readCoordinates(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_SOUND_CUE.equals(reader.getLocalName())
                            && URI_GX.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return GxReader.gxFactory.createLatLonQuad(objectSimpleExtensions, idAttributes, coordinates);
    }

    public TimeSpan readTimeSpan() 
            throws XMLStreamException{
        
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = null;
        IdAttributes idAttributes = kmlReader.readIdAttributes();

        // AbstractTimePrimitive
        List<SimpleTypeContainer> AbstractTimePrimitiveSimpleExtensions = null;
        List<Object> AbstractTimePrimitiveObjectExtensions = null;

        // TimeSpan
        Calendar begin = null;
        Calendar end = null;
        List<SimpleTypeContainer> TimeSpanSimpleExtensions = null;
        List<Object> TimeSpanObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        if (KmlConstants.TAG_BEGIN.equals(eName)) {
                            begin = (Calendar) fastDateParser.getCalendar(reader.getElementText()).clone();
                        } else if (KmlConstants.TAG_END.equals(eName)) {
                            end = (Calendar) fastDateParser.getCalendar(reader.getElementText()).clone();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_TIME_SPAN.equals(reader.getLocalName())
                            && URI_GX.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return kmlFactory.createTimeSpan(objectSimpleExtensions, idAttributes,
                AbstractTimePrimitiveSimpleExtensions, AbstractTimePrimitiveObjectExtensions,
                begin, end, TimeSpanSimpleExtensions, TimeSpanObjectExtensions);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    public TimeStamp readTimeStamp() 
            throws XMLStreamException {
        
        // AbstractObject
        List<SimpleTypeContainer> objectSimpleExtensions = null;
        IdAttributes idAttributes = kmlReader.readIdAttributes();

        // AbstractTimePrimitive
        List<SimpleTypeContainer> AbstractTimePrimitiveSimpleExtensions = null;
        List<Object> AbstractTimePrimitiveObjectExtensions = null;

        // TimeStamp
        Calendar when = null;
        List<SimpleTypeContainer> TimeStampSimpleExtensions = null;
        List<Object> TimeStampObjectExtensions = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_KML.equals(eUri)) {
                        if (KmlConstants.TAG_WHEN.equals(eName)) {
                            when = (Calendar) fastDateParser.getCalendar(reader.getElementText()).clone();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_TIME_STAMP.equals(reader.getLocalName()) 
                            && URI_GX.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return kmlFactory.createTimeStamp(objectSimpleExtensions, idAttributes,
                AbstractTimePrimitiveSimpleExtensions, AbstractTimePrimitiveObjectExtensions,
                when, TimeStampSimpleExtensions, TimeStampObjectExtensions);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    public AltitudeMode readAltitudeMode() throws XMLStreamException{
        return EnumAltitudeMode.transform(reader.getElementText());
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    public boolean readBalloonVisibility() throws XMLStreamException {
        return parseBoolean(reader.getElementText());
    }

    /**
     * 
     * @param eName
     * @return
     */
    public boolean isAbstractTourPrimitive(String eName) {
        return (TAG_FLY_TO.equals(eName)
                || TAG_ANIMATED_UPDATE.equals(eName)
                || TAG_TOUR_CONTROL.equals(eName)
                || TAG_WAIT.equals(eName)
                || TAG_SOUND_CUE.equals(eName));
    }

    /*
     *
     * Implements KmlExtensionReader
     */

    @Override
    public Extensions.Names getComplexExtensionLevel(String containingTag, String contentsTag) {
        try {
            return this.complexTable.get(contentsTag).get(containingTag);
        } catch (NullPointerException e){
            return null;
        }
    }

    @Override
    public Extensions.Names getSimpleExtensionLevel(String containingTag, String contentsTag) {
        try {
            return this.simpleTable.get(contentsTag).get(containingTag);
        } catch (NullPointerException e){
            return null;
        }
    }

    @Override
    public Object readExtensionElement(String containingTag, String contentsTag)
            throws XMLStreamException, KmlException, URISyntaxException {
        Object resultat = null;
        if(GxConstants.TAG_ALTITUDE_MODE.equals(contentsTag)){
            resultat = readAltitudeMode();
        } else if(GxConstants.TAG_BALLOON_VISIBILITY.equals(contentsTag)){
            resultat = kmlFactory.createSimpleTypeContainer(URI_GX, TAG_BALLOON_VISIBILITY,readBalloonVisibility());
        } else if (GxConstants.TAG_LAT_LON_QUAD.equals(contentsTag)){
            resultat = readLatLonQuad();
        } else if (GxConstants.TAG_TIME_SPAN.equals(contentsTag)){
            resultat = readTimeSpan();
        } else if (GxConstants.TAG_TIME_STAMP.equals(contentsTag)){
            resultat = readTimeStamp();
        } else if(GxConstants.TAG_TOUR.equals(contentsTag)){
            resultat = readTour();
        }
        return resultat;
    }

    private void initComplexTable(){
        // Tour peut se trouver dans toute extension d'abstractObject... à compléter.
        Map<String, Extensions.Names> tourBinding = new HashMap<String, Extensions.Names>();
        tourBinding.put( KmlConstants.TAG_DOCUMENT, Extensions.Names.DOCUMENT);

        Map<String, Extensions.Names> latLonQuadBinding = new HashMap<String, Extensions.Names>();
        latLonQuadBinding.put( KmlConstants.TAG_GROUND_OVERLAY, Extensions.Names.GROUND_OVERLAY);

        Map<String, Extensions.Names> timeSpan = new HashMap<String, Extensions.Names>();
        timeSpan.put( KmlConstants.TAG_CAMERA, Extensions.Names.VIEW);
        timeSpan.put( KmlConstants.TAG_LOOK_AT, Extensions.Names.VIEW);

        Map<String, Extensions.Names> timeStamp = new HashMap<String, Extensions.Names>();
        timeStamp.put( KmlConstants.TAG_CAMERA, Extensions.Names.VIEW);
        timeStamp.put( KmlConstants.TAG_LOOK_AT, Extensions.Names.VIEW);

        complexTable = new HashMap<String, Map<String, Extensions.Names>>();
        complexTable.put(GxConstants.TAG_ALTITUDE_MODE, EMPTY_MAP);
        complexTable.put(GxConstants.TAG_LAT_LON_QUAD, latLonQuadBinding);
        complexTable.put(GxConstants.TAG_TIME_SPAN, timeSpan);
        complexTable.put(GxConstants.TAG_TIME_STAMP, timeStamp);
        complexTable.put(GxConstants.TAG_TOUR, tourBinding);
    }

    private void initSimpleTable(){
        Map<String, Extensions.Names> balloonVisibilityBinding = new HashMap<String, Extensions.Names>();
        balloonVisibilityBinding.put(KmlConstants.TAG_NETWORK_LINK, Extensions.Names.FEATURE);
        balloonVisibilityBinding.put(KmlConstants.TAG_PLACEMARK, Extensions.Names.FEATURE);
        balloonVisibilityBinding.put(KmlConstants.TAG_FOLDER, Extensions.Names.FEATURE);
        balloonVisibilityBinding.put(KmlConstants.TAG_DOCUMENT, Extensions.Names.FEATURE);
        balloonVisibilityBinding.put(KmlConstants.TAG_GROUND_OVERLAY, Extensions.Names.FEATURE);
        balloonVisibilityBinding.put(KmlConstants.TAG_SCREEN_OVERLAY, Extensions.Names.FEATURE);
        balloonVisibilityBinding.put(KmlConstants.TAG_PHOTO_OVERLAY, Extensions.Names.FEATURE);

        Map<String, Extensions.Names> latLonQuadBinding = new HashMap<String, Extensions.Names>();
        latLonQuadBinding.put(KmlConstants.TAG_GROUND_OVERLAY, Extensions.Names.GROUND_OVERLAY);

        simpleTable = new HashMap<String, Map<String, Extensions.Names>>();
        simpleTable.put(GxConstants.TAG_BALLOON_VISIBILITY, balloonVisibilityBinding);
    }

}
