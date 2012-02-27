/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.internal.simple;

import java.util.Date;
import java.util.Collection;
import java.util.Collections;
import java.io.Serializable;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Series;
import org.opengis.util.InternationalString;

import org.geotoolkit.util.SimpleInternationalString;


/**
 * A trivial implementation of {@link Citation}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public class SimpleCitation implements Citation, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -1490465918703910949L;

    /**
     * The title to be returned by {@link #getTitle()}.
     */
    protected final String title;

    /**
     * Creates a new object for the given name.
     *
     * @param title The title to be returned by {@link #getTitle()}.
     */
    public SimpleCitation(final String title) {
        this.title = title;
    }

    /**
     * Returns the title as an international string.
     */
    @Override
    public InternationalString getTitle() {
        return new SimpleInternationalString(title);
    }

    /**
     * Methods inherited from the {@link Citation} interface which are
     * not of interest to this {@code SimpleCitation} implementation.
     */
    @Override public Collection<InternationalString>  getAlternateTitles()         {return Collections.emptyList();}
    @Override public Collection<CitationDate>         getDates()                   {return Collections.emptyList();}
    @Override public InternationalString              getEdition()                 {return null;}
    @Override public Date                             getEditionDate()             {return null;}
    @Override public Collection<? extends Identifier> getIdentifiers()             {return Collections.emptyList();}
    @Override public Collection<ResponsibleParty>     getCitedResponsibleParties() {return Collections.emptyList();}
    @Override public Collection<PresentationForm>     getPresentationForms()       {return Collections.emptyList();}
    @Override public Series                           getSeries()                  {return null;}
    @Override public InternationalString              getOtherCitationDetails()    {return null;}
    @Override public InternationalString              getCollectiveTitle()         {return null;}
    @Override public String                           getISBN()                    {return null;}
    @Override public String                           getISSN()                    {return null;}

    /**
     * Returns a string representation of this citation.
     */
    @Override
    public String toString() {
        return "Citation[\"" + title + "\"]";
    }
}
