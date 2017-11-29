package com.example.silvee.criminal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by silvee on 28.11.2017.
 */

public class CrimeActivity extends SingleFragmentActivity {

    public static final String EXTRA_CRIME_ID = "com.example.silvee.criminal.crime_id";

    @Override
    protected Fragment createFragment() {
        UUID mID = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(mID);
    }

    public static Intent newIntent(Context packageContext, UUID CrimeID) {
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, CrimeID);
        return intent;
    }
}
