package org.geotoolkit.data.mif;

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.Strings;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

import java.util.Scanner;

/**
 * MIF reader which is designed to browse data AND ONLY data, it's to say geometry data from MIF file, and all data from
 * MID file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 22/02/13
 */
public class MIFFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {

    Scanner mifScanner = null;
    Scanner midScanner = null;

    MIFReader master;
    SimpleFeatureType readType;

    public MIFFeatureReader(MIFReader parent, Name typeName) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Parent reader", parent);
        master = parent;
        readType = master.getType(typeName);
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public SimpleFeature next() throws FeatureStoreRuntimeException {
        try {
        if(readType.equals(master.getBaseType())) {
            final String line = midScanner.nextLine();
            final String[] splitted = Strings.split(line, master.mifDelimiter);
//            for (int i = 0; i < splitted.length; i++) {
//                AttributeType att = mifBaseType.getType(i);
//                Object value = null;
//                if (!splitted[i].isEmpty()) {
//                    value = Converters.convert(splitted[i], att.getBinding());
//                }
//                sfb.set(i, value);
//            }
        }
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException("Can't reach next feature with typename "+readType.getTypeName(), ex);
        }
        return null;
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        try {
            if(readType.getName().equals(master.getBaseType())) {
                if(midScanner.hasNextLine()) {
                    return true;
                }
            } else {
                if(midScanner.hasNextLine()) {
                    // Check the MapInfo geometry typename to se if there's some next in the file.
                    String geomName = readType.getGeometryDescriptor().getLocalName();
                    if(mifScanner.hasNext(geomName.toUpperCase()+"|"+geomName.toLowerCase())) {
                        return true;
                    }
                }
            }
        }catch(DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
        return false;
    }

    @Override
    public void close() {
        if(mifScanner != null) {
            mifScanner.close();
        }
        if(midScanner != null) {
            midScanner.close();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("MIF datastore is Read Only.");
    }
}
