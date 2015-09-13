package com.formakidov.rssreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.fragment.NewsListFragment;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final NewsListFragment fragment;
    private final List<RssItem> items;

    public NewsAdapter(NewsListFragment fragment, List<RssItem> items) {
        this.fragment = fragment;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_news, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        RssItem item = items.get(position);
        String strTitle = item.getTitle().isEmpty() ? item.getDefTitle() : item.getTitle();
        viewHolder.title.setText(strTitle);
        viewHolder.pubDate.setText(item.getFormattedPubDate());
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

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            pubDate = (TextView) itemView.findViewById(R.id.pubDate);
        }
    }
}
