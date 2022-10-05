/**
 *Terms and definitions
 *
 * <h2>SensorThings<h2>
 * <a href="https://www.ogc.org/standards/sensorthings">OGC</a>
 * <a href="https://en.wikipedia.org/wiki/SensorThings_API">Wikipedia</a>
 *
 * <ul>
 *   <li>Thing : A physical object which is identifiable (like a radar,satellite). which transmits the datas</li>
 *   <li>Locations : (geo-)position of a Thing</li>
 *   <li>HistoricalLocations : serie of time related positions of a Thing</li>
 *   <li>DataStream : A Thing has several DataStreams. A Datastream is associated with one Sensor. A DataStream is a collection of Observations of a single ObservedProperty.</li>
 *   <li>ObservedProperty : defines/describes a Phenomenon</li>
 *   <li>Sensor : An instrument which observe a Phenomenon and generates values stored in the DataStream</li>
 *   <li>Observation : measuring the value of an ObservedProperty (using the sensor obviously)</li>
 *   <li>FeatureOfInterest : <li>
 *   <li>Phenomenon : </li>
 * </ul>
 *
 * <h2>Observations and Measurements<h2>
 * <a href="https://www.ogc.org/standards/om">OGC</a>
 * <a href="https://en.wikipedia.org/wiki/Observations_and_Measurements"> Wikipedia</a>
 *
 * <ul>
 *   <li>Observation : act of measuring the value of a feature property. This imply using a defined procedure(sensor,instrument,algo or process chain)</li>
 *   <li>FeatureOfInterest : </li>
 *   <li>ObservedProperty : </li>
 *   <li>Observation : </li>
 *   <li>ObservationType : </li>
 *   <li>ObservationContext : </li>
 *   <li>Result : </li>
 *   <li>Procedure : </li>
 *   <li>Phenomenon : </li>
 *   <li>Measure : </li>
 *   <li>Measurement : </li>
 *   <li>Process : </li>
 *   <li>ProcessType : </li>
 *   <li>Specimen : </li>
 *   <li>LocatedSpecimen : </li>
 *   <li>SamplingFeature : </li>
 *   <li>SamplingFeatureComplex : </li>
 *   <li>SamplingCurve : </li>
 *   <li>SamplingPoint : </li>
 *   <li>SamplingSolid : </li>
 *   <li>SamplingSurface : </li>
 *   <li>SpatiallyExtensiveSamplingFeature : </li>
 *   <li>SurveyProcedure : </li>
 * </ul>
 *
 * <h2>Sensor Observations Service<h2>
 * <a href="https://www.ogc.org/standards/sos">OGC</a>
 * <a href="https://en.wikipedia.org/wiki/Sensor_Observation_Service"> Wikipedia</a>
 *
 * <ul>
 *   <li>Observation : </li>
 *   <li>ObservationTemplate : </li>
 *   <li>Result : </li>
 *   <li>ResultModel : </li>
 *   <li>ResultTemplate : </li>
 *   <li>Sensor : </li>
 *   <li>Event : </li>
 *   <li>FeatureOfInterest : </li>
 *   <li>Offering : </li>
 *   <li>OfferingPhenomenon : </li>
 *   <li>OfferingProcedure : </li>
 *   <li>OfferingSampling : </li>
 * </ul>
 *
 *
 * <h2>SensorML<h2>
 * <a href="https://www.ogc.org/standards/sensorml">OGC</a>
 * <a href="https://en.wikipedia.org/wiki/SensorML"> Wikipedia</a>
 *
 * <ul>
 *   <li></li>
 *   <li></li>
 * </ul>
 *
 *
 * <h2>Sensor Web Enablement<h2>
 * <a href="https://www.ogc.org/standards/swecommon">OGC Common</a>
 * <a href="https://www.ogc.org/standards/swes">OGC Service</a>
 * <a href="https://en.wikipedia.org/wiki/Sensor_Web_Enablement"> Wikipedia</a>
 *
 * <ul>
 *   <li></li>
 *   <li></li>
 * </ul>
 *
 *
 * <h2>Sensor Planning Service<h2>
 * <a href="https://www.ogc.org/standards/sps">OGC</a>
 *
 * <ul>
 *   <li></li>
 *   <li></li>
 * </ul>
 *
 * Entity
 * Measure
 * Measurement
 * Observation
 * SamplingFeature
 * Procedure
 * ProcedureType
 * Offering
 * OfferingEvent
 * Process
 * Sensor(-Location-Time)
 * DataRecord
 * Phenomenon
 * CompoundPhenomenon
 * CompositePhenomenon
 * ConstraintPhenomenon
 * PhenomenonSeries
 * PhenomenonType
 * PhenomenonSeriesType
 * PhenomenonProperty
 * PhenomenonField
 *
 */
package org.geotoolkit.observation;
