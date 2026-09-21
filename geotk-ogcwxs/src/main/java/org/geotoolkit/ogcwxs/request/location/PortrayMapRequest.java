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

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.ols.xml.v121.LayerType;
import org.geotoolkit.ols.xml.v121.OutputType;
import org.geotoolkit.ols.xml.v121.OverlayType;

/**
 * Parameters for the OLS Presentation Service {@code PortrayMapRequest} operation.
 *
 * <blockquote><q>
 * This service renders geographic information for display on a Mobile
 * Terminal. Any OpenLS Application may call upon this service to obtain a
 * map of a desired area, with or without map overlays that depict one or
 * more OpenLS ADTs.
 * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=33">; OGC 07-074 OpenLS Core, Part 7: Presentation Service</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class PortrayMapRequest extends AbstractLocationRequest<PortrayMapRequest> {

    private final List<OutputType> output = new ArrayList<>();
    private LayerType basemap;
    private final List<OverlayType> overlay = new ArrayList<>();

    /**
     * Requested map outputs (size, format, bounding box, ...). At least one is required.
     *
     * @see #output(OutputType)
     */
    public List<OutputType> getOutput() {
        return output;
    }

    /**
     * <blockquote><q>
     * Specifies the output of the map(s) that should generated
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=122">; OGC 07-074 OpenLS Core, PortrayMapRequestType/Output</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>At least one Output is required.</li>
     * </ul>
     */
    public PortrayMapRequest output(OutputType output) {
        this.output.add(output);
        return this;
    }

    /**
     * Base map layer to render. Optional.
     *
     * @see #basemap(LayerType)
     */
    public LayerType getBasemap() {
        return basemap;
    }

    /**
     * <blockquote><q>
     * The layers that should make up the baseMap. (getCapabilites provides
     * the list the impl provides)
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=122">; OGC 07-074 OpenLS Core, PortrayMapRequestType/BaseMap</a></small></i>
     * </blockquote>
     */
    public PortrayMapRequest basemap(LayerType basemap) {
        this.basemap = basemap;
        return this;
    }

    /**
     * Overlays (e.g. routes, POIs) drawn on top of the base map. Optional.
     *
     * @see #overlay(OverlayType)
     */
    public List<OverlayType> getOverlay() {
        return overlay;
    }

    /**
     * <blockquote><q>
     * Overlays the set of ADT's onto the basemap
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=122">; OGC 07-074 OpenLS Core, PortrayMapRequestType/Overlay</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Optional; may be repeated.</li>
     * </ul>
     */
    public PortrayMapRequest overlay(OverlayType overlay) {
        this.overlay.add(overlay);
        return this;
    }
}
