package com.amazon.ata.metrics.classroom.activity;

import com.amazon.ata.metrics.classroom.dao.ReservationDao;
import com.amazon.ata.metrics.classroom.dao.models.Reservation;
import com.amazon.ata.metrics.classroom.metrics.MetricsPublisher;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazon.ata.metrics.classroom.metrics.MetricsConstants;

import javax.inject.Inject;

/**
 * Handles requests to book a reservation.
 */
public class BookReservationActivity {

    private ReservationDao reservationDao;
    private MetricsPublisher metricsPublisher;

    /**
     * Constructs a BookReservationActivity
     * @param reservationDao Dao used to create reservations.
     */
    @Inject
    public BookReservationActivity(ReservationDao reservationDao, MetricsPublisher metricsPublisher) {
        this.reservationDao = reservationDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Creates a reservation with the provided details.
     * and Update the BookedReservationCount metric
     * and update the ReservationRevenue metric
     * @param reservation Reservation to create.
     * @return
     */
    public Reservation handleRequest(Reservation reservation) {

        // Publish a metric to track the number of reservations created
        //Create a new reservation object with a unique ID

        Reservation response = reservationDao.bookReservation(reservation);

        //Update the metrics publisher to publish the metric Update the BookedReservationCount metric

                                        //classofEnum.enumName
        metricsPublisher.addMetric(MetricsConstants.BOOKED_RESERVATION_COUNT, 1, StandardUnit.Count);

        //Update the ReservationRevenue Count metric with the total cost of the reservation.
        //The Reservation is stored in response upon returning from the handleRequest
        // method ReservationDAO.bookReservation(reservation)
        metricsPublisher.addMetric(MetricsConstants.RESERVATION_REVENUE
                , response.getTotalCost().doubleValue()//convert from BigDecimal to doubleValue
                , StandardUnit.None);
        return response;
    }
}
