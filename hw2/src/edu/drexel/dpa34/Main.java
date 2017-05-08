package edu.drexel.dpa34;

import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    /**
     * Driver for the Test/Survey program. Will prompt for a relative path to a repository file.
     */
    public static void main(String[] args) {
        Repository repo;
        Scanner scanner = new Scanner(System.in);
        String repoPath = "";

        while (repoPath.equals("")) {
            System.out.println("Enter a path to a repo:");
            repoPath = scanner.nextLine();
        }

        try {
            repo = new Repository(repoPath);
            System.out.println("Repository opened from " + repoPath);
        } catch (FileNotFoundException e) {
            repo = new Repository();
            System.out.println("Repository not found, creating a blank one");
        } catch (ParseException e) {
            System.err.println("Malformed Repository at: " + repoPath);
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.err.println("Could not open file at: " + repoPath);
            return;
        }

        repo.menu();
    }
}
