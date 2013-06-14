/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.shapefile;

import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.geotoolkit.data.AbstractFolderFeatureStoreFactory;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * FeatureStore for a folder of Shapefiles.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ShapefileFolderFeatureStoreFactory extends AbstractFolderFeatureStoreFactory{

    /** factory identification **/
    public static final DefaultServiceIdentification IDENTIFICATION = derivateIdentification(ShapefileFeatureStoreFactory.IDENTIFICATION);
    public static final String NAME = IDENTIFICATION.getCitation().getTitle().toString();

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            derivateDescriptor(IDENTIFIER,ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

    public ShapefileFolderFeatureStoreFactory(){
        super(PARAMETERS_DESCRIPTOR);
    }

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public FileFeatureStoreFactory getSingleFileFactory() {
        return FeatureStoreFinder.getAllFactories(ShapefileFeatureStoreFactory.class).iterator().next();
    }

    @Override
    public CharSequence getDescription() {
        return new ResourceInternationalString("org/geotoolkit/shapefile/bundle", "datastoreFolderDescription");
    }

    @Override
    public CharSequence getDisplayName() {
        return new ResourceInternationalString("org/geotoolkit/shapefile/bundle", "datastoreFolderTitle");
    }



}
