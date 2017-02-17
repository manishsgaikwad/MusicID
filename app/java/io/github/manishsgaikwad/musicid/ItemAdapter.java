package io.github.manishsgaikwad.musicid;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;


import java.util.ArrayList;
import java.util.List;



/**
 * Created by manish on 30-11-2016.
 */

class ItemAdapter extends RecyclerView.Adapter<ItemHolder> implements Filterable {


    private List<MusicRetriever.Item> mItems;

    private View v;

    private List<MusicRetriever.Item> itemsCopy;




    public  ItemAdapter(List<MusicRetriever.Item> items){
        mItems = items;
        itemsCopy = items;
    }

    public int getItemCount() {
        if(mItems == null) return 0;

        return mItems.size();

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row,parent,false);


        return new ItemHolder(v);
    }



    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

       holder.bind(mItems.get(position));


    }

    public void clear(){
        this.mItems.clear();
    }

    public void updateItems(List<MusicRetriever.Item> items){
        clear();
       this.mItems=items;
        this.itemsCopy=items;
        notifyDataSetChanged();
    }

    public List<MusicRetriever.Item> getUpdatedItems(){
        return this.mItems;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
               // List<MusicRetriever.Item> temp;
                    mItems = (List<MusicRetriever.Item>) filterResults.values;
                    ItemAdapter.this.notifyDataSetChanged();
                //updateItems(temp);
            }


            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<MusicRetriever.Item> filteredResults;
                if(charSequence.length()==0){
                    filteredResults = itemsCopy;
                }
                else {
                    filteredResults = getFilteredResults(charSequence.toString().toLowerCase());
                }
                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }


        };
    }


    private List<MusicRetriever.Item> getFilteredResults(String constraint) {
        List<MusicRetriever.Item> results = new ArrayList<>();



            for (MusicRetriever.Item item : itemsCopy) {
                if (item.getTitle().toLowerCase().contains(constraint) || item.getArtist().toLowerCase().contains(constraint)) {
                    results.add(item);
                }
            }


        return results;
    }

}
