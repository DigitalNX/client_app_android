package com.digitalnx.digitalnxclient.ui.noteView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.digitalnx.digitalnxclient.R;
import com.digitalnx.digitalnxclient.networkUtils.NetworkUtils;
import com.digitalnx.digitalnxclient.ui.notes.NotesFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewNoteFragment extends Fragment {

    private ViewNoteViewModel mViewModel;
    private Integer noteId;
    private String noteTitle, noteContent;

    private RequestQueue queue;
    public static ViewNoteFragment newInstance() {
        return new ViewNoteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            NoteIdType id = (NoteIdType) bundle.getSerializable("noteId");
            noteId = id.getData();
            noteTitle = bundle.getString("noteTitle");
            noteContent = bundle.getString("noteContent");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_view_note, container, false);
        queue = Volley.newRequestQueue(requireActivity());
        EditText title = (EditText) rootView.findViewById(R.id.note_title);
        EditText content = (EditText) rootView.findViewById(R.id.note_content);

        title.setText(noteTitle);
        content.setText(noteContent);

        Button updateButton = (Button) rootView.findViewById(R.id.note_view_update_button);
        Button cancelButton = (Button) rootView.findViewById(R.id.note_view_cancel_button);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteTitle = title.getText().toString();
                noteContent = content.getText().toString();
                updateNote();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ViewNoteViewModel.class);
        // TODO: Use the ViewModel
    }

    private void updateNote() {
        String route;
        if(noteId == null) {
            route = "/api/note/new";
        } else {
            route = "/api/note/" + String.valueOf(noteId) + "/update";
        }
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("title", noteTitle);
            jsonBody.put("content", noteContent);
            final String requestBody = jsonBody.toString();
            NetworkUtils.POSTRequestWithToken(response -> {
                try {
                    JSONObject result = new JSONObject(response);
                    int statusCode = (int) result.get("result");
                    if (statusCode == 200) {
                        Toast.makeText(getActivity(), "Note updated successfully.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, requestBody, route, queue);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void cancel() {
        NotesFragment notesFragment = new NotesFragment();
        FragmentManager fm =  getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.nav_host_fragment, notesFragment).addToBackStack(null);
        ft.commit();
    }
}