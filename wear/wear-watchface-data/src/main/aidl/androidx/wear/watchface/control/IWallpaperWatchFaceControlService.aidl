/*
 * Copyright 2020 The Android Open Source Project
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
 * limitations under the License.
 */

package androidx.wear.watchface.control;

import androidx.wear.watchface.control.IInteractiveWatchFaceWCS;
import androidx.wear.watchface.control.data.WallpaperInteractiveWatchFaceInstanceParams;

/**
 * Interface for WCS to initialize a wallpaper service backed watch face.
 *
 * @hide
 */
interface IWallpaperWatchFaceControlService {
    // IMPORTANT NOTE: All methods must be given an explicit transaction id that must never change
    // in the future to remain binary backwards compatible.
    // Next Id: 3

    /**
     * API version number. This should be incremented every time a new method is added.
     */
    const int API_VERSION = 1;

    /**
     * Returns the version number for this API which the client can use to determine which methods
     * are available.
     *
     * @since API version 1.
     */
    int getApiVersion() = 1;

    /**
     * Finishes watch face initialization and returns an {@link IInteractiveWatchFaceWCS} to control
     * it. Note SysUI can obtain a {@link IInteractiveWatchFaceSysUI} via
     * {@link IWatchFaceControlService#getInteractiveWatchFaceInstanceSysUI}.
     *
     * <p> The [SurfaceHolder] must be sent via the wallpaper service before calling
     * createInteractiveWatchFaceInstance.
     *
     * <p> When finished {@link IInteractiveWatchFaceWCS#release} should be called to release
     * resources.
     */
    IInteractiveWatchFaceWCS createInteractiveWatchFaceInstance(
            in WallpaperInteractiveWatchFaceInstanceParams params) = 2;
}