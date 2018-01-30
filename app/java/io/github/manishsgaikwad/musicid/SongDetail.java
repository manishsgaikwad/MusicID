package io.github.manishsgaikwad.musicid;


import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.squareup.picasso.Picasso;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import at.markushi.ui.CircleButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.manishsgaikwad.musicid.whatsapp_profile.HeaderView;

import static io.github.manishsgaikwad.musicid.R.menu.songdetails_menu;

public class SongDetail extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private final int READ_REQUEST_CODE = 42;
    private static int CHECK_NEW_ALBUM_ART = 0;

    @Bind(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;


    @Bind(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @Bind(R.id.appbar)
    protected AppBarLayout appBarLayout;


    @Bind(R.id.mainImg)
    protected ImageView imageView;

    @Bind(R.id.track_ET)
    protected EditText track_ET;
    @Bind(R.id.album_ET)
    protected  EditText album_ET;
    @Bind(R.id.artist_ET)
    protected EditText artist_ET;
    @Bind(R.id.genre_ET)
    protected EditText genre_ET;

    @Bind(R.id.editCoverArt)
    protected Button editCoverArt;

    @Bind(R.id.sV)
    protected NestedScrollView scrollView;

    @Bind(R.id.okEdit2)
    protected CircleButton okEdit2;

    private boolean isHideToolbarView = false;

    private MusicRetriever.Item item ;
    private AudioFile mAudioFile;
    private Tag mTag;
    private Uri userSelectedImageUri = null;


    //Enter your api keys
    private static final String apiUrl = "*****************************";
    private static String userID;
    private static final String clientID = "****************************";

    private String query=null;

    private String title;
    private String album;
    private String artist;
    private String filePath;
    private Uri albumArtUri;

    private Activity activity;
    private Context context;
    MetaDataDownloaderTask metaDataDownloaderTask = null;
    static private MediaPlayer mediaPlayer;

    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
     ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(null);
        }


        activity = this;
        context = this.getApplicationContext();
        toolbar = (Toolbar)findViewById(R.id.toolbar1);

        TagOptionSingleton.getInstance().setAndroid(true);

        ButterKnife.bind(this);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);


        Intent intent = getIntent();

        if(intent!=null){
            String IID = intent.getExtras().getString("IID");
            if(IID.equals("O"))
            {
                filePath=intent.getStringExtra("File_Path");
                showSnackBar(filePath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TagOptionSingleton.getInstance().setAndroid(true);
                        AudioFile audioFile;
                        try {
                            audioFile = AudioFileIO.read(new File(filePath));
                            Tag tag = audioFile.getTag();
                            title=tag.getFirst(FieldKey.TITLE);
                            artist=tag.getFirst(FieldKey.ARTIST);
                            album=tag.getFirst(FieldKey.ALBUM);
                            albumArtUri=null;
                            Bitmap bmp = BitmapFactory.decodeByteArray(tag.getFirstArtwork().getBinaryData(),0,tag.getFirstArtwork().getBinaryData().length);
                            imageView.setImageBitmap(bmp);
                            //Picasso.with(context).load("").error(R.drawable.default_art).into(imageView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /*
                        try{
                        Bitmap bmp = BitmapFactory.decodeByteArray(tag.getFirstArtwork().getBinaryData(),0,tag.getFirstArtwork().getBinaryData().length);
                        imageView.setImageBitmap(bmp);}
                        catch (Exception e){

                        } */
                    }
                });

            }
            if(IID.equals("I"))
            {
                Bundle bundle = intent.getExtras();
                item = bundle.getParcelable("io.github.manishsgaikwad.musicid");
                filePath=item.getPath();
                title = item.getTitle();
                artist = item.getArtist();
                album = item.getAlbum();
                albumArtUri = item.getAlbumartUri();
            }
        }

        imageView.post(new Runnable() {
            @Override
            public void run() {
                initUi(title,album,artist,albumArtUri);
            }
        });




        toolbar.inflateMenu(songdetails_menu);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //Circular save changes button code
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                okEdit2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            writeMeta(filePath);
                            writeMeta(filePath);

                        } catch (Exception e){
                            e.printStackTrace();
                            Log.d("Error Writing Data","Error!");
                            showSnackBar("Corrupted Tags!");
                        }

                        // okEdit2.setVisibility(View.GONE);
                            showSnackBar("Done! All changes saved.");
                        if(albumArtUri!=null)
                            Picasso.with(getApplicationContext()).invalidate(albumArtUri);
                        setETeditable(false);
                        // okEdit2.setVisibility(View.GONE);
                        //initUi(metaUpdated[0],metaUpdated[1],metaUpdated[2]);
                    }
                });


            }
        });



        SharedPreferences mSharedPreferences = getSharedPreferences("Gracenote",0);
        if(mSharedPreferences.getString("userID","error").compareToIgnoreCase("error") == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(isNetworkAvailable()) {
                        userID = register();
                        SharedPreferences mSettings = getSharedPreferences("Gracenote", 0);
                        final SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("userID", userID);
                        editor.apply();
                    }
                }
            }).start();

        } else {
          // Log.i("sharedPref",mSharedPreferences.getString("userID","error"));
            userID = mSharedPreferences.getString("userID","");
        }


 new Thread(new Runnable() {
     @Override
     public void run() {
         mediaPlayer = new MediaPlayer();
         try {
             mediaPlayer.setDataSource(context,Uri.fromFile(new File(filePath)));
             mediaPlayer.prepare();
         } catch (IOException e) {
             e.printStackTrace();
         }


         floatingActionButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 try{
                     if(mediaPlayer.isPlaying()){
                         mediaPlayer.pause();
                         floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_white_24dp));
                     }
                     else {
                         mediaPlayer.start();
                         floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause_white_24dp));
                         showSnackBar("Now Playing: "+title);
                     }
                 }catch (Exception e){
                     e.printStackTrace();
                 }
             }
         });

     }
 }).start();

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.setTitle("Please Wait");



        //Check if file is MP3 or not
       // Log.i("fileee",filePath);
        final String extension = filePath.substring(filePath.lastIndexOf(".")+1);
        if(extension.compareToIgnoreCase("mp3")!=0)
        {
            new MaterialDialog.Builder(this)
                    .title("This is a "+extension+" file")
                    .content("Only mp3 files can be edited. Do you want to convert it to mp3 to edit its tags?")
                    .positiveText("Yes")
                    .negativeText("No")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //code to convert file to mp3
                            //AudioToMp3ConverterTask audioToMp3ConverterTask = new AudioToMp3ConverterTask(imageView.getContext(),filePath,pd);
                           // audioToMp3ConverterTask.execute();
                            //pd.show();
                            convertToMp3(filePath,"Converting...");
                           // pd.dismiss();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    })
                    .iconRes(R.drawable.ic_info_black_18dp)
                    .cancelable(false)
                    .show();
        }

    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            final String PREFS_NAME = "MyPrefsFile";

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            if (settings.getBoolean("my_first_time", true)) {
                //the app is being launched for first time, do something
                Log.d("Comments", "First time");

                    try {
                        // first time task
                        Toast.makeText(context, "Click on the highlighted areas to know more", Toast.LENGTH_LONG).show();
                        new TapTargetSequence(activity)
                                .targets(
                                        TapTarget.forToolbarMenuItem(toolbar, R.id.editMeta, "Edit Button!", "Use this button to start editing your music files.")
                                                .dimColor(android.R.color.black)
                                                .outerCircleColor(R.color.colorAccent)
                                                .targetCircleColor(android.R.color.white)
                                                .transparentTarget(false)
                                                .textColor(android.R.color.black)
                                                .cancelable(false),
                                        TapTarget.forToolbarMenuItem(toolbar, R.id.downloadMeta, "Download Button!", "Use it to automatically do editing for you.")
                                                .dimColor(android.R.color.black)
                                                .outerCircleColor(R.color.colorAccent)
                                                .targetCircleColor(android.R.color.white)
                                                .transparentTarget(false)
                                                .textColor(android.R.color.black)
                                                .cancelable(false),
                                        TapTarget.forToolbarMenuItem(toolbar, R.id.fixFile, "Fix Button!", "Use it to fix music files for which the changes are not saved. Don't worry, old files will be kept as it is.")
                                                .dimColor(android.R.color.black)
                                                .outerCircleColor(R.color.colorAccent)
                                                .targetCircleColor(android.R.color.white)
                                                .transparentTarget(false)
                                                .textColor(android.R.color.black)
                                                .cancelable(false)
                                )
                                .start();

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                // record the fact that the app has been started at least once
                settings.edit().putBoolean("my_first_time", false).commit();
            }



        }
    });




    }




    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //Fn() to initialize the UI elements...
    private void initUi(String title, String album, String artist, Uri uri) {
      appBarLayout.addOnOffsetChangedListener(this);

        toolbarHeaderView.bindTo(title, artist);
        floatHeaderView.bindTo(title,artist);
        if(uri!=null) {
            Picasso.with(this).load(uri).error(R.drawable.default_art).into(imageView);
        }
        //track_ET.setFocusableInTouchMode(true);
        setETeditable(false);

        track_ET.setText(title);
        album_ET.setText(album);
        artist_ET.setText(artist);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    TagOptionSingleton.getInstance().setAndroid(true);
                    //TagOptionSingleton.isAndroid(true);
                    AudioFile audioFile = AudioFileIO.read(new File(filePath));
                    Tag tag = audioFile.getTag();
                    genre_ET.setText(tag.getFirst(FieldKey.GENRE));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        if(uri!=null)
        setPersonalizedColor(uri);

    }

    private void setETeditable(boolean val){
        track_ET.setFocusable(val);
        track_ET.setClickable(val);
        track_ET.setFocusableInTouchMode(val);
        album_ET.setClickable(val);
        album_ET.setFocusable(val);
        album_ET.setFocusableInTouchMode(val);
        artist_ET.setClickable(val);
        artist_ET.setFocusable(val);
        artist_ET.setFocusableInTouchMode(val);
        genre_ET.setFocusable(val);
        genre_ET.setFocusableInTouchMode(val);
        if(val){
        editCoverArt.setVisibility(View.VISIBLE);}
        else {
            editCoverArt.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }



    @Override
    public void onBackPressed() {
        try {
            File del = new File(Environment.getExternalStorageDirectory() + "/CacheAlbumArt/1.jpg");
            del.delete();
            MediaScannerConnection.scanFile(context,
                    new String[] { del.getPath() },
                    new String[] { "image/jpeg" }, null);

        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        catch (Exception e){}

        supportFinishAfterTransition();
            this.finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in,R.anim.trans_right_out);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(songdetails_menu,menu);
         return true;
    }

    public MusicRetriever.Item getItem(){
        return item;
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_white_24dp));
            }
           // pd.dismiss();

        }
        catch (Exception e){

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.editMeta:
                showSnackBar("Go ahead. Edit the MetaData!");
                setETeditable(true);
                okEdit2.setVisibility(View.VISIBLE);
                break;

            case R.id.downloadMeta:
                //downloadMetaData();
                setETeditable(true);
                okEdit2.setVisibility(View.VISIBLE);
                artist=artist_ET.getText().toString();
                title=track_ET.getText().toString();
                album=album_ET.getText().toString();
                MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
                MaterialDialog.Builder resultbuilder = new MaterialDialog.Builder(this);
                ProgressDialog progressDialog = new ProgressDialog(this);
                Integer[] selectedIndices = {0};
                metaDataDownloaderTask = new MetaDataDownloaderTask(track_ET,album_ET,artist_ET,resultbuilder,imageView,album,apiUrl,artist,clientID,context,progressDialog,query,title,userID,genre_ET);
                builder.title("Select items to include in search:")
                        .items("Track","Album","Artist")
                        .itemsCallbackMultiChoice(selectedIndices, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                StringBuilder str = new StringBuilder();
                                boolean isTrack=false,isAlbum=false,isArtist = false;
                                for(int i=0;i< which.length;i++){
                                    if(i>0)
                                        str.append(which[i]);
                                    str.append(text[i]);
                                }
                                if(str.toString().contains("Track"))
                                    isTrack=true;

                                if(str.toString().contains("Album"))
                                    isAlbum=true;

                                if(str.toString().contains("Artist"))
                                    isArtist=true;

                                metaDataDownloaderTask.isSet(isTrack,isAlbum,isArtist);
                               // isSet(isTrack,isAlbum,isArtist);
                                return true;
                            }
                        })
                        .positiveText("ADD")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                metaDataDownloaderTask.execute();
                            }
                        })
                        .show();
                break;

            case R.id.fixFile:

                new MaterialDialog.Builder(this)
                        .content("Press 'Fix' to continue")
                        .negativeText("Cancel")
                        .positiveText("Fix")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                convertToMp3(filePath,"Fixing...");
                            }
                        })
                        .show();
                break;

            case android.R.id.home:

                mediaPlayer.reset();
                mediaPlayer.release();
                supportFinishAfterTransition();
               // overridePendingTransition(R.anim.trans_right_in,R.anim.trans_right_out);
                return true;


        }

        return super.onOptionsItemSelected(item);
    }




    private void writeMeta(final String path) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException, CannotWriteException {
         File song  = new File(path);
        Tag oldTag = null;
         mAudioFile = AudioFileIO.read(song);
        if(mAudioFile.getTag() != null){
        oldTag = mAudioFile.getTag();
        }
        //Tag mTag = new ID3v23Tag();
        Tag mTag = new ID3v24Tag();
        //mAudioFile.setTag(new ID3v24Tag());
        //mTag = mAudioFile.getTag();

        String track1,album1,artist1;
        track1=track_ET.getText().toString();
        album1=album_ET.getText().toString();
        artist1=artist_ET.getText().toString();
        String genre1 = genre_ET.getText().toString();
       // Log.i("Hmmm1",track1+":"+album1+";"+artist1);

         //mTag.setField(FieldKey.TRACK,track1);
       // mTag.setField(FieldKey.TRACK,track1);
         mTag.setField(FieldKey.TITLE,track1);

         mTag.setField(FieldKey.ALBUM,album1);

         mTag.setField(FieldKey.ARTIST,artist1);

        mTag.setField(FieldKey.GENRE,genre1);

        if(CHECK_NEW_ALBUM_ART == 1){
           // Log.i("Artwork",getRealPathFromURI(userSelectedImageUri));
            File img = new File(getRealPathFromURI(userSelectedImageUri));
            final Artwork artwork = ArtworkFactory.createArtworkFromFile(img);
           // mTag.deleteField(FieldKey.COVER_ART);
            oldTag.deleteArtworkField();
            mTag.deleteArtworkField();
            mTag.createField(artwork);
           // mTag.setField(FieldKey.COVER_ART,artwork.toString());
            mTag.setField(artwork);
           // mAudioFile.commit();
            CHECK_NEW_ALBUM_ART = 0;
        }
        else if (metaDataDownloaderTask != null && metaDataDownloaderTask.getDownloadedImageUrl() != null){
                File img = new File(Environment.getExternalStorageDirectory()+"/CacheAlbumArt/1.jpg");
            Artwork artwork = ArtworkFactory.createArtworkFromFile(img);
            oldTag.deleteArtworkField();
            mTag.deleteArtworkField();
            mTag.createField(artwork);
            mTag.setField(artwork);
            setPersonalizedColor(Uri.fromFile(img));
        }
        else {
            if(oldTag.getFirstArtwork() != null){
                mTag.setField(oldTag.getFirstArtwork());
            }
        }


        // mTag.setField(oldTag.getFirstArtwork());

        //Artwork artwork = ArtworkFactory.createLinkedArtworkFromURL(String.valueOf(item.getAlbumartUri()));
       // mTag.setField(artwork);
        mAudioFile.setTag(mTag);
        AudioFileIO.write(mAudioFile);
        if(userSelectedImageUri != null) {
            initUi(track1, album1, artist1, userSelectedImageUri);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                scanMedia(path);

                   // updateAlbumArtMediaStore(getApplicationContext(),item.getId(),ArtworkFactory.createArtworkFromFile(new File(getRealPathFromURI(userSelectedImageUri))).toString());

            }
        }).start();
        //Picasso.with(getApplicationContext()).invalidate(item.getAlbumartUri());
        try {
            if (userSelectedImageUri != null) {
                updateAlbumArtMediaStore(getApplicationContext(), item.getAlbumID(), getRealPathFromURI(userSelectedImageUri));
            } else if (metaDataDownloaderTask.getDownloadedImageUrl() != null) {
                updateAlbumArtMediaStore(getApplicationContext(), item.getAlbumID(), Environment.getExternalStorageDirectory().toString() + "/CacheAlbumArt/1.jpg");
            }
        }
        catch (NullPointerException e){
           // e.printStackTrace();
        }

        //mAudioFile.commit();
    }


    private  void showSnackBar(String message){

            Snackbar mSnackbar = Snackbar.make(findViewById(R.id.toolbar_header_view),message,Snackbar.LENGTH_LONG);
            TextView tv = (TextView) mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.YELLOW);
            mSnackbar.show();

        }

    private void scanMedia(String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        getBaseContext().sendBroadcast(scanFileIntent);
        MediaScannerConnection.scanFile(context,
                new String[] { path },
                new String[] {"audio/*"} , null);
    }


    public void getAlbumArt(View view){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(data != null){
                userSelectedImageUri = data.getData();
               // Log.i("Image uri",userSelectedImageUri.toString());
                Picasso.with(getApplicationContext()).load(userSelectedImageUri).into(imageView);
                CHECK_NEW_ALBUM_ART = 1;

            }
        }
    }


    private String getRealPathFromURI(Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    public void updateAlbumArtMediaStore(Context context, final long id, String art){
        Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), id);
        context.getContentResolver().delete(uri,null, null);
        ContentValues values = new ContentValues();
        values.put("album_id", id+Math.random());
        values.put("_data", art);

        Uri newuri = context.getContentResolver()
                .insert(Uri.parse("content://media/external/audio/albumart"),
                        values);
        if(newuri!=null){
            //Toast.makeText(this, "UPDATED", Toast.LENGTH_LONG).show();
            context.getContentResolver().notifyChange(uri, null);

        }
    }
    
    public void setPersonalizedColor(final Uri imageUri)  {
        try {
            if (imageUri != null) {

                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if (bitmap != null) {
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(final Palette palette) {
                            final Palette.Swatch vibrant = palette.getVibrantSwatch();

                            //Palette.Swatch something = palette.getDominantSwatch();
                            if (vibrant != null) {
                                //scrollView.setBackgroundColor(vibrant.getRgb());
                                //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(something.getRgb()));


                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


                                    scrollView.post(new Runnable() {
                                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                        @Override
                                        public void run() {
                                            int cx = scrollView.getWidth()/2;
                                            int cy = scrollView.getHeight()/2;
                                            float finalRadius = (float)Math.hypot(cx,cy);
                                            Animator anim = ViewAnimationUtils.createCircularReveal(scrollView,cx,cy,0,finalRadius);
                                            scrollView.setBackgroundColor(vibrant.getRgb());
                                            anim.setDuration(400);
                                            anim.start();
                                            /*
                                            Window window = getWindow();

                                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                            if(palette.getDarkVibrantSwatch()!=null) {
                                                window.setStatusBarColor(palette.getDarkVibrantSwatch().getRgb());
                                                }
                                                */
                                        }
                                    });

                                }
                                else{
                                    scrollView.setBackgroundColor(vibrant.getRgb());
                                }
                            }
                        }
                    });
                } else {
                    scrollView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
            }
        }
        catch (Exception e){
            scrollView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

    }

        //background task
    public boolean isOnline(){
        Runtime runtime = Runtime.getRuntime();
        try{
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
        return false;
    }




    public String register(){
            String request = "<QUERIES>\n" +
                    "  <QUERY CMD=\"REGISTER\">\n" +
                    "    <CLIENT>64548121-8795C19E5616F630BDA0EE857D224F5A</CLIENT>\n" +
                    "  </QUERY>\n" +
                    "</QUERIES>";
        String response = _httpPostRequest(apiUrl,request);
       // Log.i("Response",response);
        Document xml = _checkResponse(response);
        if(xml != null) {
            userID = xml.getDocumentElement().getElementsByTagName("USER").item(0).getFirstChild().getNodeValue();
           // Log.i("userID", userID);
        }

        return userID;

    }
        //background task
    protected String _httpPostRequest(String url, String data){
        try{
            URL u =  new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setRequestProperty("Charset","utf-8");
            connection.setRequestProperty("Content-Lenght",""+Integer.toString(data.getBytes().length));
            connection.setUseCaches(false);

            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
            wr.write(data);
            wr.flush();
            wr.close();

            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));

            String line;
            while ((line = reader.readLine()) != null){
                output.append(line);
            }
            reader.close();
            connection.disconnect();

            return output.toString();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Document _checkResponse(String response)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            // Get and parse into a document
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(response)));

            // Navigate to the status code and read it.
            Element root = doc.getDocumentElement();
            NodeList nl = root.getElementsByTagName("RESPONSE");
            String status = "ERROR";
            if (nl != null && nl.getLength() > 0)
            {
                status = nl.item(0).getAttributes().getNamedItem("STATUS").getNodeValue();
            }

            // Handle error codes accordingly
            if (status.equals("ERROR"))    { showSnackBar("API response error.");}
            if (status.equals("NO_MATCH")) { showSnackBar("No match response."); }
            if (!status.equals("OK"))      { showSnackBar("Non-OK API response."); }

            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }


    private void convertToMp3(String filePath, final String message){
        try{
            String[] f = filePath.split("\\.");
            final String converted_fPath = filePath.replace(f[f.length-1],"mp3");
            File output = new File(Environment.getExternalStorageDirectory().getPath()+"/Converted Files");

            if(!output.exists())
                output.mkdir();

            final String convertedFilePath = output+"/"+new File(converted_fPath).getName();
           // Log.i("Converted file path@:",convertedFilePath);

            final String [] cmd = new String[]{"-i",filePath,"-f","mp3",convertedFilePath};


            FFmpeg.getInstance(context).execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                  //  Log.i("Converting","HO GAYA!@:"+convertedFilePath);
                     Toast.makeText(context,"Done @: "+convertedFilePath,Toast.LENGTH_LONG).show();
                     context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(convertedFilePath))));
                }

                @Override
                public void onProgress(String message) {
                   // Log.i("Converting","Ho raha hai!");
                }

                @Override
                public void onFailure(String message) {
                  //  Log.i("Converting","Nai Hua!(Failure): "+message);
                }

                @Override
                public void onStart() {
                    Toast.makeText(context,message,Toast.LENGTH_LONG).show();
                    pd.setMessage(message);
                    pd.show();
                  //  Log.i("Converting","Shuru Hua!");
                }

                @Override
                public void onFinish() {
                    //scanMedia(Environment.getExternalStorageDirectory().getPath()+"/test.mp3");
                   // Toast.makeText(context,"Done @: "+convertedFilePath,Toast.LENGTH_LONG).show();
                   // context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(convertedFilePath))));
                    pd.dismiss();
                    try {
                        if(new File(convertedFilePath).exists()) {
                            Intent intent = new Intent(context, SongDetail.class);
                            intent.putExtra("IID", "O");
                            intent.putExtra("File_Path", convertedFilePath);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    }
                    catch (Exception e){}
                   // Log.i("Converting","Kahtam Ho gaya!@ "+convertedFilePath);
                }
            });
        }
        catch (Exception e){
           // Log.i("Converting","Error!!!!!!!!!");
            e.printStackTrace();
        }
    }


}



