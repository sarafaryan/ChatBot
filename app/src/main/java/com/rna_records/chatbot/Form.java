package com.rna_records.chatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Form extends AppCompatActivity {
 EditText namef,phonef,address1f,address2f,cityf,pincodef,contact1f,contact2f;
 Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        namef = findViewById(R.id.nameEditText);
        phonef = findViewById(R.id.phone);
        address1f = findViewById(R.id.add1);
        address2f = findViewById(R.id.add2);
        cityf = findViewById(R.id.city);
        pincodef = findViewById(R.id.pc);
        contact1f = findViewById(R.id.EC1);
        contact2f = findViewById(R.id.EC2);
        submit = findViewById(R.id.btn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name,phone,address1,address2,city,pincode,contact1,contact2;
                name = namef.getText().toString();
                phone = phonef.getText().toString();
                address1 = address1f.getText().toString();
                address2 = address2f.getText().toString();
                city = cityf.getText().toString();
                pincode = pincodef.getText().toString();
                contact1 = contact1f.getText().toString();
                contact2 = contact2f.getText().toString();
                Intent intent = new Intent(Form.this,MainActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("phone",phone);
                intent.putExtra("address1",address1);
                intent.putExtra("address2",address2);
                intent.putExtra("city",city);
                intent.putExtra("pincode",pincode);
                intent.putExtra("contact1",contact1);
                intent.putExtra("contact2",contact2);
                startActivity(intent);

            }
        });

    }
}