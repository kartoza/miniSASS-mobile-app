package com.rk.amii.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.rk.amii.models.PhotoModel;
import com.rk.amii.models.AssessmentModel;
import com.rk.amii.models.SitesModel;
import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "minisassdb";
    private static final int DB_VERSION = 2;

    private static final String SITES_TABLE_NAME = "sites";
    private static final String SITES_ID = "id";
    private static final String SITES_SITE_NAME = "site_name";
    private static final String SITES_SITE_LOCATION = "site_location";
    private static final String SITES_RIVER_NAME = "river_name";
    private static final String SITES_DESCRIPTION = "description";
    private static final String SITES_RIVER_TYPE = "river_type";
    private static final String SITES_DATE = "date";
    private static final String SITES_SITE_ONLINE_ID = "online_id";

    private static final String ASSESSMENT_TABLE_NAME = "assessments";
    private static final String ASSESSMENT_ID = "id";
    private static final String ASSESSMENT_MINISASS_SCORE = "score";
    private static final String ASSESSMENT_ML_SCORE = "ml_score";
    private static final String ASSESSMENT_NOTES = "notes";
    private static final String ASSESSMENT_PH = "ph";
    private static final String ASSESSMENT_WATER_TEMP = "water_temp";
    private static final String ASSESSMENT_DISSOLVED_OXYGEN = "dissolved_oxygen";
    private static final String ASSESSMENT_DISSOLVED_OXYGEN_UNIT = "dissolved_oxygen_unit";
    private static final String ASSESSMENT_WATER_CLARITY = "water_clarity";
    private static final String ASSESSMENT_ELECTRICAL_CONDUCTIVITY = "electrical_conductivity";
    private static final String ASSESSMENT_ELECTRICAL_CONDUCTIVITY_UNIT = "electrical_conductivity_unit";

    private static final String SITE_ASSESSMENT_TABLE_NAME = "site_assessments";
    private static final String SITE_ASSESSMENT_ID = "id";
    private static final String SITE_ASSESSMENT_SITE_ID = "site_id";
    private static final String SITE_ASSESSMENT_ASSESSMENT_ID = "assessment_id";

    private static final String PHOTO_TABLE_NAME = "photos";
    private static final String PHOTO_ID = "id";
    private static final String PHOTO_ASSESSMENT_ID = "assessment_id";
    private static final String PHOTO_LOCATION = "location";
    private static final String PHOTO_USER_CHOICE = "user_choice";
    private static final String PHOTO_ML_PREDICTIONS = "ml_predictions";

    private static final String SITE_IMAGE_TABLE_NAME = "site_photos";
    private static final String SITE_IMAGE_ID = "id";
    private static final String SITE_IMAGE_SITE_ID = "site_id";
    private static final String SITE_IMAGE_LOCATION = "image_location";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + SITES_TABLE_NAME + " ("
                + SITES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SITES_SITE_NAME + " TEXT,"
                + SITES_SITE_LOCATION + " TEXT,"
                + SITES_RIVER_NAME + " TEXT,"
                + SITES_DESCRIPTION + " TEXT,"
                + SITES_RIVER_TYPE + " TEXT,"
                + SITES_DATE + " TEXT,"
                + SITES_SITE_ONLINE_ID + " INTEGER)";
        db.execSQL(query);

        String assessmentQuery = "CREATE TABLE " + ASSESSMENT_TABLE_NAME + " ("
                + ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ASSESSMENT_MINISASS_SCORE + " TEXT,"
                + ASSESSMENT_ML_SCORE + " TEXT,"
                + ASSESSMENT_NOTES + " TEXT,"
                + ASSESSMENT_PH + " TEXT,"
                + ASSESSMENT_WATER_TEMP + " TEXT,"
                + ASSESSMENT_DISSOLVED_OXYGEN + " TEXT,"
                + ASSESSMENT_DISSOLVED_OXYGEN_UNIT + " TEXT,"
                + ASSESSMENT_ELECTRICAL_CONDUCTIVITY + " TEXT,"
                + ASSESSMENT_ELECTRICAL_CONDUCTIVITY_UNIT + " TEXT,"
                + ASSESSMENT_WATER_CLARITY + " TEXT)";

        db.execSQL(assessmentQuery);

        String siteAssessmentQuery = "CREATE TABLE " + SITE_ASSESSMENT_TABLE_NAME + " ("
                + SITE_ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SITE_ASSESSMENT_SITE_ID + " INTEGER,"
                + SITE_ASSESSMENT_ASSESSMENT_ID + " INTEGER)";

        db.execSQL(siteAssessmentQuery);

        String sitePhotoQuery = "CREATE TABLE " + SITE_IMAGE_TABLE_NAME + " ("
                + SITE_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SITE_IMAGE_SITE_ID + " INTEGER, "
                + SITE_IMAGE_LOCATION + " TEXT)";

        db.execSQL(sitePhotoQuery);

        String photo_query = "CREATE TABLE " + PHOTO_TABLE_NAME + " ("
                + PHOTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PHOTO_ASSESSMENT_ID + " INTEGER,"
                + PHOTO_LOCATION + " TEXT,"
                + PHOTO_USER_CHOICE + " TEXT,"
                + PHOTO_ML_PREDICTIONS + " TEXT)";

        db.execSQL(photo_query);
    }

    /**
     * Add a new site to the database
     * @param siteName site name
     * @param siteLocation site location
     * @param riverName river name
     * @param description site description
     * @param date date created
     * @param riverType river type
     * @return The site id
     */
    public long addNewSite(String siteName, String siteLocation, String riverName,
                           String description, String date, String riverType, Boolean uploaded) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SITES_SITE_NAME, siteName);
        values.put(SITES_SITE_LOCATION, siteLocation);
        values.put(SITES_RIVER_NAME, riverName);
        values.put(SITES_DESCRIPTION, description);
        values.put(SITES_DATE, date);
        values.put(SITES_RIVER_TYPE, riverType);
        values.put(SITES_SITE_ONLINE_ID, 0);
        long id = db.insert(SITES_TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /**
     * Remove a site from the database based on the site ID
     * @param siteId The site's id
     * @return Id of the site removed
     */
    public long deleteSite(String siteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.delete(SITES_TABLE_NAME, "id = ?", new String[]{siteId});
        db.close();
        return id;
    }

    /**
     * Update a site in the database with new values
     * @param siteId The site id
     * @param siteName The site name
     * @param siteLocation The site location
     * @param riverName The river name
     * @param description The site description
     * @param date The date created
     * @param riverType The river type
     * @return Id of the updated site
     */
    public int updateSite(String siteId, String siteName, String siteLocation, String riverName,
                           String description, String date, String riverType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SITES_SITE_NAME, siteName);
        values.put(SITES_SITE_LOCATION, siteLocation);
        values.put(SITES_RIVER_NAME, riverName);
        values.put(SITES_DESCRIPTION, description);
        values.put(SITES_DATE, date);
        values.put(SITES_RIVER_TYPE, riverType);
        int updated = db.update(SITES_TABLE_NAME, values, "id = ?", new String[]{siteId});

        db.close();
        return updated;
    }

    /**
     * Update the site, set online site id value
     * @param siteId site id
     * @param onlineId online site id
     * @return Id of the uploaded site
     */
    public int updateSiteUploaded(String siteId, Integer onlineId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SITES_SITE_ONLINE_ID, onlineId);
        int updated = db.update(SITES_TABLE_NAME, values, "id = ?", new String[]{siteId});
        db.close();
        return updated;
    }

    /**
     * Add a new assessment to the database
     * @param miniSassScore The user score
     * @param mlScore The ml score
     * @param notes The site notes
     * @param ph The water ph reading
     * @param waterTemp The water temp reading
     * @param dissolvedOxygen The water dissolved oxygen reading
     * @param dissolvedOxygenUnit The water dissolved oxygen unit
     * @param electricalConductivity The water electrical conductivity reading
     * @param electricalConductivityUnit The water electrical conductivity unit
     * @param waterClarity The water clarity reading
     * @return The newly added assessment's id
     */
    public long addNewAssessment(String miniSassScore, String mlScore, String notes, String ph,
                                 String waterTemp, String dissolvedOxygen, String dissolvedOxygenUnit,
                                 String electricalConductivity, String electricalConductivityUnit,
                                 String waterClarity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ASSESSMENT_MINISASS_SCORE, miniSassScore);
        values.put(ASSESSMENT_ML_SCORE, mlScore);
        values.put(ASSESSMENT_NOTES, notes);
        values.put(ASSESSMENT_PH, ph);
        values.put(ASSESSMENT_WATER_TEMP, waterTemp);
        values.put(ASSESSMENT_DISSOLVED_OXYGEN, dissolvedOxygen);
        values.put(ASSESSMENT_DISSOLVED_OXYGEN_UNIT, dissolvedOxygenUnit);
        values.put(ASSESSMENT_ELECTRICAL_CONDUCTIVITY, electricalConductivity);
        values.put(ASSESSMENT_ELECTRICAL_CONDUCTIVITY_UNIT, electricalConductivityUnit);
        values.put(ASSESSMENT_WATER_CLARITY, waterClarity);
        long id = db.insert(ASSESSMENT_TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /**
     * Link an assessment to a site
     * @param siteId The site id
     * @param assessmentId The assessment id
     */
    public void addNewSiteAssessment(Integer siteId, Integer assessmentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SITE_ASSESSMENT_SITE_ID, siteId);
        values.put(SITE_ASSESSMENT_ASSESSMENT_ID, assessmentId);
        db.insert(SITE_ASSESSMENT_TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Add a new photo to the database and link to the correct assessment
     * @param assessmentId The assessment id
     * @param photoLocation The image path
     * @param userChoice The user invert choice
     * @param mlPredictions The invert ml prediction
     */
    public void addNewPhoto(Integer assessmentId, String photoLocation, String userChoice,
                            String mlPredictions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHOTO_ASSESSMENT_ID, assessmentId);
        values.put(PHOTO_LOCATION, photoLocation);
        values.put(PHOTO_USER_CHOICE, userChoice);
        values.put(PHOTO_ML_PREDICTIONS, mlPredictions);
        db.insert(PHOTO_TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Add a new image path to a site
     * @param siteId The sites id
     * @param imageLocation The image path
     */
    public void addNewSiteImage(long siteId, String imageLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SITE_IMAGE_SITE_ID, siteId);
        values.put(SITE_IMAGE_LOCATION, imageLocation);
        db.insert(SITE_IMAGE_TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Get the site image paths
     * @param siteId The site id
     * @return A list of image paths
     */
    public ArrayList<String> getSiteImagePathsBySiteId(long siteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + SITE_IMAGE_LOCATION + " FROM "
                + SITE_IMAGE_TABLE_NAME + " WHERE "
                + SITE_IMAGE_SITE_ID + " = " + siteId, null);

        ArrayList<String> imagePaths = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                imagePaths.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return imagePaths;
    }

    /**
     * Gets a list of sites from the database with optional filtering
     * @param whereClause The optional WHERE clause, excluding the 'WHERE' itself (e.g., "riverType = ? AND siteName LIKE ?")
     * @param whereArgs The arguments for the WHERE clause placeholders
     * @return A list of filtered sites
     */
    public ArrayList<SitesModel> getSites(String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + SITES_TABLE_NAME;

        if (whereClause != null && !whereClause.trim().isEmpty()) {
            query += " WHERE " + whereClause;
        }

        Cursor cursor = db.rawQuery(query, whereArgs);
        ArrayList<SitesModel> sites = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                System.out.println("DB: " + cursor.getString(5));
                sites.add(new SitesModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(6),
                        cursor.getString(5),
                        cursor.getString(7)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sites;
    }

    public ArrayList<SitesModel> getSites() {
        return getSites(null, null);
    }

    /**
     * Get an assessment from the database based on the site ID provided
     * @param siteId site id
     * @return A list of assessments
     */
    public ArrayList<Integer> getAssessmentIdsBySiteId(Integer siteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + SITE_ASSESSMENT_ID + " FROM "
                + SITE_ASSESSMENT_TABLE_NAME + " WHERE "
                + SITE_ASSESSMENT_SITE_ID + " = " + siteId, null);
        ArrayList<Integer> assessments = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                assessments.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return assessments;
    }

    /**
     * Get a site from an assessment id
     * @param assessmentId The assessment id
     * @return A site id
     */
    public Integer getSiteIdByAssessmentId(Integer assessmentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + SITE_ASSESSMENT_SITE_ID + " FROM "
                + SITE_ASSESSMENT_TABLE_NAME + " WHERE "
                + SITE_ASSESSMENT_ASSESSMENT_ID + " = " + assessmentId, null);
        Integer siteId = 0;
        if (cursor.moveToFirst()) {
            do {
                siteId = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return siteId;
    }

    /**
     * Get an assessment from the database based on the assessment ID provided
     * @param assessmentId assessment id
     * @return An assessment
     */
    public AssessmentModel getAssessmentById(Integer assessmentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "
                + ASSESSMENT_TABLE_NAME + " WHERE "
                + ASSESSMENT_ID + " = " + assessmentId, null);
        AssessmentModel assessment = null;
        if (cursor.moveToFirst()) {
            assessment = new AssessmentModel(
                    cursor.getInt(0),
                    cursor.getFloat(1),
                    cursor.getFloat(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10)
            );
        }
        cursor.close();
        db.close();
        return assessment;
    }

    /**
     * Get a site from the database based on the site ID provided
     * @param siteId The site's id
     * @return A site
     */
    public SitesModel getSiteById(Integer siteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "
                + SITES_TABLE_NAME + " WHERE "
                + SITES_ID + " = " + siteId, null);
        SitesModel site = null;
        if (cursor.moveToFirst()) {
            site = new SitesModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(6),
                    cursor.getString(5),
                    cursor.getString(7)
            );
        }
        cursor.close();
        db.close();
        return site;
    }

    /**
     * Get a list of photos from the database linked to an assessment based on the
     * assessment ID provided
     * @param assessmentId The assessment's id
     * @return A list of photos
     */
    public ArrayList<PhotoModel> getPhotoInfo(Integer assessmentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT *  FROM "
                + PHOTO_TABLE_NAME + " WHERE "
                + PHOTO_ASSESSMENT_ID + " = " + assessmentId, null);
        ArrayList<PhotoModel> photos = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                photos.add(new PhotoModel(cursor.getInt(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getInt(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return photos;
    }

    /**
     * Upgrade the database on the device
     * @param db The database that needs to be updated
     * @param oldVersion The old version
     * @param newVersion The new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SITES_TABLE_NAME);
        onCreate(db);
    }

}
