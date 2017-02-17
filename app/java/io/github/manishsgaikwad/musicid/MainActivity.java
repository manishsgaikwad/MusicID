package io.github.manishsgaikwad.musicid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.RuntimePermissions;



@RuntimePermissions
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener { //implements RuntimePermissionListener {


    private RecyclerView rcv;
    private ItemAdapter adp;

    private final static String LIST_STATE_KEY = "recycler_list_state";
    private Parcelable listState;
    private List<MusicRetriever.Item> mItems;
    private Uri albumArtUri;

    private Drawer result=null;
    private Toolbar toolbar;
    private Context context;
    private Activity activity;
    private int lastFirstVisiblePosition;


    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        activity = this;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        ButterKnife.bind(this);
        //supportRequestWindowFeature(Window.FEATURE_PROGRESS);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.ic_launcher);
         //toolbar.setSubtitle("Manish"); //getSupportActionBar().setTitle("");

        mItems = new ArrayList<>();
        rcv = (RecyclerView)findViewById(R.id.rcv);
        rcv.setLayoutManager(new LinearLayoutManager(context));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
        rcv.addItemDecoration(itemDecoration);



       adp = new ItemAdapter(mItems);
        rcv.setAdapter(new AlphaInAnimationAdapter(adp));
        rcv.setHasFixedSize(true);



                MainActivityPermissionsDispatcher.createSongListWithCheck(this);
                createSongList();

            // rcv.setAdapter(new AlphaInAnimationAdapter(adp));

        //rcv.setHasFixedSize(true);



        //Drawer--------------------------------------------------------------------
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    result = new DrawerBuilder()
                            .withActivity(activity)
                            .withToolbar(toolbar)
                            .withActionBarDrawerToggle(true)
                            .withActionBarDrawerToggleAnimated(true)
                            .withDisplayBelowStatusBar(true)
                            .withTranslucentStatusBar(true)
                            .withSliderBackgroundColor(Color.WHITE)
                            .withHeader(R.layout.dheader)
                            .withHasStableIds(true)
                            .addDrawerItems(

                                    new PrimaryDrawerItem()
                                            .withName("Open Source Licenses")
                                            .withIcon(R.drawable.ic_list_black_18dp)
                                            .withIdentifier(200)
                                    ,

                                    new DividerDrawerItem(),
                                    new PrimaryDrawerItem()
                                            .withName("Feedback")
                                            .withDescription("Send feedback to the author")
                                            .withIcon(R.drawable.ic_feedback_black_18dp)
                                            .withIdentifier(300)
                                    ,
                                    new DividerDrawerItem(),

                                    new PrimaryDrawerItem()
                                            .withName("Author")
                                            .withDescription("Manish Gaikwad")
                                            .withIcon(R.drawable.ic_touch_app_black_18dp)
                                            .withIdentifier(100)
                                            .withSelectable(false)
                                            .withSetSelected(false),

                                    new PrimaryDrawerItem()
                                            .withName("Version")
                                            .withDescription("1.0")
                                            .withIcon(R.drawable.ic_info_black_18dp)
                                            .withIdentifier(305)
                                            .withSelectable(false)
                                            .withSetSelected(false)



                            )
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                                    if(drawerItem.getIdentifier()==200){
                                        //OpenSourceLicenses
                                        new MaterialDialog.Builder(context)
                                                .title("OpenSourceLicenses:")
                                                .content("1:Gracenote WebAPI\n2:com.android.support:appcompat-v7:25.0.1\n3:om.android.support:recyclerview-v7:25.0.1\n4:com.squareup.picasso:picasso:2.5.2\n5:jp.wasabeef:recyclerview-animators:2.2.5\n6:com.jakewharton:butterknife:7.0.1\n7:com.android.support:design:25.0.1\n8:com.github.hotchemi:permissionsdispatcher:2.2.1\n9:com.facebook.shimmer:shimmer:0.1.0@aar\n10:com.afollestad.material-dialogs:core:0.9.1.0\n11:com.mikepenz:materialdrawer:5.8.1@aar")
                                                .positiveText("DISMISS")
                                                .contentGravity(GravityEnum.START)
                                                .show();
                                    }
                                    else if(drawerItem.getIdentifier()==300){
                                        Intent feedbackEmail = new Intent(Intent.ACTION_SEND);
                                        feedbackEmail.setType("text/email");
                                        feedbackEmail.putExtra(Intent.EXTRA_EMAIL, new String[] {"manishsgaikwad23@gmail.com"});
                                        feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback: MusicID");
                                        startActivity(Intent.createChooser(feedbackEmail, "Send Feedback:"));
                                    }
                                    return false;
                                }
                            })
                            .build();

                    ItemClickSupport.addTo(rcv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            albumArtUri = mItems.get(position).getAlbumartUri();
                            //launchActivity(mItems.get(position),v);
                            launchActivity(adp.getUpdatedItems().get(position),v);

                        }
                    });


                    ItemClickSupport.addTo(rcv).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {

                            final File f = new File(adp.getUpdatedItems().get(position).getPath());

                            new MaterialDialog.Builder(context)
                                    .items("Share")
                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                            if(position==0) {
                                                Uri uri = Uri.parse("file://" + f.getAbsolutePath());
                                                Intent share = new Intent(Intent.ACTION_SEND);
                                                share.putExtra(Intent.EXTRA_STREAM, uri);
                                                share.setType("audio/*");
                                                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                context.startActivity(Intent.createChooser(share, "Share audio File"));
                                            }


                                        }
                                    })
                                    .show();
                            return false;
                        }
                    });


                }
            });



    }


private void launchActivity(MusicRetriever.Item item , View v){
    Intent intent = new Intent(this, SongDetail.class);
    intent.putExtra("io.github.manishsgaikwad.musicid",item);
    Picasso.with(this).invalidate(item.getAlbumartUri());
    //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,v,"transition_3");
    Bundle bundleAnimations = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),R.anim.animation,R.anim.animation2).toBundle();
    ActivityCompat.startActivity(this,intent,bundleAnimations);
}

    @Override
    protected void onPause() {
        lastFirstVisiblePosition = ((LinearLayoutManager)rcv.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        super.onPause();

    }


    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
   // @AskPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void createSongList(){
        MusicRetriever musicRetriever = new MusicRetriever(getContentResolver(),this);

       PrepareMusicRetrieverTask prepareMusicRetrieverTask= new PrepareMusicRetrieverTask(musicRetriever,rcv,context,lastFirstVisiblePosition,progressBar,adp);
        prepareMusicRetrieverTask.execute(mItems);

        try {
           mItems = prepareMusicRetrieverTask.get();
            if(mItems.isEmpty()){
                Toast.makeText(this,"No songs found. How boring!",Toast.LENGTH_LONG).show();
                rcv.setVisibility(View.INVISIBLE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    /*
    @Override
    public void onShowPermissionRationale(List<String> permissionList, RuntimePermissionRequest permissionRequest) {
        Toast.makeText(this,"Need the permission to show list of songs",Toast.LENGTH_LONG).show();
        permissionRequest.retry();
    }
     */

    /*
    @Override
    public void onPermissionDenied(List<DeniedPermission> deniedPermissionList) {
        setContentView(R.layout.no_permission);
        TextView nopermissionTV = (TextView)findViewById(R.id.nopermission);
        nopermissionTV.setText("Go to settings to enable storage permission to continue further!");
        //Toast.makeText(this,"Go to app setting to grant storage permission.",Toast.LENGTH_LONG).show();

    }
  */
    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onPD(){
        //setContentView(R.layout.no_permission);
        Intent permissionDenied = new Intent(this,NoPermissionActivity.class);
        startActivity(permissionDenied);
        //Toast.makeText(this,"Go to app setting to grant storage permission.",Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = rcv.getLayoutManager().onSaveInstanceState();
        //outState.putParcelableArrayList(LIST_STATE_KEY, (ArrayList<? extends Parcelable>)items);
        outState.putParcelable(LIST_STATE_KEY,listState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState!=null){
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            // items = savedInstanceState.getParcelableArrayList(LIST_STATE_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(listState!=null){
            rcv.getLayoutManager().onRestoreInstanceState(listState);
        }
        if(albumArtUri != null) {
            Picasso.with(getApplicationContext()).invalidate(albumArtUri);
        }

        createSongList();
        //((LinearLayoutManager) rcv.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
    }

    @Override
    public void onBackPressed() {
        if(result.isDrawerOpen()){
            result.closeDrawer();
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        //search logic
        //adp.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //     adp.filter(newText);
        adp.getFilter().filter(newText);
        return true;
    }
}









