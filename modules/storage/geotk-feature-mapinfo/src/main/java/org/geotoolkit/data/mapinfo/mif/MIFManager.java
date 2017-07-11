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
package org.geotoolkit.data.mapinfo.mif;

import org.geotoolkit.nio.IOUtilities;
import org.opengis.util.GenericName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.util.NamesExt;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.apache.sis.util.logging.Logging;

import org.apache.sis.util.Utilities;
import static java.nio.file.StandardOpenOption.*;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.IllegalNameException;
import org.geotoolkit.internal.data.GenericNameIndex;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.PropertyType;
import org.opengis.util.FactoryException;

/**
 * Read types of .mif file, and manage readers / writers (from mif/mid mapinfo exchange format).
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 20/02/13
 */
public class MIFManager {

    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.mapinfo.mif");

    /**
     * A pattern frequently used to find MIF categories (for words without digit).
     */
    public static final Pattern ALPHA_PATTERN = Pattern.compile("[\\p{L}_][\\p{javaLetterOrDigit}_]*");

    /** To manage accesses to file. */
    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();

    /**
     * Mif file access
     */
    private String mifName;
    private final URI mifPath;
    private Scanner mifScanner;

    /** Path to the MID file. */
    private URI midPath;

    /**
     * Header tag values. See {@link MIFHeaderCategory} for tag description.
     */
    private short mifVersion = 300;
    private String mifCharset = "Neutral";
    public char mifDelimiter = '\t';
    private final ArrayList<Short> mifUnique = new ArrayList<>();
    private final ArrayList<Short> mifIndex = new ArrayList<>();
    private CoordinateReferenceSystem mifCRS = CommonCRS.WGS84.normalizedGeographic();
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
    private GenericNameIndex<GenericName> names = null;
    private FeatureType mifBaseType = null;
    private final ArrayList<FeatureType> mifChildTypes = new ArrayList<>();


    public MIFManager(File mifFile) throws NullArgumentException, DataStoreException, IOException, URISyntaxException {
        ArgumentChecks.ensureNonNull("Input file", mifFile);
        mifPath = mifFile.toURI();
        init();
    }

    public MIFManager(URI mifFilePath) throws NullArgumentException, DataStoreException, IOException, URISyntaxException {
        ArgumentChecks.ensureNonNull("Input file path", mifFilePath);
        mifPath = mifFilePath;
        init();
    }

    /**
     * Basic operations needed in both constructors.
     */
    private void init() throws DataStoreException, IOException, URISyntaxException {
        final String mifStr = mifPath.getPath();
        int lastSeparatorIndex = mifStr.lastIndexOf(System.getProperty("file.separator"));
        mifName = mifStr.substring(lastSeparatorIndex+1);
        if (mifName.toLowerCase().endsWith(".mif")) {
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
     *
     * If the scanner did not already read them, we catch them all by parsing the file with {@link MIFManager#buildDataTypes()}.
     *
     * @return a list ({@link HashSet}) of available feature types in that document.
     * @throws DataStoreException if we get a problem parsing the file.
     */
    public Set<GenericName> getTypeNames() throws DataStoreException {
        if (names == null) {
            names = new GenericNameIndex<>();
            checkDataTypes();
        }

        for (FeatureType t : mifChildTypes) {
            if(!names.getNames().contains(t.getName())) {
                names.add(null, t.getName(),t.getName());
            }
        }

        if(names.getNames().isEmpty()) {
            if(mifBaseType!=null) {
                names.add(null, mifBaseType.getName(),mifBaseType.getName());
            }/* else {
                throw new DataStoreException("No valid type can be found into this feature store.");
            }*/
        }

        return names.getNames();
    }


    /**
     * Try to add a new Feature type to the current store.
     * @param typeName The name of the type to add.
     * @param toAdd The type to add.
     * @throws DataStoreException If an unexpected error occurs while referencing given type.
     * @throws URISyntaxException If the URL specified at store creation is invalid.
     */
    public void addSchema(GenericName typeName, FeatureType toAdd) throws DataStoreException, URISyntaxException, IOException {
        ArgumentChecks.ensureNonNull("New feature type", toAdd);

        /*
         * We'll try to get the available types from datastore. If an exception raises while this operation, the source
         * file is invalid, so we try to delete it before going on.
         */
        try {
            getTypeNames();
        } catch (Exception e) {
            // Try to clear files before rewriting in it.
            try {
                Path mifPathObj = IOUtilities.toPath(mifPath);
                Path midPathObj = IOUtilities.toPath(midPath);

                Files.deleteIfExists(mifPathObj);
                Files.deleteIfExists(midPathObj);
            } catch (IOException e1) {
                throw new DataStoreException("Unable to erase MIF and MID files.", e1);
            }
            refreshMetaModel();
        }

        if(!toAdd.isSimple()){
            throw new DataStoreException("Only Simple Features, or features with a Simple Feature as parent can be added.");
        }

        //We check for the crs first
        checkTypeCRS(toAdd);

        boolean isBaseType = false;
        // If we're on a new store, we must set the base type and write the header. If the source type is non-geometric,
        // we save it as our base type. Otherwise, we set it's super type as base type, and if there's not, we set it as
        // base type, but we extract geometry first.
        if (mifBaseType == null) {
            final IdentifiedType geom = MIFUtils.findGeometryProperty(toAdd);
            if (geom == null) {
                mifBaseType = toAdd;
                isBaseType = true;

            } else if (!toAdd.getSuperTypes().isEmpty()) {
                mifBaseType = (FeatureType) toAdd.getSuperTypes().iterator().next();
                checkTypeCRS(toAdd);

            } else {
                final FeatureTypeBuilder builder = new FeatureTypeBuilder(toAdd);
                builder.getProperty(geom.getName().toString()).remove();
                builder.setName(mifName+".baseType");

                mifBaseType = builder.build();
            }
            mifColumnsCount = mifBaseType.getProperties(true).size();

            flushHeader();
        }

       // If the given type has not been added as is as base type, we try to put it into our childTypes.
       if(!isBaseType) {
           FeatureType childType = toAdd;
           if(!toAdd.getSuperTypes().contains(mifBaseType)) {
               FeatureTypeBuilder builder = new FeatureTypeBuilder(toAdd);
               builder.setSuperTypes(mifBaseType);
               childType = builder.build();
           }
            if (MIFUtils.identifyFeature(childType) != null) {
                mifChildTypes.add(childType);
            } else {
                throw new DataStoreException("The geometry for the given type is not supported for MIF geometry");
            }
        }

    }

    private void checkTypeCRS(FeatureType toCheck) throws DataStoreException {
        CoordinateReferenceSystem crs = FeatureExt.getCRS(toCheck);
        if (crs == null) return;

        if (!crsSet) {
            mifCRS = crs;
            crsSet = true;
            /**
             * We check if mif conversion will modify the defined CRS. If it is the case, we store the modified CRS.
             * This CRS will serve us as file writing, as we will have to reproject our features to fit the final system.
             */
            if (!Utilities.equalsIgnoreMetadata(mifCRS, CommonCRS.WGS84.normalizedGeographic())) {
                try {
                    final String mifCRSDefinition = ProjectionUtils.crsToMIFSyntax(mifCRS);
                    if (mifCRSDefinition != null && !mifCRSDefinition.isEmpty()) {
                        writtenCRS = ProjectionUtils.buildCRSFromMIF(mifCRSDefinition);
                        if (Utilities.equalsIgnoreMetadata(mifCRS, writtenCRS)) {
                            writtenCRS = null;
                        }
                    }
                } catch (Exception e) {
                    // Nothing to do here, if a CRS incompatibility has been raised, it will be well raise at MIF file flushing.
                }
            }
        } else if (!mifCRS.equals(crs)) {
            throw new DataStoreException("Given type CRS is not compatible with the one previously specified." +
                    "\nExpected : " + mifCRS + "\nFound : " + crs);
        }
    }


    public void deleteSchema(String typeName) throws DataStoreException {
        getTypeNames();

        final GenericName fullName = names.get(null, typeName);
        if (fullName!=null) {
            if (mifBaseType.getName().equals(fullName)) {
                mifBaseType = null;
            } else {
                for (int i = 0 ; i < mifChildTypes.size() ; i++) {
                    if(mifChildTypes.get(i).getName().equals(fullName)) {
                        mifChildTypes.remove(i);
                        break;
                    }
                }
            }
        } else {
            throw new DataStoreException("Unable to delete the feature type named " + typeName + "because it does not exists in this data store.");
        }
    }


    public FeatureType getType(String typeName) throws DataStoreException {
        getTypeNames();

        final GenericName fullName = names.get(null, typeName);
        if(mifBaseType.getName().equals(fullName)) {
            return mifBaseType;
        }

        if(names.getNames().contains(fullName)) {
            for(FeatureType t : mifChildTypes) {
                if(t.getName().equals(fullName)) {
                    return t;
                }
            }
        }
        throw new DataStoreException("No type matching the given name have been found.");
    }


    public FeatureType getBaseType() throws DataStoreException {
        if(mifBaseType == null && mifColumnsCount <0) {
            parseHeader();
        }
        return mifBaseType;
    }

    public URI getMIFPath() {
        return mifPath;
    }

    public URI getMIDPath() {
        return midPath;
    }

    /**
     * Initialize the path to MID file. It can set midPath if, and ONLY if the mif header have been successfully parsed.
     * If column count is 0, MID file won't contain any content, so we don't care about it.
     *
     * @throws DataStoreException if the MIF path is malformed (because we use it to build MID path), or if there's no
     * valid file at built location.
     */
    private void buildMIDPath() throws DataStoreException, IOException, URISyntaxException {

        // We try to retrieve the mid path using files, so we can use filter which don't care about case.
        if (IOUtilities.canProcessAsPath(mifPath)) {
            final Path mif = IOUtilities.toPath(mifPath);
            final String mifName = mif.getFileName().toString();

            final String midCandidate = mifName.replaceFirst("\\.(?i)mif$", "") + "\\.mid";
            if (Files.exists(mif)) {

                DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
                    @Override
                    public boolean accept(Path entry) throws IOException {
                        Pattern pattern = Pattern.compile(midCandidate, Pattern.CASE_INSENSITIVE);
                        Matcher match = pattern.matcher(entry.getFileName().toString());
                        return match.matches() && Files.isRegularFile(entry);
                    }
                };

                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(mif.getParent(), filter)) {
                    Iterator<Path> iterator = dirStream.iterator();
                    if (iterator.hasNext()) {
                        midPath = iterator.next().toUri();
                    }
                }
            }

            if (midPath == null) {
                midPath = IOUtilities.changeExtension(mif, "mid").toUri();
            }

        } else {
            final String mifStr = mifPath.getPath();
            String midStr = null;
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
            midPath = URI.create(midStr);
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
        InputStream mifStream = null;
        try {
            if (mifScanner != null) {
                mifScanner.close();
            }

            RWLock.readLock().lock();
            mifStream = IOUtilities.open(mifPath);
            mifScanner = new Scanner(mifStream, getCharset().name());

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
                    final String tmpCharset = mifScanner.findInLine(ALPHA_PATTERN);
                    if (tmpCharset != null && !tmpCharset.equalsIgnoreCase(mifCharset)) {
                        mifCharset = tmpCharset;
                        parseHeader();
                        return;
                    }

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
                        mifIndex.add(mifScanner.nextShort());
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
     * @throws DataStoreException If we cannot read header to build data types.
     */
    public void checkDataTypes() throws DataStoreException {
        if (mifBaseType == null && mifColumnsCount < 0) {
            parseHeader();
        }

        if (mifChildTypes.isEmpty() && mifScanner!=null) {
            try {
                mifScanner.close();
                RWLock.readLock().lock();
                mifScanner = new Scanner(IOUtilities.open(mifPath), getCharset().name());
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

        ArrayList<String> triggeredTypes = new ArrayList<>();
        while (mifScanner.hasNextLine()) {
            final String typename = mifScanner.findInLine(ALPHA_PATTERN);
            if (typename != null) {
                if (triggeredTypes.contains(typename)) {
                    continue;
                }
                triggeredTypes.add(typename);
                final FeatureType bind = MIFUtils.getGeometryType(typename, mifCRS, mifBaseType);
                if (bind != null) {
                    FeatureTypeBuilder builder = new FeatureTypeBuilder(bind);
                    builder.setName(NamesExt.getNamespace(bind.getName()), mifBaseType.getName().tip() + "_" + bind.getName().tip());
                    mifChildTypes.add(builder.build());
                }
            }
            mifScanner.nextLine();
        }
    }

    /**
     * Parse Column section of MIF file header.
     */
    private void parseColumns() throws DataStoreException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(mifName);

        // Check the attributes
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
            ftb.addAttribute(binding).setName(attName);
        }
        mifBaseType = ftb.build();
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
            File mifFile = new File(mifPath);
            File midFile = new File(midPath);

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
     * @return MIF formatted header.
     * @throws DataStoreException If the current FeatureType is not fully compliant with MIF constraints. If there's a
     * problem while writing the featureType in MIF header.
     */
    public String buildHeader() throws DataStoreException {

        final FeatureType toWorkWith = mifBaseType;
        int tmpCount = toWorkWith.getProperties(true).size();
        final StringBuilder headBuilder = new StringBuilder();
        try {
            headBuilder.append(MIFUtils.HeaderCategory.VERSION).append(' ').append(mifVersion).append('\n');
            headBuilder.append(MIFUtils.HeaderCategory.CHARSET).append(' ').append(mifCharset).append('\n');
            headBuilder.append(MIFUtils.HeaderCategory.DELIMITER).append(' ').append('\"').append(mifDelimiter).append('\"').append('\n');

            if (mifCRS != null && mifCRS != CommonCRS.WGS84.normalizedGeographic()) {
                String strCRS = ProjectionUtils.crsToMIFSyntax(mifCRS);
                if (!strCRS.isEmpty()) {
                    headBuilder.append(strCRS).append('\n');
                } else {
                    throw new DataStoreException("Given CRS can't be written in MIF file.");
                }
            }

            // Check the number of attributes, as the fact we've got at most one geometry.
            boolean geometryFound = false;
            for (PropertyType desc : toWorkWith.getProperties(true)) {
                if (AttributeConvention.isGeometryAttribute(desc)) {
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

        } catch (FactoryException e) {
            throw new DataStoreException("Datastore can't write MIF file header.", e);
        }

        // Header successfully built, we can report featureType values on datastore attributes.
        mifColumnsCount = tmpCount;
        mifBaseType = toWorkWith;

        return headBuilder.toString();
    }


    private void flushHeader() throws DataStoreException, IOException {
        // Cache the header in memory.
        final String head = buildHeader();

        RWLock.writeLock().lock();
        try (OutputStream out = IOUtilities.openWrite(mifPath, CREATE, WRITE, TRUNCATE_EXISTING);
                final OutputStreamWriter stream = new OutputStreamWriter(out, getCharset())) {
            // writing MIF header and geometries.
            stream.write(head);
        } finally {
            RWLock.writeLock().unlock();
        }
    }


    /**
     * When opening MIF file in writing mode, we write all data in tmp file. This function is used for writing tmp file
     * data into the real file.
     */
    public void flushData(final Path mifToFlush, final Path midToFlush) throws IOException {
        RWLock.writeLock().lock();
        try {
            // writing MIF header and geometries.
            try (final OutputStream out = IOUtilities.openWrite(mifPath, CREATE, WRITE, APPEND)) {
                // writing MIF header and geometries.
                Files.copy(mifToFlush, out);
            }

            // MID writing
            try (final OutputStream out = IOUtilities.openWrite(midPath, CREATE, WRITE, APPEND)) {
                Files.copy(midToFlush, out);
            }

        } finally {
            RWLock.writeLock().unlock();
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


        if(mifBaseType.isAssignableFrom(fType)) {
            int i=0;
            for(PropertyType pt : mifBaseType.getProperties(true)){
                if(AttributeConvention.contains(pt.getName())) continue;
                if(i!=0) builder.append(mifDelimiter);
                builder.append(MIFUtils.getStringValue(toParse.getPropertyValue(pt.getName().toString())));
                i++;
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void refreshMetaModel() throws IllegalNameException {
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

    public Charset getCharset() {
        try {
            return Charset.forName(mifCharset);
        } catch (NullPointerException | IllegalArgumentException e) {
            return StandardCharsets.ISO_8859_1;
        }
    }
}
