package com.mamba.grapple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by vash on 4/18/15.
 */
public class PostSession extends Activity {

    Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postsession);


        doneButton = (Button) findViewById(R.id.doneBtn);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: get tutor rating

                // return to search and finish
                Intent intent = new Intent(PostSession.this, Search.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
