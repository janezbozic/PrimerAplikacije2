package si.uni_lj.fri.pbd.miniapp1.ui.contacts;

import androidx.lifecycle.ViewModel;

import java.util.LinkedList;

import si.uni_lj.fri.pbd.miniapp1.contacts.Contact;

public class ContactsViewModel extends ViewModel {

    private LinkedList<Contact> contactHolder;
    private LinkedList<Contact> checkedContacts;

    public LinkedList<Contact> getCheckedContacts() {
        return checkedContacts;
    }

    public void setCheckedContacts(LinkedList<Contact> checkedContacts) {
        this.checkedContacts = checkedContacts;
    }

    public LinkedList<Contact> getContactHolder() {
        return contactHolder;
    }

    public void setContactHolder(LinkedList<Contact> contactHolder) {
        this.contactHolder = contactHolder;
    }

}