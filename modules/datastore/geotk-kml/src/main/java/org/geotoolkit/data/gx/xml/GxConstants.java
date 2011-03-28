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

import org.geotoolkit.data.gx.model.EnumFlyToMode;
import org.geotoolkit.data.gx.model.EnumPlayMode;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.xml.KmlConstants;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public final class GxConstants {

    // NAMESPACES
    public static final String URI_GX = "http://www.google.com/kml/ext/2.2";
    public static final String URI_KML_2_2 = KmlConstants.URI_KML_2_2;
    public static final String URI_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String PREFIX_KML = "kml";
    public static final String PREFIX_XSI = "xsi";

    // TAGS
    public static final String TAG_ALTITUDE_MODE = "altitudeMode";
    public static final String TAG_ANGLES = "angles";
    public static final String TAG_COORD = "coord";
    public static final String TAG_FLY_TO_MODE = "flyToMode";
    public static final String TAG_PLAY_MODE = "playMode";
    public static final String TAG_ABSTRACT_TOUR_PRIMITIVE = "AbstractTourPrimitive";
    public static final String TAG_ANIMATED_UPDATE = "AnimatedUpdate";
    public static final String TAG_BALLOON_VISIBILITY = "balloonVisibility";
    public static final String TAG_DURATION = "duration";
    public static final String TAG_FLY_TO = "FlyTo";
    public static final String TAG_H = "h";
    public static final String TAG_INTERPOLATE = "interpolate";
    public static final String TAG_LAT_LON_QUAD = "LatLonQuad";
    public static final String TAG_MULTI_TRACK = "MultiTrack";
    public static final String TAG_PLAYLIST = "Playlist";
    public static final String TAG_SOUND_CUE = "SoundCue";
    public static final String TAG_TOUR = "Tour";
    public static final String TAG_TIME_STAMP = "TimeStamp";
    public static final String TAG_TIME_SPAN = "TimeSpan";
    public static final String TAG_TOUR_CONTROL = "TourControl";
    public static final String TAG_TRACK = "Track";
    public static final String TAG_W = "w";
    public static final String TAG_WAIT = "Wait";
    public static final String TAG_X = "x";
    public static final String TAG_Y = "y";

    /*
     * DEFAULT VALUES
     */
    public static final AltitudeMode DEF_ALTITUDE_MODE = KmlConstants.DEF_ALTITUDE_MODE;
    public static final boolean DEF_BALLOON_VISIBILITY = true;
    public static final double DEF_DURATION = 0.0;
    public static final boolean DEF_INTERPOLATE = false;
    public static final EnumFlyToMode DEF_FLY_TO_MODE = EnumFlyToMode.BOUNCE;
    public static final EnumPlayMode DEF_PLAY_MODE = EnumPlayMode.PAUSE;

    private GxConstants(){}
}
