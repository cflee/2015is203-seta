package net.cflee.seta.entity;

import java.util.ArrayList;

/**
 * Records one validation error in one record of a particular file.
 */
public class FileValidationError implements Comparable<FileValidationError> {

    private String filename;
    private int lineNumber;
    private ArrayList<String> messageList;

    /**
     * Construct a fileValidationError object with filename, lineNumber and an arrayList of error messages
     *
     * @param filename filename
     * @param lineNumber line number in file that the errors occurred
     * @param messages ArrayList of message strings
     */
    public FileValidationError(String filename, int lineNumber, ArrayList<String> messages) {
        this.filename = filename;
        this.lineNumber = lineNumber;
        this.messageList = messages;
    }

    /**
     * Retrieve the filename of the file
     *
     * @return filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Set the filename of the file
     *
     * @param filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Retrieve the lineNumber where the error occurs
     *
     * @return lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Set the lineNumber where the error occurs
     *
     * @param lineNumber
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Retrieve an arrayList of errorMessages
     *
     * @return messageList
     */
    public ArrayList<String> getMessages() {
        return messageList;
    }

    /**
     * Set an arrayList of errorMessages
     *
     * @param messageList
     */
    public void setMessages(ArrayList<String> messages) {
        this.messageList = messages;
    }

    /**
     * Natural order: lineNumber ascending
     *
     * @param that FileValidationError to be compared against
     * @return negative if this object should be before that, positive if this object should be after that, and 0 when
     * they are tied
     */
    @Override
    public int compareTo(FileValidationError that) {
        return this.lineNumber - that.lineNumber;
    }
}
