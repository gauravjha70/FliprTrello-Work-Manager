package com.example.flipr;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MemberDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private EditText emailSearch;
    private Button addButton;
    private ProgressBar progressBar;
    private TextView adminName, adminEmail;
    private ListView memberList;


    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    private String boardId;
    private ArrayList<TeamMemberClass> members;
    private MemberNamesItemsAdapter adapter;


    public MemberDetailsBottomSheetFragment(String boardId)
    {
        this.boardId = boardId;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.member_details_bottom_fragment,container,false);

        emailSearch = rootView.findViewById(R.id.email_to_search);
        addButton = rootView.findViewById(R.id.add_button);
        progressBar = rootView.findViewById(R.id.progress_bar);
        adminName = rootView.findViewById(R.id.admin_name);
        adminEmail = rootView.findViewById(R.id.admin_email);
        memberList = rootView.findViewById(R.id.members);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        members = new ArrayList<TeamMemberClass>();
        adapter = new MemberNamesItemsAdapter(getContext(),R.layout.member_names_item,members);

        getBoardMembers();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!emailSearch.getText().toString().trim().equals(""))
                {
                    addMember();
                }
            }
        });

        return rootView;
    }

    private void addMember()
    {
        final String email = emailSearch.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference ref = firebaseDatabase.getReference().child("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean flag = false;
                String userName ="";
                String userId = "";
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    if(snapshot.child("Email").getValue().toString().equals(email))
                    {
                        flag = true;
                        userName = snapshot.child("Name").getValue().toString();
                        userId = snapshot.getKey();
                        break;
                    }
                }
                if(!flag)
                {
                    Toast.makeText(getContext(),"User not found",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getContext(),"Member Added",Toast.LENGTH_LONG).show();
                    getDialog().dismiss();
                    addMemberToBoard(userId,userName,email);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addMemberToBoard(String userId, String userName, String email)
    {
        DatabaseReference ref = firebaseDatabase.getReference().child("Users").child(userId).child("Boards")
                .child("Public");
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("BoardId",boardId);
        ref.push().setValue(map);

        HashMap<String,String> map2 = new HashMap<String, String>();
        map2.put("name",userName);
        map2.put("email",email);
        DatabaseReference refMain = firebaseDatabase.getReference().child("Boards").child(boardId).child("Members");
        refMain.push().setValue(map2);
    }


    private void getBoardMembers()
    {
        DatabaseReference ref = firebaseDatabase.getReference().child("Boards");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(boardId)){
                    adminName.setText(dataSnapshot.child("adminName").getValue().toString());
                    adminEmail.setText(dataSnapshot.child("adminEmail").getValue().toString());
                    members = (ArrayList<TeamMemberClass>)dataSnapshot.child("Members").getValue();
                    adapter = new MemberNamesItemsAdapter(getContext(),R.layout.member_names_item,members);
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

}
