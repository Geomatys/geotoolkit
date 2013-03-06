package org.geotoolkit.data.mif;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Read types of .mif file, and manage readers / writers (from mif/mid mapinfo exchange format).
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 20/02/13
 */
public class MIFManager {

    private static final Logger LOGGER = Logging.getLogger(MIFManager.class.getName());

    /**
     * The number of mandatory tags to match in the header.
     */
    private static final int HEADER_MANDATORY_COUNT = 2;

    /**
     * A pattern frequently used to find MIF categories
     */
    public static final Pattern alphaPattern = Pattern.compile("\\w+");

    /** To manage accesses to file. */
    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();

    /**
     * Mif file access
     */
    private String mifName;
    private String mifPath;
    private Scanner mifScanner;

    /** Path to the MID file. */
    private String midPath;

    /**
     * Header tag values. See {@link MIFHeaderCategory} for tag description.
     */
    private short mifVersion = 300;
    private String mifCharset = "UTF-8";
    public char mifDelimiter = '\t';
    private ArrayList<Short> mifUnique = new ArrayList<Short>();
    private ArrayList<Short> mifIndex = new ArrayList<Short>();
    private CoordinateReferenceSystem mifCRS = null;
    private MathTransform mifTransform = null;
    private short mifColumnsCount = -1;

    /**
     * Type and data containers
     */
    private Set<Name> names = null;
    private DefaultSimpleFeatureType mifBaseType = null;
    private ArrayList<FeatureType> mifChildTypes = null;


    public MIFManager(File mifFile) throws NullArgumentException, DataStoreException {
        ArgumentChecks.ensureNonNull("Input file", mifFile);
        mifPath = mifFile.getAbsolutePath();
        init();
    }

    public MIFManager(String mifFilePath) throws NullArgumentException, DataStoreException {
        ArgumentChecks.ensureNonNull("Input file path", mifFilePath);
        mifPath = mifFilePath;
        init();
    }

    /**
     * Basic operations needed in both constructors.
     */
    private void init() throws DataStoreException {
        int lastSeparatorIndex = mifPath.lastIndexOf(System.getProperty("file.separator"));
        mifName = mifPath.substring(lastSeparatorIndex);
        if (mifName.endsWith(".mif") || mifName.endsWith(".MIF")) {
            mifName = mifName.substring(0, mifName.length() - 4);
        }
        mifCRS = DefaultGeographicCRS.WGS84;
        buildMIDPath();
    }


    public CoordinateReferenceSystem getMifCRS() {
        return mifCRS;
    }

    public MathTransform getTransform() {
        return mifTransform;
    }


    /**
     * Return the different type names specified by this document.
     * <p/>
     * If the scanner did not already read them, we catch them all by parsing the file with {@link MIFManager#buildDataTypes()}.
     *
     * @return a list ({@link HashSet}) of available feature types in that document.
     * @throws DataStoreException if we get a problem parsing the file.
     */
    public Set<Name> getTypeNames() throws DataStoreException {
        if (names == null) {
            Set<Name> names = new HashSet<Name>();

            checkDataTypes();
            names.add(mifBaseType.getName());
            for (FeatureType t : mifChildTypes) {
                names.add(t.getName());
            }
        }
        return names;
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

        mifChildTypes.add(featureType);
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

    public FeatureType getType(Name typeName) throws DataStoreException {
        getTypeNames();

        if(mifBaseType.getName().equals(typeName)) {
            return mifBaseType;
        }

        if(names.contains(typeName)) {
            for(FeatureType t : mifChildTypes) {
                if(t.getName().equals(typeName)) {
                    return t;
                }
            }
        }
        throw new DataStoreException("No type matching the given name have been found.");
    }

    public SimpleFeatureType getBaseType() throws DataStoreException {
        if(mifBaseType == null && mifColumnsCount <0) {
            parseHeader();
        }
        return mifBaseType;
    }

    public String getMIFPath() {
        return mifPath;
    }

    public String getMIDPath() {
        return midPath;
    }

    /**
     * Initialize the path to MID file. It can set midPath if, and ONLY if the mif header have been successfully parsed.
     * If column count is 0, MID file won't contain any content, so we don't care about it.
     *
     * @throws DataStoreException if the MIF path is malformed (because we use it to build MID path), or if there's no
     * valid file at built location.
     */
    private void buildMIDPath() throws DataStoreException {

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

    }


    /**********************************************
     *          Methods with file access
     **********************************************/

    /**
     * Read .MIF file header and get needed information for data reading.
     *
     * @throws DataStoreException If all mandatory data can't be read.
     */
    private void parseHeader() throws DataStoreException {
        //Reset the file scanner to ensure we'll start on file top position.
        try {
            if (mifScanner != null) {
                mifScanner.close();
                // Should we try to unlock the file at the same time we close scanner ?
                //RWLock.readLock().unlock();
            }

            RWLock.readLock().lock();
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
                        allRequiredDone++;
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
                    double xResample, yResample, xTranslate, yTranslate;
                    xResample = mifScanner.nextDouble();
                    yResample = mifScanner.nextDouble();
                    xTranslate = mifScanner.nextDouble();
                    yTranslate = mifScanner.nextDouble();
                    mifTransform = new AffineTransform2D(xResample, 0, 0, yResample, xTranslate, yTranslate);
                    //Build the parent feature type for data contained in this MIF.
                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.COLUMNS.name())) {
                    if (mifScanner.hasNextShort()) {
                        mifColumnsCount = mifScanner.nextShort();
                    } else {
                        throw new DataStoreException("MIF Columns has no attribute count specified.");
                    }
                    // If there's no defined column, there will not be any base type, only pure geometry features.
                    if (mifColumnsCount > 0) {
                        parseColumns();
                    }
                    allRequiredDone++;
                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.DATA.name())) {
                    if (allRequiredDone < HEADER_MANDATORY_COUNT) {
                        throw new DataStoreException("File header can't be read (Mandatory marks are missing)");
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataStoreException("MIF file header can't be read.", e);
        } finally {
            RWLock.readLock().unlock();
        }
    }

    /**
     * Ensure that dataTypes are built. If not, call {@link MIFManager#buildDataTypes()}.
     */
    public void checkDataTypes() throws DataStoreException {
        if (mifBaseType == null && mifColumnsCount < 0) {
            parseHeader();
        } else {
            if (mifChildTypes == null) {
                try {
                    mifScanner.close();
                    RWLock.readLock().lock();
                    mifScanner = new Scanner(mifPath);
                    buildDataTypes();
                } catch (Exception e) {
                    throw new DataStoreException("Reading types from MIF file failed.", e);
                } finally {
                    mifScanner.close();
                    RWLock.readLock().unlock();
                }
            }
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
        mifChildTypes = new ArrayList<FeatureType>();

        ArrayList<String> triggeredTypes = new ArrayList<String>();
        while (mifScanner.hasNextLine()) {
            final String typename = mifScanner.findInLine(alphaPattern);
            if (triggeredTypes.contains(typename)) {
                continue;
            }
            final FeatureType bind = MIFUtils.getGeometryType(typename, mifCRS, mifBaseType);
            if (bind != null) {
                mifChildTypes.add(bind);
            }
        }
    }

    /**
     * Parse Column section of MIF file header.
     */
    private void parseColumns() throws DataStoreException {

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
    }

}
