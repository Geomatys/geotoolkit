/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.postgresql.epsg;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.sql.DataSource;
import org.geotoolkit.coverage.postgresql.PGCoverageStore;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.Projection;
import org.opengis.util.FactoryException;
import static org.geotoolkit.coverage.postgresql.epsg.PGEPSGQueries.*;
import org.geotoolkit.referencing.EPSGWriter;
import org.geotoolkit.referencing.datum.AbstractDatum;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.geotoolkit.util.ComparisonMode;
import org.opengis.metadata.extent.Extent;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.cs.VerticalCS;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.referencing.datum.VerticalDatum;

/**
 * EPSG Writer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class PGEPSGWriter implements EPSGWriter {
    
    private final PGCoverageStore store;
    private final DataSource source;
    private final ThreadedEpsgFactory factory;

    public PGEPSGWriter(final PGCoverageStore store) throws SQLException {
        this.store = store;
        this.source = store.getDataSource();
        this.factory = store.getEPSGFactory();
    }
    
    /**
     * Use sequence in database to ensure having a unique code.
     * @return next free epsg code in range >= 32768 et < 60 000 000
     */
    private int getNextCode() throws SQLException{
        Connection cnx = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            cnx = source.getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.prepareStatement(NEXT_CODE.query());
            rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }
        
    }
    
    private String searchSimilar(Class clazz, IdentifiedObject candidate) throws FactoryException{
        final IdentifiedObjectFinder finder = factory.getIdentifiedObjectFinder(clazz);
        finder.setComparisonMode(ComparisonMode.APPROXIMATIVE);
        final String code = finder.findIdentifier(candidate);
        return code;
    }
    
    @Override
    public int getOrCreateCoordinateReferenceSystem(final CoordinateReferenceSystem candidate) throws FactoryException{
        
        //search if this object already exist
        final String code = searchSimilar(CoordinateReferenceSystem.class, candidate);
        if(code != null){
            return codeToID(code);
        }
        
        //all requiered parameters
        final Integer coord_ref_sys_code;
        final String coord_ref_sys_name = candidate.getName().getCode();
        final String coord_ref_sys_kind;
        final Integer coord_sys_code;
        final Integer datum_code;
        final Integer source_geogcrs_code;
        final Integer projection_conv_code;
        final Integer cmpd_horizcrs_code;
        final Integer cmpd_vertcrs_code;
        final String crs_scope = (candidate.getScope() == null)? "" : candidate.getScope().toString();
        final String remarks = (candidate.getRemarks() == null)? null : candidate.getRemarks().toString();
        final String information_source = (candidate.getName().getCodeSpace()==null)? null : candidate.getName().getCodeSpace();
        final String data_source = (candidate.getName().getCodeSpace()==null)? "" : candidate.getName().getCodeSpace();
        final Date revision_date = new Date(System.currentTimeMillis());
        final String change_id = "";
        final Integer show_crs = 1;
        final Integer deprecated = 0;
        
        if(candidate instanceof CompoundCRS){
            coord_ref_sys_kind = "compound";
            coord_sys_code = null;
            datum_code = null;
            source_geogcrs_code = null;
            projection_conv_code = null;
            
            final CompoundCRS compound = (CompoundCRS) candidate;
            final List<CoordinateReferenceSystem> parts = compound.getComponents();
            
            cmpd_horizcrs_code = getOrCreateCoordinateReferenceSystem(parts.get(0));
            if(parts.size() == 2){
                cmpd_vertcrs_code = getOrCreateCoordinateReferenceSystem(parts.get(1));
            }else{
                //we can only aggregate crs two by two
                final CoordinateReferenceSystem[] toSplit = new CoordinateReferenceSystem[parts.size()-1];
                for(int i=1;i<parts.size();i++){
                    toSplit[i-1] = parts.get(i);
                }
                final CompoundCRS secondPart = new DefaultCompoundCRS("Split-"+candidate.getName().getCode(), toSplit);
                cmpd_vertcrs_code = getOrCreateCoordinateReferenceSystem(secondPart);
            }
            
        }else{
            cmpd_horizcrs_code = null;
            cmpd_vertcrs_code = null;
            
            final CoordinateSystem cs = candidate.getCoordinateSystem();
            coord_sys_code = getOrCreateCoordinateSystem(cs);

            if(candidate instanceof GeographicCRS){
                final GeographicCRS geocrs = (GeographicCRS) candidate;
                coord_ref_sys_kind = "geographic 2D";
                datum_code = getOrCreateDatum(geocrs.getDatum());
                source_geogcrs_code = null; //TODO this can be non-null, when ?
                projection_conv_code = null; //TODO this can be non-null, when ?
                
            }else if(candidate instanceof ProjectedCRS){
                final ProjectedCRS projcrs = (ProjectedCRS) candidate;
                coord_ref_sys_kind = "projected";
                datum_code = null;
                source_geogcrs_code = getOrCreateCoordinateReferenceSystem(projcrs.getBaseCRS());
                projection_conv_code = getOrCreateProjection(projcrs.getConversionFromBase());
                
            }else if(candidate instanceof TemporalCRS){
                final TemporalCRS tempcrs = (TemporalCRS) candidate;
                coord_ref_sys_kind = "temporal";
                datum_code = getOrCreateDatum(tempcrs.getDatum());
                source_geogcrs_code = null;
                projection_conv_code = null;
                
            }else if(candidate instanceof VerticalCRS){
                final VerticalCRS vertcrs = (VerticalCRS) candidate;
                coord_ref_sys_kind = "vertical";
                datum_code = getOrCreateDatum(vertcrs.getDatum());
                source_geogcrs_code = null;
                projection_conv_code = null;
                
            }else if(candidate instanceof SingleCRS){
                final SingleCRS singlecrs = (SingleCRS) candidate;
                coord_ref_sys_kind = "single";
                datum_code = getOrCreateDatum(singlecrs.getDatum());
                source_geogcrs_code = null;
                projection_conv_code = null;
                
            }else{
                throw new FactoryException("Can not store given crs : " +candidate);
            }
            
        }
        
        
        //save object
        Connection cnx = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{            
            cnx = source.getConnection();
            
            //find similar
            final Integer similar = findSimilar(cnx, FIND_COORDINATE_REFERENCE_SYSTEM, 
                    coord_ref_sys_kind,coord_sys_code,datum_code,source_geogcrs_code,
                    projection_conv_code,cmpd_horizcrs_code,cmpd_vertcrs_code);
            if(similar != null){ return similar; }
            
            
            final Integer area_of_use_code = getOrCreateArea(candidate.getDomainOfValidity());
            
            cnx.setReadOnly(false);
            coord_ref_sys_code = getNextCode();
            stmt = cnx.prepareStatement(CREATE_COORDINATE_REFERENCE_SYSTEM.query());
            CREATE_COORDINATE_REFERENCE_SYSTEM.fillStatement(stmt, 
                    coord_ref_sys_code,
                    coord_ref_sys_name,
                    area_of_use_code,
                    coord_ref_sys_kind,
                    coord_sys_code,
                    datum_code,
                    source_geogcrs_code,
                    projection_conv_code,
                    cmpd_horizcrs_code,
                    cmpd_vertcrs_code,
                    crs_scope,
                    remarks,
                    information_source,
                    data_source,
                    revision_date,
                    change_id,
                    show_crs,
                    deprecated);
            stmt.executeUpdate();
        }catch(SQLException ex){
            throw new FactoryException(ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }
        
        return coord_ref_sys_code;
    }
    
    @Override
    public int getOrCreateCoordinateSystem(final CoordinateSystem candidate) throws FactoryException{
        
        //search if this object already exist
        final String code = searchSimilar(CoordinateSystem.class, candidate);
        if(code != null){
            return codeToID(code);
        }
        
        final Integer coord_sys_code;
        final String coord_sys_name = candidate.getName().getCode();
        final String coord_sys_type = getCoordinateSystemType(candidate);
        final Integer dimension = candidate.getDimension();
        final String remarks = (candidate.getRemarks() == null)? null : candidate.getRemarks().toString();
        final String information_source = (candidate.getName().getCodeSpace()==null)? null : candidate.getName().getCodeSpace();
        final String data_source = (candidate.getName().getCodeSpace()==null)? "" : candidate.getName().getCodeSpace();
        final Date revision_date = new Date(System.currentTimeMillis());
        final String change_id = "";
        final Integer deprecated = 0;
        
        
        //save object
        Connection cnx = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{            
            cnx = source.getConnection();
            
            //find similar
            final Integer similar = findSimilarCoordinateSystem(candidate, cnx);
            if(similar != null){ return similar; }
                        
            cnx.setReadOnly(false);
            coord_sys_code = getNextCode();
            stmt = cnx.prepareStatement(CREATE_COORDINATE_SYSTEM.query());
            CREATE_COORDINATE_SYSTEM.fillStatement(stmt, 
                    coord_sys_code,
                    coord_sys_name,
                    coord_sys_type,
                    dimension,
                    remarks,
                    information_source,
                    data_source,
                    revision_date,
                    change_id,
                    deprecated
                    );
            stmt.executeUpdate();
        }catch(SQLException ex){
            throw new FactoryException(ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }
        
        //generate axis
        for(int i=0;i<dimension;i++){
            final CoordinateSystemAxis axis = candidate.getAxis(i);
            createCoordinateSystemAxis(coord_sys_code,i+1,axis);
        }
        
        return coord_sys_code;
    }
    
    private int createCoordinateSystemAxis(final int csid, final int axisOrder, 
            final CoordinateSystemAxis candidate) throws FactoryException{
                
        final Integer coord_axis_code;
        final Integer coord_sys_code = csid;
        final Integer coord_axis_name_code = createCoordinateSystemAxisName(candidate);
        final String coord_axis_orientation = candidate.getDirection().name();
        final String coord_axis_abbreviation = candidate.getAbbreviation();
        final Integer uom_code = getOrCreateUOM(candidate.getUnit());
        final Integer coord_axis_order = axisOrder;
        
        //save object
        Connection cnx = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{          
            coord_axis_code = getNextCode();  
            
            cnx = source.getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.prepareStatement(CREATE_COORDINATE_AXIS.query());
            CREATE_COORDINATE_AXIS.fillStatement(stmt, 
                    coord_axis_code,
                    coord_sys_code,
                    coord_axis_name_code,
                    coord_axis_orientation,
                    coord_axis_abbreviation,
                    uom_code,
                    coord_axis_order
                    );
            stmt.executeUpdate();
        }catch(SQLException ex){
            throw new FactoryException(ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }
        
        return coord_axis_code;
    }
    
    private int createCoordinateSystemAxisName(final CoordinateSystemAxis candidate) throws FactoryException{
                
        final Integer coord_axis_name_code;
        final String coord_axis_name = candidate.getName().getCode();
        final String description = (candidate.getRemarks() == null)? null : candidate.getRemarks().toString();
        final String remarks = (candidate.getRemarks() == null)? null : candidate.getRemarks().toString();
        final String information_source = (candidate.getName().getCodeSpace()==null)? null : candidate.getName().getCodeSpace();
        final String data_source = (candidate.getName().getCodeSpace()==null)? "" : candidate.getName().getCodeSpace();
        final Date revision_date = new Date(System.currentTimeMillis());
        final String change_id = "";
        final Integer deprecated = 0;
        
        //save object
        Connection cnx = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            coord_axis_name_code = getNextCode();
            
            cnx = source.getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.prepareStatement(CREATE_COORDINATE_AXIS_NAME.query());
            CREATE_COORDINATE_AXIS_NAME.fillStatement(stmt, 
                    coord_axis_name_code,
                    coord_axis_name,
                    description,
                    remarks,
                    information_source,
                    data_source,
                    revision_date,
                    change_id,
                    deprecated
                    );
            stmt.executeUpdate();
        }catch(SQLException ex){
            throw new FactoryException(ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }
        
        return coord_axis_name_code;
    }
    
    @Override
    public int getOrCreateDatum(final Datum candidate) throws FactoryException{
        
        //search if this object already exist
        final String code = searchSimilar(Datum.class, candidate);
        if(code != null){
            return codeToID(code);
        }
        
        final Integer datum_code;
        final String datum_name = candidate.getName().getCode();
        final String datum_type;
        final String origin_description;
        final Integer realization_epoch;
        final Integer ellipsoid_code;
        final Integer prime_meridian_code;
        final String datum_scope = (candidate.getRemarks() == null)? "" : candidate.getRemarks().toString();
        final String remarks = (candidate.getRemarks() == null)? null : candidate.getRemarks().toString();
        final String information_source = (candidate.getName().getCodeSpace()==null)? null : candidate.getName().getCodeSpace();
        final String data_source = (candidate.getName().getCodeSpace()==null)? "" : candidate.getName().getCodeSpace();
        final Date revision_date = new Date(System.currentTimeMillis());
        final String change_id = "";
        final Integer deprecated = 0;
        
        if(candidate.getRealizationEpoch() == null){
            realization_epoch = null;
        }else{
            final Calendar c = Calendar.getInstance();
            c.setTime(candidate.getRealizationEpoch());
            realization_epoch = c.get(Calendar.YEAR);
        }
        
        if(candidate instanceof GeodeticDatum){
            final GeodeticDatum gd = (GeodeticDatum) candidate;
            origin_description = "";
            datum_type = "geodetic";
            ellipsoid_code = getOrCreateEllipsoid(gd.getEllipsoid());
            prime_meridian_code = getOrCreatePrimeMeridian(gd.getPrimeMeridian());
            
        }else if(candidate instanceof VerticalDatum){
            final VerticalDatum vd = (VerticalDatum) candidate;
            origin_description = "";
            datum_type = "vertical";
            ellipsoid_code = null;
            prime_meridian_code = null;
            
        }else if(candidate instanceof TemporalDatum){
            final TemporalDatum td = (TemporalDatum) candidate;
            origin_description = TemporalUtilities.toISO8601Z(td.getOrigin(), null);
            datum_type = "temporal";
            ellipsoid_code = null;
            prime_meridian_code = null;
        }else if(candidate instanceof AbstractDatum){
            final AbstractDatum td = (AbstractDatum) candidate;
            origin_description = "";
            datum_type = "abstract";
            ellipsoid_code = null;
            prime_meridian_code = null;
        }else {
            throw new FactoryException("Can not store given datum : " +candidate);
        }
        
        //save object
        Connection cnx = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{            
            cnx = source.getConnection();
            
            //find similar
            final Integer similar = findSimilar(cnx, FIND_DATUM, datum_type,origin_description,ellipsoid_code,prime_meridian_code);
            if(similar != null){ return similar; }
                        
            final Integer area_of_use_code = getOrCreateArea(candidate.getDomainOfValidity());
            
            cnx.setReadOnly(false);
            datum_code = getNextCode();
            stmt = cnx.prepareStatement(CREATE_DATUM.query());
            CREATE_DATUM.fillStatement(stmt, 
                    datum_code,
                    datum_name,
                    datum_type,
                    origin_description,
                    realization_epoch,
                    ellipsoid_code,
                    prime_meridian_code,
                    area_of_use_code,
                    datum_scope,
                    remarks,
                    information_source,
                    data_source,
                    revision_date,
                    change_id,
                    deprecated
                    );
            stmt.executeUpdate();
        }catch(SQLException ex){
            throw new FactoryException(ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }
        
        return datum_code;
    }
    
    @Override
    public int getOrCreateProjection(final Projection candidate) throws FactoryException{
        
        //search if this object already exist
        final String code = searchSimilar(Projection.class, candidate);
        if(code != null){
            return codeToID(code);
        }
        
        throw new FactoryException("No supported yet.");
        
//        final ParameterValueGroup parameters = candidate.getParameterValues();
//        
//        final Integer coord_op_code;
//        final String coord_op_name;
//        final String coord_op_type;
//        final Integer source_crs_code;
//        final Integer target_crs_code;
//        final String coord_tfm_version;
//        final Integer coord_op_variant;
//        final Integer area_of_use_code;
//        final String coord_op_scope;
//        final Double  coord_op_accuracy;
//        final Integer coord_op_method_code;
//        final Integer uom_code_source_coord_diff;
//        final Integer uom_code_target_coord_diff;
//        final String remarks = (candidate.getRemarks() == null)? null : candidate.getRemarks().toString();
//        final String information_source = (candidate.getName().getCodeSpace()==null)? null : candidate.getName().getCodeSpace();
//        final String data_source = (candidate.getName().getCodeSpace()==null)? "" : candidate.getName().getCodeSpace();
//        final Date revision_date = new Date(System.currentTimeMillis());
//        final String change_id = "";
//        final Integer show_operation = 1;
//        final Integer deprecated = 0;
//        
//        //save object
//        Connection cnx = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        try{
//            coord_op_code = getNextCode();
//            
//            cnx = source.getConnection();
//            stmt = cnx.prepareStatement(CREATE_COORDINATE_OPERATION.query());
//            CREATE_COORDINATE_OPERATION.fillStatement(stmt, 
//                    coord_op_code,
//                    coord_op_name,
//                    coord_op_type,
//                    source_crs_code,
//                    target_crs_code,
//                    coord_tfm_version,
//                    coord_op_variant,
//                    area_of_use_code,
//                    coord_op_scope,
//                    coord_op_accuracy,
//                    coord_op_method_code,
//                    uom_code_source_coord_diff,
//                    uom_code_target_coord_diff,
//                    remarks,
//                    information_source,
//                    data_source,
//                    revision_date,
//                    change_id,
//                    show_operation,
//                    deprecated
//                    );
//            stmt.executeUpdate();
//        }catch(SQLException ex){
//            throw new FactoryException(ex);
//        }finally{
//            store.closeSafe(cnx, stmt, rs);
//        }
//        
//        return coord_op_code;
    }
    
    @Override
    public int getOrCreateArea(final Extent candidate) throws FactoryException{
        if(candidate == null){
            //world extent
            return 1262;
        }
        
        if(candidate instanceof IdentifiedObject){
            final IdentifiedObject ie = (IdentifiedObject) candidate;
            final String code = searchSimilar(ie.getClass(), ie);
            if(code != null){
                return codeToID(code);
            }
        }
        
        //TODO save object, return code
        //return world extent for now
        return 1262;
    }
    
    @Override
    public int getOrCreateUOM(final Unit candidate) throws FactoryException{
        
        final Integer uom_code;
        final String unit_of_meas_name = candidate.toString();
        final String unit_of_meas_type;
        Integer target_uom_code = null; 
        final Double factor_b = 1d; //TODO must check if unit is derivate and extract scale
        final Double factor_c = 1d; //TODO must check if unit is derivate and extract offset
        final String remarks = "";
        final String information_source = null;
        final String data_source = "";
        final Date revision_date = new Date(System.currentTimeMillis());
        final String change_id = "";
        final Integer deprecated = 0;
        
        //save object
        Connection cnx = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{

            final boolean selfDefined;
            if(candidate.isCompatible(SI.METRE)){
                selfDefined = false;
                target_uom_code = 9001;
                unit_of_meas_type = "length";
            }else if(candidate.isCompatible(SI.RADIAN)){
                selfDefined = false;
                target_uom_code = 9102;
                unit_of_meas_type = "angle";
            }else if(candidate.isCompatible(SI.SECOND)){
                selfDefined = true;
                //refer to self, epsg does not contain any temporal unit we could refer to
                unit_of_meas_type = "temp";            
            }else{
                selfDefined = true;
                //refer to self
                unit_of_meas_type = "unknown";      
            }
        
            cnx = source.getConnection();
            
            //find similar
            final Integer similar;
            if(selfDefined){
                similar = findSimilar(cnx, FIND_UNIT_OF_MEASURE_SELF, unit_of_meas_name, unit_of_meas_type,factor_b,factor_c);
            }else{
                similar = findSimilar(cnx, FIND_UNIT_OF_MEASURE, unit_of_meas_name, unit_of_meas_type,target_uom_code,factor_b,factor_c);
            }
            if(similar != null){ return similar; }
            
            cnx.setReadOnly(false);
            uom_code = getNextCode();
            if(selfDefined){ 
                target_uom_code = uom_code; 
            }
            
            stmt = cnx.prepareStatement(CREATE_UNIT_OF_MEASURE.query());
            CREATE_UNIT_OF_MEASURE.fillStatement(stmt, 
                    uom_code,
                    unit_of_meas_name,
                    unit_of_meas_type,
                    target_uom_code,
                    factor_b,
                    factor_c,
                    remarks,
                    information_source,
                    data_source,
                    revision_date,
                    change_id,
                    deprecated
                    );
            stmt.executeUpdate();
        }catch(SQLException ex){
            throw new FactoryException(ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }
        
        return uom_code;
    }
 
    @Override
    public int getOrCreateEllipsoid(final Ellipsoid candidate) throws FactoryException{
        
        final String code = searchSimilar(Ellipsoid.class, candidate);
        if(code != null){
            return codeToID(code);
        }
        
        final Integer ellipsoid_code;
        final String ellipsoid_name = candidate.getName().getCode();
        final Double semi_major_axis = candidate.getSemiMajorAxis();
        final Integer uom_code = getOrCreateUOM(candidate.getAxisUnit());
        final Double inv_flattening = candidate.getInverseFlattening();
        final Double semi_minor_axis = candidate.getSemiMinorAxis();
        final Integer ellipsoid_shape = 1; //TODO how do we know that ?
        final String remarks = (candidate.getRemarks()==null)? "" : candidate.getRemarks().toString();
        final String information_source = (candidate.getName().getCodeSpace()==null)? null : candidate.getName().getCodeSpace();
        final String data_source = (candidate.getName().getCodeSpace()==null)? "" : candidate.getName().getCodeSpace();
        final Date revision_date = new Date(System.currentTimeMillis());
        final String change_id = "";
        final Integer deprecated = 0;
        
        //save object
        Connection cnx = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            
            cnx = source.getConnection();
            
            //find similar
            final Integer similar = findSimilar(cnx, FIND_ELLIPSOID, semi_major_axis,uom_code,inv_flattening,semi_minor_axis,ellipsoid_shape);
            if(similar != null){ return similar; }
            
            cnx.setReadOnly(false);
            ellipsoid_code = getNextCode();
            stmt = cnx.prepareStatement(CREATE_UNIT_OF_MEASURE.query());
            CREATE_UNIT_OF_MEASURE.fillStatement(stmt, 
                    ellipsoid_code,
                    ellipsoid_name,
                    semi_major_axis,
                    uom_code,
                    inv_flattening,
                    semi_minor_axis,
                    ellipsoid_shape,
                    remarks,
                    information_source,
                    data_source,
                    revision_date,
                    change_id,
                    deprecated
                    );
            stmt.executeUpdate();
        }catch(SQLException ex){
            throw new FactoryException(ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }
        
        return ellipsoid_code;
    }
    
    @Override
    public int getOrCreatePrimeMeridian(final PrimeMeridian candidate) throws FactoryException{
        
        final String code = searchSimilar(PrimeMeridian.class, candidate);
        if(code != null){
            return codeToID(code);
        }
        
        final Integer prime_meridian_code;
        final String prime_meridian_name = candidate.getName().getCode();
        final Double greenwich_longitude = candidate.getGreenwichLongitude();
        final Integer uom_code = getOrCreateUOM(candidate.getAngularUnit());
        final String remarks = (candidate.getRemarks()==null)? "" : candidate.getRemarks().toString();
        final String information_source = (candidate.getName().getCodeSpace()==null)? null : candidate.getName().getCodeSpace();
        final String data_source = (candidate.getName().getCodeSpace()==null)? "" : candidate.getName().getCodeSpace();
        final Date revision_date = new Date(System.currentTimeMillis());
        final String change_id = "";
        final Integer deprecated = 0;
        
        //save object
        Connection cnx = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            
            cnx = source.getConnection();
            
            //find similar
            final Integer similar = findSimilar(cnx, FIND_PRIME_MERIDIAN, prime_meridian_name,greenwich_longitude,uom_code);
            if(similar != null){ return similar; }
            
            cnx.setReadOnly(false);
            prime_meridian_code = getNextCode();
            stmt = cnx.prepareStatement(CREATE_PRIME_MERIDIAN.query());
            CREATE_PRIME_MERIDIAN.fillStatement(stmt, 
                    prime_meridian_code,
                    prime_meridian_name,
                    greenwich_longitude,
                    uom_code,
                    remarks,
                    information_source,
                    data_source,
                    revision_date,
                    change_id,
                    deprecated
                    );
            stmt.executeUpdate();
        }catch(SQLException ex){
            throw new FactoryException(ex);
        }finally{
            store.closeSafe(cnx, stmt, rs);
        }
        
        return prime_meridian_code;
    }
    
    private int codeToID(String code){
        return Integer.valueOf(code.split(":")[1]);
    }
    
    private String getCoordinateSystemType(CoordinateSystem candidate) throws FactoryException{
        if(candidate instanceof CartesianCS){
            return "Cartesian";
        }else if(candidate instanceof EllipsoidalCS){
            return "ellipsoidal";
        }else if(candidate instanceof TimeCS){
            return "temporal";
        }else if(candidate instanceof VerticalCS){
            return "vertical";
        }else{
            return "abstract";
        }
    }
    
    /**
     * Search for a similar record in the database.
     */
    private Integer findSimilar(final Connection cnx, final PGEPSGQueries query, final Object ... parameters) throws SQLException{
        Integer similar = null;
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try{
            stmt = query.createStatement(cnx, parameters);
            rs = stmt.executeQuery();
            if(rs.next()){
                similar = rs.getInt(1);
            }
        }finally{
            store.closeSafe(null, stmt, rs);
        }
        
        return similar;
    }
    
    /**
     * Find a similar Coordinate sysmtem, will check axis
     */
    private Integer findSimilarCoordinateSystem(final CoordinateSystem candidate, 
            final Connection cnx) throws SQLException, FactoryException{
        Integer similar = null;
        
        final String coord_sys_name = candidate.getName().getCode();
        final String coord_sys_type = getCoordinateSystemType(candidate);
        final Integer dimension = candidate.getDimension();
        
        final int nbDim = candidate.getDimension();
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try{
            stmt = FIND_COORDINATE_SYSTEM.createStatement(cnx, coord_sys_name,coord_sys_type,dimension);
            rs = stmt.executeQuery();
            
            search:
            while(rs.next()){
                //check each candidate axis
                
                boolean match = true;
                for(int i=0;i<nbDim;i++){
                    final CoordinateSystemAxis axis = candidate.getAxis(i);
                    final String expectedOrientation = axis.getDirection().name();
                    final String expectedAbbreviation = axis.getAbbreviation();
                    final int expectedUomCode = getOrCreateUOM(axis.getUnit());
                    
                    final int coord_sys_code = rs.getInt(1);
                    final String coord_axis_orientation = rs.getString(2);
                    final String coord_axis_abbreviation = rs.getString(3);
                    final int uomCode = rs.getInt(4);
                    
                    if(!(expectedOrientation.equalsIgnoreCase(coord_axis_orientation) &&
                       expectedAbbreviation.equalsIgnoreCase(coord_axis_abbreviation) &&
                       expectedUomCode == uomCode)){
                        match = false;
                    }                    
                    
                    if(i==nbDim-1 && match){
                        //last axis, and all axis match
                        similar = coord_sys_code;
                        break search;
                    }
                    
                    if(!rs.next()){ break; }
                }
            }
            
        }finally{
            store.closeSafe(null, stmt, rs);
        }
        
        return similar;
    }
    
}
