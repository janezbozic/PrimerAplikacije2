package si.uni_lj.fri.pbd.miniapp1.contacts;

public class Contact {

    //Class used for Contact object.

    public String id,name,phone,label, email;

    public Contact(String id, String name,String phone,String label, String email){
        this.id=id;
        this.name=name;
        this.phone=phone;
        this.label=label;
        this.email = email;
    }

    //Here we send the text, we want to be displayed for each contact.
    @Override
    public String toString()
    {
        return name + " - " + label;
    }

}