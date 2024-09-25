package com.chdman.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.LinkedList;

public class FilePicker {
    private LinkedList<String> openedFiles = new LinkedList<>();
    private ActivityResultLauncher<Intent> arl;
    
    private String pathFromUri(Uri uri) {
        String path = "";
        String documentID = uri.getPath();
        String[] splitted = documentID.split(":");
        switch (splitted[0]) {
            case "/document/primary":
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + splitted[1];
                break;
            default:
                break;
        }
        return path;
    }

    public FilePicker(AppCompatActivity act) {
        super();
        this.arl = act.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>(){
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            String file = pathFromUri(data.getClipData().getItemAt(i).getUri());
                            openedFiles.add(file);
                        }
                    }
                    else {
                        String file = pathFromUri(data.getData());
                        openedFiles.add(file);
                    }        
                }
            }
        });
    }

    public void pickMultiple(String mimetype) {
        Intent launcher = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        launcher.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        launcher.addCategory(Intent.CATEGORY_OPENABLE);
        launcher.setType(mimetype);
        Intent intent = Intent.createChooser(launcher, "Choose a file");
        this.arl.launch(intent);
    }

    public LinkedList<String> getFilesList() {
        return this.openedFiles;
    }
}
