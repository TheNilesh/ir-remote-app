package io.github.thenilesh.httpirremote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.github.thenilesh.httpirremote.dao.AppDatabase;
import io.github.thenilesh.httpirremote.dto.AppConfig;
import io.github.thenilesh.httpirremote.dto.RButton;
import io.github.thenilesh.httpirremote.utils.IRRemoteService;
import io.github.thenilesh.httpirremote.utils.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nilesh on 15/4/18.
 */
public class RButtonAdapter extends RecyclerView.Adapter<RButtonAdapter.RButtonViewHolder> {

    private final Context context;
    private List<RButton> rButtons;

    public RButtonAdapter(List<RButton> rButtons, Context context) {
        this.rButtons = rButtons;
        this.context = context;
    }

    @Override
    public RButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rbutton, parent, false);
        RButtonViewHolder rButtonViewHolder = new RButtonViewHolder(itemView);
        itemView.setOnClickListener(rButtonViewHolder);
        itemView.setOnLongClickListener(rButtonViewHolder);
        return rButtonViewHolder;
    }

    @Override
    public void onBindViewHolder(RButtonViewHolder holder, int position) {
        RButton rButton = rButtons.get(position);
        holder.tvName.setText(rButton.getName());
        holder.ivThumbnail.setImageURI(rButton.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return rButtons.size();
    }

    public class RButtonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView tvName;
        ImageView ivThumbnail;
        public RButtonViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.rbutton_name);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos != -1) {
                //TODO:Multiple ir codes feature
                RButton rButton = rButtons.get(pos);
                Log.d("Adapter", "Clicked: " + rButton.getName());
                (new AsyncTask<RButton, Void, Response<Void>>() {

                    @Override
                    protected Response<Void> doInBackground(RButton... rButtons) {
                        RButton rButton = rButtons[0];
                        AppConfig appConfig = AppDatabase.getInstance(context).configDao().load();
                        String url = appConfig.getBaseUrl();
                        IRRemoteService irRemoteService = ServiceGenerator.create(IRRemoteService.class);
                        Call<Void> blastResponse = irRemoteService.sendIrCode(url, rButton.getIrCode());
                        try {
                            Response<Void> response = blastResponse.execute();
                            return response;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Response<Void> response) {
                        super.onPostExecute(response);
                        Toast.makeText(context, "Buttons sent",
                                Toast.LENGTH_LONG).show();
                    }
                }).execute();

            }
        }

        @Override
        public boolean onLongClick(View view) {
            int pos = getAdapterPosition();
            if (pos != -1) {
                RButton rButton = rButtons.get(pos);
                Log.d("Adapter", "LongClicked: " + rButton.getName());
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                DialogInterface.OnClickListener dialogClickListener = new ButtonDeleteConfirmationListener(rButton);
                builder.setMessage("Are you sure to remove?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
            return true;
        }
    }

    class ButtonDeleteConfirmationListener implements DialogInterface.OnClickListener {

        private final RButton rButton;

        public ButtonDeleteConfirmationListener(RButton rButton) {
            this.rButton = rButton;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Log.d("Adapter", "Deleting button");
                    (new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            File thumbFile = new File(rButton.getThumbnail().getPath());
                            Log.d("Adapter", "Deleting: " + thumbFile.getAbsolutePath());
                            thumbFile.delete();
                            AppDatabase.getInstance(context).rButtonDao().delete(rButton);
                            return null;
                        }
                    }).execute();
                    rButtons.remove(rButton);
                    notifyDataSetChanged();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    Log.d("Adapter", "Cancelled remove");
                    break;
            }

        }
    }
}
