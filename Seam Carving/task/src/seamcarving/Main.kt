package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

fun energy (x: Int, y: Int, image: BufferedImage): Double {
    val colorX1 = when (x) {
        0 -> Color(image.getRGB(x, y))
        image.width - 1 -> Color(image.getRGB(x - 2, y))
        else -> Color(image.getRGB(x - 1, y))
    }
    val colorX2 = when (x) {
        0 -> Color(image.getRGB(x + 2, y))
        image.width - 1 -> Color(image.getRGB(x, y))
        else -> Color(image.getRGB(x + 1, y))
    }
    val colorY1 = when (y) {
        0 -> Color(image.getRGB(x, y))
        image.height - 1 -> Color(image.getRGB(x, y - 2))
        else -> Color(image.getRGB(x, y - 1))
    }
    val colorY2 = when (y) {
        0 -> Color(image.getRGB(x, y + 2))
        image.height - 1 -> Color(image.getRGB(x, y))
        else -> Color(image.getRGB(x, y + 1))
    }
    val colorX = (colorX1.red - colorX2.red).toDouble().pow(2) +
            (colorX1.green - colorX2.green).toDouble().pow(2) +
            (colorX1.blue - colorX2.blue).toDouble().pow(2)
    val colorY = (colorY1.red - colorY2.red).toDouble().pow(2) +
            (colorY1.green - colorY2.green).toDouble().pow(2) +
            (colorY1.blue - colorY2.blue).toDouble().pow(2)
    return sqrt(colorX + colorY)
}

fun main(args: Array<String>) {
    val image: BufferedImage = ImageIO.read(File(args[1]))
    val energyArray = Array(image.height){Array(image.width){0.0} }
    var maxEnergy = 0.0
    for (i in 0 until image.height) {
        for (j in 0 until image.width) {
            energyArray[i][j]  =  energy(j, i, image)
            if (maxEnergy < energyArray[i][j]) maxEnergy = energyArray[i][j]

        }
    }

    for (i in 0 until image.height) {
        for (j in 0 until image.width) {
            val intensity = (255.0 * energyArray[i][j] / maxEnergy).toInt()
            val newColor = Color(intensity, intensity, intensity)
            image.setRGB(j, i, newColor.rgb)
        }
    }
    ImageIO.write(image, "png", File(args[3]))
}
