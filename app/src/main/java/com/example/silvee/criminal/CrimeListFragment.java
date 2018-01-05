package com.example.silvee.criminal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

/**
 * Created by silvee on 22.11.2017.
 */

public class CrimeListFragment extends Fragment {
    private static final int REQUIRE_POLICE = 1;
    private static final int NOT_REQUIRE_POLICE = 0;
    private static final int REQUEST_CRIME = 1;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private Callbacks callbacks;
    private RecyclerView mCrimeRecyclerView;
    private TextView mEmptyListTextView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mEmptyListTextView = view.findViewById(R.id.empty_list_textview);

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    // Get result containing id of Crime from CrimeFragment and update item that was changed
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CRIME) {
            if (resultCode == Activity.RESULT_OK) {
                int pos = data.getExtras().getInt(CrimeFragment.ARG_CRIME_ID);
                updateSingleView(pos);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem menuItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            menuItem.setTitle(R.string.hide_subtitle);
        } else {
            menuItem.setTitle(R.string.show_subtitle);
        }
    }

    // Item of Option menu clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).add(crime);
                callbacks.onCrimeSelected(crime);
                updateUI();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Show number of crimes in action bar
    private void updateSubtitle() {
        int nCrimes = CrimeLab.get(getActivity()).getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, nCrimes, nCrimes);
        if (!mSubtitleVisible) subtitle = null;
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private abstract class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitleTextView;
        TextView mDateTextview;
        private Crime mCrime;

        private CrimeHolder(View v) {
            super(v);
            itemView.setOnClickListener(this);
        }

        private void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextview.setText(DateFormat.format("EEE, MMM d, yyyy", mCrime.getDate()));
        }

        // start CrimePagerActivity if item clicked
        @Override
        public void onClick(View v) {
            callbacks.onCrimeSelected(mCrime);
        }
    }

    private class SimpleCrimeHolder extends CrimeHolder {

        // Constructor
        private SimpleCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            mTitleTextView = itemView.findViewById(R.id.crimeTitle);
            mDateTextview = itemView.findViewById(R.id.crimeDate);
        }
    }

    // CrimeHolder with extra "Send police" button
    private class PoliceCrimeHolder extends CrimeHolder {
        Button mSendPoliceButton;

        private PoliceCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_police_crime, parent, false));
            mTitleTextView = itemView.findViewById(R.id.policeCrimeTitle);
            mDateTextview = itemView.findViewById(R.id.policeCrimeDate);
            mSendPoliceButton = itemView.findViewById(R.id.buttonSendPolice);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        // Constructor
        private CrimeAdapter(List<Crime> crimes) {
            this.mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            switch (viewType) {
                case REQUIRE_POLICE: return new PoliceCrimeHolder(layoutInflater, parent);
                case NOT_REQUIRE_POLICE: return new SimpleCrimeHolder(layoutInflater, parent);
                default: return new SimpleCrimeHolder(layoutInflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.get(position);
            if (crime.requirePolice()) return REQUIRE_POLICE;
            else return NOT_REQUIRE_POLICE;
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

        public void remove(int pos) {
            CrimeLab.get(getActivity()).remove(mCrimes.get(pos));
            updateUI();
            Crime crime = null;

            // if first element was removed - show first, else show the previous one
            if (!mCrimes.isEmpty()) {
                crime = (pos != 0) ? (mCrimes.get(pos - 1)) : (mCrimes.get(pos));
            }
            callbacks.onCrimeSelected(crime);
        }
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (crimes.isEmpty()) {
           mEmptyListTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyListTextView.setVisibility(View.INVISIBLE);
        }
        if (mAdapter == null) {
            // Create adapter and link it to recyclerview
            mAdapter = new CrimeAdapter(crimes);
            ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
                    ItemTouchHelper.START | ItemTouchHelper.END) {
                @Override
                public boolean onMove(RecyclerView recyclerView,
                                      RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    mAdapter.remove(viewHolder.getAdapterPosition());
                }
            });
            helper.attachToRecyclerView(mCrimeRecyclerView);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            // Update items
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();

        }
        updateSubtitle();
    }

    private void updateSingleView(int pos) {
        mAdapter.notifyItemChanged(pos);
    }


}
