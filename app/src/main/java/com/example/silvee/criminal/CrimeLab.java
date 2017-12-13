package com.example.silvee.criminal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.silvee.criminal.database.CrimeDatabaseHelper;
import com.example.silvee.criminal.database.CrimeDbSchema;
import com.example.silvee.criminal.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by silvee on 22.11.2017.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    // Private constructor
    private CrimeLab(Context context) {
//        mCrimes = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            Crime crime = new Crime();
//            crime.setTitle("Crime #" + i);
//            crime.setSolved(i % 2 == 0); // for each second object
//            if (i % 6 == 0) crime.setRequirePolice(true); // for each 6th object
//            mCrimes.add(crime);
//        }
        mContext = context.getApplicationContext();
        mDatabase = new CrimeDatabaseHelper(mContext).getWritableDatabase();
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public void add(Crime crime) {
        mDatabase.insert(CrimeTable.NAME, null, getContentValues(crime));
    }

    public void remove(Crime crime) {
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[] {crime.getId().toString()});
    }

    public void update(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues cv = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, cv, CrimeTable.Cols.UUID + " = ?", new String[] {uuidString});
    }

    public ContentValues getContentValues(Crime crime) {
        ContentValues cv = new ContentValues();
        cv.put(CrimeTable.Cols.UUID, crime.getId().toString());
        cv.put(CrimeTable.Cols.TITLE, crime.getTitle());
        cv.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        cv.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        cv.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return cv;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(CrimeTable.NAME, null,
                                        whereClause, whereArgs, null, null, null);

        return new CrimeCursorWrapper(cursor);
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " = ?", new String[] {id.toString()});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    // get all strings from database, convert them to Crime objects and them to List
    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public File getPhotoFile(Crime crime) {
        File photoFile = mContext.getFilesDir();
        return new File(photoFile, crime.getPhotoFilename());
    }
}