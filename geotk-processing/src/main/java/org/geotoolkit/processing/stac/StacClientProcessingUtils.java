package org.geotoolkit.processing.stac;

import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.stac.client.DownloadURIExtractor;
import org.geotoolkit.stac.client.StacClient;
import org.opengis.geometry.Envelope;

import java.lang.reflect.InvocationTargetException;
import java.net.http.HttpClient;

class StacClientProcessingUtils {

    /**
     * Utility method to convert a spatial extent represented as an Envelope into a bounding box array of doubles in the format [minX, minY, maxX, maxY].
     * @param spatialExtent an Envelope representing the spatial extent, which may be null. If null, the method will return null.
     * @return an array of doubles representing the bounding box in the format [minX, minY, maxX, maxY] if the input spatialExtent is not null, or null if the input is null.
     */
    static double[] getBbox(Envelope spatialExtent) {
        if (spatialExtent != null) {
            GeneralEnvelope env = new GeneralEnvelope(spatialExtent);
            return  new double[]{
                    env.getMinimum(0), env.getMinimum(1),
                    env.getMaximum(0), env.getMaximum(1)
            };
        }
        return null;
    }

    /**
     * Utility method to convert a temporal extent represented as an array of strings into a single string in the format "start/end".
     * @param temporalExtentArray an array of strings representing the temporal extent,
     *                            where the first element is the start time and the second element is the end time.
     *                            If the array has only one element, it will be returned as is.
     * @return a string representing the temporal extent in the format "start/end" if the input array has
     * two or more elements, or the single element if the array has only one element. Returns null if the input array is null or empty.
     */
    static String getTemporalExtent(String[] temporalExtentArray) {
        if (temporalExtentArray != null && temporalExtentArray.length > 0) {
            if (temporalExtentArray.length == 1) {
                return temporalExtentArray[0];
            } else if (temporalExtentArray.length >= 2) {
                return temporalExtentArray[0] + "/"
                        + temporalExtentArray[1];
            }
        }
        return null;
    }

    /**
     * Utility method to create a StacClient instance, optionally using a custom DownloadURIExtractor if the class name is provided.
     * @param extractorClassName the fully qualified class name of the custom DownloadURIExtractor to use, or null/empty to use the default extractor
     * @return a StacClient instance configured with the specified DownloadURIExtractor or the default one if no class name is provided
     * @throws ClassNotFoundException if the specified class name does not correspond to a valid class
     * @throws NoSuchMethodException if the specified class does not have a no-argument constructor
     * @throws InvocationTargetException if the constructor of the specified class throws an exception during instantiation
     * @throws InstantiationException if the specified class cannot be instantiated (e.g., if it's abstract or an interface)
     * @throws IllegalAccessException if the constructor of the specified class is not accessible (e.g., if it's private)
     */
    static StacClient getStacClient(String extractorClassName)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        if (extractorClassName != null && !extractorClassName.trim().isEmpty()) {
            final Class<?> extractorClass = Class.forName(extractorClassName);
            final DownloadURIExtractor extractor = (DownloadURIExtractor)
                    extractorClass.getDeclaredConstructor().newInstance();
            return new StacClient(HttpClient.newHttpClient(), extractor);
        } else {
            return new StacClient();
        }
    }
}
