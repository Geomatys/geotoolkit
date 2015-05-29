/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.coverage.processing;

import java.util.Objects;
import java.io.Serializable;
import java.awt.RenderingHints;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.processing.Operation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.util.InternationalString;

import org.geotoolkit.factory.Hints;
import org.apache.sis.util.Classes;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Provides descriptive information for a {@linkplain Coverage} processing operation.
 * The descriptive information includes such information as the name of the operation,
 * operation description, and number of source grid coverages required for the operation.
 * <p>
 * This base class implements all methods from the {@link Operation} interface. Those methods get
 * the information they need from a {@link ParameterDescriptorGroup} object which must be supplied
 * at construction time. Every {@linkplain ParameterDescriptor#getValueClass() value class} that
 * are {@linkplain Class#isAssignableFrom(Class) assignable} to {@link Coverage} are considered as
 * a <cite>source</cite> and will be included in the count returned by {@link #getNumSources()}.
 * Other parameters are "ordinary" and do not get any special processing.
 * <p>
 * This base class declares an abstract method, {@link #doOperation doOperation}, which must be
 * implemented by subclasses. This base class makes no assumption about the kind of sources and
 * the kind of result that this operation works on, except that they must be at least of the
 * {@link Coverage} type. Subclasses will typically restrict the kind of sources to a subclasses
 * of {@code Coverage}, or restrict the number of spatio-temporal dimensions that the sources can
 * have.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
public abstract class AbstractOperation implements Operation, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -1441856042779942954L;;

    /**
     * The parameters descriptor.
     */
    protected final ParameterDescriptorGroup descriptor;

    /**
     * Constructs an operation. The operation name will be the same than the
     * parameter descriptor name.
     *
     * @param descriptor The parameters descriptor.
     */
    protected AbstractOperation(final ParameterDescriptorGroup descriptor) {
        ensureNonNull("descriptor", descriptor);
        this.descriptor = descriptor;
    }

    /**
     * Returns the name of the processing operation. The default implementation
     * returns the {@linkplain #descriptor} code name.
     *
     * @todo The return type will be changed from {@link String} to {@code Identifier}.
     */
    @Override
    public String getName() {
        return descriptor.getName().getCode();
    }

    /**
     * Returns the description of the processing operation. If there is no description,
     * returns {@code null}. The default implementation returns the {@linkplain #descriptor}
     * remarks.
     *
     * @deprecated Return type need to be changed, maybe to {@link InternationalString}.
     */
    @Override
    @Deprecated
    public String getDescription() {
        final InternationalString remarks = descriptor.getRemarks();
        return (remarks!=null) ? remarks.toString() : null;
    }

    /**
     * Returns the URL for documentation on the processing operation. If no online documentation
     * is available the string will be null. The default implementation returns {@code null}.
     *
     * @deprecated To be replaced by a method returning a {@code Citation}.
     */
    @Override
    @Deprecated
    public String getDocURL() {
        return null;
    }

    /**
     * Returns the version number of the implementation.
     *
     * @deprecated Replacement to be determined.
     */
    @Override
    @Deprecated
    public String getVersion() {
        return descriptor.getName().getVersion();
    }

    /**
     * Returns the vendor name of the processing operation implementation.
     * The default implementation returns "Geotoolkit.org".
     *
     * @deprecated To be replaced by {@code getName().getAuthority()}.
     */
    @Override
    @Deprecated
    public String getVendor() {
        return "Geotoolkit.org";
    }

    /**
     * Returns the number of source coverages required for the operation.
     */
    @Override
    public int getNumSources() {
        return getNumSources(descriptor);
    }

    /**
     * Returns the number of source coverages in the specified parameter group.
     */
    private static int getNumSources(final ParameterDescriptorGroup descriptor) {
        int count = 0;
        for (final GeneralParameterDescriptor candidate : descriptor.descriptors()) {
            if (candidate instanceof ParameterDescriptorGroup) {
                count += getNumSources((ParameterDescriptorGroup) candidate);
                continue;
            }
            if (candidate instanceof ParameterDescriptor<?>) {
                final Class<?> type = ((ParameterDescriptor<?>) candidate).getValueClass();
                if (Coverage.class.isAssignableFrom(type)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns an initially empty set of parameters.
     */
    @Override
    public ParameterValueGroup getParameters() {
        return descriptor.createValue();
    }

    /**
     * Applies a process operation to a coverage. This method is invoked by
     * {@link DefaultCoverageProcessor}.
     *
     * @param  parameters List of name value pairs for the parameters required for the operation.
     * @param  hints A set of rendering hints, or {@code null} if none. The {@code DefaultCoverageProcessor}
     *         may provides hints for the following keys: {@link Hints#COORDINATE_OPERATION_FACTORY}
     *         and {@link Hints#JAI_INSTANCE}.
     * @return The result as a coverage.
     *
     * @throws IllegalArgumentException If a parameter has an illegal value.
     * @throws CoverageProcessingException if the operation can't be applied.
     */
    protected abstract Coverage doOperation(final ParameterValueGroup parameters, final Hints hints)
            throws IllegalArgumentException, CoverageProcessingException;

    /**
     * Returns the {@code CoverageProcessor} instance used for an operation. The instance is
     * fetch from the rendering hints given to the {@link #doOperation} method. If no processor
     * is specified, then a default one is returned.
     *
     * @param  hints The rendering hints, or {@code null} if none.
     * @return The {@code CoverageProcessor} instance in use (never {@code null}).
     */
    static AbstractCoverageProcessor getProcessor(final RenderingHints hints) {
        if (hints != null) {
            final Object value = hints.get(Hints.GRID_COVERAGE_PROCESSOR);
            if (value instanceof AbstractCoverageProcessor) {
                return (AbstractCoverageProcessor) value;
            }
        }
        return AbstractCoverageProcessor.getInstance();
    }

    /**
     * Returns a hash value for this operation. This value need not remain consistent between
     * different implementations of the same class.
     */
    @Override
    public int hashCode() {
        // Since we should have only one operation registered for each name,
        // the descriptors hash code should be enough.
        return descriptor.hashCode() ^ (int) serialVersionUID;
    }

    /**
     * Compares the specified object with this operation for equality.
     *
     * @param object The object to compare with this operation.
     * @return {@code true} if the given object is equals to this operation.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final AbstractOperation that = (AbstractOperation) object;
            return Objects.equals(this.descriptor, that.descriptor);
        }
        return false;
    }

    /**
     * Returns a string representation of this operation. The returned string is
     * implementation dependent. It is usually provided for debugging purposes only.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + '[' + getName() + ']';
    }
}
