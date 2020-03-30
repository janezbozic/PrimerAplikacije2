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

        if (contactsViewModel.getCheckedContacts() == null) {
            this.selectedContacts = new LinkedList<>();
            contactsViewModel.setCheckedContacts(this.selectedContacts);
        }
        else{
            this.selectedContacts = contactsViewModel.getCheckedContacts();
        }

    }

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

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        CheckBoxViewHolder viewHolder;

        if(view==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(R.layout.contact_check_box, null);
            viewHolder = new CheckBoxViewHolder();
            viewHolder.checkBoxContact = (CheckBox) view.findViewById(R.id.chk_contact);
            view.setTag(viewHolder);
        }else {
            viewHolder = (CheckBoxViewHolder) view.getTag();
        }

        viewHolder.checkBoxContact.setText(this.contacts.get(position).toString());
        viewHolder.checkBoxContact.setId(Integer.parseInt(this.contacts.get(position).id));
        viewHolder.checkBoxContact.setChecked(isChecked(contacts.get(position)));

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

    public boolean isChecked(Contact contact)
    {
        if(getContact(Integer.parseInt(contact.id), selectedContacts)!=null)
            return true;
        return false;
    }

    public static class CheckBoxViewHolder{
        CheckBox checkBoxContact;
    }
}