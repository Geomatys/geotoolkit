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
import org.geotoolkit.data.gx.model.EnumFlyToMode;
import org.geotoolkit.data.gx.model.EnumPlayMode;
import org.geotoolkit.data.gx.model.FlyTo;
import org.geotoolkit.data.gx.model.LatLonQuad;
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
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Model;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.xal.model.AddressDetails;
import org.opengis.feature.Feature;

/**
 *
 * @author Samuel Andrés
 */
public interface GxFactory {

    Angles createAngles();

    Angles createAngles(double... angles);

    AnimatedUpdate createAnimatedUpdate();

    AnimatedUpdate createAnimatedUpdate(List<SimpleTypeContainer> objectSimpleExtensions,
        IdAttributes idAttributes, double duration, Update update);

    Coordinate createCoordinate(String listCoordinates);

    FlyTo createFlyTo();

    FlyTo createFlyTo(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, double duration,
            EnumFlyToMode flyToMOde, AbstractView view);

    LatLonQuad createLatLonQuad();

    LatLonQuad createLatLonQuad(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, CoordinateSequence coordinates);

    PlayList createPlayList();

    PlayList createPlayList(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<AbstractTourPrimitive> tourPrimitives);

    Feature createTour();

    Feature createTour(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name,
            boolean visibility,
            boolean open,
            AtomPersonConstruct author,
            AtomLink link,
            String address,
            AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleTypeContainer> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<PlayList> playLists);

    Track createTrack();

    Track createTrack(AltitudeMode altitudeMode,
            List<Calendar> whens, CoordinateSequence coord,
            List<Angles> angleList, Model model, ExtendedData extendedData);

    SoundCue createSoundCue();

    SoundCue createSoundCue(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, String href);

    TourControl createTourControl();

    TourControl createTourControl(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, EnumPlayMode playMode);

    Wait createWait();

    Wait createWait(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, double duration);
}
