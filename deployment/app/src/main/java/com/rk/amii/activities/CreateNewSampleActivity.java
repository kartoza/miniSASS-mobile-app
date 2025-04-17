package com.rk.amii.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.canhub.cropper.CropImageView;
import com.rk.amii.R;
import com.rk.amii.adapters.SampleItemAdapter;
import com.rk.amii.camera.Camera;
import com.rk.amii.database.DBHandler;
import com.rk.amii.ml.Minisass;
import com.rk.amii.models.AssessmentModel;
import com.rk.amii.models.PhotoModel;
import com.rk.amii.models.SampleItemModel;
import com.rk.amii.models.ScoreModel;
import com.rk.amii.models.SitesModel;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;


public class CreateNewSampleActivity extends AppCompatActivity {

    private ImageView imageView;
    private LinearLayout chooseInvertView;
    private Button takePhotoButton;
    private Button addSampleItem;
    private Button addAssessment;
    private ArrayList<SampleItemModel> sampleItems;
    private Bitmap bitmap;
    private String photoLocation;
    private RecyclerView sampleItemsView;
    private SampleItemAdapter sampleItemAdapter;
    private NestedScrollView sampleItemsContainer;
    private ArrayList<ScoreModel> scores;
    private ArrayList<String> mlLabels;
    private Interpreter tflite;
    private DBHandler dbHandler;
    private boolean loading;
    private LinearLayout loadingView;
    private TextView placeholderText;
    private String invertType;
    private Spinner macroinvertebrates;
    private EditText ph;
    private EditText waterTemp;
    private EditText dissolvedOxygen;
    private AutoCompleteTextView dissolvedOxygenUnit;
    private EditText electricalConductivity;
    private AutoCompleteTextView electricalConductivityUnit;
    private EditText waterClarity;
    private EditText notes;
    private LinearLayout showAssessmentDetails;
    private LinearLayout assessmentDetailsContainer;
    private boolean isOnline;
    private HashMap<String, String> onlineInvertMapping = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_sample);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        isOnline = Utils.isNetworkAvailable(this);

        setTitle("Create Assessment");

        Intent intent = getIntent();

        long siteId = Integer.parseInt(intent.getStringExtra("siteId"));

        dbHandler = new DBHandler(CreateNewSampleActivity.this);

        sampleItems = new ArrayList<>();
        scores = new ArrayList<>();

        scores.add(new ScoreModel("Bugs & Beetles", 5));
        scores.add(new ScoreModel("Caddisflies",9));
        scores.add(new ScoreModel("Damselflies",4));
        scores.add(new ScoreModel("Dragonflies",6));
        scores.add(new ScoreModel("Flat worms",3));
        scores.add(new ScoreModel("Crabs & Shrimps",6));
        scores.add(new ScoreModel("Leeches",2));
        scores.add(new ScoreModel("Minnow Mayflies",5));
        scores.add(new ScoreModel("Other Mayflies",11));
        scores.add(new ScoreModel("Snails/Clams/Mussels",4));
        scores.add(new ScoreModel("Stoneflies",17));
        scores.add(new ScoreModel("Trueflies",2));
        scores.add(new ScoreModel("Worms",2));

        mlLabels = new ArrayList<>();
        mlLabels.add("Bugs & Beetles");
        mlLabels.add("Caddisflies");
        mlLabels.add("Crabs & Shrimps");
        mlLabels.add("Damselflies");
        mlLabels.add("Dragonflies");
        mlLabels.add("Flat worms");
        mlLabels.add("Leeches");
        mlLabels.add("Minnow Mayflies");
        mlLabels.add("Other Mayflies");
        mlLabels.add("Snails/Clams/Mussels");
        mlLabels.add("Stoneflies");
        mlLabels.add("Trueflies");
        mlLabels.add("Worms");


        onlineInvertMapping.put("Bugs & Beetles", "bugs_beetles");
        onlineInvertMapping.put("Caddisflies", "caddisflies");
        onlineInvertMapping.put("Damselflies", "damselflies");
        onlineInvertMapping.put("Dragonflies", "dragonflies");
        onlineInvertMapping.put("Flat worms", "flatworms");
        onlineInvertMapping.put("Crabs & Shrimps", "crabs_shrimps");
        onlineInvertMapping.put("Leeches", "leeches");
        onlineInvertMapping.put("Minnow Mayflies", "minnow_mayflies");
        onlineInvertMapping.put("Other Mayflies", "other_mayflies");
        onlineInvertMapping.put("Snails/Clams/Mussels", "snails");
        onlineInvertMapping.put("Stoneflies", "stoneflies");
        onlineInvertMapping.put("Trueflies", "true_flies");
        onlineInvertMapping.put("Worms", "worms");


        ph = findViewById(R.id.idPhAdd);
        waterTemp = findViewById(R.id.idWaterTempAdd);
        dissolvedOxygen = findViewById(R.id.idDissolvedOxygenAdd);
        dissolvedOxygenUnit = findViewById(R.id.idDissolvedOxygenUnitAdd);
        electricalConductivity = findViewById(R.id.idElectricalConductivityAdd);
        electricalConductivityUnit = findViewById(R.id.idElectricalConductivityUnitAdd);
        waterClarity = findViewById(R.id.idWaterClarityAdd);
        notes = findViewById(R.id.idNotesAdd);

        // Populate dissolved oxygen unit spinner with list of units
        AutoCompleteTextView dissolvedOxygenUnit = (AutoCompleteTextView) findViewById(R.id.idDissolvedOxygenUnitAdd);

        ArrayList<String> dissolvedOxygenUnitTypes = new ArrayList<>();

        dissolvedOxygenUnitTypes.add("mg/l");
        dissolvedOxygenUnitTypes.add("%DO");
        dissolvedOxygenUnitTypes.add("PPM");
        dissolvedOxygenUnitTypes.add("Unknown");

        ArrayAdapter<String> dissolvedOxygenUnitAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, dissolvedOxygenUnitTypes);
        dissolvedOxygenUnit.setAdapter(dissolvedOxygenUnitAdapter);
        dissolvedOxygenUnit.setText("mg/l", false);

        // Populate dissolved oxygen unit spinner with list of units
        AutoCompleteTextView electricalConductivityUnit = (AutoCompleteTextView) findViewById(R.id.idElectricalConductivityUnitAdd);

        ArrayList<String> electricalConductivityUnitTypes = new ArrayList<>();

        electricalConductivityUnitTypes.add("S/m");
        electricalConductivityUnitTypes.add("µS/cm");
        electricalConductivityUnitTypes.add("m S/m");
        electricalConductivityUnitTypes.add("Unknown");


        ArrayAdapter<String> electricalConductivityUnitAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, electricalConductivityUnitTypes);
        electricalConductivityUnit.setAdapter(electricalConductivityUnitAdapter);
        electricalConductivityUnit.setText("S/m", false);

        TextView assessmentToggleMessage = findViewById(R.id.idAssessmentToggleMessage);

        assessmentDetailsContainer = findViewById(R.id.assessmentDetailsContainer);
        assessmentDetailsContainer.setVisibility(View.GONE);

        showAssessmentDetails = findViewById(R.id.idShowAssessmentDetails);
        showAssessmentDetails.setOnClickListener(view -> {
            ImageButton showAssessmentDetailsIconButton = findViewById(R.id.idShowAssessmentDetailsIconButton);

            if (assessmentDetailsContainer.getVisibility() != View.GONE) {
                //TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                assessmentDetailsContainer.setVisibility(View.GONE);
                showAssessmentDetailsIconButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                assessmentToggleMessage.setText("Add measurements");

            } else {
                //TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                assessmentDetailsContainer.setVisibility(View.VISIBLE);
                showAssessmentDetailsIconButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                assessmentToggleMessage.setText("Hide");
            }
        });


        //final Loading loadingdialog = new Loading(CreateNewSampleActivity.this);

        try {
            tflite = new Interpreter(loadModelFile());
        }catch (Exception ex){
            ex.printStackTrace();
        }



        //Hide choose invert view
        chooseInvertView = (LinearLayout)this.findViewById(R.id.chooseInvertView);
        chooseInvertView.setVisibility(View.INVISIBLE);

        addSampleItem = (Button)this.findViewById(R.id.idBtnAddSampleItem);

        addAssessment = (Button)this.findViewById(R.id.idBtnAddAssessment);

        sampleItemsView = (RecyclerView) this.findViewById(R.id.rvSampleItems);

        sampleItemsContainer = (NestedScrollView) this.findViewById(R.id.sampleItems);
        sampleItemsContainer.setVisibility(View.VISIBLE);

        loadingView = (LinearLayout)this.findViewById(R.id.idLoadingView);
        loadingView.setVisibility(View.INVISIBLE);

        placeholderText = (TextView)this.findViewById(R.id.idNoAssessment);

        if (sampleItems.size() > 0) {
            placeholderText.setVisibility(View.INVISIBLE);
        } else {
            addAssessment.setVisibility(View.INVISIBLE);
        }

        // Populate spinner with list of macroinvertebrates
        macroinvertebrates = (Spinner) findViewById(R.id.macroinvertebratesSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,R.array.macroinvertebrates,R.layout.spinner);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        macroinvertebrates.setAdapter(adapter);

        macroinvertebrates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getItemAtPosition(position).equals("Select your identification")) {
                    invertType = parent.getItemAtPosition(position).toString();
                    addSampleItem.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button showKey = findViewById(R.id.idShowKey);

        showKey.setOnClickListener(view -> {
            Intent showKeyIntent = new Intent(CreateNewSampleActivity.this, KeyActivity.class);
            startActivityForResult(showKeyIntent, 150);
        });

        this.imageView = (ImageView)this.findViewById(R.id.image);
        takePhotoButton = (Button)this.findViewById(R.id.idBtnTakePhoto);

        if(ContextCompat.checkSelfPermission(
                CreateNewSampleActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateNewSampleActivity.this, new String[]{
                    Manifest.permission.CAMERA},100);
        }

        if(ContextCompat.checkSelfPermission(
                CreateNewSampleActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateNewSampleActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
        }

        takePhotoButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(CreateNewSampleActivity.this, Camera.class);
            startActivityForResult(intent1, 100);
        });

        addSampleItem.setOnClickListener(view -> {
            sampleItems.add(new SampleItemModel(bitmap, invertType, "", photoLocation));
            sampleItemAdapter = new SampleItemAdapter (CreateNewSampleActivity.this, sampleItems);
            sampleItemsView.setLayoutManager(new LinearLayoutManager(CreateNewSampleActivity.this));
            sampleItemsView.setAdapter(sampleItemAdapter);
            sampleItemsContainer.setVisibility(View.VISIBLE);
            chooseInvertView.setVisibility(View.INVISIBLE);
            takePhotoButton.setVisibility(View.VISIBLE);
            addAssessment.setVisibility(View.VISIBLE);
            showAssessmentDetails.setVisibility(View.VISIBLE);

            if (sampleItems.size() > 0) {
                placeholderText.setVisibility(View.INVISIBLE);
            }

            macroinvertebrates.setSelection(0);
            addSampleItem.setEnabled(false);
        });

        addAssessment.setOnClickListener(view -> {

            double totalFileSize = 0;
            for(int i = 0; i < sampleItems.size(); i++) {
                SampleItemModel currentSample = sampleItems.get(i);
                File image = new File(currentSample.getLocation());
                totalFileSize += (double) image.length() / (1024 * 1024);
            }
            String formattedSize = String.format("%.2f", totalFileSize);

            if(isOnline) {
                new AlertDialog.Builder(CreateNewSampleActivity.this)
                        .setTitle(getResources().getString(R.string.upload_images))
                        .setMessage(getResources().getString(R.string.do_you_want_to_upload_images_start) + " " + formattedSize + " " + getResources().getString(R.string.do_you_want_to_upload_images_end))
                        .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
                            saveAssessment((int)siteId, false);
                        })
                        .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                            saveAssessment((int)siteId, true);
                        })
                        .setIcon(R.drawable.ic_baseline_image_24)
                        .show();
            } else {
                saveAssessment((int)siteId, false);
            }

        });
    }

    private void saveAssessment(int siteId, boolean uploadImages) {
        loading = true;
        loadingView.setVisibility(View.VISIBLE);
        sampleItemsContainer.setVisibility(View.GONE);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            calculateScores(siteId, uploadImages);
            loading = false;
        }, 1000);
    }

    private void calculateScores(int siteId, boolean uploadImages) {
        Double miniSassScore = calculateScore();
        Double mlScore = calculateMLScore();
        SitesModel site = dbHandler.getSiteById((int)siteId);
        float assessmentId = dbHandler.addNewAssessment(
                miniSassScore.toString(),
                mlScore.toString(),
                notes.getText().toString(),
                ph.getText().toString(),
                waterTemp.getText().toString(),
                dissolvedOxygen.getText().toString(),
                dissolvedOxygenUnit.getText().toString(),
                electricalConductivity.getText().toString(),
                electricalConductivityUnit.getText().toString(),
                waterClarity.getText().toString()
        );
        dbHandler.addNewSiteAssessment((int)siteId, (int)assessmentId);
        for(int i = 0; i < sampleItems.size(); i++) {
            SampleItemModel currentSample = sampleItems.get(i);
            dbHandler.addNewPhoto((int)assessmentId, currentSample.getLocation(),
                    currentSample.getInvertType(), currentSample.getMlPredictions());
        }

        if (isOnline) {
            JSONObject assessmentData = new JSONObject();
            JSONObject assessmentDataObject = new JSONObject();
            try {
                ApiService service = new ApiService(this);

                if (uploadImages) {
                    for(int i = 0; i < sampleItems.size(); i++) {
                        SampleItemModel currentSample = sampleItems.get(i);

                        String imageKey = "pest_"+i+":"+onlineInvertMapping.get(currentSample.getInvertType());

                        File image = new File(currentSample.getLocation());

                        byte[] fileData = new byte[(int) image.length()];
                        DataInputStream dis = new DataInputStream(new FileInputStream(image));
                        assessmentData.put(imageKey, fileData);
                        assessmentDataObject.put(onlineInvertMapping.get(currentSample.getInvertType()), true);
                        dis.close();
                    }
                }

                JSONObject assessmentInputObject = new JSONObject();

                assessmentInputObject.put("notes", notes.getText().toString());
                assessmentInputObject.put("waterclaritycm", waterClarity.getText().toString());
                assessmentInputObject.put("watertemperaturOne", waterTemp.getText().toString());
                assessmentInputObject.put("ph", ph.getText().toString());
                assessmentInputObject.put("dissolvedoxygenOne", dissolvedOxygen.getText().toString());
                assessmentInputObject.put("dissolvedoxygenOneUnit", dissolvedOxygenUnit.getText().toString());
                assessmentInputObject.put("electricalconduOne", electricalConductivity.getText().toString());
                assessmentInputObject.put("electricalconduOneUnit", electricalConductivityUnit.getText().toString());
                assessmentInputObject.put("selectedSite", site.getOnlineSiteId());


                assessmentDataObject.put("score", miniSassScore);
                assessmentDataObject.put("datainput", assessmentInputObject);

                assessmentData.put("data", assessmentDataObject);

                System.out.println(assessmentData);

                boolean created = service.createAssessment(assessmentData);

                if (created) {
                    finish();
                } else {
                    this.showCouldNotSaveSiteOnlineDialog();
                }


            } catch (JSONException | IOException e) {
                this.showCouldNotSaveSiteOnlineDialog();
            }
        } else {
            this.showCouldNotSaveSiteOnlineDialog();
        }


    }

    private void showCouldNotSaveSiteOnlineDialog() {
        new AlertDialog.Builder(CreateNewSampleActivity.this)
                .setTitle(getResources().getString(R.string.could_not_save_online_assessment))
                .setMessage(getResources().getString(R.string.online_assessment_save))
                .setNeutralButton(getResources().getString(R.string.ok), (dialog, which) -> {
                    finish();
                })
                .setIcon(R.drawable.ic_baseline_error_24)
                .show();
    }

    private String buildSiteMetaData(int siteId, int assessmentId) {
        JSONObject jsonObject = new JSONObject();
        try {
            SitesModel site = dbHandler.getSiteById(siteId);
            AssessmentModel assessment = dbHandler.getAssessmentById(assessmentId);
            ArrayList<PhotoModel> photoInfo =  dbHandler.getPhotoInfo(assessmentId);

            // Add site metadata
            jsonObject.put("site_id", site.getSiteId().toString());
            jsonObject.put("site_name", site.getSiteName());
            jsonObject.put("site_location", site.getSiteLocation());
            jsonObject.put("river_name", site.getRiverName());
            jsonObject.put("description", site.getDescription());
            jsonObject.put("date", site.getDate());
            // Add assessment metadata
            jsonObject.put("assessment_id", assessment.getAssessmentId().toString());
            jsonObject.put("minisass_score", assessment.getMiniSassScore().toString());
            jsonObject.put("minisass_ml_score", assessment.getMiniSassMLScore().toString());

            JSONArray photoInfoArray = new JSONArray();

            for (int i = 0; i < photoInfo.size(); i++) {
                JSONObject photoInfoObject = new JSONObject();
                photoInfoObject.put("image_id", photoInfo.get(i).getPhotoId().toString());
                photoInfoObject.put("assessment_id", photoInfo.get(i).getAssessmentId().toString());
                photoInfoObject.put("ml_prediction", photoInfo.get(i).getMlPredictions());
                photoInfoObject.put("user_choice", photoInfo.get(i).getUserChoice());
                photoInfoArray.put(photoInfoObject);

            }

            jsonObject.put("assessment_data", photoInfoArray);

            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return jsonObject.toString();
        }
    }

    private Bitmap rotateImageIfRequired(String imagePath) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateBitmap(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateBitmap(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateBitmap(bitmap, 270);
                default:
                    return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return BitmapFactory.decodeFile(imagePath);
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("REQUEST CODE: " + requestCode);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Extract photo location
            photoLocation = data.getExtras().get("data").toString();
            bitmap = rotateImageIfRequired(photoLocation);

            // Show cropper in a dialog
            showCropDialog(bitmap);

        } else if (requestCode == 150 && resultCode == RESULT_OK) {
            String selected = data.getStringExtra("selected");
            System.out.println("SELECTED: " + selected);

            String[] selections = getResources().getStringArray(R.array.macroinvertebrates);
            for (int i = 0; i < selections.length; i++) {
                if (selections[i].toLowerCase().contains(selected)) {
                    macroinvertebrates.setSelection(i);
                    addSampleItem.setEnabled(true);
                }
            }
        }
    }

    /**
     * Displays the CropImageView inside a dialog.
     */
    private void showCropDialog(Bitmap bitmap) {
        Dialog cropDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        cropDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cropDialog.setContentView(R.layout.dialog_crop); // Ensure correct layout file

        CropImageView cropImageView = cropDialog.findViewById(R.id.cropImageView);
        cropImageView.setImageBitmap(bitmap);
        cropImageView.setGuidelines(CropImageView.Guidelines.ON);
        cropImageView.setFixedAspectRatio(false);  // Free-hand cropping
        cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);

        Button btnCrop = cropDialog.findViewById(R.id.btnCrop);
        btnCrop.setOnClickListener(v -> {
            Bitmap croppedBitmap = cropImageView.getCroppedImage();
            if (croppedBitmap != null) {
                processCroppedImage(croppedBitmap);
                cropDialog.dismiss();
            } else {
                System.out.println("Cropping failed: croppedBitmap is null");
            }
        });

        cropDialog.show();
    }

    /**
     * Processes the cropped image and updates UI elements.
     */
    private void processCroppedImage(Bitmap bitmap) {
        try {
            FileOutputStream fos = new FileOutputStream(photoLocation, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            galleryAddPic();
            imageView.setImageBitmap(bitmap);
            sampleItemsContainer.setVisibility(View.INVISIBLE);
            chooseInvertView.setVisibility(View.VISIBLE);
            takePhotoButton.setVisibility(View.INVISIBLE);
            addAssessment.setVisibility(View.INVISIBLE);
            showAssessmentDetails.setVisibility(View.GONE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected Double calculateScore() {
        Double totalScore = 0.00;
        ArrayList<String> groupsUsed = new ArrayList<>();
        for(int i=0; i<sampleItems.size(); i++) {
            SampleItemModel sample = sampleItems.get(i);
            String invertType = sample.getInvertType();
            System.out.println("croups used index of: "+ groupsUsed.indexOf(invertType));
            if (groupsUsed.indexOf(invertType) == -1) {
                System.out.println("Using group: " + invertType);
                for(int j=0; j<scores.size(); j++) {
                    ScoreModel score = scores.get(j);
                    String invert = score.getInvertType();
                    if (invertType.equals(invert)) {
                        totalScore += score.getScore();
                        groupsUsed.add(invertType);
                    }
                }
            }
        }
        System.out.println("ML SCORE: " + (totalScore / groupsUsed.size()));
        return totalScore / groupsUsed.size();
    }

    protected Double calculateMLScore() {
        Double totalScore = 0.00;
        ArrayList<String> mlGroupsUsed = new ArrayList<>();
        for(int i=0; i<sampleItems.size(); i++) {
            SampleItemModel sample = sampleItems.get(i);
            Bitmap image = sample.getImage();
            String prediction = getMLPrediction(image);
            sampleItems.get(i).setMlPredictions(prediction);
            System.out.println("ml croups used index of: "+ mlGroupsUsed.indexOf(prediction));
            if (mlGroupsUsed.indexOf(prediction) == -1) {
                System.out.println("ML Using group: " + prediction);
                for(int j=0; j<scores.size(); j++) {
                    ScoreModel score = scores.get(j);
                    String invert = score.getInvertType();
                    if (prediction.equals(invert)) {
                        totalScore += score.getScore();
                        mlGroupsUsed.add(prediction);
                    }
                }
            }
        }
        System.out.println("ML SCORE: " + (totalScore / mlGroupsUsed.size()));
        return totalScore / mlGroupsUsed.size();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoLocation);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    protected String getMLPrediction(Bitmap image) {
        try {
            Minisass model = Minisass.newInstance(CreateNewSampleActivity.this);

            Bitmap resizedImage = Bitmap.createScaledBitmap(image, 224, 224, true);

            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(resizedImage);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(tensorImage.getBuffer());
            Minisass.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            printFloatArray(outputFeature0.getFloatArray());

            float[] data1=outputFeature0.getFloatArray();
            //Arrays.sort(data1);

            int highestPredictionIndex = 0;
            float highestPrediction = 0.0f;
            for (int i = 0; i < data1.length; i++) {
                float prediction = data1[i];
                System.out.println("curr pred i : " + prediction);
                if (prediction > highestPrediction) {
                    highestPrediction = prediction;
                    highestPredictionIndex = i;
                }
            }

            // Releases model resources if no longer used.
            return mlLabels.get(highestPredictionIndex);
        } catch (IOException e) {
            Log.i("ERROR", e.toString());
            return "error";
        }
    }

    public static void printFloatArray(float[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.println("Array [" + i + "] = "
                    + array[i]);
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor=this.getAssets().openFd("minisass.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declareLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLength);
    }

    @Override
    public void onBackPressed() {
        if (!loading) {
            super.onBackPressed();
        }
    }
}
