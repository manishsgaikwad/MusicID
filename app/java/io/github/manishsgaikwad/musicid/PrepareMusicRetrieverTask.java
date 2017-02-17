package io.github.manishsgaikwad.musicid;

/**
 * Created by manish on 02-12-2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

/**
 * Asynchronous task that prepares a MusicRetriever. This asynchronous task essentially calls
 * {@link MusicRetriever#prepare()} on a {@link MusicRetriever}, which may take some time to
 * run. Upon finishing, it notifies the indicated {@MusicRetrieverPreparedListener}.
 */
public class PrepareMusicRetrieverTask extends AsyncTask<List<MusicRetriever.Item>, Void, List<MusicRetriever.Item>> {

    MusicRetriever mRetriever;
    ItemAdapter mItemAdapter;
    List<MusicRetriever.Item> items;
    RecyclerView recyclerView;
    Context context;
    int sP;
    ProgressBar progressBar;

    public PrepareMusicRetrieverTask(MusicRetriever retriever, RecyclerView rcv, Context context, int sP,ProgressBar pb,ItemAdapter ia) {
        this.mRetriever = retriever;
        this.recyclerView = rcv;
        this.context = context;
        this.sP = sP;
        this.progressBar=pb;
        this.mItemAdapter = ia;
    }



    @Override
    protected void onPostExecute(List<MusicRetriever.Item> items) {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
            if(hasSongs()) {

                mItemAdapter.updateItems(this.items);
               // recyclerView.setLayoutManager(new LinearLayoutManager(context));
              //  mItemAdapter = new ItemAdapter(this.items);
               // recyclerView.setAdapter(new AlphaInAnimationAdapter(mItemAdapter));
               // recyclerView.setHasFixedSize(true);
                // mItemAdapter.updateItems(items);
                // mItemAdapter.notifyDataSetChanged();
                if (this.sP != 0) {
                    recyclerView.getLayoutManager().scrollToPosition(sP);
                }
            }
        else{
                Toast.makeText(context,"No Songs Found",Toast.LENGTH_LONG).show();
            }

        super.onPostExecute(items);
    }



    @SafeVarargs
    @Override

    protected final List<MusicRetriever.Item> doInBackground(List<MusicRetriever.Item>... params) {
        mRetriever.prepare();
        items = mRetriever.getItems();
        params[0] = items;
        //items = params[0];
       // SortItems sortItems = new SortItems();
        //sortItems.sort(items);
        return params[0];
    }

    @Override
    protected void onPreExecute() {
       recyclerView.setVisibility(View.INVISIBLE);


        super.onPreExecute();
    }

    private boolean hasSongs(){
        if(!this.items.isEmpty()) return true;

        return false;
    }

}