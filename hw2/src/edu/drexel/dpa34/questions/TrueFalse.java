package edu.drexel.dpa34.questions;

import edu.drexel.dpa34.FormatException;
import edu.drexel.dpa34.InputException;
import org.json.simple.JSONObject;

import java.util.Scanner;

public class TrueFalse extends Question {
    TrueFalse(boolean graded) {
        Scanner scanner = new Scanner(System.in);
        readPrompt();

        if (graded) {
            System.out.println("Enter the correct answer (True/False):");
            while (!answerValid())
                this.answer = scanner.nextLine().toLowerCase();
        }
    }

    TrueFalse(JSONObject object, boolean graded) throws FormatException {
        if (!object.containsKey("prompt"))
            throw new FormatException("True/false question definition must contain a prompt.");
        if (!object.containsKey("answer") && graded)
            throw new FormatException("True/false question definition must contain an answer.");

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

    public Response collectAnswer(int questionNumber) throws InputException {
        display(questionNumber, false);

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().toLowerCase();

        JSONObject response = new JSONObject();
        response.put("answer", answer);

        return new Response(questionNumber, response);
    }

    public boolean gradeAnswer(Response response) throws FormatException {
        if (!response.getResponse().containsKey("answer"))
            throw new FormatException("True/false question definition must contain an answer");

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
