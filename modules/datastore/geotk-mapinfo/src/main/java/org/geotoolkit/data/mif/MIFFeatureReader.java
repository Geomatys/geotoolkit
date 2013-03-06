package org.geotoolkit.data.mif;

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.Strings;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.operation.MathTransform;

import java.util.Scanner;

/**
 * MIF reader which is designed to browse data AND ONLY data, it's to say geometry data from MIF file, and all data from
 * MID file.
 *
 * @author Alexis Manin (Geomatys)
 * @date : 22/02/13
 */
public class MIFFeatureReader implements FeatureReader<FeatureType, Feature> {

    Scanner mifScanner = null;
    Scanner midScanner = null;

    /**
     * Counters : feature counter (Mid and mif lines are not equal for the same feature.
     */
    int mifCounter = 0;
    int midCounter = 0;

    /**
     * booleans to check if we just read mid file (feature type doesn't contain any geometry) or just MIF file
     * (geometries only), or both.
     */
    boolean readMid = false;
    boolean readMif = false;

    MIFManager master;
    FeatureType readType;

    public MIFFeatureReader(MIFManager parent, Name typeName) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Parent reader", parent);
        master = parent;
        readType = master.getType(typeName);
        if(readType.equals(master.getBaseType()) || readType.getSuper().equals(master.getBaseType())) {
            readMid = true;
        } else if(readType.getGeometryDescriptor() != null) {
            readMif = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleFeatureType getFeatureType() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Feature next() throws FeatureStoreRuntimeException {

        Feature resFeature = null;
        final SimpleFeature mifFeature;

        try {
            checkScanners();

            String name = (readMif)? "mif"+mifCounter : "mid"+midCounter;
            resFeature = FeatureUtilities.defaultFeature(readType, name);

            // We check the MIF file first, because it will define the feature count to reach the next good typed data.
            if(readMif) {
                final String geomId = readType.getName().getLocalPart();
                String currentPattern;
                while(mifScanner.hasNextLine()) {
                    currentPattern = mifScanner.findInLine(master.alphaPattern);
                    if(geomId.equalsIgnoreCase(currentPattern)) {
                        parseGeometry(geomId, resFeature, master.getTransform());
                    } else if(MIFUtils.getGeometryType(currentPattern, master.getMifCRS(), master.getBaseType()) != null) {
                        mifCounter++;
                    }
                    mifScanner.nextLine();
                }
                mifCounter++;
            }

            if(readMid) {
                final SimpleFeatureType baseType = master.getBaseType();
                //parse MID line.
                while(midCounter < mifCounter) {
                    midScanner.nextLine();
                    midCounter++;
                }
                final String line = midScanner.nextLine();
                final String[] split = Strings.split(line, master.mifDelimiter);
                for (int i = 0; i < split.length; i++) {
                    AttributeType att = baseType.getType(i);
                    Object value = null;
                    if (!split[i].isEmpty()) {
                        value = Converters.convert(split[i], att.getBinding());
                    }
                    resFeature.getProperty(att.getName()).setValue(value);
                }
                midCounter++;
            }

        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException("Can't reach next feature with type name " + readType.getName().getLocalPart(), ex);
        }

        return resFeature;
    }

    private void parseGeometry(String geomId, Feature toFill, MathTransform transform) {
        final String upperId = geomId.toUpperCase();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        boolean midNext = false;
        boolean mifNext = false;

        final SimpleFeatureType masterBaseType;
        try {
            checkScanners();

            if (readMid) {
                masterBaseType = master.getBaseType();
                if (midScanner.hasNextLine()) {
                    midNext = true;
                }
            }

            if (readMif) {
                // Check the MapInfo geometry typename to see if there's some next in the file.
                String geomName = readType.getGeometryDescriptor().getLocalName();
                if (mifScanner.hasNext(geomName.toUpperCase() + "|" + geomName.toLowerCase())) {
                    mifNext = true;
                }
            }
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }

        if (readMid && !readMif) {
            return midNext;
        } else if (readMif && !readMid) {
            return mifNext;
        } else return (midNext && mifNext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (mifScanner != null) {
            mifScanner.close();
        }
        if (midScanner != null) {
            midScanner.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("MIF datastore is Read Only.");
    }


    /**
     * Check if we have an open access to mif/mid files. If not, try to get one.
     *
     * @throws DataStoreException If we're unable to access files.
     */
    private void checkScanners() throws DataStoreException {
        if(readMif) {
            if(mifScanner == null) {
                mifCounter = 0;
                mifScanner = new Scanner(master.getMIFPath());
            }
        }

        if(readMid) {
            if(midScanner == null) {
                midCounter = 0;
                midScanner = new Scanner(master.getMIDPath());
            }
        }
    }
}
