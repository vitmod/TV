/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.tv;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.VisibleForTesting;

import com.android.tv.common.experiments.Experiments;
import com.android.tv.common.feature.CommonFeatures;
import com.android.tv.common.feature.ExperimentFeature;
import com.android.tv.common.feature.Feature;
import com.android.tv.common.feature.GServiceFeature;
import com.android.tv.common.feature.PropertyFeature;
import com.android.tv.common.util.PermissionUtils;

import static com.android.tv.common.feature.EngOnlyFeature.ENG_ONLY_FEATURE;
import static com.android.tv.common.feature.FeatureUtils.OFF;
import static com.android.tv.common.feature.FeatureUtils.ON;
import static com.android.tv.common.feature.FeatureUtils.and;
import static com.android.tv.common.feature.FeatureUtils.aospFeature;
import static com.android.tv.common.feature.FeatureUtils.or;


/**
 * List of {@link Feature} for the Live TV App.
 *
 * <p>Remove the {@code Feature} once it is launched.
 */
public final class TvFeatures extends CommonFeatures {

    /** When enabled store network affiliation information to TV provider */
    public static final Feature STORE_NETWORK_AFFILIATION = ENG_ONLY_FEATURE;

    /** When enabled use system setting for turning on analytics. */
    public static final Feature ANALYTICS_OPT_IN =
            ExperimentFeature.from(Experiments.ENABLE_ANALYTICS_VIA_CHECKBOX);
    /**
     * Analytics that include sensitive information such as channel or program identifiers.
     *
     * <p>See <a href="http://b/22062676">b/22062676</a>
     */
    public static final Feature ANALYTICS_V2 = and(ON, ANALYTICS_OPT_IN);

    /** Enables Embedded tuner */
    public static final Feature TUNER =
            aospFeature(
                    OFF);

    // TODO(b/76149661): Fix EPG search or remove it
    public static final Feature EPG_SEARCH = OFF;

    private static final String GSERVICE_KEY_UNHIDE = "live_channels_unhide";
    /** A flag which indicates that LC app is unhidden even when there is no input. */
    public static final Feature UNHIDE =
            or(
                    new GServiceFeature(GSERVICE_KEY_UNHIDE, false),
                    new Feature() {
                        @Override
                        public boolean isEnabled(Context context) {
                            // If LC app runs as non-system app, we unhide the app.
                            return !PermissionUtils.hasAccessAllEpg(context);
                        }
                    });

    public static final Feature PICTURE_IN_PICTURE =
            new Feature() {
                private Boolean mEnabled;

                @Override
                public boolean isEnabled(Context context) {
                    if (mEnabled == null) {
                        mEnabled =
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                        && context.getPackageManager()
                                                .hasSystemFeature(
                                                        PackageManager.FEATURE_PICTURE_IN_PICTURE);
                    }
                    return mEnabled;
                }
            };

    /** Enable a conflict dialog between currently watched channel and upcoming recording. */
    public static final Feature SHOW_UPCOMING_CONFLICT_DIALOG = OFF;

    /** Use input blacklist to disable partner's tuner input. */
    public static final Feature USE_PARTNER_INPUT_BLACKLIST = ON;

    @VisibleForTesting
    public static final Feature TEST_FEATURE = PropertyFeature.create("test_feature", false);

    private TvFeatures() {}
}
