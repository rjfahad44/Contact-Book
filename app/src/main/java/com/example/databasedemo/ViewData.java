package com.example.databasedemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class ViewData extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    LinearLayout linearLayout;
    Dialog dialog;
    FloatingActionButton floatingAddBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Contact Book");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 100);
        }

        linearLayout = findViewById(R.id.container);

        floatingAddBtn = findViewById(R.id.floatingAddBtn);

        databaseHelper = new DatabaseHelper(this);
        dialog = new Dialog(this);

        floatingAddBtn.setOnClickListener(v -> addData());

        addDataintoLinearlayout();
//        createPdf();
    }

    public void addData() {
        dialog.setContentView(R.layout.entry_dialog_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        EditText name_editText, email_editText, contact_editText;
        Button savebtn, cancelbtn;

        name_editText = dialog.findViewById(R.id.name_editText);
        email_editText = dialog.findViewById(R.id.email_editText);
        contact_editText = dialog.findViewById(R.id.contact_editText);

        savebtn = dialog.findViewById(R.id.savebtn);
        cancelbtn = dialog.findViewById(R.id.cancelbtn);

        savebtn.setOnClickListener(v -> {

            String currentDate = new SimpleDateFormat("EEE, d MMM, yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date());

            boolean checkInsertUserData = databaseHelper.insertUserData(name_editText.getText().toString(),
                    email_editText.getText().toString(),
                    contact_editText.getText().toString(),
                    currentDate, currentTime);

            if (checkInsertUserData) {
                Toast.makeText(ViewData.this, "Data Inserted Successfully.", Toast.LENGTH_LONG).show();
                name_editText.setText("");
                email_editText.setText("");
                contact_editText.setText("");
            } else {
                Toast.makeText(ViewData.this, "Data Not Inserted!!!", Toast.LENGTH_LONG).show();
            }
            linearLayout.removeAllViews();
            addDataintoLinearlayout();
            dialog.dismiss();
        });
        cancelbtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addDataintoLinearlayout() {
        Cursor result = databaseHelper.getUserData();
        if (result.getCount() == 0) {
            Toast.makeText(ViewData.this, "Data Not Exists.", Toast.LENGTH_SHORT).show();
        } else {
            while (result.moveToNext()) {
                addView(result.getString(0),
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getString(5));
            }
        }

    }

    public void addView(String id, String name, String email, String contact, String date, String time) {
        View view = getLayoutInflater().inflate(R.layout.card_view, null);

        TextView current_Date = view.findViewById(R.id.current_Date);
        TextView current_Time = view.findViewById(R.id.current_Time);
        TextView id_textview = view.findViewById(R.id.id_textview);
        TextView name_textview = view.findViewById(R.id.name_textview);
        TextView email_textview = view.findViewById(R.id.email_textview);
        TextView contact_textview = view.findViewById(R.id.contact_textview);

        Button updatebtn, deletebtn;

        ImageButton callButton = view.findViewById(R.id.callBtn);

        updatebtn = view.findViewById(R.id.update);
        deletebtn = view.findViewById(R.id.delete);

        current_Date.setText(date);
        current_Time.setText(time);

        id_textview.setText(id);
        name_textview.setText(name);
        email_textview.setText(email);
        contact_textview.setText(contact);
        linearLayout.addView(view, 0);

//        profileImageView.setOnClickListener(v ->selectImage());

        updatebtn.setOnClickListener(v -> updateData(id_textview.getText().toString()));

        deletebtn.setOnClickListener(v -> deleteData(id_textview.getText().toString()));

        callButton.setOnClickListener(v -> {

            String number = contact_textview.getText().toString();

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+number));
            startActivity(intent);

        });
    }

    private void updateData(String id) {

        dialog.setContentView(R.layout.entry_dialog_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        EditText name_editText, email_editText, contact_editText;
        Button savebtn, cancelbtn;

        name_editText = dialog.findViewById(R.id.name_editText);
        email_editText = dialog.findViewById(R.id.email_editText);
        contact_editText = dialog.findViewById(R.id.contact_editText);

        savebtn = dialog.findViewById(R.id.savebtn);
        savebtn.setText("Update");
        cancelbtn = dialog.findViewById(R.id.cancelbtn);

        Cursor result = databaseHelper.searchUserData(id);
        result.moveToFirst();
        name_editText.setText(result.getString(1));
        email_editText.setText(result.getString(2));
        contact_editText.setText(result.getString(3));

        savebtn.setOnClickListener(v -> {
                boolean checkUpdateUserData = databaseHelper.updateUserData(id, name_editText.getText().toString(),
                        email_editText.getText().toString(),
                        contact_editText.getText().toString());
                if (checkUpdateUserData) {
                    Toast.makeText(ViewData.this, "Data Updated Successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewData.this, "Data Not Update!!!", Toast.LENGTH_SHORT).show();
                }

            //Reload Page//
//            finish();
//            startActivity(getIntent());
            linearLayout.removeAllViews();
            addDataintoLinearlayout();

            dialog.dismiss();
        });

        cancelbtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void deleteData(String id) {

        dialog.setContentView(R.layout.delete_dialog_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        TextView name_Text, email_Text, contact_Text;
        Button deletebtn, cancelbtn;

        name_Text = dialog.findViewById(R.id.name_Text);
        email_Text = dialog.findViewById(R.id.email_Text);
        contact_Text = dialog.findViewById(R.id.contact_Text);

        deletebtn = dialog.findViewById(R.id.deletebtn);
        cancelbtn = dialog.findViewById(R.id.cancelbtn);

        Cursor result = databaseHelper.searchUserData(id);
        result.moveToFirst();
        name_Text.setText(result.getString(1));
        email_Text.setText(result.getString(2));
        contact_Text.setText(result.getString(3));

        deletebtn.setOnClickListener(v -> {
            boolean checkDeleteUserData = databaseHelper.deleteUserData(id);
            if (checkDeleteUserData) {
                Toast.makeText(ViewData.this, "Data Deleted Successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ViewData.this, "Data Not Deleted!!!", Toast.LENGTH_SHORT).show();
            }

            //Reload Page//
//            startActivity(getIntent());
            linearLayout.removeAllViews();
            addDataintoLinearlayout();

            dialog.dismiss();
        });

        cancelbtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    //Pdf Creator Function//
    private void createPdf(){
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "myContactList.pdf");



    }

}