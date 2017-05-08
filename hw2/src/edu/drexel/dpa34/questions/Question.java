package edu.drexel.dpa34.questions;

import edu.drexel.dpa34.AsJSON;
import edu.drexel.dpa34.JSONFormatException;
import edu.drexel.dpa34.UserInputException;
import edu.drexel.dpa34.JSONSpec;
import org.json.simple.JSONObject;

import java.util.Scanner;

/**
 * Base class for all other question classes.
 */
public abstract class Question implements AsJSON {
    protected String prompt = "";
    String answer = "";
    private static String jsonSpec = "{\"type\":\"\"}";

    /**
     * Create a question from a serialized state.
     * @param object The serialized object. Must have a "type" key.
     * @param graded Whether the question will be graded for points.
     * @return A de-serialized Question.
     * @throws JSONFormatException When no "type" key exists, or contains an unknown type.
     */
    public static Question fromJSON(JSONObject object, boolean graded) throws JSONFormatException {
        JSONSpec.testObject(jsonSpec, object);

        switch ((String) object.get("type")) {
            case "Essay":
                return new Essay(object, graded);
            case "Matching":
                return new Matching(object, graded);
            case "MultipleChoice":
                return new MultipleChoice(object, graded);
            case "Ranking":
                return new Ranking(object, graded);
            case "ShortAnswer":
                return new ShortAnswer(object, graded);
            case "TrueFalse":
                return new TrueFalse(object, graded);
            default:
                throw new JSONFormatException("Invalid Question type.");
        }
    }

    void readPrompt() {
        Scanner scanner = new Scanner(System.in);

        while (this.prompt.equals("")) {
            System.out.println("Enter the prompt for the question:");
            this.prompt = scanner.nextLine();
        }
    }

    /**
     * Create a question from user input.
     * @param graded Whether the question will be graded for points.
     * @return A newly-created Question.
     * @throws UserInputException When a user provides invalid input.
     */
    public static Question fromInput(boolean graded) throws UserInputException {
        System.out.println("Add a new question:");
        System.out.println("1) Add a new T/F question");
        System.out.println("2) Add a new multiple choice question");
        System.out.println("3) Add a new short-answer question");
        System.out.println("4) Add a new essay question");
        System.out.println("5) Add a new ranking question");
        System.out.println("6) Add a new matching question");

        Scanner scanner = new Scanner(System.in);

        switch (scanner.nextLine()) {
            case "1":
                return new TrueFalse(graded);
            case "2":
                return new MultipleChoice(graded);
            case "3":
                return new ShortAnswer(graded);
            case "4":
                return new Essay(graded);
            case "5":
                return new Ranking(graded);
            case "6":
                return new Matching(graded);
            default:
                throw new UserInputException("Not a valid selection.");
        }
    }

    /**
     * Collect a test/survey taker's response to a question.
     * @param questionNumber The question number to display.
     * @return The user's response to the question.
     * @throws UserInputException When a user provides invalid input.
     */
    public abstract Response collectAnswer(int questionNumber) throws UserInputException;

    /**
     * Judge whether an answer is correct or not.
     * @param response The answer to judge.
     * @return Whether the answer is correct.
     * @throws JSONFormatException If the JSON of the response isn't formatted as expected by the question.
     */
    public abstract boolean gradeAnswer(Response response) throws JSONFormatException;

    /**
     * Show the prompt and possible answers (when appropriate).
     * @param questionNumber The question number to display.
     * @param showAnswer Whether to also display the correct answer.
     */
    public abstract void display(int questionNumber, boolean showAnswer);
}
