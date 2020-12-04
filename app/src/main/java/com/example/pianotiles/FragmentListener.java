package com.example.pianotiles;

import android.os.Bundle;

interface FragmentListener {
    public void changePage(FragmentType fragmentType, boolean isPop, Bundle savedBundleInstance);
    public void updateScore();
    public void closeApplication();
}
