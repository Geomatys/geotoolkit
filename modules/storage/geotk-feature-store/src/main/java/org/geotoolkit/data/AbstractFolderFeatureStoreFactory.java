/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011 - 2012, Geomatys
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
package org.geotoolkit.data;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.DataStore;
import org.opengis.metadata.Identifier;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Factory to open a feature store from a folder of specific file types.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public abstract class AbstractFolderFeatureStoreFactory extends AbstractFeatureStoreFactory{
    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data");

    /**
     * url to the folder.
     */
    public static final ParameterDescriptor<URI> FOLDER_PATH = new ParameterBuilder()
            .addName("path")
            .addName(Bundle.formatInternational(Bundle.Keys.paramPathAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramPathRemarks))
            .setRequired(true)
            .create(URI.class, null);

    /**
     * recursively search folder.
     */
    public static final ParameterDescriptor<Boolean> RECURSIVE = new ParameterBuilder()
            .addName("recursive")
            .addName(Bundle.formatInternational(Bundle.Keys.recursive))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.recursive_remarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.TRUE);

    public static final ParameterDescriptor<Boolean> EMPTY_DIRECTORY = new ParameterBuilder()
            .addName("emptyDirectory")
            .addName(Bundle.formatInternational(Bundle.Keys.emptyDirectory))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.emptyDirectory_remarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);

    private final ParameterDescriptorGroup paramDesc;

    public AbstractFolderFeatureStoreFactory(final ParameterDescriptorGroup desc){
        ArgumentChecks.ensureNonNull("desc", desc);
        paramDesc = desc;
    }

    public abstract FileFeatureStoreFactory getSingleFileFactory();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canProcess(final ParameterValueGroup params) {
        final boolean valid = super.canProcess(params);
        if (!valid) {
            return false;
        }

        final Object obj = params.parameter(FOLDER_PATH.getName().toString()).getValue();
        if(!(obj instanceof URI)){
            return false;
        }

        final URI path = (URI)obj;
        Path pathFile = Paths.get(path);
        return Files.isDirectory(pathFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return paramDesc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence getDescription() {
        return super.getDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataStore open(final ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new DefaultFolderFeatureStore(params,this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataStore create(final ParameterValueGroup params) throws DataStoreException {
        //we can open an empty featurestore of this type
        //the open featurestore will always work, it will just be empty if there are no files in it.
        return open(params);
    }

    /**
     * Derivate a folder factory identification from original single file factory.
     */
    protected static DefaultServiceIdentification derivateIdentification(final DefaultServiceIdentification identification){
        final String name = identification.getCitation().getTitle().toString()+"-folder";
        final DefaultServiceIdentification ident = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(name);
        final DefaultCitation citation = new DefaultCitation(name);
        citation.setIdentifiers(Collections.singleton(id));
        ident.setCitation(citation);
        return ident;
    }

    /**
     * Create a Folder FeatureStore descriptor group based on the single file factory
     * parameters.
     *
     * @return ParameterDescriptorGroup
     */
    protected static ParameterDescriptorGroup derivateDescriptor(
            final ParameterDescriptor identifierParam,final ParameterDescriptorGroup sd){

        final List<GeneralParameterDescriptor> params = new ArrayList<GeneralParameterDescriptor>(sd.descriptors());
        for(int i=0;i<params.size();i++){
            if(params.get(i).getName().getCode().equals(AbstractFeatureStoreFactory.IDENTIFIER.getName().getCode())){
                params.remove(i);
                break;
            }
        }
        params.remove(AbstractFileFeatureStoreFactory.PATH);
        params.add(0,identifierParam);
        params.add(1, FOLDER_PATH);
        params.add(2,RECURSIVE);
        params.add(3,EMPTY_DIRECTORY);

        return new ParameterBuilder().addName(sd.getName().getCode()+"Folder").createGroup(
                params.toArray(new GeneralParameterDescriptor[params.size()]));
    }

}
