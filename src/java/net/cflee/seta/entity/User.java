
package net.cflee.seta.entity;

public class User {

    private String macAddress;
    private String name;
    private String password;
    private String email;
    private char gender;
    private String school;
    private int year;

    /**
     * Construct an user object with macAddress, name, password, email, gender,
     * school, year
     *
     * @param macAddress
     * @param name
     * @param password
     * @param email
     * @param gender
     * @param school
     * @param year
     */
    public User(String macAddress, String name, String password, String email,
            char gender, String school, int year) {
        this.macAddress = macAddress;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.school = school;
        this.year = year;
    }

    /**
     * Retrieve the macaddress of the user
     *
     * @return macAddress
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Retrieve the name of the user
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the password of the user
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retrieve the email of the user
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retrieve the gender of the user
     *
     * @return gender
     */
    public char getGender() {
        return gender;
    }

    /**
     * Retrieve the school of the user
     *
     * @return
     */
    public String getSchool() {
        return school;
    }

    /**
     * Retrieve the year of the user
     *
     * @return
     */
    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "User["
                + "macAddress=" + macAddress
                + ",name=" + name
                + ",password=" + password
                + ",email=" + email
                + ",gender=" + gender
                + ",school=" + school
                + ",year=" + year
                + "]";
    }

}
