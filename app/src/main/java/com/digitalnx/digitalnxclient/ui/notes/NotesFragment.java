package com.digitalnx.digitalnxclient.ui.notes;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.digitalnx.digitalnxclient.R;
import com.digitalnx.digitalnxclient.ui.noteView.NoteIdType;
import com.digitalnx.digitalnxclient.ui.noteView.ViewNoteFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.digitalnx.digitalnxclient.networkUtils.NetworkUtils;

public class NotesFragment extends Fragment {
    private NotesViewModel mViewModel;
    private JSONArray notesJSON;

    private LinearLayout articleLayout;
    private RequestQueue queue;

    public static NotesFragment newInstance() {
        return new NotesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_notes, container, false);
        articleLayout = (LinearLayout) rootView.findViewById(R.id.notes);
        queue = Volley.newRequestQueue(requireActivity());
        grabNotes();

        FloatingActionButton fab = rootView.findViewById(R.id.new_note_fab);
        fab.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            NoteIdType id = new NoteIdType(null);

            bundle.putSerializable("noteId", id);
            bundle.putString("noteTitle", null);
            bundle.putString("noteContent", null);
            Fragment viewNoteFragment = new ViewNoteFragment();
            viewNoteFragment.setArguments(bundle);

            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.nav_host_fragment, viewNoteFragment);
            ft.commit();
        });
        return rootView;
    }

    private void grabNotes() {
        NetworkUtils.GETRequestWithToken(response -> {
            try  {
                notesJSON = new JSONArray(response);
                renderNotes();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, "/api/notes", queue);
    }

    private void renderNotes() {
        try {
            articleLayout.removeAllViews();
            for (int i = 0; i < notesJSON.length(); i++) {
                JSONObject jsonObj = notesJSON.getJSONObject(i);
                int noteId = (int) jsonObj.get("id");

                LinearLayout noteLayout = new LinearLayout(getActivity());
                noteLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                noteLayout.setOrientation(LinearLayout.VERTICAL);
                noteLayout.setPadding(10, 20, 0, 10);
                noteLayout.setId(noteId);

                TextView noteTitle = new TextView(getActivity());
                noteTitle.setText(jsonObj.get("title").toString());
                noteTitle.setTextColor(Color.parseColor("#000000"));
                noteTitle.setTypeface(null, Typeface.BOLD);
                noteTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                noteTitle.setPadding(5, 50, 0, 7);

                TextView noteBody = new TextView(getActivity());
                String noteContent = jsonObj.get("content").toString();
                if (noteContent.length() > 200) {
                    String noteContentSub = noteContent.substring(0, 200) + "...";
                    noteBody.setText(noteContentSub);
                } else {
                    noteBody.setText(noteContent);
                }

                noteBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                noteBody.setPadding(5, 0, 0, 7);
                noteBody.setTextColor(Color.parseColor("#666666"));

                noteLayout.addView(noteTitle);
                noteLayout.addView(noteBody);

                // View and Delete buttons
                LinearLayout buttons = new LinearLayout(getActivity());
                Button viewButton = new Button(getActivity());
                viewButton.setText("View");
                viewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewNote(noteId);
                    }
                });

                Button delButton = new Button(getActivity());
                delButton.setText("Delete");
                delButton.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                        .setTitle("Note Delete")
                        .setMessage("Do you really wish to delete the note?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteNote(noteId);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show());
                delButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E50000")));

                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                buttonParams.gravity = Gravity.CENTER;
                viewButton.setLayoutParams(buttonParams);
                delButton.setLayoutParams(buttonParams);

                buttons.addView(delButton);
                buttons.addView(viewButton);

                articleLayout.addView(noteLayout);
                articleLayout.addView(buttons);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void viewNote(int noteId) {
        try {
            for(int i = 0; i < notesJSON.length(); i++) {
                JSONObject jsonObj = notesJSON.getJSONObject(i);
                if((int) jsonObj.get("id") == noteId ) {
                    Bundle bundle = new Bundle();
                    NoteIdType id = new NoteIdType(noteId);
                    bundle.putSerializable("noteId", id);
                    bundle.putString("noteTitle", jsonObj.get("title").toString());
                    bundle.putString("noteContent", jsonObj.get("content").toString());
                    Fragment viewNoteFragment = new ViewNoteFragment();
                    viewNoteFragment.setArguments(bundle);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.nav_host_fragment, viewNoteFragment);
                    ft.commit();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void deleteNote(int noteId) {
        NetworkUtils.GETRequestWithToken(response -> {
            try {
                JSONObject result = new JSONObject(response);
                int statusCode = (int) result.get("result");
                if (statusCode == 200 && notesJSON != null) {
                    Toast.makeText(getActivity(), "Note deleted successfully.", Toast.LENGTH_SHORT).show();
                    JSONArray tmp = new JSONArray();
                    for (int i = 0; i < notesJSON.length(); i++) {
                        JSONObject jsonObj = notesJSON.getJSONObject(i);
                        if ((int) jsonObj.get("id") != noteId) {
                            tmp.put(notesJSON.get(i));
                        }
                    }
                    notesJSON = tmp;
                    renderNotes();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, "/api/note/" + Integer.toString(noteId) + "/remove", queue);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NotesViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            grabNotes();
        }
        return false;
    }
}