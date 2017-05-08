package edu.drexel.dpa34;

/**
 * FormatException is thrown when JSON is passed into a method that does not conform to the expected format.
 */
public class FormatException extends Exception {
    public FormatException(String message) {
        super(message);
    }
}