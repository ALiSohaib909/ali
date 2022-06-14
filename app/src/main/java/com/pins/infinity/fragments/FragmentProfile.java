package com.pins.infinity.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pins.infinity.R;
import com.pins.infinity.activity.ActivityLogin;
import com.pins.infinity.activity.PasswordChangeActivity;
import com.pins.infinity.application.MyApplication;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.PhotoUtils;
import com.pins.infinity.utility.SharedPreferences.Const;
import com.pins.infinity.utility.Utility;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by shri.kant on 19-11-2016.
 */
public class FragmentProfile extends Fragment implements View.OnClickListener, ApiResponseListener
        , CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener {
    private View view = null;
    private EditText input_fname, input_lname, input_email, mob_no, input_password;
    private String input_fname_Str = "";
    private String input_lname_Str = "";
    private String mobile_txt_Str = "";
    private String email_id_txt_Str = "";
    private String input_password_Str = "";
    private String countryCodePhone = "";
    private String countryName = "";
    private String countryCode = "";
    private String imei = "";
    private Uri mCropImageUri;
    private ProgressDialog progressDialog;
    private String token = "";
    private String userId = "";
    private Button btn_update, btnChangePassword;
    private ImageView profileImageIV;
    private CropImageView mCropImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile,
                container, false);

        initIds();
        initClickListeners();

        disableEdit();
        getProfileData();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initIds() {
        btn_update = (Button) view.findViewById(R.id.btn_update);
        btnChangePassword = (Button) view.findViewById(R.id.btn_change_password);
        input_fname = (EditText) view.findViewById(R.id.input_fname);
        input_lname = (EditText) view.findViewById(R.id.input_lname);
        input_email = (EditText) view.findViewById(R.id.input_email);
        input_email.setEnabled(false);
        mob_no = (EditText) view.findViewById(R.id.mob_no);
        input_password = (EditText) view.findViewById(R.id.input_password);
        profileImageIV = (ImageView) view.findViewById(R.id.profile_image);
        mCropImageView = (CropImageView) view.findViewById(R.id.cropImageView);

    }

    private void initClickListeners() {
        btn_update.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
        profileImageIV.setOnClickListener(this);
//        data_back_ll.setOnClickListener(this);
//        pinll.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:

                if (btn_update.getText().toString().equalsIgnoreCase("UPDATE")) {
                    updateProfileDataOnServer();
                } else {
                    enableEdit();
                }

                break;

            case R.id.btn_change_password:
                startActivity(new Intent(getActivity(), PasswordChangeActivity.class));
                break;
            case R.id.profile_image:
                if (CropImage.isExplicitCameraPermissionRequired(getActivity())) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                } else {
                    CropImage.startPickImageActivity(getActivity());
                }
                break;
//            case R.id.data_back_ll:
//                startActivity(new Intent(getActivity(), ActivityDataBackup.class));
//                break;
//            case R.id.pinll:
//                startActivity(new Intent(getActivity(), ActivityPin.class));
//                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_action_crop) {
            mCropImageView.getCroppedImageAsync();
            return true;
        } else if (item.getItemId() == R.id.main_action_rotate) {
            mCropImageView.rotateImage(90);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCropImageView != null) {
            mCropImageView.setOnSetImageUriCompleteListener(null);
            mCropImageView.setOnCropImageCompleteListener(null);
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            Toast.makeText(getActivity(), "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("AIC", "Failed to load image by URI", error);
            Toast.makeText(getActivity(), "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    private void getProfileData() {

//        getCountryInfoFromPhone();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        try {
            String response = AppSharedPrefrence.getString(getActivity(), AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                token = profileBaseModel.getResponse().getToken();
                userId = profileBaseModel.getResponse().getUser().getAccountId();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("account_id", userId);

        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, token);

        String url = ApiConstants.BASE_URL + ApiConstants.PROFILE_INFO + userId;

        ApiCall.getInstance().makeGetRequestWithUrl(getActivity(), url, null, header, this, 101);
    }


    private void updateProfileDataOnServer() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put(Const.ACCOUNT_ID, userId);
        params.put("first_name", input_fname.getText().toString());
        params.put("last_name", input_lname.getText().toString());
        params.put("phone", mob_no.getText().toString());
        params.put("email", input_email.getText().toString());


        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, token);

        ApiCall.getInstance().makePutRequestWithUrl(getActivity(), ApiConstants.API_ADDRESS.UPDATE_PROFILE.path, params, header, this, 102, userId);
    }

    private void setProfileData(String response) {

        try {
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);
            if (profileBaseModel != null &&
                    profileBaseModel.getResponse() != null &&
                    profileBaseModel.getResponse().getUser() != null) {

                input_fname.setText(profileBaseModel.getResponse().getUser().getFirstName());
                input_lname.setText(profileBaseModel.getResponse().getUser().getLastName());
                input_email.setText(profileBaseModel.getResponse().getUser().getEmail());
                mob_no.setText(profileBaseModel.getResponse().getUser().getPhone());

                String imageUrl = profileBaseModel.getResponse().getUser().getImage();
                System.out.println("image.... " + imageUrl);

                if (imageUrl != null && imageUrl.length() > 0) {
                    Picasso.with(getActivity())
                            .load(imageUrl)
                            .placeholder(R.drawable.placeholder) // optional
                            .error(R.drawable.placeholder)         // optional
                            .into(profileImageIV);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResponse(String response, int requestCode) throws IOException {

        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            progressDialog = null;
        } finally {
            progressDialog = null;
        }
        JSONObject jsonObject = null;
        if (requestCode == 101) {
            if (response != null) {
                try {
                    jsonObject = new JSONObject(response);

                    if (jsonObject != null) {
                        String message = jsonObject.getString("message");
//
                        if (!jsonObject.getString("error").equalsIgnoreCase("true")) {
//                            AppSharedPrefrence.putString(getActivity(), AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO, response);
                            setProfileData(response);
                        } else {
                            Utility.showToast(getActivity(), message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            Log.i("response", response);
        } else if (requestCode == 102) {
            try {
                jsonObject = new JSONObject(response);
                String message = jsonObject.getString("message");
                Utility.showToast(getActivity(), message);
                setProfileData(response);
                disableEdit();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (requestCode == 103) {
            try {
                jsonObject = new JSONObject(response);
                String message = jsonObject.getString("message");
                Utility.showToast(getActivity(), message);
//                setProfileData(response);
                disableEdit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error, int errorCode, int requestCode) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            progressDialog = null;
        } finally {
            progressDialog = null;
        }
        if (error != null) {
            try {
                if (errorCode == 402) {
                    AppSharedPrefrence.clearAllPrefs(getActivity());
                    Intent login = new Intent(getActivity(), ActivityLogin.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(login);
                    getActivity().finish();
                } else {
                    JSONObject jsonObject = new JSONObject(error);

                    if (jsonObject != null) {
                        String message = jsonObject.getString("message");
                        Utility.showToast(getActivity(), message);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void enableEdit() {

        input_fname.setEnabled(true);
        input_lname.setEnabled(true);
        input_email.setEnabled(true);
        mob_no.setEnabled(true);

        profileImageIV.setClickable(true);
        btn_update.setText("UPDATE");
    }

    private void disableEdit() {
        input_fname.setEnabled(false);
        input_lname.setEnabled(false);
        input_email.setEnabled(false);
        mob_no.setEnabled(false);

        profileImageIV.setClickable(false);
        btn_update.setText("EDIT");
    }


    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getActivity(), data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMultiTouchEnabled(true)
                        .start(getActivity());
//                setImageUri(imageUri);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            handleCropResult(result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(getActivity());
            } else {
                Toast.makeText(getActivity(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity(mCropImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMultiTouchEnabled(true)
                        .start(getActivity());
//                setImageUri(mCropImageUri);
            } else {
                Toast.makeText(getActivity(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Set the initial rectangle to use.
     */
    public void setInitialCropRect() {
        mCropImageView.setCropRect(new Rect(100, 300, 500, 1200));
    }

    private void handleCropResult(CropImageView.CropResult result) {
        if (null == result) {
            return;
        }
        if (result.getError() == null) {
            mCropImageView.setImageUriAsync(result.getUri());
            File file = new File(Objects.requireNonNull(result.getUri().getPath()));
            try {
                Bitmap bitmap = PhotoUtils.preparePhotoForUpload(file, false);
                profileImageIV.setImageBitmap(bitmap);
                updateProfilePicOnServer(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.e("AIC", "Failed to crop image", result.getError());
            Toast.makeText(getActivity(), "Image crop failed: " + result.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateProfilePicOnServer(Bitmap bitmap) throws IOException {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String response = AppSharedPrefrence.getString(MyApplication.getInstance(), AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
        ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

        HashMap<String, String> params = new HashMap<>();
        params.put(Const.ACCOUNT_ID, profileBaseModel.getResponse().getUser().getAccountId());

        File file = Utility.getFileFromBitmap(bitmap);

        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, token);
        System.out.println("token " + token);


        ApiCall.getInstance().makeMultiPartRequest(ApiConstants.API_ADDRESS.UPDATE_PROFILE_PIC.path,
                file, params, header, this, 103, false);
    }
}