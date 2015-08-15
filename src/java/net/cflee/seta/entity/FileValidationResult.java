package net.cflee.seta.entity;

import java.util.ArrayList;

/**
 * An entity class of group consisting of the total number of rows without bootstrapping errors and an arrayList of
 * error messages
 */
public class FileValidationResult {

    private int numOfValidRows;
    private ArrayList<FileValidationError> errorList;

    /**
     * Construct a fileValidationResult object with the total number of rows without bootstrapping errors and an
     * arrayList of error messages
     *
     * @param numOfValidRows
     * @param errors
     */
    public FileValidationResult(int numOfValidRows, ArrayList<FileValidationError> errors) {
        this.numOfValidRows = numOfValidRows;
        this.errorList = errors;
    }

    /**
     * Retrieve the total number of rows without bootstrapping errors
     *
     * @return numOfValidRows
     */
    public int getNumOfValidRows() {
        return numOfValidRows;
    }

    /**
     * Retrieve an arrayList of error messages
     *
     * @return errorList
     */
    public ArrayList<FileValidationError> getErrors() {
        return errorList;
    }
}
