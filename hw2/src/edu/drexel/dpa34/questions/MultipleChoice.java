package edu.drexel.dpa34.questions;

import edu.drexel.dpa34.JSONFormatException;
import edu.drexel.dpa34.UserInputException;
import edu.drexel.dpa34.JSONSpec;
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
    private static String jsonSpecUngraded = "{\"prompt\":\"\",\"options\":[\"\"]}";
    private static String jsonSpecGraded = "{\"prompt\":\"\",\"options\":[\"\"],\"answer\":\"\"}";

    /**
     * Create a new MultipleChoice question from user input
     * @param graded Whether to collect a correct answer.
     * @throws UserInputException If the user provides invalid input.
     */
    MultipleChoice(boolean graded) throws UserInputException {
        Scanner scanner = new Scanner(System.in);
        readPrompt();

        for (int i = 0; i < labels.length; i++) {
            System.out.println("Enter option #" + (i + 1) + " (Press Enter to finish):");
            String input = scanner.nextLine();

            if (!input.equals("")) {
                options.add(input);
            } else {
                if (options.size() == 0)
                    throw new UserInputException("Can't have 0 options");
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
     * @throws JSONFormatException If the JSON is not formatted properly.
     */
    MultipleChoice(JSONObject object, boolean graded) throws JSONFormatException {
        JSONSpec.testObject(graded ? jsonSpecGraded : jsonSpecUngraded, object);

        this.prompt = (String) object.get("prompt");
        JSONArray options = (JSONArray) object.get("options");
        options.forEach(option -> this.options.add((String) option));

        if (this.options.size() > labels.length)
            throw new JSONFormatException("Can't have more than 5 options per question");

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
     * @throws UserInputException If the user provides invalid input.
     */
    public Response collectAnswer(int questionNumber) throws UserInputException {
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
     * @throws JSONFormatException If the provided response is not valid.
     */
    public boolean gradeAnswer(Response response) throws JSONFormatException {
        JSONObject jsonResponse = response.getResponse();

        if (!jsonResponse.containsKey("answer"))
            throw new JSONFormatException("Response must contain an answer.");

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
