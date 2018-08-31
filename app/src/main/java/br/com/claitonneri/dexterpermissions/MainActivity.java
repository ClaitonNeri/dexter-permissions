package br.com.claitonneri.dexterpermissions;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/*
*       OBS:  Adicionar as permissões solicitadas no AndroidManifest.xml
*/

public class MainActivity extends AppCompatActivity {

        private Button btnCamera;
        private Button btnStorage;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                btnCamera = findViewById(R.id.btn_camera);
                btnStorage = findViewById(R.id.btn_storage);

                btnCamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                requestCameraPermission();
                        }
                });

                btnStorage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                requestStoragePermission();
                        }
                });
        }

        /**
         *   Solicitando várias permissões (armazenamento e localização) de uma só vez
         *   Este usa vários pedidos de permissão
         *  Na negação permanente abre o diálogo de configurações
         */
        private void requestStoragePermission() {
                Dexter.withActivity(this)
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                        //   Checar se todas as permissões foram concedidas
                                        if (report.areAllPermissionsGranted()) {
                                                Toast.makeText(getApplicationContext(), "Todas as permissões foram concedidas.", Toast.LENGTH_SHORT).show();
                                        }
                                        //   Checar se foi negado permanentemente todos os pedidos de permissão
                                        if (report.isAnyPermissionPermanentlyDenied()) {
                                                //   Mostrar caixa dialog para abrir as configurações do aplicativo
                                                showSettingsDialog();
                                        }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                        token.continuePermissionRequest();
                                }
                        })
                        .withErrorListener(new PermissionRequestErrorListener() {
                                @Override
                                public void onError(DexterError error) {
                                        Toast.makeText(getApplicationContext(), "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                                }
                        })
                        .onSameThread()
                        .check();
        }

        /**
         *  Solicitar permissão da Camera
         *  Neste método foi solicitado um único pedido de permissão
         *  Uma vez concedida a permissão, a camera abrirá
         *  Caso for negado permanentemente a permissão será aberto o Dialog para acessar as configurações do aplicativo
         */
        private void requestCameraPermission() {
                Dexter.withActivity(this)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                        //  Se a permissão for concedida
                                        openCamera();
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                        //  Checar se foi negado permanentemente a permissão a Camera
                                        if (response.isPermanentlyDenied()) {
                                                showSettingsDialog();
                                        }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                }
                        }).check();
        }

        //  Metodo para chamar a caixa de diálogo para acesso as configurações do aplicativo
        private void showSettingsDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Permissões");
                builder.setMessage("Esta aplicação precisa  permissão para utilizar este recurso. Você pode conceder nas configurações deste aplicativo.");
                builder.setPositiveButton("IR PARA CONFIGURAÇÕES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                openSettings();
                        }
                });
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                        }
                });
                builder.show();
        }

        private void openSettings() {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
        }

        private void openCamera() {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
        }


        //--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--/--//
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_main, menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_settings) {
                        return true;
                }

                return super.onOptionsItemSelected(item);
        }
}
