package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.xal.model.AddressDetails;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultDocument extends DefaultAbstractContainer implements Document {

    private List<Schema> schemas;
    private List<AbstractFeature> features;
    private List<SimpleType> documentSimpleExtensions;
    private List<AbstractObject> documentObjectExtensions;

    /**
     * 
     */
    public DefaultDocument() {
        this.schemas = EMPTY_LIST;
        this.features = EMPTY_LIST;
        this.documentSimpleExtensions = EMPTY_LIST;
        this.documentObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param link
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
     * @param abstractContainerSimpleExtensions
     * @param abstractContainerObjectExtensions
     * @param schemas
     * @param features
     * @param documentSimpleExtensions
     * @param documentObjectExtensions
     */
    public DefaultDocument(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions,
            List<Schema> schemas, List<AbstractFeature> features,
            List<SimpleType> documentSimpleExtensions,
            List<AbstractObject> documentObjectExtensions) {

        super(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber,
                snippet, description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions);

        this.schemas = (schemas == null) ? EMPTY_LIST : schemas;
        this.features = (features == null) ? EMPTY_LIST : features;
        this.documentSimpleExtensions = (documentSimpleExtensions == null) ? EMPTY_LIST : documentSimpleExtensions;
        this.documentObjectExtensions = (documentObjectExtensions == null) ? EMPTY_LIST : documentObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Schema> getSchemas() {
        return this.schemas;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractFeature> getAbstractFeatures() {
        return this.features;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getDocumentObjectExtensions() {
        return this.documentObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getDocumentSimpleExtensions() {
        return this.documentSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSchemas(List<Schema> schemas) {
        this.schemas = schemas;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractFeatures(List<AbstractFeature> features) {
        this.features = features;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDocumentSimpleExtensions(List<SimpleType> documentSimpleExtensions) {
        this.documentSimpleExtensions = documentSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDocumentObjectExtensions(List<AbstractObject> documentObjectExtensions) {
        this.documentObjectExtensions = documentObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tDocumentDefault : ";
        return resultat;
    }
}
