package si.uni_lj.fri.pbd.miniapp1.contacts;

public class Contact {

    public String id,name,phone,label, email;

    public Contact(String id, String name,String phone,String label, String email){
        this.id=id;
        this.name=name;
        this.phone=phone;
        this.label=label;
        this.email = email;
    }

    @Override
    public String toString()
    {
        return name + " - " + label;
    }

}