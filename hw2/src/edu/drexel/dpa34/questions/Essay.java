package edu.drexel.dpa34.questions;

import edu.drexel.dpa34.JSONFormatException;
import edu.drexel.dpa34.UserInputException;
import edu.drexel.dpa34.JSONSpec;
import org.json.simple.JSONObject;

import java.util.Scanner;

public class Essay extends Question {
    private static String jsonSpec = "{\"prompt\":\"\"}";

    Essay(boolean graded) {
        readPrompt();
    }

    Essay(JSONObject object, boolean graded) throws JSONFormatException {
        JSONSpec.testObject(jsonSpec, object);

        this.prompt = (String) object.get("prompt");
    }

    public void display(int questionNumber, boolean showAnswer) {
        System.out.println(questionNumber + ") " + this.prompt);
        System.out.println();
    }

    public Response collectAnswer(int questionNumber) throws UserInputException {
        display(questionNumber, false);

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();

        JSONObject response = new JSONObject();
        response.put("answer", answer);

        return new Response(questionNumber, response);
    }

    public boolean gradeAnswer(Response response) throws JSONFormatException {
        return false;
    }

    public JSONObject asJSON() {
        JSONObject object = new JSONObject();

        object.put("prompt", this.prompt);
        object.put("type", "Essay");

        return object;
    }
}
