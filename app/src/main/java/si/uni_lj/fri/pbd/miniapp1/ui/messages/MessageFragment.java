package si.uni_lj.fri.pbd.miniapp1.ui.messages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.LinkedList;

import si.uni_lj.fri.pbd.miniapp1.R;
import si.uni_lj.fri.pbd.miniapp1.contacts.Contact;
import si.uni_lj.fri.pbd.miniapp1.ui.contacts.ContactsViewModel;

public class MessageFragment extends Fragment {

    private ContactsViewModel contactsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_message, container, false);

        contactsViewModel = new ViewModelProvider(requireActivity()).get(ContactsViewModel.class);

        Button buttonEmail = root.findViewById(R.id.button_email);
        Button buttonMMS = root.findViewById(R.id.button_mms);

        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactsViewModel.getCheckedContacts() != null && contactsViewModel.getCheckedContacts().size() > 0)
                    sendEmail(contactsViewModel.getCheckedContacts());
                else
                    Toast.makeText(getActivity(), "The are no contacts selected. Please select one or more.",Toast.LENGTH_SHORT).show();
            }
        });

        buttonMMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactsViewModel.getCheckedContacts() != null && contactsViewModel.getCheckedContacts().size() > 0)
                    sendMMS(contactsViewModel.getCheckedContacts());
                else
                    Toast.makeText(getActivity(), "The are no contacts selected. Please select one or more.",Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void sendEmail(LinkedList<Contact> checkedContacts){

        String s = "";

        for (int i = 0; i<checkedContacts.size(); i++){
            s += checkedContacts.get(i).email;
            if (i != checkedContacts.size()-1)
                s += ";";
        }

        String uriText = "mailto:" + Uri.encode(s) + "?subject=" + Uri.encode("PBD2020 Group Email") + "&body=" + Uri.encode("Sent from my Android mini app 1");

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse(uriText));
        startActivity(emailIntent);

        checkedContacts.clear();

    }

    private void sendMMS(LinkedList<Contact> checkedContacts) {

        String s = "";

        for (int i = 0; i<checkedContacts.size(); i++){
            s += checkedContacts.get(i).phone;
            if (i != checkedContacts.size()-1)
                s += ";";
        }

        Intent mmsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + s));
        mmsIntent.putExtra("sms_body", "Sent from my Android mini app 1");
        startActivity(mmsIntent);

        checkedContacts.clear();

    }
}
