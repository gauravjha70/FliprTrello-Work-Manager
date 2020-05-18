package com.example.flipr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ListView personalList, teamList;
    FloatingActionButton addButton;
    Button logout;

    //Firebase
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    ArrayList<BoardClass> personalItems, teamItems;
    BoardAdapter personalAdapter, teamAdapter;

    String userName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        personalList = findViewById(R.id.personal_board_list);
        teamList = findViewById(R.id.team_board_list);
        addButton = findViewById(R.id.add_button);
        logout = findViewById(R.id.logout);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = firebaseDatabase.getInstance();

        personalItems = new ArrayList<BoardClass>();
        personalAdapter = new BoardAdapter(MainActivity.this,R.layout.board_item_adapter,personalItems);

        teamItems = new ArrayList<BoardClass>();
        teamAdapter = new BoardAdapter(MainActivity.this,R.layout.board_item_adapter,teamItems);

        personalList.setAdapter(personalAdapter);
        teamList.setAdapter(teamAdapter);

        //Set user name
        setuserName();

        //Inflate board Lists
        getPrivateBoards();
        getTeamBoards();

        personalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,BoardActivity.class);
                intent.putExtra("BoardId",personalItems.get(i).getId());
                startActivity(intent);
            }
        });

        teamList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,TeamBoardActivity.class);
                intent.putExtra("BoardId",teamItems.get(i).getId());
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start new board creation dialog
                createNewBoard();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private void setuserName()
    {
        final String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference ref = firebaseDatabase.getReference().child("Users");

        Log.d("User ref",ref.toString());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(userId))
                {
                    userName = dataSnapshot.child("Name").getValue().toString();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createNewBoard()
    {
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View addListDialogView = factory.inflate(R.layout.add_item_dialog, null);
        final AlertDialog addDialog = new AlertDialog.Builder(MainActivity.this).create();
        addDialog.setView(addListDialogView);
        addDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final EditText listName = addListDialogView.findViewById(R.id.list_name);
        Button addListButton = addListDialogView.findViewById(R.id.add_list_button);
        final ProgressBar progressBar = addListDialogView.findViewById(R.id.progress_bar);
        final RadioGroup radioGroup = addListDialogView.findViewById(R.id.board_type_radio_group);
        TextView header = addListDialogView.findViewById(R.id.header);

        header.setText("Create a new board");
        listName.setHint("Board Name");
        radioGroup.setVisibility(View.VISIBLE);

        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!listName.getText().toString().trim().equals(""))
                {
                    progressBar.setVisibility(View.VISIBLE);

                    String boardName = listName.getText().toString().trim();


                    int typeId = radioGroup.getCheckedRadioButtonId();

                    if(typeId == R.id.personal)
                    {
                        createPersonalBoard(addDialog,boardName,progressBar);
                    }
                    else
                    {
                        createTeamBoard(addDialog,boardName,progressBar);
                    }
                }
            }
        });

        addDialog.show();
    }

    private void createPersonalBoard(final AlertDialog addDialog, String boardName, final ProgressBar progressBar)
    {
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("Name",boardName);
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference ref = firebaseDatabase.getReference().child("Users").child(userId).child("Boards").child("Private");

        ref.push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Board created!",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                addDialog.cancel();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Board cannot be created!",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                addDialog.cancel();
            }
        });
    }

    private void createTeamBoard(final AlertDialog addDialog, String boardName, final ProgressBar progressBar)
    {
        final String userId = firebaseAuth.getCurrentUser().getUid();
        String email = firebaseAuth.getCurrentUser().getEmail();
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("Name",boardName);
        map.put("adminId",userId);
        map.put("adminName",userName);
        map.put("adminEmail",email);

        final DatabaseReference mainRef = firebaseDatabase.getReference().child("Boards");
        final String newItemId = mainRef.push().getKey();
        mainRef.child(newItemId).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                DatabaseReference ref = firebaseDatabase.getReference().child("Users").child(userId).child("Boards").child("Public");
                HashMap<String,String> publicBoard = new HashMap<String, String>();
                publicBoard.put("BoardId",newItemId);
                ref.push().setValue(publicBoard).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Board created!",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        addDialog.cancel();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Board cannot be created!",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        addDialog.cancel();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void getPrivateBoards()
    {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference ref = firebaseDatabase.getReference().child("Users").child(userId).child("Boards").child("Private");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                BoardClass boardClass = new BoardClass();
                boardClass.setId(dataSnapshot.getKey());
                boardClass.setName(dataSnapshot.child("Name").getValue().toString());
                boardClass.setType("Private");

                personalItems.add(boardClass);
                personalAdapter = new BoardAdapter(MainActivity.this,R.layout.board_item_adapter,personalItems);

                personalList.setAdapter(personalAdapter);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                BoardClass boardClass = new BoardClass();
                boardClass.setId(dataSnapshot.getKey());
                boardClass.setName(dataSnapshot.child("Name").getValue().toString());
                boardClass.setType("Private");

                removeFromPersonalList(boardClass);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeFromPersonalList(BoardClass obj)
    {
        for(int i=0;i<personalItems.size();i++)
        {
            if(personalItems.get(i).getId().equals(obj.getId()))
            {
                personalItems.remove(i);
                break;
            }
        }
        personalAdapter = new BoardAdapter(MainActivity.this,R.layout.board_item_adapter,personalItems);
        personalList.setAdapter(personalAdapter);
    }

    //Inflate team boards
    private void getTeamBoards()
    {
        final String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference ref = firebaseDatabase.getReference().child("Users").child(userId).child("Boards").child("Public");

        final ArrayList<String> teamBoardIds = new ArrayList<String>();

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                teamBoardIds.add(dataSnapshot.child("BoardId").getValue().toString());
                getTeamBoardDetails(teamBoardIds);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTeamBoardDetails(final ArrayList<String> boardIds)
    {
        teamItems = new ArrayList<BoardClass>();
        final DatabaseReference ref = firebaseDatabase.getReference().child("Boards");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    if(boardIds.contains(snapshot.getKey()))
                    {
                        BoardClass obj = snapshot.getValue(BoardClass.class);
                        obj.setId(snapshot.getKey());
                        teamItems.add(obj);
                        Log.d("Board Name",obj.getName());
                    }
                }
                teamAdapter = new BoardAdapter(MainActivity.this,R.layout.board_item_adapter,teamItems);
                teamList.setAdapter(teamAdapter);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




}
