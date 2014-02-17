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
package org.geotoolkit.data.nmea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class NMEAFileReader implements FeatureReader {

    private static final Logger LOGGER = Logging.getLogger(NMEAFileReader.class);

    private static final SentenceFactory FACTORY = SentenceFactory.getInstance();

    private final InputStream input;

    private final NMEABuilder builder = new NMEABuilder();
    private final BufferedReader reader;
    private Feature next = null;
    private boolean finished = false;

    public NMEAFileReader(InputStream input) {
        this.input = input;
        final InputStreamReader isr = new InputStreamReader(input);
        this.reader = new BufferedReader(isr);
    }

    @Override
    public FeatureType getFeatureType() {
        return NMEAFeatureStore.NMEA_TYPE;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        findNext();
        Feature tmp = next;
        next = null;
        return tmp;
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        findNext();
        return next != null;
    }

    private void findNext() throws FeatureStoreRuntimeException {
        if (next != null || finished) {
            return;
        }
        boolean nextComplete = false;
        String data = "";
        while (!nextComplete && data != null) {
            try {
                data = reader.readLine();
            } catch (IOException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            try {
                if (SentenceValidator.isValid(data)) {
                    Sentence s = FACTORY.createParser(data);
                    nextComplete = builder.readSentence(s);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, ex.getLocalizedMessage());
                nextComplete = false;
            }

            if(!nextComplete && data == null){
                builder.endFeature();
            }

            if(data == null){
                finished = true;
                if(!nextComplete){
                    //try to build a last feature
                    builder.endFeature();
                }
            }
        }
        next = builder.next();
    }

    @Override
    public void close() {
        try {
            input.close();
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public void remove() {
        throw new FeatureStoreRuntimeException("Read-only reader.");
    }

}
