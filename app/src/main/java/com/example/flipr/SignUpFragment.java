package com.example.flipr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpFragment extends Fragment {

    EditText name, email, password, confirmPassword;
    Button signUpButton;
    ProgressBar progressBar;

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_fragment,container,false);

        name = rootView.findViewById(R.id.full_name_edit);
        email = rootView.findViewById(R.id.email_edit);
        password= rootView.findViewById(R.id.password_edit);
        confirmPassword = rootView.findViewById(R.id.verify_password_edit);
        signUpButton = rootView.findViewById(R.id.signup_button);
        progressBar = rootView.findViewById(R.id.progress_bar);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();               //Validating Fields
                createNewUser(email.getText().toString(), password.getText().toString());

            }
        });

        return rootView;
    }

    private void validateFields()
    {}

    private void createNewUser(String email, String password)
    {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            String emailId = firebaseAuth.getCurrentUser().getEmail();
                            createNewUserProfile(userId, emailId, name.getText().toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),"An error has occurred",Toast.LENGTH_LONG).show();
                        Log.e("New User",e.getMessage());
                    }
                });
    }

    private void createNewUserProfile(String userId, String emailId, String name)
    {
        HashMap<String,String> user = new HashMap<String, String>();
        user.put("Name",name);
        user.put("Email", emailId);
        DatabaseReference ref = firebaseDatabase.getReference().child("Users");
        ref.child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),"User created successfully",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("DB entry",e.getMessage());
            }
        });
    }



}
