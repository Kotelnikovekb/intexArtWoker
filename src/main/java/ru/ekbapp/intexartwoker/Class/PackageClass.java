package ru.ekbapp.intexartwoker.Class;

public class PackageClass {
    public final String ID;
    public final String Name;
    public final String Description;
    public final String Position;
    public final String DocumentList;

    public PackageClass(String ID, String name, String description, String position, String documentList) {
        this.ID = ID;
        Name = name;
        Description = description;
        Position = position;
        DocumentList = documentList;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public String getPosition() {
        return Position;
    }

    public String getDocumentList() {
        return DocumentList;
    }
}
