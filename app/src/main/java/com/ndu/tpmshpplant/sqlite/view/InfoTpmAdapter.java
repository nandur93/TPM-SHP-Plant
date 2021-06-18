package com.ndu.tpmshpplant.sqlite.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ndu.tpmshpplant.R;
import com.ndu.tpmshpplant.sqlite.database.DatabaseHelper;
import com.ndu.tpmshpplant.sqlite.database.model.InfoTpm;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InfoTpmAdapter extends RecyclerView.Adapter<InfoTpmAdapter.InfoTpmViewHolder> {

    private final List<InfoTpm> infoTpmList;

    public static class InfoTpmViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtContentId;
        public final ImageView txtIconLink;
        public final TextView txtTitle;
        public final TextView txtDescription;
        public final TextView txtAuthor;
        public final TextView dtmPublishDate;

        /*txtContentId;
        txtIconLink;
        txtTitle;
        txtDescription;
        txtThumbnail;
        txtAuthor;
        dtmPublishDate;*/
        //public TextView dot;
        //public TextView timestamp;

        public InfoTpmViewHolder(View view) {
            super(view);
            txtContentId = view.findViewById(R.id.tv_Id);
            txtIconLink = view.findViewById(R.id.iv_Thumbnail);
            txtTitle = view.findViewById(R.id.tv_Judul);
            txtDescription = view.findViewById(R.id.tv_Desc);
            txtAuthor = view.findViewById(R.id.tv_Author);
            dtmPublishDate = view.findViewById(R.id.tv_PublishedDate);
        }
    }


    public InfoTpmAdapter(List<InfoTpm> infoTpmList) {
        this.infoTpmList = infoTpmList;
    }

    @NonNull
    @Override
    public InfoTpmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tpm_info_list_row, parent, false);

        return new InfoTpmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InfoTpmViewHolder holder, final int position) {
        InfoTpm infoTpm = infoTpmList.get(position);
        holder.txtContentId.setText(infoTpm.getTxtContentId());
        try {
            Picasso picasso = Picasso.get();
            picasso.setIndicatorsEnabled(true);
            picasso.load(infoTpm.getTxtIconLink()).into(holder.txtIconLink);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.txtDescription.setText(infoTpm.getTxtDescription());
        holder.txtAuthor.setText(infoTpm.getTxtAuthor());
        holder.dtmPublishDate.setText(infoTpm.getDtmPublishDate());
        String s = infoTpm.getTxtTitle();
        try {
            if (s.length() <= 23) {
                holder.txtTitle.setText(s);
            } else {
                holder.txtTitle.setText(s.substring(0, 23).concat("..."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Displaying dot from HTML character code
        //holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        //holder.timestamp.setText(formatDate(asset.getTimestamp()));
        holder.itemView.setOnClickListener(v -> Log.d("TAG", "onClick: " + position));
    }

    @Override
    public int getItemCount() {
        return infoTpmList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
/*    private String formatDate(String dateStr) {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(Objects.requireNonNull(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }*/

    /*https://stackoverflow.com/questions/42363135/get-position-of-specific-cardview-in-recyclerview-without-clicking-scrolling*/
    public int getContentIdPosition(String contentId) {
        for (int i = 0; i < infoTpmList.size(); i++) {
            if (infoTpmList.get(i).getTxtContentId().equals(contentId)) {
                return i;
            }
        }
        return 0;
    }

    public String getInfoTitle(int position) {
        return infoTpmList.get(position).getTxtTitle();
    }

    public String getInfoDesc(int position) {
        return infoTpmList.get(position).getTxtDescription();
    }

    /*https://stackoverflow.com/a/37562572/7772358*/
    public void filter(String text, DatabaseHelper db) {
        infoTpmList.clear();
        if (text.isEmpty()) {
            infoTpmList.addAll(db.getAllInfoTpm());
        } else {
            text = text.toLowerCase();
            try {
                for (InfoTpm infoTpm : db.getAllInfoTpm()) {
                    if (
                            infoTpm.getTxtContentId().toLowerCase().contains(text) ||
                                    infoTpm.getTxtAuthor().toLowerCase().contains(text) ||
                                    infoTpm.getTxtDescription().toLowerCase().contains(text) ||
                                    infoTpm.getTxtTitle().toLowerCase().contains(text) ||
                                    infoTpm.getDtmPublishDate().toLowerCase().contains(text)) {
                        infoTpmList.add(infoTpm);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }
}
