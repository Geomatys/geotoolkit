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

package org.geotoolkit.metadata.geotiff;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import java.io.IOException;

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.internal.referencing.provider.LambertConformal2SP;
import org.apache.sis.internal.referencing.provider.LambertConformal1SP;
import org.apache.sis.internal.referencing.provider.Mercator1SP;
import org.apache.sis.internal.referencing.provider.Mercator2SP;
import org.apache.sis.internal.referencing.provider.PolarStereographicA;
import org.apache.sis.internal.referencing.provider.PolarStereographicB;
import org.apache.sis.internal.referencing.provider.TransverseMercator;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform;
import org.apache.sis.internal.referencing.ReferencingUtilities;

import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.referencing.operation.provider.Orthographic;
import org.geotoolkit.referencing.operation.provider.AlbersEqualArea;
import org.geotoolkit.referencing.operation.provider.ObliqueMercator;
import org.geotoolkit.referencing.operation.provider.Stereographic;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import static org.apache.sis.util.ArgumentChecks.*;

/**
 * Encode a CoordinateReferenceSystem as GeoTiff tags.
 *
 * note : with java 9 class will become only accessible by its module (no public signature on class header).
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class GeoTiffCRSWriter {

    /**
     * Fill the Geotiff CRS metadatas with the given CRS.
     */
    public void fillCRSMetaDatas(final GeoTiffMetaDataStack stack,
            CoordinateReferenceSystem crs) throws IOException, FactoryException {
        ensureNonNull("crs", crs);

        // extract 2D part from multidimensional CRS
        if (crs.getCoordinateSystem().getDimension() > 2) {
            try {
                crs = CRSUtilities.getCRS2D(crs);
            } catch (TransformException ex) {
                throw new IllegalStateException("Impossible to extract 2D part of coordinate referenceSystem", ex);
            }
        }

        final int crsType;
        if (crs instanceof ProjectedCRS) {
            crsType = ModelTypeProjected;
            fillProjectedCRSMetaDatas(stack, (ProjectedCRS) crs);
        } else if(crs instanceof GeocentricCRS) {
            crsType = ModelTypeGeocentric;
            fillGeocentricCRSMetaDatas(stack, (GeocentricCRS) crs);
        } else if(crs instanceof GeographicCRS) {
            crsType = ModelTypeGeographic;
            fillGeographicCRSMetaDatas(stack, (GeographicCRS) crs);
        } else {
            throw new IOException("GeoTiff only handle ProjectedCRS, GeocentricCRS or GeographicCRS. Can not support CRS : " + crs);
        }

        // add GTModelTypeGeoKey in GeoKeyDirectoryEntry
        stack.addShort(GTModelTypeGeoKey, crsType);
    }

    /**
     * Fill a projected CRS metadatas in geotiff tags.
     */
    private static void fillProjectedCRSMetaDatas(final GeoTiffMetaDataStack stack,
            final ProjectedCRS crs) throws IOException, FactoryException {

        //first see if it is an EPSG CRS
        final Integer code = getEPSGCode(crs);
        if (code != null) {
            //it is a usual EPSG, store it's code and return
            stack.addShort(ProjectedCSTypeGeoKey, code);
            return;
        }

        // user defined projected coordinate reference system.
        stack.addShort(ProjectedCSTypeGeoKey, GTUserDefinedGeoKey);

        // name of the user defined projected crs
        stack.addAscii(PCSCitationGeoKey, crs.getName().getCode());

        // projection
        fillProjection(stack, crs);

        // geographic crs
        fillGeoGCS(stack, crs.getBaseCRS());
    }

    /**
     * Fill a geographic CRS metadatas in geotiff tags.
     */
    private static void fillGeographicCRSMetaDatas(final GeoTiffMetaDataStack stack,
            final GeographicCRS crs) throws IOException, FactoryException {

        //first see if it is an EPSG CRS
        final Integer code = getEPSGCode(crs);
        if (code != null) {
            //it is a usual EPSG, store it's code and return
            stack.addShort(GeographicTypeGeoKey, code);
            return;
        }

        //it is a user defined CRS
        stack.addShort(GeographicTypeGeoKey, GTUserDefinedGeoKey);

        //use CRS code as a citation
        stack.addAscii(GeogCitationGeoKey, crs.getName().getCode());

        // geodetic datum
        final GeodeticDatum datum = crs.getDatum();
        fillDatum(stack, datum);

        // angular unit
        final Unit angularUnit = crs.getCoordinateSystem().getAxis(0).getUnit();
        fillUnit(stack, angularUnit, 0);

        // prime meridian
        fillPrimeMeridian(stack, datum.getPrimeMeridian());

        // linear unit
        final Unit linearUnit = datum.getEllipsoid().getAxisUnit();
        fillUnit(stack,linearUnit, 1);
    }

    /**
     * Fill a geocentric CRS metadatas in geotiff tags.
     */
    private static void fillGeocentricCRSMetaDatas(final GeoTiffMetaDataStack stack,
            final GeocentricCRS crs) throws IOException {
        throw new IOException("Not implemented.");
    }

    /**
     * Parsing ProjectionGeoKey 3074 for a <code>ProjectedCRS</code>.
     *
     * @param projectedCRS
     *            The <code>ProjectedCRS</code> to parse.
     * @param metadata
     */
    private static void fillProjection(final GeoTiffMetaDataStack stack,
            final ProjectedCRS projectedCRS) throws FactoryException, IOException {

        final Projection projection = projectedCRS.getConversionFromBase();

        //first see if it is an EPSG Projection
        final Integer code = getEPSGCode(projection);
        if (code != null) {
            //it is a usual EPSG, store it's code and return
            stack.addShort(ProjectionGeoKey, code);
            return;
        }

        // user defined projection
        stack.addShort(ProjectionGeoKey, GTUserDefinedGeoKey);
        stack.addAscii(PCSCitationGeoKey, projection.getName().getCode());

        final OperationMethod method = projection.getMethod();

        // looking for the parameters
        String name = method.getName().getCode();
        name = name.trim();
        name = name.replace(' ', '_');

        final MathTransform trs = projection.getMathTransform();

        if(trs instanceof AbstractMathTransform){
            final AbstractMathTransform mt = (AbstractMathTransform) projection.getMathTransform();
            final ParameterValueGroup parameters = mt.getParameterValues();

            // key 3075 and parameters
            fillCoordinateProjectionTransform(stack, parameters, name);
        }else{
            throw new IOException("Unsupported transform " + trs);
        }

        // parse linear unit
        fillLinearUnit(stack, projectedCRS);
    }

    /**
     * Parses a "GEOGCS" element. This element has the following pattern:
     *
     * <blockquote><code>
     * GEOGCS["<name>", <datum>, <prime meridian>, <angular unit>  {,<twin axes>} {,<authority>}]
     * </code></blockquote>
     */
    private static void fillGeoGCS(final GeoTiffMetaDataStack stack,
            final GeographicCRS geographicCRS) throws FactoryException {

        //first see if it is an EPSG geographic CRS
        final Integer code = getEPSGCode(geographicCRS);
        if (code != null) {
            //it is a usual EPSG, store it's code and return
            stack.addShort(GeographicTypeGeoKey,code);
            return;
        }

        // /////////////////////////////////////////////////////////////////////
        // User defined CRS
        // /////////////////////////////////////////////////////////////////////
        // user defined geographic coordinate reference system.
        stack.addShort(GeographicTypeGeoKey, GTUserDefinedGeoKey);

        // get the name of the gcs which will become a citation for the user
        // define crs
        stack.addAscii(GeogCitationGeoKey, geographicCRS.getName().getCode());

        // geodetic datum
        final GeodeticDatum datum = geographicCRS.getDatum();
        fillDatum(stack, datum);

        // angular unit
        final Unit angularUnit = geographicCRS.getCoordinateSystem().getAxis(0).getUnit();
        fillUnit(stack, angularUnit, 0);

        // prime meridian
        fillPrimeMeridian(stack, datum.getPrimeMeridian());

        // linear unit
        final Unit linearUnit = datum.getEllipsoid().getAxisUnit();
        fillUnit(stack, linearUnit, 1);
    }

    /**
     * Parses a "DATUM" element. This element has the following pattern:
     *
     * <blockquote><code>
     * DATUM["<name>", <spheroid> {,<to wgs84>} {,<authority>}]
     * </code></blockquote>
     */
    private static void fillDatum(final GeoTiffMetaDataStack stack, final GeodeticDatum datum) throws FactoryException {

        //first see if it is an EPSG Datum
        final Integer code = getEPSGCode(datum);
        if (code != null) {
            //it is a usual EPSG, store it's code and return
            stack.addShort(GeogGeodeticDatumGeoKey, code);
            return;
        }

        //it is a user defined CRS
        stack.addShort(GeogGeodeticDatumGeoKey, GTUserDefinedGeoKey);
        //set the name
        stack.addAscii(GeogCitationGeoKey, datum.getName().getCode());

        fillEllipsoid(stack, datum.getEllipsoid());
    }

    /**
     * Parses a "SPHEROID" element. This element has the following pattern:
     *
     * <blockquote><code>
     * SPHEROID["<name>", <semi-major axis>, <inverse flattening> {,<authority>}]
     * </code></blockquote>
     */
    private static void fillEllipsoid(final GeoTiffMetaDataStack stack, final Ellipsoid ellipsoid) throws FactoryException {

        //first see if it is an EPSG Elipsoid
        final Integer code = getEPSGCode(ellipsoid);
        if (code != null) {
            //it is a usual EPSG, store it's code and return
            stack.addShort(GeogEllipsoidGeoKey, code);
            return;
        }

        // user defined ellipsoid
        stack.addShort(GeogEllipsoidGeoKey, GTUserDefinedGeoKey);
        // setting the name
        stack.addAscii(GeogCitationGeoKey, ellipsoid.getName().getCode());
        // setting semimajor axis
        stack.addDouble(GeogSemiMajorAxisGeoKey, ellipsoid.getSemiMajorAxis());
        // setting inverse flattening
        stack.addDouble(GeogInvFlatteningGeoKey, ellipsoid.getInverseFlattening());
    }

    /**
     * Parses a "PRIMEM" element. This element has the following pattern:
     *
     * <blockquote><code>
     * PRIMEM["<name>", <longitude> {,<authority>}]
     * </code></blockquote>
     */
    private static void fillPrimeMeridian(final GeoTiffMetaDataStack stack, final PrimeMeridian pm) throws FactoryException {

        //first see if it is an EPSG Elipŝoid
        final Integer code = getEPSGCode(pm);
        if (code != null) {
            //it is a usual EPSG, store it's code and return
            stack.addShort(GeogPrimeMeridianGeoKey, code);
            return;
        }

        //user defined prime meridien
        stack.addShort(GeogPrimeMeridianGeoKey, GTUserDefinedGeoKey);
        //citation
        stack.addAscii(GeogCitationGeoKey, pm.getName().getCode());
        //longitude
        stack.addDouble(GeogPrimeMeridianLongGeoKey, pm.getGreenwichLongitude());
    }

    /**
     * Parses a along with coordinate transformation and its parameters.
     */
    private static void fillCoordinateProjectionTransform(final GeoTiffMetaDataStack stack,
            final ParameterValueGroup parameters, final String name) throws IOException{
        final String desc = parameters.getDescriptor().getName().getCode();

        // /////////////////////////////////////////////////////////////////////
        // Transverse Mercator
        // /////////////////////////////////////////////////////////////////////
        if (IdentifiedObjects.isHeuristicMatchForName(new TransverseMercator().getParameters(), desc)) {    // TODO: avoid creation of temporary object.
            // key 3075
            stack.addShort(ProjCoordTransGeoKey, CT_TransverseMercator);
            stack.addAscii(PCSCitationGeoKey, name);

            // params
            stack.addDouble(ProjNatOriginLongGeoKey,    value(parameters,TransverseMercator.LONGITUDE_OF_ORIGIN));
            stack.addDouble(ProjNatOriginLatGeoKey,     value(parameters,TransverseMercator.LATITUDE_OF_ORIGIN));
            stack.addDouble(ProjScaleAtNatOriginGeoKey, value(parameters,TransverseMercator.SCALE_FACTOR));
            stack.addDouble(ProjFalseEastingGeoKey,     value(parameters,TransverseMercator.FALSE_EASTING));
            stack.addDouble(ProjFalseNorthingGeoKey,    value(parameters,TransverseMercator.FALSE_NORTHING));
            return;
        }

        // /////////////////////////////////////////////////////////////////////
        // Mercator_1SP
        // Mercator_2SP
        // /////////////////////////////////////////////////////////////////////
        if (IdentifiedObjects.isHeuristicMatchForName(new Mercator2SP().getParameters(), desc) ||   // TODO: need an other way to check for match.
            IdentifiedObjects.isHeuristicMatchForName(new Mercator1SP().getParameters(), desc))
        {
            // key 3075
            stack.addShort(ProjCoordTransGeoKey, CT_Mercator);
            stack.addAscii(PCSCitationGeoKey, name);

            // params
            stack.addDouble(ProjNatOriginLongGeoKey,    value(parameters,Mercator1SP.LONGITUDE_OF_ORIGIN));
            //stack.addDouble(ProjNatOriginLatGeoKey,     value(parameters,Mercator1SP.LATITUDE_OF_ORIGIN));
            stack.addDouble(ProjScaleAtNatOriginGeoKey, value(parameters,Mercator1SP.SCALE_FACTOR));
            stack.addDouble(ProjFalseEastingGeoKey,     value(parameters,Mercator1SP.FALSE_EASTING));
            stack.addDouble(ProjFalseNorthingGeoKey,    value(parameters,Mercator1SP.FALSE_NORTHING));
            return;
        }

        // /////////////////////////////////////////////////////////////////////
        // Lamber conformal 1sp
        // /////////////////////////////////////////////////////////////////////
        if (IdentifiedObjects.isHeuristicMatchForName(new LambertConformal1SP().getParameters(), desc)) {   // TODO: need an other way to check for match.
            // key 3075
            stack.addShort(ProjCoordTransGeoKey, CT_LambertConfConic_Helmert);
            stack.addAscii(PCSCitationGeoKey, name);

            // params
            stack.addDouble(ProjNatOriginLongGeoKey,    value(parameters,LambertConformal1SP.LONGITUDE_OF_ORIGIN));
            stack.addDouble(ProjNatOriginLatGeoKey,     value(parameters,LambertConformal1SP.LATITUDE_OF_ORIGIN));
            stack.addDouble(ProjScaleAtNatOriginGeoKey, value(parameters,LambertConformal1SP.SCALE_FACTOR));
            stack.addDouble(ProjFalseEastingGeoKey,     value(parameters,LambertConformal1SP.FALSE_EASTING));
            stack.addDouble(ProjFalseNorthingGeoKey,    value(parameters,LambertConformal1SP.FALSE_NORTHING));
            return;
        }

        // /////////////////////////////////////////////////////////////////////
        // LAMBERT_CONFORMAL_CONIC_2SP
        // lambert_conformal_conic_2SP_Belgium
        // /////////////////////////////////////////////////////////////////////
        if (IdentifiedObjects.isHeuristicMatchForName(new LambertConformal2SP().getParameters(), desc)) {   // TODO: need an other way to check for match.
            // key 3075
            stack.addShort(ProjCoordTransGeoKey, CT_LambertConfConic_2SP);
            stack.addAscii(PCSCitationGeoKey, name);

            // params
            stack.addDouble(ProjNatOriginLongGeoKey, value(parameters,LambertConformal2SP.LONGITUDE_OF_FALSE_ORIGIN));
            stack.addDouble(ProjNatOriginLatGeoKey,  value(parameters,LambertConformal2SP.LATITUDE_OF_FALSE_ORIGIN));
            stack.addDouble(ProjStdParallel1GeoKey,  value(parameters,LambertConformal2SP.STANDARD_PARALLEL_1));
            stack.addDouble(ProjStdParallel2GeoKey,  value(parameters,LambertConformal2SP.STANDARD_PARALLEL_2));
            stack.addDouble(ProjFalseEastingGeoKey,  value(parameters,LambertConformal2SP.FALSE_EASTING));
            stack.addDouble(ProjFalseNorthingGeoKey, value(parameters,LambertConformal2SP.FALSE_NORTHING));
            return;
        }

        // /////////////////////////////////////////////////////////////////////
        // stereographic
        // /////////////////////////////////////////////////////////////////////
        if (IdentifiedObjects.isHeuristicMatchForName(Stereographic.PARAMETERS, desc)) {
            // key 3075
            stack.addShort(ProjCoordTransGeoKey, CT_Stereographic);
            stack.addAscii(PCSCitationGeoKey, name);

            // params
            stack.addDouble(ProjNatOriginLongGeoKey,    value(parameters,Stereographic.CENTRAL_MERIDIAN));
            stack.addDouble(ProjNatOriginLatGeoKey,     value(parameters,Stereographic.LATITUDE_OF_ORIGIN));
            stack.addDouble(ProjScaleAtNatOriginGeoKey, value(parameters,Stereographic.SCALE_FACTOR));
            stack.addDouble(ProjFalseEastingGeoKey,     value(parameters,Stereographic.FALSE_EASTING));
            stack.addDouble(ProjFalseNorthingGeoKey,    value(parameters,Stereographic.FALSE_NORTHING));
            return;
        }

        // /////////////////////////////////////////////////////////////////////
        // polar_stereographic varian B
        // /////////////////////////////////////////////////////////////////////
        if (IdentifiedObjects.isHeuristicMatchForName(new PolarStereographicB().getParameters(), desc)) {   // TODO: need an other way to check for match.
            // key 3075
            stack.addShort(ProjCoordTransGeoKey, CT_PolarStereographic);
            stack.addAscii(PCSCitationGeoKey, name);

            // params
            stack.addDouble(ProjStraightVertPoleLongGeoKey, value(parameters,PolarStereographicB.LONGITUDE_OF_ORIGIN));
            stack.addDouble(ProjStdParallel1GeoKey,         value(parameters,PolarStereographicB.STANDARD_PARALLEL));
            stack.addDouble(ProjFalseEastingGeoKey,         value(parameters,PolarStereographicB.FALSE_EASTING));
            stack.addDouble(ProjFalseNorthingGeoKey,        value(parameters,PolarStereographicB.FALSE_NORTHING));
            return;
        }

        // /////////////////////////////////////////////////////////////////////
        // polar_stereographic Varian A
        // /////////////////////////////////////////////////////////////////////
        if (IdentifiedObjects.isHeuristicMatchForName(new PolarStereographicA().getParameters(), desc)) {   // TODO: need an other way to check for match.
            // key 3075
            stack.addShort(ProjCoordTransGeoKey, CT_PolarStereographic);
            stack.addAscii(PCSCitationGeoKey, name);

            // params
            stack.addDouble(ProjNatOriginLongGeoKey,        value(parameters,PolarStereographicA.LONGITUDE_OF_ORIGIN));
            stack.addDouble(ProjStraightVertPoleLongGeoKey, value(parameters,PolarStereographicA.LATITUDE_OF_ORIGIN));
            stack.addDouble(ProjScaleAtNatOriginGeoKey,     value(parameters,PolarStereographicA.SCALE_FACTOR));
            stack.addDouble(ProjFalseEastingGeoKey,         value(parameters,PolarStereographicA.FALSE_EASTING));
            stack.addDouble(ProjFalseNorthingGeoKey,        value(parameters,PolarStereographicA.FALSE_NORTHING));
            return;
        }

        // /////////////////////////////////////////////////////////////////////
        // Oblique Mercator
        // /////////////////////////////////////////////////////////////////////
        if (IdentifiedObjects.isHeuristicMatchForName(ObliqueMercator.PARAMETERS, desc)) {
            // key 3075
            stack.addShort(ProjCoordTransGeoKey, CT_ObliqueMercator);
            stack.addAscii(PCSCitationGeoKey, name);

            // params
            stack.addDouble(ProjCenterLongGeoKey,    value(parameters,ObliqueMercator.LONGITUDE_OF_CENTRE));
            stack.addDouble(ProjCenterLatGeoKey,     value(parameters,ObliqueMercator.LATITUDE_OF_CENTRE));
            stack.addDouble(ProjScaleAtCenterGeoKey, value(parameters,ObliqueMercator.SCALE_FACTOR));
            stack.addDouble(ProjFalseEastingGeoKey,  value(parameters,ObliqueMercator.FALSE_EASTING));
            stack.addDouble(ProjAzimuthAngleGeoKey,  value(parameters,ObliqueMercator.AZIMUTH));
            stack.addDouble(ProjFalseNorthingGeoKey, value(parameters,ObliqueMercator.FALSE_NORTHING));
            // rectified grid angle???
            return;
        }

        // /////////////////////////////////////////////////////////////////////
        // albers_Conic_Equal_Area
        // /////////////////////////////////////////////////////////////////////
        if (IdentifiedObjects.isHeuristicMatchForName(AlbersEqualArea.PARAMETERS, desc)) {
            // key 3075
            stack.addShort(ProjCoordTransGeoKey, CT_AlbersEqualArea);
            stack.addAscii(PCSCitationGeoKey, name);

            // params
            stack.addDouble(ProjNatOriginLongGeoKey, parameters.parameter("longitude_of_center").doubleValue()); //TODO no direct match found ?
            stack.addDouble(ProjNatOriginLatGeoKey,  parameters.parameter("latitude_of_center").doubleValue()); //TODO no direct match found ?
            stack.addDouble(ProjFalseEastingGeoKey,  value(parameters,AlbersEqualArea.FALSE_EASTING));
            stack.addDouble(ProjFalseNorthingGeoKey, value(parameters,AlbersEqualArea.FALSE_NORTHING));
            stack.addDouble(ProjStdParallel1GeoKey,  value(parameters,AlbersEqualArea.STANDARD_PARALLEL_1));
            stack.addDouble(ProjStdParallel2GeoKey,  value(parameters,AlbersEqualArea.STANDARD_PARALLEL_2));
            // rectified grid angle???
            return;
        }

        // /////////////////////////////////////////////////////////////////////
        // Orthographic
        // /////////////////////////////////////////////////////////////////////
        if (IdentifiedObjects.isHeuristicMatchForName(Orthographic.PARAMETERS, desc)) {
            // key 3075
            stack.addShort(ProjCoordTransGeoKey, CT_Orthographic);
            stack.addAscii(PCSCitationGeoKey, name);

            // params
            stack.addDouble(ProjCenterLongGeoKey,    value(parameters,Orthographic.LONGITUDE_OF_CENTRE));
            stack.addDouble(ProjCenterLatGeoKey,     value(parameters,Orthographic.LATITUDE_OF_CENTRE));
            stack.addDouble(ProjFalseEastingGeoKey,  value(parameters,Orthographic.FALSE_EASTING));
            stack.addDouble(ProjFalseNorthingGeoKey, value(parameters,Orthographic.FALSE_NORTHING));
            return;
        }

        throw new IOException("Could not find match for projection " + name);
    }

    /**
     * Parses a linear unit for a <code>ProjectedCRS</code>.
     *
     * @todo complete the list of linear unit of measures and clean the
     *       exception
     * @param projectedCRS
     * @param metadata
     */
    private static void fillLinearUnit(final GeoTiffMetaDataStack stack, final ProjectedCRS projectedCRS) {

        // getting the linear unit
        final Unit linearUnit = ReferencingUtilities.getUnit(projectedCRS.getCoordinateSystem());
        if (linearUnit != null && !SI.METRE.isCompatible(linearUnit)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.NonLinearUnit_1, linearUnit));
        }
        if (SI.METRE.isCompatible(linearUnit)) {
            if (SI.METRE.equals(linearUnit)) {
                stack.addShort(ProjLinearUnitsGeoKey,Linear_Meter);
                stack.addDouble(ProjLinearUnitSizeGeoKey, 1.0);
            }
            if (NonSI.NAUTICAL_MILE.equals(linearUnit)) {
                stack.addShort(ProjLinearUnitsGeoKey,Linear_Mile_International_Nautical);
                stack.addDouble(ProjLinearUnitSizeGeoKey, linearUnit.getConverterTo(SI.METRE).convert(1));
            }
            if (NonSI.FOOT.equals(linearUnit)) {
                stack.addShort(ProjLinearUnitsGeoKey,  Linear_Foot);
                stack.addDouble(ProjLinearUnitSizeGeoKey, linearUnit.getConverterTo(SI.METRE).convert(1));
            }
            if (NonSI.YARD.equals(linearUnit)) {
                stack.addShort(ProjLinearUnitsGeoKey,Linear_Yard_Sears);// ??
                stack.addDouble(ProjLinearUnitSizeGeoKey, linearUnit.getConverterTo(SI.METRE).convert(1));
            }
        }
    }

    /**
     * Parses an "UNIT" element. This element has the following pattern:
     *
     * <blockquote><code>
     * UNIT["<name>", <conversion factor> {,<authority>}]
     * </code></blockquote>
     */
    private static void fillUnit(final GeoTiffMetaDataStack stack, final Unit unit, final int model) {

        // user defined unit
        stack.addShort(
                (model == 0) ? GeogAngularUnitsGeoKey : ProjLinearUnitsGeoKey,
                GTUserDefinedGeoKey);

        try {
            // citation
            stack.addAscii(GeogCitationGeoKey, unit.toString());
        } catch(Exception ex) {
            // do nothing unit is unamed. Adding citation is useless.
        }


        final Unit base;
        if (SI.METRE.isCompatible(unit)) {
            base = SI.METRE;
        } else if (SI.SECOND.isCompatible(unit)) {
            base = SI.SECOND;
        } else if (SI.RADIAN.isCompatible(unit) && !Unit.ONE.equals(unit)) {
            base = SI.RADIAN;
        } else {
            base = null;
        }

        if (base != null) {
            stack.addDouble(
                    model == 0 ? GeogAngularUnitSizeGeoKey
                    : GeogLinearUnitSizeGeoKey, unit.getConverterTo(base).convert(1));
        } else {
            stack.addDouble(
                    model == 0 ? GeogAngularUnitSizeGeoKey
                    : GeogLinearUnitSizeGeoKey, 1);
        }
    }


    /**
     * Searches for an EPSG code inside this <code>IdentifiedObject</code>.
     * If it can not be found, this method will search in the EPSG authority
     * for an equivalant object and return it's identifier
     *
     * @return EPSG numeric code, if one is found, null otherwise.
     */
    private static Integer getEPSGCode(final IdentifiedObject candidate) throws FactoryException {
        // looking for an EPSG code
        for(final Identifier rid : candidate.getIdentifiers()){
            final Citation citation = rid.getAuthority();
            if(org.apache.sis.metadata.iso.citation.Citations.identifierMatches(citation, Citations.EPSG)){
                return Integer.parseInt(rid.getCode());
            }
        }

        //search for an IdentifiedObject with the same definition
        return org.apache.sis.referencing.IdentifiedObjects.lookupEPSG(candidate);
    }

    private static double value(final ParameterValueGroup parameters, final ParameterDescriptor desc){
        return parameters.parameter(desc.getName().getCode()).doubleValue();
    }

}
