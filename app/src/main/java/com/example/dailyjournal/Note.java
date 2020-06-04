package com.example.dailyjournal;

public class Note {
    private Integer NoteId;
    private String Title;
    private String NoteDate;
    private String NotePath;
    private String ImagePath;

    public Note(String title, String noteDate, String notePath, String imagePath) {
        Title = title;
        NoteDate = noteDate;
        NotePath = notePath;
        ImagePath = imagePath;
    }

    public Note() {
    }

    public Note(String title, String noteDate, String notePath) {
        Title = title;
        NoteDate = noteDate;
        NotePath = notePath;
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

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }
}
