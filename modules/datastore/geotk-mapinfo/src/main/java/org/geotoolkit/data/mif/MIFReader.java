package org.geotoolkit.data.mif;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.feature.type.DefaultGeometryDescriptor;
import org.geotoolkit.feature.type.DefaultGeometryType;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.Strings;
import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Read data of .mif file (from mif/mid mapinfo exchange format).
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 20/02/13
 */
public class MIFReader {

    private static final Logger LOGGER = Logging.getLogger(MIFReader.class.getName());

    /**
     * The number of mandatoty tags to match in the header.
     */
    private static final int HEADER_MANDATORY_COUNT = 4;

    /**
     * A pattern frequently used to find MIF categories
     */
    private static final Pattern alphaPattern = Pattern.compile("^\\w+");

    /**
     * Mif file access
     */
    private String mifName;
    private String mifPath;
    private Scanner mifScanner;

    /**
     * Header tag values. See {@link MIFHeaderCategory} for tag description.
     */
    private short mifVersion = 300;
    private String mifCharset = "UTF-8";
    public char mifDelimiter = '\t';
    private ArrayList<Short> mifUnique = new ArrayList<Short>();
    private ArrayList<Short> mifIndex = new ArrayList<Short>();
    private CoordinateReferenceSystem mifCRS = null;
    private double[] mifTransform = null;
    private short mifColumnsCount = -1;

    /**
     * Type and data containers
     */
    private Set<Name> names = null;
    private DefaultSimpleFeatureType mifBaseType = null;
    private ArrayList<SimpleFeatureType> mifChildTypes = null;
    private FeatureCollection mifFeatures = null;

    public MIFReader(File mifFile) throws NullArgumentException, FileNotFoundException {
        ArgumentChecks.ensureNonNull("Input file", mifFile);
        mifPath = mifFile.getAbsolutePath();
        int lastSeparatorIndex = mifPath.lastIndexOf(System.getProperty("file.separator"));
        mifName = mifPath.substring(lastSeparatorIndex);
        if (mifName.endsWith(".mif") || mifName.endsWith(".MIF")) {
            mifName = mifName.substring(0, mifName.length() - 4);
        }
        mifScanner = new Scanner(mifFile);
        try {
            mifCRS = CRS.decode("EPSG:4326");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Default CRS can't be initialized.", e);
        }
    }

    public MIFReader(String mifFilePath) throws NullArgumentException, FileNotFoundException {
        ArgumentChecks.ensureNonNull("Input file path", mifFilePath);
        mifPath = mifFilePath;
        int lastSeparatorIndex = mifPath.lastIndexOf(System.getProperty("file.separator"));
        mifName = mifPath.substring(lastSeparatorIndex);
        if (mifName.endsWith(".mif") || mifName.endsWith(".MIF")) {
            mifName = mifName.substring(0, mifName.length() - 4);
        }
        mifScanner = new Scanner(mifFilePath);
    }

    /**
     * Read .MIF file header and get needed information for data reading.
     *
     * @throws DataStoreException If all mandatory data can't be read.
     */
    private void parseHeader() throws DataStoreException {
        //Reset the file scanner to ensure we'll start on file top position.
        if (mifScanner != null) {
            mifScanner.close();
        }
        mifScanner = new Scanner(mifPath);

        // A trigger to tell us if all mandatory categories have been parsed.
        int allRequiredDone = 0;
        while (mifScanner.hasNextLine()) {
            final String matched = mifScanner.findInLine(alphaPattern);

            if (matched == null && allRequiredDone < HEADER_MANDATORY_COUNT) {
                throw new DataStoreException("File header can't be read (Mandatory marks are missing)");
            }

            if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.VERSION.name())) {
                if (mifScanner.hasNextShort()) {
                    mifVersion = mifScanner.nextShort();
                } else {
                    throw new DataStoreException("MIF Version can't be read.");
                }

            } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.CHARSET.name())) {
                final String charset = mifScanner.findInLine(alphaPattern);

            } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.DELIMITER.name())) {
                final String tmpStr = mifScanner.findInLine("(\"|\')[^\"](\"|\')");
                if (tmpStr == null || tmpStr.length() != 3) {
                    throw new DataStoreException(MIFHeaderCategory.DELIMITER.name() +
                            " tag value is not formatted as it should (must be \"C\" with C the wanted delimiter character).");
                }
                mifDelimiter = (char) tmpStr.getBytes()[1];

            } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.UNIQUE.name())) {
                while (mifScanner.hasNextShort()) {
                    mifUnique.add(mifScanner.nextShort());
                }

            } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.INDEX.name())) {
                while (mifScanner.hasNextShort()) {
                    mifUnique.add(mifScanner.nextShort());
                }

            } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.COORDSYS.name())) {

            } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.TRANSFORM.name())) {

                //Build the parent feature type for data contained in this MIF.
            } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.COLUMNS.name())) {
                if (mifScanner.hasNextShort()) {
                    mifColumnsCount = mifScanner.nextShort();
                } else {
                    throw new DataStoreException("MIF Columns has no attribute count specified.");
                }

                // Check the attributes
                final ArrayList<AttributeDescriptor> schema = new ArrayList<AttributeDescriptor>();
                for (int i = 0; i < mifColumnsCount; i++) {
                    mifScanner.nextLine();
                    final String attName = mifScanner.findInLine(alphaPattern);
                    final String tmpType = mifScanner.findInLine(alphaPattern);
                    // Since scanner doesn't move if no matching pattern is found, we can test only the second string.
                    if (tmpType == null) {
                        throw new DataStoreException("A problem occured while reading columns tag from .MIF header.");
                    }
                    final Class binding = MIFUtils.getColumnType(tmpType);
                    if (binding == null) {
                        throw new DataStoreException(
                                "The typename " + tmpType + "(from " + attName + " attribute) is an unknown attribute type.");
                    }
                    /** todo : instantiate filters for String & Double type (length limitations). */
                    final Name name = new DefaultName(attName);
                    final DefaultAttributeType attType = new DefaultAttributeType(name, binding, true, false, null, null, null);
                    final DefaultAttributeDescriptor desc = new DefaultAttributeDescriptor(attType, name, 1, 1, true, null);
                    schema.add(desc);
                }
                Name name = new DefaultName(mifName);
                mifBaseType = new DefaultSimpleFeatureType(name, schema, null, false, null, null, null);

            } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.DATA.name())) {
                if (allRequiredDone < HEADER_MANDATORY_COUNT) {
                    throw new DataStoreException("File header can't be read (Mandatory marks are missing)");
                } else {
                    break;
                }
            }
        }
    }


    /**
     * Read DATA part of the .MIF file to get feature geometries.
     *
     * @throws DataStoreException If a problem is encountered while reading file.
     */
    private void readData() {

    }


    /**
     * Try to get and read the .MID file which should contains the feature attributes.
     * <p/>
     * The MID file is in reality a CSV file whose columns are specified in the MIF header file.
     *
     * @throws FileNotFoundException
     * @throws DataStoreException
     */
    private void readMIDFile() throws FileNotFoundException, DataStoreException {
        // Ensure the needed feature type is built.
        if (mifBaseType == null) {
            parseHeader();
        }

        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(mifBaseType);
        if (mifColumnsCount <= 0) {
            return;
        }

        final String midPath;
        if (mifPath.endsWith(".mif") || mifPath.endsWith(".MIF")) {
            midPath = mifPath.substring(0, mifPath.length() - 4);
        } else {
            throw new DataStoreException("There's an extension problem with Mif file. A correct extension is needed in order to retrieve the associated mid file.");
        }

        // We have to check if the extension is upper or lower case, since unix file systems are case sensitive.
        if (mifPath.endsWith(".mif")) {
            midPath.concat(".mid");
        } else if (mifPath.endsWith(".MIF")) {
            midPath.concat(".MID");
        }
        File midFile = new File(midPath);
        if (midFile == null || !midFile.exists()) {
            throw new DataStoreException("Unable to find the mandatory mid file.");
        }

        // Parse the mid file line by line, as it's as much as CSV file.
        Scanner midScanner = new Scanner(midPath);
        while (midScanner.hasNextLine()) {
            final String curLine = midScanner.nextLine();
            final String[] splitted = Strings.split(curLine, mifDelimiter);
            for (int i = 0; i < splitted.length; i++) {
                AttributeType att = mifBaseType.getType(i);
                Object value = null;
                if (!splitted[i].isEmpty()) {
                    value = Converters.convert(splitted[i], att.getBinding());
                }
                sfb.set(i, value);
            }
        }
    }

    /**
     * Return the different type names specified by this document.
     * <p/>
     * If the scanner did not already read them, we catch them all by parsing the file with {@link org.geotoolkit.data.mif.MIFReader#buildDataTypes()}.
     *
     * @return a list ({@link HashSet}) of available feature types in that document.
     * @throws DataStoreException if we get a problem parsing the file.
     */
    public Set<Name> getTypeNames() throws DataStoreException {
        if (names == null) {
            Set<Name> names = new HashSet<Name>();

            checkDataTypes();
            names.add(mifBaseType.getName());
            for (SimpleFeatureType t : mifChildTypes) {
                names.add(t.getName());
            }
        }
        return names;
    }

    /**
     * Ensure that dataTypes are built. If not, build them.
     */
    public void checkDataTypes() throws DataStoreException {
        if (mifBaseType == null) {
            parseHeader();
        } else {
            if (mifChildTypes == null) {
                mifScanner.close();
                mifScanner = new Scanner(mifPath);
            }
        }

        if (mifChildTypes == null) {
            buildDataTypes();
        }
    }

    /**
     * Browse the MIF file to get the geometry types it contained. With it, we create a new feature type
     * for each geometry type found. They'll all get the base feature type (defining MID attributes) as parent.
     * <p/>
     * IMPORTANT :  we'll browse the file only for geometry TYPES, so no other data we'll be read.
     * <p/>
     * MORE IMPORTANT : This method does not manage scanner start position, we assume that caller have prepared it
     * itself (to avoid close / re-open each time).
     */
    private void buildDataTypes() {
        mifChildTypes = new ArrayList<SimpleFeatureType>();

        ArrayList<String> triggeredTypes = new ArrayList<String>();
        while (mifScanner.hasNextLine()) {
            final String typename = mifScanner.findInLine(alphaPattern);
            final Class bind = MIFUtils.getGeometryType(typename);
            if (bind != null) {
                if (triggeredTypes.contains(typename)) {
                    continue;
                }
                triggeredTypes.add(typename);
                Name tName = new DefaultName(typename);
                Name ftName = new DefaultName(mifBaseType.getTypeName() + "." + typename);
                GeometryType geomType = new DefaultGeometryType(tName, bind, mifCRS, true, false, null, null, null);
                final GeometryDescriptor geomDesc = new DefaultGeometryDescriptor(geomType, tName, 1, 1, true, null);
                final DefaultSimpleFeatureType fType =
                        new DefaultSimpleFeatureType(ftName, null, geomDesc, false, null, mifBaseType, null);
                mifChildTypes.add(fType);
            }
        }
    }


    public void addSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        if (!(featureType instanceof SimpleFeatureType)) {
            throw new DataStoreException("The given feature type can't be added : MIF format only handle simple features.");
        }

        getTypeNames();

        if (names.contains(typeName)) {
            throw new DataStoreException("A feature type already exists with the name " + typeName);
        } else if (mifBaseType.equals(featureType) || mifChildTypes.contains(featureType)) {
            throw new DataStoreException("The given feature type already exists : " + typeName);
        }

        mifChildTypes.add((SimpleFeatureType) featureType);
    }

    public void deleteSchema(Name typeName) throws DataStoreException {
        getTypeNames();

        if (names.contains(typeName)) {
            if (mifBaseType.getName().equals(typeName)) {
                mifBaseType = null;
            } else {
                for (int i = 0 ; i < mifChildTypes.size() ; i++) {
                    if(mifChildTypes.get(i).getName().equals(typeName)) {
                        mifChildTypes.remove(i);
                        break;
                    }
                }
            }
        } else {
            throw new DataStoreException("Unable to delete the feature type named " + typeName.getLocalPart() + "because it does not exists in this data store.");
        }
    }

    public SimpleFeatureType getType(Name typeName) throws DataStoreException {
        getTypeNames();

        if(mifBaseType.getName().equals(typeName)) {
            return mifBaseType;
        }

        if(names.contains(typeName)) {
            for(SimpleFeatureType t : mifChildTypes) {
                if(t.getName().equals(typeName)) {
                    return t;
                }
            }
        }
        throw new DataStoreException("No type matching the given name have been found.");
    }

    public SimpleFeatureType getBaseType() throws DataStoreException {
        if(mifBaseType == null) {
            parseHeader();
        }
        return mifBaseType;
    }
}
