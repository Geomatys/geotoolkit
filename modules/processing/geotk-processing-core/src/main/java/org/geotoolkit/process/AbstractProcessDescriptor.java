/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.process;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.InternationalString;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractProcessDescriptor implements ProcessDescriptor {

    private final Identifier id;
    private final InternationalString abs;
    private final ParameterDescriptorGroup inputDesc;
    private final ParameterDescriptorGroup outputdesc;

    public AbstractProcessDescriptor(final String name, final Identification factoryId, final InternationalString abs,
            final ParameterDescriptorGroup inputDesc, final ParameterDescriptorGroup outputdesc) {
        this(new DerivateIdentifier(name, factoryId),abs,inputDesc,outputdesc);
    }

    public AbstractProcessDescriptor(final Identifier id, final InternationalString abs,
            final ParameterDescriptorGroup inputDesc, final ParameterDescriptorGroup outputdesc) {
        ensureNonNull("id", id);
        ensureNonNull("abs", abs);
        ensureNonNull("inputDesc", inputDesc);
        ensureNonNull("outputdesc", outputdesc);
        this.id = id;
        this.abs = abs;
        this.inputDesc = inputDesc;
        this.outputdesc = outputdesc;
    }

    @Override
    public final Identifier getName() {
        return id;
    }

    @Override
    public final InternationalString getAbstract() {
        return abs;
    }

    @Override
    public final ParameterDescriptorGroup getInputDescriptor() {
        return inputDesc;
    }

    @Override
    public final ParameterDescriptorGroup getOutputDescriptor() {
        return outputdesc;
    }

    
    protected static class DerivateIdentifier implements Identifier{

        private final String code;
        private final Identification factoryId;

        public DerivateIdentifier(final String code, final Identification factoryId) {
            this.code = code;
            this.factoryId = factoryId;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public Citation getAuthority() {
            return factoryId.getCitation();
        }

    }

}
