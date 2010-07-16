/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.kml.model;

import java.net.URI;
import java.util.List;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.data.kml.xsd.Cdata;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractFeature extends DefaultAbstractObject implements AbstractFeature {

    protected String name;
    protected boolean visibility;
    protected boolean open;
    protected AtomPersonConstruct author;
    protected AtomLink atomLink;
    protected String address;
    protected AddressDetails addressDetails;
    protected String phoneNumber;
    protected Object snippet;
    protected Object description;
    protected AbstractView view;
    protected AbstractTimePrimitive timePrimitive;
    protected URI styleUrl;
    protected List<AbstractStyleSelector> styleSelector;
    protected Region region;
    protected Object extendedData;

    /**
     * 
     */
    protected DefaultAbstractFeature() {
        super();
        this.visibility = DEF_VISIBILITY;
        this.open = DEF_OPEN;
        this.styleSelector = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param atomLink
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     */
    protected DefaultAbstractFeature(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions) {

        super(objectSimpleExtensions, idAttributes);
        this.name = name;
        this.visibility = visibility;
        this.open = open;
        this.author = author;
        this.atomLink = atomLink;
        this.address = address;
        this.addressDetails = addressDetails;
        this.phoneNumber = phoneNumber;
        if(snippet instanceof String 
                || snippet instanceof Cdata
                || snippet instanceof Snippet)
            this.snippet = snippet;
        else if (snippet != null)
            throw new IllegalArgumentException("snippet muts be a String, Cdata or Snippet (deprecated) instance.");
        this.description = description;
        this.view = view;
        this.timePrimitive = timePrimitive;
        this.styleUrl = styleUrl;
        this.styleSelector = (styleSelector == null) ? EMPTY_LIST : styleSelector;
        this.region = region;
        if(extendedData instanceof ExtendedData
                || extendedData instanceof Metadata)
            this.extendedData = extendedData;
        else if (extendedData != null)
            throw new IllegalArgumentException("snippet muts be an ExtendedData or MetaData (deprecated) instance.");
        if (abstractFeatureSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.FEATURE).addAll(abstractFeatureSimpleExtensions);
        }
        if (abstractFeatureObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.FEATURE).addAll(abstractFeatureObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getVisibility() {
        return this.visibility;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getOpen() {
        return this.open;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomPersonConstruct getAuthor() {
        return this.author;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomLink getAtomLink() {
        return this.atomLink;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getAddress() {
        return this.address;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AddressDetails getAddressDetails() {
        return this.addressDetails;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Object getSnippet() {
        return this.snippet;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Object getDescription() {
        return this.description;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractView getView() {
        return this.view;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractTimePrimitive getTimePrimitive() {
        return this.timePrimitive;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public URI getStyleUrl() {
        return this.styleUrl;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractStyleSelector> getStyleSelectors() {
        return this.styleSelector;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Region getRegion() {
        return this.region;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Object getExtendedData() {
        return this.extendedData;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAuthor(AtomPersonConstruct author) {
        this.author = author;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAtomLink(AtomLink atomLink) {
        this.atomLink = atomLink;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddressDetails(AddressDetails addressDetails) {
        this.addressDetails = addressDetails;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSnippet(String snippet) {
            this.snippet = snippet;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSnippet(Cdata snippet) {
            this.snippet = snippet;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    @Deprecated
    public void setSnippet(Snippet snippet) {
            this.snippet = snippet;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDescription(Object description) {
        this.description = description;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setView(AbstractView view) {
        this.view = view;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTimePrimitive(AbstractTimePrimitive timePrimitive) {
        this.timePrimitive = timePrimitive;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setStyleUrl(URI styleUrl) {
        this.styleUrl = styleUrl;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setStyleSelectors(List<AbstractStyleSelector> styleSelectors) {
        this.styleSelector = styleSelectors;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRegion(Region region) {
        this.region = region;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setExtendedData(ExtendedData extendedData) {
        this.extendedData = extendedData;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    @Deprecated
    public void setExtendedData(Metadata metaData) {
        this.extendedData = metaData;
    }

    @Override
    public String toString() {
        String resultat = "AbstractFeatureDefault : "
                + "\n\tname : " + this.name
                + "\n\tvisibility : " + this.visibility
                + "\n\topen : " + this.open
                + "\n\tauthor : " + this.author
                + "\n\tlink : " + this.atomLink
                + "\n\taddress : " + this.address
                + "\n\taddressDetails : " + this.addressDetails
                + "\n\tphoneNumber : " + this.phoneNumber
                + "\n\tsnippet : " + this.snippet
                + "\n\tdescription : " + this.description
                + "\n\tview : " + this.view
                + "\n\ttimePrimitive : " + this.timePrimitive
                + "\n\tstyleUrl : " + this.styleUrl
                + "\n\tstyleSelectors : " + this.styleSelector.size();
        for (AbstractStyleSelector s : this.styleSelector) {
            resultat += "\n\tstyleSelector : " + s;
        }
        resultat += "\n\tregion : " + this.region
                + "\n\textendedData : " + this.extendedData;
        return resultat;
    }
}
