package xgao.com.text_crypt_android.logic;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;

import xgao.com.text_crypt_android.MainActivity;


/**
 * Created by xgao on 10/21/17.
 */

public class model  implements Parcelable{

    public static final String ALL_GOOD = "Everything is good";

    private File inputFile;
    private boolean uriInput = false;
    private String uriInputFileName = "TextCryptTemFile";
    private File outputFile;
    private String key = "";
    private String encryptionMode;

    public model() {
    }

    public void convert() throws cryptoException{
        if (encryptionMode.equals(MainActivity.ENCRYPT_MODE)) {
            try {
                this.encrypt();
            }catch (OutOfMemoryError e){
                throw new cryptoException("File is too big, app can not fit the file into your ram but don't worry, I will fix it in a patch.");
            }
        }

        else if(this.encryptionMode.equals(MainActivity.DECRYPT_MODE)){
            try {
                this.decrypt();
            }catch (OutOfMemoryError e){
                throw new cryptoException("File is too big, app can not fit the file into your ram but don't worry, I will fix it in a patch.");
            }
        }
    }

    public void setOutputPath(File outputFile){
        this.outputFile = outputFile;
    }

    public void setKey(String key){
        this.key = key;
    }


    public void setInputFile(File input) {
        this.inputFile = input;
    }

    public void setEncryptionMode(String encryptionMode){
        this.encryptionMode = encryptionMode;
    }

    public String isEverythingValid(){
        if(inputFile == null || outputFile == null) {
            return "Please select both an input file and an output destination";
        }

        if (!inputFile.exists() && !uriInput){
            return "Input file path is invalid or it no longer exists";
        }

        if (!outputFile.exists()){
            return "Output directory is invalid or it no longer exists";
        }


        return ALL_GOOD;
    }

    public void setUriInput(boolean uriInput){
        this.uriInput = uriInput;
    }

    private void decrypt() throws cryptoException{
        File decryptedFile = new File(this.outputFile.getPath() +"/" +"decrypted_"+this.inputFile.getName());
        if(decryptedFile.exists()) {
            throw new cryptoException("File with same name already exists, delete the old one first");
        }
        if(uriInput){
            uriInputFileName.substring(10);
            String path = this.outputFile.getPath() + "/" + "decrypted_" + uriInputFileName;
            decryptedFile = new File(path);
        }
        cryptoUtils.decrypt(this.key, this.inputFile, decryptedFile);
    }

    private void encrypt() throws  cryptoException{
        File encryptedFile = new File(this.outputFile.getPath() + "/" + "encrypted_" + this.inputFile.getName());
        if(encryptedFile.exists()) {
                throw new cryptoException("File with same name already exists, delete the old one first");
        }
        if(uriInput){
           String path = this.outputFile.getPath() + "/" + "encrypted_" + uriInputFileName;
           encryptedFile = new File(path);
        }
        cryptoUtils.encrypt(this.key, this.inputFile, encryptedFile);

    }

    public void setUriInputFileName(String uriInputFileName){ this.uriInputFileName = uriInputFileName;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.inputFile);
        dest.writeByte(this.uriInput ? (byte) 1 : (byte) 0);
        dest.writeString(this.uriInputFileName);
        dest.writeSerializable(this.outputFile);
        dest.writeString(this.key);
        dest.writeString(this.encryptionMode);
    }

    protected model(Parcel in) {
        this.inputFile = (File) in.readSerializable();
        this.uriInput = in.readByte() != 0;
        this.uriInputFileName = in.readString();
        this.outputFile = (File) in.readSerializable();
        this.key = in.readString();
        this.encryptionMode = in.readString();
    }

    public static final Creator<model> CREATOR = new Creator<model>() {
        @Override
        public model createFromParcel(Parcel source) {
            return new model(source);
        }

        @Override
        public model[] newArray(int size) {
            return new model[size];
        }
    };
}
