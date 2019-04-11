package edu.css.cis3334_unit12_firebase_2019;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    Button btnPost;
    Button btnLogout;
    EditText etMessage;
    TextView tvMsgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnPost = findViewById(R.id.buttonPost);
        btnLogout = findViewById(R.id.buttonChatLogout);
        etMessage = findViewById(R.id.editTextMessage);
        tvMsgList = findViewById(R.id.textViewMsgList);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgText = etMessage.getText().toString();
                etMessage.setText("");           // clear out the message text box to be ready for the next message

                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");

                myRef.setValue(msgText);

                // This does not push message to firebase yet...
                //    The next line should really be in the Firebase onDataChange() method
                tvMsgList.setText(msgText+ "\n" + tvMsgList.getText());

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
