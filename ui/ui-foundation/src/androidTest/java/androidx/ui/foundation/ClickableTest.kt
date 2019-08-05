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

package androidx.ui.foundation

import androidx.compose.composer
import androidx.test.filters.MediumTest
import androidx.ui.core.TestTag
import androidx.ui.core.Text
import androidx.ui.layout.Center
import androidx.ui.test.assertHasClickAction
import androidx.ui.test.assertHasNoClickAction
import androidx.ui.test.assertSemanticsIsEqualTo
import androidx.ui.test.createComposeRule
import androidx.ui.test.createFullSemantics
import androidx.ui.test.doClick
import androidx.ui.test.findByTag
import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@MediumTest
@RunWith(JUnit4::class)
class ClickableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickableTest_defaultSemantics() {
        composeTestRule.setContent {
            Center {
                TestTag(tag = "myClickable") {
                    Clickable(onClick = {}) {
                        Text("ClickableText")
                    }
                }
            }
        }

        findByTag("myClickable")
            .assertSemanticsIsEqualTo(
                createFullSemantics(
                    isEnabled = true
                )
            )
            .assertHasClickAction()
    }

    @Test
    fun clickableTest_disabledSemantics() {
        composeTestRule.setContent {
            Center {
                TestTag(tag = "myClickable") {
                    Clickable {
                        Text("ClickableText")
                    }
                }
            }
        }

        findByTag("myClickable")
            .assertSemanticsIsEqualTo(
                createFullSemantics(
                    isEnabled = false
                )
            )
            .assertHasNoClickAction()
    }

    @Test
    fun clickableTest_click() {
        var counter = 0
        val onClick: () -> Unit = { ++counter }

        composeTestRule.setContent {
            Center {
                TestTag(tag = "myClickable") {
                    Clickable(onClick = onClick) {
                        Text("ClickableText")
                    }
                }
            }
        }

        findByTag("myClickable")
            .doClick()

        Truth
            .assertThat(counter)
            .isEqualTo(1)

        findByTag("myClickable")
            .doClick()

        Truth
            .assertThat(counter)
            .isEqualTo(2)
    }
}