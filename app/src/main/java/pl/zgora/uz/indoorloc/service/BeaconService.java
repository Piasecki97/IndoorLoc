package pl.zgora.uz.indoorloc.service;

import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.List;

import pl.zgora.uz.indoorloc.model.CalibratedBluetoothDevice;
import pl.zgora.uz.indoorloc.trilateration.NonLinearLeastSquaresSolver;
import pl.zgora.uz.indoorloc.trilateration.TrilaterationFunction;

public class BeaconService {
    Boolean positionsSet = false;

    public double[] calculatePosition(List<CalibratedBluetoothDevice> calibratedBluetoothDeviceList) {
        double[][] positions = new double[calibratedBluetoothDeviceList.size()][3];
        double[] distances = new double[calibratedBluetoothDeviceList.size()];
        int i = 0;
        for (CalibratedBluetoothDevice device : calibratedBluetoothDeviceList) {
            positionsSet = true;
            positions[i][0] = device.getX();
            positions[i][1] = device.getY();
            positions[i][2] = device.getZ();
            i++;
            distances[i] = device.getDistance();
        }


        TrilaterationFunction trilaterationFunction = new TrilaterationFunction(positions, distances);
        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(trilaterationFunction, new LevenbergMarquardtOptimizer());
        return solver.solve().getPoint().toArray();
    }

}
