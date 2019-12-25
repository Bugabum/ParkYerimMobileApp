package com.example.parkyerim;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    EditText mFullName,mEmail,mPassword,mPassword2,mPhone;
    Button mRegisterBtn;
    TextView mLoginBtn;
    ProgressBar progressBar;

    public static final String TAG = "TAG";
    FirebaseFirestore fStore;
    String userID;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName   = findViewById(R.id.fullName);
        mEmail      = findViewById(R.id.email);
        mPassword   = findViewById(R.id.password);
        mPassword2  = findViewById(R.id.password2);
        mPhone      = findViewById(R.id.phone);
        mRegisterBtn= findViewById(R.id.registerButton);
        mLoginBtn   = findViewById(R.id.loginText);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String password2 = mPassword2.getText().toString().trim();
                final String fullname = mFullName.getText().toString().trim();
                final String phone = mPhone.getText().toString();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email alanı boş olamaz.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Şifre alanı boş olamaz.");
                    return;
                }
                if(!TextUtils.equals(password, password2)){
                    mPassword2.setError("Şifre alanları aynı olmalıdır.");
                    return;
                }
                if(TextUtils.isEmpty(fullname)){
                    mFullName.setError("İsim alanı boş olamaz.");
                    return;
                }
                if(TextUtils.isEmpty(phone)){
                    mPhone.setError("Numara alanı boş olamaz.");
                    return;
                }
                if(password.length()<6){
                    mPassword.setError("Şifre 6 karakter ya da daha fazla olmalıdır");
                    return;
                }
                try{
                    long phonenumber = Long.parseLong(phone);
                }
                catch(Exception e){
                    return;
                }
                long phonenumber = Long.parseLong(phone);
                //numara kontrolü yapılmak isteniyorsa

                //
                //Kontroller tamamlandı

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "Kullanıcı Oluşturuldu.", Toast.LENGTH_SHORT).show();

                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName",fullname);
                            user.put("email",email);
                            user.put("phone",phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                  Log.d(TAG,"onSuccess: Kullanıcı Profili oluşturuldu: "+userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure: "+e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
}
