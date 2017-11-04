package xgao.com.text_crypt_android.File_Browser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import xgao.com.text_crypt_android.R;
import xgao.com.text_crypt_android.logic.intentCodes;

/**
 Copyright (C) 2011 by Brad Greco <brad@bgreco.net>

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 /////////////////////////////////////////////////////////////////////////////////////

 I modified much of the original codes to get it up to date with the latest android operating system. The orginal code is from the year 2011

 Created by GAO November 1st, 2017

 */
public class FileBrowserActivity extends ListActivity {

    public static final String START_DIR = "startDir";
    public static final String ONLY_DIRS = "onlyDirs";
    public static final String SHOW_HIDDEN = "showHidden";
    public static final String RETURN_PATH = "returnPath";
    public static final String BROWSER_MODE = "browserMode";
    private static final int  MY_PERMISSIONS_REQUEST_READ_AND_WRITE_SDK = 1555454;
    private File dir;
    private boolean showHidden = false;
    private boolean onlyDirs = true ;
    private int operationMode = intentCodes.REQUEST_FILE_BROWSER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooser_list);
        Bundle extras = getIntent().getExtras();
        dir = Environment.getExternalStorageDirectory();
        if (extras != null) {
            this.operationMode = extras.getInt(BROWSER_MODE, intentCodes.REQUEST_FILE_BROWSER);
            String preferredStartDir = extras.getString(START_DIR);
            showHidden = extras.getBoolean(SHOW_HIDDEN, false);
            onlyDirs = extras.getBoolean(ONLY_DIRS, true);
            if (preferredStartDir != null) {
                File startDir = new File(preferredStartDir);
                if (startDir.isDirectory()) {
                    dir = startDir;
                }
            }
        }
       this.persmissionChecking();
        ((TextView) this.findViewById(R.id.currentDirectory)).setText(dir.getAbsolutePath());


    }



    private void restOfSetUp(){

        setTitle(dir.getAbsolutePath());
        Button btnChoose = (Button) findViewById(R.id.btnChoose);
        String name = dir.getName();
        if(this.operationMode == intentCodes.REQUEST_FILE){
            btnChoose.setVisibility(View.GONE);
        }

       else if(this.operationMode == intentCodes.REQUEST_DIRECTORY){
            btnChoose.setText("Select " + "'/" + name + "'");
            btnChoose.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    returnPath(dir.getAbsolutePath());
                }
            });
        }

        else if (this.operationMode == intentCodes.REQUEST_FILE_BROWSER){
            btnChoose.setText("Close Browser");
            btnChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    simplyClose();
                }
            });
        }


        ListView lv = getListView();
        lv.setTextFilterEnabled(true);


        if(!dir.canRead()) {
            Context context = getApplicationContext();
            String msg = "Could not read folder contents.";
            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        final ArrayList<File> files = filter(dir.listFiles(), onlyDirs, showHidden);
        String[] names = names(files);
         setListAdapter(new fileExploererAdaptor(this, files, names));
        //  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,names));
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // This returns the file path
                if(!files.get(position).isDirectory() && FileBrowserActivity.this.operationMode == intentCodes.REQUEST_FILE) {
                    returnPath(files.get(position).getAbsolutePath());
                    return;
                }
                else if (!files.get(position).isDirectory() && FileBrowserActivity.this.operationMode == intentCodes.REQUEST_FILE_BROWSER){
                    return;
                }
                else if(!files.get(position).isDirectory() && FileBrowserActivity.this.operationMode == intentCodes.REQUEST_DIRECTORY){
                     returnPath(files.get(position).getAbsolutePath());
                }
                String path = files.get(position).getAbsolutePath();
                if(path.endsWith("emulated")){
                    path += "/0";
                }
                Intent intent = new Intent(FileBrowserActivity.this, FileBrowserActivity.class);
                intent.putExtra(FileBrowserActivity.START_DIR, path);
                intent.putExtra(FileBrowserActivity.SHOW_HIDDEN, showHidden);
                intent.putExtra(FileBrowserActivity.ONLY_DIRS, onlyDirs);
                intent.putExtra(FileBrowserActivity.BROWSER_MODE, operationMode);
                startActivityForResult(intent, operationMode);



            }
        });
    }










    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == intentCodes.REQUEST_DIRECTORY|| requestCode == intentCodes.REQUEST_FILE) && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String path = (String) extras.get(FileBrowserActivity.RETURN_PATH);
            returnPath(path);
        }

        if(requestCode == intentCodes.REQUEST_FILE_BROWSER){
            this.simplyClose();
        }

    }


    public String[] names(ArrayList<File> files) {
        String[] names = new String[files.size()];
        int i = 0;
        for(File file: files) {
            names[i] = file.getName();
            i++;
        }
        return names;
    }




    public ArrayList<File> filter(File[] file_list, boolean onlyDirs, boolean showHidden) {
        ArrayList<File> files = new ArrayList<File>();
        for(File file: file_list) {
            if(onlyDirs && !file.isDirectory())
                continue;
            if(!showHidden && file.isHidden())
                continue;
            files.add(file);
        }
        Collections.sort(files);
        return files;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_AND_WRITE_SDK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        this.restOfSetUp();
                }else{
                    this.displayAlert("You gotta give me access to your files bud");
                }
                break;
        }

    }

    private void persmissionChecking(){
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((ListActivity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},  MY_PERMISSIONS_REQUEST_READ_AND_WRITE_SDK);
        }
        else{
            this.restOfSetUp();
        }
    }




    private void returnPath(String path) {
        Intent result = new Intent();
        result.putExtra(RETURN_PATH, path);
        setResult(RESULT_OK, result);
        finish();
    }

    private void simplyClose(){
        Intent result = new Intent();
        setResult(RESULT_OK, result);
        finish();
    }



    private void displayAlert(String alertMessage){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(alertMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

























}
