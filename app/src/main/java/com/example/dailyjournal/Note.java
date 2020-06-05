package com.example.dailyjournal;

import android.graphics.Bitmap;

public class Note {
    private Integer NoteId;
    private String Title;
    private String NoteDate;
    private String NotePath;
    private Bitmap ImagePath;
    private boolean HaveImage;

    public Note() {
    }

    public Note(String title, String noteDate, String notePath, Bitmap imagePath, boolean haveImage) {
        Title = title;
        NoteDate = noteDate;
        NotePath = notePath;
        ImagePath = imagePath;
        HaveImage = haveImage;
    }

    public Note(String title, String noteDate, String notePath, boolean haveImage) {
        Title = title;
        NoteDate = noteDate;
        NotePath = notePath;
        HaveImage = haveImage;
    }

    public Integer getNoteId() {
        return NoteId;
    }

    public void setNoteId(Integer noteId) {
        NoteId = noteId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getNoteDate() {
        return NoteDate;
    }

    public void setNoteDate(String noteDate) {
        NoteDate = noteDate;
    }

    public String getNotePath() {
        return NotePath;
    }

    public void setNotePath(String notePath) {
        NotePath = notePath;
    }

    public Bitmap getImagePath() {
        return ImagePath;
    }

    public void setImagePath(Bitmap imagePath) {
        ImagePath = imagePath;
    }

    public boolean isHaveImage() {
        return HaveImage;
    }

    public void setHaveImage(boolean haveImage) {
        HaveImage = haveImage;
    }
}
