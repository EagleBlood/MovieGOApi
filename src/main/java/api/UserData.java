package api;

public class UserData {
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String homeAddress;
    private String birthDate;

    private String message;

    public UserData() {
    }

    public UserData(String name, String surname, String email, String phoneNumber, String homeAddress, String birthDate) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.homeAddress = homeAddress;
        this.birthDate = birthDate;
    }

    public UserData(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
