package seamcarving


import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.min
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

fun transposed(image: BufferedImage): BufferedImage {
    val newImage = BufferedImage(image.height, image.width,BufferedImage.TYPE_INT_RGB)
    for (i in 0 until image.height) {
        for (j in 0 until image.width) {
            newImage.setRGB(i, j, image.getRGB(j, i))
        }
    }
    return  newImage
}

fun seamDel(image: BufferedImage): BufferedImage {
    val energyArray = Array(image.height){Array(image.width){0.0} }
    val trackArray = Array(image.height){Array(image.width){0.0} }

    for (i in 0 until image.height) {
        for (j in 0 until image.width) energyArray[i][j]  =  energy(j, i, image)
    }

    for (i in 0 until image.height) {
        for (j in 0 until image.width) {
            if (i == 0) trackArray[i][j] = energyArray[i][j]
            else {
                when (j) {
                    0 -> trackArray[i][j] = energyArray[i][j] + min(trackArray[i - 1][j], trackArray[i - 1][j + 1])
                    image.width - 1 -> trackArray[i][j] = energyArray[i][j] + min(trackArray[i - 1][j], trackArray[i - 1][j - 1])
                    else -> trackArray[i][j] = energyArray[i][j] + min(min(trackArray[i - 1][j - 1], trackArray[i - 1][j]), trackArray[i - 1][j + 1])
                }
            }
        }
    }

    var jMin = 0
    var min = trackArray[image.height - 1][0]
    for (j in 0 until image.width) {
        if (min > trackArray[image.height - 1][j]) {
            min = trackArray[image.height - 1][j]
            jMin = j
        }
    }

    val list = mutableListOf<Int>()

    for (i in image.height - 1 downTo 0 ) {
        if (i < image.height - 1) {
            val temp = jMin
            when (jMin) {
                0 -> if (trackArray[i][1] < trackArray[i][0]) jMin = 1
                image.width - 1 -> if (trackArray[i][image.width - 2] < trackArray[i][image.width - 1]) jMin = image.width - 2
                else -> {
                    if (trackArray[i][temp - 1] < trackArray[i][jMin]) jMin = temp - 1
                    if (trackArray[i][temp + 1] < trackArray[i][jMin]) jMin = temp + 1
                }
            }
        }
        list += jMin
    }
    list.reverse()

    val newImage = BufferedImage(image.width - 1, image.height, BufferedImage.TYPE_INT_RGB)
    for (i in 0 until image.height) {
        for (j in 0 until image.width) {
            if (j == image.width - 1 && list[i] == image.width - 1) continue
            if (j <= list[i]) newImage.setRGB(j, i, image.getRGB(j, i))
            else newImage.setRGB(j - 1, i, image.getRGB(j, i))
        }
    }
    return newImage
}

fun main(args: Array<String>) {
    var image: BufferedImage = ImageIO.read(File(args[1]))

    for (i in 1..args[5].toInt()) {
        image = seamDel(image)
    }

    image = transposed(image)
    for (i in 1..args[7].toInt()) image = seamDel(image)

    ImageIO.write(transposed(image), "png", File(args[3]))
}
