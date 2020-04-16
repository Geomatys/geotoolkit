/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.sts;

import org.geotoolkit.util.StringUtilities;

/**
 *
 * @author guilhem
 */
public class ExpandOptions {


public final boolean multiDatastreams;
public final boolean featureOfInterest;
public final boolean datastreams;
public final boolean observedProperties;
public final boolean observations;
public final boolean sensors;
public final boolean things;
public final boolean historicalLocations;
public final boolean locations;

    public ExpandOptions(STSRequest req) {
        multiDatastreams    = StringUtilities.containsIgnoreCase(req.getExpand(), "MultiDatastreams");
        featureOfInterest   = StringUtilities.containsIgnoreCase(req.getExpand(), "FeatureOfInterest") || StringUtilities.containsIgnoreCase(req.getExpand(), "FeaturesOfInterest");
        datastreams         = StringUtilities.containsIgnoreCase(req.getExpand(), "Datastreams");
        observedProperties  = StringUtilities.containsIgnoreCase(req.getExpand(), "ObservedProperties") || StringUtilities.containsIgnoreCase(req.getExpand(), "ObservedProperty");
        observations        = StringUtilities.containsIgnoreCase(req.getExpand(), "Observations");
        sensors             = StringUtilities.containsIgnoreCase(req.getExpand(), "Sensors");
        things              = StringUtilities.containsIgnoreCase(req.getExpand(), "Things");
        historicalLocations = StringUtilities.containsIgnoreCase(req.getExpand(), "HistoricalLocations");
        locations           = StringUtilities.containsIgnoreCase(req.getExpand(), "Locations");
    }
}
