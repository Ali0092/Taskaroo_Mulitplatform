/**
 * Onboarding introduction screen for new users.
 *
 * This screen provides the initial onboarding experience for new users,
 * featuring a horizontal pager with motivational quotes and imagery to
 * introduce the app's purpose and value proposition. Users can swipe through
 * multiple pages before proceeding to preferences setup.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.common.DotIndicator
import com.dev.taskaroo.modal.PagerModel
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.onPrimary
import com.dev.taskaroo.primaryColorVariant
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.onboarding_1
import taskaroo.composeapp.generated.resources.onboarding_2
import taskaroo.composeapp.generated.resources.onboarding_3


/**
 * Introduction and onboarding screen with horizontal pager.
 *
 * This screen presents:
 * - Three-page horizontal pager with motivational quotes
 * - Visual imagery representing task management concepts
 * - Dot indicator for page navigation
 * - "Get Started" button to proceed to preferences setup
 *
 * Each page features:
 * - An illustrative image
 * - A motivational quote about productivity
 * - Attribution to the quote's author
 */
class IntroScreen : Screen {


    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val picturesList = listOf(
            PagerModel(
                image = Res.drawable.onboarding_1,
                quote = "Manage your tasks & goals \nwith Taskaroo",
                author = "Peter Parker."
            ),
            PagerModel(
                image = Res.drawable.onboarding_2,
                quote = "Focus on being productive instead of \nbusy",
                author = "– Tim Ferriss"
            ),
            PagerModel(
                image = Res.drawable.onboarding_3,
                quote = "What gets measured gets \nmanaged.",
                author = "– Peter Drucker"
            ),
        )
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val pagerState = rememberPagerState(pageCount = { 3 })


                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = "Manage Tasks & goals\n with Taskaroo",
                    color = onBackgroundColor,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 28.sp
                )

                //Imaged....
                HorizontalPager(state = pagerState) { page ->
                    PagerView(picturesList[page].image, picturesList[page].quote, picturesList[page].author)
                }
                //Dots
                DotIndicator(3, pagerState.currentPage)

                Spacer(modifier = Modifier.weight(1f))


                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF798F79)),
                    onClick = {
                        navigator.push(PreferencesScreen())
                    }
                ) {
                    Text(text = "Get Started", color = onPrimary, modifier = Modifier.padding(vertical = 3.dp))

                }

                Spacer(modifier = Modifier.weight(1f))

            }
        }
    }

}

/**
 * Individual page view within the introduction pager.
 *
 * Displays a single onboarding page with an image, motivational quote,
 * and author attribution in a vertically arranged layout.
 *
 * @param image The drawable resource for the page illustration
 * @param quote The motivational quote text to display
 * @param author The attribution text for the quote author
 */
@Composable
fun PagerView(image: DrawableResource, quote: String, author: String) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .padding(top = 28.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth()
                .alpha(0.6f),
            text = quote,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = author,
            color = primaryColorVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

    }
}