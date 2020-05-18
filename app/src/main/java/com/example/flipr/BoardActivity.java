package com.example.flipr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class BoardActivity extends AppCompatActivity {

    ViewPager viewPager;
    FloatingActionButton addButton;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ArrayList<ListClass> items;
    ArrayList<String> listIds;
    String boardId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        viewPager = findViewById(R.id.view_pager);
        addButton = findViewById(R.id.add_button);

        //Set the current board ID
        boardId = getIntent().getStringExtra("BoardId");

        items = new ArrayList<ListClass>();
        listIds = new ArrayList<String>();
        viewPager.setAdapter(new CardsPagerAdapater(getSupportFragmentManager(),items,boardId,listIds,"Private"));

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        addListsToBoard();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inflateAddListDialog();
            }
        });
    }

    private void addListsToBoard()
    {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference ref = firebaseDatabase.getReference().child("Users").child(userId)
                .child("Boards").child("Private").child(boardId).child("Lists");
        Log.d("Board ID",boardId);
        Log.d("Reference",ref.toString());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ListClass obj = dataSnapshot.getValue(ListClass.class);
                items.add(obj);
                listIds.add(dataSnapshot.getKey());
                viewPager.setAdapter(new CardsPagerAdapater(getSupportFragmentManager(),items,boardId,listIds,"Private"));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ListClass obj = dataSnapshot.getValue(ListClass.class);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inflateAddListDialog()
    {
        LayoutInflater factory = LayoutInflater.from(BoardActivity.this);
        final View addListDialogView = factory.inflate(R.layout.add_item_dialog, null);
        final AlertDialog addDialog = new AlertDialog.Builder(BoardActivity.this).create();
        addDialog.setView(addListDialogView);
        addDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final EditText listName = addListDialogView.findViewById(R.id.list_name);
        Button addListButton = addListDialogView.findViewById(R.id.add_list_button);
        final ProgressBar progressBar = addListDialogView.findViewById(R.id.progress_bar);

        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!listName.getText().toString().trim().equals(""))
                {
                    progressBar.setVisibility(View.VISIBLE);

                    ListClass newList = new ListClass();
                    newList.setListName(listName.getText().toString().trim());
                    newList.setStatus(true);
                    newList.setCards(new ArrayList<CardsClass>());
                    newList.setDateOfCreation(new Date());

                    String userId = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference ref = firebaseDatabase.getReference().child("Users").child(userId).child("Boards").child("Private").child(boardId).child("Lists");

                    ref.push().setValue(newList).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"List added!",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            addDialog.cancel();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"List cannot be created!",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            addDialog.cancel();
                        }
                    });
                }
            }
        });

        addDialog.show();

    }


}
