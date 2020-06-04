package com.example.dailyjournal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.CompoundButtonCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout notes_row;
    private LinearLayout horizontalLayout;
    private DBController controller;
    private int i = 0;
    private int checkCount = 0;
    public static int noteId;
    private final ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private final ArrayList<Note> notesToDelete = new ArrayList<>();
    FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new DBController(this);
        controller.init();
        notes_row = findViewById(R.id.notes_row);
        floatingActionButton = findViewById(R.id.fab);
        noteId = 0;
        getNotesList();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCount == 0) {
                    Intent noteActivity = new Intent(getApplicationContext(), NoteActivity.class);
                    startActivity(noteActivity);
                } else {
                    showDialog();
                }
            }
        });
    }

    private void createRow(Note note) {
        horizontalLayout = new LinearLayout(this);
        LinearLayout.LayoutParams horizontalParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setBackgroundResource(R.drawable.tags_rounded_corners);
        horizontalParams.setMargins(10, 20, 10, 20);
        horizontalLayout.setLayoutParams(horizontalParams);
        horizontalLayout.setGravity(Gravity.CENTER);
        createTextView(note);
        createCheckbox();

        notes_row.addView(horizontalLayout);
    }

    private void createTextView(final Note note) {
        final TextView textView = new TextView(this);
        textView.setSingleLine(false);
        int titleSize = 24;
        int dateSize = 13;


        String title = note.getTitle();
        String date = note.getNoteDate();

        SpannableString span1 = new SpannableString(title);
        span1.setSpan(new AbsoluteSizeSpan(convertDpToPixel(titleSize)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(date);
        span2.setSpan(new AbsoluteSizeSpan(convertDpToPixel(dateSize)), 0, date.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        CharSequence finalText = TextUtils.concat(span1, "\n", span2);
        textView.setText(finalText);

        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setId(i);
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                noteId = note.getNoteId();
                Intent noteActivity = new Intent(getApplicationContext(), NoteActivity.class);
                startActivity(noteActivity);
            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                noteId = note.getNoteId();
                CheckBox checkBox = checkBoxes.get(textView.getId());

                if (checkBox.getVisibility() == View.VISIBLE) {
                    checkBox.setVisibility(View.GONE);
                    notesToDelete.remove(note);
                    checkCount--;
                    if (checkCount == 0) {
                        floatingActionButton.setImageResource(R.drawable.ic_add_black);
                    }
                } else {
                    checkBox.setVisibility(View.VISIBLE);
                    floatingActionButton.setImageResource(R.drawable.ic_delete_black);
                    notesToDelete.add(note);
                    checkCount++;
                }
                return true;
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;
        textView.setLayoutParams(params);
        horizontalLayout.addView(textView);
        i++;
    }

    private void createCheckbox() {
        final CheckBox checkBox = new CheckBox(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 6.0f;

        checkBox.setLayoutParams(params);
        checkBox.setVisibility(View.GONE);
        checkBox.setChecked(true);
        CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(getResources().getColor(R.color.colorGray)));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!checkBox.isChecked()) {
                    checkBox.setVisibility(View.GONE);
                }
            }
        });

        horizontalLayout.addView(checkBox);
        checkBoxes.add(checkBox);
    }


    @Override
    public void onRestart() {
        super.onRestart();
        noteId = 0;
        getNotesList();
    }

    private void getNotesList() {
        List<Note> selectedNotes = controller.selectAllNotes();
        notes_row.removeAllViews();
        for (Note note : selectedNotes) {
            createRow(note);
        }
    }

    private int convertDpToPixel(int dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Potwierdź");
        builder.setMessage("Czy jesteś pewny, że chcesz usunąć zaznaczone notatki?");
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Delete all selected notes
                deleteNotes();
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

    private void deleteNotes() {
        for (Note note : notesToDelete) {
            //delete txt file
            String dir = getFilesDir().getAbsolutePath();
            File f0 = new File(dir, note.getNotePath());
            boolean d0 = f0.delete();

            //delete image
            //TODO

            //delete from database
            controller.removeNote(note);
        }
        floatingActionButton.setImageResource(R.drawable.ic_add_black);
        Toast.makeText(this, "Wpisy usunięte", Toast.LENGTH_SHORT).show();

        onRestart();
    }


}
