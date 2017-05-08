package edu.drexel.dpa34;

import edu.drexel.dpa34.questions.Response;
import org.json.simple.JSONObject;

public class Test extends Answerable implements AsJSON {
    Test(JSONObject object) throws JSONFormatException {
        super(object);
    }

    Test(String name) {
        super(name);
    }

    public double gradeResponses(Response[] responses) {
        return 0.0; // Ayy, lmao
    }

    protected boolean gradeable() {
        return true;
    }

    public Response[] collectResponse() {
        return new Response[0];
    }
}
