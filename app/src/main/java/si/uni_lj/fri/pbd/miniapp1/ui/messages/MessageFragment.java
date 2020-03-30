package si.uni_lj.fri.pbd.miniapp1.ui.messages;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import java.util.LinkedList;

import si.uni_lj.fri.pbd.miniapp1.R;
import si.uni_lj.fri.pbd.miniapp1.contacts.Contact;
import si.uni_lj.fri.pbd.miniapp1.ui.contacts.ContactsViewModel;

public class MessageFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 34;
    private static final int MY_PERMISSIONS_REQUEST_SEND_EMAIL = 35;
    private ContactsViewModel contactsViewModel;

    // PROBI DODAT PERMISSIONE

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_message, container, false);

        contactsViewModel = new ViewModelProvider(requireActivity()).get(ContactsViewModel.class);

        Button buttonEmail = root.findViewById(R.id.button_email);
        Button buttonMMS = root.findViewById(R.id.button_mms);

        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactsViewModel.getCheckedContacts() != null && contactsViewModel.getCheckedContacts().size() > 0)
                    permissionEmail();
                else
                    Toast.makeText(getActivity(), "The are no contacts selected. Please select one or more.",Toast.LENGTH_SHORT).show();
            }
        });

        buttonMMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactsViewModel.getCheckedContacts() != null && contactsViewModel.getCheckedContacts().size() > 0)
                    permissionMMS();
                else
                    Toast.makeText(getActivity(), "The are no contacts selected. Please select one or more.",Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    public void permissionMMS(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        else {
            sendMMS(contactsViewModel.getCheckedContacts());
        }
    }

    public void permissionEmail(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_SEND_EMAIL);
        }
        else {
            sendEmail(contactsViewModel.getCheckedContacts());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMMS(contactsViewModel.getCheckedContacts());
                } else {
                    Toast.makeText(getActivity(), "Permission for sending MMS messages not granted.",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_SEND_EMAIL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendEmail(contactsViewModel.getCheckedContacts());
                } else {
                    Toast.makeText(getActivity(), "Permission for accessing to internet not granted.",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
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
