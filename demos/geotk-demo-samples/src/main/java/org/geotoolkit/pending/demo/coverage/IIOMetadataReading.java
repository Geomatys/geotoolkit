package org.geotoolkit.pending.demo.coverage;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.pending.demo.Demos;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * An example of how we can read raw metadata from a coverage file.
 * We generally need it for files like netcdf or gribs objects whose metadata 
 * are not completely bound to ISO 19115 files.
 * 
 * Used file in this example is a grib file download from  : 
 * http://www.globalmarinenet.com/grib_downloads.php#Download
 * 
 * @author Alexis Manin (Geomatys)
 */
public class IIOMetadataReading {
    
    public static final Logger LOGGER = Logger.getLogger(IIOMetadataReading.class.getName());
    
    public static void main(String[] args) throws Exception {
    
        Demos.init();
        
        /* Here, we'll proceed example using a little grib file we want to 
         * discover each layer capability. First of all, we'll need a java 
         * ImageReader to access raw metadata, not a coverage reader.
         * If you already get a CoverageReader, maybe you could retrieve its 
         * underlying ImageReader using coverageReader.getInput().
         */
        URL source = IIOMetadataReading.class.getResource("/data/grib/Atlantic.wave.grb");
        
        /* Get an ImageReader. It's important to set the second boolean value to 
         * false, because it's the parameter that ensure choosen reader will be 
         * compliant with source metadata format.
         */        
        ImageReader reader = XImageIO.getReader(source, Boolean.FALSE, Boolean.FALSE);
        
        /* For image file, metadata are split into multiples parts. First, we
         * get the global metadata of the image file, and then we've got metadata
         * for each image data into the given file.
         * To get raw metadata, we need to ask it as a tree in native format.
         */
        final IIOMetadata metadata = reader.getStreamMetadata();
        final Node root = metadata.getAsTree(metadata.getNativeMetadataFormatName());
        
        // All metadata are stored as a dom tree we'll now browse. 
        recursiveBrowse(root, 0);
        
        /* We've printed general metadata, now we'll braowse all images contained
         * in our file to get their metadata. 
         */
        final int imageNumber = reader.getNumImages(true);
        for (int imageCount =0 ; imageCount < imageNumber ; imageCount++) {
            LOGGER.log(Level.INFO, "IMAGE AT INDEX "+imageCount);
            IIOMetadata imgMetadata = reader.getImageMetadata(imageCount);
            final Node imgNode = imgMetadata.getAsTree(imgMetadata.getNativeMetadataFormatName());
            recursiveBrowse(imgNode, 2);
        }
    }
    
    /**
     * Browse a dom tree to print its content.
     * @param node The node to use as root.
     * @param indent the initial indentation factor.
     */
    private static void recursiveBrowse(final Node node, int indent) {
        // Print current node.
        final StringBuilder builder = new StringBuilder();
        for (int i =0 ; i < indent ; i++) {
            builder.append(' ');
        }
        builder.append("|_")
               .append(node.getNodeName())
               .append(" : ")
               .append(node.getNodeValue());
        LOGGER.log(Level.INFO, builder.toString());
        indent+=2;
        
        // Print node attributes.
        final int attrIndent = indent;
        NamedNodeMap attributes = node.getAttributes();
        for (int attrCount = 0 ; attrCount < attributes.getLength(); attrCount++) {
            recursiveBrowse(attributes.item(attrCount), attrIndent);
        }
            
        // Browse node children
        indent +=2;
        final NodeList children = node.getChildNodes();
        for (int childCount = 0 ; childCount < children.getLength() ; childCount++ ) {
            recursiveBrowse(children.item(childCount), indent);
        }
    }
}
