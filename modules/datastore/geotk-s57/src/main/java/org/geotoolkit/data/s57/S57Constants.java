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
package org.geotoolkit.data.s57;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.data.iso8211.SubFieldDescription;
import org.opengis.util.CodeList;
import static org.geotoolkit.data.iso8211.FieldValueType.*;

/**
 * S-57 constants.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class S57Constants {

    private S57Constants() {}
    
    public static abstract class S57CodeList<T extends CodeList<T>> extends CodeList<T>{
        
        private final Class<T> clazz;
        public final String ascii;
        public final int binary;

        private S57CodeList(final Class<T> clazz,final String ascii) {
            this(clazz,ascii,-1);
        }
        
        private S57CodeList(final Class<T> clazz, final int binary) {
            this(clazz,String.valueOf(binary),binary);
        }
        
        private S57CodeList(final Class<T> clazz,final String ascii, final int binary) {
            super(ascii,getValuesByField(clazz));
            this.clazz = clazz;
            this.ascii = ascii;
            this.binary = binary;
        }
            
        private static List getValuesByField(Class c){
            try {
                final Field m = c.getDeclaredField("VALUES");
                return (List)m.get(null);
            } catch (SecurityException ex) {
                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a static VALUES list.");
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a static VALUES list.");
            } catch (NoSuchFieldException ex) {
                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a static VALUES list.");
            }
        }
        
        private static S57CodeList[] getValuesByMethod(Class c) {
            try {
                final Method m = c.getDeclaredMethod("values");
                return (S57CodeList[])m.invoke(m);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a static values() method.");
            } catch (SecurityException ex) {
                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a static values() method.");
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a static values() method.");
            } catch (InvocationTargetException ex) {
                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a static values() method.");
            }
        }
        
        static S57CodeList valueOf(List<? extends S57CodeList> lst, Object code) {
            
            final String ascii;
            final int binary;
            if(code instanceof Number){
                ascii = code.toString();
                binary = ((Number)code).intValue();
                for(S57CodeList exp : lst){
                    if(exp.binary == binary) return exp;
                }
            }else if(code instanceof String){
                ascii = (String)code;
                binary = -1;
                for(S57CodeList exp : lst){
                    if(exp.ascii.equalsIgnoreCase(ascii)) return exp;
                }
            }else{
                throw new IllegalArgumentException("Expected a String or Number object, received : "+code);
            }
            
            throw new IllegalArgumentException("Unknwonec type : "+ code);
            
//            try {
//                final Constructor construct = c.getDeclaredConstructor(String.class,int.class);
//                return (S57CodeList) construct.newInstance(ascii,binary);
//            } catch (NoSuchMethodException ex) {
//                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a constructor with String,Integer arguments.",ex);
//            } catch (SecurityException ex) {
//                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a constructor with String,Integer arguments.",ex);
//            } catch (IllegalAccessException ex) {
//                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a constructor with String,Integer arguments.",ex);
//            } catch (InvocationTargetException ex) {
//                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a constructor with String,Integer arguments.",ex);
//            } catch (InstantiationException ex) {
//                throw new IllegalStateException("Class "+c.getSimpleName()+" has not been properly declared, expecting a constructor with String,Integer arguments.",ex);
//            }
        }
                
    } 
    
    /**
     * 2.2.1
     */
    public static final class RecordType extends S57CodeList<RecordType> {

        public static final String NAME = "RCNM";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 2);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<RecordType> VALUES = new ArrayList<RecordType>();
        //---------------
        /**
         * Data Set General Information
         */
        public static final RecordType DATASET_GENERAL_INFORMATIONS = new RecordType("DS", 10);
        /**
         * Data Set Geographic Reference
         */
        public static final RecordType DATASET_GEOGRAPHIC_REFERENCE = new RecordType("DP", 20);
        /**
         * Data Set History
         */
        public static final RecordType DATASET_HISTORY = new RecordType("DH", 30);
        /**
         * Data Set Accuracy
         */
        public static final RecordType DATASET_ACCURACY = new RecordType("DA", 40);
        /**
         * Catalogue Directory {*)}
         */
        public static final RecordType CATALOG_DIRECTORY = new RecordType("CD", 50);
        /**
         * Catalogue Cross Reference
         */
        public static final RecordType CATALOG_CROSS_REFERENCE = new RecordType("CR", 60);
        /**
         * Data Dictionary Definition
         */
        public static final RecordType DATADICO_DEFINITION = new RecordType("ID", 70);
        /**
         * Data Dictionary Domain
         */
        public static final RecordType DATADICO_DOMAIN = new RecordType("IO", 80);
        /**
         * Data Dictionary Schema
         */
        public static final RecordType DATADICO_SCHEMA = new RecordType("IS", 90);
        /**
         * Feature
         */
        public static final RecordType FEATURE = new RecordType("FE", 100);
        /**
         * Vector Isolated node
         */
        public static final RecordType VECTOR_ISOLATED_NODE = new RecordType("VI", 110);
        /**
         * Vector Connected node
         */
        public static final RecordType VECTOR_CONNECTED_NODE = new RecordType("VC", 120);
        /**
         * Vector Edge node
         */
        public static final RecordType EDGE_NODE = new RecordType("VE", 130);
        /**
         * Vector Face node
         */
        public static final RecordType FACE_NODE = new RecordType("VF", 140);

        RecordType(final String ascii, final int binary) {
            super(RecordType.class, ascii, binary);
        }

        public static RecordType[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new RecordType[VALUES.size()]);
            }
        }

        @Override
        public RecordType[] family() {
            return values();
        }
        
        public static RecordType valueOf(Object code) {
            return (RecordType) valueOf(VALUES, code);
        }

    }

    /**
     * 3.2.1 Unit
     * Coordinates can be encoded in three different ways. Only one type of units is allowed within a data set.
     * The type of unit is encoded in the “Coordinate Unit” [COUN] subfield of the “Data set Parameter” [DSPM] field.
     */
    public static final class Unit extends S57CodeList<Unit> {
        public static final String NAME = "COUN";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 2);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<Unit> VALUES = new ArrayList<Unit>();
        //---------------
        /** LL {1} Latitude and longitude : Degrees of arc */
        public static final Unit LATLON = new Unit("LL", 1);
        /** EN {2} Easting/Northing : Meters */
        public static final Unit EASTNORTH = new Unit("EN", 2);
        /** UC {3} Units on chart/map : Milimeters */
        public static final Unit MAPUNIT = new Unit("UC", 3);

        Unit(final String ascii, final int binary) {
            super(Unit.class, ascii, binary);
        }

        public static Unit[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new Unit[VALUES.size()]);
            }
        }

        @Override
        public Unit[] family() {
            return values();
        }
        
        public static Unit valueOf(Object code) {
            return (Unit) valueOf(VALUES, code);
        }
    }

    /** 3.2.2 */
    public static final class Projection extends S57CodeList<Projection> {
        public static final String NAME = "PROJ";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 3);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<Projection> VALUES = new ArrayList<Projection>();
        //---------------
        /**
         * When transforming units, other than latitude and longitude, into geographical positions (referenced to the
         * earth’ surface), the following data must be available:
         * • the chart/map projection employed, including the necessary parameters;
         * • a sufficient number of registration points (points for which both the unit coordinates and the
         * geographical position are known).
         * The data indicated above must be encoded in “Data Set Projection” [DSPR] and “Data Set Registration Control” [DSRC] fields.
         * Up to 4 parameters can be specified in the “Data Set Projection” field.
         * 
         * Albert equal area ALA {1} :
         * - Central meridian 
         * - Std. parallel nearer to equator
         * - Std. parallel farther from equator 
         * - Parallel of origin
         */
        public static final Projection ALBERT_EQUAL_AREA = new Projection("ALA",1);
        /**
         * Azimuthal equal area {2} :
         * - Longitude of tangency 
         * - Latitude of tangency 
         */
        public static final Projection AZIMUTHAL_EQUAL_AREA = new Projection("AZA",2);
        /**
         * Azimuthal equal distance {3} :
         * - Longitude of tangency 
         * - Latitude of tangency 
         */
        public static final Projection AZIMUTHAL_EQUAL_DISTANCE = new Projection("AZD",3);
        /**
         * Gnonomic {4} :
         * - Longitude of tangency 
         * - Latitude of tangency 
         */
        public static final Projection GNONOMIC = new Projection("GNO",4);
        /**
         * Hotline oblique Mercator (rectified skew orthomorphic) {5} :
         * - Longitude of projection origin 
         * - Latitude of projection origin 
         * - Azimuth of skew X-axis at projection origin 
         * - Scale factor at projection origin
         */
        public static final Projection HOTINE_OBLIQUE_MERCATOR = new Projection("HOM",5);
        /**
         * Lambert conformal conic {6} :
         * - Central meridian 
         * - Std. parallel nearer to equator 
         * - Std. parallel farther from equator 
         * - Parallel of origin
         */
        public static final Projection LAMBER_CONFORMAL_CONIC = new Projection("LCC",6);
        /**
         * Lambert equal area {7} :
         * - Central meridian 
         */
        public static final Projection LAMBERT_EQUAL_AREA = new Projection("LEA",7);
        /**
         * Mercator {8} :
         * - Central meridian 
         * - Latitude of true scale 
         * - Parallel of origin 
         */
        public static final Projection MERCATOR = new Projection("MER",8);
        /**
         * Oblique Mercator {9} :
         * - Longitude of reference point on great circle 
         * - Latitude reference point of great circle 
         * - Azimuth of great circle at ref. point 
         */
        public static final Projection OBLIQUE_MERCATOR = new Projection("OME",9);
        /**
         * Orthographic {10} :
         * - Longitude of tangency 
         * - Latitude of tangency 
         */
        public static final Projection ORTHOGRAPHIC = new Projection("ORT",10);
        /**
         * Polar stereo graphic {11} :
         * - Central meridian 
         * - Latitude of true scale 
         */
        public static final Projection POLAR_STEREO_GRAPHIC = new Projection("PST",11);
        /**
         * Azimuthal equal area {12} :
         * - Central meridian 
         */
        public static final Projection POLYCONIC = new Projection("POL",12);
        /**
         * Transverse Mercator {13} :
         * - Central meridian 
         * - Central scale factor
         * - Parallel of origin
         */
        public static final Projection TRANSVERSE_MERCATOR = new Projection("TME",13);
        /**
         * Oblique stereographic {14} :
         * - Longitude of origin 
         * - Latitude of origin 
         * - Scale factor at origin 
         */
        public static final Projection OBLIQUE_STEREOGRAPHIC = new Projection("OST",14);

        Projection(final String ascii, final int binary) {
            super(Projection.class, ascii, binary);
        }

        public static Projection[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new Projection[VALUES.size()]);
            }
        }

        @Override
        public Projection[] family() {
            return values();
        }
        
        public static Projection valueOf(Object code) {
            return (Projection) valueOf(VALUES, code);
        }
    }
        
    /**
     * Feature record identifier field (4.2).
     * The “Object Geometric Primitive” [PRIM] subfield is used to specify the geometric primitive of the encoded object. 
     */
    public static final class Primitive extends S57CodeList<Primitive> {
        public static final String NAME = "PRIM";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<Primitive> VALUES = new ArrayList<Primitive>();
        //---------------        
        /** Point */
        public static final Primitive PRIMITIVE_POINT = new Primitive("P",1);
        /** Line */
        public static final Primitive PRIMITIVE_LINE = new Primitive("L",2);
        /** Area */
        public static final Primitive PRIMITIVE_AREA = new Primitive("A",3);
        /** Object does not directly reference any geometry */
        public static final Primitive PRIMITIVE_NONE = new Primitive("N",255);

        Primitive(final String ascii, final int binary) {
            super(Primitive.class, ascii, binary);
        }

        public static Primitive[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new Primitive[VALUES.size()]);
            }
        }

        @Override
        public Primitive[] family() {
            return values();
        }
        
        public static Primitive valueOf(Object code) {
            return (Primitive) valueOf(VALUES, code);
        }
    }
        
    /** Usage indicator */
    public static final class Usage extends S57CodeList<Usage> {
        public static final String NAME = "USAG";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<Usage> VALUES = new ArrayList<Usage>();
        //---------------
        /** exterior */
        public static final Usage EXTERIOR = new Usage("E",1);
        /** interior */
        public static final Usage INTERIOR = new Usage("I",2);
        /** Exterior boundary truncated by the data limit */
        public static final Usage EXTERIOR_TRUNCATED = new Usage("C",3);
        /** null */
        public static final Usage NULL = new Usage("N",255);

        Usage(final String ascii, final int binary) {
            super(Usage.class, ascii, binary);
        }

        public static Usage[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new Usage[VALUES.size()]);
            }
        }

        @Override
        public Usage[] family() {
            return values();
        }
        
        public static Usage valueOf(Object code) {
            return (Usage) valueOf(VALUES, code);
        }
    }
    
    /** Orientation */
    public static final class Orientation extends S57CodeList<Orientation> {
        public static final String NAME = "ORNT";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<Orientation> VALUES = new ArrayList<Orientation>();
        //---------------
        /** Forward */
        public static final Orientation FORWARD = new Orientation("F",1);
        /** Reverse */
        public static final Orientation REVERSE = new Orientation("R",2);
        /** NULL */
        public static final Orientation NOREVELANT = new Orientation("N",255);

        Orientation(final String ascii, final int binary) {
            super(Orientation.class, ascii, binary);
        }

        public static Orientation[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new Orientation[VALUES.size()]);
            }
        }

        @Override
        public Orientation[] family() {
            return values();
        }
        
        public static Orientation valueOf(Object code) {
            return (Orientation) valueOf(VALUES, code);
        }
    }
    
    /** Mask */
    public static final class Mask extends S57CodeList<Mask> {
        public static final String NAME = "MASK";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<Mask> VALUES = new ArrayList<Mask>();
        //---------------
        /** Forward */
        public static final Mask MASK = new Mask("M",1);
        /** Reverse */
        public static final Mask SHOW = new Mask("S",2);
        /** NULL */
        public static final Mask NOREVELANT = new Mask("N",255);

        Mask(final String ascii, final int binary) {
            super(Mask.class, ascii, binary);
        }

        public static Mask[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new Mask[VALUES.size()]);
            }
        }

        @Override
        public Mask[] family() {
            return values();
        }
        
        public static Mask valueOf(Object code) {
            return (Mask) valueOf(VALUES, code);
        }
    }
        
    /** Topology indicator */
    public static final class Topology extends S57CodeList<Topology> {
        public static final String NAME = "TOPI";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<Topology> VALUES = new ArrayList<Topology>();
        //---------------
        /** Forward */
        public static final Topology TOPI_BEGIN_NODE = new Topology("B", 1);
        public static final Topology TOPI_END_NODE = new Topology("E", 2);
        public static final Topology TOPI_LEFT_FACE = new Topology("S", 3);
        public static final Topology TOPI_RIGHT_FACE = new Topology("D", 4);
        public static final Topology TOPI_FACE = new Topology("F", 5);
        public static final Topology TOPI_NONE = new Topology("N", 255);;

        Topology(final String ascii, final int binary) {
            super(Topology.class, ascii, binary);
        }

        public static Topology[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new Topology[VALUES.size()]);
            }
        }

        @Override
        public Topology[] family() {
            return values();
        }
        
        public static Topology valueOf(Object code) {
            return (Topology) valueOf(VALUES, code);
        }
    }
        
    /** Arc type */
    public static final class ArcType extends S57CodeList<ArcType> {
        public static final String NAME = "ATYP";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<ArcType> VALUES = new ArrayList<ArcType>();
        //---------------
        /** Forward */
        public static final ArcType ATYP_ARC_3_POINT = new ArcType("C",1);
        public static final ArcType ATYP_ELLIPTICAL = new ArcType("E",2);
        public static final ArcType ATYP_UNIFORM_BSPLINE = new ArcType("U",3);
        public static final ArcType ATYP_BEZIER = new ArcType("B",4);
        public static final ArcType ATYP_NONUNIFORM_BSPLINE  = new ArcType("N",5);

        ArcType(final String ascii, final int binary) {
            super(ArcType.class, ascii, binary);
        }

        public static ArcType[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new ArcType[VALUES.size()]);
            }
        }

        @Override
        public ArcType[] family() {
            return values();
        }
        
        public static ArcType valueOf(Object code) {
            return (ArcType) valueOf(VALUES, code);
        }
    }
    
    /** Arc construction surface */
    public static final class ConstructionSurface extends S57CodeList<ConstructionSurface> {
        public static final String NAME = "SURF";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<ConstructionSurface> VALUES = new ArrayList<ConstructionSurface>();
        //---------------
        /** Ellipsoidal Object must be reconstructed prior to projection onto a 2-D surface */
        public static final ConstructionSurface MASK = new ConstructionSurface("E",1);
        /** Planar Object must be reconstructed after projection onto a 2-D surface,regardless of projection used */
        public static final ConstructionSurface SHOW = new ConstructionSurface("P",2);

        ConstructionSurface(final String ascii, final int binary) {
            super(ConstructionSurface.class, ascii, binary);
        }

        public static ConstructionSurface[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new ConstructionSurface[VALUES.size()]);
            }
        }

        @Override
        public ConstructionSurface[] family() {
            return values();
        }
        
        public static ConstructionSurface valueOf(Object code) {
            return (ConstructionSurface) valueOf(VALUES, code);
        }
    }
    
    /**
     * 7.3.11 Exchange purpose
     */
    public static final class ExchangePurpose extends S57CodeList<ExchangePurpose> {

        public static final String NAME = "EXPP";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<ExchangePurpose> VALUES = new ArrayList<ExchangePurpose>();
        //---------------
        public static final ExchangePurpose NEW = new ExchangePurpose("N", 1);
        public static final ExchangePurpose REVISION = new ExchangePurpose("R", 2);

        ExchangePurpose(final String ascii, final int binary) {
            super(ExchangePurpose.class, ascii, binary);
        }

        public static ExchangePurpose[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new ExchangePurpose[VALUES.size()]);
            }
        }

        @Override
        public ExchangePurpose[] family() {
            return values();
        }
        
        public static ExchangePurpose valueOf(Object code) {
            return (ExchangePurpose) valueOf(VALUES, code);
        }
    }

    /**
     * 7.3.11 Indended usage
     */
    public static final class IntendedUsage extends S57CodeList<IntendedUsage> {

        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<IntendedUsage> VALUES = new ArrayList<IntendedUsage>();
        //---------------
        public static final IntendedUsage OVERVIEW = new IntendedUsage("1", 1);
        public static final IntendedUsage GENERAL = new IntendedUsage("2", 2);
        public static final IntendedUsage COASTAL = new IntendedUsage("3", 3);
        public static final IntendedUsage APPROACH = new IntendedUsage("4", 4);
        public static final IntendedUsage HARBOUR = new IntendedUsage("5", 5);
        public static final IntendedUsage BERTHING = new IntendedUsage("6", 6);

        IntendedUsage(final String ascii, final int binary) {
            super(IntendedUsage.class, ascii, binary);
        }

        public static IntendedUsage[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new IntendedUsage[VALUES.size()]);
            }
        }

        @Override
        public IntendedUsage[] family() {
            return values();
        }
        
        public static IntendedUsage valueOf(Object code) {
            return (IntendedUsage) valueOf(VALUES, code);
        }
    }

    /**
     * 7.3.11 Product Specification
     */
    public static final class ProductSpecification extends S57CodeList<ProductSpecification> {

        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 3);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<ProductSpecification> VALUES = new ArrayList<ProductSpecification>();
        //---------------
        public static final ProductSpecification ENC = new ProductSpecification("ENC", 1);
        public static final ProductSpecification ODD = new ProductSpecification("ODD", 2);

        ProductSpecification(final String ascii, final int binary) {
            super(ProductSpecification.class, ascii, binary);
        }

        public static ProductSpecification[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new ProductSpecification[VALUES.size()]);
            }
        }

        @Override
        public ProductSpecification[] family() {
            return values();
        }
        
        public static ProductSpecification valueOf(Object code) {
            return (ProductSpecification) valueOf(VALUES, code);
        }
    }

    /**
     * 7.3.11 Application Profile Identification
     */
    public static final class ApplicationProfile extends S57CodeList<ApplicationProfile> {

        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 2);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<ApplicationProfile> VALUES = new ArrayList<ApplicationProfile>();
        //---------------
        public static final ApplicationProfile ENC_NEW = new ApplicationProfile("EN", 1);
        public static final ApplicationProfile ENC_REVISION = new ApplicationProfile("ER", 2);
        public static final ApplicationProfile IHO_DICO = new ApplicationProfile("DD", 3);

        ApplicationProfile(final String ascii, final int binary) {
            super(ApplicationProfile.class, ascii, binary);
        }

        public static ApplicationProfile[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new ApplicationProfile[VALUES.size()]);
            }
        }

        @Override
        public ApplicationProfile[] family() {
            return values();
        }
        
        public static ApplicationProfile valueOf(Object code) {
            return (ApplicationProfile) valueOf(VALUES, code);
        }
    }

    /**
     * 7.3.11 Agency , see S-62(TODO add all the list)
     */
    public static final class Agency extends S57CodeList<Agency> {

        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 2);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 2);
        static final List<Agency> VALUES = new ArrayList<Agency>();
        //---------------
        // LAST UPDATE 11/04/2013 from http://registry.iho.int/s100_gi_registry/home.php
        
        ////////////////////////////////////////////////////////////////////////
        // MAIN PRODUCERS : IHO MS /////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        /** Algeria, Service Hydrographique des Forces Navales (2008-10-16) */
        public static final Agency ALGERIA_SERVICE_HYDROGRAPHIQUE_DES_FORCES_NAVALES = new Agency("DZ", 610);
        /** Argentina, Servicio de Hidrografía Naval (SHN) (2008-10-16) */
        public static final Agency ARGENTINA_SHN = new Agency("AR", 1);
        /** Australia, Australian Hydrographic Service (AHS) (2008-10-16) */
        public static final Agency AUSTRALIA_AHS = new Agency("AU", 10);
        /** Bahrain, Hydrographic Survey Office (2008-10-16) */
        public static final Agency BAHRAIN_HYDROGRAPHIC_SURVEY_OFFICE = new Agency("BH", 20);
        /** Bangladesh, Hydrographic Department (2008-10-16) */
        public static final Agency BANGLADESH_HYDROGRAPHIC_DEPARTMENT = new Agency("BD", 660);
        /** Belgium, MDK – Afdeling Kust – Division Coast (2008-10-16) */
        public static final Agency BELGIUM_MDK_AFDELING_KUST_DIVISION_COAST = new Agency("BE", 30);
        /** Brazil, Directorate of Hydrography and Navigation (DHN) (2008-10-16) */
        public static final Agency BRAZIL_DHN = new Agency("BR", 40);
        /** Cameroon, Port Autonome de Douala (PAD) (2008-10-16) */
        public static final Agency CAMEROON_PAD = new Agency("CM", 740);
        /** Canada, Canadian Hydrographic Service (CHS) (2008-10-16) */
        public static final Agency CANADA_CHS = new Agency("CA", 50);
        /** Canada, Canadian Forces (2008-10-16) */
        public static final Agency CANADA_CANADIAN_FORCES = new Agency("C4", 51);
        /** Chile, Servicio Hidrográfico y Oceanográfico de la Armada (SHOA) (2008-10-16) */
        public static final Agency CHILE_SHOA = new Agency("CL", 60);
        /** China, Maritime Safety Administration (MSA) (2008-10-16) */
        public static final Agency CHINA_MSA = new Agency("CN", 70);
        /** China, The Navigation Guarantee Department of The Chinese Navy Headquarters (2008-10-16) */
        public static final Agency CHINA_THE_NAVIGATION_GUARANTEE_DEPARTMENT_OF_THE_CHINESE_NAVY_HEADQUARTERS = new Agency("C1", 71);
        /** China, Hong Kong Special Administrative Region (2008-10-16) */
        public static final Agency CHINA_HONG_KONG_SPECIAL_ADMINISTRATIVE_REGION = new Agency("C2", 72);
        /** China, Macau Special Administrative Region (2008-10-16) */
        public static final Agency CHINA_MACAU_SPECIAL_ADMINISTRATIVE_REGION = new Agency("C3", 73);
        /** Colombia, Ministerio de Defensa Nacional (2008-10-16) */
        public static final Agency COLOMBIA_MINISTERIO_DE_DEFENSA_NACIONAL = new Agency("CO", 760);
        /** Congo (Dem. Rep. of), Ministère des Transports et Communications (2008-10-16) */
        public static final Agency CONGO_MINISTÈRE_DES_TRANSPORTS_ET_COMMUNICATIONS = new Agency("CD", 590);
        /** Croatia, Hrvatski Hidrografski Institut (2008-10-16) */
        public static final Agency CROATIA_HRVATSKI_HIDROGRAFSKI_INSTITUT = new Agency("HR", 80);
        /** Cuba, Oficina Nacional de Hidrografia y Geodesia (2008-10-16) */
        public static final Agency CUBA_OFICINA_NACIONAL_DE_HIDROGRAFIA_Y_GEODESIA = new Agency("CU", 90);
        /** Cyprus, Hydrographic Unit of the Department of Lands and Surveys (2008-10-16) */
        public static final Agency CYPRUS_HYDROGRAPHIC_UNIT_OF_THE_DEPARTMENT_OF_LANDS_AND_SURVEYS = new Agency("CY", 100);
        /** Denmark, Kort-Og Matrikelstyrelsen (KMS) (2008-10-16) */
        public static final Agency DENMARK_KMS = new Agency("DK", 110);
        /** Dominican Rep., Instituto Cartografico Militar (2008-10-16) */
        public static final Agency DOMINICAN_REP_INSTITUTO_CARTOGRAFICO_MILITAR = new Agency("DO", 120);
        /** Ecuador, Instituto Oceanográfico de la Armada (INOCAR) (2008-10-16) */
        public static final Agency ECUADOR_INOCAR = new Agency("EC", 130);
        /** Egypt, Shobat al Misaha al Baharia (2008-10-16) */
        public static final Agency EGYPT_SHOBAT_AL_MISAHA_AL_BAHARIA = new Agency("EG", 140);
        /** Estonia, Estonian Maritime Administration (EMA) (2008-10-16) */
        public static final Agency ESTONIA_EMA = new Agency("EE", 870);
        /** Fiji, Fiji Islands Maritime Safety Administration (FIMSA) (2008-10-16) */
        public static final Agency FIJI_FIMSA = new Agency("FJ", 150);
        /** Finland, Finnish Maritime Administration (FMA) (2008-10-16) */
        public static final Agency FINLAND_FMA = new Agency("FI", 160);
        /** France, Service Hydrographique et Océanographique de la Marine (SHOM) (2008-10-16) */
        public static final Agency FRANCE_SHOM = new Agency("FR", 170);
        /** Germany, Bundesamt für Seeschiffahrt und Hydrographie (BSH) (2008-10-16) */
        public static final Agency GERMANY_BSH = new Agency("DE", 180);
        /** Greece, Hellenic Navy Hydrographic Service (HNHS) (2008-10-16) */
        public static final Agency GREECE_HNHS = new Agency("GR", 190);
        /** Guatemala, Ministerio de la Defensa Nacional (2008-10-16) */
        public static final Agency GUATEMALA_MINISTERIO_DE_LA_DEFENSA_NACIONAL = new Agency("GT", 200);
        /** Guatemala, Comisión Portuaria Nacional (2008-10-16) */
        public static final Agency GUATEMALA_COMISIÓN_PORTUARIA_NACIONAL = new Agency("G1", 201);
        /** Iceland, Icelandic Coast Guard (2008-10-16) */
        public static final Agency ICELAND_ICELANDIC_COAST_GUARD = new Agency("IS", 210);
        /** India, National Hydrographic Office (NHO) (2008-10-16) */
        public static final Agency INDIA_NHO = new Agency("IN", 220);
        /** Indonesia, Jawatan Hidro-Oseanografi (JANHIDROS) (2008-10-16) */
        public static final Agency INDONESIA_JANHIDROS = new Agency("ID", 230);
        /** Ireland, Maritime Safety Directorate (2008-10-16) */
        public static final Agency IRELAND_MARITIME_SAFETY_DIRECTORATE = new Agency("IE", 990);
        /** Islamic Rep. of Iran, Ports and Shipping Organization (PSO) (2008-10-16) */
        public static final Agency ISLAMIC_REP_OF_IRAN_PSO = new Agency("IR", 240);
        /** Italy, Istituto Idrografico della Marina (IIM) (2008-10-16) */
        public static final Agency ITALY_IIM = new Agency("IT", 250);
        /** Jamaica, Surveys and Mapping Division (2008-10-16) */
        public static final Agency JAMAICA_SURVEYS_AND_MAPPING_DIVISION = new Agency("JM", 1010);
        /** Japan, Japan Hydrographic and Oceanographic Department (JHOD) (2008-10-16) */
        public static final Agency JAPAN_JHOD = new Agency("JP", 260);
        /** Korea (DPR of), Hydrographic Department (2008-10-16) */
        public static final Agency KOREA_HYDROGRAPHIC_DEPARTMENT = new Agency("KP", 270);
        /** Korea (Rep. of), National Oceanographic Research Institute (NORI) (2008-10-16) */
        public static final Agency KOREA_NORI = new Agency("KR", 280);
        /** Kuwait, Ministry of Communications (2008-10-16) */
        public static final Agency KUWAIT_MINISTRY_OF_COMMUNICATIONS = new Agency("KW", 1050);
        /** Latvia, Maritime Administration of Latvia (2008-10-16) */
        public static final Agency LATVIA_MARITIME_ADMINISTRATION_OF_LATVIA = new Agency("LV", 1060);
        /** Malaysia, National Hydrographic Centre (2008-10-16) */
        public static final Agency MALAYSIA_NATIONAL_HYDROGRAPHIC_CENTRE = new Agency("MY", 290);
        /** Mauritius , Ministry of Housing and Land, Hydrographic Unit (2008-10-16) */
        public static final Agency MAURITIUS_MINISTRY_OF_HOUSING_AND_LAND_HYDROGRAPHIC_UNIT = new Agency("MU", 1170);
        /** Mexico, Secretaria de Marina – Armada de Mexico, Direccion General Adjunta de Oceanografia, Hidrografia y Meteorologia (2008-10-16) */
        public static final Agency MEXICO_SECRETARIA_DE_MARINA_ARMADA_DE_MEXICO_DIRECCION_GENERAL_ADJUNTA_DE_OCEANOGRAFIA_HIDROGRAFIA_Y_METEOROLOGIA = new Agency("MX", 1180);
        /** Monaco, Direction des Affaires Maritimes (2008-10-16) */
        public static final Agency MONACO_DIRECTION_DES_AFFAIRES_MARITIMES = new Agency("MC", 300);
        /** Morocco , Division Hydrographie et Cartographie (DHC) de la Marine Royale  (2008-10-16) */
        public static final Agency MOROCCO_DHC = new Agency("MA", 1200);
        /** Mozambique, Instituto Nacional de Hidrografia e Navegação (INAHINA) (2008-10-16) */
        public static final Agency MOZAMBIQUE_INAHINA = new Agency("MZ", 1210);
        /** Myanmar , Central Naval Hydrographic Depot (CNHD) (2008-10-16) */
        public static final Agency MYANMAR_CNHD = new Agency("MM", 1220);
        /** Netherlands, Koninklijke Marine Dienst der Hydrografie / CZSK (2008-10-16) */
        public static final Agency NETHERLANDS_KONINKLIJKE_MARINE_DIENST_DER_HYDROGRAFIE = new Agency("NL", 310);
        /** New Zealand, Land Information New Zealand (LINZ) (2008-10-16) */
        public static final Agency NEW_ZEALAND_LINZ = new Agency("NZ", 320);
        /** New Zealand, New Zealand Defence force (NZDF) Geospatial Intelligence Organisation (GIO) (2010-08-12) */
        public static final Agency NEW_ZEALAND_NZDF = new Agency("N3", 321);
        /** Nigeria, Nigerian Navy Hydrographic Office (2008-10-16) */
        public static final Agency NIGERIA_NIGERIAN_NAVY_HYDROGRAPHIC_OFFICE = new Agency("NG", 330);
        /** Norway, Norwegian Hydrographic Service (2008-10-16) */
        public static final Agency NORWAY_NORWEGIAN_HYDROGRAPHIC_SERVICE = new Agency("NO", 340);
        /** Norway, Electronic Chart Centre (2008-10-16) */
        public static final Agency NORWAY_ELECTRONIC_CHART_CENTRE = new Agency("N1", 341);
        /** Norway, Norwegian Defence (2008-10-16) */
        public static final Agency NORWAY_NORWEGIAN_DEFENCE = new Agency("N2", 342);
        /** Oman, National Hydrographic Office (2008-10-16) */
        public static final Agency OMAN_NATIONAL_HYDROGRAPHIC_OFFICE = new Agency("OM", 350);
        /** Pakistan, Pakistan Hydrographic Department (2008-10-16) */
        public static final Agency PAKISTAN_PAKISTAN_HYDROGRAPHIC_DEPARTMENT = new Agency("PK", 360);
        /** Papua New Guinea, Hydrographic Division, National Maritime Safety Division (NMSA) (2008-10-16) */
        public static final Agency PAPUA_NEW_GUINEA_NMSA = new Agency("PG", 370);
        /** Peru, Dirección de Hidrografía y Navegación (DHN) (2008-10-16) */
        public static final Agency PERU_DHN = new Agency("PE", 380);
        /** Philippines, National Mapping and Resource Information Authority, Coast & Geodetic Survey Department (2008-10-16) */
        public static final Agency PHILIPPINES_NATIONAL_MAPPING_AND_RESOURCE_INFORMATION_AUTHORITY_COAST_AND_GEODETIC_SURVEY_DEPARTMENT = new Agency("PH", 390);
        /** Poland, Biuro Hydrograficzne  (2008-10-16) */
        public static final Agency POLAND_BIURO_HYDROGRAFICZNE_ = new Agency("PL", 400);
        /** Portugal, Instituto Hidrografico, Portugal (IHP) (2008-10-16) */
        public static final Agency PORTUGAL_IHP = new Agency("PT", 410);
        /** Qatar, Urban Planning & Development Authority, Hydrographic Section (2008-10-16) */
        public static final Agency QATAR_URBAN_PLANNING_AND_DEVELOPMENT_AUTHORITY_HYDROGRAPHIC_SECTION = new Agency("QA", 1290);
        /** Romania , Directia Hidrografica Maritima (2008-10-16) */
        public static final Agency ROMANIA_DIRECTIA_HIDROGRAFICA_MARITIMA = new Agency("RO", 1300);
        /** Russian Federation, Head Department of Navigation & Oceanography (DNO) (2008-10-16) */
        public static final Agency RUSSIAN_FEDERATION_DNO = new Agency("RU", 420);
        /** Russian Federation, Federal State Unitary Hydrographc Department (2011-11-16) */
        public static final Agency RUSSIAN_FEDERATION_FEDERAL_STATE_UNITARY_HYDROGRAPHC_DEPARTMENT = new Agency("R1", 425);
        /** Saudi Arabia, General Directorate of Military Survey (GDMS) (2008-10-16) */
        public static final Agency SAUDI_ARABIA_GDMS = new Agency("SA", 1360);
        /** Saudi Arabia, General Commission for Survey (GCS) (2011-11-23) */
        public static final Agency SAUDI_ARABIA_GCS = new Agency("S1", 1365);
        /** Serbia , Direkcija Za Unutrašnje Plovne Puteve (2008-10-16) */
        public static final Agency SERBIA_DIREKCIJA_ZA_UNUTRAŠNJE_PLOVNE_PUTEVE = new Agency("RS", 580);
        /** Singapore, Hydrographic Department, Maritime and Port Authority (MPA) (2008-10-16) */
        public static final Agency SINGAPORE_MPA = new Agency("SG", 430);
        /** Slovenia , Ministry of Transport Maritime Office (2008-10-16) */
        public static final Agency SLOVENIA_MINISTRY_OF_TRANSPORT_MARITIME_OFFICE = new Agency("SI", 1400);
        /** South Africa (Rep. of), South African Navy Hydrographic Office (SANHO) (2008-10-16) */
        public static final Agency REP_SOUTH_AFRICA_SANHO = new Agency("ZA", 440);
        /** Spain, Instituto Hidrográfico de la Marina (IHM) (2008-10-16) */
        public static final Agency SPAIN_IHM = new Agency("ES", 450);
        /** Sri Lanka, National Hydrographic Office, National Aquatic Resources Research and Development Agency (NARA) (2008-10-16) */
        public static final Agency SRI_LANKA_NARA = new Agency("LK", 460);
        /** Suriname, Maritieme Autoriteit Suriname (MAS) (2008-10-16) */
        public static final Agency SURINAME_MAS = new Agency("SR", 470);
        /** Sweden, Sjöfartsverket, Swedish Maritime Administration (2008-10-16) */
        public static final Agency SWEDEN_SJÖFARTSVERKET_SWEDISH_MARITIME_ADMINISTRATION = new Agency("SE", 480);
        /** Syrian Arab Republic, General Directorate of Ports (2008-10-16) */
        public static final Agency SYRIAN_ARAB_REPUBLIC_GENERAL_DIRECTORATE_OF_PORTS = new Agency("SY", 490);
        /** Thailand, Hydrographic Department, Royal Thai Navy (2008-10-16) */
        public static final Agency THAILAND_HYDROGRAPHIC_DEPARTMENT_ROYAL_THAI_NAVY = new Agency("TH", 500);
        /** Tonga, Tonga Defence Services (2008-10-16) */
        public static final Agency TONGA_TONGA_DEFENCE_SERVICES = new Agency("TO", 505);
        /** Trinidad & Tobago, Trinidad & Tobago Hydrographic Unit (2008-10-16) */
        public static final Agency TRINIDAD_AND_TOBAGO_TRINIDAD_AND_TOBAGO_HYDROGRAPHIC_UNIT = new Agency("TT", 510);
        /** Tunisia, Service Hydrographique et Océanographique (SHO), Armée de Mer  (2008-10-16) */
        public static final Agency TUNISIA_SHO = new Agency("TN", 1470);
        /** Turkey, Seyir, Hidrografi ve Osinografi Dairesi Baskanligi, Office of Navigation, Hydrography and Oceanography (2008-10-16) */
        public static final Agency TURKEY_SEYIR_HIDROGRAFI_VE_OSINOGRAFI_DAIRESI_BASKANLIGI_OFFICE_OF_NAVIGATION_HYDROGRAPHY_AND_OCEANOGRAPHY = new Agency("TR", 520);
        /** UK, United Kingdom Hydrographic Office (2008-10-16) */
        public static final Agency UK_UNITED_KINGDOM_HYDROGRAPHIC_OFFICE = new Agency("GB", 540);
        /** Ukraine, State Hydrographic Service of Ukraine (2008-10-16) */
        public static final Agency UKRAINE_STATE_HYDROGRAPHIC_SERVICE_OF_UKRAINE = new Agency("UA", 1490);
        /** United Arab Emirates, Ministry of Communications (2008-10-16) */
        public static final Agency UNITED_ARAB_EMIRATES_MINISTRY_OF_COMMUNICATIONS = new Agency("AE", 530);
        /** Uruguay, Servicio de Oceanografía, Hidrografía y Meteorología de la Armada (SOHMA) (2008-10-16) */
        public static final Agency URUGUAY_SOHMA = new Agency("UY", 560);
        /** USA , Office of Coast Survey, National Ocean Service, National Oceanic and Atmospheric Administration (NOS) (2008-10-16) */
        public static final Agency USA_NOS = new Agency("US", 550);
        /** USA ,  National Geospatial-Intelligence Agency Department of Defense (NGA) (2008-10-16) */
        public static final Agency USA_NGA = new Agency("U1", 551);
        /** USA ,  Commander, Naval Meteorology and Oceanography Command (CNMOC) (2008-10-16) */
        public static final Agency USA_CNMOC = new Agency("U2", 552);
        /** USA , U.S. Army Corps of Engineers (USACE) (2008-10-16) */
        public static final Agency USA_USACE = new Agency("U3", 553);
        /** Venezuela, Commandancia General de la Armada, Dirección de Hidrografía y Navegación (DHN) (2008-10-16) */
        public static final Agency VENEZUELA_DHN = new Agency("VE", 570);

        ////////////////////////////////////////////////////////////////////////
        // MAIN PRODUCERS : OTHER STATES ///////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////

        /** , International Hydrographic Organization (IHO) (2008-10-16) */
        public static final Agency _IHO = new Agency("AA", 1810);
        /** , Co-operating Hydrographic Offices in the Malacca and Singapore Straits (Indonesia, Japan, Malaysia and Singapore) (2008-10-16) */
        public static final Agency _INDONESIA_JAPAN_MALAYSIA_AND_SINGAPORE = new Agency("MS", 2010);
        /** , East Asia Hydrographic Commission (EAHC) (2008-10-16) */
        public static final Agency _EAHC = new Agency("EA", 2040);
        /** Albania, Albanian Hydrographic Service (2008-10-16) */
        public static final Agency ALBANIA_ALBANIAN_HYDROGRAPHIC_SERVICE = new Agency("AL", 600);
        /** Angola, Not known (2008-10-16) */
        public static final Agency ANGOLA_NOT_KNOWN = new Agency("AO", 620);
        /** Anguilla, Ministry of Infrastructure, Communications & Utilities (2008-10-16) */
        public static final Agency ANGUILLA_MINISTRY_OF_INFRASTRUCTURE_COMMUNICATIONS_AND_UTILITIES = new Agency("AI", 625);
        /** Antigua and Barbuda, Antigua and Barbuda Port Authority (2008-10-16) */
        public static final Agency ANTIGUA_AND_BARBUDA_ANTIGUA_AND_BARBUDA_PORT_AUTHORITY = new Agency("AG", 630);
        /** Aruba, Netherlands ENC charting responsibility (2008-10-16) */
        public static final Agency ARUBA_NETHERLANDS_ENC_CHARTING_RESPONSIBILITY = new Agency("AW", 640);
        /** Azerbaijan, Azerbaijan Navy (2008-10-16) */
        public static final Agency AZERBAIJAN_AZERBAIJAN_NAVY = new Agency("AZ", 645);
        /** Bahamas,  Port Department, Ministry of Transport and Aviation (2008-10-16) */
        public static final Agency BAHAMAS_PORT_DEPARTMENT_MINISTRY_OF_TRANSPORT_AND_AVIATION = new Agency("BS", 650);
        /** Barbados, Barbados Port Inc (2008-10-16) */
        public static final Agency BARBADOS_BARBADOS_PORT_INC = new Agency("BB", 670);
        /** Belize, Belize Port Authority (2008-10-16) */
        public static final Agency BELIZE_BELIZE_PORT_AUTHORITY = new Agency("BZ", 680);
        /** Benin, Direction Générale du Port Autonome de Cotonou (2008-10-16) */
        public static final Agency BENIN_DIRECTION_GÉNÉRALE_DU_PORT_AUTONOME_DE_COTONOU = new Agency("BJ", 690);
        /** Bermuda, Ministry of Works, Engineering and Housing (2008-10-16) */
        public static final Agency BERMUDA_MINISTRY_OF_WORKS_ENGINEERING_AND_HOUSING = new Agency("BM", 695);
        /** Bolivia, Servicio Nacional de Hidrografia Naval de Bolivia (2008-10-16) */
        public static final Agency BOLIVIA_SERVICIO_NACIONAL_DE_HIDROGRAFIA_NAVAL_DE_BOLIVIA = new Agency("BO", 700);
        /** British Virgin Islands, Chief Minister’s Office (2008-10-16) */
        public static final Agency BRITISH_VIRGIN_ISLANDS_CHIEF_MINISTERS_OFFICE = new Agency("VG", 705);
        /** Brunei Darussalam, Department of Marine (2008-10-16) */
        public static final Agency BRUNEI_DARUSSALAM_DEPARTMENT_OF_MARINE = new Agency("BN", 710);
        /** Brunei Darussalam, Survey Department (2008-10-16) */
        public static final Agency BRUNEI_DARUSSALAM_SURVEY_DEPARTMENT = new Agency("B2", 715);
        /** Bulgaria , Hidrografska Sluzhba Pri Ministerstvo Na Otbranata (2008-10-16) */
        public static final Agency BULGARIA_HIDROGRAFSKA_SLUZHBA_PRI_MINISTERSTVO_NA_OTBRANATA = new Agency("BG", 720);
        /** Cambodia, Service de l’Hydrologie et des grands barrages (2008-10-16) */
        public static final Agency CAMBODIA_SERVICE_DE_LHYDROLOGIE_ET_DES_GRANDS_BARRAGES = new Agency("KH", 730);
        /** Cape Verde, Instituto Marityimo e Portuário (IMP) (2008-10-16) */
        public static final Agency CAPE_VERDE_IMP = new Agency("CV", 750);
        /** Comoros, Not Known (2008-10-16) */
        public static final Agency COMOROS_NOT_KNOWN = new Agency("KM", 770);
        /** Congo (Rep. of), Port Autonome de Pointe Noire (2008-10-16) */
        public static final Agency CONGO_PORT_AUTONOME_DE_POINTE_NOIRE = new Agency("CG", 780);
        /** Cook Islands, Maritime Division, Ministry of Tourism and Transport (2008-10-16) */
        public static final Agency COOK_ISLANDS_MARITIME_DIVISION_MINISTRY_OF_TOURISM_AND_TRANSPORT = new Agency("CK", 790);
        /** Costa-Rica, Instituto Geografico Nacional (IGN) (2008-10-16) */
        public static final Agency COSTA_RICA_IGN = new Agency("CR", 800);
        /** Côte d’Ivoire, Direction Générale du Port Autonome d’Abidjan (2008-10-16) */
        public static final Agency CÔTE_DIVOIRE_DIRECTION_GÉNÉRALE_DU_PORT_AUTONOME_DABIDJAN = new Agency("CI", 810);
        /** Djibouti, Ministère de l’Equipement et des Transports, Direction des Affaires Maritimes  (2008-10-16) */
        public static final Agency DJIBOUTI_MINISTÈRE_DE_LEQUIPEMENT_ET_DES_TRANSPORTS_DIRECTION_DES_AFFAIRES_MARITIMES_ = new Agency("DJ", 820);
        /** Dominica, Not known (2008-10-16) */
        public static final Agency DOMINICA_NOT_KNOWN = new Agency("DM", 830);
        /** El Salvador, Gerente de Geodesia, Centro Nacional de Registros, Instituto Geografico y del Catastro Nacional (2008-10-16) */
        public static final Agency EL_SALVADOR_GERENTE_DE_GEODESIA_CENTRO_NACIONAL_DE_REGISTROS_INSTITUTO_GEOGRAFICO_Y_DEL_CATASTRO_NACIONAL = new Agency("SV", 840);
        /** Equatorial Guinea, Ministry of Transportation and Civil Aviation (2008-10-16) */
        public static final Agency EQUATORIAL_GUINEA_MINISTRY_OF_TRANSPORTATION_AND_CIVIL_AVIATION = new Agency("GQ", 850);
        /** Eritrea, Department of Marine Transport (2008-10-16) */
        public static final Agency ERITREA_DEPARTMENT_OF_MARINE_TRANSPORT = new Agency("ER", 860);
        /** Ethiopia, Ministry of Transport and Communications Marine Transport Authority (2008-10-16) */
        public static final Agency ETHIOPIA_MINISTRY_OF_TRANSPORT_AND_COMMUNICATIONS_MARINE_TRANSPORT_AUTHORITY = new Agency("ET", 880);
        /** Gabon, Direction Générale de la Marine Marchande (2008-10-16) */
        public static final Agency GABON_DIRECTION_GÉNÉRALE_DE_LA_MARINE_MARCHANDE = new Agency("GA", 890);
        /** Gambia, Gambia Ports Authority (2008-10-16) */
        public static final Agency GAMBIA_GAMBIA_PORTS_AUTHORITY = new Agency("GM", 900);
        /** Georgia, State Hydrographic Service of Georgia (2008-10-16) */
        public static final Agency GEORGIA_STATE_HYDROGRAPHIC_SERVICE_OF_GEORGIA = new Agency("GE", 905);
        /** Ghana, Ghana Ports and Harbours Authority (2008-10-16) */
        public static final Agency GHANA_GHANA_PORTS_AND_HARBOURS_AUTHORITY = new Agency("GH", 910);
        /** Grenada, Grenada Ports Authority (2008-10-16) */
        public static final Agency GRENADA_GRENADA_PORTS_AUTHORITY = new Agency("GD", 920);
        /** Guinea, Port Autonome de Conakry (2008-10-16) */
        public static final Agency GUINEA_PORT_AUTONOME_DE_CONAKRY = new Agency("GN", 930);
        /** Guinea-Bissau, Administração dos Portos da Guiné-Bissau (2008-10-16) */
        public static final Agency GUINEA_BISSAU_ADMINISTRAÇÃO_DOS_PORTOS_DA_GUINÉ_BISSAU = new Agency("GW", 940);
        /** Guyana, Maritime Administration Department Hydrographic Office (2008-10-16) */
        public static final Agency GUYANA_MARITIME_ADMINISTRATION_DEPARTMENT_HYDROGRAPHIC_OFFICE = new Agency("GY", 950);
        /** Haiti, Service Maritime et de Navigation d’Haïti (2008-10-16) */
        public static final Agency HAITI_SERVICE_MARITIME_ET_DE_NAVIGATION_DHAÏTI = new Agency("HT", 960);
        /** Honduras, Empresa Nacional Portuaria (2008-10-16) */
        public static final Agency HONDURAS_EMPRESA_NACIONAL_PORTUARIA = new Agency("HN", 970);
        /** Iraq, Marine Department, General Company for Iraki Ports (2008-10-16) */
        public static final Agency IRAQ_MARINE_DEPARTMENT_GENERAL_COMPANY_FOR_IRAKI_PORTS = new Agency("IQ", 980);
        /** Israel, Administration of Shipping and Ports (2008-10-16) */
        public static final Agency ISRAEL_ADMINISTRATION_OF_SHIPPING_AND_PORTS = new Agency("IL", 1000);
        /** Israel, Survey of Israel (2008-10-16) */
        public static final Agency ISRAEL_SURVEY_OF_ISRAEL = new Agency("I1", 1001);
        /** Israel, Israel Navy (2008-10-16) */
        public static final Agency ISRAEL_ISRAEL_NAVY = new Agency("I2", 1002);
        /** Jordan, The Ports Corporation, Jordan (2008-10-16) */
        public static final Agency JORDAN_THE_PORTS_CORPORATION_JORDAN = new Agency("JO", 1020);
        /** Kenya, Survey of Kenya, Kenya Ports Authority (2008-10-16) */
        public static final Agency KENYA_SURVEY_OF_KENYA_KENYA_PORTS_AUTHORITY = new Agency("KE", 1030);
        /** Kiribati, Ministry of Transport and Communications (2008-10-16) */
        public static final Agency KIRIBATI_MINISTRY_OF_TRANSPORT_AND_COMMUNICATIONS = new Agency("KI", 1040);
        /** Lebanon, Ministry of Public Works & Transport (2008-10-16) */
        public static final Agency LEBANON_MINISTRY_OF_PUBLIC_WORKS_AND_TRANSPORT = new Agency("LB", 1070);
        /** Liberia, Ministry of Lands, Mines and Energy (2008-10-16) */
        public static final Agency LIBERIA_MINISTRY_OF_LANDS_MINES_AND_ENERGY = new Agency("LR", 1080);
        /** Libyan Arab Jamahiriya, Not known (2008-10-16) */
        public static final Agency LIBYAN_ARAB_JAMAHIRIYA_NOT_KNOWN = new Agency("LY", 1090);
        /** Lithuania, Lithuanian Maritime Safety Administration (2008-10-16) */
        public static final Agency LITHUANIA_LITHUANIAN_MARITIME_SAFETY_ADMINISTRATION = new Agency("LT", 1100);
        /** Madagascar, Institut Géographique et Hydrographique National (2008-10-16) */
        public static final Agency MADAGASCAR_INSTITUT_GÉOGRAPHIQUE_ET_HYDROGRAPHIQUE_NATIONAL = new Agency("MG", 1110);
        /** Malawi, Hydrographic Survey Unit (2008-10-16) */
        public static final Agency MALAWI_HYDROGRAPHIC_SURVEY_UNIT = new Agency("MW", 1120);
        /** Malawi, Marine Department (2008-10-16) */
        public static final Agency MALAWI_MARINE_DEPARTMENT = new Agency("M2", 1121);
        /** Maldives, Department of Information and Broadcasting (2008-10-16) */
        public static final Agency MALDIVES_DEPARTMENT_OF_INFORMATION_AND_BROADCASTING = new Agency("MV", 1130);
        /** Malta, Malta Maritime Authority Ports Directorate, Hydrographic Unit (2008-10-16) */
        public static final Agency MALTA_MALTA_MARITIME_AUTHORITY_PORTS_DIRECTORATE_HYDROGRAPHIC_UNIT = new Agency("MT", 1140);
        /** Marshall Islands, Ministry of Resources and Development (2008-10-16) */
        public static final Agency MARSHALL_ISLANDS_MINISTRY_OF_RESOURCES_AND_DEVELOPMENT = new Agency("MH", 1150);
        /** Mauritania , Ministère de la Défense Nationale (2008-10-16) */
        public static final Agency MAURITANIA_MINISTÈRE_DE_LA_DÉFENSE_NATIONALE = new Agency("MR", 1160);
        /** Micronesia (Federated States of), Not known (2008-10-16) */
        public static final Agency MICRONESIA_NOT_KNOWN = new Agency("FM", 1190);
        /** Montenegro, Ministry of Defence, Navy Headquarters (2008-10-16) */
        public static final Agency MONTENEGRO_MINISTRY_OF_DEFENCE_NAVY_HEADQUARTERS = new Agency("ME", 1225);
        /** Montserrat, Montserrat Port Authority (2008-10-16) */
        public static final Agency MONTSERRAT_MONTSERRAT_PORT_AUTHORITY = new Agency("M3", 1197);
        /** Namibia, Ministry of Works, Transports and Communications (2008-10-16) */
        public static final Agency NAMIBIA_MINISTRY_OF_WORKS_TRANSPORTS_AND_COMMUNICATIONS = new Agency("NA", 1230);
        /** Nauru, Nauru Phosphate Corporation (2008-10-16) */
        public static final Agency NAURU_NAURU_PHOSPHATE_CORPORATION = new Agency("NR", 1240);
        /** Nicaragua, Ministero de la Presidencia, Instituto Nicaragüense de Estudios Territoriales, Dirección de Recursos Hídricos, Departamento de Hidrografía (2008-10-16) */
        public static final Agency NICARAGUA_MINISTERO_DE_LA_PRESIDENCIA_INSTITUTO_NICARAGÜENSE_DE_ESTUDIOS_TERRITORIALES_DIRECCIÓN_DE_RECURSOS_HÍDRICOS_DEPARTAMENTO_DE_HIDROGRAFÍA = new Agency("NI", 1250);
        /** Niue , Lands and Survey Division (2008-10-16) */
        public static final Agency NIUE_LANDS_AND_SURVEY_DIVISION = new Agency("NU", 1255);
        /** Palau, Bureau of Domestic Affairs (2008-10-16) */
        public static final Agency PALAU_BUREAU_OF_DOMESTIC_AFFAIRS = new Agency("PW", 1260);
        /** Panama, Autoridad Maritima de Panama (2008-10-16) */
        public static final Agency PANAMA_AUTORIDAD_MARITIMA_DE_PANAMA = new Agency("PA", 1270);
        /** Paraguay, Fuerzas Armadas de la Nacion, Armada Paraguaya, Comando de apoyo de combate (2008-10-16) */
        public static final Agency PARAGUAY_FUERZAS_ARMADAS_DE_LA_NACION_ARMADA_PARAGUAYA_COMANDO_DE_APOYO_DE_COMBATE = new Agency("PY", 1280);
        /** Saint Kitts and Nevis, St. Christopher Air and Sea Ports Authority, Maritime Division (2008-10-16) */
        public static final Agency SAINT_KITTS_AND_NEVIS_AIR_AND_SEA_PORTS_AUTHORITY_MARITIME_DIVISION = new Agency("KN", 1310);
        /** Saint Lucia, Saint Lucia Air and Sea Ports Authority, Division of Maritime Affairs (2008-10-16) */
        public static final Agency SAINT_LUCIA_SAINT_LUCIA_AIR_AND_SEA_PORTS_AUTHORITY_DIVISION_OF_MARITIME_AFFAIRS = new Agency("LC", 1320);
        /** Saint Vincent and the Grenadines, Ministry of Communications and Works (2008-10-16) */
        public static final Agency SAINT_VINCENT_AND_THE_GRENADINES_MINISTRY_OF_COMMUNICATIONS_AND_WORKS = new Agency("VC", 1330);
        /** Samoa, Ministry of Transport, Marine and Shipping Division (2008-10-16) */
        public static final Agency SAMOA_MINISTRY_OF_TRANSPORT_MARINE_AND_SHIPPING_DIVISION = new Agency("WS", 1340);
        /** Sao Tome and Principe, Not known (2008-10-16) */
        public static final Agency SAO_TOME_AND_PRINCIPE_NOT_KNOWN = new Agency("ST", 1350);
        /** Senegal, Service de sécurité maritime du Sénégal, Port autonome de Dakar (2008-10-16) */
        public static final Agency SENEGAL_SERVICE_DE_SÉCURITÉ_MARITIME_DU_SÉNÉGAL_PORT_AUTONOME_DE_DAKAR = new Agency("SN", 1370);
        /** Seychelles, Hydrographic and Topographic Brigade of the Seychelles (2008-10-16) */
        public static final Agency SEYCHELLES_HYDROGRAPHIC_AND_TOPOGRAPHIC_BRIGADE_OF_THE_SEYCHELLES = new Agency("SC", 1380);
        /** Sierra Leone, Sierra Leone Maritime Administration, Sierra Leone Ports Authority (2008-10-16) */
        public static final Agency SIERRA_LEONE_SIERRA_LEONE_MARITIME_ADMINISTRATION_SIERRA_LEONE_PORTS_AUTHORITY = new Agency("SL", 1390);
        /** Solomon Islands, Solomon Islands Hydrographic Office (SIHO) (2008-10-16) */
        public static final Agency SOLOMON_ISLANDS_SIHO = new Agency("SB", 1410);
        /** Somalia, Somali Hydrographic Office (2008-10-16) */
        public static final Agency SOMALIA_SOMALI_HYDROGRAPHIC_OFFICE = new Agency("SO", 1420);
        /** Sudan, Survey Department (2008-10-16) */
        public static final Agency SUDAN_SURVEY_DEPARTMENT = new Agency("SD", 1430);
        /** Tanzania, Hydrographic Surveys Section, Surveys and Mapping Division, Ministry of Lands, Housing and Human Settlements Development  (2008-10-16) */
        public static final Agency TANZANIA_HYDROGRAPHIC_SURVEYS_SECTION_SURVEYS_AND_MAPPING_DIVISION_MINISTRY_OF_LANDS_HOUSING_AND_HUMAN_SETTLEMENTS_DEVELOPMENT_ = new Agency("TZ", 1440);
        /** Tanzania, Tanzania Ports Authority (TPA) (2008-10-16) */
        public static final Agency TANZANIA_TPA = new Agency("T1", 1441);
        /** The Cayman Islands, Governor’s Office (2008-10-16) */
        public static final Agency THE_CAYMAN_ISLANDS_GOVERNORS_OFFICE = new Agency("KY", 755);
        /** Togo, University of Benin, Research Department (2008-10-16) */
        public static final Agency TOGO_UNIVERSITY_OF_BENIN_RESEARCH_DEPARTMENT = new Agency("TG", 1450);
        /** Tokelau, Not known (2008-10-16) */
        public static final Agency TOKELAU_NOT_KNOWN = new Agency("TK", 1460);
        /** Turks & Caicos Islands, Governor’s Office (2008-10-16) */
        public static final Agency TURKS_AND_CAICOS_ISLANDS_GOVERNORS_OFFICE = new Agency("TC", 1475);
        /** Tuvalu, Ministry of Labour, Works and Communications (2008-10-16) */
        public static final Agency TUVALU_MINISTRY_OF_LABOUR_WORKS_AND_COMMUNICATIONS = new Agency("TV", 1480);
        /** Uganda, Commissioner for Transport Regulation (2008-10-16) */
        public static final Agency UGANDA_COMMISSIONER_FOR_TRANSPORT_REGULATION = new Agency("UG", 1485);
        /** Vanuatu, Vanuatu Hydrographic Unit (2008-10-16) */
        public static final Agency VANUATU_VANUATU_HYDROGRAPHIC_UNIT = new Agency("VU", 1500);
        /** Viet Nam, Viet Nam Maritime Safety Agency (VMSA-1) (2008-10-16) */
        public static final Agency VIET_NAM_VMSA_1 = new Agency("VN", 1510);
        /** Viet Nam, Viet Nam Maritime Safety Agency (VMSA-2) (2008-10-16) */
        public static final Agency VIET_NAM_VMSA_2 = new Agency("V1", 1511);
        /** Yemen, Ministry of Transport, Yemen Ports Authority, Maritime Affairs Authority (2008-10-16) */
        public static final Agency YEMEN_MINISTRY_OF_TRANSPORT_YEMEN_PORTS_AUTHORITY_MARITIME_AFFAIRS_AUTHORITY = new Agency("YE", 1520);

        ////////////////////////////////////////////////////////////////////////
        // SUPPLEMENTARY PRODUCERS /////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        /** A.F.D.J. R.A. Galati (2008-10-16) */
        public static final Agency AFDJ_RA_GALATI = new Agency("3R", 16203);
        /** Abris, Llc (2011-10-20) */
        public static final Agency ABRIS_LLC = new Agency("8T", 36363);
        /** ADVETO Advanced Technology AB (2008-10-16) */
        public static final Agency ADVETO_ADVANCED_TECHNOLOGY_AB = new Agency("4S", 20316);
        /** AEMDR, Rousse, Bulgaria (2008-10-16) */
        public static final Agency AEMDR_ROUSSE_BULGARIA = new Agency("3B", 15163);
        /** Aero Karta Complex Ltd (2011-10-18) */
        public static final Agency AERO_KARTA_COMPLEX_LTD = new Agency("3Z", 16038);
        /** AMEC (2008-10-16) */
        public static final Agency AMEC = new Agency("6A", 27242);
        /** American Commercial Lines (ACL), Inc. (2008-10-16) */
        public static final Agency ACL = new Agency("5A", 23130);
        /** Amt fuer Geoinformationswesen der Bundeswehr (2008-10-16) */
        public static final Agency AMT_FUER_GEOINFORMATIONSWESEN_DER_BUNDESWEHR = new Agency("1D", 7453);
        /** Antarctic Treaty Consultative Committee (2008-10-16) */
        public static final Agency ANTARCTIC_TREATY_CONSULTATIVE_COMMITTEE = new Agency("QM", 1600);
        /** ARAMCO (2008-10-16) */
        public static final Agency ARAMCO = new Agency("1A", 6682);
        /** Arctic and Antarctic Research Institute (AARI) of the Russian Federal Service for Hydrometeorology and Environmental Monitoring (Roshydromet) (2008-10-16) */
        public static final Agency AARI = new Agency("4A", 19018);
        /** Austrian Supreme Shipping Authority (2008-10-16) */
        public static final Agency AUSTRIAN_SUPREME_SHIPPING_AUTHORITY = new Agency("1S", 7980);
        /** Azerbaijan Navy, Service of Navigation and Oceanography  (2008-10-16) */
        public static final Agency AZERBAIJAN_NAVY_SERVICE_OF_NAVIGATION_AND_OCEANOGRAPHY_ = new Agency("7A", 31354);
        /** Azienda Regionale Navigazione Interna (ARNI) (2008-10-16) */
        public static final Agency ARNI = new Agency("2A", 10794);
        /** Azovo-Donskoe State Basin Waterway and Shipping Authority (2008-10-16) */
        public static final Agency AZOVO_DONSKOE_STATE_BASIN_WATERWAY_AND_SHIPPING_AUTHORITY = new Agency("3A", 14906);
        /** BaikalChart, Russia (2008-10-16) */
        public static final Agency BAIKALCHART_RUSSIA = new Agency("4B", 19275);
        /** BMT ARGOSS Ltd (2011-03-21) */
        public static final Agency BMT_ARGOSS_LTD = new Agency("0M", 3817);
        /** Bundesanstalt für Wasserbau, Karlsruhe (2008-10-16) */
        public static final Agency BUNDESANSTALT_FÜR_WASSERBAU_KARLSRUHE = new Agency("2B", 11051);
        /** C-Map Russia (2008-10-16) */
        public static final Agency C_MAP_RUSSIA = new Agency("4Z", 20323);
        /** C-Tech SRL, Romania (2008-10-16) */
        public static final Agency C_TECH_SRL_ROMANIA = new Agency("6B", 27499);
        /** Canadian Coast Guard (2008-10-16) */
        public static final Agency CANADIAN_COAST_GUARD = new Agency("1G", 7968);
        /** Canadian Ice Service (2008-10-16) */
        public static final Agency CANADIAN_ICE_SERVICE = new Agency("4I", 20306);
        /** CARIS (2008-10-16) */
        public static final Agency CARIS = new Agency("1C", 7196);
        /** Center for Coastal & Ocean Mapping/Joint Hydrographic Center, University of New Hampshire (2008-10-16) */
        public static final Agency CENTER_FOR_COASTAL_AND_OCEAN_MAPPING_JOINT_HYDROGRAPHIC_CENTER_UNIVERSITY_OF_NEW_HAMPSHIRE = new Agency("4U", 20318);
        /** Centre Sevzapgeoinform (SZGI) (2008-10-16) */
        public static final Agency SZGI = new Agency("7S", 32652);
        /** Channel of Moscow (2008-10-16) */
        public static final Agency CHANNEL_OF_MOSCOW = new Agency("1M", 7974);
        /** Chart Pilot Ltd. (2011-10-17) */
        public static final Agency CHART_PILOT_LTD = new Agency("2G", 11973);
        /** ChartCo Limited (2011-10-05) */
        public static final Agency CHARTCO_LIMITED = new Agency("7J", 32298);
        /** Chartworld Gmbh (2008-10-16) */
        public static final Agency CHARTWORLD_GMBH = new Agency("9C", 40092);
        /** CherSoft Ltd (2008-10-16) */
        public static final Agency CHERSOFT_LTD = new Agency("9A", 39578);
        /** Comite International Radio-Maritime (2008-10-16) */
        public static final Agency COMITE_INTERNATIONAL_RADIO_MARITIME = new Agency("QO", 1620);
        /** Command & Control Technologies GmbH (2008-10-16) */
        public static final Agency COMMAND_AND_CONTROL_TECHNOLOGIES_GMBH = new Agency("3C", 15420);
        /** CRUP d.o.o., Croatia (2008-10-16) */
        public static final Agency CRUP_DOO_CROATIA = new Agency("5C", 23644);
        /** Development Centre for Ship Technology and Transport Systems, Germany (2008-10-16) */
        public static final Agency DEVELOPMENT_CENTRE_FOR_SHIP_TECHNOLOGY_AND_TRANSPORT_SYSTEMS_GERMANY = new Agency("3E", 15934);
        /** Digital Geographic Information Working Group (2008-10-16) */
        public static final Agency DIGITAL_GEOGRAPHIC_INFORMATION_WORKING_GROUP = new Agency("QQ", 1640);
        /** DMER, Zagreb (2008-10-16) */
        public static final Agency DMER_ZAGREB = new Agency("2H", 12055);
        /** drait (2013-01-16) */
        public static final Agency DRAIT = new Agency("0A", 3860);
        /** e-MLX, Korea (2008-10-16) */
        public static final Agency E_MLX_KOREA = new Agency("2X", 12097);
        /** ENC Center, National Taiwan Ocean University (2008-10-16) */
        public static final Agency ENC_CENTER_NATIONAL_TAIWAN_OCEAN_UNIVERSITY = new Agency("1U", 7982);
        /** Environmental Systems Research Institute (ESRI) (2008-10-16) */
        public static final Agency ESRI = new Agency("4E", 20046);
        /** Euronav Ltd UK (2008-10-16) */
        public static final Agency EURONAV_LTD_UK = new Agency("2E", 11822);
        /** European Communities Commission (2008-10-16) */
        public static final Agency EUROPEAN_COMMUNITIES_COMMISSION = new Agency("QR", 1650);
        /** European Harbour Masters Association (2008-10-16) */
        public static final Agency EUROPEAN_HARBOUR_MASTERS_ASSOCIATION = new Agency("QS", 1660);
        /** European Inland ECDIS Expert Group (2011-04-07) */
        public static final Agency EUROPEAN_INLAND_ECDIS_EXPERT_GROUP = new Agency("5I", 24418);
        /** Fachstelle fuer Geoinformationen Sued beim WSA Regensburg (2008-10-16) */
        public static final Agency FACHSTELLE_FUER_GEOINFORMATIONEN_SUED_BEIM_WSA_REGENSBURG = new Agency("4W", 20320);
        /** Federation Internationale des Geometres (2008-10-16) */
        public static final Agency FEDERATION_INTERNATIONALE_DES_GEOMETRES = new Agency("QU", 1680);
        /** Finnish Navy (2008-10-16) */
        public static final Agency FINNISH_NAVY = new Agency("3F", 16191);
        /** Food and Agriculture Organization (2008-10-16) */
        public static final Agency FOOD_AND_AGRICULTURE_ORGANIZATION = new Agency("QT", 1670);
        /** Force Technology, Danish Maritime Institute (2008-10-16) */
        public static final Agency FORCE_TECHNOLOGY_DANISH_MARITIME_INSTITUTE = new Agency("1F", 7967);
        /** FPAEMDR DRIIREST (2011-07-18) */
        public static final Agency FPAEMDR_DRIIREST = new Agency("3D", 16038);
        /** Guoy Consultancy Sdn Bhd (2008-10-16) */
        public static final Agency GUOY_CONSULTANCY_SDN_BHD = new Agency("6C", 27756);
        /** Hamburg Port Authority (2008-10-16) */
        public static final Agency HAMBURG_PORT_AUTHORITY = new Agency("9H", 40865);
        /** Hochschule Bremen (Nautik) (2008-10-16) */
        public static final Agency NAUTIK = new Agency("2N", 12061);
        /** HSA Systems Pty Ltd (2008-10-16) */
        public static final Agency HSA_SYSTEMS_PTY_LTD = new Agency("8A", 35466);
        /** Hydrographic Office of the Sarawak Marine Department (2008-10-16) */
        public static final Agency HYDROGRAPHIC_OFFICE_OF_THE_SARAWAK_MARINE_DEPARTMENT = new Agency("5M", 24422);
        /** HYPACK, Inc. (2008-10-16) */
        public static final Agency HYPACK_INC = new Agency("3H", 16193);
        /** ICAN (2008-10-16) */
        public static final Agency ICAN = new Agency("3I", 16194);
        /** IHO Data Centre for Digital Bathymetry (2008-10-16) */
        public static final Agency IHO_DATA_CENTRE_FOR_DIGITAL_BATHYMETRY = new Agency("QP", 1630);
        /** IIC Technologies (2008-10-16) */
        public static final Agency IIC_TECHNOLOGIES = new Agency("2C", 11308);
        /** Innovative Navigation GmbH (2008-10-16) */
        public static final Agency INNOVATIVE_NAVIGATION_GMBH = new Agency("2I", 12056);
        /** Intergovernmental Oceanographic Commission (2008-10-16) */
        public static final Agency INTERGOVERNMENTAL_OCEANOGRAPHIC_COMMISSION = new Agency("XK", 1850);
        /** International Association of Geodesy (2008-10-16) */
        public static final Agency INTERNATIONAL_ASSOCIATION_OF_GEODESY = new Agency("QW", 1700);
        /** International Association of Institutes of Navigation (2008-10-16) */
        public static final Agency INTERNATIONAL_ASSOCIATION_OF_INSTITUTES_OF_NAVIGATION = new Agency("QX", 1710);
        /** International Association of Lighthouse Authorities (2008-10-16) */
        public static final Agency INTERNATIONAL_ASSOCIATION_OF_LIGHTHOUSE_AUTHORITIES = new Agency("QY", 1720);
        /** International Association of Ports and Harbours (2008-10-16) */
        public static final Agency INTERNATIONAL_ASSOCIATION_OF_PORTS_AND_HARBOURS = new Agency("QZ", 1730);
        /** International Atomic Energy Agency (2008-10-16) */
        public static final Agency INTERNATIONAL_ATOMIC_ENERGY_AGENCY = new Agency("QV", 1690);
        /** International Cable Protection Committee (2008-10-16) */
        public static final Agency INTERNATIONAL_CABLE_PROTECTION_COMMITTEE = new Agency("XB", 1750);
        /** International Cartographic Association (2008-10-16) */
        public static final Agency INTERNATIONAL_CARTOGRAPHIC_ASSOCIATION = new Agency("XA", 1740);
        /** International Centre for ENC (IC-ENC) (2008-10-16) */
        public static final Agency IC_ENC = new Agency("IC", 2030);
        /** International Chamber of Shipping (2008-10-16) */
        public static final Agency INTERNATIONAL_CHAMBER_OF_SHIPPING = new Agency("XC", 1760);
        /** International Commission for the Scientific Exploration of the Mediterranean (2008-10-16) */
        public static final Agency INTERNATIONAL_COMMISSION_FOR_THE_SCIENTIFIC_EXPLORATION_OF_THE_MEDITERRANEAN = new Agency("XD", 1770);
        /** International Council of Scientific Unions (2008-10-16) */
        public static final Agency INTERNATIONAL_COUNCIL_OF_SCIENTIFIC_UNIONS = new Agency("XE", 1780);
        /** International Electrotechnical Commission (2008-10-16) */
        public static final Agency INTERNATIONAL_ELECTROTECHNICAL_COMMISSION = new Agency("XF", 1790);
        /** International Geographical Union (2008-10-16) */
        public static final Agency INTERNATIONAL_GEOGRAPHICAL_UNION = new Agency("XG", 1800);
        /** International Maritime Academy (2008-10-16) */
        public static final Agency INTERNATIONAL_MARITIME_ACADEMY = new Agency("XH", 1820);
        /** International Maritime Organization (2008-10-16) */
        public static final Agency INTERNATIONAL_MARITIME_ORGANIZATION = new Agency("XI", 1830);
        /** International Maritime Satellite Organization (2008-10-16) */
        public static final Agency INTERNATIONAL_MARITIME_SATELLITE_ORGANIZATION = new Agency("XJ", 1840);
        /** International Organization for Standardization (2008-10-16) */
        public static final Agency INTERNATIONAL_ORGANIZATION_FOR_STANDARDIZATION = new Agency("XL", 1860);
        /** International Radio Consultative Committee (2008-10-16) */
        public static final Agency INTERNATIONAL_RADIO_CONSULTATIVE_COMMITTEE = new Agency("QN", 1610);
        /** International Society for Photogrammetry and Remote Sensing (2008-10-16) */
        public static final Agency INTERNATIONAL_SOCIETY_FOR_PHOTOGRAMMETRY_AND_REMOTE_SENSING = new Agency("XM", 1870);
        /** International Telecommunication Union (2008-10-16) */
        public static final Agency INTERNATIONAL_TELECOMMUNICATION_UNION = new Agency("XN", 1880);
        /** International Union of Geodesy and Geophysics (2008-10-16) */
        public static final Agency INTERNATIONAL_UNION_OF_GEODESY_AND_GEOPHYSICS = new Agency("XO", 1890);
        /** International Union of Surveying and Mapping (2008-10-16) */
        public static final Agency INTERNATIONAL_UNION_OF_SURVEYING_AND_MAPPING = new Agency("XP", 1900);
        /** Jeppesen Marine (2008-10-16) */
        public static final Agency JEPPESEN_MARINE = new Agency("2J", 12083);
        /** JS Co Geocentre-Consulting, Moscow (2008-10-16) */
        public static final Agency JS_CO_GEOCENTRE_CONSULTING_MOSCOW = new Agency("1Z", 7987);
        /** Kamvodput (2008-10-16) */
        public static final Agency KAMVODPUT = new Agency("2K", 12084);
        /** Kingway Technology Co (2008-10-16) */
        public static final Agency KINGWAY_TECHNOLOGY_CO = new Agency("1K", 7972);
        /** Land Information New Zealand Hydrographic Services (2008-10-16) */
        public static final Agency LAND_INFORMATION_NEW_ZEALAND_HYDROGRAPHIC_SERVICES = new Agency("2Z", 12099);
        /** Laser-Scan Ltd (2008-10-16) */
        public static final Agency LASER_SCAN_LTD = new Agency("1L", 7973);
        /** Latincomp (2011-03-31) */
        public static final Agency LATINCOMP = new Agency("7L", 32264);
        /** MARIN (Maritime Research Institute Netherlands) (2008-10-16) */
        public static final Agency MARITIME_RESEARCH_INSTITUTE_NETHERLANDS = new Agency("2M", 12060);
        /** MD Atlantic Technologies (2008-10-16) */
        public static final Agency MD_ATLANTIC_TECHNOLOGIES = new Agency("4R", 20315);
        /** MeteoConsult (2008-10-16) */
        public static final Agency METEOCONSULT = new Agency("4N", 20311);
        /** National Navigation Authority of the Czech Republic (2011-04-06) */
        public static final Agency NATIONAL_NAVIGATION_AUTHORITY_OF_THE_CZECH_REPUBLIC = new Agency("9D", 39624);
        /** Nautical Data International, Inc. (2008-10-16) */
        public static final Agency NAUTICAL_DATA_INTERNATIONAL_INC = new Agency("1N", 7975);
        /** Navionics S.p.A. (2008-10-16) */
        public static final Agency NAVIONICS_SPA = new Agency("1I", 7970);
        /** Navionics test and sample datasets (2008-10-16) */
        public static final Agency NAVIONICS_TEST_AND_SAMPLE_DATASETS = new Agency("1J", 7971);
        /** Navtor AS (2012-02-11) */
        public static final Agency NAVTOR_AS = new Agency("6N", 28233);
        /** NAVTRON SRL (2008-10-16) */
        public static final Agency NAVTRON_SRL = new Agency("5N", 24423);
        /** Nobeltec, Inc (2008-10-16) */
        public static final Agency NOBELTEC_INC = new Agency("9Z", 40883);
        /** Noorderzon Software (2008-10-16) */
        public static final Agency NOORDERZON_SOFTWARE = new Agency("1X", 7985);
        /** nv De Scheepvaart (2008-10-16) */
        public static final Agency NV_DE_SCHEEPVAART = new Agency("7V", 32655);
        /** Ocean Surveys Inc. (2008-10-16) */
        public static final Agency OCEAN_SURVEYS_INC = new Agency("3O", 16200);
        /** Offshore Systems Ltd. (2008-10-16) */
        public static final Agency OFFSHORE_SYSTEMS_LTD = new Agency("1O", 7976);
        /** Oil Companies International Marine Forum (2008-10-16) */
        public static final Agency OIL_COMPANIES_INTERNATIONAL_MARINE_FORUM = new Agency("XQ", 1910);
        /** OOO Tekhpromcomplect (2008-10-16) */
        public static final Agency OOO_TEKHPROMCOMPLECT = new Agency("4T", 20317);
        /** Pan American Institute of Geography and History (2008-10-16) */
        public static final Agency PAN_AMERICAN_INSTITUTE_OF_GEOGRAPHY_AND_HISTORY = new Agency("XR", 1920);
        /** Panama Canal Authority (2012-07-31) */
        public static final Agency PANAMA_CANAL_AUTHORITY = new Agency("6P", 28243);
        /** Pechora Waterways and Navigation Board (2008-10-16) */
        public static final Agency PECHORA_WATERWAYS_AND_NAVIGATION_BOARD = new Agency("3P", 16201);
        /** Petroslav Hydroservice, Russia (2008-10-16) */
        public static final Agency PETROSLAV_HYDROSERVICE_RUSSIA = new Agency("7H", 32641);
        /** PLOVPUT Beograd (2008-10-16) */
        public static final Agency PLOVPUT_BEOGRAD = new Agency("2P", 12063);
        /** Port Of London Authority (2008-10-16) */
        public static final Agency PORT_OF_LONDON_AUTHORITY = new Agency("1P", 7977);
        /** Port of Rotterdam (2008-10-16) */
        public static final Agency PORT_OF_ROTTERDAM = new Agency("2R", 12065);
        /** PRIMAR - European ENC Coordinating Centre (2008-10-16) */
        public static final Agency PRIMAR_EUROPEAN_ENC_COORDINATING_CENTRE = new Agency("PM", 2020);
        /** Public Works and Government Services Canada - Pacific Region (2011-03-30) */
        public static final Agency PUBLIC_WORKS_AND_GOVERNMENT_SERVICES_CANADA_PACIFIC_REGION = new Agency("4P", 16012);
        /** Quality Positioning Services (2008-10-16) */
        public static final Agency QUALITY_POSITIONING_SERVICES = new Agency("1Q", 7978);
        /** Radio Technical Commission for Maritime Services (2008-10-16) */
        public static final Agency RADIO_TECHNICAL_COMMISSION_FOR_MARITIME_SERVICES = new Agency("XS", 1930);
        /** Rheinschifffahrtsdirektion (RSD) Basel (2008-10-16) */
        public static final Agency RSD = new Agency("4C", 19532);
        /** Rijkswaterstaat (2008-10-16) */
        public static final Agency RIJKSWATERSTAAT = new Agency("1R", 7979);
        /** River Transport Authority (RTA), Egypt (2008-10-16) */
        public static final Agency RTA = new Agency("5E", 24158);
        /** Safe Trip SA, Argentina (2008-10-16) */
        public static final Agency SAFE_TRIP_SA_ARGENTINA = new Agency("5P", 24425);
        /** Science Applications International Corp (2008-10-16) */
        public static final Agency SCIENCE_APPLICATIONS_INTERNATIONAL_CORP = new Agency("3S", 16204);
        /** Scientific Commission on Antarctic Research (2008-10-16) */
        public static final Agency SCIENTIFIC_COMMISSION_ON_ANTARCTIC_RESEARCH = new Agency("XT", 1940);
        /** SeaZone Solutions (2012-03-23) */
        public static final Agency SEAZONE_SOLUTIONS = new Agency("5Z", 24168);
        /** Seebyte Ltd. (2008-10-16) */
        public static final Agency SEEBYTE_LTD = new Agency("8C", 35980);
        /** SevenCs AG & Co KG (2008-10-16) */
        public static final Agency SEVENCS_AG_AND_CO_KG = new Agency("7C", 31868);
        /** SHOM test data (2013-03-11) */
        public static final Agency SHOM_TEST_DATA = new Agency("4L", 20127);
        /** Solutions from Silicon, Sydney (2008-10-16) */
        public static final Agency SOLUTIONS_FROM_SILICON_SYDNEY = new Agency("9S", 40876);
        /** Ssangyong Information & Communications Corp. (2008-10-16) */
        public static final Agency SSANGYONG_INFORMATION_AND_COMMUNICATIONS_CORP = new Agency("2S", 12079);
        /** State Federal Unitary Enterprise NW Regional Production Centre of Geoinformation and Mine Surveying Centre, "Sevzapgeoinform" (Russia) (2008-10-16) */
        public static final Agency RUSSIA = new Agency("5R", 24427);
        /** SVP, s.p., OZ Bratislava (2008-10-16) */
        public static final Agency SVP_SP_OZ_BRATISLAVA = new Agency("2D", 11565);
        /** TEC Asociados (2008-10-16) */
        public static final Agency TEC_ASOCIADOS = new Agency("5T", 24455);
        /** Tér-Team Ltd., Budapest (2008-10-16) */
        public static final Agency TÉR_TEAM_LTD_BUDAPEST = new Agency("1Y", 7986);
        /** Terra Corp (2008-10-16) */
        public static final Agency TERRA_CORP = new Agency("7T", 32653);
        /** TerraNautical Data (2008-10-16) */
        public static final Agency TERRANAUTICAL_DATA = new Agency("1E", 7710);
        /** The Federal Service of Geodesy and Cartography of Russia (2008-10-16) */
        public static final Agency THE_FEDERAL_SERVICE_OF_GEODESY_AND_CARTOGRAPHY_OF_RUSSIA = new Agency("7R", 32651);
        /** The Hydrographic Society (2008-10-16) */
        public static final Agency THE_HYDROGRAPHIC_SOCIETY = new Agency("XU", 1950);
        /** The Volga State Territorial Department for Waterways (2008-10-16) */
        public static final Agency THE_VOLGA_STATE_TERRITORIAL_DEPARTMENT_FOR_WATERWAYS = new Agency("2V", 12095);
        /** The Volga-Baltic State Territorial Department for Waterways (2008-10-16) */
        public static final Agency THE_VOLGA_BALTIC_STATE_TERRITORIAL_DEPARTMENT_FOR_WATERWAYS = new Agency("1V", 7983);
        /** The Volga-Don Waterways And Navigation Board (2008-10-16) */
        public static final Agency THE_VOLGA_DON_WATERWAYS_AND_NAVIGATION_BOARD = new Agency("3V", 16207);
        /** Transas Marine (2008-10-16) */
        public static final Agency TRANSAS_MARINE = new Agency("2T", 12093);
        /** Tresco Engineering bvba (2008-10-16) */
        public static final Agency TRESCO_ENGINEERING_BVBA = new Agency("9T", 40877);
        /** Tresco Navigation Systems (2008-10-16) */
        public static final Agency TRESCO_NAVIGATION_SYSTEMS = new Agency("3T", 16205);
        /** Tridentnav Systems (2008-10-16) */
        public static final Agency TRIDENTNAV_SYSTEMS = new Agency("3N", 16199);
        /** U.S. Geological Survey (USGS) - Coastal and Marine Geology (2008-10-16) */
        public static final Agency USGS = new Agency("6U", 28542);
        /** UKHO - private production (2008-10-16) */
        public static final Agency UKHO_PRIVATE_PRODUCTION = new Agency("1T", 7981);
        /** UKHO test and sample datasets (2008-10-16) */
        public static final Agency UKHO_TEST_AND_SAMPLE_DATASETS = new Agency("1B", 0);
        /** ULTRANS TM srl (2008-10-16) */
        public static final Agency ULTRANS_TM_SRL = new Agency("2U", 12094);
        /** United Kingdom Royal Navy (2008-10-16) */
        public static final Agency UNITED_KINGDOM_ROYAL_NAVY = new Agency("5U", 24430);
        /** United Nations, Office for Ocean Affairs and Law of the Sea (2008-10-16) */
        public static final Agency UNITED_NATIONS_OFFICE_FOR_OCEAN_AFFAIRS_AND_LAW_OF_THE_SEA = new Agency("XW", 1970);
        /** US Army Corps of Engineers - Channel Condition Data (2008-10-16) */
        public static final Agency US_ARMY_CORPS_OF_ENGINEERS_CHANNEL_CONDITION_DATA = new Agency("3U", 16206);
        /** via donau - ?sterreichische Wasserstrassen-Gesellschaft mbH (2008-10-16) */
        public static final Agency VIA_DONAU_STERREICHISCHE_WASSERSTRASSEN_GESELLSCHAFT_MBH = new Agency("2W", 12096);
        /** Vituki Water Resources Research Centre Hungary (2008-10-16) */
        public static final Agency VITUKI_WATER_RESOURCES_RESEARCH_CENTRE_HUNGARY = new Agency("1H", 7969);
        /** Voies Navigables de France (VNF) (2008-10-16) */
        public static final Agency VNF = new Agency("4V", 20319);
        /** Wasser- und Schiffahrtsdirektion Nord (2008-10-16) */
        public static final Agency WASSER_UND_SCHIFFAHRTSDIREKTION_NORD = new Agency("5W", 24432);
        /** Wasser- und Schiffahrtsverwaltung des Bundes - Direktion SW (2008-10-16) */
        public static final Agency WASSER_UND_SCHIFFAHRTSVERWALTUNG_DES_BUNDES_DIREKTION_SW = new Agency("1W", 7984);
        /** Wasserschutzpolizei-Schule (2008-10-16) */
        public static final Agency WASSERSCHUTZPOLIZEI_SCHULE = new Agency("3W", 16208);
        /** Waterwegen en Zeekanaal (2008-10-16) */
        public static final Agency WATERWEGEN_EN_ZEEKANAAL = new Agency("7W", 32656);
        /** World Meteorological Organization (2008-10-16) */
        public static final Agency WORLD_METEOROLOGICAL_ORGANIZATION = new Agency("XV", 1960);

        Agency(final String ascii, final int binary) {
            super(Agency.class, ascii, binary);
        }

        public static Agency[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new Agency[VALUES.size()]);
            }
        }

        @Override
        public Agency[] family() {
            return values();
        }
        
        public static Agency valueOf(Object code) {
            return (Agency) valueOf(VALUES, code);
        }
    }

    /**
     * Only one vector data structure may be used within a data set. The data
     * structure used must be explicitly encoded in the “Data Structure” [DSTR]
     * subfield of the “Data Set Structure Information” [DSSI] field. This field
     * is part of the “Data Set General Information” record (see clause 7.3.1).
     */
    public static final class DataStructure extends S57CodeList<DataStructure> {

        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 2);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<DataStructure> VALUES = new ArrayList<DataStructure>();
        //---------------
        /**
         * {1} Cartographic spaghetti (see part 2, clause 2.2.1.1)
         */
        public static final DataStructure SPAGHETTI = new DataStructure("CS", 1);
        /**
         * {2} Chain-node (see part 2, clause 2.2.1.2)
         */
        public static final DataStructure CHAINNODE = new DataStructure("CN", 2);
        /**
         * {3} Planar graph (see part 2, clause 2.2.1.3)
         */
        public static final DataStructure GRAPH = new DataStructure("PG", 3);
        /**
         * {4} Full topology (see part 2, clause 2.2.1.4)
         */
        public static final DataStructure FULL = new DataStructure("FT", 4);
        /**
         * {255} Topology is not relevant
         */
        public static final DataStructure NONREV = new DataStructure("NO", 255);

        DataStructure(final String ascii, final int binary) {
            super(DataStructure.class, ascii, binary);
        }

        public static DataStructure[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new DataStructure[VALUES.size()]);
            }
        }

        @Override
        public DataStructure[] family() {
            return values();
        }
        
        public static DataStructure valueOf(Object code) {
            return (DataStructure) valueOf(VALUES, code);
        }
    }

    /**
     * 7.3.11 Lexical Level
     */
    public static final class LexicalLevel extends S57CodeList<LexicalLevel> {

        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<LexicalLevel> VALUES = new ArrayList<LexicalLevel>();
        //---------------
        /**
         * ASCII text, IRV of ISO/IEC 646
         */
        public static final LexicalLevel LEVEL0 = new LexicalLevel("0", 0);
        /**
         * ISO 8859 part 1, Latin alphabet 1 repertoire (i.e. Western European
         * Latin alphabet based languages.
         */
        public static final LexicalLevel LEVEL1 = new LexicalLevel("1", 1);
        /**
         * Universal Character Set repertoire UCS-2 implementation level 1 (no
         * combining characters), Base Multilingual plane of ISO/IEC 10646 (i.e.
         * including Latin alphabet, Greek, Cyrillic, Arabic, Chinese, Japanese
         * etc.)
         */
        public static final LexicalLevel LEVEL2 = new LexicalLevel("2", 2);

        LexicalLevel(final String ascii, final int binary) {
            super(LexicalLevel.class, ascii, binary);
        }

        public static LexicalLevel[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new LexicalLevel[VALUES.size()]);
            }
        }

        @Override
        public LexicalLevel[] family() {
            return values();
        }
        
        public static LexicalLevel valueOf(Object code) {
            return (LexicalLevel) valueOf(VALUES, code);
        }
    }

    /** 
     * The “Relationship indicator” [RIND] subfield is used to indicate the nature of the relationship. 
     */
    public static final class RelationShip extends S57CodeList<RelationShip> {

        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<RelationShip> VALUES = new ArrayList<RelationShip>();
        //---------------
        public static final RelationShip LEVEL0 = new RelationShip("M", 1);
        public static final RelationShip LEVEL1 = new RelationShip("S", 2);
        public static final RelationShip LEVEL2 = new RelationShip("P", 3);

        RelationShip(final String ascii, final int binary) {
            super(RelationShip.class, ascii, binary);
        }

        public static RelationShip[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new RelationShip[VALUES.size()]);
            }
        }

        @Override
        public RelationShip[] family() {
            return values();
        }
        
        public static RelationShip valueOf(Object code) {
            return (RelationShip) valueOf(VALUES, code);
        }
    }
        
    /** Update instruction */
    public static final class UpdateInstruction extends S57CodeList<UpdateInstruction> {
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<UpdateInstruction> VALUES = new ArrayList<UpdateInstruction>();
        //---------------
        /** Insert */
        public static final UpdateInstruction INSERT = new UpdateInstruction("I",1);
        /** Delete */
        public static final UpdateInstruction DELETE = new UpdateInstruction("D",2);
        /** Modify */
        public static final UpdateInstruction MODIFY = new UpdateInstruction("M",3);

        UpdateInstruction(final String ascii, final int binary) {
            super(UpdateInstruction.class, ascii, binary);
        }

        public static UpdateInstruction[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new UpdateInstruction[VALUES.size()]);
            }
        }

        @Override
        public UpdateInstruction[] family() {
            return values();
        }
        
        public static UpdateInstruction valueOf(Object code) {
            return (UpdateInstruction) valueOf(VALUES, code);
        }
    }
    
    /** AttributeSet */
    public static final class AttributeSet extends S57CodeList<AttributeSet> {
        public static final String NAME = "ASET";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<AttributeSet> VALUES = new ArrayList<AttributeSet>();
        //---------------
        /** Atribute set A : Attributes in this subset define the individual 
         * characteristics of an object; */
        public static final AttributeSet SET_A = new AttributeSet("A", 1);
        /** Atribute set B : Attributes in this subset provide information 
         * relevant to the use of the data, e.g. for presentation or for 
         * an information system; */
        public static final AttributeSet SET_B = new AttributeSet("B", 2);
        /** Atribute set C : Attributes in this subset provide administrative 
         * information about the object and the data describing it; */
        public static final AttributeSet SET_C = new AttributeSet("C", 3);

        AttributeSet(final String ascii, final int binary) {
            super(AttributeSet.class, ascii, binary);
        }

        public static AttributeSet[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new AttributeSet[VALUES.size()]);
            }
        }

        @Override
        public AttributeSet[] family() {
            return values();
        }
        
        public static AttributeSet valueOf(Object code) {
            return (AttributeSet) valueOf(VALUES, code);
        }
    }
    
    /** Reference type */
    public static final class ReferenceType extends S57CodeList<ReferenceType> {
        public static final String NAME = "RFTP";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 2);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<ReferenceType> VALUES = new ArrayList<ReferenceType>();
        //---------------
        /** INT 1 : International chart 1, Symbols, Abbreviations, Terms used on charts */
        public static final ReferenceType SET_A = new ReferenceType("I1", 1);
        /** M-4 : Chart specifications of the IHO and Regulations of the IHO for international (INT) charts */
        public static final ReferenceType SET_B = new ReferenceType("M4", 2);

        ReferenceType(final String ascii, final int binary) {
            super(ReferenceType.class, ascii, binary);
        }

        public static ReferenceType[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new ReferenceType[VALUES.size()]);
            }
        }

        @Override
        public ReferenceType[] family() {
            return values();
        }
        
        public static ReferenceType valueOf(Object code) {
            return (ReferenceType) valueOf(VALUES, code);
        }
    }
    
    /** Range or value */
    public static final class RangeOrValue extends S57CodeList<RangeOrValue> {
        public static final String NAME = "RAVA";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<RangeOrValue> VALUES = new ArrayList<RangeOrValue>();
        //---------------
        /** DVAL contains the maximum value */
        public static final RangeOrValue MINIMUM = new RangeOrValue("M", 1);
        /** DVAL contains the minimum value */
        public static final RangeOrValue MAXIMUM = new RangeOrValue("N", 2);
        /** DVAL contains a specific single value from the domain of ATDO */
        public static final RangeOrValue SINGLE = new RangeOrValue("V", 3);

        RangeOrValue(final String ascii, final int binary) {
            super(RangeOrValue.class, ascii, binary);
        }

        public static RangeOrValue[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new RangeOrValue[VALUES.size()]);
            }
        }

        @Override
        public RangeOrValue[] family() {
            return values();
        }
        
        public static RangeOrValue valueOf(Object code) {
            return (RangeOrValue) valueOf(VALUES, code);
        }
    }
    
    /** Attibute domain */
    public static final class AttributeDomain extends S57CodeList<AttributeDomain> {
        public static final String NAME = "ATDO";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<AttributeDomain> VALUES = new ArrayList<AttributeDomain>();
        //---------------
        /** Enumerated */
        public static final AttributeDomain ENUMERATED = new AttributeDomain("E", 1);
        /** List of enumerated */
        public static final AttributeDomain ENUMERATED_LIST = new AttributeDomain("L", 2);
        /** Float */
        public static final AttributeDomain FLOAT = new AttributeDomain("F", 3);
        /** Integer */
        public static final AttributeDomain INTEGER = new AttributeDomain("I", 4);
        /** Code string in ASCII characters */
        public static final AttributeDomain CODE_STRING = new AttributeDomain("A", 5);
        /** Free text format */
        public static final AttributeDomain FREE_TEXT = new AttributeDomain("S", 6);

        AttributeDomain(final String ascii, final int binary) {
            super(AttributeDomain.class, ascii, binary);
        }

        public static AttributeDomain[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new AttributeDomain[VALUES.size()]);
            }
        }

        @Override
        public AttributeDomain[] family() {
            return values();
        }
        
        public static AttributeDomain valueOf(Object code) {
            return (AttributeDomain) valueOf(VALUES, code);
        }
    }
    
    /** Object type */
    public static final class ObjectType extends S57CodeList<ObjectType> {
        public static final String NAME = "OATY";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<ObjectType> VALUES = new ArrayList<ObjectType>();
        //---------------
        /** Meta object */
        public static final ObjectType METADATA = new ObjectType("M", 1);
        /** Cartographic object */
        public static final ObjectType CARTOGRAPHIC = new ObjectType("$", 2);
        /** Geo object */
        public static final ObjectType GEOGRAPHIC = new ObjectType("G", 3);
        /** Collection object */
        public static final ObjectType COLLECTION = new ObjectType("C", 4);
        /** Feature attribute */
        public static final ObjectType FEATURE_ATT = new ObjectType("F", 5);
        /** Feature national attribute */
        public static final ObjectType FEATURE_NAT_ATT = new ObjectType("N", 6);
        /** Spatial attribute */
        public static final ObjectType SPATIAL_ATT = new ObjectType("S", 7);

        ObjectType(final String ascii, final int binary) {
            super(ObjectType.class, ascii, binary);
        }

        public static ObjectType[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new ObjectType[VALUES.size()]);
            }
        }

        @Override
        public ObjectType[] family() {
            return values();
        }
        
        public static ObjectType valueOf(Object code) {
            return (ObjectType) valueOf(VALUES, code);
        }
    }
    
    /** Object or Attribute */
    public static final class ObjectOrAttribute extends S57CodeList<ObjectOrAttribute> {
        public static final String NAME = "OORA";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 1);
        public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(LE_INTEGER_UNSIGNED, 1);
        static final List<ObjectOrAttribute> VALUES = new ArrayList<ObjectOrAttribute>();
        //---------------
        /** The content of OAAC/OACO is an attribute */
        public static final ObjectOrAttribute OBJECT = new ObjectOrAttribute("A", 1);
        /** The content of OAAC/OACO is an object */
        public static final ObjectOrAttribute ATTRIBUTE = new ObjectOrAttribute("O", 2);

        ObjectOrAttribute(final String ascii, final int binary) {
            super(ObjectOrAttribute.class, ascii, binary);
        }

        public static ObjectOrAttribute[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new ObjectOrAttribute[VALUES.size()]);
            }
        }

        @Override
        public ObjectOrAttribute[] family() {
            return values();
        }
        
        public static ObjectOrAttribute valueOf(Object code) {
            return (ObjectOrAttribute) valueOf(VALUES, code);
        }
    }
    
    /** ASCII or BINARY Implementation */
    public static final class Implementation extends S57CodeList<Implementation> {
        public static final String NAME = "IMPL";
        public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(TEXT, 3);
        public static final SubFieldDescription BINARYFORMAT = null;
        static final List<Implementation> VALUES = new ArrayList<Implementation>();
        //---------------
        /** ASCII */
        public static final Implementation ASCII = new Implementation("ASC", -1);
        /** Binary */
        public static final Implementation BINARY = new Implementation("BIN", -1);

        Implementation(final String ascii, final int binary) {
            super(Implementation.class, ascii, binary);
        }

        public static Implementation[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new Implementation[VALUES.size()]);
            }
        }

        @Override
        public Implementation[] family() {
            return values();
        }
        
        public static Implementation valueOf(Object code) {
            return (Implementation) valueOf(VALUES, code);
        }
    }
    
}
