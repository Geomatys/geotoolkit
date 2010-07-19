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
package org.geotoolkit.data.kml.model;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.feature.DefaultGeometryAttribute;
import org.opengis.feature.Property;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPlacemark extends DefaultAbstractFeature implements Placemark {

    private AbstractGeometry abstractGeometry;

    /**
     * 
     */
    public DefaultPlacemark() {
        super(KmlModelConstants.TYPE_PLACEMARK);
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param atomLink
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param abstractGeometry
     * @param placemarkSimpleExtensions
     * @param placemarkObjectExtensions
     */
    public DefaultPlacemark(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            AbstractGeometry abstractGeometry,
            List<SimpleType> placemarkSimpleExtensions,
            List<AbstractObject> placemarkObjectExtensions) {

        super(KmlModelConstants.TYPE_PLACEMARK,
                objectSimpleExtensions, idAttributes,
                name, visibility, open,
                author, atomLink,
                address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive,
                styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions);
        this.setAbstractGeometry(abstractGeometry);
        if (placemarkSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.PLACEMARK).addAll(placemarkSimpleExtensions);
        }
        if (placemarkObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.PLACEMARK).addAll(placemarkObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractGeometry getAbstractGeometry() {
        return this.abstractGeometry;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractGeometry(AbstractGeometry abstractGeometry) {
        this.abstractGeometry = abstractGeometry;
        Property prop = this.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName());
        if (prop == null){
            value.add(new DefaultGeometryAttribute(this.abstractGeometry, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));
        } else {
            prop.setValue(abstractGeometry);
        }
    }

    @Override
    public String toString() {
        String resultat = super.toString();
        resultat += "Placemark : ";
        //resultat += "\n\t"+abstractGeometry;
        return resultat;
    }
}
