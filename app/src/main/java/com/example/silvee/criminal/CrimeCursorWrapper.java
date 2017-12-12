package com.example.silvee.criminal;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.silvee.criminal.database.CrimeDbSchema;
import com.example.silvee.criminal.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by silvee on 11.12.2017.
 */

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String titleString = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long dateLong = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int solved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspectString = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(titleString);
        crime.setDate(new Date(dateLong));
        crime.setSolved(solved != 0);
        crime.setSuspect(suspectString);

        return crime;
    }
}
