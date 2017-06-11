package com.example.rashmitiwari.we_sync;

import android.content.pm.PackageInstaller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.google.firebase.*;

/**
 * Created by Rashmi on 6/9/2017.
 */

public class sketchServer {
    static Set<PackageInstaller.Session> connected_user= Collections.synchronizedSet(new HashSet<PackageInstaller.Session>());

    public static Set<PackageInstaller.Session> getConnected_user() {
        return connected_user;
    }
}
