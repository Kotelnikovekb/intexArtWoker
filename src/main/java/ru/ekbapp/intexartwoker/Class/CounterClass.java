package ru.ekbapp.intexartwoker.Class;

public class CounterClass {
    public final String Position;
    public final String Type;
    public  String Number;
    public String DateNextCheck;
    public String Peal;
    public String Indications;

    public CounterClass(String position, String type, String number, String dateNextCheck, String peal, String indications) {
        Position = position;
        Type = type;
        Number = number;
        DateNextCheck = dateNextCheck;
        Peal = peal;
        Indications = indications;
    }

    public String getPosition() {
        return Position;
    }

    public String getType() {
        return Type;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getDateNextCheck() {
        return DateNextCheck;
    }

    public void setDateNextCheck(String dateNextCheck) {
        DateNextCheck = dateNextCheck;
    }

    public String getPeal() {
        return Peal;
    }

    public void setPeal(String peal) {
        Peal = peal;
    }

    public String getIndications() {
        return Indications;
    }

    public void setIndications(String indications) {
        Indications = indications;
    }
}
