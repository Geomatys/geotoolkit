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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.gx.model.AbstractTourPrimitive;
import org.geotoolkit.data.gx.model.AnimatedUpdate;
import org.geotoolkit.data.gx.model.EnumFlyToMode;
import org.geotoolkit.data.gx.model.EnumPlayMode;
import org.geotoolkit.data.gx.model.FlyTo;
import org.geotoolkit.data.gx.model.GxModelConstants;
import org.geotoolkit.data.gx.model.LatLonQuad;
import org.geotoolkit.data.gx.model.PlayList;
import org.geotoolkit.data.gx.model.SoundCue;
import org.geotoolkit.data.gx.model.TourControl;
import org.geotoolkit.data.gx.model.Wait;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.Extensions.Names;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.TimeSpan;
import org.geotoolkit.data.kml.model.TimeStamp;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xml.KmlExtensionWriter;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.xml.StaxStreamWriter;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import static org.geotoolkit.data.gx.xml.GxConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class GxWriter extends StaxStreamWriter implements KmlExtensionWriter {

    private String URI_KML = KmlConstants.URI_KML_2_2;
    private final KmlWriter kmlWriter;
    public Map<Extensions.Names, List<Object>> complexTable = new HashMap<Extensions.Names, List<Object>>();
    public Map<Extensions.Names, List<String>> simpleTable = new HashMap<Extensions.Names, List<String>>();

    /**
     *
     */
    public GxWriter(KmlWriter w){
        super();
        this.initComplexTable();
        this.initSimpleTable();
        this.kmlWriter = w;
    }

    /**
     * <p>Set output. This method use kml uri version 2.2.</p>
     *
     * @param output
     * @throws XMLStreamException
     * @throws IOException
     */
    @Override
    public void setOutput(Object output) throws XMLStreamException, IOException{
        super.setOutput(output);
//        try {
//            this.kmlWriter.setOutput(writer, URI_KML);
//        } catch (KmlException ex) {
//            Logger.getLogger(GxWriter.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    /**
     *
     * @param tour
     * @throws XMLStreamException
     * @throws KmlException
     */
    public void writeTour(Feature tour)
            throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_GX, TAG_TOUR);
        this.kmlWriter.writeCommonAbstractFeature(tour);
        if(tour.getProperties(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()) != null){
            Iterator i = tour.getProperties(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()).iterator();
            while(i.hasNext()){
                this.writePlayList((PlayList) ((Property) i.next()).getValue());
            }
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param playlist
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writePlayList(PlayList playlist) throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_GX, TAG_PLAYLIST);
        for(AbstractTourPrimitive tourPrimitive : playlist.getTourPrimitives()){
            this.writeAbstractTourPrimitive(tourPrimitive);
        }
        writer.writeEndElement();
    }

    private void writeAbstractTourPrimitive(AbstractTourPrimitive tourPrimitive) throws XMLStreamException, KmlException{
        if(tourPrimitive instanceof FlyTo){
            this.writeFlyTo((FlyTo) tourPrimitive);
        } else if (tourPrimitive instanceof AnimatedUpdate){
            this.writeAnimatedUpdate((AnimatedUpdate) tourPrimitive);
        } else if (tourPrimitive instanceof TourControl){
            this.writeTourControl((TourControl) tourPrimitive);
        } else if (tourPrimitive instanceof Wait){
            this.writeWait((Wait) tourPrimitive);
        } else if (tourPrimitive instanceof SoundCue){
            this.writeSoundCue((SoundCue) tourPrimitive);
        }
    }

    private void writeCommonAbstractTourPrimitive(AbstractTourPrimitive tourPrimitive) throws XMLStreamException{
        this.kmlWriter.writeCommonAbstractObject(tourPrimitive);
    }

    private void writeFlyTo(FlyTo flyTo) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_GX, TAG_FLY_TO);
        writeCommonAbstractTourPrimitive(flyTo);
        if(KmlUtilities.isFiniteNumber(flyTo.getDuration())){
            this.writeDuration(flyTo.getDuration());
        }
        if (flyTo.getFlyToMode() != null){
            this.writeFlyToMode(flyTo.getFlyToMode());
        }
        if(flyTo.getView() != null){
            this.kmlWriter.writeAbstractView(flyTo.getView());
        }
        writer.writeEndElement();
    }

    private void writeAnimatedUpdate(AnimatedUpdate animatedUpdate) throws XMLStreamException, KmlException {
        writer.writeStartElement(URI_GX, TAG_ANIMATED_UPDATE);
        writeCommonAbstractTourPrimitive(animatedUpdate);
        if(KmlUtilities.isFiniteNumber(animatedUpdate.getDuration())){
            this.writeDuration(animatedUpdate.getDuration());
        }
        if(animatedUpdate.getUpdate() != null){
            this.kmlWriter.writeUpdate(animatedUpdate.getUpdate());
        }
        writer.writeEndElement();
    }

    private void writeTourControl(TourControl tourControl) throws XMLStreamException {
        writer.writeStartElement(URI_GX, TAG_TOUR_CONTROL);
        writeCommonAbstractTourPrimitive(tourControl);
        if(tourControl.getPlayMode() != null){
            this.writePlayMode(tourControl.getPlayMode());
        }
        writer.writeEndElement();
    }

    private void writeWait(Wait wait) throws XMLStreamException {
        writer.writeStartElement(URI_GX, TAG_WAIT);
        writeCommonAbstractTourPrimitive(wait);
        if(KmlUtilities.isFiniteNumber(wait.getDuration())){
            this.writeDuration(wait.getDuration());
        }
        writer.writeEndElement();
    }

    private void writeSoundCue(SoundCue soundCue) throws XMLStreamException {
        writer.writeStartElement(URI_GX, TAG_SOUND_CUE);
        writeCommonAbstractTourPrimitive(soundCue);
        if(soundCue.getHref() != null){
            System.out.println(soundCue.getHref());
            this.kmlWriter.writeHref(soundCue.getHref());
        }
        writer.writeEndElement();
    }

    public void writeLatLonQuad(LatLonQuad latLonQuad) throws XMLStreamException {
        writer.writeStartElement(URI_GX, TAG_LAT_LON_QUAD);
        kmlWriter.writeCommonAbstractObject(latLonQuad);
        if(latLonQuad.getCoordinates() != null){
            kmlWriter.writeCoordinates(latLonQuad.getCoordinates());
        }
        writer.writeEndElement();
    }

    public void writeTimeSpan(TimeSpan timeSpan) throws XMLStreamException{
        writer.writeStartElement(URI_GX, TAG_TIME_SPAN);
        kmlWriter.writeCommonAbstractTimePrimitive(timeSpan);
        if (timeSpan.getBegin() != null){
            kmlWriter.writeBegin(timeSpan.getBegin());
        }
        if (timeSpan.getEnd() != null){
            kmlWriter.writeEnd(timeSpan.getEnd());
        }
        if (timeSpan.extensions().simples(Names.TIME_SPAN) != null){
        }
        if (timeSpan.extensions().complexes(Names.TIME_SPAN) != null){
        }
        writer.writeEndElement();
    }

    public void writeTimeStamp(TimeStamp timeStamp) throws XMLStreamException{
        writer.writeStartElement(URI_GX, TAG_TIME_STAMP);
        kmlWriter.writeCommonAbstractTimePrimitive(timeStamp);
        if (timeStamp.getWhen() != null){
            kmlWriter.writeWhen(timeStamp.getWhen());
        }
        if (timeStamp.extensions().simples(Names.TIME_STAMP) != null){
        }
        if (timeStamp.extensions().complexes(Names.TIME_STAMP) != null){
        }
        writer.writeEndElement();
    }

    private void writeDuration(double duration) throws XMLStreamException {
        if (DEF_DURATION != duration){
            writer.writeStartElement(URI_GX, TAG_DURATION);
            this.writer.writeCharacters(Double.toString(duration));
            writer.writeEndElement();
        }
    }

    private void writeFlyToMode(EnumFlyToMode enumFlyToMode) throws XMLStreamException {
        if(!DEF_FLY_TO_MODE.equals(enumFlyToMode)){
            writer.writeStartElement(URI_GX, TAG_FLY_TO_MODE);
            this.writer.writeCharacters(enumFlyToMode.getFlyToMode());
            writer.writeEndElement();
        }
    }

    private void writePlayMode(EnumPlayMode playMode) throws XMLStreamException {
        writer.writeStartElement(URI_GX, TAG_PLAY_MODE);
        this.writer.writeCharacters(playMode.getPlayMode());
        writer.writeEndElement();
    }

    public void writeAltitudeMode(AltitudeMode altitudeMode) throws XMLStreamException{
        if(DEF_ALTITUDE_MODE != altitudeMode){
            writer.writeStartElement(URI_GX, TAG_ALTITUDE_MODE);
            writer.writeCharacters(altitudeMode.getAltitudeMode());
            writer.writeEndElement();
        }
    }

    public void writeBalloonVisibility(Boolean bv) throws XMLStreamException{
        boolean balloonVisibility = bv.booleanValue();
        if (DEF_BALLOON_VISIBILITY != balloonVisibility){
            writer.writeStartElement(URI_GX, TAG_BALLOON_VISIBILITY);
            if(balloonVisibility){
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    @Override
    public void writeComplexExtensionElement(Object contentsElement)
            throws XMLStreamException, KmlException {

        if(contentsElement instanceof Feature){
            Feature feature = (Feature) contentsElement;
            if(feature.getType().equals(GxModelConstants.TYPE_TOUR)){
                writeTour(feature);
            }
        } else if(contentsElement instanceof LatLonQuad){
            writeLatLonQuad((LatLonQuad) contentsElement);
        } else if(contentsElement instanceof TimeSpan){
            writeTimeSpan((TimeSpan) contentsElement);
        } else if(contentsElement instanceof TimeStamp){
            writeTimeStamp((TimeStamp) contentsElement);
        }
    }

    @Override
    public void writeSimpleExtensionElement(SimpleTypeContainer contentsElement)
            throws XMLStreamException, KmlException {

        if(TAG_BALLOON_VISIBILITY.equals(contentsElement.getTagName())){
            writeBalloonVisibility((Boolean) contentsElement.getValue());
        }
    }

    @Override
    public boolean canHandleComplex(Extensions.Names ext, Object contentObject){
        List<Object> liste = this.complexTable.get(ext);
        Boolean reponse = false;
        if(liste != null){
            if (contentObject instanceof Feature){
                if (liste.contains(((Feature) contentObject).getType())){
                    reponse = true;
                }
            } else {
                for(Object i : liste){
                    if(i instanceof Class
                            && ((Class) i).isInstance(contentObject)){
                        reponse = true;
                    }
                }
            }
        }
        return reponse;
    }

    @Override
    public boolean canHandleSimple(Names ext, String elementTag) {
        List<String> liste = this.simpleTable.get(ext);
        Boolean reponse = false;
        if(liste != null
                && liste.contains(elementTag)){
            reponse = true;
        }
        return reponse;
    }

    private void initComplexTable(){
        List<Object> documentExtensionsList = new ArrayList<Object>();
        documentExtensionsList.add(GxModelConstants.TYPE_TOUR);
        
        List<Object> groundOverlayExtensionsList = new ArrayList<Object>();
        groundOverlayExtensionsList.add(LatLonQuad.class);

        List<Object> abstractViewExtensionsList = new ArrayList<Object>();
        abstractViewExtensionsList.add(TimeSpan.class);
        abstractViewExtensionsList.add(TimeStamp.class);

        complexTable.put(Names.DOCUMENT, documentExtensionsList);
        complexTable.put(Names.VIEW, abstractViewExtensionsList);
        complexTable.put(Names.GROUND_OVERLAY, groundOverlayExtensionsList);

    }

    private void initSimpleTable(){
        List<String> featureExtensionsList = new ArrayList<String>();
        featureExtensionsList.add(GxConstants.TAG_BALLOON_VISIBILITY);

        simpleTable.put(Extensions.Names.FEATURE, featureExtensionsList);
    }
}
