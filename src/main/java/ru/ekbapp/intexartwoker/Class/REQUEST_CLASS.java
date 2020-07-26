package ru.ekbapp.intexartwoker.Class;

public class REQUEST_CLASS {
    public final String id;
    public String number;
    public String AddressText;
    public String apartmentText;
    public String dateCreate;
    public String phoneCustomer;
    public String message;
    public String status;
    public String direction;
    public String type;
    public String responsible;
    public String customerName;
    public String head;



    public REQUEST_CLASS(String id, String number, String addressText, String apartmentText, String dateCreate, String phoneCustomer, String message, String status, String direction, String type, String responsible) {
        this.id = id;
        this.number = number;
        AddressText = addressText;
        this.apartmentText = apartmentText;
        this.dateCreate = dateCreate;
        this.phoneCustomer = phoneCustomer;
        this.message = message;
        this.status = status;
        this.direction = direction;
        this.type = type;
        this.responsible = responsible;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getApartmentText() {
        return apartmentText;
    }

    public void setApartmentText(String apartmentText) {
        this.apartmentText = apartmentText;
    }

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddressText() {
        return AddressText;
    }

    public void setAddressText(String addressText) {
        AddressText = addressText;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getPhoneCustomer() {
        return phoneCustomer;
    }

    public void setPhoneCustomer(String phoneCustomer) {
        this.phoneCustomer = phoneCustomer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }
}
