package com.example.chataaap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private  static int sign_in_request_code=1;
    private FirebaseListAdapter<chatmessage> adapter;
    RelativeLayout activity_main;
    FloatingActionButton fab;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==R.id.menu_sign_out)
        {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main,"you have been signed out",Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        return  true;
    }

    @Override

    protected void onActivityResult(int requestCode, int resultcode, Intent data)
    {
        super.onActivityResult(requestCode,resultcode,data);
        if(requestCode==sign_in_request_code)
        {
            if(resultcode==RESULT_OK)
            {
                Snackbar.make(activity_main,"successfully signed in.Welcome!",Snackbar.LENGTH_SHORT).show();
                displaychatmessage();
            }
            else {

                Snackbar.make(activity_main,"we couldn't sign you in,please_try later",Snackbar.LENGTH_SHORT).show();
                finish();
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity_main=(RelativeLayout) findViewById(R.id.activity_main);
        fab=(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                EditText input=(EditText) findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new chatmessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");

            }
        });


        //check if not sign in then navigate to sign in page

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),sign_in_request_code);
        }

        else{
            Snackbar.make(activity_main,"Welcome"+FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();

            //load content
            displaychatmessage();
        }


    }
    private void displaychatmessage()
    {
        ListView listofmessage= (ListView) findViewById(R.id.list_of_message);
        adapter=new FirebaseListAdapter<chatmessage>(this,chatmessage.class,R.layout.list_item,
                FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void  populateView(View v,chatmessage model,int postion) {
                //get references to the views of list_item.xml
                TextView messageText,messageUser,messagegetime;

                messageText=(TextView) v.findViewById(R.id.message_text);
                messageUser=(TextView) v.findViewById(R.id.message_user);
                messagegetime=(TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messagegetime.setText(DateFormat.format("dd/MM/yyyy HH:mm:ss.SS",model.getMessageTime()));
            }
        };
        listofmessage.setAdapter(adapter);

    }

}
