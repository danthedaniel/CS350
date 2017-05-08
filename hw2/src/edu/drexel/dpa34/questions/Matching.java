package edu.drexel.dpa34.questions;

import edu.drexel.dpa34.JSONFormatException;
import edu.drexel.dpa34.UserInputException;
import edu.drexel.dpa34.JSONSpec;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Scanner;

public class Matching extends Question {
    private static String jsonSpec = "{\"prompt\":\"\",\"matchingPairs\":{}}";

    private HashMap<String, String> matchingPairs = new HashMap<>();

    Matching(boolean graded) {
        Scanner scanner = new Scanner(System.in);
        readPrompt();

        String keyInput = "", valueInput = "";

        while ((!keyInput.equals("") && !valueInput.equals("")) || this.matchingPairs.size() == 0) {
            System.out.println("Enter a key:");
            keyInput = scanner.nextLine();

            if (!keyInput.equals("")) {
                System.out.println("Enter a value:");
                valueInput = scanner.nextLine();

                if (!keyInput.equals(""))
                    this.matchingPairs.put(keyInput, valueInput);
            }
        }
    }

    Matching(JSONObject object, boolean graded) throws JSONFormatException {
        JSONSpec.testObject(jsonSpec, object);

        this.prompt = (String) object.get("prompt");
        JSONObject jsonPairs = (JSONObject) object.get("matchingPairs");

        for (Object key : jsonPairs.keySet())
            this.matchingPairs.put((String) key, (String) jsonPairs.get(key));
    }

    public void display(int questionNumber, boolean showAnswer) {
        System.out.println(questionNumber + ") " + this.prompt);

        System.out.print("Keys: ");
        for (String key : this.matchingPairs.keySet())
            System.out.print(key + ", ");

        System.out.println();

        System.out.print("Values: ");
        for (String value : this.matchingPairs.values())
            System.out.print(value + ", ");

        System.out.println();
        System.out.println();
    }

    public Response collectAnswer(int questionNumber) throws UserInputException {
        return new Response(questionNumber, new JSONObject());
    }

    public boolean gradeAnswer(Response response) throws JSONFormatException {
        return false;
    }

    public JSONObject asJSON() {
        JSONObject object = new JSONObject();
        JSONObject jsonPairs = new JSONObject();

        for (String key : this.matchingPairs.keySet())
            jsonPairs.put(key, this.matchingPairs.get(key));

        object.put("prompt", this.prompt);
        object.put("type", "Matching");
        object.put("matchingPairs", jsonPairs);

        return object;
    }
}
