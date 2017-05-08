package edu.drexel.dpa34;

import edu.drexel.dpa34.serialization.AsJSON;
import edu.drexel.dpa34.serialization.JSONFormatException;
import edu.drexel.dpa34.serialization.JSONSpec;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A Repository is a collection of surveys and tests. It contains both the test and survey definitions, as well as their
 * collections of completed responses.
 */
public class Repository implements AsJSON {
    private ArrayList<Survey> surveys = new ArrayList<>();
    private ArrayList<Test> tests = new ArrayList<>();
    private static String jsonSpec = "{\"tests\":[{}],\"surveys\":[{}]}";

    /**
     * Create a Repository from the contents of a JSON file.
     * @param path The file path to a file containing a JSON-serialized Repository.
     * @throws IOException If the file can not be opened.
     * @throws ParseException If the file is not valid JSON.
     * @throws JSONFormatException If the file does not conform to the spec.
     */
    Repository(String path) throws JSONFormatException, ParseException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject repo = (JSONObject) parser.parse(new FileReader(path));

        JSONSpec.testObject(jsonSpec, repo);

        JSONArray surveyRepos = (JSONArray) repo.get("surveys");
        JSONArray testRepos = (JSONArray) repo.get("tests");

        surveyRepos.forEach(surveyRepo -> addSurveyFromJSON((JSONObject) surveyRepo));
        testRepos.forEach(testRepo -> addTestFromJSON((JSONObject) testRepo));
    }

    /**
     * Create an empty Repository.
     */
    Repository() {
        this.surveys = new ArrayList<>();
        this.tests = new ArrayList<>();
    }

    /**
     * Display the top-level Repository menu and take user input.
     */
    void menu() {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        while (running) {
            System.out.println("1) Survey Menu");
            System.out.println("2) Test Menu");
            System.out.println("3) Save Repo");
            System.out.println("4) Quit");

            switch (scanner.nextLine()) {
                case "1":
                    surveyMenu();
                    break;
                case "2":
                    testMenu();
                    break;
                case "3":
                    System.out.println("Enter the destination file path to save to:");
                    String path = scanner.nextLine();

                    try {
                        saveToFile(path);
                    } catch (IOException e) {
                        System.err.println("Could not save to file: " + path);
                    }
                    break;
                case "4":
                    System.out.println("Exiting...");
                    running = false;
                    break;
                default:
                    System.err.println("Input not valid");
            }
        }
    }

    /**
     * Display the Test-related menu and take user input.
     */
    private void testMenu() {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        while (running) {
            System.out.println("1) Create a new Test");
            System.out.println("2) Display a Test");
            System.out.println("3) Return to previous menu");

            switch (scanner.nextLine()) {
                case "1":
                    String newName = "";
                    while (newName.equals("")) {
                        System.out.println("Enter a name for the test:");
                        newName = scanner.nextLine();
                    }

                    if (findByName(this.tests, newName) == null) {
                        Test newTest = createTest(newName);
                        newTest.addQuestions();
                    } else {
                        System.err.println("Test by that name already exists.");
                    }
                    break;
                case "2":
                    System.out.println("Enter the name of the test:");
                    String query = scanner.nextLine();
                    Test test = (Test) findByName(this.tests, query);

                    if (test == null)
                        System.out.println("Could not find test by the name: " + query);
                    else
                        test.display();
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.err.println("Input not valid");
            }
        }
    }

    /**
     * Display the Survey-related menu and take user input.
     */
    private void surveyMenu() {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        while (running) {
            System.out.println("1) Create a new Survey");
            System.out.println("2) Display a Survey");
            System.out.println("3) Return to previous menu");

            switch (scanner.nextLine()) {
                case "1":
                    String newName = "";
                    while (newName.equals("")) {
                        System.out.println("Enter a name for the survey:");
                        newName = scanner.nextLine();
                    }

                    if (findByName(this.surveys, newName) == null) {
                        Survey newSurvey = createSurvey(newName);
                        newSurvey.addQuestions();
                    } else {
                        System.err.println("Survey by that name already exists.");
                    }
                    break;
                case "2":
                    System.out.println("Enter the name of the survey:");
                    String query = scanner.nextLine();
                    Survey survey = (Survey) findByName(this.surveys, query);

                    if (survey == null)
                        System.out.println("Could not find survey by the name: " + query);
                    else
                        survey.display();
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.err.println("Input not valid");
            }
        }
    }

    /**
     * Find a test/survey in the repository by its name. Returns null if none is found.
     * @param list A list of Answerables.
     * @param name The name of the target test/survey.
     * @return A test/survey with the given name or null.
     */
    private Answerable findByName(ArrayList<? extends Answerable> list, String name) {
        for (Answerable ans : list)
            if (ans.getName().equals(name))
                return ans;

        return null;
    }

    /**
     * Add a Test object to the Repository from a JSONObject.
     * @param testRepo The JSONObject containing a Test's responses and definition.
     */
    private void addTestFromJSON(JSONObject testRepo) {
        try {
            this.tests.add(new Test(testRepo));
        } catch (JSONFormatException e) {
            System.err.println("Invalid Test format.");
        }
    }

    /**
     * Add a Survey object to the Repository from a JSONObject.
     * @param surveyRepo The JSONObject containing a Survey's responses and definition.
     */
    private void addSurveyFromJSON(JSONObject surveyRepo) {
        try {
            this.surveys.add(new Survey(surveyRepo));
        } catch (JSONFormatException e) {
            System.err.println("Invalid Survey format.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Add a new empty Test to the Repository.
     * @param name The name of the Test.
     * @return The newly created empty Test object.
     */
    private Test createTest(String name) {
        Test test = new Test(name);
        this.tests.add(test);
        return test;
    }

    /**
     * Add a new empty Survey to the Repository.
     * @param name The name of the Survey.
     * @return The newly created empty Survey object.
     */
    private Survey createSurvey(String name) {
        Survey survey = new Survey(name);
        this.surveys.add(survey);
        return survey;
    }

    /**
     * Save the current repo to the destination specified.
     * @throws IOException If the file could not be written to.
     */
    private void saveToFile(String path) throws IOException {
        JSONObject object = asJSON();

        try{
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            object.writeJSONString(writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("Could not write to file: " + path);
        }
    }

    /**
     * Serialize the Repository as a JSONObject.
     * @return A complete, serialized Repository.
     */
    public JSONObject asJSON() {
        JSONObject repo = new JSONObject();
        JSONArray surveyRepos = new JSONArray();
        JSONArray testRepos = new JSONArray();

        this.surveys.forEach(survey -> surveyRepos.add(survey.asJSON()));
        this.tests.forEach(test -> testRepos.add(test.asJSON()));

        repo.put("surveys", surveyRepos);
        repo.put("tests", testRepos);

        return repo;
    }
}
