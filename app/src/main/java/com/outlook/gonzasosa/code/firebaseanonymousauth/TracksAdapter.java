package com.outlook.gonzasosa.code.firebaseanonymousauth;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.outlook.gonzasosa.code.firebaseanonymousauth.Models.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.TracksViewHolder> {
    private ArrayList<Track> tracks;
    private Context context;
    //MediaPlayer mediaPlayer;
    public static String canci;

    TracksAdapter (Context ctx, ArrayList<Track> t) {
        tracks = t;
        context = ctx;
    }

    @Override
    public TracksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.track_item, null, false);
        return new TracksViewHolder (view);
    }

   /* void play(String ruta) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource("/storage/6FC2-7617/musica/" + ruta+".mp3");
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }*/

        @Override
    public void onBindViewHolder (TracksViewHolder holder, int position) {
        final Track track = tracks.get (position);
        holder.setData (track.title, track.album, track.year);
        holder.itemView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                //se crea el intent para pasar la informacion a la clase servicios
               final Intent intent = new Intent(context, my_services.class);
                try {
                    getSong (track.title);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //se le dice al contexto que comienze la ejecucion de servicios
                context.startService(intent);
            }
        });
    }

    @Override
    public int getItemCount () {
        return tracks.size ();
    }




    class TracksViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAlbum, tvYear;

        TracksViewHolder (View itemView) {
            super (itemView);

            tvTitle = itemView.findViewById (R.id.tvTitle);
            tvAlbum = itemView.findViewById (R.id.tvAlbum);
            tvYear = itemView.findViewById (R.id.tvYear);
        }


        void setData (String title, String album, int year) {
            tvTitle.setText (title);
            tvAlbum.setText (album);

            String s = String.format (Locale.US, "%s", year);
            tvYear.setText (s);
        }
    }

    /*private void getSongsList () {
        ContentResolver musicResolver = context.getContentResolver ();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String [] myProjection = {
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST
        };

        Cursor musicCursor = musicResolver.query (musicUri, myProjection, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst ()) {
            int titleColumn = musicCursor.getColumnIndex (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex (MediaStore.Audio.Media.ARTIST);

            do {
                String output = String.format (Locale.US,
                        "Title: %s, Artist: %s,  Album: %s", musicCursor.getString (titleColumn),
                        musicCursor.getString (artistColumn), musicCursor.getString (idColumn));

                Log.d (MainActivity.TAG, output);

            } while (musicCursor.moveToNext ());
        }

        if (musicCursor != null) musicCursor.close ();
    }*/

    private void getSong (String song) throws IOException {
        ContentResolver musicResolver = context.getContentResolver ();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // obtiene la direccion de la cancion donde se hizo el onclick

        String [] myProjection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST
        };

        String mySelectionClause = String.format (Locale.US, "%s = ?", MediaStore.Audio.Media.TITLE);
        String [] mySelectionArgs = new String [] {song};

        Cursor musicCursor = musicResolver.query (musicUri, myProjection, mySelectionClause, mySelectionArgs, null);

        if (musicCursor != null && musicCursor.moveToFirst ()) {
            int titleColumn = musicCursor.getColumnIndex (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex (MediaStore.Audio.Media.ARTIST);

            do {
                //apartir de este punto se tiene un contexto, ruta, nombre de la cancion
                String output = String.format (Locale.US,"%s" , musicCursor.getString (titleColumn));
                canci= output;

                //play(output);
                Toast.makeText(context, output, Toast.LENGTH_LONG).show ();


            } while (musicCursor.moveToNext ());
        }

        if (musicCursor != null) musicCursor.close ();
    }
}
