package com.kernelapps.onlinejobz.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.anythink.nativead.api.ATNativeAdView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kernelapps.onlinejobz.BaseActivity;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.utils.AdsManager;
import com.kernelapps.onlinejobz.utils.NativeAdManager;

import java.util.HashMap;
import java.util.Map;

public class ContactActivity extends BaseActivity {
    private EditText nameEditText, emailEditText, messageEditText;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, "Contact us", true);

        // Initialize views
        nameEditText = findViewById(R.id.textOtp);
        emailEditText = findViewById(R.id.textEmail);
        messageEditText = findViewById(R.id.textMessage);
        Button sendButton = findViewById(R.id.btnSend);

        // Set click listener for send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToFirestore();
            }
        });

        ATNativeAdView adContainer = findViewById(R.id.native_ad_view);
        NativeAdManager adManager = new NativeAdManager(this, adContainer);

        adManager.loadAd();

    }

    private void sendDataToFirestore() {
        // Get data from EditText fields
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (message.isEmpty()) {
            messageEditText.setError("Message is required");
            messageEditText.requestFocus();
            return;
        }

        // Create a new contact message
        Map<String, Object> contact = new HashMap<>();
        contact.put("name", name);
        contact.put("email", email);
        contact.put("message", message);
        contact.put("timestamp", System.currentTimeMillis());

        // Add a new document with a generated ID
        db.collection("contacts")
                .add(contact)
                .addOnSuccessListener(documentReference -> {
                    // Clear fields after successful submission
                    nameEditText.setText("");
                    emailEditText.setText("");
                    messageEditText.setText("");

                    Toast.makeText(ContactActivity.this,
                            "Message sent successfully!",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ContactActivity.this,
                            "Error sending message: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
