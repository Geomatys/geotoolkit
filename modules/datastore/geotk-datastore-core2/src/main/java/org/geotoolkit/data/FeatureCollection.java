
package org.geotoolkit.data;

import java.util.Collection;
import org.geotoolkit.data.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.Envelope;

public interface FeatureCollection<F extends Feature> extends Collection<F> {

    String getID();

    FeatureType getSchema();

    Envelope getEnvelope();

    @Override
    FeatureIterator<F> iterator();

    void addListener();

    void removeListener();

}
