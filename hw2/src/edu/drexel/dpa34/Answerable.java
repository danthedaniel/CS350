package edu.drexel.dpa34;

import edu.drexel.dpa34.questions.Question;
import edu.drexel.dpa34.questions.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

public abstract class Answerable {
    protected ArrayList<Question> questions = new ArrayList<>();
    private ArrayList<Response[]> completed = new ArrayList<>();
    private String name;
    private static String jsonSpec = "{\"definition\":{\"name\":\"\",\"questions\":[{}]},\"completed\":[[{}]]}";

    /**
     * Create an empty Answerable with nothing but a name.
     * @param name The name of the survey/test.
     */
    Answerable(String name) {
        this.name = name;
    }

    /**
     * De-serialize an Answerable from JSON.
     * @param object Contains the Answerable's name, questions, and previous responses.
     * @throws JSONFormatException When the JSON is not formatted properly.
     */
    Answerable(JSONObject object) throws JSONFormatException {
        JSONSpec.testObject(jsonSpec, object);

        JSONObject definition = (JSONObject) object.get("definition");
        this.name = (String) definition.get("name");

        JSONArray questions = (JSONArray) definition.get("questions");
        JSONArray completed = (JSONArray) object.get("completed");
        questions.forEach(question -> addQuestionFromJSON((JSONObject) question));
        completed.forEach(responses -> addCompletedFromJSON((JSONArray) responses));
    }

    /**
     * De-serialize a question for the Answerable.
     * @param question One of the questions in the test/survey.
     */
    private void addQuestionFromJSON(JSONObject question) {
        try {
            this.questions.add(Question.fromJSON(question, gradeable()));
        } catch (JSONFormatException e) {
            System.err.println("Question JSON is malformed:");
            System.err.println(e.getMessage());
        }
    }

    /**
     * De-serialize a response for the Answerable.
     * @param responses A set of responses from the test/survey.
     */
    private void addCompletedFromJSON(JSONArray responses) {
        try {
            Response[] responseArray = new Response[this.questions.size()];

            for (int i = 0; i < this.questions.size(); i++)
                responseArray[i] = new Response((JSONObject) responses.get(i));

            this.completed.add(responseArray);
        } catch (JSONFormatException e) {
            System.err.println("Response JSON is malformed.");
            System.err.println(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Response JSON is not in sync with the surveys's definition.");
        }
    }

    /**
     * If no responses already exist for the Answerable, take user input to add a new Question.
     */
    private void addQuestion() {
        if (modifiable()) {
            try {
                this.questions.add(Question.fromInput(gradeable()));
            } catch (UserInputException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.err.println("Can not modify this by add a question.");
        }
    }

    void addQuestions() {
        Scanner scanner = new Scanner(System.in);

        while (this.questions.size() == 0)
            addQuestion();

        System.out.println("Would you like to add another question? (Y/n)");
        while (scanner.nextLine().toLowerCase().toCharArray()[0] == 'y') {
            addQuestion();
            System.out.println("Would you like to add another question? (Y/n)");
        }
    }

    /**
     * Display all questions and (if applicable) answers.
     */
    void display() {
        for (int i = 0; i < this.questions.size(); i++)
            this.questions.get(i).display(i, gradeable());
    }

    /**
     * Whether the Answerable can safely have questions added to it without breaking all previous responses.
     * @return Whether the test/survey is safe to modify.
     */
    private boolean modifiable() {
        // Only allow un-answered tests/surveys to be modified
        return this.completed.size() == 0;
    }

    /**
     * Whether the Answerable will grade responses, instead of just recording them.
     * @return Whether the Answerable will grade responses.
     */
    protected abstract boolean gradeable();

    /**
     * Collect a set of responses to all questions in the test/survey.
     * @return A set of responses to all questions.
     */
    public abstract Response[] collectResponse();

    String getName() {
        return this.name;
    }

    /**
     * Serialize the Answerable as JSON.
     * @return The JSON representing the test/survey.
     */
    public JSONObject asJSON() {
        JSONObject object = new JSONObject();

        // Create the definition object
        JSONObject definition = new JSONObject();
        definition.put("name", this.name);

        JSONArray jsonQuestions = new JSONArray();
        for (Question question : this.questions)
            jsonQuestions.add(question.asJSON());

        definition.put("questions", jsonQuestions);

        // Create the "completed" array
        JSONArray jsonCompleted = new JSONArray();
        for (Response[] responses : this.completed) {
            JSONArray jsonResponses = new JSONArray();

            for (Response response : responses)
                jsonResponses.add(response);

            jsonCompleted.add(jsonResponses);
        }

        // Add both fields to the final object
        object.put("definition", definition);
        object.put("completed", jsonCompleted);

        return object;
    }
}
