package com.example.arjun.cortina;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.IntegerRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class CourtingCortina extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;
    protected EditText text;
    protected Button butone;
    protected String apple = " ";
    protected TextToSpeech t1;
    protected Button speech;
    protected Button butone1,butone2;
    protected EditText textering;
    protected String query = "";
    protected TextView andy;
    protected Button btw;
    protected String ET = "";
    protected String ad = "";
    protected Handler h;
    protected String rece = "";
    protected Button play;
    protected Button pause;
    protected Button stop;
    protected Button photo;
    protected ImageView imageView;
    private ProgressDialog progress;
    protected MediaPlayer mp;
    protected double lat,longer;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String address = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //widgets
    Button btnPaired;
    ListView devicelist;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("high");
    //CTB cttr;

    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courting_cortina);
        //Calling widgets
        btnPaired = (Button)findViewById(R.id.button);
        devicelist = (ListView)findViewById(R.id.listView);
        mp = new MediaPlayer();
        try{
            File f = (Environment.getExternalStoragePublicDirectory("court/miner.mp3"));
            String g = f.getPath();
            mp.setDataSource(g);
            mp.prepare();
            Toast.makeText(getApplicationContext(),"Found",Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Media File Missing",Toast.LENGTH_SHORT).show();
        }

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pairedDevicesList();
            }
        });
        andy = (TextView) findViewById(R.id.reply);
        play = (Button)findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
            }
        });
        pause = (Button)findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.pause();
            }
        });
        stop = (Button)findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
            }
        });









        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });
        t1.speak("Wassup", TextToSpeech.QUEUE_FLUSH, null);
        text = (EditText) findViewById(R.id.inputter);
        butone = (Button) findViewById(R.id.buttonone);
        butone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apple = text.getText().toString();
                t1.speak(apple, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        speech = (Button) findViewById(R.id.speecher);
        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                longer = location.getLongitude();
                andy.setText(Double.toString(lat)+" "+Double.toString(longer));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        photo = (Button)findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }


            }
        });
        imageView = (ImageView)findViewById(R.id.imager);

        btw = (Button) findViewById(R.id.welin);
        btw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t1.speak("Welcome! My name is Cortina and I'm not a terrorist!", TextToSpeech.QUEUE_FLUSH, null);
                andy.setText("Welcome! My name is Cortina and I'm not a terrorist!");
            }
        });
        textering = (EditText) findViewById(R.id.cortinp);
        butone1 = (Button) findViewById(R.id.speecher);
        butone2 = (Button) findViewById(R.id.court);
        butone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    textp="";
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
        butone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ppl = textering.getText().toString();
                BrainCortina obj = new BrainCortina();
                ppl = obj.kickout(ppl);
                String pele = obj.recog(ppl);

                if (!pele.equalsIgnoreCase("none")) {
                    query = "I ain't " + pele + ". I've already started hating you!";
                    andy.setText(query);
                    t1.speak(query, TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    String ben = obj.answer(ppl);
                    String pz[] = ben.split(",");
                    int pd = (int) (Math.random() * 1000);
                    query = pz[pd % pz.length];
                    if (!query.equalsIgnoreCase("###")) {
                        andy.setText(query);
                        t1.speak(query, TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        int c1 = 0;
                        int c2 = 0;
                        String hp[] = ppl.split(" ");
                        for(int i = 0;i<hp.length;i++){
                            if(hp[i].equalsIgnoreCase("on")){
                                c1++;
                            }
                            else if(hp[i].equalsIgnoreCase("off")){
                                c2++;
                            }
                        }
                        int ytr = 0;
                        for(int i = 0;i<hp.length;i++){
                            if(hp[i].equalsIgnoreCase("music")||hp[i].equalsIgnoreCase("play")){
                                ytr++;
                            }
                        }
                        int pel = 0;
                        for(int i = 0;i<hp.length;i++){
                            if(hp[i].equalsIgnoreCase("in")||hp[i].equalsIgnoreCase("deja"))
                                pel++;
                        }
                        if(c1>0){
                            turnOnLed();
                        }
                        else if(c2>0){
                            turnOffLed();
                        }
                        else if(ytr>0){
                            mp.start();
                        }
                        else if(pel>0){
                            helt();
                        }
                        else {
                            if(ppl.equalsIgnoreCase("status")){
                                String p = ttl();
                                if(passant>40){
                                    //p = "Nobody Hone";
                                }
                                else{
                                    //p = "Somebody Home";
                                }

                                Toast.makeText(getApplicationContext(),p,Toast.LENGTH_LONG).show();

                            }
                        }
                    }

                }

            }
        });

    }
    protected String textp = "";
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case RESULT_SPEECH: {
                    if (resultCode == RESULT_OK && null != data) {

                        ArrayList<String> text = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        this.textp+=text.get(0);

                    }
                    textering.setText(textp);
                    break;
                }

            }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }

    }
    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        myRef.child("pic").setValue(imageEncoded);
    }

    private void turnOffLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("0".toString().getBytes());
                myRef.child("light").setValue("0");
            }
            catch (IOException e)
            {

            }
        }
    }
    protected int passant = 0;
    private String ttl()
    {
        String a = " ";
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("2".toString().getBytes());
                ArrayList<Byte> al = new ArrayList<>();
                al.add((byte)(btSocket.getInputStream().read()));
                passant = (int)(al.get(0));
                al.add((byte)(btSocket.getInputStream().read()));
                al.add((byte)(btSocket.getInputStream().read()));
                a = al.toString();
                return a;
            }
            catch (IOException e)
            {

            }
        }
        return a;
    }

    private void turnOnLed() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("1".toString().getBytes());
                myRef.child("light").setValue("1");
            } catch (IOException e) {

            }
        }
    }
    private void helt() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("3".toString().getBytes());
            } catch (IOException e) {

            }
        }
    }
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }
    ArrayList<Integer> arrayList = new ArrayList<>();
    private ArrayList reader(){

        Runnable r = new Runnable() {
            @Override
            public void run() {
                byte[] buffer;
                ArrayList<Integer> arr_byte = new ArrayList<Integer>();
                while (true) {
                    try {
                        int data = btSocket.getInputStream().read();
                        if(btSocket.getInputStream().available()>0) {
                            arr_byte.add(data);
                        } else {
                            arr_byte.add(data);
                            buffer = new byte[arr_byte.size()];
                            for(int i = 0 ; i < arr_byte.size() ; i++) {
                                buffer[i] = arr_byte.get(i).byteValue();
                            }
                            Log.e("INPUT",new String(buffer));
                            arr_byte = new ArrayList<Integer>();
                        }
                    } catch (IOException e) {
                        arrayList = arr_byte;
                        break;
                    }
                }

            }
        };
        Handler h = new Handler();
        h.post(r);

        return arrayList;
    }

    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an intent to start next activity.

            ET = EXTRA_ADDRESS;
            ad = address;
            new ConnectBT().execute();
            //Change the activity.

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(ad);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {

                finish();
            }
            else
            {

                isBtConnected = true;
                Toast.makeText(getApplicationContext(),"Connection Success",Toast.LENGTH_SHORT).show();
            }

        }
    }
    int TAKE_PHOTO_CODE = 0;



}
