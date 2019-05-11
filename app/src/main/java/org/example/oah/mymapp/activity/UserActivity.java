package org.example.oah.mymapp.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import org.example.oah.mymapp.R;

public class UserActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Bundle bundle = new Bundle();
            SalonsListFragment salonsListFragment;
            switch (item.getItemId()) {
                case R.id.navigation_user:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.user_fragment_container, new UserViewFragment())
                            .commit();
                    return true;

                case R.id.navigation_favorite:
                    salonsListFragment = new SalonsListFragment();
                    bundle.putInt("listType", 2);
                    salonsListFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.user_fragment_container, salonsListFragment)
                            .commit();
                    return true;

                case R.id.navigation_salons:
                    salonsListFragment = new SalonsListFragment();
                    bundle.putInt("listType", 1);
                    salonsListFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.user_fragment_container, salonsListFragment)
                            .commit();
                    return true;

                case R.id.navigation_addsalon:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.user_fragment_container, new AddEditSalonFragment())
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.user_fragment_container, new UserViewFragment())
                .commit();


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

}
