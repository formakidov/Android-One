package com.formakidov.rssreader.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.formakidov.rssreader.DatabaseManager;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.data.RssItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public void reset(final List<RssItem> result) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        List<RssItem> saved = manager.getSavedNews(result.get(0).getRssUrl());
        items.clear();
        if (null != saved && saved.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                RssItem item = result.get(i);
                for (RssItem savedItem : saved) {
                    if (savedItem.getPubDate().equals(item.getPubDate()) &&
                            savedItem.getTitle().equals(item.getTitle()) &&
                            savedItem.getDescription().equals(item.getDescription())) {
                        result.remove(i);
                        i--;
                        break;
                    }
                }
            }
            items.addAll(saved);
        }
        items.addAll(result);
        Collections.sort(items, new NewsComparator());
        notifyDataSetChanged();
        saveNewsInDatabase(result);
    }

    private void saveNewsInDatabase(final List<RssItem> items) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatabaseManager manager = DatabaseManager.getInstance(context);
                manager.resetNews(items.get(0).getRssUrl(), items);
                return null;
            }
        }.execute();
    }

    private List<RssItem> getSavedNews() {
        List<RssItem> saved = new ArrayList<>();
        for (RssItem item : items) {
            if (item.isSaved()) {
                saved.add(item);
            }
        }
        return saved;
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

    public class NewsComparator implements Comparator<RssItem> {
        public int compare(RssItem object1, RssItem object2) {
            return object1.getPubDateMs() < object2.getPubDateMs() ? 1 : -1;
        }
    }
}