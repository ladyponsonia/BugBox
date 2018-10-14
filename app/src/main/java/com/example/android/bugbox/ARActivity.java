package com.example.android.bugbox;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.android.bugbox.ARHelpers.AsyncFileDownloader;
import com.example.android.bugbox.ARHelpers.helpers.CameraPermissionHelper;
import com.example.android.bugbox.ARHelpers.helpers.DisplayRotationHelper;
import com.example.android.bugbox.ARHelpers.helpers.FullScreenHelper;
import com.example.android.bugbox.ARHelpers.helpers.SnackbarHelper;
import com.example.android.bugbox.ARHelpers.helpers.TapHelper;
import com.example.android.bugbox.ARHelpers.rendering.BackgroundRenderer;
import com.example.android.bugbox.ARHelpers.rendering.ObjectRenderer;
import com.example.android.bugbox.ARHelpers.rendering.PlaneRenderer;
import com.example.android.bugbox.ARHelpers.rendering.PointCloudRenderer;
import com.example.android.bugbox.contentProvider.BugsContract.BugEntry;
import com.example.android.bugbox.network.GetDataService;
import com.example.android.bugbox.network.RetrofitClientInstance;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Some of the code in this class copied from Google Poly's PolySampleARCore app
public class ARActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,GLSurfaceView.Renderer{

    private final String TAG = this.getClass().getSimpleName();
    private static final int DB_QUERY_LOADER_ID = 555;

    private int mId;
    private String mName;
    private float mScale;
    private Cursor mCursor;
    private String mObjUrl;
    private String mPngUrl;
    private String mObjFilename;
    private String mPngFilename;
    private byte[] mObjBytes;
    private byte[] mPngBytes;

    //variables from PolySampleARCore
    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;
    private boolean installRequested;
    private Session session;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    private DisplayRotationHelper displayRotationHelper;
    private TapHelper tapHelper;
    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private final PlaneRenderer planeRenderer = new PlaneRenderer();
    private final PointCloudRenderer pointCloudRenderer = new PointCloudRenderer();
    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] anchorMatrix = new float[16];
    private final ArrayList<Anchor> anchors = new ArrayList<>();
    private ObjectRenderer virtualObject = null;
    // Our background thread, which does all of the heavy lifting so we don't block the main thread.
    private HandlerThread mBackgroundThread;
    // Handler for the background thread, to which we post background thread tasks.
    private Handler mBackgroundThreadHandler;
    // The AsyncFileDownloader responsible for downloading a set of data files from Poly.
    private AsyncFileDownloader mFileDownloader;
    // When we're finished downloading the asset files, we flip this boolean to true to
    // indicate to the GL thread that it can import and load the model.
    private volatile boolean mReadyToImport;
    // Attributions text to display for the object (title and author).
    private String mAttributionText = "";
    // Have we already shown the attribution toast?
    private boolean mShowedAttributionToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        //mCursor =null;

        //get selected bug id
        Intent intent = getIntent();
        mId = intent.getExtras().getInt(MyBugsFragment.BUG_ID);
        Log.d(TAG, String.valueOf(mId));
        //start loader to get bug info from db
        getSupportLoaderManager().initLoader(DB_QUERY_LOADER_ID, null, this);

        surfaceView = findViewById(R.id.surfaceview);
        displayRotationHelper = new DisplayRotationHelper(this);

        // Set up tap listener.
        tapHelper = new TapHelper(this );
        surfaceView.setOnTouchListener(tapHelper);

        // Set up renderer.
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        installRequested = false;

        // Create a background thread, where we will do the heavy lifting.
        mBackgroundThread = new HandlerThread("Worker");
        mBackgroundThread.start();
        mBackgroundThreadHandler = new Handler(mBackgroundThread.getLooper());
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {

        String[] mProjection = {BugEntry.COLUMN_SCALE, BugEntry.COLUMN_NAME, BugEntry.COLUMN_OBJ_URL,
                BugEntry.COLUMN_OBJ_FILENAME, BugEntry.COLUMN_TEXTURE_FILENAME, BugEntry.COLUMN_TEXTURE_URL};
        String mSelection = "_id=?";
        String[] mSelectionArgs = new String[]{String.valueOf(mId)};

        return new CursorLoader(this,
                BugEntry.CONTENT_URI,
                mProjection,
                mSelection,
                mSelectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //if (mCursor != null) mCursor.close();
        mCursor = data;

        //get name and files paths
        if (data != null && data.moveToFirst()) {
            mScale = data.getFloat(data.getColumnIndex(BugEntry.COLUMN_SCALE));
            mName = data.getString(data.getColumnIndex(BugEntry.COLUMN_NAME));
            mObjFilename = data.getString(data.getColumnIndex(BugEntry.COLUMN_OBJ_FILENAME));
            mPngFilename = data.getString(data.getColumnIndex(BugEntry.COLUMN_TEXTURE_FILENAME));
            mObjUrl = data.getString(data.getColumnIndex(BugEntry.COLUMN_OBJ_URL));
            mPngUrl = data.getString(data.getColumnIndex(BugEntry.COLUMN_TEXTURE_URL));
        }
        if(mName != null){
            setTitle(mName);
        }
        //download files
        requestDataFiles();

        //mCursor.close();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d (TAG, "onLoaderReset");
        //if (mCursor != null) mCursor.close();
        mCursor = null;

    }

    //below code copied from PolySampleARCore with some modifications
    @Override
    protected void onResume() {
        super.onResume();
        //check connection
        if (!isOnline(this)) {
            Toast.makeText(this, R.string.ar_connection_error, Toast.LENGTH_LONG).show();
        }
        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }

                // Create the session.
                session = new Session( this);

            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = getString(R.string.instal_arcore);
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = getString(R.string.update_arcore);
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = getString(R.string.update_app);
                exception = e;
            } catch (UnavailableDeviceNotCompatibleException e) {
                message = getString(R.string.no_support_ar);
                exception = e;
            } catch (Exception e) {
                message = getString(R.string.ar_session_fail);
                exception = e;
            }

            if (message != null) {
                messageSnackbarHelper.showError(this, message);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session.resume();
        } catch (CameraNotAvailableException e) {
            // In some cases (such as another camera app launching) the camera may be given to
            // a different app instead. Handle this properly by showing a message and recreate the
            // session at the next iteration.
            messageSnackbarHelper.showError(this, getString(R.string.camera_unavailable));
            session = null;
            return;
        }

        surfaceView.onResume();
        displayRotationHelper.onResume();

        messageSnackbarHelper.showMessage(this, getString(R.string.searching_surfaces));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, R.string.camera_permission_denied, Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
        try {
            // Create the texture and pass it to ARCore session to be filled during update().
            backgroundRenderer.createOnGlThread(/*context=*/ this);
            planeRenderer.createOnGlThread(/*context=*/ this, "models/trigrid.png");
            pointCloudRenderer.createOnGlThread(/*context=*/ this);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read plane texture", e);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // If we are ready to import the object and haven't done so yet, do it now.
        if (mReadyToImport && virtualObject == null) {
            importDownloadedObject();
        }
        if (session == null) {
            return;
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session);

        try {
            session.setCameraTextureName(backgroundRenderer.getTextureId());

            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = session.update();
            Camera camera = frame.getCamera();

            // Handle one tap per frame.
            handleTap(frame, camera);

            // Draw background.
            backgroundRenderer.draw(frame);

            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                return;
            }

            // Get projection matrix.
            float[] projmtx = new float[16];
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

            // Get camera matrix and draw.
            float[] viewmtx = new float[16];
            camera.getViewMatrix(viewmtx, 0);

            // Compute lighting from average intensity of the image.
            // The first three components are color scaling factors.
            // The last one is the average pixel intensity in gamma space.
            final float[] colorCorrectionRgba = new float[4];
            frame.getLightEstimate().getColorCorrection(colorCorrectionRgba, 0);

            // Visualize tracked points.
            PointCloud pointCloud = frame.acquirePointCloud();
            pointCloudRenderer.update(pointCloud);
            pointCloudRenderer.draw(viewmtx, projmtx);

            // Application is responsible for releasing the point cloud resources after
            // using it.
            pointCloud.release();

            // Check if we detected at least one plane. If so, hide the loading message.
            if (messageSnackbarHelper.isShowing()) {
                for (Plane plane : session.getAllTrackables(Plane.class)) {
                    if (plane.getTrackingState() == TrackingState.TRACKING) {
                        messageSnackbarHelper.hide(this);
                        break;
                    }
                }
            }

            // Visualize planes.
            planeRenderer.drawPlanes(
                    session.getAllTrackables(Plane.class), camera.getDisplayOrientedPose(), projmtx);

            // Visualize anchors created by touch.
            float scaleFactor = 1.0f;
            for (Anchor anchor : anchors) {
                if (anchor.getTrackingState() != TrackingState.TRACKING) {
                    continue;
                }
                // Get the current pose of an Anchor in world space. The Anchor pose is updated
                // during calls to session.update() as ARCore refines its estimate of the world.
                anchor.getPose().toMatrix(anchorMatrix, 0);

                // Update and draw the model.
                if (virtualObject != null) {
                    virtualObject.updateModelMatrix(anchorMatrix, mScale * scaleFactor);
                    virtualObject.draw(viewmtx, projmtx, colorCorrectionRgba);

                    // If we haven't yet showing the attribution toast, do it now.
                    if (!mShowedAttributionToast) {
                        showAttributionToast();
                    }
                }
            }

        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }

    // Handle only one tap per frame, as taps are usually low frequency compared to frame rate.
    private void handleTap(Frame frame, Camera camera) {
        MotionEvent tap = tapHelper.poll();
        if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                // Check if any plane was hit, and if it was hit inside the plane polygon
                Trackable trackable = hit.getTrackable();
                // Creates an anchor if a plane or an oriented point was hit.
                if ((trackable instanceof Plane
                        && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())
                        && (PlaneRenderer.calculateDistanceToPlane(hit.getHitPose(), camera.getPose()) > 0))
                        || (trackable instanceof Point
                        && ((Point) trackable).getOrientationMode()
                        == Point.OrientationMode.ESTIMATED_SURFACE_NORMAL)) {
                    // Hits are sorted by depth. Consider only closest hit on a plane or oriented point.
                    // Cap the number of objects created. This avoids overloading both the
                    // rendering system and ARCore.
                    if (anchors.size() >= 20) {
                        anchors.get(0).detach();
                        anchors.remove(0);
                    }

                    // Adding an Anchor tells ARCore that it should track this position in
                    // space. This anchor is created on the Plane to place the 3D model
                    // in the correct position relative both to the world and to the plane.
                    anchors.add(hit.createAnchor());
                    break;
                }
            }
        }
    }

    private void importDownloadedObject() {
        try {
            virtualObject = new ObjectRenderer();

            for (int i = 0; i < mFileDownloader.getEntryCount(); i++) {
                AsyncFileDownloader.Entry thisEntry = mFileDownloader.getEntry(i);
                if (thisEntry.fileName.toLowerCase().endsWith(".obj")) {
                    mObjBytes = thisEntry.contents;
                } else if (thisEntry.fileName.toLowerCase().endsWith(".png")) {
                    mPngBytes = thisEntry.contents;
                }
            }

            if (mObjBytes == null || mPngBytes == null) {
                Log.e(TAG, "Downloaded asset doesn't have OBJ data and a PNG texture.");
                return;
            }
            Log.d(TAG, "Importing OBJ.");

            virtualObject.createOnGlThread(this, mObjBytes, mPngBytes);
            virtualObject.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read obj file");
        }
    }
    // Requests the data files for the OBJ format.
    // NOTE: this runs on the background thread.
    private void requestDataFiles(){

        //Download obj and png
        mFileDownloader = new AsyncFileDownloader();
        mFileDownloader.add(mObjFilename, mObjUrl);
        mFileDownloader.add(mPngFilename, mPngUrl);

        // Now start downloading the data files. When this is done, the callback will call
        // importDownloadedObject().
        Log.d(TAG, "Starting to download data files, # files: " + mFileDownloader.getEntryCount());
        mFileDownloader.start(mBackgroundThreadHandler, new AsyncFileDownloader.CompletionListener() {
            @Override
            public void onPolyDownloadFinished(AsyncFileDownloader downloader) {
                if (downloader.isError()) {
                    Log.e(TAG, "Failed to download data files for asset.");
                    return;
                }
                // Signal to the GL thread that download is complete, so it can go ahead and
                // import the model.
                Log.d(TAG, "Download complete, ready to import model.");
                mReadyToImport = true;
            }
        });
    }



    // NOTE: this runs on the background thread.
    private void handleRequestFailure(int statusCode, String message, Exception exception) {
        Log.e(TAG, "Request failed. Status code " + statusCode + ", message: " + message +
                ((exception != null) ? ", exception: " + exception : ""));
        if (exception != null) exception.printStackTrace();
    }

    private void showAttributionToast() {
        mShowedAttributionToast = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ARActivity.this, mAttributionText, Toast.LENGTH_LONG).show();
            }
        });
    }

    //check internet connection from https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
