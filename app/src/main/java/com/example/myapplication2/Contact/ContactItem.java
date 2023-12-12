package com.example.myapplication2.Contact;

public class ContactItem {

    private String sectionTitle, contactPhoneNo, contactName;

    public ContactItem(String sectionTitle, String contactPhoneNo, String contactName){
        this.sectionTitle = sectionTitle;
        this.contactPhoneNo = contactPhoneNo;
        this.contactName = contactName;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getContactPhoneNo() {
        return contactPhoneNo;
    }

    public void setContactPhoneNo(String contactPhoneNo) {
        this.contactPhoneNo = contactPhoneNo;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
