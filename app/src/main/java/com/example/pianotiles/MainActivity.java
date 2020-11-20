package com.example.pianotiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.pianotiles.databinding.ActivityMainBinding;
import com.example.pianotiles.databinding.FragmentGameplayBinding;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements FragmentListener {

    //
    LobbyFragment lobbyFragment;
    GamePlayFragment gamePlayFragment;
    ScoreFragment scoreFragment;
    SettingFragment settingFragment;

    //ArrayList to put fragments
    //implementation for hiding all other fragments can be easily applied using iterator
    protected ArrayList<Fragment> fragmentsList;

    //Fragment manager
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        this.fragmentsList = new ArrayList<>();

        //Instantiate all fragments and add it to list
        this.gamePlayFragment = new GamePlayFragment();
        this.fragmentsList.add(this.gamePlayFragment);
        this.lobbyFragment = new LobbyFragment();
        this.fragmentsList.add(this.lobbyFragment);
        this.settingFragment = new SettingFragment();
        this.fragmentsList.add(this.settingFragment);
        this.scoreFragment = new ScoreFragment();
        this.fragmentsList.add(this.scoreFragment);

        this.fragmentManager = this.getSupportFragmentManager();

        setContentView(binding.getRoot());

        //The first fragment shown when app being run
        this.changePage(FragmentType.FRAGMENT_GAME_PLAY);
    }

    /**
     * Method to hide all the other fragments, except the selected fragment based on the type passed as the parameter.
     *
     * @param fragmentType Type of fragment that is going to be shown
     */
    public void changePage(FragmentType fragmentType) {
        FragmentTransaction ft = this.fragmentManager.beginTransaction();

        //fragment to be shown based on the parameter fragmentType
        Fragment selectedFragment;

        switch (fragmentType) {
            case FRAGMENT_LOBBY:
                //to be implemented
                selectedFragment = this.lobbyFragment;
                break;
            case FRAGMENT_GAME_PLAY:
                //to be implemented
                selectedFragment = this.gamePlayFragment;
                break;
            case FRAGMENT_SETTING:
                //to be implemented
                selectedFragment = this.settingFragment;
                break;
            case FRAGMENT_LIST_SONG:
                //to be implemented
                selectedFragment = null;
                break;
            case FRAGMENT_HIGH_SCORE:
                //to be implemented
                selectedFragment = this.scoreFragment;
                break;
            default:
                //to be implemented
                selectedFragment = this.lobbyFragment;
        }

        //Check if the selectedFragment has been added to activity
        if (selectedFragment.isAdded()) {
            ft.show(selectedFragment);
        } else {
            ft.add(R.id.fragment_container, selectedFragment);
        }

        //Use iterator to hide all other fragments except the selectedFragment
        Iterator<Fragment> iterator = this.fragmentsList.iterator();
        while (iterator.hasNext()) {
            //Get next fragment from the list that is going to be hidden
            Fragment fragment = iterator.next();

            Log.d("debug change page", "Curr: " + fragment.getClass().getName() + ", Selected " + (selectedFragment.getClass().getName())); //Uncomment to debug

            if (fragment.getClass().getName().equals(selectedFragment.getClass().getName()))
                //If curent fragment class name (string) equal to the selected fragment class name,
                // then skip hiding current fragment
                continue;
            if (fragment.isAdded()) {
                ft.hide(fragment);
            }
        }

        ft.commit();


    }
}