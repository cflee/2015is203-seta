package net.cflee.seta.entity;

import java.util.ArrayList;


public class DeleteFileValidationResult {

    private int numOfDeletedRows;
    private int numOfUnmatchedRows;
    private ArrayList<FileValidationError> errorList;

    /**
     * Construct a fileValidationResult object with the total number of rows without bootstrapping errors and an
     * arrayList of error messages
     *
     * @param numOfValidRows
     * @param errors
     */
    public DeleteFileValidationResult(int numOfDeletedRows, int numOfUnmatchedRows,
            ArrayList<FileValidationError> errors) {
        this.numOfDeletedRows = numOfDeletedRows;
        this.numOfUnmatchedRows = numOfUnmatchedRows;
        this.errorList = errors;
    }

    /**
     * Retrieve the total number of valid rows found and deleted
     *
     * @return numOfDeletedRows
     */
    public int getNumOfDeletedRows() {
        return numOfDeletedRows;
    }

    /**
     * Retrieve the total number of valid rows not found
     *
     * @return numOfDeletedRows
     */
    public int getNumOfUnmatchedRows() {
        return numOfUnmatchedRows;
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
