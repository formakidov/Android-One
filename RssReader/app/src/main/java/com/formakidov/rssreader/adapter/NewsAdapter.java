package com.formakidov.rssreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.formakidov.rssreader.DatabaseManager;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.data.RssItem;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private final List<RssItem> items;
    private final Context context;

    public NewsAdapter(Context context, List<RssItem> items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_news, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final RssItem item = items.get(position);
        String strTitle = item.getTitle().isEmpty() ? item.getDefTitle() : item.getTitle();
        viewHolder.title.setText(strTitle);
        viewHolder.pubDate.setText(item.getFormattedPubDate());
        viewHolder.favorite.setImageResource(item.isSaved() ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite);
        viewHolder.favouriteParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean curStatus = !item.isSaved();
                item.setSaved(curStatus);
                viewHolder.favorite.setImageResource(curStatus ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite);
                DatabaseManager manager = DatabaseManager.getInstance(context);
                manager.updateNews(item);
                notifyItemChanged(items.indexOf(item));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public RssItem getItem(int position) {
        return items.get(position);
    }

    public void reset(List<RssItem> result) {
        items.clear();
        items.addAll(result);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView pubDate;
        public final ImageView favorite;
        public final View favouriteParentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            pubDate = (TextView) itemView.findViewById(R.id.pubDate);
            favorite = (ImageView) itemView.findViewById(R.id.favorite);
            favouriteParentLayout = itemView.findViewById(R.id.info_layout);
        }
    }
}
