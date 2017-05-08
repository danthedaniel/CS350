package edu.drexel.dpa34.serialization;

import org.json.simple.JSONObject;

/**
 * Guarantees an object is serializable as JSON.
 */
public interface AsJSON {
    /**
     * Serialize the implementing class as a JSONObject.
     * @return The serialized object.
     */
    JSONObject asJSON();
}
