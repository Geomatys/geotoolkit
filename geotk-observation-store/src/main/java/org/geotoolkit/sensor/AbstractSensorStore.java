/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.sensor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.sml.xml.AbstractSensorML;
import org.geotoolkit.sml.xml.SensorMLMarshallerPool;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractSensorStore extends DataStore implements SensorStore {

    protected final ParameterValueGroup config;
    protected SensorReader reader;
    protected SensorWriter writer;

    public AbstractSensorStore(ParameterValueGroup source) {
        this.config = source;
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.ofNullable(config);
    }

    @Override
    public Map<String, List<String>> getAcceptedSensorMLFormats() {
        if (reader != null) {
            return reader.getAcceptedSensorMLFormats();
        }
        return new HashMap<>();
    }

    @Override
    public Collection<String> getSensorNames() throws DataStoreException {
        if (reader != null) {
            return reader.getSensorNames();
        }
        return new ArrayList<>();
    }

    @Override
    public int getSensorCount() throws DataStoreException {
        if (reader != null) {
            return reader.getSensorCount();
        }
        return -1;
    }

    @Override
    public AbstractSensorML getSensorML(final String sensorID) throws DataStoreException {
        if (reader != null) {
            return reader.getSensor(sensorID);
        }
        return null;
    }

    @Override
    public boolean deleteSensor(String id) throws DataStoreException {
        if (writer != null) {
            final boolean result = writer.deleteSensor(id);
            if (result && reader != null) {
                reader.removeFromCache(id);
            }
            return result;
        }
        return false;
    }

    @Override
    public String getNewSensorId() throws DataStoreException {
        return writer.getNewSensorId();
    }

    @Override
    public boolean writeSensor(String id, Object sensor) throws DataStoreException {
        if (writer != null) {
            return writer.writeSensor(id, (AbstractSensorML) sensor);
        }
        return false;
    }


    @Override
    public int replaceSensor(String id, Object sensor) throws DataStoreException {
        if (writer != null) {
            return writer.replaceSensor(id, (AbstractSensorML) sensor);
        }
        return -1;
    }

    @Override
    public void close() {
        if (reader != null) {reader.destroy();}
        if (writer != null) {writer.destroy();}
    }

    protected String marshallSensor(AbstractSensorML sml) throws JAXBException {
        final Marshaller m = SensorMLMarshallerPool.getInstance().acquireMarshaller();
        final StringWriter sw = new StringWriter();
        m.marshal(sml, sw);
        SensorMLMarshallerPool.getInstance().recycle(m);
        return sw.toString();
    }
}
