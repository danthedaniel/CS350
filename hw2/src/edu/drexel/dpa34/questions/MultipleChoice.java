package edu.drexel.dpa34.questions;

import edu.drexel.dpa34.FormatException;
import edu.drexel.dpa34.InputException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * A multiple-choice question consists of multiple options with associated labels.
 */
public class MultipleChoice extends Question {
    private static String[] labels = {"A", "B", "C", "D", "E"};
    private ArrayList<String> options = new ArrayList<>();

    /**
     * Create a new MultipleChoice question from user input
     * @param graded Whether to collect a correct answer.
     * @throws InputException If the user provides invalid input.
     */
    MultipleChoice(boolean graded) throws InputException {
        Scanner scanner = new Scanner(System.in);
        readPrompt();

        for (int i = 0; i < labels.length; i++) {
            System.out.println("Enter option #" + (i + 1) + " (Press Enter to finish):");
            String input = scanner.nextLine();

            if (!input.equals("")) {
                options.add(input);
            } else {
                if (options.size() == 0)
                    throw new InputException("Can't have 0 options");
                else
                    break;
            }
        }

        if (graded) {
            System.out.println("Enter the correct answer:");
            while (!answerValid())
                this.answer = scanner.nextLine();

            this.answer = normalizeAnswer(this.answer);
        }
    }

    /**
     * De-serialize a MultipleChoice question from JSON.
     * @param object The JSON object to reify from.
     * @param graded Whether to expect a correct answer in the JSON.
     * @throws FormatException If the JSON is not formatted properly.
     */
    MultipleChoice(JSONObject object, boolean graded) throws FormatException {
        if (!object.containsKey("prompt"))
            throw new FormatException("Multiple choice question definition must contain a prompt.");
        if (!object.containsKey("options"))
            throw new FormatException("Multiple choice question definition must contain options.");
        if (!object.containsKey("answer") && graded)
            throw new FormatException("This multiple choice requires an answer.");

        this.prompt = (String) object.get("prompt");
        JSONArray options = (JSONArray) object.get("options");
        options.forEach(option -> this.options.add((String) option));

        if (this.options.size() > labels.length)
            throw new FormatException("Can't have more than 5 options per question");

        if (graded)
            this.answer = (String) object.get("answer");
    }

    /**
     * If the answer provided is one of the full option values, set it to the corresponding label.
     * @param answer The answer to normalize.
     * @return The normalized answer.
     */
    private String normalizeAnswer(String answer) {
        if (this.options.contains(answer))
            return labels[this.options.indexOf(answer)];

        return answer;
    }

    /**
     * Check if the provided answer is valid. Used only in the user-input constructor.
     * @return Whether the answer is valid.
     */
    private boolean answerValid() {
        if (this.options.contains(this.answer))
            return true;

        for (String label : labels)
            if (label.equals(this.answer))
                return true;

        return false;
    }

    /**
     * Display the question and all options. If showAnswer is set, will also print the answer.
     * @param questionNumber The question number to display.
     * @param showAnswer Whether to also display the correct answer.
     */
    public void display(int questionNumber, boolean showAnswer) {
        System.out.println(questionNumber +") " + this.prompt);

        for (int i = 0; i < this.options.size(); i ++)
            System.out.print(labels[i] + ") " + this.options.get(i) + " ");

        System.out.println();

        if (showAnswer)
            System.out.println("Correct answer: " + this.answer);

        System.out.println();
    }

    /**
     * Show the prompt and get user input for an answer.
     * @param questionNumber The question number to display.
     * @return The user's answer as a Response object.
     * @throws InputException If the user provides invalid input.
     */
    public Response collectAnswer(int questionNumber) throws InputException {
        display(questionNumber, false);

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();

        JSONObject response = new JSONObject();
        response.put("answer", answer);

        return new Response(questionNumber, response);
    }

    /**
     * Judge whether the answer provided is correct.
     * @param response The answer to judge.
     * @return Whether the answer provided is correct
     * @throws FormatException If the provided response is not valid.
     */
    public boolean gradeAnswer(Response response) throws FormatException {
        JSONObject jsonResponse = response.getResponse();

        if (!jsonResponse.containsKey("answer"))
            throw new FormatException("Response must contain an answer.");

        String userAnswer = (String) jsonResponse.get("answer");

        return normalizeAnswer(userAnswer).equals(this.answer);
    }

    /**
     * Serialize the MultipleChoice object.
     * @return The serialized question.
     */
    public JSONObject asJSON() {
        JSONObject object = new JSONObject();
        JSONArray jsonOptions = new JSONArray();

        if (!this.answer.equals(""))
            object.put("answer", this.answer);

        jsonOptions.addAll(this.options);
        object.put("prompt", this.prompt);
        object.put("type", "MultipleChoice");
        object.put("options", jsonOptions);

        return object;
    }
}
