/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.lucene.analyzer;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.index.LogicalFilterType;
import org.geotoolkit.lucene.filter.LuceneOGCFilter;
import org.geotoolkit.lucene.filter.SerialChainFilter;
import org.geotoolkit.lucene.filter.SpatialQuery;
import org.geotoolkit.lucene.index.LuceneIndexSearcher;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import org.geotoolkit.io.wkb.WKBUtils;
import org.geotoolkit.lucene.DocumentIndexer.DocumentEnvelope;
import org.geotoolkit.lucene.LuceneUtils;

import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@RunWith(BlockJUnit4ClassRunner.class)
public abstract class AbstractAnalyzerTest {

    protected static final FilterFactory2 FF = (FilterFactory2) DefaultFactories.forBuildin(FilterFactory.class);

    protected static final Logger LOGGER = Logging.getLogger("org.constellation.metadata.index.generic");

    protected static final FieldType SORT_TYPE = new FieldType();

    static {
        SORT_TYPE.setTokenized(false);
        SORT_TYPE.setStored(false);
        SORT_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        SORT_TYPE.setDocValuesType(DocValuesType.SORTED);
    }

    protected static final FieldType RAW_TYPE = new FieldType();

    static {
        RAW_TYPE.setTokenized(false);
        RAW_TYPE.setStored(false);
        RAW_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
    }

    protected static final FieldType TEXT_TYPE = new FieldType();

    static {
        TEXT_TYPE.setTokenized(true);
        TEXT_TYPE.setStored(true);
        TEXT_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
    }

    protected static final FieldType SEARCH_TYPE = new FieldType();

    static {
        SEARCH_TYPE.setTokenized(true);
        SEARCH_TYPE.setStored(false);
        SEARCH_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
    }

    private static CoordinateReferenceSystem treeCrs;

    static {
        try {
            // the tree CRS (must be) cartesian
            treeCrs = CRS.forCode("CRS:84");
        } catch (FactoryException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    protected static final FieldType ID_TYPE = new FieldType();
    static {
        ID_TYPE.setTokenized(false);
        ID_TYPE.setStored(true);
        ID_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
    }

    protected static LuceneIndexSearcher indexSearcher;

    public static List<DocumentEnvelope> fillTestData() throws Exception {
        List<DocumentEnvelope> docs = new ArrayList<>();

        Document doc = new Document();
        doc.add(new Field("metafile", "doc", SEARCH_TYPE));
        doc.add(new Field("Modified", "20090101040000", SEARCH_TYPE));

        doc.add(new Field("Modified_raw", "20090101040000", RAW_TYPE));

        doc.add(new Field("Modified_sort", new BytesRef("20090101040000".getBytes()), SORT_TYPE));

        doc.add(new Field("Authority", "L101", SEARCH_TYPE));

        doc.add(new Field("Authority_raw", "L101", RAW_TYPE));

        doc.add(new Field("Authority_sort", new BytesRef("L101".getBytes()), SORT_TYPE));

        doc.add(new Field("CreationDate", "19790802220000", SEARCH_TYPE));

        doc.add(new Field("CreationDate_raw", "19790802220000", RAW_TYPE));

        doc.add(new Field("CreationDate_sort", new BytesRef("19790802220000".getBytes()), SORT_TYPE));

        doc.add(new Field("Identifier", "42292_5p_19900609195600", SEARCH_TYPE));

        doc.add(new Field("Identifier_raw", "42292_5p_19900609195600", RAW_TYPE));

        doc.add(new Field("Identifier_sort", new BytesRef("42292_5p_19900609195600".getBytes()), SORT_TYPE));

        doc.add(new Field("Language", "eng", SEARCH_TYPE));

        doc.add(new Field("Language_raw", "eng", RAW_TYPE));

        doc.add(new Field("Language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("Format", "MEDATLAS ASCII", SEARCH_TYPE));

        doc.add(new Field("Format_raw", "MEDATLAS ASCII", RAW_TYPE));

        doc.add(new Field("Format_sort", new BytesRef("MEDATLAS ASCII".getBytes()), SORT_TYPE));

        indexNumericField("CloudCover", 50.0d, doc);

        indexNumericField("CloudCover_sort", 50.0d, doc);

        doc.add(new Field("ID", "EPSG:4326", SEARCH_TYPE));

        doc.add(new Field("ID_raw", "EPSG:4326", RAW_TYPE));

        doc.add(new Field("ID_sort", new BytesRef("EPSG:4326".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName_sort", new BytesRef("IFREMER / IDM/SISMER".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName", "UNIVERSITE DE LA MEDITERRANNEE (U2) / COM - LAB. OCEANOG. BIOGEOCHIMIE - LUMINY", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "UNIVERSITE DE LA MEDITERRANNEE (U2) / COM - LAB. OCEANOG. BIOGEOCHIMIE - LUMINY", RAW_TYPE));

        doc.add(new Field("KeywordType", "parameter", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "parameter", RAW_TYPE));

        doc.add(new Field("KeywordType_sort", new BytesRef("parameter".getBytes()), SORT_TYPE));

        doc.add(new Field("Abstract", "Donnees CTD NEDIPROD VI 120", SEARCH_TYPE));

        doc.add(new Field("Abstract_raw", "Donnees CTD NEDIPROD VI 120", RAW_TYPE));

        doc.add(new Field("Abstract_sort", new BytesRef("Donnees CTD NEDIPROD VI 120".getBytes()), SORT_TYPE));

        doc.add(new Field("PublicationDate", "null", SEARCH_TYPE));

        doc.add(new Field("PublicationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("PublicationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("Title", "90008411.ctd", SEARCH_TYPE));

        doc.add(new Field("Title_raw", "90008411.ctd", RAW_TYPE));

        doc.add(new Field("Title_sort", new BytesRef("90008411.ctd".getBytes()), SORT_TYPE));

        doc.add(new Field("TopicCategory", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("TopicCategory_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("TopicCategory_sort", new BytesRef("OCEANS".getBytes()), SORT_TYPE));

        doc.add(new Field("ResourceLanguage", "eng", SEARCH_TYPE));

        doc.add(new Field("ResourceLanguage_raw", "eng", RAW_TYPE));

        doc.add(new Field("ResourceLanguage_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "Transmittance and attenuance of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Transmittance and attenuance of the water column", RAW_TYPE));

        doc.add(new Field("Subject_sort", new BytesRef("Transmittance and attenuance of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("TempExtent_end", "1990-07-02", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "1990-07-02", RAW_TYPE));

        doc.add(new Field("TempExtent_end_sort", new BytesRef("1990-07-02".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_end", "19900701220000", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "19900701220000", RAW_TYPE));

        doc.add(new Field("Type", "DATASET", SEARCH_TYPE));

        doc.add(new Field("Type_raw", "DATASET", RAW_TYPE));

        doc.add(new Field("Type_sort", new BytesRef("DATASET".getBytes()), SORT_TYPE));

        doc.add(new Field("RevisionDate", "19900604220000", SEARCH_TYPE));

        doc.add(new Field("RevisionDate_raw", "19900604220000", RAW_TYPE));

        doc.add(new Field("RevisionDate_sort", new BytesRef("19900604220000".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_begin", "1990-06-05", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "1990-06-05", RAW_TYPE));

        doc.add(new Field("TempExtent_begin_sort", new BytesRef("1990-06-05".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_begin", "19900604220000", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "19900604220000", RAW_TYPE));

        doc.add(new Field("AlternateTitle", "42292_5p_19900609195600", SEARCH_TYPE));

        doc.add(new Field("AlternateTitle_raw", "42292_5p_19900609195600", RAW_TYPE));

        doc.add(new Field("AlternateTitle_sort", new BytesRef("42292_5p_19900609195600".getBytes()), SORT_TYPE));

        doc.add(new Field("id", "42292_5p_19900609195600", ID_TYPE));

        doc.add(new Field("identifier", "42292_5p_19900609195600", SEARCH_TYPE));

        doc.add(new Field("identifier_raw", "42292_5p_19900609195600", RAW_TYPE));

        doc.add(new Field("identifier_sort", new BytesRef("42292_5p_19900609195600".getBytes()), SORT_TYPE));

        doc.add(new Field("date", "20090101040000", SEARCH_TYPE));

        doc.add(new Field("date_raw", "20090101040000", RAW_TYPE));

        doc.add(new Field("date_sort", new BytesRef("20090101040000".getBytes()), SORT_TYPE));

        doc.add(new Field("subject", "Transmittance and attenuance of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Transmittance and attenuance of the water column", RAW_TYPE));

        doc.add(new Field("subject_sort", new BytesRef("Transmittance and attenuance of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("format", "MEDATLAS ASCII", SEARCH_TYPE));

        doc.add(new Field("format_raw", "MEDATLAS ASCII", RAW_TYPE));

        doc.add(new Field("format_sort", new BytesRef("MEDATLAS ASCII".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("creator_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("language", "eng", SEARCH_TYPE));

        doc.add(new Field("language_raw", "eng", RAW_TYPE));

        doc.add(new Field("language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "Transmittance and attenuance of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Transmittance and attenuance of the water column", RAW_TYPE));

        doc.add(new Field("description_sort", new BytesRef("Transmittance and attenuance of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("description_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("type", "DATASET", SEARCH_TYPE));

        doc.add(new Field("type_raw", "DATASET", RAW_TYPE));

        doc.add(new Field("type_sort", new BytesRef("DATASET".getBytes()), SORT_TYPE));

        doc.add(new Field("abstract", "Donnees CTD NEDIPROD VI 120", SEARCH_TYPE));

        doc.add(new Field("abstract_raw", "Donnees CTD NEDIPROD VI 120", RAW_TYPE));

        doc.add(new Field("abstract_sort", new BytesRef("Donnees CTD NEDIPROD VI 120".getBytes()), SORT_TYPE));

        doc.add(new Field("title", "90008411.ctd", SEARCH_TYPE));

        doc.add(new Field("title_raw", "90008411.ctd", RAW_TYPE));

        doc.add(new Field("title_sort", new BytesRef("90008411.ctd".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_end", "1990-07-02", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "1990-07-02", RAW_TYPE));

        doc.add(new Field("TemporalExtent_end_sort", new BytesRef("1990-07-02".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_end", "19900701220000", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "19900701220000", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin", "1990-06-05", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "1990-06-05", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin_sort", new BytesRef("1990-06-05".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_begin", "19900604220000", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "19900604220000", RAW_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("contributor_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("relation", "MEDIPROD VI", SEARCH_TYPE));

        doc.add(new Field("relation_raw", "MEDIPROD VI", RAW_TYPE));

        doc.add(new Field("relation_sort", new BytesRef("MEDIPROD VI".getBytes()), SORT_TYPE));

        doc.add(new Field("rights", "LICENCE", SEARCH_TYPE));

        doc.add(new Field("rights_raw", "LICENCE", RAW_TYPE));

        doc.add(new Field("rights_sort", new BytesRef("LICENCE".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        doc.add(new Field("publisher_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        indexNumericField("SouthBoundLatitude", 36.6d, doc);

        indexNumericField("SouthBoundLatitude_sort", 36.6d, doc);

        indexNumericField("NorthBoundLatitude", 36.6d, doc);

        indexNumericField("NorthBoundLatitude_sort", 36.6d, doc);

        indexNumericField("WestBoundLongitude", 1.1667d, doc);

        indexNumericField("WestBoundLongitude_sort", 1.1667d, doc);

        indexNumericField("EastBoundLongitude", 1.1667d, doc);

        indexNumericField("EastBoundLongitude_sort", 1.1667d, doc);

        NamedEnvelope env = addBoundingBox(doc,  1.1667d,  1.1667d,  36.6d,   36.6d, CommonCRS.defaultGeographic());

        docs.add(new DocumentEnvelope(doc, env));

        doc = new Document();
        doc.add(new Field("metafile", "doc", SEARCH_TYPE));
        doc.add(new Field("Modified", "20090126110000", SEARCH_TYPE));

        doc.add(new Field("Modified_raw", "20090126110000", RAW_TYPE));

        doc.add(new Field("Modified_sort", new BytesRef("20090126110000".getBytes()), SORT_TYPE));

        doc.add(new Field("CreationDate", "19700204010426", SEARCH_TYPE));

        doc.add(new Field("CreationDate_raw", "19700204010426", RAW_TYPE));

        doc.add(new Field("CreationDate_sort", new BytesRef("19700204010426".getBytes()), SORT_TYPE));

        doc.add(new Field("Identifier", "42292_9s_19900610041000", SEARCH_TYPE));

        doc.add(new Field("Identifier_raw", "42292_9s_19900610041000", RAW_TYPE));

        doc.add(new Field("Identifier_sort", new BytesRef("42292_9s_19900610041000".getBytes()), SORT_TYPE));

        doc.add(new Field("Language", "eng", SEARCH_TYPE));

        doc.add(new Field("Language_raw", "eng", RAW_TYPE));

        doc.add(new Field("Language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("Format", "MEDATLAS ASCII", SEARCH_TYPE));

        doc.add(new Field("Format_raw", "MEDATLAS ASCII", RAW_TYPE));

        doc.add(new Field("Format_sort", new BytesRef("MEDATLAS ASCII".getBytes()), SORT_TYPE));

        indexNumericField("CloudCover", 21.0d, doc);

        indexNumericField("CloudCover_sort", 21.0d, doc);

        doc.add(new Field("ID", "World Geodetic System 84", SEARCH_TYPE));

        doc.add(new Field("ID_raw", "World Geodetic System 84", RAW_TYPE));

        doc.add(new Field("ID_sort", new BytesRef("World Geodetic System 84".getBytes()), SORT_TYPE));

        doc.add(new Field("KeywordType", "parameter", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "parameter", RAW_TYPE));

        doc.add(new Field("KeywordType_sort", new BytesRef("parameter".getBytes()), SORT_TYPE));

        doc.add(new Field("KeywordType", "INSTRUMENT", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "INSTRUMENT", RAW_TYPE));

        doc.add(new Field("KeywordType", "platform_class", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "platform_class", RAW_TYPE));

        doc.add(new Field("Abstract", "Donnees CTD MEDIPROD VI 120", SEARCH_TYPE));

        doc.add(new Field("Abstract_raw", "Donnees CTD MEDIPROD VI 120", RAW_TYPE));

        doc.add(new Field("Abstract_sort", new BytesRef("Donnees CTD MEDIPROD VI 120".getBytes()), SORT_TYPE));

        doc.add(new Field("PublicationDate", "null", SEARCH_TYPE));

        doc.add(new Field("PublicationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("PublicationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("AlternateTitle", "42292_9s_19900610041000", SEARCH_TYPE));

        doc.add(new Field("AlternateTitle_raw", "42292_9s_19900610041000", RAW_TYPE));

        doc.add(new Field("AlternateTitle_sort", new BytesRef("42292_9s_19900610041000".getBytes()), SORT_TYPE));

        doc.add(new Field("Title", "90008411-2.ctd", SEARCH_TYPE));

        doc.add(new Field("Title_raw", "90008411-2.ctd", RAW_TYPE));

        doc.add(new Field("Title_sort", new BytesRef("90008411-2.ctd".getBytes()), SORT_TYPE));

        doc.add(new Field("TopicCategory", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("TopicCategory_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("TopicCategory_sort", new BytesRef("OCEANS".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName_sort", new BytesRef("IFREMER / IDM/SISMER".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName", "UNIVERSITE DE LA MEDITERRANNEE (U2) / COM - LAB. OCEANOG. & BIOGEOCHIMIE - LUMINY", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "UNIVERSITE DE LA MEDITERRANNEE (U2) / COM - LAB. OCEANOG. & BIOGEOCHIMIE - LUMINY", RAW_TYPE));

        doc.add(new Field("ResourceLanguage", "eng", SEARCH_TYPE));

        doc.add(new Field("ResourceLanguage_raw", "eng", RAW_TYPE));

        doc.add(new Field("ResourceLanguage_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("Type", "DATASET", SEARCH_TYPE));

        doc.add(new Field("Type_raw", "DATASET", RAW_TYPE));

        doc.add(new Field("Type_sort", new BytesRef("DATASET".getBytes()), SORT_TYPE));

        doc.add(new Field("RevisionDate", "19900604220000", SEARCH_TYPE));

        doc.add(new Field("RevisionDate_raw", "19900604220000", RAW_TYPE));

        doc.add(new Field("RevisionDate_sort", new BytesRef("19900604220000".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_end", "1990-07-02", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "1990-07-02", RAW_TYPE));

        doc.add(new Field("TempExtent_end_sort", new BytesRef("1990-07-02".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_end", "19900701220000", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "19900701220000", RAW_TYPE));

        doc.add(new Field("TempExtent_begin", "1990-06-05", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "1990-06-05", RAW_TYPE));

        doc.add(new Field("TempExtent_begin_sort", new BytesRef("1990-06-05".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_begin", "19900604220000", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "19900604220000", RAW_TYPE));

        doc.add(new Field("Subject", "Transmittance and attenuance of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Transmittance and attenuance of the water column", RAW_TYPE));

        doc.add(new Field("Subject_sort", new BytesRef("Transmittance and attenuance of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Dissolved oxygen parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Dissolved oxygen parameters in the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Light extinction and diffusion coefficients", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Light extinction and diffusion coefficients", RAW_TYPE));

        doc.add(new Field("Subject", "Dissolved noble gas concentration parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Dissolved noble gas concentration parameters in the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Optical backscatter", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Optical backscatter", RAW_TYPE));

        doc.add(new Field("Subject", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Dissolved concentration parameters for 'other' gases in the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Dissolved concentration parameters for 'other' gases in the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Visible waveband radiance and irradiance measurements in the atmosphere", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Visible waveband radiance and irradiance measurements in the atmosphere", RAW_TYPE));

        doc.add(new Field("Subject", "Visible waveband radiance and irradiance measurements in the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Visible waveband radiance and irradiance measurements in the water column", RAW_TYPE));

        doc.add(new Field("Subject", "CTD profilers", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "CTD profilers", RAW_TYPE));

        doc.add(new Field("Subject", "research vessel", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("Subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("date", "20090126110000", SEARCH_TYPE));

        doc.add(new Field("date_raw", "20090126110000", RAW_TYPE));

        doc.add(new Field("date_sort", new BytesRef("20090126110000".getBytes()), SORT_TYPE));

        doc.add(new Field("id", "42292_9s_19900610041000", ID_TYPE));

        doc.add(new Field("identifier", "42292_9s_19900610041000", SEARCH_TYPE));

        doc.add(new Field("identifier_raw", "42292_9s_19900610041000", RAW_TYPE));

        doc.add(new Field("identifier_sort", new BytesRef("42292_9s_19900610041000".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_end", "1990-07-02", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "1990-07-02", RAW_TYPE));

        doc.add(new Field("TemporalExtent_end_sort", new BytesRef("1990-07-02".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_end", "19900701220000", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "19900701220000", RAW_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("creator_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("language", "eng", SEARCH_TYPE));

        doc.add(new Field("language_raw", "eng", RAW_TYPE));

        doc.add(new Field("language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("format", "MEDATLAS ASCII", SEARCH_TYPE));

        doc.add(new Field("format_raw", "MEDATLAS ASCII", RAW_TYPE));

        doc.add(new Field("format_sort", new BytesRef("MEDATLAS ASCII".getBytes()), SORT_TYPE));

        doc.add(new Field("abstract", "Donnees CTD MEDIPROD VI 120", SEARCH_TYPE));

        doc.add(new Field("abstract_raw", "Donnees CTD MEDIPROD VI 120", RAW_TYPE));

        doc.add(new Field("abstract_sort", new BytesRef("Donnees CTD MEDIPROD VI 120".getBytes()), SORT_TYPE));

        doc.add(new Field("title", "90008411-2.ctd", SEARCH_TYPE));

        doc.add(new Field("title_raw", "90008411-2.ctd", RAW_TYPE));

        doc.add(new Field("title_sort", new BytesRef("90008411-2.ctd".getBytes()), SORT_TYPE));

        doc.add(new Field("type", "DATASET", SEARCH_TYPE));

        doc.add(new Field("type_raw", "DATASET", RAW_TYPE));

        doc.add(new Field("type_sort", new BytesRef("DATASET".getBytes()), SORT_TYPE));

        doc.add(new Field("relation", "MEDIPROD VI", SEARCH_TYPE));

        doc.add(new Field("relation_raw", "MEDIPROD VI", RAW_TYPE));

        doc.add(new Field("relation_sort", new BytesRef("MEDIPROD VI".getBytes()), SORT_TYPE));

        doc.add(new Field("relation", "9s", SEARCH_TYPE));

        doc.add(new Field("relation_raw", "9s", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin", "1990-06-05", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "1990-06-05", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin_sort", new BytesRef("1990-06-05".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_begin", "19900604220000", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "19900604220000", RAW_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("contributor_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("rights", "LICENCE", SEARCH_TYPE));

        doc.add(new Field("rights_raw", "LICENCE", RAW_TYPE));

        doc.add(new Field("rights_sort", new BytesRef("LICENCE".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "Transmittance and attenuance of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Transmittance and attenuance of the water column", RAW_TYPE));

        doc.add(new Field("description_sort", new BytesRef("Transmittance and attenuance of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("description", "Dissolved oxygen parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Dissolved oxygen parameters in the water column", RAW_TYPE));

        doc.add(new Field("description", "Light extinction and diffusion coefficients", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Light extinction and diffusion coefficients", RAW_TYPE));

        doc.add(new Field("description", "Dissolved noble gas concentration parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Dissolved noble gas concentration parameters in the water column", RAW_TYPE));

        doc.add(new Field("description", "Optical backscatter", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Optical backscatter", RAW_TYPE));

        doc.add(new Field("description", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("description", "Dissolved concentration parameters for 'other' gases in the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Dissolved concentration parameters for 'other' gases in the water column", RAW_TYPE));

        doc.add(new Field("description", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("description", "Visible waveband radiance and irradiance measurements in the atmosphere", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Visible waveband radiance and irradiance measurements in the atmosphere", RAW_TYPE));

        doc.add(new Field("description", "Visible waveband radiance and irradiance measurements in the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Visible waveband radiance and irradiance measurements in the water column", RAW_TYPE));

        doc.add(new Field("description", "CTD profilers", SEARCH_TYPE));

        doc.add(new Field("description_raw", "CTD profilers", RAW_TYPE));

        doc.add(new Field("description", "research vessel", SEARCH_TYPE));

        doc.add(new Field("description_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("description", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("description_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("subject", "Transmittance and attenuance of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Transmittance and attenuance of the water column", RAW_TYPE));

        doc.add(new Field("subject_sort", new BytesRef("Transmittance and attenuance of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("subject", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("subject", "Dissolved oxygen parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Dissolved oxygen parameters in the water column", RAW_TYPE));

        doc.add(new Field("subject", "Light extinction and diffusion coefficients", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Light extinction and diffusion coefficients", RAW_TYPE));

        doc.add(new Field("subject", "Dissolved noble gas concentration parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Dissolved noble gas concentration parameters in the water column", RAW_TYPE));

        doc.add(new Field("subject", "Optical backscatter", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Optical backscatter", RAW_TYPE));

        doc.add(new Field("subject", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("subject", "Dissolved concentration parameters for 'other' gases in the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Dissolved concentration parameters for 'other' gases in the water column", RAW_TYPE));

        doc.add(new Field("subject", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("subject", "Visible waveband radiance and irradiance measurements in the atmosphere", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Visible waveband radiance and irradiance measurements in the atmosphere", RAW_TYPE));

        doc.add(new Field("subject", "Visible waveband radiance and irradiance measurements in the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Visible waveband radiance and irradiance measurements in the water column", RAW_TYPE));

        doc.add(new Field("subject", "CTD profilers", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "CTD profilers", RAW_TYPE));

        doc.add(new Field("subject", "research vessel", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        doc.add(new Field("publisher_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        indexNumericField("WestBoundLongitude", 1.3667d, doc);

        indexNumericField("WestBoundLongitude_sort", 1.3667d, doc);

        indexNumericField("WestBoundLongitude", 12.1d, doc);

        indexNumericField("WestBoundLongitude_sort", 12.1d, doc);

        indexNumericField("SouthBoundLatitude", 36.6d, doc);

        indexNumericField("SouthBoundLatitude_sort", 36.6d, doc);

        indexNumericField("SouthBoundLatitude", 31.2d, doc);

        indexNumericField("SouthBoundLatitude_sort", 31.2d, doc);

        indexNumericField("NorthBoundLatitude", 36.6d, doc);

        indexNumericField("NorthBoundLatitude_sort", 36.6d, doc);

        indexNumericField("NorthBoundLatitude", 31.2d, doc);

        indexNumericField("NorthBoundLatitude_sort", 31.2d, doc);

        indexNumericField("EastBoundLongitude", 1.3667d, doc);

        indexNumericField("EastBoundLongitude_sort", 1.3667d, doc);

        indexNumericField("EastBoundLongitude", 12.1d, doc);

        indexNumericField("EastBoundLongitude_sort", 12.1d, doc);

        env = addBoundingBox(doc,  Arrays.asList(1.3667d,12.1d),  Arrays.asList(1.3667d,12.1d),  Arrays.asList(36.6d,31.2d),   Arrays.asList(36.6d,31.2d), CommonCRS.defaultGeographic());

        docs.add(new DocumentEnvelope(doc, env));
        doc = new Document();
        doc.add(new Field("metafile", "doc", SEARCH_TYPE));

        doc.add(new Field("Modified", "20090126112145", SEARCH_TYPE));

        doc.add(new Field("Modified_raw", "20090126112145", RAW_TYPE));

        doc.add(new Field("Modified_sort", new BytesRef("20090126112145".getBytes()), SORT_TYPE));

        doc.add(new Field("CreationDate", "null", SEARCH_TYPE));

        doc.add(new Field("CreationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("CreationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("Identifier", "39727_22_19750113062500", SEARCH_TYPE));

        doc.add(new Field("Identifier_raw", "39727_22_19750113062500", RAW_TYPE));

        doc.add(new Field("Identifier_sort", new BytesRef("39727_22_19750113062500".getBytes()), SORT_TYPE));

        doc.add(new Field("Language", "eng", SEARCH_TYPE));

        doc.add(new Field("Language_raw", "eng", RAW_TYPE));

        doc.add(new Field("Language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("Format", "MEDATLAS ASCII", SEARCH_TYPE));

        doc.add(new Field("Format_raw", "MEDATLAS ASCII", RAW_TYPE));

        doc.add(new Field("Format_sort", new BytesRef("MEDATLAS ASCII".getBytes()), SORT_TYPE));

        indexNumericField("CloudCover", 100.0d, doc);

        indexNumericField("CloudCover_sort", 100.0d, doc);

        doc.add(new Field("ID", "World Geodetic System 84", SEARCH_TYPE));

        doc.add(new Field("ID_raw", "World Geodetic System 84", RAW_TYPE));

        doc.add(new Field("ID_sort", new BytesRef("World Geodetic System 84".getBytes()), SORT_TYPE));

        doc.add(new Field("KeywordType", "parameter", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "parameter", RAW_TYPE));

        doc.add(new Field("KeywordType_sort", new BytesRef("parameter".getBytes()), SORT_TYPE));

        doc.add(new Field("KeywordType", "INSTRUMENT", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "INSTRUMENT", RAW_TYPE));

        doc.add(new Field("KeywordType", "platform_class", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "platform_class", RAW_TYPE));

        doc.add(new Field("PublicationDate", "null", SEARCH_TYPE));

        doc.add(new Field("PublicationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("PublicationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("AlternateTitle", "39727_22_19750113062500", SEARCH_TYPE));

        doc.add(new Field("AlternateTitle_raw", "39727_22_19750113062500", RAW_TYPE));

        doc.add(new Field("AlternateTitle_sort", new BytesRef("39727_22_19750113062500".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName_sort", new BytesRef("IFREMER / IDM/SISMER".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName", "IRD / CENTRE OF HANN", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IRD / CENTRE OF HANN", RAW_TYPE));

        doc.add(new Field("Abstract", "Donnees CTD ANGOLA CAP 7501 78", SEARCH_TYPE));

        doc.add(new Field("Abstract_raw", "Donnees CTD ANGOLA CAP 7501 78", RAW_TYPE));

        doc.add(new Field("Abstract_sort", new BytesRef("Donnees CTD ANGOLA CAP 7501 78".getBytes()), SORT_TYPE));

        doc.add(new Field("Title", "75000111.ctd", SEARCH_TYPE));

        doc.add(new Field("Title_raw", "75000111.ctd", RAW_TYPE));

        doc.add(new Field("Title_sort", new BytesRef("75000111.ctd".getBytes()), SORT_TYPE));

        doc.add(new Field("TopicCategory", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("TopicCategory_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("TopicCategory_sort", new BytesRef("OCEANS".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("Subject_sort", new BytesRef("Electrical conductivity of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "Dissolved oxygen parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Dissolved oxygen parameters in the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Dissolved noble gas concentration parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Dissolved noble gas concentration parameters in the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Dissolved concentration parameters for 'other' gases in the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Dissolved concentration parameters for 'other' gases in the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("Subject", "CTD profilers", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "CTD profilers", RAW_TYPE));

        doc.add(new Field("Subject", "research vessel", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("Subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("ResourceLanguage", "eng", SEARCH_TYPE));

        doc.add(new Field("ResourceLanguage_raw", "eng", RAW_TYPE));

        doc.add(new Field("ResourceLanguage_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_end", "1975-02-04", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "1975-02-04", RAW_TYPE));

        doc.add(new Field("TempExtent_end_sort", new BytesRef("1975-02-04".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_end", "19750203230000", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "19750203230000", RAW_TYPE));

        doc.add(new Field("Type", "DATASET", SEARCH_TYPE));

        doc.add(new Field("Type_raw", "DATASET", RAW_TYPE));

        doc.add(new Field("Type_sort", new BytesRef("DATASET".getBytes()), SORT_TYPE));

        doc.add(new Field("RevisionDate", "19750106230000", SEARCH_TYPE));

        doc.add(new Field("RevisionDate_raw", "19750106230000", RAW_TYPE));

        doc.add(new Field("RevisionDate_sort", new BytesRef("19750106230000".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_begin", "1975-01-07", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "1975-01-07", RAW_TYPE));

        doc.add(new Field("TempExtent_begin_sort", new BytesRef("1975-01-07".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_begin", "19750106230000", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "19750106230000", RAW_TYPE));

        doc.add(new Field("date", "20090126112145", SEARCH_TYPE));

        doc.add(new Field("date_raw", "20090126112145", RAW_TYPE));

        doc.add(new Field("date_sort", new BytesRef("20090126112145".getBytes()), SORT_TYPE));

        doc.add(new Field("id", "39727_22_19750113062500", ID_TYPE));

        doc.add(new Field("identifier", "39727_22_19750113062500", SEARCH_TYPE));

        doc.add(new Field("identifier_raw", "39727_22_19750113062500", RAW_TYPE));

        doc.add(new Field("identifier_sort", new BytesRef("39727_22_19750113062500".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("creator_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("TemporalExtent_end", "1975-02-04", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "1975-02-04", RAW_TYPE));

        doc.add(new Field("TemporalExtent_end_sort", new BytesRef("1975-02-04".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_end", "19750203230000", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "19750203230000", RAW_TYPE));

        doc.add(new Field("format", "MEDATLAS ASCII", SEARCH_TYPE));

        doc.add(new Field("format_raw", "MEDATLAS ASCII", RAW_TYPE));

        doc.add(new Field("format_sort", new BytesRef("MEDATLAS ASCII".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("description_sort", new BytesRef("Electrical conductivity of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "Dissolved oxygen parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Dissolved oxygen parameters in the water column", RAW_TYPE));

        doc.add(new Field("description", "Dissolved noble gas concentration parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Dissolved noble gas concentration parameters in the water column", RAW_TYPE));

        doc.add(new Field("description", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("description", "Dissolved concentration parameters for 'other' gases in the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Dissolved concentration parameters for 'other' gases in the water column", RAW_TYPE));

        doc.add(new Field("description", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("description", "CTD profilers", SEARCH_TYPE));

        doc.add(new Field("description_raw", "CTD profilers", RAW_TYPE));

        doc.add(new Field("description", "research vessel", SEARCH_TYPE));

        doc.add(new Field("description_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("description", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("description_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("subject", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("subject_sort", new BytesRef("Electrical conductivity of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("subject", "Dissolved oxygen parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Dissolved oxygen parameters in the water column", RAW_TYPE));

        doc.add(new Field("subject", "Dissolved noble gas concentration parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Dissolved noble gas concentration parameters in the water column", RAW_TYPE));

        doc.add(new Field("subject", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("subject", "Dissolved concentration parameters for 'other' gases in the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Dissolved concentration parameters for 'other' gases in the water column", RAW_TYPE));

        doc.add(new Field("subject", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("subject", "CTD profilers", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "CTD profilers", RAW_TYPE));

        doc.add(new Field("subject", "research vessel", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("language", "eng", SEARCH_TYPE));

        doc.add(new Field("language_raw", "eng", RAW_TYPE));

        doc.add(new Field("language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("abstract", "Donnees CTD ANGOLA CAP 7501 78", SEARCH_TYPE));

        doc.add(new Field("abstract_raw", "Donnees CTD ANGOLA CAP 7501 78", RAW_TYPE));

        doc.add(new Field("abstract_sort", new BytesRef("Donnees CTD ANGOLA CAP 7501 78".getBytes()), SORT_TYPE));

        doc.add(new Field("title", "75000111.ctd", SEARCH_TYPE));

        doc.add(new Field("title_raw", "75000111.ctd", RAW_TYPE));

        doc.add(new Field("title_sort", new BytesRef("75000111.ctd".getBytes()), SORT_TYPE));

        doc.add(new Field("type", "DATASET", SEARCH_TYPE));

        doc.add(new Field("type_raw", "DATASET", RAW_TYPE));

        doc.add(new Field("type_sort", new BytesRef("DATASET".getBytes()), SORT_TYPE));

        doc.add(new Field("relation", "ANGOLA 75/1", SEARCH_TYPE));

        doc.add(new Field("relation_raw", "ANGOLA 75/1", RAW_TYPE));

        doc.add(new Field("relation_sort", new BytesRef("ANGOLA 75/1".getBytes()), SORT_TYPE));

        doc.add(new Field("relation", "22", SEARCH_TYPE));

        doc.add(new Field("relation_raw", "22", RAW_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("contributor_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin", "1975-01-07", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "1975-01-07", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin_sort", new BytesRef("1975-01-07".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_begin", "19750106230000", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "19750106230000", RAW_TYPE));

        doc.add(new Field("rights", "LICENCE", SEARCH_TYPE));

        doc.add(new Field("rights_raw", "LICENCE", RAW_TYPE));

        doc.add(new Field("rights_sort", new BytesRef("LICENCE".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        doc.add(new Field("publisher_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        indexNumericField("WestBoundLongitude", -4.967d, doc);

        indexNumericField("WestBoundLongitude_sort", -4.967d, doc);

        indexNumericField("WestBoundLongitude", -5.1d, doc);

        indexNumericField("WestBoundLongitude_sort", -5.1d, doc);

        indexNumericField("SouthBoundLatitude", -6.95d, doc);

        indexNumericField("SouthBoundLatitude_sort", -6.95d, doc);

        indexNumericField("SouthBoundLatitude", -7.2d, doc);

        indexNumericField("SouthBoundLatitude_sort", -7.2d, doc);

        indexNumericField("NorthBoundLatitude", -6.95d, doc);

        indexNumericField("NorthBoundLatitude_sort", -6.95d, doc);

        indexNumericField("NorthBoundLatitude", -7.2d, doc);

        indexNumericField("NorthBoundLatitude_sort", -7.2d, doc);

        indexNumericField("EastBoundLongitude", -4.967d, doc);

        indexNumericField("EastBoundLongitude_sort", -4.967d, doc);

        indexNumericField("EastBoundLongitude", -5.1d, doc);

        indexNumericField("EastBoundLongitude_sort", -5.1d, doc);

        env = addBoundingBox(doc, Arrays.asList(-4.967d,-5.1d), Arrays.asList(-4.967d,-5.1d),  Arrays.asList(-6.95d,-7.2d),   Arrays.asList(-6.95d,-7.2d), CommonCRS.defaultGeographic());

        docs.add(new DocumentEnvelope(doc, env));

        doc = new Document();
        doc.add(new Field("metafile", "doc", SEARCH_TYPE));
        doc.add(new Field("Modified", "20090126112224", SEARCH_TYPE));

        doc.add(new Field("Modified_raw", "20090126112224", RAW_TYPE));

        doc.add(new Field("Modified_sort", new BytesRef("20090126112224".getBytes()), SORT_TYPE));

        doc.add(new Field("CreationDate", "null", SEARCH_TYPE));

        doc.add(new Field("CreationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("CreationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("Identifier", "11325_158_19640418141800", SEARCH_TYPE));

        doc.add(new Field("Identifier_raw", "11325_158_19640418141800", RAW_TYPE));

        doc.add(new Field("Identifier_sort", new BytesRef("11325_158_19640418141800".getBytes()), SORT_TYPE));

        doc.add(new Field("Language", "eng", SEARCH_TYPE));

        doc.add(new Field("Language_raw", "eng", RAW_TYPE));

        doc.add(new Field("Language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("Format", "ASCII MEDATLAS", SEARCH_TYPE));

        doc.add(new Field("Format_raw", "ASCII MEDATLAS", RAW_TYPE));

        doc.add(new Field("Format_sort", new BytesRef("ASCII MEDATLAS".getBytes()), SORT_TYPE));

        doc.add(new Field("ID", "0UINDITENE", SEARCH_TYPE));

        doc.add(new Field("ID_raw", "0UINDITENE", RAW_TYPE));

        doc.add(new Field("ID_sort", new BytesRef("0UINDITENE".getBytes()), SORT_TYPE));

        doc.add(new Field("KeywordType", "parameter", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "parameter", RAW_TYPE));

        doc.add(new Field("KeywordType_sort", new BytesRef("parameter".getBytes()), SORT_TYPE));

        doc.add(new Field("KeywordType", "INSTRUMENT", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "INSTRUMENT", RAW_TYPE));

        doc.add(new Field("KeywordType", "platform_class", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "platform_class", RAW_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName_sort", new BytesRef("IFREMER / IDM/SISMER".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName", "IRD ANTENNE INSTITUT OCEANOGRAPHIQUE (IRD)", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IRD ANTENNE INSTITUT OCEANOGRAPHIQUE (IRD)", RAW_TYPE));

        doc.add(new Field("AlternateTitle", "11325_158_19640418141800", SEARCH_TYPE));

        doc.add(new Field("AlternateTitle_raw", "11325_158_19640418141800", RAW_TYPE));

        doc.add(new Field("AlternateTitle_sort", new BytesRef("11325_158_19640418141800".getBytes()), SORT_TYPE));

        doc.add(new Field("PublicationDate", "null", SEARCH_TYPE));

        doc.add(new Field("PublicationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("PublicationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("Abstract", "Donnees Bouteille Campagne 64061411 -  268 stations + Meteo", SEARCH_TYPE));

        doc.add(new Field("Abstract_raw", "Donnees Bouteille Campagne 64061411 -  268 stations + Meteo", RAW_TYPE));

        doc.add(new Field("Abstract_sort", new BytesRef("Donnees Bouteille Campagne 64061411 -  268 stations + Meteo".getBytes()), SORT_TYPE));

        doc.add(new Field("Title", "64061411.bot", SEARCH_TYPE));

        doc.add(new Field("Title_raw", "64061411.bot", RAW_TYPE));

        doc.add(new Field("Title_sort", new BytesRef("64061411.bot".getBytes()), SORT_TYPE));

        doc.add(new Field("TopicCategory", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("TopicCategory_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("TopicCategory_sort", new BytesRef("OCEANS".getBytes()), SORT_TYPE));

        doc.add(new Field("ResourceLanguage", "eng", SEARCH_TYPE));

        doc.add(new Field("ResourceLanguage_raw", "eng", RAW_TYPE));

        doc.add(new Field("ResourceLanguage_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("Subject_sort", new BytesRef("Electrical conductivity of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "Dissolved oxygen parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Dissolved oxygen parameters in the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Dissolved noble gas concentration parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Dissolved noble gas concentration parameters in the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Dissolved concentration parameters for 'other' gases in the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Dissolved concentration parameters for 'other' gases in the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("Subject", "discrete water samplers", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "discrete water samplers", RAW_TYPE));

        doc.add(new Field("Subject", "research vessel", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("Subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("Type", "DATASET", SEARCH_TYPE));

        doc.add(new Field("Type_raw", "DATASET", RAW_TYPE));

        doc.add(new Field("Type_sort", new BytesRef("DATASET".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_end", "1964-05-29T11:06:00.00", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "1964-05-29T11:06:00.00", RAW_TYPE));

        doc.add(new Field("TempExtent_end_sort", new BytesRef("1964-05-29T11:06:00.00".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_end", "19640529100600", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "19640529100600", RAW_TYPE));

        doc.add(new Field("RevisionDate", "19640219072400", SEARCH_TYPE));

        doc.add(new Field("RevisionDate_raw", "19640219072400", RAW_TYPE));

        doc.add(new Field("RevisionDate_sort", new BytesRef("19640219072400".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_begin", "1964-02-19T07:24:00.00", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "1964-02-19T07:24:00.00", RAW_TYPE));

        doc.add(new Field("TempExtent_begin_sort", new BytesRef("1964-02-19T07:24:00.00".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_begin", "19640219062400", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "19640219062400", RAW_TYPE));

        doc.add(new Field("id", "11325_158_19640418141800", ID_TYPE));

        doc.add(new Field("identifier", "11325_158_19640418141800", SEARCH_TYPE));

        doc.add(new Field("identifier_raw", "11325_158_19640418141800", RAW_TYPE));

        doc.add(new Field("identifier_sort", new BytesRef("11325_158_19640418141800".getBytes()), SORT_TYPE));

        doc.add(new Field("date", "20090126112224", SEARCH_TYPE));

        doc.add(new Field("date_raw", "20090126112224", RAW_TYPE));

        doc.add(new Field("date_sort", new BytesRef("20090126112224".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("creator_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("TemporalExtent_end", "1964-05-29T11:06:00.00", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "1964-05-29T11:06:00.00", RAW_TYPE));

        doc.add(new Field("TemporalExtent_end_sort", new BytesRef("1964-05-29T11:06:00.00".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_end", "19640529100600", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "19640529100600", RAW_TYPE));

        doc.add(new Field("subject", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("subject_sort", new BytesRef("Electrical conductivity of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("subject", "Dissolved oxygen parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Dissolved oxygen parameters in the water column", RAW_TYPE));

        doc.add(new Field("subject", "Dissolved noble gas concentration parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Dissolved noble gas concentration parameters in the water column", RAW_TYPE));

        doc.add(new Field("subject", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("subject", "Dissolved concentration parameters for 'other' gases in the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Dissolved concentration parameters for 'other' gases in the water column", RAW_TYPE));

        doc.add(new Field("subject", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("subject", "discrete water samplers", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "discrete water samplers", RAW_TYPE));

        doc.add(new Field("subject", "research vessel", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("format", "ASCII MEDATLAS", SEARCH_TYPE));

        doc.add(new Field("format_raw", "ASCII MEDATLAS", RAW_TYPE));

        doc.add(new Field("format_sort", new BytesRef("ASCII MEDATLAS".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("description_sort", new BytesRef("Electrical conductivity of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "Dissolved oxygen parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Dissolved oxygen parameters in the water column", RAW_TYPE));

        doc.add(new Field("description", "Dissolved noble gas concentration parameters in the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Dissolved noble gas concentration parameters in the water column", RAW_TYPE));

        doc.add(new Field("description", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("description", "Dissolved concentration parameters for 'other' gases in the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Dissolved concentration parameters for 'other' gases in the water column", RAW_TYPE));

        doc.add(new Field("description", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("description", "discrete water samplers", SEARCH_TYPE));

        doc.add(new Field("description_raw", "discrete water samplers", RAW_TYPE));

        doc.add(new Field("description", "research vessel", SEARCH_TYPE));

        doc.add(new Field("description_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("description", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("description_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("language", "eng", SEARCH_TYPE));

        doc.add(new Field("language_raw", "eng", RAW_TYPE));

        doc.add(new Field("language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("abstract", "Donnees Bouteille Campagne 64061411 -  268 stations + Meteo", SEARCH_TYPE));

        doc.add(new Field("abstract_raw", "Donnees Bouteille Campagne 64061411 -  268 stations + Meteo", RAW_TYPE));

        doc.add(new Field("abstract_sort", new BytesRef("Donnees Bouteille Campagne 64061411 -  268 stations + Meteo".getBytes()), SORT_TYPE));

        doc.add(new Field("title", "64061411.bot", SEARCH_TYPE));

        doc.add(new Field("title_raw", "64061411.bot", RAW_TYPE));

        doc.add(new Field("title_sort", new BytesRef("64061411.bot".getBytes()), SORT_TYPE));

        doc.add(new Field("type", "DATASET", SEARCH_TYPE));

        doc.add(new Field("type_raw", "DATASET", RAW_TYPE));

        doc.add(new Field("type_sort", new BytesRef("DATASET".getBytes()), SORT_TYPE));

        doc.add(new Field("relation", "64061411", SEARCH_TYPE));

        doc.add(new Field("relation_raw", "64061411", RAW_TYPE));

        doc.add(new Field("relation_sort", new BytesRef("64061411".getBytes()), SORT_TYPE));

        doc.add(new Field("relation", "158", SEARCH_TYPE));

        doc.add(new Field("relation_raw", "158", RAW_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("contributor_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("rights", "LICENCE", SEARCH_TYPE));

        doc.add(new Field("rights_raw", "LICENCE", RAW_TYPE));

        doc.add(new Field("rights_sort", new BytesRef("LICENCE".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        doc.add(new Field("publisher_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin", "1964-02-19T07:24:00.00", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "1964-02-19T07:24:00.00", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin_sort", new BytesRef("1964-02-19T07:24:00.00".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_begin", "19640219062400", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "19640219062400", RAW_TYPE));

        indexNumericField("WestBoundLongitude", 9.2667d, doc);

        indexNumericField("WestBoundLongitude_sort", 9.2667d, doc);

        indexNumericField("SouthBoundLatitude", 3.55d, doc);

        indexNumericField("SouthBoundLatitude_sort", 3.55d, doc);

        indexNumericField("NorthBoundLatitude", 3.55d, doc);

        indexNumericField("NorthBoundLatitude_sort", 3.55d, doc);

        indexNumericField("EastBoundLongitude", 9.2667d, doc);

        indexNumericField("EastBoundLongitude_sort", 9.2667d, doc);

        env = addBoundingBox(doc, 9.2667d, 9.2667d,  3.55d,   3.55d, CommonCRS.defaultGeographic());

        docs.add(new DocumentEnvelope(doc, env));
        doc = new Document();
        doc.add(new Field("metafile", "doc", SEARCH_TYPE));
        doc.add(new Field("Modified", "20080126112252", SEARCH_TYPE));

        doc.add(new Field("Modified_raw", "20080126112252", RAW_TYPE));

        doc.add(new Field("Modified_sort", new BytesRef("20080126112252".getBytes()), SORT_TYPE));

        doc.add(new Field("Identifier", "40510_145_19930221211500", SEARCH_TYPE));

        doc.add(new Field("Identifier_raw", "40510_145_19930221211500", RAW_TYPE));

        doc.add(new Field("Identifier_sort", new BytesRef("40510_145_19930221211500".getBytes()), SORT_TYPE));

        doc.add(new Field("Language", "eng", SEARCH_TYPE));

        doc.add(new Field("Language_raw", "eng", RAW_TYPE));

        doc.add(new Field("Language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("Format", "MEDATLAS ASCII", SEARCH_TYPE));

        doc.add(new Field("Format_raw", "MEDATLAS ASCII", RAW_TYPE));

        doc.add(new Field("Format_sort", new BytesRef("MEDATLAS ASCII".getBytes()), SORT_TYPE));

        doc.add(new Field("ID", "World Geodetic System 84", SEARCH_TYPE));

        doc.add(new Field("ID_raw", "World Geodetic System 84", RAW_TYPE));

        doc.add(new Field("ID_sort", new BytesRef("World Geodetic System 84".getBytes()), SORT_TYPE));

        doc.add(new Field("KeywordType", "parameter", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "parameter", RAW_TYPE));

        doc.add(new Field("KeywordType_sort", new BytesRef("parameter".getBytes()), SORT_TYPE));

        doc.add(new Field("KeywordType", "INSTRUMENT", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "INSTRUMENT", RAW_TYPE));

        doc.add(new Field("KeywordType", "platform_class", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "platform_class", RAW_TYPE));

        doc.add(new Field("KeywordType", "PROJECT", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "PROJECT", RAW_TYPE));

        doc.add(new Field("PublicationDate", "null", SEARCH_TYPE));

        doc.add(new Field("PublicationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("PublicationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("CreationDate", "null", SEARCH_TYPE));

        doc.add(new Field("CreationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("CreationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("Abstract", "Donnees CTD COARE POI 165", SEARCH_TYPE));

        doc.add(new Field("Abstract_raw", "Donnees CTD COARE POI 165", RAW_TYPE));

        doc.add(new Field("Abstract_sort", new BytesRef("Donnees CTD COARE POI 165".getBytes()), SORT_TYPE));

        doc.add(new Field("AlternateTitle", "40510_145_19930221211500", SEARCH_TYPE));

        doc.add(new Field("AlternateTitle_raw", "40510_145_19930221211500", RAW_TYPE));

        doc.add(new Field("AlternateTitle_sort", new BytesRef("40510_145_19930221211500".getBytes()), SORT_TYPE));

        doc.add(new Field("Title", "92005711.ctd", SEARCH_TYPE));

        doc.add(new Field("Title_raw", "92005711.ctd", RAW_TYPE));

        doc.add(new Field("Title_sort", new BytesRef("92005711.ctd".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName_sort", new BytesRef("IFREMER / IDM/SISMER".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName", "IRD CENTRE DE  NOUMEA", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IRD CENTRE DE  NOUMEA", RAW_TYPE));

        doc.add(new Field("TopicCategory", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("TopicCategory_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("TopicCategory_sort", new BytesRef("OCEANS".getBytes()), SORT_TYPE));

        doc.add(new Field("ResourceLanguage", "eng", SEARCH_TYPE));

        doc.add(new Field("ResourceLanguage_raw", "eng", RAW_TYPE));

        doc.add(new Field("ResourceLanguage_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("Subject_sort", new BytesRef("Electrical conductivity of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("Subject", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("Subject", "CTD profilers", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "CTD profilers", RAW_TYPE));

        doc.add(new Field("Subject", "research vessel", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("Subject", "COARE FRANCE", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "COARE FRANCE", RAW_TYPE));

        doc.add(new Field("Subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("Type", "DATASET", SEARCH_TYPE));

        doc.add(new Field("Type_raw", "DATASET", RAW_TYPE));

        doc.add(new Field("Type_sort", new BytesRef("DATASET".getBytes()), SORT_TYPE));

        doc.add(new Field("RevisionDate", "19921130230000", SEARCH_TYPE));

        doc.add(new Field("RevisionDate_raw", "19921130230000", RAW_TYPE));

        doc.add(new Field("RevisionDate_sort", new BytesRef("19921130230000".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_end", "1993-03-02", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "1993-03-02", RAW_TYPE));

        doc.add(new Field("TempExtent_end_sort", new BytesRef("1993-03-02".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_end", "19930301230000", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "19930301230000", RAW_TYPE));

        doc.add(new Field("TempExtent_begin", "1992-12-01", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "1992-12-01", RAW_TYPE));

        doc.add(new Field("TempExtent_begin_sort", new BytesRef("1992-12-01".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_begin", "19921130230000", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "19921130230000", RAW_TYPE));

        doc.add(new Field("id", "40510_145_19930221211500", ID_TYPE));

        doc.add(new Field("identifier", "40510_145_19930221211500", SEARCH_TYPE));

        doc.add(new Field("identifier_raw", "40510_145_19930221211500", RAW_TYPE));

        doc.add(new Field("identifier_sort", new BytesRef("40510_145_19930221211500".getBytes()), SORT_TYPE));

        doc.add(new Field("date", "20080126112252", SEARCH_TYPE));

        doc.add(new Field("date_raw", "20080126112252", RAW_TYPE));

        doc.add(new Field("date_sort", new BytesRef("20080126112252".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("creator_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("TemporalExtent_end", "1993-03-02", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "1993-03-02", RAW_TYPE));

        doc.add(new Field("TemporalExtent_end_sort", new BytesRef("1993-03-02".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_end", "19930301230000", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "19930301230000", RAW_TYPE));

        doc.add(new Field("subject", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("subject_sort", new BytesRef("Electrical conductivity of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("subject", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("subject", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("subject", "CTD profilers", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "CTD profilers", RAW_TYPE));

        doc.add(new Field("subject", "research vessel", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("subject", "COARE FRANCE", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "COARE FRANCE", RAW_TYPE));

        doc.add(new Field("subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("format", "MEDATLAS ASCII", SEARCH_TYPE));

        doc.add(new Field("format_raw", "MEDATLAS ASCII", RAW_TYPE));

        doc.add(new Field("format_sort", new BytesRef("MEDATLAS ASCII".getBytes()), SORT_TYPE));

        doc.add(new Field("language", "eng", SEARCH_TYPE));

        doc.add(new Field("language_raw", "eng", RAW_TYPE));

        doc.add(new Field("language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("type", "DATASET", SEARCH_TYPE));

        doc.add(new Field("type_raw", "DATASET", RAW_TYPE));

        doc.add(new Field("type_sort", new BytesRef("DATASET".getBytes()), SORT_TYPE));

        doc.add(new Field("abstract", "Donnees CTD COARE POI 165", SEARCH_TYPE));

        doc.add(new Field("abstract_raw", "Donnees CTD COARE POI 165", RAW_TYPE));

        doc.add(new Field("abstract_sort", new BytesRef("Donnees CTD COARE POI 165".getBytes()), SORT_TYPE));

        doc.add(new Field("title", "92005711.ctd", SEARCH_TYPE));

        doc.add(new Field("title_raw", "92005711.ctd", RAW_TYPE));

        doc.add(new Field("title_sort", new BytesRef("92005711.ctd".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "Electrical conductivity of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Electrical conductivity of the water column", RAW_TYPE));

        doc.add(new Field("description_sort", new BytesRef("Electrical conductivity of the water column".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "Salinity of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Salinity of the water column", RAW_TYPE));

        doc.add(new Field("description", "Temperature of the water column", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Temperature of the water column", RAW_TYPE));

        doc.add(new Field("description", "CTD profilers", SEARCH_TYPE));

        doc.add(new Field("description_raw", "CTD profilers", RAW_TYPE));

        doc.add(new Field("description", "research vessel", SEARCH_TYPE));

        doc.add(new Field("description_raw", "research vessel", RAW_TYPE));

        doc.add(new Field("description", "COARE FRANCE", SEARCH_TYPE));

        doc.add(new Field("description_raw", "COARE FRANCE", RAW_TYPE));

        doc.add(new Field("description", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("description_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin", "1992-12-01", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "1992-12-01", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin_sort", new BytesRef("1992-12-01".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_begin", "19921130230000", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "19921130230000", RAW_TYPE));

        doc.add(new Field("relation", "COARE POI", SEARCH_TYPE));

        doc.add(new Field("relation_raw", "COARE POI", RAW_TYPE));

        doc.add(new Field("relation_sort", new BytesRef("COARE POI".getBytes()), SORT_TYPE));

        doc.add(new Field("relation", "145", SEARCH_TYPE));

        doc.add(new Field("relation_raw", "145", RAW_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("contributor_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("rights", "LICENCE", SEARCH_TYPE));

        doc.add(new Field("rights_raw", "LICENCE", RAW_TYPE));

        doc.add(new Field("rights_sort", new BytesRef("LICENCE".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        doc.add(new Field("publisher_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        indexNumericField("WestBoundLongitude", 156.0d, doc);

        indexNumericField("WestBoundLongitude_sort", 156.0d, doc);

        indexNumericField("SouthBoundLatitude", 0.0d, doc);

        indexNumericField("SouthBoundLatitude_sort", 0.0d, doc);

        indexNumericField("NorthBoundLatitude", 0.0d, doc);

        indexNumericField("NorthBoundLatitude_sort", 0.0d, doc);

        indexNumericField("EastBoundLongitude", 156.0d, doc);

        indexNumericField("EastBoundLongitude_sort", 156.0d, doc);

        env = addBoundingBox(doc, 156.0d, 156.0d,  0.0d,  0.0d, CommonCRS.defaultGeographic());

        docs.add(new DocumentEnvelope(doc, env));
        doc = new Document();
        doc.add(new Field("metafile", "doc", SEARCH_TYPE));
        doc.add(new Field("Modified", "20090210000000", SEARCH_TYPE));

        doc.add(new Field("Modified_raw", "20090210000000", RAW_TYPE));

        doc.add(new Field("Modified_sort", new BytesRef("20090210000000".getBytes()), SORT_TYPE));

        doc.add(new Field("CreationDate", "null", SEARCH_TYPE));

        doc.add(new Field("CreationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("CreationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("Identifier", "CTDF02", SEARCH_TYPE));

        doc.add(new Field("Identifier_raw", "CTDF02", RAW_TYPE));

        doc.add(new Field("Identifier_sort", new BytesRef("CTDF02".getBytes()), SORT_TYPE));

        doc.add(new Field("Language", "eng", SEARCH_TYPE));

        doc.add(new Field("Language_raw", "eng", RAW_TYPE));

        doc.add(new Field("Language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName_sort", new BytesRef("IFREMER / IDM/SISMER".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("OrganisationName", "IFREMER / IDM/SISMER", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "IFREMER / IDM/SISMER", RAW_TYPE));

        doc.add(new Field("PublicationDate", "null", SEARCH_TYPE));

        doc.add(new Field("PublicationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("PublicationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("AlternateTitle", "CTDF02", SEARCH_TYPE));

        doc.add(new Field("AlternateTitle_raw", "CTDF02", RAW_TYPE));

        doc.add(new Field("AlternateTitle_sort", new BytesRef("CTDF02".getBytes()), SORT_TYPE));

        doc.add(new Field("Abstract", "CTD data collected during the French oceanographic cruises or joint programs.", SEARCH_TYPE));

        doc.add(new Field("Abstract_raw", "CTD data collected during the French oceanographic cruises or joint programs.", RAW_TYPE));

        doc.add(new Field("Abstract_sort", new BytesRef("CTD data collected during the French oceanographic cruises or joint programs.".getBytes()), SORT_TYPE));

        doc.add(new Field("KeywordType", "PLACE", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "PLACE", RAW_TYPE));

        doc.add(new Field("KeywordType_sort", new BytesRef("PLACE".getBytes()), SORT_TYPE));

        doc.add(new Field("KeywordType", "parameter", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "parameter", RAW_TYPE));

        doc.add(new Field("KeywordType", "projects", SEARCH_TYPE));

        doc.add(new Field("KeywordType_raw", "projects", RAW_TYPE));

        doc.add(new Field("Title", "FRENCH CTD DATA - VERTICAL PROFILES", SEARCH_TYPE));

        doc.add(new Field("Title_raw", "FRENCH CTD DATA - VERTICAL PROFILES", RAW_TYPE));

        doc.add(new Field("Title_sort", new BytesRef("FRENCH CTD DATA - VERTICAL PROFILES".getBytes()), SORT_TYPE));

        doc.add(new Field("TopicCategory", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("TopicCategory_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("TopicCategory_sort", new BytesRef("OCEANS".getBytes()), SORT_TYPE));

        doc.add(new Field("ResourceLanguage", "eng", SEARCH_TYPE));

        doc.add(new Field("ResourceLanguage_raw", "eng", RAW_TYPE));

        doc.add(new Field("ResourceLanguage_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "WORLD WIDE COVERAGE", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "WORLD WIDE COVERAGE", RAW_TYPE));

        doc.add(new Field("Subject_sort", new BytesRef("WORLD WIDE COVERAGE".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "Pressure, temperature, conductivity (or salinity), dissolved Oxygen, nephelometry, fluorescence", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Pressure, temperature, conductivity (or salinity), dissolved Oxygen, nephelometry, fluorescence", RAW_TYPE));

        doc.add(new Field("Subject", "OCEANOGRAPHIC DATA CENTER", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "OCEANOGRAPHIC DATA CENTER", RAW_TYPE));

        doc.add(new Field("Subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("Type", "SERIES", SEARCH_TYPE));

        doc.add(new Field("Type_raw", "SERIES", RAW_TYPE));

        doc.add(new Field("Type_sort", new BytesRef("SERIES".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_end", "2009-02-10", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "2009-02-10", RAW_TYPE));

        doc.add(new Field("TempExtent_end_sort", new BytesRef("2009-02-10".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_end", "20090209230000", SEARCH_TYPE));

        doc.add(new Field("TempExtent_end_raw", "20090209230000", RAW_TYPE));

        doc.add(new Field("RevisionDate", "20080603220000", SEARCH_TYPE));

        doc.add(new Field("RevisionDate_raw", "20080603220000", RAW_TYPE));

        doc.add(new Field("RevisionDate_sort", new BytesRef("20080603220000".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_begin", "1971-01-01", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "1971-01-01", RAW_TYPE));

        doc.add(new Field("TempExtent_begin_sort", new BytesRef("1971-01-01".getBytes()), SORT_TYPE));

        doc.add(new Field("TempExtent_begin", "19701231230000", SEARCH_TYPE));

        doc.add(new Field("TempExtent_begin_raw", "19701231230000", RAW_TYPE));

        doc.add(new Field("id", "CTDF02", ID_TYPE));

        doc.add(new Field("identifier", "CTDF02", SEARCH_TYPE));

        doc.add(new Field("identifier_raw", "CTDF02", RAW_TYPE));

        doc.add(new Field("identifier_sort", new BytesRef("CTDF02".getBytes()), SORT_TYPE));

        doc.add(new Field("date", "20090210000000", SEARCH_TYPE));

        doc.add(new Field("date_raw", "20090210000000", RAW_TYPE));

        doc.add(new Field("date_sort", new BytesRef("20090210000000".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_end", "2009-02-10", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "2009-02-10", RAW_TYPE));

        doc.add(new Field("TemporalExtent_end_sort", new BytesRef("2009-02-10".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_end", "20090209230000", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_end_raw", "20090209230000", RAW_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("creator_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("subject", "WORLD WIDE COVERAGE", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "WORLD WIDE COVERAGE", RAW_TYPE));

        doc.add(new Field("subject_sort", new BytesRef("WORLD WIDE COVERAGE".getBytes()), SORT_TYPE));

        doc.add(new Field("subject", "Pressure, temperature, conductivity (or salinity), dissolved Oxygen, nephelometry, fluorescence", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Pressure, temperature, conductivity (or salinity), dissolved Oxygen, nephelometry, fluorescence", RAW_TYPE));

        doc.add(new Field("subject", "OCEANOGRAPHIC DATA CENTER", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "OCEANOGRAPHIC DATA CENTER", RAW_TYPE));

        doc.add(new Field("subject", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("language", "eng", SEARCH_TYPE));

        doc.add(new Field("language_raw", "eng", RAW_TYPE));

        doc.add(new Field("language_sort", new BytesRef("eng".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "WORLD WIDE COVERAGE", SEARCH_TYPE));

        doc.add(new Field("description_raw", "WORLD WIDE COVERAGE", RAW_TYPE));

        doc.add(new Field("description_sort", new BytesRef("WORLD WIDE COVERAGE".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "Pressure, temperature, conductivity (or salinity), dissolved Oxygen, nephelometry, fluorescence", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Pressure, temperature, conductivity (or salinity), dissolved Oxygen, nephelometry, fluorescence", RAW_TYPE));

        doc.add(new Field("description", "OCEANOGRAPHIC DATA CENTER", SEARCH_TYPE));

        doc.add(new Field("description_raw", "OCEANOGRAPHIC DATA CENTER", RAW_TYPE));

        doc.add(new Field("description", "OCEANS", SEARCH_TYPE));

        doc.add(new Field("description_raw", "OCEANS", RAW_TYPE));

        doc.add(new Field("abstract", "CTD data collected during the French oceanographic cruises or joint programs.", SEARCH_TYPE));

        doc.add(new Field("abstract_raw", "CTD data collected during the French oceanographic cruises or joint programs.", RAW_TYPE));

        doc.add(new Field("abstract_sort", new BytesRef("CTD data collected during the French oceanographic cruises or joint programs.".getBytes()), SORT_TYPE));

        doc.add(new Field("type", "SERIES", SEARCH_TYPE));

        doc.add(new Field("type_raw", "SERIES", RAW_TYPE));

        doc.add(new Field("type_sort", new BytesRef("SERIES".getBytes()), SORT_TYPE));

        doc.add(new Field("title", "FRENCH CTD DATA - VERTICAL PROFILES", SEARCH_TYPE));

        doc.add(new Field("title_raw", "FRENCH CTD DATA - VERTICAL PROFILES", RAW_TYPE));

        doc.add(new Field("title_sort", new BytesRef("FRENCH CTD DATA - VERTICAL PROFILES".getBytes()), SORT_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("contributor_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin", "1971-01-01", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "1971-01-01", RAW_TYPE));

        doc.add(new Field("TemporalExtent_begin_sort", new BytesRef("1971-01-01".getBytes()), SORT_TYPE));

        doc.add(new Field("TemporalExtent_begin", "19701231230000", SEARCH_TYPE));

        doc.add(new Field("TemporalExtent_begin_raw", "19701231230000", RAW_TYPE));

        doc.add(new Field("rights", "NoAccessRestriction", SEARCH_TYPE));

        doc.add(new Field("rights_raw", "NoAccessRestriction", RAW_TYPE));

        doc.add(new Field("rights_sort", new BytesRef("NoAccessRestriction".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        doc.add(new Field("publisher_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

         indexNumericField("WestBoundLongitude", -179.0d, doc);

        indexNumericField("WestBoundLongitude_sort", -179.0d, doc);

        indexNumericField("SouthBoundLatitude", -90.0d, doc);

        indexNumericField("SouthBoundLatitude_sort", -90.0d, doc);

        indexNumericField("NorthBoundLatitude", 90.0d, doc);

        indexNumericField("NorthBoundLatitude_sort", 90.0d, doc);

        indexNumericField("EastBoundLongitude", 180.0d, doc);

        indexNumericField("EastBoundLongitude_sort", 180.0d, doc);


        env = addBoundingBox(doc, -179.0d, 180.0d,  -90.0d,  90.0d, CommonCRS.defaultGeographic());

        docs.add(new DocumentEnvelope(doc, env));
        doc = new Document();
        doc.add(new Field("metafile", "doc", SEARCH_TYPE));
        doc.add(new Field("Modified", "20061005220000", SEARCH_TYPE));

        doc.add(new Field("Modified_raw", "20061005220000", RAW_TYPE));

        doc.add(new Field("Modified_sort", new BytesRef("20061005220000".getBytes()), SORT_TYPE));

        doc.add(new Field("CreationDate", "20051231230000", SEARCH_TYPE));

        doc.add(new Field("CreationDate_raw", "20051231230000", RAW_TYPE));

        doc.add(new Field("CreationDate_sort", new BytesRef("20051231230000".getBytes()), SORT_TYPE));

        doc.add(new Field("GeographicDescriptionCode", "N_43.456_E_3.692_S_43.331_W_3.543", SEARCH_TYPE));

        doc.add(new Field("GeographicDescriptionCode_raw", "N_43.456_E_3.692_S_43.331_W_3.543", RAW_TYPE));

        doc.add(new Field("GeographicDescriptionCode_sort", new BytesRef("N_43.456_E_3.692_S_43.331_W_3.543".getBytes()), SORT_TYPE));

        doc.add(new Field("Identifier", "MDWeb_FR_SY_couche_vecteur_258", SEARCH_TYPE));

        doc.add(new Field("Identifier_raw", "MDWeb_FR_SY_couche_vecteur_258", RAW_TYPE));

        doc.add(new Field("Identifier_sort", new BytesRef("MDWeb_FR_SY_couche_vecteur_258".getBytes()), SORT_TYPE));

        doc.add(new Field("Format", "shape file", SEARCH_TYPE));

        doc.add(new Field("Format_raw", "shape file", RAW_TYPE));

        doc.add(new Field("Format_sort", new BytesRef("shape file".getBytes()), SORT_TYPE));

        doc.add(new Field("Language", "fre", SEARCH_TYPE));

        doc.add(new Field("Language_raw", "fre", RAW_TYPE));

        doc.add(new Field("Language_sort", new BytesRef("fre".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "ifremer Laboratoire Environnement Ressource du Languedoc Roussillon (LER LR)", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "ifremer Laboratoire Environnement Ressource du Languedoc Roussillon (LER LR)", RAW_TYPE));

        doc.add(new Field("OrganisationName_sort", new BytesRef("ifremer Laboratoire Environnement Ressource du Languedoc Roussillon (LER LR)".getBytes()), SORT_TYPE));

        doc.add(new Field("OrganisationName", "ifremer Laboratoire Environnement Ressource du Languedoc Roussillon (LER LR)", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "ifremer Laboratoire Environnement Ressource du Languedoc Roussillon (LER LR)", RAW_TYPE));

        doc.add(new Field("OrganisationName", "ifremer Laboratoire Environnement Ressource du Languedoc Roussillon (LER LR)", SEARCH_TYPE));

        doc.add(new Field("OrganisationName_raw", "ifremer Laboratoire Environnement Ressource du Languedoc Roussillon (LER LR)", RAW_TYPE));

        doc.add(new Field("PublicationDate", "null", SEARCH_TYPE));

        doc.add(new Field("PublicationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("PublicationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("Abstract", "Abondance de cellule de cyanopicophytoplancton (< 3 µm). Méthode: cytométrie en flux.", SEARCH_TYPE));

        doc.add(new Field("Abstract_raw", "Abondance de cellule de cyanopicophytoplancton (< 3 µm). Méthode: cytométrie en flux.", RAW_TYPE));

        doc.add(new Field("Abstract_sort", new BytesRef("Abondance de cellule de cyanopicophytoplancton (< 3 µm). Méthode: cytométrie en flux.".getBytes()), SORT_TYPE));

        doc.add(new Field("Title", "Physico-chimie de la colonne d'eau (cyanopicophytoplancton), acquis dans le cadre du Réseau du Suivi Lagunaire: lagune de Thau", SEARCH_TYPE));

        doc.add(new Field("Title_raw", "Physico-chimie de la colonne d'eau (cyanopicophytoplancton), acquis dans le cadre du Réseau du Suivi Lagunaire: lagune de Thau", RAW_TYPE));

        doc.add(new Field("Title_sort", new BytesRef("Physico-chimie de la colonne d'eau (cyanopicophytoplancton), acquis dans le cadre du Réseau du Suivi Lagunaire: lagune de Thau".getBytes()), SORT_TYPE));

        doc.add(new Field("TopicCategory", "ENVIRONMENT", SEARCH_TYPE));

        doc.add(new Field("TopicCategory_raw", "ENVIRONMENT", RAW_TYPE));

        doc.add(new Field("TopicCategory_sort", new BytesRef("ENVIRONMENT".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "Eutrophisation", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "Eutrophisation", RAW_TYPE));

        doc.add(new Field("Subject_sort", new BytesRef("Eutrophisation".getBytes()), SORT_TYPE));

        doc.add(new Field("Subject", "thau", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "thau", RAW_TYPE));

        doc.add(new Field("Subject", "ENVIRONMENT", SEARCH_TYPE));

        doc.add(new Field("Subject_raw", "ENVIRONMENT", RAW_TYPE));

        doc.add(new Field("RevisionDate", "null", SEARCH_TYPE));

        doc.add(new Field("RevisionDate_raw", "null", RAW_TYPE));

        doc.add(new Field("RevisionDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        indexNumericField("Denominator", 32l, doc);

        indexNumericField("Denominator_sort", 32l, doc);

        doc.add(new Field("id", "MDWeb_FR_SY_couche_vecteur_258", ID_TYPE));

        doc.add(new Field("identifier", "MDWeb_FR_SY_couche_vecteur_258", SEARCH_TYPE));

        doc.add(new Field("identifier_raw", "MDWeb_FR_SY_couche_vecteur_258", RAW_TYPE));

        doc.add(new Field("identifier_sort", new BytesRef("MDWeb_FR_SY_couche_vecteur_258".getBytes()), SORT_TYPE));

        doc.add(new Field("date", "20061005220000", SEARCH_TYPE));

        doc.add(new Field("date_raw", "20061005220000", RAW_TYPE));

        doc.add(new Field("date_sort", new BytesRef("20061005220000".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("creator_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("format", "shape file", SEARCH_TYPE));

        doc.add(new Field("format_raw", "shape file", RAW_TYPE));

        doc.add(new Field("format_sort", new BytesRef("shape file".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "Eutrophisation", SEARCH_TYPE));

        doc.add(new Field("description_raw", "Eutrophisation", RAW_TYPE));

        doc.add(new Field("description_sort", new BytesRef("Eutrophisation".getBytes()), SORT_TYPE));

        doc.add(new Field("description", "thau", SEARCH_TYPE));

        doc.add(new Field("description_raw", "thau", RAW_TYPE));

        doc.add(new Field("description", "ENVIRONMENT", SEARCH_TYPE));

        doc.add(new Field("description_raw", "ENVIRONMENT", RAW_TYPE));

        doc.add(new Field("subject", "Eutrophisation", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "Eutrophisation", RAW_TYPE));

        doc.add(new Field("subject_sort", new BytesRef("Eutrophisation".getBytes()), SORT_TYPE));

        doc.add(new Field("subject", "thau", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "thau", RAW_TYPE));

        doc.add(new Field("subject", "ENVIRONMENT", SEARCH_TYPE));

        doc.add(new Field("subject_raw", "ENVIRONMENT", RAW_TYPE));

        doc.add(new Field("language", "fre", SEARCH_TYPE));

        doc.add(new Field("language_raw", "fre", RAW_TYPE));

        doc.add(new Field("language_sort", new BytesRef("fre".getBytes()), SORT_TYPE));

        doc.add(new Field("abstract", "Abondance de cellule de cyanopicophytoplancton (< 3 µm). Méthode: cytométrie en flux.", SEARCH_TYPE));

        doc.add(new Field("abstract_raw", "Abondance de cellule de cyanopicophytoplancton (< 3 µm). Méthode: cytométrie en flux.", RAW_TYPE));

        doc.add(new Field("abstract_sort", new BytesRef("Abondance de cellule de cyanopicophytoplancton (< 3 µm). Méthode: cytométrie en flux.".getBytes()), SORT_TYPE));

        doc.add(new Field("title", "Physico-chimie de la colonne d'eau (cyanopicophytoplancton), acquis dans le cadre du Réseau du Suivi Lagunaire: lagune de Thau", SEARCH_TYPE));

        doc.add(new Field("title_raw", "Physico-chimie de la colonne d'eau (cyanopicophytoplancton), acquis dans le cadre du Réseau du Suivi Lagunaire: lagune de Thau", RAW_TYPE));

        doc.add(new Field("title_sort", new BytesRef("Physico-chimie de la colonne d'eau (cyanopicophytoplancton), acquis dans le cadre du Réseau du Suivi Lagunaire: lagune de Thau".getBytes()), SORT_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("contributor_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        doc.add(new Field("publisher_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        docs.add(new DocumentEnvelope(doc, null));

        doc = new Document();
        doc.add(new Field("metafile", "doc", SEARCH_TYPE));
        doc.add(new Field("CreationDate", "null", SEARCH_TYPE));

        doc.add(new Field("CreationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("CreationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("Identifier", "Spot5-Cyprus-THX-IMAGERY3_ortho1", SEARCH_TYPE));

        doc.add(new Field("Identifier_raw", "Spot5-Cyprus-THX-IMAGERY3_ortho1", RAW_TYPE));

        doc.add(new Field("Identifier_sort", new BytesRef("Spot5-Cyprus-THX-IMAGERY3_ortho1".getBytes()), SORT_TYPE));

        doc.add(new Field("PublicationDate", "null", SEARCH_TYPE));

        doc.add(new Field("PublicationDate_raw", "null", RAW_TYPE));

        doc.add(new Field("PublicationDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("RevisionDate", "null", SEARCH_TYPE));

        doc.add(new Field("RevisionDate_raw", "null", RAW_TYPE));

        doc.add(new Field("RevisionDate_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("id", "Spot5-Cyprus-THX-IMAGERY3_ortho1", ID_TYPE));

        doc.add(new Field("identifier", "Spot5-Cyprus-THX-IMAGERY3_ortho1", SEARCH_TYPE));

        doc.add(new Field("identifier_raw", "Spot5-Cyprus-THX-IMAGERY3_ortho1", RAW_TYPE));

        doc.add(new Field("identifier_sort", new BytesRef("Spot5-Cyprus-THX-IMAGERY3_ortho1".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("creator_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("creator", "null", SEARCH_TYPE));

        doc.add(new Field("creator_raw", "null", RAW_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("contributor_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("contributor", "null", SEARCH_TYPE));

        doc.add(new Field("contributor_raw", "null", RAW_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        doc.add(new Field("publisher_sort", new BytesRef("null".getBytes()), SORT_TYPE));

        doc.add(new Field("publisher", "null", SEARCH_TYPE));

        doc.add(new Field("publisher_raw", "null", RAW_TYPE));

        indexNumericField("WestBoundLongitude", 3.54323d, doc);

        indexNumericField("WestBoundLongitude_sort", 3.54323d, doc);

        indexNumericField("SouthBoundLatitude", 43.331364d, doc);

        indexNumericField("SouthBoundLatitude_sort", 43.331364d, doc);

        indexNumericField("NorthBoundLatitude", 43.456142d, doc);

        indexNumericField("NorthBoundLatitude_sort", 43.456142d, doc);

        indexNumericField("EastBoundLongitude", 3.692524d, doc);

        indexNumericField("EastBoundLongitude_sort", 3.692524d, doc);

        env = addBoundingBox(doc, 3.54323d, 3.692524d,  43.331364d, 43.456142d, CommonCRS.defaultGeographic());

        docs.add(new DocumentEnvelope(doc, env));
        return docs;
    }

    protected static void indexNumericField(final String fieldName, final Number numValue, final Document doc) {

        final Field numField;
        final Field numSortField;

        final FieldType numericType = new FieldType(SORT_TYPE);
        numericType.setDocValuesType(DocValuesType.SORTED_NUMERIC);
        if (numValue instanceof Integer) {
            numericType.setNumericType(FieldType.NumericType.INT);
            numField = new IntField(fieldName, (Integer) numValue, Field.Store.NO);
            numSortField = new IntField(fieldName + "_sort", (Integer) numValue, numericType);
        } else if (numValue instanceof Double) {
            numericType.setNumericType(FieldType.NumericType.DOUBLE);
            numField = new DoubleField(fieldName, (Double) numValue, Field.Store.NO);
            numSortField = new DoubleField(fieldName + "_sort", (Double) numValue, numericType);
        } else if (numValue instanceof Float) {
            numericType.setNumericType(FieldType.NumericType.FLOAT);
            numField = new FloatField(fieldName, (Float) numValue, Field.Store.NO);
            numSortField = new FloatField(fieldName + "_sort", (Float) numValue, numericType);
        } else if (numValue instanceof Long) {
            numericType.setNumericType(FieldType.NumericType.LONG);
            numField = new LongField(fieldName, (Long) numValue, Field.Store.NO);
            numSortField = new LongField(fieldName + "_sort", (Long) numValue, numericType);
        } else {
            throw new RuntimeException("numeric type not expected");
        }
        doc.add(numField);
        doc.add(numSortField);
    }

    protected void logResultReport(String reportName, Set<String> result) {
        String resultReport = "";
        for (String s : result) {
            resultReport = resultReport + s + '\n';
        }
        LOGGER.log(Level.FINER, reportName + "\n{0}", resultReport);
    }

    /**
     * Test simple lucene search.
     *
     * @throws java.lang.Exception
     */
    public void simpleSearchTest() throws Exception {
        Filter nullFilter = null;

        /**
         * Test 1 simple search: title = 90008411.ctd", SEARCH_TYPE));
         */
        SpatialQuery spatialQuery = new SpatialQuery("Title:\"90008411.ctd\"", nullFilter, LogicalFilterType.AND);
        Set<String> result = indexSearcher.doSearch(spatialQuery);
        logResultReport("simpleSearch 1:", result);

        // the result we want are this
        Set<String> expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");

        assertEquals(expectedResult, result);

        /**
         * Test 2 simple search: indentifier != 40510_145_19930221211500
         */
        spatialQuery = new SpatialQuery("metafile:doc NOT identifier:\"40510_145_19930221211500\"", nullFilter, LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("simpleSearch 2:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("CTDF02");
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");
        expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1");

        assertEquals(expectedResult, result);

        /**
         * Test 3 simple search: originator = Donnees CTD NEDIPROD VI 120
         */
        spatialQuery = new SpatialQuery("abstract:\"Donnees CTD NEDIPROD VI 120\"", nullFilter, LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("simpleSearch 3:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");

        assertEquals(expectedResult, result);

        /**
         * Test 4 simple search: ID = World Geodetic System 84
         */
        spatialQuery = new SpatialQuery("ID:\"World Geodetic System 84\"", nullFilter, LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("simpleSearch 4:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("40510_145_19930221211500");

        assertEquals(expectedResult, result);

        /**
         * Test 5 simple search: ID = 0UINDITENE
         */
        spatialQuery = new SpatialQuery("ID:\"0UINDITENE\"", nullFilter, LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("simpleSearch 5:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("11325_158_19640418141800");

        assertEquals(expectedResult, result);

        /**
         * Test 6 range search: Title <= FRA
         */
        spatialQuery = new SpatialQuery("Title_raw:[0 TO FRA]", nullFilter, LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("simpleSearch 6:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("40510_145_19930221211500");

        assertEquals(expectedResult, result);

        /**
         * Test 7 range search: Title > FRA
         */
        spatialQuery = new SpatialQuery("Title_raw:[FRA TO z]", nullFilter, LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("simpleSearch 7:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("CTDF02");
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");
        //expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1"); no more null value => Spot5 ... has no title

        assertEquals(expectedResult, result);
    }

    /**
     * Test simple lucene search.
     *
     * @throws java.lang.Exception
     */
    public void wildCharUnderscoreSearchTest() throws Exception {
        Filter nullFilter = null;

        /**
         * Test 1 simple search: title = title1
         */
        SpatialQuery spatialQuery = new SpatialQuery("identifier:*MDWeb_FR_SY*", nullFilter, LogicalFilterType.AND);
        Set<String> result = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharUnderscoreSearch 1:", result);

        Set<String> expectedResult = new LinkedHashSet<>();
        //expectedResult.add("MDWeb_FR_SY_couche_vecteur_258"); error '_' is tokenized

        assertEquals(expectedResult, result);

        /**
         * Test 2 simple search: title =
         * identifier:Spot5-Cyprus-THX-IMAGERY3_ortho*
         */
        spatialQuery = new SpatialQuery("identifier:Spot5-Cyprus-THX-IMAGERY3_ortho*", nullFilter, LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharUnderscoreSearch 2:", result);

        expectedResult = new LinkedHashSet<>();
        //expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1"); // error

        assertEquals(expectedResult, result);
    }

    public void dateSearchTest() throws Exception {
        Filter nullFilter = null;

        /**
         * Test 1 date search: date after 25/01/2009
         */
        SpatialQuery spatialQuery = new SpatialQuery("date:{20090125 30000101}", nullFilter, LogicalFilterType.AND);
        Set<String> result = indexSearcher.doSearch(spatialQuery);
        logResultReport("DateSearch 1:", result);

        Set<String> expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("CTDF02");

        assertEquals(expectedResult, result);
    }

    /**
     * Test sorted lucene search.
     *
     * @throws java.lang.Exception
     */
    public void sortedSearchTest() throws Exception {

        Filter nullFilter = null;

        /**
         * Test 1 sorted search: all orderBy identifier ASC
         */
        SpatialQuery spatialQuery = new SpatialQuery("metafile:doc", nullFilter, LogicalFilterType.AND);
        SortField sf = new SortField("identifier_sort", SortField.Type.STRING, false);
        spatialQuery.setSort(new Sort(sf));

        Set<String> result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SortedSearch 1:", result);

        Set<String> expectedResult = new LinkedHashSet<>();
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("CTDF02");
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");
        expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1");

        assertEquals(expectedResult, result);

        /**
         * Test 2 sorted search: all orderBy identifier DSC
         */
        spatialQuery = new SpatialQuery("metafile:doc", nullFilter, LogicalFilterType.AND);
        sf = new SortField("identifier_sort", SortField.Type.STRING, true);
        spatialQuery.setSort(new Sort(sf));

        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SortedSearch 2:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1");
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");
        expectedResult.add("CTDF02");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");

        assertEquals(expectedResult, result);

        /**
         * Test 3 sorted search: all orderBy Abstract ASC
         */
        spatialQuery = new SpatialQuery("metafile:doc", nullFilter, LogicalFilterType.AND);
        sf = new SortField("Abstract_sort", SortField.Type.STRING, false);
        spatialQuery.setSort(new Sort(sf));

        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SortedSearch 3:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");
        expectedResult.add("CTDF02");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1");

        assertEquals(expectedResult, result);

        /**
         * Test 4 sorted search: all orderBy Abstract DSC
         */
        spatialQuery = new SpatialQuery("metafile:doc", nullFilter, LogicalFilterType.AND);
        sf = new SortField("Abstract_sort", SortField.Type.STRING, true);
        spatialQuery.setSort(new Sort(sf));

        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SortedSearch 4:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1");
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("CTDF02");
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");

        assertEquals(expectedResult, result);
    }

    /**
     *
     * Test spatial lucene search.
     *
     * @throws java.lang.Exception
     */
    public void spatialSearchTest() throws Exception {

        /**
         * Test 1 spatial search: BBOX filter
         */
        double min1[] = {-20, -20};
        double max1[] = {20, 20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        CoordinateReferenceSystem crs = CommonCRS.defaultGeographic();
        bbox.setCoordinateReferenceSystem(crs);
        LuceneOGCFilter sf = LuceneOGCFilter.wrap(FF.bbox(LuceneOGCFilter.GEOMETRY_PROPERTY, -20, -20, 20, 20, "EPSG:4326"));
        SpatialQuery spatialQuery = new SpatialQuery("metafile:doc", sf, LogicalFilterType.AND);

        Set<String> result = indexSearcher.doSearch(spatialQuery);
        logResultReport("spatialSearch 1:", result);

        Set<String> expectedResult = new LinkedHashSet<>();
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("CTDF02");

        assertEquals(expectedResult, result);

        /**
         * Test 1 spatial search: NOT BBOX filter
         */
        List<Filter> lf = new ArrayList<>();
        //sf           = new BBOXFilter(bbox, "urn:x-ogc:def:crs:EPSG:6.11:4326");
        sf = LuceneOGCFilter.wrap(FF.bbox(LuceneOGCFilter.GEOMETRY_PROPERTY, -20, -20, 20, 20, "EPSG:4326"));
        lf.add(sf);
        LogicalFilterType[] op = {LogicalFilterType.NOT};
        SerialChainFilter f = new SerialChainFilter(lf, op);
        spatialQuery = new SpatialQuery("metafile:doc", f, LogicalFilterType.AND);

        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("spatialSearch 2:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");
        expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1");

        assertEquals(expectedResult, result);
    }

    public void TermQueryTest() throws Exception {

        /**
         * Test 1
         */
        String identifier = "39727_22_19750113062500";
        String result = indexSearcher.identifierQuery(identifier);

        LOGGER.log(Level.FINER, "identifier query 1:\n{0}", result);

        String expectedResult = "39727_22_19750113062500";

        assertEquals(expectedResult, result);

        /**
         * Test 2
         */
        identifier = "CTDF02";
        result = indexSearcher.identifierQuery(identifier);

        LOGGER.log(Level.FINER, "identifier query 2:\n{0}", result);

        expectedResult = "CTDF02";

        assertEquals(expectedResult, result);
    }

    private static NamedEnvelope addBoundingBox(final Document doc, final double minx, final double maxx, final double miny, final double maxy, final CoordinateReferenceSystem crs) throws FactoryException, TransformException {
        return addBoundingBox(doc, Arrays.asList(minx), Arrays.asList(maxx), Arrays.asList(miny), Arrays.asList(maxy), crs);
    }
    /**
     * Add a boundingBox geometry to the specified Document.
     *
     * @param doc The document to add the geometry
     * @param minx the minimun X coordinate of the bounding box.
     * @param maxx the maximum X coordinate of the bounding box.
     * @param miny the minimun Y coordinate of the bounding box.
     * @param maxy the maximum Y coordinate of the bounding box.
     * @param crsName The coordinate reference system in witch the coordinates
     * are expressed.
     */
    private static NamedEnvelope addBoundingBox(final Document doc, final List<Double> minx, final List<Double> maxx, final List<Double> miny, final List<Double> maxy, final CoordinateReferenceSystem crs) throws FactoryException, TransformException {

        final Polygon[] polygons = LuceneUtils.getPolygons(minx, maxx, miny, maxy, crs);
        Geometry geom = null;
        if (polygons.length == 1) {
            geom = polygons[0];
        } else if (polygons.length > 1 ){
            geom = LuceneUtils.GF.createGeometryCollection(polygons);
            JTS.setCRS(geom, crs);
        }
        final String id = doc.get("id");
        final NamedEnvelope namedBound = LuceneUtils.getNamedEnvelope(id, geom, treeCrs);

        doc.add(new StoredField(LuceneOGCFilter.GEOMETRY_FIELD_NAME, WKBUtils.toWKBwithSRID(geom)));
        return namedBound;
    }
}
