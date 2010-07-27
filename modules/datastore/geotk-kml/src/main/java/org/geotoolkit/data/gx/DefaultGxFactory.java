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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.gx.model.AbstractTourPrimitive;
import org.geotoolkit.data.gx.model.AnimatedUpdate;
import org.geotoolkit.data.gx.model.DefaultAnimatedUpdate;
import org.geotoolkit.data.gx.model.DefaultFlyTo;
import org.geotoolkit.data.gx.model.DefaultLatLonQuad;
import org.geotoolkit.data.gx.model.DefaultPlayList;
import org.geotoolkit.data.gx.model.DefaultSoundCue;
import org.geotoolkit.data.gx.model.DefaultTourControl;
import org.geotoolkit.data.gx.model.DefaultWait;
import org.geotoolkit.data.gx.model.EnumFlyToMode;
import org.geotoolkit.data.gx.model.EnumPlayMode;
import org.geotoolkit.data.gx.model.FlyTo;
import org.geotoolkit.data.gx.model.GxModelConstants;
import org.geotoolkit.data.gx.model.LatLonQuad;
import org.geotoolkit.data.gx.model.PlayList;
import org.geotoolkit.data.gx.model.SoundCue;
import org.geotoolkit.data.gx.model.TourControl;
import org.geotoolkit.data.gx.model.Wait;
import org.geotoolkit.data.kml.model.AbstractObject;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.AbstractTimePrimitive;
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.xal.model.AddressDetails;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultGxFactory implements GxFactory {


    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

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
            IdAttributes idAttributes, Coordinates coordinates) {
        return new DefaultLatLonQuad(objectSimpleExtensions, idAttributes, coordinates);
    }

    @Override
    public PlayList createPlayList() {
        return new DefaultPlayList();
    }

    @Override
    public PlayList createPlayList(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes, List<AbstractTourPrimitive> tourPrimitives) {
        return new DefaultPlayList(objectSimpleExtensions, idAttributes, tourPrimitives);
    }

    @Override
    public Feature createTour() {
        List<Property> properties = new ArrayList<Property>();
        properties.add(FF.createAttribute(KmlConstants.DEF_VISIBILITY, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_OPEN, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(new Extensions(), KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, GxModelConstants.TYPE_TOUR, "Tour");
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
            List<PlayList> playLists) {

        List<Property> properties = new ArrayList<Property>();

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

        properties.add(FF.createAttribute(idAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));
        properties.add(FF.createAttribute(name, KmlModelConstants.ATT_NAME, null));
        properties.add(FF.createAttribute(visibility, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(open, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(author, KmlModelConstants.ATT_AUTHOR, null));
        properties.add(FF.createAttribute(link, KmlModelConstants.ATT_LINK, null));
        properties.add(FF.createAttribute(address, KmlModelConstants.ATT_ADDRESS, null));
        properties.add(FF.createAttribute(addressDetails, KmlModelConstants.ATT_ADDRESS_DETAILS, null));
        properties.add(FF.createAttribute(phoneNumber, KmlModelConstants.ATT_PHONE_NUMBER, null));
        properties.add(FF.createAttribute(snippet, KmlModelConstants.ATT_SNIPPET, null));
        properties.add(FF.createAttribute(description, KmlModelConstants.ATT_DESCRIPTION, null));
        properties.add(FF.createAttribute(view, KmlModelConstants.ATT_VIEW, null));
        properties.add(FF.createAttribute(timePrimitive, KmlModelConstants.ATT_TIME_PRIMITIVE, null));
        properties.add(FF.createAttribute(styleUrl, KmlModelConstants.ATT_STYLE_URL, null));
        for (AbstractStyleSelector ass : styleSelector){
            properties.add(FF.createAttribute(ass, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        }
        properties.add(FF.createAttribute(region, KmlModelConstants.ATT_REGION, null));
        properties.add(FF.createAttribute(extendedData, KmlModelConstants.ATT_EXTENDED_DATA, null));
        for (PlayList pl : playLists){
            properties.add(FF.createAttribute(pl, GxModelConstants.ATT_TOUR_PLAY_LIST, null));
        }
        properties.add(FF.createAttribute(extensions, KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, GxModelConstants.TYPE_TOUR, "Tour");
    }

    @Override
    public SoundCue createSoundCue() {
        return new DefaultSoundCue();
    }

    @Override
    public SoundCue createSoundCue(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes, String href) {
        return new DefaultSoundCue(objectSimpleExtensions, idAttributes, href);
    }

    @Override
    public TourControl createTourControl() {
        return new DefaultTourControl();
    }

    @Override
    public TourControl createTourControl(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, EnumPlayMode playMode) {
        return new DefaultTourControl(objectSimpleExtensions, idAttributes, playMode);
    }

    @Override
    public Wait createWait() {
        return new DefaultWait();
    }

    @Override
    public Wait createWait(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, double duration) {
        return new DefaultWait(objectSimpleExtensions, idAttributes, duration);
    }

}
