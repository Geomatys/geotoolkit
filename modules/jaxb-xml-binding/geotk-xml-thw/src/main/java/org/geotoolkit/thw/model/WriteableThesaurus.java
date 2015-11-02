/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

package org.geotoolkit.thw.model;

import java.io.IOException;
import java.sql.SQLException;
import org.geotoolkit.skos.xml.Concept;
import org.geotoolkit.skos.xml.RDF;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface WriteableThesaurus extends Thesaurus {
    
    void updateThesaurusProperties() throws SQLException;
    
    void store() throws SQLException, IOException;
    
    void delete() throws SQLException;
    
    void delete(final ISOLanguageCode language) throws SQLException;
    
    void writeRdf(final RDF rdf) throws SQLException;
    
    String writeConcept(final Concept concept) throws SQLException;
    
    void deleteConcept(final Concept concept) throws SQLException;
    
    void deleteConceptCascad(final Concept concept) throws SQLException;
 
    void addLanguage(final ISOLanguageCode currentLanguage) throws SQLException;
    
    void computeTopMostConcept() throws SQLException;
}
