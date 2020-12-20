package com.digitalnx.digitalnxclient.ui.noteView;

import java.io.Serializable;

// The purpose of this class is to permit "noteid" to be nullable (by internally being declared as an Integer).
public class NoteIdType implements Serializable {
    Integer Id;
    public NoteIdType(Integer id) {
        Id = id;
    }
    public Integer getData() {
        return Id;
    }
}
