package com.example.duret.lilia_alize_demo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.duret.lilia_alize_demo.speakerslist.Speaker;
import com.example.duret.lilia_alize_demo.speakerslist.SpeakerListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import AlizeSpkRec.AlizeException;
import AlizeSpkRec.IdAlreadyExistsException;

public class NewSpeakerActivity extends BaseActivity {

    private EditText addSpeakerEditText;
    private Button addSpeakerButton;
    private SpeakerListAdapter list;
    private TextView noSpeakers;
    private String speakerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_speaker);
        setTitle(R.string.addspeaker_activity_name);

        setupListViewAdapter();

        addSpeakerEditText = findViewById(R.id.addSpeakerEditText);
        addSpeakerButton = findViewById(R.id.addSpeakerButton);
        noSpeakers = findViewById(R.id.no_speakers);
        noSpeakers.setVisibility(View.INVISIBLE);

        addSpeakerEditText.addTextChangedListener(addSpeakerEditTextListener);
        addSpeakerButton.setOnClickListener(addSpeakerButtonListener);

        try {
            clearAndFillSpeakersList();
            updateListViewContent();
        } catch (AlizeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInit(int i) {
        say(getResources().getString(R.string.new_speaker_text));
    }

    @Override
    public void onResume() {
        super.onResume();
        addSpeakerEditText.setText("");
    }

    public void updateSpeakerOnClickHandler(View v) {
        try {
            Speaker speaker = (Speaker)v.getTag();
            alizeSystem.adaptSpeakerModel(speaker.getName());
            alizeSystem.resetAudio();
            alizeSystem.resetFeatures();
            makeToast(getString(R.string.update_speakermodel)+" '"+speaker.getName()+"'.");
            startDialog();

        } catch (AlizeException e) {
            e.printStackTrace();
        }
    }

    public void removeSpeakerOnClickHandler(View v) {
        Speaker itemToRemove = (Speaker)v.getTag();
        String speakerId = itemToRemove.getName();
        try {
            if (!speakerId.isEmpty()) {
                alizeSystem.removeSpeaker(speakerId);
            }
            list.remove(itemToRemove);
            updateListViewContent();
            if(!speakerAlreadyExist(speakerName)) {
                addSpeakerButton.setEnabled(true);
            }

        } catch (AlizeException e) {
            System.out.println(e.getMessage());
        }
    }

    private TextWatcher addSpeakerEditTextListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            speakerName = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                if (speakerName.isEmpty() || speakerAlreadyExist(speakerName)) {
                    addSpeakerButton.setEnabled(false);
                }
                else {
                    addSpeakerButton.setEnabled(true);
                }
            } catch (AlizeException e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener addSpeakerButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            try {
                if (speakerAlreadyExist(speakerName)) {
                    makeToast(getString(R.string.speakerId_already_exists));
                    return;
                }

                alizeSystem.createSpeakerModel(speakerName);
                alizeSystem.resetAudio();
                alizeSystem.resetFeatures();
                addSpeakerButton.setEnabled(false);

                clearAndFillSpeakersList();
                updateListViewContent();
                makeToast(getString(R.string.add_speakername_start)+" '"+speakerName+"' "+ getString(R.string.add_speakername_end));
                startDialog();

            } catch (IdAlreadyExistsException e) {
                e.printStackTrace();
                makeToast(getString(R.string.speakerId_already_exists));
            } catch (AlizeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                makeToast(e.getMessage());
            }
        }
    };

    private void setupListViewAdapter() {
        list = new SpeakerListAdapter(NewSpeakerActivity.this, R.layout.list_item, new ArrayList<Speaker>());
        ListView speakerListView = findViewById(R.id.speakerListView);
        speakerListView.setAdapter(list);
    }

    private void clearAndFillSpeakersList() throws AlizeException {
        if (alizeSystem.speakerCount() == 0) {
            return;
        }
        list.clear();
        for (String speakerId : alizeSystem.speakerIDs()) {
            list.insert(new Speaker(speakerId), list.getCount());
        }
    }

    private void updateListViewContent() throws AlizeException {
        if (alizeSystem.speakerCount() == 0) {
            if (list.getCount() == 0) {
                noSpeakers.setVisibility(View.VISIBLE);
            }
            else
                noSpeakers.setVisibility(View.INVISIBLE);
        }
        else {
            noSpeakers.setVisibility(View.INVISIBLE);
        }
    }

    private boolean speakerAlreadyExist(String speakerName) throws AlizeException {
        for (String name : alizeSystem.speakerIDs()) {
            if (name.toLowerCase().equals(speakerName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void startDialog() {
        say(
        getResources().getString(R.string.hello_message_start) + " "
                + speakerName + " "
                + getResources().getString(R.string.hello_message_end)
        );

        startActivity(DialogActivity.class, new HashMap<String, Object>(){{
            put("speakerName", speakerName);
        }});
    }
}
