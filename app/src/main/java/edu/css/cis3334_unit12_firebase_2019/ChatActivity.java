package edu.css.cis3334_unit12_firebase_2019;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    Button btnPost;
    Button btnLogout;
    EditText etMessage;
    TextView tvMsgList;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");

        btnPost = findViewById(R.id.buttonPost);
        btnLogout = findViewById(R.id.buttonChatLogout);
        etMessage = findViewById(R.id.editTextMessage);
        tvMsgList = findViewById(R.id.textViewMsgList);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgText = etMessage.getText().toString();
                etMessage.setText("");           // clear out the message text box to be ready for the next message

                //If message empty, do nothing
                if(msgText.isEmpty()){
                    return;
                }
                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Chat");

                String key = myRef.push().getKey();

                //Make new ChatMessage object with email and message text
                ChatMessage cm = new ChatMessage(email, msgText);
                myRef.child(key).setValue(cm);

                myRef.addValueEventListener(new ValueEventListener() {
                    //Set tvMsgList to all chat messages
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tvMsgList.setText("");
                        for(DataSnapshot chatSnapshot : dataSnapshot.getChildren()){
                            ChatMessage value = chatSnapshot.getValue(ChatMessage.class);
                            tvMsgList.append("\n" + value.getEmail() + " says: " + value.getMessage());
                        }
                    }

                    //Do nothing on cancel
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();           // quit this activity and return to mainActivity
            }
        });

    }
}
