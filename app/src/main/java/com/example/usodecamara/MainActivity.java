package com.example.usodecamara;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.sql.Struct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button tomafoto;

    ImageView imgfoto;

    String rutadsoluta;

    private static final int REQUEST_CODIGO_CAMARA = 200;
    private static final int REQUEST_CODIGO_CAPTURAR_IMAGEN = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tomafoto = findViewById(R.id.btncaptura);
        imgfoto = findViewById(R.id.imgmostrar);

        tomafoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procesofoto();
            }
        });
    }
    public void Tomafoto(){

        Intent intentcamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intentcamera.resolveActivity(getPackageManager())!=null){
            File archivofoto = null;
            archivofoto = generofoto();

            if (archivofoto!=null){
                Uri rutafoto = FileProvider.getUriForFile(MainActivity.this,"com.example.usodecamara",archivofoto);
                intentcamera.putExtra(MediaStore.EXTRA_OUTPUT, rutafoto);
                startActivityForResult(intentcamera, REQUEST_CODIGO_CAPTURAR_IMAGEN);
            }
        }
    }

    public void procesofoto(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            Tomafoto();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            },REQUEST_CODIGO_CAMARA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODIGO_CAMARA){
            if (permissions.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Tomafoto();
            }else {
                Toast.makeText(MainActivity.this, "Error no hay permisos", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public File generofoto(){

        String nomenclatura = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String prefijoArchivo = "UGB_"+nomenclatura+"_";
        File directorioImagen = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = null;

        try {
            imagen = File.createTempFile(prefijoArchivo,".jpg",directorioImagen);
            rutadsoluta = imagen.getAbsolutePath();
        }catch (Exception error){
            Log.e("ErrorGenerarFoto", error.getMessage().toString());
        }
        return imagen;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==REQUEST_CODIGO_CAPTURAR_IMAGEN){
            if (resultCode == Activity.RESULT_OK){
                imgfoto.setImageURI(Uri.parse(rutadsoluta));

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}