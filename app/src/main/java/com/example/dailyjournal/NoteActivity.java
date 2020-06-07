package com.example.dailyjournal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    //Camera Permissions
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;

    private DBController controller;
    private EditText titleEditText;
    private EditText dataEditText;
    private TextView dateTextView;
    private Note note;
    private LinearLayout imageLayout;
    private ImageView imageView;
    private int dimension;
    private int noteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        dataEditText = findViewById(R.id.dataEditText);
        titleEditText = findViewById(R.id.titleEditText);
        dateTextView = findViewById(R.id.dateTextView);
        imageLayout = findViewById(R.id.imageLayout);
        dateTextView.setVisibility(View.GONE);
        dataEditText.setText("");
        titleEditText.setText("");
        dateTextView.setText("");
        imageLayout.removeAllViews();

        controller = new DBController(this);
        dimension = convertDpToPixel(300);


        noteId = MainActivity.noteId;

        if (noteId != 0) {
            note = controller.selectNote(noteId);
            if (note.isHaveImage()) {
                imageView = new ImageView(this);
                imageView.setImageBitmap(note.getImagePath());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimension, dimension);
                imageView.setLayoutParams(params);
                imageLayout.addView(imageView);
            } else {
                createButtonsForImages();
            }
            loadFile();
        } else {
            createButtonsForImages();
        }
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    public void saveButtonClick(View view) {
        String title = titleEditText.getText().toString();
        String date;
        if(title.equals("") || title.equals(null)){
            Toast.makeText(this, R.string.emptyTitle, Toast.LENGTH_LONG).show();
            return;
        }
        if (noteId != 0) {
            date = note.getNoteDate();
            deleteFile();
        } else {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date aDate = new Date();
            date = dateFormat.format(aDate);
        }

        String noteText = dataEditText.getText().toString();

        String fileName = date;
        fileName = fileName.replaceAll(" ", "_").toLowerCase();
        fileName = fileName + ".txt";

        String dirName = Environment.getExternalStorageDirectory() + "/DailyJournal/";
        File myDir = new File(dirName);
        /*if directory doesn't exist, create it*/
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        FileOutputStream fOut;
        try {
            fOut = openFileOutput(fileName, MODE_PRIVATE);
            fOut.write(noteText.getBytes());
            fOut.close();
            Toast.makeText(this, R.string.Saved, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        Bitmap imagePath;
        if (noteId != 0) {
            //user edited note so update it
            if (note.isHaveImage()) {
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                imagePath = drawable.getBitmap();
                note.setImagePath(imagePath);
            }
            note.setTitle(titleEditText.getText().toString());
            note.setNoteDate(date);
            note.setNotePath(fileName);
            controller.updateNote(note);
        } else {
            //create new note
            if (imageView != null) {
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                imagePath = drawable.getBitmap();
                controller.addNote(new Note(title, date, fileName, imagePath, true));
            } else {
                controller.addNote(new Note(title, date, fileName, false));
            }
        }
    }

    private void loadFile() {
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
        dateTextView.setVisibility(View.VISIBLE);

    }


    private void createButtonsForImages() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);

        Button addPhoto = new Button(this);
        addPhoto.setText(R.string.addPhoto);
        addPhoto.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        addPhoto.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
        addPhoto.setLayoutParams(params);
        addPhoto.setBackgroundResource(R.drawable.tags_rounded_corners);

        final Button makePhoto = new Button(this);
        makePhoto.setText(R.string.makePhoto);
        makePhoto.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        makePhoto.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
        makePhoto.setLayoutParams(params);
        makePhoto.setBackgroundResource(R.drawable.tags_rounded_corners);

        addPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getPhoto();
            }
        });

        makePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                makePhoto();
            }
        });


        imageLayout.addView(addPhoto);
        imageLayout.addView(makePhoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imageView = new ImageView(this);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                Bundle extras = data.getExtras();
                Bitmap image = (Bitmap) extras.get("data");
                imageView.setImageBitmap(image);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimension, dimension);
                imageView.setLayoutParams(params);

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            try {
                Uri selectedImage = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                imageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimension, dimension);
                imageView.setLayoutParams(params);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        }
        if (imageView.getDrawable() != null) {
            if (noteId != 0) {
                note.setHaveImage(true);
            }
            imageLayout.removeAllViews();
            imageLayout.addView(imageView);
        }
    }

    private void makePhoto() {
        if (!verifyCameraPermissions(this)) {
            Toast.makeText(this, R.string.tryAgain, Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        }
    }

    private void getPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectPhoto)), GALLERY_REQUEST);
    }

    private int convertDpToPixel(int dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.deleteImageConfirm);
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                deleteImage();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteImage() {
        imageLayout.removeAllViews();
        if (noteId != 0) {
            note.setHaveImage(false);
        }
        createButtonsForImages();
    }

    private void deleteFile() {
        String dir = getFilesDir().getAbsolutePath();
        File f0 = new File(dir, note.getNotePath());
        boolean d0 = f0.delete();
    }


    private boolean verifyCameraPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST);
            return false;
        }
        return true;
    }


}
