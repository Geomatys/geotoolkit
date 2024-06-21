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

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

public class MockEnvelopeDescriptor extends AbstractProcessDescriptor{

    public static final String NAME = "envelopeToString";

    public static final ParameterDescriptor<Envelope> ENVELOPE = new ParameterBuilder()
            .addName("envelope")
            .setRequired(true)
            .create(Envelope.class, null);

    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder()
            .addName("InputParameters")
            .createGroup(ENVELOPE);

    public static final ParameterDescriptor<String> RESULT_ENVELOPE_STRING = new ParameterBuilder()
            .addName("result")
            .setRequired(true)
            .create(String.class, null);

    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder()
            .addName("OutputParameters")
            .createGroup(RESULT_ENVELOPE_STRING);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new MockEnvelopeDescriptor();

    private MockEnvelopeDescriptor() {
        super(NAME, MockProcessRegistry.IDENTIFICATION,
                new SimpleInternationalString(""),INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new EnvelopeToStringProcess(this, input);
    }

    public class EnvelopeToStringProcess extends AbstractProcess {

        public EnvelopeToStringProcess(final ProcessDescriptor descriptor, final ParameterValueGroup input) {
            super(descriptor, input);
        }

        @Override
        protected void execute() {
            final Envelope envelope = (Envelope) inputParameters.parameter(MockEnvelopeDescriptor.ENVELOPE.getName().getCode()).getValue();

            StringBuilder sb = new StringBuilder();
            sb.append(envelope.getDimension()).append(':');
            for(int i=0; i<envelope.getDimension(); i++) {
                sb.append(envelope.getMinimum(i)).append(':');
                sb.append(envelope.getMaximum(i)).append(':');
            }
            sb.append(envelope.getCoordinateReferenceSystem().toWKT());

            outputParameters.parameter(MockEnvelopeDescriptor.RESULT_ENVELOPE_STRING.getName().getCode()).setValue(sb.toString());
        }
    }

}
