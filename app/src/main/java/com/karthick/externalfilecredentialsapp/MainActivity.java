package com.karthick.externalfilecredentialsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    private static final String MYTAG = "external storage";
    EditText username, password;
    Button login;

    String usrnm, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(MYTAG, "onCreate: ");

        username = findViewById(R.id.edtUsername);
        password = findViewById(R.id.edtPassword);
        login = findViewById(R.id.button);

        //CHECKING PERMISSIONS WHILE APP IS LAUNCHED
        if ((ContextCompat.checkSelfPermission(this, "Manifest.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, "Manifest.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 202);
        }

        //CHECK IF ALREADY CREDENTIALS FILE EXISTING
        readExternalStorage();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usrnm = username.getText().toString();
                pwd = password.getText().toString();
                checkExternalStorage();
            }
        });
    }

    public void checkExternalStorage() {

        String state = Environment.getExternalStorageState();

        //CHECK FOR SD CARD
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/downloads");

            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, "Credentials.txt");
            try {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.println(usrnm);
                pw.print(pwd);
                pw.flush();
                pw.close();
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(MYTAG, "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(MYTAG, "File written to " + file);
            Toast.makeText(MainActivity.this, "File written to " + file, Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "SD Card not found", Toast.LENGTH_LONG).show();
        }
    }

    public void readExternalStorage() {

        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/downloads");
        File file = new File(dir, "Credentials.txt");
        String un;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            while ((un = bufferedReader.readLine()) != null) {
                stringBuffer.append(un + "\n");
            }

            //SEPARATING USERNAME AND PASSWORD USING NEW LINE CHARACTER
            un = stringBuffer.toString();
            String lines[] = un.split("\n");

            //SETTING IT TO THE USERNAME AND PASSWORD FIELDS
            username.setText(lines[0].toString());
            password.setText(lines[1].toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
