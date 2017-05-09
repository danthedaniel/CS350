package edu.drexel.dpa34;

import edu.drexel.dpa34.questions.Response;
import edu.drexel.dpa34.serialization.AsJSON;
import edu.drexel.dpa34.serialization.JSONFormatException;
import org.json.simple.JSONObject;

public class Test extends Answerable implements AsJSON {
    Test(JSONObject object) throws JSONFormatException {
        super(object);
    }

    Test(String name) {
        super(name);
    }

    public double gradeResponses(Response[] responses) throws JSONFormatException {
        if (responses.length != this.questions.size())
            throw new JSONFormatException("Response length is not the same as the number of questions.");

        double score = 0.0;
        double total = (double) this.questions.size();

        for (int i = 0; i < responses.length; i++)
            score += this.questions.get(i).gradeAnswer(responses[i]) ? 1.0 : 0.0;

        return score / total;
    }

    protected boolean gradeable() {
        return true;
    }

    public Response[] collectResponse() {
        Response[] responses = new Response[this.questions.size()];

        for (int i = 0; i < responses.length; i++) {
            boolean unsuccessful = true;
            while (unsuccessful) {
                try {
                    responses[i] = this.questions.get(i).collectAnswer(i);
                    unsuccessful = false;
                } catch (UserInputException e) {
                    System.err.println(e.getMessage());
                }
            }
        }

        return responses;
    }
}
