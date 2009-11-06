
package org.geotoolkit.feature.xml;

import org.geotoolkit.data.collection.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A interface to serialize feature in XML.
 *
 * @module pending
 * 
 * @author Guilhem Legal (Geomatys)
 */
public interface XmlFeatureWriter {

    /**
     * Return an XML representation of the specified feature.
     *
     * @param feature The feature to marshall.
     * @return An XML string representing the feature.
     */
    String write(SimpleFeature feature);

    /**
     * Return an XML representation of the specified feature collection.
     *
     * @param feature The feature collection to marshall.
     * @return An XML string representing the feature collection.
     */
    String write(FeatureCollection featureCollection);
}
