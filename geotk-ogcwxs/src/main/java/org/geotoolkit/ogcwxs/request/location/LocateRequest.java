/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.request.location;

import org.geotoolkit.ols.xml.v121.InputGatewayParametersType;

/**
 * Parameters for the OLS Gateway Service {@code SLIR} (Standard Location Immediate Request) operation.
 *
 * <blockquote><q>
 * This is the interface between the GeoMobility Server and Location Server
 * that resides in the GMLC or MPC through which OpenLS services obtain
 * position data for Mobile Terminals.
 * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=23">; OGC 07-074 OpenLS Core, Part 2: Location Services Gateway</a></small></i>
 * </blockquote>
 * <br>
 * <pre>
 * &lt;SLIR&gt;
 *   &lt;InputGatewayParameters priority="HIGH" locationType="CURRENT_OR_LAST" requestedsrsName="WGS84"&gt;
 *     &lt;InputMSID&gt;
 *       &lt;InputMSInformation msIDType="IPV4" msIDValue="461018765728"&gt;&lt;/MSInformation&gt;
 *     &lt;/InputMSID&gt;
 *     &lt;RequestedQoP responseReq="No_Delay" responseTimer="20"&gt;
 *       &lt;HorizontalAcc&gt;
 *         &lt;Distance value="1000"&gt;&lt;/Distance&gt;
 *       &lt;/HorizontalAcc&gt;
 *     &lt;/RequestedQoP&gt;
 *   &lt;/InputGatewayParameters&gt;
 * &lt;/SLIR&gt;
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class LocateRequest extends AbstractLocationRequest<LocateRequest> {

    private InputGatewayParametersType inputGatewayParameters;

    /**
     * Mobile subscriber ids to locate, and how to locate them. Required.
     *
     * @see #inputGatewayParameters(InputGatewayParametersType)
     */
    public InputGatewayParametersType getInputGatewayParameters() {
        return inputGatewayParameters;
    }

    /**
     * <blockquote><q>
     * All Gateway Requests and Responses descend from this Type. This is
     * required to support other Location Requests defined in LIF.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=111">; OGC 07-074 OpenLS Core, AbstractGatewayParametersType</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Required.</li>
     *   <li>locationType attribute default: CURRENT (allowed: CURRENT, LAST, CURRENT_OR_LAST).</li>
     * </ul>
     */
    public LocateRequest inputGatewayParameters(InputGatewayParametersType inputGatewayParameters) {
        this.inputGatewayParameters = inputGatewayParameters;
        return this;
    }
}
