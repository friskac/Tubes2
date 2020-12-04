package com.example.pianotiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;


import com.example.pianotiles.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements FragmentListener {

    //
    LobbyFragment lobbyFragment;
    GamePlayFragment gamePlayFragment;
    ScoreFragment scoreFragment;
    SettingFragment settingFragment;
    PopupScoreFragment popupScoreFragment;
    PianoTilesPreference pianoTilesPreference;

    //Fragment history
    Stack<FragmentType> states;
    FragmentType lastState;

    Presenter presenter;
    AdapterHighScore adapter;
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

        //Instantiate Storage, Presenter, and Adapter
        this.pianoTilesPreference = new PianoTilesPreference(this);
        this.presenter = new Presenter(this.pianoTilesPreference);
        this.adapter = new AdapterHighScore(this, this.presenter);


        //Instantiate all fragments and add it to list
        this.gamePlayFragment = new GamePlayFragment();
        this.fragmentsList.add(this.gamePlayFragment);
        this.lobbyFragment = new LobbyFragment();
        this.fragmentsList.add(this.lobbyFragment);
        this.settingFragment = new SettingFragment();
        this.fragmentsList.add(this.settingFragment);
        this.scoreFragment = ScoreFragment.newInstance(this.presenter,this.adapter);
        this.fragmentsList.add(this.scoreFragment);
        this.popupScoreFragment = new PopupScoreFragment();
        this.fragmentsList.add(this.popupScoreFragment);

        this.fragmentManager = this.getSupportFragmentManager();

        this.states = new Stack<>();

        setContentView(binding.getRoot());

        //The first fragment shown when app being run
        this.lastState = null;
        this.changePage(FragmentType.FRAGMENT_LOBBY, false, null);

    }

    /**
     * Method to hide all the other fragments, except the selected fragment based on the type passed as the parameter.
     *
     * @param fragmentType Type of fragment that is going to be shown
     */
    public void changePage(FragmentType fragmentType, boolean isPop, Bundle savedBundleInstance) {
        FragmentTransaction ft = this.fragmentManager.beginTransaction();

        //fragment to be shown based on the parameter fragmentType
        Fragment selectedFragment;

        Log.d("debug change page", "Last state: " + lastState); //Uncomment to debug

        if(this.lastState != fragmentType && !isPop){
            this.states.push(this.lastState);
        }

        this.lastState =  fragmentType;

        switch (fragmentType) {
            case FRAGMENT_LOBBY:
                selectedFragment = this.lobbyFragment;
                break;
            case FRAGMENT_GAME_PLAY:
                //On case where the game ends, user press play again button, then the game ends again, and the user press exit button,
                //the screen will be changed to the second latest gameplay fragment, because it has not being removed from fragment transaction.
                //To prevent it from happens, the fragment must be removed from fragment transaction
                ft.remove(this.gamePlayFragment);

                //Remove the gameplay fragment from fragment list
                this.fragmentsList.remove(this.gamePlayFragment);
                //Create new instance of gameplay fragment
                this.gamePlayFragment = GamePlayFragment.newInstance(this.presenter, savedBundleInstance);
                this.fragmentsList.add(this.gamePlayFragment);
                selectedFragment = this.gamePlayFragment;
                break;
            case FRAGMENT_SETTING:
                selectedFragment = this.settingFragment;
                break;
            case FRAGMENT_LIST_SONG:
                selectedFragment = null;
                break;
            case FRAGMENT_HIGH_SCORE:
                selectedFragment = this.scoreFragment;
                break;
            default:
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
                //If current fragment class name (string) equal to the selected fragment class name,
                // then skip hiding current fragment
                continue;
            if (fragment.isAdded()) {
                ft.hide(fragment);
            }
        }

        ft.commit();
    }

    @Override
    public void updateScore() {
        this.scoreFragment.updateScore();
    }


    @Override
    public void closeApplication() {
        this.moveTaskToBack(true);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if(!this.states.isEmpty()){
            changePage(this.states.pop(), true, null);
        }
        else {
            closeApplication();
        }
    }

}