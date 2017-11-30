package com.example.silvee.criminal;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
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

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CRIME) {
            if (resultCode == Activity.RESULT_OK) {
                int pos = data.getExtras().getInt(CrimeFragment.ARG_CRIME_ID);
                updateSingleView(pos);
            }
        }
    }

    private abstract class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitleTextView;
        TextView mDateTextview;
        protected Crime mCrime;

        public CrimeHolder(View v) {
            super(v);
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextview.setText(DateFormat.format("EEE, MMM d, yyyy", mCrime.getDate()));
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            intent.putExtra("pos", getLayoutPosition());
            startActivityForResult(intent, REQUEST_CRIME);
        }
    }

    private class SimpleCrimeHolder extends CrimeHolder{

        // Constructor
        public SimpleCrimeHolder(LayoutInflater inflater, ViewGroup parent)  {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            mTitleTextView = itemView.findViewById(R.id.crimeTitle);
            mDateTextview = itemView.findViewById(R.id.crimeDate);
        }
    }

    private class PoliceCrimeHolder extends CrimeHolder {
        Button mSendPoliceButton;

        public PoliceCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_police_crime, parent, false));
            mTitleTextView = itemView.findViewById(R.id.policeCrimeTitle);
            mDateTextview = itemView.findViewById(R.id.policeCrimeDate);
            mSendPoliceButton = itemView.findViewById(R.id.buttonSendPolice);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        // Constructor
        public CrimeAdapter(List<Crime> crimes) {
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
    }


    // Create adapter and link it to recyclerview
    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateSingleView(int pos) {
        mAdapter.notifyItemChanged(pos);
    }
}
