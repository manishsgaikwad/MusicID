package io.github.manishsgaikwad.musicid;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by manish on 21-12-2016.
 */

public class MetaDataDownloaderTask extends AsyncTask<Void,Void,Void> {

    private String track,album,artist;
    private Context context;
    private ProgressDialog progressDialog;
    private String query;
    private String userID;
    private String clientID;
    private String apiUrl;
    private boolean isTrack1,isAlbum1,isArtist1;
    private String result;
    private String downloadedImageUrl;
    private String downloadedTrack;
    private String downloadedArtist;
    private String downloadedAlbum;
    private String downloadedGenre;
    private ImageView imageView;
    private MaterialDialog.Builder resultBuilder;
     EditText tr;
    EditText al;
     EditText ar;
    EditText gr;

    public MetaDataDownloaderTask(EditText tr, EditText al, EditText ar,MaterialDialog.Builder resultbuilder, ImageView v, String album, String apiUrl, String artist, String clientID, Context context, ProgressDialog progressDialog, String query, String track, String userID, EditText gr) {
        this.album = album;
        this.apiUrl = apiUrl;
        this.artist = artist;
        this.clientID = clientID;
        this.context = context;
        this.progressDialog = progressDialog;
        this.query = query;
        this.track = track;
        this.userID = userID;
        this.imageView = v;
        this.resultBuilder = resultbuilder;
        this.tr = tr;
        this.al=al;
        this.ar=ar;
        this.gr=gr;
    }

    @Override
    protected void onPreExecute() {

                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Searching for Albums...");
                progressDialog.setCancelable(false);
                progressDialog.show();

        resultBuilder.title(result)
                .content("Try changing the search parameters manually or check the internet connection of your device.")
                .contentGravity(GravityEnum.START)
                .positiveText("DISMISS")
                .iconRes(R.drawable.ic_error_outline_white_48dp)
                .limitIconToDefaultSize()
                .theme(Theme.DARK)
                .positiveColorRes(R.color.material_red_400)
                .titleGravity(GravityEnum.START)
                .titleColorRes(R.color.material_red_400)
                .contentColorRes(android.R.color.white)
                .backgroundColorRes(R.color.material_blue_grey_800)
                .dividerColorRes(R.color.accent)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE);

        super.onPreExecute();
    }



    @Override
    protected Void doInBackground(Void... voids) {
        if(isNetworkAvailable()) {
            String response = _httpPostRequest(this.apiUrl, this.query);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                // Get and parse into a document
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(new StringReader(response)));

                // Navigate to the status code and read it.
                Element root = doc.getDocumentElement();
                NodeList nl = root.getElementsByTagName("RESPONSE");
                String status = "ERROR";
                if (nl != null && nl.getLength() > 0) {
                    status = nl.item(0).getAttributes().getNamedItem("STATUS").getNodeValue();
                }

                // Handle error codes accordingly
                if (status.equals("ERROR")) {
                    result="API response error.";
                }
                if (status.equals("NO_MATCH")) {
                    result="No match response.";
                }
                if (!status.equals("OK")) {
                    result="Non-OK API response.";
                }

                if (status.equals("OK")) {
                    //parsethe Response
                    result="Match Found!";
                   // Log.i("Response", response.replaceAll(">", ">\n"));
                    NodeList nl1 = root.getElementsByTagName("URL");
                    if(nl1!=null && nl1.getLength()>0){
                        String imageUrl = nl1.item(0).getTextContent();
                       // Log.i("Image url", imageUrl);
                        this.downloadedImageUrl = imageUrl;

                        File dir = new File(Environment.getExternalStorageDirectory()+"/CacheAlbumArt");
                        try{
                             URL url = new URL(downloadedImageUrl);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream is = connection.getInputStream();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.RGB_565;
                            Bitmap bmImg = BitmapFactory.decodeStream(is, null, options);
                            dir.mkdirs();
                            File file = new File(dir,"1.jpg");
                            file.createNewFile();
                            FileOutputStream fos = new FileOutputStream(file);
                            bmImg.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                            fos.flush();
                            fos.close();
                            //File imageFile = file;
                            MediaScannerConnection.scanFile(context,
                                    new String[] { file.getPath() },
                                    new String[] { "image/jpeg" }, null);
                        }catch (Exception e){
                                e.printStackTrace();
                        }
                    }

                    NodeList nl2 = root.getElementsByTagName("TITLE");
                    downloadedAlbum = nl2.item(0).getTextContent();
                    //Log.i("Album",downloadedAlbum);
                    downloadedTrack = nl2.item(1).getTextContent();
                   // Log.i("Track",downloadedTrack);
                    //downloadedArtist = root.getElementsByTagName("ARTIST").item(0).getTextContent();
                    if(root.getElementsByTagName("ARTIST").getLength()>1){
                        downloadedArtist = root.getElementsByTagName("ARTIST").item(1).getTextContent();
                    }
                    else {
                        downloadedArtist = root.getElementsByTagName("ARTIST").item(0).getTextContent();
                    }
                   // Log.i("Artist",downloadedArtist);
                    downloadedGenre = root.getElementsByTagName("GENRE").item(0).getTextContent();
                    //Log.i("Genre",downloadedGenre);
                }

            } catch (Exception e) {
                e.printStackTrace();
                response = "INTERNET_ERROR";
            }

        } else result="Error Connecting to the Internet!";
        return null;
    }

    public String getDownloadedImageUrl() {
        return downloadedImageUrl;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
        try {

            if (result.compareToIgnoreCase("match found!") == 0) {

                tr.setText(downloadedTrack);
                al.setText(downloadedAlbum);
                ar.setText(downloadedArtist);
                gr.setText(downloadedGenre);
                if(downloadedImageUrl != null){
                    Uri img = Uri.parse(downloadedImageUrl);
                    Picasso.with(context).load(img).error(R.drawable.default_art).into(imageView);

                }else{
                    result = "AlbumArt not found!";
                }

                Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e){
            result = "Oops! This is embarrassing";
           resultBuilder.title(result)
                   .show();

        }

        super.onPostExecute(aVoid);
    }

    public void isSet(boolean track , boolean album, boolean artist){
        this.isTrack1 = track;
        this.isAlbum1 = album;
        this.isArtist1 = artist;
        String t=this.track,al=this.album,ar=this.artist;
        if(!isTrack1)t="";
        if (!isAlbum1)al="";
        if(!isArtist1)ar="";

        this.query = _constructQuery(t,ar,al);
      // Log.i("Query new",query);

    }

    //checks if the device is connected to the internet
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //builds the query to be sent GracenoteWebAPI
    protected String _constructQuery(String track, String artist, String album){

        String initReq = "<QUERIES>\n" +
                "  <AUTH>\n" +
                "    <CLIENT>"+clientID+"</CLIENT>\n" +
                "    <USER>"+userID+"</USER>\n" +
                "  </AUTH>\n"+
                "  <QUERY CMD=\"ALBUM_SEARCH\">\n";

        if(!artist.equals("")) initReq += "<TEXT TYPE=\"ARTIST\">"+artist.replaceAll("[^a-zA-Z0-9\\.]", " ")+"</TEXT>\n";
        if(!album.equals("")) initReq += "<TEXT TYPE=\"ALBUM_TITLE\">"+album.replaceAll("[^a-zA-Z0-9\\.]", " ")+"</TEXT>\n";
        if(!track.equals("")) initReq +=  "<TEXT TYPE=\"TRACK_TITLE\">"+track.replaceAll("[^a-zA-Z0-9\\.]", " ")+"</TEXT>\n";

        initReq += "<MODE>SINGLE_BEST_COVER</MODE>\n" +
                "    <OPTION>\n" +
                "      <PARAMETER>SELECT_DETAIL</PARAMETER>\n" +
                "      <VALUE>GENRE:3LEVEL</VALUE>\n" +
                "    </OPTION>\n" +
                "    <OPTION>\n" +
                "      <PARAMETER>COVER_SIZE</PARAMETER>\n" +
                "      <VALUE>medium</VALUE>\n" +
                "    </OPTION>\n" +
                "  </QUERY>\n" +
                "</QUERIES>";

        //Log.i("Qeury: ",initReq);
        return initReq;
    }


    //method to post request to the GracenoteWebAPI
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
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
