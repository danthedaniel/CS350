package edu.drexel.dpa34.questions;

import edu.drexel.dpa34.AsJSON;
import edu.drexel.dpa34.FormatException;
import org.json.simple.JSONObject;

/**
 * Represents a test/survey taker's response to one question.
 */
public class Response implements AsJSON {
    private JSONObject response;
    private int questionNumber;

    /**
     * Create a response from newly-entered information.
     * @param questionNumber The question number the response belongs to.
     * @param response The answer to a question.
     */
    public Response(int questionNumber, JSONObject response) {
        this.questionNumber = questionNumber;
        this.response = response;
    }

    /**
     * De-serialize a Response from a JSON object.
     * @param object The serialized Response object.
     */
    public Response(JSONObject object) throws FormatException {
        if (!object.containsKey("questionNumber"))
            throw new FormatException("Response must have a question number");

        if (!object.containsKey("response"))
            throw new FormatException("Response must have contents.");

        this.questionNumber = (int) object.get("questionNumber");
        this.response = (JSONObject) object.get("response");
    }

    /**
     * @return The original answer from the Question object.
     */
    public JSONObject getResponse() {
        return this.response;
    }

    /**
     * Serialize the Response as JSON.
     * @return The serialized Response.
     */
    public JSONObject asJSON() {
        JSONObject object = new JSONObject();
        object.put("questionNumber", this.questionNumber);
        object.put("response", this.response);
        return object;
    }
}
