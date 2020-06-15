/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.animation

import androidx.animation.Spring.DampingRatioNoBouncy
import androidx.animation.Spring.StiffnessMedium
import androidx.animation.Spring.StiffnessVeryLow

/**
 * Animation builder for creating an animation that animates a value of type [T].
 */
abstract class AnimationBuilder<T> {
    abstract fun <V : AnimationVector> build(
        converter: TwoWayConverter<T, V>
    ): AnimationSpec<V>
}

/**
 * The default duration used in [AnimationSpec]s.
 */
const val DefaultDuration: Long = 300L

/**
 * Used as a iterations count for [RepeatableBuilder] to create an infinity repeating animation.
 */
const val Infinite: Int = Int.MAX_VALUE

/**
 * [KeyframesBuilder] creates a [KeyframesSpec] animation.
 * [KeyframesSpec] animation based on the values defined at different timestamps in
 * the duration of the animation (i.e. different keyframes). Each keyframe can be defined using
 * [at]. [KeyframesSpec] allows very specific animation definitions with a precision to millisecond.
 *
 * @sample androidx.animation.samples.FloatKeyframesBuilder
 *
 * You can also provide a custom [Easing] for the interval with use of [with] function applied
 * for the interval starting keyframe.
 * @sample androidx.animation.samples.KeyframesBuilderWithEasing
 */
class KeyframesBuilder<T> : DurationBasedAnimationBuilder<T>() {

    private val keyframes = mutableMapOf<Long, KeyframeEntity<T>>()
    /**
     * Adds a keyframe so that animation value will be [this] at time: [timeStamp]
     *
     * @param timeStamp The time in the during when animation should reach value: [this]
     * @return an [KeyframeEntity] so a custom [Easing] can be added by [with] method.
     */
    infix fun T.at(timeStamp: Int): KeyframeEntity<T> {
        return if (timeStamp >= 0) {
            KeyframeEntity(this).also {
                keyframes[timeStamp.toLong()] = it
            }
        } else {
            // TODO: adding a timestamp < 0 should cause a compile time error
            throw IllegalArgumentException("Time cannot be negative.")
        }
    }

    /**
     * Adds an [Easing] for the interval started with the just provided timestamp.
     *
     * @sample androidx.animation.samples.KeyframesBuilderWithEasing
     * @param easing [Easing] to be used for the next interval.
     */
    infix fun KeyframeEntity<T>.with(easing: Easing) {
        this.easing = easing
    }

    override fun <V : AnimationVector> build(
        converter: TwoWayConverter<T, V>
    ): DurationBasedAnimationSpec<V> {
        return KeyframesSpec(keyframes.mapValues {
            it.value.toPair(converter.convertToVector)
        }, duration.toLong(), delay.toLong())
    }

    /**
     * Holder class for building a keyframes animation.
     */
    inner class KeyframeEntity<T> internal constructor(
        internal val value: T,
        internal var easing: Easing = LinearEasing
    ) {
        internal fun <V : AnimationVector> toPair(convertToVector: (T) -> V) =
            convertToVector.invoke(value) to easing
    }
}

/**
 * Used for creating repeated animations where each iteration is defined by one of
 * the duration based animations like [TweenBuilder] or [KeyframesBuilder].
 */
class RepeatableBuilder<T> : AnimationBuilder<T>() {
    /**
     * The count of iterations. Can't be less then 1. Use [Infinite] to
     * have an infinity repeating animation.
     */
    var iterations: Int? = null
        set(value) {
            if (value != null && value < 1) {
                throw IllegalStateException("Iterations count can't be less than 1")
            }
            field = value
        }

    /**
     * Use [TransitionSpec.tween] or [TransitionSpec.keyframes] as a specification
     * for the animation iteration.
     */
    var animation: DurationBasedAnimationBuilder<T>? = null

    /**
     * Creates a repeating animation that runs [animation] for the given [iterations].
     *
     * @throws IllegalStateException if the [iterations] or [animation] are undefined
     */
    override fun <V : AnimationVector> build(converter: TwoWayConverter<T, V>): AnimationSpec<V> {
        val iterationsCount = iterations?.toLong()
            ?: throw IllegalStateException("The iterations count should be provided")
        val animation = animation
            ?: throw IllegalStateException("The animation should be provided")
        return RepeatableSpec(
            iterationsCount,
            animation.build(converter)
        )
    }
}

/**
 * Base class for an [AnimationBuilder] to create animations based on a fixed duration.
 */
abstract class DurationBasedAnimationBuilder<T> : AnimationBuilder<T>() {
    /**
     * Duration of the animation in milliseconds. Defaults to [DefaultDuration]
     */
    var duration: Int = DefaultDuration.toInt()
        set(value) {
            if (value < 0) {
                throw IllegalStateException("Duration shouldn't be negative")
            }
            field = value
        }

    /**
     * The amount of time that the animation should be delayed.
     */
    var delay: Int = 0
        set(value) {
            if (value < 0) {
                throw IllegalStateException("Delay shouldn't be negative")
            }
            field = value
        }

    abstract override fun <V : AnimationVector> build(
        converter: TwoWayConverter<T, V>
    ): DurationBasedAnimationSpec<V>
}

/**
 * TweenBuilder builds a tween animation that animates from start to end value, based on an
 * [easing] curve within the given [duration].
 */
class TweenBuilder<T> : DurationBasedAnimationBuilder<T>() {
    /**
     * Easing function for the Tween animation. Default: [FastOutSlowInEasing]
     *
     * Easing functions define the rate of change of the value being animated. They allow animation
     * to accelerate or decelerate in a specific pattern.
     */
    var easing: Easing = FastOutSlowInEasing

    override fun <V : AnimationVector> build(
        converter: TwoWayConverter<T, V>
    ): TweenSpec<V> {
        val delay = this.delay.toLong()
        val duration = this.duration.toLong()
        return TweenSpec(duration, delay, easing)
    }
}

/**
 * PhysicsBuilder takes in the configuration of a spring as its constructor parameters.
 *
 * @param dampingRatio Damping ratio of the spring. Defaults to [DampingRatioNoBouncy]
 * @param stiffness Stiffness of the spring. Defaults to [StiffnessVeryLow]
 */
class PhysicsBuilder<T>(
    var dampingRatio: Float = DampingRatioNoBouncy,
    var stiffness: Float = StiffnessMedium,
    displacementThreshold: T? = null
) : AnimationBuilder<T>() {

    private val genericThreshold: T? = displacementThreshold

    override fun <V : AnimationVector> build(
        converter: TwoWayConverter<T, V>
    ): SpringSpec<V> {
        val threshold: V? = genericThreshold?.let { converter.convertToVector(genericThreshold) }
        return SpringSpec(
            dampingRatio,
            stiffness,
            threshold
        )
    }
}

/**
 * Builds Snap animation for immediately switching the animating value to the end value.
 */
class SnapBuilder<T> : AnimationBuilder<T>() {
    /**
     * The amount of time in milliseconds that the snap animation should wait before it runs. 0
     * by default.
     */
    var delay: Int = 0

    override fun <V : AnimationVector> build(converter: TwoWayConverter<T, V>): SnapSpec<V> =
        SnapSpec(delay.toLong())
}

/**
 * Convenient internal class to set a duration on a multi-dimensional animation.
 */
private class SimpleDurationBasedAnimationSpec<V : AnimationVector>(
    override val duration: Long,
    override val delay: Long,
    private val anim: AnimationSpec<V>
) : DurationBasedAnimationSpec<V> {
    override fun getValue(playTime: Long, start: V, end: V, startVelocity: V): V {
        return anim.getValue(playTime, start, end, startVelocity)
    }

    override fun getVelocity(playTime: Long, start: V, end: V, startVelocity: V): V {
        return anim.getVelocity(playTime, start, end, startVelocity)
    }
}