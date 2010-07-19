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
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.xal.model.AddressDetails;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultFolder extends DefaultAbstractContainer implements Folder {

    private List<AbstractFeature> features;

    public DefaultFolder() {
        super(KmlModelConstants.TYPE_FOLDER);
        this.features = EMPTY_LIST;
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
     * @param abstractContainerSimpleExtensions
     * @param abstractContainerObjectExtensions
     * @param features
     * @param folderSimpleExtensions
     * @param folderObjectExtensions
     */
    public DefaultFolder(List<SimpleType> objectSimpleExtensions,
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
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions,
            List<AbstractFeature> features,
            List<SimpleType> folderSimpleExtensions,
            List<AbstractObject> folderObjectExtensions) {

        super(KmlModelConstants.TYPE_FOLDER,
                objectSimpleExtensions, idAttributes,
                name, visibility, open,
                author, atomLink,
                address, addressDetails,
                phoneNumber, snippet, description,
                view, timePrimitive,
                styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions,
                abstractContainerSimpleExtensions,
                abstractContainerObjectExtensions);
        this.features = (features == null) ? EMPTY_LIST : features;
        if (folderSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.FOLDER).addAll(folderSimpleExtensions);
        }
        if (folderObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.FOLDER).addAll(folderObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractFeature> getAbstractFeatures() {
        return this.features;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractFeatures(List<AbstractFeature> abstractFeatures) {
        this.features = abstractFeatures;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tFolderDefault : ";
        return resultat;
    }
}
