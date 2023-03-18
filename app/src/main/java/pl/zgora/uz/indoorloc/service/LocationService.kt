package pl.zgora.uz.indoorloc.service
import kotlin.math.*
import pl.zgora.uz.indoorloc.model.Point
import pl.zgora.uz.indoorloc.model.RelativePoint
import java.util.Vector

class LocationService {

    fun calculateLocationTwoDimension(beaconSubjectivePosition: Map<Point, Double>): Point? {
        if (beaconSubjectivePosition.size < 3) {
            return null
        }
        val points = beaconSubjectivePosition.keys.toList()
        val distances = beaconSubjectivePosition.values.toList()
        val n = points.size
        val a = Array(n - 1) { DoubleArray(2) }
        val b = DoubleArray(n - 1)

        for (i in 0 until n - 1) {
            a[i][0] = 2 * (points[i + 1].x - points[0].x)
            a[i][1] = 2 * (points[i + 1].y - points[0].y)
            b[i] = distances[0] * distances[0] - distances[i + 1] * distances[i + 1] -
                    points[0].x * points[0].x + points[i + 1].x * points[i + 1].x -
                    points[0].y * points[0].y + points[i + 1].y * points[i + 1].y
        }

        val AT = Array(2) { DoubleArray(n - 1) }
        for (i in 0 until n - 1) {
            AT[0][i] = a[i][0]
            AT[1][i] = a[i][1]
        }

        val ATA = Array(2) { DoubleArray(2) }
        for (i in 0..1) {
            for (j in 0..1) {
                ATA[i][j] = 0.0
                for (k in 0 until n - 1) {
                    ATA[i][j] += AT[i][k] * a[k][j]
                }
            }
        }

        val atb = DoubleArray(2)
        for (i in 0..1) {
            atb[i] = 0.0
            for (j in 0 until n - 1) {
                atb[i] += AT[i][j] * b[j]
            }
        }

        val detA = ATA[0][0] * ATA[1][1] - ATA[0][1] * ATA[1][0]
        if (detA == 0.0) {
            return null
        }

        val x = (ATA[1][1] * atb[0] - ATA[0][1] * atb[1]) / detA
        val y = (ATA[0][0] * atb[1] - ATA[1][0] * atb[0]) / detA

        return Point(x, y)
    }

    fun trilateration(relativePoints: List<RelativePoint>, initialApproximation: Point): Point {
        // Increasing iterations will decrease error
        val iterations = 5
        var approximation = initialApproximation

        for (i in 0..iterations) {
            val vector = Vector<Double>(3)
            vector.add(0, initialApproximation.x)
            vector.add(1, initialApproximation.y)
            vector.add(2, initialApproximation.z)
            val transpose = Array(3) { DoubleArray(1) }
            for (i in 0..2) {
                transpose[i][0] = vector.get(i);
            }
            
        }

        return Point(0.0,0.0)
    }

}