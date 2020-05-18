package com.example.flipr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MemberNamesItemsAdapter extends ArrayAdapter<TeamMemberClass> {

    Context context;
    ArrayList<TeamMemberClass> items;

    public MemberNamesItemsAdapter(@NonNull Context context, int resource, @NonNull List<TeamMemberClass> items) {
        super(context, resource, items);
        this.context = context;
        this.items = (ArrayList<TeamMemberClass>) items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.member_names_item, null);
        }

        TextView name = convertView.findViewById(R.id.name);
        TextView email = convertView.findViewById(R.id.email);

        TeamMemberClass obj = getItem(position);

        if(obj!=null)
        {
            name.setText(obj.getName());
            email.setText(obj.getEmail());
        }

        return convertView;
    }

}
