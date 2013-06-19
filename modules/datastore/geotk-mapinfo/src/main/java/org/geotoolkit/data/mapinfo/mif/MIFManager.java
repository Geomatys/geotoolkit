package org.geotoolkit.data.mapinfo.mif;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Read types of .mif file, and manage readers / writers (from mif/mid mapinfo exchange format).
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 20/02/13
 */
public class MIFManager {

    public static final Logger LOGGER = Logging.getLogger(MIFManager.class.getName());

    /**
     * A pattern frequently used to find MIF categories (for words without digit).
     */
    public static final Pattern ALPHA_PATTERN = Pattern.compile("[a-zA-Z_]\\w*");

    /** To manage accesses to file. */
    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();

    /**
     * Mif file access
     */
    private String mifName;
    private URL mifPath;
    private Scanner mifScanner;

    /** Path to the MID file. */
    private URL midPath;

    /**
     * Header tag values. See {@link MIFHeaderCategory} for tag description.
     */
    private short mifVersion = 300;
    private String mifCharset = "Neutral";
    public char mifDelimiter = '\t';
    private ArrayList<Short> mifUnique = new ArrayList<Short>();
    private ArrayList<Short> mifIndex = new ArrayList<Short>();
    private CoordinateReferenceSystem mifCRS = DefaultGeographicCRS.WGS84;
    private MathTransform mifTransform = null;
    private int mifColumnsCount = -1;

    /**
     * The mif crs as it will be defined in final MIF file. We need it because it could be some differences between the
     * CRS of features added by user, and the one that will be written (Ex : written crs first axis have to be east).
     */
    private CoordinateReferenceSystem writtenCRS = null;

    /**
     * All geometries in a MIF file must get the same CRS. This trigger will serve to know if user add multiple
     * geometries with different CRS,
     */
    private boolean crsSet = false;

    /**
     * Type and data containers
     */
    private Set<Name> names = null;
    private SimpleFeatureType mifBaseType = null;
    private ArrayList<FeatureType> mifChildTypes = new ArrayList<FeatureType>();


    public MIFManager(File mifFile) throws NullArgumentException, DataStoreException, MalformedURLException, URISyntaxException {
        ArgumentChecks.ensureNonNull("Input file", mifFile);
        mifPath = mifFile.toURI().toURL();
        init();
    }

    public MIFManager(URL mifFilePath) throws NullArgumentException, DataStoreException, MalformedURLException, URISyntaxException {
        ArgumentChecks.ensureNonNull("Input file path", mifFilePath);
        mifPath = mifFilePath;
        init();
    }

    /**
     * Basic operations needed in both constructors.
     */
    private void init() throws DataStoreException, MalformedURLException, URISyntaxException {
        final String mifStr = mifPath.getPath();
        int lastSeparatorIndex = mifStr.lastIndexOf(System.getProperty("file.separator"));
        mifName = mifStr.substring(lastSeparatorIndex+1);
        if (mifName.endsWith(".mif") || mifName.endsWith(".MIF")) {
            mifName = mifName.substring(0, mifName.length() - 4);
        }
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
            names = new HashSet<Name>();
            checkDataTypes();
        }

        for (FeatureType t : mifChildTypes) {
            if(!names.contains(t.getName())) {
                names.add(t.getName());
            }
        }

        if(names.isEmpty()) {
            if(mifBaseType!=null) {
                names.add(mifBaseType.getName());
            } else {
                throw new DataStoreException("No valid type can be found into this feature store.");
            }
        }

        return names;
    }


    /**
     * Try to add a new Feature type to the current store.
     * @param typeName The name of the type to add.
     * @param toAdd The type to add.
     * @throws DataStoreException If an unexpected error occurs while referencing given type.
     * @throws URISyntaxException If the URL specified at store creation is invalid.
     */
    public void addSchema(Name typeName, FeatureType toAdd) throws DataStoreException, URISyntaxException {
        ArgumentChecks.ensureNonNull("New feature type", toAdd);

        /*
         * We'll try to get the available types from datastore. If an exception raises while this operation, the source
         * file is invalid, so we try to delete it before going on.
         */
        try {
            getTypeNames();
        } catch (Exception e) {
            // Try to clear files before rewriting in it.
            if (MIFUtils.isLocal(mifPath)) {
                File f = new File(mifPath.toURI());
                if (f.exists()) {
                    boolean deleted = f.delete();
                    if (!deleted) {
                        throw new DataStoreException("MIF data already exists and can't be erased.");
                    }
                }
            }

            if (MIFUtils.isLocal(midPath)) {
                File f = new File(midPath.toURI());
                if (f.exists()) {
                    boolean deleted = f.delete();
                    if (!deleted) {
                        throw new DataStoreException("MID data already exists and can't be erased.");
                    }
                }
            }

            refreshMetaModel();
        }

        if (!(toAdd instanceof SimpleFeatureType)
                && (toAdd.getSuper() == null || !(toAdd.getSuper() instanceof SimpleFeatureType))) {

            throw new DataStoreException("Only Simple Features, or features with a Simple Feature as parent can be added.");
        }

        //We check for the crs first
        checkTypeCRS(toAdd);

        boolean isBaseType = false;
        // If we're on a new store, we must set the base type and write the header. If the source type is non-geometric,
        // we save it as our base type. Otherwise, we set it's super type as base type, and if there's not, we set it as
        // base type, but we extract geometry first.
        if (mifBaseType == null) {
            if (toAdd.getGeometryDescriptor() == null && toAdd instanceof SimpleFeatureType) {
                mifBaseType = (SimpleFeatureType) toAdd;
                isBaseType = true;

            } else if (toAdd.getSuper() != null && toAdd.getSuper() instanceof SimpleFeatureType && ((SimpleFeatureType) toAdd.getSuper()).getAttributeCount()>0) {
                mifBaseType = (SimpleFeatureType) toAdd.getSuper();
                checkTypeCRS(toAdd);

            } else {
                Collection<PropertyDescriptor> properties = toAdd.getDescriptors();

                FeatureTypeBuilder builder = new FeatureTypeBuilder();
                builder.setName(mifName+".baseType");
                builder.addAll(properties);
                builder.remove(toAdd.getGeometryDescriptor().getLocalName());

                mifBaseType = builder.buildSimpleFeatureType();
            }
            mifColumnsCount = mifBaseType.getAttributeCount();

            flushHeader();
        }

        // If the given type has not been added as is as base type, we try to put it into our childTypes.
       if(!isBaseType) {
            if (MIFUtils.identifyFeature(toAdd) != null) {
                mifChildTypes.add(toAdd);
            } else {
                throw new DataStoreException("The geometry for the given type is not supported for MIF geometry");
            }
        }

    }


    private void checkTypeCRS(FeatureType toCheck) throws DataStoreException {

        if (toCheck.getCoordinateReferenceSystem() == null) return;

        if (!crsSet) {
            mifCRS = toCheck.getCoordinateReferenceSystem();
            crsSet = true;
            /**
             * We check if mif conversion will modify the defined CRS. If it is the case, we store the modified CRS.
             * This CRS will serve us as file writing, as we will have to reproject our features to fit the final system.
             */
            if (!CRS.equalsIgnoreMetadata(mifCRS, DefaultGeographicCRS.WGS84)) {
                try {
                    final String mifCRSDefinition = ProjectionUtils.crsToMIFSyntax(mifCRS);
                    if (mifCRSDefinition != null && !mifCRSDefinition.isEmpty()) {
                        writtenCRS = ProjectionUtils.buildCRSFromMIF(mifCRSDefinition);
                        if (CRS.equalsIgnoreMetadata(mifCRS, writtenCRS)) {
                            writtenCRS = null;
                        }
                    }
                } catch (Exception e) {
                    // Nothing to do here, if a CRS incompatibility has been raised, it will be well raise at MIF file flushing.
                }
            }
        } else if (!mifCRS.equals(toCheck.getCoordinateReferenceSystem())) {
            throw new DataStoreException("Given type CRS is not compatible with the one previously specified." +
                    "\nExpected : " + mifCRS + "\nFound : " + toCheck.getCoordinateReferenceSystem());
        }
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

    public URL getMIFPath() {
        return mifPath;
    }

    public URL getMIDPath() {
        return midPath;
    }

    /**
     * Initialize the path to MID file. It can set midPath if, and ONLY if the mif header have been successfully parsed.
     * If column count is 0, MID file won't contain any content, so we don't care about it.
     *
     * @throws DataStoreException if the MIF path is malformed (because we use it to build MID path), or if there's no
     * valid file at built location.
     */
    private void buildMIDPath() throws DataStoreException, MalformedURLException, URISyntaxException {
        String midStr = null;

        // We try to retrieve the mid path using files, so we can use filter which don't care about case.
        if (mifPath.getProtocol().contains("file")) {
            final File mif = new File(mifPath.toURI());
            final String mifName = mif.getName();

            File[] matchingFiles = null;
            if (mif.exists()) {
                matchingFiles = mif.getParentFile().listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        Pattern patoche = Pattern.compile(mifName.replaceFirst("\\.(?i)mif$", "") + "\\.mid", Pattern.CASE_INSENSITIVE);
                        Matcher match = patoche.matcher(pathname.getName());
                        return match.matches();
                    }
                });
            }

            if (matchingFiles == null || matchingFiles.length < 1) {
                midStr = mif.getAbsolutePath().replaceFirst("\\.(?i)mif$", "") + ".mid";
            } else {
                midStr = matchingFiles[0].getAbsolutePath();
            }

        } else {
            final String mifStr = mifPath.getPath();

            if (mifStr.endsWith(".mif") || mifStr.endsWith(".MIF")) {
                midStr = mifStr.substring(0, mifStr.length() - 4);
            } else {
                throw new DataStoreException("There's an extension problem with Mif file. A correct extension is needed in order to retrieve the associated mid file.");
            }

            // We have to check if the extension is upper or lower case, since unix file systems are case sensitive.
            if (mifStr.endsWith(".mif")) {
                midStr = midStr.concat(".mid");
            } else if (mifStr.endsWith(".MIF")) {
                midStr = midStr.concat(".MID");
            }
        }

        midPath = new URL(mifPath.getProtocol(), mifPath.getHost(), mifPath.getPort(), midStr);
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
        InputStream mifStream = null;
        try {
            if (mifScanner != null) {
                mifScanner.close();
                // Should we try to unlock the file at the same time we close scanner ?
                //RWLock.readLock().unlock();
            }

            RWLock.readLock().lock();
            mifStream = MIFUtils.openInConnection(mifPath);
            mifScanner = new Scanner(mifStream);

            // A trigger to tell us if all mandatory categories have been parsed.
            boolean columnsParsed = false;
            while (mifScanner.hasNextLine()) {
                final String matched = mifScanner.findInLine(ALPHA_PATTERN);

                if (matched == null && !columnsParsed) {
                    // maybe we missed a line ?
                    mifScanner.nextLine();
                    continue;
                }

                if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.VERSION.name())) {
                    if (mifScanner.hasNextShort()) {
                        mifVersion = mifScanner.nextShort();
                    } else {
                        throw new DataStoreException("MIF Version can't be read.");
                    }

                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.CHARSET.name())) {
                    final String charset = mifScanner.findInLine(ALPHA_PATTERN);

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
                    /*
                     * Don't know how many coefficients will be defined in the CRS, nor if it's written on a single
                     * line,so we iterate until the next header category clause, storing encountered data.
                     */
                    final StringBuilder crsStr = new StringBuilder();
                    boolean coordSysCase = true;
                    while(coordSysCase) {
                        crsStr.append(mifScanner.next());
                        for(MIFUtils.HeaderCategory category : MIFUtils.HeaderCategory.values()) {
                            Pattern pat = Pattern.compile(category.name(), Pattern.CASE_INSENSITIVE);
                            if(mifScanner.hasNext(pat)) {
                                coordSysCase = false;
                                break;
                            }
                        }
                    }

                    final CoordinateReferenceSystem crs = ProjectionUtils.buildCRSFromMIF(crsStr.toString());
                    if(crs != null) {
                        mifCRS = crs;
                    }
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
                    columnsParsed = true;
                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.DATA.name())) {
                    if (!columnsParsed) {
                        throw new DataStoreException("File header can't be read (Columns mark is missing)");
                    } else {
                        break;
                    }
                }
                mifScanner.nextLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "File header can't be read (creation mode ?).");
        } catch (Exception e) {
            throw new DataStoreException("MIF file header can't be read.", e);
        } finally {
            if(mifStream != null) {
                try {
                    mifStream.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Input connection to MIF data can't be closed.", e);
                }
            }
            RWLock.readLock().unlock();
        }
    }

    /**
     * Ensure that dataTypes are built. If not, call {@link MIFManager#buildDataTypes()}.
     */
    public void checkDataTypes() throws DataStoreException {
        if (mifBaseType == null && mifColumnsCount < 0) {
            parseHeader();
        }

        if (mifChildTypes.isEmpty() && mifScanner!=null) {
            try {
                mifScanner.close();
                RWLock.readLock().lock();
                mifScanner = new Scanner(MIFUtils.openInConnection(mifPath));
                buildDataTypes();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Reading types from MIF file failed.", e);
            } finally {
                mifScanner.close();
                RWLock.readLock().unlock();
            }
        }
    }

    /**
     * Browse the MIF file to get the geometry types it contained. With it, we create a new feature type
     * for each geometry type found. They'll all get the base feature type (defining MID attributes) as parent.
     * <p/>
     * IMPORTANT :  we'll browse the file only for geometry TYPES, so no other data is read.
     * <p/>
     * MORE IMPORTANT : This method does not manage scanner start position, we assume that caller have prepared it
     * itself (to avoid close / re-open each time).
     */
    private void buildDataTypes() {
        mifChildTypes.clear();

        ArrayList<String> triggeredTypes = new ArrayList<String>();
        while (mifScanner.hasNextLine()) {
            final String typename = mifScanner.findInLine(ALPHA_PATTERN);
            if (typename != null) {
                if (triggeredTypes.contains(typename)) {
                    continue;
                }
                triggeredTypes.add(typename);
                final FeatureType bind = MIFUtils.getGeometryType(typename, mifCRS, mifBaseType);
                if (bind != null) {
                    mifChildTypes.add(bind);
                }
            }
            mifScanner.nextLine();
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
            final String attName = mifScanner.findInLine(ALPHA_PATTERN);
            final String tmpType = mifScanner.findInLine(ALPHA_PATTERN);
            // Since scanner doesn't move if no matching pattern is found, we can test only the second string.
            if (tmpType == null) {
                throw new DataStoreException("A problem occured while reading columns tag from .MIF header.");
            }
            final Class binding = MIFUtils.getColumnJavaType(tmpType);
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

    /**
     * delete the MIF/MID files currently pointed by this manager.
     *
     * @return true if the files have successfully been deleted, false otherwise.
     */
    private boolean delete() throws DataStoreException {
        int deleteCounter = 0;

        RWLock.writeLock().lock();
        try {
            File mifFile = new File(mifPath.toURI());
            File midFile = new File(midPath.toURI());

            if (mifFile.exists()) {
                if (mifFile.delete()) {
                    deleteCounter++;
                }
            } else {
                deleteCounter++;
            }

            if (midFile.exists()) {
                if (midFile.delete()) {
                    deleteCounter++;
                }
            } else {
                deleteCounter++;
            }

        } catch (Exception ex) {
            throw new DataStoreException("MIF/MID data files can't be removed.", ex);
        } finally {
            RWLock.writeLock().unlock();
        }

        return (deleteCounter > 1);
    }

    /**
     * Write the MIF file header(Version, MID columns and other stuff).
     *
     * @throws DataStoreException If the current FeatureType is not fully compliant with MIF constraints. If there's a
     * problem while writing the featureType in MIF header.
     */
    public String buildHeader() throws DataStoreException {
        if (!(mifBaseType instanceof SimpleFeatureType)) {
            throw new DataStoreException("Only simple schema can be written in MIF file header. Given is : \n" + mifBaseType);
        }

        final SimpleFeatureType toWorkWith = (SimpleFeatureType) mifBaseType;
        int tmpCount = toWorkWith.getAttributeCount();
        final StringBuilder headBuilder = new StringBuilder();
        try {
            headBuilder.append(MIFUtils.HeaderCategory.VERSION).append(' ').append(mifVersion).append('\n');
            headBuilder.append(MIFUtils.HeaderCategory.CHARSET).append(' ').append(mifCharset).append('\n');
            headBuilder.append(MIFUtils.HeaderCategory.DELIMITER).append(' ').append('\"').append(mifDelimiter).append('\"').append('\n');

            if (mifCRS != null && mifCRS != DefaultGeographicCRS.WGS84) {
                String strCRS = ProjectionUtils.crsToMIFSyntax(mifCRS);
                if(!strCRS.isEmpty()) {
                    headBuilder.append(strCRS).append('\n');
                } else {
                    throw new DataStoreException("Given CRS can't be written in MIF file.");
                }
            }

            // Check the number of attributes, as the fact we've got at most one geometry.
            boolean geometryFound = false;
            for (AttributeDescriptor desc : toWorkWith.getAttributeDescriptors()) {
                if (desc instanceof GeometryDescriptor) {
                    if (geometryFound) {
                        throw new DataStoreException("Only mono geometry types are managed for MIF format, but given featureType get at least 2 geometry descriptor.");
                    } else {
                        tmpCount--;
                        geometryFound = true;
                    }
                }
            }
            headBuilder.append(MIFUtils.HeaderCategory.COLUMNS).append(' ').append(mifColumnsCount).append('\n');
            MIFUtils.featureTypeToMIFSyntax(toWorkWith, headBuilder);

            headBuilder.append(MIFUtils.HeaderCategory.DATA).append('\n');

        } catch (Exception e) {
            throw new DataStoreException("Datastore can't write MIF file header.", e);
        }
        // Header successfully built, we can report featureType values on datastore attributes.
        mifColumnsCount = tmpCount;
        mifBaseType = toWorkWith;

        return headBuilder.toString();
    }


    private void flushHeader() throws DataStoreException {
        // Cache the header in memory.
        final String head = buildHeader();

        // Writing pass, with datastore locking.
        OutputStreamWriter stream = null;
        InputStream mifInput = null;
        InputStream midInput = null;
        RWLock.writeLock().lock();
        try {
            // writing MIF header and geometries.
            OutputStream out = MIFUtils.openOutConnection(mifPath);
            stream = new OutputStreamWriter(out);
            stream.write(head);
        } catch (Exception e) {
            throw new DataStoreException("A problem have been encountered while flushing data.", e);
        } finally {
            RWLock.writeLock().unlock();
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Writer stream can't be closed.", e);
                }
            }
        }
    }


    /**
     * When opening MIF file in writing mode, we write all data in tmp file. This function is used for writing tmp file
     * data into the real file.
     */
    public void flushData(MIFFeatureWriter dataToWrite) throws DataStoreException {

        // Writing pass, with datastore locking.
        OutputStreamWriter stream = null;
        InputStream mifInput = null;
        InputStreamReader reader;
        InputStream midInput = null;
        RWLock.writeLock().lock();
        try {
            // writing MIF header and geometries.
            OutputStream out = MIFUtils.openOutConnection(mifPath);
            stream = new OutputStreamWriter(out);
            mifInput = dataToWrite.getMIFTempStore();
            MIFUtils.write(mifInput, stream);
            stream.close();

            // MID writing
            out = MIFUtils.openOutConnection(midPath);
            stream = new OutputStreamWriter(out);
            midInput = dataToWrite.getMIDTempStore();
            MIFUtils.write(midInput, stream);

        } catch (Exception e) {
            throw new DataStoreException("A problem have been encountered while flushing data.", e);
        } finally {
            RWLock.writeLock().unlock();
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Writer stream can't be closed.", e);
                }
            }
            if (mifInput != null) {
                try {
                    mifInput.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Temporary data store can't be closed.", e);
                }
            }
            if (midInput != null) {
                try {
                    midInput.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Temporary data store can't be closed.", e);
                }
            }
        }
    }

    /**
     * Build a string representation of the given feature attributes for MID file writing.
     * @param toParse The feature to convert into MID syntax.
     * @return A string representation of the given feature. Never null, but empty string is possible.
     */
    public String buildMIDAttributes(Feature toParse) {
         final StringBuilder builder = new StringBuilder();
        final FeatureType fType = toParse.getType();


        if(mifBaseType.equals(fType) || mifBaseType.equals(fType.getSuper())
                || fType.getDescriptors().containsAll(mifBaseType.getDescriptors())) {
            final Name name = mifBaseType.getType(0).getName();
            builder.append(MIFUtils.getStringValue(toParse.getProperty(name)));

            for(int i = 1 ; i < mifBaseType.getTypes().size() ; i++) {
                final Name propName = mifBaseType.getType(i).getName();
                builder.append(mifDelimiter).append(MIFUtils.getStringValue(toParse.getProperty(propName)));
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void refreshMetaModel() {
        names.clear();
        names = null;
        mifBaseType = null;
        mifChildTypes.clear();
        mifColumnsCount = -1;
    }

    public void setDelimiter(char delimiter) {
        this.mifDelimiter = delimiter;
    }

    public CoordinateReferenceSystem getWrittenCRS() {
        return writtenCRS;
    }
}
