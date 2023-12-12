package com.example.myapplication2.Contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{

    private List<ContactItem> contactItems;
    private DetailsClickListener detailsClickListener;
    private String sectionTitleAlphabet ="Z";


    public ContactAdapter(List<ContactItem> contactItems,
                          DetailsClickListener detailsClickListener){
        this.contactItems = contactItems;
        this.detailsClickListener = detailsClickListener;
    }


    //when recyclerview need create new viewholder for item in list
    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ContactAdapter.ViewHolder(view);
    }

    //bind data to the view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ContactItem contactItem = contactItems.get(position);

        /*if (sectionTitleAlphabet.equalsIgnoreCase(contactItem.getSectionTitle())){
            viewHolder.sectionTitle.setVisibility(View.GONE);
        } else{
            sectionTitleAlphabet = contactItem.getSectionTitle();
        }

        viewHolder.sectionTitle.setText(contactItem.getSectionTitle());*/
        viewHolder.contactName.setText(contactItem.getContactName());
        viewHolder.contactPhoneNo.setText(contactItem.getContactPhoneNo());

        viewHolder.contactDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(detailsClickListener != null){
                    detailsClickListener.detailsClick(contactItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         TextView sectionTitle, contactName, contactPhoneNo;
         ImageButton contactDetailsBtn;

         public ViewHolder(@NonNull View itemView){
             super(itemView);
             //sectionTitle=itemView.findViewById(R.id.sectionTitle);
             contactName=itemView.findViewById(R.id.contactName);
             contactPhoneNo=itemView.findViewById(R.id.contactPhoneNo);
             contactDetailsBtn=itemView.findViewById(R.id.contactDetailsBtn);
         }
    }

    //interface for contacts details button
    public interface DetailsClickListener {
        void detailsClick(ContactItem contactItem);
    }
}
