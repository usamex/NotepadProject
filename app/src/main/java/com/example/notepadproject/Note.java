package com.example.notepadproject;

public class Note {
    private int noteID;
    private String noteName;
    private String noteDesc;
    private int notePriority;
    private String noteLocation;
    private String noteDate;
    private Long noteAlarmDate;
    private String noteHtmlDesc;
    public Note(int noteID, String noteName, String noteDesc, int notePriority, String noteLocation,
                String noteDate, Long noteAlarmDate, String noteHtmlDesc){
        this.noteID = noteID;
        this.noteName = noteName;
        this.noteDesc = noteDesc;
        this.notePriority = notePriority;
        this.noteLocation = noteLocation;
        this.noteDate = noteDate;
        this.noteAlarmDate = noteAlarmDate;
        this.setNoteHtmlDesc(noteHtmlDesc);
    }
    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteDesc() {
        return noteDesc;
    }

    public void setNoteDesc(String noteDesc) {
        this.noteDesc = noteDesc;
    }

    public int getNotePriority() {
        return notePriority;
    }

    public void setNotePriority(int notePriority) {
        this.notePriority = notePriority;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public Long getNoteAlarmDate() {
        return noteAlarmDate;
    }

    public void setNoteAlarmDate(Long noteAlarmDate) {
        this.noteAlarmDate = noteAlarmDate;
    }

    public String getNoteLocation() {
        return noteLocation;
    }

    public void setNoteLocation(String noteLocation) {
        this.noteLocation = noteLocation;
    }

    public String getNoteHtmlDesc() {
        return noteHtmlDesc;
    }

    public void setNoteHtmlDesc(String noteHtmlDesc) {
        this.noteHtmlDesc = noteHtmlDesc;
    }
}
