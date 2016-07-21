/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
/*
 * NOTICE OF RELEASE TO THE PUBLIC DOMAIN
 *
 * This work was created by employees of the USDA Forest Service's
 * Fire Science Lab for internal use.  It is therefore ineligible for
 * copyright under title 17, section 105 of the United States Code.  You
 * may treat it as you would treat any public domain work: it may be used,
 * changed, copied, or redistributed, with or without permission of the
 * authors, for free or for compensation.  You may not claim exclusive
 * ownership of this code because it is already owned by everyone.  Use this
 * software entirely at your own risk.  No warranty of any kind is given.
 *
 * A copy of 17-USC-105 should have accompanied this distribution in the file
 * 17USC105.html.  If not, you may access the law via the US Government's
 * public websites:
 *   - http://www.copyright.gov/title17/92chap1.html#105
 *   - http://www.gpoaccess.gov/uscode/  (enter "17USC105" in the search box.)
 */

package org.geotoolkit.metadata.geotiff;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.crs.GeodeticCRS;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.util.FactoryException;

import org.apache.sis.internal.referencing.Formulas;
import org.apache.sis.internal.referencing.provider.AlbersEqualArea;
import org.apache.sis.internal.referencing.provider.Equirectangular;
import org.apache.sis.internal.referencing.provider.Mercator1SP;
import org.apache.sis.internal.referencing.provider.Mercator2SP;
import org.apache.sis.internal.referencing.provider.LambertConformal1SP;
import org.apache.sis.internal.referencing.provider.LambertConformal2SP;
import org.apache.sis.internal.referencing.provider.ObliqueStereographic;
import org.apache.sis.internal.referencing.provider.PolarStereographicA;
import org.apache.sis.internal.referencing.provider.PolarStereographicB;
import org.apache.sis.internal.referencing.provider.PolarStereographicC;
import org.apache.sis.internal.referencing.provider.TransverseMercator;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.measure.Latitude;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.apache.sis.referencing.cs.DefaultCartesianCS;
import org.apache.sis.referencing.crs.DefaultProjectedCRS;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.datum.DefaultEllipsoid;
import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.factory.GeodeticAuthorityFactory;
import org.apache.sis.util.logging.Logging;

import org.geotoolkit.referencing.factory.ReferencingFactoryContainer;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.operation.provider.Krovak;
import org.geotoolkit.referencing.operation.provider.LambertAzimuthalEqualArea;
import org.geotoolkit.referencing.operation.provider.NewZealandMapGrid;
import org.geotoolkit.referencing.operation.provider.ObliqueMercator;
import org.geotoolkit.referencing.operation.provider.Orthographic;
import org.geotoolkit.referencing.operation.provider.Stereographic;
import org.geotoolkit.resources.Vocabulary;

import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import static org.geotoolkit.metadata.geotiff.GeoTiffMetaDataReader.*;

/**
 * TODO this class must be rewritten, redundant code is used here and all geotiff
 * crs codes are not supported.
 *
 * @author Bryce Nordgren / USDA Forest Service
 * @author Simone Giannecchini
 * @author Daniele Romagnoli
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class GeoTiffCRSReader {

    /**
     * Cached {@link MathTransformFactory} for building {@link MathTransform}
     * objects.
     */
    private final static MathTransformFactory mtFactory = FactoryFinder.getMathTransformFactory(null);

    /**
     * Logger to diffuse no blocking error message.
     */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.metadata.geotiff");

    /** EPSG factories for various purposes. */
    private final GeodeticAuthorityFactory epsgFactory;

    /** EPSG Factory for creating {@link GeodeticDatum}objects. */
    private final DatumFactory datumObjFactory;
    /** CRS Factory for creating CRS objects. */
    private final CRSFactory crsFactory;

    /** Group Factory for creating {@link ProjectedCRS} objects. */
    private final ReferencingFactoryContainer factories;

    public GeoTiffCRSReader() {
        try {
            epsgFactory = (GeodeticAuthorityFactory) CRS.getAuthorityFactory("EPSG");
        } catch (FactoryException e) {
            throw new IllegalStateException(e);
        }

        // factory = new ThreadedEpsgFactory(hints);
        datumObjFactory = FactoryFinder.getDatumFactory(null);
        crsFactory = FactoryFinder.getCRSFactory(null);
        factories = ReferencingFactoryContainer.instance(null);
    }

    /**
     * Fill the CRS metadatas with the values available in the geotiff tags.
     */
    public void fillCRSMetaDatas(final SpatialMetadata metadatas, final ValueMap entries) throws IOException, FactoryException{

        final Object type = entries.get(GTModelTypeGeoKey);

        if (type == null) {
            LOGGER.log(Level.FINE, "GTModelTypeGeoKey (Tiff CRS metadatas information) is not defined in tags.");
            return;
        }

        switch( (Integer)type ){
            case ModelTypeProjected:  fillProjectedCRSMetaDatas(metadatas,entries);break;
            case ModelTypeGeographic: fillGeographicCRSMetaDatas(metadatas,entries);break;
            case ModelTypeGeocentric: fillGeocentricCRSMetaDatas(metadatas,entries);break;
            default: throw new IOException("Unexpected crs model type : "+(Integer)type);
        }
    }

    /**
     * Fill a projected CRS metadatas with the values available in the geotiff tags.
     */
    private void fillProjectedCRSMetaDatas(final SpatialMetadata metadatas, final ValueMap entries) throws IOException, FactoryException {

        final ReferencingBuilder rb = new ReferencingBuilder(metadatas);
        final CoordinateReferenceSystem crs;

        // //
        // Get the projection reference system code in case we have one by
        // lookig for the ProjectedCSTypeGeoKey key
        // //
        String tempCode = entries.getAsString(ProjectedCSTypeGeoKey);
        if (tempCode == null) {
            tempCode = "unnamed";
        }
        final StringBuffer projCode = new StringBuffer(tempCode.trim().intern());

        // //
        // getting the linear unit used by this coordinate reference system
        // since we will use it anyway.
        // //
        Unit linearUnit;
        try {
            linearUnit = createUnit(ProjLinearUnitsGeoKey,
                    ProjLinearUnitSizeGeoKey, SI.METRE,
                    SI.METRE, entries);
        } catch (IOException e) {
            linearUnit = null;
        }
        // //
        // if it's user defined, there's a lot of work to do, we have to parse
        // many information.
        // //
        if (tempCode.equalsIgnoreCase("unnamed")
                || tempCode.equals(GTUserDefinedGeoKey_String)) {
            crs = createUserDefinedPCS(entries, linearUnit);

        }else{
            // //
            // if it's not user defined, just use the EPSG factory to create the
            // coordinate system
            // //
            try {
                if (!tempCode.startsWith("EPSG") && !tempCode.startsWith("epsg")) {
                    projCode.insert(0, "EPSG:");
                }
                // it is an EPSG crs let's create it.
                //TODO : jsorel : are we sure of this ? always long/lat order ?
                final ProjectedCRS pcrs = (ProjectedCRS) AbstractCRS.castOrCopy(CRS.forCode(projCode.toString())).forConvention(AxesConvention.RIGHT_HANDED);
                // //
                // We have nothing to do with the unit of measure
                // //
                if (linearUnit == null || linearUnit.equals(pcrs.getCoordinateSystem().getAxis(0).getUnit())) {
                    crs = pcrs;
                }else{
                    // //
                    // Creating anew projected CRS
                    // //
                    crs = new DefaultProjectedCRS(
                            java.util.Collections.singletonMap("name", IdentifiedObjects.getName(pcrs, new DefaultCitation("EPSG"))),
                            (GeographicCRS) pcrs.getBaseCRS(),
                            pcrs.getConversionFromBase(),
                            createProjectedCS(linearUnit));
                }
            } catch (FactoryException fe) {
                throw new IOException(fe);
            }
        }

        rb.setCoordinateReferenceSystem(crs);
    }

    /**
     * Fill a geographic CRS metadatas with the values available in the geotiff tags.
     */
    private void fillGeographicCRSMetaDatas(final SpatialMetadata metadatas, final ValueMap entries) throws IOException, FactoryException{
        GeographicCRS gcs = null;

        // ////////////////////////////////////////////////////////////////////
        // Get the crs code
        // ////////////////////////////////////////////////////////////////////
        final String tempCode = entries.getAsString(GeographicTypeGeoKey);
        // lookup the angular units used in this geotiff image
        Unit angularUnit = null;
        try {
            angularUnit = createUnit(GeogAngularUnitsGeoKey,
                    GeogAngularUnitSizeGeoKey, SI.RADIAN,
                    NonSI.DEGREE_ANGLE, entries);
        } catch (IOException e) {
            angularUnit = null;
        }
        // linear unit
        Unit linearUnit = null;
        try {
            linearUnit = createUnit(GeogLinearUnitsGeoKey,
                    GeogLinearUnitSizeGeoKey, SI.METRE,
                    SI.METRE, entries);
        } catch (IOException e) {
            linearUnit = null;
        }
        // if it's user defined, there's a lot of work to do
        if (tempCode == null
                || tempCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            // ////////////////////////////////////////////////////////////////////
            // it is user-defined we have to parse a lot of information in order
            // to built it.
            // ////////////////////////////////////////////////////////////////////
            gcs = createUserDefinedGCS(entries, linearUnit, angularUnit);

        } else {
            try {

                // ////////////////////////////////////////////////////////////////////
                // If it's not user defined, just use the EPSG factory to create
                // the coordinate system but check if the user specified a
                // different angular unit. In this case we need to create a
                // user-defined GCRS.
                // ////////////////////////////////////////////////////////////////////
                final StringBuffer geogCode = new StringBuffer(tempCode);
                if (!tempCode.startsWith("EPSG") && !tempCode.startsWith("epsg")) {
                    geogCode.insert(0, "EPSG:");
                }
                //TODO : jsorel : are we sure of this ? always long/lat order ?
                gcs = (GeographicCRS) AbstractCRS.castOrCopy(CRS.forCode(geogCode.toString())).forConvention(AxesConvention.RIGHT_HANDED);
                if (angularUnit != null
                        && !angularUnit.equals(gcs.getCoordinateSystem().getAxis(0).getUnit())) {
                    // //
                    // Create a user-defined GCRS using the provided angular
                    // unit.
                    // //
                    gcs = new DefaultGeographicCRS(name(IdentifiedObjects.getName(gcs, new DefaultCitation("EPSG"))),
                            (GeodeticDatum) gcs.getDatum(),
                            PredefinedCS.usingUnit(CommonCRS.defaultGeographic().getCoordinateSystem(), angularUnit));
                }
            } catch (FactoryException ex) {
                throw new IOException(ex);
            }
        }

        ReferencingBuilder rb = new ReferencingBuilder(metadatas);
        rb.setCoordinateReferenceSystem(gcs);
    }

    /**
     * Fill a geocentric CRS metadatas with the values available in the geotiff tags.
     */
    private void fillGeocentricCRSMetaDatas(final SpatialMetadata metadatas, final ValueMap entries) throws IOException{
        throw new IOException("Not done yet.");
    }

    /**
     * The GeoTIFFWritingUtilities spec requires that a user defined GCS be
     * comprised of the following:
     *
     * <ul>
     * <li> a citation </li>
     * <li> a datum definition </li>
     * <li> a prime meridian definition (if not Greenwich) </li>
     * <li> an angular unit definition (if not degrees) </li>
     * </ul>
     *
     * @param metadata to use for building this {@link GeographicCRS}.
     * @param linearUnit
     * @param angularUnit
     * @return a {@link GeographicCRS}.
     *
     * @throws IOException
     */
    private GeographicCRS createUserDefinedGCS(final ValueMap metadata, final Unit linearUnit,
            final Unit angularUnit) throws IOException, FactoryException {
        // //
        // coordinate reference system name (GeogCitationGeoKey)
        // //
        String name = metadata.getAsString(GeogCitationGeoKey);
        if (name == null) {
            name = "unnamed";
        }

        // lookup the Geodetic datum
        final GeodeticDatum datum = createGeodeticDatum(linearUnit, metadata);

        // coordinate reference system
        GeographicCRS gcs = null;

        // property map is reused
        final Map props = new HashMap();
        // make the user defined GCS from all the components...
        props.put("name", name);
        return crsFactory.createGeographicCRS(props, datum,
                PredefinedCS.usingUnit(CommonCRS.defaultGeographic().getCoordinateSystem(), angularUnit));
    }

    /**
     * We have a user defined {@link ProjectedCRS}, let's try to parse it.
     *
     * @param linearUnit
     *            is the UoM that this {@link ProjectedCRS} will use. It could
     *            be null.
     *
     * @return a user-defined {@link ProjectedCRS}.
     * @throws IOException
     * @throws FactoryException
     */
    private ProjectedCRS createUserDefinedPCS(
            final ValueMap metadata, final Unit linearUnit)
            throws IOException, FactoryException {

        // /////////////////////////////////////////////////////////////////
        // At the top level a user-defined PCRS is made by
        // <ol>
        // <li>PCSCitationGeoKey (NAME)
        // <li>ProjectionGeoKey
        // <li>GeographicTypeGeoKey
        // </ol>
        // /////////////////////////////////////////////////////////////////
        // //
        // NAME of the user defined projected coordinate reference system.
        // //
        String projectedCrsName = metadata.getAsString(PCSCitationGeoKey);
        if (projectedCrsName == null) {
            projectedCrsName = "unnamed".intern();
        }

        // /////////////////////////////////////////////////////////////////////
        // PROJECTION geo key for this projected coordinate reference system.
        // get the projection code for this PCRS to build it from the GCS.
        //
        // In case i is user defined it requires:
        // PCSCitationGeoKey
        // ProjCoordTransGeoKey
        // ProjLinearUnitsGeoKey
        // /////////////////////////////////////////////////////////////////////
        final String projCode = metadata.getAsString(ProjectionGeoKey);
        boolean projUserDefined = false;
        if (projCode == null
                || projCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            projUserDefined = true;
        }

        // is it user defined?
        Conversion projection = null;
        final ParameterValueGroup parameters;
        if (projUserDefined) {
            // /////////////////////////////////////////////////////////////////
            // A user defined projection is made up by
            // <ol>
            // <li>PCSCitationGeoKey (NAME)
            // <li>ProjCoordTransGeoKey
            // <li>ProjLinearUnitsGeoKey
            // </ol>
            // /////////////////////////////////////////////////////////////////
            // NAME of this projection coordinate transformation
            // getting user defined parameters
            String projectionName = metadata.getAsString(PCSCitationGeoKey);
            if (projectionName == null) {
                projectionName = "unnamed";
            }

            // //
            // getting default parameters for this projection and filling them
            // with the values found
            // inside the geokeys list.
            // //
            parameters = createUserDefinedProjectionParameter(projectionName,metadata);
            if (parameters == null) {
                throw new IOException("Projection is not supported.");
            }

            projection = new DefiningConversion(projectionName, parameters);

        } else {
            parameters = null;
            projection = (Conversion) epsgFactory.createCoordinateOperation(String.valueOf(projCode));

        }

        // /////////////////////////////////////////////////////////////////////
        // GEOGRAPHIC CRS
        // /////////////////////////////////////////////////////////////////////
        final GeographicCRS gcs = createGeographicCoordinateSystem(metadata);

        // was the projection user defined?
        // in such case we need to set the remaining parameters.
        if (projUserDefined) {
            final GeodeticDatum tempDatum = ((GeodeticDatum) gcs.getDatum());
            final DefaultEllipsoid tempEll = (DefaultEllipsoid) tempDatum.getEllipsoid();
            double inverseFlattening = tempEll.getInverseFlattening();
            double semiMajorAxis = tempEll.getSemiMajorAxis();
            // setting missing parameters
            parameters.parameter("semi_minor").setValue(
                    semiMajorAxis * (1 - (1 / inverseFlattening)));
            parameters.parameter("semi_major").setValue(semiMajorAxis);

        }

        // /////////////////////////////////////////////////////////////////////
        // PROJECTED CRS
        // /////////////////////////////////////////////////////////////////////
        // //
        //
        // I am putting particular attention on the management of the unit
        // of measure since it seems that very often people change the unit
        // of measure to feet even if the standard UoM for the request
        // projection is M.
        //
        // ///
        if (projUserDefined) {
            CartesianCS cs = PredefinedCS.PROJECTED;
            if(linearUnit != null && !linearUnit.equals(SI.METRE)){
                cs = PredefinedCS.usingUnit(cs, linearUnit);
            }

            return this.factories.getCRSFactory().createProjectedCRS(
                    Collections.singletonMap("name", projectedCrsName),
                    gcs, projection, cs);
        }
        // standard projection
        if (linearUnit != null && !linearUnit.equals(SI.METRE)) {
            return factories.getCRSFactory().createProjectedCRS(Collections.singletonMap(
                    "name", projectedCrsName), gcs, projection,
                    PredefinedCS.usingUnit(PredefinedCS.PROJECTED, linearUnit));
        }
        return factories.getCRSFactory().createProjectedCRS(Collections.singletonMap("name",
                projectedCrsName), gcs, projection,
                PredefinedCS.PROJECTED);
    }


    ////////////////////////////////////////////////////////////////////////////
    // UTILS ///////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve the scale factor parameter as defined by the geotiff
     * specification.
     *
     * @param metadata to use for searching the scale factor.
     * @return the scale factor
     */
    private double getScaleFactor(final ValueMap metadata) {
        String scale = metadata.getAsString(ProjScaleAtCenterGeoKey);
        if (isZero(scale)) {
            scale = metadata.getAsString(ProjScaleAtNatOriginGeoKey);
        }
        if (scale == null) {
            return 1.0;
        }
        return Double.parseDouble(scale);
    }

    /**
     * Getting the false easting with a minimum of tolerance with respect to the
     * parameters name. I saw that often people use the wrong geokey to store
     * the false easting, we cannot be too picky we need to get going pretty
     * smoothly.
     *
     * @param metadata to use for searching the false easting.
     * @return double False easting.
     */
    private double getFalseEasting(final ValueMap metadata) {
        String easting = metadata.getAsString(ProjFalseEastingGeoKey);
        if (isZero(easting)) {
            easting = metadata.getAsString(ProjFalseOriginEastingGeoKey);
        }
        if (easting == null) {
            return 0.0;
        }
        return Double.parseDouble(easting);

    }

    /**
     * Getting the false northing with a minimum of tolerance with respect to
     * the parameters name. I saw that often people use the wrong geokey to
     * store the false easting, we cannot be too picky we need to get going
     * pretty smoothly.
     *
     * @param metadata to use for searching the false northing.
     * @return double False northing.
     */
    private double getFalseNorthing(final ValueMap metadata) {
        String northing = metadata.getAsString(ProjFalseNorthingGeoKey);
        if (isZero(northing)) {
            northing = metadata.getAsString(ProjFalseOriginNorthingGeoKey);
        }
        if (northing == null) {
            return 0.0;
        }
        return Double.parseDouble(northing);

    }

    /**
     * Getting the origin long with a minimum of tolerance with respect to the
     * parameters name. I saw that often people use the wrong geokey to store
     * the false easting, we cannot be too picky we need to get going pretty
     * smoothly.
     *
     * @param metadata
     *            to use for searching the originating longitude.
     * @return double origin longitude.
     */
    private double getOriginLong(final ValueMap metadata) {
        String origin = metadata.getAsString(ProjCenterLongGeoKey);
        if (isZero(origin)) {
            origin = metadata.getAsString(ProjNatOriginLongGeoKey);
        }
        if (isZero(origin)) {
            origin = metadata.getAsString(ProjFalseOriginLongGeoKey);
        }
        if (isZero(origin)) {
            origin = metadata.getAsString(ProjFalseNorthingGeoKey);
        }
        if (origin == null) {
            return 0.0;
        }
        return Double.parseDouble(origin);
    }

    /**
     * Getting the origin lat with a minimum of tolerance with respect to the
     * parameters name. I saw that often people use the wrong geokey to store
     * the false easting, we cannot be too picky we need to get going pretty
     * smoothly.
     *
     * @param metadata to use for searching the origin latitude.
     * @return double origin latitude.
     */
    private double getOriginLat(final ValueMap metadata) {
        String origin = metadata.getAsString(ProjCenterLatGeoKey);
        if (isZero(origin)) {
            origin = metadata.getAsString(ProjNatOriginLatGeoKey);
        }
        if (isZero(origin)) {
            origin = metadata.getAsString(ProjFalseOriginLatGeoKey);
        }
        if (origin == null) {
            return 0.0;
        }

        return Double.parseDouble(origin);
    }

    /**
     * Check if given string is null, empty or equals to zero.
     * Geotiff tags are often badly defined, this ensure we skip "0.0" tags in the
     * hope another tag will define a proper value.
     * In the worst case if no valid tags are found the 0.0 value will be used anyway.
     *
     * @param code
     * @return true if value is zero
     */
    private static boolean isZero(String code){
        if(code==null ||code.isEmpty()) return true;

        try{
            final double d = Double.parseDouble(code);
            return d==0.0;
        }catch(NumberFormatException ex){
            return true;
        }
    }

    /**
     * @todo we should somehow try to to support user defined coordinate
     *       transformation even if for the moment is not so clear to me how we
     *       could achieve that since if we have no clue about the coordinate
     *       transform what we are supposed to do in order to build a
     *       conversion, guess it? How could we pick up the parameters, should
     *       look for all and then guess the right transformation?
     *
     * @param name indicates the name for the projection.
     * @param metadata to use for building this {@link ParameterValueGroup}.
     * @return a {@link ParameterValueGroup} that can be used to trigger this
     *         projection.
     * @throws IOException
     * @throws FactoryException
     */
    private ParameterValueGroup createUserDefinedProjectionParameter(
            final String name, final ValueMap metadata)
            throws IOException, FactoryException {
        // //
        //
        // Trying to get the name for the coordinate transformation involved.
        //
        // ///
        final String coordTrans = metadata.getAsString(ProjCoordTransGeoKey);

        // throw descriptive exception if ProjCoordTransGeoKey not defined
        if ((coordTrans == null)
                || coordTrans.equalsIgnoreCase(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            throw new IOException(
                    "User defined projections must specify coordinate transformation code in ProjCoordTransGeoKey");
        }

        // getting math transform factory
        return setParametersForProjection(name, coordTrans, metadata);
    }

    /**
     * Set the projection parameters basing its decision on the projection name.
     * I found a complete list of projections on the geotiff website at address
     * http://www.remotesensing.org/geotiff/proj_list.
     *
     * I had no time to implement support for all of them therefore you will not
     * find all of them. If you want go ahead and add support for the missing
     * ones. I have tested this code against some geotiff files you can find on
     * the geotiff website under the ftp sample directory but I can say that
     * they are a real mess! I am respecting the specification strictly while
     * many of those fields do not! I could make this method trickier and use
     * workarounds in order to be less strict but I will not do this, since I
     * believe it is may lead us just on a very dangerous path.
     *
     *
     * @param name
     * @param metadata to use fo building this {@link ParameterValueGroup}.
     * @param coordTrans
     *            a {@link ParameterValueGroup} that can be used to trigger this
     *            projection.
     *
     * @return
     * @throws GeoTiffException
     */
    private ParameterValueGroup setParametersForProjection(String name,
            final String coordTransCode,
            final ValueMap metadata) throws IOException {
        ParameterValueGroup parameters = null;
        try {
            int code = 0;
            if (coordTransCode != null) {
                code = Integer.parseInt(coordTransCode);
            }
            if (name == null) {
                name = "unnamed";
            }

            /**
             * Transverse Mercator
             */
            if (name.equalsIgnoreCase("transverse_mercator")
                    || code == CT_TransverseMercator) {
                parameters = mtFactory.getDefaultParameters(code(new TransverseMercator().getParameters()));    // TODO: avoid creation of temporary object.
                parameters.parameter(code(TransverseMercator.LONGITUDE_OF_ORIGIN)).setValue(getOriginLong(metadata));
                parameters.parameter(code(TransverseMercator.LATITUDE_OF_ORIGIN)).setValue(getOriginLat(metadata));
                parameters.parameter(code(TransverseMercator.SCALE_FACTOR)).setValue(metadata.getAsDouble(ProjScaleAtNatOriginGeoKey));
                parameters.parameter(code(TransverseMercator.FALSE_EASTING)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(TransverseMercator.FALSE_NORTHING)).setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * Equidistant Cylindrical - Plate Caree - Equirectangular
             */
            if (name.equalsIgnoreCase("Equidistant_Cylindrical")
                    || name.equalsIgnoreCase("Plate_Carree")
                    || name.equalsIgnoreCase("Equidistant_Cylindrical")
                    || code == CT_Equirectangular) {
                parameters = mtFactory.getDefaultParameters("Equirectangular");
                parameters.parameter(code(Equirectangular.STANDARD_PARALLEL)).setValue(getOriginLat(metadata));
                parameters.parameter(code(Equirectangular.LONGITUDE_OF_ORIGIN)).setValue(getOriginLong(metadata));
                parameters.parameter(code(Equirectangular.FALSE_EASTING)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(Equirectangular.FALSE_NORTHING)).setValue(getFalseNorthing(metadata));
                return parameters;
            }
            /**
             * Mercator_1SP
             * Mercator_2SP
             */
            if (name.equalsIgnoreCase("mercator_1SP")
                    || name.equalsIgnoreCase("Mercator_2SP")
                    || code == CT_Mercator) {

                final double standard_parallel_1 = metadata.getAsDouble(ProjStdParallel1GeoKey);
                boolean isMercator2SP = false;
                if (!Double.isNaN(standard_parallel_1)) {
                    parameters = mtFactory.getDefaultParameters("Mercator_2SP");
                    isMercator2SP = true;
                } else {
                    parameters = mtFactory.getDefaultParameters("Mercator_1SP");
                }

                parameters.parameter(code(Mercator1SP.LONGITUDE_OF_ORIGIN)).setValue(getOriginLong(metadata));
                parameters.parameter(code(Mercator1SP.LATITUDE_OF_ORIGIN)).setValue(getOriginLat(metadata));
                parameters.parameter(code(Mercator2SP.FALSE_EASTING)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(Mercator2SP.FALSE_NORTHING)).setValue(getFalseNorthing(metadata));
                if (isMercator2SP) {
                    parameters.parameter(code(Mercator2SP.STANDARD_PARALLEL)).setValue(standard_parallel_1);
                } else {
                    parameters.parameter(code(Mercator1SP.SCALE_FACTOR)).setValue(getScaleFactor(metadata));
                }
                return parameters;
            }

            /**
             * Lambert_conformal_conic_1SP
             */
            if (name.equalsIgnoreCase("lambert_conformal_conic_1SP")
                    || code == CT_LambertConfConic_Helmert) {
                parameters = mtFactory.getDefaultParameters("Lambert_Conformal_Conic_1SP");
                parameters.parameter(code(LambertConformal1SP.LONGITUDE_OF_ORIGIN)).setValue(getOriginLong(metadata));
                parameters.parameter(code(LambertConformal1SP.LATITUDE_OF_ORIGIN)).setValue(getOriginLat(metadata));
                parameters.parameter(code(LambertConformal1SP.SCALE_FACTOR)).setValue(metadata.getAsDouble(ProjScaleAtNatOriginGeoKey));
                parameters.parameter(code(LambertConformal1SP.FALSE_EASTING)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(LambertConformal1SP.FALSE_NORTHING)).setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * Lambert_conformal_conic_2SP
             */
            if (name.equalsIgnoreCase("lambert_conformal_conic_2SP")
                    || code == CT_LambertConfConic_2SP) {
                parameters = mtFactory.getDefaultParameters("Lambert_Conformal_Conic_2SP");
                parameters.parameter(code(LambertConformal2SP.LONGITUDE_OF_FALSE_ORIGIN)).setValue(getOriginLong(metadata));
                parameters.parameter(code(LambertConformal2SP.LATITUDE_OF_FALSE_ORIGIN)).setValue(getOriginLat(metadata));
                parameters.parameter(code(LambertConformal2SP.STANDARD_PARALLEL_1)).setValue(metadata.getAsDouble(ProjStdParallel1GeoKey));
                parameters.parameter(code(LambertConformal2SP.STANDARD_PARALLEL_2)).setValue(metadata.getAsDouble(ProjStdParallel2GeoKey));
                parameters.parameter(code(LambertConformal2SP.FALSE_EASTING)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(LambertConformal2SP.FALSE_NORTHING)).setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * Krovak
             */
            if (name.equalsIgnoreCase("Krovak")) {
                parameters = mtFactory.getDefaultParameters(code(Krovak.PARAMETERS));
                parameters.parameter(code(Krovak.LONGITUDE_OF_CENTRE)).setValue(getOriginLong(metadata));
                parameters.parameter(code(Krovak.LATITUDE_OF_CENTRE)).setValue(getOriginLat(metadata));
                parameters.parameter(code(Krovak.AZIMUTH)).setValue(metadata.getAsDouble(ProjStdParallel1GeoKey));
                parameters.parameter(code(Krovak.PSEUDO_STANDARD_PARALLEL)).setValue(metadata.getAsDouble(ProjStdParallel2GeoKey));
                parameters.parameter(code(Krovak.SCALE_FACTOR)).setValue(getFalseEasting(metadata));
                return parameters;
            }

            /**
             * STEREOGRAPHIC
             */
            if (name.equalsIgnoreCase("stereographic")
                    || code == CT_Stereographic) {
                parameters = mtFactory.getDefaultParameters(code(Stereographic.PARAMETERS));
                parameters.parameter(code(Stereographic.CENTRAL_MERIDIAN)).setValue(this.getOriginLong(metadata));
                parameters.parameter(code(Stereographic.LATITUDE_OF_ORIGIN)).setValue(this.getOriginLat(metadata));
                parameters.parameter(code(Stereographic.SCALE_FACTOR)).setValue(metadata.getAsDouble(ProjScaleAtNatOriginGeoKey));
                parameters.parameter(code(Stereographic.FALSE_EASTING)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(Stereographic.FALSE_NORTHING)).setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * POLAR_STEREOGRAPHIC variant A B and C
             */
            if (code == CT_PolarStereographic) {

                /**
                 * They exist 3 kind of polar StereoGraphic projections,define the case
                 * relative to existing needed attributs
                 */
                //-- set the mutual projection attributs
                //-- all polar stereographic formulas share LONGITUDE_OF_ORIGIN
                final double longitudeOfOrigin = metadata.getAsDouble(ProjStraightVertPoleLongGeoKey);

                /*
                * For polar Stereographic variant A only latitudeOfNaturalOrigin expected values are {-90; +90}.
                * In some case, standard parallele is stipulate into latitudeOfNaturalOrigin tiff tag by error.
                * To avoid CRS problem creation, try to anticipe this comportement by switch latitudeOfNaturalOrigin into standard parallele.
                * HACK FOR USGS LANDSAT 8 difference between geotiff tag and Landsat 8 metadata MTL.txt file.
                */
                double standardParallel                 = metadata.getAsDouble(ProjStdParallel1GeoKey);
                final double latitudeOfNaturalOrigin    = metadata.getAsDouble(ProjNatOriginLatGeoKey);
                final boolean isVariantALatitudeConform = (Math.abs(Latitude.MAX_VALUE - Math.abs(latitudeOfNaturalOrigin)) <  Formulas.ANGULAR_TOLERANCE);

                if (!isVariantALatitudeConform && Double.isNaN(standardParallel)) {
                    LOGGER.log(Level.WARNING, "The latitudeOfNaturalOrigin for Polar Stereographic variant A is not conform.\n"
                            + "Expected values are {-90; +90}, found : "+latitudeOfNaturalOrigin+"\n"
                            + "Switch latitudeOfNaturalOrigin by Latitude of standard parallel to try building of Polar Stereographic Variant B or C.");
                    standardParallel = latitudeOfNaturalOrigin;
                }

                if (Double.isNaN(standardParallel)) {
                    //-- no standard parallele : PolarStereoGraphic VARIANT A
                    final OperationMethod method = DefaultFactories.forBuildin(CoordinateOperationFactory.class)
                    .getOperationMethod("Polar Stereographic (variant A)");

                    parameters = method.getParameters().createValue();
                    parameters.parameter(code(PolarStereographicA.LONGITUDE_OF_ORIGIN)).setValue(longitudeOfOrigin);
                    parameters.parameter(code(PolarStereographicA.LATITUDE_OF_ORIGIN)).setValue(latitudeOfNaturalOrigin);
                    parameters.parameter(code(PolarStereographicA.SCALE_FACTOR)).setValue(metadata.getAsDouble(ProjScaleAtNatOriginGeoKey));
                    parameters.parameter(code(PolarStereographicA.FALSE_EASTING)).setValue(metadata.getAsDouble(ProjFalseEastingGeoKey));
                    parameters.parameter(code(PolarStereographicA.FALSE_NORTHING)).setValue(metadata.getAsDouble(ProjFalseNorthingGeoKey));

                } else {

                    //-- Variant B and C share STANDARD_PARALLEL

                    final double falseOriginEasting = metadata.getAsDouble(ProjFalseOriginEastingGeoKey);
                    if (Double.isNaN(falseOriginEasting)) {
                        //-- no false Origin Easting : PolarStereoGraphic VARIANT B
                        final OperationMethod method = DefaultFactories.forBuildin(CoordinateOperationFactory.class)
                              .getOperationMethod("Polar Stereographic (variant B)");

                        parameters = method.getParameters().createValue();
                        parameters.parameter(code(PolarStereographicB.STANDARD_PARALLEL)).setValue(standardParallel);
                        parameters.parameter(code(PolarStereographicB.LONGITUDE_OF_ORIGIN)).setValue(longitudeOfOrigin);
                        parameters.parameter(code(PolarStereographicB.FALSE_EASTING)).setValue(metadata.getAsDouble(ProjFalseEastingGeoKey));
                        parameters.parameter(code(PolarStereographicB.FALSE_NORTHING)).setValue(metadata.getAsDouble(ProjFalseNorthingGeoKey));
                    } else {
                        //-- PolarStereoGraphic VARIANT C
                        final OperationMethod method = DefaultFactories.forBuildin(CoordinateOperationFactory.class)
                              .getOperationMethod("Polar Stereographic (variant C)");

                        parameters = method.getParameters().createValue();
                        parameters.parameter(code(PolarStereographicB.STANDARD_PARALLEL)).setValue(standardParallel);
                        parameters.parameter(code(PolarStereographicB.LONGITUDE_OF_ORIGIN)).setValue(longitudeOfOrigin);
                        parameters.parameter(code(PolarStereographicC.EASTING_AT_FALSE_ORIGIN)).setValue(metadata.getAsDouble(ProjFalseOriginEastingGeoKey));
                        parameters.parameter(code(PolarStereographicC.NORTHING_AT_FALSE_ORIGIN)).setValue(metadata.getAsDouble(ProjFalseNorthingGeoKey));
                    }
                }
            }

            /**
             * Oblique Stereographic
             */
            if (name.equalsIgnoreCase("oblique_stereographic")
                    || code == CT_ObliqueStereographic) {
                parameters = mtFactory.getDefaultParameters(code(new ObliqueStereographic().getParameters()));  // TODO: use a more efficient way.
                parameters.parameter(code(ObliqueStereographic.LONGITUDE_OF_ORIGIN)).setValue(getOriginLong(metadata));
                parameters.parameter(code(ObliqueStereographic.LATITUDE_OF_ORIGIN)).setValue(getOriginLat(metadata));
                parameters.parameter(code(ObliqueStereographic.SCALE_FACTOR)).setValue(metadata.getAsDouble(ProjScaleAtNatOriginGeoKey));
                parameters.parameter(code(ObliqueStereographic.FALSE_EASTING)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(ObliqueStereographic.FALSE_NORTHING)).setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * OBLIQUE_MERCATOR.
             */
            if (name.equalsIgnoreCase("oblique_mercator")
                    || name.equalsIgnoreCase("hotine_oblique_mercator")
                    || code == CT_ObliqueMercator) {
                parameters = mtFactory.getDefaultParameters(code(ObliqueMercator.PARAMETERS));
                parameters.parameter(code(ObliqueMercator.SCALE_FACTOR)).setValue(getScaleFactor(metadata));
                parameters.parameter(code(ObliqueMercator.AZIMUTH)).setValue(metadata.getAsDouble(ProjAzimuthAngleGeoKey));
                parameters.parameter(code(ObliqueMercator.FALSE_EASTING)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(ObliqueMercator.FALSE_NORTHING)).setValue(getFalseNorthing(metadata));
                parameters.parameter(code(ObliqueMercator.LONGITUDE_OF_CENTRE)).setValue(getOriginLong(metadata));
                parameters.parameter(code(ObliqueMercator.LATITUDE_OF_CENTRE)).setValue(getOriginLat(metadata));
                return parameters;
            }

            /**
             * albers_Conic_Equal_Area
             */
            if (name.equalsIgnoreCase("albers_Conic_Equal_Area")
                    || code == CT_AlbersEqualArea) {
                parameters = mtFactory.getDefaultParameters("Albers Equal Area");
                parameters.parameter(code(AlbersEqualArea.STANDARD_PARALLEL_1)).setValue(metadata.getAsDouble(ProjStdParallel1GeoKey));
                parameters.parameter(code(AlbersEqualArea.STANDARD_PARALLEL_2)).setValue(metadata.getAsDouble(ProjStdParallel2GeoKey));
                parameters.parameter(code(AlbersEqualArea.LATITUDE_OF_FALSE_ORIGIN)).setValue(getOriginLat(metadata)); //TODO what is the correct match ?
                parameters.parameter(code(AlbersEqualArea.LONGITUDE_OF_FALSE_ORIGIN)).setValue(getOriginLong(metadata)); //TODO what is the correct match ?
                parameters.parameter(code(AlbersEqualArea.EASTING_AT_FALSE_ORIGIN)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(AlbersEqualArea.NORTHING_AT_FALSE_ORIGIN)).setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * Orthographic
             */
            if (name.equalsIgnoreCase("Orthographic")
                    || code == CT_Orthographic) {
                parameters = mtFactory.getDefaultParameters(code(Orthographic.PARAMETERS));
                parameters.parameter(code(Orthographic.LATITUDE_OF_CENTRE)).setValue(getOriginLat(metadata));
                parameters.parameter(code(Orthographic.LONGITUDE_OF_CENTRE)).setValue(getOriginLong(metadata));
                parameters.parameter(code(Orthographic.FALSE_EASTING)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(Orthographic.FALSE_NORTHING)).setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * Lambert Azimuthal Equal Area
             */
            if (name.equalsIgnoreCase("Lambert_Azimuthal_Equal_Area")
                    || code == CT_LambertAzimEqualArea) {
                parameters = mtFactory.getDefaultParameters(code(LambertAzimuthalEqualArea.PARAMETERS));
                parameters.parameter(code(LambertAzimuthalEqualArea.LATITUDE_OF_CENTRE)).setValue(getOriginLat(metadata));
                parameters.parameter(code(LambertAzimuthalEqualArea.LONGITUDE_OF_CENTRE)).setValue(getOriginLong(metadata));
                parameters.parameter(code(LambertAzimuthalEqualArea.FALSE_EASTING)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(LambertAzimuthalEqualArea.FALSE_NORTHING)).setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * New Zealand Map Grid
             */
            if (name.equalsIgnoreCase("New_Zealand_Map_Grid")
                    || code == CT_NewZealandMapGrid) {
                parameters = mtFactory.getDefaultParameters(code(NewZealandMapGrid.PARAMETERS));
                parameters.parameter(code(NewZealandMapGrid.LATITUDE_OF_ORIGIN)).setValue(this.getOriginLat(metadata));
                parameters.parameter(code(NewZealandMapGrid.CENTRAL_MERIDIAN)).setValue(getOriginLong(metadata));
                parameters.parameter(code(NewZealandMapGrid.FALSE_EASTING)).setValue(getFalseEasting(metadata));
                parameters.parameter(code(NewZealandMapGrid.FALSE_NORTHING)).setValue(getFalseNorthing(metadata));
                return parameters;
            }

        } catch (FactoryException e) {
            throw new IOException(e.getLocalizedMessage(), e);
        }

        return parameters;
    }

    /**
     * Creation of a geographic coordinate reference system as specified in the
     * GeoTiff specification. User defined values are supported for all the
     * possible levels of the above mentioned specification.
     *
     * @param metadata
     *            to use for building a {@link GeographicCRS}.
     *
     * @return
     * @throws IOException
     */
    private GeographicCRS createGeographicCoordinateSystem(
            final ValueMap metadata) throws IOException, FactoryException {
        GeographicCRS gcs = null;

        // ////////////////////////////////////////////////////////////////////
        // Get the crs code
        // ////////////////////////////////////////////////////////////////////
        final String tempCode = metadata.getAsString(GeographicTypeGeoKey);
        // lookup the angular units used in this geotiff image
        Unit angularUnit = null;
        try {
            angularUnit = createUnit(GeogAngularUnitsGeoKey,
                    GeogAngularUnitSizeGeoKey, SI.RADIAN,
                    NonSI.DEGREE_ANGLE, metadata);
        } catch (IOException e) {
            angularUnit = null;
        }
        // linear unit
        Unit linearUnit = null;
        try {
            linearUnit = createUnit(GeogLinearUnitsGeoKey,
                    GeogLinearUnitSizeGeoKey, SI.METRE,
                    SI.METRE, metadata);
        } catch (IOException e) {
            linearUnit = null;
        }
        // if it's user defined, there's a lot of work to do
        if (tempCode == null
                || tempCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            // ////////////////////////////////////////////////////////////////////
            // it is user-defined we have to parse a lot of information in order
            // to built it.
            // ////////////////////////////////////////////////////////////////////
            gcs = createUserDefinedGCS(metadata, linearUnit, angularUnit);

        } else {
            try {

                // ////////////////////////////////////////////////////////////////////
                // If it's not user defined, just use the EPSG factory to create
                // the coordinate system but check if the user specified a
                // different angular unit. In this case we need to create a
                // user-defined GCRS.
                // ////////////////////////////////////////////////////////////////////
                final StringBuffer geogCode = new StringBuffer(tempCode);
                if (!tempCode.startsWith("EPSG")
                        && !tempCode.startsWith("epsg")) {
                    geogCode.insert(0, "EPSG:");
                }

                final CoordinateReferenceSystem decCRS = AbstractCRS.castOrCopy(CRS.forCode(geogCode.toString())).forConvention(AxesConvention.RIGHT_HANDED);
                //-- all CRS must be Geodetic
                if (!(decCRS instanceof GeodeticCRS))
                    throw new IllegalArgumentException("Impossible to define CRS from none Geodetic base. found : "+decCRS.toWKT());

                if (decCRS instanceof GeographicCRS) {
                    gcs = (GeographicCRS) AbstractCRS.castOrCopy(CRS.forCode(geogCode.toString())).forConvention(AxesConvention.RIGHT_HANDED);
                } else {
                    //-- Try to build it from datum and re-create Geographic CRS.
                    LOGGER.log(Level.WARNING, "Impossible to build Projected CRS from none Geographic base CRS, replaced by Geographic CRS.");
                    final GeodeticCRS geodeticCrs = (GeodeticCRS) decCRS;
                    final GeodeticDatum datum = geodeticCrs.getDatum();
                    final HashMap<String, Object> properties = new HashMap<String, Object>();
                    properties.put(GeographicCRS.NAME_KEY, decCRS.getName());
                    gcs = new DefaultGeographicCRS(properties, datum, org.apache.sis.referencing.CommonCRS.defaultGeographic().getCoordinateSystem());
                }

                if (angularUnit != null
                        && !angularUnit.equals(gcs.getCoordinateSystem().getAxis(0).getUnit())) {
                    // //
                    // Create a user-defined GCRS using the provided angular
                    // unit.
                    // //
                    gcs = new DefaultGeographicCRS(name(IdentifiedObjects.getName(gcs, new DefaultCitation("EPSG"))),
                            (GeodeticDatum) gcs.getDatum(),
                            PredefinedCS.usingUnit(CommonCRS.defaultGeographic().getCoordinateSystem(), angularUnit));
                }
            } catch (FactoryException fe) {
                throw new IOException(fe);
            }
        }
        return gcs;
    }

    /**
     * Creates a {@link CartesianCS} for a {@link ProjectedCRS} given the
     * provided {@link Unit}.
     *
     * @todo consider caching this items
     * @param linearUnit
     *            to be used for building this {@link CartesianCS}.
     * @return an instance of {@link CartesianCS} using the provided
     *         {@link Unit},
     */
    private DefaultCartesianCS createProjectedCS(final Unit linearUnit) {
        if (linearUnit == null) {
            throw new NullPointerException(
                    "Error when trying to create a PCS using this linear UoM ");
        }
        if (!linearUnit.isCompatible(SI.METRE)) {
            throw new IllegalArgumentException(
                    "Error when trying to create a PCS using this linear UoM "
                    + linearUnit.toString());
        }
        return new DefaultCartesianCS(name(Vocabulary.formatInternational(Vocabulary.Keys.Projected).toString()),
                new DefaultCoordinateSystemAxis(name(Vocabulary.formatInternational(Vocabulary.Keys.Easting).toString()), "E",
                AxisDirection.EAST, linearUnit),
                new DefaultCoordinateSystemAxis(name(Vocabulary.formatInternational(Vocabulary.Keys.Northing).toString()), "N",
                AxisDirection.NORTH, linearUnit));
    }

    /**
     * Creating a prime meridian for the gcs we are creating at an higher level.
     * As usual this method tries to follow the geotiff specification.
     *
     * @param linearUnit
     *            to use for building this {@link PrimeMeridian}.
     * @return a {@link PrimeMeridian} built using the provided {@link Unit} and
     *         the provided metadata.
     * @throws IOException
     */
    private PrimeMeridian createPrimeMeridian(
            final ValueMap metadata, final Unit linearUnit)
            throws IOException {
        // look up the prime meridian:
        // + could be an EPSG code
        // + could be user defined
        // + not defined = greenwich
        final String pmCode = metadata.getAsString(GeogPrimeMeridianGeoKey);
        PrimeMeridian pm = null;

        try {
            if (pmCode != null) {
                if (pmCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
                    try {
                        final String name = metadata.getAsString(GeogCitationGeoKey);
                        final String pmValue = metadata.getAsString(GeogPrimeMeridianLongGeoKey);
                        final double pmNumeric = Double.parseDouble(pmValue);
                        // is it Greenwich?
                        if (pmNumeric == 0) {
                            return CommonCRS.WGS84.primeMeridian();
                        }
                        final Map props = new HashMap();
                        props.put("name", (name != null) ? name
                                : "User Defined GEOTIFF Prime Meridian");
                        pm = datumObjFactory.createPrimeMeridian(props,
                                pmNumeric, linearUnit);
                    } catch (NumberFormatException nfe) {
                        throw new IOException("Invalid user-defined prime meridian spec.",nfe);
                    }
                } else {
                    pm = epsgFactory.createPrimeMeridian(String.valueOf(pmCode));
                }
            } else {
                pm = CommonCRS.WGS84.primeMeridian();
            }
        } catch (FactoryException fe) {
            throw new IOException(fe);
        }

        return pm;
    }

    /**
     * Looks up the Geodetic Datum as specified in the GeoTIFFWritingUtilities
     * file. The geotools definition of the geodetic datum includes both an
     * ellipsoid and a prime meridian, but the code in the
     * GeoTIFFWritingUtilities file does NOT include the prime meridian, as it
     * is specified separately. This code currently does not support user
     * defined datum.
     *
     * @param unit to use for building this {@link GeodeticDatum}.
     * @return a {@link GeodeticDatum}.
     * @throws IOException
     * @throws GeoTiffException
     *
     */
    private GeodeticDatum createGeodeticDatum(final Unit unit,
            final ValueMap metadata) throws IOException {
        // lookup the datum (w/o PrimeMeridian), error if "user defined"
        GeodeticDatum datum = null;
        final String datumCode = metadata.getAsString(GeogGeodeticDatumGeoKey);

        if (datumCode == null) {
            throw new IOException("A user defined Geographic Coordinate system must include a predefined datum!");
        }

        if (datumCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            /**
             * USER DEFINED DATUM
             */
            // datum name
            final String datumName = (metadata.getAsString(GeogCitationGeoKey) != null ? metadata.getAsString(GeogCitationGeoKey)
                    : "unnamed");

            // is it WGS84?
            if (datumName.trim().equalsIgnoreCase("WGS84")) {
                return CommonCRS.WGS84.datum();
            }

            // ELLIPSOID
            final Ellipsoid ellipsoid = createEllipsoid(unit, metadata);

            // PRIME MERIDIAN
            // lookup the Prime Meridian.
            final PrimeMeridian primeMeridian = createPrimeMeridian(metadata,
                    unit);

            // DATUM
            datum = new DefaultGeodeticDatum(Collections.singletonMap(GeodeticDatum.NAME_KEY, datumName), ellipsoid,
                    primeMeridian);
        } else {
            /**
             * NOT USER DEFINED DATUM
             */
            // we are going to use the provided EPSG code
            try {
                datum = (GeodeticDatum) (epsgFactory.createDatum(String.valueOf(datumCode)));
            } catch (FactoryException fe) {
                throw new IOException(fe.getLocalizedMessage(), fe);
            } catch (ClassCastException cce) {
                throw new IOException(cce.getLocalizedMessage(), cce);
            }
        }

        return datum;
    }

    /**
     * Creating an ellipsoid following the GeoTiff spec.
     *
     * @param unit to build this {@link Ellipsoid}..
     * @return an {@link Ellipsoid}.
     * @throws GeoTiffException
     */
    private Ellipsoid createEllipsoid(final Unit unit,
            final ValueMap metadata) throws IOException {
        // /////////////////////////////////////////////////////////////////////
        // Getting the ellipsoid key in order to understand if we are working
        // against a common ellipsoid or a user defined one.
        // /////////////////////////////////////////////////////////////////////
        // ellipsoid key
        final String ellipsoidKey = metadata.getAsString(GeogEllipsoidGeoKey);
        String temp = null;
        // is the ellipsoid user defined?
        if (ellipsoidKey.equalsIgnoreCase(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            // /////////////////////////////////////////////////////////////////////
            // USER DEFINED ELLIPSOID
            // /////////////////////////////////////////////////////////////////////
            String nameEllipsoid = metadata.getAsString(GeogCitationGeoKey);
            if (nameEllipsoid == null) {
                nameEllipsoid = "unnamed";
            }
            // is it the default for WGS84?
            if (nameEllipsoid.trim().equalsIgnoreCase("WGS84")) {
                return CommonCRS.WGS84.ellipsoid();
            }

            // //
            // It is worth to point out that I ALWAYS use the inverse flattening
            // along with the semi-major axis to builde the Flattened Sphere.
            // This
            // has to be done in order to comply with the opposite process of
            // going from CRS to metadata where this coupls is always used.
            // //
            // getting temporary parameters
            temp = metadata.getAsString(GeogSemiMajorAxisGeoKey);
            final double semiMajorAxis = (temp != null ? Double.parseDouble(temp) : Double.NaN);
            temp = metadata.getAsString(GeogInvFlatteningGeoKey);
            final double inverseFlattening;
            if (temp != null) {
                inverseFlattening = (temp != null ? Double.parseDouble(temp)
                        : Double.NaN);
            } else {
                temp = metadata.getAsString(GeogSemiMinorAxisGeoKey);
                final double semiMinorAxis = (temp != null ? Double.parseDouble(temp) : Double.NaN);
                inverseFlattening = semiMajorAxis
                        / (semiMajorAxis - semiMinorAxis);

            }
            // look for the Ellipsoid first then build the datum
            return DefaultEllipsoid.createFlattenedSphere(
                    Collections.singletonMap(DefaultEllipsoid.NAME_KEY, nameEllipsoid),
                    semiMajorAxis, inverseFlattening, unit);
        }

        try {
            // /////////////////////////////////////////////////////////////////////
            // EPSG STANDARD ELLIPSOID
            // /////////////////////////////////////////////////////////////////////
            return epsgFactory.createEllipsoid(String.valueOf(ellipsoidKey));
        } catch (FactoryException fe) {
            throw new IOException(fe.getLocalizedMessage(), fe);
        }
    }

    /**
     * This code creates an <code>javax.Units.Unit</code> object out of the
     * <code>ProjLinearUnitsGeoKey</code> and the
     * <code>ProjLinearUnitSizeGeoKey</code>. The unit may either be
     * specified as a standard EPSG recognized unit, or may be user defined.
     *
     * @param key
     * @param userDefinedKey
     * @param base
     * @param def
     * @return <code>Unit</code> object representative of the tags in the file.
     * @throws IOException
     *             if the<code>ProjLinearUnitsGeoKey</code> is not specified
     *             or if unit is user defined and
     *             <code>ProjLinearUnitSizeGeoKey</code> is either not defined
     *             or does not contain a number.
     */
    private Unit createUnit(final int key, final int userDefinedKey, final Unit base, final Unit def,
            final ValueMap metadata) throws IOException {
        final String unitCode = metadata.getAsString(key);

        // if not defined, return the default unit of measure
        if (unitCode == null) {
            return def;
        }

        // if specified, retrieve the appropriate unit code. There are two case
        // to keep into account, first case is when the unit of measure has an
        // EPSG code, alternatively it can be instantiated as a conversion from
        // meter.
        if (unitCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            try {
                final String unitSize = metadata.getAsString(userDefinedKey);

                // throw descriptive exception if required key is not there.
                if (unitSize == null) {
                    throw new IOException("Must define unit length when using a user defined unit");
                }

                double sz = Double.parseDouble(unitSize);
                return base.times(sz);
            } catch (NumberFormatException nfe) {
                throw new IOException(nfe);
            }
        } else {
            try {
                // using epsg code for this unit
                return epsgFactory.createUnit(String.valueOf(unitCode));
            } catch (FactoryException fe) {
                throw new IOException(fe);
            }
        }
    }

    private static String code(final GeneralParameterDescriptor desc){
        return desc.getName().getCode();
    }

    private static Map<String,?> name(final String name) {
        return Collections.singletonMap(IdentifiedObject.NAME_KEY, name);
    }
}
