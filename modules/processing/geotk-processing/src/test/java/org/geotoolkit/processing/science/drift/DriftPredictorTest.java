/*
 *    (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift;

import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.junit.Test;
import org.opengis.util.NoSuchIdentifierException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class DriftPredictorTest {

    @Test
    public void testInput() throws NoSuchIdentifierException {
        final DriftPredictionDescriptor dpd = new DriftPredictionDescriptor(new DefaultServiceIdentification());
    }
}
