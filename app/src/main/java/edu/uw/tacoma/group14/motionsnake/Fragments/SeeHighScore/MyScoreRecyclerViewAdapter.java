package edu.uw.tacoma.group14.motionsnake.Fragments.SeeHighScore;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tacoma.group14.motionsnake.Fragments.SeeHighScore.Score.Score;
import edu.uw.tacoma.group14.motionsnake.R;

import java.util.List;

/**
 * The View adapter to display list of score.
 * {@link RecyclerView.Adapter} that can display a {@link Score} and makes a call to the
 * Created by Justin Arnett Son Vu on 5/31/2016.
 */
public class MyScoreRecyclerViewAdapter extends RecyclerView.Adapter<MyScoreRecyclerViewAdapter.ViewHolder> {

    private final List<Score> mValues;
    //private final ScoreFragment.OnListFragmentInteractionListener mListener;

    public MyScoreRecyclerViewAdapter(List<Score> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getmUserId());
        holder.mScoreView.setText(mValues.get(position).getmScore());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Class to create a holder for the score.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mScoreView;
        public Score mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mScoreView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mScoreView.getText() + "'";
        }
    }
}
