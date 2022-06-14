package com.pins.infinity.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.appcenter.AppCenter;
import com.pins.infinity.R;
import com.pins.infinity.adapters.PicturesAdapter;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.PictureBaseModel;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.SharedPreferences.Const;
import com.pins.infinity.utility.Utility;
import com.vlk.multimager.activities.GalleryActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by shri.kant on 10-11-2016.
 */
public class PicturesActivity extends AppCompatActivity implements ApiResponseListener {
    Map<String, String> header;
    List<Image> imageList;
    int remainingCount = 0;
    HashMap<String, String> params;
    String url;
    RecyclerView recyclerView;
    ImageView addMedia;
    private List<PictureBaseModel> pictureModelList = new ArrayList<>();
    private PicturesAdapter mAdapter;
    private ProgressDialog progressDialog;
    private String token = "";
    private String userId = "";
    private String imei = "";
    private TextView numberPictures;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);
        try {
            String response = AppSharedPrefrence.getString(PicturesActivity.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                // You can call any combination of these three methods
                AppCenter.setUserId(profileBaseModel.getResponse().getUser().getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        numberPictures = (TextView) findViewById(R.id.number_pictures);
        numberPictures.setVisibility(View.GONE);
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        addMedia = (ImageView) findViewById(R.id.add_media);
        addMedia.setVisibility(View.VISIBLE);

        addMedia.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PicturesActivity.this, GalleryActivity.class);
                Params params = new Params();
                params.setCaptureLimit(10);
                params.setPickerLimit(10);
                params.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                params.setActionButtonColor(getResources().getColor(R.color.colorPrimary));
                params.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
                intent.putExtra(Constants.KEY_PARAMS, params);
                startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);
            }
        });
        loadDataForPictures();

    }


    private void loadDataForPictures() {
        recyclerView = (RecyclerView) findViewById(R.id.picture_recycler_view);


        if(progressDialog!=null && progressDialog.isShowing()) {

        } else {
            progressDialog = new ProgressDialog(PicturesActivity.this);
            progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        readPictures();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_action_crop) {
            Log.d("crop", "click");
            return true;
        } else if (item.getItemId() == R.id.main_action_rotate) {
            Log.d("rotate", "click");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.TYPE_MULTI_PICKER:
                imageList = data.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
                Log.d("image size", imageList.size()+"");
                updateProfilePicOnServer();
                break;

            case Constants.TYPE_MULTI_CAPTURE:
                imageList = data.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
                Log.d("capture image size", imageList.size()+"");
                updateProfilePicOnServer();
                break;
        }
    }

    /**
     * Set the image to show for cropping.
     */
    public Bitmap getBitmapFromUri(Uri imageUri) {
        try {
            return MediaStore.Images.Media.getBitmap(PicturesActivity.this.getContentResolver(), imageUri);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateProfilePicOnServer() {
if(null!=imageList && imageList.size() > 0) {
    remainingCount = 0;
    progressDialog = new ProgressDialog(PicturesActivity.this);
    progressDialog.setMessage("Uploading media, Please wait...");
    progressDialog.setCancelable(false);
    progressDialog.show();

    imei = AppSharedPrefrence.getString(PicturesActivity.this, Const.IMEI);

    params = new HashMap<>();
    params.put("account_id", userId);
    params.put(Const.IMEI, Utility.checkString(imei) ? imei : "na");

    header = new HashMap<>();
    header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
    header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
    header.put(ApiConstants.AUTH_TOKEN_KEY, token);
    System.out.println("ttoken " + token);

    url = ApiConstants.API_ADDRESS.MEDIA_SYNC.path + "/" + userId + "/" + imei;

    ApiCall.getInstance().makeMultiPartRequest(url, Utility.getFileFromBitmap(getBitmapFromUri(imageList.get(0).uri)), params, header, this, 103, true);
}
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);

    }


void readPictures() {
    try {
        String response = AppSharedPrefrence.getString(PicturesActivity.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
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
    imei = AppSharedPrefrence.getString(PicturesActivity.this, Const.IMEI);

    Map<String, String> header = new HashMap<>();
    header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
    header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
    header.put(ApiConstants.AUTH_TOKEN_KEY, token);

    String url = ApiConstants.BASE_URL + ApiConstants.MEDIA_LIST + userId + "/" + imei;
    ApiCall.getInstance().makeGetRequestWithUrl(PicturesActivity.this, url, params, header, this, 101);
}

    void deletePicture(String file) {

        progressDialog = new ProgressDialog(PicturesActivity.this);
        progressDialog.setMessage("Deleting media, Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {
            String response = AppSharedPrefrence.getString(PicturesActivity.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
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
        params.put("file_name", file);

        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, token);

        String url = ApiConstants.BASE_URL + ApiConstants.MEDIA_DELETE + userId+"/"+file;

        ApiCall.getInstance().makeDeletePictureRequest(url, params, header, this, 102);
    }



    @Override
    public void onResponse(String response, int requestCode) throws IOException {

//        {"message": "Media fetched", "code": 200, "response": {"media": {"total": 0, "list": []}}, "error": false}
        JSONObject jsonObject = null;
        if (requestCode == 101) {

            if (response != null) {
                try {
                    jsonObject = new JSONObject(response);

                    if (jsonObject != null) {
                        String message = jsonObject.getString("message");
                        JSONObject data = jsonObject.optJSONObject("response");
                        if (!jsonObject.getString("error").equalsIgnoreCase("true")) {

                            JSONArray array = data.optJSONObject("media").optJSONArray("list");
                            if(array.length() == 0) {
                                Toast.makeText(PicturesActivity.this, "No media found", Toast.LENGTH_SHORT).show();
                            } else {
                                System.out.println("list   "+array.toString());
                                pictureModelList = new ArrayList<>();

                                        for(int count = 0; count < array.length(); count++) {
                                            PictureBaseModel model = new PictureBaseModel();
                                            JSONObject object = array.optJSONObject(count);
                                            model.setMediaId(object.optString("media_id"));
                                            model.setAccountId(object.optString("account_id"));
                                            model.setTime(object.optString("timestamp"));
                                            model.setFileName(object.optString("file_name"));
                                            model.setBucket(object.optString("bucket"));
                                            model.setExtension(object.optString("ext"));
                                            model.setDate(object.optString("created_date"));
                                            model.setSize(object.optString("size"));
                                            model.setImageUrl(object.optString("url"));
//                                            model.setImageUrl("https://s3.amazonaws.com/"+object.optString("bucket")+"-"+object.optString("account_id")+"/"+object.optString("file_name"));
                                            pictureModelList.add(model);

                                        }

                            }
                            numberPictures.setText("Total Pictures : " + pictureModelList.size());
                            numberPictures.setVisibility(View.VISIBLE);
                            mAdapter = new PicturesAdapter(PicturesActivity.this, pictureModelList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(PicturesActivity.this));
                            recyclerView.setAdapter(mAdapter);
                            recyclerView.setHasFixedSize(true);
                            setUpItemTouchHelper();
                            setUpAnimationDecoratorHelper();
                        } else {
                            Utility.showToast(PicturesActivity.this, message);
                        }
                        try {
                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();
                        } catch (IllegalArgumentException e) {
                            progressDialog = null;
                        } finally {
                            progressDialog = null;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == 103) {
            if (response != null) {
                try {
                    jsonObject = new JSONObject(response);

                    if (jsonObject != null) {
                        String message = jsonObject.getString("message");
                        JSONObject data = jsonObject.optJSONObject("response");
                        if (!jsonObject.getString("error").equalsIgnoreCase("true")) {
                            remainingCount++;
                            if(remainingCount != imageList.size()) {
                                ApiCall.getInstance().makeMultiPartRequest(url, Utility.getFileFromBitmap(getBitmapFromUri(imageList.get(remainingCount).uri)), params, header, this, 103, true);
                            } else {
                                refreshList();
                            }
                        } else {
                            Utility.showToast(PicturesActivity.this, message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("response", response);
        } else if (requestCode == 102) {
            if (response != null) {
                try {
                    jsonObject = new JSONObject(response);

                    if (jsonObject != null) {
                        try {
                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();
                        } catch (IllegalArgumentException e) {
                            progressDialog = null;
                        } finally {
                            progressDialog = null;
                        }
                        String message = jsonObject.getString("message");
                        if (!jsonObject.getString("error").equalsIgnoreCase("true")) {
                          refreshList();
                        } else {
                            Utility.showToast(PicturesActivity.this, message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    @Override
    public void onError(String errorInfo, int error, int requestCode) {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (IllegalArgumentException e) {
            progressDialog = null;
        } finally {
            progressDialog = null;
        }
        if (error != 0) {
            try {
                if (error == 402) {
                    AppSharedPrefrence.clearAllPrefs(PicturesActivity.this);
                    Intent login = new Intent(PicturesActivity.this, ActivityLogin.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(login);
                    finish();
                } else {
                    JSONObject jsonObject = new JSONObject(errorInfo);

                    if (jsonObject != null) {
                        String message = jsonObject.getString("message");
                        Utility.showToast(this, message);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void refreshList() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               readPictures();
            }
        }, 1000);
    }
    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
//            Drawable xMark;
//            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
//                xMark = ContextCompat.getDrawable(PicturesActivity.this, R.drawable.ic_clear_24dp);
//                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//                xMarkMargin = (int) MainActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
//                TestAdapter testAdapter = (TestAdapter)recyclerView.getAdapter();
//                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
//                    return 0;
//                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
//                    mAdapter.remove(swipedPosition);
               Log.d("swiped", "finish......");
                deletePicture(pictureModelList.get(swipedPosition).getFileName());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
//                int intrinsicWidth = xMark.getIntrinsicWidth();
//                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight();// - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight();// - xMarkMargin;
//                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkTop = itemView.getTop() + (itemHeight)/2;
                int xMarkBottom = xMarkTop;// + intrinsicHeight;
//                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
//
//                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }
}