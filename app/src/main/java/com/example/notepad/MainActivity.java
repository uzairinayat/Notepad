package com.example.notepad;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fab;

    // Views for detailed note view
    private AlertDialog detailedNoteDialog;
    private TextView textViewNoteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        noteList = databaseHelper.getAllNotes();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(noteList, new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(int position) {
                showDetailedNoteDialog(position);
            }
        });
        recyclerView.setAdapter(noteAdapter);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNoteDialog();
            }
        });
    }

    private void showAddNoteDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        final EditText editTextNote = dialogView.findViewById(R.id.noteTitle);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editTextNote.getText().toString();
                if (!content.isEmpty()) {
                    databaseHelper.addNote(content);
                    displayNotes(); // Refresh notes after adding a new note
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void displayNotes() {
        noteList.clear();
        noteList.addAll(databaseHelper.getAllNotes());
        noteAdapter.notifyDataSetChanged();
    }

    private void showDetailedNoteDialog(int position) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.display, null);
        textViewNoteContent = dialogView.findViewById(R.id.textViewNoteContent);

        // Set note content
        String content = noteList.get(position).getContent();
        textViewNoteContent.setText(content);

        detailedNoteDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Optional: Handle OK button click if needed
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dismiss the detailed note dialog if it's showing to prevent window leak
        if (detailedNoteDialog != null && detailedNoteDialog.isShowing()) {
            detailedNoteDialog.dismiss();
        }
    }
}
