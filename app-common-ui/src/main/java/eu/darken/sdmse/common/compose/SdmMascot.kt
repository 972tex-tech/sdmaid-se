package eu.darken.sdmse.common.compose

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import eu.darken.sdmse.common.compose.preview.Preview2
import eu.darken.sdmse.common.compose.preview.PreviewWrapper
import eu.darken.sdmse.common.ui.R

@Composable
fun SdmMascot(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.splash_mascot),
        contentDescription = null,
        modifier = modifier,
    )
}

@Preview2
@Composable
private fun SdmMascotPreview() {
    PreviewWrapper {
        SdmMascot()
    }
}
