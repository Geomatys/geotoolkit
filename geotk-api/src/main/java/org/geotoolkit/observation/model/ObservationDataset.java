/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.observation.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ObservationDataset {

    public final GeoSpatialBound spatialBound = new GeoSpatialBound();

    public final List<Observation> observations = new ArrayList<>();

    public final List<SamplingFeature> featureOfInterest = new ArrayList<>();

    public final List<Phenomenon> phenomenons = new ArrayList<>();

    public final List<ProcedureDataset> procedures = new ArrayList<>();

    public final List<Offering> offerings = new ArrayList<>();
}
