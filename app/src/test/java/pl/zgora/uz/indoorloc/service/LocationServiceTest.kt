package pl.zgora.uz.indoorloc.service

import org.junit.Test
import pl.zgora.uz.indoorloc.model.Point

class LocationServiceTest {
    val service = LocationService()
    @Test
    fun calculateTwoDimensionLocationTest() {
        val point1 = Point(0.0, 0.0)
        val distance1 = 5.0
        val point2 = Point(10.0, 0.0)
        val distance2 = 6.0
        val point3 = Point(5.0, 8.0)
        val distance3 = 4.0

        val beaconMap = HashMap<Point, Double>()
        beaconMap.put(point1, distance1)
        beaconMap.put(point2, distance2)
        beaconMap.put(point3, distance3)
        val loc = service.calculateLocationTwoDimension(beaconMap)

        //Position(x=4.45, y=3.34375)
        println(loc)
    }

    @Test
    fun testSolution() {
        // Wprowadzenie znanych wartości
        val x1 = 15.966
        val y1 = 0.0
        val z1 = 10.646
        val distance1 = 10.7031

        val x2 = 0.0
        val y2 = 0.0
        val z2 = 0.0
        val distance2 = 9.37

        val x3 = 8.4316
        val y3 = 10.93
        val z3 = 4.6427
        val distance3 = 8.6

        println(service.trilateration(Point(x1, y1, z1), distance1, Point(x2, y2, z2), distance2, Point(x3, y3, z3), distance3))
    }
}