package si.uni_lj.fri.pbd.miniapp1.contacts;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.lifecycle.ViewModelProvider;

import java.util.LinkedList;

import si.uni_lj.fri.pbd.miniapp1.MainActivity;
import si.uni_lj.fri.pbd.miniapp1.R;
import si.uni_lj.fri.pbd.miniapp1.ui.contacts.ContactsViewModel;

public class ContactsListAdapter extends BaseAdapter {

    Context context;
    LinkedList<Contact> contacts,selectedContacts;
    ContactsViewModel contactsViewModel;

    public ContactsListAdapter(Context context, LinkedList<Contact> contactLinkedList){

        super();
        this.context = context;
        this.contacts = contactLinkedList;

        contactsViewModel = new ViewModelProvider((MainActivity)context).get(ContactsViewModel.class);

        //We check in ContactsViewModel if LinkedList for checked contacts already exists.
        //Otherwise we create it and set it.
        if (contactsViewModel.getCheckedContacts() == null) {
            this.selectedContacts = new LinkedList<>();
            contactsViewModel.setCheckedContacts(this.selectedContacts);
        }
        else{
            this.selectedContacts = contactsViewModel.getCheckedContacts();
        }

    }

    //This method gets contact from it's id.
    public Contact getContact(int id, LinkedList<Contact> c){

        for(int i=0;i<c.size();i++){
            if(Integer.parseInt(c.get(i).id)==id)
                return c.get(i);
        }

        return null;
    }

    public void addContacts(LinkedList<Contact> contacts){
        this.contacts.addAll(contacts);
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(this.getItem(position).id);
    }

    //Get view is activated for every element in a listview (for a contact in linkedlist).
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        CheckBoxViewHolder viewHolder;
        //ViewHolder is used, so the app is little bit more responsive
        //Because it doesn't have to serach for a view contact_check_box every time from layout.
        if(view==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(R.layout.contact_check_box, null);
            viewHolder = new CheckBoxViewHolder();
            viewHolder.checkBoxContact = (CheckBox) view.findViewById(R.id.chk_contact);
            view.setTag(viewHolder);
        }else {
            viewHolder = (CheckBoxViewHolder) view.getTag();
        }

        //We add data to checkBox (displayed contact)
        viewHolder.checkBoxContact.setText(this.contacts.get(position).toString());
        viewHolder.checkBoxContact.setId(Integer.parseInt(this.contacts.get(position).id));
        viewHolder.checkBoxContact.setChecked(isChecked(contacts.get(position)));

        //We add click listener to checkbox, so we add contact to list of checked contacts.
        viewHolder.checkBoxContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Contact contact = getContact(buttonView.getId(), contacts);
                if(contact!=null && isChecked && !isChecked(contact)){
                    selectedContacts.add(contact);
                }
                else if(contact!=null && !isChecked){
                    selectedContacts.remove(contact);
                }
            }
        });
        return view;
    }

    //We just check if contact was checked (if it is in a linkedlist of selected contacts).
    public boolean isChecked(Contact contact)
    {
        if(getContact(Integer.parseInt(contact.id), selectedContacts)!=null)
            return true;
        return false;
    }

    //Class of ViewHolder for checkbox.
    public static class CheckBoxViewHolder{
        CheckBox checkBoxContact;
    }
}