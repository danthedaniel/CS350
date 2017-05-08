package edu.drexel.dpa34;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Contains methods for comparing a reference JSON specification against a JSONObject.
 */
public class JSONSpec {
    /**
     * Assert that a JSONObject conforms to the structure of a reference serialized JSON object.
     * @param spec The reference JSON string.
     * @param object The object to test.
     * @throws FormatException If the tested object does not conform to the spec.
     * @throws ParseException If the spec object is not valid JSON.
     */
    public static void testObject(String spec, JSONObject object) throws FormatException {
        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonSpec = (JSONObject) parser.parse(spec);
            objectTypeCheck(jsonSpec, object, "");
        } catch (ParseException e) {
            System.err.println("JSON spec string is not valid JSON. No tests will be performed on the provided object.");
        }
    }

    /**
     * Check all shallow-level keys for existence and type equality, then recurse into all values that are JSONObjects.
     * @param reference The reference JSONObject.
     * @param testObj The object to test.
     * @param path The current location in the tested JSONObject (e.g. "key1.key2.key3").
     * @throws FormatException If the tested object does not match the structure of the reference object.
     */
    private static void objectTypeCheck(JSONObject reference, JSONObject testObj, String path) throws FormatException {
        // testObj can contain keys not in the reference object, but all keys in the reference object must exist in testObj
        for (Object key : reference.keySet()) {
            String newPath = addToPath(path, (String) key);

            if (!testObj.containsKey(key)) {
                throw new FormatException("Expected object to contain key " + newPath);
            } else if (!valuesForKeyEqual(key, reference, testObj)) {
                throw new FormatException("Expected value at " + newPath + " to be of type " + reference.get(key).getClass().toString());
            } else if (reference.get(key) instanceof JSONArray) {
                arrayTypeCheck((JSONArray) reference.get(key), (JSONArray) testObj.get(key), newPath);
            } else if (reference.get(key) instanceof JSONObject) {
                objectTypeCheck((JSONObject) reference.get(key), (JSONObject) testObj.get(key), newPath);
            }
        }
    }

    private static boolean valuesForKeyEqual(Object key, JSONObject object1, JSONObject object2) {
        return object1.get(key).getClass().equals(object2.get(key).getClass());
    }

    /**
     * Check for array type equality. The reference array should contain either 0 or 1 elements. If it contains no
     * elements, no type checking will be performed on the testArray. If it contains 1 element, the type of the first
     * element must match that of all elements in the testArray.
     * @param reference JSONArray containing 0 or 1 elements.
     * @param testArray JSONArray to be tested for element type equality with the reference.
     * @param path The path of the tested array.
     */
    private static void arrayTypeCheck(JSONArray reference, JSONArray testArray, String path) throws FormatException {
        // If no elements exist in the reference, no checks can be performed
        if (reference.size() == 0)
            return;

        // The first element will determine the structure of all elements in testArray
        Object referenceElement = reference.get(0);
        Class referenceClass = referenceElement.getClass();

        for (int i = 0; i < testArray.size(); i++) {
            Object testElement = testArray.get(i);
            String newPath = addToPath(path, i);

            if (!testElement.getClass().equals(referenceClass)) {
                throw new FormatException("Expected value at " + newPath + " to be of type " + referenceClass.toString());
            } else if (referenceElement instanceof JSONObject) {
                objectTypeCheck((JSONObject) referenceElement, (JSONObject) testElement, newPath);
            } else if (referenceElement instanceof JSONArray) {
                arrayTypeCheck((JSONArray) referenceElement, (JSONArray) testElement, newPath);
            }
        }
    }

    private static String addToPath(String basePath, String key) {
        return basePath.equals("") ? key : basePath + "." + key;
    }

    private static String addToPath(String basePath, int index) {
        return basePath + "[" + index + "]";
    }
}
