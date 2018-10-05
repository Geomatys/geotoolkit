/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.processing.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.processing.chain.model.Chain;
import org.geotoolkit.processing.chain.model.Parameter;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.chain.model.ParameterFormat;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ChainProcessDescriptor extends AbstractProcessDescriptor{

    /** registry name **/
    public static final DefaultServiceIdentification FALLBACK_IDENTIFICATION;
    static {
        FALLBACK_IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier("dynamicchain");
        final DefaultCitation citation = new DefaultCitation("dynamicchain");
        citation.setIdentifiers(Collections.singleton(id));
        FALLBACK_IDENTIFICATION.setCitation(citation);
    }

    public static final String KEY_DISTANT_CLASS = "distanClass";

    private final Collection<? extends ProcessingRegistry> factories;
    private final Chain model;

    /**
     * Create a process descriptor with default registry.
     */
    public ChainProcessDescriptor(final Chain model){
        this(model,FALLBACK_IDENTIFICATION,null);
    }

    /**
     * Create a process descriptor with default registry.
     */
    public ChainProcessDescriptor(final Chain model, final Identification registryId){
        this(model,registryId,null);
    }

    /**
     * Create a process descriptor with given registry.
     */
    public ChainProcessDescriptor(final Chain model, final Identification registryId,
            final Collection<? extends ProcessingRegistry> factories){
        super(new DerivateIdentifier(model.getName(), registryId),
                model.getAbstract() != null ? new SimpleInternationalString(model.getAbstract()) : new SimpleInternationalString(model.getName()),
                model.getTitle() != null ? new SimpleInternationalString(model.getTitle()) : new SimpleInternationalString(model.getName()),
                createParams(model.getInputs(), "inputParameters", true),
                createParams(model.getOutputs(), "outputParameters", true));

        this.model = model;
        this.factories = factories;
    }

    /**
     * @return Chain model used.
     */
    public Chain getModel(){
        return model;
    }

    /**
     * Context in which this process is executed.
     *
     * @return Collection of ProcessingRegistry.
     */
    public Collection<? extends ProcessingRegistry> getFactories() {
        if(factories == null){
            final Iterator<ProcessingRegistry> ite = ProcessFinder.getProcessFactories();
            final Collection<ProcessingRegistry> factories = new ArrayList<>();
            while(ite.hasNext()){
                factories.add(ite.next());
            }
            return factories;
        }
        return factories;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new ChainProcess(this, input);
    }

    public static ParameterDescriptorGroup createParams(final List<Parameter> inputs,
            final String name, final boolean realType){

        int parameterSize = inputs.size();

        final GeneralParameterDescriptor[] paramDescs = new GeneralParameterDescriptor[parameterSize];

        int index = 0;
        for (Parameter param : inputs) {

            final ParameterDescriptor desc = convertParameterDtoToParameterDescriptor(param, realType);
            paramDescs[index] = desc;
            index++;
        }
        return new ParameterBuilder().addName(name).createGroup(paramDescs);
    }

    public static ParameterDescriptor convertParameterDtoToParameterDescriptor(final Parameter param, final boolean realType) {
        final Map<String, Object> ext = new HashMap<>();
        Class type;
        if (realType) {
            type = param.getType().getRealClass();
        } else {
            ext.put(KEY_DISTANT_CLASS, param.getType());
            try {
                type = Class.forName(param.getType().getName());
            } catch (ClassNotFoundException ex) {
                type = Object.class;
            }
        }
        if (param.getFormats()!= null && !param.getFormats().isEmpty()) {
            List<Map> formats = new ArrayList<>();
            for (ParameterFormat format : param.getFormats()) {
                Map m = new HashMap<>();
                if (format.getEncoding() != null) m.put("encoding", format.getEncoding());
                if (format.getMimeType()!= null)  m.put("mimetype", format.getMimeType());
                if (format.getSchema()!= null)    m.put("schema",   format.getSchema());
                formats.add(m);
            }
            ext.put("formats", formats);
        }
        if (param.getTitle() != null) {
            ext.put("Title", param.getTitle());
        }
        if (param.getUserMap() != null) {
            ext.putAll(param.getUserMap());
        }
        return new ExtendedParameterDescriptor(param.getCode(), null, param.getRemarks(), param.getMinOccurs(), param.getMaxOccurs(), type, convertDefaultValueInClass(param.getDefaultValue(), type), null, ext);
    }

    /**
     * Converts the default value in the given class if the default value is a string, otherwise
     * return {@code null}.
     *
     * @param defaultValue The default value to convert.
     * @param clazz Class in which the default value should be converted.
     * @return The default value converted in the given class, or {@code null} if impossible to convert.
     */
    public static Object convertDefaultValueInClass(final Object defaultValue, final Class clazz) {
        if (defaultValue == null) {
            return null;
        }

        if (defaultValue.getClass().isAssignableFrom(clazz)) {
            // don't need to convert value since it's already in the right class
            return defaultValue;
        }

        if (defaultValue instanceof String && !defaultValue.getClass().isAssignableFrom(clazz) &&
                (clazz.isAssignableFrom(String.class) || clazz.isAssignableFrom(Boolean.class) ||
                 clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(Integer.class)))
        {
            return ConstantUtilities.stringToValue(defaultValue.toString(), clazz);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ChainProcessDescriptor) {
            ChainProcessDescriptor that = (ChainProcessDescriptor) obj;
            return Objects.equals(this.model, that.model);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.model);
        return hash;
    }
}
