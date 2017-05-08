package edu.drexel.dpa34.questions;

import edu.drexel.dpa34.serialization.JSONFormatException;
import edu.drexel.dpa34.UserInputException;
import edu.drexel.dpa34.serialization.JSONSpec;
import org.json.simple.JSONObject;

import java.util.Scanner;

public class TrueFalse extends Question {
    private static String jsonSpecUngraded = "{\"prompt\":\"\"}";
    private static String jsonSpecGraded = "{\"prompt\":\"\",\"answer\":\"\"}";

    TrueFalse(boolean graded) {
        Scanner scanner = new Scanner(System.in);
        readPrompt();

        if (graded) {
            System.out.println("Enter the correct answer (True/False):");
            while (!answerValid())
                this.answer = scanner.nextLine().toLowerCase();
        }
    }

    TrueFalse(JSONObject object, boolean graded) throws JSONFormatException {
        JSONSpec.testObject(graded ? jsonSpecGraded : jsonSpecUngraded, object);

        this.prompt = (String) object.get("prompt");
        if (graded)
            this.answer = (String) object.get("answer");
    }

    /**
     * Check if the provided answer is valid. Used only in the user-input constructor.
     * @return Whether the answer is valid.
     */
    private boolean answerValid() {
        return (this.answer.equals("true") || this.answer.equals("false"));
    }

    public void display(int questionNumber, boolean showAnswer) {
        System.out.println(questionNumber +") " + this.prompt + " (True/False)");
        System.out.println();
    }

    public Response collectAnswer(int questionNumber) throws UserInputException {
        display(questionNumber, false);

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().toLowerCase();

        JSONObject response = new JSONObject();
        response.put("answer", answer);

        return new Response(questionNumber, response);
    }

    public boolean gradeAnswer(Response response) throws JSONFormatException {
        if (!response.getResponse().containsKey("answer"))
            throw new JSONFormatException("True/false question definition must contain an answer");

        String answer = (String) response.getResponse().get("answer");
        return answer.equals(this.answer);
    }

    public JSONObject asJSON() {
        JSONObject object = new JSONObject();

        if (!this.answer.equals(""))
            object.put("answer", this.answer);

        object.put("prompt", this.prompt);
        object.put("type", "TrueFalse");

        return object;
    }
}
