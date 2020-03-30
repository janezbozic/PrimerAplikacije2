package si.uni_lj.fri.pbd.miniapp1.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.LinkedList;

import si.uni_lj.fri.pbd.miniapp1.contacts.Contact;
import si.uni_lj.fri.pbd.miniapp1.contacts.ContactsListAdapter;
import si.uni_lj.fri.pbd.miniapp1.R;

public class ContactsFragment extends Fragment {

    private ContactsViewModel contactsViewModel;
    View view;
    LinkedList<Contact> tempContactHolder;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        contactsViewModel = new ViewModelProvider(requireActivity()).get(ContactsViewModel.class);
        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        tempContactHolder = contactsViewModel.getContactHolder();

        contacts();

        return view;

    }

    ListView contactsChooser;
    ContactsListAdapter contactsListAdapter;

    public void contacts(){

        contactsChooser = (ListView) view.findViewById(R.id.contactChooser);
        contactsListAdapter = new ContactsListAdapter(getActivity(),new LinkedList<Contact>());

        try{
            contactsListAdapter.addContacts(tempContactHolder);
            contactsChooser.setAdapter(contactsListAdapter);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
