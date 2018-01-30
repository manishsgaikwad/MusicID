package io.github.manishsgaikwad.musicid;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;


/**
 * Created by manish on 30-11-2016.
 */

public class ItemHolder extends RecyclerView.ViewHolder  {

    private TextView textView;
    private ImageView imageView;
    private TextView textView2;
    private TextView textView3;
    Context context;



    public ItemHolder(final View itemView) {
        super(itemView);
        context = itemView.getContext();
        textView = (TextView)itemView.findViewById(R.id.filename);
        imageView = (ImageView)itemView.findViewById(R.id.albumart);
        textView2 = (TextView)itemView.findViewById(R.id.artist);
        textView3 = (TextView) itemView.findViewById(R.id.duration);
        textView.setTextColor(Color.DKGRAY);



    }




    void bind(final MusicRetriever.Item item) {

            long duration = item.getDuration();
            textView.setText(item.getTitle());
            textView.setTextColor(Color.WHITE);
            textView2.setText(item.getArtist());
            textView3.setText(TimeUnit.MILLISECONDS.toMinutes(duration) + ":" + (TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
        if(item.albumartUri == null){
            Picasso.with(context).load(R.drawable.default_art).fit().into(imageView);
        }
        else {
            Picasso.with(context).load(item.albumartUri).fit().error(R.drawable.default_art).into(imageView);
        }


    }

}
