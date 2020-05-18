package com.example.flipr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginButton, signUpButton;
    FrameLayout fragmentContainer;
    ProgressBar progressBar;

    //FIrebase
    FirebaseAuth firebaseAuth;

    FragmentManager fragmentManager;

    SignUpFragment signUpFragment;

    Boolean signUpFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signUpButton = findViewById(R.id.signup_button);
        fragmentContainer = findViewById(R.id.fragment_container);
        progressBar = findViewById(R.id.progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        signUpFrag = false;

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpFragment = new SignUpFragment();
                signUpFrag = true;
                fragmentContainer.setClickable(true);
                fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_from_right);
                fragmentTransaction.add(R.id.fragment_container,signUpFragment);
                fragmentTransaction.commit();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyFields();
                loginUser(email.getText().toString(),password.getText().toString());
            }
        });

    }

    private void verifyFields()
    {


    }

    private void loginUser(String email, String password)
    {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"Login successfully",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        else
                        {
//                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onBackPressed() {
        if(signUpFrag)
        {
            //Removing SignUp fragment to Login Activity
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_from_right);
            transaction.remove(signUpFragment).commit();
            fragmentContainer.setClickable(false);
            signUpFrag = false;
        }
        else
            super.onBackPressed();
    }
}
