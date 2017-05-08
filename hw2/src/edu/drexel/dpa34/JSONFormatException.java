package edu.drexel.dpa34;

/**
 * JSONFormatException is thrown when JSON is passed into a method that does not conform to the expected format.
 */
public class JSONFormatException extends Exception {
    public JSONFormatException(String message) {
        super(message);
    }
}