package net.cflee.seta.controller;

import com.csvreader.CsvReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.cflee.seta.dao.AppDAO;
import net.cflee.seta.dao.AppUpdateDAO;
import net.cflee.seta.dao.LocationDAO;
import net.cflee.seta.dao.LocationUpdateDAO;
import net.cflee.seta.dao.UserDAO;
import net.cflee.seta.entity.App;
import net.cflee.seta.entity.AppUpdate;
import net.cflee.seta.entity.FileValidationError;
import net.cflee.seta.entity.FileValidationResult;
import net.cflee.seta.entity.Location;
import net.cflee.seta.entity.LocationUpdate;
import net.cflee.seta.entity.User;

/**
 * A controller class that provides services to the Servlet layer, to validate
 * and process the various CSV data files that can be supplied for Bootstrap use
 * case.
 */
public class BootstrapController {

    /**
     * Processes an InputStream of demographics.csv data, validates it, and then
     * passes valid records to UserDAO for insertion.
     *
     * @param inputStream InputStream of demographics.csv data
     * @param filename filename to use in FileValidationErrors
     * @param conn to create a connection to JDBC
     * @return FileValidationResult
     * @throws java.io.IOException if InputStream cannot be read
     * @throws java.sql.SQLException
     */
    public static FileValidationResult processDemographicsFile(
            InputStream inputStream, String filename, Connection conn) throws IOException, SQLException {
        CsvReader demographics = new CsvReader(
                new InputStreamReader(inputStream), ',');
        ArrayList<FileValidationError> errorList = new ArrayList<FileValidationError>();
        ArrayList<User> userList = new ArrayList<User>();

        // Trim white space
        demographics.setTrimWhitespace(true);

        // Read the first record of data as column headers.
        demographics.readHeaders();

        // counter for row number
        // used for FileValidationError's lineNumber attribute
        // starts with 2 because 1 is the header
        int rowNumber = 2;

        // counter for valid records
        int numOfValidRecords = 0;

        // iterate through all records in the file
        while (demographics.readRecord()) {
            ArrayList<String> errorMessageList = new ArrayList<String>();

            String macAddress = demographics.get("mac-address").toLowerCase();
            String name = demographics.get("name");
            String password = demographics.get("password");
            String email = demographics.get("email");
            String genderString = demographics.get("gender").toUpperCase();
            String school = null;
            int year = 0;
            char gender = ' ';

            // test for blank fields
            if (macAddress.isEmpty()) {
                errorMessageList.add("mac-address is blank");
            }

            if (name.isEmpty()) {
                errorMessageList.add("name is blank");
            }

            if (password.isEmpty()) {
                errorMessageList.add("password is blank");
            }

            if (email.isEmpty()) {
                errorMessageList.add("email is blank");
            }

            if (genderString.isEmpty()) {
                errorMessageList.add("gender is blank");
            }

            // check if there are any blank fields
            if (errorMessageList.isEmpty()) {
                // none of the fields are blanks, validate other fields

                // validation for mac address
                // check if mac address is 40 char long
                if (!isValidMacAddress(macAddress)) {
                    errorMessageList.add("invalid mac address");
                }

                // validation for password: length and whitespace
                // assuming white spaces has been trimmed and the white space means space
                if (password.length() < 8 || password.indexOf(" ") != -1) {
                    errorMessageList.add("invalid password");
                }

                // validation for email
                if (email.indexOf(" ") == -1) {

                    // no white space is found
                    String[] emailHalves = email.toLowerCase().split("@");

                    // check if there is 1 @ sign inside (2 halves)
                    if (emailHalves.length != 2) {
                        // either more or less than 1 @ sign
                        errorMessageList.add("invalid email");
                    } else {
                        // one @ sign

                        // check the first half of email address (email ID)
                        String[] firstEmailParts = emailHalves[0].split("\\.");

                        // check if there is at least one dot in email ID
                        if (firstEmailParts.length >= 2) {
                            // at least one dot in email ID

                            // check if last part of the email ID is a number
                            // and number is 2010 to 2014 (both inclusive)
                            try {
                                year = Integer.parseInt(
                                        firstEmailParts[firstEmailParts.length - 1]);

                                // it is a number as no exception has been thrown
                                // check if number is between 2010 and 2014
                                if (year < 2011 || year > 2015) {
                                    // too low or too high
                                    errorMessageList.add("invalid email");
                                } else {
                                    // year is within the range

                                    // check if rest of email ID is only consisting of
                                    // letters and numbers
                                    // (dots have been removed when we split the
                                    // email ID by dot earlier
                                    boolean emailIdIsValid = true;

                                    for (int i = 0; i < firstEmailParts.length - 1 && emailIdIsValid; i++) {
                                        String emailId = firstEmailParts[i];
                                        for (int j = 0; j < emailId.length() && emailIdIsValid; j++) {
                                            char characterCheck = emailId.
                                                    charAt(j);
                                            if ((characterCheck < 'a' || characterCheck > 'z') && (characterCheck < '0' || characterCheck > '9')) {
                                                emailIdIsValid = false;
                                            }
                                        }
                                    }

                                    if (!emailIdIsValid) {
                                        // email ID has other chars
                                        errorMessageList.add("invalid email");
                                    } else {
                                        // email ID is all letters and numbers
                                        String secondEmailPart = emailHalves[1];

                                        // check if email domain ends with
                                        // .smu.edu.sg
                                        if (!secondEmailPart.endsWith(
                                                ".smu.edu.sg")) {
                                            // does not end with smu.edu.sg
                                            errorMessageList.
                                                    add("invalid email");
                                        } else {
                                            // check if the email domain apart from ".smu.edu.sg"
                                            // is one of the six schools
                                            school = secondEmailPart.
                                                    substring(0,
                                                            secondEmailPart.
                                                            length() - 11).
                                                    toLowerCase();
                                            if (!school.equals("sis") && !school.
                                                    equals("business") && !school.
                                                    equals("economics") && !school.
                                                    equals("accountancy") && !school.
                                                    equals("law") && !school.
                                                    equals("socsc")) {
                                                errorMessageList.add(
                                                        "invalid email");
                                            }
                                        }
                                    }
                                }
                            } catch (NumberFormatException e) {
                                // last part of email ID is not a number
                                errorMessageList.add("invalid email");
                            }
                        } else {
                            // no dots inside email ID
                            errorMessageList.add("invalid email");
                        }
                    }
                } else {
                    // white space is found in the email
                    errorMessageList.add("invalid email");
                }

                // validation for gender
                // we know it is at least 1 char long because it is not blank
                gender = genderString.charAt(0);
                if (gender != 'M' && gender != 'F') {
                    errorMessageList.add("invalid gender");
                }
            }

            // there is at least one field that is blank
            if (!errorMessageList.isEmpty()) {
                errorList.add(new FileValidationError(filename, rowNumber,
                        errorMessageList));
            } else {
                userList.add(new User(macAddress, name, password, email, gender,
                        school, year));
                numOfValidRecords++;
            }
            rowNumber++;
        }

        //call the UserDAO to update user database by using users
        UserDAO.insertUsers(userList, conn);

        // finished reading all records
        demographics.close();

        return new FileValidationResult(numOfValidRecords, errorList);
    }

    /**
     * Processes an InputStream of app-lookup.csv data, validates it, and then
     * passes valid records to AppDAO for insertion.
     *
     * @param inputStream InputStream of app-lookup.csv data
     * @param filename filename to use in FileValidationErrors
     * @param conn connection object to connect to jdbc
     * @return FileValidationResult
     * @throws java.io.IOException if InputStream cannot be read
     * @throws java.sql.SQLException
     */
    public static FileValidationResult processAppLookupFile(
            InputStream inputStream, String filename, Connection conn) throws IOException, SQLException {
        CsvReader appLookup = new CsvReader(new InputStreamReader(inputStream),
                ',');
        ArrayList<FileValidationError> errorList = new ArrayList<FileValidationError>();
        ArrayList<App> appList = new ArrayList<App>();
        List<String> validCategories = Arrays.asList("Books", "Social",
                "Education", "Entertainment", "Information", "Library",
                "Local", "Tools", "Fitness", "Games", "Others");

        // Trim white space
        appLookup.setTrimWhitespace(true);

        // Read the first record of data as column headers.
        appLookup.readHeaders();

        // counter for row number
        // used for FileValidationError's lineNumber attribute
        // starts with 2 because 1 is the header
        int rowNumber = 2;

        // counter for valid records
        int numOfValidRecords = 0;

        // iterate through all records in the file
        while (appLookup.readRecord()) {
            ArrayList<String> errorMessageList = new ArrayList<String>();

            String appIdString = appLookup.get("app-id");
            String appName = appLookup.get("app-name");
            String appCategory = appLookup.get("app-category");
            String normalisedAppCategory = appCategory;
            int appId = 0;

            // test for blank field
            if (appIdString.isEmpty()) {
                errorMessageList.add("app-id is blank");
            }

            if (appName.isEmpty()) {
                errorMessageList.add("app-name is blank");
            }

            if (appCategory.isEmpty()) {
                errorMessageList.add("app-category is blank");
            }

            // check if there are any blank fields
            if (errorMessageList.isEmpty()) {
                // none of the fields are blanks, validate other fields

                // validation for app-id
                // check if the app-id is a valid positive integer value
                try {
                    appId = Integer.parseInt(appIdString);

                    // check if the location-id is not a positive integer value
                    if (appId < 1) {
                        errorMessageList.add("invalid app id");
                    }

                } catch (NumberFormatException e) {
                    // location-id is not an integer
                    errorMessageList.add("invalid app id");
                }

                // validation for app-category
                // normalise to initial caps
                if (appCategory.length() > 2) {
                    normalisedAppCategory = appCategory.substring(0, 1).
                            toUpperCase()
                            + appCategory.substring(1).toLowerCase();
                    if (!validCategories.contains(normalisedAppCategory)) {
                        errorMessageList.add("invalid app category");
                    }
                } else {
                    errorMessageList.add("invalid app category");
                }
            }

            if (!errorMessageList.isEmpty()) {
                errorList.add(new FileValidationError(filename, rowNumber,
                        errorMessageList));
            } else {
                appList.add(new App(appId, appName, normalisedAppCategory));
                numOfValidRecords++;
            }
            rowNumber++;
        }
        // calls the appDAO to save the valid records
        AppDAO.insertApps(appList, conn);

        // finished reading all records
        appLookup.close();

        return new FileValidationResult(numOfValidRecords, errorList);
    }

    /**
     * Processes an InputStream of app.csv data, validates it, and then passes
     * valid records to AppUpdateDAO for insertion.
     *
     * @param inputStream InputStream of app.csv data
     * @param filename filename to use in FileValidationErrors
     * @param conn connection object to talk to jdbc
     * @return FileValidationResult
     * @throws java.io.IOException if InputStream cannot be read
     * @throws java.sql.SQLException
     */
    public static FileValidationResult processAppFile(
            InputStream inputStream, String filename, Connection conn) throws IOException, SQLException {
        CsvReader app = new CsvReader(new InputStreamReader(inputStream),
                ',');
        ArrayList<FileValidationError> errorList = new ArrayList<FileValidationError>();

        // Trim white space
        app.setTrimWhitespace(true);

        // Read the first record of data as column headers.
        app.readHeaders();

        // counter for row number
        // used for FileValidationError's lineNumber attribute
        // starts with 2 because 1 is the header
        int rowNumber = 2;

        // counter for valid records
        int numOfValidRecords = 0;

        // set SQL connection autocommit = false
        conn.setAutoCommit(false);

        // retrieve the list of all the (valid) app IDs for validation
        ArrayList<Integer> allAppIds = AppDAO.getAllAppIds(conn);

        // retrieve the list of all the (valid) mac addresses for validation
        ArrayList<String> allMacAddresses = UserDAO.getAllMacAddresses(conn);

        // iterate through all records in the file
        while (app.readRecord()) {
            ArrayList<String> errorMessageList = new ArrayList<String>();

            String timestampString = app.get("timestamp");
            String macAddress = app.get("mac-address").toLowerCase();
            String appIdString = app.get("app-id");
            int appId = 0;
            Date timestamp = null;

            // test for blank field
            if (timestampString.isEmpty()) {
                errorMessageList.add("timestamp is blank");
            }

            if (macAddress.isEmpty()) {
                errorMessageList.add("mac-address is blank");
            }

            if (appIdString.isEmpty()) {
                errorMessageList.add("app-id is blank");
            }

            // check if there are any blank fields
            if (errorMessageList.isEmpty()) {
                // none of the fields are blanks, validate other fields

                // validation for app-id
                try {
                    appId = Integer.parseInt(appIdString);

                    if (!allAppIds.contains(appId)) {
                        errorMessageList.add("invalid app");
                    }
                } catch (NumberFormatException e) {
                    // if the location-id is not an integer
                    errorMessageList.add("invalid app");
                }

                // validation for mac-address
                if (!isValidMacAddress(macAddress)) {
                    errorMessageList.add("invalid mac address");
                }

                // check if there's a matching mac address
                if (!allMacAddresses.contains(macAddress)) {
                    errorMessageList.add("no matching mac address");
                }

                // validation for timestamp
                // create the date format to check against
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");

                try {
                    // check if the timestampString fits with the pattern
                    timestamp = simpleDateFormat.parse(timestampString);

                    // create a string representation of timestamp
                    String formattedTimestamp = simpleDateFormat.format(
                            timestamp);

                    // validation for the formattedTimestamp
                    if (!formattedTimestamp.equals(timestampString)) {
                        //if formattedTimestamp is different from timeStampString
                        errorMessageList.add("invalid timestamp");
                    }

                } catch (ParseException e) {
                    // if the timestamp could not be parsed by the date format
                    errorMessageList.add("invalid timestamp");
                }

                //validation for duplicate row
                if (errorMessageList.isEmpty()) {

                    AppUpdate appUpdate = new AppUpdate(
                            macAddress, timestamp, appId, rowNumber);

                    int existingRowNo = AppUpdateDAO.
                            checkForExistingRecord(appUpdate, conn);
                    if (existingRowNo != -1) {
                        // there is a duplicate!
                        if (existingRowNo == 0) {
                            // duplicate is not from this round of bootstrap
                            // we can let the error be emitted for this row
                            // number
                            errorMessageList.add("duplicate row");
                        } else {
                            // it is a positive integer!
                            // it is from earlier in this file, need to emit
                            // the error at the earlier row number!
                            //
                            // if there is a FileValidationError with that
                            // row number, add to its message list, otherwise
                            // create a new FVE and add it to errorList
                            boolean hasExistingError = false;
                            for (FileValidationError existingError : errorList) {
                                if (existingError.getLineNumber() == existingRowNo) {
                                    hasExistingError = true;
                                    existingError.getMessages().add(
                                            "duplicate row");
                                }
                            }
                            if (!hasExistingError) {
                                ArrayList<String> tempMessageList = new ArrayList<String>();
                                tempMessageList.add("duplicate row");
                                errorList.add(new FileValidationError(filename,
                                        existingRowNo, tempMessageList));
                            }
                            // then update the database with this new location
                            // update's location ID
                            AppUpdateDAO.updateLocationId(appUpdate,
                                    conn);
                        }
                    } else {
                        // no existing LocationUpdate that matches
                        // go ahead and insert
                        AppUpdateDAO.insert(appUpdate, conn);
                        numOfValidRecords++;
                    }
                }
            }
            if (!errorMessageList.isEmpty()) {

                errorList.add(new FileValidationError(filename, rowNumber,
                        errorMessageList));
            }
            rowNumber++;
        }

        // reset all the row numbers in the database
        AppUpdateDAO.clearRowNumberRecords(conn);

        // done, commit and set SQL connection autocommit = true
        conn.commit();
        conn.setAutoCommit(true);

        // finished reading all records
        app.close();

        // sort the errorList to ensure that the FileValidationErrors are
        // in ascending order of line number. they may be out of order due to
        // tacking on the errors for rows that previously did not have any
        // errors, but were found to be duplicate rows later
        Collections.sort(errorList);

        return new FileValidationResult(numOfValidRecords, errorList);
    }

    /**
     * Processes an InputStream of location-lookup.csv data, validates it, and
     * then passes valid records to LocationDAO for insertion.
     *
     * @param inputStream InputStream of location-lookup.csv data
     * @param filename filename to use in FileValidationErrors
     * @param conn connection object to connect to jdbc
     * @return FileValidationResult
     * @throws java.io.IOException if InputStream cannot be read
     * @throws java.sql.SQLException
     */
    public static FileValidationResult processLocationLookUpFile(
            InputStream inputStream, String filename, Connection conn) throws IOException, SQLException {
        CsvReader locationLookUp = new CsvReader(new InputStreamReader(
                inputStream), ',');
        ArrayList<FileValidationError> errorList = new ArrayList<FileValidationError>();
        ArrayList<Location> locationList = new ArrayList<Location>();

        // Trim white space
        locationLookUp.setTrimWhitespace(true);

        // Read the first record of data as column headers.
        locationLookUp.readHeaders();

        // counter for row number
        // used for FileValidationError's lineNumber attribute
        // starts with 2 because 1 is the header
        int rowNumber = 2;

        // counter for valid records
        int numOfValidRecords = 0;

        // iterate through all records in the file
        while (locationLookUp.readRecord()) {
            ArrayList<String> errorMessageList = new ArrayList<String>();

            String locationIdString = locationLookUp.get("location-id");
            String semanticPlace = locationLookUp.get("semantic-place");
            int locationId = 0;

            // test for blank field
            if (locationIdString.isEmpty()) {
                errorMessageList.add("location-id is blank");
            }

            if (semanticPlace.isEmpty()) {
                errorMessageList.add("semantic-place is blank");
            }

            // check if there are any blank fields
            if (errorMessageList.isEmpty()) {
                // none of the fields are blanks, validate other fields

                // validation for location-id
                // check if the location is a valid positive integer value
                try {
                    locationId = Integer.parseInt(locationIdString);

                    // check if the location-id is not a positive integer value
                    if (locationId < 1) {
                        errorMessageList.add("invalid location id");
                    }

                } catch (NumberFormatException e) {
                    // location-id is not an integer
                    errorMessageList.add("invalid location id");
                }

                // validation for semantic-place
                if (semanticPlace.length() < 9) {
                    // minimum length is 9 char because of SMUSISL, number, location
                    errorMessageList.add("invalid semantic place");
                } else if (!semanticPlace.startsWith("SMUSISL")
                        && !semanticPlace.startsWith("SMUSISB")) {
                    // doesn't start with either SMUSISL or SMUSISB
                    errorMessageList.add("invalid semantic place");
                } else {
                    // length is correct, prefix is correct, check if the 8th
                    // char in the semantic place name is the level number
                    try {
                        Integer.parseInt("" + semanticPlace.charAt(7));
                    } catch (NumberFormatException e) {
                        errorMessageList.add("invalid semantic place");
                    }
                }
            }

            if (!errorMessageList.isEmpty()) {
                errorList.add(new FileValidationError(filename, rowNumber,
                        errorMessageList));
            } else {
                locationList.add(new Location(locationId, semanticPlace));
                numOfValidRecords++;
            }
            rowNumber++;
        }
        // calls the locationDAO to save the valid records
        LocationDAO.insertLocations(locationList, conn);

        // finished reading all records
        locationLookUp.close();

        return new FileValidationResult(numOfValidRecords, errorList);
    }

    /**
     * Processes an InputStream of location.csv data, validates it, and then
     * passes valid records to LocationUpdateDAO for insertion.
     *
     * @param inputStream InputStream of location.csv data
     * @param filename filename to use in FileValidationErrors
     * @param conn connection object to talk to jdbc
     * @return FileValidationResult
     * @throws java.io.IOException if InputStream cannot be read
     * @throws java.sql.SQLException
     */
    public static FileValidationResult processLocationFile(
            InputStream inputStream, String filename, Connection conn) throws IOException, SQLException {
        CsvReader location = new CsvReader(new InputStreamReader(inputStream),
                ',');
        ArrayList<FileValidationError> errorList = new ArrayList<FileValidationError>();

        // Trim white space
        location.setTrimWhitespace(true);

        // Read the first record of data as column headers.
        location.readHeaders();

        // counter for row number
        // used for FileValidationError's lineNumber attribute
        // starts with 2 because 1 is the header
        int rowNumber = 2;

        // counter for valid records
        int numOfValidRecords = 0;

        // set SQL connection autocommit = false
        conn.setAutoCommit(false);

        // retrieve the list of all the (valid) location IDs for validation
        ArrayList<Integer> allLocationIds = LocationDAO.getAllLocationIds(conn);

        // iterate through all records in the file
        while (location.readRecord()) {

            ArrayList<String> errorMessageList = new ArrayList<String>();

            String timestampString = location.get("timestamp");
            String macAddress = location.get("mac-address").toLowerCase();
            String locationIdString = location.get("location-id");
            int locationId = 0;
            Date timestamp = null;

            // test for blank field
            if (timestampString.isEmpty()) {
                errorMessageList.add("timestamp is blank");
            }

            if (macAddress.isEmpty()) {
                errorMessageList.add("mac-address is blank");
            }

            if (locationIdString.isEmpty()) {
                errorMessageList.add("location-id is blank");
            }

            // check if there are any blank fields
            if (errorMessageList.isEmpty()) {
                // none of the fields are blanks, validate other fields

                //validation for location-id
                try {
                    locationId = Integer.parseInt(locationIdString);

                    if (!allLocationIds.contains(locationId)) {
                        errorMessageList.add("invalid location");
                    }
                } catch (NumberFormatException e) {
                    // if the location-id is not an integer
                    errorMessageList.add("invalid location");
                }

                //validation for mac-address
                if (!isValidMacAddress(macAddress)) {
                    errorMessageList.add("invalid mac address");
                }

                // validation for timestamp
                // create the date format to check against
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");

                try {
                    // check if the timestampString fits with the pattern
                    timestamp = simpleDateFormat.parse(timestampString);

                    // create a string representation of timestamp
                    String formattedTimestamp = simpleDateFormat.format(
                            timestamp);

                    // validation for the formattedTimestamp
                    if (!formattedTimestamp.equals(timestampString)) {
                        //if formattedTimestamp is different from timeStampString
                        errorMessageList.add("invalid timestamp");
                    }

                } catch (ParseException e) {
                    // if the timestamp could not be parsed by the date format
                    errorMessageList.add("invalid timestamp");
                }

                //validation for duplicate row
                if (errorMessageList.isEmpty()) {

                    LocationUpdate locationUpdate = new LocationUpdate(
                            macAddress, timestamp, locationId, rowNumber);

                    int existingRowNo = LocationUpdateDAO.
                            checkForExistingRecord(locationUpdate, conn);
                    if (existingRowNo != -1) {
                        // there is a duplicate!
                        if (existingRowNo == 0) {
                            // duplicate is not from this round of bootstrap
                            // we can let the error be emitted for this row
                            // number
                            errorMessageList.add("duplicate row");
                        } else {
                            // it is a positive integer!
                            // it is from earlier in this file, need to emit
                            // the error at the earlier row number!
                            //
                            // if there is a FileValidationError with that
                            // row number, add to its message list, otherwise
                            // create a new FVE and add it to errorList
                            boolean hasExistingError = false;
                            for (FileValidationError existingError : errorList) {
                                if (existingError.getLineNumber() == existingRowNo) {
                                    hasExistingError = true;
                                    existingError.getMessages().add(
                                            "duplicate row");
                                }
                            }
                            if (!hasExistingError) {
                                ArrayList<String> tempMessageList = new ArrayList<String>();
                                tempMessageList.add("duplicate row");
                                errorList.add(new FileValidationError(filename,
                                        existingRowNo, tempMessageList));
                            }
                            // then update the database with this new location
                            // update's location ID
                            LocationUpdateDAO.updateLocationId(locationUpdate,
                                    conn);
                        }
                    } else {
                        // no existing LocationUpdate that matches
                        // go ahead and insert
                        LocationUpdateDAO.insert(locationUpdate, conn);
                        numOfValidRecords++;
                    }
                }
            }
            if (!errorMessageList.isEmpty()) {

                errorList.add(new FileValidationError(filename, rowNumber,
                        errorMessageList));
            }
            rowNumber++;
        }

        // reset all the row numbers in the database
        LocationUpdateDAO.clearRowNumberRecords(conn);

        // done, commit and set SQL connection autocommit = true
        conn.commit();
        conn.setAutoCommit(true);

        // finished reading all records
        location.close();

        // sort the errorList to ensure that the FileValidationErrors are
        // in ascending order of line number. they may be out of order due to
        // tacking on the errors for rows that previously did not have any
        // errors, but were found to be duplicate rows later
        Collections.sort(errorList);

        return new FileValidationResult(numOfValidRecords, errorList);
    }

    /**
     * Check if the mac address is valid
     *
     * @param macAddress specified mac address
     * @return true if mac address is valid, else return false
     */
    private static boolean isValidMacAddress(String macAddress) {
        macAddress = macAddress.toLowerCase();
        // check if mac address is 40 char long
        if (macAddress.length() != 40) {
            // not exactly 40 char
            return false;
        } else {
            // exactly 40 char
            // check if all chars are from 0-9 or a-f
            for (int i = 0; i < macAddress.length(); i++) {
                char characterCheck = macAddress.charAt(i);
                if ((characterCheck < 'a' || characterCheck > 'f') && (characterCheck < '0' || characterCheck > '9')) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Clear all the table in the database
     *
     * @param conn connection to the database
     * @throws java.sql.SQLException
     */
    public static void resetDatabase(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
        AppUpdateDAO.clear(conn);
        AppDAO.clear(conn);
        UserDAO.clear(conn);
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        stmt.close();
    }
}
