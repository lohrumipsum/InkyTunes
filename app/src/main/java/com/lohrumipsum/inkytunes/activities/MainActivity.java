package com.lohrumipsum.inkytunes.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.lohrumipsum.inkytunes.R;
import com.lohrumipsum.inkytunes.adapters.ViewPagerAdapter;
import com.lohrumipsum.inkytunes.fragments.AlbumFragment;
import com.lohrumipsum.inkytunes.fragments.ArtistFragment;
import com.lohrumipsum.inkytunes.fragments.TrackFragment;
import com.lohrumipsum.inkytunes.services.MusicService;
import com.lohrumipsum.inkytunes.utils.PlayerControlsDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private MediaController mediaController;
    private ListenableFuture<MediaController> controllerFuture;
    private PlayerControlsDelegate playerControlsDelegate;

    public interface ControllerCallback {
        void onControllerReady(@NonNull MediaController mediaController);
    }

    // Changed to a list to support multiple fragment listeners
    private final List<ControllerCallback> controllerCallbacks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);

        playerControlsDelegate = new PlayerControlsDelegate(findViewById(R.id.playback_controls_container));

        if (checkPermissions()) {
            setupViewPager();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeController();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaController != null) {
            MediaController.releaseFuture(controllerFuture);
        }
        playerControlsDelegate.release();
    }

    private void initializeController() {
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, MusicService.class));
        controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
            try {
                mediaController = controllerFuture.get();
                playerControlsDelegate.setMediaController(mediaController);
                // Notify all registered fragments
                for (ControllerCallback callback : controllerCallbacks) {
                    callback.onControllerReady(mediaController);
                }
            } catch (ExecutionException | InterruptedException e) {
                // Handle error
            }
        }, MoreExecutors.directExecutor());
    }

    // Modified to add to the list
    public void registerCallback(ControllerCallback callback) {
        controllerCallbacks.add(callback);
        if (mediaController != null) {
            callback.onControllerReady(mediaController);
        }
    }

    // Modified to remove from the list
    public void unregisterCallback(ControllerCallback callback) {
        controllerCallbacks.remove(callback);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TrackFragment(), "Tracks");
        adapter.addFragment(new AlbumFragment(), "Albums");
        adapter.addFragment(new ArtistFragment(), "Artists");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    private boolean checkPermissions() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_AUDIO;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupViewPager();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // TODO: Implement search logic
        return true;
    }
}

