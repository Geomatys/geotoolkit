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

import com.vividsolutions.jts.geom.Coordinate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.gx.GxUtilities;
import org.geotoolkit.data.gx.model.AbstractTourPrimitive;
import org.geotoolkit.data.gx.model.Angles;
import org.geotoolkit.data.gx.model.AnimatedUpdate;
import org.geotoolkit.data.gx.model.EnumFlyToMode;
import org.geotoolkit.data.gx.model.EnumPlayMode;
import org.geotoolkit.data.gx.model.FlyTo;
import org.geotoolkit.data.gx.model.GxModelConstants;
import org.geotoolkit.data.gx.model.LatLonQuad;
import org.geotoolkit.data.gx.model.MultiTrack;
import org.geotoolkit.data.gx.model.PlayList;
import org.geotoolkit.data.gx.model.SoundCue;
import org.geotoolkit.data.gx.model.TourControl;
import org.geotoolkit.data.gx.model.Track;
import org.geotoolkit.data.gx.model.Wait;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.Extensions.Names;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.TimeSpan;
import org.geotoolkit.data.kml.model.TimeStamp;
import org.geotoolkit.data.kml.xml.KmlExtensionWriter;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.xml.StaxStreamWriter;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import static org.geotoolkit.data.gx.xml.GxConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class GxWriter extends StaxStreamWriter implements KmlExtensionWriter {

    private final KmlWriter kmlWriter;
    public Map<Object, List<Extensions.Names>> complexTable = new HashMap<Object, List<Extensions.Names>>();
    public Map<String, List<Extensions.Names>> simpleTable = new HashMap<String, List<Extensions.Names>>();

    /**
     *
     * @param w Kmlwriter used by GxWriter.
     */
    public GxWriter(KmlWriter w){
        super();
        this.initComplexTable();
        this.initSimpleTable();
        this.kmlWriter = w;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOutput(Object output) throws XMLStreamException, IOException{
        super.setOutput(output);
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

    /**
     * 
     * @param tourPrimitive
     * @throws XMLStreamException
     * @throws KmlException
     */
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

    /**
     * 
     * @param tourPrimitive
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeCommonAbstractTourPrimitive(AbstractTourPrimitive tourPrimitive) 
            throws XMLStreamException, KmlException{

        this.kmlWriter.writeCommonAbstractObject(tourPrimitive);
    }

    /**
     * 
     * @param flyTo
     * @throws XMLStreamException
     * @throws KmlException
     */
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

    /**
     *
     * @param animatedUpdate
     * @throws XMLStreamException
     * @throws KmlException
     */
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

    /**
     *
     * @param tourControl
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeTourControl(TourControl tourControl) 
            throws XMLStreamException, KmlException {

        writer.writeStartElement(URI_GX, TAG_TOUR_CONTROL);
        writeCommonAbstractTourPrimitive(tourControl);
        if(tourControl.getPlayMode() != null){
            this.writePlayMode(tourControl.getPlayMode());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param wait
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeWait(Wait wait) 
            throws XMLStreamException, KmlException {

        writer.writeStartElement(URI_GX, TAG_WAIT);
        writeCommonAbstractTourPrimitive(wait);
        if(KmlUtilities.isFiniteNumber(wait.getDuration())){
            this.writeDuration(wait.getDuration());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param soundCue
     * @throws XMLStreamException
     * @throws KmlException
     */
    private void writeSoundCue(SoundCue soundCue) 
            throws XMLStreamException, KmlException {

        writer.writeStartElement(URI_GX, TAG_SOUND_CUE);
        writeCommonAbstractTourPrimitive(soundCue);
        if(soundCue.getHref() != null){
            this.kmlWriter.writeHref(soundCue.getHref());
        }
        writer.writeEndElement();
    }

    /**
     * 
     * @param latLonQuad
     * @throws XMLStreamException
     * @throws KmlException
     */
    public void writeLatLonQuad(LatLonQuad latLonQuad) 
            throws XMLStreamException, KmlException {

        writer.writeStartElement(URI_GX, TAG_LAT_LON_QUAD);
        kmlWriter.writeCommonAbstractObject(latLonQuad);
        if(latLonQuad.getCoordinates() != null){
            kmlWriter.writeCoordinates(latLonQuad.getCoordinates());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param timeSpan
     * @throws XMLStreamException
     * @throws KmlException
     */
    public void writeTimeSpan(TimeSpan timeSpan) 
            throws XMLStreamException, KmlException{

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

    /**
     * 
     * @param timeStamp
     * @throws XMLStreamException
     * @throws KmlException
     */
    public void writeTimeStamp(TimeStamp timeStamp) 
            throws XMLStreamException, KmlException{

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

    /**
     *
     * @param track
     * @throws XMLStreamException
     * @throws KmlException
     */
    public void writeTrack(Track track)
            throws XMLStreamException, KmlException{

        writer.writeStartElement(URI_GX, TAG_TRACK);
        kmlWriter.writeCommonAbstractGeometry(track);
        if(track.getAltitudeMode() != null){
            kmlWriter.writeAltitudeMode(track.getAltitudeMode());
        }
        for(Calendar when : track.getWhens()){
            kmlWriter.writeWhen(when);
        }
        if(track.getCoord() != null){
            for(Coordinate coord : track.getCoord().toCoordinateArray()){
                this.writeCoord(coord);
            }
        }
        for(Angles angles : track.getAngles()){
            this.writeAngles(angles);
        }
        if(track.getModel() != null){
            kmlWriter.writeModel(track.getModel());
        }
        if(track.getExtendedData() != null){
            kmlWriter.writeExtendedData(track.getExtendedData());
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param multiTrack
     * @throws XMLStreamException
     * @throws KmlException
     */
    public void writeMultiTrack(MultiTrack multiTrack)
            throws XMLStreamException, KmlException{
        writer.writeStartElement(URI_GX, TAG_MULTI_TRACK);
        kmlWriter.writeCommonAbstractGeometry(multiTrack);
        if(multiTrack.getAltitudeMode() != null){
            kmlWriter.writeAltitudeMode(multiTrack.getAltitudeMode());
        }
        this.writeInterpolate(multiTrack.getInterpolate());
        for(Track track : multiTrack.getTracks()){
            this.writeTrack(track);
        }
        writer.writeEndElement();
    }

    /**
     *
     * @param coord
     * @throws XMLStreamException
     */
    public void writeCoord(Coordinate coord)
            throws XMLStreamException{

        writer.writeStartElement(URI_GX, TAG_COORD);
        writer.writeCharacters(GxUtilities.toString(coord));
        writer.writeEndElement();
    }

    /**
     * 
     * @param angles
     * @throws XMLStreamException
     */
    public void writeAngles(Angles angles)
            throws XMLStreamException{

        writer.writeStartElement(URI_GX, TAG_ANGLES);
        writer.writeCharacters(GxUtilities.toString(angles));
        writer.writeEndElement();
    }

    /**
     *
     * @param duration
     * @throws XMLStreamException
     */
    private void writeDuration(double duration) throws XMLStreamException {
        if (DEF_DURATION != duration){
            writer.writeStartElement(URI_GX, TAG_DURATION);
            this.writer.writeCharacters(Double.toString(duration));
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param enumFlyToMode
     * @throws XMLStreamException
     */
    private void writeFlyToMode(EnumFlyToMode enumFlyToMode) throws XMLStreamException {
        if(!DEF_FLY_TO_MODE.equals(enumFlyToMode)){
            writer.writeStartElement(URI_GX, TAG_FLY_TO_MODE);
            this.writer.writeCharacters(enumFlyToMode.getFlyToMode());
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param playMode
     * @throws XMLStreamException
     */
    private void writePlayMode(EnumPlayMode playMode) throws XMLStreamException {
        writer.writeStartElement(URI_GX, TAG_PLAY_MODE);
        this.writer.writeCharacters(playMode.getPlayMode());
        writer.writeEndElement();
    }

    /**
     *
     * @param altitudeMode
     * @throws XMLStreamException
     */
    public void writeAltitudeMode(AltitudeMode altitudeMode) throws XMLStreamException{
        if(DEF_ALTITUDE_MODE != altitudeMode){
            writer.writeStartElement(URI_GX, TAG_ALTITUDE_MODE);
            writer.writeCharacters(altitudeMode.getAltitudeMode());
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param bv
     * @throws XMLStreamException
     */
    public void writeBalloonVisibility(Boolean bv) throws XMLStreamException{
        if (DEF_BALLOON_VISIBILITY != bv.booleanValue()){
            writer.writeStartElement(URI_GX, TAG_BALLOON_VISIBILITY);
            if(bv){
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     * 
     * @param inter
     * @throws XMLStreamException
     */
    public void writeInterpolate(Boolean inter) throws XMLStreamException{
        if (DEF_INTERPOLATE != inter.booleanValue()){
            writer.writeStartElement(URI_GX, TAG_INTERPOLATE);
            if(inter){
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_TRUE);
            } else {
                writer.writeCharacters(SimpleTypeContainer.BOOLEAN_FALSE);
            }
            writer.writeEndElement();
        }
    }

    /**
     *
     * @param inter
     * @throws XMLStreamException
     */
    public void writeX(int abs) throws XMLStreamException{
        writer.writeStartElement(URI_GX, TAG_X);
        writer.writeCharacters(String.valueOf(abs));
        writer.writeEndElement();
    }

    /**
     *
     * @param inter
     * @throws XMLStreamException
     */
    public void writeY(int abs) throws XMLStreamException{
        writer.writeStartElement(URI_GX, TAG_Y);
        writer.writeCharacters(String.valueOf(abs));
        writer.writeEndElement();
    }

    /**
     *
     * @param inter
     * @throws XMLStreamException
     */
    public void writeW(int abs) throws XMLStreamException{
        writer.writeStartElement(URI_GX, TAG_W);
        writer.writeCharacters(String.valueOf(abs));
        writer.writeEndElement();
    }

    /**
     *
     * @param inter
     * @throws XMLStreamException
     */
    public void writeH(int abs) throws XMLStreamException{
        writer.writeStartElement(URI_GX, TAG_H);
        writer.writeCharacters(String.valueOf(abs));
        writer.writeEndElement();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void writeComplexExtensionElement(Object contentsElement)
            throws XMLStreamException, KmlException {

        // Feature element
        if(contentsElement instanceof Feature){
            Feature feature = (Feature) contentsElement;
            if(feature.getType().equals(GxModelConstants.TYPE_TOUR)){
                writeTour(feature);
            }
        }
        // non-Feature element
        else if(contentsElement instanceof LatLonQuad){
            writeLatLonQuad((LatLonQuad) contentsElement);
        } else if(contentsElement instanceof TimeSpan){
            writeTimeSpan((TimeSpan) contentsElement);
        } else if(contentsElement instanceof TimeStamp){
            writeTimeStamp((TimeStamp) contentsElement);
        } else if(contentsElement instanceof AltitudeMode){
            writeAltitudeMode((AltitudeMode) contentsElement);
        } else if(contentsElement instanceof Track){
            writeTrack((Track) contentsElement);
        } else if(contentsElement instanceof MultiTrack){
            writeMultiTrack((MultiTrack) contentsElement);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void writeSimpleExtensionElement(SimpleTypeContainer contentsElement)
            throws XMLStreamException, KmlException {

        if(TAG_BALLOON_VISIBILITY.equals(contentsElement.getTagName())){
            writeBalloonVisibility((Boolean) contentsElement.getValue());
        } else if (TAG_X.equals(contentsElement.getTagName())){
            writeX((Integer) contentsElement.getValue());
        } else if (TAG_Y.equals(contentsElement.getTagName())){
            writeY((Integer) contentsElement.getValue());
        } else if (TAG_W.equals(contentsElement.getTagName())){
            writeW((Integer) contentsElement.getValue());
        } else if (TAG_H.equals(contentsElement.getTagName())){
            writeH((Integer) contentsElement.getValue());
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean canHandleComplex(Extensions.Names ext, Object contentObject){
        Boolean reponse = false;
        List<Extensions.Names> liste = null;
        if (contentObject instanceof Feature){
            liste = this.complexTable.get(((Feature) contentObject).getType());
        } else {
            for (Object c : this.complexTable.keySet()){
                if(c instanceof Class
                        && ((Class) c).isInstance(contentObject)){
                    liste = this.complexTable.get(c);
                    break;
                }
            }
        }
        if(liste != null){
            if (EMPTY_LIST.equals(liste)
                    || liste.contains(ext)){
                reponse = true;
            }
        }
        return reponse;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean canHandleSimple(Names ext, String elementTag) {
        List<Names> liste = this.simpleTable.get(elementTag);
        Boolean reponse = false;
        if(liste != null
                && liste.contains(ext)){
            reponse = true;
        }
        return reponse;
    }

    /**
     *
     */
    private void initComplexTable(){
        
        List<Names> latLonQuadContainersList = new ArrayList<Names>();
        latLonQuadContainersList.add(Names.GROUND_OVERLAY);

        List<Names> timeSpanContainers = new ArrayList<Names>();
        timeSpanContainers.add(Names.VIEW);

        List<Names> timeStampContainers = new ArrayList<Names>();
        timeStampContainers.add(Names.VIEW);

        complexTable.put(GxModelConstants.TYPE_TOUR, EMPTY_LIST);
        complexTable.put(Track.class, EMPTY_LIST);
        complexTable.put(MultiTrack.class, EMPTY_LIST);
        complexTable.put(TimeSpan.class, timeSpanContainers);
        complexTable.put(TimeStamp.class, timeStampContainers);
        complexTable.put(LatLonQuad.class, latLonQuadContainersList);
        complexTable.put(AltitudeMode.class, EMPTY_LIST);

    }

    /**
     *
     */
    private void initSimpleTable(){
        List<Names> balloonVisibilityList = new ArrayList<Names>();
        balloonVisibilityList.add(Extensions.Names.FEATURE);

        List<Names> hList = new ArrayList<Names>();
        hList.add(Extensions.Names.BASIC_LINK);

        List<Names> wList = new ArrayList<Names>();
        wList.add(Extensions.Names.BASIC_LINK);

        List<Names> xList = new ArrayList<Names>();
        xList.add(Extensions.Names.BASIC_LINK);

        List<Names> yList = new ArrayList<Names>();
        yList.add(Extensions.Names.BASIC_LINK);

        simpleTable.put(GxConstants.TAG_BALLOON_VISIBILITY, balloonVisibilityList);
        simpleTable.put(GxConstants.TAG_H, hList);
        simpleTable.put(GxConstants.TAG_W, wList);
        simpleTable.put(GxConstants.TAG_X, xList);
        simpleTable.put(GxConstants.TAG_Y, yList);
    }
}
