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

package org.geotoolkit.processing;

import org.geotoolkit.process.ProcessDescriptor;
import org.opengis.metadata.lineage.Algorithm;
import java.util.Collections;
import java.util.Collection;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.metadata.iso.citation.Citations;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.InternationalString;

import static org.apache.sis.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public abstract class AbstractProcessDescriptor implements ProcessDescriptor {
    private static final String DEFAULT_VERSION = "1.0";

    private final Identifier id;
    private final InternationalString abs;
    private final InternationalString displayName;
    private final ParameterDescriptorGroup inputDesc;
    private final ParameterDescriptorGroup outputdesc;
    private final String version;

    public AbstractProcessDescriptor(final String name, final Identification factoryId, final InternationalString abs,
            final ParameterDescriptorGroup inputDesc, final ParameterDescriptorGroup outputdesc) {
        this(new DerivateIdentifier(name, factoryId),abs,inputDesc,outputdesc);
    }

    public AbstractProcessDescriptor(final String name, final Identification factoryId, final InternationalString abs,
            final InternationalString displayName, final ParameterDescriptorGroup inputDesc, final ParameterDescriptorGroup outputdesc) {
        this(new DerivateIdentifier(name, factoryId), abs, displayName, inputDesc, outputdesc);
    }

    public AbstractProcessDescriptor(final Identifier id, final InternationalString abs,
            final ParameterDescriptorGroup inputDesc, final ParameterDescriptorGroup outputdesc) {
        this(id, abs, null, inputDesc, outputdesc);
    }

    public AbstractProcessDescriptor(final Identifier id, final InternationalString abs, final InternationalString displayName,
            final ParameterDescriptorGroup inputDesc, final ParameterDescriptorGroup outputdesc) {
        this(id, abs, displayName, inputDesc, outputdesc, DEFAULT_VERSION);
    }

    public AbstractProcessDescriptor(final Identifier id, final InternationalString abs, final InternationalString displayName,
            final ParameterDescriptorGroup inputDesc, final ParameterDescriptorGroup outputdesc, final String version) {
        ensureNonNull("id", id);
        ensureNonNull("abs", abs);
        ensureNonNull("inputDesc", inputDesc);
        ensureNonNull("outputdesc", outputdesc);
        this.id = id;
        this.abs = abs;
        this.displayName = displayName;
        this.inputDesc = inputDesc;
        this.outputdesc = outputdesc;
        this.version = (version == null) ? DEFAULT_VERSION : version;
    }



    @Override
    public Identifier getIdentifier() {
        return id;
    }

    @Override
    public InternationalString getDisplayName() {
        return displayName;
    }

    @Override
    public InternationalString getProcedureDescription() {
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

    /**
     * Get the process version. By default, return {@code 1.0}.
     *
     * @return The process version. By default {@code 1.0}.
     */
    public String getVersion() {
        return version;
    }

    @Override
    public Collection<? extends Citation> getSoftwareReferences() {
        return Collections.emptySet();
    }

    @Override
    public Collection<? extends Citation> getDocumentations() {
        return Collections.emptySet();
    }

    @Override
    public InternationalString getRunTimeParameters() {
        return null;
    }

    @Override
    public Collection<? extends Algorithm> getAlgorithms() {
        return Collections.emptySet();
    }


    protected static class DerivateIdentifier implements Identifier {

        private final String code;
        private final Identification factoryId;

        public DerivateIdentifier(final String code, final Identification factoryId) {
            ArgumentChecks.ensureNonNull("factoryId", factoryId);
            ArgumentChecks.ensureNonNull("code", code);
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

        @Override
        public String getCodeSpace() {
            return Citations.getIdentifier(getAuthority());
        }

        @Override
        public String getVersion() {
            return null;
        }

        @Override
        public InternationalString getDescription() {
            return null;
        }
    }

    /**
     * @return process authority and name. Also table of process inputs and outputs.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Authority         : ");
        sb.append(id.getAuthority().getTitle().toString()).append("\n");
        sb.append("Code              : ");
        sb.append(id.getCode()).append("\n");
        sb.append("Display name      : ");
        sb.append(displayName).append("\n");
        sb.append("Abstract          : ");
        sb.append(abs.toString()).append("\n");
        sb.append(inputDesc.toString());
        sb.append(outputdesc.toString());
        return sb.toString();

    }


}
