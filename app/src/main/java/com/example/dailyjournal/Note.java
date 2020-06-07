package com.example.dailyjournal;

import android.graphics.Bitmap;

class Note {
    private Integer NoteId;
    private String Title;
    private String NoteDate;
    private String NotePath;
    private Bitmap ImagePath;
    private boolean HaveImage;

    Note() {
    }

    Note(String title, String noteDate, String notePath, Bitmap imagePath, boolean haveImage) {
        Title = title;
        NoteDate = noteDate;
        NotePath = notePath;
        ImagePath = imagePath;
        HaveImage = haveImage;
    }

    Note(String title, String noteDate, String notePath, boolean haveImage) {
        Title = title;
        NoteDate = noteDate;
        NotePath = notePath;
        HaveImage = haveImage;
    }

    Integer getNoteId() {
        return NoteId;
    }

    void setNoteId(Integer noteId) {
        NoteId = noteId;
    }

    String getTitle() {
        return Title;
    }

    void setTitle(String title) {
        Title = title;
    }

    String getNoteDate() {
        return NoteDate;
    }

    void setNoteDate(String noteDate) {
        NoteDate = noteDate;
    }

    String getNotePath() {
        return NotePath;
    }

    void setNotePath(String notePath) {
        NotePath = notePath;
    }

    Bitmap getImagePath() {
        return ImagePath;
    }

    void setImagePath(Bitmap imagePath) {
        ImagePath = imagePath;
    }

    boolean isHaveImage() {
        return HaveImage;
    }

    void setHaveImage(boolean haveImage) {
        HaveImage = haveImage;
    }
}
