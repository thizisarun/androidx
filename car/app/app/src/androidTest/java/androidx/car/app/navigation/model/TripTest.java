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

package androidx.car.app.navigation.model;

import static androidx.car.app.TestUtils.createDateTimeWithZone;
import static androidx.car.app.navigation.model.LaneDirection.SHAPE_SHARP_LEFT;
import static androidx.car.app.navigation.model.Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW;

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import androidx.car.app.model.CarIcon;
import androidx.car.app.model.Distance;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Duration;

/** Tests for {@link Trip}. */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class TripTest {

    private final Step mStep =
            Step.builder("Take the second exit of the roundabout.")
                    .addLane(Lane.builder().addDirection(
                            LaneDirection.create(SHAPE_SHARP_LEFT, true)).build())
                    .setManeuver(Maneuver.builder(TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW)
                            .setRoundaboutExitNumber(/*roundaboutExitNumber=*/ 2)
                            .setIcon(CarIcon.APP_ICON)
                            .build())
                    .build();
    private final Destination mDestination =
            Destination.builder().setName("Google BVE").setAddress("1120 112th Ave NE").build();
    private final TravelEstimate mStepTravelEstimate =
            TravelEstimate.create(
                    Distance.create(/* displayDistance= */ 10, Distance.UNIT_KILOMETERS),
                    Duration.ofHours(1).getSeconds(),
                    createDateTimeWithZone("2020-04-14T15:57:00", "US/Pacific"));
    private final TravelEstimate mDestinationTravelEstimate =
            TravelEstimate.create(
                    Distance.create(/* displayDistance= */ 100, Distance.UNIT_KILOMETERS),
                    Duration.ofHours(1).getSeconds(),
                    createDateTimeWithZone("2020-04-14T16:57:00", "US/Pacific"));
    private static final String ROAD = "State St.";

    @Test
    public void createInstance() {
        Trip trip =
                Trip.builder()
                        .addDestination(mDestination)
                        .addStep(mStep)
                        .addDestinationTravelEstimate(mDestinationTravelEstimate)
                        .addStepTravelEstimate(mStepTravelEstimate)
                        .setCurrentRoad(ROAD)
                        .setIsLoading(false)
                        .build();

        assertThat(trip.getDestinations()).hasSize(1);
        assertThat(mDestination).isEqualTo(trip.getDestinations().get(0));
        assertThat(trip.getSteps()).hasSize(1);
        assertThat(mStep).isEqualTo(trip.getSteps().get(0));
        assertThat(trip.getDestinationTravelEstimates()).hasSize(1);
        assertThat(mDestinationTravelEstimate).isEqualTo(
                trip.getDestinationTravelEstimates().get(0));
        assertThat(trip.getStepTravelEstimates()).hasSize(1);
        assertThat(mStepTravelEstimate).isEqualTo(trip.getStepTravelEstimates().get(0));
        assertThat(trip.isLoading()).isFalse();
    }

    @Test
    public void getDestinationWithEstimates_mismatch_count() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Trip.builder().addDestination(mDestination).build());
        assertThrows(
                IllegalArgumentException.class,
                () -> Trip.builder().addDestinationTravelEstimate(
                        mDestinationTravelEstimate).build());
    }

    @Test
    public void getStepWithEstimates_mismatch_count() {
        assertThrows(IllegalArgumentException.class, () -> Trip.builder().addStep(mStep).build());
        assertThrows(
                IllegalArgumentException.class,
                () -> Trip.builder().addStepTravelEstimate(mStepTravelEstimate).build());
    }

    @Test
    public void createInstance_loading_no_steps() {
        Trip trip =
                Trip.builder()
                        .addDestination(mDestination)
                        .addDestinationTravelEstimate(mDestinationTravelEstimate)
                        .setCurrentRoad(ROAD)
                        .setIsLoading(true)
                        .build();

        assertThat(trip.getDestinations()).hasSize(1);
        assertThat(mDestination).isEqualTo(trip.getDestinations().get(0));
        assertThat(trip.getSteps()).hasSize(0);
        assertThat(trip.getDestinationTravelEstimates()).hasSize(1);
        assertThat(mDestinationTravelEstimate).isEqualTo(
                trip.getDestinationTravelEstimates().get(0));
        assertThat(trip.getStepTravelEstimates()).hasSize(0);
        assertThat(trip.isLoading()).isTrue();
    }

    @Test
    public void createInstance_loading_with_steps() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Trip.builder().addStep(mStep).setIsLoading(true).build());
        assertThrows(
                IllegalArgumentException.class,
                () -> Trip.builder().addStepTravelEstimate(mStepTravelEstimate).setIsLoading(
                        true).build());
        assertThrows(
                IllegalArgumentException.class,
                () -> Trip.builder()
                        .addStep(mStep)
                        .addStepTravelEstimate(mStepTravelEstimate)
                        .setIsLoading(true)
                        .build());
    }
}
