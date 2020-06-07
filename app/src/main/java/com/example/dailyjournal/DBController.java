package com.example.dailyjournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

class DBController extends SQLiteOpenHelper {

    DBController(Context context) {
        super(context, "notes.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table if not exists  note(" +
                        "idNote integer primary key autoincrement," +
                        "title text," +
                        "noteDate text," +
                        "notePath text," +
                        "haveImage boolean," +
                        "imagePath blob);" + "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    void init() {
        SQLiteDatabase db = getWritableDatabase();
        //removeAll(db);
        onCreate(db);
    }

    public void removeAll(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS note");
    }


    void addNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("title", note.getTitle());
        content.put("noteDate", note.getNoteDate());
        content.put("notePath", note.getNotePath());
        content.put("haveImage", note.isHaveImage());
        if (note.isHaveImage()) {
            content.put("imagePath", getBitmapAsByteArray(note.getImagePath()));
        }
        db.insertOrThrow("note", null, content);
    }


    List<Note> selectAllNotes() {
        List<Note> students = new LinkedList<>();
        String[] columns = {"idNote", "title", "noteDate", "notePath"};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("note", columns, null, null, null, null, null);
        while (cursor.moveToNext()) { //
            Note note = new Note();
            note.setNoteId(cursor.getInt(0));
            note.setTitle(cursor.getString(1));
            note.setNoteDate(cursor.getString(2));
            note.setNotePath(cursor.getString(3));
            students.add(note);
        }
        cursor.close();
        return students;
    }

    Note selectNote(int id) {
        Note note = new Note();
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"idNote", "title", "noteDate", "notePath", "haveImage", "imagePath"};
        String[] args = {id + ""};
        Cursor cursor = db.query("note", columns, " idNote=?", args, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            note.setNoteId(cursor.getInt(0));
            note.setTitle(cursor.getString(1));
            note.setNoteDate(cursor.getString(2));
            note.setNotePath(cursor.getString(3));
            note.setHaveImage(cursor.getInt(4) > 0);
            if (cursor.getInt(4) > 0) {
                byte[] image = cursor.getBlob(5);
                note.setImagePath(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
            cursor.close();
        }
        return note;
    }

    void updateNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("title", note.getTitle());
        content.put("noteDate", note.getNoteDate());
        content.put("notePath", note.getNotePath());
        content.put("haveImage", note.isHaveImage());
        if (note.isHaveImage()) {
            content.put("imagePath", getBitmapAsByteArray(note.getImagePath()));
        } else {
            content.put("imagePath", (byte[]) null);
        }
        String[] args = {note.getNoteId() + ""};
        db.update("note", content, "idNote=?", args);

    }

    void removeNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        String table = "note";
        String whereClause = "idNote=?";
        String[] whereArgs = new String[]{String.valueOf(note.getNoteId())};
        db.delete(table, whereClause, whereArgs);
    }

    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }


}
