package ru.ekbapp.intexartwoker.Class;

public class AdsClass {
    public String Id;
    public String AddressId;
    public String AddressText;
    public String DateCreate;
    public String Text;
    public String Number;
    public String CreatorText;

    public AdsClass(String id, String addressId, String addressText, String dateCreate, String text, String number, String creatorText) {
        Id = id;
        AddressId = addressId;
        AddressText = addressText;
        DateCreate = dateCreate;
        Text = text;
        Number = number;
        CreatorText = creatorText;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getAddressId() {
        return AddressId;
    }

    public void setAddressId(String addressId) {
        AddressId = addressId;
    }

    public String getAddressText() {
        return AddressText;
    }

    public void setAddressText(String addressText) {
        AddressText = addressText;
    }

    public String getDateCreate() {
        return DateCreate;
    }

    public void setDateCreate(String dateCreate) {
        DateCreate = dateCreate;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getCreatorText() {
        return CreatorText;
    }

    public void setCreatorText(String creatorText) {
        CreatorText = creatorText;
    }
}
