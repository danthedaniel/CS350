package edu.drexel.dpa34.questions;

import edu.drexel.dpa34.FormatException;
import edu.drexel.dpa34.InputException;
import org.json.simple.JSONObject;

import java.util.Scanner;

public class Essay extends Question {
    Essay(boolean graded) {
        readPrompt();
    }

    Essay(JSONObject object, boolean graded) throws FormatException {
        if (!object.containsKey("prompt"))
            throw new FormatException("Essay question definition must contain a prompt.");

        this.prompt = (String) object.get("prompt");
    }

    public void display(int questionNumber, boolean showAnswer) {
        System.out.println(questionNumber + ") " + this.prompt);
        System.out.println();
    }

    public Response collectAnswer(int questionNumber) throws InputException {
        display(questionNumber, false);

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();

        JSONObject response = new JSONObject();
        response.put("answer", answer);

        return new Response(questionNumber, response);
    }

    public boolean gradeAnswer(Response response) throws FormatException {
        return false;
    }

    public JSONObject asJSON() {
        JSONObject object = new JSONObject();

        object.put("prompt", this.prompt);
        object.put("type", "Essay");

        return object;
    }
}
