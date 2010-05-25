package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.xal.AddressDetails;

/**
 *
 * @author Samuel Andrés
 */
public class FolderDefault extends AbstractContainerDefault implements Folder {

    private List<AbstractFeature> features;
    private List<SimpleType> folderSimpleExtensions;
    private List<AbstractObject> folderObjectExtensions;

    public FolderDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions,
            List<AbstractFeature> features,
            List<SimpleType> folderSimpleExtensions,
            List<AbstractObject> folderObjectExtensions){

        super(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber,
                snippet, description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions);

        this.features = features;
        this.folderSimpleExtensions = folderSimpleExtensions;
        this.folderObjectExtensions = folderObjectExtensions;
    }

    @Override
    public List<AbstractFeature> getAbstractFeatures() {return this.features;}

    @Override
    public List<AbstractObject> getFolderObjectExtensions() {return this.folderObjectExtensions;}

    @Override
    public List<SimpleType> getFolderSimpleExtensions() { return this.folderSimpleExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tFolderDefault : ";
        return resultat;
    }

}
