package android.ktcodelab.mydailynote.model

import android.ktcodelab.mydailynote.R
import android.ktcodelab.mydailynote.ui.theme.*
import androidx.compose.ui.graphics.Color

enum class MoodModel(

    val icon: Int,
    val contentColor: Color,
    val containerColor: Color,
    val backgroundColor: Color
) {

    Neutral(
        icon = R.drawable.ic_neutral,
        contentColor = Color.Black,
        containerColor = LightGray,
        backgroundColor = NeutralColor
    ),
    Happy(
        icon = R.drawable.ic_happy,
        contentColor = Color.Black,
        containerColor = HappyColor,
        backgroundColor = BabyBlue
    ),
    Romantic(
        icon = R.drawable.ic_romantic,
        contentColor = Color.Black,
        containerColor = RomanticColor,
        backgroundColor = RedPink
    ),
    Love(
        icon = R.drawable.love,
        contentColor = Color.Black,
        containerColor = CalmColor,
        backgroundColor = Violet
    ),
    Tense(
        icon = R.drawable.ic_tensed,
        contentColor = Color.Black,
        containerColor = TenseColor,
        backgroundColor = BabyBlue
    ),
    Lonely(
        icon = R.drawable.ic_lonely,
        contentColor = Color.Black,
        containerColor = LonelyColor,
        backgroundColor = LightGreen
    ),
    Mysterious(
        icon = R.drawable.ic_mysterious,
        contentColor = Color.Black,
        containerColor = MysteriousColor,
        backgroundColor = RedOrange
    ),
    Angry(
        icon = R.drawable.ic_angry,
        contentColor = Color.White,
        containerColor = AngryColor,
        backgroundColor = Violet
    ),
    Laugh(
        icon = R.drawable.ic_laugh,
        contentColor = Color.Black,
        containerColor = AwfulColor,
        backgroundColor = BabyBlue
    ),
    Yummy(
        icon = R.drawable.ic_yummy,
        contentColor = Color.Black,
        containerColor = HumorousColor,
        backgroundColor = SuspiciousColor
    ),
    Suspicious(
        icon = R.drawable.ic_suspicious,
        contentColor = Color.Black,
        containerColor = SuspiciousColor,
        backgroundColor = RedPink
    ),
    Bored(
        icon = R.drawable.ic_bored,
        contentColor = Color.Black,
        containerColor = BoredColor,
        backgroundColor = Violet
    ),
    Surprise(
        icon = R.drawable.ic_surprised,
        contentColor = Color.Black,
        containerColor = SurpriseColor,
        backgroundColor = HappyColor
    ),
    Disappointed(
        icon = R.drawable.ic_disappointed,
        contentColor = Color.White,
        containerColor = DisappointedColor,
        backgroundColor = DepressedColor
    ),
    Confused(
        icon = R.drawable.ic_confused,
        contentColor = Color.Black,
        containerColor = ShamefulColor,
        backgroundColor = RedOrange
    ),
    Depressed(
        icon = R.drawable.ic_depressed,
        contentColor = Color.Black,
        containerColor = DepressedColor,
        backgroundColor = AwfulColor
    )
}