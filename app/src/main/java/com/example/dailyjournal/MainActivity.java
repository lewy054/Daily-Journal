package com.example.dailyjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    LinearLayout notes_row, horizontalLayout;
    DBController controller;
    int width;
    public static int noteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        width = displayMetrics.widthPixels;
        controller = new DBController(this);
        controller.init();
        FloatingActionButton fab = findViewById(R.id.fab);
        notes_row = findViewById(R.id.notes_row);
        noteId = 0;
        getNotesList();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent noteActivity = new Intent(getApplicationContext(), NoteActivity.class);
                startActivity(noteActivity);
            }
        });
    }

    private void createRow(Note note) {
        horizontalLayout = new LinearLayout(this);
        LinearLayout.LayoutParams horizontalParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalParams.setMargins(10, 20, 10, 20);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setLayoutParams(horizontalParams);

        createTextView(note);

        notes_row.addView(horizontalLayout);
    }

    private void createTextView(final Note note) {
        TextView textView = new TextView(this);
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
        //textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.tags_rounded_corners);
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                noteId = note.getNoteId();
                Intent noteActivity = new Intent(getApplicationContext(), NoteActivity.class);
                startActivity(noteActivity);
            }
        });

        notes_row.addView(textView);
    }


    @Override
    public void onRestart() {
        super.onRestart();
        noteId = 0;
        getNotesList();
    }

    void getNotesList() {
        List<Note> selectedNotes = controller.selectAllNotes();
        notes_row.removeAllViews();
        for (Note note : selectedNotes) {
            createRow(note);
        }
    }

    public int convertDpToPixel(int dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


}
