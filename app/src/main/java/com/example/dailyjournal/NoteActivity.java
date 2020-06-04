package com.example.dailyjournal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    DBController controller;
    int noteId;
    EditText titleEditText, dataEditText;
    TextView dateTextView;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        dataEditText = findViewById(R.id.dataEditText);
        titleEditText = findViewById(R.id.titleEditText);
        dateTextView = findViewById(R.id.dateTextView);


        dataEditText.setText("");
        titleEditText.setText("");
        dateTextView.setText("");
        controller = new DBController(this);
        noteId = MainActivity.noteId;
        if (noteId != 0) {
            note = controller.selectNote(noteId);
            loadFile();

        }
    }

    public void saveButtonClick(View view) {
        if (!verifyStoragePermissions(this)) {
            return;
        }


        String note = dataEditText.getText().toString();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date aDate = new Date();
        String date = dateFormat.format(aDate);


        String fileName = date.toString();
        fileName = fileName.replaceAll(" ", "_").toLowerCase();
        fileName = fileName + ".txt";

        //String dirName = Environment.getExternalStorageDirectory() + "/" + R.string.app_name + "/";
        String dirName = Environment.getExternalStorageDirectory() + "/DailyJournal/";
        File myDir = new File(dirName);
        /*if directory doesn't exist, create it*/
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        FileOutputStream fOut;
        try {
            fOut = openFileOutput(fileName, MODE_PRIVATE); // ustanowienie strumienia zapisu

            fOut.write(note.getBytes()); // zapis wymaga obsługi wyjątku, tą obsługę można wygenerować za pomocą środowiska

            fOut.close(); // zamknięcie procesu - WAŻNE, nie ignorować
            Toast.makeText(this, "Zapisano w:" + getFilesDir() + "/" + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        //without image
        controller.addNote(new Note(titleEditText.getText().toString(), date, fileName));

    }


    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }
        return true;
    }

    public void loadFile() {
        try {
            FileInputStream fIn = openFileInput(note.getNotePath());
            InputStreamReader inStreamReader = new InputStreamReader(fIn);
            BufferedReader bufReader = new BufferedReader(inStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            dataEditText.setText(sb.toString());

            bufReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        titleEditText.setText(note.getTitle());
        dateTextView.setText(note.getNoteDate());

    }
}
