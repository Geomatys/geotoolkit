/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.processing.coverage.math;

import java.awt.image.DataBuffer;
import org.apache.sis.parameter.ParameterBuilder;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Shared optional "outputSampleType" parameter for the coverage math processes (substract, sum,
 * multiply, divide, and their *WithValue variants). Results are always computed as double; this
 * parameter only controls the sample type of the output image. Default is DOUBLE (lossless).
 * SOURCE reproduces the legacy behaviour of copying the input coverage's original sample type,
 * which truncates non-integral results (e.g. NDVI/EVI ratios on byte imagery).
 */
public final class OutputSampleTypeParameter {

    public static final String NAME = "outputSampleType";
    public static final String SOURCE = "SOURCE";

    public static final ParameterDescriptor<String> DESCRIPTOR = new ParameterBuilder()
            .addName(NAME)
            .setRemarks("Sample type of the result coverage: BYTE, USHORT, SHORT, INT, FLOAT, DOUBLE "
                    + "(default), or SOURCE to keep the input coverage's original sample type (legacy "
                    + "behaviour, truncates non-integral results).")
            .setRequired(false)
            .createEnumerated(String.class, new String[]{SOURCE, "BYTE", "USHORT", "SHORT", "INT", "FLOAT", "DOUBLE"}, "DOUBLE");

    private OutputSampleTypeParameter() {
    }

    /**
     * @param input input parameters of the process
     * @param sourceDataType {@link DataBuffer} type of the input coverage, used only when SOURCE is requested
     * @return the {@link DataBuffer} type constant to use for the output image
     */
    public static int resolve(ParameterValueGroup input, int sourceDataType) {
        return switch (input.parameter(NAME).stringValue()) {
            case SOURCE -> sourceDataType;
            case "BYTE" -> DataBuffer.TYPE_BYTE;
            case "USHORT" -> DataBuffer.TYPE_USHORT;
            case "SHORT" -> DataBuffer.TYPE_SHORT;
            case "INT" -> DataBuffer.TYPE_INT;
            case "FLOAT" -> DataBuffer.TYPE_FLOAT;
            default -> DataBuffer.TYPE_DOUBLE;
        };
    }

    public static boolean isSource(ParameterValueGroup input) {
        return SOURCE.equals(input.parameter(NAME).stringValue());
    }
}
