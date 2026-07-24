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
package org.geotoolkit.ogcapi.dto.connectedsystems.geojson;

/**
 * The {@code properties} sub-object of a {@link Procedure} GeoJSON feature.
 *
 * <p>Carries no extra fields beyond {@link FeatureProperties}. {@code featureType} is restricted
 * to one of: {@code http://www.w3.org/ns/sosa/Procedure}, {@code
 * http://www.w3.org/ns/sosa/ObservingProcedure}, {@code
 * http://www.w3.org/ns/sosa/SamplingProcedure}, {@code
 * http://www.w3.org/ns/sosa/ActuatingProcedure}, {@code http://www.w3.org/ns/sosa/System}, {@code
 * http://www.w3.org/ns/sosa/Sensor}, {@code http://www.w3.org/ns/sosa/Actuator}, {@code
 * http://www.w3.org/ns/sosa/Sampler}, {@code http://www.w3.org/ns/sosa/Platform}, or the
 * equivalent CURIE forms ({@code sosa:Procedure}, {@code sosa:ObservingProcedure}, {@code
 * sosa:SamplingProcedure}, {@code sosa:ActuatingProcedure}, {@code sosa:System}, {@code
 * sosa:Sensor}, {@code sosa:Actuator}, {@code sosa:Sampler}, {@code sosa:Platform}).</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public class ProcedureProperties extends FeatureProperties {

}
