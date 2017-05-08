package edu.drexel.dpa34.questions;

import edu.drexel.dpa34.JSONFormatException;
import edu.drexel.dpa34.UserInputException;
import edu.drexel.dpa34.JSONSpec;
import org.json.simple.JSONObject;

import java.util.Scanner;

public class ShortAnswer extends Question {
    private static String jsonSpecUngraded = "{\"prompt\":\"\"}";
    private static String jsonSpecGraded = "{\"prompt\":\"\",\"answer\":\"\"}";

    ShortAnswer(boolean graded) {
        Scanner scanner = new Scanner(System.in);
        readPrompt();

        if (graded) {
            System.out.println("Enter the correct answer:");
            while (this.answer.equals(""))
                this.answer = scanner.nextLine();
        }
    }

    ShortAnswer(JSONObject object, boolean graded) throws JSONFormatException {
        JSONSpec.testObject(graded ? jsonSpecGraded : jsonSpecUngraded, object);

        this.prompt = (String) object.get("prompt");

        if (graded)
            this.answer = (String) object.get("answer");
    }

    public void display(int questionNumber, boolean showAnswer) {
        System.out.println(questionNumber +") " + this.prompt);
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
        if (!response.getResponse().containsKey("answer"))
            throw new JSONFormatException("Short answer question definition must contain an answer.");

        String answer = (String) response.getResponse().get("answer");
        return answer.equals(this.answer);
    }

    public JSONObject asJSON() {
        JSONObject object = new JSONObject();

        if (!this.answer.equals(""))
            object.put("answer", this.answer);

        object.put("prompt", this.prompt);
        object.put("type", "ShortAnswer");

        return object;
    }
}
