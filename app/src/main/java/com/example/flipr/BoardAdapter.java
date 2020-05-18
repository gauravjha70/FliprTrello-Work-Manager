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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BoardAdapter extends ArrayAdapter<BoardClass> {

    Context context;
    ArrayList<BoardClass> items;


    public BoardAdapter(@NonNull Context context, int resource, @NonNull List<BoardClass> items) {
        super(context, resource, items);
        this.context = context;
        this.items = (ArrayList<BoardClass>) items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.board_item_adapter, null);
        }

        TextView name = convertView.findViewById(R.id.name);
        final ImageButton deleteButton = convertView.findViewById(R.id.delete_button);

        final BoardClass boardClass = getItem(position);

        if(boardClass!=null)
        {
            name.setText(boardClass.getName());

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String userId = firebaseAuth.getCurrentUser().getUid();
            try{
                if(!boardClass.getType().equals("Private") && !boardClass.getAdminId().equals(userId))
                {
                    deleteButton.setVisibility(View.GONE);
                }
            }catch (Exception e){}

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setMessage("This action can't be undone. Press yes to delete "+boardClass.getName()+" board").setTitle("Are you sure?")
                    .setCancelable(false)
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            deleteBoard(boardClass,dialogInterface);
                        }
                    });

                    builder.show();
                }
            });

        }

        return convertView;
    }

    private void deleteBoard(BoardClass boardClass, final DialogInterface dialogInterface)
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if(boardClass.getType() == null)
        {
            DatabaseReference mainRef = firebaseDatabase.getReference().child("Boards").child(boardClass.getId());

            //Private Board Deletion
            mainRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(),"Board deleted !",Toast.LENGTH_LONG).show();
                    dialogInterface.cancel();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Error deleting the board",Toast.LENGTH_LONG).show();
                    dialogInterface.cancel();
                }
            });
        }
        else if(boardClass.getType().equals("Private"))
        {
            DatabaseReference ref = firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("Boards").child("Private").child(boardClass.getId());

            //Private Board Deletion
            ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(),"Board deleted !",Toast.LENGTH_LONG).show();
                    dialogInterface.cancel();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Error deleting the board",Toast.LENGTH_LONG).show();
                    dialogInterface.cancel();
                }
            });
        }
    }


}
