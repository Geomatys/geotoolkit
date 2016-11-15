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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;

import java.net.URI;
import java.util.Calendar;
import java.util.List;

import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.gx.model.AbstractTourPrimitive;
import org.geotoolkit.data.gx.model.Angles;
import org.geotoolkit.data.gx.model.AnimatedUpdate;
import org.geotoolkit.data.gx.model.DefaultAngles;
import org.geotoolkit.data.gx.model.DefaultAnimatedUpdate;
import org.geotoolkit.data.gx.model.DefaultFlyTo;
import org.geotoolkit.data.gx.model.DefaultLatLonQuad;
import org.geotoolkit.data.gx.model.DefaultMultiTrack;
import org.geotoolkit.data.gx.model.DefaultPlayList;
import org.geotoolkit.data.gx.model.DefaultSoundCue;
import org.geotoolkit.data.gx.model.DefaultTourControl;
import org.geotoolkit.data.gx.model.DefaultTrack;
import org.geotoolkit.data.gx.model.DefaultWait;
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
import org.geotoolkit.data.kml.model.AbstractObject;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.AbstractTimePrimitive;
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Model;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.xal.model.AddressDetails;
import org.opengis.feature.Feature;


/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultGxFactory implements GxFactory {

    private static final GxFactory GXF = new DefaultGxFactory();

    private DefaultGxFactory(){}

    public static GxFactory getInstance(){
        return GXF;
    }

    @Override
    public Angles createAngles() {
        return new DefaultAngles();
    }

    @Override
    public Angles createAngles(double... angles) {
        return new DefaultAngles(angles);
    }

    @Override
    public AnimatedUpdate createAnimatedUpdate() {
        return new DefaultAnimatedUpdate();
    }

    @Override
    public AnimatedUpdate createAnimatedUpdate(List<SimpleTypeContainer> objectSimpleExtensions,
        IdAttributes idAttributes, double duration, Update update) {
        return new DefaultAnimatedUpdate(objectSimpleExtensions,
       idAttributes, duration, update);
    }

    @Override
    public Coordinate createCoordinate(String listCoordinates) {
        return GxUtilities.toCoordinate(listCoordinates);
    }

    @Override
    public FlyTo createFlyTo() {
        return new DefaultFlyTo();
    }

    @Override
    public FlyTo createFlyTo(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, double duration,
            EnumFlyToMode flyToMOde, AbstractView view) {
        return new DefaultFlyTo(objectSimpleExtensions, idAttributes,
                duration, flyToMOde, view);
    }

    @Override
    public LatLonQuad createLatLonQuad() {
        return new DefaultLatLonQuad();
    }

    @Override
    public LatLonQuad createLatLonQuad(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, CoordinateSequence coordinates) {
        return new DefaultLatLonQuad(objectSimpleExtensions, idAttributes, coordinates);
    }

    @Override
    public MultiTrack createMultiTrack() {
        return new DefaultMultiTrack();
    }

    @Override
    public MultiTrack createMultiTrack(AltitudeMode altitudeMode,
            boolean interpolate, List<Track> tracks) {
        return new DefaultMultiTrack(altitudeMode, interpolate, tracks);
    }

    @Override
    public PlayList createPlayList() {
        return new DefaultPlayList();
    }

    @Override
    public PlayList createPlayList(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, List<AbstractTourPrimitive> tourPrimitives)
    {
        return new DefaultPlayList(objectSimpleExtensions, idAttributes, tourPrimitives);
    }

    @Override
    public Feature createTour() {
        final Feature f = GxModelConstants.TYPE_TOUR.newInstance();
        f.setPropertyValue(KmlConstants.TAG_EXTENSIONS, new Extensions());
        return f;
    }

    @Override
    public Feature createTour(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name,
            boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet, Object description,
            AbstractView view, AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleTypeContainer> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<PlayList> playLists)
    {
        Extensions extensions = new Extensions();
        if (objectSimpleExtensions != null) {
            extensions.simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        if (abstractFeatureSimpleExtensions != null) {
            extensions.simples(Extensions.Names.FEATURE).addAll(abstractFeatureSimpleExtensions);
        }
        if (abstractFeatureObjectExtensions != null) {
            extensions.complexes(Extensions.Names.FEATURE).addAll(abstractFeatureObjectExtensions);
        }
        final Feature f = GxModelConstants.TYPE_TOUR.newInstance();
        f.setPropertyValue(KmlConstants.ATT_ID, idAttributes);
        f.setPropertyValue(KmlConstants.TAG_NAME, name);
        f.setPropertyValue(KmlConstants.TAG_VISIBILITY, visibility);
        f.setPropertyValue(KmlConstants.TAG_OPEN, open);
        f.setPropertyValue(KmlConstants.TAG_ATOM_AUTHOR, author);
        f.setPropertyValue(KmlConstants.TAG_ATOM_LINK, link);
        f.setPropertyValue(KmlConstants.TAG_ADDRESS, address);
        f.setPropertyValue(KmlConstants.TAG_XAL_ADDRESS_DETAILS, addressDetails);
        f.setPropertyValue(KmlConstants.TAG_PHONE_NUMBER, phoneNumber);
        f.setPropertyValue(KmlConstants.TAG_SNIPPET, snippet);
        f.setPropertyValue(KmlConstants.TAG_DESCRIPTION, description);
        f.setPropertyValue(KmlConstants.TAG_VIEW, view);
        f.setPropertyValue(KmlConstants.TAG_TIME_PRIMITIVE, timePrimitive);
        f.setPropertyValue(KmlConstants.TAG_STYLE_URL, styleUrl);
        f.setPropertyValue(KmlConstants.TAG_STYLE_SELECTOR, styleSelector);
        f.setPropertyValue(KmlConstants.TAG_REGION, region);
        f.setPropertyValue(KmlConstants.TAG_EXTENDED_DATA, extendedData);
        f.setPropertyValue(KmlConstants.ATT_PLAYLIST, playLists);
        f.setPropertyValue(KmlConstants.TAG_EXTENSIONS, extensions);
        return f;
    }

    @Override
    public SoundCue createSoundCue() {
        return new DefaultSoundCue();
    }

    @Override
    public SoundCue createSoundCue(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, String href)
    {
        return new DefaultSoundCue(objectSimpleExtensions, idAttributes, href);
    }

    @Override
    public TourControl createTourControl() {
        return new DefaultTourControl();
    }

    @Override
    public TourControl createTourControl(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, EnumPlayMode playMode)
    {
        return new DefaultTourControl(objectSimpleExtensions, idAttributes, playMode);
    }

    @Override
    public Track createTrack() {
        return new DefaultTrack();
    }

    @Override
    public Track createTrack(AltitudeMode altitudeMode, List<Calendar> whens,
            CoordinateSequence coord, List<Angles> angleList, Model model,
            ExtendedData extendedData)
    {
        return new DefaultTrack(altitudeMode, whens, coord, angleList, model, extendedData);
    }

    @Override
    public Wait createWait() {
        return new DefaultWait();
    }

    @Override
    public Wait createWait(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, double duration)
    {
        return new DefaultWait(objectSimpleExtensions, idAttributes, duration);
    }
}
