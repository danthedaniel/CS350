package edu.drexel.dpa34.questions;

import edu.drexel.dpa34.JSONFormatException;
import edu.drexel.dpa34.UserInputException;
import edu.drexel.dpa34.JSONSpec;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

public class Ranking extends Question {
    private ArrayList<String> ranked = new ArrayList<>();
    private ArrayList<String> options = new ArrayList<>();
    private static String jsonSpecUngraded = "{\"prompt\":\"\",\"options\":[\"\"]}";
    private static String jsonSpecGraded = "{\"prompt\":\"\",\"options\":[\"\"],\"answer\":[\"\"]}";

    Ranking(boolean graded) throws UserInputException {
        Scanner scanner = new Scanner(System.in);
        readPrompt();

        String optionInput = "", rankInput;

        System.out.println("Enter options for ranking (minimum of 2):");
        while (!optionInput.equals("") || this.options.size() < 2) {
            System.out.println("Option #" + this.options.size());
            optionInput = scanner.nextLine();

            if (!optionInput.equals(""))
                this.options.add(optionInput);
        }

        if (graded) {
            System.out.println("Enter correct ranking:");
            while (ranked.size() < options.size()) {
                System.out.println("Rank #" + this.ranked.size());
                rankInput = scanner.nextLine();

                if (this.options.contains(rankInput))
                    this.ranked.add(rankInput);
            }
        }
    }

    Ranking(JSONObject object, boolean graded) throws JSONFormatException {
        JSONSpec.testObject(graded ? jsonSpecGraded : jsonSpecUngraded, object);

        this.prompt = (String) object.get("prompt");

        JSONArray options = (JSONArray) object.get("options");
        options.forEach(option -> this.options.add((String) option));

        if (graded) {
            JSONArray ranked = (JSONArray) object.get("answer");
            ranked.forEach(rank -> this.ranked.add((String) rank));

            if (this.options.size() != this.ranked.size())
                throw new JSONFormatException("Ranking answer and options must be of the same size.");
        }
    }

    public void display(int questionNumber, boolean showAnswer) {
        System.out.println(questionNumber +") " + this.prompt);
        System.out.print("Options: ");

        for (String option : this.options)
            System.out.print(option + ", ");

        System.out.println();

        if (showAnswer) {
            System.out.print("Correct answer: ");
            for (String rank : this.ranked)
                System.out.print(rank + ", ");

            System.out.println();
        }

        System.out.println();
    }

    public Response collectAnswer(int questionNumber) throws UserInputException {
        ArrayList<String> userRanks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String rankInput;

        display(questionNumber, false);

        while (userRanks.size() < options.size()) {
            System.out.println("Rank #" + userRanks.size());
            rankInput = scanner.nextLine();

            if (this.options.contains(rankInput))
                userRanks.add(rankInput);
        }

        JSONObject response = new JSONObject();
        JSONArray answer = new JSONArray();
        answer.addAll(userRanks);
        response.put("answer", answer);

        return new Response(questionNumber, response);
    }

    public boolean gradeAnswer(Response response) throws JSONFormatException {
        return false;
    }

    public JSONObject asJSON() {
        JSONObject object = new JSONObject();
        JSONArray jsonOptions = new JSONArray();

        if (this.ranked.size() > 0) {
            JSONArray answer = new JSONArray();
            answer.addAll(this.ranked);
            object.put("answer", answer);
        }

        jsonOptions.addAll(this.options);
        object.put("prompt", this.prompt);
        object.put("type", "Ranking");
        object.put("options", jsonOptions);

        return object;
    }
}
