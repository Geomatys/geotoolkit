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

import com.vividsolutions.jts.geom.Geometry;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.data.iso8211.SubFieldDescription;
import org.opengis.util.CodeList;
import static org.geotoolkit.data.iso8211.FieldValueType.*;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.opengis.feature.type.FeatureType;

/**
 * S-57 constants.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class S57Constants {

    public static final FeatureType ABSTRACT_S57FEATURETYPE;
    static {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setAbstract(true);
        ftb.setName("S57");
        ftb.add("spatial", Geometry.class, null);
        ABSTRACT_S57FEATURETYPE = ftb.buildFeatureType();
    }

    private S57Constants() {}

    public static abstract class S57CodeList<T extends CodeList<T>> extends CodeList<T>{

        private final Class<T> clazz;
        public final String ascii;
        public final int binary;

        protected S57CodeList(final Class<T> clazz,final String ascii, List allValues) {
            this(clazz,ascii,-1, allValues);
        }

        protected S57CodeList(final Class<T> clazz, final int binary, List allValues) {
            this(clazz,String.valueOf(binary),binary, allValues);
        }

        protected S57CodeList(final Class<T> clazz,final String ascii, final int binary, List allValues) {
            super(ascii,allValues);
            this.clazz = clazz;
            this.ascii = ascii;
            this.binary = binary;
        }

        static S57CodeList valueOf(final List<? extends S57CodeList> lst, final Object code) {

            final String ascii;
            final int binary;
            if(code instanceof Number){
                binary = ((Number)code).intValue();
                for(S57CodeList exp : lst){
                    if(exp.binary == binary) return exp;
                }
            }else if(code instanceof String){
                ascii = (String)code;
                for(S57CodeList exp : lst){
                    if(exp.ascii.equals(ascii)) return exp;
                }
            }else{
                throw new IllegalArgumentException("Expected a String or Number object, received : "+code);
            }

            throw new IllegalArgumentException("Unknwoned type : "+ code);

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
            super(RecordType.class, ascii, binary, VALUES);
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
            super(Unit.class, ascii, binary, VALUES);
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
            super(Projection.class, ascii, binary, VALUES);
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
            super(Primitive.class, ascii, binary, VALUES);
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
            super(Usage.class, ascii, binary, VALUES);
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
            super(Orientation.class, ascii, binary, VALUES);
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
            super(Mask.class, ascii, binary, VALUES);
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
            super(Topology.class, ascii, binary, VALUES);
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
            super(ArcType.class, ascii, binary, VALUES);
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
            super(ConstructionSurface.class, ascii, binary, VALUES);
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
            super(ExchangePurpose.class, ascii, binary, VALUES);
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
            super(IntendedUsage.class, ascii, binary, VALUES);
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
            super(ProductSpecification.class, ascii, binary, VALUES);
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
            super(ApplicationProfile.class, ascii, binary, VALUES);
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
            super(DataStructure.class, ascii, binary, VALUES);
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
        public static final LexicalLevel LEVEL0 = new LexicalLevel("0", 0, Charset.forName("US-ASCII"), (char)0x7F);
        /**
         * ISO 8859 part 1, Latin alphabet 1 repertoire (i.e. Western European
         * Latin alphabet based languages.
         */
        public static final LexicalLevel LEVEL1 = new LexicalLevel("1", 1, Charset.forName("ISO-8859-1"), (char)0x7F);
        /**
         * Universal Character Set repertoire UCS-2 implementation level 1 (no
         * combining characters), Base Multilingual plane of ISO/IEC 10646 (i.e.
         * including Latin alphabet, Greek, Cyrillic, Arabic, Chinese, Japanese
         * etc.)
         */
        public static final LexicalLevel LEVEL2 = new LexicalLevel("2", 2, Charset.forName("UTF-16"), (char)0x007F);

        private final Charset charset;
        private final char deleteValue;

        LexicalLevel(final String ascii, final int binary, Charset cs, char deleteValue) {
            super(LexicalLevel.class, ascii, binary, VALUES);
            this.charset = cs;
            this.deleteValue = deleteValue;
        }

        /**
         * Charset to use to decode
         * @return Charset, never null
         */
        public Charset getCharSet(){
            return charset;
        }

        /**
         * Value used in update instructions to express a attribute deletion.
         * @return char
         */
        public char getDeleteValue() {
            return deleteValue;
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
            super(RelationShip.class, ascii, binary, VALUES);
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
            super(UpdateInstruction.class, ascii, binary, VALUES);
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
            super(AttributeSet.class, ascii, binary, VALUES);
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
            super(ReferenceType.class, ascii, binary, VALUES);
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
            super(RangeOrValue.class, ascii, binary, VALUES);
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
            super(AttributeDomain.class, ascii, binary, VALUES);
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
            super(ObjectType.class, ascii, binary, VALUES);
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
            super(ObjectOrAttribute.class, ascii, binary, VALUES);
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
            super(Implementation.class, ascii, binary, VALUES);
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
