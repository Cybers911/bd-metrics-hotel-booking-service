package com.amazon.ata.metrics.classroom.activity;

import com.amazon.ata.metrics.classroom.dao.ReservationDao;
import com.amazon.ata.metrics.classroom.dao.models.Reservation;
import com.amazon.ata.metrics.classroom.metrics.MetricsConstants;
import com.amazon.ata.metrics.classroom.metrics.MetricsPublisher;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

import javax.inject.Inject;

/**
 * Handles requests to cancel a reservation.
 */
public class CancelReservationActivity {

    private ReservationDao reservationDao;
    private MetricsPublisher metricsPublisher;

    /**
     * Constructs a CancelReservationActivity
     * @param reservationDao Dao used to update reservations.
     */
    @Inject
    public CancelReservationActivity(ReservationDao reservationDao, MetricsPublisher metricsPublisher) {
        this.reservationDao = reservationDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Cancels the given reservation.
     * And Update the CancelReservationCount metric
     * And Update the ReservationRevenue Count metric with the total cost of the reservation.
     * @param reservationId of the reservation to cancel.
     * @return canceled reservation
     */
    public Reservation handleRequest(final String reservationId) {

        Reservation response = reservationDao.cancelReservation(reservationId);

        //Update the metrics publisher to publish the metric Update the CancelReservationCount metric

        //classofEnum.enumName
        metricsPublisher.addMetric(MetricsConstants.CANCEL_COUNT, 1, StandardUnit.Count);

        //Update the ReservationRevenue Count metric with the total cost of the reservation.
        //The Reservation is stored in response upon returning from the handleRequest
        // method ReservationDAO.bookReservation(reservation)
        //You can use the totalCost to calculate and publish this metric.
        //TotalCost in the reservation is negative if we Lost it, we need to store it in the database
        //Then publish it when the reservation is canceled.
        metricsPublisher.addMetric(MetricsConstants.RESERVATION_REVENUE
                , response.getTotalCost().doubleValue()//convert from BigDecimal to doubleValue
                , StandardUnit.None);
        return response;
    }
}
