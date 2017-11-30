package com.example.silvee.criminal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    public static final String EXTRA_CRIME_ID = "com.example.silvee.criminal.crime_id";

    private ViewPager viewPager;
    private List<Crime> mCrimes;
    private Button buttonNext;
    private Button buttonPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        viewPager = findViewById(R.id.crime_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();

        // Buttons Next and Previous
        buttonNext = findViewById((R.id.next_crime_button));
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 2) {
                    buttonNext.setEnabled(false);
                } else if (viewPager.getCurrentItem() == 0) {
                    buttonPrevious.setEnabled(true);
                }
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });
        buttonPrevious = findViewById(R.id.prev_crime_button);
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 1) {
                    buttonPrevious.setEnabled(false);
                } else if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                    buttonNext.setEnabled(true);
                }
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1,  true );
            }
        });

        // Setting Adapter for ViewPager
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });


        // Open Crime that was clicked, not the first
        UUID crimeID = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(crimeID)) {
                viewPager.setCurrentItem(mCrimes.indexOf(crime));
                if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) buttonNext.setEnabled(false);
                if (viewPager.getCurrentItem() == 0) buttonPrevious.setEnabled(false);
                break;
            }
        }
    }

    public static Intent newIntent(Context packageContext, UUID CrimeID) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, CrimeID);
        return intent;
    }

}
